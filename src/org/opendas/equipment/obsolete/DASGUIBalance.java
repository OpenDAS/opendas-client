package org.opendas.equipment.obsolete;
//package org.opendas.equipment;
//
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
//import org.opendas.DASLog;
//import org.opendas.ctrl.DASController;
//import org.opendas.equipment.DASBalanceListener.TypeCause;
//import org.opendas.translate.I18n;
//
///**
// * Simule une balance asynchrone pour les tests
// * 
// * @author vaznj repris par laugraudc et martineaua
// * 
// * 
// */
//public class DASGUIBalance extends DASACBalance
//{
//
//	// interface permettant de simuler une balance
//	private JDialog		dialog		= new JDialog((JFrame) null);
//	private JTextField	textField	= new JTextField();
//	private Etat		etat		= Etat.ATTENTE;
//
//	public enum Etat
//	{
//		ATTENTE, PESEE
//	}
//
//	public DASGUIBalance()
//	{
//		this("Balance sans nom");
//	}
//
//	public DASGUIBalance(String name)
//	{
//		textField.setFont(new Font("Arial", Font.BOLD, 18));
//		// textField.setBackground(new Color(200,220,255));
//		dialog.setTitle("[" + name + "] " + I18n._("Enter a weight"));
//		dialog.add(textField);
//		dialog.setSize(500, 60);
//		dialog.setResizable(false);
//		dialog.setLocationRelativeTo(null);
//		// permet de capturer les frappes clavier
//		dialog.addComponentListener(new ComponentListener() {
//
//			@Override
//			public void componentHidden(ComponentEvent e)
//			{
//				if (etat == Etat.PESEE)
//				{
//					log("Pesée annulée");
//					dispatchPeseeAnnulee("Action utilisateur", TypeCause.UTILISATEUR);
//				}
//				etat = Etat.ATTENTE;
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
//				try
//				{
//					double poids = Double.parseDouble(textField.getText());
//					dispatchReceptionPoids(""+poids);
//				} catch (Exception ee)
//				{
//					dispatchPeseeAnnulee("Bug de la balance", TypeCause.ERREUR);
//					logErr("Bug de la balance");
//					ee.printStackTrace();
//				}
//				etat = Etat.ATTENTE;
//				//dialog.setVisible(false);
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
//	// methode héritée
//	@Override
//	public void demandePoids()
//	{
//		if (etat == Etat.PESEE)
//			logErr("ATTENTION ! Plusieurs demandes de pesée à la fois");
//		etat = Etat.PESEE;
//		dialog.setVisible(true);
//	}
//
//	// methode héritée
//	@Override
//	public void annulerPesee()
//	{
//		dialog.setVisible(false);
//		etat = Etat.ATTENTE;
//		dispatchPeseeAnnulee("utilisateur", TypeCause.UTILISATEUR);
//	}
//
//	private void log(String log)
//	{
//		DASLog.log(getClass().getSimpleName(), log);
//	}
//
//	private void logErr(String log)
//	{
//		DASLog.logErr(getClass().getSimpleName(), log);
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
//	public void addProperty(String key, String value)
//	{
//		// TODO Auto-generated method stub
//		
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
//	public String getProperty(String key)
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getType()
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean getstopAsking()
//	{
//		// TODO Auto-generated method stub
//		return false;
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
//	public void setType(String type)
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public String toString()
//	{
//		return "DASGUIBalance [dialog=" + dialog + ", etat=" + etat + ", textField=" + textField + "]";
//	}
//
//	@Override
//	public void sendDialog(String name)
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	/*@Override
//	public void setController(DASController controller)
//	{
//		// TODO Auto-generated method stub
//		
//	}*/
//	
//	
//}
