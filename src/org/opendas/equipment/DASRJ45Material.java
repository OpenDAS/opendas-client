package org.opendas.equipment;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
import org.opendas.DASLog;
import org.opendas.ctrl.DASController;
import org.opendas.gui.DASPanel;
import org.opendas.modele.DASConfigMaterial;
import org.opendas.modele.DASDialog;
import org.opendas.modele.DASTypeTransmitProtocol;
public class DASRJ45Material extends DASBaseMaterial
{
	public DASController 						controller;
	private HashMap<String, List<DASDialog>>	dialogSorted	= new HashMap<String, List<DASDialog>>();
	static ServerSocketChannel					server1;
	static String								host			= "";
	int											port			= 0;
	
	public DASRJ45Material(DASConfigMaterial configMatRj45){
		super(configMatRj45.getCode(),configMatRj45.getConfigTypeMaterial(),configMatRj45.getPort());	
	}
	/*
	 * Set and Sort the list of dialog
	 */
	public void setDialog(List<DASDialog> dialog)
	{
		
		if (dialog != null)
		{
			List<DASDialog> tmpD = new LinkedList<DASDialog>();
			tmpD.addAll(dialog);
			int priority = 0;
			while (tmpD.size() > 0)
			{
				priority++;
				for (DASDialog dia : dialog)
				{
					if (dia.getPriority() == priority)
					{
						if (dialogSorted.containsKey(dia.getName()))
						{
							dialogSorted.get(dia.getName()).add(dia);
							tmpD.remove(dia);
						}
						else
						{
							List<DASDialog> tmpDialogList = new LinkedList<DASDialog>();
							tmpDialogList.add(dia);
							dialogSorted.put(dia.getName(), tmpDialogList);
							tmpD.remove(dia);
						}
					}
				}
			}
		}
	}

	//	public void setAskDialog(String name)
	//	{
	//		logDebug("Passage par setAskDialog");
	//		if (dialogSorted != null && dialogSorted.containsKey(name))
	//		{
	//			for (DASDialog dialog : dialogSorted.get(name))
	//			{
	//				break;
	//			}
	//		}
	//	}
	// Emission/Reception data to configurable material
	
	public Map<String,String> send(String materialCode,List<org.opendas.modele.DASTypeTransmitProtocol> listtypetransmit) throws UnknownHostException, IOException, InterruptedException, ExecutionException
	{	
		logDebug("Passage by send rj45");	
		String codeEmissionValide = "";
		String codeReceptionValide ="";
		String languageUsed="";
		//For calcul eval java code		
			//Recuperation controller to access at superContext
			controller = DASPanel.getController();
				
			logDebug("************");
			for(DASTypeTransmitProtocol typetransmit: listtypetransmit){
				logDebug(typetransmit.getType());
			}
			logDebug("************");
			
			for(DASTypeTransmitProtocol typetransmit: listtypetransmit){
				if(typetransmit.getType().equals("e")){
					codeEmissionValide = typetransmit.getName();
					logDebug("codeEmissionValide :" + codeEmissionValide);
				}else if(typetransmit.getType().equals("r") && !codeEmissionValide.equals("")){
					codeReceptionValide = typetransmit.getName();
					logDebug("codeReceptionValide :" + codeReceptionValide);
					if(typetransmit.getLanguage() != null){
						languageUsed = typetransmit.getLanguage();
					}
					
					logDebug("EMISSION DATA OWNER : " + ("" + this.getClass()).split(" ")[1]);
					logDebug("portname : " + this.getPortname());
			
					host = this.getPortname().split(":")[1].replace("/","");
					port = Integer.parseInt(this.getPortname().split(":")[2]);
					
					logDebug("host :"+ host + "port :" + port);		
					try{
						Socket client = new Socket("127.0.0.1",port);
						//Socket client = new Socket(host,port);
						//Démarrage du Thread de lecture.	
						ExecutorService execute = Executors.newSingleThreadExecutor();
						Future<Map<String,String>> future = execute.submit(new CommunicationReceptionNet(client,codeReceptionValide,codeEmissionValide,languageUsed));
						//Démarrage du thread d'écriture.
						if(!codeEmissionValide.equals("")){	
							Thread NetEnv=new Thread(new CommunicationEmissionNet(client,codeEmissionValide));	
							/*NetEnv.setPriority(Thread.MAX_PRIORITY);*/
							// Start socket server in background
							NetEnv.start();
							//Datas recuperation Map<String,String>
							if(future.get() != null){
								this.dataReceived.putAll(future.get());
							}	
						}
					}catch(ConnectException ce){
						logErr("Material not connected");
						this.dataReceived = null;
					}catch(NullPointerException npe){
						logErr("Material not connected");
						this.dataReceived = null;
					}
				}
			}
		return this.dataReceived;
	}
	// Emission data to Configurable Material
	
