package org.opendas.equipment.obsolete;
//package org.opendas.equipment;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.opendas.ctrl.DASController;
//import org.opendas.equipment.DASBalanceListener.TypeCause;
//
///**
// * La classe DASACBalance, permet de gérer les Balances liées au poste.
// * 
// * @author vaznj repris par martineaua
// * 
// */
//public abstract class DASACBalance
//{
//
//	// liste des balances.
//	private List<DASBalanceListener>	listeners	= new ArrayList<DASBalanceListener>();
//
//	// permet d'ajouter un écouteur au listener
//	public void addBalanceListener(DASBalanceListener listener)
//	{
//		listeners.add(listener);
//	}
//
//	// permet de retirer un écouteur au listener
//	public void removeBalanceListener(DASBalanceListener listener)
//	{
//		listeners.remove(listener);
//	}
//
//	// récupère le poids
//	protected void dispatchReceptionPoids(String poids)
//	{
//		for (DASBalanceListener l : listeners)
//		{
//			l.receptionPoids(this, poids);
//		}
//	}
//
//	// annule la demande 
//	protected void dispatchPeseeAnnulee(String cause, TypeCause typeCause)
//	{
//		for (DASBalanceListener l : listeners)
//		{
//			l.peseeAnnulee(this, cause, typeCause);
//		}
//	}
//
//	/**
//	 * Suite à l'appel de cette méthode, la classe devra renvoyer
//	 * obligatoirement un événement receptionPoids ou peseeAnnulee.
//	 */
//	public abstract void demandePoids();
//
//	public abstract void sendDialog(String name);
//	
//	/**
//	 * Suite à l'appel de cette méthode, la classe devra renvoyer
//	 * obligatoirement un événement peseeAnnulee.
//	 */
//	public abstract void annulerPesee();
//
//	public abstract int getId();
//
//	public abstract String getCode();
//
//	public abstract String getType();
//	
//	public abstract String toString();
//
//	public abstract void setType(String type);
//
//	public abstract void setStopAsking(boolean ask);
//
//	public abstract boolean getstopAsking();
//
//	public abstract String getProperty(String key);
//
//	public abstract void addProperty(String key, String value);
//	
//	/*public abstract void setController(DASController controller);*/
//}
