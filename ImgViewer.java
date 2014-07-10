/*  
 * ImgViewer.java - image file viewer
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

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class ImgViewer extends JDialog {	

	private static final long serialVersionUID = 1L;

	ImgViewer (String s, EcoEdit parent) {
		super(parent, s, false);
		ImageIcon img = new ImageIcon(s);
		ScrollablePicture p = new ScrollablePicture(img);
		JScrollPane scroll = new JScrollPane(p);
		add(scroll);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 		
		pack();
	    setVisible(true);
	    toFront(); // brings the window to the front
	}
	
	class ScrollablePicture extends JLabel {

		private static final long serialVersionUID = 1L;

		ScrollablePicture (ImageIcon i) {
		    super (i);
		  }
	
	}
}
