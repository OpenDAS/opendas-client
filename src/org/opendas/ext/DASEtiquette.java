package org.opendas.ext;

import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.commons.codec.binary.Base64;
import org.opendas.DASLog;

/***
 * 
 * classe qui permet d'éditer des étiquettes cette classe implémente l'interface
 * DASIEtiquettes qui a pour but de normaliser les méthodes utilisées.
 * 
 * @author laugraudc et martineaua
 * 
 */
public class DASEtiquette /* implements DASIEtiquettes */
{

	private final String	labelXmlJasperTemplatePath		= System.getProperty("user.dir") + "/ressources/";
	private final String	labelTxtTemplate				= System.getProperty("user.dir") + "/ressources/etiquette2.txt";
	private String			path;
	private JasperPrint		jasperPrint;
	private String			txt;
	private String			type;
	private String			pdf;

	public DASEtiquette(Map<String, Object> parameters, String type, String name, String templateXml, String path)
	{
		this.path = path;
		if (path == null)
		{
			this.path = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + name +".pdf";
		}
		this.type = type;
		if (type.equals("jasper"))
		{
			try
			{
				String rFile = labelXmlJasperTemplatePath + name + ".jrxml";
				FileInputStream fs = new FileInputStream(rFile);
				// - Chargement et compilation du rapport
				JasperDesign jasperDesign = JRXmlLoader.load(labelXmlJasperTemplatePath + name + ".jrxml");
				JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
				// - Execution du rapport

				Map<String, String> castparameters = new HashMap<String, String>();
				for (Entry<String, Object> entry : parameters.entrySet())
				{
					String key = entry.getKey();
					Object elem = entry.getValue();
					if (elem != null)
					{
						castparameters.put(key, elem.toString());
					}
				}
				jasperPrint = JasperFillManager.fillReport(jasperReport, castparameters, new net.sf.jasperreports.engine.JREmptyDataSource());
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		} else if (type.equals("txt"))
		{
			try
			{
				InputStream ips = new FileInputStream(labelTxtTemplate);
				InputStreamReader ipsr = new InputStreamReader(ips);
				BufferedReader br = new BufferedReader(ipsr);
				txt = "";
				String ligne;
				while ((ligne = br.readLine()) != null)
				{
					Pattern pattern = Pattern.compile("DASP\\W(\\w*)\\W");
					Matcher matcher = pattern.matcher(ligne);
					while (matcher.find())
					{
						String name_balise = matcher.group().replace("DASP{", "").replace("}", "");
						if (parameters.containsKey(name_balise))
						{
							if (parameters.get(name_balise).getClass() == String.class)
							{
								ligne = ligne.replace("DASP{" + name_balise + "}", (String) parameters.get(name_balise));
							} else
							{
								ligne = ligne.replace("DASP{" + name_balise + "}", parameters.get(name_balise).toString());
							}
						}
					}
					txt += ligne + "\n";
				}
				br.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		} else
		{
			if (parameters.containsKey(type))
			{
				if(parameters.get(type) instanceof String){
					this.pdf = parameters.get(type).toString();
				}
			}
		}
	}

	public boolean save()
	{
		logDebug("save " + this.path);
		if (this.path != null)
		{
			if (type.equals("jasper"))
			{
				try
				{
					JasperExportManager.exportReportToPdfFile(jasperPrint, this.path);
				} catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
			} else if (type.equals("txt"))
			{
				try
				{
					FileWriter fichier = new FileWriter(new File(this.path), false);
					fichier.write(this.txt);
					fichier.close();
				} catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
			} else
			{
				try
				{
					DataOutputStream e = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.path)));
					Base64 decoder = new Base64();
					e.write(decoder.decodeBase64(this.pdf));
					e.close();
				} catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	public boolean show()
	{
		if (type.equals("jasper"))
		{
			JasperViewer.viewReport(jasperPrint, false);
			return true;
		} else if (type.equals("txt"))
		{
			if (this.txt != null)
			{
				try
				{
					File myFile = new File(this.path);
					Desktop.getDesktop().open(myFile);
					return true;
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		} else
		{
			if (this.pdf != null)
			{
				try
				{
					File myFile = new File(this.path);
					Desktop.getDesktop().open(myFile);
					return true;
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public byte[] xlsReportToArray(JasperPrint jasperPrint)
	{
		byte[] bytes = null;
		try
		{
			JRXlsExporter jasperXlsExportMgr = new JRXlsExporter();
			ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
			jasperXlsExportMgr.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			jasperXlsExportMgr.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
			jasperXlsExportMgr.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, java.lang.Boolean.FALSE);
			jasperXlsExportMgr.setParameter(JRExporterParameter.OUTPUT_STREAM, xlsReport);
			jasperXlsExportMgr.exportReport();
			bytes = xlsReport.toByteArray();
		} catch (JRException jex)
		{
			jex.printStackTrace();
		}
		return bytes;
	}

	public byte[] pdfReportToArray(JasperPrint jasperPrint)
	{
		byte[] bytes = null;
		try
		{
			JRPdfExporter jasperPdfExportMgr = new JRPdfExporter();
			ByteArrayOutputStream pdfReport = new ByteArrayOutputStream();
			jasperPdfExportMgr.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			jasperPdfExportMgr.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
			jasperPdfExportMgr.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, java.lang.Boolean.FALSE);
			jasperPdfExportMgr.setParameter(JRExporterParameter.OUTPUT_STREAM, pdfReport);
			jasperPdfExportMgr.exportReport();
			bytes = pdfReport.toByteArray();
		} catch (JRException jex)
		{
			// throw new Exception ("Unable to create the XLS file" + jex);
			jex.printStackTrace();
		}
		return bytes;
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
// -----------------------------------
// //Selection de l'imprimante :
// PrintService service = null;
// PrintService[] servicesList = PrintServiceLookup.lookupPrintServices(null,
// null);
//
// for (PrintService ps : servicesList){
// System.out.println(ps+" Supports :");
// DocFlavor[] flavors = ps.getSupportedDocFlavors();
// for (int i = 0; i < flavors.length;i++)
// {
// System.out.println("\t" + flavors[i]);
// }
// }
// for(int k=0;k<servicesList.length;k++){
// if(servicesList[k].getName().trim().equals(modelPrintIdMaterial.trim())){
// service = servicesList[k];
// }
// }
//
//
// if(service != null){
// System.out.println("service not null =>"+service);
// JRExporter exporter = new JRPrintServiceExporter();
// exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
// exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET,
// service.getAttributes());
// exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG,
// Boolean.FALSE);
// exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG,
// Boolean.FALSE);
// exporter.exportReport();
// }

