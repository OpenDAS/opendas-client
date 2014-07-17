package org.opendas.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opendas.translate.I18n;


public class DASPickerNumber60 {

	private int			value;
	private JDialog		d;
	private JButton[]	button;
	private int			nbR;
	private int 		nbC;


	public DASPickerNumber60(JFrame parent, int oldNum, boolean isDate) {
		this.value = oldNum;
		this.d = new JDialog();
        this.d.setAlwaysOnTop(true);
		this.d.setModal(true);
		
		JPanel p1 = new JPanel();
		p1.setPreferredSize(new Dimension(600, 400));
		
		String[] header;

		if(isDate){
			String[] headerTmp = {"0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};
			header = headerTmp;
			nbR	= 3;
			nbC = 4;
			p1.setLayout(new GridLayout(nbR, nbC));
			button	= new JButton[12];
		}else{
			String[] headerTmp = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "30", "35", "40", "45", "50"};
			header = headerTmp;
			nbR = 6;
			nbC = 5;
			p1.setLayout(new GridLayout(nbR,nbC));
			button	= new JButton[30];
		}
		for (int x = 0; x < nbR*nbC; x++) {
			final int selection = x;

			button[x] = new JButton();
			button[x].setFocusPainted(false);
			button[x].setBackground(Color.white);

			button[x].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if(button[selection].getActionCommand().equals("")){
					}else{
						value = Integer.parseInt(button[selection].getActionCommand());
					}
					d.dispose();
				}
			});
			button[x].setText(header[x]);
			p1.add(button[x]);
		}

		JPanel p2 = new JPanel(new GridLayout(1, 3));
		p2.setPreferredSize(new Dimension(600, 50));
		JButton closeButton = new JButton(I18n._("Close"));
        closeButton.setFocusable(false);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				d.dispose();
			}
		});
		p2.add(closeButton);

		d.add(p1, BorderLayout.CENTER);
		d.add(p2, BorderLayout.SOUTH);
		d.pack();
		d.setLocationRelativeTo(parent);
		d.setTitle(I18n._("Number Picker"));
		d.setVisible(true);
	}

	public int setPickedDate() {
		return value;
	}
}