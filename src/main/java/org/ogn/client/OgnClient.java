/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.client;

public interface OgnClient {

	/**
	 * subscribes a listener to the aircraft beacons
	 * 
	 * @param listener
	 */
	void subscribeToAircraftBeacons(AircraftBeaconListener listener);

	/**
	 * unsubscribes a listener from receiving aircraft beacons
	 * 
	 * @param listener
	 */
	void unsubscribeFromAircraftBeacons(AircraftBeaconListener listener);

	/**
	 * subscribes a listener to the base stations beacons
	 * 
	 * @param listener
	 */
	void subscribeToReceiverBeacons(ReceiverBeaconListener listener);

	/**
	 * unsubscribes a listener from receiving base stations beacons
	 * 
	 * @param listener
	 */
	void unsubscribeFromReceiverBeacons(ReceiverBeaconListener listener);

	/**
	 * connects the client to the OGN service (no filtering)
	 */
	void connect();

	/**
	 * connects to the OGN service
	 * 
	 * @param filter
	 *            optional filter, if null no filter will be used, as it is in case of {@link #connect()}
	 */
	void connect(String filter);

	/**
	 * disconnects a client from the OGN service
	 */
	void disconnect();
}