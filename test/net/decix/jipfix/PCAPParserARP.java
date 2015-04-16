package net.decix.jipfix;

import net.decix.jipfix.header.DataRecord;
import net.decix.jipfix.header.L2IPDataRecord;
import net.decix.jipfix.header.MessageHeader;
import net.decix.jipfix.header.SetHeader;
import net.decix.util.HeaderParseException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UdpPacket;

public class PCAPParserARP {  
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/2014-03-05_IPFIX_capture01.pcap"; // IPFIX-Test.pcap
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/IPFIX-Test.pcap";
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/test.pcap";
	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/ipfix-all-routers-2014-12-18.pcap";
	
	
	public static void main(String[] args) throws PcapNativeException, NotOpenException, InterruptedException {

		PcapHandle handleRead = Pcaps.openOffline(PCAP_FILE_READ);
		

		PacketListener listener = new PacketListener() {
			public void gotPacket(Packet fullPacket) {
//				System.out.println(packet);
				UdpPacket udpPacket = fullPacket.get(UdpPacket.class);
				if (udpPacket == null) return;
//				if (udpPacket.getHeader().getDstPort().value() != 2055) return;
//				System.out.println(packet);
				
				byte[] bytes = udpPacket.getRawData();
				byte[] onlyIPFIX = new byte[bytes.length - 8];
				System.arraycopy(bytes, 8, onlyIPFIX, 0, bytes.length - 8);

				try {
					MessageHeader mh = MessageHeader.parse(onlyIPFIX);
					
					for (SetHeader sh : mh.getSetHeaders()) {
						for (DataRecord dr : sh.getDataRecords()) {
							if (dr instanceof L2IPDataRecord) {
								
								L2IPDataRecord lidr = (L2IPDataRecord) dr;
								
								if (lidr.getSourceTransportPort() == 0) {
									System.out.println(lidr);
								}
								
//								if ((!Utility.isConfigured(lidr.getDestinationIPv4Address())) &&
//									(!Utility.isConfigured(lidr.getSourceIPv4Address())) &&
//									(!Utility.isConfigured(lidr.getDestinationIPv6Address())) &&
//									(!Utility.isConfigured(lidr.getSourceIPv6Address()))) {
//									System.out.println(lidr);
//								}
							}
						}
					}
				} catch (HeaderParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
//				} catch (UtilityException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			}
		};

		handleRead.loop(-1, listener);

		handleRead.close();
	}  
}  