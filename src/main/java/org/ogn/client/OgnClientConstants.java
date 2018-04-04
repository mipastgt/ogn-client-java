/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.client;

public interface OgnClientConstants {
	String	OGN_DEFAULT_SERVER_NAME						= "aprs.glidernet.org";
	String	OGN_DEFAULT_APP_NAME						= "ogn-client-java";
	String	OGN_DEFAULT_APP_VERSION						= "1.0.0";

	Integer	OGN_DEFAULT_SRV_PORT						= 10152;
	Integer	OGN_DEFAULT_SRV_PORT_FILTERED				= 14580;

	Integer	OGN_DEFAULT_RECONNECTION_TIMEOUT_MS			= 5000;

	// default connection keep alive message interval (5 min)
	Integer	OGN_CLIENT_DEFAULT_KEEP_ALIVE_INTERVAL_MS	= 5 * 60 * 1000;
}