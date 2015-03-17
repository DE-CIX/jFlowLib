package net.decix.alu;

import junit.framework.TestCase;

public class InnerAndOuterVlanIDsTest extends TestCase {
	public void testParse() {
		InnerAndOuterVlanIDs innerAndOuter = InnerAndOuterVlanIDs.parse(262144108);
		assertEquals(108, innerAndOuter.getInnerVlanId());
		assertEquals(4000, innerAndOuter.getOuterVlanId());
	}
	
	public void testToInterfaceIndex() {
		InnerAndOuterVlanIDs innerAndOuter = InnerAndOuterVlanIDs.parse(262144108);
		assertEquals(262144108, innerAndOuter.toInterfaceIndex());
	}
}
