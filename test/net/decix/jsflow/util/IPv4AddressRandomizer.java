package net.decix.jsflow.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPv4AddressRandomizer extends IPAddressRadomizer {

	
	@Override
	protected InetAddress ipAddressRandomizer() {
		// TODO Auto-generated method stub
		
		int first = ((int) (Math.random()*140))+11;
		if (first == 127) { 
			first = 100;
		}
		int second = ((int) (Math.random()*240))+1;
		int third = ((int) (Math.random()*240))+1;
		int fourth = ((int) (Math.random()*10))+200;
		
		
		Inet4Address fakeV4 = null;
		try {
			fakeV4 = (Inet4Address) Inet4Address.getByName(first + "." + second + "." + third + "." + fourth);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		return fakeV4;
		
	}
	


}
