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

			var ipVersionValue = Short.parseShort("4");
			var transportProtocolValue = Short.parseShort("6");
			var srcIPv4Value = (Inet4Address) Inet4Address.getByName("100.1.2.1");
			var destIPv4Value = (Inet4Address) Inet4Address.getByName("100.1.2.7");
			var exporterIPv4Value =  (Inet4Address) Inet4Address.getByName("10.43.7.117");
			var collectorIPv4Value = (Inet4Address) Inet4Address.getByName("10.43.7.116"); //192.168.1.254
			var srcPortValue = Integer.parseInt("4001");
			var destPortValue = Integer.parseInt("4002");
			var exporterPortValue = Integer.parseInt("4003");
			var collectorPortValue = Integer.parseInt("2055");
			Random random = new Random();
			int samplingRateValue = 10000;

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

			L2IPDataRecord l2ip = new L2IPDataRecord();

			l2ip.setSourceIPv4Address(srcIPv4Value);

			l2ip.setDestinationIPv4Address(destIPv4Value);

			var packetsValue = 10;
			var octetsValue = 100000;
			l2ip.setPacketDeltaCount(packetsValue);
			l2ip.setOctetDeltaCount(octetsValue);

			BigInteger flowStartEnd = BigInteger.valueOf(new Date().getTime());

			l2ip.setFlowStartMilliseconds(flowStartEnd);
			l2ip.setFlowEndMilliseconds(flowStartEnd);

			l2ip.setSourceTransportPort(srcPortValue);
			l2ip.setDestinationTransportPort(destPortValue);

			l2ip.setProtocolIdentifier(transportProtocolValue);


			l2ip.setIpVersion(ipVersionValue);



			int dataRateValue = 1;


			shDataRecord.addDataRecord(l2ip);
			shDataRecord.addDataRecord(l2ip);
			shDataRecord.addDataRecord(l2ip);
			shDataRecord.addDataRecord(l2ip);
			shDataRecord.addDataRecord(l2ip);
			shDataRecord.addDataRecord(l2ip);

			mh.addSetHeader(shDataRecord);

			// socket handling
			DatagramSocket datagramSocket = null;

			long seqNumber = 0;
			while (true) {
				// Message header updating
				mh.setSequenceNumber(seqNumber);
				mh.setExportTime(new Date());
				seqNumber++;

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
				udp.computeUDPChecksum(true);
				udp.computeIPChecksum(true);
				udp.getData(pktData);
				DatagramPacket dp = new DatagramPacket(mh.getBytes(), mh.getBytes().length, collectorIPv4Value, collectorPortValue);
				datagramSocket = new DatagramSocket(exporterPortValue);
				datagramSocket.send(dp);
				datagramSocket.close();
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

	private static TemplateRecord getTemplateRecord() {
		TemplateRecord tr = new TemplateRecord();

		tr.setTemplateID(306);
		tr.setFieldCount(10);

		InformationElement iEIPv4Src = new InformationElement();
		iEIPv4Src.setFieldLength(4);
		iEIPv4Src.setInformationElementID(8);
		tr.getInformationElements().add(iEIPv4Src);

		InformationElement iEIPv4Dest = new InformationElement();
		iEIPv4Dest.setFieldLength(4);
		iEIPv4Dest.setInformationElementID(12);
		tr.getInformationElements().add(iEIPv4Dest);

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
		iEPortDest.setInformationElementID(11);
		tr.getInformationElements().add(iEPortDest);

		InformationElement iETCPFlags = new InformationElement();
		iETCPFlags.setFieldLength(1);
		iETCPFlags.setInformationElementID(6);
		tr.getInformationElements().add(iETCPFlags);

		InformationElement iEProtocol = new InformationElement();
		iEProtocol.setFieldLength(1);
		iEProtocol.setInformationElementID(4);
		tr.getInformationElements().add(iEProtocol);

//		InformationElement iEFlowLabel = new InformationElement();
//		iEFlowLabel.setFieldLength(4);
//		iEFlowLabel.setInformationElementID(31);
//		tr.getInformationElements().add(iEFlowLabel);
//
//		InformationElement iEIPVersion = new InformationElement();
//		iEIPVersion.setFieldLength(1);
//		iEIPVersion.setInformationElementID(60);
//		tr.getInformationElements().add(iEIPVersion);
		return tr;
	}
}
