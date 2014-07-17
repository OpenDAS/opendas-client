package org.opendas.equipment.obsolete;
//package org.opendas.equipment;
//
//import gnu.io.CommPort;
//
//import java.io.BufferedOutputStream;
//import java.io.FileOutputStream;
//
///**
// * Cette classe permet d'écouter une imprimante sur le port série
// * 
// * 
// */
//public class DASCOMPrinter extends DASACPrinter
//{
//
//	private int			id;
//	protected CommPort	commPort;
//	private int			speed;
//	private int			dataBits;
//	private int			stopBits;
//	private int			parity;
//	private String		portName;
//
//	// private boolean stopAsking = false;
//	//
//	// private int timeout = 1000;
//	public void imprimer(byte[] data)
//	{
//		try
//		{
//			FileOutputStream fos = new FileOutputStream(portName);
//			BufferedOutputStream bos = new BufferedOutputStream(fos);
//			fos.write(data, 0, data.length);
//			fos.close();
//		} catch (Exception e)
//		{
//			System.out.println("FAIL STREAM => PRINTER");
//		}
//	}
//
//	public int getSpeed()
//	{
//		return speed;
//	}
//
//	public void setSpeed(int speed)
//	{
//		this.speed = speed;
//	}
//
//	public int getDataBits()
//	{
//		return dataBits;
//	}
//
//	public void setDataBits(int dataBits)
//	{
//		this.dataBits = dataBits;
//	}
//
//	public int getStopBits()
//	{
//		return stopBits;
//	}
//
//	public void setStopBits(int stopBits)
//	{
//		this.stopBits = stopBits;
//	}
//
//	public int getParity()
//	{
//		return parity;
//	}
//
//	public void setParity(int parity)
//	{
//		this.parity = parity;
//	}
//
//	public String getPortName()
//	{
//		return portName;
//	}
//
//	public void setPortName(String portName)
//	{
//		this.portName = portName;
//	}
//
//	public int getId()
//	{
//		return this.id;
//	}
//
//	public void setId(int id)
//	{
//		this.id = id;
//	}
//
//	@Override
//	public String toString()
//	{
//		return "DASCOMPrinter [id=" + id + ", commPort=" + commPort + ", dataBits=" + dataBits + ", parity=" + parity + ", portName=" + portName + ", speed=" + speed + ", stopBits=" + stopBits + "]";
//	}
//}
