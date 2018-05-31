/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.client;

import static org.ogn.client.OgnClientConstants.OGN_CLIENT_DEFAULT_KEEP_ALIVE_INTERVAL_MS;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_APP_NAME;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_APP_VERSION;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_RECONNECTION_TIMEOUT_MS;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_SERVER_NAME;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_SRV_PORT_FILTERED;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_SRV_PORT_UNFILTERED;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_SRV_SSL_PORT_FILTERED;
import static org.ogn.client.OgnClientConstants.OGN_DEFAULT_SRV_SSL_PORT_UNFILTERED;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_APP_NAME;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_APP_VERSION;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_APRS_FILTER;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_ID;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_KEEP_ALIVE_INTERVAL;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_USE_SSL;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_VALIDATE;
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

	private static String	serverName			= System.getProperty(PROP_OGN_SRV_NAME, OGN_DEFAULT_SERVER_NAME);

	private static int		unfilteredPort		=
			Integer.getInteger(PROP_OGN_SRV_PORT_UNFILTERED, OGN_DEFAULT_SRV_PORT_UNFILTERED);
	private static int		filteredPort		=
			Integer.getInteger(PROP_OGN_SRV_PORT_FILTERED, OGN_DEFAULT_SRV_PORT_FILTERED);

	private static int		unfilteredSslPort	=
			Integer.getInteger(PROP_OGN_SRV_PORT_UNFILTERED, OGN_DEFAULT_SRV_SSL_PORT_UNFILTERED);
	private static int		filteredSslPort		=
			Integer.getInteger(PROP_OGN_SRV_PORT_FILTERED, OGN_DEFAULT_SRV_SSL_PORT_FILTERED);

	private static int		reconnectionTimeout	=
			Integer.getInteger(PROP_OGN_SRV_RECONNECTION_TIMEOUT, OGN_DEFAULT_RECONNECTION_TIMEOUT_MS);

	private static int		keepAliveInterval	=
			Integer.getInteger(PROP_OGN_CLIENT_KEEP_ALIVE_INTERVAL, OGN_CLIENT_DEFAULT_KEEP_ALIVE_INTERVAL_MS);

	private static String	appName				= System.getProperty(PROP_OGN_CLIENT_APP_NAME, OGN_DEFAULT_APP_NAME);
	private static String	appVersion			=
			System.getProperty(PROP_OGN_CLIENT_APP_VERSION, OGN_DEFAULT_APP_VERSION);

	private static String	ognClientId			= System.getProperty(PROP_OGN_CLIENT_ID);

	private static boolean	useSsl				= Boolean.parseBoolean(System.getProperty(PROP_OGN_CLIENT_USE_SSL));
	private static boolean	ognClientValidate	= Boolean.parseBoolean(System.getProperty(PROP_OGN_CLIENT_VALIDATE));

	private static String	aprsFilter			= System.getProperty(PROP_OGN_CLIENT_APRS_FILTER);

	private OgnClientFactory() {

	}

	public static AprsOgnClient.Builder getBuilder() {
		return new AprsOgnClient.Builder().serverName(serverName).useSsl(useSsl).unfilteredPort(unfilteredPort)
				.filteredPort(filteredPort).unfilteredSslPort(unfilteredSslPort).filteredSslPort(filteredSslPort)
				.aprsFilter(aprsFilter).reconnectionTimeout(reconnectionTimeout).ognClientId(ognClientId)
				.validateClient(ognClientValidate).appName(appName).appVersion(appVersion).keepAlive(keepAliveInterval);
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