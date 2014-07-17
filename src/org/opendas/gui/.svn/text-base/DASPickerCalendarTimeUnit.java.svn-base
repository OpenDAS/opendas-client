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

import org.opendas.DASLog;
import org.opendas.translate.I18n;


public class DASPickerCalendarTimeUnit {

	String value;
	JDialog d;
	JButton[] button = new JButton[7];
	int nbR = 7, nbC = 1;

	public DASPickerCalendarTimeUnit(JFrame parent, String oldChar) {
		this.value = oldChar;
		this.d = new JDialog();
		this.d.setAlwaysOnTop(true);
		this.d.setModal(true);
		JPanel p1 = new JPanel(new GridLayout(nbR, nbC));
		p1.setPreferredSize(new Dimension(600, 400));

		String[] header = { I18n._("Second(s)") , I18n._("Minute(s)"), I18n._("Hour(s)"), I18n._("Day(s)"), I18n._("Week(s)"), I18n._("Month(s)"), I18n._("Year(s)") };

		for (int x = 0; x < nbR*nbC; x++) {
			final int selection = x;

			button[x] = new JButton();
			button[x].setFocusPainted(false);
			button[x].setBackground(Color.white);

			button[x].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if(button[selection].getActionCommand().equals("")){
					}else{
						value = button[selection].getActionCommand();
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

	public String setPickedDate() {
		logDebug(value);
		return value;
	}
	
	private void logDebug(String log)
	{
		DASLog.logDebug(getClass().getSimpleName(), log);
	}
}
