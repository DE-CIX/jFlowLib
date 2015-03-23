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

import java.math.BigInteger;

import net.decix.util.HeaderBytesException;
import net.decix.util.HeaderParseException;
import net.decix.util.Utility;

public class GenericInterfaceCounterHeader {
	// enterprise = 0, format = 1
	private long ifIndex;
	private long ifType;
	private BigInteger ifSpeed;
	private long ifDirection; // 0 = unknown, 1 = full-duplex, 2 = half-duplex, 3 = in, 4 = out
	private long ifStatus; // bit 0 => ifAdminStatus 0=down|1=up, bit 1 => ifOperStatus 0=down|1=up
	private BigInteger ifInOctets;
	private long ifInUcastPkts;
	private long ifInMulticastPkts;
	private long ifInBroadcastPkts;
	private long ifInDiscards;
	private long ifInErrors;
	private long ifInUnknownProtos;
	private BigInteger ifOutOctets;
	private long ifOutUcastPkts;
	private long ifOutMulticastPkts;
	private long ifOutBroadcastPkts;
	private long ifOutDiscards;
	private long ifOutErrors;
	private long ifPromiscuousMode;

	private CounterData counterData;

	public long getIfIndex() {
		return ifIndex;
	}

	public long getIfType() {
		return ifType;
	}

	public BigInteger getIfSpeed(){
		return ifSpeed;
	}

	public long getIfDirection() {
		return ifDirection;
	}

	public long getIfStatus() {
		return ifStatus;
	}

	public BigInteger getIfInOctets(){
		return ifInOctets;
	}

	public long getIfInUcastPkts() {
		return ifInUcastPkts;
	}

	public long getIfInMulticastPkts() {
		return ifInMulticastPkts;
	}

	public long getIfInBroadcastPkts() {
		return ifInBroadcastPkts;
	}

	public long getIfInDiscards() {
		return ifInDiscards;
	}

	public long getIfInErrors() {
		return ifInErrors;
	}

	public long getIfInUnknownProtos() {
		return ifInUnknownProtos;
	}

	public BigInteger getIfOutOctets(){
		return ifOutOctets;
	}

	public long getIfOutUcastPkts() {
		return ifOutUcastPkts;
	}

	public long getIfOutMulticastPkts() {
		return ifOutMulticastPkts;
	}

	public long getIfOutBroadcastPkts() {
		return ifOutBroadcastPkts;
	}

	public long getIfOutDiscards() {
		return ifOutDiscards;
	}

	public long getIfOutErrors() {
		return ifOutErrors;
	}

	public long getIfPromiscuousMode() {
		return ifPromiscuousMode;
	}

	public void setIfIndex(long ifIndex) {
		this.ifIndex = ifIndex;
	}

	public void setIfType(long ifType) {
		this.ifType = ifType;
	}

	public void setIfSpeed(BigInteger ifSpeed){
		this.ifSpeed = ifSpeed;
	}

	public void setIfDirection(long ifDirection) {
		this.ifDirection = ifDirection;
	}

	public void setIfStatus(long ifStatus) {
		this.ifStatus = ifStatus;
	}

	public void setIfInOctets(BigInteger ifInOctets){
		this.ifInOctets = ifInOctets;
	}

	public void setIfInUcastPkts(long ifInUcastPkts) {
		this.ifInUcastPkts = ifInUcastPkts;
	}

	public void setIfInMulticastPkts(long ifInMulticastPkts) {
		this.ifInMulticastPkts = ifInMulticastPkts;
	}

	public void setIfInBroadcastPkts(long ifInBroadcastPkts) {
		this.ifInBroadcastPkts = ifInBroadcastPkts;
	}

	public void setIfInDiscards(long ifInDiscards) {
		this.ifInDiscards = ifInDiscards;
	}

	public void setIfInErrors(long ifInErrors) {
		this.ifInErrors = ifInErrors;
	}

	public void setIfInUnknownProtos(long ifInUnknownProtos) {
		this.ifInUnknownProtos = ifInUnknownProtos;
	}

	public void setIfOutOctets(BigInteger ifOutOctets){
		this.ifOutOctets = ifOutOctets;
	}

	public void setIfOutUcastPkts(long ifOutUcastPkts) {
		this.ifOutUcastPkts = ifOutUcastPkts;
	}

	public void setIfOutMulticastPkts(long ifOutMulticastPkts) {
		this.ifOutMulticastPkts = ifOutMulticastPkts;
	}

	public void setIfOutBroadcastPkts(long ifOutBroadcastPkts) {
		this.ifOutBroadcastPkts = ifOutBroadcastPkts;
	}

	public void setIfOutDiscards(long ifOutDiscards) {
		this.ifOutDiscards = ifOutDiscards;
	}

	public void setIfOutErrors(long ifOutErrors) {
		this.ifOutErrors = ifOutErrors;
	}

	public void setIfPromiscuousMode(long ifPromiscuousMode) {
		this.ifPromiscuousMode = ifPromiscuousMode;
	}

