package net.decix.jsflow.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

public class PcapToJavaByteConverter {

	public static void main(String args[]) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(new File("test/net/decix/jsflow/utils/sflowByte2.txt")));
		
		String byteString = new String("byte[] data = {");
		
		String line = null;
		while ((line = br.readLine()) != null) {
			int counterLine = 0;
			StringTokenizer st = new StringTokenizer(line);
			while (st.hasMoreTokens()) {
				if (counterLine == 0) {
					st.nextToken();
					counterLine++;
					continue;
				} else {
					byteString += " (byte) 0x" + st.nextToken();
				}
				if (st.hasMoreTokens() || br.ready()) byteString += ",";
				if (!st.hasMoreTokens()) {
					System.out.println(byteString);
					byteString = "";
				}
				counterLine++;
			}
		}
		byteString += " };";
		System.out.println(byteString);
	}
}
