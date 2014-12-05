package net.decix.jipfix.detector;

import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.decix.jipfix.header.DataRecord;
import net.decix.jipfix.header.L2IPDataRecord;
import net.decix.jipfix.header.MessageHeader;
import net.decix.jipfix.header.SetHeader;

public class MissingDataRecordDetector {
	private final static Logger LOGGER = Logger.getLogger(MissingDataRecordDetector.class.getName());
	
	private HashMap<IPObservationDomain, Long> sequenceNumbers = new HashMap<IPObservationDomain, Long>(5000);
	
	public synchronized boolean detectMissing(DatagramPacket dp, MessageHeader mh) {
		// handle sequence number check
		boolean detectedMissing = false;
		IPObservationDomain currentIPObservationDomain = new IPObservationDomain(dp.getAddress(), mh.getObservationDomainID());
		long expectedSequenceNumber = -1l;
		if (sequenceNumbers.containsKey(currentIPObservationDomain)) {
			expectedSequenceNumber = sequenceNumbers.get(currentIPObservationDomain);
		}
		if (expectedSequenceNumber != -1l) {
			long currentSequenceNumber = mh.getSequenceNumber();
			if (currentSequenceNumber != expectedSequenceNumber) {
				LOGGER.log(Level.INFO, "Missing data record: " + currentIPObservationDomain + ": received sequence number " + currentSequenceNumber + " but expected " + expectedSequenceNumber);
				detectedMissing = true;
			}
		}
		
		long numberDataRecords = 0;
		for (SetHeader sh : mh.getSetHeaders()) {
			for (DataRecord dr : sh.getDataRecords()) {
				if (dr instanceof L2IPDataRecord) numberDataRecords++;
			}
		}
		sequenceNumbers.put(currentIPObservationDomain, new Long(mh.getSequenceNumber() + numberDataRecords));
		return detectedMissing;
	}
}