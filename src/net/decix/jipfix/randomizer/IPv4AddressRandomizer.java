package net.decix.jipfix.randomizer;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class IPv4AddressRandomizer extends IPAddressRadomizer {
	
	@Override
	protected Inet4Address ipAddressRandomizer(InetAddress realAddress) {
		Random rand = new Random();
		int first = rand.nextInt(256);
		int second = rand.nextInt(256);
		int third = rand.nextInt(256);
		int fourth = rand.nextInt(256);
		
		Inet4Address fakeV4 = null;
		try {
			fakeV4 = (Inet4Address) Inet4Address.getByName(first + "." + second + "." + third + "." + fourth);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}	

		return fakeV4;
	}
}