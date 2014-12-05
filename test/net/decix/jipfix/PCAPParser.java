package net.decix.jipfix;

import java.util.HashSet;
import java.util.Set;

import net.decix.jipfix.header.DataRecord;
import net.decix.jipfix.header.L2IPDataRecord;
import net.decix.jipfix.header.MessageHeader;
import net.decix.jipfix.header.SetHeader;
import net.decix.jsflow.header.HeaderBytesException;
import net.decix.jsflow.header.HeaderParseException;
import net.decix.util.MacAddress;
import net.decix.util.Utility;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapDumper;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.packet.UnknownPacket;

public class PCAPParser {  
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/2014-03-05_IPFIX_capture01.pcap"; // IPFIX-Test.pcap
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/IPFIX-Test.pcap";
	private static final String PCAP_FILE_READ = "c:\\tmp\\trace.pcap";
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/ipfix-2014-04-14.2.pcap";
//	private static final String PCAP_FILE_WRITE = "/Users/tking/Downloads/IPFIX-Test_Created_By_JIPFIX.pcap";
	private static final String PCAP_FILE_WRITE = "c:\\tmp\\trace_out.pcap";
//	private static final String PCAP_FILE_WRITE = "/Users/tking/Downloads/IPFIX-Test_Created_By_JIPFIX2.pcap";
	
//	private static PcapHandle handleWrite;
	private static PcapDumper dumper;
	
	public static void main(String[] args) throws PcapNativeException, NotOpenException, InterruptedException {

		final Set<Long> observationDomainIDs = new HashSet<Long>();
		final Set<MacAddress> srcMacAddresses = new HashSet<MacAddress>();
		final Set<MacAddress> destMacAddresses = new HashSet<MacAddress>();

		PcapHandle handleRead = Pcaps.openOffline(PCAP_FILE_READ);
		
//		handleWrite = Pcaps.openOffline(PCAP_FILE_WRITE);
		dumper = handleRead.dumpOpen(PCAP_FILE_WRITE);

		PacketListener listener = new PacketListener() {
			public void gotPacket(Packet fullPacket) {
//				System.out.println(packet);
				UdpPacket udpPacket = fullPacket.get(UdpPacket.class);
				if (udpPacket == null) return;
				if (udpPacket.getHeader().getDstPort().value() != 2056) return;
//				System.out.println(packet);
				
				byte[] bytes = udpPacket.getRawData();
				byte[] onlyIPFIX = new byte[bytes.length - 8];
				System.arraycopy(bytes, 8, onlyIPFIX, 0, bytes.length - 8);

				try {
					MessageHeader mh = MessageHeader.parse(onlyIPFIX);
										
					Packet.Builder udpB = fullPacket.get(UdpPacket.class).getBuilder();
					UnknownPacket.Builder upB = new UnknownPacket.Builder();
					upB.rawData(mh.getBytes());
					udpB.payloadBuilder(upB);

					Packet.Builder ipB = fullPacket.get(IpV4Packet.class).getBuilder();
					ipB.payloadBuilder(udpB);

					Packet.Builder etherB = fullPacket.get(EthernetPacket.class).getBuilder();
					etherB.payloadBuilder(ipB);

					Packet newPacket = etherB.build();
					dumper.dump(newPacket, 0l, 0);
					
					if (onlyIPFIX.length != mh.getBytes().length) {
						System.out.println("Lenght: OnlyIPFIX: " + onlyIPFIX.length + " : Generated: " + mh.getBytes().length);
					}

					boolean containsOtherThan306SetID = false;
				 	for (SetHeader sh : mh.getSetHeaders()) {
						if (sh.getSetID() != 306) containsOtherThan306SetID = true;
					}
					if (containsOtherThan306SetID) {
						//System.out.printf("frame #%d%n", packet.getFrameNumber());
						System.out.println("Template?");
					} else {
						//								Random r = new Random();
						//								int i = r.nextInt(100);
						//								if (i <= 0) {							
						//									System.out.printf("frame #%d%n", packet.getFrameNumber());
						//									System.out.println(mh);
						//								}
						observationDomainIDs.add(mh.getObservationDomainID());
						for (SetHeader sh : mh.getSetHeaders()) {
							for (DataRecord dr : sh.getDataRecords()) {
								if (dr instanceof L2IPDataRecord) {
									L2IPDataRecord lidr = (L2IPDataRecord) dr;
									srcMacAddresses.add(lidr.getSourceMacAddress());
									destMacAddresses.add(lidr.getDestinationMacAddress());

									if (Utility.isConfigured(lidr.getDestinationIPv6Address())) {
										//												System.out.println(mh);			
									}
								}
							}
						}
					}
				} catch (HeaderParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HeaderBytesException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotOpenException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		handleRead.loop(-1, listener);

		handleRead.close();
		
		dumper.close();

		System.out.println("\nObservation domain IDs: (" + observationDomainIDs.size() + ")");
		for (Long id : observationDomainIDs) {
			System.out.println(id);
		}
		System.out.println("\nSrc MacAddresses: (" + srcMacAddresses.size() + ")");
		for (MacAddress ma : srcMacAddresses) {
			System.out.println(ma);
		}
		System.out.println("\nDest MacAddresses: (" + destMacAddresses.size() + ")");
		for (MacAddress ma : destMacAddresses) {
			System.out.println(ma);
		}
	}  
}  
