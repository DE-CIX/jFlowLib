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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.decix.util.Address;
import net.decix.util.AddressPort;
import net.decix.util.MacAddress;
import net.decix.util.UtilityException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigParser {
	private Address listenAddress;
	private int listenPort;
	
	private Vector<AddressPort> plainDestinations;
	private HashMap<MacAddress, AddressPort> plainFilterDestinations;
	private Vector<AddressPort> rewrittenDestinations;
	private HashMap<MacAddress, AddressPort> rewrittenFilterDestinations;
	
	public ConfigParser() {
		this.plainDestinations = new Vector<AddressPort>();
		this.plainFilterDestinations = new HashMap<MacAddress, AddressPort>();
		this.rewrittenDestinations = new Vector<AddressPort>();
		this.rewrittenFilterDestinations = new HashMap<MacAddress, AddressPort>();
	}
	
	public Address getListenAddress() {
		return listenAddress;
	}

	public int getListenPort() {
		return listenPort;
	}

	public Vector<AddressPort> getPlainDestinations() {
		return plainDestinations;
	}

	public HashMap<MacAddress, AddressPort> getPlainFilterDestinations() {
		return plainFilterDestinations;
	}

	public Vector<AddressPort> getRewrittenDestinations() {
		return rewrittenDestinations;
	}

	public HashMap<MacAddress, AddressPort> getRewrittenFilterDestinations() {
		return rewrittenFilterDestinations;
	}

	public void loadConfig(String cfgPath) throws IOException, SAXException, ParserConfigurationException, UtilityException {
		File configFile = new File(cfgPath + File.separator + "sflow-rewriter.xml");
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configFile);

		NodeList nodeList = doc.getElementsByTagName("jsflow-rewriter");
		Node jsflowRewriter = nodeList.item(0);
		NodeList muxerList = jsflowRewriter.getChildNodes();
		for (int i = 0; i < muxerList.getLength(); i++) {
			Node muxer = muxerList.item(i);
			if (muxer.getNodeName().equals("listen")) {
				NodeList ipPort = muxer.getChildNodes();
				for (int k = 0; k < ipPort.getLength(); k++) {
					Node ipOrPort = ipPort.item(k);
					if (ipOrPort.getNodeName().equals("ip")) {
						listenAddress = new Address(ipOrPort.getTextContent());
					}
					if (ipOrPort.getNodeName().equals("port")) {
						listenPort = Integer.parseInt(ipOrPort.getTextContent());
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
							// filter
							if (node.getNodeName().equals("filter")) {
								MacAddress input = new MacAddress(node.getAttributes().item(0).getNodeValue());
								NodeList filterList = node.getChildNodes();
								for (int l = 0; l < filterList.getLength(); l++) {
									Node collector = filterList.item(l);
									if (collector.getNodeName().equals("collector")) {
										NodeList ipPort = collector.getChildNodes();
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
												plainFilterDestinations.put(input, new AddressPort(address, port));
												address = null;
												port = null;
												input = null;
											}
										}
									}
								}
							}

						}
					}
					if (muxer.getAttributes().item(0).getNodeValue().equals("rewritten")) {
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
										rewrittenDestinations.add(new AddressPort(address, port));
										address = null;
										port = null;
									}
								}
							}
							// filter
							if (node.getNodeName().equals("filter")) {
								MacAddress input = new MacAddress(node.getAttributes().item(0).getNodeValue());
								NodeList filterList = node.getChildNodes();
								for (int l = 0; l < filterList.getLength(); l++) {
									Node collector = filterList.item(l);
									if (collector.getNodeName().equals("collector")) {
										NodeList ipPort = collector.getChildNodes();
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
												rewrittenFilterDestinations.put(input, new AddressPort(address, port));
												address = null;
												port = null;
												input = null;
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
		System.out.println("Config file successfully loaded: " + configFile.toPath());
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Listen address: ");
		sb.append(listenAddress.toString());
		sb.append("\n");
		sb.append("Listen port: ");
		sb.append(listenPort);
		sb.append("\n");
		sb.append("Plain destinations: ");
		sb.append("\n");
		for (AddressPort ap : plainDestinations) {
			sb.append(" " + ap.getAddress().toString() + ":" + ap.getPort());
			sb.append("\n");
		}
		
		sb.append("Plain filter destinations: ");
		sb.append("\n");
		for (MacAddress ma : plainFilterDestinations.keySet()) {
			AddressPort ap = plainFilterDestinations.get(ma);
			sb.append(" " + ap.getAddress().toString() + ":" + ap.getPort());
			sb.append("\n");
		}
		
		sb.append("Rewritten destinations: ");
		sb.append("\n");
		for (AddressPort ap : rewrittenDestinations) {
			sb.append(" " + ap.getAddress().toString() + ":" + ap.getPort());
			sb.append("\n");
		}
		
		sb.append("Rewritten filter destinations: ");
		sb.append("\n");
		for (MacAddress ma : rewrittenFilterDestinations.keySet()) {
			AddressPort ap = rewrittenFilterDestinations.get(ma);
			sb.append(" " + ap.getAddress().toString() + ":" + ap.getPort());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}