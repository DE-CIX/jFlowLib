package net.decix.alu;

import junit.framework.TestCase;

public class PhysicalPortOrLagTest extends TestCase {
	
	public void testParse() {
		// Physical port (non-XRS)
		net.decix.alu.PhysicalPortOrLag ppol = net.decix.alu.PhysicalPortOrLag.parse(138739712);
		assertFalse(ppol.isXRS());
		assertTrue(ppol.isPhysicalPort());
		assertFalse(ppol.isLag());
		assertEquals(0, ppol.getLagId());
		assertEquals(4, ppol.getSlot());
		assertEquals(2, ppol.getMda());
		assertEquals(10, ppol.getPort());
		
		// Physical port (XRS)
		ppol = net.decix.alu.PhysicalPortOrLag.parse(1611170072);
		assertTrue(ppol.isXRS());
		assertTrue(ppol.isPhysicalPort());
		assertFalse(ppol.isLag());
		assertEquals(0, ppol.getLagId());
		assertEquals(2, ppol.getSlot());
		assertEquals(2, ppol.getMda());
		assertEquals(35, ppol.getPort());
		
		// Lag
		ppol = net.decix.alu.PhysicalPortOrLag.parse(1342177336);
		assertTrue(ppol.isXRS());
		assertFalse(ppol.isPhysicalPort());
		assertTrue(ppol.isLag());
		assertEquals(56, ppol.getLagId());
		assertEquals(0, ppol.getSlot());
		assertEquals(0, ppol.getMda());
		assertEquals(0, ppol.getPort());
	}
	
	public void testToInterfaceIndex() {
		// Physical port (XRS)
		net.decix.alu.PhysicalPortOrLag ppol = net.decix.alu.PhysicalPortOrLag.parse(1611170072);
		assertEquals(1611170072, ppol.toInterfaceIndex());
		
		// Physical port (non-XRS)
		ppol = net.decix.alu.PhysicalPortOrLag.parse(138739712);
		assertEquals(138739712, ppol.toInterfaceIndex());
		
		// Lag
		ppol = net.decix.alu.PhysicalPortOrLag.parse(1342177336);
		assertEquals(1342177336, ppol.toInterfaceIndex());
	}
}
