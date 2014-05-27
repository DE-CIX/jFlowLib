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
package net.decix.util;

import java.util.StringTokenizer;

public class MacAddress {
	private int firstOctet;
	private int secondOctet;
	private int thirdOctet;
	private int fourthOctet;
	private int fifthOctet;
	private int sixthOctet;
		
	public MacAddress(String address) throws UtilityException {
		StringTokenizer st = new StringTokenizer(address, ":");
		if (st.countTokens() != 6) {
			throw new UtilityException("6 octets in a MAC address string are required.");
		}
		int i = 0;
		while (st.hasMoreTokens()) {
			int temp = Integer.parseInt(st.nextToken(), 16);
			if ((temp < 0) || (temp > 255)) {
				throw new UtilityException("Address is in incorrect format.");
			}
			switch (i) {
			case 0: firstOctet = temp; ++i; break;
			case 1: secondOctet = temp; ++i; break;
			case 2: thirdOctet = temp; ++i; break;
			case 3: fourthOctet = temp; ++i; break;
			case 4: fifthOctet = temp; ++i; break;
			case 5: sixthOctet = temp; ++i; break;
			}
		}
	}
	
	public MacAddress(byte[] address) throws UtilityException {
		if (address.length < 6) {
			throw new UtilityException("6 bytes are required.");
		}
		firstOctet = Utility.oneByteToInteger(address[0]);
		secondOctet = Utility.oneByteToInteger(address[1]);
		thirdOctet = Utility.oneByteToInteger(address[2]);
		fourthOctet = Utility.oneByteToInteger(address[3]);
		fifthOctet = Utility.oneByteToInteger(address[4]);
		sixthOctet = Utility.oneByteToInteger(address[5]);
	}
	
	public String toString() {
		return Utility.prependZeroIfNeededForMacAddress(Integer.toHexString(firstOctet)) + ":" + Utility.prependZeroIfNeededForMacAddress(Integer.toHexString(secondOctet)) + ":" + Utility.prependZeroIfNeededForMacAddress(Integer.toHexString(thirdOctet)) + ":" + Utility.prependZeroIfNeededForMacAddress(Integer.toHexString(fourthOctet)) + ":" + Utility.prependZeroIfNeededForMacAddress(Integer.toHexString(fifthOctet)) + ":" + Utility.prependZeroIfNeededForMacAddress(Integer.toHexString(sixthOctet));
	}
	
	public byte[] getBytes() throws UtilityException {
		byte[] result = new byte[6];
		result[0] = Utility.integerToOneByte(firstOctet);
		result[1] = Utility.integerToOneByte(secondOctet);
		result[2] = Utility.integerToOneByte(thirdOctet);
		result[3] = Utility.integerToOneByte(fourthOctet);
		result[4] = Utility.integerToOneByte(fifthOctet);
		result[5] = Utility.integerToOneByte(sixthOctet);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MacAddress other = (MacAddress) obj;
		if (firstOctet != other.firstOctet) return false;
		if (secondOctet != other.secondOctet) return false;
		if (thirdOctet != other.thirdOctet) return false;
		if (fourthOctet != other.fourthOctet) return false;
		if (fifthOctet != other.fifthOctet) return false;		
		if (sixthOctet != other.sixthOctet) return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		return (firstOctet << 40) + (secondOctet << 32) + (thirdOctet << 24) + (fourthOctet << 16) + (fifthOctet << 8) + (sixthOctet); 
	}
}
