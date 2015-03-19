package net.decix.jipfix.collector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import net.decix.jipfix.header.MessageHeader;
import net.decix.util.HeaderParseException;

public class IPFIXCollector {
	public static void main(String args[]) {
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(2055);
			while (true) {
				byte[] data = new byte[65536];
				DatagramPacket dp = new DatagramPacket(data, data.length);
				ds.receive(dp);
				MessageHeader mh = MessageHeader.parse(dp.getData());
				System.out.println(mh);
			}
		} catch (SocketException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (HeaderParseException hpe) {
			hpe.printStackTrace();
		} finally {
			if (ds != null) ds.close();
		}
	}
}
