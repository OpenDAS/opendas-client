package org.opendas.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.opendas.DASLoader;
import org.opendas.DASLog;
import org.opendas.ctrl.DASController;
import org.opendas.equipment.DASBaseMaterial;
import org.opendas.equipment.DASCOMMaterial;
import org.opendas.equipment.DASEquipments;
import org.opendas.ext.DASParserXmlGui;
import org.opendas.gui.DASGuiParams.DASFontException;
import org.opendas.modele.DASAcquisitionMethod;
import org.opendas.modele.DASConfigMaterial;

/**
 * Screen of the virtual keyboard
 * 
 * @author mlaroche
 */
public class DASKeyboardPanel extends JPanel
{

	private static final long			serialVersionUID			= 1L;
	private JPanel						parent						= null;
	private JTextField					instructions                = new JTextField();
	private JTextArea					input                       = null;
	private List<DASGradientJButton>	buttons						= new ArrayList<DASGradientJButton>();
	private Map<String, String>			params;
	private List<String>				completeABC 				= new LinkedList<String>();
	private List<String>				complete123 				= new LinkedList<String>();
	private List<String>				displayedABC				= new LinkedList<String>();
	private List<String>				displayed123				= new LinkedList<String>();
	private List<String>				options;
	private int							lines_buttons				= 6;
	private int							columns_buttons				= 7;
	/*private Dimension					information_dimension		= new Dimension(500, 30);*/
	private Font						instructions_font			= new Font("Arial", Font.PLAIN, 12);													// INSTRUCTIONS_POLICE
	private Font						inputs_font					= new Font("Arial", Font.PLAIN ,15);
	private Font						mid_font					= new Font("Arial", Font.PLAIN, 50);													// police_clavier
	private Font						next_font					= new Font("Arial", Font.PLAIN, 26);													// police_options
	private Font						top_field_font				= new Font("Arial", Font.PLAIN, 20);													// police_saisie
	private int							maxLengthInput				= 255;
	private String						inputRequestMsg				= "Please make your input";
	private String						completeInputMsg			= "Your input is complete. You can correct, cancel or validate it ";
	private String						emptyInputMsg				= "It's impossible to correct an empty input. You can correct, cancel or validate it ";
	private boolean						validatedInput				= false;

	// Récupérer le séparateur (depuis la base) et modifier pour acceptation java (double/string) ?
	private String						correctionMsg				= "Correction";
	private String						cancellationMsg				= "Cancel";
	private String						validationMsg				= "Validate";
	private String						caseMsg						= "Shift Lock";
	/** background color of the top screen */
	private Color						bg_top_color				= new Color(238, 238, 238);
	/** fields background color of the top screen */
	private Color						bg_top_field_color			= new Color(238, 238, 238);
	/** background color of the middle screen */
	private Color						bg_mid_color				= new Color(238, 238, 238);
	/** buttons background color of the middle screen */
	private Color						bg_mid_button_color			= new Color(150, 200, 255);
	/** navigation buttons background color */
	private Color						bg_next_color				= new Color(255, 200, 100);
	/** */
	/** inactive buttons background color of the middle screen*/
	private Color						bg_mid_inact_button_color	= new Color(238, 238, 238);
	private byte						bg_mid_gradient				= 1;
	private byte						bg_next_gradient			= 1;
	/** fields writing color of the top screen */
	private Color						fg_top_field_color			= new Color(0, 0, 0);
	/** font color of top screen if error*/
	private Color						fg_error_top_field_color	= new Color(255, 0, 0);
	/** navigation buttons writing color*/
	private Color						fg_next_color				= new Color(0, 0, 0);
	/** buttons writing color of the middle screen*/
	private Color						fg_mid_button_color			= new Color(0, 0, 0);

	private static Map<String, JTextArea>   inputs 						= new TreeMap<String, JTextArea>(Collections.reverseOrder());

	private static final int 			BOX_VSTRUT  				= 5;
	private DASController			controller                  = null; 

	private static Dimension 		LABEL_DIMENSION = new Dimension(100, 30);
	private static Dimension      BUTON_DIMENSION = new Dimension(95, 91);
	private static Dimension 		INPUTS_DIMENSION = new Dimension(300, 91);
	private static Dimension 		NUMBTN_DIMENSION = new Dimension(70, 100);
	private static Dimension 		ABCBTN_DIMENSION = new Dimension(40, 100);
	private static final String  		CODE_VKEYBOARD = "vkb";
	private static final String 		CODE_PKEYBOARD = "pkb";
	private static final String         CODE_VALID_BTN = "OK";
	
