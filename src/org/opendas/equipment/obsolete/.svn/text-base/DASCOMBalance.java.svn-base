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
//import java.io.DataInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.TooManyListenersException;
//import java.util.concurrent.TimeoutException;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.opendas.DASLog;
//import org.opendas.ctrl.DASController;
//import org.opendas.ext.DASConnexion;
//import org.opendas.modele.DASDialog;
//
///**
// * 
// * @author hloiret
// * 
// */
//public class DASCOMBalance extends DASACBalance
//{
//
//	protected CommPort							commPort;
//	private int									id;
//	private String								code;
//	private String								type;
//	private int									speed;
//	private int									dataBits;
//	private int									stopBits;
//	private int									parity;
//	private String								portName;
//	private boolean								stopAsking		= false;
//	private String								dataReceived;
//	public CommPortIdentifier					portIdentifier;
//	private Map<String, String>					propertyMap		= new HashMap<String, String>();
//	private int									timeout			= 1000;
//	private List<DASDialog>						dialog;
//	private SerialPort							serialPort;
//	private DASDialog							rcvDialog;
//	private DASController						controller;
//	private DASDialog							askDialog;
//	private boolean								isNoGoodData	= false;
//	private HashMap<String, List<DASDialog>>	dialogSorted	= new HashMap<String, List<DASDialog>>();
//
//	/*
//	 * Set and Sort the list of dialog
//	 */
//	public void setDialog(List<DASDialog> dialog)
//	{
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
//		this.dialog = dialog;
//	}
//
//	public DASCOMBalance()
//	{
//	}
//
//	@Override
//	public void annulerPesee()
//	{
//		stopAsking = true;
//	}
//
//	public void demandePoids()
//	{
//		logDebug("DEMANDE POIDS OWNER : " + ("" + this.getClass()).split(" ")[1]);
//		try
//		{
//			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(this.getPortName());
//			if (portIdentifier.isCurrentlyOwned())
//			{
//				logErr("Port " + portIdentifier.getName() + " is currently in use");
//			} else
//			{
//				this.commPort = portIdentifier.open(this.getClass().getName(), 2000);
//				if (commPort instanceof SerialPort)
//				{
//					serialPort = (SerialPort) commPort;
//					serialPort.setSerialPortParams(this.getSpeed(), this.getDataBits(), this.getStopBits(), this.getParity());
//					InputStream in = serialPort.getInputStream();
//					dataReceived = null;
//
//					serialPort.addEventListener(new SerialReader(in, this));
//					serialPort.notifyOnDataAvailable(true);
//					serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN);
//					serialPort.enableReceiveTimeout(timeout);
//					serialPort.enableReceiveThreshold(0);
//
//				} else
//				{
//					logErr("Only serial ports are handled by this example.");
//				}
//				logDebug("Balance : " + this.toString());
//			}
//			//TODO TEST OU FINAL ?
//			setAskDialog("balance_default");
//
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
//		for(DASDialog dialog : dialogSorted.get(name)){
//			askDialog = dialog;
//			break;
//		}
//
//		rcvDialog = askDialog;
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
//					if(dialog.getSend() == 1)
//					{
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
//					}
//					else
//					{
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
//	/**
//	 * La classe qui permet de lire et de parser la réponse de la balance.
//	 * 
//	 * @author hloiret
//	 * 
//	 */
//	public static class SerialReader implements SerialPortEventListener
//	{
//
//		InputStream		in;
//		DASCOMBalance	dasBalance; // pour les notifications
//
//		public SerialReader(InputStream in, DASCOMBalance dasBalance)
//		{
//			this.in = in;
//			this.dasBalance = dasBalance;
//		}
//
//		public void serialEvent(SerialPortEvent event){
//			
//			/*logDebug("Passage par serialEvent");
//			
//			if (!dasBalance.stopAsking)
//			{
//				logDebug("EVENEMENT : " + "DATA AVAILABLE : " + SerialPortEvent.DATA_AVAILABLE + " , PROCESSING  : "+ !dasBalance.stopAsking);
//				BufferedReader bufRead = new BufferedReader(new InputStreamReader(this.in));
//				if(dasBalance.rcvDialog.equals(null)){
//					dasBalance.rcvDialog = dasBalance.askDialog;
//				}
//				int timerRate = 100,timeElapsed = 0, waitingSecond = dasBalance.timeout;
//				boolean timerOver = false;
//				while (timerOver == false && dasBalance.rcvDialog == null)
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
//				if(dasBalance.rcvDialog != null){
//					if(dasBalance.rcvDialog.getSend() == 0){
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
//						String waitedHeader = (String) dasBalance.rcvDialog.getHeader_code();
//						logDebug("Waited Header => "+waitedHeader);
//
//						dasBalance.dataReceived = null;
//
//						if(!tmpDr.contains(waitedHeader)){
//							logDebug("Not Good Data Received");
//							dasBalance.isNoGoodData = true;
//						}else{
//							dasBalance.dataReceived = tmpDr.replace(waitedHeader, "");
//							dasBalance.isNoGoodData = false;
//						}
//
//						if(dasBalance.dataReceived != null){
//							String tmpSRD = dasBalance.rcvDialog.getSendReceiveData();
//							logDebug("Parse : "+tmpSRD);
//							if(tmpSRD != null){
//								dasBalance.dataReceived = processingData(dasBalance.dataReceived, tmpSRD);
//							}
//
//							if (dasBalance.dataReceived != null)
//							{
//								logDebug("Received : "+dasBalance.dataReceived);
//								if(dasBalance.controller.getPanel().isEnabled()){ //TODO CAN BE BETTER
//									dasBalance.dispatchReceptionPoids(dasBalance.dataReceived);
//								}
//							}
//							dasBalance.rcvDialog = dasBalance.askDialog;	
//						}
//					}
//				}else{
//					try
//					{
//						dasBalance.dataReceived = ""+bufRead.readLine();
//					}
//					catch (IOException e)
//					{
//						e.printStackTrace();
//					}
//					logDebug("No Process Data : "+dasBalance.dataReceived);
//					dasBalance.dispatchReceptionPoids(dasBalance.dataReceived);
//				}
//			}*/
//		}
//
//		private String processingData(String data, String dataParser)
//		{
//			/*data = data.trim();
//			Double pesee = -666.666;
//			if(dataParser.contains(",")){
//				String[] dPtmp = dataParser.split(",");
//				for(int i=0;i<dPtmp.length;i++){
//					if(dPtmp[i].contains("DASB")){
//						parseData(data,dPtmp[i]);
//					}else{
//						pesee = parseData(data,dPtmp[i]);
//					}
//				}
//			}else{
//				pesee = parseData(data,dataParser);
//			}
//
//			if(pesee != -666.666){
//				return pesee.toString();
//			}*/
//			return null;
//		}
//		
//		private void setValueToVariable(String variablename, String variabledata){
//			/*if(dasBalance.controller.getDataEnCours().containsKey(variablename)){
//				dasBalance.controller.getDataEnCours().remove(variablename);
//			}
//			List<String> tmpList = new LinkedList<String>();
//			tmpList.add(variabledata);
//			dasBalance.controller.getDataEnCours().put(variablename, tmpList);
//			//dasBalance.controller.getPanel().updateKeyboardView(variablename, variabledata);*/
//		}
//		
//		private double parseData(String data, String dataParser){
//			/*Pattern p;
//			Matcher m;
//
//			if(dataParser.contains("DASB")){
//				//DASB{Poids:rcvseq=(\d*))
//				Pattern patternParser = Pattern.compile("DASB\\{(.*?)\\}");
//				Matcher matcherParser = patternParser.matcher(dataParser);
//				List<String> paramFormatDataList = new LinkedList<String>();
//				while (matcherParser.find())
//				{
//					paramFormatDataList.add(matcherParser.group().replace("DASB{", "").replace("}", ""));
//				}
//
//				for(String paramFormatData : paramFormatDataList){
//					String dataTmp = data;
//					logDebug(paramFormatData);
//					String vTmp = paramFormatData.split(":")[0];
//					String dPTmp = paramFormatData.split(":")[1];
////					logDebug("SourceFull : "+ dataTmp);
////					logDebug("DataParser : "+ dPTmp);
//					logDebug("-Variable : "+ vTmp);
//					p = Pattern.compile(dPTmp);
//					m = p.matcher(data);
//					while (m.find()){
////						logDebug("m0 => "+m.group(0));
////						logDebug("split0 =>"+dPTmp);
////						logDebug("m1 => "+m.group(1));
//						if (m.group(0).contains(dPTmp.split("=")[0]))
//						{
//							logDebug("-Data : "+m.group(1));
//							setValueToVariable(vTmp,m.group(1));
//							break;
//						}
//					}
//				}
//			}else{
//				String[] datasplit = data.split("/");
//				for(String datasingle : datasplit){
//					if(!datasingle.contains("=")){
//						p = Pattern.compile(dataParser);
//						m = p.matcher(datasingle);
//						if (m.matches() && m.groupCount() == 1)
//						{
//							double pesee = Double.parseDouble(m.group(1));
//							//logDebug("No Variable-Assign-Data : "+pesee);
//							return pesee;
//						}
//					}
//				}
//			}
//		 	*/
//			return 0.0;
//		}
//	
//		private void logDebug(String log)
//		{
//			DASLog.logDebug("DASCOMBalance-SerialReader", log);
//		}
//	}
//	
//	/**
//	 * 
//	 * Classe d'écriture dans le flux vers la balance
//	 * 
//	 * @author hloiret
//	 * 
//	 */
//	public static class SerialWriter implements Runnable
//	{
//
//		OutputStream	out;
//
//		public SerialWriter(OutputStream out)
//		{
//			this.out = out;
//		}
//
//		public void run()
//		{
//			try
//			{
//				OutputStreamWriter ow = new OutputStreamWriter(this.out);
//				ow.write("I2");
//				ow.flush();
//				ow.close();
//			} catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}
//
//
//	public CommPort getCommPort()
//	{
//		return commPort;
//	}
//
//
//	public void setCommPort(CommPort commPort)
//	{
//		this.commPort = commPort;
//	}
//
//
//	public int getId()
//	{
//		return id;
//	}
//
//
//	public void setId(int id)
//	{
//		this.id = id;
//	}
//
//
//	public String getCode()
//	{
//		return code;
//	}
//
//
//	public void setCode(String code)
//	{
//		this.code = code;
//	}
//
//
//	public String getType()
//	{
//		return type;
//	}
//
//
//	public void setType(String type)
//	{
//		this.type = type;
//	}
//
//
//	public int getSpeed()
//	{
//		return speed;
//	}
//
//
//	public void setSpeed(int speed)
//	{
//		this.speed = speed;
//	}
//
//
//	public int getDataBits()
//	{
//		return dataBits;
//	}
//
//
//	public void setDataBits(int dataBits)
//	{
//		this.dataBits = dataBits;
//	}
//
//
//	public int getStopBits()
//	{
//		return stopBits;
//	}
//
//
//	public void setStopBits(int stopBits)
//	{
//		this.stopBits = stopBits;
//	}
//
//
//	public int getParity()
//	{
//		return parity;
//	}
//
//
//	public void setParity(int parity)
//	{
//		this.parity = parity;
//	}
//
//
//	public String getPortName()
//	{
//		return portName;
//	}
//
//
//	public void setPortName(String portName)
//	{
//		this.portName = portName;
//	}
//
//
//	public boolean isStopAsking()
//	{
//		return stopAsking;
//	}
//
//
//	public void setStopAsking(boolean stopAsking)
//	{
//		this.stopAsking = stopAsking;
//	}
//
//
//	public String getCodeRecu()
//	{
//		return dataReceived;
//	}
//
//
//	public void setCodeRecu(String codeRecu)
//	{
//		this.dataReceived = codeRecu;
//	}
//
//
//	public CommPortIdentifier getPortIdentifier()
//	{
//		return portIdentifier;
//	}
//
//
//	public void setPortIdentifier(CommPortIdentifier portIdentifier)
//	{
//		this.portIdentifier = portIdentifier;
//	}
//
//
//	public Map<String, String> getPropertyMap()
//	{
//		return propertyMap;
//	}
//
//
//	public void setPropertyMap(Map<String, String> propertyMap)
//	{
//		this.propertyMap = propertyMap;
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
//	public boolean getstopAsking()
//	{
//		return stopAsking;
//	}
//
//	public DASController getController()
//	{
//		return controller;
//	}
//
//	public void setController(DASController controller)
//	{
//		this.controller = controller;
//	}
//
//
//	public List<DASDialog> getDialog()
//	{
//
//		return dialog;
//	}
//
//	@Override
//	public String toString()
//	{
//		return "DASCOMBalance [code=" + code + ", commPort=" + commPort + ", dataBits=" + dataBits + ", dialog=" + dialog + ", id=" + id + ", parity=" + parity + ", poidsRecu=" + dataReceived + ", portIdentifier=" + portIdentifier + ", portName=" + portName + ", propertyMap=" + propertyMap + ", speed=" + speed + ", stopAsking=" + stopAsking + ", stopBits=" + stopBits + ", timeout=" + timeout + ", type=" + type + "]";
//	}
//
//
//}