	public void setCounterData(CounterData counterData) {
		this.counterData = counterData;
	}

	public CounterData getCounterData() {
		return counterData;
	}

	public static GenericInterfaceCounterHeader parse(byte[] data) throws HeaderParseException {
		try {
			if (data.length < 88) throw new HeaderParseException("Data array too short.");
			GenericInterfaceCounterHeader gic = new GenericInterfaceCounterHeader();
			// interface index
			byte[] ifIndex = new byte[4];
			System.arraycopy(data, 0, ifIndex, 0, 4);
			gic.setIfIndex(Utility.fourBytesToLong(ifIndex));
			// interface type
			byte[] ifType = new byte[4];
			System.arraycopy(data, 4, ifType, 0, 4);
			gic.setIfType(Utility.fourBytesToLong(ifType));
			// interface speed
			byte[] ifSpeed = new byte[8];
			System.arraycopy(data, 8, ifSpeed, 0, 8);
			gic.setIfSpeed(Utility.eightBytesToBigInteger(ifSpeed));
			// interface direction
			byte[] ifDirection = new byte[4];
			System.arraycopy(data, 16, ifDirection, 0, 4);
			gic.setIfDirection(Utility.fourBytesToLong(ifDirection));
			// interface status
			byte[] ifStatus = new byte[4];
			System.arraycopy(data, 20, ifStatus, 0, 4);
			gic.setIfStatus(Utility.fourBytesToLong(ifStatus));
			// interface input octets -
			byte[] ifInOctets = new byte[8];
			System.arraycopy(data, 24, ifInOctets, 0, 8);
			gic.setIfInOctets(Utility.eightBytesToBigInteger(ifInOctets));
			// interface input unicast packets
			byte[] ifInUcastPkts = new byte[4];
			System.arraycopy(data, 32, ifInUcastPkts, 0, 4);
			gic.setIfInUcastPkts(Utility.fourBytesToLong(ifInUcastPkts));
			// interface input multicast packets
			byte[] ifInMulticastPkts = new byte[4];
			System.arraycopy(data, 36, ifInMulticastPkts, 0, 4);
			gic.setIfInMulticastPkts(Utility.fourBytesToLong(ifInMulticastPkts));
			// interface input broadcast packets
			byte[] ifInBroadcastPkts = new byte[4];
			System.arraycopy(data, 40, ifInBroadcastPkts, 0, 4);
			gic.setIfInBroadcastPkts(Utility.fourBytesToLong(ifInBroadcastPkts));
			// interface input discards
			byte[] ifInDiscards = new byte[4];
			System.arraycopy(data, 44, ifInDiscards, 0, 4);
			gic.setIfInDiscards(Utility.fourBytesToLong(ifInDiscards));
			// interface input errors
			byte[] ifInErrors = new byte[4];
			System.arraycopy(data, 48, ifInErrors, 0, 4);
			gic.setIfInErrors(Utility.fourBytesToLong(ifInErrors));
			// interface input unknown protocols
			byte[] ifInUnknownProtos = new byte[4];
			System.arraycopy(data, 52, ifInUnknownProtos, 0, 4);
			gic.setIfInUnknownProtos(Utility.fourBytesToLong(ifInUnknownProtos));
			// interface output octets
			byte[] ifOutOctets = new byte[8];
			System.arraycopy(data, 56, ifOutOctets, 0, 8);
			gic.setIfOutOctets(Utility.eightBytesToBigInteger(ifOutOctets));
			// interface output unicast packets
			byte[] ifOutUcastPkts = new byte[4];
			System.arraycopy(data, 64, ifOutUcastPkts, 0, 4);
			gic.setIfOutUcastPkts(Utility.fourBytesToLong(ifOutUcastPkts));
			// interface output multicast packets
			byte[] ifOutMulticastPkts = new byte[4];
			System.arraycopy(data, 68, ifOutMulticastPkts, 0, 4);
			gic.setIfOutMulticastPkts(Utility.fourBytesToLong(ifOutMulticastPkts));
			// interface output broadcast packets
			byte[] ifOutBroadcastPkts = new byte[4];
			System.arraycopy(data, 72, ifOutBroadcastPkts, 0, 4);
			gic.setIfOutBroadcastPkts(Utility.fourBytesToLong(ifOutBroadcastPkts));
			// interface output discards
			byte[] ifOutDiscards = new byte[4];
			System.arraycopy(data, 76, ifOutDiscards, 0, 4);
			gic.setIfOutDiscards(Utility.fourBytesToLong(ifOutDiscards));
			// interface output errors
			byte[] ifOutErrors = new byte[4];
			System.arraycopy(data, 80, ifOutErrors, 0, 4);
			gic.setIfOutErrors(Utility.fourBytesToLong(ifOutErrors));
			// interface promiscuous mode
			byte[] ifPromiscuousMode = new byte[4];
			System.arraycopy(data, 84, ifPromiscuousMode, 0, 4);
			gic.setIfPromiscuousMode(Utility.fourBytesToLong(ifPromiscuousMode));


			// counter data
			byte[] subData = new byte[data.length - 88]; 
			System.arraycopy(data, 88, subData, 0, data.length - 88);
			CounterData cd = CounterData.parse(subData);
			gic.setCounterData(cd);

			return gic;
		} catch (Exception e) {
			throw new HeaderParseException("Parse error: " + e.getMessage());
		}
	}

