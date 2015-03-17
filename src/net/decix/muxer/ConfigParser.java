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
	
	private Address ipfixListenAddress;
	private int ipfixListenPort;
	private boolean ipfixPingCollectors;
	private Vector<Address> ipfixPingIPs;
	private Vector<AddressPort> ipfixPlainDestinations;
	private boolean ipfixStartMissingDataRecordDetector;
	
	private Address sflowListenAddress;
	private int sflowListenPort;
	private boolean sflowPingCollectors;
	private Vector<Address> sflowPingIPs;
	private Vector<AddressPort> sflowPlainDestinations;
	private boolean sflowStartMissingDataRecordDetector;
	
	public ConfigParser() {
		ipfixPingCollectors = false;
		ipfixPingIPs = new Vector<Address>();
		ipfixPlainDestinations = new Vector<AddressPort>();
		ipfixStartMissingDataRecordDetector = false;
		
		sflowPingCollectors = false;
		sflowPingIPs = new Vector<Address>();
		sflowPlainDestinations = new Vector<AddressPort>();
		sflowStartMissingDataRecordDetector = false;
	}
	
	public Address getIPFIXListenAddress() {
		return ipfixListenAddress;
	}

	public int getIPFIXListenPort() {
		return ipfixListenPort;
	}
	
	public boolean isIPFIXPingCollectors() {
		return ipfixPingCollectors;
	}
	
	public void setIPFIXPingCollectors(boolean ipfixPingCollectors) {
		this.ipfixPingCollectors = ipfixPingCollectors;
	}
	
	public boolean isIPFIXStartMissingDataRecordDector() {
		return ipfixStartMissingDataRecordDetector;
	}
	
	public void setIPFIXStartMissingDataRecordDector(boolean ipfixStartMissingDataRecordDetector) {
		this.ipfixStartMissingDataRecordDetector = ipfixStartMissingDataRecordDetector;
	}
	
	public Vector<Address> getIPFIXPingIPs() {
		return ipfixPingIPs;
	}

	public Vector<AddressPort> getIPFIXPlainDestinations() {
		return ipfixPlainDestinations;
	}
	
	public Address getSFlowListenAddress() {
		return sflowListenAddress;
	}

	public int getSFlowListenPort() {
		return sflowListenPort;
	}
	
	public boolean isSFlowPingCollectors() {
		return sflowPingCollectors;
	}
	
	public void setSFlowPingCollectors(boolean sflowPingCollectors) {
		this.sflowPingCollectors = sflowPingCollectors;
	}
	
	public boolean isSFlowStartMissingDataRecordDector() {
		return sflowStartMissingDataRecordDetector;
	}
	
	public void setSFlowStartMissingDataRecordDector(boolean sflowStartMissingDataRecordDetector) {
		this.sflowStartMissingDataRecordDetector = sflowStartMissingDataRecordDetector;
	}
	
	public Vector<Address> getSFlowPingIPs() {
		return sflowPingIPs;
	}

	public Vector<AddressPort> getSFlowPlainDestinations() {
		return sflowPlainDestinations;
	}

	public void loadConfig(String cfgPath) throws IOException, SAXException, ParserConfigurationException, UtilityException {
		File configFile = new File(cfgPath + File.separator + "jflowlib.xml");
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configFile);

		NodeList nodeList = doc.getElementsByTagName("jflowlib-muxer");
		Node rootNode = nodeList.item(0);
		NodeList rootNodeList = rootNode.getChildNodes();
		for (int p = 0; p < rootNodeList.getLength(); p++) {
			Node jFlowLibMuxer = rootNodeList.item(p);
			if (jFlowLibMuxer.getNodeName().equals("jipfix-muxer")) {
				NodeList jipfixMuxerList = jFlowLibMuxer.getChildNodes();
				for (int i = 0; i < jipfixMuxerList.getLength(); i++) {
					Node muxer = jipfixMuxerList.item(i);
					if (muxer.getNodeName().equals("listen")) {
						NodeList ipPort = muxer.getChildNodes();
						for (int k = 0; k < ipPort.getLength(); k++) {
							Node ipOrPortOrDetector = ipPort.item(k);
							if (ipOrPortOrDetector.getNodeName().equals("ip")) {
								ipfixListenAddress = new Address(ipOrPortOrDetector.getTextContent());
							}
							if (ipOrPortOrDetector.getNodeName().equals("port")) {
								ipfixListenPort = Integer.parseInt(ipOrPortOrDetector.getTextContent());
							}
							if (ipOrPortOrDetector.getNodeName().equals("startmissingdatarecorddetector")) {
								ipfixStartMissingDataRecordDetector = Boolean.parseBoolean(ipOrPortOrDetector.getTextContent());	
							}
						}
					}

					if (muxer.getNodeName().equals("ping")) {
						NodeList pingList = muxer.getChildNodes();
						for (int k = 0; k < pingList.getLength(); k++) {
							Node pingConfig = pingList.item(k);
							if (pingConfig.getNodeName().equals("collectors")) {
								ipfixPingCollectors = Boolean.parseBoolean(pingConfig.getTextContent());
							}
							if (pingConfig.getNodeName().equals("ip")) {
								Address address = new Address(pingConfig.getTextContent());
								getIPFIXPingIPs().add(address);
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
												ipfixPlainDestinations.add(new AddressPort(address, port));
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
			}
			if (jFlowLibMuxer.getNodeName().equals("jsflow-muxer")) {
				NodeList jsflowMuxerList = jFlowLibMuxer.getChildNodes();
				for (int i = 0; i < jsflowMuxerList.getLength(); i++) {
					Node muxer = jsflowMuxerList.item(i);
					if (muxer.getNodeName().equals("listen")) {
						NodeList ipPort = muxer.getChildNodes();
						for (int k = 0; k < ipPort.getLength(); k++) {
							Node ipOrPortOrDetector = ipPort.item(k);
							if (ipOrPortOrDetector.getNodeName().equals("ip")) {
								sflowListenAddress = new Address(ipOrPortOrDetector.getTextContent());
							}
							if (ipOrPortOrDetector.getNodeName().equals("port")) {
								sflowListenPort = Integer.parseInt(ipOrPortOrDetector.getTextContent());
							}
							if (ipOrPortOrDetector.getNodeName().equals("startmissingdatarecorddetector")) {
								sflowStartMissingDataRecordDetector = Boolean.parseBoolean(ipOrPortOrDetector.getTextContent());	
							}
						}
					}

					if (muxer.getNodeName().equals("ping")) {
						NodeList pingList = muxer.getChildNodes();
						for (int k = 0; k < pingList.getLength(); k++) {
							Node pingConfig = pingList.item(k);
							if (pingConfig.getNodeName().equals("collectors")) {
								sflowPingCollectors = Boolean.parseBoolean(pingConfig.getTextContent());
							}
							if (pingConfig.getNodeName().equals("ip")) {
								Address address = new Address(pingConfig.getTextContent());
								getSFlowPingIPs().add(address);
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
												sflowPlainDestinations.add(new AddressPort(address, port));
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
			}
		}
//		LOGGER.log(Level.FINE, "Config file successfully loaded: " + configFile.toPath());
		LOGGER.log(Level.FINE, "Config file content: " + this.toString());
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IPFIX: Listen address: ");
		sb.append(ipfixListenAddress != null ? ipfixListenAddress.toString() : "");
		sb.append("\n");
		sb.append("IPFIX: Listen port: ");
		sb.append(ipfixListenPort);
		sb.append("\n");
		sb.append("IPFIX: Start missing data record detector: ");
		sb.append(ipfixStartMissingDataRecordDetector);
		sb.append("\n");
		sb.append("IPFIX: Ping collectors: ");
		sb.append(ipfixPingCollectors);
		sb.append("\n");
		sb.append("IPFIX: Ping IPs: ");
		sb.append("\n");
		for (Address a : ipfixPingIPs) {
			sb.append(" " + a.toString());
			sb.append("\n");
		}
		sb.append("IPFIX: Plain destinations: ");
		sb.append("\n");
		for (AddressPort ap : ipfixPlainDestinations) {
			sb.append(" " + ap.getAddress().toString() + ":" + ap.getPort());
			sb.append("\n");
		}
		
		sb.append("sFlow: Listen address: ");
		sb.append(sflowListenAddress != null ? sflowListenAddress.toString() : "");
		sb.append("\n");
		sb.append("sFlow: Listen port: ");
		sb.append(sflowListenPort);
		sb.append("\n");
		sb.append("sFlow: Start missing data record detector: ");
		sb.append(sflowStartMissingDataRecordDetector);
		sb.append("\n");
		sb.append("sFlow: Ping collectors: ");
		sb.append(sflowPingCollectors);
		sb.append("\n");
		sb.append("sFlow: Ping IPs: ");
		sb.append("\n");
		for (Address a : sflowPingIPs) {
			sb.append(" " + a.toString());
			sb.append("\n");
		}
		sb.append("sFlow: Plain destinations: ");
		sb.append("\n");
		for (AddressPort ap : sflowPlainDestinations) {
			sb.append(" " + ap.getAddress().toString() + ":" + ap.getPort());
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public static void main(String args[]) throws IOException, SAXException, ParserConfigurationException, UtilityException {
		ConfigParser cp = new ConfigParser();
		cp.loadConfig("res");
		System.out.println(cp.toString());
	}
}