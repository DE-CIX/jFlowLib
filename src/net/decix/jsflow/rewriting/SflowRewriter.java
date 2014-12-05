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
package net.decix.jsflow.rewriting;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import net.decix.jsflow.header.ExpandedFlowSampleHeader;
import net.decix.jsflow.header.FlowRecordHeader;
import net.decix.jsflow.header.HeaderBytesException;
import net.decix.jsflow.header.HeaderParseException;
import net.decix.jsflow.header.SampleDataHeader;
import net.decix.jsflow.header.SflowHeader;
import net.decix.util.Address;
import net.decix.util.AddressPort;
import net.decix.util.MacAddress;
import net.decix.util.Utility;
import net.decix.util.UtilityException;

import org.savarese.vserv.tcpip.IPPacket;
import org.savarese.vserv.tcpip.OctetConverter;
import org.savarese.vserv.tcpip.UDPPacket;
import org.xml.sax.SAXException;

import com.savarese.rocksaw.net.RawSocket;

public class SflowRewriter {
	private static String mapPath = "/opt/jsflow-rewriter/var/lib/";
	private static String cfgPath = "/opt/jsflow-rewriter/etc";
	
	public static void main(String args[]) {
		try {
			if (args.length == 0) {
				System.out.println("Usage: java -Djava.library.path=./ -jar jsflow-rewriter.jar [options]\n");
				System.out.println("Options:");
				System.out.println("        -map: path where the mapping.txt file will be stored");
				System.out.println("        -cfg: path to the config.xml file");
				System.out.println();
				System.exit(0);
			}
			if ((args.length == 2) || (args.length == 4)) {
				if (args[0].equals("-map")) {
					mapPath = args[1];
				}
				if (args[0].equals("-cfg")) {
					cfgPath = args[1];
				}
				if (args.length == 4) {
					if (args[2].equals("-map")) {
						mapPath = args[3];
					}
					if (args[2].equals("-cfg")) {
						cfgPath = args[3];
					}
				}
			}
			
			
			MacInterfaceIndexMapper miim = new MacInterfaceIndexMapper(new File(mapPath + File.separator + "mapping.txt"));
			miim.loadMapping();
			
			ConfigParser cp = new ConfigParser();
			cp.loadConfig(cfgPath);
			
			// listen on this address and port to receive sflow data coming from switches
			Address listenAddress = cp.getListenAddress();
			int listenPort = cp.getListenPort();
						
			// list of destinations of plain sflow stream
			Vector<AddressPort> plainDestinations = cp.getPlainDestinations();
			
			// Input IP -> Destination of plain sflow stream
			HashMap<MacAddress, AddressPort> plainFilterDestinations = cp.getPlainFilterDestinations();
			
			// list of destinations of rewritten sflow stream
			Vector<AddressPort> rewrittenDestinations = cp.getRewrittenDestinations();
			
			// Input IP -> Destination of rewritten sflow stream
			HashMap<MacAddress, AddressPort> rewrittenFilterDestinations = cp.getRewrittenFilterDestinations();
			
			DatagramSocket ds = new DatagramSocket(listenPort, listenAddress.getInetAddress());
			System.out.println("Datagram socket for receiving created.");

			RawSocket socket = new RawSocket();
			socket.open(RawSocket.PF_INET, RawSocket.getProtocolByName("udp"));
			socket.setIPHeaderInclude(true);
			System.out.println("Raw socket for sending created.");
			
			DatagramPacket dp = null;
			while (true) {
				try {
					byte[] data = new byte[2500];
					dp = new DatagramPacket(data, data.length);
					ds.receive(dp);
					SflowHeader rph = SflowHeader.parse(dp.getData());
									
					byte[] payload = rph.getBytes();

					UDPPacket udp = new UDPPacket(28 + payload.length);
					udp.setIPVersion(4);
					udp.setIPHeaderLength(5);
					udp.setProtocol(IPPacket.PROTOCOL_UDP);
					udp.setTTL(5);

					udp.setUDPDataByteLength(payload.length);
					udp.setUDPPacketLength(payload.length + 8);

					udp.setDestinationPort(6343);
					udp.setSourcePort(6343);
					
					byte[] pktData = new byte[udp.size()];
					
					// plain MUXER part
					List<AddressPort> p = (List<AddressPort>) plainDestinations.clone();
					if (plainFilterDestinations.containsKey(rph.getAddressAgent())) p.add(plainFilterDestinations.get(rph.getAddressAgent()));
					for (AddressPort ap : p) {
						udp.setDestinationAsWord(OctetConverter.octetsToInt(ap.getAddress().getBytes()));
						udp.setSourceAsWord(OctetConverter.octetsToInt(rph.getAddressAgent().getBytes()));
						udp.setDestinationPort(ap.getPort());
						udp.getData(pktData);
						System.arraycopy(payload, 0, pktData, 28, payload.length);
						udp.setData(pktData);
						udp.computeUDPChecksum(true);
						udp.computeIPChecksum(true);
						udp.getData(pktData);
						socket.write(ap.getAddress().getInetAddress(), pktData);
					}
					
										
					// REWRITING part
					// do the actual rewriting:
					// 1. manage mac <-> interface index
					// 2. rewrite the interface index
					Vector<SampleDataHeader> sdhs = rph.getSampleDataHeaders();
					for (SampleDataHeader sdh : sdhs) {
						if (sdh.getSampleDataFormat() == SampleDataHeader.EXPANDEDFLOWSAMPLE) {
							ExpandedFlowSampleHeader efs = sdh.getExpandedFlowSampleHeader();
							Vector<FlowRecordHeader> frs = efs.getFlowRecords();
							for (FlowRecordHeader fr : frs) {
								long macSource = fr.getRawPacketHeader().getMacHeader().getSource(); 
								miim.checkMac(macSource);
								efs.setInputInterfaceValue(miim.getInterfaceIndex(macSource));
								long macDestination = fr.getRawPacketHeader().getMacHeader().getDestination();
								miim.checkMac(macDestination);
								efs.setOutputInterfaceValue(miim.getInterfaceIndex(macDestination));
							}
						}
					}

					// rewritten MUXER part
					List<AddressPort> r = (List<AddressPort>) rewrittenDestinations.clone();
					if (rewrittenFilterDestinations.containsKey(rph.getAddressAgent())) r.add(rewrittenFilterDestinations.get(rph.getAddressAgent()));
					for (AddressPort ap : r) {
						udp.setDestinationAsWord(OctetConverter.octetsToInt(ap.getAddress().getBytes()));
						udp.setDestinationPort(ap.getPort());
						payload = rph.getBytes();
						
						System.arraycopy(payload, 0, pktData, 28, payload.length);
						udp.setData(pktData);
						udp.computeUDPChecksum(true);
						udp.computeIPChecksum(true);
						udp.getData(pktData);
						
						socket.write(ap.getAddress().getInetAddress(), pktData);
					}
				} catch (HeaderParseException hpe) {
					hpe.printStackTrace();
					System.out.println("========================================================");
					System.out.println("HeaderParseException: *");
					System.out.println(Utility.dumpBytes(dp.getData()));
					System.out.println("========================================================");
				}
			}
		} catch (SocketException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (HeaderBytesException hbe) {
			hbe.printStackTrace();
		} catch (UtilityException ue) {
			ue.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
	}
}