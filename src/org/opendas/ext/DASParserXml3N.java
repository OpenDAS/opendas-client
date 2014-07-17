package org.opendas.ext;

import javax.xml.parsers.*;

import org.opendas.DASLog;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * ParserXML pour recupérer les paramètres d'un code xml à trois niveaux (comme
 * button_xml et keyboard_xml)
 * 
 * @author mlaroche
 */
public class DASParserXml3N
{

	private Document	document	= null;

	public DASParserXml3N(String strXml)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new StringReader(strXml)));
		} catch (ParserConfigurationException pce)
		{
			log("Erreur lors de la création du parseur");
		} catch (SAXException se)
		{
			log("Erreur de parsing");
		} catch (IOException ioe)
		{
			log("Erreur d'acces au fichier xml");
		} catch (NullPointerException npe)
		{
			log("Pas de fichier xml");
		}
	}

	/**
	 * Retourne la liste des parametres definis dans le xml
	 * 
	 * @return la liste des parametres definis dans le xml
	 */
	public Map<String, String> getParameters()
	{
		if (document == null)
		{
			return new HashMap<String, String>();
		}
		Map<String, String> parameters = new HashMap<String, String>();
		Element root = document.getDocumentElement();
		NodeList elements = root.getChildNodes();
		for (int i = 0; i < elements.getLength(); ++i)
		{
			String element = "";
			String value = null;
			NodeList children = elements.item(i).getChildNodes();
			for (int j = 0; j < children.getLength(); ++j)
			{
				if (children.item(j) instanceof Element)
				{
					Element e = (Element) children.item(j);
					element = elements.item(i).getNodeName() + "_" + e.getTagName();
					element = element.toUpperCase();
					value = e.getTextContent();
					if (value != null)
					{
						value = value.trim();
					}
					parameters.put(element, value);
				}
			}
		}
		return parameters;
	}

	private void log(String log)
	{
		DASLog.log(getClass().getSimpleName(), log);
	}

	private void logDebug(String log)
	{
		DASLog.logDebug(getClass().getSimpleName(), log);
	}
}
