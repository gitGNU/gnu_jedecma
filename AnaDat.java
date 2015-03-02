/*  
 * AnaDat.java - patient data object
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


/*
 * ToDo:
 * se key duplicata, dopo errore, in anaMgr viene messa egualmente in tabella
 * 
 */

package jedecma;

import java.sql.*;

public class AnaDat {

	java.util.Date anbrtd, datupd;
	String anname, anaddr, ancapc, anloca, anrprv, anbprv, anteln, antel2,
			anprof, annote;
	String anfami, email;
	int ancode, anmena, anchld, an1cha, ansuck, anmens, anmenp;
	float anheig, anweig;

	public AnaDat() {
		datupd = new java.util.Date();
		anbrtd = null; // new java.util.Date();
		anname = new String("");
		anaddr = new String("");
		ancapc = new String("");
		anloca = new String("");
		anrprv = new String("");
		anbprv = new String("");
		anteln = new String("");
		antel2 = new String("");
		email = new String("");
		anprof = new String("");
		annote = new String("");
		anfami = new String("");
		ancode = 0;
		anmena = 0;
		anchld = 0;
		an1cha = 0;
		ansuck = 0;
		anmens = 0;
		anmenp = 0;
		anheig = 0;
		anweig = 0;
	}

	public int getAnaUid(JDBCMgr db, String name, String bdat, String bloc) {
		// dati nome, data nascita, prov, riporta l'uid della prima anagrafica
		int rc = 0;
		ResultSet rSet;
		String q = "SELECT ancode FROM EDECAN WHERE anname = '"
				+ Uti1.escape(name) + "'";
		if (bdat.length() > 0) {
			q += " AND anbrtd = '" + bdat + "'";
		}

		q += " AND anbprv = '" + Uti1.escape(bloc) + "'";

		q += " ORDER by ancode";

		rSet = db.executeQuery(q);
		try {
			if (rSet.next()) {
				rc = rSet.getInt("ancode");
			}
		} catch (SQLException ex) {
			System.err.println(ex);
			Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
		}
		return (rc);
	}

	public int readAna(JDBCMgr db, int nr) {
		int rc = -1;
		ResultSet rSet;
		java.util.Date d;
		String q = "SELECT * FROM EDECAN WHERE ancode = " + String.valueOf(nr);
		rSet = db.executeQuery(q);
		try {
			if (rSet.next()) {
				rc = 0;
				d = rSet.getDate("datupd");
				if (!Uti1.isDateNull(d)) {
					datupd = d;
				}
				d = rSet.getDate("anbrtd");
				if (!Uti1.isDateNull(d)) {
					anbrtd = d;
				}
				anname = rSet.getString("anname");
				anaddr = rSet.getString("anaddr");
				ancapc = rSet.getString("ancapc");
				anloca = rSet.getString("anloca");
				anrprv = rSet.getString("anrprv");
				anbprv = rSet.getString("anbprv");
				anteln = rSet.getString("anteln");
				antel2 = rSet.getString("antel2");
				email = rSet.getString("email");
				anprof = rSet.getString("anprof");
				annote = rSet.getString("annote");
				anfami = rSet.getString("anfami");
				ancode = rSet.getInt("ancode");
				anmena = rSet.getInt("anmena");
				anchld = rSet.getInt("anchld");
				an1cha = rSet.getInt("an1cha");
				ansuck = rSet.getInt("ansuck");
				anmens = rSet.getInt("anmens");
				anmenp = rSet.getInt("anmenp");
				anheig = rSet.getFloat("anheig");
				anweig = rSet.getFloat("anweig");
			}
		} catch (SQLException ex) {
			System.err.println(ex);
			Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
		}
		return (rc);
	}

