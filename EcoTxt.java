/*  
 * EcoTxt.java - examination text object
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

import java.sql.*;

public class EcoTxt {
  
  String text;
  int examnr, lnread;
  java.util.Date datupd;
  
  public EcoTxt ()  {
    text = "";
    examnr = 0;
    lnread = 0;
    datupd = new java.util.Date();
  }

  public int readEcoTxt (JDBCMgr db, int nr) {
   int rc = -1;
   ResultSet rSet;
   String q = "SELECT * FROM DIAGTXT WHERE examnr = " + String.valueOf(nr);
   rSet = db.executeQuery(q);
   try {
     if (rSet.next()) {
     rc=0;
     try {
       datupd = rSet.getDate("datupd");
     } catch (java.sql.SQLException e) {
       datupd = Uti1.string2Date ("0000-00-00");
     }
     text += rSet.getString("text");
     lnread++;
     }
   } catch (SQLException ex) {
     System.err.println(ex);
     Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
   }
   return (rc);
  }
  
  public void writeEcoTxt (JDBCMgr db, int nr) {
    String s1 = Uti1.escape(text);
    String q = "";
    datupd = new java.util.Date();
     
    if (lnread > 0) {
      q = "UPDATE DIAGTXT " 
      + "set datupd = '" + Uti1.date2Ansi(Uti1.date2String(datupd)) + "'"
      + ", text = '" + s1 + "'"
	  +" WHERE examnr = " + String.valueOf(nr);
    } else {      
      q = "INSERT INTO DIAGTXT (examnr, text, datupd) VALUES ("
    		  + String.valueOf(nr)
    		  + ", '" + s1 + "'"
    		  + ", '" + Uti1.date2Ansi(Uti1.date2String(datupd)) + "'"
              + ")";
    }
    int rc = db.executeUpdate(q);
    if ( rc != 1 ) {
      Uti1.error("EcoTxt: "
    		  + Jedecma.localMessagesBundle.getString("INS_UPD_FAILURE")
    		  , true);
    }
  }

} // end EcoTxt class
