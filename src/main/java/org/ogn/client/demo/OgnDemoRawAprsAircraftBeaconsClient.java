/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.client.demo;

import static java.lang.System.out;

import java.util.Optional;

import org.ogn.client.AircraftBeaconListener;
import org.ogn.client.OgnClient;
import org.ogn.client.OgnClientFactory;
import org.ogn.commons.beacon.AircraftBeacon;
import org.ogn.commons.beacon.AircraftDescriptor;

/**
 * A small demo program demonstrating the basic usage of the ogn-client.
 * 
 * @author wbuczak
 */
public class OgnDemoRawAprsAircraftBeaconsClient {

	static class AcListener implements AircraftBeaconListener {

		@Override
		public void onUpdate(AircraftBeacon beacon, Optional<AircraftDescriptor> descriptor) {
			// simply print out raw beacon
			out.println(beacon.getRawPacket());
		}
	}

	public static void main(String[] args) throws Exception {
		final OgnClient client = OgnClientFactory.createClient();

		out.println("connecting...");

		// all beacons except FNT*
		client.connect("t/* -p/FNT");

		client.subscribeToAircraftBeacons(new AcListener());
		Thread.sleep(Long.MAX_VALUE);
	}

}