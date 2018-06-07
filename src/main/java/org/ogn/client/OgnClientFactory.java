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
import static org.ogn.client.OgnClientProperties.ENV_OGN_CLIENT_APP_NAME;
import static org.ogn.client.OgnClientProperties.ENV_OGN_CLIENT_APP_VERSION;
import static org.ogn.client.OgnClientProperties.ENV_OGN_CLIENT_APRS_FILTER;
import static org.ogn.client.OgnClientProperties.ENV_OGN_CLIENT_ID;
import static org.ogn.client.OgnClientProperties.ENV_OGN_CLIENT_KEEP_ALIVE_INTERVAL;
import static org.ogn.client.OgnClientProperties.ENV_OGN_CLIENT_SRV_NAME;
import static org.ogn.client.OgnClientProperties.ENV_OGN_CLIENT_SRV_PORT_FILTERED;
import static org.ogn.client.OgnClientProperties.ENV_OGN_CLIENT_SRV_PORT_UNFILTERED;
import static org.ogn.client.OgnClientProperties.ENV_OGN_CLIENT_SRV_RECONNECTION_TIMEOUT;
import static org.ogn.client.OgnClientProperties.ENV_OGN_CLIENT_USE_SSL;
import static org.ogn.client.OgnClientProperties.ENV_OGN_CLIENT_VALIDATE;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_APP_NAME;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_APP_VERSION;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_APRS_FILTER;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_ID;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_KEEP_ALIVE_INTERVAL;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_SRV_NAME;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_SRV_PORT_FILTERED;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_SRV_PORT_UNFILTERED;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_SRV_RECONNECTION_TIMEOUT;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_USE_SSL;
import static org.ogn.client.OgnClientProperties.PROP_OGN_CLIENT_VALIDATE;

import java.util.Arrays;
import java.util.List;

import org.ogn.client.aprs.AprsOgnClient;
import org.ogn.commons.beacon.descriptor.AircraftDescriptorProvider;
import org.ogn.commons.utils.Configuration;

/**
 * This factory creates instances of OGN client. Several parameters can be tuned through the environment variables.
 * 
 * @author wbuczak
 */
public class OgnClientFactory {

	private static String	serverName			=
			Configuration.getValue(ENV_OGN_CLIENT_SRV_NAME, PROP_OGN_CLIENT_SRV_NAME, OGN_DEFAULT_SERVER_NAME);

	private static int		unfilteredPort		= Configuration.getIntValue(ENV_OGN_CLIENT_SRV_PORT_UNFILTERED,
			PROP_OGN_CLIENT_SRV_PORT_UNFILTERED, OGN_DEFAULT_SRV_PORT_UNFILTERED);

	private static int		filteredPort		= Configuration.getIntValue(ENV_OGN_CLIENT_SRV_PORT_FILTERED,
			PROP_OGN_CLIENT_SRV_PORT_FILTERED, OGN_DEFAULT_SRV_PORT_FILTERED);

	private static int		unfilteredSslPort	= Configuration.getIntValue(ENV_OGN_CLIENT_SRV_PORT_UNFILTERED,
			PROP_OGN_CLIENT_SRV_PORT_UNFILTERED, OGN_DEFAULT_SRV_SSL_PORT_UNFILTERED);

	private static int		filteredSslPort		= Configuration.getIntValue(ENV_OGN_CLIENT_SRV_PORT_FILTERED,
			PROP_OGN_CLIENT_SRV_PORT_FILTERED, OGN_DEFAULT_SRV_SSL_PORT_FILTERED);

	private static int		reconnectionTimeout	= Configuration.getIntValue(ENV_OGN_CLIENT_SRV_RECONNECTION_TIMEOUT,
			PROP_OGN_CLIENT_SRV_RECONNECTION_TIMEOUT, OGN_DEFAULT_RECONNECTION_TIMEOUT_MS);

	private static int		keepAliveInterval	= Configuration.getIntValue(ENV_OGN_CLIENT_KEEP_ALIVE_INTERVAL,
			PROP_OGN_CLIENT_KEEP_ALIVE_INTERVAL, OGN_CLIENT_DEFAULT_KEEP_ALIVE_INTERVAL_MS);

	private static String	appName				=

			Configuration.getValue(ENV_OGN_CLIENT_APP_NAME, PROP_OGN_CLIENT_APP_NAME, OGN_DEFAULT_APP_NAME);

	private static String	appVersion			=
			Configuration.getValue(ENV_OGN_CLIENT_APP_VERSION, PROP_OGN_CLIENT_APP_VERSION, OGN_DEFAULT_APP_VERSION);

	private static String	ognClientId			= Configuration.getValue(ENV_OGN_CLIENT_ID, PROP_OGN_CLIENT_ID);

	private static boolean	useSsl				=
			Configuration.getBooleanValue(ENV_OGN_CLIENT_USE_SSL, PROP_OGN_CLIENT_USE_SSL);
	private static boolean	ognClientValidate	=
			Configuration.getBooleanValue(ENV_OGN_CLIENT_VALIDATE, PROP_OGN_CLIENT_VALIDATE);

	private static String	aprsFilter			=
			Configuration.getValue(ENV_OGN_CLIENT_APRS_FILTER, PROP_OGN_CLIENT_APRS_FILTER);

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