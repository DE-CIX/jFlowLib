package net.decix.jipfix;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.decix.jipfix.header.DataRecord;
import net.decix.jipfix.header.L2IPDataRecord;
import net.decix.jipfix.header.MessageHeader;
import net.decix.jipfix.header.SamplingDataRecord;
import net.decix.jipfix.header.SetHeader;
import net.decix.jsflow.header.HeaderBytesException;
import net.decix.jsflow.header.HeaderParseException;
import net.decix.jsflow.util.IPv4AddressRandomizer;
import net.decix.jsflow.util.IPv6AddressRandomizer;
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

public class PCAPanonyminizer {  
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/2014-03-05_IPFIX_capture01.pcap"; // IPFIX-Test.pcap
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/IPFIX-Test.pcap";
	private static final String PCAP_FILE_READ = "c:\\tmp\\trace.pcap";
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/ipfix-2014-04-14.2.pcap";
//	private static final String PCAP_FILE_WRITE = "/Users/tking/Downloads/IPFIX-Test_Created_By_JIPFIX.pcap";
	private static final String PCAP_FILE_WRITE = "c:\\tmp\\trace_out.pcap";
//	private static final String PCAP_FILE_WRITE = "/Users/tking/Downloads/IPFIX-Test_Created_By_JIPFIX2.pcap";
	
//	private static PcapHandle handleWrite;
	private static PcapDumper pcapDumper;

	
	public static void main(String[] args) throws PcapNativeException, NotOpenException, InterruptedException {

		final Set<Long> observationDomainIDs = new HashSet<Long>();
		final Set<MacAddress> srcMacAddresses = new HashSet<MacAddress>();
		final Set<MacAddress> destMacAddresses = new HashSet<MacAddress>();

		PcapHandle pcapHandleReadOffline = Pcaps.openOffline(PCAP_FILE_READ);
		
//		handleWrite = Pcaps.openOffline(PCAP_FILE_WRITE);
		pcapDumper = pcapHandleReadOffline.dumpOpen(PCAP_FILE_WRITE);



		
		PacketListener packetListener = new PacketListener() {
			int dumpedCounter = 0;
			IPv4AddressRandomizer ipV4randomizer = new IPv4AddressRandomizer();
			IPv6AddressRandomizer ipV6randomizer = new IPv6AddressRandomizer();

			public void gotPacket(Packet fullPacket) {

//				System.out.println(packet);
				UdpPacket udpPacket = fullPacket.get(UdpPacket.class);

				if (udpPacket == null) return;
				if (udpPacket.getHeader().getDstPort().value() != 2056) return; // discard all packets which are not IPFIX
//				System.out.println(packet);

				
				byte[] rawUpdPacketBytes = udpPacket.getRawData();
				byte[] onlyIPFIXbytes = new byte[rawUpdPacketBytes.length - 8];
				System.arraycopy(rawUpdPacketBytes, 8, onlyIPFIXbytes, 0, rawUpdPacketBytes.length - 8);
				


				try {
					MessageHeader messageHeader = MessageHeader.parse(onlyIPFIXbytes);
					List <SetHeader> setHeaders = messageHeader.getSetHeaders();
					for (SetHeader currentSetHeader : setHeaders) {
						List<DataRecord> dataRecords = currentSetHeader.getDataRecords();
						for (DataRecord currentDataRecord : dataRecords) {
							
							try {
									boolean foundipv6 = false;
									boolean foundipv4 = false;
									if (currentDataRecord instanceof L2IPDataRecord) {
										L2IPDataRecord l2IPDataRecord = (L2IPDataRecord) currentDataRecord;
										//System.out.println(l2IPDataRecord);
										if (!l2IPDataRecord.getDestinationIPv6Address().toString().equals("/0:0:0:0:0:0:0:0")) {
											// /2a03:2880:f01c:301:face:b00c:0:1
											// /2a02:26f0:64:0:0:0:170e:5c09
											// /2a02:26f0:64:0:0:0:170e:5c09
											//System.out.println(l2IPDataRecord.getDestinationIPv6Address());
											foundipv6 = true;
										}
										//System.out.println(l2IPDataRecord.getDestinationIPv4Address());
										if (!l2IPDataRecord.getDestinationIPv4Address().toString().equals("/0.0.0.0")) {
											foundipv4 = true;
										}
										//System.out.println(l2IPDataRecord.getDestinationIPv4Address());		

										Inet4Address realDestinationIpv4 = l2IPDataRecord.getDestinationIPv4Address();
										Inet4Address realSourceIpv4 = l2IPDataRecord.getSourceIPv4Address();
										
										Inet6Address realDestinationIpv6 = l2IPDataRecord.getDestinationIPv6Address();
										Inet6Address realSourceIpv6 = l2IPDataRecord.getSourceIPv6Address();
										
		
										
										
										Inet4Address fakeDestinationIpv4 = realDestinationIpv4;
										Inet4Address fakeSourceIpv4 = realSourceIpv4;
										
										Inet6Address fakeDestinationIpv6 = realDestinationIpv6;
										Inet6Address fakeSourceIpv6 = realSourceIpv6;
										
										
										
										// do faking here
										
										if (foundipv4) {
											fakeDestinationIpv4 = (Inet4Address) ipV4randomizer.anomyzeAddress(realDestinationIpv4);
											fakeSourceIpv4 = (Inet4Address) ipV4randomizer.anomyzeAddress(realSourceIpv4);
											
											l2IPDataRecord.setDestinationIPv4Address(fakeDestinationIpv4);
											l2IPDataRecord.setSourceIPv4Address(fakeSourceIpv4);
										}
										
										
										if (foundipv6) {
										
											fakeSourceIpv6 = (Inet6Address) ipV6randomizer.anomyzeAddress(realSourceIpv6);
											fakeDestinationIpv6 = (Inet6Address) ipV6randomizer.anomyzeAddress(realDestinationIpv6);
											
											l2IPDataRecord.setDestinationIPv6Address(fakeDestinationIpv6);
											l2IPDataRecord.setSourceIPv6Address(fakeSourceIpv6);
										}
										
										
									}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}
					//messageHeader.setObservationDomainID(4711);
					
										
					Packet.Builder packetBuilderUDP = fullPacket.get(UdpPacket.class).getBuilder();
					UnknownPacket.Builder unknownPacketBuilder = new UnknownPacket.Builder();

					unknownPacketBuilder.rawData(messageHeader.getBytes());
					packetBuilderUDP.payloadBuilder(unknownPacketBuilder);

					Packet.Builder packetBuilderIPv4 = fullPacket.get(IpV4Packet.class).getBuilder();
					packetBuilderIPv4.payloadBuilder(packetBuilderUDP);

					Packet.Builder packetBuilderEthernet = fullPacket.get(EthernetPacket.class).getBuilder();
					packetBuilderEthernet.payloadBuilder(packetBuilderIPv4);

					Packet newPacket = packetBuilderEthernet.build();
					pcapDumper.dump(newPacket);
					dumpedCounter++;
					//System.out.println("Dumped " + dumpedCounter + " packets");
					
					
					if (onlyIPFIXbytes.length != messageHeader.getBytes().length) {
						System.out.println("Lenght: OnlyIPFIX: " + onlyIPFIXbytes.length + " : Generated: " + messageHeader.getBytes().length);
					}

					
					boolean containsOtherThan306SetID = false;
				 	for (SetHeader sh : messageHeader.getSetHeaders()) {
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
						observationDomainIDs.add(messageHeader.getObservationDomainID());
						for (SetHeader sh : messageHeader.getSetHeaders()) {
							for (DataRecord dr : sh.getDataRecords()) {
								if (dr instanceof L2IPDataRecord) {
									L2IPDataRecord lidr = (L2IPDataRecord) dr;
									srcMacAddresses.add(lidr.getSourceMacAddress());
									destMacAddresses.add(lidr.getDestinationMacAddress());

									if (Utility.isConifgured(lidr.getDestinationIPv6Address())) {
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
				//System.out.println("Count IPv6 Addresses: " + ipV6randomizer.getDatabaseSize());
				//System.out.println("Count IPv4 Addresses: " + ipV4randomizer.getDatabaseSize());
			}


		};

		pcapHandleReadOffline.loop(-1, packetListener);

		pcapHandleReadOffline.close();
		
		pcapDumper.close();

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