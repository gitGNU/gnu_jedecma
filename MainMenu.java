/*  
 * MainMenu.java - the menu
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

import java.awt.event.*;
import javax.swing.*;

public class MainMenu {
	JMenuBar menuBar;

	public JMenuBar buildMenu() {
		menuBar = new JMenuBar();

		JMenu menu1 = new JMenu(Jedecma.localMessagesBundle.getString("FILE"));
		menu1.setMnemonic('F');
		menuBar.add(menu1);

		JMenuItem item11 = new JMenuItem(
				Jedecma.localMessagesBundle.getString("ECO_EXAMINATIONS"));
		item11.addActionListener(new MenuAct(11));
		item11.setMnemonic('E');
		menu1.add(item11);
		menu1.addSeparator();

		JMenuItem item12 = new JMenuItem(
				Jedecma.localMessagesBundle.getString("PAT_DATA"));
		item12.addActionListener(new MenuAct(12));
		item12.setMnemonic('A');
		menu1.add(item12);

		JMenuItem item13 = new JMenuItem(
				Jedecma.localMessagesBundle.getString("JOB_LIST"));
		item13.addActionListener(new MenuAct(13));
		item13.setMnemonic('P');
		menu1.add(item13);

		JMenuItem item15 = new JMenuItem(
				Jedecma.localMessagesBundle.getString("EXIT"));
		item15.addActionListener(new MenuAct(15));
		item15.setMnemonic('I');
		menu1.add(item15);

		if (Jedecma.ak.isEnableStatistics()) {
			JMenu menu2 = new JMenu(
					Jedecma.localMessagesBundle.getString("STATISTICS"));
			menu2.setMnemonic('S');
			menuBar.add(menu2);

			JMenuItem item21 = new JMenuItem(
					Jedecma.localMessagesBundle.getString("BY_TUMOR"));
			item21.addActionListener(new MenuAct(21));
			item21.setMnemonic('T');
			menu2.add(item21);

			JMenuItem item22 = new JMenuItem(
					Jedecma.localMessagesBundle.getString("BY_PATHOLOGY"));
			item22.addActionListener(new MenuAct(22));
			item22.setMnemonic('P');
			menu2.add(item22);

			menu2.addSeparator();

			JMenuItem item28 = new JMenuItem(
					Jedecma.localMessagesBundle.getString("EXPORT_EDECAN"));
			item28.addActionListener(new MenuAct(28));
			item28.setMnemonic('A');
			menu2.add(item28);

			JMenuItem item29 = new JMenuItem(
					Jedecma.localMessagesBundle.getString("EXPORT_EDECMA"));
			item29.addActionListener(new MenuAct(29));
			item29.setMnemonic('E');
			menu2.add(item29);

		}

		if (Jedecma.ak.isEnableTools()) {
			JMenu menu3 = new JMenu(
					Jedecma.localMessagesBundle.getString("TOOLS"));
			menu3.setMnemonic('T');
			menuBar.add(menu3);
			if (Jedecma.user != null) {
				if (Integer.parseInt(Jedecma.user.userpr) < 1) {
					menu3.setEnabled(false);
				}
			}

			JMenuItem item32 = new JMenuItem(
					Jedecma.localMessagesBundle.getString("CONFIG"));
			item32.addActionListener(new MenuAct(32));
			item32.setMnemonic('G');
			menu3.add(item32);

			JMenuItem item33 = new JMenuItem(
					Jedecma.localMessagesBundle.getString("USERS_MANAGEMENT"));
			item33.addActionListener(new MenuAct(33));
			item33.setMnemonic('U');
			if (!Jedecma.multiuser) {
				item33.setEnabled(false);
			}
			menu3.add(item33);

			JMenuItem item31 = new JMenuItem(
					Jedecma.localMessagesBundle.getString("DATA_MANAGEMENT"));
			item31.addActionListener(new MenuAct(31));
			item31.setMnemonic('D');
			menu3.add(item31);

			JMenuItem item34 = new JMenuItem(
					Jedecma.localMessagesBundle.getString("CCDB_IMPORT"));
			item34.addActionListener(new MenuAct(34));
			item34.setMnemonic('I');
			menu3.add(item34);

		}

		menuBar.add(Box.createHorizontalGlue());

		JMenu menu9 = new JMenu("Help        ");
		menu9.setMnemonic('H');
		menuBar.add(menu9);

		JMenuItem item92 = new JMenuItem("About Jedecma");
		item92.addActionListener(new MenuAct(92));
		item92.setMnemonic('A');
		menu9.add(item92);

		JMenuItem item91 = new JMenuItem("Session info");
		item91.addActionListener(new MenuAct(91));
		item91.setMnemonic('I');
		menu9.add(item91);

		return (menuBar);
	}

}

class MenuAct implements ActionListener {
	static Menuable activeProc = null;
	static JMenuItem menuItemInUse = null;
	int item;

	MenuAct(int i) {
		item = i;
	}

	public void actionPerformed(ActionEvent evt) {

		if (activeProc != null) {
			System.out.println("terminating: " + activeProc);
			activeProc.stop();
			if (menuItemInUse != null) {
				menuItemInUse.setEnabled(true);
			}
		}

		if (!Jedecma.splash.isVisible()) {
			Jedecma.splash.setVisible(true);
		}

		activeProc = null;
		switch (item) {
		case 11:
			activeProc = new EcoMgr();
			break;
		case 12:
			activeProc = new AnaMgr();
			break;
		case 13:
			new ProfTable();
			break;
		case 15:
			Jedecma.endProc();
			break;
		case 21:
			activeProc = new Stat1();
			break;
		case 22:
			activeProc = new Stat2();
			break;
		case 28:
			activeProc = new AnaExp();
			break;
		case 29:
			activeProc = new EcoExp();
			break;
		case 31:
			activeProc = new SQLTool();
			break;
		case 32:
			activeProc = new Config();
			break;
		case 33:
			new UsersMgr();
			break;
		case 34:
			activeProc = new CcdbImp();
			break;
		case 91:
			new DsplInfo();
			break;
		case 92:
			new About();
			break;
		default:
			Uti1.error("MenuAct: item " + item + " not yet active", false,
					Jedecma.mf);
		}
		if (activeProc != null) {
			menuItemInUse = (JMenuItem) evt.getSource();
			menuItemInUse.setEnabled(false);
			System.out.println("activating: " + activeProc);
			if (Jedecma.splash.isVisible()) {
				Jedecma.splash.setVisible(false);
			}
			activeProc.start();
		}
	}

}  // end of MainMenu class


  
