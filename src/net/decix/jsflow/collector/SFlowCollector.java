/*
 * This file is part of jsFlow.
 *
 * Copyright (c) 2009 DE-CIX Management GmbH <http://www.de-cix.net> - All rights
 * reserved.
 * 
 * Author: Thomas King <thomas.king@de-cix.net>
 *
 * This software is licensed under the Apache License, version 2.0. A copy of 
 * the license agreement is included in this distribution.
 */
package net.decix.jsflow.collector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Vector;

import net.decix.jsflow.header.CounterRecordHeader;
import net.decix.jsflow.header.ExpandedCounterSampleHeader;
import net.decix.jsflow.header.ExpandedFlowSampleHeader;
import net.decix.jsflow.header.FlowRecordHeader;
import net.decix.jsflow.header.HeaderParseException;
import net.decix.jsflow.header.SampleDataHeader;
import net.decix.jsflow.header.SFlowHeader;

public class SFlowCollector {
	public static void main(String args[]) {
		try {
			DatagramSocket ds = new DatagramSocket(6343);
			while (true) {
				byte[] data = new byte[65536];
				DatagramPacket dp = new DatagramPacket(data, data.length);
				ds.receive(dp);
				SFlowHeader rph = SFlowHeader.parse(dp.getData());
				System.out.println(rph.getAddressAgent().toString());
				Vector<SampleDataHeader> sdhs = rph.getSampleDataHeaders();
				for (SampleDataHeader sdh : sdhs) {
					System.out.println("SampleDataHeader format: " + sdh.getSampleDataFormat());
					if (sdh.getSampleDataFormat() == SampleDataHeader.EXPANDEDFLOWSAMPLE) {
						ExpandedFlowSampleHeader efs = sdh.getExpandedFlowSampleHeader();
						Vector<FlowRecordHeader> frs = efs.getFlowRecords();
						for (FlowRecordHeader fr : frs) {
							System.out.println("Flow record header format: " + fr.getFlowDataFormat());
							System.out.println("Raw packet header protocol: " + fr.getRawPacketHeader().getHeaderProtocol());
						}
					}
					if (sdh.getSampleDataFormat() == SampleDataHeader.EXPANDEDCOUNTERSAMPLE) {
						ExpandedCounterSampleHeader ecs = sdh.getExpandedCounterSampleHeader();
						Vector<CounterRecordHeader> crs = ecs.getCounterRecords();
						for (CounterRecordHeader cr : crs) {
							System.out.println("Counter record header format: " + cr.getCounterDataFormat());
							if (cr.getEthernetInterfaceCounterHeader() != null) {
								System.out.println("Single collision frames: " + cr.getEthernetInterfaceCounterHeader().getDot3StatsSingleCollisionFrames());
							}
							if (cr.getGenericInterfaceCounterHeader() != null) {
								System.out.println("Interface index: " + cr.getGenericInterfaceCounterHeader().getIfIndex());
								System.out.println("Interface index: " + cr.getGenericInterfaceCounterHeader().getIfInUcastPkts());
							}
						}
					}
				}
			}
		} catch (SocketException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (HeaderParseException hpe) {
			hpe.printStackTrace();
		}
	}
}