	public byte[] getBytes() throws HeaderBytesException {
		try {
			byte[] counterDataBytes = counterData.getBytes();
			byte[] data = new byte[88 + counterDataBytes.length];
			// interface index
			System.arraycopy(Utility.longToFourBytes(ifIndex), 0, data, 0, 4);
			// interface type
			System.arraycopy(Utility.longToFourBytes(ifType), 0, data, 4, 4);
			// interface speed
			System.arraycopy(Utility.BigIntegerToEightBytes(ifSpeed), 0, data, 8, 8);
			// interface direction
			System.arraycopy(Utility.longToFourBytes(ifDirection), 0, data, 16, 4);
			// interface status
			System.arraycopy(Utility.longToFourBytes(ifStatus), 0, data, 20, 4);
			// interface input octets
			System.arraycopy(Utility.BigIntegerToEightBytes(ifInOctets), 0, data, 24, 8);
			// interface input unicast packets
			System.arraycopy(Utility.longToFourBytes(ifInUcastPkts), 0, data, 32, 4);
			// interface input multicast packets
			System.arraycopy(Utility.longToFourBytes(ifInMulticastPkts), 0, data, 36, 4);
			// interface input broadcast packets
			System.arraycopy(Utility.longToFourBytes(ifInBroadcastPkts), 0, data, 40, 4);
			// interface input discards
			System.arraycopy(Utility.longToFourBytes(ifInDiscards), 0, data, 44, 4);
			// interface input errors
			System.arraycopy(Utility.longToFourBytes(ifInErrors), 0, data, 48, 4);
			// interface input unknown protocols
			System.arraycopy(Utility.longToFourBytes(ifInUnknownProtos), 0, data, 52, 4);
			// interface output octets
			System.arraycopy(Utility.BigIntegerToEightBytes(ifOutOctets), 0, data, 56, 8);
			// interface output unicast packets
			System.arraycopy(Utility.longToFourBytes(ifOutUcastPkts), 0, data, 64, 4);
			// interface output multicast packets
			System.arraycopy(Utility.longToFourBytes(ifOutMulticastPkts), 0, data, 68, 4);
			// interface output broadcast packets
			System.arraycopy(Utility.longToFourBytes(ifOutBroadcastPkts), 0, data, 72, 4);
			// interface output discards
			System.arraycopy(Utility.longToFourBytes(ifOutDiscards), 0, data, 76, 4);
			// interface output errors
			System.arraycopy(Utility.longToFourBytes(ifOutErrors), 0, data, 80, 4);
			// interface promiscuous mode
			System.arraycopy(Utility.longToFourBytes(ifPromiscuousMode), 0, data, 84, 4);

			// counter data
			System.arraycopy(counterDataBytes, 0, data, 88, counterDataBytes.length);

			return data;
		} catch (Exception e) {
			throw new HeaderBytesException("Error while generating the bytes: " + e.getMessage());
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[GenericInterfaceCounterHeader]: ");
		sb.append("Index: ");
		sb.append(getIfIndex());
		sb.append(", Type: ");
		sb.append(getIfType());
		sb.append(", Speed: ");
		sb.append(getIfSpeed());
		sb.append(", Direction: ");
		sb.append(getIfDirection());
		sb.append(", Status: ");
		sb.append(getIfStatus());
		sb.append(", Octets(IN): ");
		sb.append(getIfInOctets());
		sb.append(", Unicast(IN): ");
		sb.append(getIfInUcastPkts());
		sb.append(", Multicast(IN): ");
		sb.append(getIfInMulticastPkts());
		sb.append(", Broadcast(IN): ");
		sb.append(getIfInBroadcastPkts());
		sb.append(", Discards(IN): ");
		sb.append(getIfInDiscards());
		sb.append(", Errors(IN): ");
		sb.append(getIfInErrors());
		sb.append(", tUnknownProtocol(IN): ");
		sb.append(getIfInUnknownProtos());
		sb.append(", Octets(OUT): ");
		sb.append(getIfOutOctets());
		sb.append(", Unicast(OUT): ");
		sb.append(getIfOutUcastPkts());
		sb.append(", Multicast(OUT): ");
		sb.append(getIfOutMulticastPkts());
		sb.append(", Broadcast(OUT): ");
		sb.append(getIfOutBroadcastPkts());
		sb.append(", Discards(OUT): ");
		sb.append(getIfOutDiscards());
		sb.append(", Errors(OUT): ");
		sb.append(getIfOutErrors());
		sb.append(", PromiscuousMode: ");
		sb.append(getIfPromiscuousMode());
		
		return sb.toString();
	}
}