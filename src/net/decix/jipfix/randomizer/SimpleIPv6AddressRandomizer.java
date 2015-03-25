package net.decix.jipfix.randomizer;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SimpleIPv6AddressRandomizer extends IPAddressRadomizer {
	
	@Override
	protected Inet6Address ipAddressRandomizer(InetAddress realAddress) {
		byte[] addr = { realAddress.getAddress()[0], realAddress.getAddress()[1], realAddress.getAddress()[2], realAddress.getAddress()[3], 
						realAddress.getAddress()[4], realAddress.getAddress()[5], realAddress.getAddress()[6], realAddress.getAddress()[7],
						realAddress.getAddress()[8], realAddress.getAddress()[9], realAddress.getAddress()[10], realAddress.getAddress()[11],
						realAddress.getAddress()[12], realAddress.getAddress()[13], (byte) 0, (byte) 0};
		
		Inet6Address fakeV4 = null;
		try {
			fakeV4 = (Inet6Address) Inet6Address.getByAddress(addr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}	

		return fakeV4;
	}
}