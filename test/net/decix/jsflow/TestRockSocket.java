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
package net.decix.jsflow;

import java.net.InetAddress;

import org.savarese.vserv.tcpip.IPPacket;
import org.savarese.vserv.tcpip.OctetConverter;
import org.savarese.vserv.tcpip.UDPPacket;

import com.savarese.rocksaw.net.RawSocket;

public class TestRockSocket {
	public static void main(String args[]) {
		try {
			byte[] payload = "Hallo Welt!".getBytes();
			
			RawSocket socket = new RawSocket();
			socket.open(RawSocket.PF_INET, RawSocket.getProtocolByName("udp"));
			socket.setIPHeaderInclude(true);
			 
			
			
			UDPPacket udp = new UDPPacket(28 + payload.length);
			udp.setIPVersion(4);
            udp.setIPHeaderLength(5);
            udp.setProtocol(IPPacket.PROTOCOL_UDP);
            udp.setTTL(5);
            
            udp.setUDPDataByteLength(payload.length);
            udp.setUDPPacketLength(payload.length + 8);
			
            udp.setDestinationPort(1234);
			udp.setSourcePort(1234);
			
			udp.setDestinationAsWord(OctetConverter.octetsToInt(InetAddress.getByName("www.t-king.de").getAddress()));
			udp.setSourceAsWord(OctetConverter.octetsToInt(InetAddress.getByName("www.t-king.de").getAddress()));
						
			byte[] data = new byte[udp.size()];
			udp.getData(data);
			System.arraycopy(payload, 0, data, 28, payload.length);
            udp.setData(data);
            udp.computeUDPChecksum(true);
            udp.computeIPChecksum(true);
            udp.getData(data);

			socket.write(InetAddress.getByName("www.de-cix.net"), data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
