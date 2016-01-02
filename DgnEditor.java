/*  
 * DgnEditor.java - examination text management
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

import java.util.*;
import java.awt.*; 
import java.awt.event.*; 
import javax.swing.*; 
import javax.swing.text.*;
import javax.swing.event.*; 

public class DgnEditor extends JPanel {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private final JTextArea editor = new JTextArea();
  private int maxSizeText;
  private Vector dict;
  private boolean completionEnable = true; // false; // vedi anche cBox.setSelected(true)
  private boolean tipStatus = false;
  private TextAreaDocumentListener dList; 

  public DgnEditor() { this(1024); }

  public DgnEditor(int mSize) {
    super();
    dict = getDict(); 
    maxSizeText = mSize; 
      
    if (Jedecma.jTextAreaFont != null) {
      editor.setFont(Jedecma.jTextAreaFont);
    }
    editor.registerKeyboardAction(new ActionListener() {
      KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
      public void actionPerformed(ActionEvent ae) {
      
        final DocumentListener listener = dList;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
          public void run() {
	    editor.getDocument().removeDocumentListener(listener);
	    int i = editor.getCaretPosition();    
	    int start = editor.getSelectionStart();
	    int end = editor.getSelectionEnd();
	    if (( start == end ) && (getTipStatus() == true)) {
	      // potrebbe avere rinunciato al tip
	      setTipStatus(false);
	    }
	    //   System.out.println("tipStatus= " + getTipStatus());
            if (! getTipStatus()) {
	      String text = editor.getText();
            //	  System.out.println("setect Start/End=" + start +" "+end);
	      String s = text.substring(0, start);
	      String r = text.substring(end);
	      editor.setText(s + "\n" + r);   
	      editor.select(start +1, start +1);
	    //      System.out.println("ora il caret e' " + (editor.getCaretPosition()));
	    } else {   
	      editor.select(end, end);
	    }
	    //  System.out.println("ENTER");	  
	    setTipStatus(false);
	    editor.getDocument().addDocumentListener(listener);
          }
        });     
      }
    }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);

    editor.setDocument(new FixedSizePlainDocument(maxSizeText));
    editor.setLineWrap(true);
    editor.setWrapStyleWord(true);
    setLayout(new BorderLayout());
    add(new JScrollPane(editor), BorderLayout.CENTER);
    JPanel bPanel = new JPanel();
    add(bPanel, BorderLayout.SOUTH);
    
    Action action = new AbstractAction(Jedecma.localMessagesBundle.getString("AUTOCOMP")) {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	// This method is called when the button is pressed
      public void actionPerformed(ActionEvent evt) {
      // Perform action
        JCheckBox cb = (JCheckBox)evt.getSource();
        // Determine status
        boolean isSel = cb.isSelected();
        if (isSel) {
	  if (dict.size() > 0) {
            setCompletionEnable(true);
	  } else { cb.setSelected(false); };
        } else {
          setCompletionEnable(false);
        }
      }
    };    
  
    
    JCheckBox cBox = new JCheckBox(action);
    //cBox.setSelected(false); // vedi anche setCompletionEnable
    cBox.setSelected(true);
    bPanel.add(cBox);
    
    TextAreaDocumentListener dList = new TextAreaDocumentListener();
    editor.getDocument().addDocumentListener(dList); 
  }

  public String getText() {
    return editor.getText();
  }
  
  public void setText(String text) {
    editor.setText(text);
  } 

  private void setCompletionEnable(boolean b) {
    completionEnable = b;
  }

  private boolean getCompletionEnable() {
    return completionEnable;
  }

  private void setTipStatus(boolean b) {
    tipStatus = b;
  }

  private boolean getTipStatus() {
    return tipStatus;
  }

  private Vector getDict() {
    return Jedecma.dict;
  }
  
  public Iterator getDictIterator(){
    return dict.iterator();
  }

   class TextAreaDocumentListener implements DocumentListener {
    public void insertUpdate(DocumentEvent e) {
      //System.out.println("insertUpdate ");
      if (getCompletionEnable()) {
        findTip();
      }
    }
     			
    public void changedUpdate(DocumentEvent e) {
      //System.out.println("changedUpdate");
    }
  			
    public void removeUpdate(DocumentEvent e) {
      //System.out.println("removeUpdate ");
      setTipStatus(false); // serve veramente?
    }    
   
   private String find (String s) {
     String found = "";
     for (Iterator i = getDictIterator(); i.hasNext();) {
       String item = (String) i.next();
       int wordLen = Math.min(s.length(), item.length());
       //System.out.println ("item=" + item);
       if ((item.substring(0,wordLen)).equals(s.substring(0,wordLen))) {
         found = item;
	 break;
       }
     }
     //if ( found.length()>0 ) System.out.println("trovato="+found);
     return found;
   }
   
   private void findTip () {
     String word = currWord();
     if (word.length() >= 4 ) {
       char ch = word.charAt(word.length()-1);
       word = word.toUpperCase();
       //System.out.println ("cerco="+word);
       String s = find(word);
       if (s.length() > 0) {
     
         final int caretInitial = editor.getCaretPosition();
        //System.out.println("caret iniziale=" + caretInitial);
         String st = s.substring(word.length());
        //System.out.println("'case' determinato da=" + ch);
         if (Character.isLowerCase(ch)) {st = st.toLowerCase();}
         final String subst = st + " ";
     
         String text = editor.getText();
         int lText = text.length();
         if (lText + st.length() <= maxSizeText ) {
     
           final int caretFinal = caretInitial + subst.length();
           //System.out.println("CompletionEnable()="+getCompletionEnable());
           final DocumentListener listener = this;
           javax.swing.SwingUtilities.invokeLater(new Runnable() {
             public void run() {
               editor.getDocument().removeDocumentListener(listener);
	    //System.out.println("inserisco '" + subst + "' a pos " + (caretInitial + 1));
               editor.insert(subst, caretInitial +1);
	    //System.out.println("ora il testo e' " + editor.getText());
	       editor.setSelectionStart(caretInitial + 1);
	       editor.setSelectionEnd(caretFinal + +1);
	    //System.out.println("ora il caret e' " + (editor.getCaretPosition()));
	       setTipStatus(true);
               editor.getDocument().addDocumentListener(listener);
             }
           });     
         }
       }
     }         
   }
   
   private String currWord() {
   // determina prima parola a sx del cursore
     String cw = "";
     //System.out.println("text=" + editor.getText());
     int caretPosition = editor.getCaretPosition();
     if (caretPosition >= 0) {
         String text = editor.getText();
         char c; 
	 //System.out.println("caretPos=" + caretPosition);
	 try {
         for (int i = caretPosition; i >=0; i--) {
           c = text.charAt(i);
	   //System.out.println("i=" + i + " " + c);
	   if (c == ' ') {  // separatore default spazio
	     break;
	   }
	   if (c == '\n') { // considera separatore valido anche newline
	     break;
	   }
	   if (! Character.isLetter(c)) {
	     cw = "";
             break;
	   } 
	   //System.out.println("cs=" + cs);
	   cw = Character.toString(c) + cw;
         }
	 
	 } catch (StringIndexOutOfBoundsException e) {
	 }
      //System.out.println("currWord=" + cw);
     }
   return cw; 
   }
   
  }
  
   class FixedSizePlainDocument extends PlainDocument {
        /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private int maxSize;

        public FixedSizePlainDocument(int limit) {
            maxSize = limit;
        }
	
 public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
     if ((getLength() + str.length()) <= maxSize) {
         super.insertString(offs, str, a);
     } else {
         throw new BadLocationException("Insertion exceeds max size of document", offs);
     }
   }
   
 } 
  
  
} 

