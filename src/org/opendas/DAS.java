package org.opendas;

import javax.swing.SwingUtilities;

import org.opendas.gui.DASFrame;
import org.opendas.translate.I18n;

/**
 * Main class DAS Client
 * 
 */
public class DAS
{

	public static void main(String[] args)
	{
		// Logs init
		DASLog.getInstance();
		// Configuration file load
		if (args.length > 0)
		{
			DASLoader.getInstance(args[0]);
			log(I18n._("Configuration file:") + args[0]);
		} else
		{
			/*DASLoader.getInstance("./config/opendas-client7_sensor.conf");
			log(I18n._("Default configuration file:")+ "./config/opendas-client7_sensor.conf");*/
			
			DASLoader.getInstance("./config/opendas-client.conf");
			log(I18n._("Default configuration file:")+ "./config/opendas-client.conf");
			
		}
		// Start graphical interface
		String mask = DASLoader.getMask().toUpperCase();
		if (mask.equals("None"))
		{
			// TODO Mask unknown
			logErr(String.format(I18n._("Mask '%s' is unknown"),mask));
		} else if (mask.equals(""))
		{
			logErr(String.format(I18n._("No mask defined for workstation '%s'"),DASLoader.getWorkstationCode()));
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable() {

				public void run()
				{
					DASFrame dasMainWindow = new DASFrame();
					dasMainWindow.setVisible(true);
				}
			});
			log(I18n._("Mask load"));
		}
	}

	private static void log(String log)
	{
		DASLog.log("DAS", log);
	}

	private static void logErr(String log)
	{
		DASLog.logErr("DAS", log);
	}
}
