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
import java.util.*;
import java.util.concurrent.TimeUnit;

import net.decix.jipfix.header.InformationElement;
import net.decix.jipfix.header.L2IPDataRecord;
import net.decix.jipfix.header.MessageHeader;
import net.decix.jipfix.header.OptionTemplateRecord;
import net.decix.jipfix.header.SamplingDataRecord;
import net.decix.jipfix.header.SetHeader;
import net.decix.jipfix.header.TemplateRecord;
import net.decix.util.HeaderBytesException;

import org.savarese.vserv.tcpip.IPPacket;
import org.savarese.vserv.tcpip.OctetConverter;
import org.savarese.vserv.tcpip.TCPPacket;
import org.savarese.vserv.tcpip.UDPPacket;

import com.savarese.rocksaw.net.RawSocket;

public class IPFIXGenerator {
	public static void main(String args[]) {
		try {

			var ipVersionValue = 4;
			var transportProtocolValue = Short.parseShort("6");
			Random rand = new Random();



			/**
			 *
			 * Nginx (443) 10.1.1.1 --> Graphql (4000) 10.1.1.2
			 * Graphql (4000) 10.1.1.2 --> signup(6422) 10.1.1.3
			 * Graphql (4000) 10.1.1.2 --> Payments (4230) 10.1.1.4
			 * signup(6422) 10.1.1.3 --> Postgres (5432) 10.1.1.5
			 * Payments (4230) 10.1.1.4 --> Arango (8529) 10.1.1.6
			 * Payments (4230) 10.1.1.4 --> kafka (9092) 10.1.1.7
			 * kafka (9092) 10.1.1.7 --> zookeeper (2181) 10.1.1.8
			 * kafka (9092) 10.1.1.7 --> history (5430) 10.1.1.9
			 * history (5430) 10.1.1.9 -> Memcached (11211) 10.1.1.10
			 * Memcached (11211) 10.1.1.10 -> Cassandra (7000) 10.1.1.11
			 *
			 * Every IPFix should contain the following:
			 * 1 flow of the following :
			 * Nginx -> GraphQL -> TxBytes 2048, Rx-bytes: 1024, duration: 400 ms
			 * GraphQL -> SignuP -> TxBytes 2048, Rx-bytes: 1024, duration: 100 ms
			 * SignUp -> postgres  -> TxBytes 2048, Rx-Bytes: 1024, duration: 200 ms
			 *
			 * 4 flows of the following
			 * Nginx -> GraphQL  -> TxBytes 1024, Rx-Bytes: 512 duration: 800 ms
			 * GraphQL -> payments -> TxBytes: 1024, Rx-Bytes: 512, duration: 400ms
			 * Payments -> Arango -> TxBytes: 1024, Rx-Bytes: 512, duration: 400ms
			 * Payments -> Kafka -> TxBytes: 1024, Rx-Bytes: 512, duration: 100 ms
			 * Kafka -> History TxBytes: 1024, Rx-Bytes: 512, duration: 200ms
			 * History -> MemCached: TxBytes: 1024, Rx-Bytes: 512, duration: 50ms
			 *
			 * 10 flows of the following:
			 * MemCached -> Cassandra TxBytes: 8192, Rx-Bytes: 256, duration: 300ms
			 *
			 * 1 flow of the following:
			 * Kafka -> Zookeeper, TxBytes 256, Rx-bytes: 1024, duration: 50 ms
			 */

			var edgeMap = new ArrayList<>(
					List.of(
							List.of("10.1.1.1", "443", "10.1.1.2", "4000", "2048", "1024", "400"),
							List.of("10.1.1.2", "4000", "10.1.1.3", "6422", "2048", "1024", "100"),
							List.of("10.1.1.3", "6422", "10.1.1.5", "5432", "2048", "1024", "200"),
							List.of("10.1.1.2", "4000", "10.1.1.4", "4230", "1024", "512", "400"),
							List.of("10.1.1.4", "4230", "10.1.1.6", "8529", "1024", "512", "400"),
							List.of("10.1.1.4", "4230", "10.1.1.7", "9092", "1024", "512", "100"),
							List.of("10.1.1.7", "9092", "10.1.1.9", "5430", "1024", "512", "200"),
							List.of("10.1.1.9", "5430", "10.1.1.10", "11211", "1024", "512", "50"),
							List.of("10.1.1.2", "4000", "10.1.1.4", "4230", "1024", "512", "400"),
							List.of("10.1.1.4", "4230", "10.1.1.6", "8529", "1024", "512", "400"),
							List.of("10.1.1.4", "4230", "10.1.1.7", "9092", "1024", "512", "100"),
							List.of("10.1.1.7", "9092", "10.1.1.9", "5430", "1024", "512", "200"),
							List.of("10.1.1.9", "5430", "10.1.1.10", "11211", "1024", "512", "50"),
							List.of("10.1.1.2", "4000", "10.1.1.4", "4230", "1024", "512", "400"),
							List.of("10.1.1.4", "4230", "10.1.1.6", "8529", "1024", "512", "400"),
							List.of("10.1.1.4", "4230", "10.1.1.7", "9092", "1024", "512", "100"),
							List.of("10.1.1.7", "9092", "10.1.1.9", "5430", "1024", "512", "200"),
							List.of("10.1.1.9", "5430", "10.1.1.10", "11211", "1024", "512", "50"),
							List.of("10.1.1.2", "4000", "10.1.1.4", "4230", "1024", "512", "400"),
							List.of("10.1.1.4", "4230", "10.1.1.6", "8529", "1024", "512", "400"),
							List.of("10.1.1.4", "4230", "10.1.1.7", "9092", "1024", "512", "100"),
							List.of("10.1.1.7", "9092", "10.1.1.9", "5430", "1024", "512", "200"),
							List.of("10.1.1.9", "5430", "10.1.1.10", "11211", "1024", "512", "50"),
							List.of("10.1.1.7", "9092", "10.1.1.8", "2181", "256", "1024", "50"),
							List.of("10.1.1.10", "11211", "10.1.1.11", "7000", "8192", "256", "300"),
							List.of("10.1.1.10", "11211", "10.1.1.11", "7000", "8192", "256", "300"),
							List.of("10.1.1.10", "11211", "10.1.1.11", "7000", "8192", "256", "300"),
							List.of("10.1.1.10", "11211", "10.1.1.11", "7000", "8192", "256", "300"),
							List.of("10.1.1.10", "11211", "10.1.1.11", "7000", "8192", "256", "300"),
							List.of("10.1.1.10", "11211", "10.1.1.11", "7000", "8192", "256", "300"),
							List.of("10.1.1.10", "11211", "10.1.1.11", "7000", "8192", "256", "300"),
							List.of("10.1.1.10", "11211", "10.1.1.11", "7000", "8192", "256", "300"),
							List.of("10.1.1.10", "11211", "10.1.1.11", "7000", "8192", "256", "300"),
							List.of("10.1.1.10", "11211", "10.1.1.11", "7000", "8192", "256", "300")
							)
			);


			var destIp = new ArrayList<String>();
			destIp.add("10.43.7.116");
			destIp.add("10.43.15.1");








			MessageHeader mh = new MessageHeader();
			mh.setVersionNumber(10);
			mh.setObservationDomainID(67108864);

			// Set header for the template
			SetHeader shTemplate = new SetHeader();
			shTemplate.setSetID(2);

			TemplateRecord tr = getTemplateRecord();


			shTemplate.addTemplateRecord(tr);

			mh.addSetHeader(shTemplate);

			// Set header for the L2IP
			SetHeader shDataRecord = new SetHeader();
			shDataRecord.setSetID(306);

			for(List<String> arr: edgeMap) {

				var srcIPv4Value = (Inet4Address) Inet4Address.getByName(arr.get(0));
				var destIPv4Value = (Inet4Address) Inet4Address.getByName(arr.get(2));
				var srcPortValue = Integer.parseInt(arr.get(1));
				var destPortValue = Integer.parseInt(arr.get(3));
				var TX = Integer.parseInt(arr.get(4));
				var RX = Integer.parseInt(arr.get(5));
				long duration = Long.parseLong(arr.get(6));

				L2IPDataRecord l2ip = getDataRecord((short) ipVersionValue,
						transportProtocolValue,
						srcIPv4Value,
						destIPv4Value,
						srcPortValue,
						destPortValue,
						TX,
						RX,
						duration);
				shDataRecord.addDataRecord(l2ip);
			}



			mh.addSetHeader(shDataRecord);

			// socket handling
			DatagramSocket datagramSocket = null;

			long seqNumber = 0;
			var curTime = System.currentTimeMillis();
			var count = 0;
			while (true) {
				// Message header updating
				mh.setSequenceNumber(seqNumber);
				mh.setExportTime(new Date());
				seqNumber++;
				count = count + 1;
				var nextIp = destIp.get(rand.nextInt(2));

				var exporterIPv4Value =  (Inet4Address) Inet4Address.getByName("10.43.7.117");
				var collectorIPv4Value = (Inet4Address) Inet4Address.getByName(nextIp); //192.168.1.254 //10.43.7.116"
				var exporterPortValue = Integer.parseInt("4003");
				var collectorPortValue = Integer.parseInt("2055");

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

				udp.setDestinationAsWord(OctetConverter.octetsToInt(collectorIPv4Value.getAddress()));
				udp.setSourceAsWord(OctetConverter.octetsToInt(exporterIPv4Value.getAddress()));
				System.arraycopy(data, 0, pktData, 28, data.length);
				udp.setData(pktData);
				udp.computeUDPChecksum(true);
				udp.computeIPChecksum(true);
				udp.getData(pktData);
				DatagramPacket dp = new DatagramPacket(mh.getBytes(), mh.getBytes().length, collectorIPv4Value, collectorPortValue);
				datagramSocket = new DatagramSocket(exporterPortValue);
				datagramSocket.send(dp);
				datagramSocket.close();
				if(System.currentTimeMillis() - curTime > 1000) {
					System.out.println("Current Rate (flows/second):");
					System.out.println(count * 34);
					System.out.println("Sample Packet: " + mh);
					count = 0;
					curTime = System.currentTimeMillis();
				}
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

	private static L2IPDataRecord getDataRecord(short ipVersionValue, short transportProtocolValue, Inet4Address srcIPv4Value, Inet4Address destIPv4Value, int srcPortValue, int destPortValue, int TX, int RX, long duration) throws UnknownHostException {
		L2IPDataRecord l2ip = new L2IPDataRecord();
		l2ip.setSourceIPv4Address(srcIPv4Value);
		l2ip.setDestinationIPv4Address(destIPv4Value);
		l2ip.setTxCount(TX);
		l2ip.setRxCount(RX);
		l2ip.setFlowDuration(duration);
		l2ip.setSourceTransportPort(srcPortValue);
		l2ip.setDestinationTransportPort(destPortValue);
		l2ip.setProtocolIdentifier(transportProtocolValue);
		l2ip.setIpVersion(ipVersionValue);
		return l2ip;
	}

//	private static void setDataRecord(short ipVersionValue,
//									  short transportProtocolValue,
//									  Inet4Address srcIPv4Value,
//									  Inet4Address destIPv4Value,
//									  int srcPortValue,
//									  int destPortValue,
//									  int TX,
//									  int RX,
//									  long duration,
//									  L2IPDataRecord l2ip) {
//		l2ip.setSourceIPv4Address(srcIPv4Value);
//		l2ip.setDestinationIPv4Address(destIPv4Value);
//		l2ip.setTxCount(TX);
//		l2ip.setRxCount(RX);
//		l2ip.setFlowDuration(duration);
//		l2ip.setSourceTransportPort(srcPortValue);
//		l2ip.setDestinationTransportPort(destPortValue);
//		l2ip.setProtocolIdentifier(transportProtocolValue);
//		l2ip.setIpVersion(ipVersionValue);
//	}

	private static TemplateRecord getTemplateRecord() {
		TemplateRecord tr = new TemplateRecord();

		tr.setTemplateID(306);
		tr.setFieldCount(9);

		InformationElement iEIPv4Src = new InformationElement();
		iEIPv4Src.setFieldLength(4);
		iEIPv4Src.setInformationElementID(8);
		tr.getInformationElements().add(iEIPv4Src);

		InformationElement iEIPv4Dest = new InformationElement();
		iEIPv4Dest.setFieldLength(4);
		iEIPv4Dest.setInformationElementID(12);
		tr.getInformationElements().add(iEIPv4Dest);


		InformationElement iEBytes = new InformationElement();
		iEBytes.setFieldLength(4);
		iEBytes.setInformationElementID(231);
		tr.getInformationElements().add(iEBytes);

		InformationElement iEReversedBytes = new InformationElement();
		iEReversedBytes.setFieldLength(4);
		iEReversedBytes.setInformationElementID(232);
		tr.getInformationElements().add(iEReversedBytes);


		InformationElement iEFlowDuration = new InformationElement();
		iEFlowDuration.setFieldLength(4);
		iEFlowDuration.setInformationElementID(161);
		tr.getInformationElements().add(iEFlowDuration);

		InformationElement iEPortSrc = new InformationElement();
		iEPortSrc.setFieldLength(2);
		iEPortSrc.setInformationElementID(7);
		tr.getInformationElements().add(iEPortSrc);

		InformationElement iEPortDest = new InformationElement();
		iEPortDest.setFieldLength(2);
		iEPortDest.setInformationElementID(11);
		tr.getInformationElements().add(iEPortDest);

		InformationElement iEProtocol = new InformationElement();
		iEProtocol.setFieldLength(1);
		iEProtocol.setInformationElementID(4);
		tr.getInformationElements().add(iEProtocol);

		InformationElement iEIPVersion = new InformationElement();
		iEIPVersion.setFieldLength(1);
		iEIPVersion.setInformationElementID(60);
		tr.getInformationElements().add(iEIPVersion);
		return tr;
	}
}
