/*  
 * AccessPane.java - input of the user credentials
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AccessPane extends JDialog {

	private static final long serialVersionUID = 1L;
	public String username, password;
	private JButton okBut, canBut;
	JTextField userFld;
	JPasswordField passFld;
	int lap;
	final int RETMAX = 3;
	boolean auth = false;

	public AccessPane() {
		super(Jedecma.mf, "Jedecma " + Jedecma.progVers + ": "
				+ Jedecma.localMessagesBundle.getString("LOGIN"), true);

		JPanel mainPan = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		mainPan.setLayout(gb);

		getContentPane().add(mainPan);

		MyJlabel l;
		Uti1.bldConst(gbc, 0, 0, 1, 1, 0, 0);
		l = new MyJlabel(Jedecma.localMessagesBundle.getString("USER"));
		gb.setConstraints(l, gbc);
		mainPan.add(l);

		userFld = new JTextField(30);
		Uti1.bldConst(gbc, 0, 1, 1, 1, 0, 0);
		String s = System.getProperty("user.name");
		if (Jedecma.username.length() > 0) {
			s = Jedecma.username;
		}
		userFld.setText(s);
		gb.setConstraints(userFld, gbc);
		mainPan.add(userFld);

		l = new MyJlabel(Jedecma.localMessagesBundle.getString("PASSWORD"));
		Uti1.bldConst(gbc, 0, 2, 1, 1, 0, 0);
		gb.setConstraints(l, gbc);
		mainPan.add(l);

		passFld = new JPasswordField(30);
		Uti1.bldConst(gbc, 0, 3, 1, 1, 0, 0);
		// passFld.setText("");
		gb.setConstraints(passFld, gbc);
		mainPan.add(passFld);

		JPanel butPan = new JPanel();
		butPan.setLayout(new FlowLayout(FlowLayout.CENTER));
		butPan.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		Uti1.bldConst(gbc, 0, 4, 1, 1, 0, 0);
		gb.setConstraints(butPan, gbc);
		mainPan.add(butPan);

		okBut = new JButton(Jedecma.localMessagesBundle.getString("CB_OK"));
		okBut.setMnemonic(KeyEvent.VK_O);
		butPan.add(okBut);

		okBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lap++;
				String su = userFld.getText();
				String sp = getPwStr(passFld);
				User u = new User(su, sp);
				if (u.userid > 0) {
					auth = true;
					username = su;
					password = sp;
					setVisible(false);
					dispose();
					return;
				} else {
					if (lap >= RETMAX) {
						auth = true;
						username = su;
						password = "";
						setVisible(false);
						dispose();
						return;
					}
				}
				passFld.setText("");
			}
		});
		okBut.setEnabled(true);

		canBut = new JButton(Jedecma.localMessagesBundle.getString("CLEAR"));
		canBut.setMnemonic(KeyEvent.VK_C);
		canBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				auth = false;
				dispose();
			}
		});
		butPan.add(canBut);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				auth = false;
			}
		});

		// setResizable(false);
		pack();
		setVisible(true);

	}

	String getPwStr(JPasswordField pf) {
		String s = "";
		char[] pass = pf.getPassword();
		for (int i = 0; i < pass.length; i++) {
			s += pass[i];
		}
		return (s);
	}

} // end of class
