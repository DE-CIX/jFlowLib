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
package net.decix.jsflow.util;

import java.math.BigInteger;

import net.decix.util.Utility;
import junit.framework.TestCase;

public class UtilityTest extends TestCase {
	private byte macBytes[] = { 0, 28, -7, -83, 121, 50 };
	private long macLong = 0x001CF9AD7932l;
	
	public void setUp() {
	}
	
	public void testSixBytesToLong() throws Exception {
		assertEquals(macLong, Utility.sixBytesToLong(macBytes));
	}
	
	public void testLongToSixBytes() throws Exception {
		byte actual[] = Utility.longToSixBytes(macLong);
		for (int i = 0; i < Math.min(actual.length, macBytes.length); i++) {
			assertEquals(macBytes[i], actual[i]);
		}	
	}
	
	public void testEightBytesToBigInteger() throws Exception {
		byte[] val = new byte[8];
		val[0] = (byte) 0xff; 
		val[1] = (byte) 0xef;
		val[2] = (byte) 0xff; 
		val[3] = (byte) 0xef;
		val[4] = (byte) 0xff; 
		val[5] = (byte) 0xef;
		val[6] = (byte) 0xff; 
		val[7] = (byte) 0xef;
		BigInteger bInt = Utility.eightBytesToBigInteger(val);
		byte [] test = bInt.toByteArray();
		for (int i = 0; i < Math.min(val.length, test.length); i++) {
			assertEquals(test[i + 1], val[i]);
		}
	}
	
	public void testBigIntegerToEightBytes() throws Exception {
		byte[] val = new byte[8];
		val[0] = (byte) 0xaf; 
		val[1] = (byte) 0xbf;
		val[2] = (byte) 0xcf; 
		val[3] = (byte) 0xdf;
		val[4] = (byte) 0xef; 
		val[5] = (byte) 0xff;
		val[6] = (byte) 0x1f; 
		val[7] = (byte) 0x2f;
		BigInteger bInt = new BigInteger(1, val);
		byte[] test = Utility.BigIntegerToEightBytes(bInt);
		for (int i = 0; i < Math.min(test.length, bInt.bitLength()); i++) {
			assertEquals(test[i], val[i]);
		}
	}
}
