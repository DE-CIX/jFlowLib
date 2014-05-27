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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.decix.util.Address;
import net.decix.util.AddressPort;
import net.decix.util.UtilityException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigParser {
	private final static Logger LOGGER = Logger.getLogger(ConfigParser.class.getName());
	
	private Address listenAddress;
	private int listenPort;
	private boolean pingCollectors;
	private Vector<Address> pingIPs;
	private Vector<AddressPort> plainDestinations;
	private boolean startMissingDataRecordDetector;
	
	public ConfigParser() {
		pingCollectors = false;
		pingIPs = new Vector<Address>();
		plainDestinations = new Vector<AddressPort>();
		startMissingDataRecordDetector = false;
	}
	
	public Address getListenAddress() {
		return listenAddress;
	}

	public int getListenPort() {
		return listenPort;
	}
	
	public boolean isPingCollectors() {
		return pingCollectors;
	}
	
	public void setPingCollectors(boolean pingCollectors) {
		this.pingCollectors = pingCollectors;
	}
	
	public boolean isStartMissingDataRecordDector() {
		return startMissingDataRecordDetector;
	}
	
	public void setStartMissingDataRecordDector(boolean startMissingDataRecordDetector) {
		this.startMissingDataRecordDetector = startMissingDataRecordDetector;
	}
	
	public Vector<Address> getPingIPs() {
		return pingIPs;
	}

	public Vector<AddressPort> getPlainDestinations() {
		return plainDestinations;
	}

	public void loadConfig(String cfgPath) throws IOException, SAXException, ParserConfigurationException, UtilityException {
		File configFile = new File(cfgPath + File.separator + "jipfix.xml");
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configFile);

		NodeList nodeList = doc.getElementsByTagName("jipfix-muxer");
		Node jsflowRewriter = nodeList.item(0);
		NodeList muxerList = jsflowRewriter.getChildNodes();
		for (int i = 0; i < muxerList.getLength(); i++) {
			Node muxer = muxerList.item(i);
			if (muxer.getNodeName().equals("listen")) {
				NodeList ipPort = muxer.getChildNodes();
				for (int k = 0; k < ipPort.getLength(); k++) {
					Node ipOrPortOrDetector = ipPort.item(k);
					if (ipOrPortOrDetector.getNodeName().equals("ip")) {
						listenAddress = new Address(ipOrPortOrDetector.getTextContent());
					}
					if (ipOrPortOrDetector.getNodeName().equals("port")) {
						listenPort = Integer.parseInt(ipOrPortOrDetector.getTextContent());
					}
					if (ipOrPortOrDetector.getNodeName().equals("startmissingdatarecorddetector")) {
						startMissingDataRecordDetector = Boolean.parseBoolean(ipOrPortOrDetector.getTextContent());	
					}
				}
			}
			
			if (muxer.getNodeName().equals("ping")) {
				NodeList pingList = muxer.getChildNodes();
				for (int k = 0; k < pingList.getLength(); k++) {
					Node pingConfig = pingList.item(k);
					if (pingConfig.getNodeName().equals("collectors")) {
						pingCollectors = Boolean.parseBoolean(pingConfig.getTextContent());
					}
					if (pingConfig.getNodeName().equals("ip")) {
						Address address = new Address(pingConfig.getTextContent());
						getPingIPs().add(address);
					}
				}
			}
			
			if (muxer.getNodeName().equals("muxer")) {
				if (muxer.getAttributes().item(0).getNodeName().equals("type")) {
					if (muxer.getAttributes().item(0).getNodeValue().equals("plain")) {
						NodeList list = muxer.getChildNodes();
						for (int j = 0; j < list.getLength(); j++) {
							Node node = list.item(j);
							// collector
							if (node.getNodeName().equals("collector")) {
								NodeList ipPort = node.getChildNodes();
								Address address = null;
								Integer port = null;
								for (int k = 0; k < ipPort.getLength(); k++) {
									Node ipOrPort = ipPort.item(k);
									if (ipOrPort.getNodeName().equals("ip")) {
										address = new Address(ipOrPort.getTextContent());
									}
									if (ipOrPort.getNodeName().equals("port")) {
										port = Integer.parseInt(ipOrPort.getTextContent());
									}
									if ((address != null) && (port != null)) {
										plainDestinations.add(new AddressPort(address, port));
										address = null;
										port = null;
									}
								}
							}
						}
					}
				}
			}			
		}
//		LOGGER.log(Level.FINE, "Config file successfully loaded: " + configFile.toPath());
		LOGGER.log(Level.FINE, "Config file content: " + this.toString());
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Listen address: ");
		sb.append(listenAddress.toString());
		sb.append("\n");
		sb.append("Listen port: ");
		sb.append(listenPort);
		sb.append("\n");
		sb.append("Start missing data record detector: ");
		sb.append(startMissingDataRecordDetector);
		sb.append("\n");
		sb.append("Ping collectors: ");
		sb.append(pingCollectors);
		sb.append("\n");
		sb.append("Ping IPs: ");
		sb.append("\n");
		for (Address a : pingIPs) {
			sb.append(" " + a.toString());
			sb.append("\n");
		}
		sb.append("Plain destinations: ");
		sb.append("\n");
		for (AddressPort ap : plainDestinations) {
			sb.append(" " + ap.getAddress().toString() + ":" + ap.getPort());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}