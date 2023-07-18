package net.decix.jipfix.generator;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.decix.jipfix.header.InformationElement;
import net.decix.jipfix.header.L2IPDataRecord;
import net.decix.jipfix.header.MessageHeader;
import net.decix.jipfix.header.OptionTemplateRecord;
import net.decix.jipfix.header.SamplingDataRecord;
import net.decix.jipfix.header.SetHeader;
import net.decix.jipfix.header.TemplateRecord;
import net.decix.util.HeaderBytesException;
import net.decix.util.MacAddress;
import net.decix.util.UtilityException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.savarese.vserv.tcpip.IPPacket;
import org.savarese.vserv.tcpip.OctetConverter;
import org.savarese.vserv.tcpip.TCPPacket;
import org.savarese.vserv.tcpip.UDPPacket;

import com.savarese.rocksaw.net.RawSocket;

public class IPFIXGenerator {
	public static void main(String args[]) {




//		/Library/Java/JavaVirtualMachines/jdk-18.0.1.1.jdk/Contents/Home/bin/java -Djava.library.path=/home/dev/repo/ws/jFlowLib/lib -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=54403:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /home/dev/repo/ws/jFlowLib/classes/production/jFlowLib:/home/dev/repo/ws/jFlowLib/lib/commons-cli-1.2.jar:/home/dev/repo/ws/jFlowLib/lib/jna.jar:/home/dev/repo/ws/jFlowLib/lib/pcap4j-core-1.1.0.jar:/home/dev/repo/ws/jFlowLib/lib/pcap4j-packetfactory-propertiesbased-1.1.0.jar:/home/dev/repo/ws/jFlowLib/lib/pcap4j-packetfactory-static-1.1.0.jar:/home/dev/repo/ws/jFlowLib/lib/pcap4j-packettest-1.1.0-tests.jar:/home/dev/repo/ws/jFlowLib/lib/pcap4j-sample-1.1.0.jar:/home/dev/repo/ws/jFlowLib/lib/rocksaw-1.1.0.jar:/home/dev/repo/ws/jFlowLib/lib/slf4j-api-1.7.7.jar:/home/dev/repo/ws/jFlowLib/lib/slf4j-nop-1.7.7.jar net.decix.jipfix.generator.IPFIXGenerator -collectorIPv4 127.0.0.1 -destIPv4 0.0.0.0 -destPort 8008 -srcIPv4 100.1.2.1 -srcPort 4000 -ipVersion 4 -exporterIPv4 127.0.0.2

///home/dev/repo/ws/jFlowLib/classes/production/jFlowLib/
//		-classpath /home/dev/repo/ws/jFlowLib/classes/production/jFlowLib:/home/dev/repo/ws/jFlowLib/lib/commons-cli-1.2.jar:/home/dev/repo/ws/jFlowLib/lib/jna.jar:/home/dev/repo/ws/jFlowLib/lib/pcap4j-core-1.1.0.jar:/home/dev/repo/ws/jFlowLib/lib/pcap4j-packetfactory-propertiesbased-1.1.0.jar:/home/dev/repo/ws/jFlowLib/lib/pcap4j-packetfactory-static-1.1.0.jar:/home/dev/repo/ws/jFlowLib/lib/pcap4j-packettest-1.1.0-tests.jar:/home/dev/repo/ws/jFlowLib/lib/pcap4j-sample-1.1.0.jar:/home/dev/repo/ws/jFlowLib/lib/rocksaw-1.1.0.jar:/home/dev/repo/ws/jFlowLib/lib/slf4j-api-1.7.7.jar:/home/dev/repo/ws/jFlowLib/lib/slf4j-nop-1.7.7.jar



//		Option ingressPhysicalInterfaceID = OptionBuilder.withArgName("id").hasArgs().withDescription("ingress physical interface identifier").create("ingressPhysicalInterfaceID");
//		Option egressPhysicalInterfaceID = OptionBuilder.withArgName("id").hasArgs().withDescription("egress physical interface identifer").create("egressPhysicalInterfaceID");
//
//		Option innerVLAN = OptionBuilder.withArgName("vlan").hasArgs().withDescription("inner VLAN").create("innerVLAN");
//		Option outerVLAN = OptionBuilder.withArgName("vlan").hasArgs().withDescription("outer VLAN").create("outerVLAN");
//
//		Option srcMAC = OptionBuilder.withArgName("mac").hasArgs().withDescription("source MAC address").create("srcMAC");
//		Option destMAC = OptionBuilder.withArgName("mac").hasArgs().withDescription("destination MAC address").create("destMAC");
//
//		Option ipVersion = OptionBuilder.withArgName("version").hasArgs().withDescription("IP version").create("ipVersion");
//		Option transportProtocol = OptionBuilder.withArgName("protocol").hasArgs().withDescription("transport layer protocol").create("transportProtocol");
//
//		Option srcIPv4 = OptionBuilder.withArgName("ipv4").hasArgs().withDescription("source IPv4 address").create("srcIPv4");
//		Option destIPv4 = OptionBuilder.withArgName("ipv4").hasArgs().withDescription("destination IPv4 address").create("destIPv4");
//
//		Option srcIPv6 = OptionBuilder.withArgName("ipv6").hasArgs().withDescription("source IPv6 address").create("srcIPv6");
//		Option destIPv6 = OptionBuilder.withArgName("ipv6").hasArgs().withDescription("destination IPv6 address").create("destIPv6");
//
//		Option srcPort = OptionBuilder.withArgName("port").hasArgs().withDescription("source port").create("srcPort");
//		Option destPort = OptionBuilder.withArgName("port").hasArgs().withDescription("destination port").create("destPort");
//
//		Option icmpType = OptionBuilder.withArgName("type").hasArgs().withDescription("ICMP type").create("icmpType");
//
//		Option packets = OptionBuilder.withArgName("packets").hasArgs().withDescription("packets (default: 1)").create("packets");
//		Option octets = OptionBuilder.withArgName("octets").hasArgs().withDescription("octets in Bytes (default: 100000)").create("octets");
//
//		Option dataRate = OptionBuilder.withArgName("rate").hasArgs().withDescription("data rate in MBit/s (default: 1 MBit/s)").create("dataRate");
//		Option samplingRate = OptionBuilder.withArgName("rate").hasArgs().withDescription("sampling rate (default: 10000)").create("samplingRate");
//		Option collectorIPv4 = OptionBuilder.withArgName("ipv4").hasArgs().withDescription("IPFIX collector IPv4 address (required)").create("collectorIPv4");
//		Option collectorPort = OptionBuilder.withArgName("port").hasArgs().withDescription("IPFIX collector UDP port (default: 2601)").create("collectorPort");
//
//		Option exporterIPv4 = OptionBuilder.withArgName("ipv4").hasArgs().withDescription("IPFIX exporter (spoofed) IPv4 address").create("exporterIPv4");
//		Option exporterPort = OptionBuilder.withArgName("port").hasArgs().withDescription("IPFIX exporter UDP port (default: 2601)").create("exporterPort");
//
//		Option debug = OptionBuilder.withArgName("debug").withDescription("enable debug").create("debug");
//
//		Options options = new Options();
//		options.addOption(ingressPhysicalInterfaceID);
//		options.addOption(egressPhysicalInterfaceID);
//		options.addOption(innerVLAN);
//		options.addOption(outerVLAN);
//		options.addOption(srcMAC);
//		options.addOption(destMAC);
//		options.addOption(ipVersion);
//		options.addOption(transportProtocol);
//		options.addOption(srcIPv4);
//		options.addOption(destIPv4);
//		options.addOption(srcIPv6);
//		options.addOption(destIPv6);
//		options.addOption(srcPort);
//		options.addOption(destPort);
//		options.addOption(icmpType);
//		options.addOption(srcIPv4);
//		options.addOption(packets);
//		options.addOption(octets);
//		options.addOption(dataRate);
//		options.addOption(samplingRate);
//		options.addOption(collectorIPv4);
//		options.addOption(collectorPort);
//		options.addOption(exporterIPv4);
//		options.addOption(exporterPort);
//		options.addOption(debug);

//		CommandLineParser parser = new BasicParser();
		try {
//			CommandLine line = parser.parse(options, args);
//
//			if (!line.hasOption("collectorIPv4")) {
//				HelpFormatter formatter = new HelpFormatter();
//				formatter.printHelp( "IPFIXGenerator", options );
//				System.exit(0);
//			}

			Random random = new Random();

			// physical interface IDs
//			int ingressPhysicalInterfaceIDValue = random.nextInt();
//			while (ingressPhysicalInterfaceIDValue < 0) ingressPhysicalInterfaceIDValue = random.nextInt();
//			if (line.hasOption("ingressPhysicalInterfaceID")) {
//				ingressPhysicalInterfaceIDValue = Integer.parseInt(line.getOptionValue("ingressPhysicalInterfaceID"));
//			}
//
//			int egressPhysicalInterfaceIDValue = random.nextInt();
//			while (egressPhysicalInterfaceIDValue < 0) egressPhysicalInterfaceIDValue = random.nextInt();
//			if (line.hasOption("egressPhysicalInterfaceID")) {
//				egressPhysicalInterfaceIDValue = Integer.parseInt(line.getOptionValue("egressPhysicalInterfaceID"));
//			}
//
//			// VLANs
//			int innerVLANValue = random.nextInt(4096);
//			if (line.hasOption("innerVLAN")) {
//				innerVLANValue = Integer.parseInt(line.getOptionValue("innerVLAN"));
//			}
//
//			int outerVLANValue = random.nextInt(4096);
//			if (line.hasOption("outerVLAN")) {
//				outerVLANValue = Integer.parseInt(line.getOptionValue("outerVLAN"));
//			}
//
//			// MACs
//			byte[] mac = new byte[6];
//			random.nextBytes(mac);
//
//			MacAddress srcMACValue = new MacAddress(mac);
//			if (line.hasOption("srcMAC")) {
//				srcMACValue = new MacAddress(line.getOptionValue("srcMAC"));
//			}
//
//			random.nextBytes(mac);
//			MacAddress destMACValue = new MacAddress(mac);
//			if (line.hasOption("destMAC")) {
//				destMACValue = new MacAddress(line.getOptionValue("destMAC"));
//			}
//
//			// IP version
//			short ipVersionValue = (short) (random.nextBoolean() ? 4 : 6);
//			if (line.hasOption("ipVersion")) {
//				ipVersionValue = Short.parseShort(line.getOptionValue("ipVersion"));
//			}
//
//			// transport protocol
//			short transportProtocolValue = (short) (random.nextBoolean() ? 6 : 17);
//			if (line.hasOption("transportProtocol")) {
//				transportProtocolValue = Short.parseShort(line.getOptionValue("transportProtocol"));
//			}
//
//			// IPv4 address
//			byte[] addrIPv4 = new byte[4];
//			random.nextBytes(addrIPv4);
//			Inet4Address srcIPv4Value = (Inet4Address) Inet4Address.getByAddress(addrIPv4);
//			if (line.hasOption("srcIPv4")) {
//				srcIPv4Value = (Inet4Address) Inet4Address.getByName(line.getOptionValue("srcIPv4"));
//			}
//
//			random.nextBytes(addrIPv4);
//			Inet4Address destIPv4Value = (Inet4Address) Inet4Address.getByAddress(addrIPv4);
//			if (line.hasOption("destIPv4")) {
//				destIPv4Value = (Inet4Address) Inet4Address.getByName(line.getOptionValue("destIPv4"));
//			}
//
//			// IPv6 address
//			byte[] addrIPv6 = new byte[16];
//			random.nextBytes(addrIPv6);
//			Inet6Address srcIPv6Value = (Inet6Address) Inet6Address.getByAddress(addrIPv6);
//			if (line.hasOption("srcIPv6")) {
//				srcIPv6Value = (Inet6Address) Inet6Address.getByName(line.getOptionValue("srcIPv6"));
//			}
//
//			random.nextBytes(addrIPv6);
//			Inet6Address destIPv6Value = (Inet6Address) Inet6Address.getByAddress(addrIPv6);
//			if (line.hasOption("destIPv6")) {
//				destIPv6Value = (Inet6Address) Inet6Address.getByName(line.getOptionValue("destIPv6"));
//			}
//
//			// ports
//			int srcPortValue = random.nextInt(65534);
//			if (line.hasOption("srcPort")) {
//				srcPortValue = Integer.parseInt(line.getOptionValue("srcPort"));
//			}
//
//			int destPortValue = random.nextInt(65534);
//			if (line.hasOption("destPort")) {
//				destPortValue = Integer.parseInt(line.getOptionValue("destPort"));
//			}
//
//			// icmp type
//			int icmpTypeValue = 0;
//			if (line.hasOption("icmpType")) {
//				icmpTypeValue = Integer.parseInt(line.getOptionValue("icmpType"));
//			}
//
//			// packets
//			int packetsValue = 1;
//			if (line.hasOption("packets")) {
//				packetsValue = Integer.parseInt(line.getOptionValue("packets"));
//			}
//
//			// octets
//			int octetsValue = 100000;
//			if (line.hasOption("octets")) {
//				octetsValue = Integer.parseInt(line.getOptionValue("octets"));
//			}
//
//			// data rate
//			int dataRateValue = 1; // 1 MBit/s
//			if (line.hasOption("dataRate")) {
//				dataRateValue = Integer.parseInt(line.getOptionValue("dataRate"));
//			}
//
//			// sampling rate
			int samplingRateValue = 10000;
//			if (line.hasOption("samplingRate")) {
//				samplingRateValue = Integer.parseInt(line.getOptionValue("samplingRate"));
//			}
//
//			// collector IP and port
//			InetAddress collectorIPv4Value = null;
//			if (line.hasOption("collectorIPv4")) {
//				collectorIPv4Value = InetAddress.getByName(line.getOptionValue("collectorIPv4"));
//			}
//
//			int collectorPortValue = 2601;
//			if (line.hasOption("collectorPort")) {
//				collectorPortValue = Integer.parseInt(line.getOptionValue("collectorPort"));
//			}
//
//			// exporter IP and port
//			InetAddress exporterIPv4Value = null;
//			if (line.hasOption("exporterIPv4")) {
//				exporterIPv4Value = InetAddress.getByName(line.getOptionValue("exporterIPv4"));
//			}
//
//			int exporterPortValue = 2601;
//			if (line.hasOption("exporterPort")) {
//				collectorPortValue = Integer.parseInt(line.getOptionValue("exporterPort"));
//			}

			// create IPFIX data
			//
			// Message header
			MessageHeader mh = new MessageHeader();
			mh.setVersionNumber(10);
			mh.setObservationDomainID(67108864);
//			mh.setSetHeaders();

			// Set header for the template
			SetHeader shTemplate = new SetHeader();
			shTemplate.setSetID(2);

			TemplateRecord tr = new TemplateRecord();
			shTemplate.getTemplateRecords().add(tr);

			tr.setTemplateID(306);
			tr.setFieldCount(26);

			InformationElement iESrcMAC = new InformationElement();
			iESrcMAC.setFieldLength(6);
			iESrcMAC.setInformationElementID(56);
			tr.getInformationElements().add(iESrcMAC);

			InformationElement iEDestMAC = new InformationElement();
			iEDestMAC.setFieldLength(6);
			iEDestMAC.setInformationElementID(80);
			tr.getInformationElements().add(iEDestMAC);

			InformationElement iEingressPhysicalID = new InformationElement();
			iEingressPhysicalID.setFieldLength(4);
			iEingressPhysicalID.setInformationElementID(252);
			tr.getInformationElements().add(iEingressPhysicalID);

			InformationElement iEegressPhysicalID = new InformationElement();
			iEegressPhysicalID.setFieldLength(4);
			iEegressPhysicalID.setInformationElementID(253);
			tr.getInformationElements().add(iEegressPhysicalID);

			InformationElement iEdot1qVlandId = new InformationElement();
			iEdot1qVlandId.setFieldLength(2);
			iEdot1qVlandId.setInformationElementID(243);
			tr.getInformationElements().add(iEdot1qVlandId);

			InformationElement iEdot1qCVlandId = new InformationElement();
			iEdot1qCVlandId.setFieldLength(2);
			iEdot1qCVlandId.setInformationElementID(245);
			tr.getInformationElements().add(iEdot1qCVlandId);

			InformationElement iEPostDot1qVlandId = new InformationElement();
			iEPostDot1qVlandId.setFieldLength(2);
			iEPostDot1qVlandId.setInformationElementID(255);
			tr.getInformationElements().add(iEPostDot1qVlandId);

			InformationElement iEPostDot1qCVlandId = new InformationElement();
			iEPostDot1qCVlandId.setFieldLength(2);
			iEPostDot1qCVlandId.setInformationElementID(254);
			tr.getInformationElements().add(iEPostDot1qCVlandId);

			InformationElement iEIPv4Src = new InformationElement();
			iEIPv4Src.setFieldLength(4);
			iEIPv4Src.setInformationElementID(8);
			tr.getInformationElements().add(iEIPv4Src);

			InformationElement iEIPv4Dest = new InformationElement();
			iEIPv4Dest.setFieldLength(4);
			iEIPv4Dest.setInformationElementID(12);
			tr.getInformationElements().add(iEIPv4Dest);

			InformationElement iEIPv6Src = new InformationElement();
			iEIPv6Src.setFieldLength(16);
			iEIPv6Src.setInformationElementID(27);
			tr.getInformationElements().add(iEIPv6Src);

			InformationElement iEIPv6Dest = new InformationElement();
			iEIPv6Dest.setFieldLength(16);
			iEIPv6Dest.setInformationElementID(28);
			tr.getInformationElements().add(iEIPv6Dest);

			InformationElement iEPkts = new InformationElement();
			iEPkts.setFieldLength(4);
			iEPkts.setInformationElementID(2);
			tr.getInformationElements().add(iEPkts);

			InformationElement iEBytes = new InformationElement();
			iEBytes.setFieldLength(4);
			iEBytes.setInformationElementID(1);
			tr.getInformationElements().add(iEBytes);

			InformationElement iEFlowStart = new InformationElement();
			iEFlowStart.setFieldLength(8);
			iEFlowStart.setInformationElementID(152);
			tr.getInformationElements().add(iEFlowStart);

			InformationElement iEFlowEnd = new InformationElement();
			iEFlowEnd.setFieldLength(8);
			iEFlowEnd.setInformationElementID(153);
			tr.getInformationElements().add(iEFlowEnd);

			InformationElement iEPortSrc = new InformationElement();
			iEPortSrc.setFieldLength(2);
			iEPortSrc.setInformationElementID(7);
			tr.getInformationElements().add(iEPortSrc);

			InformationElement iEPortDest = new InformationElement();
			iEPortDest.setFieldLength(2);
			iEPortDest.setInformationElementID(7);
			tr.getInformationElements().add(iEPortDest);

			InformationElement iETCPFlags = new InformationElement();
			iETCPFlags.setFieldLength(1);
			iETCPFlags.setInformationElementID(6);
			tr.getInformationElements().add(iETCPFlags);

			InformationElement iEProtocol = new InformationElement();
			iEProtocol.setFieldLength(1);
			iEProtocol.setInformationElementID(4);
			tr.getInformationElements().add(iEProtocol);

			InformationElement iEIPv6OptionHeaders = new InformationElement();
			iEIPv6OptionHeaders.setFieldLength(4);
			iEIPv6OptionHeaders.setInformationElementID(64);
			tr.getInformationElements().add(iEIPv6OptionHeaders);

			InformationElement iENextHeaderIPv6 = new InformationElement();
			iENextHeaderIPv6.setFieldLength(1);
			iENextHeaderIPv6.setInformationElementID(193);
			tr.getInformationElements().add(iENextHeaderIPv6);

			InformationElement iEFlowLabel = new InformationElement();
			iEFlowLabel.setFieldLength(4);
			iEFlowLabel.setInformationElementID(31);
			tr.getInformationElements().add(iEFlowLabel);

			InformationElement iEIPTOS = new InformationElement();
			iEIPTOS.setFieldLength(1);
			iEIPTOS.setInformationElementID(5);
			tr.getInformationElements().add(iEIPTOS);

			InformationElement iEIPVersion = new InformationElement();
			iEIPVersion.setFieldLength(1);
			iEIPVersion.setInformationElementID(60);
			tr.getInformationElements().add(iEIPVersion);

			InformationElement iEICMPType = new InformationElement();
			iEICMPType.setFieldLength(2);
			iEICMPType.setInformationElementID(32);
			tr.getInformationElements().add(iEICMPType);

			mh.getSetHeaders().add(shTemplate);


			// Set header for the template options
			SetHeader shTemplateOptions = new SetHeader();
			shTemplateOptions.setSetID(3);

			OptionTemplateRecord otr = new OptionTemplateRecord();
			shTemplateOptions.getOptionTemplateRecords().add(otr);
			mh.getSetHeaders().add(shTemplateOptions);

			otr.setTemplateID(256);
			otr.setScopeFieldCount(1);
			otr.setFieldCount(4);

			InformationElement iEObservationDomainID = new InformationElement();
			iEObservationDomainID.setFieldLength(4);
			iEObservationDomainID.setInformationElementID(149);
			otr.getInformationElements().add(iEObservationDomainID);

			InformationElement iESelectorAlgorithm = new InformationElement();
			iESelectorAlgorithm.setFieldLength(2);
			iESelectorAlgorithm.setInformationElementID(304);
			otr.getInformationElements().add(iESelectorAlgorithm);

			InformationElement iESamplingPacketInterval = new InformationElement();
			iESamplingPacketInterval.setFieldLength(4);
			iESamplingPacketInterval.setInformationElementID(305);
			otr.getInformationElements().add(iESamplingPacketInterval);

			InformationElement iESamplingPacketSpace = new InformationElement();
			iESamplingPacketSpace.setFieldLength(4);
			iESamplingPacketSpace.setInformationElementID(306);
			otr.getInformationElements().add(iESamplingPacketSpace);

			// Set header for the sampling
			SetHeader shSampling = new SetHeader();
			shSampling.setSetID(256);

			SamplingDataRecord sdr = new SamplingDataRecord();
			shSampling.getDataRecords().add(sdr);
			sdr.setObservationDomainId(67108864);
			sdr.setSelectorAlgorithm(1);
			sdr.setSamplingPacketInterval(1);
			sdr.setSamplingPacketSpace(samplingRateValue - 1);
			mh.getSetHeaders().add(shSampling);

			// Set header for the L2IP
			SetHeader shDataRecord = new SetHeader();
			shDataRecord.setSetID(306);

			L2IPDataRecord l2ip = new L2IPDataRecord();
			shDataRecord.getDataRecords().add(l2ip);
			mh.getSetHeaders().add(shDataRecord);

//			l2ip.setIngressPhysicalInterface(ingressPhysicalInterfaceIDValue);
//			l2ip.setEgressPhysicalInterface(egressPhysicalInterfaceIDValue);
//			l2ip.setDot1qVlanId(outerVLANValue);
//			l2ip.setDot1qCustomerVlanId(innerVLANValue);
//			l2ip.setSourceMacAddress(srcMACValue);
//			l2ip.setDestinationMacAddress(destMACValue);

//		-collectorIPv4 127.0.0.1 -destIPv4 0.0.0.0 -destPort 8008 -srcIPv4 100.1.2.1 -srcPort 4000 -ipVersion 4 -exporterIPv4 127.0.0.2
			var ipVersionValue = Short.parseShort("4");
			l2ip.setIpVersion(ipVersionValue);
			var transportProtocolValue = Short.parseShort("6");
			l2ip.setProtocolIdentifier(transportProtocolValue);
			var srcIPv4Value = (Inet4Address) Inet4Address.getByName("100.1.2.1");
			l2ip.setSourceIPv4Address(srcIPv4Value);
			var destIPv4Value = (Inet4Address) Inet4Address.getByName("100.2.2.1");
			var exporterIPv4Value =  (Inet4Address) Inet4Address.getByName("100.2.2.1");
			var collectorIPv4Value = (Inet4Address) Inet4Address.getByName("100.2.2.9");
			l2ip.setDestinationIPv4Address(destIPv4Value);
			var srcPortValue = Integer.parseInt("4000");
			var destPortValue = Integer.parseInt("4000");
			var exporterPortValue = Integer.parseInt("4000");
			var collectorPortValue = Integer.parseInt("4000");
			int dataRateValue = 1;
			l2ip.setSourceTransportPort(srcPortValue);
			l2ip.setDestinationTransportPort(destPortValue);
//			l2ip.setIcmpTypeCodeIPv4(icmpTypeValue);
			var packetsValue = 10;
			var octetsValue = 100000;
			l2ip.setPacketDeltaCount(packetsValue);
			l2ip.setOctetDeltaCount(octetsValue);
			// socket handling
			RawSocket rawSocket = null;
			DatagramSocket datagramSocket = null;
//			if (exporterIPv4Value != null) {
//				rawSocket = new RawSocket();
//				rawSocket.open(RawSocket.PF_INET, RawSocket.getProtocolByName("udp"));
//				rawSocket.setIPHeaderInclude(true);
//			} else {
			datagramSocket = new DatagramSocket(exporterPortValue);
//			}

			long seqNumber = 0;
			while (true) {
				// Message header updating
				mh.setSequenceNumber(seqNumber);
				mh.setExportTime(new Date());
				seqNumber++;

				// L2IP updating
				BigInteger flowStartEnd = BigInteger.valueOf(new Date().getTime());
				l2ip.setFlowStartMilliseconds(flowStartEnd);
				l2ip.setFlowEndMilliseconds(flowStartEnd);

//				if (exporterIPv4Value != null) {
				byte[] data = new byte[mh.getBytes().length];
				UDPPacket udp = new UDPPacket(28 + data.length);
				udp.setIPVersion(4);
				udp.setIPHeaderLength(5);
				udp.setProtocol(IPPacket.PROTOCOL_UDP);
				udp.setTTL(5);

				udp.setUDPDataByteLength(data.length);
				udp.setUDPPacketLength(data.length + 8);

				udp.setDestinationPort(collectorPortValue);
				udp.setSourcePort(exporterPortValue);

				byte[] pktData = new byte[udp.size()];

//					Flow
				udp.setDestinationAsWord(OctetConverter.octetsToInt(collectorIPv4Value.getAddress()));
				udp.setSourceAsWord(OctetConverter.octetsToInt(exporterIPv4Value.getAddress()));
				System.arraycopy(data, 0, pktData, 28, data.length);
				udp.setData(pktData);
//					udp.computeTCPChecksum(true);
				udp.computeUDPChecksum(true);
				udp.computeIPChecksum(true);
				udp.getData(pktData);
				System.out.print(pktData.toString());
//					rawSocket.write(collectorIPv4Value, pktData);
//				} else {
				DatagramPacket dp = new DatagramPacket(mh.getBytes(), mh.getBytes().length, collectorIPv4Value, collectorPortValue);
				datagramSocket.send(dp);
//				}

				System.out.println("Sending: " + mh);

//				try {
//					double sleep = samplingRateValue * (1000000 / ((double) (dataRateValue * 1000 * 1000) / (double) ((octetsValue * packetsValue * 8))));
////					if (line.hasOption("debug")) System.out.println("Waiting: " + (long) sleep + " microseconds");
//					TimeUnit.MICROSECONDS.sleep((long) sleep);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeaderBytesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
