package net.decix.jipfix.detector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GroupMissingSequenceNumberChecker {
	private final static Logger mLogger = Logger.getLogger(GroupMissingSequenceNumberChecker.class.getName());
	private static int CHECK_LAST_SEQEUNCE_NUMBERS = 199;
	private static int DISTANCE_FROM_CURRENT_SEQUENCE_NUMBER = 100;
	private List<Long> mSequenceNumbers = Collections.synchronizedList(new ArrayList<Long>());
	
	public GroupMissingSequenceNumberChecker() {
		
	}
	
	public List<Long> getSequenceNumbers() {
		return mSequenceNumbers;
	}
	
	public void addSequenceNumber(long sequenceNumber) {
		mSequenceNumbers.add(sequenceNumber);
		if ((sequenceNumber % DISTANCE_FROM_CURRENT_SEQUENCE_NUMBER) == 0) {
			mLogger.finest("Sequence: " + sequenceNumber);
			checkSequenceNumbers(sequenceNumber);
		}
	}
	
	public void removeSequenceNumber(long sequenceNumber) {
		mSequenceNumbers.remove(mSequenceNumbers);
	}
	
	public void removeSequenceNumbers(List<Long> sequenceNumbers) {
		mSequenceNumbers.removeAll(sequenceNumbers);
	}
	
	public synchronized void checkSequenceNumbers(long lastSequenceNumber) {
		// sequence number overflow handling is needed -> done
		// limit the space needed -> done
		boolean missingSequenceNumber = false;
		mLogger.fine("Start sequence number check in the range of: " + (lastSequenceNumber - CHECK_LAST_SEQEUNCE_NUMBERS) + " - " + (lastSequenceNumber - DISTANCE_FROM_CURRENT_SEQUENCE_NUMBER));
		int missingSequenceNumbers = 0;
		for (long i = (lastSequenceNumber - CHECK_LAST_SEQEUNCE_NUMBERS); i <= (lastSequenceNumber - DISTANCE_FROM_CURRENT_SEQUENCE_NUMBER); i++) {
			long j = i;
			if (i < 0) {
				j = (long) ((((long) Math.pow(2, 32)) + (i + 10)) % Math.pow(2, 32));
			}
			if (!mSequenceNumbers.contains(j)) {
				missingSequenceNumber = true;
				missingSequenceNumbers++;
			}
		}
		if (lastSequenceNumber == mSequenceNumbers.get(mSequenceNumbers.size() - 1)) { // hack needed to handle ALU bug which results in equal sequence number in consecutive IPFIX packets
			missingSequenceNumber = false;
		}
		if (!missingSequenceNumber) {
			mLogger.fine("No sequence number is missing");
			removeSequenceNumbers(lastSequenceNumber - CHECK_LAST_SEQEUNCE_NUMBERS, lastSequenceNumber - DISTANCE_FROM_CURRENT_SEQUENCE_NUMBER);
		} else {
			mLogger.info("Number of missing sequence numbers: " + missingSequenceNumbers);
		}
		
		shrinkList();
	}
	
	private void removeSequenceNumbers(long startRange, long endRange) {
		List<Long> removeSequenceNumbers = new ArrayList<Long>();
		for (long i = startRange; i <= endRange; i++) {
			removeSequenceNumbers.add(i);
		}
		removeSequenceNumbers(removeSequenceNumbers);	
	}
	
	private void shrinkList() {
		if (mSequenceNumbers.size() > (CHECK_LAST_SEQEUNCE_NUMBERS * 2)) {
			mLogger.fine("Shrink " + (mSequenceNumbers.size() - (2 * CHECK_LAST_SEQEUNCE_NUMBERS)) + " elements. Size: " + mSequenceNumbers.size());
			int counter = (mSequenceNumbers.size() - (2 * CHECK_LAST_SEQEUNCE_NUMBERS));
			for (int i = 0; i < counter; i++) {
				mSequenceNumbers.remove(0);
			}
		} else {
			mLogger.fine("Nothing to shrink");	
		}
	}
	
	public static void main(String args[]) throws SecurityException, IOException {
		FileHandler fh = new FileHandler("ipfix-muxer.log", 5 * 10485760, 20, true); // 20 x 50MByte
		fh.setFormatter(new SimpleFormatter());
		Logger l = Logger.getLogger("");
		l.addHandler(fh);
		l.setLevel(Level.FINEST);
		
		System.setProperty("java.net.preferIPv4Stack" , "true");
		
		mLogger.log(Level.INFO, "Program Start");
		
		GroupMissingSequenceNumberChecker msnc = new GroupMissingSequenceNumberChecker();
		for (long i = (((long) Math.pow(2, 32)) - 1000000); i <= Math.pow(2, 32); i++) {
			msnc.addSequenceNumber(i);
		}
		
		for (long i = 0; i <= 100; i++) {
			msnc.addSequenceNumber(i);
		}
		
		System.out.println(msnc.getSequenceNumbers().size());
	}
}
