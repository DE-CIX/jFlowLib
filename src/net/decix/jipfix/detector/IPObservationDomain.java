package net.decix.jipfix.detector;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPObservationDomain {
	private InetAddress address;
	private Long observationDomain;
	
	public IPObservationDomain() {
		try {
			this.address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.observationDomain = 1l;
	}
	
	public IPObservationDomain(InetAddress address, long observationDomain) {
		this.address = address;
		this.observationDomain = observationDomain;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public Long getObservationDomain() {
		return observationDomain;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result += ((address == null) ? 0 : address.hashCode());
		result += ((observationDomain == null) ? 0 : observationDomain.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		IPObservationDomain other = (IPObservationDomain) obj;
		if (address == null) {
			if (other.address != null) return false;
		} else if (!address.equals(other.address)) return false;
		if (observationDomain == null) {
			if (other.observationDomain != null) return false;
		} else if (!observationDomain.equals(other.observationDomain)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "Address: " + address + ", observation domain: " + observationDomain;
	}
}
