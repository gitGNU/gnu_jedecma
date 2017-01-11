/*
 * EcoExam.java - examination data object 
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

import java.sql.*;

public class EcoExam {

	java.util.Date datesa, datnas, datums, dtmamm, datupd;
	java.util.Date dtleco, dtlmri, dtlelg, dtlfna, dtlbio;
	String cognom, dgneco, dgnist, dgncit, fampat, termed;
	String terchi, tmsede, tmdime, drmamm, ecbprv, mastse;
	String opleco, oplmri, oplelg, oplfna, oplbio;
	int numarc, citeco, isteco, tmmalg, allatt;
	int nmrfgl, menarc, menopa, mstrzn, egmamm, tcapez, tdotti, mpfbcs;
	int tescrc, mpmics, ematic, mpmacs, esmica, vgencs, esmaca, tsttum, tmcomp;
	int tmcont, tmmarg, tmegen, tmattn, tmcono, tmtunn, tmmaca, tmmica, tmtmac;
	int tmtmic, tmcoop, tmegcu, tmsasc, tmsarm, tmegfm, tminfa, tmdopp, taprgh;
	int taregh, taingh, arprol, fibnod, esmamm, mastod, prolid;
	int releco, relmri, relelg, relfna, relbio;
	float pesopz, altzpz, spsghi, tmsupe, tmvolu, spsgh1, discut, disfas,
			discap;

	public EcoExam() {
		datupd = new java.util.Date();
		datesa = new java.util.Date();
		cognom = new String("");
		datnas = null; // new java.util.Date();
		dgneco = new String("");
		dgnist = new String("");
		dgncit = new String("");
		fampat = new String("");
		termed = new String("");
		terchi = new String("");
		tmsede = new String("");
		tmdime = new String("");
		datums = null; // new java.util.Date();
		dtmamm = null; // new java.util.Date();
		dtleco = dtlmri = dtlelg = dtlfna = dtlbio = null;
		opleco = oplmri = oplelg = oplfna = oplbio = new String("");
		releco = relmri = relelg = relfna = relbio = 0;
		drmamm = new String("");
		ecbprv = new String("");
		mastse = new String("");
		allatt = 0;
		numarc = 0;
		citeco = 0;
		isteco = 0;
		tmmalg = 0;
		nmrfgl = 0;
		menarc = 0;
		menopa = 0;
		mstrzn = 0;
		egmamm = 0;
		tcapez = 0;
		tdotti = 0;
		mpfbcs = 0;
		tescrc = 0;
		mpmics = 0;
		ematic = 0;
		mpmacs = 0;
		esmica = 0;
		vgencs = 0;
		esmaca = 0;
		tsttum = 0;
		tmcomp = 0;
		tmcont = 0;
		tmmarg = 0;
		tmegen = 0;
		tmattn = 0;
		tmcono = 0;
		tmtunn = 0;
		tmmaca = 0;
		tmmica = 0;
		tmtmac = 0;
		tmtmic = 0;
		tmcoop = 0;
		tmegcu = 0;
		tmsasc = 0;
		tmsarm = 0;
		tmegfm = 0;
		tminfa = 0;
		tmdopp = 0;
		taprgh = 0;
		taregh = 0;
		taingh = 0;
		arprol = 0;
		fibnod = 0;
		esmamm = 0;
		mastod = 0;
		prolid = 0;
		pesopz = 0;
		altzpz = 0;
		spsghi = 0;
		tmsupe = 0;
		tmvolu = 0;
		spsgh1 = 0;
		discut = 0;
		disfas = 0;
		discap = 0;
	}

	public int readExam(JDBCMgr db, int nr) {
		int rc = -1;
		ResultSet rSet;
		java.util.Date d;
		String q = "SELECT * FROM EDECMA WHERE numarc = " + String.valueOf(nr);
		rSet = db.executeQuery(q);
		try {
			if (rSet.next()) {
				rc = 0;
				d = rSet.getDate("datupd");
				if (!Uti1.isDateNull(d)) {
					datupd = d;
				}
				d = rSet.getDate("datesa");
				if (!Uti1.isDateNull(d)) {
					datesa = d;
				}
				cognom = rSet.getString("cognom");
				d = rSet.getDate("datnas");
				if (!Uti1.isDateNull(d)) {
					datnas = d;
				}
				dgneco = rSet.getString("dgneco");
				dgnist = rSet.getString("dgnist");
				dgncit = rSet.getString("dgncit");
				fampat = rSet.getString("fampat");
				termed = rSet.getString("termed");
				terchi = rSet.getString("terchi");
				tmsede = rSet.getString("tmsede");
				tmdime = rSet.getString("tmdime");
				d = rSet.getDate("datums");
				if (!Uti1.isDateNull(d)) {
					datums = d;
				}
				d = rSet.getDate("dtmamm");
				if (!Uti1.isDateNull(d)) {
					dtmamm = d;
				}
				drmamm = rSet.getString("drmamm");
				ecbprv = rSet.getString("ecbprv");
				mastse = rSet.getString("mastse");
				numarc = rSet.getInt("numarc");
				allatt = rSet.getInt("allatt");
				citeco = rSet.getInt("citeco");
				isteco = rSet.getInt("isteco");
				tmmalg = rSet.getInt("tmmalg");
				nmrfgl = rSet.getInt("nmrfgl");
				menarc = rSet.getInt("menarc");
				menopa = rSet.getInt("menopa");
				mstrzn = rSet.getInt("mstrzn");
				egmamm = rSet.getInt("egmamm");
				tcapez = rSet.getInt("tcapez");
				tdotti = rSet.getInt("tdotti");
				mpfbcs = rSet.getInt("mpfbcs");
				tescrc = rSet.getInt("tescrc");
				mpmics = rSet.getInt("mpmics");
				ematic = rSet.getInt("ematic");
				mpmacs = rSet.getInt("mpmacs");
				esmica = rSet.getInt("esmica");
				vgencs = rSet.getInt("vgencs");
				esmaca = rSet.getInt("esmaca");
				tsttum = rSet.getInt("tsttum");
				tmcomp = rSet.getInt("tmcomp");
				tmcont = rSet.getInt("tmcont");
				tmmarg = rSet.getInt("tmmarg");
				tmegen = rSet.getInt("tmegen");
				tmattn = rSet.getInt("tmattn");
				tmcono = rSet.getInt("tmcono");
				tmtunn = rSet.getInt("tmtunn");
				tmmaca = rSet.getInt("tmmaca");
				tmmica = rSet.getInt("tmmica");
				tmtmac = rSet.getInt("tmtmac");
				tmtmic = rSet.getInt("tmtmic");
				tmcoop = rSet.getInt("tmcoop");
				tmegcu = rSet.getInt("tmegcu");
				tmsasc = rSet.getInt("tmsasc");
				tmsarm = rSet.getInt("tmsarm");
				tmegfm = rSet.getInt("tmegfm");
				tminfa = rSet.getInt("tminfa");
				tmdopp = rSet.getInt("tmdopp");
				taprgh = rSet.getInt("taprgh");
				taregh = rSet.getInt("taregh");
				taingh = rSet.getInt("taingh");
				arprol = rSet.getInt("arprol");
				fibnod = rSet.getInt("fibnod");
				esmamm = rSet.getInt("esmamm");
				mastod = rSet.getInt("mastod");
				prolid = rSet.getInt("prolid");
				pesopz = rSet.getFloat("pesopz");
				altzpz = rSet.getFloat("altzpz");
				spsghi = rSet.getFloat("spsghi");
				tmsupe = rSet.getFloat("tmsupe");
				tmvolu = rSet.getFloat("tmvolu");
				spsgh1 = rSet.getFloat("spsgh1");
				discut = rSet.getFloat("discut");
				disfas = rSet.getFloat("disfas");
				discap = rSet.getFloat("discap");
				d = rSet.getDate("dtleco");
				if (!Uti1.isDateNull(d)) {
					dtleco = d;
				}
				d = rSet.getDate("dtlmri");
				if (!Uti1.isDateNull(d)) {
					dtlmri = d;
				}
				d = rSet.getDate("dtlelg");
				if (!Uti1.isDateNull(d)) {
					dtlelg = d;
				}
				d = rSet.getDate("dtlfna");
				if (!Uti1.isDateNull(d)) {
					dtlfna = d;
				}
				d = rSet.getDate("dtlbio");
				if (!Uti1.isDateNull(d)) {
					dtlbio = d;
				}
				opleco = rSet.getString("opleco");
				oplmri = rSet.getString("oplmri");
				oplelg = rSet.getString("oplelg");
				oplfna = rSet.getString("oplfna");
				oplbio = rSet.getString("oplbio");
				releco = rSet.getInt("releco");
				relmri = rSet.getInt("relmri");
				relelg = rSet.getInt("relelg");
				relfna = rSet.getInt("relfna");
				relbio = rSet.getInt("relbio");
			}
		} catch (SQLException ex) {
			System.err.println(ex);
			Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
		}
		return (rc);
	}

	public int writeExam(JDBCMgr db, int nr) {

		String lockCode = "";
		int rc = 0;
		String q = "";
		datupd = new java.util.Date();
		String tmpDate = "";

		if (nr > 0) {
			q = "UPDATE EDECMA set " + " datupd = '" + Uti1.date2Ansi(datupd)
					+ "'" + ", datesa = '" + Uti1.date2Ansi(datesa) + "'"
					+ ", cognom = '" + Uti1.escape(cognom) + "'";

			if (Uti1.date2Ansi(datnas).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(datnas) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}
			q += ", datnas = " + tmpDate

			+ ", dgneco = '" + Uti1.escape(dgneco) + "'" + ", dgnist = '"
					+ Uti1.escape(dgnist) + "'" + ", dgncit = '"
					+ Uti1.escape(dgncit) + "'" + ", fampat = '"
					+ Uti1.escape(fampat) + "'" + ", termed = '"
					+ Uti1.escape(termed) + "'" + ", terchi = '"
					+ Uti1.escape(terchi) + "'" + ", tmsede = '"
					+ Uti1.escape(tmsede) + "'" + ", tmdime = '"
					+ Uti1.escape(tmdime) + "'";

			if (Uti1.date2Ansi(datums).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(datums) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}
			q += ", datums = " + tmpDate;

			if (Uti1.date2Ansi(dtmamm).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(dtmamm) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}
			q += ", dtmamm = " + tmpDate

			+ ", drmamm = '" + Uti1.escape(drmamm) + "'" + ", ecbprv = '"
					+ Uti1.escape(ecbprv) + "'" + ", mastse = '"
					+ Uti1.escape(mastse) + "'" + ", allatt = " + allatt + ""
					+ ", citeco = " + citeco + ", isteco = " + isteco
					+ ", tmmalg = " + tmmalg + ", nmrfgl = " + nmrfgl
					+ ", menarc = " + menarc + ", menopa = " + menopa
					+ ", mstrzn = " + mstrzn + ", egmamm = " + egmamm
					+ ", tcapez = " + tcapez + ", tdotti = " + tdotti
					+ ", mpfbcs = " + mpfbcs + ", tescrc = " + tescrc
					+ ", mpmics = " + mpmics + ", ematic = " + ematic
					+ ", mpmacs = " + mpmacs + ", esmica = " + esmica
					+ ", vgencs = " + vgencs + ", esmaca = " + esmaca

					/*
					 * nota che il flag tsttum viene attivato dall'operatore
					 */

					+ ", tsttum = " + tsttum + ", tmcomp = " + tmcomp
					+ ", tmcont = " + tmcont + ", tmmarg = " + tmmarg
					+ ", tmegen = " + tmegen + ", tmattn = " + tmattn
					+ ", tmcono = " + tmcono + ", tmtunn = " + tmtunn
					+ ", tmmaca = " + tmmaca + ", tmmica = " + tmmica
					+ ", tmtmac = " + tmtmac + ", tmtmic = " + tmtmic
					+ ", tmcoop = " + tmcoop + ", tmegcu = " + tmegcu
					+ ", tmsasc = " + tmsasc + ", tmsarm = " + tmsarm
					+ ", tmegfm = " + tmegfm + ", tminfa = " + tminfa
					+ ", tmdopp = " + tmdopp + ", taprgh = " + taprgh
					+ ", taregh = " + taregh + ", taingh = " + taingh
					+ ", arprol = " + arprol + ", fibnod = " + fibnod
					+ ", esmamm = " + esmamm + ", mastod = " + mastod
					+ ", prolid = " + prolid + ", pesopz = " + pesopz
					+ ", altzpz = " + altzpz + ", spsghi = " + spsghi
					+ ", tmsupe = " + tmsupe + ", tmvolu = " + tmvolu
					+ ", spsgh1 = " + spsgh1 + ", discut = " + discut
					+ ", disfas = " + disfas + ", discap = " + discap

					+ ", opleco = '" + Uti1.escape(opleco) + "'"
					+ ", oplmri = '" + Uti1.escape(oplmri) + "'"
					+ ", oplelg = '" + Uti1.escape(oplelg) + "'"
					+ ", oplfna = '" + Uti1.escape(oplfna) + "'"
					+ ", oplbio = '" + Uti1.escape(oplbio) + "'";

			if (Uti1.date2Ansi(dtleco).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(dtleco) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}
			q += ", dtleco = " + tmpDate;

			if (Uti1.date2Ansi(dtlmri).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(dtlmri) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}
			q += ", dtlmri = " + tmpDate;

			if (Uti1.date2Ansi(dtlfna).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(dtlfna) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}
			q += ", dtlfna = " + tmpDate;

			if (Uti1.date2Ansi(dtlelg).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(dtlelg) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}
			q += ", dtlelg = " + tmpDate;

			if (Uti1.date2Ansi(dtlbio).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(dtlbio) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}
			q += ", dtlbio = " + tmpDate

			+ ", releco = " + releco + ", relmri = " + relmri + ", relelg = "
					+ relelg + ", relfna = " + relfna + ", relbio = " + relbio
					+ " WHERE numarc = " + String.valueOf(nr);

			int r = db.executeUpdate(q);
			if (r != 1) {
				Uti1.error(
						"EcoExam: "
								+ Jedecma.localMessagesBundle
										.getString("INS_UPD_FAILURE"), true);
			} else {
				rc = numarc;
			}

		} else {
			// lock del file (o del contatore)
			lockCode = "EDECMA.";
			if (MyLock.setLock(Jedecma.dbmgr, lockCode, Jedecma.user.userid,
					MyLock.EXCL) < 0) {
				Uti1.error(Jedecma.localMessagesBundle.getString("CANT_LOCK"),
						true);
			}

			int ne = getLastExa(db);
			if (ne >= 0) {
				numarc = ++ne;
			} else {
				Uti1.error("EcoExam: (getLastExa()) "
						+ Jedecma.localMessagesBundle.getString("SQL_ERROR"),
						true);
			}

			q = "INSERT INTO EDECMA (" + " numarc" + ", datupd" + ", datesa"
					+ ", cognom" + ", datnas" + ", dgneco" + ", dgnist"
					+ ", dgncit" + ", fampat" + ", termed" + ", terchi"
					+ ", tmsede" + ", tmdime" + ", datums" + ", dtmamm"
					+ ", drmamm" + ", ecbprv" + ", mastse" + ", allatt"
					+ ", citeco" + ", isteco" + ", tmmalg" + ", nmrfgl"
					+ ", menarc" + ", menopa" + ", mstrzn" + ", egmamm"
					+ ", tcapez" + ", tdotti" + ", mpfbcs" + ", tescrc"
					+ ", mpmics" + ", ematic" + ", mpmacs" + ", esmica"
					+ ", vgencs" + ", esmaca" + ", tsttum" + ", tmcomp"
					+ ", tmcont" + ", tmmarg" + ", tmegen" + ", tmattn"
					+ ", tmcono" + ", tmtunn" + ", tmmaca" + ", tmmica"
					+ ", tmtmac" + ", tmtmic" + ", tmcoop" + ", tmegcu"
					+ ", tmsasc" + ", tmsarm" + ", tmegfm" + ", tminfa"
					+ ", tmdopp" + ", taprgh" + ", taregh" + ", taingh"
					+ ", arprol" + ", fibnod" + ", esmamm" + ", mastod"
					+ ", prolid" + ", pesopz" + ", altzpz" + ", spsghi"
					+ ", tmsupe" + ", tmvolu" + ", spsgh1" + ", discut"
					+ ", disfas" + ", discap" + ", opleco" + ", oplmri"
					+ ", oplelg" + ", oplfna" + ", oplbio" + ", dtleco"
					+ ", dtlmri" + ", dtlfna" + ", dtlelg" + ", dtlbio"
					+ ", releco" + ", relmri" + ", relelg" + ", relfna"
					+ ", relbio" + ") VALUES (" + numarc + ", '"
					+ Uti1.date2Ansi(datupd) + "'" + ", '"
					+ Uti1.date2Ansi(datesa) + "'" + ", '"
					+ Uti1.escape(cognom) + "'";

			if (Uti1.date2Ansi(datnas).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(datnas) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}

			q += ", " + tmpDate + "" + ", '" + Uti1.escape(dgneco) + "'"
					+ ", '" + Uti1.escape(dgnist) + "'" + ", '"
					+ Uti1.escape(dgncit) + "'" + ", '" + Uti1.escape(fampat)
					+ "'" + ", '" + Uti1.escape(termed) + "'" + ", '"
					+ Uti1.escape(terchi) + "'" + ", '" + Uti1.escape(tmsede)
					+ "'" + ", '" + Uti1.escape(tmdime) + "'";

			if (Uti1.date2Ansi(datums).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(datums) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}

			q += ", " + tmpDate;

			if (Uti1.date2Ansi(dtmamm).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(dtmamm) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}

			q += ", " + tmpDate + "" + ", '" + Uti1.escape(drmamm) + "'"
					+ ", '" + Uti1.escape(ecbprv) + "'" + ", '"
					+ Uti1.escape(mastse) + "'" + ", " + allatt + ", " + citeco
					+ ", " + isteco + ", " + tmmalg + ", " + nmrfgl + ", "
					+ menarc + ", " + menopa + ", " + mstrzn + ", " + egmamm
					+ ", " + tcapez + ", " + tdotti + ", " + mpfbcs + ", "
					+ tescrc + ", " + mpmics + ", " + ematic + ", " + mpmacs
					+ ", " + esmica + ", " + vgencs + ", " + esmaca + ", "
					+ tsttum + ", " + tmcomp + ", " + tmcont + ", " + tmmarg
					+ ", " + tmegen + ", " + tmattn + ", " + tmcono + ", "
					+ tmtunn + ", " + tmmaca + ", " + tmmica + ", " + tmtmac
					+ ", " + tmtmic + ", " + tmcoop + ", " + tmegcu + ", "
					+ tmsasc + ", " + tmsarm + ", " + tmegfm + ", " + tminfa
					+ ", " + tmdopp + ", " + taprgh + ", " + taregh + ", "
					+ taingh + ", " + arprol + ", " + fibnod + ", " + esmamm
					+ ", " + mastod + ", " + prolid + ", " + pesopz + ", "
					+ altzpz + ", " + spsghi + ", " + tmsupe + ", " + tmvolu
					+ ", " + spsgh1 + ", " + discut + ", " + disfas + ", "
					+ discap + ", '" + Uti1.escape(opleco) + "'" + ", '"
					+ Uti1.escape(oplmri) + "'" + ", '" + Uti1.escape(oplelg)
					+ "'" + ", '" + Uti1.escape(oplfna) + "'" + ", '"
					+ Uti1.escape(oplbio) + "'";

			if (Uti1.date2Ansi(dtleco).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(dtleco) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}
			q += ", " + tmpDate;

			if (Uti1.date2Ansi(dtlmri).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(dtlmri) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}
			q += ", " + tmpDate;

			if (Uti1.date2Ansi(dtlfna).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(dtlfna) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}
			q += ", " + tmpDate;

			if (Uti1.date2Ansi(dtlelg).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(dtlelg) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}
			q += ", " + tmpDate;

			if (Uti1.date2Ansi(dtlbio).length() > 0) {
				tmpDate = "'" + Uti1.date2Ansi(dtlbio) + "'";
			} else {
				tmpDate = Uti1.dateNull();
			}
			q += ", " + tmpDate + "" + ", " + releco + ", " + relmri + ", "
					+ relelg + ", " + relfna + ", " + relbio + ")";

			int r = db.executeUpdate(q);
			if (r != 1) {
				Uti1.error(
						"EcoExam: "
								+ Jedecma.localMessagesBundle
										.getString("INS_UPD_FAILURE"), true);
			} else {
				rc = numarc;
			}

			// rimuove lock file (o contatore)
			if (lockCode.length() > 0) {
				MyLock.delLock(Jedecma.dbmgr, lockCode, Jedecma.user.userid);
			}

		}
		return (rc);
	}

	public int getLastExa(JDBCMgr db) {
		// riporta l'uid dell'ultimo esame
		int rc = 0;
		ResultSet rSet;
		String q = "SELECT max(numarc) from EDECMA";
		rSet = db.executeQuery(q);
		try {
			if (rSet.next()) {
				rc = rSet.getInt(1);
			}
		} catch (SQLException ex) {
			System.err.println(ex);
			Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
		}
		System.out.println("getLastExa: " + rc);
		return (rc);
	}

} // end EcoExam class