	public DASKeyboardPanel(JPanel parent, DASController controller) //throws TimeoutException
	{
		this.parent = parent;
		this.controller = controller;
		try {
			chargeParams();
		}
		catch (TimeoutException ex)
		{}
	}

	public void inits(String mode)
	{
		if (mode == null)
			return ;
		initABC(mode);
		init123(mode);
		initOptions(mode);
	}

	private void initABC(String mode)
	{
		System.out.println(displayedABC.toString());
		
		if (mode.equals("num") || mode.equals("int") || mode.equals("barre") || mode.equals("ean13") || mode.equals("ean128"))
		{
			removeFromCompleteABC(displayedABC);
			displayedABC.clear();
		}
	}
	
	private void init123(String mode)
	{
		System.out.println(displayed123.toString());
		
		if (mode.equals("barre") || mode.equals("ean13") || mode.equals("ean128"))
		{
			removeFromComplete123(displayed123);
			displayed123.clear();
		}
	}

	private void removeFromCompleteABC(List<String> list)
	{
		for (String c : list)
			completeABC.remove(c);
	}

	private void removeFromComplete123(List<String> list)
	{
		for (String c : list)
			complete123.remove(c);
	}
	

	private void initOptions(String mode)
	{
		options = new ArrayList<String>();		
		
		if (!mode.equals("barre")){
			options.add(correctionMsg);
			//options.add(cancellationMsg);
			options.add(validationMsg);
		}

		if (mode.equals("alphaMin") || mode.equals("alpha"))
		{
			options.add(caseMsg);
		}
	}

