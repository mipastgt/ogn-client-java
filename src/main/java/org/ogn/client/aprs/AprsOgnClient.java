/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.client.aprs;

import static org.ogn.client.OgnClientConstants.OGN_CLIENT_DEFAULT_KEEP_ALIVE_INTERVAL_MS;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_APP_NAME;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_APP_VERSION;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_RECONNECTION_TIMEOUT_MS;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_SERVER_NAME;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_SRV_PORT;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_SRV_PORT_FILTERED;
import static org.ogn.commons.utils.AprsUtils.formatAprsLoginLine;
import static org.ogn.commons.utils.AprsUtils.generateClientId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.ogn.client.AircraftBeaconListener;
import org.ogn.client.OgnClient;
import org.ogn.client.ReceiverBeaconListener;
import org.ogn.commons.beacon.AircraftBeacon;
import org.ogn.commons.beacon.AircraftDescriptor;
import org.ogn.commons.beacon.OgnBeacon;
import org.ogn.commons.beacon.ReceiverBeacon;
import org.ogn.commons.beacon.descriptor.AircraftDescriptorProvider;
import org.ogn.commons.beacon.impl.aprs.AprsLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * APRS implementation of the OGN client.
 * 
 * @author wbuczak
 */
public class AprsOgnClient implements OgnClient {

	private static final Logger				LOG					= LoggerFactory.getLogger(AprsOgnClient.class);

	/**
	 * read only pass-code
	 * 
	 * @see <a href="http://www.aprs-is.net/Connecting.aspx">Connecting to APRS-IS</a>
	 */
	private static final String				READ_ONLY_PASSCODE	= "-1";

	private final String					aprsServerName;
	private final int						aprsPort;
	private final int						aprsPortFiltered;
	private final String					aprsFilter;
	private final int						reconnectionTimeout;
	private final int						keepAlive;
	private final String					appName;
	private final String					appVersion;
	private final boolean					processReceiverBeacons;
	private final boolean					processAircraftBeacons;

	private AircraftDescriptorProvider[]	descriptorProviders;

	private ExecutorService					executor;
	private ScheduledExecutorService		scheduledExecutor;

	private volatile Future<?>				socketListenerFuture;
	private volatile Future<?>				pollerFuture;
	private volatile Future<?>				keepAliveFuture;

	private class AprsSocketListenerTask implements Runnable {
		private final String	aprsFilter;

		private Socket			socket;

		public AprsSocketListenerTask(final String aprsFilter) {
			this.aprsFilter = aprsFilter;
		}

		private void processAprsLine(final String line) {
			aprsLines.offer(line);
		}

		@Override
		public void run() {
			LOG.debug("starting...");
			boolean interrupted = false;

			while (!interrupted) {

				try {

					int port = aprsPort;
					String loginSentence = null;

					final String clientId = generateClientId();
					if (null == aprsFilter) {
						loginSentence = formatAprsLoginLine(clientId, READ_ONLY_PASSCODE, appName, appVersion);
					} else {
						port = aprsPortFiltered;
						loginSentence =
								formatAprsLoginLine(clientId, READ_ONLY_PASSCODE, appName, appVersion, aprsFilter);
					}

					final InetAddress srvAddress = InetAddress.getByName(aprsServerName);

					// if filter is specified connect to a different port
					LOG.info("connecting to server: {}[{}]:{}", aprsServerName, srvAddress.getHostAddress(), port);

					socket = new Socket(srvAddress, port);

					final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					LOG.info("logging in as: {}", loginSentence);
					// out.println(loginSentence);

					// start the keep-live msg sender
					startKeepAliveThread(out, loginSentence);

					final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					LOG.info("Connected. Waiting for data...");

					String line;
					while (!interrupted && (line = in.readLine()) != null) {
						if (Thread.currentThread().isInterrupted()) {
							interrupted = true;
							LOG.warn("The AprsSocketListenerTask thread has been interrupted");
							break;
						}

						// out.println(line);
						processAprsLine(line);
					}

				} catch (final Exception e) {
					LOG.error("exception caught while trying to connect to {}:{}. retrying in {} ms", aprsServerName,
							aprsPort, reconnectionTimeout, e);
					try {
						Thread.sleep(reconnectionTimeout);
					} catch (final InterruptedException ex) {
						LOG.debug("interrupted exception caught while waiting before trying to re-connect");
						interrupted = true;
						// Restore interrupted state...
						Thread.currentThread().interrupt();
					}
				} finally {
					closeSocket();
					stopKeepAliveThread();
				}

			} // while

			closeSocket();
			LOG.debug("stopped.");

		}// run

