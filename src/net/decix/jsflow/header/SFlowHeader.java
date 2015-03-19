/*
 * This file is part of jsFlow.
 *
 * Copyright (c) 2009 DE-CIX Management GmbH <http://www.de-cix.net> - All rights
 * reserved.
 * 
 * Author: Thomas King <thomas.king@de-cix.net>
 *
 * This software is licensed under the Apache License, version 2.0. A copy of 
 * the license agreement is included in this distribution.
 */
package net.decix.jsflow.header;

import java.util.Vector;

import net.decix.util.Address;
import net.decix.util.HeaderBytesException;
import net.decix.util.HeaderException;
import net.decix.util.HeaderParseException;
import net.decix.util.MacAddress;
import net.decix.util.Utility;

public class SFlowHeader {
	private long version; // 2, 4, 5
	private long ipVersionAgent; // 1=v4, 2=v6
	private Address addressAgent;
	private long subAgentID;
	private long seqNumber;
	private long sysUptime; // in milliseconds

	private long numberSamples; // in datagrams
	private Vector<SampleDataHeader> sampleDataHeaders;

	public SFlowHeader() {
		sampleDataHeaders = new Vector<SampleDataHeader>();
	}

	public void setVersion(long version) throws HeaderException {
		if (!((version == 2) || (version == 4) || (version == 5))) throw new HeaderException("Version " + version + " is not in the valid range (2, 4, 5)");
		this.version = version;
	}

	public void setIPVersionAgent(long ipVersionAgent) throws HeaderException {
		if (!((ipVersionAgent == 1) || (ipVersionAgent == 2))) throw new HeaderException("IPVersionAgent " + ipVersionAgent + " is not in the valid rang (1, 2)");
		this.ipVersionAgent = ipVersionAgent;
	}
	
	public long getIPVersionAgent() {
		return ipVersionAgent;
	}

	public void setAddressAgent(Address addressAgent) {
		this.addressAgent = addressAgent;
	}
	
	public Address getAddressAgent() {
		return addressAgent;
	}
	
	public long getSubAgentID() {
		return subAgentID;
	}
	
	public long getSeqNumber() {
		return seqNumber;
	}
	
	public long getVersion() {
		return version;
	}
	
	public long getSysUptime() {
		return sysUptime;
	}

	public void setSubAgentID(long subAgentID) {
		this.subAgentID = subAgentID;
	}

	public void setSequenceNumber(long seqNumber) {
		this.seqNumber = seqNumber;
	}
	
	public void setSystemUptime(long sysUptime) {
		this.sysUptime = sysUptime;
	}

	public void setNumberSamples(long numberSamples) {
		this.numberSamples = numberSamples;
	}
	
	public long getNumberSample() {
		return numberSamples;
	}
	
	public void addSampleDataHeader(SampleDataHeader sampleDataHeader) {
		sampleDataHeaders.add(sampleDataHeader);
	}
	
	public Vector<SampleDataHeader> getSampleDataHeaders() {
		return sampleDataHeaders;
	}

