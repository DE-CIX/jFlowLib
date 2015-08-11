package net.decix.jipfix.header;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.decix.util.HeaderBytesException;
import net.decix.util.HeaderParseException;
import net.decix.util.Utility;

/**
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  |       Version Number          |            Length             |   
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  |                           Export Time                         |
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  |                       Sequence Number                         |
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  |                    Observation Domain ID                      |
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  
 * @author tking
 *
 */
public class MessageHeader implements IPFIXEntity {
	private final static Logger LOGGER = Logger.getLogger(SetHeader.class.getName());

	private final static int HEADERLENGTH = 16;
	
	private int versionNumber;
	private int length;
	private Date exportTime;
	private long sequenceNumber;
	private long observationDomainID;
	private List<SetHeader> setHeaders = new ArrayList<SetHeader>();
	
	public int getVersionNumber() {
		return versionNumber;
	}
	
	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public Date getExportTime() {
		return exportTime;
	}
	
	public void setExportTime(Date exportTime) {
		this.exportTime = exportTime;
	}
	
	public long getSequenceNumber() {
		return sequenceNumber;
	}
	
	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public long getObservationDomainID() {
		return observationDomainID;
	}
	
	public void setObservationDomainID(long observationDomainID) {
		this.observationDomainID = observationDomainID;
	}
	
	public List<SetHeader> getSetHeaders() {
		return setHeaders;
	}
	
	public void setSetHeaders(List<SetHeader> setHeaders) {
		this.setHeaders = setHeaders;
		int length = 0;

		for (SetHeader header : setHeaders) {
			length += header.getLength();
		}
		setLength(length + HEADERLENGTH);
	}
	
	public static MessageHeader parse(byte[] data) throws HeaderParseException {
		try {
			if (data.length < 16) throw new HeaderParseException("Data array too short.");
			MessageHeader mh = new MessageHeader();
			// version number
			byte[] versionNumber = new byte[2];
			System.arraycopy(data, 0, versionNumber, 0, 2);
			mh.setVersionNumber(Utility.twoBytesToInteger(versionNumber));
			// length
			byte[] length = new byte[2];
			System.arraycopy(data, 2, length, 0, 2);
			mh.setLength(Utility.twoBytesToInteger(length));
			// export time
			byte[] exportTime = new byte[4];
			System.arraycopy(data, 4, exportTime, 0, 4);
			long secondsSinceEpoche = Utility.fourBytesToLong(exportTime);
			long milliSecondsSinceEpoche = secondsSinceEpoche * 1000;
			mh.setExportTime(new Date(milliSecondsSinceEpoche));
			// sequence number
			byte[] sequenceNumber = new byte[4];
			System.arraycopy(data, 8, sequenceNumber, 0, 4);
			mh.setSequenceNumber(Utility.fourBytesToLong(sequenceNumber));
			// observation domain id
			byte[] observationDomainID = new byte[4];
			System.arraycopy(data, 12, observationDomainID, 0, 4);
			mh.setObservationDomainID(Utility.fourBytesToLong(observationDomainID));
			// set header
			int offset = 16;
			
			while ((mh.getLength() - offset) > 0) { 
				byte[] subData = new byte[mh.getLength() - offset]; 
				System.arraycopy(data, offset, subData, 0, subData.length);
				SetHeader sh = SetHeader.parse(subData);
				mh.getSetHeaders().add(sh);
				offset += sh.getLength();
			}
			if ((mh.getLength() - offset) != 0) LOGGER.log(Level.INFO, "Unused bytes: " + (mh.getLength() - offset));
			return mh;
		} catch (Exception e) {
			throw new HeaderParseException("Parse error: " + e.getMessage());
		}
	}
	
	public byte[] getBytes() throws HeaderBytesException {
		try {
			byte[] data = new byte[length];
			// version number
			System.arraycopy(Utility.integerToTwoBytes(versionNumber), 0, data, 0, 2);
			// length
			System.arraycopy(Utility.integerToTwoBytes(length), 0, data, 2, 2);
			// export time
			System.arraycopy(Utility.longToFourBytes(exportTime.getTime() / 1000), 0, data, 4, 4);
			// sequence number
			System.arraycopy(Utility.longToFourBytes(sequenceNumber), 0, data, 8, 4);
			// observation domain id
			System.arraycopy(Utility.longToFourBytes(observationDomainID), 0, data, 12, 4);
			// set header
			int offset = 16;
			for (SetHeader sh : setHeaders) { 
				byte[] temp = sh.getBytes();
				System.arraycopy(temp, 0, data, offset, temp.length);
				offset += temp.length;
			}

			return data;
		} catch (Exception e) {
			e.printStackTrace();
			throw new HeaderBytesException("Error while generating the bytes: " + e.getMessage());
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[MessageHeader]: ");
		sb.append("Version Number: ");
		sb.append(versionNumber);
		sb.append(", ");
		sb.append("Length: ");
		sb.append(length);
		sb.append(", ");
		sb.append("Export time: ");
		sb.append(exportTime);
		sb.append(", ");
		sb.append("Sequence number: ");
		sb.append(sequenceNumber);
		sb.append(", ");
		sb.append("Observation Domain ID: ");
		sb.append(observationDomainID);
		sb.append(", ");
		sb.append("SetHeaders: ");
		sb.append(setHeaders.size());
		sb.append(", ");
		for (SetHeader sh : setHeaders) {
			sb.append(sh);
			sb.append(", ");
		}
		return sb.toString();
	}
}
