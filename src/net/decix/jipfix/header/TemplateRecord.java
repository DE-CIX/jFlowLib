package net.decix.jipfix.header;

import java.util.ArrayList;
import java.util.List;

import net.decix.util.HeaderBytesException;
import net.decix.util.HeaderParseException;
import net.decix.util.Utility;

/**
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      Template ID (> 255)      |         Field Count           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  
 * @author tking
 */
public class TemplateRecord extends Record {
	private final static int HEADERLENGTH = 4;
	private int templateID;
	private int fieldCount;
	private List<InformationElement> informationElements = new ArrayList<InformationElement>();

	public int getTemplateID() {
		return templateID;
	}
	
	public void setTemplateID(int templateID) {
		this.templateID = templateID;
	}
	
	public int getFieldCount() {
		return fieldCount;
	}
	
	public void setFieldCount(int fieldCount) {
		this.fieldCount = fieldCount;
	}
	
	public List<InformationElement> getInformationElements() {
		return informationElements;
	}
	
	public void setInformationElements(List<InformationElement> informationElements) {
		this.informationElements = informationElements;
	}
	
	public static TemplateRecord parse(byte[] data) throws HeaderParseException {
		try {
			if (data.length < 4) throw new HeaderParseException("Data array too short.");
			TemplateRecord tr = new TemplateRecord();
			// template ID
			byte[] templateID = new byte[2];
			System.arraycopy(data, 0, templateID, 0, 2);
			tr.setTemplateID(Utility.twoBytesToInteger(templateID));
			// field count
			byte[] fieldCount = new byte[2];
			System.arraycopy(data, 2, fieldCount, 0, 2);
			tr.setFieldCount(Utility.twoBytesToInteger(fieldCount));
			int offset = 4;
			for (int i = 0; i < tr.getFieldCount(); i++) {
				byte[] subData = new byte[InformationElement.LENGTH];
				System.arraycopy(data, offset + (i * InformationElement.LENGTH), subData, 0, InformationElement.LENGTH);
				InformationElement ie = InformationElement.parse(subData);
				tr.getInformationElements().add(ie);
			}
			tr.length = data.length;
			return tr;			
		} catch (Exception e) {
			throw new HeaderParseException("Parse error: " + e.getMessage());
		}
	}
	
	public byte[] getBytes() throws HeaderBytesException {
		try {
			int length = 4 + (informationElements.size() * InformationElement.LENGTH);
			byte[] data = new byte[length];
			// template ID
			System.arraycopy(Utility.integerToTwoBytes(getTemplateID()), 0, data, 0, 2);
			// field count
			System.arraycopy(Utility.integerToTwoBytes(getFieldCount()), 0, data, 2, 2);
			// information elements
			int offset = 4;
			for (InformationElement ie : informationElements) {
				System.arraycopy(ie.getBytes(), 0, data, offset, InformationElement.LENGTH);
				offset += InformationElement.LENGTH;
			}
			return data;
		} catch (Exception e) {
			throw new HeaderBytesException("Error while generating the bytes: " + e.getMessage());
		}
	}

	@Override
        public int getLength() {
        return HEADERLENGTH + (informationElements.size() * InformationElement.LENGTH);
        }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[TemplateRecord]: ");
		sb.append("Template ID: ");
		sb.append(templateID);
		sb.append(", Field count: ");
		sb.append(fieldCount);
		sb.append(", Information elements: ");	
		sb.append(informationElements.size());
		sb.append(", ");
		for (InformationElement ie : informationElements) {
			sb.append(ie);
			sb.append(", ");
		}
		
		return sb.toString();
	}
}
