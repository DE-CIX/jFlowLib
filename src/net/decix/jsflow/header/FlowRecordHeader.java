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

import net.decix.util.HeaderBytesException;
import net.decix.util.HeaderParseException;
import net.decix.util.Utility;

public class FlowRecordHeader {
	private long flowDataFormat; // 20 bit enterprise & 12 bit format; standard enterprise 0, format 1, 2, 3, 4, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010, 1011, 1012 
	private long flowDataLength; // in byte
	
	private RawPacketHeader rawPacket;
	
	public FlowRecordHeader() {
	}

	public long getFlowDataFormat() {
		return flowDataFormat;
	}

	public long getFlowDataLength() {
		return flowDataLength;
	}

	public void setFlowDataFormat(long flowDataFormat) {
		this.flowDataFormat = flowDataFormat;
	}

	public void setFlowDataLength(long flowDataLength) {
		this.flowDataLength = flowDataLength;
	}
	
	public void setRawPacketHeader(RawPacketHeader rawPacket) {
		this.rawPacket = rawPacket;
	}
	
	public RawPacketHeader getRawPacketHeader() {
		return rawPacket;
	}
	
	public static FlowRecordHeader parse(byte[] data) throws HeaderParseException {
		try {
			if (data.length < 8) throw new HeaderParseException("Data array too short.");
			FlowRecordHeader frd = new FlowRecordHeader();
			// format
			byte[] format = new byte[4];
			System.arraycopy(data, 0, format, 0, 4);
			frd.setFlowDataFormat(Utility.fourBytesToLong(format));
			// length
			byte[] length = new byte[4];
			System.arraycopy(data, 4, length, 0, 4);
			frd.setFlowDataLength(Utility.fourBytesToLong(length));
			
			// raw packet header
			byte[] subData = new byte[(int) frd.getFlowDataLength()]; 
			System.arraycopy(data, 8, subData, 0, (int) frd.getFlowDataLength());
			RawPacketHeader rp = RawPacketHeader.parse(subData);
			frd.setRawPacketHeader(rp);
			return frd;
		} catch (Exception e) {
			throw new HeaderParseException("Parse error: " + e.getMessage());
		}
	}
	
	public byte[] getBytes() throws HeaderBytesException {
		try {
			byte[] rawPacketBytes = rawPacket.getBytes();
			byte[] data = new byte[8 + rawPacketBytes.length];
			// format
			System.arraycopy(Utility.longToFourBytes(flowDataFormat), 0, data, 0, 4);
			// length
			System.arraycopy(Utility.longToFourBytes(flowDataLength), 0, data, 4, 4);
			
			// raw packet header
			System.arraycopy(rawPacketBytes, 0, data, 8, rawPacketBytes.length);
			return data;
		} catch (Exception e) {
			throw new HeaderBytesException("Error while generating the bytes: " + e.getMessage());
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[FlowRecordHeader]:");
		sb.append("Format: ");
		sb.append(getFlowDataFormat());
		sb.append(", Length: ");
		sb.append(getFlowDataLength());
		sb.append(", ");
		sb.append(getRawPacketHeader());
		
		return sb.toString();
	}
}
