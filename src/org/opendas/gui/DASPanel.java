package org.opendas.gui;

import gnu.io.NoSuchPortException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.opendas.DASLoader;
import org.opendas.DASLog;
import org.opendas.calendar.DateLook;
import org.opendas.ctrl.DASController;
import org.opendas.equipment.DASBaseMaterial;
import org.opendas.equipment.DASCOMMaterial;
import org.opendas.equipment.DASEquipments;
import org.opendas.ext.DASFunctions;
import org.opendas.ext.DASParserXmlFcts;
import org.opendas.ext.DASParserXmlGui;
import org.opendas.gui.DASGuiParams.DASFontException;
import org.opendas.gui.DASGuiParams.DASTextAlignException;
import org.opendas.modele.DASAcquisitionMethod;
import org.opendas.modele.DASDataModel;
import org.opendas.modele.DASGeneric;
import org.opendas.modele.DASTemplateSupervision;
import org.opendas.supervision.DASSupervisionPanel;
import org.opendas.supervision.DASSupervisionPanel.DASInputGenerator;
import org.opendas.supervision.DASSupervisionPanel.DASSupervisor;
import org.opendas.supervision.DASSupervisionTopicListener;
import org.opendas.supervision.DASSupervisionTopicProducer;
import org.opendas.translate.I18n;

/**
 * Main Windows Panel 
 * @author vaznj repris par laugraudc et martineaua
 * @author mlaroche
 * @author fchauvigne
 * @author ablanchet
 */
public class DASPanel extends JPanel implements ComponentListener
{

	private DASFunctions							xmlbottombuttons				= null;
	private DASFunctions							xmltopbuttons					= null;
	private static final long						serialVersionUID				= 1L;
	private JPanel									panelTop;
	private JPanel									panelCenterBloc					= new JPanel(new GridBagLayout());
	public List<List<DASGeneric>>					genericsButtonsList				= new LinkedList<List<DASGeneric>>();
	private int										pageEnCours						= 0;
	private List<String>							sortedListPageButton			= new LinkedList<String>();
	private List<String>							shortcuts						= new LinkedList<String>();
	private JPanel									panelCenter;
	private int										numPanelEnCours					= 0;
	private List<JPanel>							panelBlocCenterComponentsList	= new LinkedList<JPanel>();
	private JPanel									panelBlocCenterComponents;
	private List<List<String>>						selectedButtonList				= new LinkedList<List<String>>();
	private List<List<DASGradientJToggleButton>>	buttonsEnCoursList				= new LinkedList<List<DASGradientJToggleButton>>();
	private List<String>							depNoData						= new LinkedList<String>();
	private String									depActive;
	private JPanel									panelFooter;
	private JTextField								informations;
	private int										CALENDAR_ROWS					= 10;
	private boolean									displayInfo						= false;
	private JTextArea								instructions;
	private List<DASGradientJToggleButton>			selectedButton					= new LinkedList<DASGradientJToggleButton>();
	private List<DASGradientJToggleButton>			buttons							= new ArrayList<DASGradientJToggleButton>();
	private List<JComponent>						bottomButtons					= new ArrayList<JComponent>();
	private HashMap<String, JComponent>				dictbottomButtons				= new HashMap<String, JComponent>();
	private List<JComponent>						topButtons						= new ArrayList<JComponent>();
	private HashMap<String, JComponent>				dicttopButtons					= new HashMap<String, JComponent>();
	private static Map<String, String>				guiParams;
	private static int								LINES_TOP_BUTTONS				= 3;
	private static int								LINES_BOTTOM_BUTTONS			= 2;
	private static int								LINES_MID_BUTTONS				= 5;
	private static int								COLUMNS_MID_BUTTONS				= 7;
	private static int								LINES_MID_BUTTONS_DEFAULT		= 5;
	private static int								COLUMNS_MID_BUTTONS_DEFAULT		= 7;
	public int										maxBtn;
	// liste des extensions d'images
	public static List<String>						extImages;
	/*
	 * On garde la liste des generics courants pour renvoyer le generic
	 * selectionné par l'utilisateur au controleur. 
	 */
	private List<DASGeneric>						generics;
	private static DASController					controller;
	private DASPrintPopup							printPopup;
	private JFrame									frame;
	final int										PANELDEP						= 666;

	// Data get by Scanner
	private String									directScannerEntry				= "";
	private String									keyconvert						= "";
	private List<DASGeneric>						depListEnCours;
	private List<List<DASGeneric>>					depList;
	private boolean									isCalendar						= false;
	private boolean									isSupervision					= false;
	private HashMap<String, Object>					calendarData;
	private JPanel									datelookCalendar;
	private DateLook								dateLookInstance;
	private JScrollBar 								verticalScrollBar;
	private String 									topPanelLittle 					= new String("normal");
	private static Dimension						DIMENSION_FRAME					= null;
	private static Dimension						DIMENSION_TOP_BUTTONS			= new Dimension(125, 50);
	private static Dimension						DIMENSION_MID_BUTTONS			= new Dimension(125, 50);
	private static Dimension						DIMENSION_BOTTOM_BUTTONS		= new Dimension(125, 50);
	private static Dimension						DIMENSION_LARGE_FIELDS			= new Dimension(500, 30);
	private static Font								TOP_BUTTON_FONT					= new Font("Times New Roman", Font.BOLD, 18);
	private static Font								TOP_FIELD_FONT					= new Font("Arial", Font.BOLD, 18);
	private static Font								MID_FONT						= new Font("Arial", Font.BOLD, 18);
	private static int								MID_LEN							= 15;
	private static Font								BOTTOM_FONT						= new Font("Arial", Font.BOLD, 18);
	private static Font								BOTTOM_FIELD_FONT				= new Font("Arial", Font.PLAIN, 18);
	private static Font								NEXT_FONT						= new Font("Arial", Font.BOLD, 28);
	private static Font								LARGE_FIELDS_FONT				= new Font("Arial", Font.BOLD, 18);
	
	/** font color of the top panel */
	private static Color BG_TOP_COLOR = new Color(238,238,238);
	/** */
	/** font color of the buttons of the top panel*/
	private static Color BG_TOP_BUTTON_COLOR = new Color(150,200,255);
	/** font color of fields of the top panel*/
	private static Color BG_TOP_FIELD_COLOR = new Color(238,238,238);
	/** font color of inactive buttons of the top panel*/
	private static Color BG_TOP_INACT_BUTTON_COLOR = new Color(238,238,238);
	/** gradation of buttons of the top panel (0=none,1=gradation 1)*/
	private static byte BG_TOP_GRADIENT = 1;
	/** background color of the state bar*/
	private static Color BG_LARGE_FIELDS_COLOR = new Color(238,238,238);
	/** writing color of the state bar*/
	private static Color FG_LARGE_FIELDS_COLOR = new Color(238,238,238);
	/** writing color of buttons of the top panel*/
	private static Color FG_TOP_BUTTON_COLOR = new Color(0,0,0);
	/** writing color of fields of the top panel*/
	private static Color FG_TOP_FIELD_COLOR = new Color(0,0,0);
	/** font color of fields of the top panel if error*/
	private static Color FG_ERROR_TOP_FIELD_COLOR = new Color(255,0,0);
	/** font color of fields of the top panel if ok*/
	private static Color							FG_OK_TOP_FIELD_COLOR			= new Color(0, 128, 0);
	/** text alignment of the top buttons*/
	private static int								TOP_BUTTON_TEXT_ALIGN			= SwingConstants.CENTER;
	/** text alignment of the top buttons*/
	private static int								TOP_FIELD_TEXT_ALIGN			= SwingConstants.LEFT;

	/** font color of the bottom panel*/
	private static Color BG_BOTTOM_COLOR = new Color(238,238,238);
	/** font color of buttons of the bottom panel*/
	private static Color BG_BOTTOM_BUTTON_COLOR = new Color(150,200,255);
	/** font color of fields of the bottom panel*/
	private static Color BG_BOTTOM_FIELD_COLOR = new Color(238,238,238);
	/** font color of inactive buttons of the bottom panel*/
	private static Color BG_BOTTOM_INACT_BUTTON_COLOR = new Color(238,238,238);
	/** gradation of buttons of the bottom panel (0=none, 1=gradation 1) */
	private static byte BG_BOTTOM_GRADIENT = 1;
	/** writing color of buttons of the bottom panel*/
	private static Color FG_BOTTOM_BUTTON_COLOR = new Color(0,0,0);
	/** writing color of fields of the bottom panel*/
	private static Color FG_BOTTOM_FIELD_COLOR = new Color(0,0,0);
	/** background color of navigation buttons*/
	private static Color BG_NEXT_COLOR = new Color(255,200,100);	
	/** gradation of navigation buttons(0=none, 1=gradation 1) */
	private static byte								BG_NEXT_GRADIENT				= 1;
	/** writing color of navigation buttons */
	private static Color							FG_NEXT_COLOR					= new Color(0, 0, 0);
	/** font color of the middle panel*/
	private static Color BG_MID_COLOR = new Color(238,238,238);
	/** font color of buttons of the middle panel*/
	private static Color BG_MID_BUTTON_COLOR = new Color(150,200,255);
	/** font color of inactives buttons of the middle panel*/
	private static Color BG_MID_INACT_BUTTON_COLOR = new Color(238,238,238);
	/** gradation of buttons of the middle panel (0=none, 1=gradation 1) */
	private static byte BG_MID_GRADIENT = 1;
	/** writing color of buttons of the middle panel*/
	private static Color FG_MID_BUTTON_COLOR = new Color(0,0,0);
	/** Text alignement of buttons of the middle panel*/
	private static int MID_BUTTON_TEXT_ALIGN = SwingConstants.CENTER;

	private DASKeyboardPanel 	keyboardPanel 		= null;
	private static final int 	BTN_CORRECT			= 1;
	private static final int 	BTN_VIEW				= 5;	
	private static final int 	BTN_NEXT				= 4;
	private static final int 	BTN_PREV				= 3;
	private String 				SWITCH_BTN_NAME					= "Keyboard view";

	public String convert(String c) {
		if (keyconvert.equals("UPPER"))
			return c.toUpperCase();
		return c;
	}
	/**
	 * Build the main panel
	 * @param window The window containing the panel
	 */

	public DASPanel(JFrame fenetre) throws NoSuchPortException {
		super();
		initExtImages();
		logDebug("Passage by DASPanel");
		frame = fenetre;
		frame.addKeyListener(new KeyAdapter() {

			public void keyTyped(KeyEvent ke)
			{	
				if (controller.isEnabledPKB() == true)
				{
					if (ke.getKeyChar() == Event.ENTER || ke.getKeyChar() == Event.TAB)
					{	
						logDebug("getAskingScan:"+controller.getAskingScan());
						if (controller.getAskingScan())
						{												
							DASEquipments equipments = controller.getEquipments();
							for(DASBaseMaterial mat : equipments.recupMaterials()){
								if(mat.getType().isSimple().equals("true") && mat.getType().getIscumulative().equals("true")){
									controller.codeReceived(mat,directScannerEntry);
								}
							}
						}else{
							validateData();
							keyboardPanel.cleanInputs();	
						}
						directScannerEntry = "";
					}else{
						char c = Character.toUpperCase(ke.getKeyChar());
						if(String.valueOf(c).matches("\\p{Alpha}") || String.valueOf(c).matches("\\p{Alnum}")){			
							directScannerEntry = directScannerEntry+c;
						}
					}
				}
			}

			public void keyReleased(KeyEvent e)
			{
				if (controller.isEnabledPKB() == true)
				{
					String character = convert(Character.toString((e.getKeyChar())));
					keyboardPanel.keyReleased(character, e.getKeyCode());
				}
			}
		});

		/*
		 * java.util.Enumeration<CommPortIdentifier> portEnum =
		 * CommPortIdentifier.getPortIdentifiers(); while (
		 * portEnum.hasMoreElements() ) { CommPortIdentifier portIdentifier =
		 * portEnum.nextElement();
		 * logDebug("SCANNER LANCER ON RECUP LISTE PORT : "
		 * +portIdentifier.getName()); }
		 */

		chargeGraphParams();

		controller = new DASController(this);
		printPopup = new DASPrintPopup(this);
		if (DASLoader.debugMode)
		{
			// DASContextViewer viewer = new DASContextViewer(controller);
			// DASLog.setViewer(viewer);
		}
		cleanHeadPanel();
		cleanBottomPanel();
	}

	/**
	 * Load differents extensions possibles of picture
	 */
	private void initExtImages()
	{// TODO Eventuellement a parametrer (et/ou
		// recuperer champ image de das_generic)
		extImages = new ArrayList<String>();
		extImages.add(".jpg");
		extImages.add(".JPG");
		extImages.add(".jpeg");
		extImages.add(".JPEG");
		extImages.add(".png");
		extImages.add(".PNG");
		extImages.add(".gif");
		extImages.add(".GIF");
	}

