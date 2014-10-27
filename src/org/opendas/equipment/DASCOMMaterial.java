package org.opendas.equipment;


import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.awt.Event;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.JOptionPane;

import org.opendas.DASLoader;
import org.opendas.DASLog;
import org.opendas.ctrl.DASController;
import org.opendas.ext.DASFunctions;
import org.opendas.ext.PropertiesAccess;
import org.opendas.gui.DASPanel;
import org.opendas.modele.DASConfigMaterial;
import org.opendas.modele.DASGeneric;
import org.opendas.modele.DASTypeTransmitProtocol;
import org.opendas.translate.I18n;

public class DASCOMMaterial extends DASBaseMaterial
{

	protected CommPort			commPort;
	public CommPortIdentifier	portIdentifier;
	private int					speed;
	private int					dataBits;
	private int					stopBits;
	private int					parity;
	private int					flowcontrol;
	private DASController		controller;
	/*
	 * private HashMap<String, List<DASDialog>> dialogSorted = new
	 * HashMap<String, List<DASDialog>>();
	 */
	// dataReceived : data received from material by send or receive
	List<Event>					eventList		= new LinkedList<Event>();
	boolean						wasSignalled	= false;
	BufferedReader				inStream;
	PrintWriter					outStream;
	// The Timeout permit of not stay block at the listener of code if
	// we finish the current operation
	// Short timeout : quick shutting at the end of the reading but none unused loops
	// long timeout : fewer useless loops but port more used for a long time at the end of reading
	private int					timeout			= 1000;
	
	public DASCOMMaterial(DASConfigMaterial configMatCom)
	{
		super(configMatCom.getCode(), configMatCom.getConfigTypeMaterial(), configMatCom.getPort());
		this.setSpeed(Integer.parseInt(configMatCom.getConfigTypeMaterial().getSpeed()));
		this.setDataBits(Integer.parseInt(configMatCom.getConfigTypeMaterial().getDatabit()));
		this.setStopBits(Integer.parseInt(configMatCom.getConfigTypeMaterial().getStop_bit()));
		this.setParity(Integer.parseInt(configMatCom.getConfigTypeMaterial().getParity()));
		this.setFlowcontrol(Integer.parseInt(configMatCom.getConfigTypeMaterial().getFlow_control()));
	}
	/*
	 * Set and Sort the list of dialog
	 */
	public int getSpeed()
	{
		return speed;
	}

	public void setSpeed(int speed)
	{
		this.speed = speed;
	}
	
	public int getDataBits()
	{
		return dataBits;
	}
	
	public void setDataBits(int dataBits)
	{
		this.dataBits = dataBits;
	}

	public int getStopBits()
	{
		return stopBits;
	}
	
	public void setStopBits(int stopBits)
	{
		this.stopBits = stopBits;
	}

	public int getParity()
	{
		return parity;
	}
	
	public void setParity(int parity)
	{
		this.parity = parity;
	}
	
	public int getFlowcontrol()
	{
		return flowcontrol;
	}
	public void setFlowcontrol(int flowcontrol)
	{
		this.flowcontrol = flowcontrol;
	}

