/*  
 * FileMgr.java - (image) file management 
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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.event.*;

public class FileMgr extends JPanel {
	private static final long serialVersionUID = 1L;
	int nrexam;
	private FileTableModel model;
	private JTable table;
	private JButton selBut, newBut, delBut;
	private ArrayList<String> fileNames = new ArrayList<String>();
	private EcoEdit parent;
	private JFileChooser fc = new JFileChooser();
	final int COL_FILE = 0;
	final int COL_OP = 1;
	final int COL_NOTE = 2;
	final int NOTE_SIZE = 128;

	public FileMgr(int nr, EcoEdit p) {
		parent = p;
		nrexam = nr;
		setGui();
	}
	
	private void setGui() {
		model = new FileTableModel();
		table = new JTable(model);
		model.addColumn(""); // "filename");
		model.addColumn("Op"); // ("operazione: C=Copia; D=Delete");
		model.addColumn(Jedecma.localMessagesBundle.getString("IMG_COL_LABEL"));
		
		TableColumn column = null;
		column = table.getColumnModel().getColumn(COL_OP);
		column.setMaxWidth(25);
		column = table.getColumnModel().getColumn(COL_NOTE);
		column.setCellEditor(new NoteEditor());
		column = table.getColumnModel().getColumn(COL_FILE);
		column.setMinWidth(0);
		column.setMaxWidth(0);
		column.setPreferredWidth(0);
		
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		SelectionListener listener = new SelectionListener(table);
		table.getSelectionModel().addListSelectionListener(listener);
		table.addMouseListener(new mouseSelListener());
		// table.getColumnModel().getColumn(1).setCellRenderer(new ImageRenderer());

		GridBagLayout gbMPan = new GridBagLayout();
		GridBagConstraints gbcMPan = new GridBagConstraints();
		setLayout(gbMPan);

		JScrollPane fileScrollPan = new JScrollPane(table);

		Uti1.bldConst(gbcMPan, 1, 0, 10, 10, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);
		gbMPan.setConstraints(fileScrollPan, gbcMPan);
		add(fileScrollPan);

		selBut = new JButton();
		selBut = new JButton(Jedecma.localMessagesBundle.getString("DSP_IMG"));
		selBut.setMnemonic(KeyEvent.VK_D);
		selBut.addActionListener(new SelListener());
		Uti1.bldConst(gbcMPan, 0, 0, 1, 1, 0, 0);
		gbMPan.setConstraints(selBut, gbcMPan);
		add(selBut);

		newBut = new JButton();
		newBut = new JButton(Jedecma.localMessagesBundle.getString("NEW_IMG"));
		newBut.setMnemonic(KeyEvent.VK_I);
		newBut.addActionListener(new NewListener());
		Uti1.bldConst(gbcMPan, 0, 1, 1, 1, 0, 0);
		gbMPan.setConstraints(newBut, gbcMPan);
		add(newBut);

		delBut = new JButton();
		delBut = new JButton(Jedecma.localMessagesBundle.getString("DEL_IMG"));
		delBut.setMnemonic(KeyEvent.VK_I);
		delBut.addActionListener(new DelListener());
		Uti1.bldConst(gbcMPan, 0, 2, 1, 1, 0, 0);
		gbMPan.setConstraints(delBut, gbcMPan);
		add(delBut);

		delBut.setEnabled(false);
		selBut.setEnabled(false);
		if ((Jedecma.ak.isEnableWrite() == false) || parent.readOnly
				|| parent.busy) {
			newBut.setEnabled(false);
		}

		clearTable();

	}

	void clearTable() { // elimina contenuto tabella
		int rows = table.getRowCount();
		for (int r = 0; r < rows; r++) {
			try {
				model.removeRow(0);
			} catch (ArrayIndexOutOfBoundsException x) {
				System.out
						.println("clearTable(): ArrayIndexOutOfBoundsException indice di riga:"
								+ r);
			}
		}
	}
	
	public int getTableSize() {
		return table.getRowCount();
	}
	
	void storeFiles() { 
		// copia i files di tipo "C" indicati in tabella
		// cancella i files di tipo "D" indicati in tabella
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				int rows = table.getRowCount();
				for (int r = 0; r < rows; r++) {
					String op = (String) table.getModel().getValueAt(r, COL_OP);
					if (op.equals("C")) {
						String fileBaseName = (String) table.getModel()
								.getValueAt(r, COL_FILE);
						String srcFile = getTmpPath()
								+ System.getProperty("file.separator")
								+ fileBaseName;
						String newFile = getFolder()
								+ System.getProperty("file.separator")
								+ fileBaseName;

						// verifica se esiste folder ed event. lo crea
						File folder = new File(getFolder());
						if (!folder.exists()) {
							try {
								System.out.println("creating folder "
										+ folder.getName());
								folder.mkdirs();
							} catch (SecurityException se) {
								System.out
										.println("Security exception creating folder "
												+ folder);
							}
						}
						if (folder.exists()) {
							copyFile(srcFile, newFile);
							System.out.println("copy from " + srcFile + " to "
									+ newFile);
						} // end if folder
						
						String note = (String) table.getModel().getValueAt(r, COL_NOTE);
						if ( note.equals("") ) { note = fileBaseName; }
						EcoImg img =findEcoImg(nrexam, fileBaseName); 
						if ( img.getExamnr() == nrexam) { // esiste: scrivo solo se nota diversa																
							if (! img.getNote().equals(note)) {  
								img.setNote(note);								
   								img.writeEcoImg(img.getId());
							}

						} else { // manca
							img.setBasename(fileBaseName); // basename
							img.setNote(note); // note
							img.setExamnr(nrexam);
							img.writeEcoImg(0);
						}							
					} // end if "C"
					if (op.equals("D")) {
							String fileBaseName = (String) table.getModel()
									.getValueAt(r, COL_FILE);
							
							String fileToDel = getFolder()
									+ System.getProperty("file.separator")
									+ fileBaseName;
							
							File fn = new File (fileToDel);
							if ( fn.exists()) {
							  try {
							    fn.delete();
							    System.out.println("deleting file " + fileBaseName);
						      } catch (SecurityException se) {
							    System.out.println("cannot delete file " + fileBaseName);
						      }
							}
							EcoImg img =findEcoImg(nrexam, fileBaseName); 
							if ( img.getExamnr() == nrexam) { // esiste
								img.deleteEcoImg();
							}
					}  // end if "D"
					if (op.equals("")) {
						String fileBaseName = (String) table.getModel()
								.getValueAt(r, COL_FILE);
						EcoImg img =findEcoImg(nrexam, fileBaseName); 
						if ( img.getExamnr() == nrexam) { // esiste
							String note = (String) table.getModel().getValueAt(r, COL_NOTE);
							if (! img.getNote().equals(note)) { // scrive solo se e' diversa
								img.setNote(note);
								img.writeEcoImg(img.getId());
							}								
						}				
					} // end if ""
				} // end for
			} // end run()
		}); // end inner class

	} // end method

	void loadTable() { // carica tabella
		clearTable();
		File path = new File(getFolder());
		if (path.exists()) {
			fileNames = readFolder(path);
			for (Iterator<String> i = fileNames.iterator(); i.hasNext();) {
				String item = (String) i.next();
				String extension = Uti1.getExtension(new File (item));
				extension = extension.toLowerCase();
				if (extension.equals("jpg")
						|| extension.equals("png")
						|| extension.equals("bmp") ) {
				  // System.out.println(item);
				  Vector<Object> nextRow = new Vector<Object>();
				  nextRow.addElement(item);
				  nextRow.addElement(""); // "" = nessuna operazione
				  // cerca commento associato al nume file
				  EcoImg img =findEcoImg(nrexam, item);
				  String note = item;
				  if ( img.getExamnr() == nrexam) { // esiste: carica note
					note = img.getNote();
				  }	
				  nextRow.addElement(note); // note
				  model.addRow(nextRow);
			} // extension
			}
		}
	}

	private EcoImg findEcoImg(int nr, String bn) {
		EcoImg img = new EcoImg();
		img.readEcoImg(nr, bn); 
		return img; 
	}
	
	private String getFolder() {
		EcoImg tmpImg = new EcoImg();
		tmpImg.setExamnr(nrexam);
		return tmpImg.getFolder();
	}

	private String getTmpPath() {
		return Jedecma.param.getProperty("imgtmp");
	}

	private ArrayList<String> readFolder(File fld) {
		ArrayList<String> fn = new ArrayList<String>(Arrays.asList(fld.list()));
		System.out.println(fn.size() + " files in folder");
		return fn;
	}

	private int copyFile(String sr, String dt) {
		try {
			File f1 = new File(sr);
			File f2 = new File(dt);
			InputStream in = new FileInputStream(f1);
			OutputStream out = new FileOutputStream(f2);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			return 1;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return 2;
		}
		return 0;
	}

	public class FileTableModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int col) {
			
			if ((col == COL_NOTE) 
				&& (Jedecma.ak.isEnableWrite() == true) 
				&& !(parent.readOnly)
				&& !(parent.busy)) {						
                return true;
            } else {
			    return false;
            }
		}

	}

	public class SelectionListener implements ListSelectionListener {
		JTable table;

		// It is necessary to keep the table since it is not possible
		// to determine the table from the event's source
		SelectionListener(JTable table) {
			this.table = table;
		}

		public void valueChanged(ListSelectionEvent e) {
			// If cell selection is enabled, both row and column
			// change events are fired
			int rows = table.getRowCount();
			int r = table.getSelectedRow();
			int c = table.getSelectedColumn();

			if (e.getSource() == table.getSelectionModel()
					&& table.getRowSelectionAllowed()) {
				// System.out.println("Row selection changed" + r);
				int first = e.getFirstIndex();
				int last = e.getLastIndex();

				if ((rows > 0) && (r != -1) && !e.getValueIsAdjusting()) {
					System.out.println("Row selected " + r);
					delBut.setEnabled(true);
					selBut.setEnabled(true);
					if ((Jedecma.ak.isEnableWrite() == false)
							|| parent.readOnly || parent.busy) {
						delBut.setEnabled(false);
					}
				}

			} else if (e.getSource() == table.getColumnModel()
					.getSelectionModel() && table.getColumnSelectionAllowed()) {
				// System.out.println("Column selection changed");
				int first = e.getFirstIndex();
				int last = e.getLastIndex();
			}

			/*
			 * if (e.getValueIsAdjusting()) { // The mouse button has not yet
			 * been released }
			 */

		}
	}

	public class mouseSelListener extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			if (evt.getClickCount() == 2) {

				delBut.setEnabled(false);
				newBut.setEnabled(false);
				selBut.setEnabled(false);

				// double-click
				int rows = table.getRowCount();
				int r = table.getSelectedRow();
				int c = table.getSelectedColumn();

				if (table.getRowSelectionAllowed()) {
					if ((rows > 0) && (r != -1)) {
					
						String fn = "";
						if (((String) table.getModel().getValueAt(r, COL_OP))
								.equals("C")) {
							fn = getTmpPath()
									+ System.getProperty("file.separator")
									+ ((String) table.getModel().getValueAt(r, COL_FILE));
						} else {
							fn = getFolder() + System.getProperty("file.separator")
									+ ((String) table.getModel().getValueAt(r, COL_FILE));
						}
						System.out.println("File (mouse) selected " + fn);
						
						final String file = fn;
						javax.swing.SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								new ImgViewer(file, parent);
							}
						});

					}
				}

				if ((Jedecma.ak.isEnableWrite() == true) && !(parent.readOnly)
						&& !(parent.busy)) {
					newBut.setEnabled(true);
				}
				table.clearSelection();
			}
		}
	}

	class SelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			delBut.setEnabled(false);
			newBut.setEnabled(false);
			selBut.setEnabled(false);

			int rows = table.getRowCount();
			int r = table.getSelectedRow();
			int c = table.getSelectedColumn();

			if (table.getRowSelectionAllowed()) {
				if ((rows > 0) && (r != -1)) {
					String fn = "";
					if (((String) table.getModel().getValueAt(r, COL_OP))
							.equals("C")) {
						fn = getTmpPath()
								+ System.getProperty("file.separator")
								+ ((String) table.getModel().getValueAt(r, COL_FILE));
					} else {
						fn = getFolder() + System.getProperty("file.separator")
								+ ((String) table.getModel().getValueAt(r, COL_FILE));
					}
					System.out.println("File (button) selected " + fn);
					
					final String file = fn;
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							new ImgViewer(file, parent);
						}
					});
					
				}
			}
			if ((Jedecma.ak.isEnableWrite() == true) && !(parent.readOnly)
					&& !(parent.busy)) {
				newBut.setEnabled(true);
			}
			table.clearSelection();
		}
	}

	class NewListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			delBut.setEnabled(false);
			newBut.setEnabled(false);
			selBut.setEnabled(false);

			System.out.println("New button selected ");

			fc.addChoosableFileFilter(new MyFileFilter());
			fc.setCurrentDirectory(new File(getTmpPath()));
			fc.setMultiSelectionEnabled(true);
			int returnVal = fc.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				final File[] fileArray = fc.getSelectedFiles();

				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						for (int fi = 0; fi < fileArray.length; fi++) {

							String item = fileArray[fi].getName();

							Vector<Object> nextRow = new Vector<Object>();
							nextRow.addElement(item); // nome file
							nextRow.addElement("C"); // C=Copy
							String note = item;
							EcoImg img =findEcoImg(nrexam, item); 
  						    if ( img.getExamnr() == nrexam) { // esiste
								note = img.getNote();
							}
							nextRow.addElement(note); // note
							model.addRow(nextRow);
							System.out.println("addRow "
									+ fileArray[fi].getAbsolutePath());

						} // end for array files
					} // end run()
				}); // end inner class
			}

			// delBut.setEnabled(false);
			// selBut.setEnabled(false);
			if ((Jedecma.ak.isEnableWrite() == true) && !(parent.readOnly)
					&& !(parent.busy)) {
				newBut.setEnabled(true);
			}
			table.clearSelection();
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
				if (extension.equals("jpg") || extension.equals("png")
						|| extension.equals("bmp")) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		}

		public String getDescription() {
			return "images";
		}
	}

	class DelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int rows = table.getRowCount();
			int r = table.getSelectedRow();
			int c = table.getSelectedColumn();

			if ((rows > 0) && (r != -1)) {
				delBut.setEnabled(false);
				selBut.setEnabled(false);
				newBut.setEnabled(false);

				{
					String op = (String) table.getModel()
							.getValueAt(r, COL_OP);
					if (op.equals("")) {
						table.getModel().setValueAt("D", r, COL_OP);
					} else if (op.equals("D")) {
						table.getModel().setValueAt("", r, COL_OP);
					} else if (op.equals("C")) {
						model.removeRow(r);
					}					
				}
				// delBut.setEnabled(true);
				// selBut.setEnabled(true);
				if ((Jedecma.ak.isEnableWrite() == true) && !(parent.readOnly)
						&& !(parent.busy)) {
					newBut.setEnabled(true);
				}
			}
			table.clearSelection();
		}
	}
	
	   public class NoteEditor extends AbstractCellEditor implements TableCellEditor {
	        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
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
		    if (text.length() > NOTE_SIZE) {
		       text = text.substring(0, NOTE_SIZE);
		    }
	            return (text.trim());
	        } 

	    }


} // end Class
