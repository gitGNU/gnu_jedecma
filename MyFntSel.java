/*  
 * MyFntSel.java - very basic font selector
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

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class MyFntSel extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton okBut, defBut;
	private String curFontSt, defFontSt, selFontSt;
	private int curSize, curStyle, selSize, selStyle, defSize, defStyle;
	private static final String okString = Jedecma.localMessagesBundle
			.getString("CB_OK");
	private static final String defString = "DEFAULT";
	String[] fonts;
	String[] sizes = { "8", "9", "10", "11", "12", "13", "14", "15", "16",
			"17", "18", "19", "20" };
	String[] styles = { "PLAIN", "BOLD", "ITALIC" };
	int[] styleId = { Font.PLAIN, Font.BOLD, Font.ITALIC };
	JList fontList, sizeList, styleList;
	MyJlabel test;
	boolean selected;

	MyFntSel() {
		super(Jedecma.mf, "Font", true); // E' un JDialog modale!
		defFontSt = "Dialog";
		defStyle = Font.PLAIN;
		defSize = 12;

		selFontSt = "";
		selSize = 0;
		selStyle = 0;

		curFontSt = "";
		curSize = 0;
		curStyle = 0;

		sizeList = new JList(sizes);
		styleList = new JList(styles);
		// ricava lista dei fontdisponibili
		GraphicsEnvironment e = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		// Font[] fonts = e.getAllFonts();
		fonts = e.getAvailableFontFamilyNames();
		fontList = new JList(fonts);

		selected = false;
	}

	String getSelFont() {
		String s = "";
		if (selected) {
			s += selFontSt;
			switch (selStyle) {
			case Font.PLAIN: {
				s += ",PLAIN";
			}
				break;
			case Font.BOLD: {
				s += ",BOLD";
			}
				break;
			case Font.ITALIC: {
				s += ",ITALIC";
			}
				break;
			}
			s += "," + String.valueOf(selSize);
		}
		return s;
	}

	public void chooseFont(String title, String fnt, String def) {
		// legge font default
		if (!def.equals("")) {
			Font font = Uti1.myFont(def);
			if (font != null) {
				defFontSt = font.getName();
				defSize = font.getSize();
				defStyle = font.getStyle();
			}
		}

		// legge font corrente
		if (!fnt.equals("")) {
			Font font = Uti1.myFont(fnt);
			if (font != null) {
				curFontSt = font.getName();
				curSize = font.getSize();
				curStyle = font.getStyle();
			}
		}

		if (curFontSt.equals("")) {
			curFontSt = defFontSt;
			curSize = defSize;
			curStyle = defStyle;
		}

		// String testString = "The Quick Silver Fox Jumped Over The Lazy Dog";
		String testString = "Lorem ipsum dolor sit amet, consectetur adipisicing elit";

		JPanel mainPan = new JPanel();
		mainPan.setLayout(new BoxLayout(mainPan, BoxLayout.Y_AXIS));

		JPanel fontPan = new JPanel();
		fontPan.setLayout(new BoxLayout(fontPan, BoxLayout.X_AXIS));
		fontPan.setBorder(BorderFactory.createTitledBorder(""));

		JScrollPane fontNamesPan = new JScrollPane(fontList);
		fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fontNamesPan.setBorder(BorderFactory.createTitledBorder("Font"));
		updateLists();

		{
			MouseListener mouseListener = new MouseAdapter() {
				public void mouseClicked(MouseEvent mouseEvent) {
					JList theList = (JList) mouseEvent.getSource();
					if (mouseEvent.getClickCount() == 1) {
						int index = theList.locationToIndex(mouseEvent
								.getPoint());
						if (index >= 0) {
							Object o = theList.getModel().getElementAt(index);
							String s = o.toString();
							setCurrent(s, curSize, curStyle);
							refreshGui();
						}
					}
				}
			};
			fontList.addMouseListener(mouseListener);
		}
		fontPan.add(fontNamesPan);

		JScrollPane fontSizePan = new JScrollPane(sizeList);
		fontSizePan.setBorder(BorderFactory.createTitledBorder("Dim"));
		updateLists();

		{
			MouseListener mouseListener = new MouseAdapter() {
				public void mouseClicked(MouseEvent mouseEvent) {
					JList theList = (JList) mouseEvent.getSource();
					if (mouseEvent.getClickCount() == 1) {
						int index = theList.locationToIndex(mouseEvent
								.getPoint());
						if (index >= 0) {
							Object o = theList.getModel().getElementAt(index);
							int size = 0;
							size = Integer.parseInt(o.toString());
							setCurrent(curFontSt, size, curStyle);
							refreshGui();
						}
					}
				}
			};
			sizeList.addMouseListener(mouseListener);
		}
		fontPan.add(fontSizePan);

		JScrollPane fontAttrPan = new JScrollPane(styleList);
		fontAttrPan.setBorder(BorderFactory.createTitledBorder("Attrib"));
		updateLists();

		{
			MouseListener mouseListener = new MouseAdapter() {
				public void mouseClicked(MouseEvent mouseEvent) {
					JList theList = (JList) mouseEvent.getSource();
					if (mouseEvent.getClickCount() == 1) {
						int index = theList.locationToIndex(mouseEvent
								.getPoint());
						if (index >= 0) {
							Object o = theList.getModel().getElementAt(index);
							int style = 0;
							if (o.toString().toUpperCase().equals("PLAIN")) {
								style = Font.PLAIN;
							} else if (o.toString().toUpperCase()
									.equals("BOLD")) {
								style = Font.BOLD;
							} else if (o.toString().toUpperCase().equals(
									"ITALIC")) {
								style = Font.ITALIC;
							}
							setCurrent(curFontSt, curSize, style);
							refreshGui();
						}
					}
				}
			};
			styleList.addMouseListener(mouseListener);
		}
		fontPan.add(fontAttrPan);

		mainPan.add(fontPan);

		JPanel testPan = new JPanel();
		testPan.setBorder(BorderFactory.createTitledBorder("Test"));
		test = new MyJlabel(testString);
		testPan.add(test);
		mainPan.add(testPan);

		JPanel butPan = new JPanel();

		okBut = new JButton(okString);
		okBut.setMnemonic(KeyEvent.VK_K);
		okBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okBut.setEnabled(false);
				selFontSt = curFontSt;
				selStyle = curStyle;
				selSize = curSize;
				selected = true;
				stop();
			}
		});
		// okBut.setActionCommand(okString);
		okBut.setEnabled(true);
		butPan.add(okBut);

		defBut = new JButton(defString);
		defBut.setMnemonic(KeyEvent.VK_K);
		defBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				defBut.setEnabled(false);
				// updateLists();
				setCurrent(defFontSt, defSize, defStyle);
				refreshGui();
				defBut.setEnabled(true);
			}
		});
		// defBut.setActionCommand(okString);
		defBut.setEnabled(true);
		butPan.add(defBut);

		mainPan.add(butPan);

		getContentPane().add(mainPan);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// quello che deve essere fatto alla chiusura della finestra
			}
		});

		pack();
		setVisible(true);

	}

	public void stop() {
		dispose();
	}

	void setCurrent(String fontSt, int size, int style) {
		curFontSt = fontSt;
		curSize = size;
		curStyle = style;

		if (curFontSt.equals("")) {
			curFontSt = defFontSt;
		}
		if (curSize < 1 || curSize > 30) {
			curSize = defSize;
		}
		if (curStyle != Font.PLAIN && curStyle != Font.BOLD
				&& curStyle != Font.ITALIC) {
			curStyle = defStyle;
		}
	}

	void updateLists() {
		{ 
			int selIndex = 0;
			for (int i = 0; i < fonts.length; i++) {
				if (fonts[i].toUpperCase().equals(curFontSt.toUpperCase())) {
					selIndex = i;
				}
			}
			fontList.setSelectedValue(fonts[selIndex], true); 
		}

		{
			int selIndex = 0;
			for (int i = 0; i < sizes.length; i++) {
				int n = Integer.parseInt(sizes[i]);
				if (n == curSize) {
					selIndex = i;
				}
			}
			sizeList.setSelectedValue(sizes[selIndex], true); 
		}

		{
			int selIndex = 0;
			for (int i = 0; i < styleId.length; i++) {
				if (styleId[i] == curStyle) {
					selIndex = i;
				}
			}
			styleList.setSelectedValue(styles[selIndex], true); 
		}

	}

	void refreshGui() {
		test.setFont(new Font(curFontSt, curStyle, curSize));
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateLists();
				repaint();
			}
		});
	}

} // end MyFntSel class
