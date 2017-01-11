/*  
 * DsplInfo.java - environment info window
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

import javax.swing.*;

class DsplInfo extends JPanel {

	private static final long serialVersionUID = 1L;

DsplInfo () {
      String s = "<html><body><center>Jedecma ";
      s	+= Jedecma.progVers + " " + Jedecma.progBuild + " "
      + "serial " + Jedecma.ak.getSerialNumber() + "<br>"
      + "use allowed until " + Uti1.date2Ansi(Jedecma.ak.getExpDate()) + "<br>"
      + "lang: " + Jedecma.language + "." + Jedecma.country + "<br><br>"
      + "Environment info<br>"
      + "java.version: " + System.getProperty("java.version") + "<br>"
      + "java.vm.name: " + System.getProperty("java.vm.name") + "<br>"
	  + "java.runtime.name: " + System.getProperty("java.runtime.name") + "<br>"
	  +"os.name: " + System.getProperty("os.name") + "<br>"
	  + "os.arch: " + System.getProperty("os.arch") + "<br>"
	  + "os.version: " + System.getProperty("os.version") + "<br>"
	  + "user.name: " + System.getProperty("user.name") + "<br>"
      + "</center></body></html>";
      JOptionPane.showMessageDialog(Jedecma.mf, s, "Info", JOptionPane.INFORMATION_MESSAGE);
  }

} // end DsplInfo class
