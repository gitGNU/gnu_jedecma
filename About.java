/*  
 * About.java - about window
 * 
 * Copyright (c) 2016 Stefano Marchetti
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

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class About extends JPanel {

	private static final long serialVersionUID = 1L;

	About() {
		if (Jedecma.language.toUpperCase().equals("IT")) {
			showIta();
		} else {
			showDefault();
		}
	}

	void showDefault() {
		String s = "<html><body><center> ";
		s += getProgInfo() + "<br>"
				+ "use until " + Uti1.date2Ansi(Jedecma.ak.getExpDate()) + "<br><br>"
				+ getCopyright() + "<br><br>";

		s += "Jedecma is free software: you can redistribute it and/or modify<br>"
				+ "it under the terms of the GNU General Public License as published by<br>"
				+ "the Free Software Foundation, version 3.<br><br>"
				+ "Jedecma is distributed WITHOUT ANY WARRANTY;<br>"
				+ "without even the implied warranty of<br>"
				+ "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.<br>"
				+ "See \"COPYING\" file and http://www.gnu.org/licenses/<br>"
				+ " for more details.<br><br>";

		s += "Jedecma includes software parts distributed under the Apache License, Version 2.0.<br>"
				+ "Please see \"NOTICE\" file for details.<br><br>";
				
		s += "Jedecma, developed under the supervision of prof. Enzo Durante, <br>"
				+ "is to be considered as an example of software<br>"
				+ "for breast ultrasound examinations archiving and reporting.<br>";
								
		s += "</center></body></html>";
				
		JOptionPane.showMessageDialog(Jedecma.mf, s, "About",
				JOptionPane.INFORMATION_MESSAGE);
	}

	void showIta() {
		String s = "<html><body><center> ";
		s += s += getProgInfo() + "<br>"
				+ "uso previsto fino al " + Uti1.date2Ansi(Jedecma.ak.getExpDate()) + "<br><br>"
				+ getCopyright() + "<br><br>";
				
		s += "Jedecma e' un software libero: e' possibile la sua redistribuzione e/o modifica<br>"
				+ "secondo i termini della Licenza Pubblica Generale GNU versione 3.<br><br>"
				+ "Jedecma viene distribuito SENZA ALCUNA GARANZIA, nemmeno quella implicita<br>"
				+ "di COMMERCIABILITA' o IDONEITA' AD UNO SCOPO PARTICOLARE.<br>"
				+ "Per ulteriori dettagli, vedere il file \"COPYING\"<br>"
				+ "e http://www.gnu.org/licenses/.<br><br>";
	
		s += "Jedecma include parti di software distribuito sotto la Licenza Apache, Versione 2.0.<br>"
				+ "Si prega di vedere il file \"NOTICE\" per ulteriori dettagli.<br><br>";
				
		s += "Jedecma, sviluppato sotto la supervisione del prof. Enzo Durante, <br>"
				+ "e' da considerarsi un esempio di software<br>"
				+ "di refertazione di ecografie mammarie.<br>";

		s +=  "</center></body></html>";
		
		JOptionPane.showMessageDialog(Jedecma.mf, s, "About",
				JOptionPane.INFORMATION_MESSAGE);
	}

	String getProgInfo() {
		return "Jedecma " + Jedecma.progVers + " " + Jedecma.progBuild + " "
				+ "serial " + Jedecma.ak.getSerialNumber();
	}

	String getCopyright() {
		return "Copyright (C) " + Uti1.leftSet(Jedecma.progBuild, 4, ' ')
				+ " Stefano Marchetti - mrcsfn@inwind.it";
	}

} // end About class
