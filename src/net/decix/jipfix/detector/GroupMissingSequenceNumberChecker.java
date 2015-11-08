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
	private static int CHECK_LAST_SEQEUNCE_NUMBERS = 1024;
	private static int DISTANCE_FROM_CURRENT_SEQUENCE_NUMBER = 512;
	private List<Long> mSequenceNumbers = Collections.synchronizedList(new ArrayList<Long>());
	private IPObservationDomain currentIPObservationDomain;
	
	public GroupMissingSequenceNumberChecker() {
		this.currentIPObservationDomain = new IPObservationDomain();
	}
	
	public GroupMissingSequenceNumberChecker(IPObservationDomain currentIPObservationDomain) {
		this.currentIPObservationDomain = currentIPObservationDomain;
	}
	
	public List<Long> getSequenceNumbers() {
		return mSequenceNumbers;
	}
	
	public void addSequenceNumber(long sequenceNumber) {
		mSequenceNumbers.add(sequenceNumber);
		if (((sequenceNumber % DISTANCE_FROM_CURRENT_SEQUENCE_NUMBER) == 0) && (sequenceNumber != 0)) {
			mLogger.fine("Sequence: " + sequenceNumber);
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
		mLogger.fine(currentIPObservationDomain.getAddress().getHostAddress() + ":" + currentIPObservationDomain.getObservationDomain() + ": Start sequence number check in the range of: " + (lastSequenceNumber - CHECK_LAST_SEQEUNCE_NUMBERS) + " - " + (lastSequenceNumber - DISTANCE_FROM_CURRENT_SEQUENCE_NUMBER));
		int missingSequenceNumbers = 0;
		
		for (long i = (lastSequenceNumber - CHECK_LAST_SEQEUNCE_NUMBERS); i < (lastSequenceNumber - DISTANCE_FROM_CURRENT_SEQUENCE_NUMBER); i++) {
			long j = i;
			if (i < 0) {
				j = Math.floorMod(i, (long) Math.pow(2, 32));
			}
			if (!mSequenceNumbers.contains(j)) {
				missingSequenceNumber = true;
				missingSequenceNumbers++;
				System.out.println("Missing: " + j);
			}
		}
//		if (lastSequenceNumber == mSequenceNumbers.get(mSequenceNumbers.size() - 1)) { // hack needed to handle ALU bug which results in equal sequence number in consecutive IPFIX packets
//			missingSequenceNumber = false;
//		}
		if (!missingSequenceNumber) {
			mLogger.info(currentIPObservationDomain.getAddress().getHostAddress() + ":" + currentIPObservationDomain.getObservationDomain() + ": No sequence number is missing in range: " + Math.floorMod((lastSequenceNumber - CHECK_LAST_SEQEUNCE_NUMBERS), (long) Math.pow(2, 32)) + " - " + Math.floorMod((lastSequenceNumber - DISTANCE_FROM_CURRENT_SEQUENCE_NUMBER - 1), (long) Math.pow(2, 32)));
			removeSequenceNumbers(lastSequenceNumber - CHECK_LAST_SEQEUNCE_NUMBERS, lastSequenceNumber - DISTANCE_FROM_CURRENT_SEQUENCE_NUMBER - 1);
		} else {
			mLogger.info("LastSequenceNumber: " + lastSequenceNumber);
			mLogger.info(currentIPObservationDomain.getAddress().getHostAddress() + ":" + currentIPObservationDomain.getObservationDomain() + ": Number of missing sequence numbers: " + missingSequenceNumbers + " in range: " + Math.floorMod((lastSequenceNumber - CHECK_LAST_SEQEUNCE_NUMBERS), (long) Math.pow(2, 32)) + " - " + Math.floorMod((lastSequenceNumber - DISTANCE_FROM_CURRENT_SEQUENCE_NUMBER - 1), (long) Math.pow(2, 32)));
		}
		
		shrinkList();
	}
	
	private void removeSequenceNumbers(long startRange, long endRange) {
		List<Long> removeSequenceNumbers = new ArrayList<Long>();
		for (long i = startRange; i <= endRange; i++) {
			removeSequenceNumbers.add(Math.floorMod(i, (long) Math.pow(2, 32)));
		}
		removeSequenceNumbers(removeSequenceNumbers);	
	}
	
	private void shrinkList() {
		if (mSequenceNumbers.size() > ((CHECK_LAST_SEQEUNCE_NUMBERS * 2) + 1)) {
			mLogger.fine(currentIPObservationDomain.getAddress().getHostAddress() + ":" + currentIPObservationDomain.getObservationDomain() + ": Shrink " + (mSequenceNumbers.size() - (2 * CHECK_LAST_SEQEUNCE_NUMBERS)) + " elements. Size: " + mSequenceNumbers.size());
			int counter = (mSequenceNumbers.size() - (2 * CHECK_LAST_SEQEUNCE_NUMBERS));
			for (int i = 0; i <= counter; i++) {
				mSequenceNumbers.remove(0);
			}
		} else {
			mLogger.fine(currentIPObservationDomain.getAddress().getHostAddress() + ":" + currentIPObservationDomain.getObservationDomain() + ": Nothing to shrink");	
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
		for (long i = (((long) Math.pow(2, 32)) - 1000); i <= Math.pow(2, 32); i++) {
			if (i != (Math.pow(2, 32) - 128)) msnc.addSequenceNumber(i);
//			msnc.addSequenceNumber(i);
		}
		
		for (long i = 0; i <= Math.pow(2, 12); i++) {
			if (i != 125) msnc.addSequenceNumber(i);
		}
		
		System.out.println(msnc.getSequenceNumbers().size());
	}
}
