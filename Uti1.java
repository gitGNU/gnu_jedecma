/*  
 * Uti1.java - simple utilities collection
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


/*
 * Notice: in most cases these are quite rudimentary and naive functions
 */

package jedecma;

import java.util.*;  // x Date
import java.text.*;
import javax.swing.text.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.geom.*;
import java.io.*;

public class Uti1 {
	
  public static String getWrkDir () {
	String path = Jedecma.param.getProperty("wrkpath");
	String dirSep = System.getProperty("file.separator");
	if (path.length() == 0 ) {
		path = "." + dirSep;
	}
	if (! path.substring(path.length()).equals(dirSep)) {
		path += dirSep;
	}
	return path;
  }
	
  public static Font myFont(String font) {
   Font newFont = null;
   String s = font.trim();
   String name = "";
   String style = "";
   int size = 0;
   int p = s.indexOf(",");
   if (p == -1) { 
     name = s; 
   } else {
     name = s.substring(0,p).trim();
     int p1 = s.lastIndexOf(",");
     if (p1 == -1) {
       style = s.substring(p+1).trim();
     } else {
       style = s.substring(p+1, p1);
       String s1 = s.substring(p1+1).trim();
       if (! s1.equals("") ) {
         size = Integer.parseInt(s1);
       }
     }
   }
   if (! name.equals("")) {
     int fontStyle = Font.PLAIN;
     int fontSize = 8;
     if (style.equals("BOLD")) 
       fontStyle = Font.BOLD;
     else if (style.equals("ITALIC"))
       fontStyle = Font.ITALIC;
     
     if (size > 0) { fontSize = size; }
     newFont = new Font(name, fontStyle, size);
   }
   return (newFont);
 }
  
public static String dateNull () {
    if (Jedecma.dbmgr.getDbType() == JDBCMgr.MYSQL) {
    	return "'0000-00-00'";
    }
     return null;
  }
  
  public static boolean isDateNull (java.util.Date d) {
    if ( (d == null) ) { return true; }
    if ( Uti1.date2String(d).equals("0000-00-00") ) { return true; }
    if ( Uti1.date2String(d).equals("00000000") ) { return true; }
    if ( Uti1.date2String(d).equals("00/00/0000") ) { return true; }
    if ( Uti1.date2String(d).equals("00-00-0000") ) { return true; }
    if ( Uti1.date2String(d).equals("01/01/0001") ) { return true; }
    if ( Uti1.date2String(d).equals("01-01-0001") ) { return true; }
    return false;
  }
  
  public static String date2FmtString (java.util.Date d, String fmt) {
	 String datestring = "";
	 if ( d != null ) { 
	    SimpleDateFormat formatter = new SimpleDateFormat(fmt);
	    try {    
	      datestring = (formatter.format(d));
	    } catch (IllegalArgumentException iae) { 
	      datestring = "";
	    }
	 }
	 return (datestring);

  }
  
  public static String date2String (java.util.Date d) {
  // trasforma una data in stringa
    String datestring = "";
    if ( d == null ) { return(""); }
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    try {    
      datestring = (formatter.format(d));
    } catch (IllegalArgumentException iae) { 
      datestring = "";
    }
      return (datestring);
  }

  public static java.util.Date fmtString2Date (String s, String fmt) {
	  // trasforma una stringa formattata in Date
	  SimpleDateFormat sdf1;
	     sdf1 = new SimpleDateFormat(fmt);
	     try {
	       return sdf1.parse(s);
	     } catch (ParseException pe) {
	       return null;
	     }
  }
  
