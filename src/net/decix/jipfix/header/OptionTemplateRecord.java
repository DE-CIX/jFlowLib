package net.decix.jipfix.header;

import java.util.ArrayList;
import java.util.List;

import net.decix.util.HeaderBytesException;
import net.decix.util.HeaderParseException;
import net.decix.util.Utility;

/**
 *   0                   1                   2                   3
 *   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  |         Template ID (> 255)   |         Field Count           |
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  |      Scope Field Count        |
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * @author tking
 *
 */
public class OptionTemplateRecord extends Record {
	private int templateID;
	private int fieldCount;
	private int scopeFieldCount;
	private List<InformationElement> informationElements = new ArrayList<InformationElement>();
	private List<InformationElement> scopeInformationElements = new ArrayList<InformationElement>();
	
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
	
	public int getScopeFieldCount() {
		return scopeFieldCount;
	}
	
	public void setScopeFieldCount(int scopeFieldCount) {
		this.scopeFieldCount = scopeFieldCount;
	}
	
	public List<InformationElement> getInformationElements() {
		return informationElements;
	}
	
	public void setInformationElements(List<InformationElement> informationElements) {
		this.informationElements = informationElements;
	}
	
	public List<InformationElement> getScopeInformationElements() {
		return scopeInformationElements;
	}
	
	public void setScopeInformationElements(List<InformationElement> scopeInformationElements) {
		this.scopeInformationElements = scopeInformationElements;
	}
	
	public static OptionTemplateRecord parse(byte[] data) throws HeaderParseException {
		try {
			if (data.length < 6) throw new HeaderParseException("Data array too short.");
			OptionTemplateRecord otr = new OptionTemplateRecord();
			// template ID
			byte[] templateID = new byte[2];
			System.arraycopy(data, 0, templateID, 0, 2);
			otr.setTemplateID(Utility.twoBytesToInteger(templateID));
			// field count
			byte[] fieldCount = new byte[2];
			System.arraycopy(data, 2, fieldCount, 0, 2);
			otr.setFieldCount(Utility.twoBytesToInteger(fieldCount));
			// scope field count
			byte[] scopeFieldCount = new byte[2];
			System.arraycopy(data, 4, scopeFieldCount, 0, 2);
			otr.setScopeFieldCount(Utility.twoBytesToInteger(scopeFieldCount));
			int offset = 6;
			for (int i = 0; i < otr.getFieldCount(); i++) {
				byte[] subData = new byte[InformationElement.LENGTH];
				System.arraycopy(data, offset + (i * InformationElement.LENGTH), subData, 0, InformationElement.LENGTH);
				InformationElement ie = InformationElement.parse(subData);
				if (i < otr.getScopeFieldCount()) {
					otr.getScopeInformationElements().add(ie);
				} else {
					otr.getInformationElements().add(ie);
				}
			}
			return otr;			
		} catch (Exception e) {
			throw new HeaderParseException("Parse error: " + e.getMessage());
		}
	}
	
	public byte[] getBytes() throws HeaderBytesException {
		try {
			int length = 6 + (scopeInformationElements.size() * InformationElement.LENGTH) + (informationElements.size() * InformationElement.LENGTH);
			if (length % 4 != 0) length += (length % 4); // padding
			byte[] data = new byte[length];
			// template ID
			System.arraycopy(Utility.integerToTwoBytes(getTemplateID()), 0, data, 0, 2);
			// field count
			System.arraycopy(Utility.integerToTwoBytes(getFieldCount()), 0, data, 2, 2);
			// scope field count
			System.arraycopy(Utility.integerToTwoBytes(getScopeFieldCount()), 0, data, 4, 2);
			// information elements
			int offset = 6;
			for (InformationElement ie : scopeInformationElements) {
				System.arraycopy(ie.getBytes(), 0, data, offset, InformationElement.LENGTH);
				offset += InformationElement.LENGTH;
			}
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
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[OptionTemplateRecord]: ");
		sb.append("Template ID: ");
		sb.append(templateID);
		sb.append(", Field count: ");
		sb.append(fieldCount);
		sb.append(", Scope field count: ");
		sb.append(scopeFieldCount);
		sb.append(", Information elements: ");
		sb.append(informationElements);
		sb.append(", Scope Information elements: ");
		sb.append(scopeInformationElements);
		
		return sb.toString();
	}
}
