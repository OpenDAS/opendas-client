package org.opendas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.swing.JOptionPane;

import org.opendas.ext.DASConnexion;
import org.opendas.ext.PropertiesAccess;
import org.opendas.jms.DASResponseListener;
import org.opendas.jms.MessageReceiver;
import org.opendas.jms.MessageSender;
import org.opendas.modele.DASDialog;
import org.opendas.modele.DASFunctionalConfig;
import org.opendas.modele.DASGraphicalConfig;
import org.opendas.modele.DASWorkstation;
import org.opendas.modele.WsConfig;
import org.opendas.server.ServerLog;
import org.opendas.supervision.DASSupervisionPanel.DASSupervisor;
import org.opendas.supervision.DASSupervisionTopicListener;
import org.opendas.supervision.DASSupervisionTopicProducer;
import org.opendas.translate.I18n;

/**
 * Class permitting to load required configuration at running
 */
public class DASLoader
{
	static PropertiesAccess				pa;
	static DASConnexion					conn;
	WsConfig							config;
	private static DASLoader			instance			= new DASLoader();
	public static boolean				debugMode;
	private static String				workstation_code;
	private static boolean 				loading_configuration_on_start;
	private static boolean 				warnings_on_material_on_start;
	private static boolean 				warnings_on_material_disconnect;
	private static boolean				warnings_on_button_background;
	public static String				confFile;
	private static String				mask;
	private static byte					printing;
	private static DASFunctionalConfig	fctConfig;
	private static DASGraphicalConfig	graphConfig;
	private static DASWorkstation		workstation;
	private static int					timeout;
	private static Map<String, String>	queues;

	public static DASLoader getInstance(String file)
	{
		confFile = file;
		queues = new HashMap<String, String>();
		instance.build();
		return instance;
	}

	public static DASLoader getInstance()
	{
		return instance;
	}

	/**
	 * 
	 */
	private DASLoader()
	{
	}

	public void build()
	{
		// loading of configuration file
		pa = new PropertiesAccess(confFile);
		// debug mode
		debugMode = "1".equals(pa.get("debugMode"));
		// id of physic station
		workstation_code = pa.get("workstation_id");
		// loading configuration on start
		loading_configuration_on_start = "1".equals(pa.get("loading_configuration_on_start"));
		// display/hide warning message
		warnings_on_button_background = "1".equals(pa.get("warnings_on_button_background"));
		warnings_on_material_on_start = "1".equals(pa.get("warnings_on_material_on_start"));
		warnings_on_material_disconnect = "1".equals(pa.get("warnings_on_material_disconnect"));
		timeout = Integer.parseInt(pa.get("timeout"));
		DASLog.log(getClass().getSimpleName(),"TimeOut :=> " + timeout);
		// mask used
		mask = pa.get("mask");
		// files ActiveMQ
		queues.put("Ext", pa.get("JMSPoolNameExt"));
		queues.put("Server", pa.get("JMSPoolNameServer"));
		queues.put("Client", pa.get("JMSPoolNameClient"));
		// connexions, workstation_id and WsConfig

		try
		{
			conn = DASConnexion.getInstance();
			loadConnections();
		} catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, I18n._("The ACTIVEMQ server not responding"), "Fatal Error", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}

