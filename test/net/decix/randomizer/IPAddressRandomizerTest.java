package net.decix.randomizer;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;

import net.decix.randomizer.IPv4AddressRandomizer;
import net.decix.randomizer.IPv6AddressRandomizer;
import net.decix.randomizer.SimpleIPv4AddressRandomizer;
import net.decix.randomizer.SimpleIPv6AddressRandomizer;
import junit.framework.TestCase;

public class IPAddressRandomizerTest extends TestCase {

	public void testIPv4() throws UnknownHostException {
		IPv4AddressRandomizer ipV4randomizer = new IPv4AddressRandomizer();
		
		Inet4Address realDestinationIpv4 = (Inet4Address) Inet4Address.getByName("172.20.111.21");
		
		Inet4Address fakeDestinationIpv4 = (Inet4Address) ipV4randomizer.randomize(realDestinationIpv4);	
		assertFalse(realDestinationIpv4.equals(fakeDestinationIpv4));
	}
	
	public void testSimpleIPv4() throws UnknownHostException {
		SimpleIPv4AddressRandomizer ipV4randomizer = new SimpleIPv4AddressRandomizer();
		
		Inet4Address realDestinationIpv4 = (Inet4Address) Inet4Address.getByName("172.20.111.21");	
		
		Inet4Address fakeDestinationIpv4 = (Inet4Address) ipV4randomizer.randomize(realDestinationIpv4);	
		assertEquals(Inet4Address.getByName("172.20.111.0"), fakeDestinationIpv4);
	}
	
	public void testIPv6() throws UnknownHostException {
		IPv6AddressRandomizer ipV6randomizer = new IPv6AddressRandomizer();
		
		Inet6Address realDestinationIpv6_1 = (Inet6Address) Inet6Address.getByName("2a02:26f0:64:0:0:0:170e:5c09");
		
		Inet6Address fakeDestinationIpv6_1 = (Inet6Address) ipV6randomizer.randomize(realDestinationIpv6_1);	
		assertFalse(realDestinationIpv6_1.equals(fakeDestinationIpv6_1));
	}
	
	public void testSimpleIPv6() throws UnknownHostException {
		SimpleIPv6AddressRandomizer ipV6randomizer = new SimpleIPv6AddressRandomizer();
		
		Inet6Address realDestinationIpv6 = (Inet6Address) Inet6Address.getByName("2a02:26f0:64:0:0:0:170e:5c09");	
		
		Inet6Address fakeDestinationIpv6 = (Inet6Address) ipV6randomizer.randomize(realDestinationIpv6);
		assertEquals(Inet6Address.getByName("2a02:26f0:64:0:0:0:170e:0000"), fakeDestinationIpv6);
	}
}
