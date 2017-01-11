/*  
 * SQLTool.java - basic SQL client to execute operations on internal database
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.sql.*;
import java.io.*;

public class SQLTool implements Menuable {
  private JButton qryBut, openBut, loadBut, showTableBut, dmpBut; 
  private JPanel mainPan, qryPan, qryFilePan, qryTablePan, msgTablePan,dmpPan;
  private JSplitPane sp0, sp1;
  private JTabbedPane tabPan;
  private JTextArea qry, qryResult;
  private QryTableModel qryTableModel;
  private JTable qryTable;
  private MyJTextField recFrom, recTo, qryFile, dateFrom, dateTo;
  private final JFileChooser fc = new JFileChooser();
  private MyJComboBox tblCmb;

  public SQLTool ()  {
  }
    
  public void stop ()  {
    saveDoneQry();
    Jedecma.mainPan.remove(mainPan);
    Jedecma.mf.repaint();
  }

  public void start() {
    mainPan = new JPanel();
    mainPan.setLayout(new BoxLayout(mainPan, BoxLayout.Y_AXIS));

    sp0 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    sp0.setTopComponent(tabPan);
    sp0.setOneTouchExpandable(true);
    // sp0.setDividerLocation(0.75);
    mainPan.add(sp0);   

    tabPan = new JTabbedPane(JTabbedPane.TOP);
    sp0.setTopComponent(tabPan);
    
    qryPan = new JPanel();
    tabPan.addTab(Jedecma.localMessagesBundle.getString("MANUAL_SQL_CMDS"),qryPan);

    GridBagLayout gbQPan = new GridBagLayout();
    GridBagConstraints gbcQPan = new GridBagConstraints();
    qryPan.setLayout(gbQPan);

    qry = new JTextArea(6, 50);
    qry.setDocument(new FixedSizePlainDocument(1024));
    //qry.setText("");     
    JScrollPane qryScrollPan;
    qryScrollPan = new JScrollPane(qry);
    qry.setLineWrap(true);
    qry.setWrapStyleWord(true);
    Uti1.bldConst(gbcQPan, 0, 0, 10, 2, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
    gbQPan.setConstraints(qryScrollPan, gbcQPan);
    qryPan.add(qryScrollPan); 
    qryBut = new JButton(Jedecma.localMessagesBundle.getString("EXECUTE"));
    qryBut.setMnemonic(KeyEvent.VK_E);
    qryBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String q = qry.getText();
        doQuery(q);
	qry.setText(q.trim());
      }      
    });
    Uti1.bldConst(gbcQPan, 10, 0, 1, 1, 0, 0);
    gbQPan.setConstraints(qryBut, gbcQPan);
    qryPan.add(qryBut);

    showTableBut = new JButton("showTables");
    showTableBut.setMnemonic(KeyEvent.VK_T);
    showTableBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String q = showTables();
        if (q.length()>0) {
          doQuery(q);
        }
      }      
    });
    Uti1.bldConst(gbcQPan, 10, 1, 1, 1, 0, 0);
    gbQPan.setConstraints(showTableBut, gbcQPan);
    qryPan.add(showTableBut);
    
    qryFilePan = new JPanel();
    GridBagLayout gbFPan = new GridBagLayout();
    GridBagConstraints gbcFPan = new GridBagConstraints();
    qryFilePan.setLayout(gbFPan);
    tabPan.addTab(Jedecma.localMessagesBundle.getString("FILED_SQL_CMDS"), qryFilePan);

    openBut = new JButton(Jedecma.localMessagesBundle.getString("BROWSE"));
    openBut.setMnemonic(KeyEvent.VK_S);
    openBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fc.addChoosableFileFilter(new MyFileFilter());
	fc.setCurrentDirectory(new File(Uti1.getWrkDir()));
        int returnVal = fc.showOpenDialog(null);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	  String s = fc.getSelectedFile().getAbsolutePath();
          qryFile.setText(s);
        }
      }      
    });
    Uti1.bldConst(gbcFPan, 0, 1, 5, 1, 1, 0);
    gbFPan.setConstraints(openBut, gbcFPan);
    qryFilePan.add(openBut);
    
    {
    MyJlabel lab = new MyJlabel(Jedecma.localMessagesBundle.getString("FILE_NAME"));
    Uti1.bldConst(gbcFPan, 0, 0, 4, 1, 1, 1);
    gbFPan.setConstraints(lab, gbcFPan);
    qryFilePan.add(lab);
    qryFile = new MyJTextField (128, "", new float[]{}, 0);    
    qryFile.addFocusListener(new CtrTextField());
    Uti1.bldConst(gbcFPan, 4, 0, 26, 1, 1, 0);
    gbFPan.setConstraints(qryFile, gbcFPan);
    qryFilePan.add(qryFile);
    }


    {
    MyJlabel lab = new MyJlabel(Jedecma.localMessagesBundle.getString("REC_START"));
    Uti1.bldConst(gbcFPan, 5, 1, 3, 1, 1, 1);
    gbFPan.setConstraints(lab, gbcFPan);
    qryFilePan.add(lab);
    recFrom = new MyJTextField (5, "0", new float[]{}, 1);    
    recFrom.addFocusListener(new CtrTextField());
    Uti1.bldConst(gbcFPan, 8, 1, 4, 1, 1, 0);
    gbFPan.setConstraints(recFrom, gbcFPan);
    qryFilePan.add(recFrom);
    }

    {
    MyJlabel lab = new MyJlabel(Jedecma.localMessagesBundle.getString("REC_END"));
    Uti1.bldConst(gbcFPan, 12, 1, 3, 1, 1, 1);
    gbFPan.setConstraints(lab, gbcFPan);
    qryFilePan.add(lab);
    recTo = new MyJTextField (5, "0", new float[]{}, 1);    
    recTo.addFocusListener(new CtrTextField());
    Uti1.bldConst(gbcFPan, 15, 1, 4, 1, 1, 0);
    gbFPan.setConstraints(recTo, gbcFPan);
    qryFilePan.add(recTo);
    }

    loadBut = new JButton(Jedecma.localMessagesBundle.getString("EXECUTE"));
    loadBut.setMnemonic(KeyEvent.VK_U);
    loadBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      loadFileQry(qryFile.getText());
      }      
    });
    Uti1.bldConst(gbcFPan, 25, 1, 5, 1, 1, 0);
    gbFPan.setConstraints(loadBut, gbcFPan);
    qryFilePan.add(loadBut);
    
    sp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
    sp1.setOneTouchExpandable(true);
    sp1.setDividerLocation(200);
    sp0.add(sp1);

    msgTablePan = new JPanel();
    sp1.add(msgTablePan);
    sp1.setTopComponent(msgTablePan);
    msgTablePan.setBorder(BorderFactory.createTitledBorder(" "
    		+ Jedecma.localMessagesBundle.getString("MESSAGES")
    		+ " "));

    GridBagLayout gbRPan = new GridBagLayout();
    GridBagConstraints gbcRPan = new GridBagConstraints();
    msgTablePan.setLayout(gbRPan);
    
    qryResult = new JTextArea(6, 50);
    //JScrollPane qryResPan = new JScrollPane(msgTable);
    JScrollPane qryResPan = new JScrollPane(qryResult);
    Uti1.bldConst(gbcRPan, 0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
    gbRPan.setConstraints(qryResPan, gbcRPan);
    msgTablePan.add(qryResPan); 
    qryResult.setEditable(false);
    
    qryTablePan = new JPanel();
    sp1.add(qryTablePan);
    qryTablePan.setBorder(BorderFactory.createTitledBorder(" "
    		+ Jedecma.localMessagesBundle.getString("LAST_CMDS")
    		+ " "));
    
    GridBagLayout gbDPan = new GridBagLayout();
    GridBagConstraints gbcDPan = new GridBagConstraints();
    qryTablePan.setLayout(gbDPan);

    qryTableModel = new QryTableModel();
    qryTable = new JTable(qryTableModel);
    qryTableModel.addColumn("");
    qryTable.setColumnSelectionAllowed(false);
    qryTable.setRowSelectionAllowed(true);
    qryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane doneQryPan = new JScrollPane(qryTable);
    Uti1.bldConst(gbcDPan, 0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
    gbDPan.setConstraints(doneQryPan, gbcDPan);
    qryTablePan.add(doneQryPan); 
    SelectionListener listener = new SelectionListener(qryTable);
    qryTable.getSelectionModel().addListSelectionListener(listener);
    qryTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);

    dmpPan = new JPanel();
    GridBagLayout gbDmPan = new GridBagLayout();
    GridBagConstraints gbcDmPan = new GridBagConstraints();
    dmpPan.setLayout(gbDmPan);
    tabPan.addTab(Jedecma.localMessagesBundle.getString("TABLE_DUMP"), dmpPan);
    
    {
    MyJlabel lab = new MyJlabel("Table");
    Uti1.bldConst(gbcDmPan, 0, 0, 3, 2, 1, 1);
    gbDmPan.setConstraints(lab, gbcDmPan);
    dmpPan.add(lab);
    tblCmb = new MyJComboBox(new String[] {
    		"ALL",
    		// "TBLUSER",
    		"TBLPROF",
    		// "TBLCCDB",
    		"DIAGTXT",
    		"EDECAN",
    		"EDECMA", 
    		"ECOIMG",},"");    
    
    Uti1.bldConst(gbcDmPan, 3, 0, 6, 1, 1, 0);
    gbDmPan.setConstraints(tblCmb, gbcDmPan);
    dmpPan.add(tblCmb);
    }   
  
    {
    	  MyJlabel lab = new MyJlabel(Jedecma.localMessagesBundle.getString("DATE_START"));
    	  Uti1.bldConst(gbcDmPan, 0, 1, 3, 1, 0, 0);
    	  gbDmPan.setConstraints(lab, gbcDmPan);
    	  dmpPan.add(lab);
    	  dateFrom = new MyJTextField (10, "", new float[]{}, 6);    
    	  dateFrom.addFocusListener(new CtrTextField());
    	  Uti1.bldConst(gbcDmPan, 4, 1, 3, 1, 0, 0);
    	  gbDmPan.setConstraints(dateFrom, gbcDmPan);
    	  dmpPan.add(dateFrom);
    }
    
    {
  	  MyJlabel lab = new MyJlabel(Jedecma.localMessagesBundle.getString("DATE_END"));
  	  Uti1.bldConst(gbcDmPan, 7, 1, 3, 1, 0, 0);
  	  gbDmPan.setConstraints(lab, gbcDmPan);
  	  dmpPan.add(lab);
  	  dateTo = new MyJTextField (10, "", new float[]{}, 6);    
  	  dateTo.addFocusListener(new CtrTextField());
  	  Uti1.bldConst(gbcDmPan, 10, 1, 3, 1, 0, 0);
  	  gbDmPan.setConstraints(dateTo, gbcDmPan);
  	  dmpPan.add(dateTo);
  }
    
    dmpBut = new JButton("dump");
    dmpBut.setMnemonic(KeyEvent.VK_D);
    dmpBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
          dmpTables(tblCmb.getInpValue());
      }      
    });
    Uti1.bldConst(gbcDmPan, 13, 0, 3, 1, 0, 0);
    gbDmPan.setConstraints(dmpBut, gbcDmPan);
    dmpPan.add(dmpBut);
    
    GridBagLayout gb = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
  
    Uti1.bldConst(gbc, 0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
    gb.setConstraints(mainPan, gbc);
    Jedecma.mainPan.setLayout(gb);
    Jedecma.mainPan.add(mainPan);

    loadDoneQry();
 
    Jedecma.mf.pack();
    Jedecma.mf.setVisible(true);
  }

	public void saveDoneQry() {
		if (Jedecma.user != null) {
			String inputFile = Uti1.getWrkDir();
			inputFile += "sqltool."
					+ Uti1.rightSet(String.valueOf(Jedecma.user.userid), 3, '0');
			FileWriter out = null;
			try {
				out = new FileWriter(inputFile);
				String line = "";
				int rows = qryTable.getRowCount();
				int r = 0;
				// System.out.println("rows="+rows);
				for (int i = 0; (i < 50 && i < rows); i++) {
					r = rows - i - 1;
					// System.out.println("r="+r);
					line = (String) qryTable.getModel().getValueAt(r, 0);
					out.write(line + '\n');
				}
				out.close();
			} catch (IOException ioex) {
				Uti1.error(
						Jedecma.localMessagesBundle.getString("IO_WRK_ERROR"),
						true);
			}
		}
	}

	public int loadDoneQry() {
		if (Jedecma.user != null) {
			String inputFile = Uti1.getWrkDir();
			inputFile += "sqltool."
					+ Uti1.rightSet(String.valueOf(Jedecma.user.userid), 3, '0');
			try {
				BufferedReader in = new BufferedReader(
						new FileReader(inputFile));
				String line = "";
				while ((line = in.readLine()) != null) {
					qryTableModel.addRow(new Object[] { line });
				}
				in.close();
			} catch (IOException ie) {
				System.err.println(ie);
			}
			int rows = qryTableModel.getRowCount();
			return (rows);
		} else {
			return 0;
		}
	}

  public int doQuery(String qs) {
    int status = -1;
    String q = qs.trim();
    if (q.length()>0) { 
      ResultSet resultSet = null;
      int rc = 0; 
      Connection connection = Jedecma.dbmgr.connection;
      Statement statement = Jedecma.dbmgr.statement;
      try {
	logMessage(q);
        if (statement.execute(q)) { 
	  resultSet = statement.getResultSet();
	  ResultSetMetaData md = resultSet.getMetaData();
          int columnCount =  md.getColumnCount();
	  boolean inLoop = true;
	  Object o = null;
          while (resultSet.next() && inLoop) {
	    ++rc;
	    String nextRow = "";
             for (int i = 1; i <= columnCount; i++) {
	     o = resultSet.getObject(i);
	     String s;
	     if (o == null) { s = "null"; }
	       else { s = o.toString(); }
	       nextRow += s;
		if (i < columnCount ) { nextRow += " "; } 
	     }
	     logMessage(nextRow);
	     if (rc >= 100) {
	       logMessage(Jedecma.localMessagesBundle.getString("LIST_TOO_LONG")); 
	       inLoop = false; 
	     }
	  }
	  status = rc;
	  logMessage(rc + " " +Jedecma.localMessagesBundle.getString("ROWS"));
	  qryTableModel.addRow(new Object[]{q});
	} else {
	  rc = statement.getUpdateCount();
	  status = rc;
	  logMessage(rc + " " + Jedecma.localMessagesBundle.getString("ROWS")
			  + Jedecma.localMessagesBundle.getString("UPDATED"));
	  qryTableModel.addRow(new Object[]{q});
	}
      } catch (SQLException ex) {
	    logMessage(ex.toString());
      }
    } else { logMessage("no query"); }
    return (status);
  }

  public int loadFileQry(String inputFile) {
    boolean breakOnError = true;
    int rows = 0;
    if (inputFile.length() > 0 ) {
      int recNo = 0;
      int rec0 = Integer.parseInt(recFrom.getText());
      int rec1 = Integer.parseInt(recTo.getText());
      try {
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        String line = "";
	int rc = 0;
	logMessage("file: " + inputFile);
        while ((line = in.readLine()) != null) { 
	  recNo++;
	  if ((recNo >= rec0 || rec0 <= 0) && (recNo <= rec1 || rec1 <= 0)) {
	    rc = doQuery(line);
	    if (rc < 0 ) {
	      logMessage("rec. " + recNo + " "
	    	+ Jedecma.localMessagesBundle.getString("QUERY_ERROR")); 
	      if (breakOnError) { continue; }
	    } else { rows++; }
	  }
        }
        in.close();
	logMessage(rows + " " + 
			Jedecma.localMessagesBundle.getString("EXECUTED_CMDS"));
      } catch (IOException ie) {
        logMessage(ie.toString());
      }     
    }
    return (rows);       
  }

  public String showTables() {
	  if (Jedecma.dbmgr.getDbType() == JDBCMgr.MYSQL) {
		  return ("show tables");
	  }
	  if (Jedecma.dbmgr.getDbType() == JDBCMgr.DERBY) {
		  return ("SELECT TABLENAME FROM SYS.SYSTABLES WHERE TABLETYPE='T'");
	  }
	  /*
	  if (Jedecma.dbmgr.getDbType() == JDBCMgr.MSSQL) {
		  return ("SELECT table_name FROM INFORMATION_SCHEMA.TABLES " +
		  		"WHERE TABLE_TYPE = 'BASE TABLE'");
	  } */
	  return ("");
  }  
  
  public class QryTableModel extends DefaultTableModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean isCellEditable(int row, int col) {
      return false;
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
	    	    
            if (e.getSource() == table.getSelectionModel()
                  && table.getRowSelectionAllowed()) {
                int first = e.getFirstIndex();
                int last = e.getLastIndex();
		
		if ( (rows > 0) && (r != -1) && !e.getValueIsAdjusting()) 
                {
		  // n.b.: vengono considerati i valori in tabella e non sul DB!
		  qry.setText((String) table.getModel().getValueAt(r, 0));
		} 
	
            } else if (e.getSource()
                   == table.getColumnModel().getSelectionModel()
                   && table.getColumnSelectionAllowed() ){
                int first = e.getFirstIndex();
                int last = e.getLastIndex();
            }
    
        }
    }

  class MyFileFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(File f) {
      if (f.isDirectory()) {
        return true;
      }
      String extension = Uti1.getExtension(f);
      if (extension != null) {
        extension = extension.toLowerCase();
        if (extension.equals("txt") ||
            extension.equals("asc") ||
	    extension.equals("sql") ||
            extension.equals("qry")) {
                return true;
        } else {
            return false;
        }
      }
      return false;
    }
    public String getDescription() {
      return "ascii";
    }
  }

  public void logMessage(String m) {
	  //System.out.println("logMessage: " + m);
	  final String msg;
	  msg = m;
  javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
    	  //System.out.println("in run: " + msg);
    	  qryResult.append(msg);
          qryResult.append("\n");
          qryResult.setCaretPosition(qryResult.getText().length() - 1);
      }
  });
  }
  
  void dmpTables(String tables) {
	  dmpBut.setEnabled(false);
	  System.out.println("dumping table: " + tables);
	  
		if (tables.equals("TBLPROF") || tables.equals("ALL")) {

			javax.swing.SwingUtilities.invokeLater(new Runnable() {

				public void run() {

					ResultSet rSet;
					String q = "SELECT profcod, profdes FROM TBLPROF";

					File outFile = outName("tblprof");
					System.out.println("dumping TBLPROF to " + outFile);

					rSet = Jedecma.dbmgr.executeQuery(q);
					FileWriter writer = null;
					try {
						writer = new FileWriter(outFile);

						while (rSet.next()) {

							q = "INSERT INTO TBLPROF (profcod, profdes) VALUES ("
							+ "'" + String.valueOf(Uti1.escape(rSet.getString("profcod"))) + "'"
							+ ", '" + Uti1.escape(rSet.getString("profdes")) + "')";

							writer.write(q + "\n");

						}

						writer.close();
						System.out.println("export to " + outFile);
						logMessage("export to " + outFile);

					} catch (IOException ex) {
						System.err.println(ex);
						Uti1.error("file IO error", true);
					} catch (SQLException ex) {
						System.err.println(ex);
						Uti1.error("SQL error", true);

					}

				} // fine run()

			}); // fine runnable

		} // fine if TBLPROF
		
		if (tables.equals("ECOIMG") || tables.equals("ALL")) {

			javax.swing.SwingUtilities.invokeLater(new Runnable() {

				public void run() {

					ResultSet rSet;
					String q = "SELECT datupd, examnr, note, basename FROM ECOIMG WHERE examnr >0";

					q = dateLim(q);

					File outFile = outName("ecoimg");
					System.out.println("dumping ECOIMG to " + outFile);

					rSet = Jedecma.dbmgr.executeQuery(q);
					FileWriter writer = null;
					try {
						writer = new FileWriter(outFile);

						java.util.Date d;
						while (rSet.next()) {

							q = "INSERT INTO ECOIMG (datupd, examnr, note, basename) VALUES (";

							d = rSet.getDate("datupd");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}

							q += ", " + String.valueOf(rSet.getInt("examnr")) 
									+ ", '" + Uti1.escape(rSet.getString("note")) + "'"
									+ ", '" + Uti1.escape(rSet.getString("basename")) + "'"
									+ ")";

							writer.write(q + "\n");

						}

						writer.close();
						System.out.println("export to " + outFile);
						logMessage("export to " + outFile);

					} catch (IOException ex) {
						System.err.println(ex);
						Uti1.error("file IO error", true);
					} catch (SQLException ex) {
						System.err.println(ex);
						Uti1.error("SQL error", true);

					}

				} // fine run()

			}); // fine runnable

		} // fine if ECOIMG	  
	  
		if (tables.equals("DIAGTXT") || tables.equals("ALL")) {

			javax.swing.SwingUtilities.invokeLater(new Runnable() {

				public void run() {

					ResultSet rSet;
					String q = "SELECT datupd, examnr, text FROM DIAGTXT WHERE examnr >0";

					q = dateLim(q);

					File outFile = outName("diagtx");
					System.out.println("dumping DIAGTXT to " + outFile);

					rSet = Jedecma.dbmgr.executeQuery(q);
					FileWriter writer = null;
					try {
						writer = new FileWriter(outFile);

						java.util.Date d;
						while (rSet.next()) {

							q = "INSERT INTO DIAGTXT (datupd, examnr, text) VALUES (";

							d = rSet.getDate("datupd");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}

							q += ", " + String.valueOf(rSet.getInt("examnr"))
									+ ", '" + Uti1.escapeNl(Uti1.escape(rSet.getString("text"))) + "'"
									//+ ", '" + Uti1.escape(rSet.getString("text")) + "'"
									+ ")";

							writer.write(q + "\n");

						}

						writer.close();
						System.out.println("export to " + outFile);
						logMessage("export to " + outFile);

					} catch (IOException ex) {
						System.err.println(ex);
						Uti1.error("file IO error", true);
					} catch (SQLException ex) {
						System.err.println(ex);
						Uti1.error("SQL error", true);

					}

				} // fine run()

			}); // fine runnable

		} // fine if DIAGTXT
	  
		if (tables.equals("EDECAN") || tables.equals("ALL")) {

			javax.swing.SwingUtilities.invokeLater(new Runnable() {

				public void run() {

					ResultSet rSet;
					String q = "SELECT datupd" + ", ancode" + ", anbrtd"
							+ ", anname" + ", anaddr" + ", ancapc" + ", anloca"
							+ ", anrprv" + ", anbprv" + ", anteln" + ", antel2"
							+ ", email" + ", anprof" + ", annote" + ", anfami"
							+ ", anmena" + ", anchld" + ", an1cha" + ", ansuck"
							+ ", anmens" + ", anmenp" + ", anheig"
							+ ", anweig FROM EDECAN WHERE ancode >0";

					q = dateLim(q);

					File outFile = outName("edecan");
					System.out.println("dumping EDECAN to " + outFile);

					rSet = Jedecma.dbmgr.executeQuery(q);

					FileWriter writer = null;
					try {
						writer = new FileWriter(outFile);

						java.util.Date d;
						while (rSet.next()) {

							q = "INSERT INTO EDECAN (datupd, ancode, anbrtd, anname"
									+ ", anaddr, ancapc, anloca, anrprv"
									+ ", anbprv, anteln, antel2, email"
									+ ", anprof, annote, anfami, anmena"
									+ ", anchld, an1cha, ansuck, anmens"
									+ ", anmenp, anheig, anweig) VALUES (";

							d = rSet.getDate("datupd");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}
							q += ", DEFAULT, ";

							d = rSet.getDate("anbrtd");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}

							q += ", '" + Uti1.escape(rSet.getString("anname"))
									+ "', '"
									+ Uti1.escape(rSet.getString("anaddr"))
									+ "', '"
									+ Uti1.escape(rSet.getString("ancapc"))
									+ "', '"
									+ Uti1.escape(rSet.getString("anloca"))
									+ "', '"
									+ Uti1.escape(rSet.getString("anrprv"))
									+ "', '"
									+ Uti1.escape(rSet.getString("anbprv"))
									+ "', '"
									+ Uti1.escape(rSet.getString("anteln"))
									+ "', '"
									+ Uti1.escape(rSet.getString("antel2"))
									+ "', '"
									+ Uti1.escape(rSet.getString("email"))
									+ "', '"
									+ Uti1.escape(rSet.getString("anprof"))
									+ "', '"
									+ Uti1.escape(rSet.getString("annote"))
									+ "', '"
									+ Uti1.escape(rSet.getString("anfami"))
									+ "', " + rSet.getInt("anmena") + ", "
									+ rSet.getInt("anchld") + ", "
									+ rSet.getInt("an1cha") + ", "
									+ rSet.getInt("ansuck") + ", "
									+ rSet.getInt("anmens") + ", "
									+ rSet.getInt("anmenp") + ", "
									+ rSet.getFloat("anheig") + ", "
									+ rSet.getFloat("anweig") + ")";

							writer.write(q + "\n");

						}

						writer.close();
						System.out.println(Jedecma.localMessagesBundle
								.getString("EXPORT_EDECAN_MSG") + outFile);
						logMessage(Jedecma.localMessagesBundle
								.getString("EXPORT_EDECAN_MSG") + outFile);

					} catch (IOException ex) {
						System.err.println(ex);
						Uti1.error("file IO error", true);
					} catch (SQLException ex) {
						System.err.println(ex);
						Uti1.error("SQL error", true);

					}

				} // fine run()

			}); // fine runnable

		} // fine if EDECAN
	  
		if (tables.equals("EDECMA") || tables.equals("ALL")) {

			javax.swing.SwingUtilities.invokeLater(new Runnable() {

				public void run() {

					ResultSet rSet;
					String q = "SELECT * FROM EDECMA WHERE numarc >0";

					q = dateLim(q);

					File f = outName("edecma");
					System.out.println("dumping EDECAN to " + f);

					rSet = Jedecma.dbmgr.executeQuery(q);

					FileWriter writer = null;
					try {
						writer = new FileWriter(f);

						java.util.Date d;
						while (rSet.next()) {

							q = "INSERT INTO EDECMA (datupd" + ", numarc"
									+ ", datesa" + ", cognom" + ", datnas"
									+ ", dgneco" + ", dgnist" + ", dgncit"
									+ ", fampat" + ", termed" + ", terchi"
									+ ", tmsede" + ", tmdime" + ", datums"
									+ ", dtmamm" + ", drmamm" + ", ecbprv"
									+ ", mastse" + ", allatt" + ", citeco"
									+ ", isteco" + ", tmmalg" + ", nmrfgl"
									+ ", menarc" + ", menopa" + ", mstrzn"
									+ ", egmamm" + ", tcapez" + ", tdotti"
									+ ", mpfbcs" + ", tescrc" + ", mpmics"
									+ ", ematic" + ", mpmacs" + ", esmica"
									+ ", vgencs" + ", esmaca" + ", tsttum"
									+ ", tmcomp" + ", tmcont" + ", tmmarg"
									+ ", tmegen" + ", tmattn" + ", tmcono"
									+ ", tmtunn" + ", tmmaca" + ", tmmica"
									+ ", tmtmac" + ", tmtmic" + ", tmcoop"
									+ ", tmegcu" + ", tmsasc" + ", tmsarm"
									+ ", tmegfm" + ", tminfa" + ", tmdopp"
									+ ", taprgh" + ", taregh" + ", taingh"
									+ ", arprol" + ", fibnod" + ", esmamm"
									+ ", mastod" + ", prolid" + ", pesopz"
									+ ", altzpz" + ", spsghi" + ", tmsupe"
									+ ", tmvolu" + ", spsgh1" + ", discut"
									+ ", disfas" + ", discap" + ", opleco"
									+ ", oplmri" + ", oplelg" + ", oplfna"
									+ ", oplbio" + ", dtleco" + ", dtlmri"
									+ ", dtlfna" + ", dtlelg" + ", dtlbio"
									+ ", releco" + ", relmri" + ", relelg"
									+ ", relfna" + ", relbio" + ") VALUES (";

							d = rSet.getDate("datupd");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}
							q += ", " + rSet.getInt("numarc") + ", ";

							d = rSet.getDate("datesa");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}
							q += ", ";

							q += "'" + Uti1.escape(rSet.getString("cognom"))
									+ "', ";

							d = rSet.getDate("datnas");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}
							q += ", ";

							q += "'" + Uti1.escape(rSet.getString("dgneco"))
									+ "'" + ", '"
									+ Uti1.escape(rSet.getString("dgnist"))
									+ "'" + ", '"
									+ Uti1.escape(rSet.getString("dgncit"))
									+ "'" + ", '"
									+ Uti1.escape(rSet.getString("fampat"))
									+ "'" + ", '"
									+ Uti1.escape(rSet.getString("termed"))
									+ "'" + ", '"
									+ Uti1.escape(rSet.getString("terchi"))
									+ "'" + ", '"
									+ Uti1.escape(rSet.getString("tmsede"))
									+ "'" + ", '"
									+ Uti1.escape(rSet.getString("tmdime"))
									+ "', ";

							d = rSet.getDate("datums");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}
							q += ", ";

							d = rSet.getDate("dtmamm");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}
							q += ", ";

							q += "'" + Uti1.escape(rSet.getString("drmamm")) + "'" 
							        + ", '" + Uti1.escape(rSet.getString("ecbprv")) + "'" 
									+ ", '" + Uti1.escape(rSet.getString("mastse")) + "'"
							        + ", " + rSet.getInt("allatt") + ", "
									+ rSet.getInt("citeco") + ", "
									+ rSet.getInt("isteco") + ", "
									+ rSet.getInt("tmmalg") + ", "
									+ rSet.getInt("nmrfgl") + ", "
									+ rSet.getInt("menarc") + ", "
									+ rSet.getInt("menopa") + ", "
									+ rSet.getInt("mstrzn") + ", "
									+ rSet.getInt("egmamm") + ", "
									+ rSet.getInt("tcapez") + ", "
									+ rSet.getInt("tdotti") + ", "
									+ rSet.getInt("mpfbcs") + ", "
									+ rSet.getInt("tescrc") + ", "
									+ rSet.getInt("mpmics") + ", "
									+ rSet.getInt("ematic") + ", "
									+ rSet.getInt("mpmacs") + ", "
									+ rSet.getInt("esmica") + ", "
									+ rSet.getInt("vgencs") + ", "
									+ rSet.getInt("esmaca") + ", "
									+ rSet.getInt("tsttum") + ", "
									+ rSet.getInt("tmcomp") + ", "
									+ rSet.getInt("tmcont") + ", "
									+ rSet.getInt("tmmarg") + ", "
									+ rSet.getInt("tmegen") + ", "
									+ rSet.getInt("tmattn") + ", "
									+ rSet.getInt("tmcono") + ", "
									+ rSet.getInt("tmtunn") + ", "
									+ rSet.getInt("tmmaca") + ", "
									+ rSet.getInt("tmmica") + ", "
									+ rSet.getInt("tmtmac") + ", "
									+ rSet.getInt("tmtmic") + ", "
									+ rSet.getInt("tmcoop") + ", "
									+ rSet.getInt("tmegcu") + ", "
									+ rSet.getInt("tmsasc") + ", "
									+ rSet.getInt("tmsarm") + ", "
									+ rSet.getInt("tmegfm") + ", "
									+ rSet.getInt("tminfa") + ", "
									+ rSet.getInt("tmdopp") + ", "
									+ rSet.getInt("taprgh") + ", "
									+ rSet.getInt("taregh") + ", "
									+ rSet.getInt("taingh") + ", "
									+ rSet.getInt("arprol") + ", "
									+ rSet.getInt("fibnod") + ", "
									+ rSet.getInt("esmamm") + ", "
									+ rSet.getInt("mastod") + ", "
									+ rSet.getInt("prolid") + ", "
									+ rSet.getFloat("pesopz") + ", "
									+ rSet.getFloat("altzpz") + ", "
									+ rSet.getFloat("spsghi") + ", "
									+ rSet.getFloat("tmsupe") + ", "
									+ rSet.getFloat("tmvolu") + ", "
									+ rSet.getFloat("spsgh1") + ", "
									+ rSet.getFloat("discut") + ", "
									+ rSet.getFloat("disfas") + ", "
									+ rSet.getFloat("discap") + ", '"
									+ Uti1.escape(rSet.getString("opleco"))
									+ "'" + ", '"
									+ Uti1.escape(rSet.getString("oplmri"))
									+ "'" + ", '"
									+ Uti1.escape(rSet.getString("oplelg"))
									+ "'" + ", '"
									+ Uti1.escape(rSet.getString("oplfna"))
									+ "'" + ", '"
									+ Uti1.escape(rSet.getString("oplbio"))
									+ "', ";

							d = rSet.getDate("dtleco");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}
							q += ", ";

							d = rSet.getDate("dtlmri");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}
							q += ", ";

							d = rSet.getDate("dtlfna");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}
							q += ", ";

							d = rSet.getDate("dtlelg");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}
							q += ", ";

							d = rSet.getDate("dtlbio");
							if (!Uti1.isDateNull(d)) {
								q += "'" + Uti1.date2Ansi(d) + "'";
							} else {
								q += "null";
							}
							q += ", ";

							q += rSet.getInt("releco") + ", "
									+ rSet.getInt("relmri") + ", "
									+ rSet.getInt("relelg") + ", "
									+ rSet.getInt("relfna") + ", "
									+ rSet.getInt("relbio") + ")";

							writer.write(q + "\n");

						}

						writer.close();
						System.out.println(Jedecma.localMessagesBundle
								.getString("EXPORT_EDECMA_MSG") + f);
						logMessage(Jedecma.localMessagesBundle
								.getString("EXPORT_EDECMA_MSG") + f);

					} catch (IOException ex) {
						System.err.println(ex);
						Uti1.error("file IO error", true);
					} catch (SQLException ex) {
						System.err.println(ex);
						Uti1.error("SQL error", true);

					}

				} // fine run()

			}); // fine runnable

		} // fine if EDECMA
		
		
 	  
	  //logMessage("dump tables");
	  dmpBut.setEnabled(true);
  }

private String dateLim(String q) {
	String tmpDt = dateFrom.getText();
	if (tmpDt.length() > 0 ) {
		q += " AND datupd >= '" + Uti1.date2Ansi(Uti1.string2Date(tmpDt)) + "'"; 
	}
	tmpDt = dateTo.getText();
	if (tmpDt.length() > 0 ) {
		q += " AND datupd <= '" + Uti1.date2Ansi(Uti1.string2Date(tmpDt)) + "'"; 
	}
	return q;
}

private File outName(String bn) {
	File f = new File(Uti1.getWrkDir() + bn + ".sql" );
	int fileCnt = 0;
	while (f.exists()) {
		fileCnt++;
		f = new File(Uti1.getWrkDir() + bn + "-" + fileCnt + ".sql" );
	}
	return f;
}
  
  
 } // end_of_class SQLTools
  