	/**
	 * Loads graphical interface parameters supplied by xml code
	 */
	private void chargeGraphParams() {

		DASGuiParams outils = DASGuiParams.getInstance();

		//chargement des parametres xml
		DASParserXmlGui parserXml = null;

		try {
			parserXml = new DASParserXmlGui(DASLoader.getGuiXml());
		} catch (TimeoutException e1) {
			e1.printStackTrace();
		}

		if(guiParams != parserXml.getParameters("main")){
			guiParams = parserXml.getParameters("main");
			
			try {
				logDebug(String.valueOf(guiParams));
				if (guiParams.get("FEN_GLOBAL_KEYBOARD_KEYBOARD") != null)
				{
					keyconvert = guiParams.get("FEN_GLOBAL_KEYBOARD_KEYBOARD");
					logDebug("Conversion of key : " + keyconvert);
				}
				else 
					logDebug("Conversion of key by default : " + keyconvert);
					
			} catch (NumberFormatException e) {
				e.printStackTrace();
				logDebug("Key convert by default: " + keyconvert);
			}
	
			
			try {
				if (guiParams.get("TOP_MODEL_STYLE_TYPE") != null)
				{
					topPanelLittle = guiParams.get("TOP_MODEL_STYLE_TYPE");
					logDebug("Menu style : " + topPanelLittle);
				}
				else{
					logDebug("Menu style by default : " + topPanelLittle);
				}
			}catch (NumberFormatException e){
				logDebug("Menu style by default : " + topPanelLittle);
			}
			
			try {
				DIMENSION_FRAME = outils.getDIM(guiParams, "FEN_GLOBAL_DIM");
				logDebug("frame size : " + DIMENSION_FRAME);
			}catch (NumberFormatException e){
				logDebug("DIMENSION_FRAME size by default : " + DIMENSION_FRAME);
			}
			
			try {
				DIMENSION_TOP_BUTTONS = outils.getDIM(guiParams, "TOP_BUTTONS_DIM");
				logDebug("max size button on the top line : " + DIMENSION_TOP_BUTTONS);
			}catch (NumberFormatException e){
				logDebug("max size button on the top line by default : " + DIMENSION_TOP_BUTTONS);
			}
			
			try {
				DIMENSION_MID_BUTTONS = outils.getDIM(guiParams, "MID_BUTTONS_DIM");
				logDebug("max size button on the middle : " + DIMENSION_MID_BUTTONS);
			}catch (NumberFormatException e){
				logDebug("max size button on the middle line by default : " + DIMENSION_MID_BUTTONS);
			}
			
			
			try {
				DIMENSION_BOTTOM_BUTTONS = outils.getDIM(guiParams, "BOTTOM_BUTTONS_DIM");
				logDebug("max size button on the bottom line : " + DIMENSION_BOTTOM_BUTTONS);
			}catch (NumberFormatException e){
				logDebug("max size button on the bottom line by default : " + DIMENSION_BOTTOM_BUTTONS);
			}
			
			try {
				DIMENSION_LARGE_FIELDS = outils.getDIM(guiParams, "TOP_STATE_DIM");
				logDebug("max size indicator : " + DIMENSION_LARGE_FIELDS);
			} catch (NumberFormatException e) {
				logDebug("max size indicator : " + DIMENSION_LARGE_FIELDS);
			}
			// nombre de bouton max sur une ligne du haut
			try
			{
				/*MODIFICATION TOPL EN LINES*/
				LINES_TOP_BUTTONS = outils.getNBB(guiParams, "TOP_MODEL_NBB_LINES");
				logDebug("max number button on the top line : " + LINES_TOP_BUTTONS);
			}
			catch (NumberFormatException e)
			{
				logDebug("max number button on the top line by default : " + LINES_TOP_BUTTONS);
			}
			try
			{
				CALENDAR_ROWS = outils.getNBB(guiParams, "TOP_MODEL_NBB_CALENDARL");
				logDebug("nombre de ligne pour le calendrier : " + CALENDAR_ROWS);
			} catch (NumberFormatException e){
				logDebug("max number button on the bottom line by default : " + CALENDAR_ROWS);
			}
			try
			{
				LINES_BOTTOM_BUTTONS = outils.getNBB(guiParams, "BOTTOM_MODEL_NBB_LINES");
				logDebug("max number button on the top line : " + LINES_BOTTOM_BUTTONS);
			}catch (NumberFormatException e){
				logDebug("max number button on the top line by default : " + LINES_BOTTOM_BUTTONS);
			}
			// max number button on the central screen line
			try{
				LINES_MID_BUTTONS_DEFAULT = outils.getNBB(guiParams, "MID_MODEL_NBB_MIDL");
				LINES_MID_BUTTONS = LINES_MID_BUTTONS_DEFAULT;
				logDebug("max number button on the central screen line: " + LINES_MID_BUTTONS);
			} catch (NumberFormatException e){
				logDebug("max number button on the central screen line by default : " + LINES_MID_BUTTONS);
			}
			// max number button on the central screen col: 
			try{
				COLUMNS_MID_BUTTONS_DEFAULT = outils.getNBB(guiParams, "MID_MODEL_NBB_COLS");
				COLUMNS_MID_BUTTONS = COLUMNS_MID_BUTTONS_DEFAULT;
				logDebug("max number button on the central screen col: " + COLUMNS_MID_BUTTONS);
			}catch (NumberFormatException e){
				logDebug("max number button on the central screen col by default: " + COLUMNS_MID_BUTTONS);
			}
			
			try {
				BG_LARGE_FIELDS_COLOR = outils.getColor(guiParams, "TOP_STATE_BG");
				logDebug("indicator background color : " + BG_LARGE_FIELDS_COLOR.toString());
			}catch (NumberFormatException e){
				logDebug("indicator background color by default : " + BG_LARGE_FIELDS_COLOR.toString());
			}
			
			try {
				FG_LARGE_FIELDS_COLOR = outils.getColor(guiParams, "TOP_STATE_FG");
				logDebug("indicator writing color: " + FG_LARGE_FIELDS_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("indicator writing color by default : " + FG_LARGE_FIELDS_COLOR.toString());
			}
			
			// background screen of the central screen 
			try {
				BG_MID_COLOR = outils.getColor(guiParams, "MID_MODEL_BG");
				logDebug("background screen of the central screen : " + BG_MID_COLOR.toString());
			}catch (NumberFormatException e){
				logDebug("background screen of the central screen by default : " + BG_MID_COLOR.toString());
			}

			// buttons font of the top screen
			try
			{
				TOP_BUTTON_FONT = outils.getFont(guiParams, "TOP_BUTTONS");
				logDebug("Buttons font of the top screen : " + TOP_BUTTON_FONT.toString());
			}catch (NumberFormatException e){
				logDebug("Buttons font of the top screen by default : " + TOP_BUTTON_FONT.toString());
			}catch (DASFontException e){
				logDebug("Buttons font of the top screen by default : " + TOP_BUTTON_FONT.toString());
			}
			
			// buttons font of the central screen
			try
			{
				MID_FONT = outils.getFont(guiParams, "MID_BUTTONS");
				logDebug("Buttons font of the central screen by default :" + MID_FONT.toString());
			}catch (NumberFormatException e){
				logDebug("Buttons font of the central screen by default :" + MID_FONT.toString());
			}catch (DASFontException e){
				logDebug("Buttons font of the central screen by default :" + MID_FONT.toString());
			}

			// buttons background color of the central screen 
			try {
				BG_MID_BUTTON_COLOR = outils.getColor(guiParams, "MID_BUTTONS_BG");
				logDebug("Buttons background color of the central screen : " + BG_MID_BUTTON_COLOR.toString());
			}catch (NumberFormatException e){
				logDebug("Buttons background color of the central screen by default : " + BG_MID_BUTTON_COLOR.toString());
			}
			// buttons gradient of the central screen
			try
			{
				BG_MID_GRADIENT = outils.getGradient(guiParams.get("MID_BUTTONS_BG_GRADIENT"));
				logDebug("Buttons gradient of the central screen" + BG_MID_GRADIENT);
			}catch (NumberFormatException e){
				logDebug("Buttons gradient of the central screen by default : " + BG_MID_GRADIENT);
			}
			// buttons font color of the central screen 
			try
			{
				FG_MID_BUTTON_COLOR = outils.getColor(guiParams, "MID_BUTTONS_FG");
				logDebug("Buttons font color of the central screen : " + FG_MID_BUTTON_COLOR.toString());
			}catch (NumberFormatException e){
				logDebug("Buttons font color of the central screen by default : " + FG_MID_BUTTON_COLOR.toString());
			}
			// inactives buttons background color of the central screen
			try
			{
				BG_MID_INACT_BUTTON_COLOR = outils.getColor(guiParams, "MID_INACTIVE-BUTTONS_BG");
				logDebug("Inactives buttons background color of the central screen: " + BG_MID_INACT_BUTTON_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Inactives buttons background color of the central screen by default" + BG_MID_INACT_BUTTON_COLOR.toString());
			}

			// buttons text alignment of the central screen
			
			try {
				MID_BUTTON_TEXT_ALIGN = outils.getTextAlign(guiParams.get("MID_BUTTONS_TEXT_ALIGN"));
				logDebug("Buttons text alignment of the central screen of the central screen: " + MID_BUTTON_TEXT_ALIGN);
			} catch (DASTextAlignException e){
				logDebug("Buttons text alignment of the central screen of the central screen by default: " + MID_BUTTON_TEXT_ALIGN);
			}

			// background color of the bottom screen
			try {
				BG_BOTTOM_COLOR = outils.getColor(guiParams, "BOTTOM_MODEL_BG");
				logDebug("Background color of the bottom screen : " + BG_BOTTOM_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Background color of the bottom screen by default: " + BG_BOTTOM_COLOR.toString());
			}

			// buttons background color of the bottom screen
			try {
				BG_BOTTOM_BUTTON_COLOR = outils.getColor(guiParams, "BOTTOM_BUTTONS_BG");
				logDebug("Buttons background color of the bottom screen: " + BG_BOTTOM_BUTTON_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Buttons background color of the bottom screen by default : " + BG_BOTTOM_BUTTON_COLOR.toString());
			}

			// fields background color of the bottom screen
			try {
				BG_BOTTOM_FIELD_COLOR = outils.getColor(guiParams, "BOTTOM_FIELDS_BG");
				logDebug("Fields background color of the bottom screen : " + BG_BOTTOM_FIELD_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Fields background color of the bottom screen by default: " + BG_BOTTOM_FIELD_COLOR.toString());
			}

			// inactive buttons background color of the bottom screen
			try {
				BG_BOTTOM_INACT_BUTTON_COLOR = outils.getColor(guiParams, "BOTTOM_INACTIVE-BUTTONS_BG");
				logDebug("Inactive buttons background color of the bottom screen : " + BG_BOTTOM_INACT_BUTTON_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Inactive buttons background color of the bottom screen by default : " + BG_BOTTOM_INACT_BUTTON_COLOR.toString());
			}

			// buttons gradation of the bottom screen
			try {
				byte gradient = outils.getGradient(guiParams.get("BOTTOM_BUTTONS_BG_GRADIENT"));
				BG_BOTTOM_GRADIENT = gradient;
				logDebug("Buttons gradation of the bottom screen : " + BG_BOTTOM_GRADIENT);
			} catch (NumberFormatException e) {
				logDebug("buttons gradation of the bottom screen by default : " + BG_BOTTOM_GRADIENT);
			}

			// buttons font of the bottom screen
			try {
				BOTTOM_FONT = outils.getFont(guiParams, "BOTTOM_BUTTONS");
				logDebug("Buttons font of the bottom screen : " + BOTTOM_FONT.toString());
			} catch (NumberFormatException e) {
				logDebug("Buttons font of the bottom screen by default : " + BOTTOM_FONT.toString());
			} catch (DASFontException e) {
				logDebug("Buttons font of the bottom screen by default : " + BOTTOM_FONT.toString());
			}
			
			try{
				LARGE_FIELDS_FONT = outils.getFont(guiParams, "TOP_STATE");
				logDebug("Indicator font : " + LARGE_FIELDS_FONT.toString());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				logDebug("Indicator font : " + LARGE_FIELDS_FONT.toString());
			} catch (DASFontException e) {
				e.printStackTrace();
				logDebug("Indicator font by default : " + LARGE_FIELDS_FONT.toString());
			}
			

			// buttons writing color of the bottom screen
			try {
				FG_BOTTOM_BUTTON_COLOR = outils.getColor(guiParams, "BOTTOM_BUTTONS_FG");
				logDebug("Buttons writing color of the bottom screen : " + FG_BOTTOM_BUTTON_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Buttons writing color of the bottom screen by default : " + FG_BOTTOM_BUTTON_COLOR.toString());
			}

			// fields writing color of the bottom screen
			try {
				FG_BOTTOM_FIELD_COLOR = outils.getColor(guiParams, "BOTTOM_FIELDS_FG");
				logDebug("Fields writing color of the bottom screen : " + FG_BOTTOM_FIELD_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Fields writing color of the bottom screen by default : " + FG_BOTTOM_FIELD_COLOR.toString());
			}

			// navigation buttons font
			try {
				NEXT_FONT = outils.getFont(guiParams, "BOTTOM_NEXT");
				logDebug("Navigation buttons font : " + NEXT_FONT.toString());
			} catch (NumberFormatException e) {
				logDebug("Navigation buttons font by default : " + NEXT_FONT.toString());
			} catch (DASFontException e) {
				logDebug("Navigation buttons font by default : " + NEXT_FONT.toString());
			}

			// navigation buttons background color
			try {
				BG_NEXT_COLOR = outils.getColor(guiParams, "BOTTOM_NEXT_BG");
				logDebug("Navigation buttons background color : " + BG_NEXT_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Navigation buttons background color by default : " + BG_NEXT_COLOR.toString());
			}

			// navigation buttons gradation
			try {
				byte gradient = outils.getGradient(guiParams.get("BOTTOM_NEXT_BG_GRADIENT"));
				BG_NEXT_GRADIENT = gradient;
				logDebug("Navigation buttons gradation : " + BG_NEXT_GRADIENT);
			} catch (NumberFormatException e) {
				logDebug("Navigation buttons gradation by default : " + BG_NEXT_GRADIENT);
			}

			// navigation button font color
			try {
				FG_NEXT_COLOR = outils.getColor(guiParams, "BOTTOM_NEXT_FG");
				logDebug("Navigation button font color : " + FG_NEXT_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Navigation button font color by default : " + FG_NEXT_COLOR.toString());
			}

			// font color of the top screen
			try {
				BG_TOP_COLOR = outils.getColor(guiParams, "TOP_MODEL_BG");
				logDebug("Font color of the top screen : " + BG_TOP_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Font color of the top screen : " + BG_TOP_COLOR.toString());
			}

			// fields font of the top screen
			try {
				TOP_FIELD_FONT = outils.getFont(guiParams, "TOP_FIELDS");
				logDebug("Fields font of the top screen : " + TOP_FIELD_FONT.toString());
			} catch (NumberFormatException e) {
				logDebug("Fields font of the top screen by default : " + TOP_FIELD_FONT.toString());
			} catch (DASFontException e) {
				logDebug("Fields font of the top screen by default : " + TOP_FIELD_FONT.toString());
			}

			// fields background color of the top screen
			try {
				BG_TOP_FIELD_COLOR = outils.getColor(guiParams, "TOP_FIELDS_BG");
				logDebug("Fields background color of the top screen : " + BG_TOP_FIELD_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Fields background color of the top screen by default: " + BG_TOP_FIELD_COLOR.toString());
			}

			// fields font color of the bottom screen
			try {
				FG_TOP_FIELD_COLOR = outils.getColor(guiParams, "TOP_FIELDS_FG");
				logDebug("Fields font color of the bottom screen : " + FG_TOP_FIELD_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Fields font color of the bottom screen by default:" + FG_TOP_FIELD_COLOR.toString());
			}

			// fields font color of the top screen if error
			try {
				FG_ERROR_TOP_FIELD_COLOR = outils.getColor(guiParams, "TOP_FIELDS_FG-ERROR");
				logDebug("Fields font color of the top screen if error : " + FG_ERROR_TOP_FIELD_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Fields font color of the top screen if error by default : " + FG_ERROR_TOP_FIELD_COLOR.toString());
			}

			// fields font color of the top screen if ok
			try {
				FG_OK_TOP_FIELD_COLOR = outils.getColor(guiParams, "TOP_FIELDS_FG-OK");
				logDebug("Fields font color of the top screen if ok : " + FG_OK_TOP_FIELD_COLOR.toString());
			} catch (NumberFormatException e) {
				logDebug("Fields font color of the top screen if ok : " + FG_OK_TOP_FIELD_COLOR.toString());
			}

		};

		if (DIMENSION_FRAME != null){
			frame.setSize(DIMENSION_FRAME);			
		}

		DASParserXmlFcts parserXmlBtnsTop = new DASParserXmlFcts(DASLoader.getBtnsXml());
		xmltopbuttons = parserXmlBtnsTop.getParameters();
		List<Entry<String, Object>> entries = new ArrayList<Entry<String, Object>>(xmltopbuttons.entrySet());
		
		for (Entry<String, Object> currentEntry : entries)
		{
			if (currentEntry.getValue() instanceof DASFunctions)
			{
				DASFunctions erase = ((DASFunctions) currentEntry.getValue()).get_child("erase");
				final DASFunctions function = ((DASFunctions) currentEntry.getValue()).get_child("function");
				DASFunctions designation = ((DASFunctions) currentEntry.getValue()).get_child("designation");
				DASFunctions shortcut_ = ((DASFunctions) currentEntry.getValue()).get_child("shortcut");
				String shortcut = new String("");
				if (shortcut_ != null)
					shortcut = (String) shortcut_.get("_value");
				String name = (String) ((DASFunctions) currentEntry.getValue()).get("_name");
				Integer sequence = (Integer) ((DASFunctions) currentEntry.getValue()).get("_sequence");
				DASFunctions supercontext = ((DASFunctions) currentEntry.getValue()).get_child("supercontext");

				logDebug("Shortcut of button '" + designation.get("_value") + "' : " + shortcut);
				if (name.equals("Button"))
				{
					Font topBtnPolice = null;
					topBtnPolice = TOP_BUTTON_FONT;
					try
					{
						topBtnPolice = outils.getFont(guiParams, "TOP_BTN" + sequence);
						logDebug("Button font '" + designation.get("_value") + "' : " + topBtnPolice.toString());
					} catch (NumberFormatException e)
					{
						logDebug("Button font '" + designation.get("_value") + "' by default : " + topBtnPolice.toString());
					} catch (DASFontException e)
					{
						logDebug("Button font '" + designation.get("_value") + "' by default : " + topBtnPolice.toString());
					}

					Color topBtnFgColor = null;
					topBtnFgColor = FG_TOP_BUTTON_COLOR;
					try
					{
						topBtnFgColor = outils.getColor(guiParams, "TOP_BTN" + sequence + "_FG");
						logDebug("Button writing color '" + designation.get("_value") + "' : " + topBtnFgColor.toString());
					} catch (NumberFormatException e)
					{
						logDebug("Button writing color '" + designation.get("_value") + "' by default: " + topBtnFgColor.toString());
					}

					Color topBtnBgColor = null;
					topBtnBgColor = BG_TOP_BUTTON_COLOR;
					try
					{
						topBtnBgColor = outils.getColor(guiParams, "TOP_BTN" + sequence + "_BG");
						logDebug("Button color'" + designation.get("_value") + "' : " + topBtnBgColor.toString());
					} catch (NumberFormatException e)
					{
						logDebug("Button color '" + designation.get("_value") + "' by default: " + topBtnBgColor.toString());
					}

					int topBtnAlign = TOP_BUTTON_TEXT_ALIGN;
					String topBtnAlignString = outils.IntAlignToTextAlign(topBtnAlign);
					try
					{
						int align = outils.getTextAlign(guiParams.get("TOP_BTN" + sequence + "_TEXT_ALIGN"));
						topBtnAlign = align;
						topBtnAlignString = outils.IntAlignToTextAlign(topBtnAlign);
						logDebug("Button text alignment '" + designation.get("_value") + "' : " + topBtnAlignString);
					} catch (DASTextAlignException e)
					{
						logDebug("Button text alignment '" + designation.get("_value") + "' by default : " + topBtnAlignString);
					}
					
					String btnNameFormate = null;
					if (shortcut != null && !shortcuts.contains(shortcut))
					{
						btnNameFormate = "<html><div align='" + topBtnAlignString + "'>" + designation.get("_value") + "</div> <div align='right'>"+shortcut+"</div></html>";
						shortcuts.add(shortcut);
					} else {
						btnNameFormate = "<html><div align='" + topBtnAlignString + "'>" + designation.get("_value") + "</div></html>";
					}
					

					// inactive button color
					Color topBtnInactiveBgColor = BG_TOP_INACT_BUTTON_COLOR;

					try
					{
						topBtnInactiveBgColor = outils.getColor(guiParams, "TOP_BTN" + sequence + "_INACTIVE-BG");
						logDebug("Button color'" + designation.get("_value") + "' when inactive : " + topBtnInactiveBgColor.toString());
					} catch (NumberFormatException e)
					{
						logDebug("Button color'" + designation.get("_value") + "' when inactive by default : " + topBtnInactiveBgColor.toString());
					}

					// Navigation buttons gradation
					byte gradient = BG_TOP_GRADIENT;
					try
					{
						gradient = outils.getGradient(guiParams.get("TOP_BTN" + sequence + "_BG_GRADIENT"));
						BG_TOP_GRADIENT = gradient;
						logDebug("Navigation buttons gradation : " + BG_TOP_GRADIENT);
					} catch (NumberFormatException e)
					{
						logDebug("Navigation buttons gradation by default : " + BG_TOP_GRADIENT);
					}

					logDebug(btnNameFormate);
					// creation et enregistrement du bouton
					DASGradientJButton jbutton = new DASGradientJButton(btnNameFormate, gradient, topBtnBgColor, topBtnInactiveBgColor);
					jbutton.setBackground(topBtnBgColor);
					jbutton.setForeground(topBtnFgColor);
					jbutton.setMargin(new Insets(1,1,1,1));
					
					
					jbutton.setFont(topBtnPolice);
					setImageButton(jbutton, "btn" + sequence);
					jbutton.setFocusable(false);
					jbutton.setPreferredSize(DIMENSION_TOP_BUTTONS);
					if (erase != null && erase.get("_value") != null && erase.get("_value").equals("true"))
						jbutton.setErase(true);
					topButtons.add(jbutton);
					dicttopButtons.put((String) function.get("_value"), jbutton);

					if (shortcut != null)
					{
						int mnemonic = -1;
						if (shortcut.equals("F1")) mnemonic = KeyEvent.VK_F1;
						if (shortcut.equals("F2")) mnemonic = KeyEvent.VK_F2;
						if (shortcut.equals("F3")) mnemonic = KeyEvent.VK_F3;
						if (shortcut.equals("F4")) mnemonic = KeyEvent.VK_F4;
						if (shortcut.equals("F5")) mnemonic = KeyEvent.VK_F5;
						if (shortcut.equals("F6")) mnemonic = KeyEvent.VK_F6;
						if (shortcut.equals("F7")) mnemonic = KeyEvent.VK_F7;
						if (shortcut.equals("F8")) mnemonic = KeyEvent.VK_F8;
						if (shortcut.equals("F9")) mnemonic = KeyEvent.VK_F9;
						if (shortcut.equals("F10")) mnemonic = KeyEvent.VK_F10;
						if (shortcut.equals("F11")) mnemonic = KeyEvent.VK_F11;
						if (shortcut.equals("F12")) mnemonic = KeyEvent.VK_F12;
						if (shortcut.equals("ESC")) mnemonic = KeyEvent.VK_ESCAPE;
						if (mnemonic != -1)
						{
							jbutton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(mnemonic, 0), shortcut);
							jbutton.getActionMap().put((Object)shortcut, (javax.swing.Action)new AbstractAction() {
								private static final long	serialVersionUID	= 1L;
								public void actionPerformed(ActionEvent e)
								{
									controller.actionBtn((String) function.get("_value"));
									showSequence((String) function.get("_value"));
								}
							});
						}
					}
					
					final String btni = Integer.toString(sequence);
					jbutton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e)
						{
							controller.actionBtn((String) function.get("_value"));
							showSequence((String) function.get("_value"));
						}
					});
				}
				else if (name.equals("Field"))
				{
					Font topBtnPolice = null;
					topBtnPolice = TOP_BUTTON_FONT;
					try
					{
						topBtnPolice = outils.getFont(guiParams, "TOP_BTN" + sequence);
						logDebug("Button font '" + designation.get("_value") + "' : " + topBtnPolice.toString());
					} catch (NumberFormatException e)
					{
						logDebug("Button font '" + designation.get("_value") + "' by default : " + topBtnPolice.toString());
					} catch (DASFontException e)
					{
						logDebug("Button font '" + designation.get("_value") + "' by default : " + topBtnPolice.toString());
					}

					Color topBtnFgColor = null;
					topBtnFgColor = FG_TOP_BUTTON_COLOR;
					try
					{
						topBtnFgColor = outils.getColor(guiParams, "TOP_BTN" + sequence + "_FG");
						logDebug("Button writing color '" + designation.get("_value") + "' : " + topBtnFgColor.toString());
					} catch (NumberFormatException e)
					{
						logDebug("Button writing color '" + designation.get("_value") + "' by default : " + topBtnFgColor.toString());
					}

					Color topBtnBgColor = null;
					topBtnBgColor = BG_TOP_BUTTON_COLOR;
					try
					{
						topBtnBgColor = outils.getColor(guiParams, "TOP_BTN" + sequence + "_BG");
						logDebug("Button color '" + designation.get("_value") + "' : " + topBtnBgColor.toString());
					} catch (NumberFormatException e)
					{
						logDebug("Button color '" + designation.get("_value") + "' by default : " + topBtnBgColor.toString());
					}

					int topBtnAlign = TOP_BUTTON_TEXT_ALIGN;
					String topBtnAlignString = outils.IntAlignToTextAlign(topBtnAlign);
					try
					{
						int align = outils.getTextAlign(guiParams.get("TOP_BTN" + sequence + "_TEXT_ALIGN"));
						topBtnAlign = align;
						topBtnAlignString = outils.IntAlignToTextAlign(topBtnAlign);
						logDebug("Button text alignment '" + designation.get("_value") + "' : " + topBtnAlignString);
					} catch (DASTextAlignException e)
					{
						logDebug("Button text alignment '" + designation.get("_value") + "' by default : " + topBtnAlignString);
					}
					String btnNameFormate = "<html><div align='" + topBtnAlignString + "'>" + designation.get("_value") + "</div></html>";

					// couleur du bouton inactif
					Color topBtnInactiveBgColor = BG_TOP_INACT_BUTTON_COLOR;

					try
					{
						topBtnInactiveBgColor = outils.getColor(guiParams, "TOP_BTN" + sequence + "_INACTIVE-BG");
						logDebug("Button color '" + designation.get("_value") + "' when inactive : " + topBtnInactiveBgColor.toString());
					} catch (NumberFormatException e)
					{
						logDebug("Button color '" + designation.get("_value") + "' when inactive by default : " + topBtnInactiveBgColor.toString());
					}

					// degrade des boutons de navigation
					byte gradient = BG_TOP_GRADIENT;
					try
					{
						gradient = outils.getGradient(guiParams.get("TOP_BTN" + sequence + "_BG_GRADIENT"));
						BG_TOP_GRADIENT = gradient;
						logDebug("Navigation button gradation: " + BG_TOP_GRADIENT);
					} catch (NumberFormatException e)
					{
						logDebug("Navigation button gradation by default: " + BG_TOP_GRADIENT);
					}

					JTextField jtextfield = new JTextField((String) designation.get("_value"));
					jtextfield.setBackground(topBtnBgColor);
					jtextfield.setForeground(topBtnFgColor);
					jtextfield.setFont(topBtnPolice);
					jtextfield.setHorizontalAlignment(topBtnAlign);
					jtextfield.setFocusable(false);
					jtextfield.setEnabled(true);
					jtextfield.setPreferredSize(DIMENSION_TOP_BUTTONS);

					topButtons.add(jtextfield);
					dicttopButtons.put((String) supercontext.get("_value"), jtextfield);
				}
			}
		}

		// Tri on _sequence key
		DASParserXmlFcts parserXmlBtnsBottom = new DASParserXmlFcts(DASLoader.getBtnsbottomXml());
		xmlbottombuttons = parserXmlBtnsBottom.getParameters();
		entries = new ArrayList<Entry<String, Object>>(xmlbottombuttons.entrySet());
		for (Entry<String, Object> currentEntry : entries)
		{
			if (currentEntry.getValue() instanceof DASFunctions)
			{
				final DASFunctions function = ((DASFunctions) currentEntry.getValue()).get_child("function");
				DASFunctions designation = ((DASFunctions) currentEntry.getValue()).get_child("designation");
				DASFunctions shortcut_ = ((DASFunctions) currentEntry.getValue()).get_child("shortcut");
				String shortcut = new String("");
				if (shortcut_ != null)
					shortcut = (String) shortcut_.get("_value");
				String name = (String) ((DASFunctions) currentEntry.getValue()).get("_name");
				Integer sequence = (Integer) ((DASFunctions) currentEntry.getValue()).get("_sequence");
				DASFunctions supercontext = ((DASFunctions) currentEntry.getValue()).get_child("supercontext");
				if (name.equals("Button"))
				{
					DASGradientJButton bouton = new DASGradientJButton(BG_BOTTOM_GRADIENT, BG_BOTTOM_BUTTON_COLOR, BG_BOTTOM_INACT_BUTTON_COLOR);
					bouton.setBackground(BG_BOTTOM_BUTTON_COLOR);
					bouton.setForeground(FG_BOTTOM_BUTTON_COLOR);
					bouton.setFocusable(false);
					bouton.setPreferredSize(DIMENSION_BOTTOM_BUTTONS);
					bouton.setFont(BOTTOM_FONT);
					bouton.setHorizontalTextPosition(SwingConstants.CENTER);
					bouton.setVerticalTextPosition(SwingConstants.CENTER);
					bouton.setMargin(new Insets(1,1,1,1));

					if (function != null)
					{
						ActionListener listener = null;
						if (((String) function.get("_value")).toString().equals("VALID"))
						{
							listener = new AbstractAction() {
								public void actionPerformed(ActionEvent e)
								{
									DASGradientJButton bouton = (DASGradientJButton) e.getSource();
									if (controller.getSubDataEnCours().size() == 0 || controller.getSubDataEnCours().isEmpty() || controller.getSubDataEnCours().get(0).equals("null"))
									{
										showError("None selection" + " - " + controller.getInstructionList().get(controller.getInstructionList().size() - 1));
									} else if (controller.unlimitedLoop())
									{

										if (controller.getSubDataEnCours().size() > 0)
										{
											bouton.setText(null);
											bouton.setEnabled(false);
											controller.receivedData("-1", -1);
										} else
										{
											showError("None selection" + " - " + controller.getInstructionList().get(controller.getInstructionList().size() - 1));
										}
									}
								}
							};
						} else if (((String) function.get("_value")).toString().equals("CORRECT"))
						{
							listener = new AbstractAction() {
								public void actionPerformed(ActionEvent e)
								{
									LINES_MID_BUTTONS = LINES_MID_BUTTONS_DEFAULT+1;
									COLUMNS_MID_BUTTONS = COLUMNS_MID_BUTTONS_DEFAULT+2;
									maxBtn = COLUMNS_MID_BUTTONS * LINES_MID_BUTTONS;
									controller.correctFunction(null);
								}
							};

						} else if (((String) function.get("_value")).toString().equals("CANCEL"))
						{
							listener = new AbstractAction() {
								public void actionPerformed(ActionEvent e)
								{
									cancelFunction();
								}
							};
						} else if (((String) function.get("_value")).toString().equals("SWITCH"))
						{
							listener = new AbstractAction() {
								public void actionPerformed(ActionEvent e)
								{
									DASGradientJButton bouton = (DASGradientJButton) e.getSource();
									logDebug("ACTION PERFORMED BUTTON : " + bouton.getText() + "SWITCH BUTTON :" + SWITCH_BTN_NAME);
									if (bouton.getText().equals(I18n._("Generics view")))
									{
										switchToGenericsView();
									} else if (bouton.getText().equals(SWITCH_BTN_NAME))
									{
										try
										{
											displayKeyboard();
											for (String key : DASCOMMaterial.mat_inputs.keySet()){
												keyboardPanel.updateInput(DASCOMMaterial.mat_inputs.get(key),key);
											}
										} catch (TimeoutException ex)
										{
										}
									}
								}
							};
						} else if (((String) function.get("_value")).toString().equals("AFTER"))
						{
							listener = new AbstractAction() {
								public void actionPerformed(ActionEvent e)
								{
									if (isCalendar)
									{
										getDateLookInstance().getDateLookPanel().nextButtonCliqued();
									}
									else if(isSupervision)
									{
										
										supPanel.moveNext();
									
									}
									else
									{
										((DASGradientJButton) e.getSource()).setEnabled(false);
										((DASGradientJButton) e.getSource()).setSelected(false);
										displayNextGenerics();
									}
								}
							};
						} else if (((String) function.get("_value")).toString().equals("BEFORE"))
						{
							listener = new AbstractAction() {
								public void actionPerformed(ActionEvent e)
								{
									if (isCalendar)
									{
										getDateLookInstance().getDateLookPanel().prevButtonCliqued();
									
									}
									else if(isSupervision){
										
										supPanel.movePrev();
									
									} 
									else
									{
										((DASGradientJButton) e.getSource()).setEnabled(false);
										((DASGradientJButton) e.getSource()).setSelected(false);
										displayPrevGenerics();
									}
								}
							};
						} else if (((String) function.get("_value")).toString().equals("DAY"))
						{
							listener = new AbstractAction() {
								public void actionPerformed(ActionEvent e)
								{
									if (isCalendar)
									{
										getDateLookInstance().getDateLookPanel().switchDisplayMode("day");
									}
								}
							};
						} else if (((String) function.get("_value")).toString().equals("WEEK"))
						{
							listener = new AbstractAction() {
								public void actionPerformed(ActionEvent e)
								{
									if (isCalendar)
									{
										getDateLookInstance().getDateLookPanel().switchDisplayMode("week");
									}
								}
							};
						} else if (((String) function.get("_value")).toString().equals("MONTH"))
						{
							listener = new AbstractAction() {
								public void actionPerformed(ActionEvent e)
								{
									if (isCalendar)
									{
										getDateLookInstance().getDateLookPanel().switchDisplayMode("month");
									}
								}
							};
						} else if (((String) function.get("_value")).toString().equals("UP"))
						{
							listener = new AbstractAction() {
								public void actionPerformed(ActionEvent e)
								{
									if (isCalendar)
									{
										getDateLookInstance().getDateLookPanel().switchVerticalList(-1);
									
									}else if(isSupervision){
									
										supPanel.moveUp();
									
									}
								}
							};
						} else if (((String) function.get("_value")).toString().equals("DOWN"))
						{
							listener = new AbstractAction() {
								public void actionPerformed(ActionEvent e)
								{
									if (isCalendar)
									{
										getDateLookInstance().getDateLookPanel().switchVerticalList(1);
									}else if(isSupervision){
									
										supPanel.moveDown();
									
									}
								}
							};
						}else if (((String) function.get("_value")).toString().equals("NONE"))
						{
							bouton.setEnabled(false);
							bouton.setVisible(false);
						}
						bottomButtons.add(bouton);
						dictbottomButtons.put((String) function.get("_value"), bouton);
						if (listener != null)
						{
							bouton.addActionListener(listener);
							if (shortcut != null)
							{
								int mnemonic = -1;
								if (shortcut.equals("F1")) mnemonic = KeyEvent.VK_F1;
								if (shortcut.equals("F2")) mnemonic = KeyEvent.VK_F2;
								if (shortcut.equals("F3")) mnemonic = KeyEvent.VK_F3;
								if (shortcut.equals("F4")) mnemonic = KeyEvent.VK_F4;
								if (shortcut.equals("F5")) mnemonic = KeyEvent.VK_F5;
								if (shortcut.equals("F6")) mnemonic = KeyEvent.VK_F6;
								if (shortcut.equals("F7")) mnemonic = KeyEvent.VK_F7;
								if (shortcut.equals("F8")) mnemonic = KeyEvent.VK_F8;
								if (shortcut.equals("F9")) mnemonic = KeyEvent.VK_F9;
								if (shortcut.equals("F10")) mnemonic = KeyEvent.VK_F10;
								if (shortcut.equals("F11")) mnemonic = KeyEvent.VK_F11;
								if (shortcut.equals("F12")) mnemonic = KeyEvent.VK_F12;
								if (shortcut.equals("ESC")) mnemonic = KeyEvent.VK_ESCAPE;
								if (mnemonic != -1)
								{
									bouton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(mnemonic, 0), shortcut);
									bouton.getActionMap().put((Object)shortcut, (javax.swing.Action)listener);
								}
							}
						}
					}
					if (designation != null)
					{
						if (shortcut != null)
							bouton.setText((String) designation.get("_value") + "    " + shortcut);
						else
							bouton.setText((String) designation.get("_value"));
					}
				} else if (name.equals("Field"))
				{
					JTextField field = new JTextField();
					field.setBackground(BG_BOTTOM_BUTTON_COLOR);
					field.setForeground(FG_BOTTOM_BUTTON_COLOR);
					field.setFocusable(false);
					field.setPreferredSize(DIMENSION_BOTTOM_BUTTONS);
					field.setFont(BOTTOM_FONT);
					if (designation != null)
					{
						field.setText((String) designation.get("_value"));
					}
					if (supercontext != null)
					{
						bottomButtons.add(field);
						dictbottomButtons.put((String) supercontext.get("_value"), field);
					}
				}
			}
		}
	}

	public void showSequence(String sequence){
		if (panelCenter.isVisible() && panelFooter.isVisible())
		{
			for (String key : dicttopButtons.keySet())
			{
				if(topPanelLittle.equals("minimize")){ 
					if(key.equals(sequence)){
						minimalizeTopPanel(dicttopButtons.get(key));
						break;
					}
				} else if(topPanelLittle.equals("label")){
					if(key.equals(sequence)){
						minimalizeTopPanel2(key);
						break;
					}
				} else if(topPanelLittle.equals("hide")){
					hideTopPanel();
				} else{
					dicttopButtons.get(key).setEnabled(key.equals(sequence));
				}
			}
		}
	}
	
	public void minimalizeTopPanel2(String key){

		panelTop.removeAll();

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);

		JLabel label = new JLabel(((DASGradientJButton)dicttopButtons.get(key)).getText().replaceAll("<div align='right'>.*", "</html>"));
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panelTop.add(label , c);
		
		c.gridy = 1;
		c.insets = new Insets(5, 5, 5, 5);
		panelTop.add(instructions, c);

		panelTop.updateUI();
	}
	
	public void minimalizeTopPanel(JComponent jComponent){

		panelTop.removeAll();

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);

		panelTop.add(instructions, c);
		c.gridx = 0;
		c.insets = new Insets(5, 5, 5, 5);
		
		DASGradientJButton button = (DASGradientJButton) jComponent;
		panelTop.add(button, c);

		panelTop.updateUI();
	}
	
	public void hideTopPanel(){

		panelTop.removeAll();
		panelTop.updateUI();
	}
	
	/*
	 * private JTextField saisieKeyboard;
	 * 
	 * public void saisirCodeClavier(GridBagConstraints c){ saisieKeyboard = new
	 * DASScannerFrame(); saisieKeyboard.setFocusable(false);
	 * saisieKeyboard.addActionListener( new ActionListener() { public void
	 * actionPerformed(ActionEvent e) { logDebug(saisieKeyboard.getText());
	 * controller.codeRecu(null,saisieKeyboard.getText());
	 * saisieKeyboard.setText(""); } }); saisieKeyboard.setEditable(true);
	 * 
	 * c.insets = new Insets(6, 5, 0, 0); panelTop.add(saisieKeyboard, c); }
	 */
	/**
	 * Construit l'ecran du haut
	 * 
	 * @return l'ecran construit
	 */

	public JPanel buildPaneHeader() {
		
		this.setLayout(new BorderLayout());
		panelTop = new JPanel(new GridBagLayout());
		panelTop.setBackground(BG_TOP_COLOR);
		instructions = new JTextArea();
		instructions.setLineWrap(true);
		instructions.setBackground(BG_LARGE_FIELDS_COLOR);
		instructions.setForeground(FG_LARGE_FIELDS_COLOR);
		instructions.setEditable(false);
		instructions.setHighlighter(null);
		instructions.setFont(LARGE_FIELDS_FONT);
		instructions.setFocusable(false);
		//instructions.setPreferredSize(DIMENSION_LARGE_FIELDS);
		cleanTopPanel();

		this.addComponentListener(this);
		this.setFocusable(true);

		return panelTop;
	}

	/**
	 * Construit l'ecran central
	 * 
	 * @return l'ecran construit
	 */

	public JPanel buildPaneCenter() {

		panelCenter = new JPanel(new BorderLayout());
		return panelCenter;

	}

	public JPanel initCalendar(){

		setDateLookInstance(new DateLook(calendarData, controller.getListEvent(), this));

		JPanel datelookpanel = (JPanel) getDateLookInstance().getPanel();

		return datelookpanel;

	}

	public JPanel buildPaneTablerDep()
	{

		logDebug("buildPaneTablerDEP => " + numPanelEnCours);

		final int numPanel = numPanelEnCours;

		List<String> ButtonSelect = new LinkedList<String>();

		if (selectedButtonList.size() == 0)
			selectedButtonList.add(ButtonSelect);

		buttons = new LinkedList<DASGradientJToggleButton>();

		panelBlocCenterComponents = new JPanel();
		panelBlocCenterComponents.setLayout(new BoxLayout(panelBlocCenterComponents, BoxLayout.Y_AXIS));
		panelBlocCenterComponents.setVisible(true);
		//panelBlocCenterComponents.setMaximumSize(new Dimension(200, 50));
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.gridy = 0;

		for (int i = 0; i < depListEnCours.size(); ++i)
		{
			DASGradientJToggleButton bouton = new DASGradientJToggleButton(BG_MID_GRADIENT, BG_MID_BUTTON_COLOR, BG_MID_INACT_BUTTON_COLOR);
			bouton.setMargin(new Insets(0,0,0,0));
			bouton.setBackground(BG_MID_BUTTON_COLOR);
			bouton.setForeground(FG_MID_BUTTON_COLOR);
			bouton.setSize(DIMENSION_MID_BUTTONS);
			bouton.setPreferredSize(DIMENSION_MID_BUTTONS);
			bouton.setFocusable(false);
			bouton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					logDebug("ACTION PERFORMED");
					DASGradientJToggleButton source = (DASGradientJToggleButton) e.getSource();

					String code = source.getId();

					for (int i = 0; i < buttonsEnCoursList.get(buttonsEnCoursList.size() - 1).size(); i++)
					{
						if (numPanel == i)
						{
							for (DASGradientJToggleButton j : buttonsEnCoursList.get(i))
							{
								j.setSelected(false);
								j.setForeground(FG_MID_BUTTON_COLOR);
							}
						}
					}

					if (source.getId() != getDepActive())
					{
						source.setForeground(FG_ERROR_TOP_FIELD_COLOR);
						setDepActive(code);
					} else
					{
						setDepActive(null);
					}
					logDebug("CODE SEND FROM PANEL N°" + numPanel);
					codeSend(code, numPanel);
				}
			});

			bouton.setFont(MID_FONT);
			bouton.setHorizontalTextPosition(SwingConstants.CENTER);
			constraint.gridy++;

			buttons.add(bouton);
			panelBlocCenterComponents.add(bouton, constraint);

		}

		buttonsEnCoursList.add(buttons);

		return panelBlocCenterComponents;
	}

	public JPanel buildPaneTablerGen()
	{

		logDebug("buildPaneTablerGEN => " + numPanelEnCours);

		final int numPanel = numPanelEnCours;

		List<String> ButtonSelect = new LinkedList<String>();

		if (selectedButtonList.size() == 0)
			selectedButtonList.add(ButtonSelect);

		buttons = new LinkedList<DASGradientJToggleButton>();

		panelBlocCenterComponents = new JPanel(new GridLayout((LINES_MID_BUTTONS), (COLUMNS_MID_BUTTONS), 5, 5));
		panelBlocCenterComponents.setVisible(true);

		for (int i = 0; i < maxBtn; ++i)
		{

			DASGradientJToggleButton bouton = new DASGradientJToggleButton(BG_MID_GRADIENT, BG_MID_BUTTON_COLOR, BG_MID_INACT_BUTTON_COLOR);
			bouton.setMargin(new Insets(0,0,0,0));
			bouton.setBackground(BG_MID_BUTTON_COLOR);
			bouton.setForeground(FG_MID_BUTTON_COLOR);
			bouton.setPreferredSize(DIMENSION_MID_BUTTONS);
			bouton.setFocusable(false);
			bouton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					logDebug("ACTION PERFORMED 2");
					DASGradientJToggleButton source = (DASGradientJToggleButton) e.getSource();
					codeSend(source.getId(), numPanel);
				}

			});

			bouton.setFont(MID_FONT);
			bouton.setHorizontalTextPosition(SwingConstants.CENTER);
			buttons.add(bouton);
			panelBlocCenterComponents.add(bouton);

		}

		buttonsEnCoursList.add(buttons);

		return panelBlocCenterComponents;
	}

	public int getNumGenericsPanel()
	{
		numPanelEnCours--;
		return (numPanelEnCours);
	};

	private boolean buttonIsSelected(DASGradientJToggleButton bouton)
	{

		if (bouton.getId() != null && controller.getLoopType() != null && (!(controller.getLoopType().equals("count"))))
		{
			for (String j : controller.getSubDataEnCours())
			{

				if(bouton.getId().equals(j)){
					logDebug("The button "+bouton.getId()+" is selected");
					return true;
				}
			}
		}

		return false;
	}

	public boolean genIsSelected(DASGeneric gen) {

		if (gen.getCode() != null && controller.getLoopType() != null && (!(controller.getLoopType().equals("count"))))
		{
			for (String j : controller.getSubDataEnCours())
			{

				if (gen.getCode().equals(j))
				{
					logDebug("The button " + gen.getCode() + " is selected");
					return true;
				}
				// else if(gen.getName().equals(j)){
				// logDebug("Le bouton "+gen.getName()+" est selectionné");
				// return true;
				// }
			}
		}

		return false;
	}

	private boolean depIsSelected(DASGeneric dep)
	{
		return controller.getParentTo(dep.getCode());
	}

	public void codeSend(String code, int numPanel) {
		logDebug("CODE SEND NU:"+numPanel);
		logDebug("CODE SEND NUENCOURS:"+numPanelEnCours);

		int numPanelSend = numPanel;

		if (numPanel == -1)
		{ // Scanner = Clic Gen

			logDebug("PANEL GEN(-1)");

			numPanel = numPanelEnCours - 1;
			numPanelSend = numPanelEnCours - 1;

		}else if(numPanel == numPanelEnCours){

			logDebug("PANEL GEN(encours:"+numPanelEnCours+")");

		}else if(numPanel == (numPanelEnCours-1) && numPanel != -1){	//Dep

			logDebug("PANEL DEP ACTIVE");

			numPanelSend = PANELDEP;

		}else{

			logDebug("PANEL DEP OLD :");

			code="PANELOLD";
			numPanelSend=numPanelEnCours;

		}
		controller.receivedData(code,numPanelSend);
	}

	/**
	 * Erase Center Elements
	 */
	public void cleanHeadPanel()
	{
		boolean clean = false;
		if (xmltopbuttons != null)
		{
			for (String key : dicttopButtons.keySet())
			{
				dicttopButtons.get(key).setEnabled(!(dicttopButtons.get(key) instanceof JTextField));
				if (dicttopButtons.get(key) instanceof JTextField)
				{
					if (controller.getContext(key) != null)
					{
						((JTextField) dicttopButtons.get(key)).setText(controller.getContext(key));
					}
					dicttopButtons.get(key).setEnabled(true);
				}
				else if (dicttopButtons.get(key) instanceof DASGradientJButton)
				{
					if (controller.getButtonAccess(key))
					{
						dicttopButtons.get(key).setEnabled(true);
					} else
					{
						dicttopButtons.get(key).setEnabled(false);
						if (((DASGradientJButton)(dicttopButtons.get(key))).isErasable() == true)
							clean = true;
					}
				} else
				{
					dicttopButtons.get(key).setEnabled(false);
					if (((DASGradientJButton)(dicttopButtons.get(key))).isErasable() == true)
						clean = true;
				}
			}
		}
		if (clean == true){
			cleanTopPanel();
		}		
	}

	public void cleanTopPanel()
	{
		if (panelTop == null)
			return ;

		panelTop.removeAll();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);

		List<JComponent> topBtnToKeep = new ArrayList<JComponent>();
		for (int i = 0; i < topButtons.size(); ++i)
		{
			DASGradientJButton button = null;
			if (topButtons.get(i) instanceof DASGradientJButton){
				button = (DASGradientJButton)(topButtons.get(i));
			}
			if (button != null && (button.isEnabled() == true || (button.isErasable() == false && button.isEnabled() == false))){
				topBtnToKeep.add(button);
			}
		}

		for (int i = 0; i < topBtnToKeep.size(); ++i)
		{
			c.gridx = i % LINES_TOP_BUTTONS;
			c.gridy = i / LINES_TOP_BUTTONS;
			c.gridwidth = 1;

			c.insets = new Insets(2, 5, 0, 0);
			if (c.gridx == 0)
				c.insets = new Insets(2, 5, 0, 0);
			if (c.gridx == LINES_TOP_BUTTONS - 1)
				c.insets = new Insets(2, 5, 0, 5);
				if (topBtnToKeep.size() == 1)
					c.insets = new Insets(2, 5, 5, 5);

			panelTop.add(topBtnToKeep.get(i), c);
		}
		c.gridx = 0;
		c.gridy = topBtnToKeep.size() / LINES_TOP_BUTTONS + 1;
		c.gridwidth = LINES_TOP_BUTTONS;
		c.insets = new Insets(5, 5, 5, 5);
		panelTop.add(instructions, c);
		panelTop.updateUI();
	}

