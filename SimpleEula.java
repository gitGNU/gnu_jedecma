/*  
 * SimpleEula.java - displays eula agreement on program start
 * 
 * Copyright (c) 2014 Stefano Marchetti
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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

public class SimpleEula {
	private String EULA_PREF = "eula_";
	private String key;
	
	SimpleEula() {
		super();
		String language = Jedecma.param.getProperty("language");
		if (language.equals("")) {
			language = "en";
		}
		String s = Jedecma.progVers;
		int i = s.lastIndexOf('.');
		key = EULA_PREF + s.substring(0, i) + "_" + language;
	}
	
	public void show() {
	    if (hasBeenShown() == false) {
	    	String title = "Jedecma " + Jedecma.progVers + " License Agreement";
	    	String msg = loadEula(key);	    	
	    	
	    	if (Uti1.txtYN(msg, title, JOptionPane.INFORMATION_MESSAGE, null) == 1) {
	    		// eula accettata
	    		System.out.println("eula accepted");
	    		Properties props = Jedecma.param;
	    		props.put("eulashown", key);
	    		String parmfile = "jedecma.prm";

				// Write properties file.
				try {
					props.store(new FileOutputStream(parmfile), null);
				} catch (IOException ioe) {
					System.out.println("unable to write parameter file "
							+ parmfile);
				}
	    		
	    	} else {
	    		// eula rifiutata
	    		System.out.println("eula refused");
	    		Uti1.error("<html><body><center>Jedecma "
					      + Jedecma.progVers + " " + Jedecma.progBuild + "<br>"
					      + Jedecma.localMessagesBundle.getString("EULA_REFUSED")
					      + "<br>"
					      +"</center></body></html>", 
						false);/**
	    		 * @author sfm
	    		 *
	    		 */
				Jedecma.endProc();
	    	} 
	    }
	}
	
	private String loadEula(String key) {
		String text = "";
		final String inputFile = key + ".txt";
		try {
			BufferedReader in = new BufferedReader(
					new FileReader(inputFile));
			String line = "";
			while ((line = in.readLine()) != null) {
				text += line + '\n';
			}
			text += "";
			in.close();
		} catch (IOException ie) {
			System.out.println("unable to read eula file " + inputFile + " (" + ie + ")");
			Uti1.error("<html><body><center>Jedecma "
				      + Jedecma.progVers + " " + Jedecma.progBuild + "<br>"
				      + Jedecma.localMessagesBundle.getString("EULA_NOTFOUND")
				      + "<br>"
				      + inputFile
				      +"</center></body></html>", 
					true);
		}
		return text;
	}
	
	  public boolean hasBeenShown() {
		  String eulaShown = Jedecma.param.getProperty("eulashown");
		  if ( eulaShown.toUpperCase().equals(key.toUpperCase())) {
			  return true;
		  }
		  return false;
	  }

	public String getKey() {
		return key;
	}

}
