package org.opendas.ctrl;

import gnu.io.NoSuchPortException;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.opendas.DASLoader;
import org.opendas.DASLog;
import org.opendas.calendar.Converter;
import org.opendas.calendar.Event;
import org.opendas.eanparser.Parser;
import org.opendas.equipment.DASBaseMaterial;
import org.opendas.equipment.DASCOMMaterial;
import org.opendas.equipment.DASEquipments;
import org.opendas.equipment.DASRJ45Material;
import org.opendas.ext.DASConnexion;
import org.opendas.ext.DASEtiquette;
import org.opendas.ext.DASFunctions;
import org.opendas.ext.DASIDataAccess;
import org.opendas.ext.DASParserXml3N;
import org.opendas.ext.DASParserXmlFcts;
import org.opendas.ext.PropertiesAccess;
import org.opendas.gui.DASPanel;
import org.opendas.modele.DASAcquisitionMethod;
import org.opendas.modele.DASConfigMaterial;
import org.opendas.modele.DASDataModel;
import org.opendas.modele.DASEan128;
import org.opendas.modele.DASError;
import org.opendas.modele.DASGeneric;
import org.opendas.modele.DASTemplateReport;
import org.opendas.modele.DASTemplateSupervision;
import org.opendas.modele.DASTypeTransmitProtocol;
import org.opendas.translate.I18n;

/**
 * Controller for PanelM5B
 * 
 * @author mlaroche
 * @author fchauvigne
 */
public class DASController
{
	/** Display panel **/
	private DASPanel									panel;
	/** Connected Equipment recognized **/
	private DASEquipments								equipments						= new DASEquipments();
	/** Connexion to consumers **/
	private DASIDataAccess								dataAccess						= DASConnexion.getInstance();
	// /** All generics (captured position) for station - Full load the first
	// call **/
	// private Map<String, DASGeneric> wsIndGenerics;
	/** Data Model **/
	private Map<String, DASDataModel>					dataModels						= new HashMap<String, DASDataModel>();
	private List<HashMap<String, Object>>				storeDataModel;
	/** Functional parameters **/
	private int											currentActionPosition;
	private String										instruction;
	private String										sequenceEnCours;
	private String										validModelEnCours				= null;
	private Object										currentFunction					= null;
	private DASFunctions								fctParams;
	private DASDataModel								currentDataModel;
	private Map<String, Object>				    		functional_context;
	public static Map<String, String> mapMaterial = new HashMap<String,String>();
	public static HashMap<String, Object>				superContext;
	/** Materials **/
	private List<DASBaseMaterial>						acMaterial;
	/** Materials : code **/
	private String										codeEntier						= null;
	private String										codeEnCours						= null;
	private String										valuesEnCours					= null;
	private boolean										manyCode						= false;
	/** material EAN prefix **/
	private List<DASEan128>								ean128List						= new LinkedList<DASEan128>();
	private static final String							EAN128prefix					= "]C1";
	private static final String							SSCCprefix						= "00";
	/** parser EAN128 **/
	private Parser										parserEAN;
	/** Printers **/
	private boolean										PrintAuthorize					= true;
	/* private DASACPrinter printersEnCours; */
	private List<DASTemplateReport>						templateReportList				= new LinkedList<DASTemplateReport>();
	/** About loop **/
	private int											nbLoops							= 1;
	private String										endLoopName;
	private String										LoopType;
	private String										LoopEnd;
	private boolean										endCodeFound					= false;
	/** a data sended to end infinite loop **/
	private static final String							endCode							= "-1";
	/** About dependency **/
	private boolean										displayDep						= true;
	private List<List<DASGeneric>>						depEnCours;
	private List<List<List<DASGeneric>>>				depEnCoursList;
	private static final int							PANELDEPSELECTED				= -2;
	/** virtual keyboard **/
	private Map<String, String>							kbParams;
	private boolean										displayKeyboard					= false;
	private boolean										virtualkeyboard					= false;
	private boolean										physicalKeyboard				= false;
	private static final String							PKEYBOARD_TYPE					= I18n._("Physical keyboard");
	/** data's current sequence **/
	private Map<String, List<String>>					currentData;
	/** current data input **/
	private List<String>								subDataEnCours;
	/** Back function **/
	private List<Map<String, List<String>>>				dataEnCoursList;
	private List<List<DASGeneric>>						depHistoryList;
	private List<DASDataModel>							dataModelEnCoursList;
	private List<Map<String, DASDataModel>>				dataModelsList;
	private List<String>								etapeList;
	private List<Object>								currentFunctionList				= null;
	private List<String>								instructionList;
	private List<String>								sequenceList;
	public static List<String>							sequenceListSorted;
	private LinkedList<List<HashMap<String, Object>>>	storeDataModelList;
	private List<List<String>>							subDataEnCoursList;
	private List<HashMap<String, Object>>				superContextList;
	private Map<String, Boolean>						dataActive;
	/** misc* */
	private boolean										authorizeDisplay				= true;
	private boolean										background						= false;
	private HashMap<String, Object>						calendarData					= new HashMap<String, Object>();
	private boolean										initialize						= false;
	private boolean										isGenericNextFunction			= true;
	private boolean										fromCorrectFunction				= false;
	private int											functionLoop					= 0;
	private List<DASGeneric>							generics;
	private List<DASGeneric>							genericsFiltred;
	private Map<String, List<DASGeneric>>				listWsGenerics;
	private Map<String, List<DASGeneric>>				listWsGenericsExt;
	private final int									PANELDEP						= 666;
	private final String								PANELOLD						= "PANELOLD";
	private int											panelSelected					= -1;
	private String										supervionBackgroundImagePath	= "";
	private boolean										displaySupervision				= true;
	private List<DASTemplateSupervision>				templateSupervisionList			= new LinkedList<DASTemplateSupervision>();
	private DASTemplateSupervision						templateSupervisionEnCours		= new DASTemplateSupervision();
	private List<DASBaseMaterial>						listEcouteurER					= new ArrayList<DASBaseMaterial>();
	private List<DASBaseMaterial>						listEcouteurR					= new ArrayList<DASBaseMaterial>();
	public static DASParserXmlFcts						parserXmlFcts					= null;
	// loading of properties file
	static PropertiesAccess pa = new PropertiesAccess("./config/das_client.conf");
	//If text instruction has been already defined or not ( messages conflict)
	private boolean instructionsAlreadyDefined = false;

	public DASController(DASPanel panel) throws NoSuchPortException
	{
		logDebug("DASController constructor");
		this.panel = panel;
		background = this.panel == null ? true : false;
		acMaterial = this.equipments.recupMaterials();
		
		try
		{
			templateReportList = (List<DASTemplateReport>) dataAccess.getFromServer("getListTemplateReport", null);
			templateSupervisionList = (List<DASTemplateSupervision>) dataAccess.getFromServer("getListTemplateSupervision", null);
			ean128List = (List<DASEan128>) dataAccess.getFromServer("getListEan128", null);
			parserEAN = new Parser(ean128List);
			parserXmlFcts = new DASParserXmlFcts(DASLoader.getFctsXml());
		}
		catch (TimeoutException e)
		{
			correctFunction("Time out.");
			if (background == false)
				panel.showError("Time out.");
			logErr(e.getMessage());
		}
		fctParams = parserXmlFcts.getParameters();
		DASParserXml3N parserXmlKB = new DASParserXml3N(DASLoader.getKeyboardXml());
		kbParams = parserXmlKB.getParameters();
		superContext = new DASFunctions();
		DASGeneric tmp = new DASGeneric();
		tmp.setCode(DASLoader.getWorkstationCode());
		tmp.setName(DASLoader.getWorkstationCode());
		superContext.put("_workstation", tmp);
		sequenceList = fctParams.getFctSequenceList();
		initMaterials();
	}

	// Initialize materials defined in functional config
	public void initMaterials() throws NoSuchPortException
	{
		String matCode = null;
		String priCode = null;
		// record count for a same material in the mask
		Map<String, Integer> cptMaterialByMask = new HashMap<String, Integer>();
		// record sequence for a same material
		Map<String, String> mapMatSeq = new HashMap<String, String>();
		Integer cpt = 0;

		sequenceListSorted = new LinkedList<String>();
		LinkedList<String> functionListNoBack = new LinkedList<String>();
		// Fill list with procedure background
		for (String seque : sequenceList)
		{
			if (fctParams.fctParams_get2(seque) != null)
			{
				// if line from xml contains a value for material then it searches
				// a material registered of which code is the same
				DASFunctions functionActuel = fctParams.fctParams_get2(seque);
				try
				{
					if (functionActuel.containsKey("BACKGROUND"))
					{
						//Browse functions on button
						for(String key: this.getPanel().getDicttopButtons().keySet()){
							//Compare name function to check if the same of that
							if(key.equals(functionActuel.get("_name")) && DASLoader.isLoading_configuration_on_start()){
								JOptionPane.showMessageDialog(null,I18n._("A button can't associated with a background function"), "Warning", JOptionPane.WARNING_MESSAGE);
							}
						}

						sequenceListSorted.add(seque);
					}
					else
					{
						functionListNoBack.add(seque);
					}
				}
				catch (NullPointerException ne)
				{}
			}
		}
		if (sequenceListSorted.size() > 0)
		{
			// Sort by background and priority
			for (String sequenc : sequenceListSorted)
			{
				if (sequenceListSorted.indexOf(sequenc) > 0)
				{
					DASFunctions functionTab = fctParams.fctParams_get2(sequenc);
					if (functionTab.containsKey("PRIORITY"))
					{
						String priority = (String) functionTab.get("PRIORITY");
						int j = sequenceListSorted.indexOf(sequenc);
						while (j > 0 && String.valueOf(fctParams.fctParams_get2(sequenceListSorted.get(j - 1)).get("PRIORITY")).compareTo(priority) > 0)
						{
							logDebug("DASController initMaterials() j-1 :" + sequenceListSorted.get(j - 1));
							// the current replaces the previous
							sequenceListSorted.set(j, sequenceListSorted.get(j - 1));
							j = j - 1;
						}
						// the previous replaces the current
						sequenceListSorted.set(j, sequenc);
					}
				}
			}
		}
		sequenceListSorted.addAll(functionListNoBack);
		
		logDebug(sequenceListSorted.toString());
		
		List<String> tabProcedure = new ArrayList<String>();
		boolean inOutAllowed = false;
		if (sequenceListSorted != null && sequenceListSorted.size() > 0)
		{
			for (String seq : sequenceListSorted)
			{
				if (fctParams.fctParams_get2(seq) != null)
				{
					// if line from xml contain a value for material then it
					// searches a material registered of which code is the same
					if (fctParams.fctParams_get2(seq).get("MATERIAL") != null)
					{
						// infos function from xml line
						matCode = (String) fctParams.fctParams_get2(seq).get("MATERIAL");
						if (fctParams.fctParams_get2(seq).get("PRIORITY") != null)
						{
							priCode = (String) fctParams.fctParams_get2(seq).get("PRIORITY");
						}
						String bckCode;
						if (fctParams.fctParams_get2(seq).containsKey("BACKGROUND"))
						{
							bckCode = (String) fctParams.fctParams_get2(seq).get("BACKGROUND");
						}
						else
						{
							bckCode = "0";
						}
						if (!matCode.equals(""))
						{
							if (bckCode.equals("1") && priCode != null)
							{
								if (tabProcedure.contains(matCode))
								{
									inOutAllowed = true;
								}
								else
								{
									tabProcedure.add(matCode);
								}
							}
							// If map empty
							if (cptMaterialByMask.size() == 0)
							{
								cpt++;
								cptMaterialByMask.put(matCode, cpt);
								mapMatSeq.put(matCode, seq);
							}
							else
							{
								// if current material is already present on the map
								try
								{
									Integer cptrec = cptMaterialByMask.get(matCode);
									// cptMaterialByMask.put(matCode,cptrec++);
									cptrec++;
									if (cptrec >= 2 && inOutAllowed == false)
									{
										JOptionPane.showMessageDialog(null, matCode + I18n._("Error : Material present in many functions"), "Fatal Error", JOptionPane.WARNING_MESSAGE);
									}
								}
								catch (NullPointerException ne)
								{
									ne.fillInStackTrace();
									cpt = 0;
									cptMaterialByMask.put(matCode, cpt++);
								}
							}
						}
						for (DASBaseMaterial material : acMaterial)
						{
							if (material.getCode() != null && material.getCode().equals(matCode))
							{
								material.addProperty("BACKGROUND", bckCode);
								material.addProperty("SEQUENCE", seq);
								material.addProperty("PRIORITY", priCode);
								initListsMaterial(material, this, seq);
							}
						}
					}
					else
					{}
				}
				else
				{}
			}
		}
	}

