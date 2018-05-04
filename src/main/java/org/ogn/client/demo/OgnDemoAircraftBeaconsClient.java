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
import org.ogn.commons.igc.IgcLogger;
import org.ogn.commons.utils.JsonUtils;

/**
 * A small demo program demonstrating the basic usage of the ogn-client.
 * 
 * @author wbuczak
 */
public class OgnDemoAircraftBeaconsClient {

	static {
		// aprs filter can be passed either by a jvm env variable or directly in the connect() method - see below
		// System.setProperty(OgnClientProperties.PROP_OGN_CLIENT_APRS_FILTER, "r/+49.782/+19.450/5");
	}

	static IgcLogger	igcLogger	= new IgcLogger();

	// enable if you want to log to IGC files
	static boolean		logIGC		= false;

	static class AcListener implements AircraftBeaconListener {

		@Override
		public void onUpdate(AircraftBeacon beacon, Optional<AircraftDescriptor> descriptor) {

			out.println("*********************************************");

			// print the beacon
			out.println(JsonUtils.toJson(beacon));

			// if the aircraft has been recognized print its descriptor too
			if (descriptor.isPresent()) {
				out.println(JsonUtils.toJson(descriptor.get()));
			}

			if (logIGC)
				igcLogger.log(beacon, descriptor);

			out.println("*********************************************");
		}
	}

	public static void main(String[] args) throws Exception {
		final OgnClient client = OgnClientFactory.createClient();

		System.out.println("connecting...");

		// client.connect("r/+51.537/+5.472/250");
		// client.connect("r/+49.782/+19.450/200");

		client.connect();

		client.subscribeToAircraftBeacons(new AcListener());

		Thread.sleep(Long.MAX_VALUE);
	}

}