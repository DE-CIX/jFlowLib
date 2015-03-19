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

public class RawPacketHeader {
	// enterprise = 0, format = 1
	public static final int ETHERNET_ISO88023 = 1;
	public static final int TOKENBUS_ISO88024 = 2;
	public static final int TOKENRING_ISO88025 = 3;
	public static final int FDDI = 4;
	public static final int FRAME_RELAY = 5;
	public static final int X25 = 6;
	public static final int PPP = 7;
	public static final int SMDS = 8;
	public static final int AAL5 = 9;
	public static final int AAL5_IP = 10;
	public static final int IPV4 = 11;
	public static final int IPV6 = 12;
	public static final int MPLS = 13;
	public static final int POS = 14;
	
	private long headerProtocol;
	private long frameLength;
	private long stripped; // number of bytes removed from the packet
	private long headerSize;
	
	private MacHeader macHeader;

	public long getHeaderProtocol() {
		return headerProtocol;
	}

	public long getFrameLength() {
		return frameLength;
	}

	public long getStripped() {
		return stripped;
	}

	public long getHeaderSize() {
		return headerSize;
	}

	public MacHeader getMacHeader() {
		return macHeader;
	}

	public void setHeaderProtocol(long headerProtocol) {
		this.headerProtocol = headerProtocol;
	}

	public void setFrameLength(long frameLength) {
		this.frameLength = frameLength;
	}

	public void setStripped(long stripped) {
		this.stripped = stripped;
	}

	public void setHeaderSize(long headerSize) {
		this.headerSize = headerSize;
	}

	public void setMacHeader(MacHeader macHeader) {
		this.macHeader = macHeader;
	}
	
	public static RawPacketHeader parse(byte[] data) throws HeaderParseException {
		try {
			if (data.length < 16) throw new HeaderParseException("Data array too short.");
			RawPacketHeader rp = new RawPacketHeader();
			// header protocol
			byte[] headerProtocol = new byte[4];
			System.arraycopy(data, 0, headerProtocol, 0, 4);
			rp.setHeaderProtocol(Utility.fourBytesToLong(headerProtocol));
			// frame length
			byte[] frameLength  = new byte[4];
			System.arraycopy(data, 4, frameLength, 0, 4);
			rp.setFrameLength(Utility.fourBytesToLong(frameLength));
			// stripped
			byte[] stripped  = new byte[4];
			System.arraycopy(data, 8, stripped, 0, 4);
			rp.setStripped(Utility.fourBytesToLong(stripped));
			// header size
			byte[] headerSize  = new byte[4];
			System.arraycopy(data, 12, headerSize, 0, 4);
			rp.setHeaderSize(Utility.fourBytesToLong(headerSize));
			
			if (rp.getHeaderProtocol() == ETHERNET_ISO88023) {
				byte[] macHeader = new byte[data.length - 16]; 
				System.arraycopy(data, 16, macHeader, 0, data.length - 16);
				MacHeader m = MacHeader.parse(macHeader);
				rp.setMacHeader(m);
			} else {
				System.err.println("Sample data format not yet supported: " + rp.getHeaderProtocol());
			}
			
			return rp;
		}  catch (Exception e) {
			throw new HeaderParseException("Parse error: " + e.getMessage());
		}		
	}
	
	public byte[] getBytes() throws HeaderBytesException {
		try {
			byte[] macHeaderBytes = macHeader.getBytes();
			byte[] data = new byte[16 + macHeaderBytes.length];
			// header protocol
			System.arraycopy(Utility.longToFourBytes(headerProtocol), 0, data, 0, 4);
			// interface type
			System.arraycopy(Utility.longToFourBytes(frameLength), 0, data, 4, 4);
			// stripped
			System.arraycopy(Utility.longToFourBytes(stripped), 0, data, 8, 4);
			// header size
			System.arraycopy(Utility.longToFourBytes(headerSize), 0, data, 12, 4);
			
			// mac header
			System.arraycopy(macHeaderBytes, 0, data, 16, macHeaderBytes.length);
			
			return data;
		} catch (Exception e) {
			throw new HeaderBytesException("Error while generating the bytes: " + e.getMessage());
		}
	}
	
	public String toString(){
		String retVal = "\n[RawPacketheader]" + "\n\tHeaderProtocol=" + this.getHeaderProtocol()
											+ "\n\tFrameLength=" + this.getFrameLength()
											+ "\n\tStrippedBytes=" + this.getStripped()
											+ "\n\tHeaderSize=" + this.getHeaderSize();
		retVal += macHeader;
		return retVal;
	}
}