	public class CommunicationEmissionNet implements Runnable{
		long y= 0;		
		Socket socket;
		String codeEmission="";
		int Cycle = 5;
		String str = "";
		String Scénario = "Scénario1";
		
		public CommunicationEmissionNet(Socket socket,String codeEmission){					
			this.socket=socket;	
			this.codeEmission = codeEmission;
		}
		
		public synchronized void run(){	
		//Thread d'émission qui va émettre l'ensemble des trames et se terminer.	
			try{
				logDebug("Data emission is in progress ...");

				//codeEmission is a string composed of materialName and currentFunctionName
				
				//Ouverture du port d'écriture.
				PrintWriter Du= new PrintWriter(socket.getOutputStream());	
				//Attend le nbr de millis sélectionnées.
				Thread.sleep(10);
				//Ecriture de la donnée, on force le buffer à ce vider.
				System.out.println("emission : code :" +codeEmission);
				Du.println(codeEmission);
				Du.flush();

			}catch (UnknownHostException e ){				
				e.printStackTrace();
			}catch (IOException e){				
				e.printStackTrace();
			}catch (IllegalStateException a){	
				a.printStackTrace();
			}catch (InterruptedException e){				
				e.printStackTrace();
			}		
		}
	}
	
	//Reception data to Configurable Material

	public class CommunicationReceptionNet implements /*Runnable*/Callable<Map<String,String>> {
		
		private Socket in;
		private boolean fini = true;
		private String valueRec;
		private String codeReception;
		private String codeEmission;
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine javaEngine ;
		String languageUsed ;
		//If codeReception isCompilable then true else exception
		boolean isCompilable = false;
		String outputDataAllowed[]; //specify the output allowed
		Map<String,String> mapMat;
			
		public CommunicationReceptionNet(Socket clientretour,String codeReception,String codeEmission,String languageUsed){
			this.in = clientretour;
			this.codeReception = codeReception;
			this.codeEmission = codeEmission;
			this.languageUsed = languageUsed;
			//Check if workstation can interpret codeReception if script
			if(!this.languageUsed.equals("")){
				this.javaEngine = mgr.getEngineByName(languageUsed);
				if(javaEngine.getFactory().getNames().size() > 1){
					for (String languageJRE :javaEngine.getFactory().getNames()){
						logDebug(languageJRE);
						if(languageJRE.equals(languageUsed)){
							this.javaEngine = mgr.getEngineByName(languageUsed);
						}
					}
				}
			}
		}

