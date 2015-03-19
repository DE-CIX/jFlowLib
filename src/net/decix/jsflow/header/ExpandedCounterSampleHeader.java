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

import net.decix.util.HeaderBytesException;
import net.decix.util.HeaderParseException;
import net.decix.util.Utility;

public class ExpandedCounterSampleHeader {
	// enterprise 0, format 4
	private long seqNumber;
	private long sourceIDType; // 0 = ifindex, 1 = smonVlanDataSource, 2 = entPhysicalEntry
	private long sourceIDIndex;
	private long numberCounterRecords;
	
	private Vector<CounterRecordHeader> counterRecords;
	
	public ExpandedCounterSampleHeader() {
		counterRecords = new Vector<CounterRecordHeader>();
	}
	
	public long getSequenceNumber() {
		return seqNumber;
	}

	public long getSourceIDType() {
		return sourceIDType;
	}

	public long getSourceIDIndex() {
		return sourceIDIndex;
	}
	
	public long getNumberCounterRecords() {
		return numberCounterRecords;
	}

	public void setSequenceNumber(long seqNumber) {
		this.seqNumber = seqNumber;
	}

	public void setSourceIDType(long sourceIDType) {
		this.sourceIDType = sourceIDType;
	}

	public void setSourceIDIndex(long sourceIDIndex) {
		this.sourceIDIndex = sourceIDIndex;
	}
	
	public void setNumberCounterRecords(long numberCounterRecords) {
		this.numberCounterRecords = numberCounterRecords;
	}
	
	public void addCounterRecord(CounterRecordHeader counterRecord) {
		counterRecords.add(counterRecord);
	}
	
	public Vector<CounterRecordHeader> getCounterRecords() {
		return counterRecords;
	}

	public static ExpandedCounterSampleHeader parse(byte[] data) throws HeaderParseException {
		try {
			if (data.length < 16) throw new HeaderParseException("Data array too short.");
			ExpandedCounterSampleHeader ecsh = new ExpandedCounterSampleHeader();
			// sample sequence number
			byte[] seqNumber = new byte[4];
			System.arraycopy(data, 0, seqNumber, 0, 4);
			ecsh.setSequenceNumber(Utility.fourBytesToLong(seqNumber));
			// source id type
			byte[] sourceIDType = new byte[4];
			System.arraycopy(data, 4, sourceIDType, 0, 4);
			ecsh.setSourceIDType(Utility.fourBytesToLong(sourceIDType));
			// source id index
			byte[] sourceIDIndex = new byte[4];
			System.arraycopy(data, 8, sourceIDIndex, 0, 4);
			ecsh.setSourceIDIndex(Utility.fourBytesToLong(sourceIDIndex));
			// number counter records
			byte[] numberCounterRecords = new byte[4];
			System.arraycopy(data, 12, numberCounterRecords, 0, 4);
			ecsh.setNumberCounterRecords(Utility.fourBytesToLong(numberCounterRecords));
			
			// counter records
			int offset = 16;
			for (int i = 0; i < ecsh.getNumberCounterRecords(); i++) {
				byte[] subData = new byte[data.length - offset]; 
				System.arraycopy(data, offset, subData, 0, data.length - offset);
				CounterRecordHeader cr = CounterRecordHeader.parse(subData);
				ecsh.addCounterRecord(cr);
				offset += (cr.getCounterDataLength() + 8);
			}
			return ecsh;
		} catch (Exception e) {
			throw new HeaderParseException("Parse error: " + e.getMessage());
		}
	}
	
	public byte[] getBytes() throws HeaderBytesException {
		try {
			int lengthCounterRecords = 0;
			for (CounterRecordHeader cr : counterRecords) {
				lengthCounterRecords += (cr.getCounterDataLength() + 8);
			}
			byte[] data = new byte[16 + lengthCounterRecords];
			// sequence number
			System.arraycopy(Utility.longToFourBytes(seqNumber), 0, data, 0, 4);
			// source id type
			System.arraycopy(Utility.longToFourBytes(sourceIDType), 0, data, 4, 4);
			// source id index
			System.arraycopy(Utility.longToFourBytes(sourceIDIndex), 0, data, 8, 4);
			// number counter records
			System.arraycopy(Utility.longToFourBytes(numberCounterRecords), 0, data, 12, 4);
			
			int offset = 0;
			for (CounterRecordHeader cr : counterRecords) {
				byte[] temp = cr.getBytes();
				System.arraycopy(temp, 0, data, 16 + offset, temp.length);
				
				offset += (cr.getCounterDataLength() + 8);
			}
			return data;
		} catch (Exception e) {
			throw new HeaderBytesException("Error while generating the bytes: " + e.getMessage());
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ExpandedCounterSampleHeader]: ");
		sb.append(" Sequence number: ");
		sb.append(getSequenceNumber());
		sb.append(", Source ID type: ");
		sb.append(getSourceIDType());
		sb.append(", Source ID index: ");
		sb.append(getSourceIDIndex());
		sb.append(", Counter records: ");
		sb.append(getNumberCounterRecords());
		for (CounterRecordHeader crh : this.getCounterRecords()) {
			sb.append(crh);
			sb.append(" ");
		}
		
		return sb.toString();
	}
}
