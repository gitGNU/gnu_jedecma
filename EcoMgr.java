/*
 * EcoMgr.java - examination data rows management
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

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

public class EcoMgr implements Menuable {
	private JButton selBut, delBut, newBut, prnBut, qryBut, clrBut, lstBut,
			dspBut;
	private MyJTextField nrFrom, nrTo, nameFrom, nameTo, dateFrom, dateTo;
	private EcoTableModel model;
	private JTable table;
	private JPanel mainPan;
	private final int limit = 100;
	private String lockCode;

	String qLst = "SELECT numarc, datesa, cognom, datnas, ecbprv FROM EDECMA ORDER BY numarc DESC";

	public EcoMgr() {
		lockCode = "";
	}

	public void start() {

		model = new EcoTableModel();
		table = new JTable(model);
		model.addColumn(Jedecma.localMessagesBundle.getString("EXAM_NR"));
		model.addColumn(Jedecma.localMessagesBundle.getString("EXAM_DATE"));
		model.addColumn(Jedecma.localMessagesBundle.getString("NAME"));
		model.addColumn(Jedecma.localMessagesBundle.getString("BIRTH_DATE"));
		model.addColumn(Jedecma.localMessagesBundle.getString("BIRTH_PROV"));

		if (Jedecma.jTableFont != null) {
			table.setFont(Jedecma.jTableFont);
		}
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(
				new SelectionListener(table));
		// table.getColumnModel().getSelectionModel().addListSelectionListener(listener);
		// // non necessaria?
		table.addMouseListener(new mouseSelListener());

		TableColumn column = null;
		column = table.getColumnModel().getColumn(0);
		column.setMaxWidth(65);

		column = table.getColumnModel().getColumn(1);
		column.setMaxWidth(90);
		column.setPreferredWidth(90);

		column = table.getColumnModel().getColumn(3);
		column.setMaxWidth(90);
		column.setPreferredWidth(90);

		column = table.getColumnModel().getColumn(4);
		column.setMaxWidth(40);

		mainPan = new JPanel();
		GridBagLayout gbMPan = new GridBagLayout();
		GridBagConstraints gbcMPan = new GridBagConstraints();
		mainPan.setLayout(gbMPan);

		JPanel qryPan = new JPanel();
		qryPan.setBorder(BorderFactory.createTitledBorder(" "
				+ Jedecma.localMessagesBundle.getString("EXAM_SEL") + " "));
		Uti1.bldConst(gbcMPan, 0, 0, 10, 3, 1, 0, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);
		gbMPan.setConstraints(qryPan, gbcMPan);
		mainPan.add(qryPan);

		JPanel but2Pan = new JPanel();
		Uti1.bldConst(gbcMPan, 10, 0, 2, 3, 0, 0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.VERTICAL);
		gbMPan.setConstraints(but2Pan, gbcMPan);
		GridBagLayout gbB2Pan = new GridBagLayout();
		GridBagConstraints gbcB2Pan = new GridBagConstraints();
		but2Pan.setLayout(gbB2Pan);
		mainPan.add(but2Pan);

		qryBut = new JButton(Jedecma.localMessagesBundle.getString("SEARCH"));
		qryBut.setMnemonic(KeyEvent.VK_C);
		qryBut.addActionListener(new QryListener());
		qryBut.setActionCommand(qryBut.getText());
		// qryBut.setEnabled(false);
		but2Pan.add(qryBut);
		Uti1.bldConst(gbcB2Pan, 0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL);
		gbB2Pan.setConstraints(qryBut, gbcB2Pan);

		clrBut = new JButton(Jedecma.localMessagesBundle.getString("CLEAR"));
		clrBut.setMnemonic(KeyEvent.VK_A);
		clrBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nrFrom.setText("0");
				nrTo.setText("0");
				nameFrom.setText("");
				nameTo.setText("");
				dateFrom.setText("");
				dateTo.setText("");
			}
		});
		clrBut.setActionCommand(clrBut.getText());
		// clrBut.setEnabled(false);
		but2Pan.add(clrBut);
		Uti1.bldConst(gbcB2Pan, 0, 1, 1, 1, 1, 0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL);
		gbB2Pan.setConstraints(clrBut, gbcB2Pan);

		lstBut = new JButton(
				Jedecma.localMessagesBundle.getString("LAST_ENTRIES"));
		lstBut.setMnemonic(KeyEvent.VK_T);
		lstBut.addActionListener(new LstListener());
		lstBut.setActionCommand(lstBut.getText());
		// lstBut.setEnabled(false);
		but2Pan.add(lstBut);
		Uti1.bldConst(gbcB2Pan, 0, 2, 1, 1, 1, 0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL);
		gbB2Pan.setConstraints(lstBut, gbcB2Pan);

		JScrollPane qryResPan = new JScrollPane(table);
		qryResPan.setBorder(BorderFactory.createTitledBorder(" "
				+ Jedecma.localMessagesBundle.getString("QRY_RESULT") + " "));
		Uti1.bldConst(gbcMPan, 0, 3, GridBagConstraints.REMAINDER, 4, 1, 1,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH);
		gbMPan.setConstraints(qryResPan, gbcMPan);
		mainPan.add(qryResPan);

		JPanel butPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Uti1.bldConst(gbcMPan, 0, 7, GridBagConstraints.REMAINDER, 1, 1, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL);
		gbMPan.setConstraints(butPan, gbcMPan);
		mainPan.add(butPan);

		delBut = new JButton(Jedecma.localMessagesBundle.getString("DELETE"));
		delBut.setMnemonic(KeyEvent.VK_L);
		delBut.addActionListener(new DelListener());
		delBut.setActionCommand(delBut.getText());
		delBut.setEnabled(false);
		butPan.add(delBut);

		newBut = new JButton(Jedecma.localMessagesBundle.getString("NEW"));
		newBut.addActionListener(new NewListener());
		newBut.setActionCommand(newBut.getText());
		newBut.setMnemonic(KeyEvent.VK_N);
		// newBut.setEnabled(false);
		butPan.add(newBut);

		selBut = new JButton(Jedecma.localMessagesBundle.getString("EDIT"));
		selBut.addActionListener(new SelListener());
		selBut.setActionCommand(selBut.getText());
		selBut.setMnemonic(KeyEvent.VK_D);
		selBut.setEnabled(false);
		butPan.add(selBut);

		dspBut = new JButton(Jedecma.localMessagesBundle.getString("VIEW"));
		dspBut.addActionListener(new dspListener());
		dspBut.setActionCommand(dspBut.getText());
		dspBut.setMnemonic(KeyEvent.VK_V);
		dspBut.setEnabled(false);
		butPan.add(dspBut);

		prnBut = new JButton(Jedecma.localMessagesBundle.getString("PRINT"));
		prnBut.addActionListener(new PrnListener());
		prnBut.setActionCommand(prnBut.getText());
		prnBut.setMnemonic(KeyEvent.VK_P);
		prnBut.setEnabled(false);
		butPan.add(prnBut);

		GridBagLayout gbQPan = new GridBagLayout();
		GridBagConstraints gbcQPan = new GridBagConstraints();
		qryPan.setLayout(gbQPan);

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("NR_START"));
			Uti1.bldConst(gbcQPan, 0, 0, 1, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			nrFrom = new MyJTextField(6, String.valueOf(0), new float[] { 0,
					999999 }, 1);
			nrFrom.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 1, 0, 1, 1, 0, 0);
			gbQPan.setConstraints(nrFrom, gbcQPan);
			qryPan.add(nrFrom);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("NR_END"));
			Uti1.bldConst(gbcQPan, 3, 0, 1, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			nrTo = new MyJTextField(6, String.valueOf(0), new float[] { 0,
					999999 }, 1);
			nrTo.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 4, 0, 1, 1, 0, 0);
			gbQPan.setConstraints(nrTo, gbcQPan);
			qryPan.add(nrTo);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("NAME_START"));
			Uti1.bldConst(gbcQPan, 0, 1, 1, 1, 0, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			nameFrom = new MyJTextField(30, "", new float[] {}, 7);
			nameFrom.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 1, 1, 6, 1, 0, 0);
			gbQPan.setConstraints(nameFrom, gbcQPan);
			qryPan.add(nameFrom);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("NAME_END"));
			Uti1.bldConst(gbcQPan, 0, 2, 1, 1, 0, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			nameTo = new MyJTextField(30, "", new float[] {}, 7);
			nameTo.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 1, 2, 6, 1, 0, 0);
			gbQPan.setConstraints(nameTo, gbcQPan);
			qryPan.add(nameTo);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("DATE_START"));
			Uti1.bldConst(gbcQPan, 0, 3, 1, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			dateFrom = new MyJTextField(10, "", new float[] {}, 6);
			dateFrom.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 1, 3, 1, 1, 0, 0);
			gbQPan.setConstraints(dateFrom, gbcQPan);
			qryPan.add(dateFrom);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("DATE_END"));
			Uti1.bldConst(gbcQPan, 3, 3, 1, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			dateTo = new MyJTextField(10, "", new float[] {}, 6);
			dateTo.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 4, 3, 1, 1, 0, 0);
			gbQPan.setConstraints(dateTo, gbcQPan);
			qryPan.add(dateTo);
		}

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		Uti1.bldConst(gbc, 0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);
		gb.setConstraints(mainPan, gbc);
		Jedecma.mainPan.setLayout(gb);
		Jedecma.mainPan.add(mainPan);

		qryTbl(qLst);

		Jedecma.mf.pack();
		Jedecma.mf.setVisible(true);

	}

	public int qryTbl(String q) {
		// esegue la query q e inserisce nella tabella il risultato
		// elimina dalla tabella selezione corrente
		int rows = table.getRowCount();
		for (int r = 0; r < rows; r++) {
			try {
				model.removeRow(0);
			} catch (ArrayIndexOutOfBoundsException x) {
				System.out
						.println("ArrayIndexOutOfBoundsException indice di riga:"
								+ r);
			}
		}
		ResultSet resultSet;
		resultSet = Jedecma.dbmgr.executeQuery(q, (limit + 1));
		// inserisce in tabella la nuova selezione
		try {
			ResultSetMetaData md = resultSet.getMetaData();
			int numberOfColumns = md.getColumnCount();
			while (resultSet.next()) {
				Vector<Object> nextRow = new Vector<Object>();
				for (int i = 1; i <= numberOfColumns; i++) {
					Object o = resultSet.getObject(i);
					// modifica formato colonne date
					if (i == 2 || i == 4) {
						String ds = Uti1.date2String((java.util.Date) o);
						o = (Object) ds;
					}
					nextRow.addElement(o);
				}
				model.addRow(nextRow);
			}
		} catch (SQLException ex) {
			System.err.println(ex);
			Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
		}
		rows = table.getRowCount();
		return (rows);
	}

	public void stop() {
		Jedecma.mainPan.remove(mainPan);
		Jedecma.mf.repaint();
	}

	void delImg(int nr) {
		final int nrexam = nr;
		if (nrexam > 0) {

			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					EcoImg tmpImg = new EcoImg();
					tmpImg.setExamnr(nrexam);
					File folder = new File(tmpImg.getFolder());
					if (folder.exists()) {

						ArrayList<String> fileNames = new ArrayList<String>(
								Arrays.asList(folder.list()));
						for (Iterator<String> i = fileNames.iterator(); i
								.hasNext();) {
							String file = (String) i.next();
							File fn = new File(folder.getAbsolutePath()
									+ System.getProperty("file.separator")
									+ file);
							try {
								fn.delete();
							} catch (SecurityException se) {
								System.out
										.println("cannot delete file " + file);
							}
							System.out.println("cancellato " + file);
						}

						// cancella folder
						fileNames = new ArrayList<String>(Arrays.asList(folder
								.list()));
						if (fileNames.isEmpty()) {
							System.out.println("deleting folder " + folder);
							try {
								folder.delete();
							} catch (SecurityException se) {
								System.out.println("cannot delete folder "
										+ folder);
							}
						}

					} // endif folder.exists

				} // end run()
			}); // end inner class

		} // endif nrexam
	} // end method

	public class EcoTableModel extends DefaultTableModel {
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
				int first = e.getFirstIndex();
				int last = e.getLastIndex();

				if ((rows > 0) && (r != -1) && !e.getValueIsAdjusting()) {
					dspBut.setEnabled(true);
					if (Jedecma.ak.isEnableWrite()) {
						newBut.setEnabled(true);
						delBut.setEnabled(true);
						selBut.setEnabled(true);
					}
					selBut.setEnabled(true);
					if (Jedecma.ak.isEnablePrinting()) {
						prnBut.setEnabled(true);
					}
				}

			} else if (e.getSource() == table.getColumnModel()
					.getSelectionModel() && table.getColumnSelectionAllowed()) {
				int first = e.getFirstIndex();
				int last = e.getLastIndex();
			}

			/*
			 * if (e.getValueIsAdjusting()) { // The mouse button has not yet
			 * been released }
			 */

		}
	}

	class LstListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			qryTbl(qLst);
			delBut.setEnabled(false);
			selBut.setEnabled(false);
			prnBut.setEnabled(false);
			table.clearSelection();
		}
	}

	class SelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int rows = table.getRowCount();
			int r = table.getSelectedRow();
			int c = table.getSelectedColumn();

			if (table.getRowSelectionAllowed()) {
				if ((rows > 0) && (r != -1)) {
					delBut.setEnabled(false);
					selBut.setEnabled(false);
					dspBut.setEnabled(false);
					prnBut.setEnabled(false);
					newBut.setEnabled(false);
					Integer in = (Integer) table.getModel().getValueAt(r, 0);
					int numarc = in.intValue();

					System.out.println("selected=" + numarc);

					// check lock
					String nrs = String.valueOf(numarc);
					lockCode = "EDECMA." + nrs;
					int lockrv = MyLock.chkLock(Jedecma.dbmgr, lockCode);
					if (lockrv > 0) {
						Uti1.error(
								"EcoEdit: "
										+ Jedecma.localMessagesBundle
												.getString("EXAM_DATA")
										+ nrs
										+ Jedecma.localMessagesBundle
												.getString("IN_USE") + " ("
										+ lockrv + ")", false);
					} else {

						EcoEdit ecoEd = new EcoEdit();
						ecoEd.edit(numarc);

					}

					dspBut.setEnabled(true);
					if (Jedecma.ak.isEnableWrite()) {
						delBut.setEnabled(true);
						selBut.setEnabled(true);
						newBut.setEnabled(true);
					}
					if (Jedecma.ak.isEnablePrinting()) {
						prnBut.setEnabled(true);
					}
					// table.clearSelection();
				}
			}
		}
	}

	class dspListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int rows = table.getRowCount();
			int r = table.getSelectedRow();
			int c = table.getSelectedColumn();

			if (table.getRowSelectionAllowed()) {
				if ((rows > 0) && (r != -1)) {
					delBut.setEnabled(false);
					selBut.setEnabled(false);
					prnBut.setEnabled(false);
					newBut.setEnabled(false);
					Integer in = (Integer) table.getModel().getValueAt(r, 0);
					int numarc = in.intValue();

					System.out.println("selected=" + numarc);

					// check lock
					String nrs = String.valueOf(numarc);
					lockCode = "EDECMA." + nrs;
					System.out.println("checking lock " + lockCode);
					int lockrv = MyLock.chkLock(Jedecma.dbmgr, lockCode);
					if (lockrv > 0) {
						Uti1.error(
								"EcoEdit: "
										+ Jedecma.localMessagesBundle
												.getString("EXAM_DATA")
										+ nrs
										+ Jedecma.localMessagesBundle
												.getString("IN_USE") + " ("
										+ lockrv + ")", false);
					} else {

						EcoEdit ecoEd = new EcoEdit(true);
						ecoEd.edit(numarc);
					}
					dspBut.setEnabled(true);
					if (Jedecma.ak.isEnableWrite()) {
						delBut.setEnabled(true);
						selBut.setEnabled(true);
						newBut.setEnabled(true);
					}
					if (Jedecma.ak.isEnablePrinting()) {
						prnBut.setEnabled(true);
					}

					// table.clearSelection();
				}
			}
		}
	}

	class NewListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			delBut.setEnabled(false);
			selBut.setEnabled(false);
			prnBut.setEnabled(false);

			// deve fare selezione anagrafica
			AnaMgr anaMgr = new AnaMgr();
			anaMgr.start2();
			System.out.println("selected=" + anaMgr.selAna);
			if (anaMgr.selAna > 0) {
				EcoEdit ecoEd = new EcoEdit();
				ecoEd.edit("dummy", anaMgr.selAna);
				delBut.setEnabled(true);
				selBut.setEnabled(true);
				if (Jedecma.ak.isEnablePrinting()) {
					prnBut.setEnabled(true);
				}
			}
			qryTbl(qLst);
			// table.clearSelection();
		}
	}

	class QryListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			nameFrom.setText(Uti1.uniqSpaces(nameFrom.getText().trim())
					.toUpperCase());
			nameTo.setText(Uti1.uniqSpaces(nameTo.getText().trim())
					.toUpperCase());
			dateFrom.setText(Uti1.date2String(Uti1.string2Date(dateFrom
					.getText().trim())));
			dateTo.setText(Uti1.date2String(Uti1.string2Date(dateTo.getText()
					.trim())));

			String q = "SELECT numarc, datesa, cognom, datnas, ecbprv FROM EDECMA WHERE numarc is NOT NULL";

			{
				int n = Integer.parseInt(nrFrom.getText());
				if (n > 0) {
					q += " AND numarc >= " + String.valueOf(n);
				}
			}

			{
				int n = Integer.parseInt(nrTo.getText());
				if (n > 0) {
					q += " AND numarc <= " + String.valueOf(n);
				}
			}

			{
				String n = Uti1.escape(nameFrom.getText());
				if (!n.equals("")) {
					if (nameTo.getText().equals("")) {
						q += " AND cognom like '" + n + "%'";
					} else {
						q += " AND cognom >= '" + n + "'";
					}
				}
			}

			{
				String n = Uti1.escape(nameTo.getText());
				if (!n.equals("")) {
					q += " AND cognom <= '" + n + "'";
				}
			}

			{
				String n = Uti1.date2Ansi(dateFrom.getText());
				if (!n.equals("")) {
					q += " AND datesa >= '" + n + "'";
				}
			}

			{
				String n = Uti1.date2Ansi(dateTo.getText());
				if (!n.equals("")) {
					q += " AND datesa <= '" + n + "'";
				}
			}

			q += " ORDER BY numarc DESC";

			if (qryTbl(q) > limit) {
				String s = "<html><body><center>";
				s += Jedecma.localMessagesBundle
						.getString("SELECTION_TOO_WIDE");
				s += "</center></body></html>";
				JOptionPane.showMessageDialog(null, s, "Info",
						JOptionPane.INFORMATION_MESSAGE);
			}
			delBut.setEnabled(false);
			selBut.setEnabled(false);
			prnBut.setEnabled(false);
			table.clearSelection();
		}
	}

	class PrnListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			int rows = table.getRowCount();
			int r = table.getSelectedRow();
			int c = table.getSelectedColumn();
			if ((rows > 0) && (r != -1)) {
				Integer in = (Integer) table.getModel().getValueAt(r, 0);
				int numarc = in.intValue();
				EcoPrint ep = new EcoPrint();
				ep.exe(numarc);
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}

	class DelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int rows = table.getRowCount();
			int r = table.getSelectedRow();
			int c = table.getSelectedColumn();

			if ((rows > 0) && (r != -1)) {

				if (Uti1.msgYN(Jedecma.localMessagesBundle
						.getString("DELETE_CONFIRMATION"), "",
						JOptionPane.ERROR_MESSAGE, null) == 1) {

					delBut.setEnabled(false);
					selBut.setEnabled(false);
					prnBut.setEnabled(false);
					Integer numarc = (Integer) table.getModel()
							.getValueAt(r, 0);

					// check lock
					String nrs = String.valueOf(numarc);
					lockCode = "EDECMA." + nrs;
					int lockrv = MyLock.setLock(Jedecma.dbmgr, lockCode,
							Jedecma.user.userid, true);
					if (lockrv < 0) {
						Uti1.error(
								"EcoEdit: "
										+ Jedecma.localMessagesBundle
												.getString("EXAM_DATA")
										+ nrs
										+ Jedecma.localMessagesBundle
												.getString("IN_USE") + " ("
										+ lockrv + ")", false);
					} else {
						int rc;
						rc = Jedecma.dbmgr
								.executeUpdate("DELETE FROM EDECMA WHERE numarc="
										+ numarc);
						if (rc != 1) {
							Uti1.error(
									"EcoMgr: "
											+ Jedecma.localMessagesBundle
													.getString("DELETE_FAILURE"),
									true);
						}
						rc = Jedecma.dbmgr
								.executeUpdate("DELETE FROM DIAGTXT WHERE examnr="
										+ numarc);
						Jedecma.dbmgr
								.executeUpdate("DELETE FROM ECOIMG WHERE examnr="
										+ numarc); // elimina da database
						delImg(numarc); // elimina immagini e cancella folder

						try { // toglie la riga dalla tabella
							model.removeRow(r);
						} catch (ArrayIndexOutOfBoundsException x) {
							System.out
									.println("ArrayIndexOutOfBoundsException indice di riga:"
											+ r
											+ " righe: "
											+ table.getRowCount());
						}

						// rimuove lock
						if (lockCode.length() > 0) {
							MyLock.delLock(Jedecma.dbmgr, lockCode,
									Jedecma.user.userid);
						}
					}

				} // end if conferma

			} else {
				Toolkit.getDefaultToolkit().beep();
			}
			table.clearSelection();
		}
	}

	public class mouseSelListener extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			if (evt.getClickCount() == 2) {
				// double-click
				int rows = table.getRowCount();
				int r = table.getSelectedRow();
				int c = table.getSelectedColumn();

				if (table.getRowSelectionAllowed()) {
					if ((rows > 0) && (r != -1)) {
						delBut.setEnabled(false);
						selBut.setEnabled(false);
						prnBut.setEnabled(false);
						newBut.setEnabled(false);
						dspBut.setEnabled(false);
						Integer in = (Integer) table.getModel()
								.getValueAt(r, 0);
						int numarc = in.intValue();

						System.out.println("selected=" + numarc);

						// check lock
						String nrs = String.valueOf(numarc);
						lockCode = "EDECMA." + nrs;
						System.out.println("checking lock " + lockCode);
						int lockrv = MyLock.chkLock(Jedecma.dbmgr, lockCode);
						if (lockrv > 0) {
							Uti1.error(
									"EcoEdit: "
											+ Jedecma.localMessagesBundle
													.getString("EXAM_DATA")
											+ nrs
											+ Jedecma.localMessagesBundle
													.getString("IN_USE") + " ("
											+ lockrv + ")", false);
						} else {

							EcoEdit ecoEd = new EcoEdit(true);
							ecoEd.edit(numarc);

						}

						dspBut.setEnabled(true);
						if (Jedecma.ak.isEnableWrite()) {
							delBut.setEnabled(true);
							selBut.setEnabled(true);
							newBut.setEnabled(true);
						}
						if (Jedecma.ak.isEnablePrinting()) {
							prnBut.setEnabled(true);
						}
						// table.clearSelection();
					}
				}
			}
		}
	}

} // end EcoMgr class