		try
		{
			config = conn.getWsConfig(workstation_code);
			ServerLog.logErr(getClass().getSimpleName(),"ok WsConfig object");
		} catch (TimeoutException e1)
		{
			JOptionPane.showMessageDialog(null, I18n._("The DAS server not responding"), "Fatal Error", JOptionPane.WARNING_MESSAGE);
			ServerLog.logErr(getClass().getSimpleName(),"Problem WsConfig object");
			System.exit(0);
		}
		ServerLog.logDebug(getClass().getSimpleName(),config.toString());
		// workstation
		try
		{
			workstation = (DASWorkstation) conn.getFromServer("getWorkstationWithId", workstation_code);
			ServerLog.logErr(getClass().getSimpleName(),"ok Workstation object");
		} catch (TimeoutException e)
		{
			JOptionPane.showMessageDialog(null,I18n._("The DAS server not responding"), "Fatal Error", JOptionPane.WARNING_MESSAGE);
			ServerLog.logErr(getClass().getSimpleName(),"Problem workstation");
			System.exit(0);
		}
		// printing
		try
		{
			printing = Byte.valueOf(pa.get("printing"));
		} catch (NumberFormatException e)
		{
			ServerLog.log(getClass().getSimpleName(),"valeur de printing dans " + confFile + " incorrecte et remplacée par 0");
			printing = 0;
		}
		// functional config
		try
		{
			getFctConfig();
			ServerLog.logErr(getClass().getSimpleName(),"ok FctConfig");
		} catch (TimeoutException e)
		{
			JOptionPane.showMessageDialog(null,I18n._("The DAS server not responding"), "Fatal Error", JOptionPane.WARNING_MESSAGE);
			ServerLog.logErr(getClass().getSimpleName(),"Problem FctConfig");
			System.exit(0);
		}
		if (fctConfig == null)
		{
			ServerLog.log(getClass().getSimpleName(),"Configuration fonctionelle (station = " + workstation_code + " et masque = " + mask + ") introuvable");
		}
		// graphical config
		try
		{
			getGraphConfig();
			ServerLog.logErr(getClass().getSimpleName(),"ok GraphConfig");
		} catch (TimeoutException e)
		{
			JOptionPane.showMessageDialog(null,I18n._("The DAS server not responding"), "Fatal Error", JOptionPane.WARNING_MESSAGE);
			ServerLog.logErr(getClass().getSimpleName(),"Problem GraphConfig");
			System.exit(0);
		}
		if (graphConfig == null)
		{
			ServerLog.log(getClass().getSimpleName(),"Configuration graphique (station = " + workstation_code + " et masque = " + mask + ") introuvable");
			JOptionPane.showMessageDialog(null, "Configuration graphique (station = " + workstation_code + " et masque = " + mask + ") introuvable", "Fatal Error", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}

	@SuppressWarnings("unchecked")
	public static void getFctConfig() throws TimeoutException
	{
		List<DASFunctionalConfig> fcs = (List<DASFunctionalConfig>) conn.getFromServer("getFctConfigsWithWsId", workstation_code);
		for (DASFunctionalConfig fc : fcs)
		{
			if (fc.getMask().equals(mask))
			{
				fctConfig = fc;
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void getGraphConfig() throws TimeoutException
	{
		List<DASGraphicalConfig> gcs = (List<DASGraphicalConfig>) conn.getFromServer("getGraphConfigsWithWsId", workstation_code);
		for (DASGraphicalConfig gc : gcs)
		{
			if (gc.getMask().equals(mask))
			{
				graphConfig = gc;
				break;
			}
		}
	}

	static MessageReceiver receiver;
	static MessageSender sender;
	
	/**
	 * 
	 */
	private void loadConnections()
	{
		// loading of all connections
		List<String> sujets = new ArrayList<String>();
		sujets.add(getUniqueId());
		receiver = MessageReceiver.getInstance(sujets);
		receiver.setUrl(pa.get("JMSUrl"));
		receiver.setPassword(pa.get("JMSPassword"));
		receiver.setUser(pa.get("JMSUser"));
		receiver.setSubject(getSubject("Client"));
		DASResponseListener reponseListener = new DASResponseListener();
		reponseListener.setTimerLength(timeout);
		receiver.setListener(reponseListener);
		try
		{
			receiver.run();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sender = MessageSender.getInstance();
		sender.setUrl(pa.get("JMSUrl"));
		sender.setUser(pa.get("JMSUser"));
		sender.setPassword(pa.get("JMSPassword"));
		sender.setSubject(getSubject("Server"));
		sender.setTimeToLive(300);
		sender.run();
	}
	
	public static DASSupervisionTopicListener addTopicListener(String topicName, DASDialog dialog, DASSupervisor supPanel)
	{
		//return new DASSupervisionTopicListener(topicName, dialog, supPanel, pa.get("JMSUrl")); 		receiver.getConnection()
		return new DASSupervisionTopicListener(receiver.getConnection(), topicName, dialog, supPanel);
	}
	
	public static DASSupervisionTopicProducer addTopicProducer(String topicName, DASSupervisor supPanel)
	{
		return new DASSupervisionTopicProducer(sender.getConnection(), topicName);
	}

	public static String getWorkstationCode()
	{
		return workstation_code;
	}

	/**
	 * @return type of asked impression : 0=none, 1=directy, 2=report
	 *         pdf
	 */
	public static byte getPrinting()
	{
		return printing;
	}

	public WsConfig getConfig()
	{
		return config;
	}

	public static String getMask()
	{
		return mask;
	}

	public static String getGuiXml() throws TimeoutException
	{
		getGraphConfig();
		return graphConfig.getGraphical_xml();
	}

	public static String getBtnsXml()
	{
		return fctConfig.getButton_xml();
	}

	public static String getBtnsbottomXml()
	{
		return fctConfig.getButton_bottom_xml();
	}

	public static String getFctsXml() throws TimeoutException
	{
		getFctConfig();
		return fctConfig.getFunction_xml();
	}

	public static String getKeyboardXml()
	{
		return fctConfig.getKeyboard_xml();
	}

	public static String getUniqueId()
	{
		return "WS" + workstation_code + "_" + mask;
	}

	public static String getSubject(String service)
	{
		return queues.get(service);
	}

	public static DASWorkstation getWorkstation()
	{
		return workstation;
	}
	
	public static boolean debugMode() {
		return debugMode;
	}

	
	public static boolean isLoading_configuration_on_start()
	{
		return loading_configuration_on_start;
	}

	
	public static void setLoading_configuration_on_start(boolean loading_configuration_on_start)
	{
		DASLoader.loading_configuration_on_start = loading_configuration_on_start;
	}

	
	public static boolean isWarnings_on_material_on_start()
	{
		return warnings_on_material_on_start;
	}

	
	public static void setWarnings_on_material_on_start(boolean warnings_on_material_on_start)
	{
		DASLoader.warnings_on_material_on_start = warnings_on_material_on_start;
	}

	
	public static boolean isWarnings_on_material_disconnect()
	{
		return warnings_on_material_disconnect;
	}

	
	public static void setWarnings_on_material_disconnect(boolean warnings_on_material_disconnect)
	{
		DASLoader.warnings_on_material_disconnect = warnings_on_material_disconnect;
	}

	
	public static boolean isWarnings_on_button_background()
	{
		return warnings_on_button_background;
	}

	
	public static void setWarnings_on_button_background(boolean warnings_on_button_background)
	{
		DASLoader.warnings_on_button_background = warnings_on_button_background;
	}
}