	private void chargeParams() throws TimeoutException
	{
		// TODO Trouver une solution pour éviter de recharger les paramètres à
		// chaque affichage du clavier (tout en l'adaptant à la donnée à saisir)
		// chargement des parametres xml
		
		DASParserXmlGui parserXml = new DASParserXmlGui(DASLoader.getGuiXml());
		params = parserXml.getParameters("keyboard");
		DASGuiParams outils = DASGuiParams.getInstance();
		// modification parameters from the xmls
		// font color of the middle screen
		try
		{
			bg_mid_color = outils.getColor(params, "MID_MODEL_BG");
			logDebug("Background color of the middle panel: " + bg_mid_color.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("Background color of the middle panel by default: " + bg_mid_color.toString());
		}
		// font of buttons of the middle screen 
		try
		{
			mid_font = outils.getFont(params, "MID_BUTTONS");
			logDebug("Buttons font of the middle panel: " + mid_font.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("Buttons font of the middle panel by default : " + mid_font.toString());
		}
		catch (DASFontException e)
		{
			logDebug("Buttons font of the middle panel by default : " + mid_font.toString());
		}
		// buttons background color of the middle screen
		try
		{
			bg_mid_button_color = outils.getColor(params, "MID_BUTTONS_BG");
			logDebug("Buttons background color of the middle panel by default :" + bg_mid_button_color.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("Buttons background color of the middle panel by default :" + bg_mid_button_color.toString());
		}
		// button gradation of the middle screen
		try
		{
			byte gradient = outils.getGradient(params.get("MID_BUTTONS_BG_GRADIENT"));
			bg_mid_gradient = gradient;
			logDebug("Buttons gradation of the middle panel : " + bg_mid_gradient);
		}
		catch (NumberFormatException e)
		{
			logDebug("Buttons gradation of the middle panel by default : " + bg_mid_gradient);
		}
		// buttons font color of the middle screen
		try
		{
			fg_mid_button_color = outils.getColor(params, "MID_BUTTONS_FG");
			logDebug("Buttons font color of the middle panel : " + fg_mid_button_color.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("Buttons font color of the middle panel by default: " + fg_mid_button_color.toString());
		}
		// inactive buttons font color of the middle screen 
		try
		{
			bg_mid_inact_button_color = outils.getColor(params, "MID_INACTIVE-BUTTONS_BG");
			logDebug("Inactive buttons font color of the middle panel : " + bg_mid_inact_button_color.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("Inactive buttons font color of the middle panel by default : " + bg_mid_inact_button_color.toString());
		}
		// navigation buttons font
		try
		{
			next_font = outils.getFont(params, "MID_NEXT");
			logDebug("Navigation buttons font: " + next_font.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("Navigation buttons font by default : " + next_font.toString());
		}
		catch (DASFontException e)
		{
			logDebug("Navigation buttons font by default : " + next_font.toString());
		}
		// navigation buttons font color
		try
		{
			bg_next_color = outils.getColor(params, "MID_NEXT_BG");
			logDebug("Navigation buttons font color : " + bg_next_color.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("Navigation buttons font color by default : " + bg_next_color.toString());
		}
		// navigation buttons gradation
		try
		{
			byte gradient = outils.getGradient(params.get("MID_NEXT_BG_GRADIENT"));
			bg_next_gradient = gradient;
			logDebug("Navigation buttons gradation : " + bg_next_gradient);
		}
		catch (NumberFormatException e)
		{
			logDebug("Navigation buttons gradation : " + bg_next_gradient);
		}
		// navigation buttons font color
		try
		{
			fg_next_color = outils.getColor(params, "MID_NEXT_FG");
			logDebug("Navigation buttons font color : " + fg_next_color.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("Navigation buttons font color by default : " + fg_next_color.toString());
		}
		// background color of the top panel
		try
		{
			outils.getColor(params, "TOP_MODEL_BG");
			logDebug("Background color of the top panel : " + bg_top_color.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("Background color of the top panel : " + bg_top_color.toString());
		}
		// Fields font of the top panel
		try
		{
			top_field_font = outils.getFont(params, "TOP_FIELDS");
			logDebug("Fields font of the top panel : " + top_field_font.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("Fields font of the top panel by default : " + top_field_font.toString());
		}
		catch (DASFontException e)
		{
			logDebug("Fields font of the top panel by default : " + top_field_font.toString());
		}
		// Fields background color of the top panel
		try
		{
			bg_top_field_color = outils.getColor(params, "TOP_FIELDS_BG");
			logDebug("Fields background color of the top panel : " + bg_top_field_color.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("Fields background color of the top panel by default : " + bg_top_field_color.toString());
		}
		// foreground color of fields of the top panel
		try
		{
			fg_top_field_color = outils.getColor(params, "TOP_FIELDS_FG");
			logDebug("fields foreground color of the top panel : " + fg_top_field_color.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("Fields foreground color of the top panel : " + fg_top_field_color.toString());
		}
		// fields font color of the top panel if error
		try
		{
			fg_error_top_field_color = outils.getColor(params, "TOP_FIELDS_FG-ERROR");
			logDebug("Fields font color of the top panel if error : " + fg_error_top_field_color.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("Fields font color of the top panel if error by default if error : " + fg_error_top_field_color.toString());
		}
		
		try
		{
			LABEL_DIMENSION = outils.getDIM(params, "TOP_FIELDS_DIM");
			logDebug("Label " + LABEL_DIMENSION.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("label : " + LABEL_DIMENSION.toString());
		}
		try
		{
			INPUTS_DIMENSION = outils.getDIM(params, "TOP_INPUTS_DIM");
			logDebug("INPUTS_DIMENSION " + INPUTS_DIMENSION.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("INPUTS_DIMENSION : " + INPUTS_DIMENSION.toString());
		}
		try
		{
			BUTON_DIMENSION = outils.getDIM(params, "TOP_BUTTONS_DIM");
			logDebug("BUTON_DIMENSION " + BUTON_DIMENSION.toString());
		}
		catch (NumberFormatException e)
		{
			logDebug("BUTON_DIMENSION : " + BUTON_DIMENSION.toString());
		}
	}

	private void changeCasse()
	{
		List<String> tmp_displayedABC = new  ArrayList<String>();
		tmp_displayedABC.addAll(displayedABC);
		for (String character : tmp_displayedABC)
		{
			char c = character.charAt(0);
			DASGradientJButton buton = getButonByName(character);
			if (buton == null)
				return ;
			if (c >= 'A' && c <= 'Z')
				buton.setText(buton.getText().toLowerCase());
			else if (c >= 'a' && c <= 'z')
				buton.setText(buton.getText().toUpperCase());
			else if (c == 'é' || c == 'è' || c == 'ê' || c == 'à')
				buton.setText(buton.getText().toUpperCase());
			else if (c == 'È' || c == 'É' || c == 'Ê' || c == 'À')
				buton.setText(buton.getText().toLowerCase());
			displayedABC.remove(character);
			displayedABC.add(buton.getText());
			completeABC.remove(character);
			completeABC.add(buton.getText());
		}
		this.parent.updateUI();
	}

	public void keyReleased(String character, int code_caractere)
	{
		logDebug("Passage by keyReleased 2");
		boolean isAllowed = false;
		
		for(DASBaseMaterial mat : controller.getAcMaterial()){
			if(mat.getType().getIscumulative().equals("true")){
				input = inputs.get(mat.getCode());
			}
		}	
		
		for (String car_temp : this.getCompleteABC())
		{
			if (car_temp.toLowerCase().equals(character) || car_temp.toUpperCase().equals(character))
			{
				if (character.equals(",") || character.equals("."))
				{
					if (input.getText().indexOf(",") != -1 || input.getText().indexOf(".") != -1)
					{
						return;
					}
				}
				isAllowed = true;
				break;
			}
		}
		if (isAllowed)
		{
			ajoutSaisie(character);
		}
		else if (code_caractere == KeyEvent.VK_BACK_SPACE)
		{
			correctionSaisie();
		}else{
		}
	}

	public JPanel buildFooterPane()
	{
		JPanel footerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 0, 5, 5);
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;

		for (int i = 0; i < options.size(); ++i)
		{
			DASGradientJButton bouton = new DASGradientJButton(bg_next_gradient, bg_next_color, bg_mid_inact_button_color);
			bouton.setBackground(bg_next_color);
			bouton.setFocusable(false);
			bouton.setText(options.get(i));
			bouton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e)
				{
					input = inputs.get(CODE_VKEYBOARD);
					String action = ((DASGradientJButton) e.getSource()).getText();
					if (action.equals(correctionMsg))
					{
						correctionSaisie();
					}
					else if (action.equals(validationMsg))
					{
						envoiSaisie();
					}
					else if (action.equals(caseMsg))
					{
						changeCasse();
					}
					else
					{
						logErr("Action " + action + " not defined");
					}
				}
			});
			bouton.setFont(top_field_font);
			bouton.setHorizontalTextPosition(SwingConstants.CENTER);
			bouton.setVerticalTextPosition(SwingConstants.BOTTOM);
			bouton.setPreferredSize(new Dimension(125, 50));
			bouton.setFocusable(false);
			if (c.gridx == 1)
				c.insets = new Insets(5, 5, 5, 5);
			else if (c.gridx + 1 == options.size())
				c.insets = new Insets(5, 5, 5, 0);
			footerPanel.add(bouton, c);
			++c.gridx;
		}
		return footerPanel;
	}

	public JPanel buildPaneInstruction()
	{
		JPanel panel = new JPanel();
		panel.setBackground(bg_top_color);
		instructions.setEditable(false);
		instructions.setHighlighter(null);
		instructions.setFont(instructions_font);
		instructions.setFocusable(false);
		instructions.setPreferredSize(new Dimension(this.getWidth() - 5, 30));
		instructions.setBackground(bg_top_field_color);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 5;
		panel.add(instructions);
		return panel;
	}

	/**
	 *  Get number of material acquisition methods 
	 **/
	private List<String> getCodesMaterials(List<DASAcquisitionMethod> methods, DASEquipments equipments)
	{
		logDebug("Passage by getCodesMaterial");
		
		List<String> codes = new LinkedList<String>();
		List<DASConfigMaterial> temps = equipments.loader.getConfig().getMaterials();

		for (DASAcquisitionMethod i : methods)
		{
			//Browse configMaterialObject
			for (DASConfigMaterial confm : temps)
			{
				DASConfigMaterial confMaterial = confm;
				org.opendas.modele.DASMaterial material = confMaterial.getMaterial();		
				//Browse list of material E/R initialized
				for(DASBaseMaterial mat : controller.getAcMaterial()){
					if(confm.getCode().equals(mat.getCode())){
						System.out.println("Mat parcouru :"+mat.getCode());
						if(i.getTypeMaterial() != null){
							//If type material lied at acquisition method is type material of material then
							System.out.println("i Name :"+i.getTypeMaterial().getName()+" comparé à typemat Name :"+material.getModelMaterial().getTypeMaterial().getName());
							if(material.getModelMaterial().getTypeMaterial().getName().equals(i.getTypeMaterial().getName()))
							{	
								codes.add(mat.getCode());
							}
						}
					}
				}
			}
		}
		return codes;
	}

	public JPanel buildPaneInputs(List<DASAcquisitionMethod> methods, DASEquipments equipments)
	{
		logDebug("Passage by buildPaneInputs");
		List<String> codes = getCodesMaterials(methods, equipments);
		logDebug("codes :" + codes.toString());
		this.setLayout(new BorderLayout());
		JPanel panelInputs = new JPanel(/*new GridBagLayout()*/);
		panelInputs.setBackground(bg_top_color);

		panelInputs.setLayout(new BoxLayout(panelInputs, BoxLayout.Y_AXIS));

		logDebug(String.valueOf(controller.isEnabledVKB()));
		if (controller.isEnabledVKB() == true)
		{
			
			JTextArea vkbInput = new JTextArea();

			vkbInput.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
			vkbInput.setLineWrap(true);
			vkbInput.setWrapStyleWord(true);
			vkbInput.setEditable(false);
			vkbInput.setHighlighter(null);
			vkbInput.setFont(inputs_font);
			vkbInput.setFocusable(false);
			vkbInput.setPreferredSize(INPUTS_DIMENSION);
			vkbInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, INPUTS_DIMENSION.height));
			vkbInput.setBackground(bg_top_field_color);

			inputs.put(CODE_VKEYBOARD, vkbInput);
		}

		for (String code : codes)
		{
			logDebug("KEYPANEL => CODE =>"+code);
			JTextArea input = new JTextArea();

			input.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
			input.setLineWrap(true);
			input.setWrapStyleWord(true);
			input.setEditable(false);
			input.setHighlighter(null);
			input.setFont(inputs_font);
			input.setFocusable(false);
			input.setPreferredSize(INPUTS_DIMENSION);
			input.setMaximumSize(new Dimension(Integer.MAX_VALUE, INPUTS_DIMENSION.height));
			input.setBackground(bg_top_field_color);
			inputs.put(code, input);
		}
		logDebug("inputs after codes:"+ inputs.toString());
		
		for (Entry<String, JTextArea> entry : inputs.entrySet())
		{
			JLabel label;
			if (entry.getKey().equals(CODE_VKEYBOARD)){
				label = new JLabel("V. Keyboard :");
			}else{
				label = new JLabel("MAT " + entry.getKey() + " :");
			}
			label.setPreferredSize(LABEL_DIMENSION);
			label.setMaximumSize(LABEL_DIMENSION);
			
			Box box = Box.createHorizontalBox();
			box.add(label);
			box.add(entry.getValue());
			DASGradientJButton buton = new DASGradientJButton(bg_next_gradient, bg_next_color, bg_mid_inact_button_color);
			buton.setBackground(bg_next_color);
			buton.setFocusable(false);
			buton.setText(CODE_VALID_BTN);
			buton.setName(entry.getKey());
			buton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e)
				{
					String key = ((DASGradientJButton) e.getSource()).getName();
					input = inputs.get(key);
					envoiSaisie();
				}
			});
			buton.setFont(top_field_font);
			buton.setHorizontalTextPosition(SwingConstants.CENTER);
			buton.setVerticalTextPosition(SwingConstants.BOTTOM);
			buton.setPreferredSize(BUTON_DIMENSION);
			buton.setMaximumSize(BUTON_DIMENSION);					
			box.add(buton);
			panelInputs.add(box);
			panelInputs.add(Box.createVerticalStrut(BOX_VSTRUT));
		}
		return panelInputs;
	}

	public JPanel buildPane123()
	{
		JPanel panel123 = null;
		if (displayed123.size() > 0)
		{
			panel123 = new JPanel(new GridBagLayout());
			panel123.setBackground(bg_top_color);

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(2, 2, 2, 2);
			c.fill = GridBagConstraints.BOTH;
			c.weighty = 1.0;
			c.weightx = 1.0;
			c.gridx = 0;
			c.gridy = 0;

			for (String key : displayed123)
			{
				if (displayed123.indexOf(key) % 3 == 0 && displayed123.indexOf(key) > 0)
				{
					c.gridx = 0;
					c.gridy++;
				}
				DASGradientJButton bouton = new DASGradientJButton(bg_mid_gradient, bg_mid_color, bg_mid_inact_button_color);
				bouton.setBackground(bg_mid_button_color);
				bouton.setFocusable(false);
				bouton.setText(key);
				bouton.setPreferredSize(NUMBTN_DIMENSION);
				bouton.setMaximumSize(new Dimension(Integer.MAX_VALUE, NUMBTN_DIMENSION.height));
				bouton.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e)
					{
						input = inputs.get(CODE_VKEYBOARD);
						if (((DASGradientJButton) e.getSource()).getText().equals(",") || ((DASGradientJButton) e.getSource()).getText().equals(","))
						{
							if (input.getText().indexOf(",") != -1 || input.getText().indexOf(".") != -1)
							{
								return;
							}
						}
						ajoutSaisie(((DASGradientJButton) e.getSource()).getText());
					}
				});
				panel123.add(bouton, c);
				c.gridx++;
			}
			int index = displayed123.size();
			while (c.gridy < lines_buttons || index % 3 != 0)
			{
				JTextArea bouton = new JTextArea();
				bouton.setBackground(parent.getBackground());
				bouton.setEnabled(false);
				bouton.setPreferredSize(NUMBTN_DIMENSION);
				bouton.setMaximumSize(new Dimension(Integer.MAX_VALUE, NUMBTN_DIMENSION.height));
				if (index % 3 == 0)
				{
					c.gridy++;
					c.gridx = 0;
				}
				panel123.add(bouton, c);
				c.gridx++;
				index++;
			}
		}
		return panel123;
	}

	public JPanel buildPaneABC()
	{
		JPanel panelABC = null;
		if (displayedABC.size() > 0)
		{
			panelABC = new JPanel(new GridBagLayout());
			panelABC.setBackground(bg_top_color);

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(2, 2, 2, 2);
			c.fill = GridBagConstraints.BOTH;
			c.weighty = 1.0;
			c.weightx = 1.0;
			c.gridx = 0;
			c.gridy = 0;

			for (String key : displayedABC)
			{
				if (displayedABC.indexOf(key) % 6 == 0 && displayedABC.indexOf(key) > 0)
				{
					c.gridx = 0;
					c.gridy++;
				}
				DASGradientJButton bouton = new DASGradientJButton(bg_mid_gradient, bg_mid_color, bg_mid_inact_button_color);
				bouton.setBackground(bg_mid_button_color);
				bouton.setFocusable(false);
				bouton.setText(key);
				bouton.setPreferredSize(ABCBTN_DIMENSION);
				bouton.setMaximumSize(new Dimension(Integer.MAX_VALUE, ABCBTN_DIMENSION.height));
				bouton.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e)
					{
						input = inputs.get(CODE_VKEYBOARD);
						if (((DASGradientJButton) e.getSource()).getText().equals(",") || ((DASGradientJButton) e.getSource()).getText().equals("."))
						{
							if (input.getText().indexOf(",") != -1 || input.getText().indexOf(".") != -1)
							{
								return;
							}
						}
						ajoutSaisie(((DASGradientJButton) e.getSource()).getText());
					}
				});
				buttons.add(bouton);
				panelABC.add(bouton, c);
				c.gridx++;
			}
			int index = displayedABC.size();
			while (c.gridy < columns_buttons || index % 6 != 0)
			{
				JTextArea bouton = new JTextArea();
				bouton.setBackground(parent.getBackground());
				bouton.setEnabled(false);
				bouton.setPreferredSize(ABCBTN_DIMENSION);
				bouton.setMaximumSize(new Dimension(Integer.MAX_VALUE, ABCBTN_DIMENSION.height));
				if (index % 6 == 0)
				{
					c.gridy++;
					c.gridx = 0;
				}
				panelABC.add(bouton, c);
				c.gridx++;
				index++;
			}
		}
		return panelABC;
	}

