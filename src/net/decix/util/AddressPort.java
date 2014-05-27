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

public class AddressPort {
	private Address address;
	private int port;
	
	public AddressPort(Address address, int port) {
		this.address = address;
		this.port = port;
	}

	public Address getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
}