  public static java.util.Date string2Date (String s) {
  // trasforma una stringa in data
     if ( s.indexOf("-") >= 0 || s.indexOf("/") >= 0 || s.indexOf(".") >= 0 ) {
     // data delimitata  
       if (s.length() == 8) {
         String ys = s.substring(6, 8);
         int y = Integer.parseInt(ys);
         if ( y >= 70 ) {
           String w = s.substring(0, 6);
	   s = w + "19" + ys;
         } else {
	   String w = s.substring(0, 6);
	   s = w + "20" + ys;
         }
       }
       if ( s.length() != 10 ) { return (null); }
       String ws = s.substring(2, 3); 
       if ( "-/.".indexOf(ws) >=0 ) {
         String ys, ms, ds;
         ys = s.substring(6, 10);
         ms = s.substring(3, 5);
         ds = s.substring(0, 2);
         s = ds + "/" + ms + "/" + ys;
       } else {
         String ys, ms, ds;
         ys = s.substring(0, 4);
         ms = s.substring(5, 7);
         ds = s.substring(8, 10);
         s = ds + "/" + ms + "/" + ys;
       }
     } else {
     // data non delimitata
       if ( s.length() >= 6 || s.length() <= 8 ) {
         if (s.length() == 6) {
           String ys = s.substring(4, 6);
	   int y = Integer.parseInt(ys);
	   if ( y >= 70 ) {
	     String w = s.substring(0, 4);
	     s = w + "19" + ys;
	   } else {
	     String w = s.substring(0, 4);
	     s = w + "20" + ys;
	   }
         }
         if ( s.length() != 8 ) { return (null); }
         String ys, ms, ds;
         ys = s.substring(4, 8);
         ms = s.substring(2, 4);
         ds = s.substring(0, 2);
         s = ds + "/" + ms + "/" + ys;
       }
     }
     { String ys, ms, ds;
       ys = s.substring(6, 10);
       ms = s.substring(3, 5);
       ds = s.substring(0, 2);
       int y = Integer.parseInt(ys);
       if (y == 0) { return (null); }
       //int d = Integer.parseInt(ds);
       //if (d <= 0 || d >31) { return (null); }
       //int m = Integer.parseInt(ms);
       //if (m <= 0 || m >12) { return (null); }
     }
     SimpleDateFormat sdf1;
     sdf1 = new SimpleDateFormat("dd/MM/yyyy");
     try {
       return (sdf1.parse(s));
     } catch (ParseException pe) {
     }

     return (null);
  }
  
  
  public static String date2Ansi (java.util.Date d) {
	// trasforma una data da Date a stringa 'yyyy-mm-dd'
	  String datestring = "";
	  if ( d != null ) { 
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    try {    
	      datestring = (formatter.format(d));
	    } catch (IllegalArgumentException iae) { 
	    }
	  }
	  return (datestring);  
  }
  
  public static String date2Ansi (String d) {
  // trasforma una data da stringa 'dd/mm/yyyy' a stringa 'yyyy-mm-dd'
    return date2Ansi(string2Date(d));
  }
  
  public static String rset (String s, int l) {
  // inserisce spazi in testa
    while (s.length() < l) {
      s = " " + s;
    }
    return (s);
  }
    
  public static String leftSet(String s, int l, char c) {
    String ws = s + replicate(l, c);
    return ws.substring(0,l);
  }
  
  public static String rightSet(String s, int l, char c) {
    String ws = replicate(l, c) + s;
    int wn = ws.length() - l;
    return ws.substring(wn);
  }
  
  public static String replicate(int l, char c) {
    String ws = "";
    for (int i = 1; i <= l; i++) {
      ws += c;
    }
    return ws;
  }
  
  public static String uniqSpaces(String s) {
    String c, ws = "";
    boolean inSpaces = false;
    for ( int i = 0; i < s.length(); i++) {
      c = s.substring(i, i+1);
      if ( c.equals(" ") ) {
        if ( inSpaces == true) { continue; }
        else { inSpaces = true; }
      } else { inSpaces = false; }
      ws += c;
    }
    return ws;
  }
  
  public static void error (String e, boolean a, Component parent) {
     String err = e;
     boolean abort = a;
     // non usare html in quanto alcune stringhe possono contenere caratteri incompatibili
     JOptionPane.showMessageDialog(parent, err, Jedecma.localMessagesBundle.getString("ERROR") + ": Jedecma " + Jedecma.progVers, JOptionPane.ERROR_MESSAGE);
     if ( abort ) { System.exit(1); }
  }
  
  public static void error (String e, boolean a) {
    error (e, a, null);
  }
  
  public static int txtYN(String text, String title, int type, Component parent) {
	    Object[] options = {Jedecma.localMessagesBundle.getString("CB_NO"), Jedecma.localMessagesBundle.getString("CB_YES")};
	    JTextArea textArea = new JTextArea(20, 80);
    	textArea.setText(text);
    	textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    	JScrollPane scrollPane = new JScrollPane(textArea);
	    return ( JOptionPane.showOptionDialog(parent, 
	    		scrollPane,
	      title,
	      JOptionPane.YES_NO_OPTION,
	      //JOptionPane.QUESTION_MESSAGE,
	      type,
	      null,     //don't use a custom Icon
	      options,  //the titles of buttons
	      options[0])); //default button title
	  }

  public static int msgYN(String text, String title, int type, Component parent) {
    Object[] options = {Jedecma.localMessagesBundle.getString("CB_NO"), Jedecma.localMessagesBundle.getString("CB_YES")};
    return ( JOptionPane.showOptionDialog(parent, 
      text,
      title,
      JOptionPane.YES_NO_OPTION,
      //JOptionPane.QUESTION_MESSAGE,
      type,
      null,     //don't use a custom Icon
      options,  //the titles of buttons
      options[0])); //default button title
  }
 
