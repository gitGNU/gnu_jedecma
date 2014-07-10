/*  
 * MyLock.java - lock management for multiuser sessions
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

/*
 * Nota bene: nata per l'uso multiutente, e' comunque operativa anche in monoutenza.
 * Si tratta di una gestione rudimentale basata su registrazioni
 * su una tabella apposita del database.
 * *** Non e' mai stata testata in un contesto d'uso reale. *** 
 * In caso di interruzione della sessione, i lock restano attivi e vanno 
 * eliminati riavviando e terminando immediatamente il programma con lo
 * stesso utente.
 */

/*
 * Notice: designed for a multiuser mode, is used even in single user mode.
 * It is a rudimentary system based on records on a dedicated database table.
 * *** Never tested in real use ***
 * In case of incorrect termination of the session, locks are left active: 
 * to erase them restart the program with same user and terminate it immediately.
  */

package jedecma;

import java.sql.*;

public class MyLock {
	final static boolean EXCL = true;
	final static boolean SHARED = false;
	final static int TRYMAX = 10; // n.massimo tentativi di accesso prima di
									// restituire l'errore

	MyLock() {

	}

	public static int chkLock(JDBCMgr db, String lc) { // 1^ modalita'
		// verifica se esiste un lock con codice lc
		// ritorna lo userid del primo lock trovato; -1 se non trovati
		int rc = -1;
		ResultSet rSet;
		if (lc.indexOf("%") > -1) {
			rSet = db
					.executeQuery("SELECT lockusr FROM TBLLCKS WHERE lockcod LIKE '"
							+ lc + "'");
		} else {
			rSet = db
					.executeQuery("SELECT lockusr FROM TBLLCKS WHERE lockcod = '"
							+ lc + "'");
		}

		try {
			if (rSet.next()) {
				rc = rSet.getInt(1);
			}
		} catch (SQLException ex) {
			System.err.println(ex);
			Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
		}
		System.out.println("chkLock: rc=" + rc);
		return (rc);
	}

	public static int chkLock(JDBCMgr db, String lc, int ui) { // 2^ modalita'
		// verifica se esiste un lock con codice lc e userid ui
		// ritorna lo userid del primo lock trovato; -1 se non trovati
		int rc = -1;
		ResultSet rSet;
		if (lc.indexOf("%") > -1) {
			rSet = db
					.executeQuery("SELECT lockusr FROM TBLLCKS WHERE lockcod LIKE '"
							+ lc + "' AND lockusr = " + ui);
		} else {
			rSet = db
					.executeQuery("SELECT lockusr FROM TBLLCKS WHERE lockcod = '"
							+ lc + "' AND lockusr = " + ui);
		}
		try {
			if (rSet.next()) {
				rc = rSet.getInt(1);
			}
		} catch (SQLException ex) {
			System.err.println(ex);
			Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
		}
		System.out.println("chkLock: rc=" + rc);
		return (rc);
	}

	public static int delLock(JDBCMgr db, String lc, int ui) {
		// cancella il lock con codice lc e userid ui
		// ritorna 0
		int rc = 0;
		String q;
		if (lc.indexOf("%") > -1) {
			q = "DELETE FROM TBLLCKS WHERE lockcod like '" + lc
					+ "' AND lockusr = " + ui;
		} else {
			q = "DELETE FROM TBLLCKS WHERE lockcod = '" + lc
					+ "' AND lockusr = " + ui;
		}
		int rowCount = db.executeUpdate(q);
		if (rowCount > 0) {
			rc = 0;
		} else {
			rc = -1;
		}
		return (rc);
	}

	public static int setLock(JDBCMgr db, String lc, int ui, boolean ex) {
		// aggiunge un lock con codice lc per userid ui
		// ritorna 0 se risulta un lock per ui; -1 in caso contrario
		int iex = ui;
		if (ex) {
			iex = Jedecma.user.IDEXCL;
		}
		int rc = -1;
		int lap = 0;
		// Thread myThread = Thread.currentThread();
		while (lap <= TRYMAX) {
			System.out.println("setLock: lap #" + lap);
			String q = "INSERT INTO TBLLCKS VALUES (DEFAULT, '" + lc + "', "
					+ ui + ", " + iex + ")";
			// Use the keyword DEFAULT to set a column explicitly to its default
			// value (->auto_increment!)
			if (db.executeUpdate(q) == 1) {
				System.out.println("setLock: " + lc + " " + ui + " " + iex
						+ " rc=" + rc);
				rc = 0;
				break;
			}
			try { // attende un tempo random
				Thread.sleep((long) (Math.random() * 100));
			} catch (InterruptedException e) {
			}
			lap++;
		}
		return (rc);
	}

} // end MyLock Class

