package net.decix.jipfix.header;

public abstract class Record implements IPFIXEntity {
	
	protected int length;
	
	public int getLength(){
		return length;
	}
}