	// TODO Waiting for better server</>bddconfig
	public void resetConf()
	{
		logDebug("Passage par resetConf");

		try
		{
			templateReportList = (List<DASTemplateReport>) dataAccess.getFromServer("getListTemplateReport", null);
			templateSupervisionList = (List<DASTemplateSupervision>) dataAccess.getFromServer("getListTemplateSupervision", null);
			ean128List = (List<DASEan128>) dataAccess.getFromServer("getListEan128", null);
			parserEAN = new Parser(ean128List);
			
			if(!DASLoader.isLoading_configuration_on_start()){
				DASParserXmlFcts parserXmlFcts = null;
				parserXmlFcts = new DASParserXmlFcts(DASLoader.getFctsXml());
				fctParams = parserXmlFcts.getParameters();
			}

		}
		catch (TimeoutException e)
		{
			correctFunction("Time out.");
			if (background == false)
				panel.showError("Time out.");
			logErr(e.getMessage());
		}
		DASParserXml3N parserXmlKB = new DASParserXml3N(DASLoader.getKeyboardXml());
		kbParams = parserXmlKB.getParameters();
		//super_context = new DASFunctions();
		DASGeneric tmp = new DASGeneric();
		tmp.setCode(DASLoader.getWorkstationCode());
		tmp.setName(DASLoader.getWorkstationCode());
		superContext.put("_workstation", tmp);
		sequenceList = fctParams.getFctSequenceList();
		if (initialize == true)
		{
			sequenceEnCours = null;
			initialize = false;
		}
	}

	public boolean getParentTo(String data)
	{
		if (!currentData.containsKey(currentDataModel.getParentId().getCode()))
		{
			return false;
		}
		List<String> dep_en_cours = currentData.get(currentDataModel.getParentId().getCode());
		for (String i : dep_en_cours)
			if (data.equals(i))
				return true;
		return false;
	}

	public boolean setParentTo(String data)
	{
		authorizeDisplay = false;
		if (!currentData.containsKey(currentDataModel.getParentId().getCode()))
		{
			currentData.put(currentDataModel.getParentId().getCode(), new LinkedList<String>());
		}
		List<String> dep_en_cours = currentData.get(currentDataModel.getParentId().getCode());
		boolean in = false;
		for (String i : dep_en_cours)
			if (data.equals(i))
				in = true;
		if (in)
			dep_en_cours.remove(data);
		else
		{
			dep_en_cours.clear();
			dep_en_cours.add(data);
		}
		this.displayScreenFilter();
		return true;
	}

	public HashMap<String, Object> getEnv()
	{
		HashMap<String, Object> result = new HashMap<String, Object>();
		for (Field i : this.getClass().getDeclaredFields())
		{
			try
			{
				result.put(i.getName(), i.get(this));
			}
			catch (IllegalArgumentException e)
			{}
			catch (SecurityException e)
			{}
			catch (IllegalAccessException e)
			{}
		}
		return result;
	}

	/**
	 * Method to call when receiving a given
	 * 
	 * @param data
	 *            received data
	 * @param numPanel
	 *            received panel
	 * @return
	 */
	public boolean receivedData(String data, int numPanel)
	{
		logDebug("receivedData, data = " + data + ", numPanel = " + numPanel);
		panelSelected = numPanel;
		DASGeneric data_generic = null;
		if (generics != null)
		{
			for (DASGeneric gen : generics)
			{
				if (gen.getCode().equals(data))
				{
					data_generic = gen;
				}
			}
		}
		if (data_generic != null)
		{
			if (((DASFunctions) functional_context.get("_function")).containsKey("SUPERCONTEXT"))
			{
				superContext.put((String) ((DASFunctions) functional_context.get("_function")).get("SUPERCONTEXT"), (Object) data_generic);
			}
			if (((DASFunctions) functional_context.get("_function")).containsKey("CONTEXT"))
			{
				functional_context.put((String) ((DASFunctions) functional_context.get("_function")).get("CONTEXT"), (Object) data_generic);
			}
		}
		// If it choice the active dep, compare generics choice parent
		// and these choice previously
		if (panelSelected == PANELDEP)
		{
			setParentTo(data);
		}
		else if (data.equals(PANELOLD))
		{
			for (int i = (numPanel - 1); i > 0; i--)
				correctFunction(null);
		}
		else
		{
			checkData(data, numPanel);
		}
		return true;
	}

	public Map<String, Object> getFunctional_context()
	{
		return functional_context;
	}

	public void setFunctional_context(Map<String, Object> functional_context)
	{
		this.functional_context = functional_context;
	}

	@SuppressWarnings("unchecked")
	public void checkData(String data, int numPanel)
	{
		logDebug("CHECKDATA : " + data + ", Numpanel :" + numPanel + "DEPPANESEL : " + PANELDEPSELECTED);
		if (numPanel > PANELDEPSELECTED)
		{
			// If we don't select a dep
			if (data.equals(endCode))
			{
				stopAskMaterials();
				loopRoundOff();
				nextActionBtn();
			}
			else
			{
				codeEntier = null;
				Object tmpobject = validData(data);
				if (tmpobject != null && tmpobject instanceof java.lang.String)
				{
					logDebug("TMPOBJECT :" + tmpobject);
					codeEntier = (String) tmpobject;
				}
				else if (tmpobject != null && tmpobject instanceof java.util.Map)
				{
					logDebug(tmpobject.toString());
					logDebug(ean128GetterMethodEnCours);
					if (ean128GetterMethodEnCours.equals("full"))
					{
						Map<String, List<DASGeneric>> mapEan = (Map<String, List<DASGeneric>>) tmpobject;
						Set<String> mapEanKey = mapEan.keySet();
						Iterator itKey = mapEanKey.iterator();
						while (itKey.hasNext())
						{
							codeEntier = (String) itKey.next();
							logDebug("CODE ENTIER = " + codeEntier);
						}
					}
					else if (ean128GetterMethodEnCours.equals("all"))
					{
						List<String> codeAndValuesList = new LinkedList<String>();
						Map<String, List<DASGeneric>> mapEan = (Map<String, List<DASGeneric>>) tmpobject;
						Set<String> mapEanKey = mapEan.keySet();
						Iterator itKey = mapEanKey.iterator();
						while (itKey.hasNext())
						{
							codeEntier = (String) itKey.next();
							data = codeEntier;
						}
						Collection<List<DASGeneric>> colEan = mapEan.values();
						Iterator itCol = colEan.iterator();
						while (itCol.hasNext())
						{
							Iterator itCol2 = ((List<String>) itCol.next()).iterator();
							while (itCol2.hasNext())
							{
								List<String> tmpList = (List<String>) itCol2.next();
								codeAndValuesList.add("poste" + "_" + codeEntier + "_" + tmpList.get(0));
								codeAndValuesList.add(tmpList.get(1));
							}
						}
						// recording of values in ex
						// poste_code_entier_code
						manyCode = false;
						for (int i = 0; (i + 1) < codeAndValuesList.size(); i += 2)
						{
							logDebug(codeAndValuesList.get(i));
							codeEnCours = codeAndValuesList.get(i);
							valuesEnCours = codeAndValuesList.get(i + 1);
							// manycode = true put each code in dataEnCours
							manyCode = true;
							nextActionBtn();
						}
						manyCode = false;
					}
					else if (!(ean128GetterMethodEnCours.equals("full")) && !(ean128GetterMethodEnCours.equals("all")))
					{
						Map<String, List<DASGeneric>> mapEan = (Map<String, List<DASGeneric>>) tmpobject;
						Collection<List<DASGeneric>> colEan = mapEan.values();
						Iterator itCol = colEan.iterator();
						while (itCol.hasNext())
						{
							Iterator itCol2 = ((List<String>) itCol.next()).iterator();
							while (itCol2.hasNext())
							{
								List<String> tmpList = (List<String>) itCol2.next();
								if (tmpList.get(0).equals(ean128GetterMethodEnCours))
								{
									codeEntier = tmpList.get(1);
									logDebug("checkData : Single code selected :" + codeEntier);
								}
							}
						}
					}
				}
				if (codeEntier != null)
				{
					// Check if received code equal proposed generics
					if (isCodeLinkToGeneric(codeEntier, numPanel))
					{
						if (tmpobject != null && tmpobject instanceof java.lang.String)
						{
							boolean inSubDataEnCours = false;
							actionByLoop(data, inSubDataEnCours);
						}
						else if (tmpobject != null && tmpobject instanceof java.util.Map)
						{
							boolean inSubDataEnCours = false;
							if (ean128GetterMethodEnCours.equals("full"))
							{
								actionByLoop(codeEntier, inSubDataEnCours);
							}
							else if (ean128GetterMethodEnCours.equals("all"))
							{
								actionByLoop(data, inSubDataEnCours);
							}
							else if (!(ean128GetterMethodEnCours.equals("full")) && !(ean128GetterMethodEnCours.equals("all")))
							{
								actionByLoop(codeEntier, inSubDataEnCours);
							}
						}
						if (nbLoops != 0)
						{
							generics = null;
						}
						nextActionBtn();
					}
					else
					{
						if (background == false)
							panel.showError(getInstructionList().get(getInstructionList().size() - 1) + " - The received data is not valid.");
						logErr("the data " + data + " not correspond at neither data '" + currentFunction + "'.");
					}
				}
				else
				{
					if (background == false)
						panel.showError(getInstructionList().get(getInstructionList().size() - 1) + " - The received data is not valid.");
					logErr("A data '" + data + "' has been received  whereas it asks a data of type '" + validModelEnCours + "'.");
				}
			}
		}
	}

	public void actionByLoop(String code, boolean inSubDataEnCours)
	{
		if ("count".equals(LoopType))
		{
			validSubData(code, +1);
			subDataEnCours.add(code);
		}
		else if ("select".equals(LoopType))
		{
			// For Data, add or remove if already selected
			for (String i : subDataEnCours)
				if (code.equals(i))
					inSubDataEnCours = true;
			if (inSubDataEnCours)
				subDataEnCours.remove(code);
			else
			{
				subDataEnCours.add(code);
			}
		}
		else if ("choice".equals(LoopType))
		{
			subDataEnCours.clear();
			subDataEnCours.add(code);
		}
		else
		{
			subDataEnCours.add(code);
		}
	}

