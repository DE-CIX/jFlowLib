package net.decix.util;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;

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
public class Utility {
	public static final byte integerToOneByte(int value) throws UtilityException {
		if ((value > Math.pow(2, 15)) || (value < 0)) {
			throw new UtilityException("Integer value " + value + " is larger than 2^15");
		}
		return (byte) (value & 0xFF);
	}
	
	public static final byte shortToOneByte(short value) throws UtilityException {
		if ((value > Math.pow(2, 15)) || (value < 0)) {
			throw new UtilityException("Integer value " + value + " is larger than 2^15");
		}
		return (byte) (value & 0xFF);
	}

	public static final byte[] integerToTwoBytes(int value) throws UtilityException {
		byte[] result = new byte[2];
		if ((value > Math.pow(2, 31)) || (value < 0)) {
			throw new UtilityException("Integer value " + value + " is larger than 2^31");
		}
		result[0] = (byte)((value >>> 8) & 0xFF);
		result[1] = (byte)(value & 0xFF);
		return result; 
	}

	public static final byte[] longToFourBytes(long value) throws UtilityException {
		byte[] result = new byte[4];
		result[0] = (byte)((value >>> 24) & 0xFF);
		result[1] = (byte)((value >>> 16) & 0xFF);
		result[2] = (byte)((value >>> 8) & 0xFF);
		result[3] = (byte)(value & 0xFF);
		return result; 
	}

	public static final byte[] longToSixBytes(long value) throws UtilityException {
		byte[] result = new byte[6];
		result[0] = (byte)((value >>> 40) & 0xFF);
		result[1] = (byte)((value >>> 32) & 0xFF);
		result[2] = (byte)((value >>> 24) & 0xFF);
		result[3] = (byte)((value >>> 16) & 0xFF);
		result[4] = (byte)((value >>> 8) & 0xFF);
		result[5] = (byte)(value & 0xFF);
		return result; 
	}

	public static final byte[] BigIntegerToEightBytes(BigInteger value) throws UtilityException{
		byte[] result = new byte[8];
		byte[] tmp = value.toByteArray();
		if(tmp.length > 8){
			System.arraycopy(tmp, tmp.length-8, result, 0, 8);
		} else {
			System.arraycopy(tmp, 0, result, 8-tmp.length, tmp.length);
		}
		return result;
	}

	public static final int oneByteToInteger(byte value) throws UtilityException {
		return (int) value & 0xFF;
	}

	public static final short oneByteToShort(byte value) throws UtilityException {
		return (short) (value & 0xFF);
	}

	public static final int twoBytesToInteger(byte[] value) throws UtilityException {
		if (value.length < 2) {
			throw new UtilityException("Byte array too short!");
		}
		int temp0 = value[0] & 0xFF;
		int temp1 = value[1] & 0xFF;
		return ((temp0 << 8) + temp1);
	}

	public static final long fourBytesToLong(byte[] value) throws UtilityException {
		if (value.length < 4) {
			throw new UtilityException("Byte array too short!");
		}
		int temp0 = value[0] & 0xFF;
		int temp1 = value[1] & 0xFF;
		int temp2 = value[2] & 0xFF;
		int temp3 = value[3] & 0xFF;
		return (((long) temp0 << 24) + (temp1 << 16) + (temp2 << 8) + temp3);
	}

	public static final long sixBytesToLong(byte[] value) throws UtilityException {
		if (value.length < 6) {
			throw new UtilityException("Byte array too short!");
		}
		int temp0 = value[0] & 0xFF;
		int temp1 = value[1] & 0xFF;
		int temp2 = value[2] & 0xFF;
		int temp3 = value[3] & 0xFF;
		int temp4 = value[4] & 0xFF;
		int temp5 = value[5] & 0xFF;
		return ((((long) temp0) << 40) + (((long) temp1) << 32) + (((long) temp2) << 24) + (temp3 << 16) + (temp4 << 8) + temp5);
	}

	public static final BigInteger eightBytesToBigInteger(byte[] value) throws UtilityException {
		if(value.length < 8){
			throw new UtilityException("Byte array too short!");
		}

		BigInteger bInt = new BigInteger(1, value);
		return bInt;
	}


	public static final String dumpBytes(byte[] data) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (byte b : data) {
			i++;
			sb.append(String.valueOf(b));
			if (i < data.length) sb.append(", ");
			if ((i % 15) == 0) sb.append("\n");
		}
		return sb.toString();
	}

	public static String prependZeroIfNeededForMacAddress(String string) {
		if (string == null) return "00";
		if (string.length() == 0) return "00";
		if (string.length() == 1) return "0" + string;
		return string;
	}

	public static boolean isConifgured(Inet4Address inet4Address) {
		if (inet4Address == null) return false;
		try {
			if (inet4Address.equals(Inet4Address.getByName("0.0.0.0"))) return false;
		} catch (UnknownHostException e) {
			return false;
		}
		return true;
	}

	public static boolean isConifgured(Inet6Address inet6Address) {
		if (inet6Address == null) return false;
		try {
			if (inet6Address.equals(Inet6Address.getByName("0:0:0:0:0:0:0:0"))) return false;
		} catch (UnknownHostException e) {
			return false;
		}
		return true;
	}
}
