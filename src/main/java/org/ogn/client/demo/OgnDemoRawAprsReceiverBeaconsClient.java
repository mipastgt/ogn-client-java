/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.client.demo;

import static java.lang.System.out;

import org.ogn.client.OgnClient;
import org.ogn.client.OgnClientFactory;
import org.ogn.client.ReceiverBeaconListener;
import org.ogn.commons.beacon.ReceiverBeacon;

/**
 * A small demo program demonstrating the basic usage of the ogn-client.
 * 
 * @author wbuczak
 */
public class OgnDemoRawAprsReceiverBeaconsClient {

	static class RbListener implements ReceiverBeaconListener {
		@Override
		public void onUpdate(ReceiverBeacon beacon) {
			// simply print out raw beacon
			out.println(beacon.getRawPacket());
		}
	}

	public static void main(String[] args) throws Exception {
		final OgnClient client = OgnClientFactory.createClient();

		out.println("connecting...");

		client.connect();
		client.subscribeToReceiverBeacons(new RbListener());

		Thread.sleep(Long.MAX_VALUE);
	}

}