	/**
	 * Method called to determine if the code received is link to showed
	 * generics.
	 * 
	 * @code Received code
	 */
	public boolean isCodeLinkToGeneric(String code, int numPanel)
	{
		logDebug("NumPanel " + numPanel);
		if (numPanel != -1)
		{
			return true;
		}
		if (currentDataModel != null)
		{
			if (currentDataModel.getTypeFct() == null || currentDataModel.getTypeFct().equals("2"))
			{
				return true;
			}
		}
		if (generics != null)
		{
			for (DASGeneric i : generics)
			{
				if (i != null && i.getCode() != null && i.getCode().equals(code))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Method called at the click of a button on the top screen It triggers the
	 * sequence of functions required
	 * 
	 * @sequence name sequence of sequence of functions to perform top screen
	 */
	public void actionBtn(String sequence)
	{
		initActionBtn(sequence, true);
		nextActionBtn();
	}

	public void nextActionBtn()
	{
		nextActionBtn(null);
	}

	/**
	 * Method called at start-up sequence and after each valid data received She
	 * performs and records necessary initializations and calls the function to
	 * perform next (even if the loop)
	 */
	public void nextActionBtn(JTextField field)
	{
		logDebug("Passage par nextActionBtn");
		if (endedLoop())
		{
			initFunction();
		}
		else
		{
			nextLoop();
		}
		functionSelecting(field);
	}

	/**
	 * Method called for back action : Init the sequence with n-1 parameters.
	 * 
	 * @param _function
	 * @param field
	 * @param logMessage
	 */
	public void backActionBtn(String _function, JTextField field, String logMessage)
	{
		stopAskMaterials();
		String instructionPrecedente = instructionList.get(instructionList.size() - 2);
		currentActionPosition = (currentActionPosition - 2);
		endCodeFound = true;
		if (background == false)
		{
			//resetTopButton(storeDataModelList.get(storeDataModelList.size() - 1));
			panel.cleanPanelBeforeDisplay();
		}
		panelSelected = -1;
		if (nbLoops != 0)
		{
			generics = null;
		}
		currentDataModel = dataModelEnCoursList.get(dataModelEnCoursList.size() - 2);
		dataModels = dataModelsList.get(dataModelsList.size() - 2);
		if (dataEnCoursList.size() >= 3)
		{
			Map<String, List<String>> tmpDEC = new HashMap<String, List<String>>();
			tmpDEC.putAll(dataEnCoursList.get(dataEnCoursList.size() - 3));
			currentData = tmpDEC;
		}
		else
		{
			currentData = new HashMap<String, List<String>>();
		}
		if (dataEnCoursList.size() >= 3)
		{
			currentFunction = currentFunctionList.get(currentFunctionList.size() - 3);
		}
		else
		{
			currentFunction = currentFunctionList.get(currentFunctionList.size() - 2);
		}
		if (dataEnCoursList.size() >= 3)
		{
			subDataEnCours = subDataEnCoursList.get(subDataEnCoursList.size() - 3);
		}
		else
		{
			subDataEnCours = subDataEnCoursList.get(subDataEnCoursList.size() - 2);
		}
		storeDataModel = storeDataModelList.get(storeDataModelList.size() - 2);
		depEnCours = depEnCoursList.get(depEnCoursList.size() - 2);
		superContext = superContextList.get(superContextList.size() - 2);
		// Erase BackList Element
		for (int i = 0; i < 2; i++)
		{
			if (instructionList.size() > 0)
				instructionList.remove(instructionList.size() - 1);
			superContextList.remove(superContextList.size() - 1);
			dataEnCoursList.remove(dataEnCoursList.size() - 1);
			dataModelsList.remove(dataModelsList.size() - 1);
			dataModelEnCoursList.remove(dataModelEnCoursList.size() - 1);
			if (depHistoryList.size() > 0)
				depHistoryList.remove(depHistoryList.size() - 1);
			etapeList.remove(etapeList.size() - 1);
			currentFunctionList.remove(currentFunctionList.size() - 1);
			subDataEnCoursList.remove(subDataEnCoursList.size() - 1);
			storeDataModelList.remove(storeDataModelList.size() - 1);
			if (depEnCoursList.size() > 0)
				depEnCoursList.remove(depEnCoursList.size() - 1);
		}
		initFunction();
		functionSelecting(field);
		if (logMessage == null || logMessage.isEmpty())
		{}
		else
		{
			if (background == false)
				panel.showError(logMessage + " - " + instructionPrecedente);
		}
	}

	/**
	 * Method called by function for saving data for back function
	 */
	public void updateHistoriqueLists(String function)
	{
		Map<String, DASDataModel> tmp_dataModels = new HashMap<String, DASDataModel>();
		tmp_dataModels.putAll(dataModels);
		Map<String, List<String>> tmp_dataEnCours = new HashMap<String, List<String>>();
		tmp_dataEnCours.putAll(currentData);
		HashMap<String, Object> tmp_super_context = new HashMap<String, Object>();
		tmp_super_context.putAll(superContext);
		dataModelsList.add(tmp_dataModels);
		dataEnCoursList.add(tmp_dataEnCours);
		dataModelEnCoursList.add(currentDataModel);
		etapeList.add(function);
		currentFunctionList.add(currentFunction);
		instructionList.add(instruction);
		subDataEnCoursList.add(subDataEnCours);
		storeDataModelList.add(storeDataModel);
		depEnCoursList.add(depEnCours);
		superContextList.add(tmp_super_context);
	};

	@SuppressWarnings("unchecked")
	public void print()
	{
		logDebug("IMPRESSION");
		String type = null;
		if (((Map<String, Object>) functional_context.get("_function")).containsKey("TYPE") && ((Map<String, Object>) functional_context.get("_function")).get("TYPE") != null)
			type = (String) ((Map<String, Object>) functional_context.get("_function")).get("TYPE");
		String name = null;
		if (((Map<String, Object>) functional_context.get("_function")).containsKey("NAME") && ((Map<String, Object>) functional_context.get("_function")).get("NAME") != null)
			name = (String) ((Map<String, Object>) functional_context.get("_function")).get("NAME");
		String material = null;
		if (((Map<String, Object>) functional_context.get("_function")).containsKey("MATERIAL") && ((Map<String, Object>) functional_context.get("_function")).get("MATERIAL") != null)
			material = (String) ((Map<String, Object>) functional_context.get("_function")).get("MATERIAL");
		String save = null;
		if (((Map<String, Object>) functional_context.get("_function")).containsKey("SAVE") && ((Map<String, Object>) functional_context.get("_function")).get("SAVE") != null)
			save = (String) ((Map<String, Object>) functional_context.get("_function")).get("SAVE");
		String show = null;
		if (((Map<String, Object>) functional_context.get("_function")).containsKey("SHOW") && ((Map<String, Object>) functional_context.get("_function")).get("SHOW") != null)
			show = (String) ((Map<String, Object>) functional_context.get("_function")).get("SHOW");
		String print = null;
		if (((Map<String, Object>) functional_context.get("_function")).containsKey("PRINT") && ((Map<String, Object>) functional_context.get("_function")).get("PRINT") != null)
			print = (String) ((Map<String, Object>) functional_context.get("_function")).get("PRINT");
		Map<String, Object> data = null;
		if (functional_context.containsKey(name) && functional_context.get(name) != null)
			data = ((DASGeneric) functional_context.get(name)).getInfos();
		else
			logErr("functional_context not contains " + name + " ! Please add store context !");
		String templateXML = "default";
		for (DASTemplateReport i : templateReportList)
		{
			if (i.getName().equals(name))
			{
				templateXML = i.getTemplateXml();
			}
		}
		String path = null;
		DASEtiquette etiquetteEnCours = null;
		// byte[] streamEtiquetteEnCours = null;
		etiquetteEnCours = new DASEtiquette(data, type, name, templateXML, path);
		if (background == false)
		{
			if (save != null && save.equals("1"))
			{
				path = panel.save("print", "/");
			}
			if (etiquetteEnCours.save())
			{}
			else
			{
				panel.showError("The save document has failed");
			}
			if (show != null && show.equals("1"))
			{
				if (etiquetteEnCours.show())
				{}
				else
				{
					panel.showError("The save document has failed");
				}
			}
		}
		nextActionBtn();
		if (background == false && print != null && print.equals("1"))
		{
			logDebug("Printout directy of pdf report on the material");
			// printersEnCours =
			// this.equipments.getCOMPrinter(Integer.parseInt(material));
			/*
			 * if (printersEnCours == null) {
			 * panel.showError("Imprimante inacessible.");
			 * panel.imprimerPopup("Imprimante inacessible."); } else { //TODO ?
			 * // streamEtiquetteEnCours = etiquetteEnCours.getByteReport(); //
			 * imp.imprimer(streamEtiquetteEnCours);
			 * panel.showInstruction("Impression en cours...");
			 * panel.imprimerPopup("Impression en cours..."); }
			 */
		}
		panel.blockScreen(false);
	}

	public String isDataModelValue(String function)
	{
		if (!(storeDataModelList.isEmpty()))
		{
			for (List<HashMap<String, Object>> y : storeDataModelList)
			{
				for (HashMap<String, Object> i : y)
				{
					if (i.get("model").equals(function))
					{
						return (String) i.get("smCode");
					}
				}
			}
		}
		return null;
	}

	public boolean checkFunctionLoop(String sequence)
	{
		int _functionLoop = -2;
		Object fl = fctParams.fctParams_get2(sequence).get("FUNCTIONLOOP");
		if (fl != null)
		{
			_functionLoop = Integer.parseInt((String) fl);
		}
		if (_functionLoop != -2)
		{
			functionLoop = _functionLoop;
		}
		else
		{
			functionLoop = 0;
		}
		logDebug("checkFunctionLoop : " + functionLoop);
		return true;
	}

	public boolean isFunctionLoop()
	{
		if (functionLoop == 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Acquisition Method -> Select & Execute
	 * 
	 * @param function
	 *            name function from xml
	 * @param field
	 *            selected field (null if nothing selected)
	 */
	public boolean functionSelecting(JTextField field)
	{
		logDebug("Passage par functionSelecting ");
		logDebug("========================================================================================");
		logDebug("********function : " + currentFunction);
		logDebug("********dataModels :" + dataModels);
		logDebug("********currentDataModel : " + currentDataModel);
		logDebug("********dataEnCours : " + currentData);
		logDebug("********currentSequence : " + sequenceEnCours);
		logDebug("********currentActionPosition : " + currentActionPosition);
		logDebug("********storeModel :" + storeDataModel);
		logDebug("********storeModelList :" + storeDataModelList);
		logDebug("********instruction : " + instruction);
		logDebug("********subDataEnCours : " + subDataEnCours);
		logDebug("********subDatalist : " + subDataEnCoursList);
		logDebug("********depEnCours : " + depEnCoursList);
		logDebug("********depEnCoursList : " + depEnCoursList);
		logDebug("********super_context : " + superContext);
		logDebug("********functional_context : " + functional_context);
		logDebug("********LoopType : " + LoopType);
		logDebug("========================================================================================");
		if (functional_context.get("_function") == null)
		{
			logDebug("function == null");
			if (isFunctionLoop())
			{
				logDebug("END + FUNCTIONLOOP --> START OF " + sequenceEnCours);
				actionBtn(sequenceEnCours);
			}
			else
			{
				logDebug("else isFunctionloop != true");
				initialize = true;
				if (background == false){
					panel.cancelFunction();
				}else{
					logDebug("Avant resetConf dans functionSelecting");
					//resetConf();
				}
			}
		}
		else
		{
			currentFunction = (String) (((DASFunctions) functional_context.get("_function")).get("_name"));
			if (isGenericNextFunction)
			{
				//It test if functionEnCours datamodel is empty
				String isdmvalue = isDataModelValue((String) currentFunction);
				if (isdmvalue != null && (!(isdmvalue.equals("none"))))
				{
					// if not null, data already received
					if (fromCorrectFunction)
					{
						// Si on viens d'utiliser la correct fonction, on
						// renvoie en
						// n-2
						logDebug("isGenericNextFunction : CorrectFunction to : " + currentFunction + " which already contains expected datas, so = N-2");
						correctFunction(null);
					}
					else
					{
						// Else, it passes next function
						logDebug("isGenericNextFunction : data exists already, so it jumps the function");
						logDebug("isGenericNextFunction : data exists already : " + isdmvalue);
						receivedData(isdmvalue, -1);
					}
				}
			}
			if (background == false)
			{
				if(this.instructionsAlreadyDefined == false){
					panel.showInstruction(instruction);
					panel.correctButton(true);
				}
			}
			if (currentFunction == null || ((String) currentFunction).isEmpty())
			{
				log("Function without name or no defined");
				if (background == false)
					panel.showError("Function without name or no defined");
			}
			else if ("ack".equals(currentFunction))
			{
				DASError response = null;
				try
				{
					HashMap<String, Object> params = new HashMap<String, Object>(currentData);
					for (Entry<String, Object> entry : superContext.entrySet())
					{
						String key = entry.getKey();
						Object elem = entry.getValue();
						if (elem instanceof DASGeneric)
						{
							if (!params.containsKey(key))
							{
								List<String> tmp = new LinkedList<String>();
								tmp.add(((DASGeneric) elem).getCode());
								params.put(key, tmp);
							}
						}
					}
					response = (DASError) dataAccess.getFromErp(sequenceEnCours, (Serializable) params);
					logDebug("ERROR CODE ==> " + response.getCode());
				}
				catch (TimeoutException e)
				{
					logErr(e.getMessage());
					correctFunction("Time out.");
					if (background == false)
						panel.showError("Time out.");
					return false;
				}
				if (background == false){

					panel.cleanEndFunction();
				}
				checkResponse(response);
				
				initFunction();
				logDebug("INIT FUNCTION OK");
				functionSelecting(field);
				panel.blockScreen(false);
				instructionsAlreadyDefined = false;

			}
			else if ("store".equals(currentFunction))
			{
				if (fromCorrectFunction)
				{
					// Si on viens d'utiliser la correct fonction, on
					// renvoie en
					// n-2
					logDebug("CorrectFunction to : " + currentFunction + " which already contains expected datas, so = N-2");
					correctFunction(null);
				}
				else
				{
					majModelesEnCours((String) (((DASFunctions) functional_context.get("_function")).get("MODEL")));
					try
					{
						getGenerics(currentDataModel);
					}
					catch (TimeoutException e)
					{
						logErr(e.getMessage());
						correctFunction("Time out.");
						return false;
					}
					if (generics == null)
					{
						logErr("No generic datas of type '" + currentDataModel.getCode() + "'");
						correctFunction("No datas to display");
						return false;
					}
					if (((DASFunctions) functional_context.get("_function")).containsKey("CONTEXT"))
					{
						if (((DASFunctions) functional_context.get("_function")).containsKey("type") && ((DASFunctions) functional_context.get("_function")).get("type").equals("latch"))
						{
							if (functional_context.containsKey((String) ((DASFunctions) functional_context.get("_function")).get("CONTEXT")))
							{
								functional_context.remove((String) ((DASFunctions) functional_context.get("_function")).get("CONTEXT"));
							}
							else
							{
								functional_context.put((String) ((DASFunctions) functional_context.get("_function")).get("CONTEXT"), generics);
							}
						}
						else
						{
							functional_context.put((String) ((DASFunctions) functional_context.get("_function")).get("CONTEXT"), generics);
						}
					}
					else if (((DASFunctions) functional_context.get("_function")).containsKey("SUPERCONTEXT"))
					{
						if (((DASFunctions) functional_context.get("_function")).containsKey("TYPE") && ((DASFunctions) functional_context.get("_function")).get("TYPE").equals("latch"))
						{
							if (superContext.containsKey((String) ((DASFunctions) functional_context.get("_function")).get("SUPERCONTEXT")))
							{
								superContext.remove((String) ((DASFunctions) functional_context.get("_function")).get("SUPERCONTEXT"));
							}
							else
							{
								superContext.put((String) ((DASFunctions) functional_context.get("_function")).get("SUPERCONTEXT"), generics);
							}
						}
						else if (((DASFunctions) functional_context.get("_function")).containsKey("TYPE") && ((DASFunctions) functional_context.get("_function")).get("TYPE").equals("erase"))
						{
							logDebug("********* type = erase");
							if (superContext.containsKey((String) ((DASFunctions) functional_context.get("_function")).get("SUPERCONTEXT")))
							{
								superContext.remove((String) ((DASFunctions) functional_context.get("_function")).get("SUPERCONTEXT"));
							}
							superContext.put((String) ((DASFunctions) functional_context.get("_function")).get("SUPERCONTEXT"), generics);
						}
						else
						{
							superContext.put((String) ((DASFunctions) functional_context.get("_function")).get("SUPERCONTEXT"), generics);
						}
					}
					logDebug("STORE : data exists already, so it jumps the function");
					initFunction();
					if (field == null && background == false)
						panel.cleanEndFunction();
					else if (field == null && background == true)
						if(DASLoader.isLoading_configuration_on_start()){
							resetConf();
						}
						//functionSelecting(field);
				}
				panel.blockScreen(false);
			}
			else if ("clean".equals(currentFunction))
			{
				if (((DASFunctions) functional_context.get("_function")).containsKey("CONTEXT"))
				{
					if (functional_context.containsKey((String) ((DASFunctions) functional_context.get("_function")).get("CONTEXT")))
						functional_context.remove((String) ((DASFunctions) functional_context.get("_function")).get("CONTEXT"));
				}
				else if (((DASFunctions) functional_context.get("_function")).containsKey("SUPERCONTEXT"))
				{
					if (superContext.containsKey((String) ((DASFunctions) functional_context.get("_function")).get("SUPERCONTEXT")))
						superContext.remove((String) ((DASFunctions) functional_context.get("_function")).get("SUPERCONTEXT"));
				}
				initFunction();
				functionSelecting(field);
				panel.blockScreen(false);
			}
			else if ("report".equals(currentFunction))
			{
				if (fromCorrectFunction)
				{
					// If we have use the correct function so n-2
					logDebug("CorrectFunction vers : " + currentFunction + " = N-2");
					correctFunction(null);
				}
				else
				{
					if (PrintAuthorize)
						print();
				}
			}
			else
			{
				majModelesEnCours((String) currentFunction);
				if (currentDataModel.getAcquisitionMethods() != null && currentDataModel.getAcquisitionMethods().size() != 0)
				{
					logDebug("UPDATING MODEL");
					authorizeDisplay = false;
					for (DASAcquisitionMethod i : currentDataModel.getAcquisitionMethods())
					{
						if (i.getCode().equals("displayScreen"))
						{
							if (isGenericsToDisplay())
								authorizeDisplay = true;
						}
						else if (i.getCode().equals("displayKeyboard"))
						{
							displayKeyboard = true;
							virtualkeyboard = true;
						}
						else if (i.getCode().equals("listenTypeMaterial"))
						{
							// displayKeyboard = true;
							if (i.getTypeMaterial().getName().equals(PKEYBOARD_TYPE))
							{
								logDebug("PHYSICAL KEYBOARD IS ENABLED");
								physicalKeyboard = true;
							}
							askingScan = true;
						}
						else if (i.getCode().equals("displaySupervision"))
						{
							displaySupervision = true;
						}
					}
					if (background == false)
					{
						if (virtualkeyboard == false)
							panel.setSwitchBtnName(I18n._("Inputs view"));
						else
							panel.setSwitchBtnName(I18n._("Keyboard view"));
						if (authorizeDisplay == true)
						{
							logDebug("AUTHORIZE DISPLAY");
							if (displayKeyboard == true)
							{
								panel.setSwitchButton(true, panel.getSwitchBtnName());
							}
							else
							{
								panel.setSwitchButton(false, "");
							}
							displayScreenFilter();
							displayKeyboard = false;
						}
						else if (displayKeyboard == true)
						{
							panel.blockScreen(false);
							logDebug("DISPLAY KEYBOARD");
							if (authorizeDisplay == true)
								panel.setSwitchButton(true, I18n._("Generics view"));
							else
								panel.setSwitchButton(false, "");
							try
							{
								if (field != null)
								{
									panel.displayKeyboard(field, "", "", true);
								}
								else
								{
									panel.displayKeyboard();
								}
							}
							catch (TimeoutException ex)
							{}
						}
						else if (displaySupervision == true)
						{
							panel.blockScreen(false);
							logDebug("DISPLAY SUPERVISION");
							try
							{
								panel.displaySupervision(templateSupervisionEnCours);
							}
							catch (TimeoutException e)
							{
								e.printStackTrace();
							}
						}
						else if (physicalKeyboard == true)
						{
							panel.blockScreen(false);
							logDebug("DISPLAY KEYBOARD");
							if (authorizeDisplay == true)
								panel.setSwitchButton(true, I18n._("Generics view"));
							else
								panel.setSwitchButton(false, "");
							try
							{
								if (field != null)
								{
									logDebug("field != null displayKeyboard");
									panel.displayKeyboard(field, "", "", true);
								}
								else
								{
									logDebug("field == null displayKeyboard");
									panel.displayKeyboard();
								}
							}
							catch (TimeoutException ex)
							{}
						}
					}
					// Reset Asking Material
					// stopAskMaterials();
					List<DASConfigMaterial> temps = this.getEquipments().loader.getConfig().getMaterials();
					for (DASAcquisitionMethod i : currentDataModel.getAcquisitionMethods())
					{
						// Browse configMaterialObject
						for (DASConfigMaterial confm : temps)
						{
							DASConfigMaterial confMaterial = confm;
							org.opendas.modele.DASMaterial material = confMaterial.getMaterial();
							// Browse list of material E/R initialized
							for (DASBaseMaterial mat : listEcouteurER)
							{
								if (confm.getCode().equals(mat.getCode()))
								{
									if (i.getTypeMaterial() != null)
									{
										// If type material lied at acquisition
										// method is type material of material
										// then
										if (material.getModelMaterial().getTypeMaterial().getName().equals(i.getTypeMaterial().getName()) && i.getCode().equals("listenTypeMaterial"))
										{
											// listenMaterial(instruction,confm.getCode());
											askMaterials(confm.getCode(), panel);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		fromCorrectFunction = false;
		return true;
	}

	public List<DASBaseMaterial> getListEcouteurER()
	{
		return listEcouteurER;
	}

	public void setListEcouteurER(List<DASBaseMaterial> listEcouteurER)
	{
		this.listEcouteurER = listEcouteurER;
	}

	private int checkResponse(DASError response)
	{
		
		if (response.getCode().equals(0))
		{
			panel.showResponse(response.getString());
			this.instructionsAlreadyDefined = true;
			if (background == false)
			{
				if (DASLoader.debugMode){
					JOptionPane.showMessageDialog(null, response.getString() ,"Ack Response", JOptionPane.DEFAULT_OPTION); 
				}
			}
		}
		else if (response.getCode().equals(1))
		{
			if (background == false)
			{
				panel.showError(response.getString());
				if (DASLoader.debugMode)
				{
					JOptionPane.showMessageDialog(null, response.getString(), "Ack Response", JOptionPane.DEFAULT_OPTION);
				}
			}
			logDebug(response.getString());
		}
		else if (response.getCode().equals(2))
		{
			if (background == false)
			{
				panel.showError(response.getString());
				if (DASLoader.debugMode)
				{
					JOptionPane.showMessageDialog(null, response.getString(), "Ack Response", JOptionPane.DEFAULT_OPTION);
				}
			}
			logDebug(response.getString());
		}
		else if (response.getCode().equals(3))
		{
			if (background == false)
			{
				panel.showError(response.getString());
				if (DASLoader.debugMode)
				{
					JOptionPane.showMessageDialog(null, response.getString(), "Ack Response", JOptionPane.DEFAULT_OPTION);
				}
			}
			logDebug(response.getString());
		}
		else if (response.getCode().equals(4))
		{
			if (background == false)
			{
				if (DASLoader.debugMode)
				{
					return JOptionPane.showConfirmDialog(null, response.getString(), "Ack Response", JOptionPane.YES_NO_OPTION);
				}
			}
			logDebug(response.getString());
		}
		return -1;
	}

	public void correctFunction(String logMessage)
	{
		fromCorrectFunction = true;
		if (background == false)
			panel.setVisibleAllPanels(true);
		switch (getEtapeList().size())
		{
			case 0 :
			case 1 :
				if (background == false)
				{
					panel.cleanEndFunction();
					panel.showError(logMessage);
				}
				else
				{
					resetConf();
				}
				break;
			default :
				if (background == false)
					panel.cleanCenterPanel();
				backActionBtn(getEtapeList().get(getEtapeList().size() - 2), null, logMessage);
				break;
		}
	}

	/**
	 * Top Field call this method :
	 * 
	 * @param field
	 *            Field who called the method
	 * @param sequence
	 *            function name to execute
	 */
	public void actionField(JTextField field, String sequence)
	{
		initActionBtn(sequence, true);
		nextActionBtn(field);
	}

	public List<DASGeneric> getGenerics(DASDataModel data_model) throws TimeoutException
	{
		int select = 0;
		if (data_model.getTypeFct() != null)
		{
			select = Integer.parseInt(data_model.getTypeFct());
		}
		generics = null;
		// try
		// {
		switch (select)
		{
			case 0 :
				generics = getListWsGenericsStatic().get(data_model.getCode());
				break;
			case 1 :
				if (data_model.getType().equals("S"))
				{
					generics = getListWsGenericsStaticExt(data_model.getCode()).get(data_model.getCode());
				}
				else
				{
					generics = getListWsGenericsExt(data_model.getCode()).get(data_model.getCode());
				}
				break;
			case 2 :
				logDebug("TODO Implementation of internal workstation data");
				break;
			default :
				generics = getListWsGenericsStatic().get(data_model.getCode());
				break;
		}
		return generics;
	}

	/**
	 * Show selected generics
	 * 
	 * @param instruction
	 *            instruction to show for keyboard
	 */
	@SuppressWarnings("unchecked")
	public boolean isGenericsToDisplay()
	{
		// If it's not the first passage on the loop, we have already generics
		if (nbLoops == 0 && (generics != null))
		{
			// Pour distinguer le changement de fonction
			if (currentFunctionList.size() >= 2)
			{
				if (currentFunction.equals(currentFunctionList.get(currentFunctionList.size() - 1)))
				{
					logDebug("Generics already received");
					return true;
				}
			}

		}
		int select = 0;
		if (currentDataModel.getTypeFct() != null)
		{
			select = Integer.parseInt(currentDataModel.getTypeFct());
		}
		if (background == false)
		{
			panel.blockScreen(true);
		}
		try
		{
			switch (select)
			{
				case 0 :
					generics = getGenerics(currentDataModel);
					break;
				case 1 :
					if (currentDataModel.getType().equals("S"))
					{
						generics = getListWsGenericsStaticExt(currentDataModel.getCode()).get(currentDataModel.getCode());
					}
					else
					{
						generics = getListWsGenericsExt(currentDataModel.getCode()).get(currentDataModel.getCode());
					}
					break;
				case 2 :
					logDebug("TODO Implementation of internal workstation data");
					break; // TODO Implementation of internal workstation data
				case 3 :
					logDebug("TIMEOUT TEST");
					generics = (List<DASGeneric>) dataAccess.getFromServer("FAIL", DASLoader.getWorkstationCode());
					break;
				default :
					generics = getGenerics(currentDataModel);
					break;
			}
		}
		catch (TimeoutException e)
		{
			logErr(e.getMessage());
			correctFunction(I18n._("Time out."));
			if (background == false)
				panel.blockScreen(false);
			panel.showError(I18n._("Time out."));
			return false;
		}
		if (generics == null)
		{
			isGenericNextFunction = false;
			logErr("No generic datas found for the type : '" + currentDataModel.getCode() + "'");
			if (currentFunctionList.size() == 1)
			{
				if (background == false)
				{
					initialize = true;
					panel.cancelFunction();
				}
				else
				{
					initialize = true;
					stopAskMaterials();
					resetConf();
				}
			}
			else if (currentFunctionList.get(currentFunctionList.size() - 2).equals("print"))
			{
				PrintAuthorize = false;
				correctFunction(I18n._("No data to display"));
				PrintAuthorize = true;
			}
			correctFunction(I18n._("No data to display"));
			if (background == false)
			{
				panel.blockScreen(false);
			}
			return false;
		}
		else if (background == false)
		{
			if (unlimitedLoop())
			{
				panel.addEndButton(endLoopName);
			}
			else
			{
				panel.addEndButton(null);
			}
			return true;
		}
		return true;
	}

	/**
	 * Show Generics And Dependency
	 * 
	 * @param instruction
	 */
	public void displayScreenFilter()
	{
		panel.cleanPanelBeforeDisplay();
		if (subDataEnCours.isEmpty() && (panelSelected != PANELDEP) && calendarData.isEmpty() && depEnCours.isEmpty())
		{
			updateDepHistoryList();
		}
		// DEP
		displayDep();
		// GENERICS
		genericsFiltred = new LinkedList<DASGeneric>();
		if (currentDataModel.getParentId() != null)
		{
			if (currentData.containsKey(currentDataModel.getParentId().getCode()))
			{
				for (String dataEC : currentData.get(currentDataModel.getParentId().getCode()))
				{
					genericsFiltred = generateGenericsFiltre(generics, dataEC);
				}
			}
		}
		if (genericsFiltred.isEmpty())
		{
			if (!calendarData.isEmpty())
			{
				try
				{
					updateCalendarList(generics);
				}
				catch (Exception e)
				{
					calendarData = new HashMap<String, Object>();
				}
			}
			if (calendarData.isEmpty())
			{
				panel.switchGenericsAll(generics, null, ++panelSelected, new HashMap<String, Object>());
			}
			else
			{
				panel.switchGenericsAll(generics, null, ++panelSelected, calendarData);
			}
		}
		else
		{
			if (!calendarData.isEmpty())
			{
				try
				{
					updateCalendarList(genericsFiltred);
				}
				catch (Exception e)
				{
					calendarData = new HashMap<String, Object>();
				}
			}
			if (calendarData.isEmpty())
			{
				panel.switchGenericsAll(genericsFiltred, null, ++panelSelected, new HashMap<String, Object>());
			}
			else
			{
				panel.switchGenericsAll(genericsFiltred, null, ++panelSelected, calendarData);
			}
		}
		if (background == false)
		{
			panel.blockScreen(false);
		}
	}

	private void updateCalendarList(List<DASGeneric> generics)
	{
		listEvent = new ArrayList<org.opendas.calendar.Event>();
		HashMap<Integer, Color> colorSchema = new HashMap<Integer, Color>();
		int num_ligne = 0;
		Long date_deb_gen = null;
		Long date_end_gen = null;
		// int taille_interface = X;
		// boolean compress = false;
		// if(genericsEnCours.size() > taille_interface){
		// compress = true;
		// }
		for (DASGeneric i : generics)
		{
			org.opendas.calendar.Event ev = new org.opendas.calendar.Event();
			if(i.getCode().contains(","))
			{
				ev.setId(Integer.parseInt(i.getCode().split(",")[1]));
			}
			else
			{
				ev.setId(Integer.parseInt(i.getCode()));
			}
			ev.set_summary("" + i.getName());
			ev.setCode(i.getCode());
			// CALENDAR TEST
			// date_deb_gen = Converter.dtstart2UTC("20110609T000000Z");
			// date_end_gen = Converter.dtstart2UTC("20110715T180000Z");
			date_deb_gen = Converter.dtstart2UTC(parseDateFromErp("" + i.getInfos().get("min_date")));
			/* BLANCHET ALEXANDRE MODIFICATION 15/03/13 */
			date_end_gen = Converter.dtstart2UTC(parseDateFromErp("" + i.getInfos().get("date")));
			/* FIN MODIFICATION */
			logDebug(String.valueOf(date_deb_gen));
			logDebug(String.valueOf(date_end_gen));
			ev.set_begin_UTC_ms(date_deb_gen);
			ev.set_end_UTC_ms(date_end_gen);
			// Save of originale Date and Name
			ev.setInit_begin_UTC_ms(date_deb_gen);
			ev.setInit_end_UTC_ms(date_end_gen);
			ev.setInit_name("" + i.getName());
			// if(compress){
			// if(date_deb_gen_old != null && date_end_gen_old != null){
			//
			// if((date_deb_gen_old == date_deb_gen)
			// || (date_end_gen_old == date_end_gen)){
			//
			// if((num_ligne+1) < taille_interface){
			// // Jump line if 2 events on the same time
			// num_ligne++;
			// }
			// }
			// }
			// }
			ev.set_renderer_color(panel.getBG_MID_BUTTON_COLOR());
			try
			{
				if (!(i.getInfos().get("parent_id").equals("False")))
				{
					int parent_id = Integer.parseInt((String) i.getInfos().get("parent_id"));
					if (colorSchema.get(parent_id) != null)
					{}
					else
					{
						// Generate Random Color, different for the eye
						int Rcolor = (45 * (1 + colorSchema.size()));
						int Gcolor = (75 * (1 + colorSchema.size()));
						int Bcolor = (95 * (1 + colorSchema.size()));
						while (Rcolor > 255)
						{
							Rcolor = Rcolor - 100;
						}
						while (Gcolor > 255)
						{
							Gcolor = Gcolor - 100;
						}
						while (Bcolor > 255)
						{
							Bcolor = Bcolor - 100;
						}
						Random rand = new Random();
						// < (500) for have light color
						while ((Rcolor + Gcolor + Bcolor) < (255 + 255))
						{
							int rn = rand.nextInt(2) + 1;
							if (Rcolor < 225 && rn == 1)
							{
								Rcolor = Rcolor + 5;
							}
							else if (Rcolor > 0 && rn == 2)
							{
								Rcolor = Rcolor - 5;
							}
							if (Gcolor < 225 && rn == 1)
							{
								Gcolor = Gcolor + 5;
							}
							else if (Gcolor > 0 && rn == 2)
							{
								Gcolor = Gcolor - 5;
							}
							if (Bcolor < 225 && rn == 1)
							{
								Bcolor = Bcolor + 5;
							}
							else if (Bcolor > 0 && rn == 2)
							{
								Bcolor = Bcolor - 5;
							}
						}
						colorSchema.put(parent_id, new Color(Rcolor, Gcolor, Bcolor));
					}
					ev.set_renderer_color(colorSchema.get(parent_id));
				}
			}
			catch (Exception e)
			{
				ev.set_renderer_color(panel.getBG_MID_BUTTON_COLOR());
			}
			if (panel.genIsSelected(i))
			{
				ev.setSelected(true);
			}
			else
			{
				ev.setSelected(false);
			}
			// if(!compress)num_ligne++;
			num_ligne++;
			listEvent.add(ev);
		}
	}

	public HashMap<String, Object> getCalendarData()
	{
		return calendarData;
	}

	public void setCalendarData(HashMap<String, Object> calendarData)
	{
		this.calendarData = calendarData;
	}

	public ArrayList<Event>	listEvent;

	public String parseDateFromErp(String date)
	{
		date = date.replace(" ", "T");
		date = date.replace(":", "");
		date = date.replace("-", "");
		date = date + "Z";
		return date;
	}

	public String parseDateToErp(Long date)
	{
		Date tmp_date = new Date(date);
		String gMt = String.valueOf(tmp_date.getMonth() + 1);
		String gD = String.valueOf(tmp_date.getDate());
		String gH = String.valueOf(tmp_date.getHours());
		String gM = String.valueOf(tmp_date.getMinutes());
		if (gMt.length() == 1)
			gMt = "0" + gMt;
		if (gD.length() == 1)
			gD = "0" + gD;
		if (gH.length() == 1)
			gH = "0" + gH;
		if (gM.length() == 1)
			gM = "0" + gM;
		String converted_date = (tmp_date.getYear() + 1900) + "-" + gMt + "-" + gD + " " + gH + ":" + gM + ":00";
		logDebug(converted_date);
		return converted_date;
	}

	public void synchroDate(Event event, boolean delete)
	{
		calendarData.remove("pageEnCours");
		calendarData.put("pageEnCours", panel.getDateLookInstance().getDateLookPanel().getVerticalListEnCours());
		HashMap<String, Object> params = new HashMap<String, Object>(currentData);
		DASError response = null;
		String keyEventOverride = "override";
		String keyEventDelete = "delete";
		try
		{
			String keyModel = "model";
			Object elemModel = "synchro_date_" + currentDataModel.getCode();
			String keyInitMinDateFull = "init_min_date_full";
			Object elemInitMinDateFull = parseDateToErp(event.getInit_begin_UTC_ms());
			String keyInitMaxDateFull = "init_max_date_full";
			Object elemInitMaxDateFull = parseDateToErp(event.getInit_end_UTC_ms());
			String keyEventInitName = "init_name";
			Object elemEventInitName = "" + event.getInit_name();
			String keyMinDate = "min_date";
			Object elemMinDate = Converter.ms2hm(event.get_begin_UTC_ms());
			String keyMaxDate = "max_date";
			Object elemMaxDate = Converter.ms2hm(event.get_end_UTC_ms());
			String keyMinDateFull = "min_date_full";
			Object elemMinDateFull = parseDateToErp(event.get_begin_UTC_ms());
			String keyMaxDateFull = "max_date_full";
			Object elemMaxDateFull = parseDateToErp(event.get_end_UTC_ms());
			String keyWorkstationDate = "workstation_code";
			Object elemDateWorkstation = Integer.parseInt(DASLoader.getWorkstation().getCode());
			String keyEventName = "name";
			Object elemEventName = "" + event.get_summary();
			String keyEventId = "id";
			Object elemEventId = event.getId();
			params.put(keyModel, elemModel);
			params.put(keyEventOverride, false);
			params.put(keyEventDelete, delete);
			params.put(keyMinDate, elemMinDate);
			params.put(keyMaxDate, elemMaxDate);
			params.put(keyInitMinDateFull, elemInitMinDateFull);
			params.put(keyInitMaxDateFull, elemInitMaxDateFull);
			params.put(keyEventInitName, elemEventInitName);
			params.put(keyMinDateFull, elemMinDateFull);
			params.put(keyMaxDateFull, elemMaxDateFull);
			params.put(keyWorkstationDate, elemDateWorkstation);
			params.put(keyEventName, elemEventName);
			params.put(keyEventId, elemEventId);
			response = (DASError) dataAccess.getFromErp("synchro_date_" + currentDataModel.getCode(), (Serializable) params);
			logDebug("ERROR CODE ==> " + response.getCode());
			int cR = checkResponse(response);
			if (response.getCode().equals(4))
			{
				if (cR == 0)
				{
					params.remove("keyEventOverride");
					params.put(keyEventOverride, true);
					try
					{
						response = (DASError) dataAccess.getFromErp("synchro_date_" + currentDataModel.getCode(), (Serializable) params);
						logDebug("ERROR CODE ==> " + response.getCode());
						checkResponse(response);
					}
					catch (TimeoutException e)
					{
						logErr(e.getMessage());
						correctFunction(I18n._("Time out."));
						if (background == false)
							panel.showError(I18n._("Time out."));
					}
				}
				else
				{
					panel.showError(I18n._("Changing dates cancellation"));
				}
			}
		}
		catch (TimeoutException e)
		{
			logErr(e.getMessage());
			correctFunction("Time out.");
			if (background == false)
				panel.showError("Time out.");
		}
		// Refresh after synchro
		generics = null;
		if (isGenericsToDisplay())
		{
			displayScreenFilter();
		}
		if (background == false)
		{
			panel.refreshCalendar(null);
			panel.blockScreen(false);
		}
	}

	private void updateDepHistoryList()
	{
		// for (List<DASGeneric> i : depEnCours)
		// logDebug("BDH => " + i);
		depEnCours.add(generateLastDep(generics));
		// for (List<DASGeneric> i : depEnCours)
		// logDebug("ADH => " + i);
	}

	public void displayDep()
	{
		//
		// if (subDataEnCours.isEmpty()) {
		// updateDepHistoryList();
		// }
		// Display (all) Dep
		if (displayDep)
		{
			panel.cleanPanelBeforeDisplay();
			for (int i = 0; i < depEnCours.size(); i++)
			{
				if (i == depEnCours.size() - 1)
				{
					panel.switchGenericsAll(null, depEnCours, panelSelected, new HashMap<String, Object>());
					panelSelected++;
				}
			}
			// Hide All Empty Panel
			for (int i = 0; i < panel.getDepNoData().size(); i++)
			{
				if (panel.getDepNoData().get(i).equals("false"))
				{
					panel.getPanelCenterBloc().getComponent(i).setVisible(false);
				}
			}
		}
	}

	private List<DASGeneric> generateGenericsFiltre(List<DASGeneric> generics, String Parent)
	{
		List<DASGeneric> genericsFiltre = new LinkedList<DASGeneric>();
		for (DASGeneric i : generics)
		{
			if (Parent != null && currentFunctionList != null && i.getCodeDependency() != null)
			{
				for (DASGeneric j : i.getCodeDependency())
				{
					if (j.getCode().equals(Parent))
					{
						genericsFiltre.add(i);
					}
				}
			}
			else
			{
				genericsFiltre.add(i);
			}
		}
		return genericsFiltre;
	}

	private List<DASGeneric> generateLastDep(List<DASGeneric> generics)
	{
		logDebug("generateLastDep");
		List<DASGeneric> depList = new LinkedList<DASGeneric>();
		if (generics != null)
		{
			for (DASGeneric i : generics)
			{
				if (i.getCodeDependency() != null)
				{
					for (DASGeneric j : i.getCodeDependency())
					{
						if (j != null)
						{
							int pId = 0;
							try
							{
								pId = currentDataModel.getParentId().getId();
							}
							catch (Exception e)
							{
								pId = j.getData_model().getId();
							}
							if (j.getData_model().getId().equals(pId))
							{
								boolean depExist = false;
								if (depList.size() > 0)
								{
									for (DASGeneric y : depList)
									{
										if (y.getCode().equals(j.getCode()))
											depExist = true;
									}
									if (depExist != true)
										depList.add(j);
								}
								else
								{
									depList.add(j);
								}
							}
						}
					}
				}
			}
		}
		return depList;
	}

	/**
	 * Returns the list of generics workstation corresponding to the model
	 * provided a parameter
	 * 
	 * @param data_model
	 *            the model data to retrieve
	 * @return the list of generics workstation corresponding to the model
	 *         provided a parameter
	 */
	public String getLoopType()
	{
		return LoopType;
	}

	/**
	 * Perform treatments necessary for the acquisition of the data and listens
	 * to the scanner
	 * 
	 * @param instruction
	 *            instruction to display the data acquisition
	 */
	private boolean	askingScan	= false;
	private boolean	askingBal	= false;

	public boolean getAskingScan()
	{
		return askingScan;
	};

	public boolean getAskingBal()
	{
		return askingBal;
	};

	/**
	 * Request a code from station material
	 * 
	 * @throws NoSuchPortException
	 */
	// Method which run reception listener material if material is simple or
	// material have background=1
	private void initListsMaterial(DASBaseMaterial material, DASController controller, String sequence) throws NoSuchPortException
	{
		if ((material.getType().isSimple().equals("true") && material.getType().getType_transmit_protocols().size() == 0) || (!material.getType().isSimple().equals("true") && material.getType().getType_transmit_protocols().size() == 1))
		{
			try
			{
				if (!material.getType().getIscumulative().equals("true"))
				{
					logDebug("Insert of " + material.getCode() + " in listEcouteurR:");
					listEcouteurR.add(material);
					if (material instanceof DASRJ45Material)
					{
						DASRJ45Material mat = (DASRJ45Material) material;
						mat.receive(material.getType().getType_transmit_protocols(), material.getProperty("BACKGROUND"), sequence, controller);
					}
					else if (material instanceof DASCOMMaterial)
					{
						DASCOMMaterial mat = (DASCOMMaterial) material;
						mat.receive(material.getType().getType_transmit_protocols(), material.getProperty("BACKGROUND"), sequence, controller);
					}
					else
					{
						logDebug("Material is a physic keyboard");
					}
				}
				else
				{
					logDebug("Material is a physic keyboard");
				}
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			logDebug("Insert of " + material.getCode() + "in listEcouteurER:");
			listEcouteurER.add(material);
		}
	}

	// Method which run emission/reception if configurable material is lied at
	// datamodel in progress
	private void askMaterials(String matcode, DASPanel panel)
	{
		logDebug("Passing by askMaterials");
		Map<String, String> dataReceived = null;
		// Browse of list configurable materials which have background = 0
		for (DASBaseMaterial material : listEcouteurER)
		{
			// Browse listeners list which are already initialized to watch if
			// current listener exists already or not
			if (material != null)
			{
				if (!material.getType().isSimple().equals("true") && material.getCode().equals(matcode))
				{
					try
					{
						// If material has transmission protocol types
						if (!matcode.equals("vkb") && material.getType().getType_transmit_protocols() != null)
						{
							// Recuperation data material after E/R
							try
							{
								if (material instanceof DASRJ45Material)
								{
									DASRJ45Material mat = (DASRJ45Material) material;
									dataReceived = mat.send(matcode, material.getType().getType_transmit_protocols());
								}
								else if (material instanceof DASCOMMaterial)
								{
									DASCOMMaterial mat = (DASCOMMaterial) material;
									dataReceived = mat.send(matcode, material.getType().getType_transmit_protocols());
								}
								// Updating of the context
								Map<String, Object> context = superContext;
								if (dataReceived != null)
								{
									for (Map.Entry<String, String> data : dataReceived.entrySet())
									{
										if (context.get("mapMaterial") instanceof Map<?, ?>)
										{
											Map<String, String> mapMaterial = new HashMap<String, String>();
											mapMaterial = (Map<String, String>) context.get("mapMaterial");
											if (mapMaterial.containsKey(data.getKey()))
											{
												mapMaterial.put(data.getKey(), data.getValue());
											}
											else
											{
												mapMaterial.put(data.getKey(), data.getValue());
											}
											superContext.put("mapMaterial", mapMaterial);
										}
										else
										{
											Map<String, String> mapMaterial = new HashMap<String, String>();
											mapMaterial.put(data.getKey(), data.getValue());
											superContext.put("mapMaterial", mapMaterial);
										}
									}
									// Browse of typetransmitprotocol to check
									// if calcul line is present
									Map<String, String> mapMaterial = (Map<String, String>) superContext.get("mapMaterial");
									try
									{
										for (DASTypeTransmitProtocol typetransmit : material.getType().getType_transmit_protocols())
										{
											if (typetransmit.getType().equals("c"))
											{
												if (!typetransmit.getLanguage().equals(""))
												{
													// Initializing to eval
													// calcul code
													ScriptEngineManager mgr = new ScriptEngineManager();
													ScriptEngine calculEngine = mgr.getEngineByName(typetransmit.getLanguage());;
													// Recuperation of
													// mapMapterial in context
													// to calcul
													calculEngine.put("mapMaterial", mapMaterial);
													try
													{
														logDebug("evaluation code");
														calculEngine.eval(typetransmit.getName());
													}
													catch (ScriptException e)
													{
														e.printStackTrace();
													}
												}
											}
										}
									}
									catch (NullPointerException ne)
									{
										logErr("None typetransmitprotocol calcul type");
									}
									if (mapMaterial.containsKey(this.getDataModelEnCours().getCode().toUpperCase()))
									{
										panel.updateKeyboardView(mapMaterial.get(this.getDataModelEnCours().getCode().toUpperCase()), matcode);
									}
								}
								break;
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
							// Update data display for the current material
						}
						else
						{
							if (matcode.equals("vkb"))
							{
								logDebug("VKB DETECTED ON LIST");
							}
						}
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	/*
	 * if (material_configurable != null) { if
	 * (acMaterial.contains(material_configurable)) { askingBal = true;
	 * panel.balanceButton(askingBal); } }
	 */
	/**
	 * Stop the askMaterials request
	 */
	public void stopAskMaterials()
	{
		/* askingScan = false; */
		for (DASBaseMaterial material : acMaterial)
		{
			if (material != null)
			{
				material.setStopAsking(false);
			}
		}
	}

	/**
	 * Indicates whether the data given in parameter is valid according to the
	 * model validation in progress (usually updated before the execution of the
	 * method of data entry)
	 * 
	 * @param donnee
	 *            the data to validate
	 * @return vrai return true if valid, else false
	 */
	private Object validData(String donnee)
	{
		Object objectReturn = null;
		// SCANNER MODEL SCANNER
		// validModelEnCours = "SSCC";
		// validModelEnCours = "EAN13";
		// validModelEnCours = "SSCC";
		if (validModelEnCours == null)
		{
			objectReturn = donnee;
		}
		else if (validModelEnCours.equals("EAN13"))
		{
			objectReturn = (validean13(donnee));
		}
		else if (validModelEnCours.equals("SSCC") && donnee.startsWith(EAN128prefix + SSCCprefix))
		{
			objectReturn = (validean128(donnee));
		}
		else if (validModelEnCours.equals("EAN128") && donnee.startsWith(EAN128prefix))
		{
			if (!(donnee.startsWith(EAN128prefix + SSCCprefix)))
			{
				objectReturn = (validean128(donnee));
			}
		}
		else if (validModelEnCours.equals("alpha"))
		{
			if (donnee.matches("[A-Z]*,[0-9]*"))
				objectReturn = donnee;
		}
		else if (validModelEnCours.equals("alphaMin"))
		{
			if (donnee.matches("[a-zA-Z0-9]*"))
				objectReturn = donnee;
		}
		else if (validModelEnCours.equals("barre"))
		{
			if (donnee.matches("[a-zA-Z0-9]*,[0-9]*"))
				objectReturn = donnee;
		}
		else if (validModelEnCours.equals("int"))
		{
			if (donnee.matches("[0-9]*"))
				objectReturn = donnee;
		}
		else if (validModelEnCours.equals("num"))
		{
			if (donnee.matches("[0-9]*\\.?[0-9]*"))
				objectReturn = donnee;
		}
		else
		{
			logErr("Validation model '" + validModelEnCours + "' unknow");
			objectReturn = null;
		}
		return objectReturn;
	}

	private Map<String, ArrayList<List<String>>> validean13(String code13)
	{
		Map<String, ArrayList<List<String>>> listEAN13 = new HashMap<String, ArrayList<List<String>>>();
		listEAN13 = parserEAN.parseEAN13(code13);
		return listEAN13;
	}

	private Object validean128(String code128)
	{
		logDebug("VALIDEAN128 : " + code128);
		Map<String, ArrayList<List<String>>> listEAN128 = new HashMap<String, ArrayList<List<String>>>();
		listEAN128 = parserEAN.parseEAN128(code128);
		return listEAN128;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	private boolean validSubData(String data, int cc)
	{
		boolean valid = false;
		int cc_before = 0;
		int cc_after = 0;
		for (String i : subDataEnCours)
		{
			if (i.equals(data))
			{
				++cc_before;
			}
		}
		cc_after = cc_before + cc;
		int qty_max = -1;
		int qty_min = -1;
		if (generics != null)
		{
			for (DASGeneric generic : generics)
			{
				if (generic.getCode().equals(data))
				{
					qty_max = generic.getQtyMax();
					qty_min = generic.getQtyMin();
				}
			}
		}
		if (qty_min == qty_max && qty_max != -1)
		{
			if (cc_after != qty_max)
			{
				instruction = "Quantity required " + qty_max + " Quantity " + cc_after + " " + instructionList.get(instructionList.size() - 1);
			}
			else
			{
				instruction = "OK " + qty_max + " Quantity " + cc_after + " " + instructionList.get(instructionList.size() - 1);
				dataActive.put(data, false);
			}
		}
		else if (qty_max > 0 && cc_after > qty_max)
		{
			instruction = "Quantity Maximum " + qty_max + " Quantity " + cc_after + " " + instructionList.get(instructionList.size() - 1);
		}
		else if (qty_min > 0 && cc_after < qty_min)
		{
			instruction = "Quantity Minimum " + qty_min + " Quantity " + cc_after + " " + instructionList.get(instructionList.size() - 1);
		}
		else
		{
			instruction = "Quantity " + cc_after + ", " + instructionList.get(instructionList.size() - 1);
		}
		valid = true;
		return valid;
	}

	/**
	 * Initializes the data to start a new function
	 * 
	 * @return the name of the function to execute
	 */
	public void initFunction()
	{
		System.out.println("Instruction : "+ instruction);

		if (background == false)
		{
			panel.blockScreen(true);
			panel.addEndButton(null);
		}
		if (subDataEnCours != null && currentDataModel != null)
		{
			if (!(subDataEnCours.isEmpty()))
			{
				logDebug("dMEC -> " + currentDataModel.getCode());
				logDebug("subDataEC -> " + subDataEnCours);
				currentData.put((String) currentFunction, subDataEnCours);
			}
		}
		LoopType = null;
		LoopEnd = null;
		subDataEnCours = new LinkedList<String>();
		storeDataModel = new LinkedList<HashMap<String, Object>>();
		depEnCours = new LinkedList<List<DASGeneric>>();
		calendarData = new HashMap<String, Object>();
		++currentActionPosition;
		logDebug("current sequence  : " + sequenceEnCours + " current actionPosition : " + currentActionPosition);
		String function = fctParams.fctParams_get(sequenceEnCours, currentActionPosition);
		logDebug("functionEnCours :" + function);
		functional_context.put("_function", fctParams.fctParams_get2(sequenceEnCours, currentActionPosition));
		
		currentFunction = function;
		logDebug("==== Function en Cours : " + currentFunction + " ====");
		
		
		System.out.println("Instruction already defined :"+ this.instructionsAlreadyDefined);
		
		
		if (((DASFunctions) functional_context.get("_function")) != null)
		{
			/* TODO transformer pour nouveau parser */
			setEan128GetterMethodEnCours(fctParams.fctParams_get(sequenceEnCours, currentActionPosition, "_EAN128"));
			logDebug("EAN128GETTER : " + sequenceEnCours + currentActionPosition + "_EAN128");
			logDebug("EAN128GETTER : " + ean128GetterMethodEnCours);
			DASFunctions loop = ((DASFunctions) functional_context.get("_function")).get_child("loop");
			if (loop != null)
			{
				logDebug("------LOOP-------");
				if (loop.containsKey("NB"))
				{
					logDebug("-- NB -- " + loop.get("NB"));
					setLoop((String) loop.get("NB"));
				}
				if (loop.containsKey("ENDNAME"))
				{
					logDebug("-- ENDNAME -- " + loop.get("ENDNAME"));
					setEndLoopName((String) loop.get("ENDNAME"));
				}
				if (loop.containsKey("TYPE"))
				{
					logDebug("-- TYPE -- " + loop.get("TYPE"));
					setLoopType((String) loop.get("TYPE"));
				}
				if (loop.containsKey("END"))
				{
					logDebug("-- END -- " + loop.get("END"));
					setLoopEnd((String) loop.get("END"));
				}
			}
			if (calendarData.isEmpty())
			{
				calendarData = new HashMap<String, Object>();
				DASFunctions calendar = ((DASFunctions) functional_context.get("_function")).get_child("calendar");
				if (calendar != null)
				{
					logDebug("------calendar-------");
					if (calendar.containsKey("DELTAT"))
					{
						logDebug("-- DELTAT -- " + calendar.get("DELTAT"));
						calendarData.put("calendarDeltaT", calendar.get("DELTAT"));
					}
					if (calendar.containsKey("LOCKNAME"))
					{
						logDebug("-- LOCKNAME -- " + calendar.get("LOCKNAME"));
						calendarData.put("calendarLockName", calendar.get("LOCKNAME"));
					}
					if (calendar.containsKey("LOCKDATE"))
					{
						logDebug("-- LOCKDATE -- " + calendar.get("LOCKDATE"));
						calendarData.put("calendarLockDate", calendar.get("LOCKDATE"));
					}
					calendarData.put("gc", null);
					calendarData.put("pageEnCours", 0);
				}
			}
		
			String name = (String) ((Map<String, Object>) functional_context.get("_function")).get("NAME");

			if(this.instructionsAlreadyDefined == false){
				DASFunctions instruction = ((DASFunctions) functional_context.get("_function")).get_child("instruction");
				if (instruction != null) { 
					if (instruction.containsKey("_value") && !((String)instruction.get("_value")).equals("")) { 
						setInstruction((String) instruction.get("_value"));
					} 
				}else{
					setInstruction("");
				}		
			}

			Object functions = functional_context.get("_function");
			
			DASFunctions supervision = ((DASFunctions) functional_context.get("_function")).get_child("supervision");
			if (supervision != null)
			{
				logDebug("------SUPERVISION-------");
				if (supervision.containsKey("NAME"))
				{
					logErr("-- NAME -- " + supervision.get("NAME"));
					setTemplateSupervisionEnCours((String) supervision.get("NAME"));
				}
			}
			
		}
		askingScan = false;
		askingBal = false;
		if (subDataEnCours != null)
		{
			updateHistoriqueLists(function);
		}
	}

	private String	ean128GetterMethodEnCours;

	public String getEan128GetterMethodEnCours()
	{
		return ean128GetterMethodEnCours;
	}

	public void setEan128GetterMethodEnCours(String ean128GetterMethodEnCours)
	{
		this.ean128GetterMethodEnCours = ean128GetterMethodEnCours;
	}

	public void setPage(int page)
	{
		if (background == false)
		{
			panel.setPageEnCours(page);
		}
	}

	/**
	 * Initializes a possible loop @ Param nbTours the number of rounds ("0" for
	 * unlimited loop, null in the abscence of loop)
	 */
	private void setLoop(String nbTours)
	{
		int nbToursInt = 1;
		if (nbTours != null)
		{
			try
			{
				nbToursInt = getNbTours(nbTours);
			}
			catch (NumberFormatException e)
			{
				logErr("Bad format of loop in functional xml : loop must be contain a integer superior or equal at 0");
			}
		}
		nbLoops = nbToursInt;
		endCodeFound = false;
	}

	/**
	 * Record the name of the button of end of unlimited loop.
	 * 
	 * @param endloop
	 *            button name of the loop that you want unlimited
	 */
	private void setEndLoopName(String endLoop)
	{
		if (nbLoops == 0)
		{
			if (endLoop != null)
			{
				endLoopName = endLoop;
			}
			else
			{
				endLoopName = "End";
			}
		}
	}

	/**
	 * Saves the instruction to display to the user for the acquisition @ Param
	 * statement the statement to display to the user
	 */
	public void setInstruction(String instruction)
	{
		if (instruction != null)
		{
			this.instruction = instruction;
		}
		else
		{
			this.instruction = "";
		}
	}

	/**
	 * Converts a number of rounds as a string
	 * 
	 * @Param nbTours the number of rounds made in the form of a string ("0" for
	 *        unlimited looping)
	 * @Return the number of rounds made in the form of an integer (0: unlimited
	 *         loop, 1: no loop, greater than 1: the number of rounds to do)
	 * @Throws NumberFormatException exception raised if the number of rounds is
	 *         invalid
	 */
	private int getNbTours(String nbTours) throws NumberFormatException
	{
		int nbToursInt = Integer.parseInt(nbTours);
		if (nbToursInt < 0)
		{
			throw new NumberFormatException();
		}
		return nbToursInt;
	}

	/**
	 * Indicates if we are outside a loop or if it is finished
	 * 
	 * @Return true if the loop is completed or outside a loop, false if not
	 *         (within a loop not completed)
	 */
	private boolean endedLoop()
	{
		return (nbLoops == 1 || endCodeFound);
	}

	/**
	 * Indicates if a loop is being unlimited @ Return true if a loop is being
	 * unlimited, false otherwise
	 */
	public boolean unlimitedLoop()
	{
		return (nbLoops == 0);
	}

	/**
	 * Goes to the next round of a loop
	 */
	private void nextLoop()
	{
		if (!unlimitedLoop())
		{
			--nbLoops;
		}
	}

	/**
	 * Method called to signal the end of a loop unlimited
	 */
	private void loopRoundOff()
	{
		endCodeFound = true;
	}

	/**
	 * Prepare to start a new sequence by removing the previous data and
	 * initializing the data needed
	 * 
	 * @Param sequence the sequence of functions we prepare to execute
	 */
	public void initActionBtn(String sequence, boolean allPanelsVisible)
	{
		logDebug("Passing by initActionBtn");
		
		dataModelEnCoursList = new LinkedList<DASDataModel>();
		currentDataModel = null;
		dataModelsList = new LinkedList<Map<String, DASDataModel>>();
		dataModels = new HashMap<String, DASDataModel>();
		depHistoryList = new LinkedList<List<DASGeneric>>();
		etapeList = new LinkedList<String>();
		currentFunctionList = new LinkedList<Object>();
		currentFunction = null;
		instructionList = new LinkedList<String>();
		subDataEnCoursList = new LinkedList<List<String>>();
		dataActive = new HashMap<String, Boolean>();
		storeDataModelList = new LinkedList<List<HashMap<String, Object>>>();
		panelSelected = -1;
		dataEnCoursList = new LinkedList<Map<String, List<String>>>();
		currentData = new HashMap<String, List<String>>();
		subDataEnCours = null;
		storeDataModel = new LinkedList<HashMap<String, Object>>();
		sequenceEnCours = sequence;
		superContext.put("_sequenceEnCours", fctParams.fctParams_get2(sequence));
		currentActionPosition = 0;
		functional_context = new DASFunctions();
		nbLoops = 1;
		askingScan = false;
		askingBal = false;
		genericsFiltred = new LinkedList<DASGeneric>();
		generics = new LinkedList<DASGeneric>();
		if (background == false)
		{
			panel.cleanPanelBeforeDisplay();
			panel.setVisibleAllPanels(allPanelsVisible);
			panel.correctButton(false);
			panel.cancelButton(true);
		}
		else
		{
			logDebug("background : true");
		}
		depEnCours = new LinkedList<List<DASGeneric>>();
		depEnCoursList = new LinkedList<List<List<DASGeneric>>>();
		checkFunctionLoop(sequenceEnCours);
		codeEntier = null;
		instruction = null;
		listWsGenericsExt = null;
		superContextList = new LinkedList<HashMap<String, Object>>();
		calendarData = new HashMap<String, Object>();
	}

	/**
	 * Displays the capture in the field indicated @ Param entry the entry to
	 * display @ Param field the field where the data must be displayed
	 */
	public void displayData(String saisie, JTextField field)
	{
		field.setText(saisie);
	}

	/**
	 * Updates the data model and validation in progress
	 * 
	 * @Param name data_model data model to use
	 */
	private void majModelesEnCours(String data_model)
	{
		if (currentDataModel == null || !currentDataModel.getCode().equals(data_model))
		{
			currentDataModel = dataModels.get(data_model);
			if (currentDataModel == null)
			{
				try
				{
					currentDataModel = (DASDataModel) dataAccess.getFromServer("getDataModelWithId", data_model);
				}
				catch (TimeoutException e)
				{
					correctFunction("Time out.");
					if (background == false)
						panel.showError("Time out.");
					logErr(e.getMessage());
				}
				if (currentDataModel != null)
				{
					dataModels.put(data_model, currentDataModel);
				}
			}
		}
		if (manyCode)
		{
			List<String> subDataEnCoursTmp = new LinkedList<String>();
			subDataEnCoursTmp.add(valuesEnCours);
			currentData.put(codeEnCours, subDataEnCoursTmp);
		}
		if (currentDataModel != null && currentDataModel.getMaxLength() == null)
		{
			currentDataModel.setMaxLength(255);
		}
		if (currentDataModel != null)
		{
			validModelEnCours = currentDataModel.getCtrl();
		}
	}

	public void setSequenceEnCours(String sequenceEnCours)
	{
		this.sequenceEnCours = sequenceEnCours;
	}

	/**
	 * Method called when receiving a code by a scanner
	 * 
	 * @Param material the material sending the data(code,weight,..) to program
	 * @Param data the data sent by the material
	 */
	public void codeReceived(DASBaseMaterial material, String code)
	{
		if (background == false && displayKeyboard == true && material.getCode() != null)
		{
			panel.updateKeyboardView(code, material.getCode());
		}
		else if (background == false && displaySupervision == true && material.getCode() != null)
		{
			panel.updateSupervisionField(material.getCode(), code);
		}
		else
		{
			String sequence = material.getProperty("SEQUENCE");
			String priority = material.getProperty("PRIORITY");
			if (sequence != null)
			{
				logDebug("Actual sequence ==> " + sequenceEnCours);
				logDebug("Sequence is in background : " + background);
				logDebug("Received code sequence : " + sequence);
				if (sequenceEnCours != null && (priority == null || (priority != null && !priority.equals("1"))))
				{
					logDebug("Actual sequence can't be interrupt");
				}
				else
				{
					logDebug("ACTION BTN");
					actionBtn(sequence);
					if (background == false)
					{
						panel.showSequence(sequence);
						panel.displayGeneric();
					}
				}
			}
			if (sequenceEnCours != null)
			{
				logDebug("RECEIVED DATA");
				receivedData(code, -1);
			}
		}
	}

	/**
	 * Return external generics list who could be show by the workstation
	 * 
	 * @return generics list the workstation is suceptible display at one time
	 *         or another
	 * @throws TimeoutException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<DASGeneric>> getListWsGenericsStatic() throws TimeoutException
	{
		// if (listWsGenerics==null) {
		listWsGenerics = new HashMap<String, List<DASGeneric>>();
		List<DASGeneric> generics = new LinkedList<DASGeneric>();
		generics = (List<DASGeneric>) dataAccess.getFromServer("getWsGenericsWithIdOrWithout", DASLoader.getWorkstationCode());
		for (DASGeneric generic : generics)
		{
			if (listWsGenerics.get(generic.getData_model().getCode()) == null)
			{
				listWsGenerics.put(generic.getData_model().getCode(), new LinkedList<DASGeneric>());
			}
			// add only generics displayed by the station
			// (c'est-a-dire ceux de la station et si le type de selection est
			// 1, ceux sans station)
			if ((generic.getWorkstation().getCode().equals(DASLoader.getWorkstationCode())) || ((generic.getData_model().getTypeSelect() == 1) && (generic.getWorkstation().getCode().equals("-1"))))
			{
				listWsGenerics.get(generic.getData_model().getCode()).add(generic);
			}
		}
		// }
		return listWsGenerics;
	}

	/**
	 * Return external generics list who could be show by the workstation
	 * 
	 * @return generics list
	 * @throws TimeoutException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<DASGeneric>> getListWsGenericsStaticExt(String data_model) throws TimeoutException
	{
		if (!listWsGenericsExt.containsKey(data_model))
		{
			HashMap<String, Object> params = new HashMap<String, Object>(currentData);
			params.put("model", data_model);
			for (Entry<String, Object> entry : superContext.entrySet())
			{
				String key = entry.getKey();
				Object elem = entry.getValue();
				if (elem instanceof DASGeneric)
				{
					if (!params.containsKey(key))
					{
						List<String> tmp = new LinkedList<String>();
						tmp.add(((DASGeneric) elem).getCode());
						params.put(key, tmp);
					}
				}
			}
			DASError res = (DASError) dataAccess.getFromErp("getWsGenericsExt", params);
			logDebug("ERROR CODE ==> " + res.getCode());
			if (res.getCode().equals("1"))
			{
				logDebug("THIS IS NOT A ERROR");
			}
			else if (background == false)
			{
				panel.showError(res.getString());
			}
			List<DASGeneric> generics = (List<DASGeneric>) res.getObject();
			List<DASGeneric> depsok = getGenerics(dataModels.get(data_model).getParentId());
			for (DASGeneric generic : generics)
			{
				if (listWsGenericsExt.get(data_model) == null)
				{
					listWsGenericsExt.put(data_model, new LinkedList<DASGeneric>());
				}
				generic.setData_model(dataModels.get(data_model));
				List<DASGeneric> deps = generic.getCodeDependency();
				List<DASGeneric> depsokok = new LinkedList<DASGeneric>();
				if (dataModels.get(data_model).getParentId() != null && deps != null)
				{
					for (DASGeneric depok : depsok)
					{
						boolean ok = false;
						for (DASGeneric dep : deps)
						{
							if (dep.getCode().equals(depok.getCode()))
							{
								ok = true;
							}
						}
						if (ok)
						{
							depsokok.add(depok);
						}
					}
					generic.setListCodeDependency(depsok);
				}
				listWsGenericsExt.get(data_model).add(generic);
			}
		}
		return listWsGenericsExt;
	}

	/**
	 * Return external generics list who could be show by the workstation
	 * 
	 * @return generics list
	 * @throws TimeoutException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<DASGeneric>> getListWsGenericsExt(String data_model) throws TimeoutException
	{
		if (listWsGenericsExt == null)
			listWsGenericsExt = new HashMap<String, List<DASGeneric>>();
		listWsGenericsExt.put(data_model, null);
		HashMap<String, Object> params = new HashMap<String, Object>(currentData);
		params.put("model", data_model);
		for (Entry<String, Object> entry : superContext.entrySet())
		{
			String key = entry.getKey();
			Object elem = entry.getValue();
			if (elem instanceof DASGeneric)
			{
				if (!params.containsKey(key))
				{
					List<String> tmp = new LinkedList<String>();
					tmp.add(((DASGeneric) elem).getCode());
					params.put(key, tmp);
				}
			}
		}
		DASError res = (DASError) dataAccess.getFromErp("getWsGenericsExt", params);
		List<DASGeneric> generics = (List<DASGeneric>) res.getObject();
		if (generics != null)
		{
			List<DASGeneric> depsok = new LinkedList<DASGeneric>();
			try
			{
				params.put("model", currentDataModel.getParentId().getCode());
				DASError resParent = (DASError) dataAccess.getFromErp("getWsGenericsExt", params);
				depsok = (List<DASGeneric>) resParent.getObject();
			}
			catch (NullPointerException e)
			{}
			for (DASGeneric generic : generics)
			{
				generic.setData_model(dataModels.get(data_model));
				List<DASGeneric> deps = generic.getCodeDependency();
				List<DASGeneric> depsokok = new LinkedList<DASGeneric>();
				if (!depsok.isEmpty())
				{
					if (deps != null)
					{
						for (DASGeneric dep : deps)
						{
							for (DASGeneric depok : depsok)
							{
								boolean ok = false;
								if (dep.getCode().equals(depok.getCode()))
								{
									ok = true;
								}
								if (ok)
								{
									depok.setData_model(currentDataModel.getParentId());
									depsokok.add(depok);
								}
							}
						}
						generic.setListCodeDependency(depsokok);
					}
				}
				if (listWsGenericsExt.get(data_model) == null)
				{
					listWsGenericsExt.put(data_model, new LinkedList<DASGeneric>());
				}
				listWsGenericsExt.get(data_model).add(generic);
			}
		}
		else
		{
			logDebug("ERROR CODE ==> " + res.getCode());
			if (res.getCode().equals("1"))
			{
				logDebug("THIS IS NOT A ERROR");
			}
			else if (background == false)
			{
				panel.showError(res.getString());
			}
		}
		return listWsGenericsExt;
	}

	// public void imprimeEtiquette(DASSscc sscc, DASGeneric generic) {
	// DASEtiquetteM5BSotecMedical DASe = new DASEtiquette(sscc, generic);
	// Thread th = new Thread(DASe);
	// th.start();
	// }
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

	public DASPanel getPanel()
	{
		return panel;
	}

	public int getCurrentActionPosition()
	{
		return currentActionPosition;
	}

	public DASDataModel getDataModelEnCours()
	{
		return currentDataModel;
	}

	public Map<String, String> getKbParams()
	{
		return kbParams;
	}

	public String getInstruction()
	{
		return instruction;
	}

	public DASFunctions getFctParams()
	{
		return fctParams;
	}

	public String getValidModelEnCours()
	{
		return validModelEnCours;
	}

	public String getSequenceEnCours()
	{
		return sequenceEnCours;
	}

	public List<Map<String, DASDataModel>> getDataModelsPList()
	{
		return dataModelsList;
	}

	public List<Map<String, List<String>>> getDataEnCoursPList()
	{
		return dataEnCoursList;
	}

	public List<DASDataModel> getDataModelEnCoursPList()
	{
		return dataModelEnCoursList;
	}

	public List<String> getEtapeList()
	{
		return etapeList;
	}

	public Map<String, DASDataModel> getDataModels()
	{
		return dataModels;
	}

	public Map<String, List<String>> getDataEnCours()
	{
		return currentData;
	}

	public List<String> getInstructionList()
	{
		return instructionList;
	}

	public int getNbLoops()
	{
		return nbLoops;
	}

	public String getContext(String sequence)
	{
		String result = "";
		if (superContext.containsKey(sequence))
		{
			return ((DASGeneric) superContext.get(sequence)).getName();
		}
		return result;
	}

	public List<List<String>> getSubDataEnCoursList()
	{
		return subDataEnCoursList;
	}

	public List<String> getSubDataEnCours()
	{
		return subDataEnCours;
	}

	public void setSubDataEnCours(List<String> subDataEnCours)
	{
		this.subDataEnCours = subDataEnCours;
	}

	private void setLoopType(String type)
	{
		LoopType = type;
	}

	private void setLoopEnd(String end)
	{
		LoopEnd = end;
	}

	@SuppressWarnings("unchecked")
	public boolean getButtonAccess(String sequence)
	{
		boolean result = false;
		DASFunctions tmp = fctParams.fctParams_get2(sequence);
		if (tmp.containsKey("GROUPS"))
		{
			List<DASGeneric> tmp_group = new LinkedList<DASGeneric>();
			String[] tmp_str = ((String) tmp.get("GROUPS")).split(",");
			for (int i = 0; i < tmp_str.length; ++i)
			{
				DASGeneric tmp_gen = new DASGeneric();
				tmp_gen.setName(tmp_str[i]);
				tmp_gen.setCode(tmp_str[i]);
				tmp_group.add(tmp_gen);
			}
			if (superContext.containsKey("_groups"))
			{
				if (cmpGenericINTER(tmp_group, (List<DASGeneric>) superContext.get("_groups")).size() > 0)
				{
					result = true;
				}
			}
		}
		else if (tmp.containsKey("DISABLE") && (String) tmp.get("DISABLE") != null)
		{
			result = !DASExpression.getvalue(superContext, (String) tmp.get("DISABLE"));
		}
		else
		{
			result = true;
		}
		return result;
	}

	public List<DASGeneric> cmpGenericINTER(List<DASGeneric> a, List<DASGeneric> b)
	{
		List<DASGeneric> c = new LinkedList<DASGeneric>();
		for (int i = 0; i < a.size(); ++i)
		{
			for (int j = 0; j < b.size(); ++j)
			{
				if (b.get(j).getCode().equals(a.get(i).getCode()))
				{
					c.add(b.get(j));
				}
			}
		}
		return c;
	}

	public void setInitialize(boolean value)
	{
		initialize = value;
	}

	public HashMap<String, Object> getSuper_context()
	{
		return superContext;
	}

	// true if keyboard is displayed
	public DASEquipments getEquipments()
	{
		return equipments;
	}

	public boolean getAuthorizeDisplay()
	{
		return authorizeDisplay;
	}

	public void setPKB(boolean value)
	{
		physicalKeyboard = value;
	}

	public void setVKB(boolean value)
	{
		virtualkeyboard = value;
	}

	public boolean isEnabledPKB()
	{
		return physicalKeyboard;
	}

	public boolean isEnabledVKB()
	{
		return virtualkeyboard;
	}

	public void setDKB(boolean value)
	{
		displayKeyboard = value;
	}

	public boolean isEnabledDKB()
	{
		return displayKeyboard;
	}

	public void setAuthorizeDisplay(boolean value)
	{
		authorizeDisplay = value;
	}

	public ArrayList<Event> getListEvent()
	{
		return listEvent;
	}

	public void setListEvent(ArrayList<Event> listEvent)
	{
		this.listEvent = listEvent;
	}

	public DASTemplateSupervision getTemplateSupervisionEnCours()
	{
		return templateSupervisionEnCours;
	}

	public void setTemplateSupervisionEnCours(DASTemplateSupervision templateSupervisionEnCours)
	{
		this.templateSupervisionEnCours = templateSupervisionEnCours;
	}

	public void setTemplateSupervisionEnCours(String name)
	{
		
		for (DASTemplateSupervision ts : templateSupervisionList)
		{
			DASLog.logErr(this.getClass().getSimpleName(),"ERROR Picture:" + ts.getImage()+" Name:"+ ts.getName() );
			if (ts.getName().equals(name))
			{
				templateSupervisionEnCours = ts;
			}
		}
	}

	public String getSupervionBackgroundImagePath()
	{
		return supervionBackgroundImagePath;
	}

	public void setSupervionBackgroundImagePath(String supervionBackgroundImagePath)
	{
		this.supervionBackgroundImagePath = supervionBackgroundImagePath;
	}

	public boolean get_active(String code)
	{
		if (LoopEnd != null && LoopEnd.equals("block"))
			if (dataActive.containsKey(code))
				return dataActive.get(code);
		return true;
	}

	public List<DASBaseMaterial> getListEcouteurR()
	{
		return listEcouteurR;
	}

	public void setListEcouteurBackground(List<DASBaseMaterial> listEcouteurR)
	{
		this.listEcouteurR = listEcouteurR;
	}

	public List<DASBaseMaterial> getAcMaterial()
	{
		return acMaterial;
	}

	public void setAcMaterial(List<DASBaseMaterial> acMaterial)
	{
		this.acMaterial = acMaterial;
	}

	public boolean isInstructionsAlreadyDefined() {
		return instructionsAlreadyDefined;
	}

	public void setInstructionsAlreadyDefined(boolean instructionsAlreadyDefined) {
		this.instructionsAlreadyDefined = instructionsAlreadyDefined;
	}
}

/*class SortByName implements Comparator<DASGeneric>
{

	public int compare(DASGeneric g1, DASGeneric g2)
	{
		return g1.getName().compareTo(g2.getName());
	}
}*/