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

public class CounterRecordHeader {
	public static final int GENERICINTERFACECOUNTER = 1;
	public static final int ETHERNETINTERFACECOUNTER = 2;
	
	private long counterDataFormat; // 20 bit enterprise & 12 bit format; standard enterprise 0, format 1, 2, 3, 4, 5, 1001 
	private long counterDataLength; // in byte
	
	private GenericInterfaceCounterHeader genericInterfaceCounter;
	private EthernetInterfaceCounterHeader ethernetInterfaceCounter;
	
	
	public CounterRecordHeader() {
	}

	public long getCounterDataFormat() {
		return counterDataFormat;
	}

	public long getCounterDataLength() {
		return counterDataLength;
	}

	public void setCounterDataFormat(long counterDataFormat) {
		this.counterDataFormat = counterDataFormat;
	}

	public void setCounterDataLength(long counterDataLength) {
		this.counterDataLength = counterDataLength;
	}
	
	public void setEthernetInterfaceCounterHeader(EthernetInterfaceCounterHeader ethernetInterfaceCounter) {
		this.ethernetInterfaceCounter = ethernetInterfaceCounter;
	}
	
	public EthernetInterfaceCounterHeader getEthernetInterfaceCounterHeader() {
		return ethernetInterfaceCounter;
	}
	
	public void setGenericInterfaceCounterHeader(GenericInterfaceCounterHeader genericInterfaceCounter) {
		this.genericInterfaceCounter = genericInterfaceCounter;
	}
	
	public GenericInterfaceCounterHeader getGenericInterfaceCounterHeader() {
		return genericInterfaceCounter;
	}

	public static CounterRecordHeader parse(byte[] data) throws HeaderParseException {
		try {
			if (data.length < 8) throw new HeaderParseException("Data array too short.");
			CounterRecordHeader crd = new CounterRecordHeader();
			// format
			byte[] format = new byte[4];
			System.arraycopy(data, 0, format, 0, 4);
			crd.setCounterDataFormat(Utility.fourBytesToLong(format));
			// length
			byte[] length = new byte[4];
			System.arraycopy(data, 4, length, 0, 4);
			crd.setCounterDataLength(Utility.fourBytesToLong(length));
			
			byte[] subData = new byte[(int) crd.getCounterDataLength()]; 
			System.arraycopy(data, 8, subData, 0, (int) crd.getCounterDataLength());
			if (crd.getCounterDataFormat() == GENERICINTERFACECOUNTER) {
				GenericInterfaceCounterHeader gic = GenericInterfaceCounterHeader.parse(subData);
				crd.setGenericInterfaceCounterHeader(gic);
			}
			if (crd.getCounterDataFormat() == ETHERNETINTERFACECOUNTER) {
				EthernetInterfaceCounterHeader eic = EthernetInterfaceCounterHeader.parse(subData);
				crd.setEthernetInterfaceCounterHeader(eic);
			}
			if ((crd.getCounterDataFormat() != GENERICINTERFACECOUNTER) && (crd.getCounterDataFormat() != ETHERNETINTERFACECOUNTER)) {
				System.err.println("Counter data format not yet supported: " + crd.getCounterDataFormat());
			}
			
			return crd;
		} catch (Exception e) {
			throw new HeaderParseException("Parse error: " + e.getMessage());
		}
	}
	
	public byte[] getBytes() throws HeaderBytesException {
		try {
			int length = 0;
			byte[] genericInterfaceCounterBytes = null;
			byte[] ethernetInterfaceCounterBytes = null;
			if (genericInterfaceCounter != null) {
				genericInterfaceCounterBytes = genericInterfaceCounter.getBytes();
				length += genericInterfaceCounterBytes.length;
			}
			if (ethernetInterfaceCounter != null) {
				ethernetInterfaceCounterBytes = ethernetInterfaceCounter.getBytes();
				length += ethernetInterfaceCounterBytes.length;
			}
			byte[] data = new byte[8 + length];
			// format
			System.arraycopy(Utility.longToFourBytes(counterDataFormat), 0, data, 0, 4);
			// length
			System.arraycopy(Utility.longToFourBytes(counterDataLength), 0, data, 4, 4);
			
			// generic interface counter
			if (genericInterfaceCounter != null) System.arraycopy(genericInterfaceCounterBytes, 0, data, 8, genericInterfaceCounterBytes.length);
			// ethernet interface counter
			if (ethernetInterfaceCounter != null) System.arraycopy(ethernetInterfaceCounterBytes, 0, data, 8, ethernetInterfaceCounterBytes.length);
			return data;
		} catch (Exception e) {
			throw new HeaderBytesException("Error while generating the bytes: " + e.getMessage());
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[CounterRecordHeader]: ");
		sb.append("Format: ");
		sb.append(getCounterDataFormat());
		sb.append(", Length:");
		sb.append(getCounterDataLength());
		if (this.getCounterDataFormat() == CounterRecordHeader.GENERICINTERFACECOUNTER){
			sb.append(genericInterfaceCounter);
		}else if(this.getCounterDataFormat() == CounterRecordHeader.ETHERNETINTERFACECOUNTER){
			sb.append(ethernetInterfaceCounter);
		}else{
			sb.append("unsupported CounterRecordData format");
		}
		return sb.toString();
	}
}