	/**
	 * Erase Center Elements
	 */

	public void cleanCenterPanel() {
		
		if (controller.getLoopType() == null)
			selectedButtonList = new LinkedList<List<String>>();

		buttonsEnCoursList = new LinkedList<List<DASGradientJToggleButton>>();

		genericsButtonsList = new LinkedList<List<DASGeneric>>();

		panelBlocCenterComponentsList = new LinkedList<JPanel>();
		try{
			panelCenterBloc.removeAll();
		}catch(NullPointerException n){}
		try{
			panelCenter.removeAll();
		}catch(NullPointerException e){}
		isCalendar = false;
		isSupervision = false;
		}

	public void cleanBottomPanel()
	{
		if (xmlbottombuttons != null)
		{
			for (String key : dictbottomButtons.keySet())
			{
				if (dictbottomButtons.get(key) instanceof JTextField)
				{
					if (controller.getContext(key) != null)
					{
						((JTextField) dictbottomButtons.get(key)).setText(controller.getContext(key));
					}
				}
			}
		}
	}

	/**
	 * Erase A Panel
	 */
	public void cleanPanel(int numPanel) {

		logDebug(" cleanPanel :: numPanel => "+numPanel);
		int numPE = 0;
		for(List<DASGradientJToggleButton> h : buttonsEnCoursList){
			if(numPE == numPanel){
				for (DASGradientJToggleButton i : h){
					DASGradientJToggleButton bouton = i;
					bouton.setId(null);
					bouton.setText(null);
					bouton.setIcon(null);
					bouton.setSelected(false);
					bouton.setEnabled(false);
					bouton.setForeground(FG_MID_BUTTON_COLOR);
				}
			}
			numPE++;
		}
	}

