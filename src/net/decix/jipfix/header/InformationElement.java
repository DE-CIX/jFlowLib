package net.decix.jipfix.header;

import net.decix.util.HeaderBytesException;
import net.decix.util.HeaderParseException;
import net.decix.util.Utility;

public class InformationElement implements IPFIXEntity {
	protected static final int LENGTH = 4;
	
	private int informationElementID;
	private int fieldLength;

	public int getInformationElementID() {
		return informationElementID;
	}

	public void setInformationElementID(int informationElementID) {
		this.informationElementID = informationElementID;
	}

	public int getFieldLength() {
		return fieldLength;
	}

	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}

	public static InformationElement parse(byte[] data) throws HeaderParseException {
		try {
			if (data.length < LENGTH) throw new HeaderParseException("Data array too short.");
			InformationElement ie = new InformationElement();
			// information element ID
			byte[] informationElementID = new byte[2];
			System.arraycopy(data, 0, informationElementID, 0, 2);
			ie.setInformationElementID(Utility.twoBytesToInteger(informationElementID));
			// field length
			byte[] fieldLength = new byte[2];
			System.arraycopy(data, 2, fieldLength, 0, 2);
			ie.setFieldLength(Utility.twoBytesToInteger(fieldLength));
			return ie;
		} catch (Exception e) {
			throw new HeaderParseException("Parse error: " + e.getMessage());
		}
	}
	
	
	public byte[] getBytes() throws HeaderBytesException {
		try {
			byte[] data = new byte[LENGTH];
			// information element ID
			System.arraycopy(Utility.integerToTwoBytes(getInformationElementID()), 0, data, 0, 2);
			// field length
			System.arraycopy(Utility.integerToTwoBytes(getFieldLength()), 0, data, 2, 2);
			return data;
		} catch (Exception e) {
			throw new HeaderBytesException("Error while generating the bytes: " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[InformationElement]: ");
		sb.append("ID: ");
		sb.append(informationElementID);
		sb.append(", ");
		sb.append("Field length: ");
		sb.append(fieldLength);
		
		return sb.toString();
	}
}
