/*  
 * EcoExp.java - exports examinations data to CSV file
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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.sql.*;
import java.io.*;

import au.com.bytecode.opencsv.CSVWriter;

public class EcoExp implements Menuable {
	private static String pcsString = Jedecma.localMessagesBundle
			.getString("EXECUTE");
	private JButton pcsBut;
	private JPanel mainPan, qryPan, butPan;
	private MyJTextField nrFrom, nrTo, dateFrom, dateTo;
	int fileCnt = 0;

	public EcoExp() {
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

		Jedecma.mf.pack();
		Jedecma.mf.setVisible(true);
	}

	public void stop() {
		Jedecma.mainPan.remove(mainPan);
		Jedecma.mf.repaint();
	}

	public void doGui(Container contPan) { // serve contPan?
		mainPan = new JPanel();
		GridBagLayout gbMPan = new GridBagLayout();
		GridBagConstraints gbcMPan = new GridBagConstraints();
		mainPan.setLayout(gbMPan);

		qryPan = new JPanel();
		qryPan.setBorder(BorderFactory.createTitledBorder(" "
				+ Jedecma.localMessagesBundle.getString("EXPORT_EDECMA") + " "));
		Uti1.bldConst(gbcMPan, 0, 0, 1, 10, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);
		gbMPan.setConstraints(qryPan, gbcMPan);
		mainPan.add(qryPan);

		butPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Uti1.bldConst(gbcMPan, 0, 11, GridBagConstraints.REMAINDER, 1, 1, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL);
		gbMPan.setConstraints(butPan, gbcMPan);
		mainPan.add(butPan);

		pcsBut = new JButton(pcsString);
		pcsBut.setMnemonic(KeyEvent.VK_C);
		pcsBut.addActionListener(new QryListener());
		pcsBut.setActionCommand(pcsString);
		butPan.add(pcsBut);

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
					Jedecma.localMessagesBundle.getString("DATE_START"));
			Uti1.bldConst(gbcQPan, 0, 2, 1, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			dateFrom = new MyJTextField(10, "", new float[] {}, 6);
			dateFrom.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 1, 2, 1, 1, 0, 0);
			gbQPan.setConstraints(dateFrom, gbcQPan);
			qryPan.add(dateFrom);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("DATE_END"));
			Uti1.bldConst(gbcQPan, 3, 2, 1, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			dateTo = new MyJTextField(10, "", new float[] {}, 6);
			dateTo.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 4, 2, 1, 1, 0, 0);
			gbQPan.setConstraints(dateTo, gbcQPan);
			qryPan.add(dateTo);
		}

		{ // spazio libero
			MyJlabel lab = new MyJlabel("");
			Uti1.bldConst(gbcQPan, 3, 3, 1, 1, 1, 1);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
		}

	}

	class QryListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			butPan.setEnabled(false);

			dateFrom.setText(Uti1.date2String(Uti1.string2Date(dateFrom
					.getText().trim())));
			dateTo.setText(Uti1.date2String(Uti1.string2Date(dateTo.getText()
					.trim())));

			ResultSet rSet;
			String q = "SELECT * FROM EDECMA WHERE numarc is NOT NULL";

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

			q += " ORDER BY numarc";

			rSet = Jedecma.dbmgr.executeQuery(q);

			String bn = "edecma";
			File f = new File(Uti1.getWrkDir() + bn + ".csv");

			while (f.exists()) {
				fileCnt++;
				f = new File(Uti1.getWrkDir() + bn + "-" + fileCnt + ".csv");
			}

			System.out.println("writing to " + f);

			FileWriter sw = null;
			try {
				sw = new FileWriter(f);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			CSVWriter writer = new CSVWriter(sw);

			try {
				// writer.writeAll(rSet, false); // no column header
				writer.writeAll(rSet, true); // column header
				writer.close();
			} catch (IOException ex) {
				System.err.println(ex);
				Uti1.error("file IO error", true);
			} catch (SQLException ex) {
				System.err.println(ex);
				Uti1.error("SQL error", true);

			}

			JOptionPane.showMessageDialog(null,
					Jedecma.localMessagesBundle.getString("EXPORT_EDECMA_MSG")
							+ ": " + f,
					Jedecma.localMessagesBundle.getString("EXPORT_EDECMA"),
					JOptionPane.INFORMATION_MESSAGE);

			butPan.setEnabled(true);

		}

	}

} // end of EcoExp class