		public Map<String,String> call() throws Exception
		{	
			mapMat = new HashMap<String,String>();
			String tabExpLines[];
			//codeReception
			String codeRec;
			String splitter="";
			//char defined to split received datas
			
			try{			
				logDebug("En attente de réponse du materiel ...");					
				if (!codeReception.equals(""))
				{	
					logDebug(codeReception);
					//Code no evaluable
					if(languageUsed.equals("")){
						tabExpLines = codeReception.split("\n");		
						codeRec = tabExpLines[0];
						splitter = String.valueOf(tabExpLines[1].charAt(0));
						logDebug("splitter : " + splitter);
						if(codeRec.contains(";")){						
							this.outputDataAllowed = codeRec.split(";");	
							logDebug("list of values asked for reception");
						}else{
							this.outputDataAllowed = new String[1];
							this.outputDataAllowed[0] = codeRec;
							logDebug(this.outputDataAllowed.toString());
							logDebug("value asked for reception");
						}			
					}else{
							isCompilable = true;
					}
				}
							
				//Ouverture du port d'écoute.
				
				BufferedReader in1 = new BufferedReader(new InputStreamReader(in.getInputStream()));
				
				while(fini == true ){
					//Reading line emitted by the server, blocking method
					valueRec = in1.readLine();
					if(valueRec == null){
						fini = false;
						logDebug("Serveur Fermé");						
					}else{
						if(!valueRec.equals(codeEmission)){
							if(isCompilable){
								//Launching of the script evaluated
								javaEngine.put("mapMat",mapMat);
								javaEngine.put("values",valueRec);
								javaEngine.eval(codeReception);
							}else{
								String result[] = valueRec.split("["+splitter+"]");
								if(outputDataAllowed.length > 0){
									for(int i=0;i<outputDataAllowed.length;i++){					
										mapMat.put(outputDataAllowed[i],result[i]);
									}
								}
							
							}
						}
						fini = false;
					}
					if(mapMat.size() <= 0){
						logErr("Neither data finded with values asked for reception");
					}else{
						logDebug(mapMat.toString());
					}					
				}							
			}catch (NullPointerException ne){
				logErr("Exception is null");			
			}catch (IOException e ){
				e.printStackTrace();				
			}		
			return mapMat;
		}			
	}
	
	public static class CommunicationServeur implements Callable<Map<String, String>>
	{

		public boolean		fini	= true;
		public String		lue		= null;
		Socket				client;				// liaison with client
		String				codeReception;
		String 				languageUsed;
		String				background;
		BufferedReader		depuisClient;			// reception of request
		PrintWriter			versClient;			// sender of responses
		String				outputDataAllowed[];	// specify the output allowed
		Map<String, String>	tmp;

		public CommunicationServeur(Socket client, String codeReception, String background,String languageUsed)
		{
			this.client = client;
			this.codeReception = codeReception;
			this.background = background;
			this.languageUsed = languageUsed;
			
			try
			{
				// Opening of the input output flows
				depuisClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
				versClient = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
			}
			catch (IOException e)
			{
				// Si une Exception est levée, on ferme le flux client.
				try
				{
					client.close();
				}
				catch (IOException ee)
				{}
				e.printStackTrace();
			}
		}

