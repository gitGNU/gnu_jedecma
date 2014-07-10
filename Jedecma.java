/* 
 * Jedecma.java - main class
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
import java.util.*;
import java.io.*;

public class Jedecma extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// ****************************************************************
	//final static String progVers = "3.0.0"; final static String progBuild = "20140310-1000"; // supporto Db-Derby; modificate query, funzioni Date per data Nul
	//final static String progVers = "3.1.0"; final static String progBuild = "20140410-1510"; // EULA
	//final static String progVers = "3.1.1"; final static String progBuild = "20140515-0930"; // var.EULA e info/about
	final static String progVers = "3.1.2"; final static String progBuild = "20140603-0000"; // var.msg.info/about
	// ****************************************************************
	static Image logo1 = null;
	static Image logo2 = null;
	static JFrame mf;
	static JPanel mainPan;
	// static JPanel msgPan;
	static MainMenu menu;
	static JDBCMgr dbmgr;
	static Properties param;
	static JLabel splash;
	static User user;
	static boolean multiuser = false;
	static String username, password, actstr;
	static String dict_file = "wrdcmpl.txt";
	static Vector<String> dict = new Vector<String>();
	static Font jTextAreaFont, jComboBoxFont, jTextFieldFont, jLabelFont,
			jTableFont = null;
	static String language = "";
	static String country = "";
	static Locale currentLocale;
	static ResourceBundle localMessagesBundle;
	static ActivationKey ak;

	Jedecma() {
	}

	public static void endProc() {
		// fine procedura.
		// salvataggio eventuali preferences, ecc.

		// rimuove tutti i lock dell'utente, se esiste
		if (user != null) {
		  MyLock.delLock(dbmgr, "%", user.userid);
		}
		dbmgr.close();

		System.out.println("+-----+");
		System.out.println("| Bye |");
		System.out.println("+-----+");
		System.exit(0);
	}

	private static void createAndShowGUI() {
		mainPan = new JPanel();
		mainPan.setPreferredSize(new Dimension(600, 460));
		mainPan.setLayout(new BorderLayout());
		/*
		 * msgPan = new JPanel();
		 * msgPan.setBorder(BorderFactory.createLoweredBevelBorder());
		 * msgPan.setPreferredSize(new Dimension(600,25));
		 * mf.getContentPane().add(msgPan, BorderLayout.SOUTH);
		 */
		mf.getContentPane().add(mainPan, BorderLayout.CENTER);
		mainPan.add(splash, BorderLayout.CENTER);

		
		try { // assegna look & fill standard
			UIManager
					//.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		

		// disabilita la possibilita' di chiudere il frame con X
		mf.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		if (multiuser)
			mf.setTitle("Jedecma " + Jedecma.progVers + " "
					+ localMessagesBundle.getString("USER") + ": " + username);
		else
			mf.setTitle("Jedecma " + Jedecma.progVers);

		// mf.setResizable(false); // se voglio che non sia ridimesionabile ...

		menu = new MainMenu();
		mf.setJMenuBar(menu.buildMenu()); // costruisce il Menu

		// attiva l'interfaccia grafica
		mf.pack();
		mf.setVisible(true);

	}

	public static void main(String[] args) { // inizio del main
		username = "";
		password = "";
		
		System.out.println("Jedecma rel." + progVers + " " +progBuild);
		System.out.println("java.version: " + System.getProperty("java.version"));
		System.out.println("java.vm.name: " + System.getProperty("java.vm.name"));
		System.out.println("java.runtime.name: " + System.getProperty("java.runtime.name"));
		System.out.println("os.name: " + System.getProperty("os.name") + " os.arch: " 
		+ System.getProperty("os.arch"));
		System.out.println("user.name: " + System.getProperty("user.name"));
		
		mf = new Jedecma(); // crea top-level container
		mf.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				String s = "<html><body><center>Jedecma " + Jedecma.progVers;
				s += "<br>" + localMessagesBundle.getString("QUIT_PROGRAM");
				s += "<br>"
						+ localMessagesBundle.getString("ALL_WINDOWS_CLOSED");
				if (Uti1.msgYN(s, localMessagesBundle.getString("END_JEDECMA"),
						JOptionPane.INFORMATION_MESSAGE, null) == 1) {
					Jedecma.endProc();
				}
			}
		});

		{ // processa argomenti di chiamata
			String arg, kw;
			int idx = -1;
			for (int i = 0; i < args.length; i++) {
				arg = args[i];
				kw = "multi";
				idx = arg.indexOf("--" + kw);
				if (idx >= 0) {
					multiuser = true;
					System.out.println("multiuser=" + multiuser);
				}
				kw = "user";
				idx = arg.indexOf("--" + kw + "=");
				if (idx >= 0) {
					username = arg.substring(idx + kw.length() + 3, arg
							.length());
					System.out.println("user=" + username);
				}
				kw = "password";
				idx = arg.indexOf("--" + kw + "=");
				if (idx >= 0) {
					password = arg.substring(idx + kw.length() + 3, arg
							.length());
				}
			}
		}

		// legge da file i parametri e inizializza param
		ak = new ActivationKey();
		param = Uti1.readProp();				
		ak.readKey();

		if (! ak.isEenableMulti() && multiuser == true) {
			multiuser = false;
			System.out.println("multiuser=" + multiuser);
		}
			
		{
			String s = Jedecma.param.getProperty("jlabel_font");
			if (!s.equals("")) {
				Font f = Uti1.myFont(s);
				if (f != null) {
					jLabelFont = f;
				}
			}
		}

		{
			String s = Jedecma.param.getProperty("jcombobox_font");
			if (!s.equals("")) {
				Font f = Uti1.myFont(s);
				if (f != null) {
					jComboBoxFont = f;
				}
			}
		}

		{
			String s = Jedecma.param.getProperty("jtextarea_font");
			if (!s.equals("")) {
				Font f = Uti1.myFont(s);
				if (f != null) {
					jTextAreaFont = f;
				}
			}
		}

		{
			String s = Jedecma.param.getProperty("jtextfield_font");
			if (!s.equals("")) {
				Font f = Uti1.myFont(s);
				if (f != null) {
					jTextFieldFont = f;
				}
			}
		}

		{
			String s = Jedecma.param.getProperty("jtable_font");
			if (!s.equals("")) {
				Font f = Uti1.myFont(s);
				if (f != null) {
					jTableFont = f;
				}
			}
		}

		{
			String s = Jedecma.param.getProperty("language");
			if (!s.equals("")) {
				language = s;
			}
		}

		{
			String s = Jedecma.param.getProperty("country");
			if (!s.equals("")) {
				country = s;
			}
		}

		// carica immagine splash
		String bgImg = param.getProperty("splash");
		ImageIcon img = new ImageIcon(bgImg);
		splash = new JLabel(img);

		{ // carica immagine logo1 stampa referto
			String logo_file1 = param.getProperty("logo1");
			// scarta quanto oltre la prima occorrenza di "["
			int p = logo_file1.indexOf("[");
			if (p > -1) {
				logo_file1 = logo_file1.substring(0, p).trim();
			}
			if (logo_file1.length() > 0) {
				final String wlogo = logo_file1;
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						MediaTracker mt = new MediaTracker(mf);
						logo1 = Toolkit.getDefaultToolkit().getImage(wlogo);
						mt.addImage(logo1, 0);
						try {
							mt.waitForID(0);
						} catch (InterruptedException e) {
						}
						// verifica caricamento: ERRORED=4 ABORTED=2 COMPLETE=8
						// LOADING=1
						int stat = mt.statusID(0, false);
						if (stat != MediaTracker.COMPLETE) {
							System.out.println("image: " + logo1
									+ " not loaded: status=" + stat);
							logo1 = null;
						} else {
							System.out.println("loaded image " + logo1
									+ " width="
									+ Jedecma.logo1.getWidth(mf) + " height="
									+ Jedecma.logo1.getHeight(mf));
						}
					}
				});
			}
		} // fine logo1

		{
			// carica immagine logo2 stampa referto
			String logo_file = param.getProperty("logo2");
			// scarta quanto oltre la prima occorrenza di "["
			int p = logo_file.indexOf("[");
			if (p > -1) {
				logo_file = logo_file.substring(0, p).trim();
			}
			if (logo_file.length() > 0) {
				final String wlogo = logo_file;
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						MediaTracker mt = new MediaTracker(mf);
						logo2 = Toolkit.getDefaultToolkit().getImage(wlogo);
						mt.addImage(logo2, 0);
						try {
							mt.waitForID(0);
						} catch (InterruptedException e) {
						}
						// verifica caricamento: ERRORED=4 ABORTED=2 COMPLETE=8
						// LOADING=1
						int stat = mt.statusID(0, false);
						if (stat != MediaTracker.COMPLETE) {
							System.out.println("image " + logo2
									+ " not loaded: status=" + stat);
							logo2 = null;
						} else {
							System.out.println("loaded image " + logo2
									+ " width="
									+ Jedecma.logo2.getWidth(mf) + " height="
									+ Jedecma.logo2.getHeight(mf));
						}
					}
				});
			}
		} // fine logo 2

		// carica il dizionario per il completamento automatico
		if (dict_file.length() > 0) {
			final String inputData = dict_file;
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					try {
						FileInputStream stream = new FileInputStream(inputData);
						InputStreamReader reader = new InputStreamReader(stream);
						StreamTokenizer tokens = new StreamTokenizer(reader);
						tokens.quoteChar((int) '"');
						tokens.eolIsSignificant(true);
						int next;
						while ((next = tokens.nextToken()) != StreamTokenizer.TT_EOF) {
							if (next == StreamTokenizer.TT_WORD) {
								// System.out.println(tokens.sval);
								if (tokens.sval.length() > 4) {
									Jedecma.dict.addElement(tokens.sval
											.toUpperCase());
								}
							}
						}
					} catch (Exception e) {
						System.out.println("error loading file "
								+ dict_file + ": " + e + "\n");
					}
					System.out.println("loaded " + dict.size()
							+ " items from file: " + dict_file);
				}
			});
		}

		// locale
		System.out.println("language: " + language + " country: " + country);
		currentLocale = new Locale(language, country);
		localMessagesBundle = ResourceBundle.getBundle("Messages",
				currentLocale);

		java.util.Date today = new java.util.Date();
		if ( today.after(ak.getExpDate()) ) {
			Uti1.error(
					"<html><body><center>Jedecma "
				      + Jedecma.progVers + " " + Jedecma.progBuild + "<br>"
				      + Jedecma.localMessagesBundle.getString("EXPIRED_LICENSE")
					  + "<br>"
				      +"</center></body></html>", 
					true);
		}
		
		// inizializza connessione JDBC
		dbmgr = new JDBCMgr();
		dbmgr.open();
				
		/* verifica: se DERBY
		 * non forzo monoutenza in quanto se usa db interno, va in errore,
		 * se invece usa server, lascio la possibilita'
		if (dbmgr.getDbType() == JDBCMgr.DERBY) {
			multiuser = false;
			System.out.println("Derby Db requires single-user mode");
		} */

		// EULA
		SimpleEula eula = new SimpleEula();
		System.out.println("eula key: " + eula.getKey());		
		eula.show();
		System.out.println("eula shown: " +Jedecma.param.getProperty("eulashown"));	
		
		// controlla esistenza tabella lock
		if (dbmgr.existTable("TBLLCKS")) {

			// se non --multi, assegna utente default
			if (!multiuser) {
				username = User.DFLUSER;
				password = User.DFLPASS;
			}

			if (username.length() == 0 || password.length() == 0) {
				try {
					synchronized (mf) {
						// crea un thread e prosegue
						javax.swing.SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								synchronized (mf) {
									AccessPane ap = new AccessPane();
									boolean auth = ap.auth;
									if (!auth) {
										Uti1.error(
														localMessagesBundle
																.getString("USER")
																+ ": <"
																+ username
																+ "> "
																+ localMessagesBundle
																		.getString("INVALID_USER_OR_PASSWORD"),
														true);
									} else {
										username = ap.username;
										password = ap.password;
									}
									mf.notifyAll(); // libera il main
									return;
								}
							}
						});

						mf.wait(); // ferma il main in attesa dell'input
					}
				} catch (InterruptedException e) {
				}
			}

			user = new User(username, password);
			System.out.println("user=" + user + " id=" + user.userid);
			if (user.userid < 0) {
				Uti1.error(localMessagesBundle.getString("USER")
						+ ": <"
						+ username
						+ "> "
						+ localMessagesBundle
								.getString("INVALID_USER_OR_PASSWORD"), true);
			}

			// imposta il lock di esecuzione
			while (MyLock.setLock(dbmgr, "EXE", user.userid, MyLock.SHARED) < 0) {
				String s = "<html><body><center>Jedecma " + Jedecma.progVers;
				s += "<br>"
						+ localMessagesBundle.getString("CANNOT_SET_EXELOCK");
				s += "</body></html>";
				if (Uti1.msgYN(s, localMessagesBundle
						.getString("FORCING_EXELOCK"),
						JOptionPane.ERROR_MESSAGE, null) == 1) {
					break;
				} else {
					dbmgr.close();
					Uti1.error(localMessagesBundle
							.getString("INCORRECT_PROGRAM_TERMINATION"), true);
				}
			}
		} else {
			String s = "<html><body><center>Jedecma " + Jedecma.progVers;
			s += "<br>"
					+ localMessagesBundle.getString("INCOMPLETE_DATABASE_LONG");
			s += "</body></html>";
			if (Uti1.msgYN(s, localMessagesBundle
					.getString("INCOMPLETE_DATABASE"),
					JOptionPane.ERROR_MESSAGE, null) != 1) {
				Uti1.error(
						localMessagesBundle.getString("INCOMPLETE_DATABASE"),
						true);
			}
		}

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

	} // fine del main

} // fine Class Jedecma

