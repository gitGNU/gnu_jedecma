/*
 * AnaEdit.java - patient data dialog
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

public class AnaEdit extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel mainPan, anaPan, butPan;
	private JButton okBut, quitBut, profBut, postCodeBut;
	AnaDat ana;
	int ancode;
	private String lockCode;
	boolean busy, abort;

	MyJTextField anbrtd, anname, anaddr, ancapc, anloca, anrprv, anbprv,
			anteln, antel2;
	MyJTextField annote, anfami, anmena, anchld, an1cha, anmenp, anheig,
			anweig, email;

	MyJComboBox ansuck, anmens;
	JComboBox anprof;

	public AnaEdit() {
		setModal(true);
		busy = false;
		abort = false;
		lockCode = "";
	}

	public void edit(String name, String bdat, String bloc) {
		System.out.println("AnaEdit: edit(" + name + ", " + bdat + ", " + bloc
				+ ")");
		AnaDat a = new AnaDat();
		int code = a.getAnaUid(Jedecma.dbmgr, name, bdat, bloc);
		if (code >= 0) {
			edit(code);
		}
	}

	public void edit(int code) {
		String nrs = String.valueOf(code);
		if (code < 0) {
			Uti1.error(
					"AnaEdit: "
							+ Jedecma.localMessagesBundle
									.getString("INVALID_PAT_CODE") + " " + nrs,
					false);
			abort = true;
			stop();
		}

		System.out.println("AnaEdit: edit(" + code + ")");
		ancode = code;
		ana = new AnaDat();
		String title = Jedecma.localMessagesBundle.getString("PAT_EDIT") + " "
				+ nrs;

		if (ancode == 0) {
			title = Jedecma.localMessagesBundle.getString("PAT_NEW");
			lockCode = "";
		}
		setTitle(title);

		if (ancode > 0) {
			// lock
			lockCode = "EDECAN." + nrs;
			int lockrv = MyLock.setLock(Jedecma.dbmgr, lockCode,
					Jedecma.user.userid, MyLock.EXCL);
			if (lockrv < 0) {
				Uti1.error(
						"AnaEdit: "
								+ Jedecma.localMessagesBundle.getString("CODE")
								+ ": "
								+ nrs
								+ " "
								+ Jedecma.localMessagesBundle
										.getString("IN_USE") + " (" + lockrv
								+ ")", false);
				busy = true;
			} else {
				busy = false;
			}

			int rc = ana.readAna(Jedecma.dbmgr, ancode);
			if (rc != 0) {
				Uti1.error(
						Jedecma.localMessagesBundle.getString("CODE")
								+ " "
								+ String.valueOf(ancode)
								+ " "
								+ Jedecma.localMessagesBundle
										.getString("NOT_AVAILABLE"), false);
				abort = true;
				stop();
			}
		}

		if (!abort) {

			mainPan = new JPanel();
			mainPan.setLayout(new BoxLayout(mainPan, BoxLayout.Y_AXIS));
			anaPan = new JPanel();
			anaPan.setBorder(BorderFactory.createTitledBorder(""));
			mainPan.add(anaPan);

			GridBagLayout gb = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();
			anaPan.setLayout(gb);

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("NAME"));
				Uti1.bldConst(gbc, 0, 0, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				anname = new MyJTextField(30, ana.anname, new float[] {}, 7);
				anname.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 1, 0, 4, 1, 1, 0);
				gb.setConstraints(anname, gbc);
				anaPan.add(anname);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("BIRTH_DATE"));
				Uti1.bldConst(gbc, 5, 0, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				String ws = "";
				if (Uti1.date2String(ana.anbrtd).length() > 0) {
					ws = Uti1.date2String(ana.anbrtd);
				}
				anbrtd = new MyJTextField(10, ws, new float[] {}, 6);
				anbrtd.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 6, 0, 1, 1, 1, 0);
				gb.setConstraints(anbrtd, gbc);
				anaPan.add(anbrtd);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("BIRTH_PROV"));
				Uti1.bldConst(gbc, 7, 0, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				anbprv = new MyJTextField(4, ana.anbprv, new float[] {}, 7);
				anbprv.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 8, 0, 1, 1, 1, 0);
				gb.setConstraints(anbprv, gbc);
				anaPan.add(anbprv);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("ADDRESS"));
				Uti1.bldConst(gbc, 0, 1, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				anaddr = new MyJTextField(30, ana.anaddr, new float[] {}, 7);
				anaddr.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 1, 1, 4, 1, 1, 0);
				gb.setConstraints(anaddr, gbc);
				anaPan.add(anaddr);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("POST_CODE"));
				Uti1.bldConst(gbc, 0, 2, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				ancapc = new MyJTextField(5, ana.ancapc,
						new float[] { 0, 99999 }, 1);
				ancapc.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 1, 2, 1, 1, 1, 0);
				gb.setConstraints(ancapc, gbc);
				anaPan.add(ancapc);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("CITY"));
				Uti1.bldConst(gbc, 2, 2, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				anloca = new MyJTextField(30, ana.anloca, new float[] {}, 7);
				anloca.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 3, 2, 4, 1, 0, 0);
				gb.setConstraints(anloca, gbc);
				anaPan.add(anloca);
			}

			postCodeBut = new JButton(
					Jedecma.localMessagesBundle.getString("SEARCH"));
			postCodeBut.setMnemonic(KeyEvent.VK_C);
			Uti1.bldConst(gbc, 7, 2, 1, 1, 0, 0);
			gb.setConstraints(postCodeBut, gbc);
			anaPan.add(postCodeBut);
			postCodeBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					postCodeBut.setEnabled(false);
					ZipCodeSrc zcs = new ZipCodeSrc();
					zcs.setSelProv(anrprv.getText());
					// zcs.setSelCode(ancapc.getText());
					zcs.setSelCity(anloca.getText());
					zcs.start();
					if (zcs.itemSelected()) {
						anrprv.setText(zcs.getSelProv());
						ancapc.setText(zcs.getSelCode());
						anloca.setText(zcs.getSelCity());
					}
					postCodeBut.setEnabled(true);
				}
			});

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("PROV"));
				Uti1.bldConst(gbc, 8, 2, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				anrprv = new MyJTextField(4, ana.anrprv, new float[] {}, 7);
				anrprv.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 9, 2, 1, 1, 1, 0);
				gb.setConstraints(anrprv, gbc);
				anaPan.add(anrprv);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("TEL") + " 1");
				Uti1.bldConst(gbc, 0, 3, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				anteln = new MyJTextField(15, ana.anteln, new float[] {}, 7);
				anteln.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 1, 3, 3, 1, 1, 0);
				gb.setConstraints(anteln, gbc);
				anaPan.add(anteln);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("JOB"));
				Uti1.bldConst(gbc, 4, 3, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				String[] items = new String[] { Jedecma.localMessagesBundle
						.getString("CB_NA") };
				anprof = new JComboBox(items);
				if (Jedecma.jComboBoxFont != null) {
					anprof.setFont(Jedecma.jComboBoxFont);
				}
				anprof.setKeySelectionManager(new myKeyManager());
				int rc = loadProf(anprof);
				setJCBValue(anprof, ana.anprof);
				Uti1.bldConst(gbc, 5, 3, 3, 1, 1, 0);
				gb.setConstraints(anprof, gbc);
				anaPan.add(anprof);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("TEL") + " 2");
				Uti1.bldConst(gbc, 0, 4, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				antel2 = new MyJTextField(15, ana.antel2, new float[] {}, 7);
				antel2.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 1, 4, 3, 1, 1, 0);
				gb.setConstraints(antel2, gbc);
				anaPan.add(antel2);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("NOTE"));
				Uti1.bldConst(gbc, 4, 4, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				annote = new MyJTextField(30, ana.annote, new float[] {}, 7);
				annote.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 5, 4, 4, 1, 0, 0);
				gb.setConstraints(annote, gbc);
				anaPan.add(annote);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("EMAIL_ADDRESS"));
				Uti1.bldConst(gbc, 0, 5, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				email = new MyJTextField(50, ana.email, new float[] {}, 0);
				email.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 1, 5, 8, 1, 1, 0);
				gb.setConstraints(email, gbc);
				anaPan.add(email);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("WEIGHT")
								+ " kg.");
				Uti1.bldConst(gbc, 0, 6, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				anweig = new MyJTextField(5, String.valueOf(ana.anweig),
						new float[] { 0, 250 }, 2);
				anweig.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 1, 6, 1, 1, 1, 0);
				gb.setConstraints(anweig, gbc);
				anaPan.add(anweig);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("HEIGHT")
								+ " cm.");
				Uti1.bldConst(gbc, 2, 6, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				anheig = new MyJTextField(5, String.valueOf(ana.anheig),
						new float[] { 0, 220 }, 2);
				anheig.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 3, 6, 1, 1, 1, 0);
				gb.setConstraints(anheig, gbc);
				anaPan.add(anheig);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("FAM_PATHOL"));
				Uti1.bldConst(gbc, 4, 6, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				anfami = new MyJTextField(20, ana.anfami, new float[] {}, 7);
				anfami.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 5, 6, 3, 1, 1, 0);
				gb.setConstraints(anfami, gbc);
				anaPan.add(anfami);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("NR_CHILDREN"));
				Uti1.bldConst(gbc, 0, 7, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				anchld = new MyJTextField(5, String.valueOf(ana.anchld),
						new float[] { 0, 20 }, 1);
				anchld.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 1, 7, 1, 1, 1, 0);
				gb.setConstraints(anchld, gbc);
				anaPan.add(anchld);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle
								.getString("AGE_1ST_CHILDBIRTH"));
				Uti1.bldConst(gbc, 2, 7, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				an1cha = new MyJTextField(5, String.valueOf(ana.an1cha),
						new float[] { 0, 99 }, 1);
				an1cha.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 3, 7, 1, 1, 1, 0);
				gb.setConstraints(an1cha, gbc);
				anaPan.add(an1cha);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("SUCKLE"));
				Uti1.bldConst(gbc, 4, 7, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				ansuck = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("CB_NO"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("CB_YES") },
						String.valueOf(ana.ansuck));
				Uti1.bldConst(gbc, 5, 7, 1, 1, 1, 0);
				gb.setConstraints(ansuck, gbc);
				anaPan.add(ansuck);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("AGE_MENARCHE"));
				Uti1.bldConst(gbc, 0, 8, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				anmena = new MyJTextField(5, String.valueOf(ana.anmena),
						new float[] { 0, 99 }, 1);
				anmena.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 1, 8, 1, 1, 1, 0);
				gb.setConstraints(anmena, gbc);
				anaPan.add(anmena);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MENSTR"));
				Uti1.bldConst(gbc, 2, 8, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				anmens = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("MENSTR_REG"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("MENSTR_IRR") },
						String.valueOf(ana.anmens));
				Uti1.bldConst(gbc, 3, 8, 1, 1, 1, 0);
				gb.setConstraints(anmens, gbc);
				anaPan.add(anmens);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MENOP"));
				Uti1.bldConst(gbc, 4, 8, 1, 1, 0, 0);
				gb.setConstraints(lab, gbc);
				anaPan.add(lab);
				anmenp = new MyJTextField(5, String.valueOf(ana.anmenp),
						new float[] { 0, 99 }, 1);
				anmenp.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbc, 5, 8, 1, 1, 1, 0);
				gb.setConstraints(anmenp, gbc);
				anaPan.add(anmenp);
			}

			butPan = new JPanel();
			mainPan.add(butPan);
			getContentPane().add(mainPan);

			okBut = new JButton("Ok");
			okBut.setMnemonic(KeyEvent.VK_O);
			butPan.add(okBut);
			okBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int rc = saveData();
					if (rc == 0) {
						stop();
					}
				}
			});
			okBut.setEnabled(!busy);
			if (!Jedecma.ak.isEnableWrite()) {
				okBut.setEnabled(false);
			}

			quitBut = new JButton(
					Jedecma.localMessagesBundle.getString("CANCEL"));
			quitBut.setMnemonic(KeyEvent.VK_A);
			butPan.add(quitBut);
			quitBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stop();
				}
			});

			profBut = new JButton(Jedecma.localMessagesBundle.getString("JOB"));
			profBut.setMnemonic(KeyEvent.VK_P);
			butPan.add(profBut);
			profBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new ProfTable();
					int rc = loadProf(anprof);
				}
			});

			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					// rimuove lock
					if (lockCode.length() > 0) {
						MyLock.delLock(Jedecma.dbmgr, lockCode,
								Jedecma.user.userid);
					}
				}
			});

			pack();
			setVisible(true);

		} // end_if_abort

	}

	public void stop() {
		if (lockCode.length() > 0) {
			// rimuove lock
			MyLock.delLock(Jedecma.dbmgr, lockCode, Jedecma.user.userid);
		}
		dispose();
	}

	public int saveData() {
		int rc = 0;
		// sarebbe opportuno rivalidare i campi ...
		// aggiorna struttura ana

		ana.anbrtd = Uti1.string2Date(anbrtd.getText());
		if (ana.anname.length() == 0) {
			ana.anname = Uti1.uniqSpaces(anname.getText());
		} else {
			ana.anname = anname.getText();
		}
		ana.anaddr = anaddr.getText();
		ana.ancapc = ancapc.getText();
		ana.anloca = anloca.getText();
		ana.anrprv = anrprv.getText();
		ana.anbprv = anbprv.getText();
		ana.anteln = anteln.getText();
		ana.antel2 = antel2.getText();
		ana.email = email.getText();
		ana.anprof = getJCBValue(anprof);
		ana.annote = annote.getText();
		ana.anfami = anfami.getText();
		ana.anmena = Integer.parseInt(anmena.getText());
		ana.anchld = Integer.parseInt(anchld.getText());
		ana.an1cha = Integer.parseInt(an1cha.getText());
		ana.ansuck = Integer.parseInt(ansuck.getInpValue());
		ana.anmens = Integer.parseInt(anmens.getInpValue());
		ana.anmenp = Integer.parseInt(anmenp.getText());
		ana.anheig = Float.parseFloat(anheig.getText());
		ana.anweig = Float.parseFloat(anweig.getText());

		ana.writeAna(Jedecma.dbmgr, ancode);
		ancode = ana.ancode;

		return (rc);
	}

	public int loadProf(JComboBox cb) {
		int rv = 0;

		for (int pos = 1; pos < cb.getItemCount(); pos++) {
			cb.removeItemAt(pos);
		}

		String profcod = "";
		String profdes = "";
		ResultSet rSet;

		// deve fare il lock della tabella ?

		rSet = Jedecma.dbmgr
				.executeQuery("SELECT PROFCOD, PROFDES FROM TBLPROF ORDER BY PROFCOD");
		try {
			while (rSet.next()) {
				profcod = rSet.getString("PROFCOD");
				profdes = rSet.getString("PROFDES");
				cb.addItem(profcod + "=" + profdes);
			}
		} catch (SQLException ex) {
			System.err.println(ex);
			rv = 1;
		}
		return (rv);
	}

	public String getJCBValue(JComboBox jcb) {
		int si = jcb.getSelectedIndex();
		String ws = "";
		if (si >= 0 && si <= jcb.getItemCount()) {
			ws = (String) jcb.getItemAt(si);
			int b = ws.indexOf("=");
			if (b > -1) {
				ws = ws.substring(0, b);
			}
		}
		return (ws);
	}

	public void setJCBValue(JComboBox cb, String value) {
		int vl = value.length();
		if (vl > 0) {
			for (int pos = 1; pos < cb.getItemCount(); pos++) {
				String iItem = (String) cb.getItemAt(pos);
				String s = iItem.substring(0, vl);
				if (value.equals(s)) {
					cb.setSelectedIndex(pos);
					break;
				}
			}
		}
	}

	class myKeyManager implements JComboBox.KeySelectionManager {
		String keys = null;
		int curSelected = 0;

		myKeyManager() {
		}

		public int selectionForKey(char aKey, ComboBoxModel aModel) {
			int iCount = aModel.getSize();
			int iPatternLen = 0;
			int iSelected = 0;
			String sItem = null;

			if (Character.getNumericValue(aKey) == -1)
				keys = null;
			else {
				if (keys != null)
					keys = keys + (new Character(aKey)).toString();
				else
					keys = (new Character(aKey)).toString();

				if (keys != null) {
					iPatternLen = keys.length();
					keys = keys.toUpperCase();
				}

				for (int pos = 0; pos < iCount; pos++) {
					sItem = (String) aModel.getElementAt(pos);

					if (sItem.length() >= iPatternLen) {
						if (sItem.substring(0, iPatternLen).equals(keys)) {
							iSelected = pos;
							curSelected = iSelected;
							break;
						}
					} else
						iSelected = curSelected;
				}
			}

			return curSelected;
		}

	}

} // end class AnaEdit

