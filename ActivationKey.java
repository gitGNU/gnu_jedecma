/*  
 * ActivationKey.java - activation key management
 * 
 * Copyright (c) 2017 Stefano Marchetti
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

public class ActivationKey {
	String serialNumber;
	final int lenActivationString = 21;
	java.util.Date expDate;
	boolean enableWrite;
	boolean enableMulti;
	boolean enablePrinting;
	boolean enableStatistics;
	boolean enableTools;

	ActivationKey() {
		serialNumber = "0000";
		expDate = Uti1.string2Date("01/01/2000");
		enableWrite = false;
		enableMulti = false;
		enablePrinting = false;
		enableStatistics = false;
		enableTools = false;
	}

	void readKey() {
		String s = Jedecma.param.getProperty("userkey");
		s = Base64.decode(s);

		if (validActivationString(s)) {
			serialNumber = s.substring(0, 4);
			expDate = Uti1.string2Date(s.substring(12, 20));

			String str;

			str = s.substring(4, 5);
			if (str.equals("0")) {
				enableMulti = false;
			} else {
				enableMulti = true;
			}

			str = s.substring(5, 6);
			if (str.equals("1")) {
				enableWrite = true;
			} else {
				enableWrite = false;
			}

			str = s.substring(6, 7);
			if (str.equals("1")) {
				enablePrinting = true;
			} else {
				enablePrinting = false;
			}

			str = s.substring(7, 8);
			if (str.equals("1")) {
				enableStatistics = true;
			} else {
				enableStatistics = false;
			}

			str = s.substring(8, 9);
			if (str.equals("1")) {
				enableTools = true;
			} else {
				enableTools = false;
			}

		}
		System.out.println(Uti1.date2String(expDate) + " serNumb="
				+ serialNumber + " EnMulti=" + enableMulti + " EnWrite="
				+ enableWrite + " EnPrint=" + enablePrinting + " EnStats="
				+ enableStatistics + " EnTools=" + enableTools);

	}

	boolean validActivationString(String key) {
		int l = key.length();
		if (l != lenActivationString) {
			return false;
		}
		for (int i = 0; i < lenActivationString; i++) {
			if (key.charAt(i) < '0' || key.charAt(i) > '9')
				return false;
		}

		String sscc_chk = sscc_chk(key.substring(0, l - 1));
		String lastDigit = key.substring(l - 1);

		if (sscc_chk.equals(lastDigit)) {
			return true;
		} else {
			return false;
		}
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public java.util.Date getExpDate() {
		return expDate;
	}

	public boolean isEnableWrite() {
		return enableWrite;
	}

	public boolean isEenableMulti() {
		return enableMulti;
	}

	public boolean isEnablePrinting() {
		return enablePrinting;
	}

	public boolean isEnableStatistics() {
		return enableStatistics;
	}

	public boolean isEnableTools() {
		return enableTools;
	}

	public String sscc_chk(String key) {
		int sscc_chk = 0;
		int i, r, wn;
		int l = key.length();

		String c;
		r = 3;
		wn = 0;
		for (i = l - 1; i >= 0; i -= 1) {
			c = key.substring(i, i + 1);
			wn += Integer.parseInt(c) * r;
			// System.out.println("i= " + i + " c= " + c + "wn= " + wn);
			r = 4 - r;
		}

		sscc_chk = (1000 - wn) % 10;

		return String.valueOf(sscc_chk);

	}

	public String genItKey() { // default key
		/*
		 * one month Calendar calendar = new GregorianCalendar();
		 * calendar.setTime(new java.util.Date()); calendar.set(Calendar.MONTH,
		 * calendar.get(Calendar.MONTH) +1); java.util.Date d =
		 * calendar.getTime(); String d1 = Uti1.date2String(d); String expire =
		 * d1.substring(0,2) + d1.substring(3,5) + d1.substring(6);
		 */
		String expire = "31122099";

		String newKey = "0000" // serial
				+ "0" // multiuser
				+ "1" // rw
				+ "1" // print
				+ "1" // statistics
				+ "1" // tools
				+ "000" // other
				+ expire; // expire

		String chk = sscc_chk(newKey);
		newKey += chk;
		String newKeyCrypt = Base64.encode(newKey);

		return newKeyCrypt;
	}

} // end class
