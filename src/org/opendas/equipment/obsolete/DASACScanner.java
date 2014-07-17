package org.opendas.equipment.obsolete;
//package org.opendas.equipment;
//
//import java.util.ArrayList;
//import java.util.List;
//import org.opendas.*;
///**
// * La classe DASScanner, permet de gérer les Scanners liés au poste.
// * 
// * @author vaznj repris par laugraudc et martineaua
// * 
// */
//
//public abstract class DASACScanner
//{
//
//	// liste des Scanners.
//	private List<DASScannerListener>	listeners	= new ArrayList<DASScannerListener>();
//
//	// permet d'ajouter un écouteur au listener
//	public void addScannerListener(DASScannerListener listener)
//	{
//		listeners.add(listener);
//	}
//
//	// permet de retirer un écouteur au listener
//	public void removeScannerListener(DASScannerListener listener)
//	{
//		listeners.remove(listener);
//	}
//
//	// récupère le code
//	protected void dispatchCodeRecu(String code)
//	{
//		for (DASScannerListener l : listeners)
//		{
//			l.codeReceived(this, code);
//		}
//	}
//
//	public abstract int getId();
//
//	public abstract String getCode();
//
//	public abstract String getType();
//
//	public abstract void setType(String type);
//
//	public abstract void demandeCode();
//
//	public abstract void setStopAsking(boolean ask);
//
//	public abstract void termineDemandeCode();
//
//	public abstract boolean getstopAsking();
//
//	public abstract String getProperty(String key);
//
//	public abstract void addProperty(String key, String value);
//	
//	
//}
