/*  
 * ZipCodeSrc.java - Zip Codes search window
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class ZipCodeSrc extends JDialog {

	private static final long serialVersionUID = 1L;
	private JButton selBut, qryBut, clrBut;
	private static final String qryString = Jedecma.localMessagesBundle
			.getString("SEARCH");
	private static final String clrString = Jedecma.localMessagesBundle
			.getString("CLEAR");
	private static String selString = Jedecma.localMessagesBundle
			.getString("CB_OK");
	private JTextField zipCode, city, prov;
	private DspDataModel model;
	private JTable table;
	private JPanel mainPan, butPan, qryPan, but2Pan;
	private String selCode, selCity, selProv;
	private boolean itemSelected;
	private final int limit = 100;

	public void setSelCode(String selCode) {
		if (Integer.parseInt(selCode) > 0) {
			this.selCode = selCode;
		}
	}

	public void setSelCity(String selCity) {
		this.selCity = selCity;
	}

	public void setSelProv(String selProv) {
		this.selProv = selProv;
	}

	public boolean itemSelected() {
		return itemSelected;
	}

	public ZipCodeSrc() {
		super(Jedecma.mf, Jedecma.localMessagesBundle.getString("CITY"), true); // E' un JDialog modale!
		selCode = "";
		selCity = "";
		selProv = "";
		itemSelected = false;
	}

	public String getSelCode() {
		return selCode;
	}

	public String getSelCity() {
		return selCity;
	}

	public String getSelProv() {
		return selProv;
	}

	void start() {
		doGui();
	}

	void stop() {
		dispose();
	}

	void doGui() {

		model = new DspDataModel();
		table = new JTable(model);
		model.addColumn(Jedecma.localMessagesBundle.getString("POST_CODE"));
		model.addColumn(Jedecma.localMessagesBundle.getString("CITY"));
		model.addColumn(Jedecma.localMessagesBundle.getString("PROV"));

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
		column = table.getColumnModel().getColumn(1);
		column.setPreferredWidth(350);

		mainPan = new JPanel();
		GridBagLayout gbMPan = new GridBagLayout();
		GridBagConstraints gbcMPan = new GridBagConstraints();
		mainPan.setLayout(gbMPan);

		getContentPane().add(mainPan, BorderLayout.CENTER);

		qryPan = new JPanel();
		qryPan.setBorder(BorderFactory.createTitledBorder(""));
		Uti1.bldConst(gbcMPan, 0, 0, 10, 3, 1, 0, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);
		gbMPan.setConstraints(qryPan, gbcMPan);
		GridBagLayout gbQPan = new GridBagLayout();
		GridBagConstraints gbcQPan = new GridBagConstraints();
		qryPan.setLayout(gbQPan);
		mainPan.add(qryPan);

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("CITY"));
			Uti1.bldConst(gbcQPan, 0, 0, 1, 1, 0, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			city = new MyJTextField(30, selCity, new float[] {}, 7);
			city.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 1, 0, 4, 1, 1, 0);
			gbQPan.setConstraints(city, gbcQPan);
			qryPan.add(city);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("PROV"));
			Uti1.bldConst(gbcQPan, 0, 1, 1, 1, 0, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			prov = new MyJTextField(4, selProv, new float[] {}, 7);
			prov.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 1, 1, 1, 1, 0, 0);
			gbQPan.setConstraints(prov, gbcQPan);
			qryPan.add(prov);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("POST_CODE"));
			Uti1.bldConst(gbcQPan, 2, 1, 1, 1, 0, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			zipCode = new MyJTextField(5, selCode, new float[] {}, 7);
			zipCode.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 3, 1, 1, 1, 0, 0);
			gbQPan.setConstraints(zipCode, gbcQPan);
			qryPan.add(zipCode);
		}

		but2Pan = new JPanel();
		Uti1.bldConst(gbcMPan, 10, 0, 3, 2, 0, 0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.VERTICAL);
		gbMPan.setConstraints(but2Pan, gbcMPan);
		GridBagLayout gbB2Pan = new GridBagLayout();
		GridBagConstraints gbcB2Pan = new GridBagConstraints();
		but2Pan.setLayout(gbB2Pan);
		mainPan.add(but2Pan);

		qryBut = new JButton(qryString);
		qryBut.setMnemonic(KeyEvent.VK_C);
		qryBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				qryBut.setEnabled(false);
				qryTbl();
				qryBut.setEnabled(true);
				selBut.setEnabled(false);
			}
		});
		but2Pan.add(qryBut);
		Uti1.bldConst(gbcB2Pan, 10, 0, 1, 1, 1, 0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL);
		gbB2Pan.setConstraints(qryBut, gbcB2Pan);

		clrBut = new JButton(clrString);
		clrBut.setMnemonic(KeyEvent.VK_A);

		clrBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearTable();
				zipCode.setText("");
				city.setText("");
				prov.setText("");
				selBut.setEnabled(false);
			}
		});
		but2Pan.add(clrBut);
		Uti1.bldConst(gbcB2Pan, 10, 1, 1, 1, 1, 0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL);
		gbB2Pan.setConstraints(clrBut, gbcB2Pan);

		JScrollPane qryResPan = new JScrollPane(table);
		table.setPreferredScrollableViewportSize(new Dimension(500, 100));
		qryResPan.setBorder(BorderFactory.createTitledBorder(""));
		Uti1.bldConst(gbcMPan, 0, 3, GridBagConstraints.REMAINDER, 3, 1, 1,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH);
		gbMPan.setConstraints(qryResPan, gbcMPan);
		mainPan.add(qryResPan);

		butPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Uti1.bldConst(gbcMPan, 0, 6, GridBagConstraints.REMAINDER, 1, 1, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL);
		gbMPan.setConstraints(butPan, gbcMPan);
		mainPan.add(butPan);

		selBut = new JButton(selString);
		selBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selBut.setEnabled(false);
				itemSelected = true;
				stop();
			}
		});
		selBut.setMnemonic(KeyEvent.VK_E);
		selBut.setEnabled(false);
		butPan.add(selBut);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//
			}
		});

		qryTbl();

		pack();
		setVisible(true);

	}

	private void clearTable() {
		// elimina dalla tabella selezione corrente
		int rows = table.getRowCount();
		for (int r = 0; r < rows; r++) {
			try {
				model.removeRow(0);
			} catch (ArrayIndexOutOfBoundsException x) {
				System.out
						.println("ArrayIndexOutOfBoundsException at row:" + r);
			}
		}

	}

	private int qryTbl() { // esegue la query
		clearTable();

		if ((zipCode.getText().length() > 0) || (city.getText().length() > 0)) {

			String q = "SELECT city, code, prov FROM TBLCCDB WHERE city like '"
					+ Uti1.escape(city.getText()) + "%'";
			if (prov.getText().length() > 0) {
				q += " AND prov = '" + Uti1.escape(prov.getText()) + "'";
			}
			if (zipCode.getText().length() > 0) {
				q += " AND code = '" + Uti1.escape(zipCode.getText()) + "'";
			}
			// q += " limit " + (limit + 1);
			ResultSet resultSet;
			resultSet = Jedecma.dbmgr.executeQuery(q, (limit + 1));

			try {
				while (resultSet.next()) {
					String cit = (String) resultSet.getString("city");
					String cod = (String) resultSet.getString("code");
					String pro = (String) resultSet.getString("prov");
					String[] nextRow = new String[] { cod, cit, pro };
					model.addRow(nextRow);
				}

			} catch (SQLException ex) {
				System.err.println(ex);
				Uti1.error("SQL error", true);
			}

		}
		return table.getRowCount();

	}

	public class DspDataModel extends DefaultTableModel {

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
					selBut.setEnabled(true);
				}

			} else if (e.getSource() == table.getColumnModel()
					.getSelectionModel() && table.getColumnSelectionAllowed()) {
				int first = e.getFirstIndex();
				int last = e.getLastIndex();
			}
		}
	}

	public class mouseSelListener extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			if (evt.getClickCount() == 1) {
				int rows = table.getRowCount();
				int r = table.getSelectedRow();
				int c = table.getSelectedColumn();

				if (table.getRowSelectionAllowed()) {
					if ((rows > 0) && (r != -1)) {
						selBut.setEnabled(false);
						selCode = (String) table.getModel().getValueAt(r, 0);
						selCity = (String) table.getModel().getValueAt(r, 1);
						selProv = (String) table.getModel().getValueAt(r, 2);
						selBut.setEnabled(true);
					}
				}
			} else {
				if (evt.getClickCount() == 2) { // double-click
					selBut.setEnabled(false);
					itemSelected = true;
					stop();
				}
			}
		}
	}

} // end ZipCodeSrc class
