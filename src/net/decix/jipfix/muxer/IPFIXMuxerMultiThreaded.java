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
package net.decix.jipfix.muxer;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.parsers.ParserConfigurationException;

import net.decix.jipfix.header.MessageHeader;
import net.decix.jsflow.header.HeaderBytesException;
import net.decix.jsflow.header.HeaderParseException;
import net.decix.util.Address;
import net.decix.util.AddressPort;
import net.decix.util.Utility;
import net.decix.util.UtilityException;

import org.savarese.vserv.tcpip.IPPacket;
import org.savarese.vserv.tcpip.OctetConverter;
import org.savarese.vserv.tcpip.UDPPacket;
import org.xml.sax.SAXException;

import com.savarese.rocksaw.net.RawSocket;

public class IPFIXMuxerMultiThreaded implements Callable<Void> {
	private final static Logger LOGGER = Logger.getLogger(IPFIXMuxerMultiThreaded.class.getName());
	
	private ConfigParser cp;
	
	public IPFIXMuxerMultiThreaded(ConfigParser cp) {
		this.cp = cp;
	}
	
	@Override
	public Void call() {
		DatagramSocket dsReceive = null;
		try {
			// listen on this address and port to receive IPFIX data coming from
			// switches
			Address listenAddress = cp.getListenAddress();
			int listenPort = cp.getListenPort();

			// list of destinations of plain IPFIX stream
			Vector<AddressPort> plainDestinations = cp.getPlainDestinations();

			dsReceive = new DatagramSocket(listenPort, listenAddress.getInetAddress());
			dsReceive.setReceiveBufferSize(26214400);
			LOGGER.log(Level.FINE, "Datagram socket for receiving created.");

			RawSocket socket = new RawSocket();
			socket.open(RawSocket.PF_INET, RawSocket.getProtocolByName("udp"));
			socket.setIPHeaderInclude(true);
			LOGGER.log(Level.FINE, "Raw socket for sending created.");
			
			ExecutorService pool = Executors.newFixedThreadPool(10);
						
			while (true) {
				byte[] data = new byte[65536];
				DatagramPacket dp = new DatagramPacket(data, data.length);
				dsReceive.receive(dp);

				pool.execute(new Handler(socket, plainDestinations, dp));
			}
		} catch (SocketException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (UtilityException ue) {
			ue.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (dsReceive != null) dsReceive.close();
		}
		return null;
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
			
			FileHandler fh = new FileHandler(logPath + File.separator + "ipfix-muxer.log", 5 * 10485760, 20, true); // 20 x 50MByt
			fh.setFormatter(new SimpleFormatter());
			Logger l = Logger.getLogger("");
			l.addHandler(fh);
			l.setLevel(Level.FINEST);
						
			ConfigParser cp = new ConfigParser();
			cp.loadConfig(cfgPath);

			IPFIXMuxerMultiThreaded ipfixMuxer = new IPFIXMuxerMultiThreaded(cp);
			ExecutorService executorIPFIXMuxer = Executors.newFixedThreadPool(1);
			executorIPFIXMuxer.submit(ipfixMuxer);
			LOGGER.log(Level.FINE, "IPFIX muxer startet.");
			
			Pinger pinger = new Pinger(cp);
			ScheduledExecutorService executorPinger = Executors.newScheduledThreadPool(1);
			executorPinger.scheduleAtFixedRate(pinger, 10, 10, TimeUnit.SECONDS);
			LOGGER.log(Level.FINE, "Pinger service startet.");
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
	
	class Handler implements Runnable {
		private RawSocket socket;
		private Vector<AddressPort> plainDestinations;
		private DatagramPacket dp;
		
		public Handler(RawSocket socket, Vector<AddressPort> plainDestinations, DatagramPacket dp) {
			this.socket = socket;
			this.plainDestinations = plainDestinations;
			this.dp = dp;
		}

		@Override
		public void run() {
			try {
				// prepare UDP packet
				byte[] dataMuxer = null;

				if (cp.isStartMissingDataRecordDector()) {
					// parsing is required
					MessageHeader mh = MessageHeader.parse(dp.getData());
					
					LOGGER.log(Level.INFO, "Be aware: The missing datarecord detector is not working in this setup, so don't expect this information");

					dataMuxer = mh.getBytes();
				} else {
					// no parsing required. So do fast and easy copying.
					dataMuxer = new byte[dp.getLength()];
					System.arraycopy(dp.getData(), 0, dataMuxer, 0, dp.getLength());
				}


				UDPPacket udp = new UDPPacket(28 + dataMuxer.length);
				udp.setIPVersion(4);
				udp.setIPHeaderLength(5);
				udp.setProtocol(IPPacket.PROTOCOL_UDP);
				udp.setTTL(5);

				udp.setUDPDataByteLength(dataMuxer.length);
				udp.setUDPPacketLength(dataMuxer.length + 8);

				udp.setDestinationPort(cp.getListenPort());
				udp.setSourcePort(dp.getPort());

				byte[] pktData = new byte[udp.size()];

				// plain MUXER part
				for (AddressPort ap : plainDestinations) {
					udp.setDestinationAsWord(OctetConverter.octetsToInt(ap.getAddress().getBytes()));
					udp.setSourceAsWord(OctetConverter.octetsToInt(dp.getAddress().getAddress()));
					udp.setDestinationPort(ap.getPort());
					udp.getData(pktData);
					System.arraycopy(dataMuxer, 0, pktData, 28, dataMuxer.length);
					udp.setData(pktData);
					udp.computeUDPChecksum(true);
					udp.computeIPChecksum(true);
					udp.getData(pktData);
					socket.write(ap.getAddress().getInetAddress(), pktData);
				}
			} catch (HeaderParseException hpe) {
				LOGGER.log(Level.INFO, "HeaderParseException: " + Utility.dumpBytes(dp.getData()));

				hpe.printStackTrace();
				System.out.println("========================================================");
				System.out.println("HeaderParseException: *");
				System.out.println(Utility.dumpBytes(dp.getData()));
				System.out.println("========================================================");
			} catch (HeaderBytesException hbe) {
				LOGGER.log(Level.INFO, "HeaderBytesException: " + Utility.dumpBytes(dp.getData()));
				hbe.printStackTrace();
			} catch (UtilityException ue) {
				LOGGER.log(Level.INFO, "UtilityException: " + Utility.dumpBytes(dp.getData()));
				ue.printStackTrace();
			} catch (InterruptedIOException e) {
				LOGGER.log(Level.INFO, "InterruptedIOException: " + Utility.dumpBytes(dp.getData()));
				e.printStackTrace();
			} catch (UnknownHostException e) {
				LOGGER.log(Level.INFO, "UnknownHostException: " + Utility.dumpBytes(dp.getData()));
				e.printStackTrace();
			} catch (IOException e) {
				LOGGER.log(Level.INFO, "IOException: " + Utility.dumpBytes(dp.getData()));
				e.printStackTrace();
			} catch (Throwable t) {
				LOGGER.log(Level.INFO, "Throwable: ");
				t.printStackTrace();
			}
		}
	}
}