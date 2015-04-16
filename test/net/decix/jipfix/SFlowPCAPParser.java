package net.decix.jipfix;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import net.decix.jsflow.header.CounterRecordHeader;
import net.decix.jsflow.header.ExpandedCounterSampleHeader;
import net.decix.jsflow.header.SFlowHeader;
import net.decix.jsflow.header.SampleDataHeader;
import net.decix.util.HeaderParseException;
import net.decix.util.MacAddress;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapDumper;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UdpPacket;

public class SFlowPCAPParser {  
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/2014-03-05_IPFIX_capture01.pcap"; // IPFIX-Test.pcap
	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/sflow-counter-all-routers-2015-03-16_1.pcap";
//	private static final String PCAP_FILE_READ = "c:\\tmp\\trace.pcap";
//	private static final String PCAP_FILE_READ = "/Users/tking/Downloads/ipfix-2014-04-14.2.pcap";
//	private static final String PCAP_FILE_WRITE = "/Users/tking/Downloads/IPFIX-Test_Created_By_JIPFIX.pcap";
//	private static final String PCAP_FILE_WRITE = "c:\\tmp\\trace_out.pcap";
	private static final String PCAP_FILE_WRITE = "/Users/tking/Downloads/IPFIX-Test_Created_By_JSFLOW2.pcap";
	
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
				
				byte[] bytes = udpPacket.getRawData();
				byte[] onlySFLOW = new byte[bytes.length - 8];
				System.arraycopy(bytes, 8, onlySFLOW, 0, bytes.length - 8);

				try {
					SFlowHeader rph = SFlowHeader.parse(onlySFLOW);
					
					Vector<SampleDataHeader> sampleDataHeaders = rph.getSampleDataHeaders();
					for (SampleDataHeader sdh : sampleDataHeaders) {
						ExpandedCounterSampleHeader expandedCounterSampleHeader = sdh.getExpandedCounterSampleHeader();
//						System.out.println(expandedCounterSampleHeader.getSourceIDIndex());
//						System.out.println(expandedCounterSampleHeader.getSourceIDType());
						for (CounterRecordHeader gich : expandedCounterSampleHeader.getCounterRecords()) {
							if (gich.getGenericInterfaceCounterHeader() != null) {
								System.out.println("seq: " + expandedCounterSampleHeader.getSequenceNumber());
								System.out.println("if index:" + gich.getGenericInterfaceCounterHeader().getIfIndex());
								System.out.println("if type:" + gich.getGenericInterfaceCounterHeader().getIfType());
							}
						}
					}
					
				} catch (HeaderParseException e) {
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
