package net.decix.jsflow.util;

import java.net.InetAddress;
import java.util.HashMap;

public abstract class IPAddressRadomizer {
	HashMap <InetAddress, InetAddress> ipDatabase = new HashMap<InetAddress, InetAddress>();
	
	private boolean staticRandomization = true;
	
	IPAddressRadomizer() {
		this.staticRandomization = true;
	}
	
	IPAddressRadomizer(boolean staticRandomization) {
		this.staticRandomization = staticRandomization;
	}
	

	
	public InetAddress randomize(InetAddress realAddress) {
		
		if (staticRandomization == false) {
			
			return ipAddressRandomizer();
			
		} else {
			
			InetAddress fakeAdress = ipDatabase.get(realAddress);
			if (fakeAdress != null) {
				return fakeAdress;
			} else {
				fakeAdress = ipAddressRandomizer();
				ipDatabase.put(realAddress, fakeAdress);
				
				return  ipDatabase.get(realAddress);
			}
			
		}
		

	
	}
	
	public int  getDatabaseSize() {
		return ipDatabase.size();
	}
	
	protected abstract InetAddress ipAddressRandomizer();
	

}