  public static String escapeNl(String s) {
	  String s1, s2;
	    int p; 
	    s1 = s;
	    s2 = "";
	    while ( (p = s1.indexOf('\n')) > -1 ) {
	    	s2 += s1.substring(0,p) + " ";
	        s1 = s1.substring(p + 1);
	    }	
	    s2 += s1;
	    return (s2);
  }
  
  
  public static String escape(String s) {
    String s1, s2;
    int p; 
    // toglie backslash per evitare sequenze di escape indesiderate
    s1 = s;
    s2 = "";
    while ( (p = s1.indexOf("\\")) > -1 ) {
      s2 += s1.substring(0,p);
      s1 = s1.substring(p + 1);
     }
    s2 += s1;
    // raddoppia il carattere '
    s1 = s2; 
    s2 = "";
    while ( (p = s1.indexOf("'")) > -1 ) {
      s2 += s1.substring(0,p) + "''";
      s1 = s1.substring(p + 1);
    }
    s2 += s1;
    return (s2);
  } 
  
  final static Paper setA4 () {
    double cm = 72/2.54;
    double w = 21*cm, h = 29.7*cm;
    // double top = 2*cm, bottom = 2*cm, left = 1.5*cm, right = 1.5*cm;
    double top = cm, bottom = cm, left = 1.3 * cm, right = cm;

    Paper A4 = new Paper();
    A4.setSize(w,h);
    A4.setImageableArea(left, top, w-left-right, h-top-bottom);
    return A4;
 }


  public static Properties readProp() {
  System.out.println("readProp()");
    String parmfile = "jedecma.prm";
    Properties defaultProps = new Properties();
    try {
      FileInputStream in = new FileInputStream(parmfile);
      try {     
        defaultProps.load(in);
        in.close();
	return (defaultProps);
      } catch (IOException e) { }
    } catch (FileNotFoundException e) { 
      System.out.println("properties file: " + parmfile + " not found"); 
      defaultProps.put("logo1", "");
      defaultProps.put("logo2", "");
      defaultProps.put("splash", "splash.jpg");
      defaultProps.put("wrkpath", "");
      defaultProps.put("drname", "");
      defaultProps.put("dbname", "jdbc:derby:jedecma-db");
      defaultProps.put("jdbcdriver", "org.apache.derby.jdbc.EmbeddedDriver");
      defaultProps.put("dbuser", "jedecma");
      defaultProps.put("dbpass", "");
      defaultProps.put("dbtype", "0");
      defaultProps.put("txt_y0", "1");
      defaultProps.put("data_font", "courier,BOLD,9");
      defaultProps.put("diag_font", "helvetica,PLAIN,9");
      defaultProps.put("label_font", "helvetica,BOLD,9");    
      // font std per MyJlabel: family=Dialog,name=Dialog,style=bold,size=12
      defaultProps.put("jlabel_font", "");
      defaultProps.put("jtextfield_font", "");
      defaultProps.put("jtextarea_font", "");
      defaultProps.put("jcombobox_font", "");
      defaultProps.put("jtable_font", "");
      defaultProps.put("language", "");
      defaultProps.put("country", "");
      defaultProps.put("userkey", Jedecma.ak.genItKey());
      defaultProps.put("imgtmp", ""); // su macchina ecografica
      defaultProps.put("imgpath", ""); // su server
      defaultProps.put("eulashown", "");
      try {
        FileOutputStream out = new FileOutputStream(parmfile);
        try {
          defaultProps.store(out, "--- " + parmfile + " ---");
          out.close();
	  return (defaultProps);
        } catch (IOException e1) { }
      } catch (FileNotFoundException e2) { 
        System.out.println("annot write properties file: " + parmfile);
	return (null);
      }
    }
    return (defaultProps);
  }