	public static SFlowHeader parse(byte[] data) throws HeaderParseException {
		try {
			if (data.length < 28) throw new HeaderParseException("Data array too short.");
			SFlowHeader rph = new SFlowHeader();
			// sflow version
			byte[] version = new byte[4];
			System.arraycopy(data, 0, version, 0, 4);
			rph.setVersion(Utility.fourBytesToLong(version));
			// agent ip version
			byte[] ipVersion = new byte[4];
			System.arraycopy(data, 4, ipVersion, 0, 4);
			rph.setIPVersionAgent(Utility.fourBytesToLong(ipVersion));
			// agent ip address
			if (rph.getIPVersionAgent() == 1) { // IPv4
				int firstOctet = Utility.oneByteToInteger(data[8]);
				int secondOctet = Utility.oneByteToInteger(data[9]);
				int thirdOctet = Utility.oneByteToInteger(data[10]);
				int fourthOctet = Utility.oneByteToInteger(data[11]);
				rph.setAddressAgent(new Address(firstOctet, secondOctet, thirdOctet, fourthOctet));
			}
			if (rph.getIPVersionAgent() == 2) { // IPv6
				System.err.println("IPv6 is not supported by RawPacketHeader");
			}
			// subagent id
			byte[] subAgentID = new byte[4];
			System.arraycopy(data, 12, subAgentID, 0, 4);
			rph.setSubAgentID(Utility.fourBytesToLong(subAgentID));
			// datagram sequence number
			byte[] seqNumber = new byte[4];
			System.arraycopy(data, 16, seqNumber, 0, 4);
			rph.setSequenceNumber(Utility.fourBytesToLong(seqNumber));
			// sys uptime
			byte[] sysUptime = new byte[4];
			System.arraycopy(data, 20, sysUptime, 0, 4);
			rph.setSystemUptime(Utility.fourBytesToLong(sysUptime));
			// number samples
			byte[] numberSamples = new byte[4];
			System.arraycopy(data, 24, numberSamples, 0, 4);
			rph.setNumberSamples(Utility.fourBytesToLong(numberSamples));
			// sample data headers
			int offset = 28;
			for (int i = 0; i < rph.getNumberSample(); i++) {
				byte[] subData = new byte[data.length - offset]; 
				System.arraycopy(data, offset, subData, 0, data.length - offset);
				SampleDataHeader sdh = SampleDataHeader.parse(subData);
				rph.addSampleDataHeader(sdh);
				offset += (sdh.getSampleLength() + 8);
			}
			return rph;
		} catch (Exception e) {
			throw new HeaderParseException("Parse error: " + e.getMessage());
		}
	}

	public byte[] getBytes() throws HeaderBytesException {
		try {
			int lengthSampleData = 0;
			for (SampleDataHeader sd : sampleDataHeaders) {
				lengthSampleData += (sd.getSampleLength() + 8);
			}
			
			byte[] data = new byte[0];
			if (ipVersionAgent == 1) data = new byte[28 + lengthSampleData];
			if (ipVersionAgent == 2) {
				System.err.println("IPv6 is not supported by RawPacketHeader");
			}
			// sflow version
			System.arraycopy(Utility.longToFourBytes(version), 0, data, 0, 4);
			// agent ip version
			System.arraycopy(Utility.longToFourBytes(ipVersionAgent), 0, data, 4, 4);
			// agent ip address
			System.arraycopy(addressAgent.getBytes(), 0, data, 8, 4);
			// subagent id
			System.arraycopy(Utility.longToFourBytes(subAgentID), 0, data, 12, 4);
			// datagram sequence number
			System.arraycopy(Utility.longToFourBytes(seqNumber), 0, data, 16, 4);
			// sys uptime
			System.arraycopy(Utility.longToFourBytes(sysUptime), 0, data, 20, 4);
			// number samples
			System.arraycopy(Utility.longToFourBytes(numberSamples), 0, data, 24, 4);
			
			int offset = 0;
			for (SampleDataHeader sd : sampleDataHeaders) {
				byte[] temp = sd.getBytes();
				System.arraycopy(temp, 0, data, 28 + offset, temp.length);
				offset += (sd.getSampleLength() + 8);
			}
			return data;
		} catch (Exception e) {
			throw new HeaderBytesException("Error while generating the bytes: " + e.getMessage());
		}
	}
	
	public String toString(){
		String retVal = "[SflowHeader]" + "\n\tVersion=" + this.getVersion()
										+ "\n\tIPAgentVersion=" + this.getIPVersionAgent()
										+ "\n\tIPAdressOfAgent=" + this.getAddressAgent()
										+ "\n\tSubAgentID=" + this.getSubAgentID()
										+ "\n\tDatagramSequenceNumber=" + this.getSeqNumber()
										+ "\n\tSwitchUptime=" + this.getSysUptime()
										+ "\n\tSamples(" + this.getNumberSample() + ")";
		for(SampleDataHeader sdh : sampleDataHeaders){
			retVal += sdh;
		}
		return retVal;
	}
}
