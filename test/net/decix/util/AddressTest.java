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

import net.decix.util.Address;
import net.decix.util.MacAddress;
import net.decix.util.UtilityException;
import junit.framework.TestCase;

public class AddressTest extends TestCase {
	Address address;
	
	public AddressTest(String mesg) {
		super(mesg);
	}

	protected void setUp() throws Exception {
		address = new Address("192.168.100.1");
	}

	public void testAddressIntIntIntInt() {
		try {
			Address comp = new Address(192,168,100,1);
			assertTrue(address.equals(comp));
		} catch (UtilityException ue) {
			ue.printStackTrace();
		}
	}

	public void testAddressString() {
		try {
			MacAddress comp = new MacAddress("192.168.100.1");
			assertTrue(address.equals(comp));
		} catch (UtilityException ue) {
			ue.printStackTrace();
		}
	}

	public void testAddressByteArray() {
		try {
			byte[] data = {(byte)192, (byte)168, (byte)100, (byte)1};
			MacAddress comp = new MacAddress(data);
			assertTrue(address.equals(comp));
		} catch (UtilityException ue) {
			ue.printStackTrace();
		}
	}

	public void testToString() {
		try {
			MacAddress comp = new MacAddress("192.168.100.1");
			assertTrue(address.equals(comp));
		} catch (UtilityException ue) {
			ue.printStackTrace();
		}
	}

	public void testGetBytes() {
		try {
			byte[] data = address.getBytes();
			assertTrue(data[0] == (byte)192);
			assertTrue(data[1] == (byte)168);
			assertTrue(data[2] == (byte)100);
			assertTrue(data[3] == (byte)1);
		} catch (UtilityException ue) {
			ue.printStackTrace();
		}
		
	}

	public void testGetInetAddress() {
		try {
			Address comp = new Address("192.168.100.1");
			assertTrue(address.getInetAddress().equals(comp.getInetAddress()));
			comp = new Address("192.168.100.2");
			assertFalse(address.getInetAddress().equals(comp.getInetAddress()));
		} catch (UtilityException ue) {
			ue.printStackTrace();
		} catch (java.net.UnknownHostException uhe) {
			uhe.printStackTrace();
		}
	}

	public void testEqualsObject() {
		try {
			MacAddress comp = new MacAddress("192.168.100.1");
			assertTrue(address.equals(comp));
			comp = new MacAddress("192.168.100.2");
			assertFalse(address.equals(comp));
		} catch (UtilityException ue) {
			ue.printStackTrace();
		}
	}
}