  public static String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');
    if (i > 0 &&  i < s.length() - 1) {
      ext = s.substring(i+1);
    }
    return ext;
  }

 public static void bldConst(GridBagConstraints gbc, int gx, int gy, // 1^ costruttore 
      int gw, int gh,
      int wx, int wy,
      int an, int fi) {
	gbc.gridx = gx;
	gbc.gridy = gy;
	gbc.gridwidth = gw;
	gbc.gridheight = gh;
	gbc.weightx = wx;
	gbc.weighty = wy;
	gbc.anchor = an;
	gbc.fill = fi;
	gbc.insets = new Insets (2, 2, 2, 2);
  }
  
  public static void bldConst(GridBagConstraints gbc, int gx, int gy,  // 2^ costruttore
      int gw, int gh,
      int wx, int wy) {
	gbc.gridx = gx;
	gbc.gridy = gy;
	gbc.gridwidth = gw;
	gbc.gridheight = gh;
	gbc.weightx = wx;
	gbc.weighty = wy;
	gbc.anchor = GridBagConstraints.NORTHWEST;
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.insets = new Insets (2, 2, 2, 2);
   } 

  public static boolean dtCmp(java.util.Date d1, java.util.Date d2) {
	  if (d1 == null && d2 == null) {
		  return true;
	  }
	  if (d1 != null && d2 != null) {
		  if (d1.equals(d2)) {
			  return true;
		  }
	  }
	  return false;
  }
	
} // end_of_class Uti1

class FixedSizePlainDocument extends PlainDocument {

	private static final long serialVersionUID = 1L;
	int maxSize;

	public FixedSizePlainDocument(int limit) {
		maxSize = limit;
	}

	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		if ((getLength() + str.length()) <= maxSize) {
			super.insertString(offs, str, a);
		} else {
			throw new BadLocationException(
					"Insertion exceeds max size of document", offs);
		}
	}
} // end FixedSizePlainDocument class

class CtrTextField implements FocusListener {
	
	public void focusGained(FocusEvent evt) {
		// final JTextComponent c = (JTextComponent)evt.getSource();
		final MyJTextField c = (MyJTextField) evt.getSource();
		if (evt.isTemporary()) {
			return;
		}
		String s = c.getText();
		c.setSelectionStart(0);
		c.setSelectionEnd(s.length());
	}

	public void focusLost(FocusEvent evt) {
		// final JTextComponent c = (JTextComponent)evt.getSource();
		final MyJTextField c = (MyJTextField) evt.getSource();
		if (evt.isTemporary()) {
			return;
		}
		if (!c.hasValidContent()) {
			c.requestFocus();
		}
	}
} // end CtrTextField class


class MyJTextField extends JTextField {

	private static final long serialVersionUID = 1L;
	int type;
	float[] range; // se definito, hasValidContent usa questi valori; altrimenti
					// fare overriding di hasValidContent

	MyJTextField(int nrChr, String v, float[] r, int t) {
		super();
		setColumns(nrChr);
		range = r;
		type = t; // 1=int; 2=float; 6=stringa-data; 7=stringa maiuscola
		if (Jedecma.jTextFieldFont != null) {
			setFont(Jedecma.jTextFieldFont);
		}
		setDocument(new FixedSizePlainDocument(nrChr));
		setText(v);
	}

	/*
	 * You should not override Component#isValid() since it is used to determine
	 * if the component layout is valid by the painting system. ... Rename it to
	 * hasValidInteger() or some such.
	 * 
	 * // esempio di overriding di hasValidContent() in fase di
	 * dichiaraz/inizializz. del campo //MyJTextField datums = new MyJTextField
	 * (10) { public boolean hasValidContent() { final MyJTextField tf = this;
	 * String ws = tf.getText(); if ( ws.length() > 0 ) { java.util.Date date =
	 * Uti1.string2Date(ws); if (date == null) { return false; }
	 * tf.setText(Uti1.date2String(date)); } return true; } };
	 */

