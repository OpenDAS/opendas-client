//package org.opendas.equipment.obsolete;
//
//import org.opendas.equipment.obsolete.*;
//import java.awt.Font;
//import java.awt.event.ComponentEvent;
//import java.awt.event.ComponentListener;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//
//import javax.swing.JDialog;
//import javax.swing.JFrame;
//import javax.swing.JTextField;
//
//import org.opendas.translate.I18n;
//
///**
// * Simule un scanner asynchrone pour les tests
// * 
// * @author vaznj repris par laugraudc et martineaua
// * 
// */
//public class DASGUIScanner extends DASACScanner
//{
//	private String		type;
//	private JDialog		dialog		= new JDialog((JFrame) null);
//	private JTextField	textField	= new JTextField();
//
//	public DASGUIScanner()
//	{
//		this("Scanner sans nom");
//	}
//
//	public DASGUIScanner(String name)
//	{
//		textField.setFont(new Font("Arial", Font.BOLD, 18));
//		// textField.setBackground(new Color(200,220,255));
//		dialog.setTitle("[" + name + "] "+ I18n._("Enter a code"));
//		dialog.add(textField);
//		dialog.setSize(500, 60);
//		dialog.setResizable(false);
//		dialog.setLocationRelativeTo(null);
//		// interface permettant de simuler un scanner
//		dialog.addComponentListener(new ComponentListener() {
//
//			@Override
//			public void componentHidden(ComponentEvent e)
//			{
//			}
//
//			@Override
//			public void componentMoved(ComponentEvent e)
//			{
//			}
//
//			@Override
//			public void componentResized(ComponentEvent e)
//			{
//			}
//
//			@Override
//			public void componentShown(ComponentEvent e)
//			{
//				textField.setText("");
//			}
//		});
//		textField.addKeyListener(new KeyListener() {
//
//			@Override
//			public void keyReleased(KeyEvent e)
//			{
//				if (e.getKeyCode() != KeyEvent.VK_ENTER)
//					return;
//				dialog.setVisible(false);
//				dispatchCodeRecu(textField.getText());
//			}
//
//			@Override
//			public void keyTyped(KeyEvent e)
//			{
//			}
//
//			@Override
//			public void keyPressed(KeyEvent e)
//			{
//			}
//		});
//	}
//
//	// méthode héritée
//	@Override
//	public void demandeCode()
//	{
//		dialog.setVisible(true);
//	}
//
//	@Override
//	public void termineDemandeCode()
//	{
//		dialog.setVisible(false);
//	}
//
//	@Override
//	public int getId()
//	{
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public boolean getstopAsking()
//	{
//		// TODO Auto-generated method stub
//		return true;
//	}
//
//	@Override
//	public String getCode()
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getType()
//	{
//		return this.type;
//	}
//
//	@Override
//	public void setType(String type)
//	{
//		this.type = type;
//	}
//
//	@Override
//	public void setStopAsking(boolean ask)
//	{
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void addProperty(String key, String value)
//	{
//	}
//
//	@Override
//	public String getProperty(String key)
//	{
//		return null;
//	}
//}