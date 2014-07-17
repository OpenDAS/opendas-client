package org.opendas.equipment.obsolete;
//package org.opendas.equipment;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.opendas.DASLog;
//import org.opendas.ctrl.DASController;
//
///**
// * Classe d'encapsulation pour permettre le timeout d'une balance. Si la valeur
// * d'un timeout est <= 0, il n'est pas appliqué.
// * 
// */
//public class DASTimeoutBalance extends DASACBalance implements DASBalanceListener
//{
//
//	private int				timeoutPesee;
//	private int				timeoutAnnuler;
//	private DASACBalance	balance;
//	private Timer			timer	= new Timer();
//	private TimerBalance	timerBalance;
//	private Etat			etat	= Etat.ATTENTE;
//
//	private enum Etat
//	{
//		ATTENTE, PESEE, ANNULATION
//	}
//
//	private class TimerBalance extends TimerTask
//	{
//
//		@Override
//		public void run()
//		{
//			timeoutExpired();
//		}
//	}
//
//	public DASTimeoutBalance(int timeout, DASACBalance balance)
//	{
//		this(timeout, timeout, balance);
//	}
//
//	public DASTimeoutBalance(int timeoutPesee, int timeoutAnnuler, DASACBalance balance)
//	{
//		this.timeoutPesee = timeoutPesee;
//		this.timeoutAnnuler = timeoutAnnuler;
//		this.balance = balance;
//		balance.addBalanceListener(this);
//	}
//
//	@Override
//	public void demandePoids()
//	{
//		logDebug("Demande de poids");
//		if (etat != Etat.ATTENTE)
//		{
//			logErr("Pesée impossible");
//			return;
//		}
//		etat = Etat.PESEE;
//		if (timeoutPesee > 0)
//		{
//			timerBalance = new TimerBalance();
//			timer.schedule(timerBalance, timeoutPesee);
//		}
//		balance.demandePoids();
//	}
//
//	@Override
//	public void annulerPesee()
//	{
//		logDebug("Annulation de pesée");
//		
//		if (etat != Etat.PESEE)
//		{
//			logErr("Annulation impossible");
//			return;
//		}
//		etat = Etat.ANNULATION;
//		if (timeoutAnnuler > 0)
//		{
//			timerBalance = new TimerBalance();
//			timer.schedule(timerBalance, timeoutAnnuler);
//		}
//		balance.annulerPesee();
//	}
//
//	@Override
//	public void peseeAnnulee(DASACBalance balance, String cause, TypeCause type)
//	{
//		if (etat == Etat.ANNULATION || etat == Etat.PESEE)
//		{
//			timerBalance.cancel();
//			timer.purge();
//			timerBalance = null;
//			etat = Etat.ATTENTE;
//			dispatchPeseeAnnulee(cause, type);
//		}
//	}
//
//	@Override
//	public void receptionPoids(DASACBalance balance, String poids)
//	{
//		logDebug("Réception du poids : " + poids);
//		
//		if (etat == Etat.PESEE)
//		{
//			timerBalance.cancel();
//			timer.purge();
//			timerBalance = null;
//			etat = Etat.ATTENTE;
//			dispatchReceptionPoids(poids);
//		}
//	}
//
//	private void timeoutExpired()
//	{
//		logDebug("timeout expiré");
//		if (etat == Etat.PESEE)
//		{
//			etat = Etat.ATTENTE;
//			timerBalance.cancel();
//			balance.annulerPesee();
//			dispatchPeseeAnnulee("Timeout", TypeCause.ERREUR);
//		} else if (etat == Etat.ANNULATION)
//		{
//			etat = Etat.ATTENTE;
//			timerBalance.cancel();
//			dispatchPeseeAnnulee("Timeout", TypeCause.ERREUR);
//		}
//	}
//
//	private void logDebug(String log)
//	{
//		DASLog.logDebug(getClass().getSimpleName(), log);
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
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void sendDialog(String name)
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setController(DASController controller)
//	{
//		// TODO Auto-generated method stub
//		
//	}
//}
