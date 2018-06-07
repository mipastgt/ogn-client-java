/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.client;

/**
 * declarations of the environment variables and system properties which can be set to overwrite the client's defaults
 * 
 * @author wbuczak
 */
public interface OgnClientProperties {

	String	ENV_OGN_CLIENT_SRV_NAME						= "OGN_CLIENT_SRV_NAME";
	String	PROP_OGN_CLIENT_SRV_NAME					= "ogn.client.srv.name";

	String	ENV_OGN_CLIENT_SRV_PORT_UNFILTERED			= "OGN_CLIENT_SRV_PORT_UNFILTERED";
	String	PROP_OGN_CLIENT_SRV_PORT_UNFILTERED			= "ogn.client.srv.port_unfiltered";

	String	ENV_OGN_CLIENT_SRV_PORT_FILTERED			= "OGN_CLIENT_SRV_PORT_FILTERED";
	String	PROP_OGN_CLIENT_SRV_PORT_FILTERED			= "ogn.client.srv.port_filtered";

	String	ENV_OGN_CLIENT_SRV_RECONNECTION_TIMEOUT		= "OGN_CLIENT_SRV_RECONNECTION_TIMEOUT";
	String	PROP_OGN_CLIENT_SRV_RECONNECTION_TIMEOUT	= "ogn.client.reconnection_timeout";

	String	ENV_OGN_CLIENT_APP_NAME						= "OGN_CLIENT_APP_NAME";
	String	PROP_OGN_CLIENT_APP_NAME					= "ogn.client.app_name";

	String	ENV_OGN_CLIENT_APP_VERSION					= "OGN_CLIENT_APP_VERSION";
	String	PROP_OGN_CLIENT_APP_VERSION					= "ogn.client.app_version";

	String	ENV_OGN_CLIENT_KEEP_ALIVE_INTERVAL			= "OGN_CLIENT_KEEP_ALIVE_INTERVAL";
	String	PROP_OGN_CLIENT_KEEP_ALIVE_INTERVAL			= "ogn.client.keep_alive";

	String	ENV_OGN_CLIENT_APRS_FILTER					= "OGN_CLIENT_APRS_FILTER";
	String	PROP_OGN_CLIENT_APRS_FILTER					= "ogn.client.aprs.filter";

	String	ENV_OGN_CLIENT_ID							= "OGN_CLIENT_ID";
	String	PROP_OGN_CLIENT_ID							= "ogn.client.id";

	String	ENV_OGN_CLIENT_VALIDATE						= "OGN_CLIENT_VALIDATE";
	String	PROP_OGN_CLIENT_VALIDATE					= "ogn.client.validate";

	String	ENV_OGN_CLIENT_USE_SSL						= "OGN_CLIENT_USE_SSL";
	String	PROP_OGN_CLIENT_USE_SSL						= "ogn.client.use_ssl";
}