	/*
	 * public void setDialog(List<DASDialog> dialog) { if (dialog != null) {
	 * List<DASDialog> tmpD = new LinkedList<DASDialog>(); tmpD.addAll(dialog);
	 * int priority = 0; while (tmpD.size() > 0) { priority++; for (DASDialog
	 * dia : dialog) { if (dia.getPriority() == priority) { if
	 * (dialogSorted.containsKey(dia.getName())) {
	 * dialogSorted.get(dia.getName()).add(dia); tmpD.remove(dia); } else {
	 * List<DASDialog> tmpDialogList = new LinkedList<DASDialog>();
	 * tmpDialogList.add(dia); dialogSorted.put(dia.getName(), tmpDialogList);
	 * tmpD.remove(dia); } } } } } } public void setAskDialog(String name) {
	 * logDebug("Passage par setAskDialog"); if (dialogSorted != null &&
	 * dialogSorted.containsKey(name)) { for (DASDialog dialog :
	 * dialogSorted.get(name)) { break; } } }
	 */
	public Map<String, String> send(String materialCode, List<org.opendas.modele.DASTypeTransmitProtocol> listtypetransmit) throws InterruptedException, ExecutionException
	{
		logDebug("Passing by send com");
		controller = DASPanel.getController();
		String codeEmissionValide = "";
		String codeReceptionValide = "";
		String languageUsed = "";
		for (DASTypeTransmitProtocol typetransmit : listtypetransmit)
		{
			logDebug(typetransmit.getType());
		}
		for (DASTypeTransmitProtocol typetransmit : listtypetransmit)
		{
			if (typetransmit.getType().equals("e"))
			{
				codeEmissionValide = typetransmit.getName();
				logDebug("codeEmissionValide :" + codeEmissionValide);
			}
			else if (typetransmit.getType().equals("r") && !codeEmissionValide.equals(""))
			{
				codeReceptionValide = typetransmit.getName();
				logDebug("codeReceptionValide :" + codeReceptionValide);
				if (typetransmit.getLanguage() != null)
				{
					languageUsed = typetransmit.getLanguage();
				}
				logDebug("EMISSION DATA COM OWNER : " + ("" + this.getClass()).split(" ")[1]);
				logDebug("portname : " + super.getPortName());
				try
				{
					
					try
					{
						portIdentifier = CommPortIdentifier.getPortIdentifier(super.getPortName());
					}
					catch (NoSuchPortException e)
					{
						// TODO Auto-generated catch block
						logErr("Material not connected on "+super.getPortname());
					}
					
					if (portIdentifier.isCurrentlyOwned())
					{
						logErr("Error: Port is currently in use");
					}
					else
					{
						this.commPort = portIdentifier.open(this.getClass().getName(), 100);
						if (commPort instanceof SerialPort)
						{
							SerialPort serialPort = (SerialPort) commPort;
							serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
							serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
							serialPort.enableReceiveTimeout(timeout);
							serialPort.notifyOnDataAvailable(true);
							ExecutorService execute = Executors.newSingleThreadExecutor();
							Future<Map<String, String>> future = execute.submit(new CommunicationReceptionCom(serialPort, codeReceptionValide,codeEmissionValide, languageUsed));
							// Starting of writer thread of writing
							if (!codeEmissionValide.equals(""))
							{
								Thread NetEnv = new Thread(new CommunicationEmissionCom(serialPort, codeEmissionValide));
								/* NetEnv.setPriority(Thread.MAX_PRIORITY); */
								// Start socket server in background
								NetEnv.start();
								// Datas recuperation Map<String,String>
								if (future.get() != null)
								{
									this.dataReceived.putAll(future.get());
								}
							}
						}
						else
						{
							logErr("Only serial ports are handled by this example.");
						}
						logDebug("Material : " + this.toString());
					}
					// TODO TEST OU FINAL ?
					/* setAskDialog("scanner_default"); */
				}
				catch (PortInUseException e)
				{
					e.printStackTrace();
				}
				catch (UnsupportedCommOperationException e)
				{
					e.printStackTrace();
				}
			}
		}
		return this.dataReceived;
	}

	public class CommunicationEmissionCom implements Runnable
	{

		long		y				= 0;
		SerialPort	serialPort;
		String		codeEmission	= "";
		int			duree			= 0;
		int			Cycle			= 5;
		String		str				= "";
		String		Scénario		= "Scénario1";

		public CommunicationEmissionCom(SerialPort serialPort, String codeEmission)
		{
			this.serialPort = serialPort;
			this.codeEmission = codeEmission;
		}

