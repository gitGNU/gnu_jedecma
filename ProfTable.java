/*  
 * ProfTable.java - job list magagement
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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.table.*;
import java.sql.*;
import java.util.Vector;

public class ProfTable extends JDialog {   
  
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private static final String delString = Jedecma.localMessagesBundle.getString("DELETE");
  private static final String newString = Jedecma.localMessagesBundle.getString("NEW");
  private static final String lockCode = "TBLPROF";
  private JButton delBut, newBut; 
  private JTextField profCod, profDes;
  private ProfTableModel model;
  private final JTable table;
  private Vector rows = new Vector();
  
  public ProfTable() {
    super (Jedecma.mf, Jedecma.localMessagesBundle.getString("JOB_LIST"), true); // E' un JDialog modale!
    model = new ProfTableModel();
    table = new JTable(model);
    
    // lock della tabella
    int lockrv = 0;
    if (Jedecma.user != null) {
       lockrv = MyLock.setLock(Jedecma.dbmgr, lockCode, Jedecma.user.userid, true);
    }
    if (lockrv == 0 ) {

    model.addColumn(Jedecma.localMessagesBundle.getString("CODE"));
    model.addColumn(Jedecma.localMessagesBundle.getString("DESC"));
  
    ResultSet resultSet;
    resultSet = Jedecma.dbmgr.executeQuery("SELECT PROFCOD, PROFDES FROM TBLPROF ORDER BY PROFCOD");
    try {
      ResultSetMetaData md = resultSet.getMetaData();
      int numberOfColumns =  md.getColumnCount();
      while (resultSet.next()) {
        Vector nextRow = new Vector();
        for (int i = 1; i <= numberOfColumns; i++) {
          nextRow.addElement(resultSet.getObject(i));
        }
        model.addRow(nextRow);
      }
    }
    catch  (SQLException ex) {
      System.err.println(ex);
      Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
    }
  
    if ( Jedecma.jTableFont != null ) {
      table.setFont(Jedecma.jTableFont);
    }
    table.setPreferredScrollableViewportSize(new Dimension(500, 100));
    table.setColumnSelectionAllowed(false);
    table.setRowSelectionAllowed(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    SelectionListener listener = new SelectionListener(table);
    table.getSelectionModel().addListSelectionListener(listener);
    table.getColumnModel().getSelectionModel().addListSelectionListener(listener);

    TableColumn column = null;
    column = table.getColumnModel().getColumn(0);
    column.setMaxWidth(45);
    column = table.getColumnModel().getColumn(1);
    column.setCellEditor(new ProfTableCellEditor());

    JScrollPane scrollPan = new JScrollPane(table);
    
    JPanel mainPan = new JPanel();
    mainPan.setLayout(new BoxLayout(mainPan,BoxLayout.Y_AXIS));
    mainPan.add(scrollPan, BorderLayout.CENTER);
  
    JPanel butPan = new JPanel();
    butPan.setLayout(new FlowLayout(FlowLayout.RIGHT));
    butPan.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));  
  
    profCod = new JTextField(4);
    profCod.setDocument(new FixedSizePlainDocument(4));
    profCod.setText("");
    profCod.addFocusListener(new focusListener());
    butPan.add(profCod);
  
    profDes = new JTextField(30);
    profDes.setDocument(new FixedSizePlainDocument(30));
    profDes.setText("");
    profDes.addFocusListener(new focusListener());
    butPan.add(profDes);
 
    delBut = new JButton(delString);
    delBut.setMnemonic(KeyEvent.VK_L);
    delBut.addActionListener(new DelListener());
    delBut.setActionCommand(delString);
    delBut.setEnabled(false);
    butPan.add(delBut);

    newBut = new JButton(newString);
    newBut.addActionListener(new NewListener());
    newBut.setActionCommand(newString);
    newBut.setMnemonic(KeyEvent.VK_N);
    newBut.setEnabled(false);
    butPan.add(newBut);

    mainPan.add(butPan, BorderLayout.SOUTH);
  
    getContentPane().add(mainPan, BorderLayout.CENTER);
 
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) { 
      // rimuove lock
        if (Jedecma.user != null) {  
           MyLock.delLock(Jedecma.dbmgr, lockCode, Jedecma.user.userid);
        }
      }
    });
    
    //setResizable(false);
    pack();
    setVisible(true);
    } else {
      Uti1.error(
    		  Jedecma.localMessagesBundle.getString("JOB_LIST") 
    		  + " " + Jedecma.localMessagesBundle.getString("IN_USE") 
              + " (" + lockrv + ")", false); 
    }
  }
   
  public String getProfDes(String c) {
    String cod = c;
    String des = "";
    ResultSet rs = Jedecma.dbmgr.executeQuery("SELECT PROFCOD, PROFDES FROM TBLPROF WHERE PROFCOD='" + Uti1.escape(cod) +"'");
    int i = 0;
    try {
      while (rs.next()) {
        des = (String)rs.getObject(2);
        i++;
      }
    }
    catch  (SQLException ex) {
      System.err.println(ex);
      Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
    }
    if (i > 0) {
      return des;
    } 
    return null;
  }
   
   public class ProfTableModel extends DefaultTableModel {
	
        public boolean isCellEditable(int row, int col) {
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }
	
        public void setValueAt(Object v, int r, int c) {
	  Object value = v;
	  int row = r;
	  int column = c;
          super.setValueAt(value, row, column); 
	  String cod = (String)table.getModel().getValueAt(row, 0);
	  String des = (String)table.getModel().getValueAt(row, 1);
	  int rc = Jedecma.dbmgr.executeUpdate("UPDATE TBLPROF SET PROFDES = '" + Uti1.escape(des) + "' WHERE PROFCOD = '" + Uti1.escape(cod) + "'");
	  if ( rc != 1 ) {
	    Uti1.error(Jedecma.localMessagesBundle.getString("JOB_LIST") + ": " + Jedecma.localMessagesBundle.getString("INS_UPD_FAILURE"), true);
	  }
     }
   }
   
   public class ProfTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        // This is the component that will handle the editing of the
        // cell value
        JComponent component = new JTextField();
    
        // This method is called when a cell value is edited by the user.
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at
            // (rowIndex, vColIndex)
            if (isSelected) {
                // cell (and perhaps other cells) are selected
	    }
            // Configure the component with the specified value
            ((JTextField)component).setText((String)value);
            // Return the configured component
            return component;
        }
    
        // This method is called when editing is completed.
        // It must return the new value to be stored in the cell.
        public Object getCellEditorValue() {
	
	    String text = ((JTextField)component).getText();
	    if (text.length() > 30) {
	       text = text.substring(0, 30);
	    }
            return (text);
        } 

    }

    class NewListener implements ActionListener {
      NewListener () {
      }
      public void actionPerformed(ActionEvent e) {
      
      profCod.setText(profCod.getText().toUpperCase());
      
      String cod = profCod.getText();
      String des = profDes.getText();
      
      if ( ! cod.equals("") ) {
        if (getProfDes(cod) != null) {
          Toolkit.getDefaultToolkit().beep();
          Uti1.error(Jedecma.localMessagesBundle.getString("CODE") + ": " + cod + " " + Jedecma.localMessagesBundle.getString("ALREADY_EXISTING"), false);
        } else {
	  try {
            model.addRow(new Object[]{cod, des});
          } catch ( Exception x ) {
            Uti1.error(Jedecma.localMessagesBundle.getString("TMP_TABLE_ERROR"), false);
          }
	  int rc = Jedecma.dbmgr.executeUpdate("INSERT INTO TBLPROF VALUES ('" + Uti1.escape(cod) + "', '" + Uti1.escape(des) + "')");
          if ( rc != 1 ) {
	      Uti1.error(Jedecma.localMessagesBundle.getString("JOB_LIST") + ": " + Jedecma.localMessagesBundle.getString("INS_UPD_FAILURE"), true);
	  }	  
        }
      }
      newBut.setEnabled(false);
    }
  }
  
    public class SelectionListener implements ListSelectionListener {
        JTable table;
    
        SelectionListener(JTable table) {
            this.table = table;
        }
        public void valueChanged(ListSelectionEvent e) {
	    int rows = table.getRowCount();
            int r = table.getSelectedRow();
            int c = table.getSelectedColumn();
	    
	    newBut.setEnabled(false);
	    delBut.setEnabled((c == 0));
	    
            if (e.getSource() == table.getSelectionModel()
                  && table.getRowSelectionAllowed()) {
                int first = e.getFirstIndex();
                int last = e.getLastIndex();
		
		if ( (rows > 0) && (r != -1) && !e.getValueIsAdjusting()) 
                {
		  // n.b.: vengono considerati i valori in tabella e non sul DB!
		  profCod.setText((String) table.getModel().getValueAt(r, 0));
		  profDes.setText((String) table.getModel().getValueAt(r, 1));
		} 
	
            } else if (e.getSource()
                   == table.getColumnModel().getSelectionModel()
                   && table.getColumnSelectionAllowed() ){
                int first = e.getFirstIndex();
                int last = e.getLastIndex();
            }
    
            /*
	    if (e.getValueIsAdjusting()) {
                // The mouse button has not yet been released
            } */
        }
    }

  class DelListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
    
    int rows = table.getRowCount();
    int r = table.getSelectedRow();
    int c = table.getSelectedColumn();
    
    if ( (rows > 0) && (r != -1) && (model.isCellEditable(r, c) == false) ) 
    {
      
      String va = (String) table.getModel().getValueAt(r, 0);
      try {
        model.removeRow(r);
	delBut.setEnabled(false);
        } catch ( ArrayIndexOutOfBoundsException x ) {
          System.out.println("ArrayIndexOutOfBoundsException indice di riga:" + r +" righe: " + table.getRowCount());
        }
	String cod = profCod.getText();
	int rc = Jedecma.dbmgr.executeUpdate("DELETE FROM TBLPROF WHERE PROFCOD='" + Uti1.escape(cod) + "'");
	if ( rc != 1 ) {
	  Uti1.error(Jedecma.localMessagesBundle.getString("JOB_LIST") + ": " + Jedecma.localMessagesBundle.getString("INS_UPD_FAILURE"), true);
	}
      } else {
        Toolkit.getDefaultToolkit().beep();
      }
    }
  }

  class focusListener implements FocusListener {
  
    public void focusGained(FocusEvent e) {
        delBut.setEnabled(false);
	newBut.setEnabled(true);
    }

    public void focusLost(FocusEvent e) {
        final JTextComponent c = (JTextComponent)e.getSource();
        delBut.setEnabled(true);
    }
  
  }
  
}  /// fine ProfTable class

