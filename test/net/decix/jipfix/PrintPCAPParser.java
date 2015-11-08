package net.decix.jipfix;

import net.decix.jipfix.header.DataRecord;
import net.decix.jipfix.header.L2IPDataRecord;
import net.decix.jipfix.header.MessageHeader;
import net.decix.jipfix.header.SetHeader;
import net.decix.util.HeaderBytesException;
import net.decix.util.HeaderParseException;
import net.decix.util.Utility;
import net.decix.util.UtilityException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapDumper;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.util.MacAddress;

public class PrintPCAPParser {  
	private static final String PCAP_FILE_READ = "/Volumes/Transcend/ipfix-2014-04-14.3.pcap";
	
	public static void main(String[] args) throws PcapNativeException, NotOpenException, InterruptedException {
		PcapHandle handleRead = Pcaps.openOffline(PCAP_FILE_READ);

		PacketListener listener = new PacketListener() {
			public void gotPacket(Packet fullPacket) {
//				System.out.println(packet);
				UdpPacket udpPacket = fullPacket.get(UdpPacket.class);
				if (udpPacket == null) return;
//				System.out.println(packet);
				
				byte[] bytes = udpPacket.getRawData();
				byte[] onlyIPFIX = new byte[bytes.length - 8];
				System.arraycopy(bytes, 8, onlyIPFIX, 0, bytes.length - 8);

				try {
					MessageHeader mh = MessageHeader.parse(onlyIPFIX);
					//System.out.println(mh);
					
//					if (onlyIPFIX.length != mh.getBytes().length) {
//						System.out.println("Lenght: OnlyIPFIX: " + onlyIPFIX.length + " : Generated: " + mh.getBytes().length);
//					}

					boolean containsOtherThan306SetID = false;
				 	for (SetHeader sh : mh.getSetHeaders()) {
						if (sh.getSetID() != 306) containsOtherThan306SetID = true;
					}
					if (containsOtherThan306SetID) {
						//System.out.printf("frame #%d%n", packet.getFrameNumber());
						//System.out.println("Template?");
					} else {
						for (SetHeader sh : mh.getSetHeaders()) {
							for (DataRecord dr : sh.getDataRecords()) {
								if (dr instanceof L2IPDataRecord) {
									L2IPDataRecord lidr = (L2IPDataRecord) dr;
//									net.decix.util.MacAddress macSearch = new net.decix.util.MacAddress("6c:9c:ed:76:91:93");
//									if (((L2IPDataRecord) dr).getSourceMacAddress().equals(macSearch) || ((L2IPDataRecord) dr).getDestinationMacAddress().equals(macSearch)) {
										if ((lidr.getDot1qVlanId() != 0) && (lidr.getDot1qCustomerVlanId() != 0)) {
											System.out.println(lidr);
										}
//									}
								}
							}
						}
					}
				} catch (HeaderParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
//				} catch (UtilityException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			}
		};

		handleRead.loop(-1, listener);

		handleRead.close();
	}  
}  