	public void activate() throws Exception
	{
		if (input == null)
			return ;
		showInstruction(inputRequestMsg);
		//input.setText("");
	}

	public boolean validatedInput()
	{
		return validatedInput;
	}

	public void ajoutSaisie(String caractere)
	{
		int longueurSaisie = input.getText().length();
		if (longueurSaisie < maxLengthInput)
		{
			input.setText(input.getText() + caractere);
			longueurSaisie = input.getText().length();
			if (longueurSaisie < maxLengthInput)
			{
				showInstruction(inputRequestMsg);
			}
			else
			{
				showInstruction(completeInputMsg);
			}
		}
		else
		{
			log(completeInputMsg);
		}
	}

	public void correctionSaisie()
	{
		int longueurSaisie = input.getText().length();
		if (longueurSaisie > 0)
		{
			input.setText(input.getText().substring(0, longueurSaisie - 1));
			showInstruction(inputRequestMsg);
		}
		else
		{
			showError(emptyInputMsg);
		}
	}

	public void envoiSaisie()
	{
		logDebug("Passage by envoiSaisie :REINITIALISER button");
		DASCOMMaterial.mat_inputs.clear();
		cleanInputs();
		((DASPanel)parent).validateData();
		
	}

	public void cleanInputs()
	{
		logDebug("Passage by cleanInputs");
		for (Entry<String, JTextArea> entry : inputs.entrySet()){
			logDebug(entry.getValue().getText() + " become");
			entry.getValue().setText("");
			logDebug("[" + entry.getValue().getText() + "]");
			controller.getFunctional_context().remove("mapMaterial");
		}
		this.parent.updateUI();
	}

