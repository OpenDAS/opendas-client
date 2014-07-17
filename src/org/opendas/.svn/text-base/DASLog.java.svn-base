package org.opendas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opendas.gui.DASContextViewer;
import org.opendas.server.ServerLoader;

/**
 * Classe permettant de gérer les logs
 * 
 * @author chauvignef
 */
public class DASLog
{

	private static DASLog			instance	= new DASLog();
	private static DASContextViewer	viewer		= null;
	private static PrintStream logPSOut;
	private static PrintStream logPSErr;

	private DASLog()
	{
		// Redirection of logs to files

		try {
			String logout = null;
			String log_file = null; 
			if (ServerLoader.getLog_path() != null)
			{
				log_file = ServerLoader.getLog_path();
			}
			else
			{
				log_file = "./logs/";
			}
			DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			logout = "./logs/DAS_" + df.format(new Date()) + ".log";
			File rep = new File(log_file);

			if (!rep.exists()) {
			    System.out.println("Directory not found");
			    System.out.println("Create directory logs ");
			    new File(log_file).mkdir();
			}
			
			log("ServerLog", "The logs will be saved in the file " + logout);

			logPSOut = new PrintStream(new FileOutputStream(logout));
			logPSErr = new PrintStream(new FileOutputStream(logout));
			
		} catch (FileNotFoundException e) {
			logErr("ServerLog", "Error during the definition of log file");
			e.printStackTrace();
		}
	}

	public static DASLog getInstance()
	{
		return instance;
	}

	public static void setViewer(DASContextViewer v)
	{
		viewer = v;
	}

	public static void log(String className, String message)
	{
		if(logPSOut != null){
			logPSOut.println(className + ": " + message);
		}
		if (viewer != null)
			viewer.setlog(className + ": " + message + "\n");
		
		System.out.println(className + ": " + message);
	}

	public static void logDebug(String className, String message)
	{
		if (DASLoader.debugMode)
		{
			if(logPSOut != null){
				logPSOut.println(className + ": " + message);
			}	
			if (viewer != null)
				viewer.setlog(className + ": " + message + "\n");
			
			System.out.println(className + ": " + message);
		}
	}

	public static void logErr(String className, String message)
	{
		if(logPSOut != null){
			logPSOut.println(className + ": " + message);
		}	
		if (viewer != null)
			viewer.setlog("[ERREUR] " + className + ": " + message + "\n");
		
		System.err.println("[ERREUR] " + className + ": " + message);
	}
}
