/*
 * This file is part of jsFlow.
 *
 * Copyright (c) 2009 DE-CIX Management GmbH <http://www.de-cix.net> - All rights
 * reserved.
 * 
 * Author: Thomas King <thomas.king@de-cix.net>
 *
 * This software is licensed under the Apache License, version 2.0. A copy of 
 * the license agreement is included in this distribution.
 */
package net.decix.jsflow.rewriting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class MacInterfaceIndexMapper {
	private File file;
	private HashMap<Long, Long> macInterfaceIndex;
	private long nextInterfaceIndexNumber;
	private boolean mappingChanged;
	private Timer timer;
	
	
	public MacInterfaceIndexMapper(File file) {
		this.file = file;
		macInterfaceIndex = new HashMap<Long, Long>(1024);
		nextInterfaceIndexNumber = 1; // do not start with 0, because 0 means the frame has been dropped
		mappingChanged = false;
		timer = new Timer();
		timer.schedule(new MappingWriter(), 300000, 300000); // write the mapping file every 5 minutes
	}
	
	public void loadMapping() throws IOException {
		if (file.canRead()) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				long mac = Long.parseLong(st.nextToken(), 16);
				long interfaceIndex = Long.parseLong(st.nextToken());
				macInterfaceIndex.put(mac, interfaceIndex);
				if (interfaceIndex >= nextInterfaceIndexNumber) nextInterfaceIndexNumber = interfaceIndex + 1;
			}
			br.close();
		}
	}
	
	public void checkMac(long mac) {
		if (macInterfaceIndex.containsKey(mac)) return;
		else {
			synchronized (macInterfaceIndex) {
				macInterfaceIndex.put(mac, nextInterfaceIndexNumber);
			}
			nextInterfaceIndexNumber++;
			mappingChanged = true;
		}
	}
	
	public long getInterfaceIndex(long mac) {
		return macInterfaceIndex.get(mac);
	}

	public boolean isMappingChanged() {
		return mappingChanged;
	}

	public void setMappingChanged(boolean mappingChanged) {
		this.mappingChanged = mappingChanged;
	}
	
	public class MappingWriter extends TimerTask {
		@SuppressWarnings("unchecked")
		public void run() {
			if (mappingChanged) {
				Map<Long, Long> macInterfaceIndexTemp;
				synchronized (macInterfaceIndex) {
					macInterfaceIndexTemp = (Map<Long, Long>) macInterfaceIndex.clone();
				}
				try {
					mappingChanged = false;
					BufferedWriter bw = new BufferedWriter(new FileWriter(file));
					
					Set<Long> keys = macInterfaceIndexTemp.keySet();
					for (Long key : keys) {
						String s = Long.toHexString(key);
						for (int i = s.length(); i < 12; i++) {
							s = "0" + s;
						}
						bw.write(s + " " + macInterfaceIndexTemp.get(key) + "\n");
					}
					bw.close();

					System.out.println(new Date().toString() + ": MappingWriter wrote " + keys.size() + " mappings");
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}
}
