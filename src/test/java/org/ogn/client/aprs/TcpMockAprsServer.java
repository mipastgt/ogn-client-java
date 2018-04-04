/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.client.aprs;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple, single-threaded mock TCP server, used for integration testing
 * 
 * @author wbuczak
 */
public class TcpMockAprsServer {

	private static Logger		LOG								= LoggerFactory.getLogger(TcpMockAprsServer.class);

	private static final long	DEFAULT_MSG_DELIVERY_INTERVAL	= 1000;
	private int					port;
	private long				msgDeliveryInterval;
	private List<String>		clientSentences					= new ArrayList<>();
	private List<String>		serverSentences;

	private ExecutorService		executor						= Executors.newSingleThreadExecutor();
	private volatile Future<?>	srvFuture;

	public TcpMockAprsServer(int port, List<String> serverSentences, long msgDeliveryInterval) {
		this.port = port;
		this.serverSentences = serverSentences;
		this.msgDeliveryInterval = msgDeliveryInterval;
	}

	public TcpMockAprsServer(int port, List<String> serverSentences) {
		this(port, serverSentences, DEFAULT_MSG_DELIVERY_INTERVAL);
	}

	private void start(boolean runInLoop) {
		if (null == srvFuture) {
			srvFuture = executor.submit(new SrvTask(runInLoop));
		}
	}

	public void runOneCycle() {
		start(false);
	}

	public void runInLoop() {
		start(true);
	}

	public void stop() {
		if (srvFuture != null)
			srvFuture.cancel(true);
		srvFuture = null;
	}

	class SrvTask implements Runnable {

		boolean loopMessages;

		SrvTask(boolean loopMessages) {
			this.loopMessages = loopMessages;
		}

		@Override
		public void run() {
			LOG.debug("starting the server..");
			Socket clientSocket = null;
			ServerSocket serverSocket = null;
			try {
				while (!Thread.interrupted()) {
					String clientSentence;
					serverSocket = new ServerSocket(port);
					clientSocket = serverSocket.accept();
					BufferedReader inFromClient =
							new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
					clientSentence = inFromClient.readLine();

					// remember client sentence
					clientSentences.add(clientSentence);

					outToClient.writeBytes(serverSentences.get(0) + "\n");
					outToClient.writeBytes(serverSentences.get(1) + "\n");

					do {
						for (int index = 2; index < serverSentences.size(); index++) {
							outToClient.writeBytes(serverSentences.get(index) + "\n");
							Thread.sleep(msgDeliveryInterval);
							// System.out.print(". ");
						}
					} while (loopMessages);

					serverSocket.close();
					LOG.info("connection terminated");

				} // while
			} catch (IOException ex) {
				LOG.warn("exception caught", ex);
			} catch (InterruptedException ex) {
				LOG.debug("server interrupted");
			} finally {
				try {
					if (serverSocket != null)
						serverSocket.close();
					if (clientSocket != null)
						clientSocket.close();
				} catch (IOException e) {
					// nothing to be done here apart from logging
					LOG.warn("exception caught", e);
				}
			}

			LOG.debug("stopped");
		}

	}
}