	public void cancelFunction()
	{
		DASBaseMaterial.mat_inputs.clear();
		controller.stopAskMaterials();
		if (supPanel != null)
		{
			if (!supPanel.getInputGeneratorList().isEmpty())
			{
				for (DASInputGenerator ig : supPanel.getInputGeneratorList())
				{
					ig.interruptRef();
					ig=null;
				}
			}
			if(!supPanel.getTopicProducerList().isEmpty()){
				for(Entry<String, DASSupervisionTopicProducer> ig : supPanel.getTopicProducerList().entrySet()){
					DASSupervisionTopicProducer tmp = ig.getValue();
					tmp.stop();
					tmp = null;
				}
			}
			if(!supPanel.getTopicListenerList().isEmpty()){
				for(Entry<String, DASSupervisionTopicListener> ig : supPanel.getTopicListenerList().entrySet()){
					DASSupervisionTopicListener tmp = ig.getValue();
					tmp.stop();
					tmp = null;
				}
			}
			supPanel = null;
		}
		
		controller.setInitialize(true);

		if(controller.isInstructionsAlreadyDefined() == false){
			instructions.setText("");
		}	
		
		cleanEndFunction();

	}

	public void cleanPanelMilieu(){
		cleanPanel(numPanelEnCours);
	}

	/**
	 * Erase panel at the end of function
	 */

