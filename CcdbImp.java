/*  
 * CcdbImp.java - import zip codes
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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import au.com.bytecode.opencsv.CSVReader;

public class CcdbImp implements Menuable {
	private JPanel qryFilePan;
	private JButton openBut, loadBut;
	private final JFileChooser fc = new JFileChooser();
	private MyJTextField recFrom, recTo, qryFile;

	public void start() {
		doGui(Jedecma.mainPan);

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		Uti1.bldConst(gbc, 0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);
		gb.setConstraints(qryFilePan, gbc);
		Jedecma.mainPan.setLayout(gb);
		Jedecma.mainPan.add(qryFilePan);

		Jedecma.mf.pack();
		Jedecma.mf.setVisible(true);

	}

	public void stop() {
		Jedecma.mainPan.remove(qryFilePan);
		Jedecma.mf.repaint();
	}

	public void doGui(Container contPan) { // serve contPan?
		qryFilePan = new JPanel();
		qryFilePan.setBorder(BorderFactory.createTitledBorder(" "
				+ Jedecma.localMessagesBundle.getString("CCDB_IMPORT") + " "));
		GridBagLayout gbFPan = new GridBagLayout();
		GridBagConstraints gbcFPan = new GridBagConstraints();
		qryFilePan.setLayout(gbFPan);

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
		Uti1.bldConst(gbcFPan, 0, 2, 1, 1, 0, 0);
		gbFPan.setConstraints(openBut, gbcFPan);
		qryFilePan.add(openBut);

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("FILE_NAME"));
			Uti1.bldConst(gbcFPan, 0, 1, 1, 1, 1, 0);
			gbFPan.setConstraints(lab, gbcFPan);
			qryFilePan.add(lab);
			qryFile = new MyJTextField(128, "", new float[] {}, 0);
			qryFile.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcFPan, 1, 1, 5, 1, 1, 0);
			gbFPan.setConstraints(qryFile, gbcFPan);
			qryFilePan.add(qryFile);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("REC_START"));
			Uti1.bldConst(gbcFPan, 1, 2, 1, 1, 1, 0);
			gbFPan.setConstraints(lab, gbcFPan);
			qryFilePan.add(lab);
			recFrom = new MyJTextField(5, "0", new float[] {}, 1);
			recFrom.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcFPan, 2, 2, 1, 1, 1, 0);
			gbFPan.setConstraints(recFrom, gbcFPan);
			qryFilePan.add(recFrom);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("REC_END"));
			Uti1.bldConst(gbcFPan, 3, 2, 1, 1, 1, 0);
			gbFPan.setConstraints(lab, gbcFPan);
			qryFilePan.add(lab);
			recTo = new MyJTextField(5, "0", new float[] {}, 1);
			recTo.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcFPan, 4, 2, 1, 1, 1, 0);
			gbFPan.setConstraints(recTo, gbcFPan);
			qryFilePan.add(recTo);
		}

		loadBut = new JButton(Jedecma.localMessagesBundle.getString("EXECUTE"));
		loadBut.setMnemonic(KeyEvent.VK_U);
		loadBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						loadFile(qryFile.getText());
					}
				});
			}
		});
		Uti1.bldConst(gbcFPan, 5, 2, 1, 1, 1, 0);
		gbFPan.setConstraints(loadBut, gbcFPan);
		qryFilePan.add(loadBut);

		{ // spazio libero
			MyJlabel lab = new MyJlabel("");
			Uti1.bldConst(gbcFPan, 0, 2, 1, 1, 1, 1);
			gbFPan.setConstraints(lab, gbcFPan);
			qryFilePan.add(lab);
		}

		{ // spazio libero
			MyJlabel lab = new MyJlabel("");
			Uti1.bldConst(gbcFPan, 0, 0, 1, 1, 1, 0);
			gbFPan.setConstraints(lab, gbcFPan);
			qryFilePan.add(lab);
		}
	}

	public void loadFile(String inputFile) {
		int rows = 0, inserts = 0;
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(inputFile), ';');
			String[] nextLine;
			rows = 0;
			int r1 = Integer.parseInt(recFrom.getText());
			int r2 = Integer.parseInt(recTo.getText());
			while ((nextLine = reader.readNext()) != null) {
				rows++;

				if ((rows >= r1 || r1 == 0) && (rows <= r2 || r2 == 0)) {

					String code = nextLine[0];
					String city = nextLine[1];
					String state = nextLine[2];
					state = Uti1.leftSet(state, 2, ' ');

					if (code.length() < 1) {
						continue;
					}
					if (Integer.parseInt(code) <= 0) {
						continue;
					}
					if (city.length() < 1) {
						continue;
					}
					if (state.length() < 1) {
						continue;
					}

					String q = "INSERT INTO TBLCCDB (code, city, prov) VALUES ("
							+ "'"
							+ code
							+ "'"
							+ ", '"
							+ (Uti1.escape(city)).toUpperCase()
							+ "'"
							+ ", '"
							+ (Uti1.escape(state)).toUpperCase() + "'" + ")";

					int rc = Jedecma.dbmgr.executeUpdate(q);
					if (rc > 0) {
						// System.out.println(q);
						inserts++;
					} else {
						System.out.println(Jedecma.localMessagesBundle
								.getString("SQL_ERROR"));
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(rows + " lines read; " + inserts + " inserts");

		String s = "<html><body><center>"
				+ Jedecma.localMessagesBundle.getString("CCDB_IMPORT_MSG")
				+ ": " + rows + "/" + inserts + "</center></body></html>";
		JOptionPane.showMessageDialog(Jedecma.mf, s, "Info",
				JOptionPane.INFORMATION_MESSAGE);

	}

} // end CcdbImp class
