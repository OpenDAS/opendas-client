package org.opendas.ext;

import javax.xml.parsers.*;

import org.opendas.DASLog;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * ParserXML pour recupérer les paramètres d'un code xml à 6 niveaux, comme
 * celui de l'interface graphique
 * 
 * @author mlaroche
 */
public class DASParserXmlGui
{

	private Document	document	= null;

	public DASParserXmlGui(String strXml)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream inSXml = new ByteArrayInputStream(strXml.getBytes());
			document = builder.parse(inSXml);
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
	 * Retourne la liste des parametres definis dans le xml pour l'ecran voulu
	 * 
	 * @param ecran
	 *            : "main" (fenetre principal) ou "keyboard" (clavier virtuel)
	 * @return la liste des parametres definis dans le xml pour l'ecran designe
	 */
	public Map<String, String> getParameters(String ecran)
	{
		if (document == null)
		{
			return new HashMap<String, String>();
		}
		Map<String, String> parameters = new HashMap<String, String>();
		Element root = document.getDocumentElement();
		NodeList screens = root.getChildNodes();
		for (int h = 0; h < screens.getLength(); ++h)
		{
			if (screens.item(h).getNodeName().equals(ecran))
			{
				String element = "";
				String value = null;
				NodeList objets = screens.item(h).getChildNodes();
				for (int i = 0; i < objets.getLength(); ++i)
				{
					NodeList children = objets.item(i).getChildNodes();
					for (int j = 0; j < children.getLength(); ++j)
					{
						NodeList grandChildren = children.item(j).getChildNodes();
						for (int k = 0; k < grandChildren.getLength(); ++k)
						{
							NodeList leaves = grandChildren.item(k).getChildNodes();
							for (int l = 0; l < leaves.getLength(); ++l)
							{
								if (leaves.item(l) instanceof Element)
								{
									Element e = (Element) leaves.item(l);
									element = objets.item(i).getNodeName() + "_" + children.item(j).getNodeName() + "_" + grandChildren.item(k).getNodeName() + "_" + e.getTagName();
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
					}
				}
				break;
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