	public void cleanEndFunction(){
		// Disbale keyboard
		controller.setDKB(false);
		controller.setVKB(false);
		controller.setPKB(false);
		//chargeGraphParams();
		pageEnCours = 0;
		genericsButtonsList = new LinkedList<List<DASGeneric>>();
		cleanCenterPanel();
		selectedButtonList = new LinkedList<List<String>>();
		//disable up and down button
		enableDownButton(false);
		enableUpButton(false);

		cleanTopPanel(); //for little panel
		cleanHeadPanel();
		cleanBottomPanel();
		
		setVisibleAllPanels(false);
		if(DASLoader.isLoading_configuration_on_start()){
			controller.resetConf();
		}
	}

	public JPanel buildPaneFooter()
	{
		this.setLayout(new BorderLayout());
		panelFooter = new JPanel(new GridBagLayout());
		panelFooter.setBackground(BG_TOP_COLOR); // test for white background
		panelFooter.setVisible(false);
		GridBagConstraints c = new GridBagConstraints();
		if(displayInfo){
			informations = new JTextField();
			informations.setBackground(BG_BOTTOM_FIELD_COLOR);
			informations.setForeground(FG_BOTTOM_FIELD_COLOR);
			informations.setEditable(false);
			informations.setHighlighter(null);
			informations.setFont(BOTTOM_FIELD_FONT);
			informations.setFocusable(false);
			informations.setPreferredSize(DIMENSION_LARGE_FIELDS);
		}

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);

		if(displayInfo){
			c.gridwidth = LINES_BOTTOM_BUTTONS;
			panelFooter.add(informations, c);
			c.gridx = 0;
			c.gridy = 1;
		}

