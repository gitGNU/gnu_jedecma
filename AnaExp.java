/*  
 * AnaExp.java - exports patient data to CSV file
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

public class AnaExp implements Menuable {
	private static String pcsString = Jedecma.localMessagesBundle
			.getString("EXECUTE");
	private JButton pcsBut;
	private JPanel mainPan, qryPan, butPan;
	private MyJTextField anrprv;
	int fileCnt = 0;

	public AnaExp() {
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
				+ Jedecma.localMessagesBundle.getString("EXPORT_EDECAN") + " "));
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
					Jedecma.localMessagesBundle.getString("PROV"));
			Uti1.bldConst(gbcQPan, 0, 2, 1, 1, 0, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			anrprv = new MyJTextField(50, "", new float[] {}, 7);
			anrprv.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 1, 2, 1, 1, 1, 0);
			gbQPan.setConstraints(anrprv, gbcQPan);
			qryPan.add(anrprv);
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

			ResultSet rSet;
			String q = "SELECT * from EDECAN";

			{
				anrprv.setText(anrprv.getText().trim());
				String s = Uti1.escape(anrprv.getText()); // in MySQL LIKE e'
															// case-insensitive
															// per default!
				if (s.length() > 0) {
					q += " where anrprv like '" + s.trim() + "%'";
				}
			}

			rSet = Jedecma.dbmgr.executeQuery(q);

			String bn = "edecan";
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
					Jedecma.localMessagesBundle.getString("EXPORT_EDECAN_MSG")
							+ ": " + f,
					Jedecma.localMessagesBundle.getString("EXPORT_EDECAN"),
					JOptionPane.INFORMATION_MESSAGE);

			butPan.setEnabled(true);

		}

	}

} // end class AnaExp