	private void showInstruction(String text)
	{
		if (text == null)
			return ;
		instructions.setForeground(fg_top_field_color);
		instructions.setText(text);
	}

	private void showError(String text)
	{
		instructions.setForeground(fg_error_top_field_color);
		instructions.setText(text);
	}

	public String getSaisie()
	{
		return input.getText();
	}

	public int getMaxLengthInput()
	{
		return maxLengthInput;
	}

	public void setLignes_boutons(int lignesBoutons)
	{
		lines_buttons = lignesBoutons;
	}

	public void setColones_boutons(int colonesBoutons)
	{
		columns_buttons = colonesBoutons;
	}

	/*public void setDimension_information_top(Dimension dimensionInformationTop)
	{
		information_dimension = dimensionInformationTop;
	}*/

	public void setPolice(Font police)
	{
		this.instructions_font = police;
	}

	public void setPolice_clavier(Font policeClavier)
	{
		mid_font = policeClavier;
	}

	public void setPolice_options(Font policeOptions)
	{
		next_font = policeOptions;
	}

	public void setPolice_saisie(Font policeSaisie)
	{
		mid_font = policeSaisie;
	}

	public void setMaxLengthInput(int maxLengthInput)
	{
		if (maxLengthInput > 0)
		{
			this.maxLengthInput = maxLengthInput;
		}
	}

