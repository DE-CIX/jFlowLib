package net.decix.jipfix.randomizer;


import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPv6AddressRandomizer extends IPAddressRadomizer {

	public InetAddress ipAddressRandomizer() {
		int block1 = ((int) (Math.random() * 60000));
		int block2 = ((int) (Math.random() * 60000));
		int block3 = ((int) (Math.random() * 60000));
		int block4 = ((int) (Math.random() * 60000));
		int block5 = ((int) (Math.random() * 60000));
		int block6 = ((int) (Math.random() * 60000));
		int block7 = ((int) (Math.random() * 60000));
		int block8 = ((int) (Math.random() * 60000));
		
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