		public Map<String, String> call() throws Exception
		{
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine javaEngine = mgr.getEngineByName(languageUsed);
			// If codeReception isCompilable then true else exception
			boolean isCompilable = false;
			String tabExpLines[];
			// codeReception
			String codeRec;
			String splitter = "";
			try
			{
				System.out.println("reception serveur ...");
				tmp = new HashMap<String, String>();
				if (!codeReception.equals(""))
				{
					// Code no evaluable
					if (languageUsed.equals("")){
						tabExpLines = codeReception.split("\n");
						codeRec = tabExpLines[0];
						splitter = String.valueOf(tabExpLines[1].charAt(0));
						if (codeRec.contains(";")){
							this.outputDataAllowed = codeRec.split(";");
							System.out.println("list of values asked for reception");
						}else{
							this.outputDataAllowed = new String[1];
							this.outputDataAllowed[0] = codeRec;
							System.out.println("value asked for reception");
						}
					}else{			
						isCompilable = true;				
					}
				}
				// While data in receive
				while (fini == true)
				{
					// Reading enter flow
					lue = depuisClient.readLine();
					if (lue == null)
					{
						// If neither data from reception, stop the reception
						versClient.println(lue);
					}
					else
					{
						try
						{
							// if codeReception is compilable code
							if(isCompilable)
							{
								javaEngine.put("mapMaterial",tmp);
								javaEngine.put("values",lue);
								javaEngine.eval(codeReception);
								System.out.println("tmp :"+ tmp);
							}else{
								String result[] = lue.split("[" + splitter + "]");
								for (int i = 0; i < outputDataAllowed.length; i++)
								{
									tmp.put(outputDataAllowed[i], result[i]);
								}
							}
							if (background.equals("0")){
								fini = false;
							}
						}catch (NullPointerException ne){
							// simple material
						}
					}
					if (tmp.size() <= 0){
						System.err.println("Aucune informations à retourner");
					}
					versClient.println(lue);
				}
			}
			catch (IOException e)
			{
				System.out.println("Fin du transfert");
			}
			// TODO Auto-generated method stub			
			return tmp;
		}
	}
		//Reception from simple material or configurable material with background = 1
		public void receive(List<org.opendas.modele.DASTypeTransmitProtocol> listtypetransmit,String background,String sequence,DASController control) throws InterruptedException, UnknownHostException, IOException
		{
			String codeRec="";
			String port;
			String languageUsed="";
			logDebug("RECEPTION DATA OWNER SUR "+ this.getCode() + ("" + this.getClass()).split(" ")[1]);
			
			//If codeReception != "" material is configurable launched in background
			//so we specify a listen only with the portNumber
			
			if(listtypetransmit.size() > 0){
				for(DASTypeTransmitProtocol typetransmit: listtypetransmit){
					if(typetransmit.getType().equals("r")){
						codeRec = typetransmit.getName();
						languageUsed = typetransmit.getLanguage();
					}
				}
			}else{
				logErr("Neither type transmit protocol" + "\n");
			}
			
			if(!codeRec.equals("")){
				port = this.getPortname().split(":")[2];
			}else{
				port = this.getPortname();
			}
			
			// Starting socket server to receive data from simple material in background
			
			logDebug("port:"+ port + "codeReception:" + codeRec);
			Thread NetRec = new Thread(new Updater(port,codeRec,background,sequence,control,this.getCode(),languageUsed));
			NetRec.start();
			//test affiche keyboard panel
		}	
	//Class permiting to generate thread to listen in reception	
	public static class Updater implements Runnable
	{
		private String mat;
		//listener port to receive data
		private int	port	= 0;
		//codeReception permit test receive data
		private String codeReception;
		//if background : 1 -> not returned value thread never stopped
		//if background : 0 -> returned value thread wait a value then stops
		private String background;
		//parameter required for actionBtn and showSequence
		private String sequence;
		//permit to access at actionBtn and showSequence to launch keyboardPanelView when a data is received
		private DASController control;
		private String languageUsed;
		public Updater(String portName,String codeReception,String background,String seq,DASController controller,String mat,String languageUsed)
		{
			this.port = Integer.parseInt(portName);
			this.codeReception = codeReception;
			this.background = background;
			this.sequence = seq;
			this.control = controller;
			this.mat = mat;
			this.languageUsed = languageUsed;
		}

		public void run()
		{
			System.out.println("Pass by run : "+ port);
			try
			{
				ServerSocketChannel server1 = ServerSocketChannel.open();
				server1.socket().bind(new InetSocketAddress(port));		
				server1.configureBlocking(false);
				while (true)
				{
					SocketChannel sc = server1.accept();
					if (sc != null)
					{
						ExecutorService executor = Executors.newSingleThreadExecutor();
						FutureTask<Map<String,String>> future = new FutureTask<Map<String,String>>(new CommunicationServeur(sc.socket(),codeReception,background,languageUsed));					
						executor.execute(future);				
						
						try
						{	
							//if result launch keyboardPanel to display received value
							if(background.equals("0")){
								System.out.println("reception result :" + future.get().toString());						
								control.actionBtn(sequence);
								control.getPanel().showSequence(sequence);
								if(future.get().containsKey(control.getDataModelEnCours().getCode().toUpperCase())){
									control.getPanel().updateKeyboardView(future.get().get(control.getDataModelEnCours().getCode().toUpperCase()),mat);
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
						}catch(NullPointerException e){
							
						}
					}
				}
			}catch (BindException be)
			{
				be.getStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		return "DASRJ45Material [rj45Port : portName=" + super.getPortName() + ", type=" + super.getType().toString() + "]";
	}
	
}










