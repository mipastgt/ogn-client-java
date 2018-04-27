/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.client;

import static org.ogn.client.OgnClientConstants.OGN_CLIENT_DEFAULT_KEEP_ALIVE_INTERVAL_MS;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_APP_NAME;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_APP_VERSION;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_RECONNECTION_TIMEOUT_MS;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_SERVER_NAME;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_SRV_PORT;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_SRV_PORT_FILTERED;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_APP_NAME;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_APP_VERSION;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_APRS_FILTER;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_IGNORE_AIRCRAFT_BEACONS;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_IGNORE_RECEIVER_BEACONS;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_KEEP_ALIVE_INTERVAL;
import static org.ogn.client.OgnClientProperties.PROP_OGN_SRV_NAME;
import static org.ogn.client.OgnClientProperties.PROP_OGN_SRV_PORT_FILTERED;
import static org.ogn.client.OgnClientProperties.PROP_OGN_SRV_PORT_UNFILTERED;
import static org.ogn.client.OgnClientProperties.PROP_OGN_SRV_RECONNECTION_TIMEOUT;

import java.util.Arrays;
import java.util.List;

import org.ogn.client.aprs.AprsOgnClient;
import org.ogn.commons.beacon.descriptor.AircraftDescriptorProvider;

/**
 * This factory creates instances of OGN client. Several parameters can be tuned through the environment variables.
 * 
 * @author wbuczak
 */
public class OgnClientFactory {

	private static String	serverName				= System.getProperty(PROP_OGN_SRV_NAME, OGN_DEFAULT_SERVER_NAME);

	private static int		port					=
			Integer.getInteger(PROP_OGN_SRV_PORT_UNFILTERED, OGN_DEFAULT_SRV_PORT);
	private static int		portFiltered			=
			Integer.getInteger(PROP_OGN_SRV_PORT_FILTERED, OGN_DEFAULT_SRV_PORT_FILTERED);
	private static int		reconnectionTimeout		=
			Integer.getInteger(PROP_OGN_SRV_RECONNECTION_TIMEOUT, OGN_DEFAULT_RECONNECTION_TIMEOUT_MS);

	private static int		keepAliveInterval		=
			Integer.getInteger(PROP_OGN_CLIENT_KEEP_ALIVE_INTERVAL, OGN_CLIENT_DEFAULT_KEEP_ALIVE_INTERVAL_MS);

	private static String	appName					=
			System.getProperty(PROP_OGN_CLIENT_APP_NAME, OGN_DEFAULT_APP_NAME);
	private static String	appVersion				=
			System.getProperty(PROP_OGN_CLIENT_APP_VERSION, OGN_DEFAULT_APP_VERSION);

	private static boolean	ignoreReceiverBeacons	=
			System.getProperty(PROP_OGN_CLIENT_IGNORE_RECEIVER_BEACONS) != null;
	private static boolean	ignoreAircraftBeacons	=
			System.getProperty(PROP_OGN_CLIENT_IGNORE_AIRCRAFT_BEACONS) != null;

	private static String	aprsFilter				= System.getProperty(PROP_OGN_CLIENT_APRS_FILTER);

	private OgnClientFactory() {

	}

	public static AprsOgnClient.Builder getBuilder() {
		return new AprsOgnClient.Builder().serverName(serverName).port(port).portFiltered(portFiltered)
				.aprsFilter(aprsFilter).reconnectionTimeout(reconnectionTimeout).appName(appName).appVersion(appVersion)
				.keepAlive(keepAliveInterval).ignoreReceiverBeacons(ignoreReceiverBeacons)
				.ignoreAicraftrBeacons(ignoreAircraftBeacons);
	}

	public static OgnClient createClient() {
		return getBuilder().build();
	}

	public static OgnClient createClient(List<AircraftDescriptorProvider> aircraftDescriptorProviders) {
		return getBuilder().descriptorProviders(aircraftDescriptorProviders).build();
	}

	public static OgnClient createClient(AircraftDescriptorProvider... aircraftDescriptorProviders) {
		return getBuilder().descriptorProviders(Arrays.asList(aircraftDescriptorProviders)).build();
	}

}