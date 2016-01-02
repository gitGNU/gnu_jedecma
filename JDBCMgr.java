/*  
 * JDBCMgr.java - connection and query management
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

import java.sql.*;

public class JDBCMgr {
  static Connection connection;
  static Statement statement;
  static ResultSet resultSet;
  static boolean isOpen;
  static int dbType;
  static final int MYSQL = 1;
  static final int DERBY = 0;
  
  public JDBCMgr() {}
  
  public void  open () {
    if ( isOpen ) {
       Uti1.error("open: connection already active", true);
    } else {
    String url = "";
    String driverName = "";
    String user = "";
    String passwd = "";
    dbType = DERBY;
   
    {
      if (Integer.parseInt(Jedecma.param.getProperty("dbtype")) == MYSQL) {
    	 dbType = MYSQL;
      }
      url = Jedecma.param.getProperty("dbname");
      driverName = Jedecma.param.getProperty("jdbcdriver");
      user =  Jedecma.param.getProperty("dbuser");
      passwd = Base64.decode(Jedecma.param.getProperty("dbpass"));
    }

    try {
      Class.forName(driverName);		       
      String connectionUrl = "";
      switch (dbType) {
        case MYSQL: {
    	  connectionUrl = url + "?user=" + user + "&password=" + passwd
                  + "&zeroDateTimeBehavior=convertToNull";
        }; break; 
        case DERBY: {
    	  //connectionUrl = url + "; create=true" + "; user=" + user + "; password=" + passwd;
        	connectionUrl = url + "; user=" + user + "; password=" + passwd;
        }; break;
        default: {
        	Uti1.error("dbType not supported:" + dbType, true);
            System.err.println("dbType not supported:" + dbType);
        }
      }
      connection = DriverManager.getConnection(connectionUrl);
      //System.out.println("AutoCommitt= " + connection.getAutoCommit());
      statement = connection.createStatement();
      System.out.println("Opening db connection type " + dbType + ": " + connection + " to: " + url);
    }
    catch (ClassNotFoundException ex) {
      Uti1.error("driver class not found:" + driverName, true);
      System.err.println(ex);
    }
    catch (SQLException ex) {
      Uti1.error("unable to connect to database: " + url, true);
      System.err.println(ex);
    }
    
    isOpen = true;
    }
  }
  
  public ResultSet executeQuery(String q) {
	    return (executeQuery(q, statement, 0));
  } 
  
  public ResultSet executeQuery(String q, int lim) {
	    return (executeQuery(q, statement, lim));
  }
  
  public ResultSet executeQuery(String q, Statement stm, int lim) {
    if ( isOpen ) {
  
    if (connection == null || statement == null) {
      Uti1.error("No database to execute the query.", true);
      return resultSet;
    }
    try {
      if (lim > 0) {
    	  if ( dbType == DERBY ) {
    	    //stm.setMaxRows(lim); // va in errore con mysql!
    		  q += " FETCH FIRST " + lim + " ROWS ONLY";
    	  }
    	  if ( dbType == MYSQL ) {
      	    q += " limit " + lim;
      	  }
      }
      System.out.println("Executing: " + q);
      System.out.println("connection: " + connection.toString());
      
      resultSet = statement.executeQuery(q);
      return resultSet;
    }
    catch (SQLException ex) {
      Uti1.error(ex.toString(), true);
    }
    return resultSet;
    } else {
      Uti1.error("exeQuery: connessione non attiva.", true);
      return null;
    }
  }
  
 public int executeUpdate(String q) {
    // ritorna il num.righe modificate; 0=nessuna riga; <0=errore
    int rowCount = 0;
    if ( isOpen ) {
      if (connection == null || statement == null) {
        Uti1.error("exeUpdate: database not found", true);
	rowCount = -1;
        System.out.println("exeUpdate: rc= " + rowCount);
        return (rowCount);
      }
      try {
        System.out.println("Executing: " + q);
        System.out.println("connection: " + connection.toString());
        rowCount = statement.executeUpdate(q);
        System.out.println("exeUpdate: rc= " + rowCount);
        return rowCount;
      } catch (SQLIntegrityConstraintViolationException icve) {
    	  return -3;  // violazione unique index 
      } catch (SQLException ex) {
        rowCount = 0;
        System.out.println("exeUpdate: rc= " + rowCount);
	System.out.println(ex.toString());
        return (rowCount);
        //System.out.println(ex.toString());
      }  
    } else {
      Uti1.error("exeUpdate: connection not active", true);
      return -2;
    }
  } 
  
	public void close() {
		if (isOpen) {
			switch (dbType) {
			case DERBY: {
				try {
					DriverManager.getConnection("jdbc:derby:;shutdown=true");
				} catch (SQLException se) {
					if (((se.getErrorCode() == 50000) && ("XJ015".equals(se
							.getSQLState())))) {
						// we got the expected exception
						System.out.println("Derby shut down normally");
						// Note that for single database shutdown, the expected
						// SQL state is "08006", and the error code is 45000.
					} else {
						// if the error code or SQLState is different, we have
						// an unexpected exception (shutdown failed)
						System.err.println("Derby did not shut down normally");
						System.err.println(se);
					}
				}
			}
				;
				break;

			case MYSQL: {
				try {
					if (resultSet != null) {
						resultSet.close();
					}
					if (statement != null) {
						statement.close();
					}
					if (connection != null) {
						connection.close();
						System.out.println("db connection closed: "
								+ connection);
						isOpen = false;
					} else {
						Uti1.error("There is no db connection to close.", true);
					}
				} catch (SQLException ex) {
					Uti1.error(ex.toString(), true);
				}

			}
				;
				break;
			}

		} else {
			Uti1.error("close: connection not active.", true);
		}
	}

  public boolean existTable(String table) {
    if ( isOpen ) {
      if (connection == null || statement == null) {
        Uti1.error("database not found", true);
        return false;
      }
      String q = "SELECT count(*) FROM " + table;
      try {
        System.out.println("Executing: " + q);
        System.out.println("connection: " + connection.toString());
        resultSet = statement.executeQuery(q);
        return true;
      } catch (SQLException ex) { 
    	  System.err.println(ex);
    	  return (false); 
      }
    } else {
      Uti1.error("existTable: connection not active.", true);
      return false;
    }
  }
  
  public int getDbType() {
	  return dbType;
  }

} /// end class JDBCMgr