	public void writeAna(JDBCMgr db, int nr) {
		String lockCode = "";
		String q = "";
		datupd = new java.util.Date();

		if (nr > 0) {
			q = "UPDATE EDECAN set" + " datupd = '" + Uti1.date2Ansi(datupd)
					+ "'" + ", anbrtd = ";
			if (Uti1.date2Ansi(anbrtd).length() > 0) {
				q += "'" + Uti1.date2Ansi(anbrtd) + "'";
			} else {
				q += Uti1.dateNull();
			}
			q += ", anname = '" + Uti1.escape(anname) + "'" + ", anaddr = '"
					+ Uti1.escape(anaddr) + "'" + ", ancapc = '"
					+ Uti1.escape(ancapc) + "'" + ", anloca = '"
					+ Uti1.escape(anloca) + "'" + ", anrprv = '"
					+ Uti1.escape(anrprv) + "'" + ", anbprv = '"
					+ Uti1.escape(anbprv) + "'" + ", anteln = '"
					+ Uti1.escape(anteln) + "'" + ", antel2 = '"
					+ Uti1.escape(antel2) + "'" + ", email = '"
					+ Uti1.escape(email) + "'" + ", anprof = '"
					+ Uti1.escape(anprof) + "'" + ", annote = '"
					+ Uti1.escape(annote) + "'" + ", anfami = '"
					+ Uti1.escape(anfami) + "'" + ", anmena = " + anmena
					+ ", anchld = " + anchld + ", an1cha = " + an1cha
					+ ", ansuck = " + ansuck + ", anmens = " + anmens
					+ ", anmenp = " + anmenp + ", anheig = " + anheig
					+ ", anweig = " + anweig + " WHERE ancode = "
					+ String.valueOf(nr);

			int rc = db.executeUpdate(q);
			if (rc == -3) {
				Uti1.error(
						Jedecma.localMessagesBundle.getString("PAT_DATA")
								+ " "
								+ Jedecma.localMessagesBundle
										.getString("DUPLICATE_KEY"), false);
			} else if (rc != 1) {
				Uti1.error(
						"AnaDat: "
								+ Jedecma.localMessagesBundle
										.getString("INS_UPD_FAILURE"), true);
			}

		} else {
			lockCode = "EDECAN.";
			if (MyLock.setLock(Jedecma.dbmgr, lockCode, Jedecma.user.userid,
					MyLock.EXCL) < 0) {
				Uti1.error(Jedecma.localMessagesBundle.getString("CANT_LOCK"),
						true);
			}

			q = "INSERT INTO EDECAN (" + "datupd" + ", anbrtd" + ", anname"
					+ ", anaddr" + ", ancapc" + ", anloca" + ", anrprv"
					+ ", anbprv" + ", anteln" + ", antel2" + ", email"
					+ ", anprof" + ", annote" + ", anfami" + ", anmena"
					+ ", anchld" + ", an1cha" + ", ansuck" + ", anmens"
					+ ", anmenp" + ", anheig" + ", anweig" + ") VALUES (" + "'"
					+ Uti1.date2Ansi(datupd) + "', ";
			if (Uti1.date2Ansi(anbrtd).length() > 0) {
				q += "'" + Uti1.date2Ansi(anbrtd) + "' ";
			} else {
				q += Uti1.dateNull();
			}
			q += ", '" + Uti1.escape(anname) + "'" + ", '"
					+ Uti1.escape(anaddr) + "'" + ", '" + Uti1.escape(ancapc)
					+ "'" + ", '" + Uti1.escape(anloca) + "'" + ", '"
					+ Uti1.escape(anrprv) + "'" + ", '" + Uti1.escape(anbprv)
					+ "'" + ", '" + Uti1.escape(anteln) + "'" + ", '"
					+ Uti1.escape(antel2) + "'" + ", '" + Uti1.escape(email)
					+ "'" + ", '" + Uti1.escape(anprof) + "'" + ", '"
					+ Uti1.escape(annote) + "'" + ", '" + Uti1.escape(anfami)
					+ "'" + ", " + anmena + ", " + anchld + ", " + an1cha
					+ ", " + ansuck + ", " + anmens + ", " + anmenp + ", "
					+ anheig + ", " + anweig + ")";

			int rc = db.executeUpdate(q);
			if (rc == -3) {
				Uti1.error(
						Jedecma.localMessagesBundle.getString("PAT_DATA")
								+ " "
								+ Jedecma.localMessagesBundle
										.getString("DUPLICATE_KEY"), false);
			} else if (rc != 1) {
				Uti1.error(
						"AnaDat: "
								+ Jedecma.localMessagesBundle
										.getString("INS_UPD_FAILURE"), true);
			}

			ancode = getAnaUid(db, anname, Uti1.date2Ansi(anbrtd), anbprv);

			// rimuove lock file (o contatore)
			if (lockCode.length() > 0) {
				MyLock.delLock(Jedecma.dbmgr, lockCode, Jedecma.user.userid);
			}

		}

	}

} // end class AnaDat
