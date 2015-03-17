package net.decix.alu;

public class InnerAndOuterVlanIDs {
	private int mInnerVlanId = 0;
	private int mOuterVlanId = 0;
	
	public InnerAndOuterVlanIDs() {
	}
	
	public int getInnerVlanId() {
		return mInnerVlanId;
	}
	
	public void setInnerVlanId(int innerVlanId) {
		this.mInnerVlanId = innerVlanId;
	}
	
	public int getOuterVlanId() {
		return mOuterVlanId;
	}
	
	public void setOuterVlanId(int outerVlanId) {
		this.mOuterVlanId = outerVlanId;
	}
	
	public String toString() {
		return new String("Inner Vlan ID: " + mInnerVlanId + ", Outer Vlan ID: " + mOuterVlanId);
	}
	
	public static InnerAndOuterVlanIDs parse(int interfaceIndex) {
		InnerAndOuterVlanIDs innerAndOuter = new InnerAndOuterVlanIDs();
		
		int innerVlanId = (interfaceIndex & 0x0000FFFF);
		innerAndOuter.setInnerVlanId(innerVlanId);
		
		int outerVlanId = (interfaceIndex & 0xFFFF0000);
		outerVlanId = (outerVlanId >> 16);
		innerAndOuter.setOuterVlanId(outerVlanId);
		
		return innerAndOuter;
	}
	
	public int toInterfaceIndex() {
		int interfaceIndex = mOuterVlanId << 16;
		interfaceIndex = interfaceIndex | mInnerVlanId;
		return interfaceIndex;
	}
}
