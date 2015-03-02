/*  
 * Base64.java - base64 encoding/decoding
 * 
 * Copyright (c) 2015 Stefano Marchetti
 * 
 * This file is part of Jedecma - breast ultrasound examinations archiving software
 * 
 * Jedecma is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Jedecma is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jedecma.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */

package jedecma;

public class Base64 {

	private static final String base64code = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "abcdefghijklmnopqrstuvwxyz" + "0123456789" + "+/";

	public static byte[] zeroPad(int length, byte[] bytes) {
		byte[] padded = new byte[length]; // initialized to zero by JVM
		System.arraycopy(bytes, 0, padded, 0, bytes.length);
		return padded;
	}

	public static String encode(String string) {

		String encoded = "";
		byte[] stringArray;
		try {
			stringArray = string.getBytes("UTF-8"); // use appropriate encoding
													// string!
		} catch (Exception ignored) {
			stringArray = string.getBytes(); // use locale default rather than
												// croak
		}
		// determine how many padding bytes to add to the output
		int paddingCount = (3 - (stringArray.length % 3)) % 3;
		// add any necessary padding to the input
		stringArray = zeroPad(stringArray.length + paddingCount, stringArray);
		// process 3 bytes at a time, churning out 4 output bytes
		// worry about CRLF insertions later
		for (int i = 0; i < stringArray.length; i += 3) {
			int j = ((stringArray[i] & 0xff) << 16)
					+ ((stringArray[i + 1] & 0xff) << 8)
					+ (stringArray[i + 2] & 0xff);
			encoded = encoded + base64code.charAt((j >> 18) & 0x3f)
					+ base64code.charAt((j >> 12) & 0x3f)
					+ base64code.charAt((j >> 6) & 0x3f)
					+ base64code.charAt(j & 0x3f);
		}
		// replace encoded padding nulls with "="
		encoded = encoded.substring(0, encoded.length() - paddingCount)
				+ "==".substring(0, paddingCount);
		return encoded;
	}

	public static String decode(String string) {
		if (string == null) {
			string = "";
		}
		// esegue il padding perche' la stringa potrebbe non avere lunghezza
		// corretta
		while (string.length() % 4 != 0) {
			string += "=";
		}

		String decode = "";
		byte[] stringArray;
		try {
			stringArray = string.getBytes("UTF-8");
		} catch (Exception ignored) {
			stringArray = string.getBytes();
		}

		// sostituisce padding con lo 0 espresso in base64
		int paddingCount = 0;
		for (int i = 0; i < stringArray.length; i++) {
			if (((char) stringArray[i]) == '=') {
				stringArray[i] = (byte) base64code.charAt(0);
				paddingCount++;
			}
		}
		for (int i = 0; i < stringArray.length; i += 4) {
			int j = ((base64code.indexOf(stringArray[i]) & 0x3f) << 18)
					+ ((base64code.indexOf(stringArray[i + 1]) & 0x3f) << 12)
					+ ((base64code.indexOf(stringArray[i + 2]) & 0x3f) << 6)
					+ (base64code.indexOf(stringArray[i + 3]) & 0x3f);
			decode = decode + (char) ((j >> 16) & 0xff)
					+ (char) ((j >> 8) & 0xff) + (char) (j & 0xff);
		}
		// elimina caratteri 0 finali
		decode = decode.substring(0, decode.length() - paddingCount);
		return decode;
	}

} // end Base64 class
