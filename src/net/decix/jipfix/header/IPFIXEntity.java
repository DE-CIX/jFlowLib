package net.decix.jipfix.header;

import net.decix.util.HeaderBytesException;

public interface IPFIXEntity {
	public String toString();
	public byte[] getBytes() throws HeaderBytesException;
}
