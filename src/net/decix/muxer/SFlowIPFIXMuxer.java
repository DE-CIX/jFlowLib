/*
 * This file is part of jIPFIX.
 *
 * Copyright (c) 2009 DE-CIX Management GmbH <http://www.de-cix.net> - All rights
 * reserved.
 * 
 * Author: Thomas King <thomas.king@de-cix.net>
 *
 * This software is licensed under the Apache License, version 2.0. A copy of 
 * the license agreement is included in this distribution.
 */
package net.decix.muxer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.parsers.ParserConfigurationException;

import net.decix.jipfix.muxer.IPFIXMuxerMultiThreaded;
import net.decix.jsflow.muxer.SFlowMuxerMultiThreaded;
import net.decix.util.UtilityException;

import org.xml.sax.SAXException;

public class SFlowIPFIXMuxer {
	private final static Logger LOGGER = Logger.getLogger(SFlowIPFIXMuxer.class.getName());
	
	public SFlowIPFIXMuxer() {
	}
	
	public static void main(String args[]) {
		String cfgPath = "/opt/jipfix-muxer/etc";
		String logPath = "/opt/jipfix-muxer/log";
		
		
		try {
			if (args.length == 0) {
				System.out.println("Usage: java -jar jipfix.jar [options]\n");
				System.out.println("Options:");
				System.out.println("        -cfg: path to the jipfix.xml config file");
				System.out.println("        -log: path to log file");
				System.out.println();
				System.exit(0);
			} else {
				for (int i = 0; i < args.length; i++) {
					String str = args[i];
					if (str.equals("-cfg")) {
						if ((i + 1) < args.length) { 
							cfgPath = args[i + 1];
						}
					}
					if (str.equals("-log")) {
						if ((i + 1) < args.length) { 
							logPath = args[i + 1];
						}
					}
				}
			}
			
			FileHandler fh = new FileHandler(logPath + File.separator + "ipfix-muxer.log", 5 * 10485760, 20, true); // 20 x 50MByte
			fh.setFormatter(new SimpleFormatter());
			Logger l = Logger.getLogger("");
			l.addHandler(fh);
			l.setLevel(Level.FINEST);
			
			System.setProperty("java.net.preferIPv4Stack" , "true");
			
			LOGGER.log(Level.FINE, "Program Start");
			
			ConfigParser cp = new ConfigParser();
			cp.loadConfig(cfgPath);
			
			ExecutorService executorMuxer = Executors.newFixedThreadPool(2);
			
			SFlowMuxerMultiThreaded sflowMuxer = new SFlowMuxerMultiThreaded(cp);
			executorMuxer.submit(sflowMuxer);
			LOGGER.log(Level.FINE, "sFlow muxer startet");
			
			IPFIXMuxerMultiThreaded ipfixMuxer = new IPFIXMuxerMultiThreaded(cp);
			executorMuxer.submit(ipfixMuxer);
			LOGGER.log(Level.FINE, "IPFIX muxer startet");
			
			Pinger pinger = new Pinger(cp);
			ScheduledExecutorService executorPinger = Executors.newScheduledThreadPool(1);
			executorPinger.scheduleAtFixedRate(pinger, 10, 10, TimeUnit.SECONDS);
			LOGGER.log(Level.FINE, "Pinger service startet");
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (UtilityException ue) {
			ue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
