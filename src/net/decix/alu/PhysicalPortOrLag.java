package net.decix.alu;
/*
 * This file is part of jFlowLib.
 * 
 * Copyright (c) 2015 DE-CIX Management GmbH <http://www.de-cix.net> - All rights
 * reserved.
 * 
 * Author: Thomas King <thomas.king@de-cix.net>
 * 
 * This software is licensed under the Apache License, version 2.0. A copy of 
 * the license agreement is included in this distribution.
 * 
 * A portid is an unique 32 bit number encoded as shown below.
 *
 *           32 30 | 29 26 | 25 22 | 21 16 | 15  1 |
 *           +-----+-------+-------+-------+-------+
 *           |000  |  slot |  mda  | port  |  zero | Physical Port
 *           +-----+-------+-------+-------+-------+
 * 
 * The XRS platform uses a new port mapping, starting in 11.0R4, as
 * follows:
 *
 *           32 29 | 28 24 | 23 19 | 18 15 | 14  11 | 10 4 | 3  1 |
 *           +-----+-------+-------+-------+--------+------+------+
 *           |0110 |  zero |  slot |  mda  |  zero  | port | zero | Physical Port
 *           +-----+-------+-------+-------+--------+------+------+ 
 *
 * Slots, mdas (if present), ports, and channels are numbered
 * starting with 1.
 *
 *           32     29 | 28                9 | 8 1 |
 *           +---------+---------------------+-----+
 *           | 0 1 0 1 |   zeros             | ID  | LAG Port
 *           +---------+---------------------+-----+
 *
 * A card port number (cpn) has significance within the context
 * of the card on which it resides(ie., cpn 2 may exist in one or
 * more cards in the chassis).  Whereas, portid is an
 * unique/absolute port number (apn) within a given chassis.
 *
 * A card port number (cpn) has significance within the context
 * of the card on which it resides(ie., cpn 2 may exist in one or
 * more cards in the chassis).  Whereas, portid is an
 * unique/absolute port number (apn) within a given chassis.
 *       
 * @author tking
 */
public class PhysicalPortOrLag {
	private boolean mLag = false; // if mIsLag is false, then it is a physical port
	private int mLagId = 0;
	private int mSlot = 0;
	private int mMda = 0;
	private int mPort = 0;
	private boolean mXRS = false;
	
	public boolean isLag() {
		return mLag;
	}
	
	public void setLag(boolean mIsLag) {
		this.mLag = mIsLag;
	}
	
	public boolean isPhysicalPort() {
		return (!mLag);
	}
	
	public void setPhysicalPort(boolean physicalPort) {
		this.mLag = (!physicalPort);
	}
	
	public int getLagId() {
		return mLagId;
	}
	
	public void setLagId(int lagId) {
		this.mLagId = lagId;
	}
	
	public int getSlot() {
		return mSlot;
	}
	
	public void setSlot(int slot) {
		this.mSlot = slot;
	}
	
	public int getMda() {
		return mMda;
	}
	
	public void setMda(int mda) {
		this.mMda = mda;
	}
	
	public int getPort() {
		return mPort;
	}
	
	public void setPort(int port) {
		this.mPort = port;
	}
	
	public boolean isXRS() {
		return mXRS;
	}
	
	public void setXRS(boolean XRS) {
		this.mXRS = XRS;
	}
	
	public int toInterfaceIndex() {
		int interfaceIndex = 0;
		if (mLag) {
			interfaceIndex = 0x50000000;
			interfaceIndex = interfaceIndex | mLagId;
		} else {
			interfaceIndex = 0x60000000;
			
			// slot mapping
			int slotMoved = mSlot << 18;
			interfaceIndex = interfaceIndex | slotMoved;
			
			// mda mapping
			int mdaMoved = mMda << 14;
			interfaceIndex = interfaceIndex | mdaMoved;
			
			// port mapping
			int portMoved = mPort << 3;
			interfaceIndex = interfaceIndex | portMoved;
		}
		return interfaceIndex;
	}
	
	public static PhysicalPortOrLag parse(int interfaceIndex) {
		PhysicalPortOrLag ppol = new PhysicalPortOrLag();
		
		//int identifier = interfaceIndex & 0x60000000;
		
		// non-XRS systems
		int identifier = interfaceIndex >> 29;
		if (identifier == 0) {
			ppol.setXRS(false);
			ppol.setPhysicalPort(true);
			
			// port mapping
			int port = interfaceIndex & 0x01F80000;
			port = port >> 15;
			
			ppol.setPort(port);
			
			// mda mapping
			int mda = interfaceIndex & 0x01E00000;
			mda = mda >> 21;
			
			ppol.setMda(mda);
			
			// slot mapping
			int slot = interfaceIndex & 0x1E000000;
			slot = slot >> 25;
			
			ppol.setSlot(slot);
			
			return ppol;
		}
		
		// XRS systems
		identifier = interfaceIndex >> 28;
		
		if (identifier == 6) {
			ppol.setXRS(true);
			ppol.setPhysicalPort(true);
			
			// port mapping
			int port = interfaceIndex & 0x000001F8;
			port = port >> 3;
			
			ppol.setPort(port);
			
			// mda mapping
			int mda = interfaceIndex & 0x0003D000;
			mda = mda >> 14;
			
			ppol.setMda(mda);
			
			// slot mapping
			int slot = interfaceIndex & 0x007C0000;
			slot = slot >> 18;
			
			ppol.setSlot(slot);
			return ppol;
		} else if (identifier == 5) {
			// lag
			ppol.setXRS(true);
			ppol.setLag(true);
			int lagId = interfaceIndex ^ 0x50000000;
			ppol.setLagId(lagId);
			return ppol;
		}
		return ppol;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("XRS: ");
		sb.append(mXRS);
		sb.append(", Lag: ");
		sb.append(mLag);
		sb.append(", Physical Port: ");
		sb.append(!mLag);
		sb.append(", Lag ID: ");
		sb.append(mLagId);
		sb.append(", Slot: ");
		sb.append(mSlot);
		sb.append(", MDA: ");
		sb.append(mMda);
		sb.append(", Port: ");
		sb.append(mPort);
		return sb.toString();
	}
}