		/**
		 * 
		 */
		private void stopKeepAliveThread() {
			if (keepAliveFuture != null) {
				keepAliveFuture.cancel(true);
			}
		}

		/**
		 * 
		 */
		private void startKeepAliveThread(final PrintWriter out, final String msg) {
			if (keepAliveFuture == null || keepAliveFuture.isCancelled()) {
				keepAliveFuture = scheduledExecutor.scheduleAtFixedRate(() -> {
					final String keepAliveMsg = msg.startsWith("#") ? msg : "#" + msg;
					try {
						LOG.debug("sending keep-alive message: {}", keepAliveMsg);
						out.println(keepAliveMsg);
					} catch (final Exception ex) {
						LOG.warn("exception caught while trying to send keep-alive msg", ex);
					}
				}, 0, keepAlive, TimeUnit.MILLISECONDS);
			}
		}

		void closeSocket() {
			try {
				if (socket != null)
					socket.close();
			} catch (final IOException e) {
				LOG.warn("could not close socket", e);
			}
		}
	}

	/**
	 * polls APRS sentences from aprsLines queue and processes them
	 * 
	 * @author wbuczak
	 */
	private class PollerTask implements Runnable {

		private Optional<AircraftDescriptor> findAircraftDescriptor(AircraftBeacon beacon) {
			Optional<AircraftDescriptor> result = Optional.empty();
			if (descriptorProviders != null) {
				for (final AircraftDescriptorProvider provider : descriptorProviders) {
					final Optional<AircraftDescriptor> ad = provider.findDescriptor(beacon.getAddress());
					if (ad.isPresent()) {
						result = ad;
						break;
					}
				} // for
			}

			return result;
		}

		private <T extends OgnBeacon> void notifyAllListeners(final T ognBeacon, final String rawBeacon) {
			if (ognBeacon instanceof AircraftBeacon) {
				for (final AircraftBeaconListener listener : acBeaconListeners) {
					final AircraftBeacon ab = (AircraftBeacon) ognBeacon;
					final Optional<AircraftDescriptor> descriptor = findAircraftDescriptor(ab);

					listener.onUpdate(ab, descriptor);
				}

			} else if (ognBeacon instanceof ReceiverBeacon) {
				for (final ReceiverBeaconListener listener : brBeaconListeners) {
					listener.onUpdate((ReceiverBeacon) ognBeacon);
				}
			} else {
				LOG.warn("unrecognized beacon type: {} .ignoring..", ognBeacon.getClass().getName());
			}
		}

		@Override
		public void run() {
			LOG.trace("starting...");
			String aprsLine = null;
			while (!Thread.interrupted()) {

				try {
					aprsLine = aprsLines.take();
					LOG.trace(aprsLine);
				} catch (final InterruptedException e) {
					LOG.warn("interrupted exception caught. Was the poller task interrupted on purpose?");
					// Restore interrupted state...
					Thread.currentThread().interrupt();
					continue;
				}

				try {

					final OgnBeacon beacon =
							AprsLineParser.get().parse(aprsLine, processAircraftBeacons, processReceiverBeacons);

					// a beacon may be null in case in hasn't been parsed
					// correctly or if a receiver or aircraft beacon parsing is
					// disabled by user
					if (beacon != null) {
						notifyAllListeners(beacon, aprsLine);
					}
				} catch (final Exception ex) {
					LOG.warn("exception caught", ex);
				}
			} // while
			LOG.trace("exiting..");
		}

	}

	private AprsOgnClient(Builder builder) {
		this.aprsServerName = builder.srvName;
		this.aprsPort = builder.srvPort;
		this.aprsPortFiltered = builder.srvPortFiltered;
		this.aprsFilter = builder.aprsFilter;
		this.reconnectionTimeout = builder.reconnectionTimeout;
		this.keepAlive = builder.keepAlive;
		this.appName = builder.appName;
		this.appVersion = builder.appVersion;
		// user may disable processing receivers beacons (to gain performance if
		// rec.beacons are not needed)
		this.processReceiverBeacons = !builder.ignoreReceiverBeacons;
		// user may disable processing receivers beacons (to gain performance if
		// rec.beacons are not needed)
		this.processAircraftBeacons = !builder.ignoreAircraftBeacons;

		// aircraft descriptor providers are not mandatory
		if (builder.descriptorProviders != null)
			this.descriptorProviders = builder.descriptorProviders.toArray(new AircraftDescriptorProvider[0]);
	}

