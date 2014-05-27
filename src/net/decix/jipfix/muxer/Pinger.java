package net.decix.jipfix.muxer;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import net.decix.util.Address;
import net.decix.util.AddressPort;
import net.decix.util.UtilityException;

import org.xml.sax.SAXException;

public class Pinger implements Runnable {
	private ConfigParser cp;
	
	public Pinger(ConfigParser cp) {
		this.cp = cp;
	}

	public void run() {
		for (Address a : cp.getPingIPs()) {
			try {
				a.getInetAddress().isReachable(500);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (ConnectException ce) {
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UtilityException e) {
				e.printStackTrace();
			}
		}
		
		if (cp.isPingCollectors()) {
			Vector<AddressPort> plainDestinations = cp.getPlainDestinations();
			for (AddressPort ap : plainDestinations) {
				try {
					ap.getAddress().getInetAddress().isReachable(500);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (ConnectException ce) {
				} catch (IOException e) {
					e.printStackTrace();
				} catch (UtilityException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String args[]) {
		String cfgPath = "/opt/jipfix-muxer/etc";
		try {
			if (args.length == 0) {
				System.out.println("Usage: java -cp jipfix.jar net.decix.jipfix.muxer.Pinger [options]\n");
				System.out.println("Options:");
				System.out.println("        -cfg: path to the jipfix.xml file");
				System.out.println();
				System.exit(0);
			}
			if ((args.length == 2) || (args.length == 4)) {
				if (args[0].equals("-cfg")) {
					cfgPath = args[1];
				}
			}
						
			ConfigParser cp = new ConfigParser();
			cp.loadConfig(cfgPath);
			
			Pinger pinger = new Pinger(cp);
			ScheduledExecutorService executorPinger = Executors.newScheduledThreadPool(1);
			executorPinger.scheduleAtFixedRate(pinger, 1, 1, TimeUnit.SECONDS);
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
