/*  
 * Config.java - configuration options management
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

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class Config implements Menuable {
	private JPanel mainPan, tab1, tab2, tab3, tab4, tab5, butPan;
	private JTabbedPane tabPan;
	private JButton okBut, labFntBut, txfFntBut, txaFntBut, boxFntBut,
			repLabFntBut, repDatFntBut, repDgnFntBut, logo1BrowseBut,
			logo2BrowseBut, splashBrowseBut;
	private MyJComboBox lang, dbType;
	private MyJTextField drName, logo1, logo2, labFnt, txfFnt, txaFnt, boxFnt,
			repLabFnt, repDatFnt, repDgnFnt, splash;
	private MyJComboBox textOffs;
	private Properties props = new Properties();
	private String lockCode = "CONFIG";
	private JFileChooser fc = new JFileChooser();
	public String username, password;
	private JTextField userFld, hostAddrFld, jdbcDriverFld;
	private JTextField srcPath, imgPath;
	private JPasswordField passFld;

	public Config() {
	}

	public void stop() {
		// rimuove lock
		if (Jedecma.user != null) {
			MyLock.delLock(Jedecma.dbmgr, lockCode, Jedecma.user.userid);
		}
		if (mainPan != null) {
			Jedecma.mainPan.remove(mainPan);
		}
		Jedecma.mf.repaint();
	}

	public void start() {

		// lock della tabella
		int lockrv = 0;
		if (Jedecma.user != null) {
			lockrv = MyLock.setLock(Jedecma.dbmgr, lockCode,
					Jedecma.user.userid, true);
		}
		if (lockrv == 0) {

			props = Jedecma.param;

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

		} else {
			Uti1.error(Jedecma.localMessagesBundle.getString("CONFIG") + " "
					+ Jedecma.localMessagesBundle.getString("IN_USE") + " ("
					+ lockrv + ")", false);
			if (!Jedecma.splash.isVisible()) {
				Jedecma.splash.setVisible(true);
			}
		}

	}

	public void doGui(Container contPan) {
		mainPan = new JPanel();
		GridBagLayout gbMPan = new GridBagLayout();
		GridBagConstraints gbcMPan = new GridBagConstraints();
		mainPan.setLayout(gbMPan);

		tabPan = new JTabbedPane(JTabbedPane.TOP);
		Uti1.bldConst(gbcMPan, 0, 0, 1, 10, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);
		gbMPan.setConstraints(tabPan, gbcMPan);
		mainPan.add(tabPan);

		tab1 = new JPanel();
		GridBagLayout gbTab1 = new GridBagLayout();
		GridBagConstraints gbcTab1 = new GridBagConstraints();
		tab1.setLayout(gbTab1);
		tabPan.addTab(Jedecma.localMessagesBundle.getString("LANG"), tab1);

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("AVAIL_LANG") + " ");
			Uti1.bldConst(gbcTab1, 0, 0, 5, 1, 0, 0);
			gbTab1.setConstraints(lab, gbcTab1);
			tab1.add(lab);
			lang = new MyJComboBox(getLangProps(), getLang());
			lang.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setLang();
				}
			});
			Uti1.bldConst(gbcTab1, 5, 0, 5, 1, 1, 0);
			gbTab1.setConstraints(lang, gbcTab1);
			tab1.add(lang);
		}

		tab3 = new JPanel();
		GridBagLayout gbTab3 = new GridBagLayout();
		GridBagConstraints gbcTab3 = new GridBagConstraints();
		tab3.setLayout(gbTab3);
		tabPan.addTab(Jedecma.localMessagesBundle.getString("ASPECT"), tab3);

		{
			MyJlabel lab = new MyJlabel("Label");
			Uti1.bldConst(gbcTab3, 0, 1, 5, 1, 0, 0);
			gbTab3.setConstraints(lab, gbcTab3);
			tab3.add(lab);
			labFnt = new MyJTextField(60,
					Jedecma.param.getProperty("jlabel_font"), new float[] {}, 0);
			labFnt.addFocusListener(new CtrTextField());
			labFnt.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setLabFnt();
				}
			});
			Uti1.bldConst(gbcTab3, 5, 1, 20, 1, 1, 0);
			gbTab3.setConstraints(labFnt, gbcTab3);
			tab3.add(labFnt);
		}

		{
			labFntBut = new JButton(
					Jedecma.localMessagesBundle.getString("SEARCH"));
			labFntBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					okBut.setEnabled(false);

					MyFntSel fs = new MyFntSel();
					fs.chooseFont("label Font", labFnt.getText(),
							"Dialog,BOLD,12");
					String s = fs.getSelFont();
					if (!s.equals("")) {
						labFnt.setText(s);
						setLabFnt();
					}

					okBut.setEnabled(true);
				}
			});
			Uti1.bldConst(gbcTab3, 25, 1, 2, 1, 0, 0);
			gbTab3.setConstraints(labFntBut, gbcTab3);
			tab3.add(labFntBut);
		}

		{
			MyJlabel lab = new MyJlabel("Text field");
			Uti1.bldConst(gbcTab3, 0, 2, 5, 1, 0, 0);
			gbTab3.setConstraints(lab, gbcTab3);
			tab3.add(lab);
			txfFnt = new MyJTextField(60,
					Jedecma.param.getProperty("jtextfield_font"),
					new float[] {}, 0);
			txfFnt.addFocusListener(new CtrTextField());
			txfFnt.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setTxfFnt();
				}
			});
			Uti1.bldConst(gbcTab3, 5, 2, 20, 1, 1, 0);
			gbTab3.setConstraints(txfFnt, gbcTab3);
			tab3.add(txfFnt);
		}

		{
			txfFntBut = new JButton(
					Jedecma.localMessagesBundle.getString("SEARCH"));
			txfFntBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					okBut.setEnabled(false);
					MyFntSel fs = new MyFntSel();
					fs.chooseFont("TextField Font", txfFnt.getText(),
							"Dialog,PLAIN,12");
					String s = fs.getSelFont();
					if (!s.equals("")) {
						txfFnt.setText(s);
						setTxfFnt();
					}
					okBut.setEnabled(true);
				}
			});
			Uti1.bldConst(gbcTab3, 25, 2, 2, 1, 0, 0);
			gbTab3.setConstraints(txfFntBut, gbcTab3);
			tab3.add(txfFntBut);
		}

		{
			MyJlabel lab = new MyJlabel("TextArea");
			Uti1.bldConst(gbcTab3, 0, 3, 5, 1, 0, 0);
			gbTab3.setConstraints(lab, gbcTab3);
			tab3.add(lab);
			txaFnt = new MyJTextField(60,
					Jedecma.param.getProperty("jtextarea_font"),
					new float[] {}, 0);
			txaFnt.addFocusListener(new CtrTextField());
			txaFnt.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setTxaFnt();
				}
			});
			Uti1.bldConst(gbcTab3, 5, 3, 20, 1, 1, 0);
			gbTab3.setConstraints(txaFnt, gbcTab3);
			tab3.add(txaFnt);
		}

		{
			txaFntBut = new JButton(
					Jedecma.localMessagesBundle.getString("SEARCH"));
			txaFntBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					okBut.setEnabled(false);

					MyFntSel fs = new MyFntSel();
					fs.chooseFont("TextArea Font", txaFnt.getText(),
							"Dialog,PLAIN,12");
					String s = fs.getSelFont();
					if (!s.equals("")) {
						txaFnt.setText(s);
						setTxaFnt();
					}
					okBut.setEnabled(true);
				}
			});
			Uti1.bldConst(gbcTab3, 25, 3, 2, 1, 0, 0);
			gbTab3.setConstraints(txaFntBut, gbcTab3);
			tab3.add(txaFntBut);
		}

		{
			MyJlabel lab = new MyJlabel("Combo Box");
			Uti1.bldConst(gbcTab3, 0, 4, 5, 1, 0, 0);
			gbTab3.setConstraints(lab, gbcTab3);
			tab3.add(lab);
			boxFnt = new MyJTextField(60,
					Jedecma.param.getProperty("jcombobox_font"),
					new float[] {}, 0);
			boxFnt.addFocusListener(new CtrTextField());
			boxFnt.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setBoxFnt();
				}
			});
			Uti1.bldConst(gbcTab3, 5, 4, 20, 1, 1, 0);
			gbTab3.setConstraints(boxFnt, gbcTab3);
			tab3.add(boxFnt);
		}

		{
			boxFntBut = new JButton(
					Jedecma.localMessagesBundle.getString("SEARCH"));
			boxFntBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					okBut.setEnabled(false);

					MyFntSel fs = new MyFntSel();
					fs.chooseFont("ComboBox Font", boxFnt.getText(),
							"Dialog,PLAIN,12");
					String s = fs.getSelFont();
					if (!s.equals("")) {
						boxFnt.setText(s);
						setBoxFnt();
					}
					okBut.setEnabled(true);
				}
			});
			Uti1.bldConst(gbcTab3, 25, 4, 2, 1, 0, 0);
			gbTab3.setConstraints(boxFntBut, gbcTab3);
			tab3.add(boxFntBut);
		}

		{
			MyJlabel lab = new MyJlabel("Splash");
			Uti1.bldConst(gbcTab3, 0, 5, 5, 1, 0, 0);
			gbTab3.setConstraints(lab, gbcTab3);
			tab3.add(lab);
			splash = new MyJTextField(100, Jedecma.param.getProperty("splash"),
					new float[] {}, 0);
			splash.addFocusListener(new CtrTextField());
			splash.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setSplash();
				}
			});
			Uti1.bldConst(gbcTab3, 5, 5, 20, 1, 0, 0);
			gbTab3.setConstraints(splash, gbcTab3);
			tab3.add(splash);
		}

		{
			splashBrowseBut = new JButton(
					Jedecma.localMessagesBundle.getString("BROWSE"));
			splashBrowseBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					splashBrowseBut.setEnabled(false);
					fc.addChoosableFileFilter(new MyFileFilter());
					fc.setCurrentDirectory(new File(Uti1.getWrkDir()));
					int returnVal = fc.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String s = fc.getSelectedFile().getAbsolutePath();
						splash.setText(s);
						setSplash();
					}
					splashBrowseBut.setEnabled(true);
				}
			});
			Uti1.bldConst(gbcTab3, 25, 5, 2, 1, 0, 0);
			gbTab3.setConstraints(splashBrowseBut, gbcTab3);
			tab3.add(splashBrowseBut);
		}

		tab2 = new JPanel();
		GridBagLayout gbTab2 = new GridBagLayout();
		GridBagConstraints gbcTab2 = new GridBagConstraints();
		tab2.setLayout(gbTab2);
		tabPan.addTab(Jedecma.localMessagesBundle.getString("REPORT"), tab2);

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("TEXT_OFFS"));
			Uti1.bldConst(gbcTab2, 0, 0, 5, 1, 0, 0);
			gbTab2.setConstraints(lab, gbcTab2);
			tab2.add(lab);
			textOffs = new MyJComboBox(new String[] { "1", "2", "3", "4", "5",
					"6" }, String.valueOf(getTextOffs()));
			textOffs.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setTextOffs();
				}
			});
			Uti1.bldConst(gbcTab2, 5, 0, 2, 1, 0, 0,
					GridBagConstraints.NORTHWEST, 0);
			gbTab2.setConstraints(textOffs, gbcTab2);
			tab2.add(textOffs);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("REP_SIGNAT"));
			Uti1.bldConst(gbcTab2, 0, 1, 5, 1, 0, 0);
			gbTab2.setConstraints(lab, gbcTab2);
			tab2.add(lab);
			drName = new MyJTextField(40, Jedecma.param.getProperty("drname"),
					new float[] {}, 0);
			drName.addFocusListener(new CtrTextField());
			drName.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setDrName();
				}
			});
			Uti1.bldConst(gbcTab2, 5, 1, 20, 1, 1, 0);
			gbTab2.setConstraints(drName, gbcTab2);
			tab2.add(drName);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("LOGO_1"));
			Uti1.bldConst(gbcTab2, 0, 2, 5, 1, 0, 0);
			gbTab2.setConstraints(lab, gbcTab2);
			tab2.add(lab);
			logo1 = new MyJTextField(100, Jedecma.param.getProperty("logo1"),
					new float[] {}, 0);
			logo1.addFocusListener(new CtrTextField());
			logo1.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setLogo1();
				}
			});
			Uti1.bldConst(gbcTab2, 5, 2, 20, 1, 0, 0);
			gbTab2.setConstraints(logo1, gbcTab2);
			tab2.add(logo1);
		}

		{
			logo1BrowseBut = new JButton(
					Jedecma.localMessagesBundle.getString("BROWSE"));
			logo1BrowseBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					logo1BrowseBut.setEnabled(false);
					fc.addChoosableFileFilter(new MyFileFilter());
					fc.setCurrentDirectory(new File(Uti1.getWrkDir()));
					int returnVal = fc.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String s = fc.getSelectedFile().getAbsolutePath();
						logo1.setText(s);
						setLogo1();
					}
					logo1BrowseBut.setEnabled(true);
				}
			});
			Uti1.bldConst(gbcTab2, 25, 2, 2, 1, 0, 0);
			gbTab2.setConstraints(logo1BrowseBut, gbcTab2);
			tab2.add(logo1BrowseBut);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("LOGO_2"));
			Uti1.bldConst(gbcTab2, 0, 3, 5, 1, 0, 0);
			gbTab2.setConstraints(lab, gbcTab2);
			tab2.add(lab);
			logo2 = new MyJTextField(100, Jedecma.param.getProperty("logo2"),
					new float[] {}, 0);
			logo2.addFocusListener(new CtrTextField());
			logo2.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setLogo2();
				}
			});
			Uti1.bldConst(gbcTab2, 5, 3, 20, 1, 0, 0);
			gbTab2.setConstraints(logo2, gbcTab2);
			tab2.add(logo2);
		}

		{
			logo2BrowseBut = new JButton(
					Jedecma.localMessagesBundle.getString("BROWSE"));
			logo2BrowseBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					logo2BrowseBut.setEnabled(false);
					fc.addChoosableFileFilter(new MyFileFilter());
					fc.setCurrentDirectory(new File(Uti1.getWrkDir()));
					int returnVal = fc.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String s = fc.getSelectedFile().getAbsolutePath();
						logo2.setText(s);
						setLogo2();
					}
					logo2BrowseBut.setEnabled(true);
				}
			});
			Uti1.bldConst(gbcTab2, 25, 3, 2, 1, 0, 0);
			gbTab2.setConstraints(logo2BrowseBut, gbcTab2);
			tab2.add(logo2BrowseBut);
		}

		{
			MyJlabel lab = new MyJlabel("Label Font");
			Uti1.bldConst(gbcTab2, 0, 4, 5, 1, 0, 0);
			gbTab2.setConstraints(lab, gbcTab2);
			tab2.add(lab);
			repLabFnt = new MyJTextField(60,
					Jedecma.param.getProperty("label_font"), new float[] {}, 0);
			repLabFnt.addFocusListener(new CtrTextField());
			repLabFnt.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setRepLabFnt();
				}
			});
			Uti1.bldConst(gbcTab2, 5, 4, 20, 1, 1, 0);
			gbTab2.setConstraints(repLabFnt, gbcTab2);
			tab2.add(repLabFnt);
		}

		{
			repLabFntBut = new JButton(
					Jedecma.localMessagesBundle.getString("SEARCH"));
			repLabFntBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					okBut.setEnabled(false);
					MyFntSel fs = new MyFntSel();
					fs.chooseFont("Label Font", repLabFnt.getText(),
							"Arial,BOLD,9");
					String s = fs.getSelFont();
					if (!s.equals("")) {
						repLabFnt.setText(s);
						setRepLabFnt();
					}

					okBut.setEnabled(true);
				}
			});
			Uti1.bldConst(gbcTab2, 25, 4, 2, 1, 0, 0);
			gbTab2.setConstraints(repLabFntBut, gbcTab2);
			tab2.add(repLabFntBut);
		}

		{
			MyJlabel lab = new MyJlabel("Data Font");
			Uti1.bldConst(gbcTab2, 0, 5, 5, 1, 0, 0);
			gbTab2.setConstraints(lab, gbcTab2);
			tab2.add(lab);
			repDatFnt = new MyJTextField(60,
					Jedecma.param.getProperty("data_font"), new float[] {}, 0);
			repDatFnt.addFocusListener(new CtrTextField());
			repDatFnt.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setRepDatFnt();
				}
			});
			Uti1.bldConst(gbcTab2, 5, 5, 20, 1, 1, 0);
			gbTab2.setConstraints(repDatFnt, gbcTab2);
			tab2.add(repDatFnt);
		}

		{
			repDatFntBut = new JButton(
					Jedecma.localMessagesBundle.getString("SEARCH"));
			repDatFntBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					okBut.setEnabled(false);
					MyFntSel fs = new MyFntSel();
					fs.chooseFont("Data Font", repDatFnt.getText(),
							"Courier,BOLD,9");
					String s = fs.getSelFont();
					if (!s.equals("")) {
						repDatFnt.setText(s);
						setRepDatFnt();
					}

					okBut.setEnabled(true);
				}
			});
			Uti1.bldConst(gbcTab2, 25, 5, 2, 1, 0, 0);
			gbTab2.setConstraints(repDatFntBut, gbcTab2);
			tab2.add(repDatFntBut);
		}

		{
			MyJlabel lab = new MyJlabel("Diagn.Font");
			Uti1.bldConst(gbcTab2, 0, 6, 5, 1, 0, 0);
			gbTab2.setConstraints(lab, gbcTab2);
			tab2.add(lab);
			repDgnFnt = new MyJTextField(60,
					Jedecma.param.getProperty("diag_font"), new float[] {}, 0);
			repDgnFnt.addFocusListener(new CtrTextField());
			repDgnFnt.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setRepDgnFnt();
				}
			});
			Uti1.bldConst(gbcTab2, 5, 6, 20, 1, 1, 0);
			gbTab2.setConstraints(repDgnFnt, gbcTab2);
			tab2.add(repDgnFnt);
		}

		{
			repDgnFntBut = new JButton(
					Jedecma.localMessagesBundle.getString("SEARCH"));
			repDgnFntBut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					okBut.setEnabled(false);
					MyFntSel fs = new MyFntSel();
					fs.chooseFont("Diagn. Font", repDgnFnt.getText(),
							"Arial,PLAIN,9");
					String s = fs.getSelFont();
					if (!s.equals("")) {
						repDgnFnt.setText(s);
						setRepDgnFnt();
					}

					okBut.setEnabled(true);
				}
			});
			Uti1.bldConst(gbcTab2, 25, 6, 2, 1, 0, 0);
			gbTab2.setConstraints(repDgnFntBut, gbcTab2);
			tab2.add(repDgnFntBut);
		}

		tab4 = new JPanel();
		GridBagLayout gbTab4 = new GridBagLayout();
		GridBagConstraints gbcTab4 = new GridBagConstraints();
		tab4.setLayout(gbTab4);
		tabPan.addTab("Database", tab4);

		{
			MyJlabel lab = new MyJlabel("Database Type");
			Uti1.bldConst(gbcTab4, 0, 0, 1, 1, 0, 0);
			gbTab4.setConstraints(lab, gbcTab4);
			tab4.add(lab);

			dbType = new MyJComboBox(new String[] {
					"0=" + "DERBY (internal database, single-user mode)",
					"1=" + "MYSQL", }, String.valueOf(Jedecma.param
					.getProperty("dbtype")));
			dbType.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setDbType();
				}
			});
			Uti1.bldConst(gbcTab4, 1, 0, 20, 1, 1, 0);
			gbTab4.setConstraints(dbType, gbcTab4);
			tab4.add(dbType);

		}

		{
			MyJlabel lab = new MyJlabel("Database URL");
			Uti1.bldConst(gbcTab4, 0, 1, 1, 1, 0, 0);
			gbTab4.setConstraints(lab, gbcTab4);
			tab4.add(lab);

			hostAddrFld = new MyJTextField(60,
					Jedecma.param.getProperty("dbname"), new float[] {}, 0);
			hostAddrFld.addFocusListener(new CtrTextField());
			hostAddrFld.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setHostAddrFld();
				}
			});
			Uti1.bldConst(gbcTab4, 1, 1, 20, 1, 1, 0);
			gbTab4.setConstraints(hostAddrFld, gbcTab4);
			tab4.add(hostAddrFld);

		}

		{
			MyJlabel lab = new MyJlabel("JDBC driver");
			Uti1.bldConst(gbcTab4, 0, 2, 1, 1, 0, 0);
			gbTab4.setConstraints(lab, gbcTab4);
			tab4.add(lab);

			jdbcDriverFld = new MyJTextField(60,
					Jedecma.param.getProperty("jdbcdriver"), new float[] {}, 0);
			jdbcDriverFld.addFocusListener(new CtrTextField());
			jdbcDriverFld.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setJdbcDriverFld();
				}
			});
			Uti1.bldConst(gbcTab4, 1, 2, 20, 1, 1, 0);
			gbTab4.setConstraints(jdbcDriverFld, gbcTab4);
			tab4.add(jdbcDriverFld);

		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("USER"));
			Uti1.bldConst(gbcTab4, 0, 3, 1, 1, 0, 0);
			gbTab4.setConstraints(lab, gbcTab4);
			tab4.add(lab);

			userFld = new MyJTextField(30, Jedecma.param.getProperty("dbuser"),
					new float[] {}, 0);
			userFld.addFocusListener(new CtrTextField());
			userFld.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setUserFld();
				}
			});
			Uti1.bldConst(gbcTab4, 1, 3, 20, 1, 0, 0);
			gbTab4.setConstraints(userFld, gbcTab4);
			tab4.add(userFld);

		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("PASSWORD"));
			Uti1.bldConst(gbcTab4, 0, 4, 1, 1, 0, 0);
			gbTab4.setConstraints(lab, gbcTab4);
			tab4.add(lab);

			passFld = new JPasswordField(30);
			passFld.setText(Base64.decode(Jedecma.param.getProperty("dbpass")));
			passFld.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setPassFld();
				}
			});
			Uti1.bldConst(gbcTab4, 1, 4, 20, 1, 0, 0);
			gbTab4.setConstraints(passFld, gbcTab4);
			tab4.add(passFld);

		}

		tab5 = new JPanel();
		GridBagLayout gbTab5 = new GridBagLayout();
		GridBagConstraints gbcTab5 = new GridBagConstraints();
		tab5.setLayout(gbTab5);
		tabPan.addTab(Jedecma.localMessagesBundle.getString("IMG_CFG"), tab5);

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("IMG_TMP"));
			Uti1.bldConst(gbcTab5, 0, 0, 1, 1, 0, 0);
			gbTab5.setConstraints(lab, gbcTab5);
			tab5.add(lab);

			srcPath = new MyJTextField(128,
					Jedecma.param.getProperty("imgtmp"), new float[] {}, 0);
			srcPath.addFocusListener(new CtrTextField());
			srcPath.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setSrcPath();
				}
			});
			Uti1.bldConst(gbcTab5, 1, 0, 5, 1, 1, 0);
			gbTab5.setConstraints(srcPath, gbcTab5);
			tab5.add(srcPath);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("IMG_PATH"));
			Uti1.bldConst(gbcTab5, 0, 1, 1, 1, 0, 0);
			gbTab5.setConstraints(lab, gbcTab5);
			tab5.add(lab);

			imgPath = new MyJTextField(128,
					Jedecma.param.getProperty("imgpath"), new float[] {}, 0);
			imgPath.addFocusListener(new CtrTextField());
			imgPath.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
				}

				public void focusLost(FocusEvent evt) {
					if (evt.isTemporary()) {
						return;
					}
					setImgPath();
				}
			});
			Uti1.bldConst(gbcTab5, 1, 1, 5, 1, 1, 0);
			gbTab5.setConstraints(imgPath, gbcTab5);
			tab5.add(imgPath);
		}

		butPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Uti1.bldConst(gbcMPan, 0, 11, GridBagConstraints.REMAINDER, 1, 1, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL);
		gbMPan.setConstraints(butPan, gbcMPan);
		mainPan.add(butPan);

		okBut = new JButton(Jedecma.localMessagesBundle.getString("CB_OK"));
		okBut.setMnemonic(KeyEvent.VK_O);
		butPan.add(okBut);

		okBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okBut.setEnabled(false);
				String parmfile = "jedecma.prm";

				// Write properties file.
				try {
					props.store(new FileOutputStream(parmfile), null);
				} catch (IOException ioe) {
					System.out.println("unable to write parameter file "
							+ parmfile);
				}

				JOptionPane.showMessageDialog(Jedecma.mf,
						Jedecma.localMessagesBundle.getString("PLS_RESTART"),
						"Info", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

	int getTextOffs() {
		return Integer.parseInt(props.getProperty("txt_y0"));
	}

	void setTextOffs() {
		int n = Integer.parseInt(textOffs.getInpValue());
		props.put("txt_y0", String.valueOf(n));
	}

	String getLang() {
		String language = props.getProperty("language");
		if (language.equals("")) {
			language = "en";
		}
		String country = props.getProperty("country");
		if (country.equals("")) {
			country = "US";
		}
		return language + "_" + country;
	}

	void setLang() {
		String v = lang.getInpValue().trim();
		String language = v.substring(0, 2);
		String country = v.substring(3, 5);

		props.put("language", language);
		props.put("country", country);
	}

	void setDrName() {
		props.put("drname", drName.getText());
	}

	void setLogo1() {
		props.put("logo1", logo1.getText());
	}

	void setLogo2() {
		props.put("logo2", logo2.getText());
	}

	void setLabFnt() {
		props.put("jlabel_font", labFnt.getText());
	}

	void setTxfFnt() {
		props.put("jtextfield_font", txfFnt.getText());
	}

	void setTxaFnt() {
		props.put("jtextarea_font", txaFnt.getText());
	}

	void setBoxFnt() {
		props.put("jcombobox_font", boxFnt.getText());
	}

	void setRepLabFnt() {
		props.put("label_font", repLabFnt.getText());
	}

	void setRepDatFnt() {
		props.put("data_font", repDatFnt.getText());
	}

	void setRepDgnFnt() {
		props.put("diag_font", repDgnFnt.getText());
	}

	void setSplash() {
		props.put("splash", splash.getText());
	}

	void setHostAddrFld() {
		props.put("dbname", hostAddrFld.getText());
	}

	void setJdbcDriverFld() {
		props.put("jdbcdriver", jdbcDriverFld.getText());
	}

	void setDbType() {
		props.put("dbtype", dbType.getInpValue());
	}

	void setUserFld() {
		props.put("dbuser", userFld.getText());
	}

	private void setPassFld() {
		String pwString = "";
		{
			char[] input = passFld.getPassword();
			for (int i = 0; i < input.length; i++) {
				pwString += input[i];
			}
		}
		props.put("dbpass", Base64.encode(pwString));
	}

	void setSrcPath() {
		props.put("imgtmp", srcPath.getText());
	}

	void setImgPath() {
		props.put("imgpath", imgPath.getText());
	}

	public String[] getLangProps() {
		String path = Uti1.getWrkDir();
		File folder = new File(path);
		LangFilenameFilter f = new LangFilenameFilter();
		File[] listOfFiles = folder.listFiles(f);
		int nr = listOfFiles.length;
		String[] list = new String[nr];
		String lang_id = "";
		int index = 0;
		list[index] = "en_US"; // default
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String name = listOfFiles[i].getName();
				if (!name.equals("Messages.properties")) {
					lang_id = name.substring(9, 14);
					index++;
					list[index] = lang_id;
				}
			}
		}
		return list;
	}
} // end Config class

class MyFileFilter extends javax.swing.filechooser.FileFilter {
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String extension = Uti1.getExtension(f);
		if (extension != null) {
			extension = extension.toLowerCase();
			if (extension.equals("jpg") || extension.equals("jpeg")
					|| extension.equals("png") || extension.equals("gif")) {
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

class LangFilenameFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		if (name.length() > 8) {
			String basename = name.substring(0, 8);
			if (basename.equals("Messages")) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

}