	public void setInputRequest(String demandeSaisie)
	{
		this.inputRequestMsg = demandeSaisie;
	}

	public void setCorrectionName(String correction)
	{
		this.correctionMsg = correction;
	}

	public void setCancellationName(String cancellation)
	{
		this.cancellationMsg = cancellation;
	}

	public void setValidationName(String validation)
	{
		this.validationMsg = validation;
	}

	public void setCaseName(String kbcase)
	{
		this.caseMsg = kbcase;
	}

	public void setCompleteInput(String saisieComplete)
	{
		this.completeInputMsg = saisieComplete;
	}

	public void setEmptyInput(String saisieVide)
	{
		this.emptyInputMsg = saisieVide;
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

	public void setDisplayedABC(String strABC)
	{
		displayedABC.clear();
		String[] tabABC = strABC.split(" ");
		for (int i = 0; i < tabABC.length; i++)
		{
			displayedABC.add(tabABC[i]);
			completeABC.add(tabABC[i]);
		}
	}

	public void setDisplayed123(String str123)
	{
		displayed123.clear();
		String[] tab123 = str123.split(" ");
		for (int i = 0; i < tab123.length; i++)
		{
			displayed123.add(tab123[i]);
			complete123.add(tab123[i]);
		}
	}

	public void updateInput(String value, String code)
	{
		logDebug("passage by updateInput");
		logDebug("inputs.size:" + inputs.size());
		for (Entry<String, JTextArea> entry : inputs.entrySet())
		{
			if (entry.getKey().equals(code))
			{
				input = entry.getValue();
				input.setText("");
				ajoutSaisie(value);
				
			}
		}
	}

	public DASGradientJButton getButonByName(String name)
	{
		for (DASGradientJButton buton : buttons)
		{
			if (buton.getText().equals(name) == true)
				return buton;
		}
		return null;
	}

	public List<String> getCompleteABC()
	{
		return completeABC;
	}
	
	public void setCompleteABC(List<String> completeABC)
	{
		this.completeABC = completeABC;
	}

	
	
	
}