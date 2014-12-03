package net.decix.jipfix;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;

import net.decix.jsflow.util.IPv4AddressRandomizer;
import net.decix.jsflow.util.IPv6AddressRandomizer;

public class IPRandomizerTester {

	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub
		IPv4AddressRandomizer ipV4randomizer = new IPv4AddressRandomizer();
		IPv6AddressRandomizer ipV6randomizer = new IPv6AddressRandomizer();
		
		Inet4Address realDestinationIpv4_1 =  (Inet4Address) Inet4Address.getByName("172.20.111.21");	
		System.out.println("real: " + realDestinationIpv4_1); 
		Inet4Address realDestinationIpv4_2 =  (Inet4Address) Inet4Address.getByName("192.168.110.33");	
		System.out.println("real: " + realDestinationIpv4_2); 
		
		Inet4Address fakelDestinationIpv4_1 = (Inet4Address) ipV4randomizer.randomize(realDestinationIpv4_1);	
		System.out.println("fake: " + fakelDestinationIpv4_1); 
		
		Inet4Address fakelDestinationIpv4_2 = (Inet4Address) ipV4randomizer.randomize(realDestinationIpv4_2);	
		System.out.println("fake: " + fakelDestinationIpv4_2);
		
		Inet4Address fakelDestinationIpv4_3 = (Inet4Address) ipV4randomizer.randomize(realDestinationIpv4_1);	
		System.out.println("fake: " + fakelDestinationIpv4_3);
		
		System.out.println("----------"); 
		
		Inet6Address realDestinationIpv6_1 =  (Inet6Address) Inet6Address.getByName("2a02:26f0:64:0:0:0:170e:5c09");	
		System.out.println("real6: " + realDestinationIpv6_1); 
		
		Inet6Address fakelDestinationIpv6_1 = (Inet6Address) ipV6randomizer.randomize(realDestinationIpv6_1);	
		System.out.println("fake6: " + fakelDestinationIpv6_1); 
		
		Inet6Address fakelDestinationIpv6_2 = (Inet6Address) ipV6randomizer.randomize(realDestinationIpv6_1);	
		System.out.println("fake6: " + fakelDestinationIpv6_2); 

		
		
	}

}
