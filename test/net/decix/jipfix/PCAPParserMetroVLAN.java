package net.decix.jipfix;

import net.decix.jipfix.header.DataRecord;
import net.decix.jipfix.header.L2IPDataRecord;
import net.decix.jipfix.header.MessageHeader;
import net.decix.jipfix.header.SetHeader;
import net.decix.util.HeaderParseException;
import net.decix.util.MacAddress;
import net.decix.util.UtilityException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapDumper;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UdpPacket;

public class PCAPParserMetroVLAN {  
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/2014-03-05_IPFIX_capture01.pcap"; // IPFIX-Test.pcap
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/IPFIX-Test.pcap";
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/ipfix-2014-09-30.pcap";
	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/ipfix-all-routers-2014-12-18.pcap";
	private static final String PCAP_FILE_WRITE = "/Users/tking/Downloads/test.pcap";
	
	private static PcapDumper pcapDumper;
	
	public static void main(String[] args) throws PcapNativeException, NotOpenException, InterruptedException {

		final PcapHandle handleRead = Pcaps.openOffline(PCAP_FILE_READ);
		pcapDumper = handleRead.dumpOpen(PCAP_FILE_WRITE);
		

		PacketListener listener = new PacketListener() {
			public void gotPacket(Packet fullPacket) {
				long timestampInts = handleRead.getTimestampInts();
				int timestampMicros = handleRead.getTimestampMicros();
				
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
					
					boolean containsOtherThan306SetID = false;
				 	for (SetHeader sh : mh.getSetHeaders()) {
						if (sh.getSetID() != 306) containsOtherThan306SetID = true;
					}
					if (containsOtherThan306SetID) {
						//System.out.printf("frame #%d%n", packet.getFrameNumber());
						System.out.println("Template?");
						System.out.println(mh);
						pcapDumper.dump(fullPacket, timestampInts, timestampMicros);
					} else {
						for (SetHeader sh : mh.getSetHeaders()) {
							for (DataRecord dr : sh.getDataRecords()) {
								if (dr instanceof L2IPDataRecord) {
									L2IPDataRecord lidr = (L2IPDataRecord) dr;
									//								if (lidr.getDestinationMacAddress().equals(new MacAddress("00:26:99:83:C6:90")) || lidr.getSourceMacAddress().equals(new MacAddress("00:26:99:83:C6:90"))) {
									if ((lidr.getPostDot1qCustomerVlanId() != 2) && (lidr.getPostDot1qCustomerVlanId() != 0)) {
										System.out.println(lidr);
										pcapDumper.dump(fullPacket, timestampInts, timestampMicros);
									}
								}
							}
						}
					}
				} catch (HeaderParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(0);
//				} catch (UtilityException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
				} catch (NotOpenException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		handleRead.loop(-1, listener);

		handleRead.close();
	}  
}  