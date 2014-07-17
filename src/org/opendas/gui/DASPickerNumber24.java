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


public class DASPickerNumber24 {

	int day;
	JDialog d;
	JButton[] button = new JButton[25];
	int nbR = 5, nbC = 5;

	public DASPickerNumber24(JFrame parent, int oldNum) {
		this.day = oldNum;
		this.d = new JDialog();
		this.d.setAlwaysOnTop(true);
		this.d.setModal(true);
		JPanel p1 = new JPanel(new GridLayout(nbR, nbC));
		p1.setPreferredSize(new Dimension(600, 400));

		@SuppressWarnings("unused")
		int selection = 0;

		for (int x = 0; x < nbR*nbC; x++) {
			selection = x;
			final int selectionI = x;

			button[x] = new JButton();
			button[x].setFocusPainted(false);
			button[x].setBackground(Color.white);

			if(x < button.length-1){
				button[x].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if(button[selectionI].getActionCommand().equals("")){
						}else{
							day = Integer.parseInt(button[selectionI].getActionCommand());
						}
						d.dispose();
					}
				});
				button[x].setText(""+(x));
			}else{
				button[x].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						d.dispose();
					}
				});
			}
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
		d.setTitle("Number Picker");
		d.setVisible(true);
	}

	public int setPickedDate() {
		logDebug(String.valueOf(day));
		return day;
	}
	
	private void logDebug(String log)
	{
		DASLog.logDebug(getClass().getSimpleName(), log);
	}
}