	public static class Builder {
		private String								srvName					= OGN_DEFAULT_SERVER_NAME;
		private int									srvPort					= OGN_DEFAULT_SRV_PORT;
		private int									srvPortFiltered			= OGN_DEFAULT_SRV_PORT_FILTERED;
		private String								aprsFilter;
		private int									reconnectionTimeout		= OGN_DEFAULT_RECONNECTION_TIMEOUT_MS;
		private int									keepAlive				= OGN_CLIENT_DEFAULT_KEEP_ALIVE_INTERVAL_MS;
		private String								appName					= OGN_DEFAULT_APP_NAME;
		private String								appVersion				= OGN_DEFAULT_APP_VERSION;
		private boolean								ignoreReceiverBeacons	= false;
		private boolean								ignoreAircraftBeacons	= false;

		private List<AircraftDescriptorProvider>	descriptorProviders;

		public Builder serverName(final String name) {
			this.srvName = name;
			return this;
		}

		public Builder port(final int port) {
			this.srvPort = port;
			return this;
		}

		public Builder portFiltered(final int port) {
			this.srvPortFiltered = port;
			return this;
		}

		public Builder aprsFilter(final String filter) {
			this.aprsFilter = filter;
			return this;
		}

		public Builder reconnectionTimeout(final int timeout) {
			this.reconnectionTimeout = timeout;
			return this;
		}

		public Builder appName(final String name) {
			this.appName = name;
			return this;
		}

		public Builder appVersion(final String version) {
			this.appVersion = version;
			return this;
		}

		public Builder keepAlive(final int keepAliveInt) {
			this.keepAlive = keepAliveInt;
			return this;
		}

		public Builder ignoreReceiverBeacons(final boolean flag) {
			this.ignoreReceiverBeacons = flag;
			return this;
		}

		public Builder ignoreAicraftrBeacons(final boolean flag) {
			this.ignoreAircraftBeacons = flag;
			return this;
		}

		public Builder descriptorProviders(List<AircraftDescriptorProvider> descProviders) {
			this.descriptorProviders = descProviders;
			return this;
		}

		public Builder descriptorProviders(AircraftDescriptorProvider... descProviders) {
			this.descriptorProviders = Arrays.asList(descProviders);
			return this;
		}

		public AprsOgnClient build() {
			return new AprsOgnClient(this);
		}

	}

	private final CopyOnWriteArrayList<AircraftBeaconListener>	acBeaconListeners	= new CopyOnWriteArrayList<>();
	private final CopyOnWriteArrayList<ReceiverBeaconListener>	brBeaconListeners	= new CopyOnWriteArrayList<>();

	private final BlockingQueue<String>							aprsLines			= new LinkedBlockingQueue<>();

	/**
	 * connects to the OGN APRS service
	 * 
	 * @param filter
	 *            optional filter, if null no filter will be used, as it is in case of {@link #connect() connect()}.
	 * @see <a href="http://www.aprs-is.net/javAPRSFilter.aspx">Server-side Filter Commands</a>
	 */
	@Override
	public synchronized void connect(final String filter) {
		if (socketListenerFuture == null) {
			executor = Executors.newCachedThreadPool();
			scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
			pollerFuture = executor.submit(new PollerTask());
			socketListenerFuture = executor.submit(new AprsSocketListenerTask(filter));
		} else {
			LOG.warn("client is currently connected and running. stop it first!");
		}
	}

	@Override
	public void connect() {
		connect(this.aprsFilter);
		// connect(null);
	}

	@Override
	public synchronized void disconnect() {
		if (socketListenerFuture != null) {

			if (socketListenerFuture != null) {
				socketListenerFuture.cancel(true);
				socketListenerFuture = null;
			}

			if (pollerFuture != null) {
				pollerFuture.cancel(true);
				pollerFuture = null;
			}

			if (keepAliveFuture != null) {
				keepAliveFuture.cancel(true);
				keepAliveFuture = null;
			}
		}

		if (executor != null)
			executor.shutdownNow();

		if (scheduledExecutor != null)
			scheduledExecutor.shutdownNow();

	}

	@Override
	public void subscribeToAircraftBeacons(AircraftBeaconListener listener) {
		acBeaconListeners.addIfAbsent(listener);
	}

	@Override
	public void subscribeToReceiverBeacons(ReceiverBeaconListener listener) {
		brBeaconListeners.addIfAbsent(listener);
	}

	@Override
	public void unsubscribeFromAircraftBeacons(AircraftBeaconListener listener) {
		acBeaconListeners.remove(listener);
	}

	@Override
	public void unsubscribeFromReceiverBeacons(ReceiverBeaconListener listener) {
		brBeaconListeners.remove(listener);
	}

}
