package org.opendas.ext;

import javax.xml.parsers.*;

import org.opendas.DASLog;
import org.w3c.dom.*;
import org.xml.sax.*;

import java.io.*;

/**
 * ParserXML pour recupérer les paramètres du code xml correspondant aux
 * fonctions a appeler
 * 
 * @author mlaroche
 */
public class DASParserXmlFcts
{

	private Document	document	= null;

	public DASParserXmlFcts(String strXml)
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
			log("Pas de fichier xml" + npe);
		}
	}

	public DASFunctions getParameters_rec(Node node)
	{
		DASFunctions result = new DASFunctions();
		NodeList nodeList = node.getChildNodes();
		int place = 1;
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node childNode = nodeList.item(i);
			if ((childNode instanceof Element))
			{
				DASFunctions attr = new DASFunctions();
				attr.put("_sequence", place);
				NamedNodeMap attributes = childNode.getAttributes();
				for (int j = 0; j < attributes.getLength(); ++j)
				{
					attr.put(attributes.item(j).getNodeName().toUpperCase(), attributes.item(j).getTextContent());
				}
				result.put(String.valueOf(place), attr);
				++place;
				attr.put("_childs", getParameters_rec(childNode));
				if (attr.get("_childs") != null && ((DASFunctions) attr.get("_childs")).containsKey("_value"))
				{
					attr.put("_value", ((DASFunctions) attr.get("_childs")).get("_value"));
					((DASFunctions) attr.get("_childs")).remove("_value");
				}
				attr.put("_name", childNode.getNodeName());
			} else
			{
				result.put("_value", childNode.getTextContent());
			}
		}
		return result;
	}

	/**
	 * Retourne la liste des parametres definis dans le xml
	 * 
	 * @return la liste des parametres definis dans le xml
	 */
	public DASFunctions getParameters()
	{
		if (document == null)
		{
			return new DASFunctions();
		}
		DASFunctions parameters = new DASFunctions();
		Element root = document.getDocumentElement();
		return getParameters_rec(root);

		// CONTROLE_DE_LIVRAISON_ASPERPGI1=bl_asperpgi
		// CONTROLE_DE_LIVRAISON_ASPERPGI1_INSTRUCTION=Sélectionnez un bon de
		// livraison
		// CONTROLE_DE_LIVRAISON_ASPERPGI2_INSTRUCTION=Sélectionnez un produit
		// CONTROLE_DE_LIVRAISON_ASPERPGI2=produits_asperpgi
		// CONTROLE_DE_LIVRAISON_ASPERPGI3=colis_asperpgi
		// CONTROLE_DE_LIVRAISON_ASPERPGI3_INSTRUCTION=Sélectionnez un colis
		// CONTROLE_DE_LIVRAISON_ASPERPGI3_LOOP=0
		// CONTROLE_DE_LIVRAISON_ASPERPGI3_LOOP_ENDNAME=Terminer
		// CONTROLE_DE_LIVRAISON_ASPERPGI3_LOOP_TYPE=select
		// CONTROLE_DE_LIVRAISON_ASPERPGI4=ack
		// CONTROLE_DE_LIVRAISON_MAGENTO1=bl_magento
		// CONTROLE_DE_LIVRAISON_MAGENTO1_INSTRUCTION=Sélectionnez un bon de
		// livraison
		// CONTROLE_DE_LIVRAISON_MAGENTO2_INSTRUCTION=Sélectionnez un produit
		// CONTROLE_DE_LIVRAISON_MAGENTO2_LOOP=0
		// CONTROLE_DE_LIVRAISON_MAGENTO2_LOOP_ENDNAME=Terminer
		// CONTROLE_DE_LIVRAISON_MAGENTO2_LOOP_TYPE=count
		// CONTROLE_DE_LIVRAISON_MAGENTO2=produits_magento
		// CONTROLE_DE_LIVRAISON_MAGENTO3=ack
		// DELETE_ASPERPGI1=etiquettes}
		// DELETE_ASPERPGI1_INSTRUCTION=Sélectionnez une liste d'etiquettes pour
		// la supression
		// DELETE_ASPERPGI2=ack
		// POINTAGE1_INSTRUCTION=Sélectionnez la personne concernée
		// POINTAGE1=perso
		// POINTAGE2=ack
		// TRACKING_ASPERPGI1=etiquettes
		// TRACKING_ASPERPGI1_INSTRUCTION=Sélectionnez une liste d'etiquettes
		// pour le tracking
		// TRACKING_ASPERPGI2=ack
		
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
