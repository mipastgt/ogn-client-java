/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.client;

/**
 * declarations of the environment properties which can be set to overwrite the client's defaults
 * 
 * @author wbuczak
 */
public interface OgnClientProperties {
	String	PROP_OGN_SRV_NAME						= "ogn.server.name";
	String	PROP_OGN_SRV_PORT_UNFILTERED			= "ogn.server.port_unfiltered";
	String	PROP_OGN_SRV_PORT_FILTERED				= "ogn.server.port_filtered";
	String	PROP_OGN_SRV_RECONNECTION_TIMEOUT		= "ogn.reconnection.timeout";

	String	PROP_OGN_CLIENT_APP_NAME				= "ogn.app.name";
	String	PROP_OGN_CLIENT_APP_VERSION				= "ogn.app.version";

	String	PROP_OGN_CLIENT_KEEP_ALIVE_INTERVAL		= "ogn.client.keep_alive";
	String	PROP_OGN_CLIENT_IGNORE_RECEIVER_BEACONS	= "ogn.client.ignore_receiver_beacons";
	String	PROP_OGN_CLIENT_IGNORE_AIRCRAFT_BEACONS	= "ogn.client.ignore_aircraft_beacons";
	String	PROP_OGN_CLIENT_APRS_FILTER				= "ogn.client.aprs.filter";
}
