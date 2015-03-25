package net.decix.randomizer;

import java.net.InetAddress;
import java.util.HashMap;

public abstract class IPAddressRadomizer {
	private HashMap <InetAddress, InetAddress> ipDatabase = new HashMap<InetAddress, InetAddress>();
	
	private boolean staticRandomization = true;
	
	public IPAddressRadomizer() {
		this.staticRandomization = true;
	}
	
	public IPAddressRadomizer(boolean staticRandomization) {
		this.staticRandomization = staticRandomization;
	}
	
	public InetAddress randomize(InetAddress realAddress) {
		if (staticRandomization == false) {
			return ipAddressRandomizer(realAddress);
		} else {
			InetAddress fakeAdress = ipDatabase.get(realAddress);
			if (fakeAdress != null) {
				return fakeAdress;
			} else {
				fakeAdress = ipAddressRandomizer(realAddress);
				ipDatabase.put(realAddress, fakeAdress);
				
				return ipDatabase.get(realAddress);
			}
		}
	}
	
	public int getDatabaseSize() {
		return ipDatabase.size();
	}
	
	protected abstract InetAddress ipAddressRandomizer(InetAddress realAddress);
}


