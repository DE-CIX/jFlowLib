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

public class ExpandedFlowSampleHeader {
	// enterprise 0, format 3
	private long seqNumber;
	private long sourceIDType; // 0 = ifindex, 1 = smonVlanDataSource, 2 = entPhysicalEntry
	private long sourceIDIndex;
	private long samplingRate;
	private long samplePool; // total number of packets that could have been sampled
	private long drops; // packets dropped due a lack of resources
	private long inputInterfaceFormat; 
	private long inputInterfaceValue;
	private long outputInterfaceFormat; 
	private long outputInterfaceValue;
	private long numberFlowRecords;
	
	private Vector<FlowRecordHeader> flowRecords;
	
	public ExpandedFlowSampleHeader() {
		flowRecords = new Vector<FlowRecordHeader>();
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

	public long getSamplingRate() {
		return samplingRate;
	}

	public long getSamplePool() {
		return samplePool;
	}

	public long getDrops() {
		return drops;
	}

	public long getInputInterfaceFormat() {
		return inputInterfaceFormat;
	}

	public long getInputInterfaceValue() {
		return inputInterfaceValue;
	}

	public long getOutputInterfaceFormat() {
		return outputInterfaceFormat;
	}

	public long getOutputInterfaceValue() {
		return outputInterfaceValue;
	}

	public long getNumberFlowRecords() {
		return numberFlowRecords;
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

	public void setSamplingRate(long samplingRate) {
		this.samplingRate = samplingRate;
	}

	public void setSamplePool(long samplePool) {
		this.samplePool = samplePool;
	}

	public void setDrops(long drops) {
		this.drops = drops;
	}

	public void setInputInterfaceFormat(long inputInterfaceFormat) {
		this.inputInterfaceFormat = inputInterfaceFormat;
	}

	public void setInputInterfaceValue(long inputInterfaceValue) {
		this.inputInterfaceValue = inputInterfaceValue;
	}

	public void setOutputInterfaceFormat(long outputInterfaceFormat) {
		this.outputInterfaceFormat = outputInterfaceFormat;
	}

	public void setOutputInterfaceValue(long outputInterfaceValue) {
		this.outputInterfaceValue = outputInterfaceValue;
	}

	public void setNumberFlowRecords(long numberFlowRecords) {
		this.numberFlowRecords = numberFlowRecords;
	}
	
	public void addFlowRecord(FlowRecordHeader flowRecord) {
		flowRecords.add(flowRecord);
	}
	
	public Vector<FlowRecordHeader> getFlowRecords() {
		return flowRecords;
	}
	
	public static ExpandedFlowSampleHeader parse(byte[] data) throws HeaderParseException {
		try {
			if (data.length < 44) throw new HeaderParseException("Data array too short.");
			ExpandedFlowSampleHeader efsh = new ExpandedFlowSampleHeader();
			// sample sequence number
			byte[] seqNumber = new byte[4];
			System.arraycopy(data, 0, seqNumber, 0, 4);
			efsh.setSequenceNumber(Utility.fourBytesToLong(seqNumber));
			// source id type
			byte[] sourceIDType = new byte[4];
			System.arraycopy(data, 4, sourceIDType, 0, 4);
			efsh.setSourceIDType(Utility.fourBytesToLong(sourceIDType));
			// source id index
			byte[] sourceIDIndex = new byte[4];
			System.arraycopy(data, 8, sourceIDIndex, 0, 4);
			efsh.setSourceIDIndex(Utility.fourBytesToLong(sourceIDIndex));
			// sampling rate
			byte[] samplingRate = new byte[4];
			System.arraycopy(data, 12, samplingRate, 0, 4);
			efsh.setSamplingRate(Utility.fourBytesToLong(samplingRate));
			// sample pool
			byte[] samplePool = new byte[4];
			System.arraycopy(data, 16, samplePool, 0, 4);
			efsh.setSamplePool(Utility.fourBytesToLong(samplePool));
			// drops
			byte[] drops = new byte[4];
			System.arraycopy(data, 20, drops, 0, 4);
			efsh.setDrops(Utility.fourBytesToLong(drops));
			// input interface format
			byte[] inputInterfaceFormat = new byte[4];
			System.arraycopy(data, 24, inputInterfaceFormat, 0, 4);
			efsh.setInputInterfaceFormat(Utility.fourBytesToLong(inputInterfaceFormat));
			// input interface value
			byte[] inputInterfaceValue = new byte[4];
			System.arraycopy(data, 28, inputInterfaceValue, 0, 4);
			efsh.setInputInterfaceValue(Utility.fourBytesToLong(inputInterfaceValue));
			// output interface format
			byte[] outputInterfaceFormat = new byte[4];
			System.arraycopy(data, 32, outputInterfaceFormat, 0, 4);
			efsh.setOutputInterfaceFormat(Utility.fourBytesToLong(outputInterfaceFormat));
			// output interface value
			byte[] outputInterfaceValue = new byte[4];
			System.arraycopy(data, 36, outputInterfaceValue, 0, 4);
			efsh.setOutputInterfaceValue(Utility.fourBytesToLong(outputInterfaceValue));
			// number flow records
			byte[] numberFlowRecords = new byte[4];
			System.arraycopy(data, 40, numberFlowRecords, 0, 4);
			efsh.setNumberFlowRecords(Utility.fourBytesToLong(numberFlowRecords));
			
			// flow records
			int offset = 44;
			for (int i = 0; i < efsh.getNumberFlowRecords(); i++) {
				byte[] subData = new byte[data.length - offset]; 
				System.arraycopy(data, offset, subData, 0, data.length - offset);
				FlowRecordHeader fr = FlowRecordHeader.parse(subData);
				efsh.addFlowRecord(fr);
				offset += (fr.getFlowDataLength() + 8);
			}
			return efsh;
		} catch (Exception e) {
			throw new HeaderParseException("Parse error: " + e.getMessage());
		}
	}
	
	public byte[] getBytes() throws HeaderBytesException {
		try {
			int lengthFlowRecords = 0;
			for (FlowRecordHeader fr : flowRecords) {
				lengthFlowRecords += (fr.getFlowDataLength() + 8);
			}
			byte[] data = new byte[44 + lengthFlowRecords];
			// sequence number
			System.arraycopy(Utility.longToFourBytes(seqNumber), 0, data, 0, 4);
			// source id type
			System.arraycopy(Utility.longToFourBytes(sourceIDType), 0, data, 4, 4);
			// source id index
			System.arraycopy(Utility.longToFourBytes(sourceIDIndex), 0, data, 8, 4);
			// sampling rate
			System.arraycopy(Utility.longToFourBytes(samplingRate), 0, data, 12, 4);
			// sample pool
			System.arraycopy(Utility.longToFourBytes(samplePool), 0, data, 16, 4);
			// drops
			System.arraycopy(Utility.longToFourBytes(drops), 0, data, 20, 4);
			// input interface format
			System.arraycopy(Utility.longToFourBytes(inputInterfaceFormat), 0, data, 24, 4);
			// input interface value
			System.arraycopy(Utility.longToFourBytes(inputInterfaceValue), 0, data, 28, 4);
			// output interface format
			System.arraycopy(Utility.longToFourBytes(outputInterfaceFormat), 0, data, 32, 4);
			// output interface value
			System.arraycopy(Utility.longToFourBytes(outputInterfaceValue), 0, data, 36, 4);
			// number flow records
			System.arraycopy(Utility.longToFourBytes(numberFlowRecords), 0, data, 40, 4);
			
			int offset = 0;
			for (FlowRecordHeader fr : flowRecords) {
				byte[] temp = fr.getBytes();
				System.arraycopy(temp, 0, data, 44 + offset, temp.length);
				offset += (fr.getFlowDataLength() + 8);
			}
			return data;
		} catch (Exception e) {
			throw new HeaderBytesException("Error while generating the bytes: " + e.getMessage());
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ExpandedFlowSampleHeader]: ");
		sb.append("SequenceNumber: ");
		sb.append(getSequenceNumber());
		sb.append(", SourceIDtype: ");
		sb.append(getSourceIDType());
		sb.append(", SourceIDindex: ");
		sb.append(getSourceIDIndex());
		sb.append(", SamplingRate: ");
		sb.append(getSamplingRate());
		sb.append(", SamplePool: ");
		sb.append(getSamplePool());
		sb.append(", Drops: ");
		sb.append(getDrops());
		sb.append(", InputInterfaceFormat: ");
		sb.append(getInputInterfaceFormat());
		sb.append(", InputInterfaceValue: ");
		sb.append(getInputInterfaceValue());
		sb.append(", OutputInterfaceFormat: ");
		sb.append(getOutputInterfaceFormat());
		sb.append(", OutputInterfaceValue: ");
		sb.append(getOutputInterfaceValue());
		sb.append(", FlowRecords: ");
		sb.append(getNumberFlowRecords());
		for (FlowRecordHeader frh : flowRecords) {
			sb.append(frh);
			sb.append(" ");
		}
		
		return sb.toString();
	}
}
