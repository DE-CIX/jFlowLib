package net.decix.jsflow.header;

import net.decix.util.HeaderBytesException;
import net.decix.util.HeaderParseException;
import net.decix.util.Utility;

public class TaggedMacHeader extends MacHeader {
	// vlan tag
	private int tpid;
	private int tci;
	
	public int getTCI() {
		return tci;
	}
	
	public int getTpid() {
		return tpid;
	}

	public void setTpid(int tpid) {
		this.tpid = tpid;
	}
	
	public void setTCI(int tci) {
		this.tci = tci;
	}
	
	public static TaggedMacHeader parse(byte data[]) throws HeaderParseException {
		try {
			if (data.length < 18) throw new HeaderParseException("Data array too short.");	
			TaggedMacHeader m = new TaggedMacHeader();
			// destination
			byte destination[] = new byte[6];
			System.arraycopy(data, 0, destination, 0, 6);
			m.setDestination(Utility.sixBytesToLong(destination));
			// source
			byte source[] = new byte[6];
			System.arraycopy(data, 6, source, 0, 6);
			m.setSource(Utility.sixBytesToLong(source));
			// vlan tag
			byte tpid[] = { (byte) 0x81, (byte) 0x00 };
			m.tpid = Utility.twoBytesToInteger(tpid);
			
			byte tci[] = new byte[2];
			System.arraycopy(data, 14, tci, 0, 2);
			m.setTCI(Utility.twoBytesToInteger(tci));
			// type
			byte type[] = new byte[2];
			System.arraycopy(data, 16, type, 0, 2);
			m.setType(Utility.twoBytesToInteger(type));
			// offcut
			byte offcut[] = new byte[data.length - 18];
			System.arraycopy(data, 18, offcut, 0, data.length - 18);
			m.setOffCut(offcut);
			return m;
		} catch (Exception e) {
			throw new HeaderParseException("Parse error: " + e.getMessage());
		}		
	}
	
	public byte[] getBytes() throws HeaderBytesException {
		try {
			byte[] data = new byte[18 + offcut.length];
			// destination
			System.arraycopy(Utility.longToSixBytes(destination), 0, data, 0, 6);
			// source
			System.arraycopy(Utility.longToSixBytes(source), 0, data, 6, 6);
			// vlan tag
			System.arraycopy(Utility.integerToTwoBytes(tpid), 0, data, 12, 2);
			System.arraycopy(Utility.integerToTwoBytes(tci), 0, data, 14, 2);
			// type
			System.arraycopy(Utility.integerToTwoBytes(type), 0, data, 16, 2);
			// offcut
			System.arraycopy(offcut, 0, data, 18, offcut.length);
			
			return data;
		} catch (Exception e) {
			throw new HeaderBytesException("Error while generating the bytes: " + e.getMessage());
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[TaggedMacHeader]: ");
		sb.append(super.toString());
		sb.append(", TPID(VLAN): ");
		sb.append(getTpid());
		sb.append(", TCI(VLAN): ");
		sb.append(getTCI());
		
		return sb.toString();
	}
}