	public boolean hasValidContent() { // validazione default del campo in base
										// al suo tipo
		// + riformattazione campo
		String ws = getText();
		switch (type) {
		case 1: { // int
			int i = 0;
			if (ws.length() > 0) {
				try {
					i = Integer.parseInt(ws);
					if (range.length > 0) {
						if (i < range[0] || i > range[1]) {
							return (false);
						}
					}
				} catch (NumberFormatException e) {
					return (false);
				}
			}
			setText(String.valueOf(i));
		}
			;
			break;
		case 2: { // float
			float f = 0;
			if (ws.length() > 0) {
				try {
					f = Float.parseFloat(ws);
					if (range.length > 0) {
						if (f < range[0] || f > range[1]) {
							return (false);
						}
					}
				} catch (NumberFormatException e) {
					return (false);
				}
			}
			setText(String.valueOf(f));
		}
			;
			break; // float
		case 6: { // stringa-data
			String s = "";
			if (ws.length() > 0) {
				java.util.Date date = Uti1.string2Date(ws);
				if (date == null) {
					return (false);
				} else {
					setText(Uti1.date2String(date));
				}
			}
		}
			;
			break; // stringa-data
		case 7: { // stringa maiuscola
			setText(ws.toUpperCase());
		}
			;
			break;
		case 8: { // stringa-data MM/dd/yyyy
			if (ws.length() > 0) {
				java.util.Date date = Uti1.fmtString2Date(ws, "MM/dd/yyyy");
				if (date == null) {
					return (false);
				} else {
					setText(Uti1.date2FmtString(date, "MM/dd/yyyy"));
				}
			}
		}
			;
			break; // stringa-data MM/dd/yyyy
		}
		return (true);
	}
} // end MyJTextField class

 
class MyJComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;
	String[] items; // nel formato "VALORE=DESCRIZ","",..

	MyJComboBox(String[] itm, String v) {
		items = itm;
		int si = 0;
		int csize = 0;
		if (Jedecma.jComboBoxFont != null) {
			setFont(Jedecma.jComboBoxFont);
		}
		for (int i = 0; i < items.length; i++) {
			String item = items[i];
			addItem(item);
			int b = item.indexOf("=");
			String iv = item;
			if (b > -1) {
				iv = item.substring(0, b);
			}
			if (v.equals(iv)) {
				si = i;
			}
			if (item.length() > csize) {
				csize = item.length();
			}
		}
		setSelectedIndex(si);
	}

	/*
	 * questo metodo commentato: in win non visualizzava correttamente combobox
	 * public boolean isValid() {
	 * //System.out.println("inputValue= "+getInpValue()); return (true); }
	 */
	public String getInpValue() {
		int si = getSelectedIndex();
		String ws = "0";
		if (si >= 0 && si <= items.length) {
			ws = items[si];
			int b = ws.indexOf("=");
			if (b > -1) {
				ws = ws.substring(0, b);
			}
		}
		return (ws);
	}

} // end MyJComboBox class
 
class MyTxtPageSet {
	int formLen, pag, rCount;
	String[] pline;
	String title, header;
	PageFormat pf;
	Book book = new Book();

	public MyTxtPageSet() {
		title = "";
		header = "";
		formLen = 62;
		rCount = 0;
		pline = new String[formLen];
		pf = new PageFormat();
		pf.setPaper(Uti1.setA4());
		pf.setOrientation(PageFormat.PORTRAIT);
	}

	public void writeLine(String line) {

		if (line.equals("*END*")) {
			book.append(new PrintablePage(pline), pf);
			return;
		}

		if (rCount + 1 > (formLen - 1)) {
			if (rCount > 0) {
				pline[rCount++] = "segue...";
			}

			book.append(new PrintablePage(pline), pf);

			for (int i = 0; i < rCount; i++) {
				pline[i] = null;
			}
			rCount = 0;
		}

		if (rCount == 0) {
			pageHeader();
		}

		pline[rCount++] = line;
		return;
	}

	public void setHeader(String t, String h) {
		title = t;
		header = h;
	}

	public void pageHeader() {
		pag++;
		String p = "pag." + Uti1.rightSet(String.valueOf(pag), 4, ' ');
		// String title = "";
		int i = 110 - (p.length() + title.length());
		if (i < 0) {
			i = 0;
		}
		title = title + Uti1.replicate(i, ' ') + p;
		pline[rCount++] = title;
		pline[rCount++] = header;
		pline[rCount++] = Uti1.replicate(110, '-');
		return;
	}

} // / end_of_PageSet

class PrintablePage implements Printable {
	String[] aline;

	public PrintablePage(String[] a) {
		aline = new String[a.length];
		for (int i = 0; i < a.length; i++) {
			aline[i] = a[i];
		}
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		int result = Printable.NO_SUCH_PAGE;

		Graphics2D g2d = (Graphics2D) g;
		final int POINTS_PER_INCH = 72;
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		g2d.setPaint(Color.black);
		Point2D.Double pen = new Point2D.Double(0.01 * POINTS_PER_INCH,
				0.01 * POINTS_PER_INCH);
		Font font1 = new Font("courier", Font.PLAIN, 8);
		g2d.setFont(font1);
		int intl = POINTS_PER_INCH / 6;
		double t1 = 0.00 * POINTS_PER_INCH;

		pen.x = t1;
		pen.y = intl;

		int lcount = 0;
		for (int i = 0; i < aline.length; i++) {
			String l = aline[i];
			if (l != null) {
				g2d.drawString(l, (int) t1, (int) pen.y);
				pen.y += intl;
				lcount++;
			}
		}

		if (lcount > 0) {
			result = Printable.PAGE_EXISTS;
		}
		return result;
	}
}
 
class MyJlabel extends JLabel {

	private static final long serialVersionUID = 1L;

	MyJlabel(String text) {
		super(text);
		if (Jedecma.jLabelFont != null) {
			setFont(Jedecma.jLabelFont);
		}
	}
 
 }
 