		for (int i = 0; i < bottomButtons.size(); ++i)
		{
			c.gridx = i % LINES_BOTTOM_BUTTONS;
			c.gridy = i / LINES_BOTTOM_BUTTONS + 1;
			c.gridwidth = 1;

			c.insets = new Insets(2, 0, 0, 5);
			if (c.gridx == 0)
				c.insets = new Insets(2, 5, 0, 5);

			panelFooter.add(bottomButtons.get(i), c);
		}
		return panelFooter;
	}

	private void switchToGenericsView()
	{
		if (controller.getAuthorizeDisplay())
		{
			if(controller.isGenericsToDisplay())
			{
				controller.setAuthorizeDisplay(true);
			}
		}
		DASGradientJButton switchButton = (DASGradientJButton) dictbottomButtons.get("SWITCH");
		switchButton.setText(SWITCH_BTN_NAME);
		controller.setDKB(false);
		controller.displayScreenFilter();
		panelFooter.updateUI();
	}

	/**
	 * Initialize the interface
	 */
	public void activate() throws Exception {
		generics = null;
		if(displayInfo){
			//informations.setText("");
		}
	}

	public boolean deactivate() {
		return true;
	}

	public Object getBean() {
		return this;
	}

	public void addEndButton(String endLoopName)
	{
		if (endLoopName == null)
		{
			DASGradientJButton bouton = (DASGradientJButton) dictbottomButtons.get("VALID");
			bouton.setText(null);
			bouton.setEnabled(false);
		} else
		{
			DASGradientJButton bouton = (DASGradientJButton) dictbottomButtons.get("VALID");
			bouton.setText(endLoopName);
			bouton.setEnabled(true);
		}
	}

	public void balanceButton(boolean visible) {

		if(((DASGradientJButton) dictbottomButtons.get("BALANCE")) != null)
		{
			if (visible)
			{
				((DASGradientJButton) dictbottomButtons.get("BALANCE")).setEnabled(true);
				((DASGradientJButton) dictbottomButtons.get("BALANCE")).setVisible(true);

			} else
			{
				((DASGradientJButton) dictbottomButtons.get("BALANCE")).setEnabled(false);
				((DASGradientJButton) dictbottomButtons.get("BALANCE")).setVisible(false);
			}
		}
	}

	public void correctButton(boolean visible) {

		if (controller.getEtapeList().size() == 1)
		{
			((DASGradientJButton) dictbottomButtons.get("CORRECT")).setEnabled(false);

		} else if (visible)
		{
			((DASGradientJButton) dictbottomButtons.get("CORRECT")).setEnabled(true);

		} else
		{
			((DASGradientJButton) dictbottomButtons.get("CORRECT")).setEnabled(false);
		}

	}

	public void cancelButton(boolean visible)
	{
		if (visible)
		{
			((DASGradientJButton) dictbottomButtons.get("CANCEL")).setEnabled(true);
		} else
		{
			((DASGradientJButton) dictbottomButtons.get("CANCEL")).setEnabled(false);
		}

	}

	public void imprimerPopup(String sujet)
	{
		printPopup.AfficherPopup(sujet);
	}

	public void SetWindowEnabled(boolean bool){
		this.frame.setEnabled(bool);
	}

	public void cleanPanelBeforeDisplay(){

		logDebug("Passage by cleanPanelBeforeDisplay");
		cleanCenterPanel();

		setDepActive(null);
		numPanelEnCours=0;
		pageEnCours = 0;
		depNoData = new LinkedList<String>();

		panelCenterBloc.removeAll();
		panelCenterBloc =  new JPanel();
		panelCenterBloc.setLayout(new BoxLayout(panelCenterBloc, BoxLayout.X_AXIS));
	}

	public void switchGenerics()
	{

		boolean depWithData = false;
		for (String i : depNoData)
		{
			if (i.equals("true"))
				depWithData = true;
		}

		Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);

		if (depList == null)
		{
			if (depWithData)
			{
				LINES_MID_BUTTONS = LINES_MID_BUTTONS_DEFAULT;
				COLUMNS_MID_BUTTONS = COLUMNS_MID_BUTTONS_DEFAULT;
			}else{
				LINES_MID_BUTTONS = LINES_MID_BUTTONS_DEFAULT+1;
				COLUMNS_MID_BUTTONS = COLUMNS_MID_BUTTONS_DEFAULT+2;
			}

			maxBtn = COLUMNS_MID_BUTTONS * LINES_MID_BUTTONS;

		}

		if(panelBlocCenterComponentsList.size() <= numPanelEnCours){

			if (depListEnCours == null)
			{

				panelBlocCenterComponents = buildPaneTablerGen();

				if (calendarData.isEmpty())
				{

					panelCenterBloc.add(Box.createRigidArea(new Dimension(5, 0)));

					JScrollPane scrollPane = new JScrollPane(panelBlocCenterComponents);
					scrollPane.setBorder(emptyBorder);

					panelCenterBloc.add(scrollPane);
					panelCenterBloc.add(Box.createRigidArea(new Dimension(5, 0)));
				} else
				{
					datelookCalendar = initCalendar();
					panelCenterBloc.add(datelookCalendar);
				}
			} else
			{
				
				panelBlocCenterComponents = buildPaneTablerDep();
				if(panelBlocCenterComponents.getComponentCount() != 0){

					JScrollPane scrollPane = new JScrollPane(panelBlocCenterComponents);
					scrollPane.setMaximumSize(new Dimension(150, 1000));
					scrollPane.getViewport().setPreferredSize(new Dimension(150, 1000));
					scrollPane.getViewport().setMaximumSize(new Dimension(150, 1000));

					scrollPane.setBorder(emptyBorder);

					panelCenterBloc.add(scrollPane);
				}
			}

			panelBlocCenterComponentsList.add(panelBlocCenterComponents);

		}

		if (depList == null)
		{
			if (calendarData.isEmpty())
			{
				displayGeneric();
			} else
			{
				displayCalendar();
			}
		} else
		{
			displayDep();
			numPanelEnCours++;
		}

	}

	public JScrollBar getVerticalScrollBar()
	{
		return verticalScrollBar;
	}


	public void setVerticalScrollBar(JScrollBar verticalScrollBar)
	{
		this.verticalScrollBar = verticalScrollBar;
	}

	public void displayCalendar()
	{
		logDebug("DISPLAYCALENDAR");

		isCalendar = true;

		displayCalendarButton(true);

		enablePrevButton(true,"");
		enableNextButton(true,"");

		if(getDateLookInstance().getDateLookPanel().getListEventEnCoursSize() > 0){
			enableUpButton(true);
		}else{
			enableUpButton(false);
		};
		if(getDateLookInstance().getDateLookPanel().getListEventEnCoursSize() < getDateLookInstance().getDateLookPanel().getListEventSize()-1){
			enableDownButton(true);
		}else{
			enableDownButton(false);
		};
	}

	public void displayCalendarButton(boolean display){
		DASGradientJButton dayButton = ((DASGradientJButton) dictbottomButtons.get("DAY"));
		DASGradientJButton weekButton = ((DASGradientJButton) dictbottomButtons.get("WEEK"));
		DASGradientJButton monthButton = ((DASGradientJButton) dictbottomButtons.get("MONTH"));
		DASGradientJButton upButton = ((DASGradientJButton) dictbottomButtons.get("UP"));
		DASGradientJButton downButton = ((DASGradientJButton) dictbottomButtons.get("DOWN"));
		if (dayButton == null || weekButton == null || monthButton == null){
			return;
		}
		if(display){
			dayButton.setText(I18n._("DAY"));
			dayButton.setEnabled(true);
			dayButton.setVisible(true);
			weekButton.setText(I18n._("WEEK"));
			weekButton.setEnabled(true);
			weekButton.setVisible(true);
			monthButton.setText(I18n._("MONTH"));
			monthButton.setEnabled(true);
			monthButton.setVisible(true);
			
			upButton.setVisible(true);
			downButton.setVisible(true);
		}else{
			dayButton.setEnabled(false);
			dayButton.setVisible(false);
			
			weekButton.setEnabled(false);
			weekButton.setVisible(false);
			
			monthButton.setEnabled(false);
			monthButton.setVisible(false);
			
			upButton.setVisible(false);
			downButton.setVisible(false);
		}
	}

	public void enablePrevButton(boolean display, String prevPage){
		
		if(dictbottomButtons.containsKey("BEFORE")){
			DASGradientJButton prevButton = ((DASGradientJButton) dictbottomButtons.get("BEFORE"));
			if(display){
				prevButton.setText(prevPage+" ←");
				prevButton.setSelected(false);
				prevButton.setEnabled(true);
			}else{
				prevButton.setText(prevPage+" ←");
				prevButton.setSelected(false);
				prevButton.setEnabled(false);	
			}
		}else{
			DASLog.logErr(this.getClass().getSimpleName(),"ERROR : AFTER Button not defined in functional configuration");
		}
	}

	public void enableNextButton(boolean display, String nextPage){
		
		if(dictbottomButtons.containsKey("AFTER")){
			DASGradientJButton nextButton = ((DASGradientJButton) dictbottomButtons.get("AFTER"));
			if(display){
				nextButton.setText("→ "+nextPage);
				nextButton.setSelected(false);
				nextButton.setEnabled(true);
			}else{
				nextButton.setText("→ "+nextPage);
				nextButton.setSelected(false);
				nextButton.setEnabled(false);	
			}
		}else{
			DASLog.logErr(this.getClass().getSimpleName(),"ERROR : AFTER Button not defined in functional configuration");
		}
	}

	public void enableSwitchButton(boolean display){
		if(dictbottomButtons.containsKey("AFTER")){
			DASGradientJButton switchButton = ((DASGradientJButton) dictbottomButtons.get("SWITCH"));
			if(display){
				switchButton.setSelected(false);
				switchButton.setEnabled(true);
			}else{
				switchButton.setSelected(false);
				switchButton.setEnabled(false);
			}
		}else{
			DASLog.logErr(this.getClass().getSimpleName(),"ERROR : AFTER Button not defined in functional configuration");
		}
	}

	public void enableUpButton(boolean display)
	{
		logDebug("DISPLAYUP");
		DASGradientJButton upButton = ((DASGradientJButton) dictbottomButtons.get("UP"));
		if (upButton == null)
			return ;
		upButton.setText("↑");
		if(display){
			upButton.setSelected(false);
			upButton.setEnabled(true);
		}else{
			upButton.setSelected(false);
			upButton.setEnabled(false);
		}
	}

	public void enableDownButton(boolean display)
	{
		logDebug("DISPLAYDOWN");
		DASGradientJButton downButton = ((DASGradientJButton) dictbottomButtons.get("DOWN"));
		if (downButton == null)
			return ;
		downButton.setText("↓");
		if(display){
			downButton.setSelected(false);
			downButton.setEnabled(true);
		}else{
			downButton.setSelected(false);
			downButton.setEnabled(false);	
		}
	}

	public void blockScreen(boolean block)
	{
		logDebug("========================");
		if (block)
		{
			logDebug("===  Screen Blocked  ===");
			this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			this.setEnabled(false);
		}else{
			logDebug("=== Screen Unblocked ===");
			this.frame.setCursor(Cursor.getDefaultCursor());
			this.setEnabled(true);
		}
		logDebug("========================");
	}

	public void refreshCalendar(String deltaT){

		if(deltaT==null)deltaT = getDateLookInstance().getDateLookPanel().getDeltaT();

		calendarData.remove("gc");
		calendarData.put("gc",getDateLookInstance().getDateLookPanel().getGc());

		calendarData.remove("calendarDeltaT");
		calendarData.put("calendarDeltaT",deltaT);

		calendarData.remove("pageEnCours");
		calendarData.put("pageEnCours",getDateLookInstance().getDateLookPanel().getVerticalListEnCours());

		controller.setCalendarData(calendarData);

		panelCenterBloc.remove(datelookCalendar);

		isCalendar = true;
		// We don't recreate a datelookInstance but just his panel
		// datelookCalendar = initCalendar();
		datelookCalendar = (JPanel) getDateLookInstance().getPanel();

		panelCenterBloc.add(datelookCalendar);

		SwingUtilities.updateComponentTreeUI(datelookCalendar);
		SwingUtilities.updateComponentTreeUI(panelCenterBloc);		
	}

	public void switchGenericsAll(List<DASGeneric> generics, List<List<DASGeneric>> dep, int panelToUse, HashMap<String, Object> calendarData)
	{
		isCalendar = false;
		depListEnCours = null;
		logDebug("switchGenericsAll : Panel Num => " + panelToUse);
		if (panelToUse == -2)
		{
			numPanelEnCours = numPanelEnCours - 1;
			panelCenter.remove(numPanelEnCours);
			buttonsEnCoursList.remove(numPanelEnCours);
			panelBlocCenterComponentsList.remove(numPanelEnCours);
		}

		maxBtn = COLUMNS_MID_BUTTONS * LINES_MID_BUTTONS;

		this.generics = generics;
		this.depList = dep;
		this.calendarData = calendarData;

		if (calendarData.isEmpty())
		{
			if (this.generics != null)
			{
				logDebug("TAILLE GENERICS : " + generics.size());
				organizeGenericsPage();
			}
		}

		if (dep == null)
		{
			switchGenerics();
			panelCenter.add(panelCenterBloc, BorderLayout.CENTER);

		} else
		{
			for (List<DASGeneric> i : depList)
			{
				this.depListEnCours = i;
				switchGenerics();
			}
		}
		panelCenter.updateUI();
	}

	/**
	 * Divise la liste de generics en plusieurs page si besoin
	 * @return 
	 */
	public void organizeGenericsPage()
	{
		logDebug(" OGPage");

		int genLeft =  generics.size();
		int numBtns = 0;
		sortedListPageButton = new LinkedList<String>();

		List<DASGeneric> ListButton = new LinkedList<DASGeneric>();

		List<DASGeneric> NoPlacedGen = new LinkedList<DASGeneric>();
		NoPlacedGen.addAll(generics);

		// Creation du template generics
		for (DASGeneric i : NoPlacedGen) {
			numBtns++;	
			genLeft--;
			ListButton.add(new DASGeneric());
			if(numBtns == maxBtn || genLeft == 0){
				numBtns = 0;
				sortedListPageButton.add("false");
				genericsButtonsList.add(ListButton);
				ListButton = new LinkedList<DASGeneric>();
			}
		}

		//remplissage du template par des generics dont la page est fixé
		for (DASGeneric i : NoPlacedGen) {	

			int genPageEnCours = 0;

			if(i.getPage() != null){
				genPageEnCours = i.getPage();

				if (genPageEnCours != 0 && (genPageEnCours <= genericsButtonsList.size() && genPageEnCours > 0))
				{
					for (int z = 0; z < maxBtn; z++)
					{
						if (genericsButtonsList.get(genPageEnCours - 1).get(z).getCode() == null)
						{
							genericsButtonsList.get(genPageEnCours - 1).set(z, i);
							z = maxBtn;
						}
					}
				}
			}
		}

		// On supprime les generics déjà placées...
		for (List<DASGeneric> y : genericsButtonsList)
		{
			for (DASGeneric z : y)
			{
				if (z.getCode() != null)
				{
					NoPlacedGen.remove(z);
				}
			}
		}

		// Et on place automatiquement les generics restants. (sans les trier
		// selon leur position)
		for (DASGeneric i : NoPlacedGen)
		{
			for (int genPageEnCours = 0; genPageEnCours < genericsButtonsList.size(); genPageEnCours++)
			{
				for (int z = 0; z < maxBtn; z++)
				{
					if (genericsButtonsList.get(genPageEnCours).get(z).getCode() == null)
					{
						genericsButtonsList.get(genPageEnCours).set(z, i);
						z = maxBtn;
						genPageEnCours = genericsButtonsList.size();
					}
				}
			}
		}

	}

	public boolean isTri(int numPage)
	{
		boolean bool = false;
		if (sortedListPageButton.size() > numPage)
		{
			if (sortedListPageButton.get(numPage) != null)
			{
				if (sortedListPageButton.get(numPage).equals("true"))
					bool = true;
			}
		} else
		{
			bool = true;
		}
		return bool;
	}

	/**
	 * Fixed Generics have priority > No-Fixed Generics
	 */
	public void organizeGenericsPosition(int page){

		List<DASGeneric> NoPlacedGen = new LinkedList<DASGeneric>();
		NoPlacedGen.addAll(genericsButtonsList.get(page));

		List<DASGeneric> PlacedGen = new LinkedList<DASGeneric>();
		for (int i = 0; i < maxBtn; i++)
		{
			PlacedGen.add(new DASGeneric());
		}

		// On place les gen avec une position fixe
		for (DASGeneric i : NoPlacedGen)
		{
			if (i != null)
			{
				if (i.getPosition() != null)
				{
					if (i.getPosition() != 0 && i.getPosition() < maxBtn)
					{
						int pos_gen = i.getPosition();
						pos_gen--;
						if (PlacedGen.get(pos_gen).getCode() == null)
						{
							PlacedGen.set(pos_gen, i);
						}
					}
				}
			}
		}

		// on vide la liste des generics déjà fixé...
		for (DASGeneric i : PlacedGen){
			NoPlacedGen.remove(i);
		}

		// ...et on place le reste
		for(DASGeneric i : NoPlacedGen){
			int positionBouton = 0;
			for(int y=0;y<maxBtn;y++){
				if(PlacedGen.get(y).getCode() == null){
					PlacedGen.set(y, i);
					y = maxBtn;
				}
				positionBouton++;
			}
		}

		genericsButtonsList.get(page).clear();
		genericsButtonsList.get(page).addAll(PlacedGen);

		sortedListPageButton.set(page, "true");
	}

	/*
	 * Affiche les generics en bouton d'après genericsButtonsList
	 */
	public void displayGeneric()
	{
		cleanPanel(numPanelEnCours);

		// On vérifie si on a besoin de trier les boutons de la page
		if (!(isTri(pageEnCours)))
		{
			organizeGenericsPosition(pageEnCours);
		}

		// On remplit les boutons non fixé du centre
		int positionBouton = 0;
		logDebug("genericsButtonsList.sier() => "+genericsButtonsList.size());
		logDebug("PageEnCours =>"+pageEnCours);
		for(DASGeneric i : genericsButtonsList.get(pageEnCours)){

			DASGradientJToggleButton bouton = buttonsEnCoursList.get(numPanelEnCours).get(positionBouton);
			if(i.getCode() != null){

				if(bouton.getId() == null){

					bouton.setId(i.getCode());
					bouton.setText(i.getHtmlName(MID_LEN));
					bouton.setForeground(FG_MID_BUTTON_COLOR);
					setImageButton(bouton, i.getCode().toString());
					if (genIsSelected(i))
					{
						bouton.setForeground(FG_ERROR_TOP_FIELD_COLOR);
					}
					bouton.setEnabled(((DASController) controller).get_active(i.getCode()));
					
				}

			}else{
				bouton.setEnabled(false);
			}

			positionBouton++;
		}

		// We display Prev, Next buttons or no

		if (pageEnCours - 1 > -1)
		{
			enablePrevButton(true, ""+pageEnCours);
		}else{
			enablePrevButton(false, "");
		}

		if (genericsButtonsList.size() > pageEnCours + 1)
		{
			enableNextButton(true, ""+(pageEnCours + 2));
		}else{
			enableNextButton(false, "");
		}

		// We don't display calendar buttons
		displayCalendarButton(false);
//		displayUpButton(false);
//		displayDownButton(false);

		// If next page, it sorts in advance
		if (!(isTri((pageEnCours + 1))))
		{
			logDebug("Call OGPos Page Next");
			organizeGenericsPosition((pageEnCours + 1));
		}

	}

	/**
	 * Show dependency   
	 */
	public void displayDep(){

		if (depList.size() >= 1)
		{
			int posBouton = 0;
			for (DASGeneric i : depList.get(depList.size() - 1))
			{
				DASGradientJToggleButton bouton = buttonsEnCoursList.get(numPanelEnCours).get(posBouton);
				bouton.setText(i.getHtmlName(MID_LEN));
				bouton.setName(i.getName());
				bouton.setId(i.getCode());
				bouton.setMaximumSize(new Dimension(150, 50));
				bouton.setEnabled(true);

				if (depIsSelected(i))
				{
					bouton.setForeground(FG_ERROR_TOP_FIELD_COLOR);
				}

				posBouton++;
			}

			depNoData.add("true");

		}else{
			depNoData.add("false");
		}
	}

	public void displayNextGenerics(){
		cleanPanel(numPanelEnCours);
		pageEnCours++;
		displayGeneric();
	}

	public void displayPrevGenerics(){
		cleanPanel(numPanelEnCours);
		pageEnCours--;
		displayGeneric();
	}

	// /**
	// * Affiche les generics figés de la page en cours
	// * @return vrai s'il y a un ou plusieurs generic(s) figé(s) à afficher
	// après la page en cours
	// */
	// private boolean displayFixedGenerics() {
	//
	// boolean suivants = false;
	//
	// List <Integer> poslist = new LinkedList<Integer>();
	//
	// // Priority to FixedGenerics...
	// logDebug("PAGE =>"+ page);
	// for (int i=(35*(page-1)) ; i < (35*page) ; ++i) {
	// if (generics.get(i).getPage() > page) {
	// suivants = true ;
	//
	// } else if (generics.get(i).getPage() == page) {
	//
	// if(generics.get(i).getPosition() != 0 && generics.get(i).getPage() ==
	// page){
	// newJToggleButton(i,(generics.get(i).getPosition()-1));
	// poslist.add(generics.get(i).getPosition());
	// }
	// }
	// }
	//
	// //...and after, FixedGenerics with bad informations
	//
	// int poslib = 1;
	//
	// for (int i=(35*(page-1)) ; i < (35*page) ; ++i) {
	//
	// if (generics.get(i).getPage() >page) {
	// suivants = true ;
	//
	// }else if (page == 1 && generics.get(i).getPage() == 0){
	//
	// // If getPage = 0, button will be on page 1
	//
	// Iterator<Integer> it = poslist.iterator();
	//
	// while(it.hasNext()){
	// Integer tabpos = it.next();
	// if(poslib == tabpos){
	// poslib++;
	// }
	// }
	//
	// if(generics.get(i).getPosition() != 0 && generics.get(i).getPosition() >=
	// poslib){
	// newJToggleButton(i,generics.get(i).getPosition()-1);
	// poslist.add(poslib);
	// }else{
	// newJToggleButton(i,poslib-1);
	// poslist.add(poslib);
	// }
	//
	// poslib++;
	//
	// } else if (generics.get(i).getPosition() == 0 &&
	// generics.get(i).getPage() == page){
	// Iterator<Integer> it = poslist.iterator();
	// while(it.hasNext()){
	// Integer tabpos = it.next();
	// if(poslib == tabpos){
	// poslib++;
	// }
	// }
	//
	// newJToggleButton(i,poslib-1);
	// poslist.add(poslib);
	// poslib++;
	//
	// }
	//
	// }
	// return suivants;
	// }

	public void newJToggleButton(Integer i, Integer position)
	{

		DASGradientJToggleButton bouton;

		bouton = buttonsEnCoursList.get(numPanelEnCours).get(position);
		bouton.setText(generics.get(i).getHtmlName(MID_LEN));
		bouton.setId(generics.get(i).getCode());
		setImageButton(bouton, generics.get(i).getCode().toString());

		bouton.setForeground(FG_MID_BUTTON_COLOR);

		if (buttonIsSelected(bouton))
			bouton.setForeground(FG_ERROR_TOP_FIELD_COLOR);

		bouton.setEnabled(true);

	}

	// /**
	// * Affiche (ou cache) les boutons precedents et suivants en fonction des
	// parametres
	// * @param arePrevGenerics vrai si le bouton precedent doit etre affiche,
	// faux sinon
	// * @param areNextGenerics vrai si le bouton suivant doit etre affiche,
	// faux sinon
	// */
	// private void displayPrevNext(boolean arePrevGenerics, boolean
	// areNextGenerics) {
	// //affichage du bouton "precedent"
	// DASGradientJButton prevButton =
	// bottomButtons.get(bottomButtons.size()-2);
	// if (arePrevGenerics) {
	// prevButton.setText((page-1) + " <");
	// prevButton.setSelected(false);
	// prevButton.setEnabled(true);
	// } else {
	// prevButton.setText(null);
	// prevButton.setIcon(null);
	// prevButton.setEnabled(false);
	// }
	//
	// //affichage du bouton "suivant"
	// DASGradientJButton nextButton =
	// bottomButtons.get(bottomButtons.size()-1);
	// if (areNextGenerics) {
	// nextButton.setText("> " + (page+1));
	// prevButton.setSelected(false);
	// nextButton.setEnabled(true);
	// } else {
	// nextButton.setText(null);
	// nextButton.setIcon(null);
	// nextButton.setEnabled(false);
	// }
	// }
	//
	// /**
	// * Affiche les generics à partir du "prochain"
	// * @param next le numero du prochain generic (dans la liste generics) à
	// afficher
	// * @return le numéro du prochain generic (dans la liste generics) à
	// afficher
	// */
	// private int displayGenerics(int next) {
	//
	// //initialisation de l'existance de pages précedentes et suivantes
	// boolean arePrevGenerics = (page>1);
	// boolean areNextGenerics = false;
	//
	// //initialisation de la liste des generics ayant une position figee
	//
	// //Map<String, DASGeneric> wsIndGenerics =
	// controller.getMapWsPositionedGenerics();
	//
	// //logDebug(" ::> "+wsIndGenerics.toString());
	//
	// //affichage des generics ayant une position figee
	// areNextGenerics = displayFixedGenerics();
	//
	// //affichage des generics n'ayant pas de position figee
	// for (int i = 0; i < buttonsListEnCours.get(numPanelEnCours).size(); ++i)
	// {
	//
	// DASGradientJToggleButton bouton =
	// buttonsListEnCours.get(numPanelEnCours).get(i);
	//
	// if (!bouton.isEnabled()) {
	//
	// while (((next < generics.size()) && (generics.get(next).getCode() !=
	// null))) {
	// ++next;
	// }
	//
	// if (next < generics.size()) {
	// bouton.setText(generics.get(next).getHtmlName(MID_LEN));
	// bouton.setForeground(FG_MID_BUTTON_COLOR);
	//
	// setImageButton(bouton, generics.get(next).getCode().toString());
	// bouton.setId(generics.get(next).getCode());
	//
	// if(buttonIsSelected(bouton)){
	// bouton.setForeground(FG_ERROR_TOP_FIELD_COLOR);
	// }
	//
	// bouton.setEnabled(true);
	// ++next;
	// }
	//
	// else {
	// if (lastPage==-1) {
	// lastPage = page;
	// lastPosition = i-1;
	// }
	// break;
	// }
	// }
	// }
	//
	// //derniere possibilité d'afficher le bouton "suivant"
	// if (!areNextGenerics && next < generics.size()) {
	// for (int i=next; i<generics.size() ; ++i) {
	// DASGeneric wsIndGeneric = generics.get(i);
	// if (wsIndGeneric!=null) {
	// areNextGenerics=true;
	// break;
	// }
	// }
	// }
	//
	// displayPrevNext(arePrevGenerics, areNextGenerics);
	//
	// if (instructions.getText().isEmpty()) {
	// showInstruction("Sélectionnez...");
	// }
	//
	// return next;
	// }

	// /**
	// * Affiche les generics sans changer de page (ou affiche la premiere page
	// lors de la demande d'affichage)
	// */
	// public void displayGenerics() {
	//
	// cleanPanel(numPanelEnCours);
	//
	// int prochain = genericPrev+1;
	// prochain = displayGenerics(prochain);
	// genericNext = prochain;
	//
	// }
	//
	// /**
	// * Affiche les generics de la page suivante
	// */
	// public void displayNextGenerics() {
	// cleanPanel(numPanelEnCours);
	// ++page;
	//
	// int prochain = genericNext;
	// prochain = displayGenerics(prochain);
	// genericPrev = genericNext-1;
	// genericNext = prochain;
	// }

	// /**
	// * Affiche les generics de la page précédente
	// */
	// public void displayPrevGenerics() {
	// cleanPanel(numPanelEnCours);
	// --page;
	//
	//
	// int next = genericPrev;
	//
	//
	// // //initialisation de la liste des generics ayant une position figee
	// // Map<String, DASGeneric> wsIndGenerics =
	// controller.getMapWsPositionedGenerics();
	//
	//
	// //affichage des generics ayant une position figee
	// //displayFixedGenerics(wsIndGenerics);
	// displayFixedGenerics();
	//
	//
	// //affichage des generics n'ayant pas de position figee
	// int iInit;
	// if (lastPage<page) {
	// iInit = -1;
	// } else if (lastPage == page) {
	// iInit = lastPosition;
	// } else {//derniere page > page
	// iInit = buttonsListEnCours.get(numPanelEnCours).size()-1;
	// }
	//
	// for (int i = iInit; i >= 0 ; --i) {
	// DASGradientJToggleButton bouton =
	// buttonsListEnCours.get(numPanelEnCours).get(i);
	//
	// if (!bouton.isEnabled()) {
	// while ((next >= 0) &&
	// (generics.get(next).getCode() != null)) {
	// --next;
	// }
	// if (next >= 0) {
	// bouton.setText(generics.get(next).getHtmlName(MID_LEN));
	// setImageButton(bouton, generics.get(next).getCode().toString());
	// bouton.setId(generics.get(next).getCode());
	//
	// if(buttonIsSelected(bouton)){
	// bouton.setForeground(FG_ERROR_TOP_FIELD_COLOR);
	// }
	// bouton.setEnabled(true);
	// --next;
	// }
	// else {
	// break;
	// }
	// }
	// }
	//
	// //determination de l'affichage des boutons "precedent" et "suivant"
	// boolean arePrev = (page>1);
	// boolean areNext = true;
	//
	// displayPrevNext(arePrev, areNext);
	//
	// if (instructions.getText().isEmpty()) {
	// showInstruction("Sélectionnez...");
	// }
	//
	// genericNext = genericPrev+1;
	// genericPrev = next;
	// }

	/**
	 * Affiche une instruction
	 * 
	 * @param text
	 *            l'instruction a afficher
	 */
	public void showInstruction(String text) 
	{		
		instructions.setForeground(FG_TOP_FIELD_COLOR);
		instructions.setText(text);
	}

	public String save(String file, String separator)
	{
		String  result = null;
		try{
			javax.swing.JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File("." + separator));
			int reponse = chooser.showDialog(chooser, I18n._("Save"));
			if (reponse == JFileChooser.APPROVE_OPTION)
			{
				result = chooser.getSelectedFile().toString();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	private DASSupervisionPanel supPanel;
	public void displaySupervision(DASTemplateSupervision ts) throws TimeoutException
	{
		try
		{
			displayCalendarButton(false);
			displayUpButton();
			displayDownButton();
			enableNextButton(false, "");
			enablePrevButton(false, "");
			enableSwitchButton(false);
			supPanel = new DASSupervisionPanel(ts, controller.getEquipments().recupSupervisorDialog(), this);
			supPanel.repaint();
			// TODO if supervision image height > screen height --> add
			// JScrollPane
			// System.out.println(Toolkit.getDefaultToolkit().getScreenSize().getHeight());
			// if(supPanel.getHeight() >
			// Toolkit.getDefaultToolkit().getScreenSize().getHeight()){
			// JScrollPane scrollPane = new JScrollPane(supPanel);
			// panelCenter.add(scrollPane);
			// }else{
			panelCenter.add(supPanel);
			// }
			panelCenter.updateUI();
			isSupervision = true;
		}
		catch (Exception e)
		{
			logDebug("ERROR :");
			e.printStackTrace();
		}
	}
	

	private void displayUpButton()
	{
		if(dictbottomButtons.containsKey("UP")){
			DASGradientJButton upButton = ((DASGradientJButton) dictbottomButtons.get("UP"));	
			upButton.setVisible(true);
		}else{
			DASLog.logErr(this.getClass().getSimpleName(),"ERROR : UP Button not defined in functional configuration");
		}
	}

	private void displayDownButton()
	{
		if(dictbottomButtons.containsKey("DOWN")){
			DASGradientJButton upButton = ((DASGradientJButton) dictbottomButtons.get("DOWN"));	
			upButton.setVisible(true);
		}else{
			DASLog.logErr(this.getClass().getSimpleName(),"ERROR : DOWN Button not defined in functional configuration");
		}
		
	}

	public void updateSupervisionField(String material, String value)
	{

		//MAJ VALUE FROM MATERIAL
		for(DASSupervisor sup : supPanel.getSupervisorList()){
			if(sup.getSourceMaterial() != null){
				if(sup.getSourceMaterial().equals(material)){
					sup.setValue(value);
				}
			}
		}
	}
	
	public void updateSupervisionFieldFromVariable(String variablename, String variabledata){

		if(this.getController().getDataEnCours().containsKey(variablename)){
			this.getController().getDataEnCours().remove(variablename);
		}
		List<String> tmpList = new LinkedList<String>();
		tmpList.add(variabledata);
		this.getController().getDataEnCours().put(variablename, tmpList);

	}
	/**
	 * Display keyboard
	 */
	public void displayKeyboard() throws TimeoutException
	{
		logDebug("Passage by displayKeyboard without parameters");
		
		controller.setDKB(true);
		enableNextButton(false, "");
		enablePrevButton(false, "");
		((DASGradientJButton) dictbottomButtons.get("SWITCH")).setText(I18n._("Generics view"));
		String mode = controller.getValidModelEnCours();
		DASDataModel model = controller.getDataModelEnCours();
		List<DASAcquisitionMethod> methods = model.getAcquisitionMethods();
		DASEquipments equipments = controller.getEquipments();

		initKeyboard(model);
		cleanCenterPanel();
		buildKeyboard(keyboardPanel, mode, methods, equipments);

		for (String key : dictbottomButtons.keySet())
		{
			if (dictbottomButtons.get(key) instanceof DASGradientJButton)
			{
				DASGradientJButton button = (DASGradientJButton) dictbottomButtons.get(key);
				if (button != null && !key.equals("SWITCH") && !key.equals("CANCEL") && (controller.getAskingBal() && !key.equals("BALANCE")))
					button.setEnabled(false);
			}
		}
		// Disable unnecessary buttons for keyboard view
		// bottomButtons.get(BTN_PREV).setEnabled(false);
		// bottomButtons.get(BTN_NEXT).setEnabled(false);
		// bottomButtons.get(BTN_CORRECT).setEnabled(false);
		// bottomButtons.get(BTN_PREV).setText("");
		// bottomButtons.get(BTN_NEXT).setText("");
		// bottomButtons.get(BTN_CORRECT).setText("");
		panelCenter.updateUI();
		panelFooter.updateUI();
	}

	/**
	 * Displays the screen keyboard to display (and possibly purchase) of a
	 * given
	 * 
	 * @Param field the field to display the data
	 * @Param prefix the prefix for the display of the data
	 * @Param suffix the suffix to the display of the data
	 * @Param sauvegarde_donnee indicates whether the data must be acquired
	 *        (true) or only displayed (false)
	 */
	public void displayKeyboard(JTextField field, String prefix, String suffix, boolean sauvegarde_donnee) throws TimeoutException
	{
		logDebug("Passage par displayKeyboard with parameters");
		
		String mode = controller.getValidModelEnCours();
		DASDataModel model = controller.getDataModelEnCours();
		List<DASAcquisitionMethod> methods = model.getAcquisitionMethods();
		DASEquipments equipments = controller.getEquipments();

		initKeyboard(model);
		cleanCenterPanel();
		buildKeyboard(keyboardPanel, mode, methods, equipments);

		// Disable unnecessary buttons for keyboard view
		// bottomButtons.get(BTN_PREV).setEnabled(false);
		// bottomButtons.get(BTN_NEXT).setEnabled(false);
		// bottomButtons.get(BTN_CORRECT).setEnabled(false);
		// bottomButtons.get(BTN_PREV).setText("");
		// bottomButtons.get(BTN_NEXT).setText("");
		// bottomButtons.get(BTN_CORRECT).setText("");

		panelCenter.updateUI();
		panelFooter.updateUI();
	}

	/**
	 * Init keyboard 
	 */
	private void initKeyboard(DASDataModel model) throws TimeoutException
	{
		logDebug(" Passage by initKeyboard");
		
		keyboardPanel = new DASKeyboardPanel(this, controller);
		int max_length = model.getMaxLength();
		Map<String, String> kbParams = controller.getKbParams();
		String kbTitle = kbParams.get("WINDOW_TITLE");
		String title = "OpenDAS";
		if (kbTitle != null)
		{
			title += (" - " + kbTitle);
		}
		keyboardPanel.setMaxLengthInput(max_length);
		keyboardPanel.setInputRequest(controller.getInstruction());
		String kbCompleteInput = kbParams.get("MESSAGES_COMPLETE");
		if (kbCompleteInput != null)
		{
			keyboardPanel.setCompleteInput(kbCompleteInput.replaceAll("\\\\max\\\\", String.valueOf(max_length)));
		}
		String kbEmptyInput = kbParams.get("MESSAGES_EMPTY");
		if (kbEmptyInput != null)
		{
			keyboardPanel.setEmptyInput(kbEmptyInput);
		}
		String kbAlphabetic = kbParams.get("BUTTONS_ALPHABETIC");
		if (kbAlphabetic != null)
		{
			keyboardPanel.setDisplayedABC(kbAlphabetic);
		}
		String kbNumeric = kbParams.get("BUTTONS_NUMERIC");
		if (kbNumeric != null)
		{
			keyboardPanel.setDisplayed123(kbNumeric);
		}
		String kbCorrection = kbParams.get("BUTTONS_CORRECTION");
		if (kbCorrection != null)
		{
			keyboardPanel.setCorrectionName(kbCorrection);
		}
		String kbValidation = kbParams.get("BUTTONS_VALIDATION");
		if (kbValidation != null)
		{
			keyboardPanel.setValidationName(kbValidation);
		}
		String kbCancellation = kbParams.get("BUTTONS_CANCELLATION");
		if (kbCancellation != null)
		{
			keyboardPanel.setCancellationName(kbCancellation);
		}
		String kbCase = kbParams.get("BUTTONS_CASE");
		if (kbCase != null)
		{
			keyboardPanel.setCaseName(kbCase);
		}
	}

	/**
	 * Build keyboard window screens Method to call after the parameters personalization
	 */
	
	public void buildKeyboard(DASKeyboardPanel keyboardPanel, String mode, List<DASAcquisitionMethod> methods, DASEquipments equipments)
	{
		if (mode == null)
			return ;
		JPanel panel01 = keyboardPanel.buildPaneInputs(methods, equipments);
		if (controller.isEnabledVKB() == true)
		{

			keyboardPanel.inits(mode);
			JPanel panel02 = keyboardPanel.buildPane123();
			JPanel panel05 = keyboardPanel.buildPaneABC();
			JPanel panel04 = keyboardPanel.buildFooterPane();
			panelCenter.add(panel01, BorderLayout.EAST);
			panelCenter.add(panel04, BorderLayout.SOUTH);
			if (panel02 == null && panel05 != null)
				panelCenter.add(panel05, BorderLayout.CENTER);
			if (panel05 == null && panel02 != null)
				panelCenter.add(panel02, BorderLayout.CENTER);
			if (panel05 != null && panel02 != null)
			{
				panelCenter.add(panel02, BorderLayout.WEST);
				panelCenter.add(panel05, BorderLayout.CENTER);
			}
		}
		else
			panelCenter.add(panel01, BorderLayout.WEST);
		try
		{
			keyboardPanel.activate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void validateData()
	{
		JTextField	field = null;
		String	prefix	= "";
		String	suffix	= ""; 

		//TODO change that
		int mode = 2;
		String kbInput = "-1";
		logDebug("Keyboard input: " + kbInput);
		if (kbInput != null && !kbInput.equals(""))
		{
			DASFunctions fctParams = controller.getFctParams();
			String sequenceEnCours = controller.getSequenceEnCours();
			int currentActionPosition = controller.getCurrentActionPosition();
			if (sequenceEnCours != null)
			{
				String champ = fctParams.fctParams_get(sequenceEnCours, currentActionPosition, "_DISPLAY");
				JTextField thefield = null;
				if (champ != null)
				{
					try
					{
						thefield = (JTextField) getTopBtn(Integer.parseInt(champ));
					}
					catch (NumberFormatException e)
					{
						logDebug("The value of <display> must be a integer corresponding at the display numero");
					}
				}
				if (thefield == null)
				{
					thefield = field;
				}
				if ("num".equals(controller.getValidModelEnCours()))
				{
					kbInput = String.valueOf(Double.parseDouble(kbInput));
				}
				else if ("int".equals(controller.getValidModelEnCours()))
				{
					kbInput = String.valueOf(Integer.parseInt(kbInput));
				}
				switch (mode)
				{
					case 1 :
						controller.displayData(prefix + kbInput + suffix, thefield);
						break;
					case 2 :
						controller.receivedData(kbInput, -1);
						break;
					case 3 :
						controller.displayData(prefix + kbInput + suffix, thefield);
						controller.receivedData(kbInput, -1);
						break;
				}
			}
		}
	}

	/**
	 * Display a error
	 * 
	 * @param text
	 *            The error have displayed
	 */
	public void showError(String text)
	{
		logDebug("Passage by showError");
		
		instructions.setForeground(FG_ERROR_TOP_FIELD_COLOR);
		instructions.setText(text);
	}

	/**
	 * Affiche une réponse
	 * 
	 * @param text
	 *            de la réponse a afficher
	 */
	public void showResponse(String text) {
		
		logDebug("Passage by showResponse");
		
		instructions.setForeground(FG_OK_TOP_FIELD_COLOR);
		instructions.setText(text);

	}

	// /**
	// * Restaure le bouton correspondant au generic passé en paramètre
	// * @param generic affiché par le bouton à restaurer
	// */
	// public void restoreGenericButton(DASGeneric generic) {
	// DASGradientJToggleButton bouton = getButtonById(generic.getCode());
	// if (bouton != null) {
	// bouton.setEnabled(true);
	// bouton.setSelected(false);
	// }
	// }

	// @Override
	// public void keyReleased(KeyEvent e) {//TOD Apparemment la méthode n'est
	// pas appelée à la pression d'une touche. Trouver pourquoi...
	// if (e.getSource() == this) {
	// if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	// controller.codeRecu(null, typedCode.toString());
	// typedCode.setLength(0);
	// }
	// else {
	// char c = e.getKeyChar();
	// if (c != KeyEvent.CHAR_UNDEFINED)
	// typedCode.append(c);
	// }
	// }
	// }

	public void componentShown(ComponentEvent arg0)
	{
		// this.requestFocus();
		this.requestFocusInWindow();
	}

	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentResized(ComponentEvent arg0) {
	}

	public JFrame getFrame() {
		return frame;
	}

	public List<DASGeneric> getGenerics() {
		return generics;
	}

	public JComponent getTopBtn(Integer i)
	{
		return topButtons.get(i);
	}

	private void setImageButton(AbstractButton bouton, String intitule) {
		bouton.setIcon(null);
		ImageIcon img;
		for (String ext : extImages)
		{
			img = new ImageIcon("images/" + intitule + ext);
			if (img.getImageLoadStatus() == MediaTracker.COMPLETE)
			{
				bouton.setIcon(img);
				break;
			}
		}
	}

	public int getPageEnCours() {
		return pageEnCours;
	}

	public void setPageEnCours(int pageEnCours) {
		this.pageEnCours = pageEnCours;
	}

	public Color getBG_MID_BUTTON_COLOR()
	{
		return BG_MID_BUTTON_COLOR;
	}


	public static void setBG_MID_BUTTON_COLOR(Color bGMIDBUTTONCOLOR)
	{
		BG_MID_BUTTON_COLOR = bGMIDBUTTONCOLOR;
	}

	public void setDepActive(String depActive) {
		this.depActive = depActive;
	}

	public String getDepActive() {
		return depActive;
	}

	public void setVisibleAllPanels(boolean bool){
		logDebug("Passage by setVisibleAllPanels");
		panelCenter.setVisible(bool);
		panelFooter.setVisible(bool);
	}

	public void setSwitchButton(boolean bool, String text)
	{
		if (dictbottomButtons.containsKey("SWITCH") == true)
		{
			logDebug("CONTAINS KEY SWITCH");
			((DASGradientJButton) dictbottomButtons.get("SWITCH")).setEnabled(bool);
			((DASGradientJButton) dictbottomButtons.get("SWITCH")).setText(text);
		}
	}

	private void logDebug(String log)
	{
		DASLog.logDebug(getClass().getSimpleName(), log);
	}

	public List <DASGradientJToggleButton> getSelectedButton(){
		return selectedButton;
	}

	public JPanel getPanelCenterBloc(){
		return panelCenterBloc;

	}
	public List<String> getDepNoData() {
		return depNoData;
	}

	public static DASController getController() {
		return controller;
	}

	public JPanel getPanelCenter()
	{
		return panelCenter;
	}

	
	public void setPanelCenter(JPanel panelCenter)
	{
		this.panelCenter = panelCenter;
	}

	public void updateKeyboardView(String value, String code)
	{
		keyboardPanel.updateInput(value, code);
	}
	
	public int getCALENDAR_ROWS()
	{
		return CALENDAR_ROWS;
	}

	public void setSwitchBtnName(String name)
	{
		this.SWITCH_BTN_NAME = name;
	}

	public String getSwitchBtnName()
	{
		return this.SWITCH_BTN_NAME;
	}

	public void setDateLookInstance(DateLook dateLookInstance)
	{
		this.dateLookInstance = dateLookInstance;
	}

	public DateLook getDateLookInstance()
	{
		return dateLookInstance;
	}
	
	public HashMap<String, JComponent> getDicttopButtons()
	{
		return dicttopButtons;
	}
	
}