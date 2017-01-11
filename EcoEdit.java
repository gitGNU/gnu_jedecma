/* 
 * EcoEdit.java - examination data dialog
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class EcoEdit extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel mainPan, hdrPan, tab1, tab2, tab3, tab4, tab5, butPan;
	private JTabbedPane tabPan;
	private JButton okBut, quitBut, anaBut;
	private String lockCode;
	boolean busy, abort, readOnly;
	int nrexam;
	EcoExam exa;
	EcoTxt ecoTxt;
	AnaDat ana;
	MyJTextField pesopz, altzpz, nmrfgl, fampat, menarc, datums, menopa,
			spsghi, spsgh1;
	MyJTextField drmamm, mastse, taprgh, taregh, taingh, termed, terchi,
			dtmamm, tmsede;
	MyJTextField tmcomp, discut, disfas, discap, tmdime, tmsupe, tmvolu,
			datesa, cognom;
	MyJTextField datnas, ecbprv, dgneco, dgnist, dgncit;
	MyJTextField dtleco, opleco, dtlmri, oplmri, dtlelg, oplelg, dtlfna,
			oplfna;
	MyJTextField dtlbio, oplbio;

	MyJComboBox citeco, isteco, allatt, mstrzn, esmamm, egmamm;
	MyJComboBox tcapez, mastod, tdotti, mpfbcs, tescrc, mpmics, ematic, mpmacs,
			fibnod;
	MyJComboBox vgencs, arprol, esmica, esmaca, tmcont, tmmarg, tmegen, tmattn,
			tmcono;
	MyJComboBox tmtunn, tmmaca, tmmica, tmtmac, tmtmic, tmcoop, tmsasc, tmsarm;
	MyJComboBox tmegfm, tminfa, tmdopp, tmegcu, tmmalg, tsttum;
	MyJComboBox releco, relmri, relelg, relfna, relbio, prolid;

	DgnEditor dgnpan;
	FileMgr filePan;

	public EcoEdit(boolean ro) {
		readOnly = ro;
		setModal(!readOnly);
		busy = false;
		abort = false;
		lockCode = "";
	}

	public EcoEdit() {
		this(false);
	}

	public void edit(String dummy, int nr) {
		// per nuovi esami: passa il numero anagrafica
		if (nr > 0) {
			ana = new AnaDat();
			int rc = ana.readAna(Jedecma.dbmgr, nr);
			if (rc != 0) {
				Uti1.error(
						Jedecma.localMessagesBundle.getString("PAT_NOT_FOUND")
								+ " " + String.valueOf(nr), false);
				abort = true;
				stop();
			} else {
				exa = new EcoExam();
				nrexam = 0;
				exa.cognom = ana.anname;
				exa.datnas = ana.anbrtd;
				exa.ecbprv = ana.anbprv;
				exa.fampat = ana.anfami;
				exa.menarc = ana.anmena;
				exa.nmrfgl = ana.anchld;
				exa.allatt = ana.ansuck;
				exa.mstrzn = ana.anmens;
				exa.menopa = ana.anmenp;
				exa.altzpz = ana.anheig;
				exa.pesopz = ana.anweig;
				edit(nrexam);
			}
		} else {
			Uti1.error(
					"EcoEdit: "
							+ Jedecma.localMessagesBundle
									.getString("INVALID_PAT_CODE") + " "
							+ String.valueOf(nr), false);
			abort = true;
			stop();
		}
	}

	public void edit(int nr) {
		nrexam = nr;
		String nrs = String.valueOf(nr);
		String title = Jedecma.localMessagesBundle.getString("EXAM_EDIT") + " "
				+ nrs;
		if (nr < 0) {
			Uti1.error(
					"EcoEdit: "
							+ Jedecma.localMessagesBundle
									.getString("INVALID_EXAM_NR") + " " + nrs,
					false);
			abort = true;
			stop();
		}

		if (nr == 0) {
			title = Jedecma.localMessagesBundle.getString("NEW_EXAM");
			lockCode = "";
		}
		setTitle(title);

		if (nr > 0) {
			exa = new EcoExam();
			// check lock
			lockCode = "EDECMA." + nrs;
			int lockrv = MyLock.setLock(Jedecma.dbmgr, lockCode,
					Jedecma.user.userid, true);
			if (lockrv < 0) {
				Uti1.error(
						"EcoEdit: "
								+ Jedecma.localMessagesBundle
										.getString("EXAM_NR")
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

			int rc = exa.readExam(Jedecma.dbmgr, nr);
			if (rc != 0) {
				Uti1.error(
						Jedecma.localMessagesBundle.getString("EXAM_NR")
								+ ": "
								+ String.valueOf(nr)
								+ Jedecma.localMessagesBundle
										.getString("NOT_AVAILABLE"), false);

				abort = true;
				stop();
			}
		}

		if (!abort) {

			ecoTxt = new EcoTxt();
			if (nr > 0) {
				ecoTxt.readEcoTxt(Jedecma.dbmgr, nr);
			}

			mainPan = new JPanel();

			hdrPan = new JPanel();
			GridBagLayout gbHdr = new GridBagLayout();
			GridBagConstraints gbcHdr = new GridBagConstraints();
			hdrPan.setLayout(gbHdr);

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("EXAM_NR"));
				Uti1.bldConst(gbcHdr, 0, 0, 3, 1, 0, 0);
				gbHdr.setConstraints(lab, gbcHdr);
				hdrPan.add(lab);
			}

			{
				MyJlabel lab = new MyJlabel(String.valueOf(nr));
				lab.setBorder(BorderFactory.createLoweredBevelBorder());
				Uti1.bldConst(gbcHdr, 3, 0, 3, 1, 0, 0);
				gbHdr.setConstraints(lab, gbcHdr);
				hdrPan.add(lab);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("EXAM_DATE"));
				Uti1.bldConst(gbcHdr, 10, 0, 3, 1, 0, 0);
				gbHdr.setConstraints(lab, gbcHdr);
				hdrPan.add(lab);
				datesa = new MyJTextField(10, Uti1.date2String(exa.datesa),
						new float[] {}, 6);
				datesa.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcHdr, 13, 0, 4, 1, 0, 0);
				gbHdr.setConstraints(datesa, gbcHdr);
				hdrPan.add(datesa);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("NAME"));
				Uti1.bldConst(gbcHdr, 0, 1, 3, 1, 0, 0);
				gbHdr.setConstraints(lab, gbcHdr);
				hdrPan.add(lab);
				cognom = new MyJTextField(30, exa.cognom, new float[] {}, 7);
				cognom.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcHdr, 3, 1, 7, 1, 0, 0);
				gbHdr.setConstraints(cognom, gbcHdr);
				hdrPan.add(cognom);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("BIRTH_DATE"));
				Uti1.bldConst(gbcHdr, 10, 1, 3, 1, 0, 0);
				gbHdr.setConstraints(lab, gbcHdr);
				hdrPan.add(lab);
				String ws = "";
				if (Uti1.date2String(exa.datnas).length() > 0) {
					ws = Uti1.date2String(exa.datnas);
				}
				datnas = new MyJTextField(10, ws, new float[] {}, 6);
				datnas.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcHdr, 13, 1, 4, 1, 0, 0);
				gbHdr.setConstraints(datnas, gbcHdr);
				hdrPan.add(datnas);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("PROV"));
				Uti1.bldConst(gbcHdr, 18, 1, 2, 1, 0, 0);
				gbHdr.setConstraints(lab, gbcHdr);
				hdrPan.add(lab);
				ecbprv = new MyJTextField(4, exa.ecbprv, new float[] {}, 7);
				ecbprv.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcHdr, 20, 1, 1, 1, 0, 0);
				gbHdr.setConstraints(ecbprv, gbcHdr);
				hdrPan.add(ecbprv);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("ECO_DIAG"));
				Uti1.bldConst(gbcHdr, 0, 2, 3, 1, 0, 0);
				gbHdr.setConstraints(lab, gbcHdr);
				hdrPan.add(lab);
				dgneco = new MyJTextField(20, exa.dgneco, new float[] {}, 0);
				dgneco.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcHdr, 3, 2, 5, 1, 0, 0);
				gbHdr.setConstraints(dgneco, gbcHdr);
				hdrPan.add(dgneco);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("IST_DIAG"));
				Uti1.bldConst(gbcHdr, 10, 2, 3, 1, 0, 0);
				gbHdr.setConstraints(lab, gbcHdr);
				hdrPan.add(lab);
				dgnist = new MyJTextField(20, exa.dgnist, new float[] {}, 0);
				dgnist.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcHdr, 13, 2, 5, 1, 0, 0);
				gbHdr.setConstraints(dgnist, gbcHdr);
				hdrPan.add(dgnist);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("PER_MALIGN"));
				Uti1.bldConst(gbcHdr, 0, 3, 3, 1, 0, 0);
				gbHdr.setConstraints(lab, gbcHdr);
				hdrPan.add(lab);
				tmmalg = new MyJComboBox(new String[] {
						"0=" + Jedecma.localMessagesBundle.getString("CB_NA"),
						"1=0", "2=25", "3=50", "4=75", "5=100" },
						String.valueOf(exa.tmmalg));
				Uti1.bldConst(gbcHdr, 3, 3, 2, 1, 0, 0);
				gbHdr.setConstraints(tmmalg, gbcHdr);
				hdrPan.add(tmmalg);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("CIT_DIAG"));
				Uti1.bldConst(gbcHdr, 10, 3, 3, 1, 0, 0);
				gbHdr.setConstraints(lab, gbcHdr);
				hdrPan.add(lab);
				dgncit = new MyJTextField(20, exa.dgncit, new float[] {}, 0);
				dgncit.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcHdr, 13, 3, 5, 1, 1, 0);
				gbHdr.setConstraints(dgncit, gbcHdr);
				hdrPan.add(dgncit);
			}

			mainPan.add(hdrPan);

			tabPan = new JTabbedPane(JTabbedPane.TOP);

			tab4 = new JPanel();
			GridBagLayout gbtab4 = new GridBagLayout();
			GridBagConstraints gbctab4 = new GridBagConstraints();
			tab4.setLayout(gbtab4);
			tabPan.addTab(Jedecma.localMessagesBundle.getString("ECO_PG4"),
					tab4);

			// buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw,
			// int gh, int wx, int wy)

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("WEIGHT") + " kg");
				Uti1.bldConst(gbctab4, 0, 0, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				pesopz = new MyJTextField(5, String.valueOf(exa.pesopz),
						new float[] { 0, 250 }, 2);
				pesopz.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 3, 0, 2, 1, 0, 0);
				gbtab4.setConstraints(pesopz, gbctab4);
				tab4.add(pesopz);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("HEIGHT") + " cm");
				Uti1.bldConst(gbctab4, 5, 0, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				altzpz = new MyJTextField(5, String.valueOf(exa.altzpz),
						new float[] { 0, 220 }, 2);
				altzpz.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 8, 0, 2, 1, 0, 0);
				gbtab4.setConstraints(altzpz, gbctab4);
				tab4.add(altzpz);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("FAM_PATHOL"));
				Uti1.bldConst(gbctab4, 10, 0, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				fampat = new MyJTextField(20, exa.fampat, new float[] {}, 7);
				fampat.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 13, 0, 7, 1, 0, 0);
				gbtab4.setConstraints(fampat, gbctab4);
				tab4.add(fampat);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("NR_CHILDREN"));
				Uti1.bldConst(gbctab4, 0, 1, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				nmrfgl = new MyJTextField(3, String.valueOf(exa.nmrfgl),
						new float[] { 0, 20 }, 1);
				nmrfgl.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 3, 1, 2, 1, 0, 0);
				gbtab4.setConstraints(nmrfgl, gbctab4);
				tab4.add(nmrfgl);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("SUCKLE"));
				Uti1.bldConst(gbctab4, 5, 1, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				allatt = new MyJComboBox(
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
						String.valueOf(exa.allatt));
				Uti1.bldConst(gbctab4, 8, 1, 2, 1, 0, 0);
				gbtab4.setConstraints(allatt, gbctab4);
				tab4.add(allatt);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("AGE_MENARCHE"));
				Uti1.bldConst(gbctab4, 0, 2, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				menarc = new MyJTextField(3, String.valueOf(exa.menarc),
						new float[] { 0, 99 }, 1);
				menarc.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 3, 2, 2, 1, 0, 0);
				gbtab4.setConstraints(menarc, gbctab4);
				tab4.add(menarc);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MENSTR"));
				Uti1.bldConst(gbctab4, 5, 2, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				mstrzn = new MyJComboBox(
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
						String.valueOf(exa.mstrzn));
				Uti1.bldConst(gbctab4, 8, 2, 2, 1, 0, 0);
				gbtab4.setConstraints(mstrzn, gbctab4);
				tab4.add(mstrzn);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("DATE_LST_CYCLE"));
				Uti1.bldConst(gbctab4, 10, 2, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				String ws = "";
				if (Uti1.date2String(exa.datums).length() > 0) {
					ws = Uti1.date2String(exa.datums);
				}
				datums = new MyJTextField(10, ws, new float[] {}, 6);
				datums.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 13, 2, 3, 1, 0, 0);
				gbtab4.setConstraints(datums, gbctab4);
				tab4.add(datums);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MENOP"));
				Uti1.bldConst(gbctab4, 15, 2, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				menopa = new MyJTextField(3, String.valueOf(exa.menopa),
						new float[] { 0, 99 }, 1);
				menopa.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 18, 2, 2, 1, 0, 0);
				gbtab4.setConstraints(menopa, gbctab4);
				tab4.add(menopa);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MEDIC_TH"));
				Uti1.bldConst(gbctab4, 0, 3, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				termed = new MyJTextField(20, exa.termed, new float[] {}, 0);
				termed.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 3, 3, 7, 1, 0, 0);
				gbtab4.setConstraints(termed, gbctab4);
				tab4.add(termed);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("LAST_SURG_TH"));
				Uti1.bldConst(gbctab4, 10, 3, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				terchi = new MyJTextField(20, exa.terchi, new float[] {}, 0);
				terchi.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 13, 3, 7, 1, 0, 0);
				gbtab4.setConstraints(terchi, gbctab4);
				tab4.add(terchi);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MAMM_PHD"));
				Uti1.bldConst(gbctab4, 0, 4, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				drmamm = new MyJTextField(20, exa.drmamm, new float[] {}, 0);
				drmamm.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 3, 4, 7, 1, 0, 0);
				gbtab4.setConstraints(drmamm, gbctab4);
				tab4.add(drmamm);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MAMM_DATE"));
				Uti1.bldConst(gbctab4, 10, 4, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				String ws = "";
				if (Uti1.date2String(exa.dtmamm).length() > 0) {
					ws = Uti1.date2String(exa.dtmamm);
				}
				dtmamm = new MyJTextField(10, ws, new float[] {}, 6);
				dtmamm.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 13, 4, 2, 1, 0, 0);
				gbtab4.setConstraints(dtmamm, gbctab4);
				tab4.add(dtmamm);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MAMM_RES"));
				Uti1.bldConst(gbctab4, 15, 4, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				esmamm = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("MAMM_RES_NEG"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("MAMM_RES_DUB"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("MAMM_RES_POS"), },
						String.valueOf(exa.esmamm));
				Uti1.bldConst(gbctab4, 18, 4, 2, 1, 0, 0);
				gbtab4.setConstraints(esmamm, gbctab4);
				tab4.add(esmamm);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("ECGR_PHD"));
				Uti1.bldConst(gbctab4, 0, 5, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				opleco = new MyJTextField(20, exa.opleco, new float[] {}, 0);
				opleco.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 3, 5, 7, 1, 0, 0);
				gbtab4.setConstraints(opleco, gbctab4);
				tab4.add(opleco);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("ECGR_DATE"));
				Uti1.bldConst(gbctab4, 10, 5, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				String ws = "";
				if (Uti1.date2String(exa.dtleco).length() > 0) {
					ws = Uti1.date2String(exa.dtleco);
				}
				dtleco = new MyJTextField(10, ws, new float[] {}, 6);
				dtleco.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 13, 5, 2, 1, 0, 0);
				gbtab4.setConstraints(dtleco, gbctab4);
				tab4.add(dtleco);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("ECGR_RES"));
				Uti1.bldConst(gbctab4, 15, 5, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				releco = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("ECGR_RES_1"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("ECGR_RES_2"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("ECGR_RES_3"), },
						String.valueOf(exa.releco));
				Uti1.bldConst(gbctab4, 18, 5, 2, 1, 0, 0);
				gbtab4.setConstraints(releco, gbctab4);
				tab4.add(releco);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MRES_PHD"));
				Uti1.bldConst(gbctab4, 0, 6, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				oplmri = new MyJTextField(20, exa.oplmri, new float[] {}, 0);
				oplmri.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 3, 6, 7, 1, 0, 0);
				gbtab4.setConstraints(oplmri, gbctab4);
				tab4.add(oplmri);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MRES_DATE"));
				Uti1.bldConst(gbctab4, 10, 6, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				String ws = "";
				if (Uti1.date2String(exa.dtlmri).length() > 0) {
					ws = Uti1.date2String(exa.dtlmri);
				}
				dtlmri = new MyJTextField(10, ws, new float[] {}, 6);
				dtlmri.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbctab4, 13, 6, 2, 1, 0, 0);
				gbtab4.setConstraints(dtlmri, gbctab4);
				tab4.add(dtlmri);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MRES_RES"));
				Uti1.bldConst(gbctab4, 15, 6, 3, 1, 0, 0);
				gbtab4.setConstraints(lab, gbctab4);
				tab4.add(lab);
				relmri = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("MRES_RES_1"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("MRES_RES_2"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("MRES_RES_3"), },
						String.valueOf(exa.relmri));
				Uti1.bldConst(gbctab4, 18, 6, 2, 1, 0, 0);
				gbtab4.setConstraints(relmri, gbctab4);
				tab4.add(relmri);
			}

			// fine tab4

			tab1 = new JPanel();
			GridBagLayout gbTab1 = new GridBagLayout();
			GridBagConstraints gbcTab1 = new GridBagConstraints();
			tab1.setLayout(gbTab1);
			tabPan.addTab(Jedecma.localMessagesBundle.getString("ECO_PG1"),
					tab1);

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle
								.getString("ECODGN_EQ_CITECO"));
				Uti1.bldConst(gbcTab1, 0, 0, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				citeco = new MyJComboBox(
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
						String.valueOf(exa.citeco));
				Uti1.bldConst(gbcTab1, 3, 0, 2, 1, 0, 0);
				gbTab1.setConstraints(citeco, gbcTab1);
				tab1.add(citeco);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle
								.getString("ECODGN_EQ_ISTECO"));
				Uti1.bldConst(gbcTab1, 10, 0, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				isteco = new MyJComboBox(
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
						String.valueOf(exa.isteco));
				Uti1.bldConst(gbcTab1, 13, 0, 2, 1, 0, 0);
				gbTab1.setConstraints(isteco, gbcTab1);
				tab1.add(isteco);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("GLND_THICK_1")
								+ " mm");
				Uti1.bldConst(gbcTab1, 0, 1, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				spsghi = new MyJTextField(4, String.valueOf(exa.spsghi),
						new float[] { 0, 999 }, 2);
				spsghi.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab1, 3, 1, 2, 1, 0, 0);
				gbTab1.setConstraints(spsghi, gbcTab1);
				tab1.add(spsghi);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("GLND_THICK_2")
								+ " mm");
				Uti1.bldConst(gbcTab1, 5, 1, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				spsgh1 = new MyJTextField(4, String.valueOf(exa.spsgh1),
						new float[] { 0, 999 }, 2);
				spsgh1.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab1, 8, 1, 2, 1, 0, 0);
				gbTab1.setConstraints(spsgh1, gbcTab1);
				tab1.add(spsgh1);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MAM_ECOGEN"));
				Uti1.bldConst(gbcTab1, 10, 1, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				egmamm = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("MAM_ECOGEN_NONE"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("MAM_ECOGEN_LOW"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("MAM_ECOGEN_MID"),
								"4="
										+ Jedecma.localMessagesBundle
												.getString("MAM_ECOGEN_HIGH") },
						String.valueOf(exa.egmamm));
				Uti1.bldConst(gbcTab1, 13, 1, 2, 1, 0, 0);
				gbTab1.setConstraints(egmamm, gbcTab1);
				tab1.add(egmamm);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle
								.getString("PREGL_ADIP_TISS") + " mm");
				Uti1.bldConst(gbcTab1, 0, 2, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				taprgh = new MyJTextField(3, String.valueOf(exa.taprgh),
						new float[] { 0, 999 }, 1);
				taprgh.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab1, 3, 2, 2, 1, 0, 0);
				gbTab1.setConstraints(taprgh, gbcTab1);
				tab1.add(taprgh);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle
								.getString("RETROMAM_ADIP_TISS") + " mm");
				Uti1.bldConst(gbcTab1, 5, 2, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				taregh = new MyJTextField(3, String.valueOf(exa.taregh),
						new float[] { 0, 999 }, 1);
				taregh.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab1, 8, 2, 2, 1, 0, 0);
				gbTab1.setConstraints(taregh, gbcTab1);
				tab1.add(taregh);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle
								.getString("INTRAGL_ADIP_TISS") + " mm");
				Uti1.bldConst(gbcTab1, 10, 2, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				taingh = new MyJTextField(3, String.valueOf(exa.taingh),
						new float[] { 0, 999 }, 1);
				taingh.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab1, 13, 2, 2, 1, 0, 0);
				gbTab1.setConstraints(taingh, gbcTab1);
				tab1.add(taingh);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("NIPPLES"));
				Uti1.bldConst(gbcTab1, 0, 3, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				tcapez = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("NIPPLES_NORM"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("NIPPLES_RETR") },
						String.valueOf(exa.tcapez));
				Uti1.bldConst(gbcTab1, 3, 3, 2, 1, 0, 0);
				gbTab1.setConstraints(tcapez, gbcTab1);
				tab1.add(tcapez);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MASTODYNIA"));
				Uti1.bldConst(gbcTab1, 5, 3, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				mastod = new MyJComboBox(
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
						String.valueOf(exa.mastod));
				Uti1.bldConst(gbcTab1, 8, 3, 2, 1, 0, 0);
				gbTab1.setConstraints(mastod, gbcTab1);
				tab1.add(mastod);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle
								.getString("MASTODYNIA_SITE"));
				Uti1.bldConst(gbcTab1, 10, 3, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				mastse = new MyJTextField(10, exa.mastse, new float[] {}, 7);
				mastse.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab1, 13, 3, 5, 1, 0, 0);
				gbTab1.setConstraints(mastse, gbcTab1);
				tab1.add(mastse);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("DUCTS"));
				Uti1.bldConst(gbcTab1, 0, 4, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				tdotti = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("DUCTS_NORM"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("DUCTS_DILAT"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("DUCTS_DIST") },
						String.valueOf(exa.tdotti));
				Uti1.bldConst(gbcTab1, 3, 4, 2, 1, 0, 0);
				gbTab1.setConstraints(tdotti, gbcTab1);
				tab1.add(tdotti);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("PROLID"));
				Uti1.bldConst(gbcTab1, 10, 4, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				prolid = new MyJComboBox(
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
						String.valueOf(exa.prolid));
				Uti1.bldConst(gbcTab1, 13, 4, 2, 1, 0, 0);
				gbTab1.setConstraints(prolid, gbcTab1);
				tab1.add(prolid);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MASTOP_FICY"));
				Uti1.bldConst(gbcTab1, 0, 5, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				mpfbcs = new MyJComboBox(
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
						String.valueOf(exa.mpfbcs));
				Uti1.bldConst(gbcTab1, 3, 5, 2, 1, 0, 0);
				gbTab1.setConstraints(mpfbcs, gbcTab1);
				tab1.add(mpfbcs);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MASTOP_MICY"));
				Uti1.bldConst(gbcTab1, 5, 5, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				mpmics = new MyJComboBox(
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
						String.valueOf(exa.mpmics));
				Uti1.bldConst(gbcTab1, 8, 5, 2, 1, 0, 0);
				gbTab1.setConstraints(mpmics, gbcTab1);
				tab1.add(mpmics);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MASTOP_MACY"));
				Uti1.bldConst(gbcTab1, 10, 5, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				mpmacs = new MyJComboBox(
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
						String.valueOf(exa.mpmacs));
				Uti1.bldConst(gbcTab1, 13, 5, 2, 1, 0, 0);
				gbTab1.setConstraints(mpmacs, gbcTab1);
				tab1.add(mpmacs);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("SECRETION"));
				Uti1.bldConst(gbcTab1, 0, 6, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				tescrc = new MyJComboBox(
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
						String.valueOf(exa.tescrc));
				Uti1.bldConst(gbcTab1, 3, 6, 2, 1, 0, 0);
				gbTab1.setConstraints(tescrc, gbcTab1);
				tab1.add(tescrc);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("HEMATIC"));
				Uti1.bldConst(gbcTab1, 10, 6, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				ematic = new MyJComboBox(
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
						String.valueOf(exa.ematic));
				Uti1.bldConst(gbcTab1, 13, 6, 2, 1, 0, 0);
				gbTab1.setConstraints(ematic, gbcTab1);
				tab1.add(ematic);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("NODULAR_FIBR"));
				Uti1.bldConst(gbcTab1, 0, 7, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				fibnod = new MyJComboBox(
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
						String.valueOf(exa.fibnod));
				Uti1.bldConst(gbcTab1, 3, 7, 2, 1, 0, 0);
				gbTab1.setConstraints(fibnod, gbcTab1);
				tab1.add(fibnod);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("ENDOC_PROL"));
				Uti1.bldConst(gbcTab1, 10, 7, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				vgencs = new MyJComboBox(
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
						String.valueOf(exa.vgencs));
				Uti1.bldConst(gbcTab1, 13, 7, 2, 1, 0, 0);
				gbTab1.setConstraints(vgencs, gbcTab1);
				tab1.add(vgencs);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MICROCAL"));
				Uti1.bldConst(gbcTab1, 0, 8, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				esmica = new MyJComboBox(
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
						String.valueOf(exa.esmica));
				Uti1.bldConst(gbcTab1, 3, 8, 2, 1, 0, 0);
				gbTab1.setConstraints(esmica, gbcTab1);
				tab1.add(esmica);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("PROL_AREA"));
				Uti1.bldConst(gbcTab1, 5, 8, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				arprol = new MyJComboBox(
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
						String.valueOf(exa.arprol));
				Uti1.bldConst(gbcTab1, 8, 8, 2, 1, 0, 0);
				gbTab1.setConstraints(arprol, gbcTab1);
				tab1.add(arprol);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MACROCAL"));
				Uti1.bldConst(gbcTab1, 10, 8, 3, 1, 0, 0);
				gbTab1.setConstraints(lab, gbcTab1);
				tab1.add(lab);
				esmaca = new MyJComboBox(
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
						String.valueOf(exa.esmaca));
				Uti1.bldConst(gbcTab1, 13, 8, 2, 1, 0, 0);
				gbTab1.setConstraints(esmaca, gbcTab1);
				tab1.add(esmaca);
			}

			/*
			 * // distanziatore finale { MyJlabel lab = new MyJlabel("");
			 * Uti1.bldConst(gbcTab1, 0,9,30,1,0,1); gbTab1.setConstraints(lab,
			 * gbcTab1); tab1.add(lab); }
			 */

			// fine tab1

			tab2 = new JPanel();
			GridBagLayout gbTab2 = new GridBagLayout();
			GridBagConstraints gbcTab2 = new GridBagConstraints();
			tab2.setLayout(gbTab2);
			tabPan.addTab(Jedecma.localMessagesBundle.getString("ECO_PG2"),
					tab2);

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("SITE"));
				Uti1.bldConst(gbcTab2, 0, 0, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmsede = new MyJTextField(10, exa.tmsede, new float[] {}, 7);
				tmsede.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab2, 3, 0, 7, 2, 0, 0);
				gbTab2.setConstraints(tmsede, gbcTab2);
				tab2.add(tmsede);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("ONSET_AGE"));
				Uti1.bldConst(gbcTab2, 10, 0, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmcomp = new MyJTextField(3, String.valueOf(exa.tmcomp),
						new float[] { 0, 99 }, 1);
				tmcomp.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab2, 13, 0, 2, 1, 0, 0);
				gbTab2.setConstraints(tmcomp, gbcTab2);
				tab2.add(tmcomp);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("DIST_SKIN")
								+ " mm.");
				Uti1.bldConst(gbcTab2, 0, 1, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				discut = new MyJTextField(4, String.valueOf(exa.discut),
						new float[] { 0, 999 }, 2);
				discut.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab2, 3, 1, 2, 1, 0, 0);
				gbTab2.setConstraints(discut, gbcTab2);
				tab2.add(discut);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("DIST_BAND")
								+ " mm.");
				Uti1.bldConst(gbcTab2, 5, 1, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				disfas = new MyJTextField(4, String.valueOf(exa.disfas),
						new float[] { 0, 999 }, 2);
				disfas.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab2, 8, 1, 2, 1, 0, 0);
				gbTab2.setConstraints(disfas, gbcTab2);
				tab2.add(disfas);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("DIST_NIPP")
								+ " mm.");
				Uti1.bldConst(gbcTab2, 10, 1, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				discap = new MyJTextField(4, String.valueOf(exa.discap),
						new float[] { 0, 999 }, 2);
				discap.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab2, 13, 1, 2, 1, 0, 0);
				gbTab2.setConstraints(discap, gbcTab2);
				tab2.add(discap);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("DIMENS")
								+ " .mm");
				Uti1.bldConst(gbcTab2, 0, 2, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmdime = new MyJTextField(12, exa.tmdime, new float[] {}, 7);
				tmdime.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab2, 3, 2, 7, 1, 0, 0);
				gbTab2.setConstraints(tmdime, gbcTab2);
				tab2.add(tmdime);
			}

			tmsupe = new MyJTextField(4, String.valueOf(exa.tmsupe),
					new float[] { 0, 999 }, 2);
			/*
			 * { eliminato 04/07/2013 MyJlabel lab = new
			 * MyJlabel(Jedecma.localMessagesBundle.getString("SURFACE")
			 * +" cmq"); Uti1.bldConst(gbcTab2, 10, 2, 3, 1, 0, 0);
			 * gbTab2.setConstraints(lab, gbcTab2); tab2.add(lab); tmsupe = new
			 * MyJTextField (4, String.valueOf(exa.tmsupe), new float[]{0, 999},
			 * 2); tmsupe.addFocusListener(new CtrTextField());
			 * Uti1.bldConst(gbcTab2, 13, 2, 2, 1, 0, 0);
			 * gbTab2.setConstraints(tmsupe, gbcTab2); tab2.add(tmsupe); }
			 */

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("VOLUME")
								+ " ml.");
				Uti1.bldConst(gbcTab2, 10, 2, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmvolu = new MyJTextField(4, String.valueOf(exa.tmvolu),
						new float[] { 0, 999 }, 2);
				tmvolu.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab2, 13, 2, 2, 1, 0, 0);
				gbTab2.setConstraints(tmvolu, gbcTab2);
				tab2.add(tmvolu);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("CONTOURS"));
				Uti1.bldConst(gbcTab2, 0, 3, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmcont = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("CONTOURS_NEV"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("CONTOURS_GRAD"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("CONTOURS_NET") },
						String.valueOf(exa.tmcont));
				Uti1.bldConst(gbcTab2, 3, 3, 2, 1, 0, 0);
				gbTab2.setConstraints(tmcont, gbcTab2);
				tab2.add(tmcont);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MARGINS"));
				Uti1.bldConst(gbcTab2, 10, 3, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmmarg = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("MARGINS_REG"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("MARGINS_IRR"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("MARGINS_JAG"),
								"4="
										+ Jedecma.localMessagesBundle
												.getString("MARGINS_INF") },
						String.valueOf(exa.tmmarg));
				Uti1.bldConst(gbcTab2, 13, 3, 2, 1, 0, 0);
				gbTab2.setConstraints(tmmarg, gbcTab2);
				tab2.add(tmmarg);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("TUM_ECOGEN"));
				Uti1.bldConst(gbcTab2, 0, 4, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmegen = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("TUM_ECOGEN_NONE"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("TUM_ECOGEN_LOW"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("TUM_ECOGEN_MID"),
								"4="
										+ Jedecma.localMessagesBundle
												.getString("TUM_ECOGEN_HIGH") },
						String.valueOf(exa.tmegen));
				Uti1.bldConst(gbcTab2, 3, 4, 2, 1, 0, 0);
				gbTab2.setConstraints(tmegen, gbcTab2);
				tab2.add(tmegen);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("ATTENUAT"));
				Uti1.bldConst(gbcTab2, 10, 4, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmattn = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("ATTENUAT_NONE"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("ATTENUAT_LOW"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("ATTENUAT_MID"),
								"4="
										+ Jedecma.localMessagesBundle
												.getString("ATTENUAT_HIGH") },
						String.valueOf(exa.tmattn));
				Uti1.bldConst(gbcTab2, 13, 4, 2, 1, 0, 0);
				gbTab2.setConstraints(tmattn, gbcTab2);
				tab2.add(tmattn);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("SHADOW_CONE"));
				Uti1.bldConst(gbcTab2, 0, 5, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmcono = new MyJComboBox(
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
						String.valueOf(exa.tmcono));
				Uti1.bldConst(gbcTab2, 3, 5, 2, 1, 0, 0);
				gbTab2.setConstraints(tmcono, gbcTab2);
				tab2.add(tmcono);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("TUNNEL"));
				Uti1.bldConst(gbcTab2, 10, 5, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmtunn = new MyJComboBox(
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
						String.valueOf(exa.tmtunn));
				Uti1.bldConst(gbcTab2, 13, 5, 2, 1, 0, 0);
				gbTab2.setConstraints(tmtunn, gbcTab2);
				tab2.add(tmtunn);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MACROCAL"));
				Uti1.bldConst(gbcTab2, 0, 6, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmmaca = new MyJComboBox(
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
						String.valueOf(exa.tmmaca));
				Uti1.bldConst(gbcTab2, 3, 6, 2, 1, 0, 0);
				gbTab2.setConstraints(tmmaca, gbcTab2);
				tab2.add(tmmaca);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MICROCAL"));
				Uti1.bldConst(gbcTab2, 10, 6, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmmica = new MyJComboBox(
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
						String.valueOf(exa.tmmica));
				Uti1.bldConst(gbcTab2, 13, 6, 2, 1, 0, 0);
				gbTab2.setConstraints(tmmica, gbcTab2);
				tab2.add(tmmica);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MACROC_TYPE"));
				Uti1.bldConst(gbcTab2, 0, 7, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmtmac = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("MACROC_TYPE_BAR"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("MACROC_TYPE_NOD") },
						String.valueOf(exa.tmtmac));
				Uti1.bldConst(gbcTab2, 3, 7, 2, 1, 0, 0);
				gbTab2.setConstraints(tmtmac, gbcTab2);
				tab2.add(tmtmac);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MICROC_TYPE"));
				Uti1.bldConst(gbcTab2, 10, 7, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmtmic = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("MICROC_TYPE_BAR"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("MICROC_TYPE_NOD") },
						String.valueOf(exa.tmtmic));
				Uti1.bldConst(gbcTab2, 13, 7, 2, 1, 0, 0);
				gbTab2.setConstraints(tmtmic, gbcTab2);
				tab2.add(tmtmic);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("COOPER"));
				Uti1.bldConst(gbcTab2, 0, 8, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmcoop = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("COOPER_NORM"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("COOPER_DIST"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("COOPER_INTER") },
						String.valueOf(exa.tmcoop));
				Uti1.bldConst(gbcTab2, 3, 8, 2, 1, 0, 0);
				gbTab2.setConstraints(tmcoop, gbcTab2);
				tab2.add(tmcoop);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("SKIN_ECOGEN"));
				Uti1.bldConst(gbcTab2, 10, 8, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmegcu = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("SKIN_ECOGEN_NORM"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("SKIN_ECOGEN_REDUC"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("SKIN_ECOGEN_INCR") },
						String.valueOf(exa.tmegcu));
				Uti1.bldConst(gbcTab2, 13, 8, 2, 1, 0, 0);
				gbTab2.setConstraints(tmegcu, gbcTab2);
				tab2.add(tmegcu);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("UB_ADIP_LAYER"));
				Uti1.bldConst(gbcTab2, 0, 9, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmsasc = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("ADIP_LAYER_NORM"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("ADIP_LAYER_REDUC") },
						String.valueOf(exa.tmsasc));
				Uti1.bldConst(gbcTab2, 3, 9, 2, 1, 0, 0);
				gbTab2.setConstraints(tmsasc, gbcTab2);
				tab2.add(tmsasc);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("RB_ADIP_LAYER"));
				Uti1.bldConst(gbcTab2, 10, 9, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmsarm = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("ADIP_LAYER_NORM"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("ADIP_LAYER_REDUC") },
						String.valueOf(exa.tmsarm));
				Uti1.bldConst(gbcTab2, 13, 9, 2, 1, 0, 0);
				gbTab2.setConstraints(tmsarm, gbcTab2);
				tab2.add(tmsarm);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MUSC_ECOGEN"));
				Uti1.bldConst(gbcTab2, 0, 10, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmegfm = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("MUSC_ECOGEN_NORM"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("MUSC_ECOGEN_REDUC") },
						String.valueOf(exa.tmegfm));
				Uti1.bldConst(gbcTab2, 3, 10, 2, 1, 0, 0);
				gbTab2.setConstraints(tmegfm, gbcTab2);
				tab2.add(tmegfm);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("MUSC_INFILT"));
				Uti1.bldConst(gbcTab2, 10, 10, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tminfa = new MyJComboBox(
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
						String.valueOf(exa.tminfa));
				Uti1.bldConst(gbcTab2, 13, 10, 2, 1, 0, 0);
				gbTab2.setConstraints(tminfa, gbcTab2);
				tab2.add(tminfa);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("DOPPLER"));
				Uti1.bldConst(gbcTab2, 0, 11, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tmdopp = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("DOPPLER_BEN"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("DOPPLER_SUSP"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("DOPPLER_MAL") },
						String.valueOf(exa.tmdopp));
				Uti1.bldConst(gbcTab2, 3, 11, 2, 1, 0, 0);
				gbTab2.setConstraints(tmdopp, gbcTab2);
				tab2.add(tmdopp);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("TUM_PRESENT"));
				Uti1.bldConst(gbcTab2, 10, 11, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				tsttum = new MyJComboBox(
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
						String.valueOf(exa.tsttum));
				Uti1.bldConst(gbcTab2, 13, 11, 2, 1, 0, 0);
				gbTab2.setConstraints(tsttum, gbcTab2);
				tab2.add(tsttum);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("ELGR_PHD"));
				Uti1.bldConst(gbcTab2, 0, 12, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				oplelg = new MyJTextField(20, exa.oplelg, new float[] {}, 0);
				oplelg.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab2, 3, 12, 7, 1, 0, 0);
				gbTab2.setConstraints(oplelg, gbcTab2);
				tab2.add(oplelg);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("ELGR_DATE"));
				Uti1.bldConst(gbcTab2, 10, 12, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				String ws = "";
				if (Uti1.date2String(exa.dtlelg).length() > 0) {
					ws = Uti1.date2String(exa.dtlelg);
				}
				dtlelg = new MyJTextField(10, ws, new float[] {}, 6);
				dtlelg.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab2, 13, 12, 2, 1, 0, 0);
				gbTab2.setConstraints(dtlelg, gbcTab2);
				tab2.add(dtlelg);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("ELGR_RES"));
				Uti1.bldConst(gbcTab2, 15, 12, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				relelg = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("ELGR_RES_1"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("ELGR_RES_2"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("ELGR_RES_3"), },
						String.valueOf(exa.relelg));
				Uti1.bldConst(gbcTab2, 18, 12, 2, 1, 0, 0);
				gbTab2.setConstraints(relelg, gbcTab2);
				tab2.add(relelg);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("FNAB_PHD"));
				Uti1.bldConst(gbcTab2, 0, 13, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				oplfna = new MyJTextField(20, exa.oplfna, new float[] {}, 0);
				oplfna.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab2, 3, 13, 7, 1, 0, 0);
				gbTab2.setConstraints(oplfna, gbcTab2);
				tab2.add(oplfna);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("FNAB_DATE"));
				Uti1.bldConst(gbcTab2, 10, 13, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				String ws = "";
				if (Uti1.date2String(exa.dtlfna).length() > 0) {
					ws = Uti1.date2String(exa.dtlfna);
				}
				dtlfna = new MyJTextField(10, ws, new float[] {}, 6);
				dtlfna.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab2, 13, 13, 2, 1, 0, 0);
				gbTab2.setConstraints(dtlfna, gbcTab2);
				tab2.add(dtlfna);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("FNAB_RES"));
				Uti1.bldConst(gbcTab2, 15, 13, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				relfna = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("FNAB_RES_1"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("FNAB_RES_2"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("FNAB_RES_3"),
								"4="
										+ Jedecma.localMessagesBundle
												.getString("FNAB_RES_4"),
								"5="
										+ Jedecma.localMessagesBundle
												.getString("FNAB_RES_5"), },
						String.valueOf(exa.relfna));
				Uti1.bldConst(gbcTab2, 18, 13, 2, 1, 0, 0);
				gbTab2.setConstraints(relfna, gbcTab2);
				tab2.add(relfna);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("CBIO_PHD"));
				Uti1.bldConst(gbcTab2, 0, 14, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				oplbio = new MyJTextField(20, exa.oplbio, new float[] {}, 0);
				oplbio.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab2, 3, 14, 7, 1, 0, 0);
				gbTab2.setConstraints(oplbio, gbcTab2);
				tab2.add(oplbio);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("CBIO_DATE"));
				Uti1.bldConst(gbcTab2, 10, 14, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				String ws = "";
				if (Uti1.date2String(exa.dtlbio).length() > 0) {
					ws = Uti1.date2String(exa.dtlbio);
				}
				dtlbio = new MyJTextField(10, ws, new float[] {}, 6);
				dtlbio.addFocusListener(new CtrTextField());
				Uti1.bldConst(gbcTab2, 13, 14, 2, 1, 0, 0);
				gbTab2.setConstraints(dtlbio, gbcTab2);
				tab2.add(dtlbio);
			}

			{
				MyJlabel lab = new MyJlabel(
						Jedecma.localMessagesBundle.getString("CBIO_RES"));
				Uti1.bldConst(gbcTab2, 15, 14, 3, 1, 0, 0);
				gbTab2.setConstraints(lab, gbcTab2);
				tab2.add(lab);
				relbio = new MyJComboBox(
						new String[] {
								"0="
										+ Jedecma.localMessagesBundle
												.getString("CB_NA"),
								"1="
										+ Jedecma.localMessagesBundle
												.getString("CBIO_RES_1"),
								"2="
										+ Jedecma.localMessagesBundle
												.getString("CBIO_RES_2"),
								"3="
										+ Jedecma.localMessagesBundle
												.getString("CBIO_RES_3"),
								"4="
										+ Jedecma.localMessagesBundle
												.getString("CBIO_RES_4"),
								"5="
										+ Jedecma.localMessagesBundle
												.getString("CBIO_RES_5"), },
						String.valueOf(exa.relbio));
				Uti1.bldConst(gbcTab2, 18, 14, 2, 1, 0, 0);
				gbTab2.setConstraints(relbio, gbcTab2);
				tab2.add(relbio);
			}

			// fine tab2

			tab3 = new JPanel();
			GridBagLayout gbTab3 = new GridBagLayout();
			GridBagConstraints gbcTab3 = new GridBagConstraints();
			tab3.setLayout(gbTab3);
			tabPan.addTab(Jedecma.localMessagesBundle.getString("ECO_PG3"),
					tab3);

			// dgntxt = new DgnEditor(1024);
			// dgntxt.setText(ecoTxt.text);
			dgnpan = new DgnEditor(1024);
			dgnpan.setText(ecoTxt.text);
			dgnpan.setBorder(BorderFactory
					.createTitledBorder(Jedecma.localMessagesBundle
							.getString("DIAG_TXT")));
			// dgntxt.setLineWrap(true);
			// dgntxt.setWrapStyleWord(true);
			Uti1.bldConst(gbcTab3, 0, 0, 19, 14, 1, 1,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH);
			gbcTab3.insets = new Insets(10, 10, 10, 10);
			gbTab3.setConstraints(dgnpan, gbcTab3);
			tab3.add(dgnpan);

			tab5 = new JPanel();
			GridBagLayout gbTab5 = new GridBagLayout();
			GridBagConstraints gbcTab5 = new GridBagConstraints();
			tab5.setLayout(gbTab5);
			tabPan.addTab(Jedecma.localMessagesBundle.getString("ECO_PG5"),
					tab5);

			// controlla path ed event. disabilita Tab
			String imgpath = Jedecma.param.getProperty("imgpath");
			File pathname = new File(imgpath);
			if (nrexam < 1 || imgpath.length() == 0 || (!pathname.exists())) {
				tabPan.setEnabledAt(4, false);
			} else {
				filePan = new FileMgr(nrexam, this);
				Uti1.bldConst(gbcTab5, 0, 0, 1, 1, 1, 0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH);
				gbcTab5.insets = new Insets(2, 2, 2, 2);
				gbTab5.setConstraints(filePan, gbcTab5);
				tab5.add(filePan);
			}
		}

		mainPan.add(tabPan);
		mainPan.setLayout(new BoxLayout(mainPan, BoxLayout.Y_AXIS));
		tabPan.setSelectedIndex(0);

		// Register a change listener
		tabPan.addChangeListener(new ChangeListener() {
			// This method is called whenever the selected tab changes
			public void stateChanged(ChangeEvent evt) {
				JTabbedPane pane = (JTabbedPane) evt.getSource();
				// Get current tab
				int sel = pane.getSelectedIndex();
				System.out.println("tab selected = " + sel);
				if (sel == 4 && filePan.getTableSize() < 1) {
					filePan.loadTable();
				}
			}
		});

		butPan = new JPanel();
		mainPan.add(butPan);
		getContentPane().add(mainPan);

		okBut = new JButton(Jedecma.localMessagesBundle.getString("CB_OK"));
		okBut.setMnemonic(KeyEvent.VK_O);
		butPan.add(okBut);
		okBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int rc = saveData();
				if (filePan != null) {
					filePan.storeFiles();
				}
				if (rc == 0) {
					stop();
				}
			}
		});
		okBut.setEnabled(!busy);
		if ((!Jedecma.ak.isEnableWrite()) || readOnly) {
			okBut.setEnabled(false);
		}

		quitBut = new JButton(Jedecma.localMessagesBundle.getString("CANCEL"));
		quitBut.setMnemonic(KeyEvent.VK_A);
		butPan.add(quitBut);
		quitBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});

		anaBut = new JButton(Jedecma.localMessagesBundle.getString("PAT_DATA"));
		anaBut.setMnemonic(KeyEvent.VK_G);
		butPan.add(anaBut);
		anaBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = cognom.getText();
				String bloc = ecbprv.getText();
				String bdat = Uti1.date2Ansi(datnas.getText());
				AnaEdit anaEdit = new AnaEdit();
				anaEdit.edit(name, bdat, bloc);
			}
		});
		if ((Jedecma.ak.isEnableWrite() == false) || readOnly || busy) {
			anaBut.setEnabled(false);
		}

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// rimuove lock
				if (lockCode.length() > 0) {
					MyLock.delLock(Jedecma.dbmgr, lockCode, Jedecma.user.userid);
				}
			}
		});

		pack();
		setVisible(true);

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
		// aggiorna struttura exa
		exa.datesa = Uti1.string2Date(datesa.getText());
		exa.cognom = cognom.getText();
		exa.datnas = Uti1.string2Date(datnas.getText());
		exa.dgneco = dgneco.getText();
		exa.dgnist = dgnist.getText();
		exa.dgncit = dgncit.getText();
		exa.fampat = fampat.getText();
		exa.termed = termed.getText();
		exa.terchi = terchi.getText();
		exa.tmsede = tmsede.getText();
		exa.tmdime = tmdime.getText();
		exa.datums = Uti1.string2Date(datums.getText());
		exa.dtmamm = Uti1.string2Date(dtmamm.getText());
		exa.drmamm = drmamm.getText();
		exa.ecbprv = ecbprv.getText();
		exa.mastse = mastse.getText();
		exa.numarc = nrexam;
		exa.allatt = Integer.parseInt(allatt.getInpValue());
		exa.citeco = Integer.parseInt(citeco.getInpValue());
		exa.isteco = Integer.parseInt(isteco.getInpValue());
		exa.tmmalg = Integer.parseInt(tmmalg.getInpValue());
		exa.nmrfgl = Integer.parseInt(nmrfgl.getText());
		exa.menarc = Integer.parseInt(menarc.getText());
		exa.menopa = Integer.parseInt(menopa.getText());
		exa.mstrzn = Integer.parseInt(mstrzn.getInpValue());
		exa.egmamm = Integer.parseInt(egmamm.getInpValue());
		exa.tcapez = Integer.parseInt(tcapez.getInpValue());
		exa.tdotti = Integer.parseInt(tdotti.getInpValue());
		exa.mpfbcs = Integer.parseInt(mpfbcs.getInpValue());
		exa.tescrc = Integer.parseInt(tescrc.getInpValue());
		exa.mpmics = Integer.parseInt(mpmics.getInpValue());
		exa.ematic = Integer.parseInt(ematic.getInpValue());
		exa.mpmacs = Integer.parseInt(mpmacs.getInpValue());
		exa.esmica = Integer.parseInt(esmica.getInpValue());
		exa.vgencs = Integer.parseInt(vgencs.getInpValue());
		exa.esmaca = Integer.parseInt(esmaca.getInpValue());
		exa.tmcomp = Integer.parseInt(tmcomp.getText());
		exa.tmcont = Integer.parseInt(tmcont.getInpValue());
		exa.tmmarg = Integer.parseInt(tmmarg.getInpValue());
		exa.tmegen = Integer.parseInt(tmegen.getInpValue());
		exa.tmattn = Integer.parseInt(tmattn.getInpValue());
		exa.tmcono = Integer.parseInt(tmcono.getInpValue());
		exa.tmtunn = Integer.parseInt(tmtunn.getInpValue());
		exa.tmmaca = Integer.parseInt(tmmaca.getInpValue());
		exa.tmmica = Integer.parseInt(tmmica.getInpValue());
		exa.tmtmac = Integer.parseInt(tmtmac.getInpValue());
		exa.tmtmic = Integer.parseInt(tmtmic.getInpValue());
		exa.tmcoop = Integer.parseInt(tmcoop.getInpValue());
		exa.tmegcu = Integer.parseInt(tmegcu.getInpValue());
		exa.tmsasc = Integer.parseInt(tmsasc.getInpValue());
		exa.tmsarm = Integer.parseInt(tmsarm.getInpValue());
		exa.tmegfm = Integer.parseInt(tmegfm.getInpValue());
		exa.tminfa = Integer.parseInt(tminfa.getInpValue());
		exa.tmdopp = Integer.parseInt(tmdopp.getInpValue());
		exa.taprgh = Integer.parseInt(taprgh.getText());
		exa.taregh = Integer.parseInt(taregh.getText());
		exa.taingh = Integer.parseInt(taingh.getText());
		exa.arprol = Integer.parseInt(arprol.getInpValue());
		exa.fibnod = Integer.parseInt(fibnod.getInpValue());
		exa.esmamm = Integer.parseInt(esmamm.getInpValue());
		exa.mastod = Integer.parseInt(mastod.getInpValue());
		exa.prolid = Integer.parseInt(prolid.getInpValue());
		exa.pesopz = Float.parseFloat(pesopz.getText());
		exa.altzpz = Float.parseFloat(altzpz.getText());
		exa.spsghi = Float.parseFloat(spsghi.getText());
		exa.tmsupe = Float.parseFloat(tmsupe.getText());
		exa.tmvolu = Float.parseFloat(tmvolu.getText());
		exa.spsgh1 = Float.parseFloat(spsgh1.getText());
		exa.discut = Float.parseFloat(discut.getText());
		exa.disfas = Float.parseFloat(disfas.getText());
		exa.discap = Float.parseFloat(discap.getText());
		exa.tsttum = Integer.parseInt(tsttum.getInpValue());
		exa.opleco = opleco.getText();
		exa.dtleco = Uti1.string2Date(dtleco.getText());
		exa.releco = Integer.parseInt(releco.getInpValue());
		exa.oplmri = oplmri.getText();
		exa.dtlmri = Uti1.string2Date(dtlmri.getText());
		exa.relmri = Integer.parseInt(relmri.getInpValue());
		exa.oplelg = oplelg.getText();
		exa.dtlelg = Uti1.string2Date(dtlelg.getText());
		exa.relelg = Integer.parseInt(relelg.getInpValue());
		exa.oplfna = oplfna.getText();
		exa.dtlfna = Uti1.string2Date(dtlfna.getText());
		exa.relfna = Integer.parseInt(relfna.getInpValue());
		exa.oplbio = oplbio.getText();
		exa.dtlbio = Uti1.string2Date(dtlbio.getText());
		exa.relbio = Integer.parseInt(relbio.getInpValue());

		int r = exa.writeExam(Jedecma.dbmgr, nrexam);
		if (r > 0) {
			nrexam = r;
			ecoTxt.text = dgnpan.getText();
			ecoTxt.writeEcoTxt(Jedecma.dbmgr, nrexam);
		} else {
			rc = 1;
		}

		return (rc);
	}

} // / fine class EcoEdit
