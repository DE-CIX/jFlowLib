package net.decix.jipfix.detector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UdpPacket;

import net.decix.jipfix.header.MessageHeader;
import net.decix.util.HeaderParseException;

public class IPFIXMissingDataRecordDetector {
	private static final String PCAP_FILE_READ = "/Volumes/Transcend/ipfix-2014-04-14.2.pcap";
	
	public static void main(String args[]) throws SecurityException, IOException, PcapNativeException, InterruptedException, NotOpenException {
		FileHandler fh = new FileHandler("ipfix-detector.log", 5 * 10485760, 20, true); // 20 x 50MByte
		fh.setFormatter(new SimpleFormatter());
		Logger l = Logger.getLogger("");
		l.addHandler(fh);
		l.setLevel(Level.FINEST);
		
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(2056);
			
			final GroupMissingDataRecordDetector gmdrd = new GroupMissingDataRecordDetector();
			
			while (true) {
				boolean file = false;
				if (!file) {
				byte[] data = new byte[65536];
					DatagramPacket dp = new DatagramPacket(data, data.length);
					ds.receive(dp);
					
					MessageHeader mh;
					try {
						mh = MessageHeader.parse(dp.getData());
						gmdrd.detectMissing(dp, mh);
					} catch (HeaderParseException e) {
						e.printStackTrace();
					}
				} else {
					PcapHandle handleRead = Pcaps.openOffline(PCAP_FILE_READ);

					PacketListener listener = new PacketListener() {
						public void gotPacket(Packet fullPacket) {
							UdpPacket udpPacket = fullPacket.get(UdpPacket.class);
							IpV4Packet ipv4Packet = fullPacket.get(IpV4Packet.class);
							DatagramPacket dp = new DatagramPacket(new byte[0], 0);
							dp.setAddress(ipv4Packet.getHeader().getSrcAddr());

							byte[] bytes = udpPacket.getRawData();
							byte[] onlyIPFIX = new byte[bytes.length - 8];
							System.arraycopy(bytes, 8, onlyIPFIX, 0, bytes.length - 8);

							try {
								MessageHeader mh = MessageHeader.parse(onlyIPFIX);
								gmdrd.detectMissing(dp, mh);
							} catch (HeaderParseException e) {
								e.printStackTrace();
							}
						}
					};

					handleRead.loop(-1, listener);
					handleRead.close();
				}
			}
		} catch (SocketException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (ds != null) ds.close();
		}
	}
}
