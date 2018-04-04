/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.client;

import org.ogn.commons.beacon.ReceiverBeacon;

/**
 * Interface that needs to be implemented by all entities subscribing to ReceiverBeacons
 * 
 * @author wbuczak
 */
public interface ReceiverBeaconListener {
	/**
	 * @param beacon
	 *            receiver beacon
	 */
	void onUpdate(final ReceiverBeacon beacon);
}
