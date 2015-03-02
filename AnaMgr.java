/*
 * AnaMgr.java - patient data rows management
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

package jedecma;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.sql.*;
import java.util.Vector;

public class AnaMgr implements Menuable {
	private static final String delString = Jedecma.localMessagesBundle
			.getString("DELETE");
	private static String selString = Jedecma.localMessagesBundle
			.getString("EDIT");
	private static final String newString = Jedecma.localMessagesBundle
			.getString("NEW");
	private static final String qryString = Jedecma.localMessagesBundle
			.getString("SEARCH");
	private static final String clrString = Jedecma.localMessagesBundle
			.getString("CLEAR");
	private JButton selBut, delBut, newBut, qryBut, clrBut;
	private MyJTextField name, bDate, bProv;
	int selAna;
	private EcoTableModel model;
	private JTable table;
	private JPanel mainPan, butPan, qryPan, but2Pan;
	private final int limit = 100;
	private String lockCode;
	private boolean seleOnly = false;
	private String qLst = "SELECT anname, anbrtd, anbprv, ancode FROM EDECAN ORDER BY anname";
	private JDialog d = new JDialog();

	public AnaMgr() {
	}

	public void stop2() {
		d.dispose();
	}

	public void start2() {
		seleOnly = true;
		selString = Jedecma.localMessagesBundle.getString("SELECT");
		d.setModal(true);
		JPanel p = new JPanel();
		d.getContentPane().add(p);
		doGui(p);

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		Uti1.bldConst(gbc, 0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);
		gb.setConstraints(mainPan, gbc);
		p.setLayout(gb);
		p.add(mainPan);

		qryTbl(qLst);

		d.pack();
		d.setVisible(true);
	}

	public void start() {
		doGui(Jedecma.mainPan);

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

	public void doGui(Container contPan) {

		model = new EcoTableModel();
		table = new JTable(model);
		model.addColumn(Jedecma.localMessagesBundle.getString("NAME"));
		model.addColumn(Jedecma.localMessagesBundle.getString("BIRTH_DATE"));
		model.addColumn(Jedecma.localMessagesBundle.getString("BIRTH_PROV"));
		model.addColumn(Jedecma.localMessagesBundle.getString("CODE"));

		if (Jedecma.jTableFont != null) {
			table.setFont(Jedecma.jTableFont);
		}
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		SelectionListener listener = new SelectionListener(table);
		table.getSelectionModel().addListSelectionListener(listener);
		table.addMouseListener(new mouseSelListener());

		TableColumn column = null;
		column = table.getColumnModel().getColumn(0);

		column = table.getColumnModel().getColumn(1);
		column.setMaxWidth(90);
		column.setPreferredWidth(80);

		column = table.getColumnModel().getColumn(2);
		column.setMaxWidth(60);
		column.setPreferredWidth(40);

		column = table.getColumnModel().getColumn(3);
		column.setPreferredWidth(80);
		column.setMaxWidth(90);

		mainPan = new JPanel();
		GridBagLayout gbMPan = new GridBagLayout();
		GridBagConstraints gbcMPan = new GridBagConstraints();
		mainPan.setLayout(gbMPan);

		qryPan = new JPanel();
		qryPan.setBorder(BorderFactory.createTitledBorder(" "
				+ Jedecma.localMessagesBundle.getString("PAT_SEL") + " "));
		Uti1.bldConst(gbcMPan, 0, 0, 10, 3, 1, 0, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);
		gbMPan.setConstraints(qryPan, gbcMPan);
		mainPan.add(qryPan);

		but2Pan = new JPanel();
		// but2Pan.setBorder(BorderFactory.createTitledBorder(""));
		Uti1.bldConst(gbcMPan, 10, 0, 2, 3, 0, 0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.VERTICAL);
		gbMPan.setConstraints(but2Pan, gbcMPan);
		GridBagLayout gbB2Pan = new GridBagLayout();
		GridBagConstraints gbcB2Pan = new GridBagConstraints();
		but2Pan.setLayout(gbB2Pan);
		mainPan.add(but2Pan);

		qryBut = new JButton(qryString);
		qryBut.setMnemonic(KeyEvent.VK_C);
		qryBut.addActionListener(new QryListener());
		qryBut.setActionCommand(qryString);
		but2Pan.add(qryBut);
		Uti1.bldConst(gbcB2Pan, 10, 0, 1, 1, 1, 0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL);
		gbB2Pan.setConstraints(qryBut, gbcB2Pan);

		clrBut = new JButton(clrString);
		clrBut.setMnemonic(KeyEvent.VK_A);

		clrBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				name.setText("");
				bDate.setText("");
				bProv.setText("");
			}
		});

		clrBut.setActionCommand(clrString);
		but2Pan.add(clrBut);
		Uti1.bldConst(gbcB2Pan, 10, 1, 1, 1, 1, 0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL);
		gbB2Pan.setConstraints(clrBut, gbcB2Pan);

		JScrollPane qryResPan = new JScrollPane(table);
		qryResPan.setBorder(BorderFactory.createTitledBorder(" "
				+ Jedecma.localMessagesBundle.getString("QRY_RESULT") + " "));
		Uti1.bldConst(gbcMPan, 0, 3, GridBagConstraints.REMAINDER, 4, 1, 1,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH);
		gbMPan.setConstraints(qryResPan, gbcMPan);
		mainPan.add(qryResPan);

		butPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Uti1.bldConst(gbcMPan, 0, 7, GridBagConstraints.REMAINDER, 1, 1, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL);
		gbMPan.setConstraints(butPan, gbcMPan);
		mainPan.add(butPan);

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
		// newBut.setEnabled(false);
		butPan.add(newBut);

		selBut = new JButton(selString);
		selBut.addActionListener(new SelListener());
		selBut.setActionCommand(selString);
		selBut.setMnemonic(KeyEvent.VK_E);
		selBut.setEnabled(false);
		butPan.add(selBut);

		GridBagLayout gbQPan = new GridBagLayout();
		GridBagConstraints gbcQPan = new GridBagConstraints();
		qryPan.setLayout(gbQPan);

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("NAME"));
			Uti1.bldConst(gbcQPan, 0, 0, 1, 1, 0, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			name = new MyJTextField(30, "", new float[] {}, 7);
			name.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 1, 0, 4, 1, 1, 0);
			gbQPan.setConstraints(name, gbcQPan);
			qryPan.add(name);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("BIRTH_DATE"));
			Uti1.bldConst(gbcQPan, 0, 1, 1, 1, 0, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			bDate = new MyJTextField(10, "", new float[] {}, 6);
			bDate.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 1, 1, 1, 1, 0, 0);
			gbQPan.setConstraints(bDate, gbcQPan);
			qryPan.add(bDate);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("BIRTH_PROV"));
			Uti1.bldConst(gbcQPan, 2, 1, 1, 1, 0, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			bProv = new MyJTextField(4, "", new float[] {}, 7);
			bProv.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 3, 1, 1, 1, 0, 0);
			gbQPan.setConstraints(bProv, gbcQPan);
			qryPan.add(bProv);
		}

	} //

	public int qryTbl(String q) {
		// esegue la query q e inserisce nella tabella il risultato
		// elimina dalla tabella selezione corrente
		int rows = table.getRowCount();
		// System.out.println("rows=" + rows);
		for (int r = 0; r < rows; r++) {
			// System.out.println("r=" + r);
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
				Vector nextRow = new Vector();
				for (int i = 1; i <= numberOfColumns; i++) {
					Object o = resultSet.getObject(i);
					// modifica formato colonne date
					if (i == 2) {
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

	public class EcoTableModel extends DefaultTableModel {
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
				// System.out.println("Row selection changed" + r);
				int first = e.getFirstIndex();
				int last = e.getLastIndex();

				if ((rows > 0) && (r != -1) && !e.getValueIsAdjusting()) {
					delBut.setEnabled(true);
					selBut.setEnabled(true);
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

	class SelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int rows = table.getRowCount();
			int r = table.getSelectedRow();
			int c = table.getSelectedColumn();

			if (table.getRowSelectionAllowed()) {
				if ((rows > 0) && (r != -1)) {
					delBut.setEnabled(false);
					selBut.setEnabled(false);
					Integer ancode = (Integer) table.getModel()
							.getValueAt(r, 3);
					int nr = ancode.intValue();
					if (!seleOnly) {
						AnaEdit anaEdit = new AnaEdit();
						anaEdit.edit(nr);
						delBut.setEnabled(true);
						selBut.setEnabled(true);
					} else {
						selAna = nr;
						if (nr > 0) {
							stop2();
						}
					}
				}
			}
		}
	}

	class NewListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			delBut.setEnabled(false);
			selBut.setEnabled(false);

			AnaEdit anaEd = new AnaEdit();

			anaEd.edit(0);

			System.out.println("NEW ancode= " + String.valueOf(anaEd.ancode));
			if (anaEd.ancode > 0) {
				Integer i = new Integer(anaEd.ancode);
				model.addRow(new Object[] { anaEd.anname.getText(),
						anaEd.anbrtd.getText(), anaEd.anbprv.getText(), i });

			}

			delBut.setEnabled(true);
			selBut.setEnabled(true);
			// table.clearSelection();
		}
	}

	class QryListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			name.setText(Uti1.uniqSpaces(name.getText().trim()).toUpperCase());
			bDate.setText(Uti1.date2String(Uti1.string2Date(bDate.getText()
					.trim())));
			bProv.setText(bProv.getText().trim().toUpperCase());

			String n1;
			String q = "SELECT anname, anbrtd, anbprv, ancode FROM EDECAN WHERE ancode is NOT NULL";

			n1 = Uti1.escape(name.getText());
			if (!n1.equals("")) {
				q += " AND anname like '" + n1 + "%'";
			}

			n1 = Uti1.date2Ansi(bDate.getText());
			if (!n1.equals("")) {
				q += " AND anbrtd = '" + n1 + "'";
			}

			n1 = Uti1.escape(bProv.getText());
			if (!n1.equals("")) {
				q += " AND anbprv = '" + n1 + "'";
			}

			q += " ORDER BY anname";

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
			table.clearSelection();
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
					Integer ancode = (Integer) table.getModel()
							.getValueAt(r, 3);

					// lock
					String nrs = String.valueOf(ancode);
					lockCode = "EDECAN." + nrs;
					int lockrv = MyLock.setLock(Jedecma.dbmgr, lockCode,
							Jedecma.user.userid, MyLock.EXCL);
					if (lockrv < 0) {
						Uti1.error(
								"EcoEdit: "
										+ Jedecma.localMessagesBundle
												.getString("PAT_DATA")
										+ nrs
										+ Jedecma.localMessagesBundle
												.getString("IN_USE") + " ("
										+ lockrv + ")", false);
					} else {

						int rc;
						rc = Jedecma.dbmgr
								.executeUpdate("DELETE FROM EDECAN WHERE ancode="
										+ ancode);
						if (rc != 1) {
							Uti1.error(
									"AnaMgr: "
											+ Jedecma.localMessagesBundle
													.getString("DELETE_FAILURE"),
									true);

						}

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

				} // fine if richiesta conferma

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
						Integer ancode = (Integer) table.getModel().getValueAt(
								r, 3);
						int nr = ancode.intValue();
						if (!seleOnly) {
							AnaEdit anaEdit = new AnaEdit();
							anaEdit.edit(nr);
							delBut.setEnabled(true);
							selBut.setEnabled(true);
						} else {
							selAna = nr;
							if (nr > 0) {
								stop2();
							}
						}
					}
				}
			}
		}
	}

} // end AnaMgr class
