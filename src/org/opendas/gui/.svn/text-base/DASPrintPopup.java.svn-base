package org.opendas.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import org.opendas.DASLog;
import org.opendas.translate.I18n;

/**
 * Panel de la fenêtre principale
 * 
 * @author vaznj repris par laugraudc et martineaua
 * @author mlaroche
 */
public class DASPrintPopup extends JPanel
{

	private static final long	serialVersionUID	= 1L;
	private DASPanel			panel;
	private JFrame				frame				= new JFrame(I18n._("Printing"));

	public DASPrintPopup(DASPanel owner)
	{
		panel = owner;
	}

	public void AfficherPopup(String sujet)
	{
		frame.setSize(250, 100);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		frame.setVisible(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JButton popupOk = new JButton("OK");
		popupOk.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
				panel.SetWindowEnabled(true);
				panel.getController().nextActionBtn();
			}
		});
		JButton popupRecommencer = new JButton(I18n._("RESTART"));
		popupRecommencer.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
				panel.getController().print();
			}
		});
		JPanel buttonPrint = new JPanel();
		buttonPrint.add(popupOk);
		buttonPrint.add(popupRecommencer);
		JPanel textPrint = new JPanel();
		textPrint.add(new JLabel(sujet));
		frame.getContentPane().add(textPrint, BorderLayout.PAGE_START);
		frame.getContentPane().add(buttonPrint, BorderLayout.CENTER);
		PopupFactory factory = PopupFactory.getSharedInstance();
		frame.setVisible(true);
		panel.SetWindowEnabled(false); // Background JFrame Unclickable
		try
		{
			Popup popup = factory.getPopup(panel, frame, 0, 0);
			popup.show();
		} catch (Exception e)
		{
			logDebug("Bad Frame, No Frame.");
		}
	}
	
	private void logDebug(String log)
	{
		DASLog.logDebug(getClass().getSimpleName(), log);
	}
}
