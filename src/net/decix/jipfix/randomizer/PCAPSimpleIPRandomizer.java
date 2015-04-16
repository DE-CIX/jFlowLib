package net.decix.jipfix.randomizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.List;

import net.decix.jipfix.header.DataRecord;
import net.decix.jipfix.header.L2IPDataRecord;
import net.decix.jipfix.header.MessageHeader;
import net.decix.jipfix.header.SetHeader;
import net.decix.randomizer.SimpleIPv4AddressRandomizer;
import net.decix.randomizer.SimpleIPv6AddressRandomizer;
import net.decix.util.HeaderParseException;
import net.decix.util.Utility;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UdpPacket;

public class PCAPSimpleIPRandomizer {
	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/ipfix-all-routers-2014-12-18.pcap";
	private static final String FILE_WRITE = "/Users/tking/Downloads/meta_simple.txt";
	
	public static void main(String[] args) throws PcapNativeException, NotOpenException, InterruptedException, IOException {
		final PcapHandle pcapHandleReadOffline = Pcaps.openOffline(PCAP_FILE_READ);
		
		final FileWriter fw = new FileWriter(new File(FILE_WRITE));
		
		PacketListener packetListener = new PacketListener() {
			SimpleIPv4AddressRandomizer ipV4randomizer = new SimpleIPv4AddressRandomizer();
			SimpleIPv6AddressRandomizer ipV6randomizer = new SimpleIPv6AddressRandomizer();

			public void gotPacket(Packet fullPacket) {
				long timestampInts = pcapHandleReadOffline.getTimestampInts();
				int timestampMicros = pcapHandleReadOffline.getTimestampMicros();

				UdpPacket udpPacket = fullPacket.get(UdpPacket.class);
				fullPacket.getHeader();

				if (udpPacket == null) return;
				
				byte[] rawUpdPacketBytes = udpPacket.getRawData();
				byte[] onlyIPFIXbytes = new byte[rawUpdPacketBytes.length - 8];
				System.arraycopy(rawUpdPacketBytes, 8, onlyIPFIXbytes, 0, rawUpdPacketBytes.length - 8);

				try {
					MessageHeader messageHeader = MessageHeader.parse(onlyIPFIXbytes);
					List<SetHeader> setHeaders = messageHeader.getSetHeaders();
					for (SetHeader currentSetHeader : setHeaders) {
						for (DataRecord currentDataRecord : currentSetHeader.getDataRecords()) {

							try {
								boolean foundIPv6 = false;
								boolean foundIPv4 = false;
								if (currentDataRecord instanceof L2IPDataRecord) {
									
									L2IPDataRecord l2IPDataRecord = (L2IPDataRecord) currentDataRecord;
									
									if (Utility.isConfigured(l2IPDataRecord.getSourceIPv4Address())) {
										foundIPv4 = true;
									}
									if (Utility.isConfigured(l2IPDataRecord.getDestinationIPv4Address())) {
										foundIPv4 = true;
									}
									
									if (Utility.isConfigured(l2IPDataRecord.getSourceIPv6Address())) {
										foundIPv6 = true;
									}
									if (Utility.isConfigured(l2IPDataRecord.getDestinationIPv6Address())) {
										foundIPv6 = true;
									}
									
									Inet4Address realDestinationIpv4 = l2IPDataRecord.getDestinationIPv4Address();
									Inet4Address realSourceIpv4 = l2IPDataRecord.getSourceIPv4Address();

									Inet6Address realDestinationIpv6 = l2IPDataRecord.getDestinationIPv6Address();
									Inet6Address realSourceIpv6 = l2IPDataRecord.getSourceIPv6Address();

									Inet4Address fakeDestinationIpv4 = null;
									Inet4Address fakeSourceIpv4 = null;

									Inet6Address fakeDestinationIpv6 = null;
									Inet6Address fakeSourceIpv6 = null;

									if (foundIPv4) {
										fakeDestinationIpv4 = (Inet4Address) ipV4randomizer.randomize(realDestinationIpv4);
										fakeSourceIpv4 = (Inet4Address) ipV4randomizer.randomize(realSourceIpv4);

										fw.write(timestampInts + "." + timestampMicros + " " + fakeSourceIpv4.getHostAddress() + " " + fakeDestinationIpv4.getHostAddress() + " " + (l2IPDataRecord.getOctetDeltaCount()/l2IPDataRecord.getPacketDeltaCount()) + "\n");
									}

									if (foundIPv6) {
										fakeSourceIpv6 = (Inet6Address) ipV6randomizer.randomize(realSourceIpv6);
										fakeDestinationIpv6 = (Inet6Address) ipV6randomizer.randomize(realDestinationIpv6);
										
										fw.write(timestampInts + "." + timestampMicros + " " + fakeSourceIpv6.getHostAddress() + " " + fakeDestinationIpv6.getHostAddress() + " " + (l2IPDataRecord.getOctetDeltaCount()/l2IPDataRecord.getPacketDeltaCount()) + "\n");
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				} catch (HeaderParseException e) {
					e.printStackTrace();
				}
			}
		};

		pcapHandleReadOffline.loop(-1, packetListener);
		pcapHandleReadOffline.close();
		
		fw.close();
	}
}