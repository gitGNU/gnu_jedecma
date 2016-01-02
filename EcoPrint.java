/*  
 * EcoPrint.java - prints examination reports
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

import java.awt.*;
import java.awt.print.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.sql.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;


public class EcoPrint extends Component { // questa sciocchezza e' per drawImage!
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
Book book;
  EcoExam exa;
  int nrexam;
  private Component me;

  EcoPrint() {
     me = this;
  }
  
  
  public int exe (int nr) {
    nrexam = nr;  

    if (nr == 0) {
      return (1);
    } 

    exa = new EcoExam();
    int rc = exa.readExam(Jedecma.dbmgr, nrexam);
    if (rc != 0) {
      return (2);
    }
    
    book = new Book();

 	new EcoPag1(exa);

    PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
    aset.add(MediaSizeName.ISO_A4);
    aset.add(new Copies(1));
    PrinterJob printerJob = PrinterJob.getPrinterJob();
    PrintService[] pservices =PrinterJob.lookupPrintServices();
    if (pservices.length > 0) {
      System.out.println("selected printer "+ pservices[0]);
      printerJob.setPageable(book);
      try {
	printerJob.setPrintService(pservices[0]);
	if  (printerJob.printDialog(aset)) {
	  printerJob.print(aset);
	}
      } catch (PrinterException pe) {
	System.err.println (pe);
      }  
    }    

    return (0);
  
  }
  
 class EcoPag1 implements Printable {
   PageFormat pf;
   EcoExam exa;
   AnaDat ana;
   EcoTxt ecoTxt;

   EcoPag1 (EcoExam e) {
     exa = e;
     ecoTxt = new EcoTxt();
     ecoTxt.readEcoTxt(Jedecma.dbmgr, exa.numarc);
     ana = new AnaDat();
     String dn = Uti1.date2Ansi(Uti1.date2String(exa.datnas));
     int ancode = ana.getAnaUid(Jedecma.dbmgr, exa.cognom, dn, exa.ecbprv);
     if (ancode > 0) {
       int rc = ana.readAna(Jedecma.dbmgr, ancode);
       if (rc != 0) {
         Uti1.error(Jedecma.localMessagesBundle.getString("PAT_NOT_FOUND") + String.valueOf(ancode), false);
       }
       //System.out.println("anaddr=" + ana.anaddr);
     }
     pf = new PageFormat();
     pf.setPaper(Uti1.setA4());
     pf.setOrientation(PageFormat.PORTRAIT);
     book.append(this, pf);
   }
  
   public int print(Graphics g,PageFormat pageFormat, int pageIndex) throws PrinterException {
     int result = Printable.NO_SUCH_PAGE;
     //System.out.println("pageIndex=" + pageIndex);
     String line;
  
     Graphics2D g2d = (Graphics2D) g;
     final int POINTS_PER_INCH = 72;
     g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
     g2d.setPaint(Color.black);
     Point2D.Double pen = new Point2D.Double (0.01 * POINTS_PER_INCH, 0.01 * POINTS_PER_INCH);
     Font font1 = new Font ("courier", Font.BOLD, 9);     // font per i dati
     {
       String s = Jedecma.param.getProperty("data_font");
       if (! s.equals("")) {
	 Font f = Uti1.myFont(s);
	 if (f != null) { font1 = f; }
       }
     }
     
     Font font2 = new Font ("helvetica", Font.PLAIN, 9);   // font per la diagnosi
     {
       String s = Jedecma.param.getProperty("diag_font");
       if (! s.equals("")) {
	 Font f = Uti1.myFont(s);
	 if (f != null) { font2 = f; }
       }
     }
     
     Font font3 = new Font ("helvetica", Font.PLAIN, 9);   // font per le diciture fisse
     {
       String s = Jedecma.param.getProperty("label_font");
       if (! s.equals("")) {
	 Font f = Uti1.myFont(s);
	 if (f != null) { font3 = f; }
       }
     }

     
     FontRenderContext frc = g2d.getFontRenderContext();
     TextLayout tl1;
     g2d.setFont (font1);
     double intl = POINTS_PER_INCH / 6;

     String testo = ecoTxt.text;

     float inct = POINTS_PER_INCH / 10;
     float t1 =  2 * inct; 
     float t2 = 15 * inct;
     float t3 = 42 * inct;
     float t4 = 55 * inct;

     pen.x = 1;
     pen.y = 1;

// se il file logo contiene "[" dopo tale carattere si specificano, separati da "," x0, y0, larghezza e altezza, chiusi da "]"     
     if ( Jedecma.logo1 != null ) {
       int x0 = (int) pen.x;
       int y0 = (int) pen.y;
       int wd = Jedecma.logo1.getWidth(me);
       int ht = Jedecma.logo1.getHeight(me);
       String logo_file1 = Jedecma.param.getProperty("logo1");
       int p = logo_file1.indexOf("[");
       if (p > -1) {
         String s1 = logo_file1.substring(p+1).trim();
	 int p1 = s1.indexOf(",");
	 if (p1 > -1) {
	   String s2 = s1.substring(0,p1).trim();
           x0 = Integer.parseInt(s2);
	   s1 = s1.substring(p1+1).trim();
	   int p2 = s1.indexOf(",");
	   if (p2 > -1) {
	     s2 = s1.substring(0,p2).trim();
	     y0 = Integer.parseInt(s2);
	     s1 = s1.substring(p2+1);
	     int p3 = s1.indexOf(",");
	     if (p3 > -1) {
	       s2 = s1.substring(0,p3).trim();
	       wd = Integer.parseInt(s2);
	       s1 = s1.substring(p3+1);
	       int p4 = s1.indexOf("]");
	       if (p3 > -1) {
                 s2 = s1.substring(0,p4).trim();
	         ht = Integer.parseInt(s2);
	       }
	     }
	   }
         }
       }     
       System.out.println("logo1= "  + Jedecma.logo1 + " x0=" + x0 + " y0=" + y0 + " wd=" + wd + " ht=" + ht);
       //AffineTransform at = new AffineTransform(1f,0f,0f,1f,pen.x,(float) pen.y);
       //boolean rv = g2d.drawImage (Jedecma.logo1, at, null);
       ////boolean rv = g2d.drawImage (Jedecma.logo1, x0, y0, wd, ht, null); // in punti per inch
       double ppmm = POINTS_PER_INCH/25.4; // punti per mm
       boolean rv = g2d.drawImage (Jedecma.logo1, (int) (x0 * ppmm), (int) (y0 * ppmm), (int) (wd * ppmm), (int) (ht * ppmm), null);
       System.out.println ("drawImage=" + rv);
     }

// se il file logo contiene "[" dopo tale carattere si specificano, separati da "," x0, y0, larghezza e altezza, chiusi da "]"     
     if ( Jedecma.logo2 != null ) {
       int x0 = (int) pen.x;
       int y0 = (int) pen.y;
       int wd = Jedecma.logo2.getWidth(me);
       int ht = Jedecma.logo2.getHeight(me);
       String logo_file2 = Jedecma.param.getProperty("logo2");
       int p = logo_file2.indexOf("[");
       if (p > -1) {
         String s1 = logo_file2.substring(p+1).trim();
	 int p1 = s1.indexOf(",");
	 if (p1 > -1) {
	   String s2 = s1.substring(0,p1).trim();
           x0 = Integer.parseInt(s2);
	   s1 = s1.substring(p1+1).trim();
	   int p2 = s1.indexOf(",");
	   if (p2 > -1) {
	     s2 = s1.substring(0,p2).trim();
	     y0 = Integer.parseInt(s2);
	     s1 = s1.substring(p2+1);
	     int p3 = s1.indexOf(",");
	     if (p3 > -1) {
	       s2 = s1.substring(0,p3).trim();
	       wd = Integer.parseInt(s2);
	       s1 = s1.substring(p3+1);
	       int p4 = s1.indexOf("]");
	       if (p3 > -1) {
                 s2 = s1.substring(0,p4).trim();
	         ht = Integer.parseInt(s2);
	       }
	     }
	   }
         }
       }     
       System.out.println("logo2= "  + Jedecma.logo2 + " x0=" + x0 + " y0=" + y0 + " wd=" + wd + " ht=" + ht);
       ///boolean rv = g2d.drawImage (Jedecma.logo2, x0, y0, wd, ht, null);
       double ppmm = POINTS_PER_INCH/25.4; // punti per mm
       boolean rv = g2d.drawImage (Jedecma.logo2, (int) (x0 * ppmm), (int) (y0 * ppmm), (int) (wd * ppmm), (int) (ht * ppmm), null);
       System.out.println ("drawImage=" + rv);
     }

     pen.x = t1;
     {
       int txt_y0 = 1;
       String s = Jedecma.param.getProperty("txt_y0");
       if (! s.equals("")) {
         txt_y0 =  Integer.parseInt(s);
       }
       pen.y = txt_y0 * intl;
     }

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_TITLE1"), font3, frc);
       Rectangle2D bounds = tl1.getBounds();
       double ww = bounds.getWidth();
       double dx = t1 + (70 * inct - ww)  / 2.0;
       tl1.draw(g2d, (float) dx, (float) pen.y);
     }
        
     pen.y += 2 * intl;
          
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_EXAM_NR"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);

       line = Uti1.rightSet(String.valueOf(exa.numarc), 6, ' ');
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }
     
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_EXAM_DATE"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
     
       line = Uti1.date2String(exa.datesa);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y += intl;
     
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_NAME"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
     
       line = exa.cognom;
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_BIRTH_DATE"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
     
       String ws = " ";
       if (Uti1.date2String(exa.datnas).length()>0) {
         ws = Uti1.date2String(exa.datnas);
       }
       line = ws;
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
       
     pen.y += 2 * intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_ADDRESS"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);

       line = ana.anaddr + " " + ana.ancapc + " " + ana.anloca + " (" + ana.anrprv + ")";
       line = Uti1.leftSet(line, 75, ' '); 
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }
     pen.y += intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_JOB"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       line = Uti1.leftSet(getProfDes(ana.anprof), 30, ' ');
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }
     
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_TEL"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       line = Uti1.leftSet(ana.anteln, 15, ' ');
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  2* intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_ECO_DIAG"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
     }
      
     pen.y += intl;

     if (testo.length() == 0 ) {
       testo = " ";
     }

     g2d.setFont (font2);
                    
     //--- Set the width of the TextLayout box
     double width =  7 * POINTS_PER_INCH;

     //--- Create the TextLayout object
     TextLayout layout; 
     TextLayout justifyLayout;
     //--- Create a Vector to temporarily store each line of text
     Vector lines = new Vector ();  

     JTextArea textArea = new JTextArea(testo);

     Element paragraph = textArea.getDocument().getDefaultRootElement();
     int contentCount = paragraph.getElementCount();
       
     for (int i=0; i<contentCount; i++) {
       Element e = paragraph.getElement(i);
       int rangeStart = e.getStartOffset();
       int rangeEnd = e.getEndOffset();
       try {
	 String textline = textArea.getText(rangeStart, rangeEnd-rangeStart);
         AttributedString paragraphText = new AttributedString (textline);
       //--- Set the font for this text
         paragraphText.addAttribute (TextAttribute.FONT, font2);
         paragraphText.addAttribute (TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT);
         LineBreakMeasurer lineBreaker = new LineBreakMeasurer (paragraphText.getIterator(), new FontRenderContext (null, true, true)); 
         while ((layout = lineBreaker.nextLayout ((float) width)) != null) {
           lines.add (layout);
         }
       } catch (BadLocationException ex) { }
     } // fine ciclo for

     double maxTextSize = 14 * intl;
     double textSizeCount = 0.0;
       //--- Scan each line of the paragraph and justify it except for the last line
     for (int i = 0; i < lines.size(); i++) { 
       //--- Get the line from the vector
       layout = (TextLayout) lines.get (i);
       //--- Check for the last line. When found print it
       //--- with justification off
       if (i != lines.size () - 1) {
         Rectangle2D bounds = layout.getBounds();
	 double ww = bounds.getWidth();
	 if ( ww > width * 0.75 ) {
           justifyLayout = layout.getJustifiedLayout ((float) width);
	 } else {
           justifyLayout = layout;
	 }
       } else {
         justifyLayout = layout;
       }
       if (textSizeCount < maxTextSize) {
        //--- Align the Y pen to the ascend of the font
         if ( i > 0) { 
           pen.y += justifyLayout.getAscent(); 
	   textSizeCount += justifyLayout.getAscent();
         }
        //--- Draw the line of text
         justifyLayout.draw (g2d, t1, (float) pen.y);
        //--- Move the pen to the next position adding the descent and
        //--- the leading of the font
         pen.y += justifyLayout.getDescent() + justifyLayout.getLeading();
	 textSizeCount += justifyLayout.getDescent() + justifyLayout.getLeading();
       }
     }
     double nint = Math.round(pen.y/intl + 0.999);
     pen.y = nint * intl;
     if (textSizeCount < (maxTextSize - intl) ) { pen.y += 1 * intl; }
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_PER_MALIGN"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {Jedecma.localMessagesBundle.getString("REP_NA"), "0%", "25%", "50%", "75%", "100%"};
       line = s[exa.tmmalg];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_FAM_PATHOL"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       line = Uti1.leftSet(exa.fampat, 20, ' ');
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 
      	
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_WEIGHT"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       line = String.valueOf(exa.pesopz);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_HEIGHT"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       line = String.valueOf(exa.altzpz);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_NR_CHILDREN"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       line = String.valueOf(exa.nmrfgl);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_SUCKLE"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
             };
       line = s[exa.allatt];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_AGE_MENARCHE"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       line = String.valueOf(exa.menarc);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_AGE_MENOP"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       line = String.valueOf(exa.menopa);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MENSTR"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_REG"),
    		   Jedecma.localMessagesBundle.getString("REP_IRR")
    		 };
       line = s[exa.mstrzn];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_LST_CYCLE"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String ws = " ";
       if (Uti1.date2String(exa.datums).length()>0) {
         ws = Uti1.date2String(exa.datums);
       }
       line = ws;       
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MASTODYNIA"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
             };
       line = s[exa.mastod];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MASTODYNIA_SITE"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       line = Uti1.leftSet(exa.mastse, 5, ' ');
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_GLND_THICK_1"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       line = String.valueOf(exa.spsghi) + " / " + String.valueOf(exa.spsgh1);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MAM_ECOGEN"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_MAM_ECOGEN_NONE"),
    		   Jedecma.localMessagesBundle.getString("REP_MAM_ECOGEN_LOW"),
    		   Jedecma.localMessagesBundle.getString("REP_MAM_ECOGEN_MID"),
    		   Jedecma.localMessagesBundle.getString("REP_MAM_ECOGEN_HIGH")
             }; 
       line = s[exa.egmamm];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_PREGL_ADIP_TISS"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       line = String.valueOf(exa.taprgh);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_RETRO_ADIP_TISS"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       line = String.valueOf(exa.taregh);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_NIPPLES"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NIPPLES_NORM"),
    		   Jedecma.localMessagesBundle.getString("REP_NIPPLES_RETR")
    		   };
       line = s[exa.tcapez];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_INTRAGL_ADIP_TISS"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       line = String.valueOf(exa.taingh);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_DUCTS"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_DUCTS_NORM"),
    		   Jedecma.localMessagesBundle.getString("REP_DUCTS_DILAT"),
    		   Jedecma.localMessagesBundle.getString("REP_DUCTS_DIST")
    		 };
       line = s[exa.tdotti];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MASTOP_FICY"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
             };
       line = s[exa.mpfbcs];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_SECRETION"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_SECRETION_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_SECRETION_YES")
    		 };
       line = s[exa.tescrc];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MASTOP_MICY"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
             };
       line = s[exa.mpmics];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_HEMATIC"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
             };
       line = s[exa.ematic];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MASTOP_MICY"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
             };
       line = s[exa.mpmacs];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_NODULAR_FIBR"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
             };
       line = s[exa.fibnod];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_ENDOC_PROL"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
             };
       line = s[exa.vgencs];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MICROCAL"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
             };
       line = s[exa.esmica];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_PROL_AREA"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
             };
       line = s[exa.arprol];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	
      
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MACROCAL"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
             };
       line = s[exa.esmaca];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_TUM_PRESENT"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
             };
       line = s[exa.tsttum];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MEDIC_TH"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       line = Uti1.leftSet(exa.termed, 20, ' ');
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_LAST_SURG_TH"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       line = Uti1.leftSet(exa.terchi, 20, ' ');
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     pen.y +=  1* intl;	 

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MAMM_RES"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_MAMM_NEG"),
    		   Jedecma.localMessagesBundle.getString("REP_MAMM_DUB"),
    		   Jedecma.localMessagesBundle.getString("REP_MAMM_POS")
    		 };
       line = s[exa.esmamm];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     
       
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MAMM_DATE"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       String ws = " ";
       if (Uti1.date2String(exa.dtmamm).length()>0) {
         ws = Uti1.date2String(exa.dtmamm);
       }
       line = ws;       
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     
     
     if ( exa.tsttum == 2 ) {
     
     pen.y += 2 * intl;
     
     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_TUM_DATA"), font3, frc);
       Rectangle2D bounds = tl1.getBounds();
       double ww = bounds.getWidth();
       double dx = t1 + (70 * inct - ww)  / 2.0;
       tl1.draw(g2d, (float) dx, (float) pen.y);
     }     
            
     pen.y +=  1* intl;
     if (textSizeCount < (maxTextSize - intl) ) { pen.y += 1 * intl; }

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_TUM_SITE"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       line = Uti1.leftSet(exa.tmsede, 5, ' ');
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_TUM_AGE"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       line = String.valueOf(exa.tmcomp);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     

     pen.y +=  1* intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_DIST_SKIN"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       line = String.valueOf(exa.discut);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_DIST_BAND"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       line = String.valueOf(exa.disfas);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     

     pen.y +=  1* intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_DIST_NIPP"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       line = String.valueOf(exa.discap);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_TUM_DIMENS"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       line = String.valueOf(exa.tmdime);
       String ws = " ";
       if (line.length()>0) {
         ws = line;
       }
       line = ws; 
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     

     pen.y +=  1* intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_TUM_SURFACE"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       line = String.valueOf(exa.tmsupe);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_TUM_VOLUME"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       line = String.valueOf(exa.tmvolu);
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     

     pen.y +=  1* intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_CONTOURS"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_CONTOURS_NEV"),
    		   Jedecma.localMessagesBundle.getString("REP_CONTOURS_GRAD"),
    		   Jedecma.localMessagesBundle.getString("REP_CONTOURS_NET")
    		 };
       line = s[exa.tmcont];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MARGINS"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_MARGINS_REG"),
    		   Jedecma.localMessagesBundle.getString("REP_MARGINS_IRR"),
    		   Jedecma.localMessagesBundle.getString("REP_MARGINS_JAG"),
    		   Jedecma.localMessagesBundle.getString("REP_MARGINS_INF")
    		 };
       line = s[exa.tmmarg];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     

     pen.y +=  1* intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_ECOGEN"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_ECOGEN_NONE"),
    		   Jedecma.localMessagesBundle.getString("REP_ECOGEN_LOW"),
    		   Jedecma.localMessagesBundle.getString("REP_ECOGEN_MID"),
    		   Jedecma.localMessagesBundle.getString("REP_ECOGEN_HIGH")
    		 };
       line = s[exa.tmegen];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_ATTEN"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_ATTEN_NONE"),
    		   Jedecma.localMessagesBundle.getString("REP_ATTEN_LOW"),
    		   Jedecma.localMessagesBundle.getString("REP_ATTEN_MID"),
    		   Jedecma.localMessagesBundle.getString("REP_ATTEN_HIGH")
    		 };
       line = s[exa.tmattn];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     

     pen.y +=  1* intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_SHADOW_CONE"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
  		   };
       line = s[exa.tmcono];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_TUNNEL"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
  		   };
       line = s[exa.tmtunn];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     

     pen.y +=  1* intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MACROCAL"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
  		   };
       line = s[exa.tmmaca];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MACROC_TYPE"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_MACROC_TYPE_BAR"),
    		   Jedecma.localMessagesBundle.getString("REP_MACROC_TYPE_NOD")
    		 };
       line = s[exa.tmtmac];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     

     pen.y +=  1* intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MICROCAL"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
  		   };
       line = s[exa.tmmica];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MICROC_TYPE"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_MICROC_TYPE_BAR"),
    		   Jedecma.localMessagesBundle.getString("REP_MICROC_TYPE_NOD")
    		 };
       line = s[exa.tmtmic];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     

     pen.y +=  1* intl;

     {
       tl1 = new TextLayout("Cooper", font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_COOPER_NORM"),
    		   Jedecma.localMessagesBundle.getString("REP_COOPER_DIST"),
    		   Jedecma.localMessagesBundle.getString("REP_COOPER_INTER")
    		 };
       line = s[exa.tmcoop];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_SKIN_ECOGEN"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_SKIN_ECOGEN_NORM"),
    		   Jedecma.localMessagesBundle.getString("REP_SKIN_ECOGEN_REDUC"),
    		   Jedecma.localMessagesBundle.getString("REP_SKIN_ECOGEN_INCR")
    		 };
       line = s[exa.tmegcu];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     

     pen.y +=  1* intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_UB_ADIP_LAYER"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_ADIP_LAYER_NORM"),
    		   Jedecma.localMessagesBundle.getString("REP_ADIP_LAYER_REDUC")
    		 };
       line = s[exa.tmsasc];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_RB_ADIP_LAYER"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_ADIP_LAYER_NORM"),
    		   Jedecma.localMessagesBundle.getString("REP_ADIP_LAYER_REDUC")
    		 };
       line = s[exa.tmsarm];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     

     pen.y +=  1* intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MUSC_ECOGEN"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_MUSC_ECOGEN_NORM"),
    		   Jedecma.localMessagesBundle.getString("REP_MUSC_ECOGEN_REDUC")
    		 };
       line = s[exa.tmegfm];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }     

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_MUSC_INFILT"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_NO"),
    		   Jedecma.localMessagesBundle.getString("REP_YES")
  		   };
       line = s[exa.tminfa];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t4, (float) pen.y);
     }     

     pen.y +=  1* intl;

     {
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_DOPPLER"), font3, frc);
       tl1.draw(g2d, t1, (float) pen.y);
       
       String s[] = new String[] {
    		   Jedecma.localMessagesBundle.getString("REP_NA"),
    		   Jedecma.localMessagesBundle.getString("REP_DOPPLER_BEN"),
    		   Jedecma.localMessagesBundle.getString("REP_DOPPLER_SUSP"),
    		   Jedecma.localMessagesBundle.getString("REP_DOPPLER_MAL")
    		 };
       line = s[exa.tmdopp];
       tl1 = new TextLayout(line, font1, frc);
       tl1.draw(g2d, t2, (float) pen.y);
     }  
     
       pen.y += 2 * intl;
     
     } else {
     
       pen.y += 4 * intl;
     
     } // end_if tsttum
     
     {	 
       tl1 = new TextLayout(Jedecma.localMessagesBundle.getString("REP_SIGNAT"), font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
     }
	 
     pen.y += intl;
     
     {
       line = Jedecma.param.getProperty("drname");
       if (line.length() == 0) { line = " "; }
       tl1 = new TextLayout(line, font3, frc);
       tl1.draw(g2d, t3, (float) pen.y);
     }

     return Printable.PAGE_EXISTS;
   }
   
  public String getProfDes(String c) {
    String cod = c;
    String des = "";
    ResultSet rs = Jedecma.dbmgr.executeQuery("SELECT PROFCOD, PROFDES FROM TBLPROF WHERE PROFCOD='" + cod +"'");
    try {
      while (rs.next()) {
        des = (String)rs.getObject(2);
      }
    }
    catch  (SQLException ex) {
      System.err.println(ex);
      Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
    }
    return des;
  }

   
 }  /// end_of_EcoPag1
  
}  /// end_of_class EcoPrint
