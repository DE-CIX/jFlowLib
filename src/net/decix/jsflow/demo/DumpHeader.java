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
package net.decix.jsflow.demo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Vector;

import net.decix.jsflow.header.CounterRecordHeader;
import net.decix.jsflow.header.EthernetInterfaceCounterHeader;
import net.decix.jsflow.header.ExpandedCounterSampleHeader;
import net.decix.jsflow.header.ExpandedFlowSampleHeader;
import net.decix.jsflow.header.FlowRecordHeader;
import net.decix.jsflow.header.GenericInterfaceCounterHeader;
import net.decix.jsflow.header.HeaderParseException;
import net.decix.jsflow.header.SampleDataHeader;
import net.decix.jsflow.header.SFlowHeader;
import net.decix.util.Utility;

public class DumpHeader {

	public static void main(String args[]) {
		try {
			DatagramSocket ds = new DatagramSocket(6343);
			System.out.println("Datagram socket for receiving created.");

			DatagramPacket dp = null;
			while (true) {
				try {
					byte[] data = new byte[2500];
					dp = new DatagramPacket(data, data.length);
					ds.receive(dp);
					System.out.println();
					System.out.println("Pkt recvd: " + dp.getAddress().toString() + ":" + dp.getPort() + " -> " + ds.getLocalAddress().toString() + ":" + ds.getLocalPort());
					SFlowHeader rph = SFlowHeader.parse(dp.getData());
					Vector<SampleDataHeader> sdhs = rph.getSampleDataHeaders();
					sdhs = rph.getSampleDataHeaders();
					for (SampleDataHeader sdh : sdhs) {
						if (sdh.getSampleDataFormat() == SampleDataHeader.EXPANDEDFLOWSAMPLE) {
							ExpandedFlowSampleHeader efsh = sdh.getExpandedFlowSampleHeader();
							Vector<FlowRecordHeader> frhs = efsh.getFlowRecords();
							for (FlowRecordHeader frh : frhs) {
								frh.getRawPacketHeader().getHeaderProtocol();
								long macSource = frh.getRawPacketHeader().getMacHeader().getSource();
								long macDestination = frh.getRawPacketHeader().getMacHeader().getSource();
								System.out.println("FlowRecord: Communication " + macSource + "->" + macDestination);
							}
						}
						if (sdh.getSampleDataFormat() == SampleDataHeader.EXPANDEDCOUNTERSAMPLE) {
							ExpandedCounterSampleHeader ecsh = sdh.getExpandedCounterSampleHeader();
							Vector<CounterRecordHeader> crhs = ecsh.getCounterRecords();
							for (CounterRecordHeader crh : crhs) {
								if (crh.getCounterDataFormat() == CounterRecordHeader.ETHERNETINTERFACECOUNTER) {
									EthernetInterfaceCounterHeader eich = crh.getEthernetInterfaceCounterHeader();
									System.out.println("If: " + ecsh.getSourceIDIndex());
									System.out.println("   Excessive Collisions: " + eich.getDot3StatsExcessiveCollisions() + " Late Collisions: " + eich.getDot3StatsLateCollisions());
								}
								if (crh.getCounterDataFormat() == CounterRecordHeader.GENERICINTERFACECOUNTER) {
									GenericInterfaceCounterHeader gic = crh.getGenericInterfaceCounterHeader();
									System.out.println("If: " + gic.getIfIndex() + " Direction: " + gic.getIfDirection());
									System.out.println("    In: Errs: " + gic.getIfInErrors() + " Unicast: " + gic.getIfInUcastPkts() + " Multicast: " + gic.getIfInMulticastPkts() + " Broadcast: " + gic.getIfInBroadcastPkts());
									System.out.println("   Out: Errs: " + gic.getIfOutErrors() + " Unicast: " + gic.getIfOutUcastPkts() + " Multicast: " + gic.getIfOutMulticastPkts() + " Broadcast: " + gic.getIfOutBroadcastPkts());
								}
							}
						}
					}
				} catch (HeaderParseException hpe) {
					hpe.printStackTrace();
					System.out.println(Utility.dumpBytes(dp.getData()));
				}
			}
		} catch (SocketException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}