package net.decix.randomizer;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SimpleIPv4AddressRandomizer extends IPAddressRadomizer {
	
	@Override
	protected Inet4Address ipAddressRandomizer(InetAddress realAddress) {
		byte[] addr = { realAddress.getAddress()[0], realAddress.getAddress()[1], realAddress.getAddress()[2], (byte) 0};
		
		Inet4Address fakeV4 = null;
		try {
			fakeV4 = (Inet4Address) Inet4Address.getByAddress(addr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}	

		return fakeV4;
	}
}