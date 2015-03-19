package net.decix.jipfix.randomizer;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.List;

import net.decix.jipfix.header.DataRecord;
import net.decix.jipfix.header.L2IPDataRecord;
import net.decix.jipfix.header.MessageHeader;
import net.decix.jipfix.header.SetHeader;
import net.decix.util.HeaderBytesException;
import net.decix.util.HeaderParseException;
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

public class PCAPAnonyminizer {
	private static final String PCAP_FILE_READ = "c:\\tmp\\ipfix-2014-04-16.1.pcap";
	private static final String PCAP_FILE_WRITE = "c:\\tmp\\trace_out_ipfix-2014-04-16.1.pcap";

	private static PcapDumper pcapDumper;
	
	public static void main(String[] args) throws PcapNativeException, NotOpenException, InterruptedException {
		final PcapHandle pcapHandleReadOffline = Pcaps.openOffline(PCAP_FILE_READ);
		
		pcapDumper = pcapHandleReadOffline.dumpOpen(PCAP_FILE_WRITE);
		
		PacketListener packetListener = new PacketListener() {
			IPv4AddressRandomizer ipV4randomizer = new IPv4AddressRandomizer();
			IPv6AddressRandomizer ipV6randomizer = new IPv6AddressRandomizer();

			public void gotPacket(Packet fullPacket) {
				long timestampInts = pcapHandleReadOffline.getTimestampInts();
				int timestampMicros = pcapHandleReadOffline.getTimestampMicros();

				UdpPacket udpPacket = fullPacket.get(UdpPacket.class);
				fullPacket.getHeader();

				if (udpPacket == null) return;
				if (udpPacket.getHeader().getDstPort().value() != 2055) return; // discard all packets which are not IPFIX

				byte[] rawUpdPacketBytes = udpPacket.getRawData();
				byte[] onlyIPFIXbytes = new byte[rawUpdPacketBytes.length - 8];
				System.arraycopy(rawUpdPacketBytes, 8, onlyIPFIXbytes, 0,rawUpdPacketBytes.length - 8);

				try {
					MessageHeader messageHeader = MessageHeader.parse(onlyIPFIXbytes);
					List<SetHeader> setHeaders = messageHeader.getSetHeaders();
					for (SetHeader currentSetHeader : setHeaders) {
						List<DataRecord> dataRecords = currentSetHeader.getDataRecords();
						for (DataRecord currentDataRecord : dataRecords) {

							try {
								boolean foundIPv6 = false;
								boolean foundIPv4 = false;
								if (currentDataRecord instanceof L2IPDataRecord) {
									L2IPDataRecord l2IPDataRecord = (L2IPDataRecord) currentDataRecord;
									if (!Utility.isConfigured(l2IPDataRecord.getDestinationIPv6Address())) {
										foundIPv6 = true;
									}
									if (!Utility.isConfigured(l2IPDataRecord.getSourceIPv6Address())) {
										foundIPv6 = true;
									}
									if (!Utility.isConfigured(l2IPDataRecord.getDestinationIPv4Address())) {
										foundIPv4 = true;
									}
									if (!Utility.isConfigured(l2IPDataRecord.getSourceIPv4Address())) {
										foundIPv4 = true;
									}
									Inet4Address realDestinationIpv4 = l2IPDataRecord.getDestinationIPv4Address();
									Inet4Address realSourceIpv4 = l2IPDataRecord.getSourceIPv4Address();

									Inet6Address realDestinationIpv6 = l2IPDataRecord.getDestinationIPv6Address();
									Inet6Address realSourceIpv6 = l2IPDataRecord.getSourceIPv6Address();

									Inet4Address fakeDestinationIpv4 = realDestinationIpv4;
									Inet4Address fakeSourceIpv4 = realSourceIpv4;

									Inet6Address fakeDestinationIpv6 = realDestinationIpv6;
									Inet6Address fakeSourceIpv6 = realSourceIpv6;

									if (foundIPv4) {
										fakeDestinationIpv4 = (Inet4Address) ipV4randomizer.randomize(realDestinationIpv4);
										fakeSourceIpv4 = (Inet4Address) ipV4randomizer.randomize(realSourceIpv4);

										l2IPDataRecord.setDestinationIPv4Address(fakeDestinationIpv4);
										l2IPDataRecord.setSourceIPv4Address(fakeSourceIpv4);
									}

									if (foundIPv6) {

										fakeSourceIpv6 = (Inet6Address) ipV6randomizer.randomize(realSourceIpv6);
										fakeDestinationIpv6 = (Inet6Address) ipV6randomizer.randomize(realDestinationIpv6);

										l2IPDataRecord.setDestinationIPv6Address(fakeDestinationIpv6);
										l2IPDataRecord.setSourceIPv6Address(fakeSourceIpv6);
									}

								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					Packet.Builder packetBuilderUDP = fullPacket.get(UdpPacket.class).getBuilder();
					UnknownPacket.Builder unknownPacketBuilder = new UnknownPacket.Builder();

					unknownPacketBuilder.rawData(messageHeader.getBytes());
					packetBuilderUDP.payloadBuilder(unknownPacketBuilder);

					Packet.Builder packetBuilderIPv4 = fullPacket.get(IpV4Packet.class).getBuilder();
					packetBuilderIPv4.payloadBuilder(packetBuilderUDP);

					Packet.Builder packetBuilderEthernet = fullPacket.get(EthernetPacket.class).getBuilder();
					packetBuilderEthernet.payloadBuilder(packetBuilderIPv4);

					Packet newPacket = packetBuilderEthernet.build();
					pcapDumper.dump(newPacket, timestampInts, timestampMicros);

					if (onlyIPFIXbytes.length != messageHeader.getBytes().length) {
						System.out.println("Lenght: OnlyIPFIX: " + onlyIPFIXbytes.length + " : Generated: "+  messageHeader.getBytes().length);
					}
				} catch (HeaderParseException e) {
					e.printStackTrace();
				} catch (HeaderBytesException e) {
					e.printStackTrace();
				} catch (NotOpenException e) {
					e.printStackTrace();
				}
			}
		};

		pcapHandleReadOffline.loop(-1, packetListener);
		pcapHandleReadOffline.close();

		pcapDumper.close();
	}
}