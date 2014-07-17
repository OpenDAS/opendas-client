package org.opendas.equipment.obsolete;
//package org.opendas.equipment;
//
//import gnu.io.CommPort;
//import gnu.io.CommPortIdentifier;
//import gnu.io.NoSuchPortException;
//import gnu.io.PortInUseException;
//import gnu.io.SerialPort;
//import gnu.io.SerialPortEvent;
//import gnu.io.SerialPortEventListener;
//import gnu.io.UnsupportedCommOperationException;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.TooManyListenersException;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.opendas.DASLog;
//import org.opendas.ctrl.DASController;
//import org.opendas.modele.DASDialog;
//
///**
// * Cette classe permet d'écouter un scanner sur le port série Lorsqu'un code est
// * demandé, on écoute le port série et envoie les codes reçus jusqu'à recevoir
// * la demande de fin d'écoute
// * 
// * @author hloiret
// * @author mlaroche
// * 
// */
//public class DASCOMScanner extends DASACScanner
//{
//
//	protected CommPort			commPort;
//	private int					id;
//	private String				code;
//	private String				type;
//	private int					speed;
//	private int					dataBits;
//	private int					stopBits;
//	private int					parity;
//	private String				portName;
//	private boolean				stopAsking	= false;
//	private String				dataReceived;
//	public CommPortIdentifier	portIdentifier;
//	private Map<String, String>	propertyMap	= new HashMap<String, String>();
//	private List<DASDialog>						dialog;
//	private SerialPort							serialPort;
//	private DASDialog							rcvDialog;
//	private DASController					controller;
//	private DASDialog							askDialog;
//	private boolean								isNoGoodData	= false;
//	private HashMap<String, List<DASDialog>>	dialogSorted	= new HashMap<String, List<DASDialog>>();
//
//	// Le Timeout permet de ne pas rester bloquer à l'écoute d'un code lorsque
//	// l'on termine l'opération de lecture
//	// Timeout court : fermeture rapide à la fin de la lecture mais plus de
//	// tours inutiles
//	// Timeout long : moins de tours inutiles mais port monopolisé plus
//	// longtemps à la fin de la lecture
//	// Principe du timeout à adapter en fonction du matériel
//	private int					timeout		= 1000;
//
//	/*
//	 * Set and Sort the list of dialog
//	 */
//	public void setDialog(List<DASDialog> dialog)
//	{
//		if(dialog != null){
//		List<DASDialog> tmpD = new LinkedList<DASDialog>();
//		tmpD.addAll(dialog);
//		int priority = 0;
//		while(tmpD.size() > 0){
//			priority++;
//			for(DASDialog dia : dialog){
//				if(dia.getPriority() == priority){
//					if(dialogSorted.containsKey(dia.getName())){
//						dialogSorted.get(dia.getName()).add(dia);
//						tmpD.remove(dia);
//					}else{
//						List<DASDialog> tmpDialogList = new LinkedList<DASDialog>();
//						tmpDialogList.add(dia);
//						dialogSorted.put(dia.getName(), tmpDialogList);
//						tmpD.remove(dia);
//					}
//				}
//			}
//		}
//		}
//		this.dialog = dialog;
//	}
//
//	
//	  //Port Config
//	 
//	@Override
//	public void demandeCode()
//	{
//		logDebug("DEMANDE CODE OWNER : " + ("" + this.getClass()).split(" ")[1]);
//		// String portName = this.getPortname();
//		try
//		{
//			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
//			if (portIdentifier.isCurrentlyOwned())
//			{
//				System.out.println("Error: Port is currently in use");
//			} else
//			{
//				this.commPort = portIdentifier.open(this.getClass().getName(), 2000);
//				if (commPort instanceof SerialPort)
//				{
//					SerialPort serialPort = (SerialPort) commPort;
//					serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//					InputStream in = serialPort.getInputStream();
//					dataReceived = null;
//					serialPort.addEventListener(new SerialReader(in, this));
//					serialPort.notifyOnDataAvailable(true);
//					commPort.enableReceiveTimeout(timeout);
//				} else
//				{
//					logErr("Only serial ports are handled by this example.");
//				}
//				logDebug("Scanner : " + this.toString());
//			}
//			//TODO TEST OU FINAL ?
//			setAskDialog("scanner_default");
//		} catch (PortInUseException e)
//		{
//			e.printStackTrace();
//		} catch (UnsupportedCommOperationException e)
//		{
//			e.printStackTrace();
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		} catch (TooManyListenersException e)
//		{
//			e.printStackTrace();
//		} catch (NoSuchPortException e)
//		{
//			e.printStackTrace();
//		}
//	}
//	
//	public void setAskDialog(String name){
//		if(dialogSorted != null && dialogSorted.containsKey(name)){
//		for(DASDialog dialog : dialogSorted.get(name)){
//			askDialog = dialog;
//			break;
//		}
//
//		rcvDialog = askDialog;
//		}
//
//	}
//
//	public void sendDialog(String name){
//
//		if(dialogSorted.containsKey(name)){
//
//			for(DASDialog dialog : dialogSorted.get(name)){
//				rcvDialog = null;
//				String sended = null;
//				dataReceived = null;
//				try{
//					if(dialog.getSend() == 1){
//						rcvDialog = null;
//						//Send data with variable
//						if(dialog.getSendReceiveData().contains("DASB")){
//							String tmpSRD = dialog.getSendReceiveData();
//
//							Pattern pattern = Pattern.compile("DASB\\{(.*?)\\}");
//							Matcher matcher = pattern.matcher(tmpSRD);
//							while (matcher.find())
//							{
//								String paramFormatDataFull = matcher.group();
//								String paramFormatData = matcher.group().replace("DASB{", "").replace("}", "");
//								logDebug(paramFormatData);
//
//								String tmpVariable = null;
//								String tmpNbChar = null;
//								String tmpLeftRight = null;
//								String tmpCharToPut = null;
//
//								String[] tmpParams = paramFormatData.split(":");
//								if(tmpParams.length == 1){
//									//Get only variable //tmpVariable = paramFormatData.split(":")[0];
//									if(controller.getDataEnCours().containsKey(paramFormatData.split(":")[0])){
//										tmpVariable = controller.getDataEnCours().get(paramFormatData.split(":")[0]).get(0);
//										tmpSRD = tmpSRD.replace(paramFormatDataFull, tmpVariable);
//									}else{
//										tmpSRD = tmpSRD.replace(paramFormatDataFull, "");
//									}
//								}else{
//									//Get all parameters for match rules
//									for(int i=0; i< tmpParams.length; i++){
//										//logDebug(i+" => "+paramFormatData.split(":")[i]);
//										if(i == 0){
//											//Variable //tmpVariable = paramFormatData.split(":")[i];
//											if(controller.getDataEnCours().containsKey(paramFormatData.split(":")[0])){
//												tmpVariable = controller.getDataEnCours().get(paramFormatData.split(":")[0]).get(0);
//											}else{
//												tmpVariable = "";
//											}
//										}else if(i == 1){
//											//NB CHAR
//											tmpNbChar = paramFormatData.split(":")[i];
//										}else if(i == 2){
//											//LEFT|RIGHT
//											tmpLeftRight = paramFormatData.split(":")[i];
//										}else if(i == 3){
//											//CHAR TO PUT
//											tmpCharToPut = paramFormatData.split(":")[i];
//										}											
//									}
//									if(Integer.parseInt(tmpNbChar) != 0){
//										int tVl = tmpVariable.length();
//										if(tmpVariable.length() != Integer.parseInt(tmpNbChar)){
//											if(tmpLeftRight.equals("left")){
//												for(int i = 0;i < (Integer.parseInt(tmpNbChar)-tVl); i++){
//													tmpVariable = tmpCharToPut + tmpVariable;
//												}
//											}else {
//												for(int i = 0;i < (Integer.parseInt(tmpNbChar)-tVl); i++){
//													tmpVariable = tmpVariable + tmpCharToPut;
//												}
//											}
//											tmpSRD = tmpSRD.replace(paramFormatDataFull, tmpVariable);
//										}
//									}
//								}
//							}
//							sended = tmpSRD+'\n';
//							logDebug("SEND : "+tmpSRD);
//						}else{
//							sended = dialog.getSendReceiveData()+'\n';
//							logDebug("SEND : "+dialog.getSendReceiveData());
//						}
//
//						byte[] bytearray = sended.getBytes();
//						OutputStream outputStream = serialPort.getOutputStream();
//						outputStream.write(bytearray);
//						outputStream.flush();
//
//						// Don't overflow the serialport
//						int timerRate = 100,timeElapsed = 0, waitingSecond = dialog.getWaitingSecond();
//						if(waitingSecond==0){
//						}else{
//							boolean timerOver = false;
//							while (true && timerOver == false)
//							{
//								try
//								{ 
//									Thread.sleep(timerRate);
//								}
//								catch (InterruptedException ioe) 
//								{
//									continue;
//								}
//								synchronized ( this )
//								{
//									timeElapsed += timerRate;
//									if (timeElapsed > waitingSecond)
//									{
//										timerOver = true;
//									}
//								}
//							}
//							rcvDialog = null;
//						}
//
//
//					}else{
//						// RECEIVED DATA
//
//						int timerRate = 100,timeElapsed = 0, waitingSecond = dialog.getWaitingSecond();
//						if(waitingSecond==0){
//						}else{
//							rcvDialog = dialog;
//							boolean timerOver = false;
//							while ((timerOver == false && (dataReceived == null || isNoGoodData)))
//							{
//								try
//								{
//									Thread.sleep(timerRate);
//								}
//								catch (InterruptedException ioe) 
//								{
//									continue;
//								}
//								synchronized ( this )
//								{
//									timeElapsed += timerRate;
//									if (timeElapsed > waitingSecond)
//									{
//										timerOver = true;
//									}
//								}
//							}
//
//							if((dialog.getSend() == 0) && dataReceived == null){
//								logDebug("Timeout : No Data Received.");
//							}	
//							rcvDialog = askDialog;
//						}
//					}
//				}catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}else{
//			logDebug("Dialog not found");
//		}
//	}
//
//	@Override
//	public void termineDemandeCode()
//	{
//		stopAsking = true;
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
//	public String getCode()
//	{
//		return this.code;
//	}
//
//	public void setCode(String code)
//	{
//		this.code = code;
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
//	private String getPortname()
//	{
//		return this.portName;
//	}
//
//	@Override
//	public void setType(String type)
//	{
//		this.type = type;
//	}
//
//	@Override
//	public String getType()
//	{
//		return this.type;
//	}
//
//	public boolean getstopAsking()
//	{
//		return this.stopAsking;
//	}
//
//	@Override
//	public void setStopAsking(boolean ask)
//	{
//		// TODO Auto-generated method stub
//		this.stopAsking = ask;
//	}
//
//	/**
//	 * La classe qui permet de lire et de parser la réponse du scanner
//	 * 
//	 * @author hloiret
//	 * 
//	 */
///*
//	public static class SerialReader implements SerialPortEventListener
//	{
//
//		InputStream		in;
//		DASCOMScanner	dasScanner; // pour les notifications
//
//		public SerialReader(InputStream in, DASCOMScanner dasScanner)
//		{
//			this.in = in;
//			this.dasScanner = dasScanner;
//		}
//
//		@Override
//		public void serialEvent(SerialPortEvent event)
//		{
////			logDebug("EVENEMENT : " + "DATA AVAILABLE : " + SerialPortEvent.DATA_AVAILABLE + " , PROCESSING  : "+ !dasBalance.stopAsking);
////			dasScanner.dataReceived = null;
////			if (!dasScanner.stopAsking)
////			{
////				BufferedReader bufRead = new BufferedReader(new InputStreamReader(this.in));
////				try
////				{
////					dasScanner.dataReceived = bufRead.readLine();
////					if (dasScanner.dataReceived.length() > 0)
////					{
////						System.out.println("Code Reçu");
////						// codeRecu = "C4000065";
////						// dasScanner.stopAsking = true;
////						// this.dasScanner.dispatchCodeRecu(codeRecu.substring(1));
////					}
////				} catch (IOException e)
////				{
////					e.printStackTrace();
////				}
////				System.out.println("DASCOMSCANNER : Code Reçu :" + dasScanner.dataReceived);
////				if (dasScanner.dataReceived != null)
////				{
////					dasScanner.dispatchCodeRecu(dasScanner.dataReceived.substring(1));
////				}
////
////			}
//
//			if (!dasScanner.stopAsking)
//			{
//				logDebug("EVENEMENT : " + "DATA AVAILABLE : " + SerialPortEvent.DATA_AVAILABLE + " , PROCESSING  : "+ !dasScanner.stopAsking);
//				BufferedReader bufRead = new BufferedReader(new InputStreamReader(this.in));
//				if(dasScanner.rcvDialog.equals(null)){
//					dasScanner.rcvDialog = dasScanner.askDialog;
//				}
//				int timerRate = 100,timeElapsed = 0, waitingSecond = dasScanner.timeout;
//				boolean timerOver = false;
//				while (timerOver == false && dasScanner.rcvDialog == null)
//				{
//					try
//					{
//						Thread.sleep(timerRate);
//					}
//					catch (InterruptedException ioe) 
//					{
//						continue;
//					}
//					synchronized ( this )
//					{
//						timeElapsed += timerRate;
//						if (timeElapsed > waitingSecond)
//						{
//							timerOver = true;
//						}
//					}
//				}
//				if(dasScanner.rcvDialog != null){
//					if(dasScanner.rcvDialog.getSend() == 0){
//
//						String tmpDr = "";
//						try
//						{
//							tmpDr = ""+bufRead.readLine();
//						}
//						catch (IOException e)
//						{
//							e.printStackTrace();
//						}
//						logDebug("bufRead/ReadLine => "+tmpDr);
//
//						String waitedHeader = (String) dasScanner.rcvDialog.getHeader_code();
//						logDebug("Waited Header => "+waitedHeader);
//
//						dasScanner.dataReceived = null;
//
//						if(!tmpDr.contains(waitedHeader)){
//							logDebug("Not Good Data Received");
//							dasScanner.isNoGoodData = true;
//						}else{
//							dasScanner.dataReceived = tmpDr.replace(waitedHeader, "");
//							dasScanner.isNoGoodData = false;
//						}
//
//						if(dasScanner.dataReceived != null){
//								logDebug("Received : "+dasScanner.dataReceived.substring(3));
//								dasScanner.dispatchCodeRecu(dasScanner.dataReceived.substring(3));
//							dasScanner.rcvDialog = dasScanner.askDialog;	
//						}
//					}
//				}else{
//					try
//					{
//						dasScanner.dataReceived = ""+bufRead.readLine();
//					}
//					catch (IOException e)
//					{
//						e.printStackTrace();
//					}
//					logDebug("No Process Data : "+dasScanner.dataReceived);
//					dasScanner.dispatchCodeRecu(dasScanner.dataReceived);
//				}
//			}
//			
//		}
//	
//	
//		private void logDebug(String log)
//		{
//			DASLog.logDebug("DASCOMScanner-SerialReader", log);
//		}
//	
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
//	// recupere une propriete
//	@Override
//	public String getProperty(String key)
//	{
//		return propertyMap.get(key);
//	}
//
//	// ajout nouvelle propriete scanner
//	@Override
//	public void addProperty(String key, String value)
//	{
//		propertyMap.put(key, value);
//	}
//	
//	@Override
//	public String toString()
//	{
//		return "DASCOMScanner [commPort=" + commPort + ", dataBits=" + dataBits + ", parity=" + parity + ", portName=" + portName + ", speed=" + speed + ", stopBits=" + stopBits + ", type=" + type + "]";
//	}
//
//}
//*/
//// /**
//// *
//// */
//// public void run()
//// {
//// dasScanner.codeRecu = null;
//// dasScanner.stopAsking = false;
//// BufferedReader bufRead = new BufferedReader(new InputStreamReader(this.in));
//// while (!dasScanner.stopAsking)
//// {
//// try
//// {
//// dasScanner.codeRecu = bufRead.readLine();
//// if (dasScanner.codeRecu.length() > 0)
//// {
//// System.out.println("Code Reçu");
//// // codeRecu = "C4000065";
//// dasScanner.stopAsking = true;
//// // this.dasScanner.dispatchCodeRecu(codeRecu.substring(1));
//// }
//// }
//// catch (IOException e)
//// {}
//// }
//// // on libere le port
//// dasScanner.commPort.close();
//// //dasScanner.portIdentifier. removePortOwnershipListener(arg0)
//// System.out.println("Cloture du port serie, code recu :" +
//// dasScanner.codeRecu);
//// if (dasScanner.codeRecu != null)
//// dasScanner.dispatchCodeRecu(dasScanner.codeRecu.substring(1),
//// dasScanner.thread);
//// }
//
//// portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
//// if (portIdentifier.isCurrentlyOwned())
//// {
//// if(portIdentifier.getCurrentOwner().equals((""+this.getClass()).split(" ")[1])){
//// this.commPort.close();
//// this.commPort = portIdentifier.open(this.getClass().getName(), 2000);
//// stopAsking = false;
//// if (commPort instanceof SerialPort)
//// {
//// SerialPort serialPort = (SerialPort) commPort;
//// serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
//// SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//// InputStream in = serialPort.getInputStream();
//// codeRecu = null;
//// // thread = new Thread(new SerialReader(in, this));
//// // thread.start();
//// commPort.enableReceiveTimeout(timeout);
//// }
//// else
//// {
//// logErr("Only serial ports are handled by this example.");
//// }
//// logDebug("Scanner : " + this.toString());
//// }else{
//// logErr("Port is currently in use");
//// }
//// }
//// else
//// {
//// this.commPort = portIdentifier.open(this.getClass().getName(), 2000);
//// stopAsking = false;
//// if (commPort instanceof SerialPort)
//// {
//// SerialPort serialPort = (SerialPort) commPort;
//// serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
//// SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//// InputStream in = serialPort.getInputStream();
//// codeRecu = null;
//// serialPort.addEventListener(new SerialReader(in, this));
//// serialPort.notifyOnDataAvailable(true);
//// commPort.enableReceiveTimeout(timeout);
//// }
//// else
//// {
//// logErr("Only serial ports are handled by this example.");
//// }
//// logDebug("Scanner : " + this.toString());
//// }