package net.decix.jipfix.header;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.decix.util.HeaderBytesException;
import net.decix.util.HeaderParseException;
import net.decix.util.Utility;

/**
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |          Set ID               |          Length               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  
 * @author tking
 *
 */
public class SetHeader implements IPFIXEntity {
	private final static Logger LOGGER = Logger.getLogger(SetHeader.class.getName());

	private int setID;
	private int length;
	private List<DataRecord> dataRecords = new ArrayList<DataRecord>();
	private List<TemplateRecord> templateRecords = new ArrayList<TemplateRecord>();
	private List<OptionTemplateRecord> optionTemplateRecords = new ArrayList<OptionTemplateRecord>();
	
	/**
	 * A value of 2 is reserved for Template Sets. A value of 3 is reserved
	 * for Options Template Sets.  Values from 4 to 255 are reserved for
	 * future use.  Values 256 and above are used for Data Sets.  The Set ID
	 * values of 0 and 1 are not used, for historical reasons.
	 *  
	 * @return
	 */
	public int getSetID() {
		return setID;
	}
	
	public void setSetID(int setID) {
		this.setID = setID;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public List<DataRecord> getDataRecords() {
		return dataRecords;
	}
	
	public void setDataRecords(List<DataRecord> dataRecords) {
		this.dataRecords = dataRecords;
	}
	
	public List<TemplateRecord> getTemplateRecords() {
		return templateRecords;
	}
	
	public void setTemplateRecords(List<TemplateRecord> templateRecords) {
		this.templateRecords = templateRecords;
	}
	
	public List<OptionTemplateRecord> getOptionTemplateRecords() {
		return optionTemplateRecords;
	}
	
	public void setOptionTemplateRecords(List<OptionTemplateRecord> optionTemplateRecords) {
		this.optionTemplateRecords = optionTemplateRecords;
	}
	
	public static SetHeader parse(byte[] data) throws HeaderParseException {
		try {
			if (data.length < 4) throw new HeaderParseException("Data array too short.");
			SetHeader sh = new SetHeader();
			// set id
			byte[] setID = new byte[2];
			System.arraycopy(data, 0, setID, 0, 2);
			sh.setSetID(Utility.twoBytesToInteger(setID));
			// length
			byte[] length = new byte[2];
			System.arraycopy(data, 2, length, 0, 2);
			sh.setLength(Utility.twoBytesToInteger(length));
			// 2 -> template sets;
			if (sh.getSetID() == 2) {
				int offset = 4;
				byte[] subData = new byte[sh.getLength() - offset]; 
				System.arraycopy(data, offset, subData, 0, subData.length);
				TemplateRecord tr = TemplateRecord.parse(subData);
				sh.getTemplateRecords().add(tr);
			} 
			// 3 -> template option sets
			else if (sh.getSetID() == 3) {
				int offset = 4;
				byte[] subData = new byte[sh.getLength() - offset];
				System.arraycopy(data, offset, subData, 0, subData.length);
				OptionTemplateRecord otr = OptionTemplateRecord.parse(subData);
				sh.getOptionTemplateRecords().add(otr);
			}
			// > 256 -> data record;
			else if (sh.getSetID() == 256) {
				int offset = 4;
				byte[] subData = new byte[sh.getLength() - offset]; 
				System.arraycopy(data, offset, subData, 0, SamplingDataRecord.LENGTH);
				SamplingDataRecord sdr = SamplingDataRecord.parse(subData); 
				sh.getDataRecords().add(sdr);
			}
			else if (sh.getSetID() == 306) {
				int offset = 4;
				while ((sh.getLength() - offset - L2IPDataRecord.LENGTH) >= 0) { 
					byte[] subData = new byte[sh.getLength() - offset]; 
					System.arraycopy(data, offset, subData, 0, L2IPDataRecord.LENGTH);
					L2IPDataRecord lidr = L2IPDataRecord.parse(subData); 
					sh.getDataRecords().add(lidr);
					offset += L2IPDataRecord.LENGTH;
				}
				if ((sh.getLength() - offset) != 0) LOGGER.log(Level.INFO, "Unused bytes: " + (sh.getLength() - offset));
			} else {
				LOGGER.log(Level.INFO, "Set ID " + sh.getSetID() + " is unknown and not handled");
			}
			return sh;			
		} catch (Exception e) {
			throw new HeaderParseException("Parse error: " + e.getMessage());
		}
	}
	
	public byte[] getBytes() throws HeaderBytesException {
		try {
			int length = 4;
			
			for (DataRecord dr : dataRecords) {
				if (dr instanceof L2IPDataRecord) length += L2IPDataRecord.LENGTH;
				if (dr instanceof SamplingDataRecord) length += SamplingDataRecord.LENGTH;
			}
			
			for (TemplateRecord tr : templateRecords) {
				length += tr.getBytes().length;
			}
			
			for (OptionTemplateRecord otr : optionTemplateRecords) {
				length += otr.getBytes().length;
			}
			
			byte[] data = new byte[length];
			// set id
			System.arraycopy(Utility.integerToTwoBytes(getSetID()), 0, data, 0, 2);
			// length
			System.arraycopy(Utility.integerToTwoBytes(getLength()), 0, data, 2, 2);
			// data record
			int offset = 4;
			
			for (DataRecord record : dataRecords) {
				if (record instanceof L2IPDataRecord) {
					System.arraycopy(record.getBytes(), 0, data, offset, L2IPDataRecord.LENGTH);
					offset += L2IPDataRecord.LENGTH;
				} else if (record instanceof SamplingDataRecord) {
					System.arraycopy(record.getBytes(), 0, data, offset, SamplingDataRecord.LENGTH);
					offset += SamplingDataRecord.LENGTH;
				}
			}
			
			for (TemplateRecord record : templateRecords) {
				byte[] recordData = record.getBytes();
				System.arraycopy(recordData, 0, data, offset, recordData.length);
				offset += recordData.length;
			}
			
			for (OptionTemplateRecord record : optionTemplateRecords) {
				byte[] recordData = record.getBytes();
				System.arraycopy(recordData, 0, data, offset, recordData.length);
				offset += recordData.length;
			}
			
			return data;
		} catch (Exception e) {
			throw new HeaderBytesException("Error while generating the bytes: " + e.getMessage());
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[SetHeader]: ");
		sb.append("Set ID: ");
		sb.append(setID);
		sb.append(", Length: ");
		sb.append(length);
		sb.append(", Data records: ");
		sb.append(dataRecords.size());
		sb.append(", ");
		for (DataRecord record : dataRecords) {
			sb.append(record);
			sb.append(", ");
		}
		sb.append("Template records: ");
		sb.append(templateRecords.size());
		sb.append(", ");
		for (TemplateRecord record : templateRecords) {
			sb.append(record);
			sb.append(", ");
		}
		sb.append("Option template records: ");
		sb.append(optionTemplateRecords.size());
		sb.append(", ");
		for (OptionTemplateRecord record : optionTemplateRecords) {
			sb.append(record);
			sb.append(", ");
		}
		return sb.toString();
	}
}
