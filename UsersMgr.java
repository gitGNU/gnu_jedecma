/*  
 * UsersMgr.java - users management
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;

public class UsersMgr extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final String delString = Jedecma.localMessagesBundle
			.getString("DELETE");
	private static final String okString = "OK";
	private static final String lockCode = "TBLUSER";
	private JButton delBut, okBut;
	private MyJTextField userName;
	private JPasswordField userPass;
	private MyJComboBox userPr;
	private JList userList;
	private Vector<String> users = new Vector<String>();

	public UsersMgr() {
		super(Jedecma.mf, Jedecma.localMessagesBundle.getString("USERS_MANAGEMENT"),
				true); // E' un JDialog modale!

		// lock della tabella
		int lockrv = MyLock.setLock(Jedecma.dbmgr, lockCode,
				Jedecma.user.userid, true);
		if (lockrv == 0) {

			ResultSet resultSet;
			resultSet = Jedecma.dbmgr
					.executeQuery("SELECT username FROM TBLUSER WHERE userno <> "
							+ Jedecma.user.userid);
			try {
				while (resultSet.next()) {
					String s = (String) resultSet.getString("username");
					users.add(s.trim());
				}
			} catch (SQLException ex) {
				System.err.println(ex);
				Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
			}

			userList = new JList(users);

			{
				MouseListener mouseListener = new MouseAdapter() {
					public void mouseClicked(MouseEvent mouseEvent) {
						JList theList = (JList) mouseEvent.getSource();
						if (mouseEvent.getClickCount() == 1) {
							int index = theList.locationToIndex(mouseEvent
									.getPoint());
							if (index >= 0) {
								Object o = theList.getModel().getElementAt(
										index);
								String uName = o.toString();
								String uPass = "";
								int iPr = 0;

								ResultSet resultSet;
								resultSet = Jedecma.dbmgr
										.executeQuery("SELECT userpr, userpw FROM TBLUSER WHERE username = '"
												+ uName + "'");

								try {
									while (resultSet.next()) {
										uPass = (String) resultSet
												.getString("userpw");
										String p = (String) resultSet
												.getString("userpr");
										if (p.equals("1")) {
											iPr = 1;
										}
									}

								} catch (SQLException ex) {
									System.err.println(ex);
									Uti1.error(Jedecma.localMessagesBundle.getString("SQL_ERROR"), true);
								}

								uPass = Base64.decode(uPass);
								userPr.setSelectedIndex(iPr);
								userName.setText(uName.trim());
								userPass.setText(uPass.trim());
								okBut.setSelected(true);
							}
						}
					}
				};
				userList.addMouseListener(mouseListener);
			}

			JPanel mainPan = new JPanel();
			mainPan.setLayout(new BoxLayout(mainPan, BoxLayout.Y_AXIS));

			JScrollPane userListPan = new JScrollPane(userList);
            userListPan.setBorder(BorderFactory.createTitledBorder(Jedecma.localMessagesBundle.getString("USERS_LIST")));
			mainPan.add(userListPan, BorderLayout.CENTER);

			JPanel userPan = new JPanel();
			GridBagLayout gbUPan = new GridBagLayout();
			GridBagConstraints gbcUPan = new GridBagConstraints();
			userPan.setLayout(gbUPan);
			userPan.setBorder(BorderFactory.createTitledBorder(""));

			{
				MyJlabel lab = new MyJlabel(Jedecma.localMessagesBundle.getString("USER"));
				Uti1.bldConst(gbcUPan, 0, 0, 5, 1, 0, 1,
						GridBagConstraints.NORTH, GridBagConstraints.BOTH);
				gbUPan.setConstraints(lab, gbcUPan);
				userPan.add(lab);
				userName = new MyJTextField (15, "", new float[]{}, 0);
				Uti1.bldConst(gbcUPan, 5, 0, 10, 1, 1, 1,
						GridBagConstraints.NORTH, GridBagConstraints.BOTH);
				gbUPan.setConstraints(userName, gbcUPan);
				userPan.add(userName);
			}

			{
				MyJlabel lab = new MyJlabel("Password");
				Uti1.bldConst(gbcUPan, 0, 1, 5, 1, 0, 1,
						GridBagConstraints.NORTH, GridBagConstraints.BOTH);
				gbUPan.setConstraints(lab, gbcUPan);
				userPan.add(lab);
				userPass = new JPasswordField(8);
				userPass.setDocument(new FixedSizePlainDocument(8));
				Uti1.bldConst(gbcUPan, 5, 1, 10, 1, 1, 1,
						GridBagConstraints.NORTH, GridBagConstraints.BOTH);
				gbUPan.setConstraints(userPass, gbcUPan);
				userPass.setText("");
				userPan.add(userPass);
			}

			{
				MyJlabel lab = new MyJlabel(Jedecma.localMessagesBundle.getString("ADMIN"));
				Uti1.bldConst(gbcUPan, 0, 2, 5, 1, 0, 1,
						GridBagConstraints.NORTH, GridBagConstraints.BOTH);
				gbUPan.setConstraints(lab, gbcUPan);
				userPan.add(lab);
    			userPr = new MyJComboBox(new String[] {
						"0=" + Jedecma.localMessagesBundle.getString("CB_NO"), 
						"1=" + Jedecma.localMessagesBundle.getString("CB_YES")
						}, String.valueOf(0));    
				Uti1.bldConst(gbcUPan, 5, 2, 5, 1, 0, 1,
						GridBagConstraints.NORTH, GridBagConstraints.BOTH);
				gbUPan.setConstraints(userPr, gbcUPan);
				userPr.setSelectedIndex(0);
				userPan.add(userPr);
			}

			mainPan.add(userPan);

			JPanel butPan = new JPanel();
			butPan.setLayout(new FlowLayout(FlowLayout.RIGHT));
			butPan.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

			delBut = new JButton(delString);
			delBut.setMnemonic(KeyEvent.VK_L);
			delBut.addActionListener(new DelListener());
			butPan.add(delBut);

			okBut = new JButton(okString);
			okBut.setMnemonic(KeyEvent.VK_O);
			okBut.addActionListener(new OkListener());
			butPan.add(okBut);

			mainPan.add(butPan, BorderLayout.SOUTH);

			getContentPane().add(mainPan, BorderLayout.CENTER);

			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					// rimuove lock
					MyLock
							.delLock(Jedecma.dbmgr, lockCode,
									Jedecma.user.userid);
				}
			});

			// setResizable(false);
			pack();
			setVisible(true);
		} else {
			Uti1.error(Jedecma.localMessagesBundle.getString("USERS_LIST")
					+ " " + Jedecma.localMessagesBundle.getString("IN_USE")
					+ " (" + lockrv + ")", false);
		}

	} // fine costruttore

	public boolean existUser(String c) {
		String name = c;

		for (Iterator<String> i = users.iterator(); i.hasNext();) {
			String item = (String) i.next();
			if (item.equals(name)) {
				return true;
			}
		}
		return false;
	}

	void refreshGui() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateLists();
				repaint();
			}
		});
	}

	void updateLists() {
		userList.setListData(users);
	}

	class OkListener implements ActionListener {
		OkListener() {
		}

		public void actionPerformed(ActionEvent e) {
			okBut.setEnabled(false);
			delBut.setEnabled(false);
			String uName = userName.getText().trim();
			String pwString = "";
			{
				char[] input = userPass.getPassword();
				for (int i = 0; i < input.length; i++) {
					pwString += input[i];
				}
			}
			pwString = pwString.trim();
			String uAdmin = String.valueOf(userPr.getSelectedIndex());

			boolean ctr = true;
			if (uName.length() == 0) {
				ctr = false;
			}

			if (ctr && pwString.length() == 0) {
				ctr = false;
			}

			if (ctr
					&& ((userPr.getSelectedIndex() < 0) || (userPr
							.getSelectedIndex() > 1))) {
				ctr = false;
			}

			if (ctr) {
				int rc = 0;
				boolean newUser = !(existUser(uName));
				if (newUser) {
					rc = Jedecma.dbmgr
							.executeUpdate("INSERT INTO TBLUSER (username, userpw, userpr) VALUES ('"
									+ uName
									+ "', '"
									+ Base64.encode(pwString)
									+ "', '" + uAdmin + "')");

				} else {
					rc = Jedecma.dbmgr.executeUpdate("UPDATE TBLUSER set "
							+ "userpw='" + Base64.encode(pwString) + "'"
							+ ", userpr='" + uAdmin + "'"
							+ " WHERE username = '" + uName + "'");
				}
				if (rc != 1) {
					Uti1.error(Jedecma.localMessagesBundle
							.getString("USERS_LIST")
							+ ": "
							+ Jedecma.localMessagesBundle
									.getString("INS_UPD_FAILURE"), true);
				} else {
					if (newUser) {
						users.add(uName);
					}
					userName.setText("");
					userPass.setText("");
					userPr.setSelectedIndex(0);
				}
			} else {
				Uti1.error(Jedecma.localMessagesBundle
								.getString("CHECK_USER_DATA"), false);
			}
			okBut.setEnabled(true);
			delBut.setEnabled(true);
			refreshGui();
		}

	}

	class DelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			delBut.setEnabled(false);
			okBut.setEnabled(false);
			String uName = userName.getText().trim();
			boolean ctr = true;
			if (uName.length() == 0) {
				ctr = false;
			}
			if (ctr) {
				int rc = Jedecma.dbmgr
						.executeUpdate("DELETE FROM TBLUSER WHERE username='"
								+ Uti1.escape(uName) + "'");
				if (rc != 1) {
					Uti1.error(Jedecma.localMessagesBundle
							.getString("USERS_LIST")
							+ ": "
							+ Jedecma.localMessagesBundle
									.getString("INS_UPD_FAILURE"), true);

				} else {
					users.removeElement((Object) uName);
				}
			}
			userName.setText("");
			userPass.setText("");
			userPr.setSelectedIndex(0);
			okBut.setEnabled(true);
			delBut.setEnabled(true);
			refreshGui();
		}
	}

} // end UsersMgr class