		public synchronized void run()
		{
			// Emission thread who are going to emit data processing and finish itself
			
			try
			{
				logDebug("Emissing data is in progres ...");
				// Ouverture du port d'écriture.
				outStream = new PrintWriter(serialPort.getOutputStream());
				// Attend le nbr de millis sélectionnées.
				Thread.sleep(10);
				// Ecriture de la donnée, on force le buffer à ce vider.
				logDebug("emission : code :" + codeEmission);
				outStream.println(codeEmission);
				outStream.flush();
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (IllegalStateException a)
			{
				a.printStackTrace();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public class CommunicationReceptionCom implements Callable<Map<String, String>>
	{

		private SerialPort	serialPort;
		int					duree			= 0;
		private boolean		fini			= false;
		private String		valueRec;
		// Expression to recept
		private String		expReception;
		private String 		codeEmission;
		ScriptEngineManager	mgr				= new ScriptEngineManager();
		ScriptEngine		javaEngine;
		String				languageUsed;
		// If codeReception isCompilable then true else exception
		boolean				isCompilable	= false;
		String				outputDataAllowed[];							// specify
		Map<String, String>	mapMat;

		public CommunicationReceptionCom(SerialPort serialPort, String expReception,String codeEmission, String languageUsed)
		{
			this.serialPort = serialPort;
			this.expReception = expReception;
			this.codeEmission = codeEmission;
			this.languageUsed = languageUsed;
			// Check if workstation can interpret codeReception if script
			if (!this.languageUsed.equals(""))
			{
				this.javaEngine = mgr.getEngineByName(languageUsed);
				if (javaEngine.getFactory().getNames().size() > 1)
				{
					for (String languageJRE : javaEngine.getFactory().getNames())
					{
						logDebug(languageJRE);
						if (languageJRE.equals(languageUsed))
						{
							this.javaEngine = mgr.getEngineByName(languageUsed);
						}
					}
				}
			}
		}

		public Map<String, String> call() throws Exception
		{
			mapMat = new HashMap<String, String>();
			String tabExpLines[];
			String receptionCodes;
			// char defined to split received datas
			String splitter = "";
			try
			{
				logDebug("Waiting response of the material ...");
				if (!expReception.equals(""))
				{
					// Code no evaluable
					if (languageUsed.equals(""))
					{
						tabExpLines = expReception.split("\n");
						// Split the first line to retrieve reception codes
						receptionCodes = tabExpLines[0];
						splitter = String.valueOf(tabExpLines[1].charAt(0));
						if (receptionCodes.contains(";"))
						{
							this.outputDataAllowed = receptionCodes.split(";");
							logDebug("list of values asked for reception");
						}else{
							this.outputDataAllowed = new String[1];
							this.outputDataAllowed[0] = receptionCodes;
							logDebug(this.outputDataAllowed.toString());
							logDebug("value asked for reception");
						}			
					}else{
							isCompilable = true;
					}
				}
				inStream = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
				while (fini == false)
				{
					try
					{
						valueRec = inStream.readLine();
						if (valueRec.equals(null))
						{
							outStream.close();
							inStream.close();
							serialPort.close();
						}
						else
						{
							if(!valueRec.equals(codeEmission)){
								if (isCompilable)
								{
									// Initialization for script evaluation
									javaEngine.put("mapMat", mapMat);
									javaEngine.put("values", valueRec);
									// Evaluation of the script from expReception
									javaEngine.eval(expReception);
								}
								else if (outputDataAllowed.length > 0)
								{
									String result[] = valueRec.split("[" + splitter + "]");
									for (int i = 0; i < outputDataAllowed.length; i++)
									{
										mapMat.put(outputDataAllowed[i], result[i]);
									}
								}
							}
							fini = true;
							if (mapMat.size() <= 0)
							{
								logErr("Neither data finded with values asked for reception");
							}
						}
					}
					catch (IOException e)
					{
						// Quand le flux est vide, on attend qu'il se remplisse.
						try
						{
							Thread.sleep(0);
						}
						catch (InterruptedException ex)
						{
							fini = true;
						}
					}
				}
			}catch(IOException e){}
			return mapMat;
		}
	}
	public void receive(List<org.opendas.modele.DASTypeTransmitProtocol> listtypetransmit,String background,String sequence,DASController control) throws InterruptedException, UnknownHostException, IOException, NoSuchPortException{
		String codeRec="";
		String port;
		String languageUsed="";
		int[] serialParams = new int[5];
		logDebug("RECEPTION DATA OWNER SUR " + this.getCode() + ("" + this.getClass()).split(" ")[1]);
		// If codeReception != "" material is configurable launched in
		// background
		// so we specify a listen only with the portNumber
		if (listtypetransmit.size() > 0)
		{
			for (DASTypeTransmitProtocol typetransmit : listtypetransmit)
			{
				if (typetransmit.getType().equals("r"))
				{
					codeRec = typetransmit.getName();
					languageUsed = typetransmit.getLanguage();
				}
			}
		}
		else
		{
			logErr("Neither type transmit protocol" + "\n");
		}
		port = this.getPortname();
		logDebug("port:" + port + "codeReception:" + codeRec);
		serialParams[0] = this.getFlowcontrol();
		serialParams[1] = this.getSpeed();
		serialParams[2] = this.getDataBits();
		serialParams[3] = this.getStopBits();
		serialParams[4] = this.getParity();
		
		Thread NetRec = new Thread(new Updater(port, serialParams, codeRec, background, sequence, control, this.getCode(), languageUsed));
		NetRec.start();
	}

	public static class Updater implements Runnable
	{
		private String			mat;
		// listener port to receive data
		private String			port		= "";
		// codeReception permit test receive data
		private String			codeReception;
		// if background : 1 -> not returned value thread never stopped
		// if background : 0 -> returned value thread wait a value then stops
		private String			background;
		// parameter required for actionBtn and showSequence
		private String			sequence;
		// permit to access at actionBtn and showSequence to launch
		// keyboardPanelView when a data is received
		private DASController	control;
		private int[]			serialParams;
		private String			languageUsed;
		CommPortIdentifier		ident;
		String					portName;
		SerialPort				serialPort	= null;

		public Updater(String portName, int[] serialParams, String codeReception, String background, String seq, DASController controller, String mat, String languageUsed) throws NoSuchPortException
		{
			if (this.ident == null)
			{
				try{
					this.ident = CommPortIdentifier.getPortIdentifier(portName);
				}catch(NoSuchPortException ne){
					if(DASLoader.isWarnings_on_material_on_start()){
						JOptionPane.showMessageDialog(null,I18n._("Error : Material defined in functional config not connected") + ": " + mat,"Warning", JOptionPane.WARNING_MESSAGE);
					}
				}
				
			}
			this.portName = portName;
			this.codeReception = codeReception;
			this.background = background;
			this.sequence = seq;
			this.control = controller;
			this.mat = mat;
			this.languageUsed = languageUsed;
			this.serialParams = serialParams;
		}

		public void run()
		{
			System.out.println("Pass by run :");
			try
			{
				// Thread en attente
				if (serialPort == null)
				{
						try{
							serialPort = (SerialPort) ident.open("test", 1000);
							serialPort.setFlowControlMode(serialParams[0]);
							serialPort.setSerialPortParams(serialParams[1], serialParams[2], serialParams[3], serialParams[4]);
						}catch(NullPointerException ne){
						}
				}
				ExecutorService executor = Executors.newSingleThreadExecutor();
				FutureTask<Map<String, String>> future = new FutureTask<Map<String, String>>(new ComServer(serialPort, codeReception, background, languageUsed, control, sequence,mat));
				executor.execute(future);
				try
				{
					// if result launch keyboardPanel to display received value
					if (background.equals("0"))
					{
						System.out.println("reception resul :" + future.get().toString());
						control.actionBtn(sequence);
						control.getPanel().showSequence(sequence);
						Map<String, String> map = future.get();
						Map<String, Object> context = control.getFunctional_context();
						for (Map.Entry<String, String> data : map.entrySet())
						{
							if (context.get("mapMaterial") instanceof Map<?, ?>)
							{
								Map<String, String> mapMaterial = (Map<String, String>) context.get("mapMaterial");
								System.out.println(mapMaterial.toString());
								if (mapMaterial.containsKey(data.getKey()))
								{
									mapMaterial.put(data.getKey(), data.getValue());
								}
								else
								{
									mapMaterial.put(data.getKey(), data.getValue());
								}
								control.getFunctional_context().put("mapMaterial", mapMaterial);
								if (data.getKey().equals(control.getDataModelEnCours().getCode().toUpperCase()))
								{
									System.out.println(data.getValue());
									mat_inputs.put(mat, data.getValue());
								}
							}
							else
							{
								Map<String, String> mapMaterial = new HashMap<String, String>();
								mapMaterial.put(data.getKey(), data.getValue());
								control.getFunctional_context().put("mapMaterial", mapMaterial);
								if (data.getKey().equals(control.getDataModelEnCours().getCode().toUpperCase()) || data.getKey().substring(0, data.getKey().length() - 1).equals(control.getDataModelEnCours().getCode().toUpperCase()))
								{
									System.out.println(data.getValue());
									mat_inputs.put(mat, data.getValue());
								}
							}
						}
					}
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (ExecutionException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (NullPointerException e)
				{}
			}
			catch (PortInUseException e)
			{
				DASLog.logErr(getClass().getSimpleName(),String.format(I18n._("Port '%s' is already use by other connection"),portName));	
			}
			catch (UnsupportedCommOperationException e)
			{
			}
		}
	}

	private void logDebug(String log)
	{
		DASLog.logDebug(getClass().getSimpleName(), log);
	}

	private void logErr(String log)
	{
		DASLog.logErr(getClass().getSimpleName(), log);
	}

	public String toString()
	{
		return "DASCOMMaterial [commPort=" + commPort + ", dataBits=" + dataBits + ", parity=" + parity + ",  speed=" + speed + ", stopBits=" + stopBits;
	}
}

class ComServer implements Callable<Map<String, String>>
{
	BufferedReader		inStream;
	PrintStream			outStream;
	DASController		control;
	String				sequence;
	String				result;
	String 				mat;
	boolean				fini = true;
	ScriptEngine		javaEngine;
	ScriptEngineManager	mgr;
	String				languageUsed;
	String				background;
	String				codeReception;
	String				outputDataAllowed[];	// specify the output allowed
	Map<String, String>	tmp;
	
	public ComServer(SerialPort connexion, String codeReception, String background, String languageUsed, DASController control, String sequence,String mat)
	{
		mgr = new ScriptEngineManager();
		if (languageUsed != null)
		{
			javaEngine = mgr.getEngineByName(languageUsed);
		}
		this.background = background;
		this.codeReception = codeReception;
		logDebug("codeReception :" + codeReception);
		
		try
		{
			// Ouverture des flux d'entrée sorties.
			inStream = new BufferedReader(new InputStreamReader(connexion.getInputStream()));
			outStream = new PrintStream(connexion.getOutputStream());
			this.control = control;
			this.sequence = sequence;
			this.mat = mat;
		}
		catch (IOException e)
		{
			connexion.close();
		}
	}

	public Map<String, String> call() throws Exception
	{
		// If codeReception isCompilable then true else exception
		boolean isCompilable = false;
		String tabExpLines[];
		// codeReception
		String codeRec;
		// test entrée sortie
		String valueRec;
		String splitter = "";
		try
		{
			tmp = new HashMap<String, String>();
			if (!codeReception.equals(""))
			{
				// Code no evaluable
				if (languageUsed == null || languageUsed.equals(""))
				{
					try
					{
						tabExpLines = codeReception.split("\n");
						codeRec = tabExpLines[0];
						if (tabExpLines.length >= 2)
						{
							splitter = String.valueOf(tabExpLines[1].charAt(0));
						}
					}
					catch (Exception ex)
					{
						codeRec = codeReception;
					}
					if (codeRec.contains(";") && !splitter.equals(""))
					{
						this.outputDataAllowed = codeRec.split(";");
					}
					else
					{
						this.outputDataAllowed = new String[1];
						this.outputDataAllowed[0] = codeRec;
					}
				}
				else
				{
					isCompilable = true;
				}
			}
			// While data in receive
			while (fini == true)
			{
				// Reading enter flow
				valueRec = inStream.readLine();
				logDebug("valueRec :" + valueRec);
				if (valueRec == null)
				{
					// If neither data from reception, stop the reception
					outStream.println(valueRec);
				}
				else
				{
					try
					{
						// if codeReception is compilable code
						if (isCompilable)
						{
							javaEngine.put("mapMaterial", tmp);
							javaEngine.put("values", valueRec);
							javaEngine.eval(codeReception);
							logDebug("tmp :" + tmp);
						}
						else if (outputDataAllowed.length > 0 && !splitter.equals(""))
						{
							String result[] = valueRec.split("[" + splitter + "]");
							if(result.length == outputDataAllowed.length){
								for (int i = 0; i < outputDataAllowed.length; i++)
								{
									tmp.put(outputDataAllowed[i], result[i]);
								}
							}else{
								for (int i = 0; i < outputDataAllowed.length; i++)
								{
									tmp.put(outputDataAllowed[i], result[0]);
								}
							}
						}
						else
						{
							tmp.put(outputDataAllowed[0], valueRec);
						}
						if (background.equals("0"))
						{
							fini = false;
						}else{
							logDebug("dataModelEnCours :" + control.getDataEnCours());
							actionScan(this.outputDataAllowed,valueRec);
						}
					}
					catch (NullPointerException ne)
					{
						logDebug("dataModelEnCours :" + control.getDataEnCours());
						actionScan(this.outputDataAllowed,valueRec);
					}
				}
				if (tmp.size() <= 0)
				{
					logErr("Neither information to return");
				}
				outStream.println(valueRec);
			}
		}
		catch (IOException e)
		{
			if(DASLoader.isWarnings_on_material_disconnect()){
				JOptionPane.showMessageDialog(null,I18n._("Material disconnected") + ": "+mat, "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
		// TODO Auto-generated method stub
		return tmp;
	}

	//test if disable condition of procedure is true or false with current params 
	public boolean checkDisable(String seq,DASFunctions fctParams){
		
		logDebug("Pass by checkDisable");
		
		boolean rep = false;
		String disableCondition = String.valueOf(fctParams.fctParams_get2(seq).get("DISABLE"));
		if (!disableCondition.equals("null"))
		{
			// Formatting disable param
			String disableTab[] = disableCondition.split(",");
			disableTab[0] = disableTab[0].substring(1);
			disableTab[2] = disableTab[2].substring(0, disableTab[2].length() - 1);
			
			// Comparaison on superContext
			if (disableTab[2].equals("null"))
			{
				disableTab[2] = null;
			}
			if (disableTab[1].equals("!="))
			{
				rep = (!(((Map<String,String>)control.getSuper_context().get("mapMaterial")).get(disableTab[0].toUpperCase()) != disableTab[2]) ? true : false);
			}
			else if (disableTab[1].equals("="))
			{
				rep = (!(((Map<String,String>)control.getSuper_context().get("mapMaterial")).get(disableTab[0].toUpperCase()) == disableTab[2]) ? true : false);
			}
			else if (disableTab[1].equals("!in"))
			{ 
				if(control.getSuper_context().containsKey(disableTab[2])){
					List<DASGeneric> generics = (List<DASGeneric>)DASController.superContext.get("_groups");
					for(DASGeneric generic : generics){
						logDebug(String.valueOf(generic.getInfos().get("name")));
						rep = (!(!generic.getInfos().get("name").equals(disableTab[0]))? true : false);
						
						if(rep == true){
							break;
						}
					}
				}	
			}
			else if (disableTab[1].equals("in"))
			{ 
				List<DASGeneric> generics = (List<DASGeneric>)DASController.superContext.get(disableTab[2]);
				for(DASGeneric generic : generics){
					rep = (!(generic.getInfos().get("name").equals(disableTab[0]))? true : false);
					if(rep == true){
						break;
					}
				}		
			}
		}
		return rep;
	}
	
	private boolean validData(DASController control,String[] tabKey,String val){
		
		logDebug("Pass by validData");
		
		Map<String, String> mapMate = (Map<String, String>) control.getSuper_context().get("mapMaterial");
		boolean isValid= false;
		
		if(control.getDataEnCours() != null ){
			
			for(int i=0;i<tabKey.length;i++){
				String key = tabKey[i].toLowerCase().trim();
				//Check if data is contained in dataEnCours
				if(control.getDataEnCours().containsKey(key)){
					//list dataValid from dataEnCours
					List<String> list = (List<String>) control.getDataEnCours().get(key);
					//check if code scanned = code session en cours
					for(String dataMat : list){
						if(mapMate != null){
							if(mapMate.containsKey(key.toUpperCase())){
								//Test if valueRec = dataEnCours = mapMaterial
								if(mapMate.get(tabKey[i].trim()).equals(val) && mapMate.get(tabKey[i].trim()).equals(dataMat)){
									logDebug("ok");
									isValid = true;
									break;
								}else{
									logDebug("ko");
									isValid = false;
									continue;
								}
							}
						}
					}
				}else{
					logDebug("not data in dataEnCours");
				}
			}
		}else{
			isValid=true;
		}
		return isValid;
	}

	//Launch action on received code
	public void actionScan(String[] tabKey,String valueRec)
	{
		logDebug("Pass by actionScan");
		
		boolean isValid = false;
		DASFunctions fctParam = DASController.parserXmlFcts.getParameters();
		String val = valueRec.replace("#","");

		//if mapMaterial not exists in superContext, it's created
		if(!control.getSuper_context().containsKey("mapMaterial")){
			Map<String,String> mapMaterial = new HashMap<String,String>();
			control.getSuper_context().put("mapMaterial",mapMaterial);
		}
		// Browse sequences to extrate value disable if it exists
		for (String seq : DASController.sequenceListSorted)
		{
			boolean rep = checkDisable(seq, fctParam);
			if(rep == false)
			{
				logDebug(" -> invalid condition");
			}
			else if (rep == true)
			{
				logDebug(" -> valid condition");
				Map<String,String> mapMater = (Map<String,String>)control.getSuper_context().get("mapMaterial");
				if(control.getDataEnCours() != null && mapMater.size() > 0){
					if(control.getSuper_context().get("mapMaterial") instanceof Map<?,?> ){
						isValid = validData(control, tabKey, val);
						logErr("isValid ="+ String.valueOf(isValid));
					}
				}else{
					isValid = true;
				}
				
				if(isValid == true){
					logDebug("data received valid");
					control.initActionBtn(seq,false);
					control.nextActionBtn();
					control.getPanel().showSequence(seq);
					control.getPanel().codeSend(val,1);
					rep = false;
					
					//if valueRec present in mapmaterial
					if(((Map<String,String>)control.getSuper_context().get("mapMaterial")).containsValue(val)){
						logDebug("deleting of mapMaterial");
						control.getSuper_context().remove("mapMaterial");
					}else{
						Map<String,String> mapMat = (Map<String, String>) control.getSuper_context().get("mapMaterial");
						//Fill mapMaterial
						for(int i=0;i<tabKey.length;i++){
							mapMat.put(tabKey[i], val);
						}
						control.getSuper_context().put("mapMaterial",mapMat);
					}
				}else{
					logErr("code received not valid");
				}
				break;
			}
		}
	}
	
	private void log(String log)
	{
		DASLog.log(getClass().getSimpleName(), log);
	}

	private void logDebug(String log)
	{
		DASLog.logDebug(getClass().getSimpleName(), log);
	}

	private void logErr(String log)
	{
		DASLog.logErr(getClass().getSimpleName(), log);
	}
}


