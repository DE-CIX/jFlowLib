package net.decix.jipfix.header;

import net.decix.jsflow.header.HeaderBytesException;

public interface IPFIXEntity {
	public String toString();
	public byte[] getBytes() throws HeaderBytesException;
}
