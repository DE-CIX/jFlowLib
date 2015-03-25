package net.decix.randomizer;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class IPv6AddressRandomizer extends IPAddressRadomizer {

	public Inet6Address ipAddressRandomizer(InetAddress realAddress) {
		Random rand = new Random();
		int block1 = rand.nextInt(65535);
		int block2 = rand.nextInt(65535);
		int block3 = rand.nextInt(65535);
		int block4 = rand.nextInt(65535);
		int block5 = rand.nextInt(65535);
		int block6 = rand.nextInt(65535);
		int block7 = rand.nextInt(65535);
		int block8 = rand.nextInt(65535);
		
		Inet6Address fakeV6 = null;
		try {
			fakeV6 = (Inet6Address) Inet6Address.getByName(Integer.toHexString(block1) + ":" + 
															Integer.toHexString(block2) + ":" +
															Integer.toHexString(block3) + ":" +
															Integer.toHexString(block4) + ":" +
															Integer.toHexString(block5) + ":" +
															Integer.toHexString(block6) + ":" +
															Integer.toHexString(block7) + ":" +
															Integer.toHexString(block8));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return fakeV6;
	}
}
