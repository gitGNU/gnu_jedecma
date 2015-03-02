/*  
 * User.java - user object
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

import java.sql.*;

public class User {
  final int IDEXCL = 0;
  final static String DFLUSER = "jedecma"; // utente default
  final static String DFLPASS = "jedecma"; // password default
  final static String DFLPR = "1"; // privilegio default
  String username, userpr; 
  int userid;

  public User (String us, String pw)  {
    userid = -1; // -1 = utente non autenticato!
    username = "";
    userpr = ""; // "1" = utente privilegiato (amministratore)
    
    JDBCMgr db = Jedecma.dbmgr;
    ResultSet rSet;
    String q = "SELECT userno, userpr, username FROM TBLUSER WHERE username = '" + us + "' AND userpw = '" + Base64.encode(pw) + "'";    
    rSet = db.executeQuery(q);
    
    try {
      if (rSet.next()) {
        userid = rSet.getInt("userno");
	    userpr = rSet.getString("userpr");
	    username = rSet.getString("username");
      }
    } catch (SQLException ex) {
      System.err.println(ex);
      Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
    }    
    
  }

} /// end User class
