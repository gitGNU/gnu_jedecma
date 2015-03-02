/*  
 * EcoImg.java - examination image object
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

package jedecma;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EcoImg {

	private String note, basename;
	private int id, examnr;
	private java.util.Date datupd;

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getBasename() {
		return basename;
	}

	public void setBasename(String basename) {
		this.basename = basename;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getExamnr() {
		return examnr;
	}

	public void setExamnr(int examnr) {
		this.examnr = examnr;
	}

	public java.util.Date getDatupd() {
		return datupd;
	}

	public void setDatupd(java.util.Date datupd) {
		this.datupd = datupd;
	}

	public String getFolder() {
		String imgpath = Jedecma.param.getProperty("imgpath");
		String s = Uti1.rightSet(String.valueOf(examnr), 8, '0');
		return (imgpath + System.getProperty("file.separator") + s);
	}

	public EcoImg() {
		note = "";
		basename = "";
		id = 0;
		examnr = 0;
		datupd = new java.util.Date();
		;
	}

	public int readEcoImg(int nr, String fn) {
		JDBCMgr db = Jedecma.dbmgr;
		int rc = -1;
		ResultSet rSet;
		String q = "SELECT * FROM ECOIMG WHERE examnr = " + String.valueOf(nr)
				+ " AND basename = '" + Uti1.escape(fn) + "'";
		rSet = db.executeQuery(q);
		try {
			if (rSet.next()) {
				rc = 0;
				try {
					datupd = rSet.getDate("datupd");
				} catch (java.sql.SQLException e) {
					datupd = Uti1.string2Date("0000-00-00");
				}
				note = rSet.getString("note");
				basename = rSet.getString("basename");
				examnr = rSet.getInt("examnr");
				id = rSet.getInt("id");
			}
		} catch (SQLException ex) {
			System.err.println(ex);
			Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
		}

		return rc;
	}

	public int readEcoImg(int nid) {
		JDBCMgr db = Jedecma.dbmgr;
		int rc = -1;
		ResultSet rSet;
		String q = "SELECT * FROM ECOIMG WHERE id = " + nid + "";
		rSet = db.executeQuery(q);
		try {
			if (rSet.next()) {
				rc = 0;
				try {
					datupd = rSet.getDate("datupd");
				} catch (java.sql.SQLException e) {
					datupd = Uti1.string2Date("0000-00-00");
				}
				note = rSet.getString("note");
				basename = rSet.getString("basename");
				examnr = rSet.getInt("examnr");
				id = rSet.getInt("id");
			}
		} catch (SQLException ex) {
			System.err.println(ex);
			Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
		}
		return (rc);
	}

	public void writeEcoImg(int nid) {
		JDBCMgr db = Jedecma.dbmgr;
		String q = "";
		datupd = new java.util.Date();

		if (nid > 0) {
			q = "UPDATE ECOIMG " + "set datupd = '"
					+ Uti1.date2Ansi(Uti1.date2String(datupd)) + "'"
					+ ", note = '" + Uti1.escape(note) + "'" + ", basename = '"
					+ Uti1.escape(basename) + "'" + " WHERE id = " + nid;
		} else {
			q = "INSERT INTO ECOIMG (examnr, note, basename, datupd) VALUES ("
					+ String.valueOf(examnr) + ", '" + Uti1.escape(note) + "'"
					+ ", '" + Uti1.escape(basename) + "'" + ", '"
					+ Uti1.date2Ansi(Uti1.date2String(datupd)) + "'" + ")";
		}
		int rc = db.executeUpdate(q);
		if (rc != 1) {
			Uti1.error(
					"EcoImg: "
							+ Jedecma.localMessagesBundle
									.getString("INS_UPD_FAILURE"), true);
		}
	}

	public void deleteEcoImg() {
		if (examnr > 0) { // esiste
			Jedecma.dbmgr.executeUpdate("DELETE FROM ECOIMG WHERE examnr="
					+ examnr + " AND id = " + id);
		}
	}

} // end EcoImg class
