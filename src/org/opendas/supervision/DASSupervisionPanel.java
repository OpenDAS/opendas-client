package org.opendas.supervision;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.opendas.DASLoader;
import org.opendas.DASLog;
import org.opendas.ext.DASFunctions;
import org.opendas.ext.DASParserXmlFcts;
import org.opendas.gui.DASPanel;
import org.opendas.modele.DASDialog;
import org.opendas.modele.DASTemplateSupervision;

@SuppressWarnings("serial")
public class DASSupervisionPanel extends JPanel
{

	private HashMap<String, List<DASDialog>>		dialogSorted		= new HashMap<String, List<DASDialog>>();
	List<DASSupervisor>								supervisorList		= new LinkedList<DASSupervisor>();
	List<DASShape>									staticElementList	= new LinkedList<DASShape>();
	HashMap<String, DASSupervisionTopicListener>	topicListenerList	= new HashMap<String, DASSupervisionTopicListener>();
	HashMap<String, DASSupervisionTopicProducer>	topicProducerList	= new HashMap<String, DASSupervisionTopicProducer>();
	List<DASInputGenerator>							inputGeneratorList	= new LinkedList<DASInputGenerator>();
	String											pathplan			= "";
	public Image									image;
	DASPanel										panelDAS;
	int												caseHeightY			= 1;
	int												caseWidthX			= 1;
	int												caseCountY			= 1;
	int												caseCountX			= 1;
	int												currentGridWidthX   = 0;
	int												currentGridHeightY  = 0;
	int												imgPositionX		= 0;
	int												imgPositionY		= 0;
	int												scaleHeightY		= 0;
	int												currentScaleHeightY	= 0;
	int												currentScaleWidthX	= 0;
	int												partLargeurEnCours	= 0;

	public DASSupervisionPanel(DASTemplateSupervision ts, List<DASDialog> dialogList, DASPanel dasPanel){
		this.panelDAS = dasPanel;
		this.setLayout(null);
		this.setDialog(dialogList);
		//BACKGROUND IMAGE

		try{
			//TODO TEST
			pathplan = System.getProperty("user.dir")+"/ressources/"+ ts.getImage()+"";
			DASLog.logErr("Path:","Ressources :"+ pathplan);
			//pathplan = System.getProperty("user.dir")+"/ressources/archi1.jpg";
			image = ImageIO.read(new File(pathplan));

			//image = new ImageIcon(ts.getImage()).getImage();
			
		}catch(Exception e){}

		caseHeightY = 1;
		caseWidthX = 1;

		//GET CASE SIZE AND GRID SIZE
		if(ts.getHeight_case() != null && ts.getWidth_case() != null){
			caseHeightY = ts.getHeight_case();
			caseWidthX = ts.getWidth_case();
		}

		if(ts.getNb_case_y() != null && ts.getNb_case_x() != null){
			caseCountY = ts.getNb_case_y();
			caseCountX = ts.getNb_case_x();
		}

		// AUTO RESIZE GRID FROM SCREEN DIMENSIONS
		int gridWidthX = (caseCountX*caseWidthX);
		if((caseCountX*caseWidthX) > Toolkit.getDefaultToolkit().getScreenSize().getWidth()){
			currentGridWidthX = (caseCountX*caseWidthX);
			
			// While the grid width is so long, we retire the last case to resize
			while(currentGridWidthX > Toolkit.getDefaultToolkit().getScreenSize().getWidth()){
				currentGridWidthX = currentGridWidthX - caseWidthX;
				currentScaleWidthX = Math.round((gridWidthX/currentGridWidthX));
			}
		}
		int gridHeightY = (caseCountY*caseHeightY);
		if((caseCountY*caseHeightY) > Toolkit.getDefaultToolkit().getScreenSize().getHeight()-250){
			currentGridHeightY = (caseCountY*caseHeightY);
			
			// While the grid height is so long, we retire the last case to resize
			while(currentGridHeightY > Toolkit.getDefaultToolkit().getScreenSize().getHeight()-250){
				currentGridHeightY = currentGridHeightY - caseHeightY;
				scaleHeightY = Math.round((gridHeightY/currentGridHeightY));
			}
		}
		
		
		// AUTO RESIZE GRID FROM SCREEN DIMENSIONS AND IMAGE SIZE
		
		/*if(image != null){
			int tmpImgX = 0;
			tmpImgX = image.getWidth(null);
			if(tmpImgX > Toolkit.getDefaultToolkit().getScreenSize().getWidth()){
				currentGridWidthX = tmpImgX;
				while(currentGridWidthX > Toolkit.getDefaultToolkit().getScreenSize().getWidth()){
					currentGridWidthX = currentGridWidthX - caseWidthX;
					currentScaleWidthX = Math.round((tmpImgX/currentGridWidthX));
				}
			}
			
			int tmpImgY = image.getHeight(null);
			if(tmpImgY > Toolkit.getDefaultToolkit().getScreenSize().getHeight()-250){
				currentGridHeightY = tmpImgY;
				while(currentGridHeightY > Toolkit.getDefaultToolkit().getScreenSize().getHeight()-250){
					currentGridHeightY = currentGridHeightY - caseHeightY;
					scaleHeightY = Math.round((tmpImgY/currentGridHeightY));
				}
			}
		}*/

		checkButtonsUpDown();
		checkButtonsPrevNext();

		//INITIALIZE DIMENSIONS FOR EACH SUPERVISION SHAPE
		List<Entry<String, Object>> entries = new ArrayList<Entry<String, Object>>(new DASParserXmlFcts(ts.getMapping()).getParameters().entrySet());
		for(Entry<String, Object> en : entries){			
			if(en.getValue() instanceof DASFunctions){
				
				//INITIALIZE SHAPE WIDTH
				int widthShape = caseWidthX;
				if(((DASFunctions) en.getValue()).get_child("sizeW") != null){
					if(((String) ((DASFunctions) en.getValue()).get_child("sizeW").get("_value")).contains(".") || ((String) ((DASFunctions) en.getValue()).get_child("sizeW").get("_value")).contains(",")){
						widthShape = (int) (caseWidthX*Double.parseDouble((String) ((DASFunctions) en.getValue()).get_child("sizeW").get("_value")));
					}else{
						widthShape = caseWidthX*Integer.parseInt((String) ((DASFunctions) en.getValue()).get_child("sizeW").get("_value"));
					}
				}

				//INITIALIZE SHAPE HEIGHT
				int heightShape = caseHeightY;
				if(((DASFunctions) en.getValue()).get_child("sizeH") != null){
					if(((String) ((DASFunctions) en.getValue()).get_child("sizeH").get("_value")).contains(".") || ((String) ((DASFunctions) en.getValue()).get_child("sizeH").get("_value")).contains(",")){
						heightShape = (int) (caseHeightY*Double.parseDouble((String) ((DASFunctions) en.getValue()).get_child("sizeH").get("_value")));
					}else{
						heightShape = caseHeightY*Integer.parseInt((String) ((DASFunctions) en.getValue()).get_child("sizeH").get("_value"));
					}
				}

				//INITIALIZE SHAPE POSITION X
				int positionShapeX = 0;
				if(((DASFunctions) en.getValue()).get_child("positionX") != null){
					if(((String) ((DASFunctions) en.getValue()).get_child("positionX").get("_value")).contains(".") || ((String) ((DASFunctions) en.getValue()).get_child("positionX").get("_value")).contains(",")){
						positionShapeX = (int) (caseWidthX*Double.parseDouble(((String) ((DASFunctions) en.getValue()).get_child("positionX").get("_value"))));
					}else{
						positionShapeX = caseWidthX*Integer.parseInt(((String) ((DASFunctions) en.getValue()).get_child("positionX").get("_value")));
					}
				}
				
				//INITIALIZE SHAPE POSITION Y
				int positionShapeY = 0;
				if(((DASFunctions) en.getValue()).get_child("positionY") != null){
					if(((String) ((DASFunctions) en.getValue()).get_child("positionY").get("_value")).contains(".") || ((String) ((DASFunctions) en.getValue()).get_child("positionY").get("_value")).contains(",")){
						positionShapeY = (int) (caseHeightY*Double.parseDouble(((String) ((DASFunctions) en.getValue()).get_child("positionY").get("_value"))));
					}else{
						positionShapeY = caseHeightY*Integer.parseInt(((String) ((DASFunctions) en.getValue()).get_child("positionY").get("_value")));
					}
				}
				
				//BUILD THE SHAPE				
				DASShape shape = new DASShape(positionShapeX,positionShapeY,widthShape,heightShape);

				//ADD OPTIONS
				if(((DASFunctions) en.getValue()).get_child("orientation") != null){
					shape.setOrientation((String) ((DASFunctions) en.getValue()).get_child("orientation").get("_value"));
				}

				if(((DASFunctions) en.getValue()).get_child("filled") != null){
					shape.setFilled((String) ((DASFunctions) en.getValue()).get_child("filled").get("_value"));
				}

				if(((DASFunctions) en.getValue()).get_child("shape") != null){
					shape.setShape((String) ((DASFunctions) en.getValue()).get_child("shape").get("_value"));
				}

				/*if(((DASFunctions) en.getValue()).get_child("linkedto") != null){
					shape.setLinkedTo(Integer.parseInt(((String) ((DASFunctions) en.getValue()).get_child("linkedto").get("_value"))));
				}

				if(((DASFunctions) en.getValue()).get_child("linkedfrom") != null){
					shape.setLinkedFrom(Integer.parseInt(((String) ((DASFunctions) en.getValue()).get_child("linkedfrom").get("_value"))));
				}*/

				//If shape is supervisor = authorize a shape to have the same color interaction than a supervisor
				//If we don't display a value, the data is not save to historic and the shape is not clickable
				if(((DASFunctions) en.getValue()).get("_name").equals("sup")){

					DASSupervisor sup = new DASSupervisor(shape,"","",this);

					if(((DASFunctions) en.getValue()).get_child("savedata") != null){
						if(((String) ((DASFunctions) en.getValue()).get_child("savedata").get("_value")).equals("1")){
							sup.setSaveData(true);
						}
					}

					if(((DASFunctions) en.getValue()).get_child("nbsaveddata") != null){
						sup.setNbSavedData(((String) ((DASFunctions) en.getValue()).get_child("nbsaveddata").get("_value")));
					}

					if(((DASFunctions) en.getValue()).get_child("displayvalue") != null){
						if(((String) ((DASFunctions) en.getValue()).get_child("displayvalue").get("_value")).equals("1")){
							shape.addMouseListener(new DASSupervisorListener(shape));
							sup.setDisplayValue(true);
						}					
					}

					if(((DASFunctions) en.getValue()).get_child("graphtype") != null){
						sup.setGraphType((String) ((DASFunctions) en.getValue()).get_child("graphtype").get("_value"));
					}
					
					if(((DASFunctions) en.getValue()).get_child("valuetype") != null){
						sup.setGraphType((String) ((DASFunctions) en.getValue()).get_child("valuetype").get("_value"));
					}
					

					if(((DASFunctions) en.getValue()).get_child("graphtime") != null){
						sup.setGraphTime((String) ((DASFunctions) en.getValue()).get_child("graphtime").get("_value"));
					}

					if(((DASFunctions) en.getValue()).get_child("graphline") != null){
						if(((String) ((DASFunctions) en.getValue()).get_child("graphline").get("_value")).equals("1")){
							sup.setGraphLine(true);
						}
					}

					//SAVE AND CREATE TOPIC LISTENER TO RECEPTION MESSAGES FROM MATERIALS
					
					if(((DASFunctions) en.getValue()).get_child("listentopic") != null){
						String tmpTopicName = (String) ((DASFunctions) en.getValue()).get_child("listentopic").get("_value");
						if(topicListenerList.containsKey(tmpTopicName)){

							sup.setTopicListener(topicListenerList.get(tmpTopicName));
							topicListenerList.get(tmpTopicName).addSupervisor(sup);

						}else{

							DASDialog dialog = null;
							if(dialogSorted != null){
								if(dialogSorted.containsKey(tmpTopicName)){
									dialog = getDialog(tmpTopicName);					
								}				
							}else{
								dialog = getDialog("supervision_default");	
							}
							
							DASSupervisionTopicListener dtl = DASLoader.addTopicListener(tmpTopicName,dialog,sup);

							topicListenerList.put(tmpTopicName, dtl);
							sup.setTopicListener(dtl);

						}
					}

					//SAVE MATERIAL QUEUE NAMES
					
					if(((DASFunctions) en.getValue()).get_child("sendtopic") != null){
						String tmpTopicName = (String) ((DASFunctions) en.getValue()).get_child("sendtopic").get("_value");
						if(topicProducerList.containsKey(tmpTopicName)){

							sup.setTopicProducer(topicProducerList.get(tmpTopicName));

						}else{
							DASSupervisionTopicProducer dtl = DASLoader.addTopicProducer(tmpTopicName, sup);

							topicProducerList.put(tmpTopicName, dtl);
							sup.setTopicProducer(dtl);

						}
					}

					if(((DASFunctions) en.getValue()).get_child("code") != null){
						sup.setCode(Integer.parseInt(((String) ((DASFunctions) en.getValue()).get_child("code").get("_value"))));
					}

					if(((DASFunctions) en.getValue()).get_child("refcode") != null){
						sup.setRefcode(Integer.parseInt(((String) ((DASFunctions) en.getValue()).get_child("refcode").get("_value"))));
					}

					if(((DASFunctions) en.getValue()).get_child("material") != null){
						sup.setSourceMaterial((String) ((DASFunctions) en.getValue()).get_child("material").get("_value"));
					}

					if(((DASFunctions) en.getValue()).get_child("variable") != null){
						sup.setSourceVariable((String) ((DASFunctions) en.getValue()).get_child("variable").get("_value"));
					}

					// CUSTOMIZE COLOR FOR MATERIAL VALUE MIN MAX
					
					List<Entry<String, Object>> entriesList = new ArrayList<Entry<String, Object>>(((DASFunctions) en.getValue()).get_childs().entrySet());
					for(Entry<String, Object> enL : entriesList)
					{
						if(((DASFunctions) enL.getValue()).get("_name").equals("cmm")){
							HashMap<String, Integer> hm = new HashMap<String, Integer>();

							if(((DASFunctions) enL.getValue()).get("MIN") != null){
								hm.put("min", Integer.parseInt((String) ((DASFunctions) enL.getValue()).get("MIN")));
							}

							if(((DASFunctions) enL.getValue()).get("MAX") != null){
								hm.put("max", Integer.parseInt((String) ((DASFunctions) enL.getValue()).get("MAX")));
							}

							sup.getColorNum().put(hm, 
									new Color(Integer.parseInt((String) ((DASFunctions) enL.getValue()).get("COLORR")), 
											Integer.parseInt((String) ((DASFunctions) enL.getValue()).get("COLORG")), 
											Integer.parseInt((String) ((DASFunctions) enL.getValue()).get("COLORB"))));

							sup.setColorMethod("num");

						}else if(((DASFunctions) enL.getValue()).get("_name").equals("cv")){

							sup.getColorList().put(
									""+Double.parseDouble((String) ((DASFunctions) enL.getValue()).get("VALUE")),
									new Color(Integer.parseInt((String) ((DASFunctions) enL.getValue()).get("COLORR")), 
											Integer.parseInt((String) ((DASFunctions) enL.getValue()).get("COLORG")), 
											Integer.parseInt((String) ((DASFunctions) enL.getValue()).get("COLORB"))));

							sup.setColorMethod("liste");

						}

						if(((DASFunctions) enL.getValue()).get("_name").equals("inputGenerator")){

							//Simule a material input with loop							
							DASInputGenerator ig = new DASInputGenerator(sup);

							if(((DASFunctions) enL.getValue()).get("MIN") != null){
								ig.setMin(Integer.parseInt((String) ((DASFunctions) enL.getValue()).get("MIN")));
							}

							if(((DASFunctions) enL.getValue()).get("MAX") != null){
								ig.setMax(Integer.parseInt((String) ((DASFunctions) enL.getValue()).get("MAX")));
							}

							if(((DASFunctions) enL.getValue()).get("CONTINUE") != null){
								ig.setCont(Integer.parseInt((String) ((DASFunctions) enL.getValue()).get("CONTINUE")));
							}

							if(((DASFunctions) enL.getValue()).get("TIMEINTERVAL") != null){
								ig.setTimeInterval(Integer.parseInt((String) ((DASFunctions) enL.getValue()).get("TIMEINTERVAL")));
							}

							if(((DASFunctions) enL.getValue()).get("VALUEINTERVAL") != null){
								ig.setValueInterval(Integer.parseInt((String) ((DASFunctions) enL.getValue()).get("VALUEINTERVAL")));
							}

							inputGeneratorList.add(ig);
							ig.start();
						}

					}
					shape.setSup(sup);
					supervisorList.add(sup); 
				}
				this.add(shape);	
				staticElementList.add(shape);
			}
		}

		//TODO Calcul lines position for material liaison
		/*for(DASShape elem : staticElementList){

			if(elem.getLinkedTo() != -1 && elem.getLinkedFrom() != -1){

				doublelistproc: for(DASSupervisor sup1 : supervisorList){

					if(sup1.getCode() != -1 && sup1.getCode() == elem.getLinkedFrom()){
						
						//Calcul of the shape middle position sup 1
						int midxsup1 = sup1.getShape().x+(sup1.getShape().w/2);
						int midysup1 = sup1.getShape().y+(sup1.getShape().h/2);
						int midxsup2 = -1;
						int midysup2 = -1;

						for(DASSupervisor sup2 : supervisorList){

							if(sup2.getCode() != -1 && sup2.getCode() == elem.getLinkedTo()){

								//Calcul of the shape middle position sup 2
								midxsup2 = sup2.getShape().x+(sup2.getShape().w/2);
								midysup2 = sup2.getShape().y+(sup2.getShape().h/2);

								if(midxsup2 > midxsup1 && midysup2 > midysup1){
									midxsup2 = sup2.getShape().getXLink()+(sup2.getShape().getWLink()/2);
									midxsup1 = sup1.getShape().getXLink()+(sup1.getShape().getWLink()/2);
									midysup2 = sup2.getShape().getYLink()+(sup2.getShape().getHLink()/2);
									midysup1 = sup1.getShape().getYLink()+(sup1.getShape().getHLink()/2);
								}else if(midxsup2 < midxsup1 && midysup2 < midysup1){
									midxsup1 = sup2.getShape().getXLink()+(sup2.getShape().getWLink()/2);
									midxsup2 = sup1.getShape().getXLink()+(sup1.getShape().getWLink()/2);
									midysup2 = sup1.getShape().getYLink()+(sup1.getShape().getHLink()/2);
									midysup1 = sup2.getShape().getYLink()+(sup2.getShape().getHLink()/2);
								}else if(midxsup2 > midxsup1 && midysup2 < midysup1){
									midxsup2 = sup1.getShape().x+sup1.getShape().getWLink();
									midxsup1 = sup2.getShape().getXLink();
									midysup2 = sup1.getShape().y+sup1.getShape().getHLink();
									midysup1 = sup2.getShape().getYLink();
								}else if(midxsup2 < midxsup1 && midysup2 > midysup1){
									midxsup2 = sup1.getShape().x+sup1.getShape().getWLink();
									midxsup1 = sup2.getShape().getXLink();
									midysup1 = sup1.getShape().y+sup1.getShape().getHLink();
									midysup2 = sup2.getShape().getYLink();
								}

								elem.setX(midxsup1);
								elem.setY(midysup1);
								elem.setW(midxsup2);
								elem.setH(midysup2);
								elem.setShape("line");
								elem.setOrientation("diagonal");
								break doublelistproc;
							} 
						}
					}
				}
			}
		}

		for(DASSupervisor sup1 : supervisorList){
			if(sup1.getShape().getLinkedTo() != -1 ^ sup1.getShape().getLinkedFrom() != -1){
				int midxsup1 = sup1.getShape().x+(sup1.getShape().w/2);
				int midysup1 = sup1.getShape().y+(sup1.getShape().h/2);
				int midxsup2 = -1;
				int midysup2 = -1;
				for(DASSupervisor sup2 : supervisorList){

					if(sup2.getCode() != -1 && (sup2.getCode() == sup1.getShape().getLinkedTo() || sup2.getCode() == sup1.getShape().getLinkedFrom())){

						midxsup2 = sup2.getShape().x+(sup2.getShape().w/2);
						midysup2 = sup2.getShape().y+(sup2.getShape().h/2);

						if(midxsup2 > midxsup1 && midysup2 > midysup1){
							midxsup2 = sup2.getShape().getXLink()+(sup2.getShape().getWLink()/2);
							midxsup1 = sup1.getShape().getXLink()+(sup1.getShape().getWLink()/2);
							midysup2 = sup2.getShape().getYLink()+(sup2.getShape().getHLink()/2);
							midysup1 = sup1.getShape().getYLink()+(sup1.getShape().getHLink()/2);
						}else if(midxsup2 < midxsup1 && midysup2 < midysup1){
							midxsup1 = sup2.getShape().getXLink()+(sup2.getShape().getWLink()/2);
							midxsup2 = sup1.getShape().getXLink()+(sup1.getShape().getWLink()/2);
							midysup2 = sup1.getShape().getYLink()+(sup1.getShape().getHLink()/2);
							midysup1 = sup2.getShape().getYLink()+(sup2.getShape().getHLink()/2);
						}else if(midxsup2 > midxsup1 && midysup2 < midysup1){
							midxsup2 = sup1.getShape().x+sup1.getShape().getWLink();
							midxsup1 = sup2.getShape().getXLink();
							midysup2 = sup1.getShape().y+sup1.getShape().getHLink();
							midysup1 = sup2.getShape().getYLink();
						}else if(midxsup2 < midxsup1 && midysup2 > midysup1){
							midxsup2 = sup1.getShape().x+sup1.getShape().getWLink();
							midxsup1 = sup2.getShape().getXLink();
							midysup1 = sup1.getShape().y+sup1.getShape().getHLink();
							midysup2 = sup2.getShape().getYLink();
						}

						DASShape shape = new DASShape(midxsup1, midysup1, midxsup2, midysup2);
						shape.setShape("line");
						shape.setOrientation("diagonal");
						staticElementList.add(shape);
						break;
					} 
				}
			}
		}*/

		// RUN TOPIC LISTENER & PRODUCER
		for(Entry ent : topicListenerList.entrySet()){
			try
			{
				((DASSupervisionTopicListener) ent.getValue()).run();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		for(Entry ent : topicProducerList.entrySet()){
			try
			{
				((DASSupervisionTopicProducer) ent.getValue()).run();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.white);
		g2.fillRect(0,
				0,
				(int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
				(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()
				);

		g2.setPaint(Color.black);

		// Image
		g2.drawImage(this.image, imgPositionX, imgPositionY, null);


		// Static Element
		for(DASShape shape : staticElementList){
			shape.paintComponent(g2);
		}

		// Supervisors Link, in waiting to sort list with line in first
		for(DASSupervisor sup : supervisorList){
			if(sup.getShape().getShape().equals("ligne") || (sup.getShape().getLinkedTo() != -1 && sup.getShape().getLinkedFrom() != -1)){
				supervisorPainting(sup, g2);
			}
		}

		// Supervisors
		for(DASSupervisor sup : supervisorList){

			if(sup.getShape().getShape().equals("ligne") || (sup.getShape().getLinkedTo() != -1 && sup.getShape().getLinkedFrom() != -1)){
			}else{
				supervisorPainting(sup, g2);
			}
		}
	}

	public void supervisorPainting(DASSupervisor sup, Graphics2D g2){

		String value = sup.getValue();
		DASShape shape = sup.getShape();

		if(!value.equals("")){	

			Double val = Double.parseDouble(value);

			if(sup.getColorMethod().equals("num")){
				for(Entry<HashMap<String, Integer>, Color> entry : sup.getColorNum().entrySet()){
					boolean min = false, max = false;
					if(entry.getKey().containsKey("min")){
						min = true;
					}
					if(entry.getKey().containsKey("max")){
						max = true;
					}
					if(min || max){
						if(min && !max){
							if(entry.getKey().get("min") <= val){
								g2.setColor(entry.getValue());
							}
						}else if(!min && max){
							if(val <= entry.getKey().get("max")){
								g2.setColor(entry.getValue());
							}
						}else if(min && max){
							if(entry.getKey().get("min") <= val && val <= entry.getKey().get("max")){
								g2.setColor(entry.getValue());
							}
						}
					}
				}

			}else if(sup.getColorMethod().equals("liste")){

				if(sup.getColorList().containsKey(""+val)){
					g2.setColor((Color) sup.getColorList().get(""+val));
				}else{
					g2.setColor(Color.black);
				}

			}
		}else{
			g2.setPaint(Color.black);
		}
		shape.paintComponent(g2);
		if(sup.isDisplayValue()){
			// TEXT

			Font f;

			if((shape.getW()-shape.getX())<(shape.getH()-shape.getY())){

				f = new Font("Arial", Font.BOLD, 24);
				g2.setFont(f);

			}else{

				f = new Font("Arial", Font.BOLD, 24);
				g2.setFont(f);

			}
			Rectangle2D bounds = f.getStringBounds(value, g2.getFontRenderContext());

			if(sup.getShape().isFilled()){
				if(g2.getColor().equals(Color.black)){
					g2.setColor(Color.white);
				}else{
					g2.setColor(Color.black);
					//INVERSED COLOR
					//						g2.setColor(
					//								new Color(
					//										255-g2.getColor().getRed(),
					//										255-g2.getColor().getGreen(),
					//										255-g2.getColor().getBlue()
					//									)
					//								);	
				}

			}else{
				g2.setColor(Color.black);
			}

			g2.drawString(sup.getValue(),(shape.getX())+(shape.getWidth()/2)-((int)bounds.getCenterX()),
					(shape.getY())+(shape.getHeight()/2)-((int)bounds.getCenterY()));
		}
	}

	public void checkButtonsUpDown(){

		if(scaleHeightY == 0){

			panelDAS.displayCalendarButton(false);

		}else{

			if((currentScaleHeightY) < (scaleHeightY)){
				panelDAS.enableDownButton(true);
			}else{
				panelDAS.enableDownButton(false);
			}

			if((currentScaleHeightY) > 0){
				panelDAS.enableUpButton(true);
			}else{
				panelDAS.enableUpButton(false);
			}
		}
	}

	public void checkButtonsPrevNext(){

		if((partLargeurEnCours) < (currentScaleWidthX)){
			panelDAS.enableNextButton(true, "");
		}else{
			panelDAS.enableNextButton(false, "");
		}

		if((partLargeurEnCours) > 0){
			panelDAS.enablePrevButton(true, "");
		}else{
			panelDAS.enablePrevButton(false, "");
		}
	}

	public void moveUp(){

		imgPositionY = imgPositionY+currentGridHeightY;

		for(DASShape sup1 : staticElementList){
			sup1.moveUp(currentGridHeightY);
		}

		currentScaleHeightY = currentScaleHeightY - 1;
		checkButtonsUpDown();

		this.repaint();
		this.updateUI();
	}

	public void moveDown(){

		imgPositionY = imgPositionY-currentGridHeightY;

		for(DASShape sup1 : staticElementList){
			sup1.moveDown(currentGridHeightY);
		}

		currentScaleHeightY = currentScaleHeightY + 1;
		checkButtonsUpDown();

		this.repaint();
		this.updateUI();
	}

	public void movePrev(){

		imgPositionX = imgPositionX+currentGridWidthX;

		for(DASShape sup1 : staticElementList){
			sup1.movePrev(currentGridWidthX);
			sup1.repaint();
		}

		partLargeurEnCours = partLargeurEnCours - 1;
		checkButtonsPrevNext();

		this.repaint();
		this.updateUI();
	}

	public void moveNext(){

		imgPositionX = imgPositionX-currentGridWidthX;

		for(DASShape sup1 : staticElementList){
			sup1.moveNext(currentGridWidthX);
			sup1.repaint();
		}

		partLargeurEnCours = partLargeurEnCours + 1;
		checkButtonsPrevNext();

		this.repaint();
		this.updateUI();
	}

	public DASPanel getPanelDAS()
	{
		return panelDAS;
	}

	public void setPanelDAS(DASPanel dasPanel)
	{
		this.panelDAS = dasPanel;
	}

	public List<DASSupervisor> getSupervisorList()
	{
		return supervisorList;
	}

	public void setSupervisorList(List<DASSupervisor> supervisorList)
	{
		this.supervisorList = supervisorList;
	}

	public void setDialog(List<DASDialog> dialog)
	{
		List<DASDialog> tmpD = new LinkedList<DASDialog>();
		tmpD.addAll(dialog);
		int priority = 0;
		while(tmpD.size() > 0){
			priority++;
			for(DASDialog dia : dialog){
				if(dia.getPriority() == priority){
					if(dialogSorted.containsKey(dia.getName())){
						dialogSorted.get(dia.getName()).add(dia);
						tmpD.remove(dia);
					}else{
						List<DASDialog> tmpDialogList = new LinkedList<DASDialog>();
						tmpDialogList.add(dia);
						dialogSorted.put(dia.getName(), tmpDialogList);
						tmpD.remove(dia);
					}
				}
			}
		}
	}

	public DASDialog getDialog(String name){
		DASDialog tmp = null;
		for(DASDialog dialog : dialogSorted.get(name)){
			tmp = dialog;
			break;
		}

		return tmp;
	}


	public List<DASInputGenerator> getInputGeneratorList()
	{
		return inputGeneratorList;
	}


	public void setInputGeneratorList(List<DASInputGenerator> inputGeneratorList)
	{
		this.inputGeneratorList = inputGeneratorList;
	}


	public HashMap<String, DASSupervisionTopicListener> getTopicListenerList()
	{
		return topicListenerList;
	}


	public void setTopicListenerList(HashMap<String, DASSupervisionTopicListener> topicListenerList)
	{
		this.topicListenerList = topicListenerList;
	}


	public HashMap<String, DASSupervisionTopicProducer> getTopicProducerList()
	{
		return topicProducerList;
	}


	public void setTopicProducerList(HashMap<String, DASSupervisionTopicProducer> topicProducerList)
	{
		this.topicProducerList = topicProducerList;
	}

	private void logDebug(String log)
	{
		DASLog.logDebug(getClass().getSimpleName(), log);
	}

	public class DASInputGenerator extends Thread {
		private int min = -50;
		private int max = 250;
		private int timeInterval = 5000;
		private int valueInterval = 1;
		private boolean cont = true;
		private DASSupervisor supervisor;
		private boolean running_loop = true;

		public boolean isRunning_loop()
		{
			return running_loop;
		}


		public void setRunning_loop(boolean running_loop)
		{
			this.running_loop = running_loop;
		}

		public DASInputGenerator(DASSupervisor sup)
		{
			this.supervisor = sup;
		}

		public void run(){

			if(cont){

				for(int i = min; i <= max; i+=valueInterval){
					synchronized("supervisor")
					{
						supervisor.setValue(""+i);
					}
					try
					{
						Thread.sleep(timeInterval);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

			}else{

				while(running_loop){
					synchronized("supervisor")
					{
						supervisor.setValue(""+(min + Math.round((Math.random() * ((max - min) + 1)))));						
					}
					try
					{
						Thread.sleep(timeInterval);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

			}
		}

		public void interruptRef()
		{
			this.running_loop = false;
			this.interrupt();
		}

		public int getMin()
		{
			return min;
		}

		public void setMin(int min)
		{
			this.min = min;
		}

		public int getMax()
		{
			return max;
		}

		public void setMax(int max)
		{
			this.max = max;
		}

		public int getTimeInterval()
		{
			return timeInterval;
		}

		public void setTimeInterval(int interval)
		{
			this.timeInterval = interval;
		}

		public int getValueInterval()
		{
			return valueInterval;
		}

		public void setValueInterval(int valueInterval)
		{
			this.valueInterval = valueInterval;
		}

		public boolean isCont()
		{
			return cont;
		}

		public void setCont(int _continue)
		{
			this.cont = (_continue == 1) ? true : false;
		}

		public DASSupervisor getSupervisor()
		{
			return supervisor;
		}

		public void setSupervisor(DASSupervisor supervisor)
		{
			this.supervisor = supervisor;
		}

		@Override
		public String toString()
		{
			return "DASInputGenerator [min=" + min + ", max=" + max + ", timeInterval=" + timeInterval + ", valueInterval=" + valueInterval + ", cont=" + cont + ", supervisor=" + supervisor + "]";
		}

	}

	class DASShape extends JComponent {

		private int x;
		private int y;
		private int w;
		private int h;
		private int linkedTo = -1;
		private int linkedFrom = -1;
		private String shape = "none";
		private String orientation = "default";
		private boolean filled = false;
		private DASSupervisor sup = null;

		public DASShape(int x,int y,int w, int h){
			this.setLocation(x, y);
			this.setPreferredSize(new Dimension(w, h));
			this.setSize(new Dimension(w, h));

			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		public void moveDown(int tmp)
		{
			y = y - tmp;
			if(shape.contains("line") || shape.contains("triangle")){
				h = h - tmp;
			}			
			this.setLocation(x, y);
		}

		public void moveUp(int tmp)
		{
			y = y + tmp;
			if(shape.contains("line") || shape.contains("triangle")){
				h = h + tmp;
			}
			this.setLocation(x, y);
		}
		public void moveNext(int tmp)
		{
			x = x - tmp;
			if(shape.contains("line") || shape.contains("triangle")){
				w = w - tmp;
			}	
			this.setLocation(x, y);
		}

		public void movePrev(int tmp)
		{
			x = x + tmp;
			if(shape.contains("line") || shape.contains("triangle")){
				w = w + tmp;
			}
			this.setLocation(x, y);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

			if(shape.equals("none")){
				Color tmpC = g2d.getColor();
				g2d.setColor(Color.white);
				g2d.fillRect(x+1, y+1, w-1, h-1);
				g2d.setColor(Color.white);
				g2d.draw(new Rectangle.Double(x, y, w, h));
			}else{

				if(shape.contains("square") || shape.contains("rectangle")){

					if(filled)
					{

						g2d.fillRect(x, y, w, h);

					}
					else
					{
						Color tmpC = g2d.getColor();
						g2d.setColor(Color.white);
						g2d.fillRect(x+1, y+1, w-1, h-1);
						g2d.setColor(tmpC);
						g2d.draw(new Rectangle.Double(x, y, w, h));
					}
				}
				else if(shape.contains("circle"))
				{
					if(filled)
					{
						g2d.fillOval(x, y, w, h);
					}
					else
					{
						Color tmpC = g2d.getColor();
						g2d.setColor(Color.white);
						g2d.fillOval(x+1, y+1, w-1, h-1);
						g2d.setColor(tmpC);
						g2d.draw(new Ellipse2D.Float(x, y, w, h));
					}
				}else if(shape.contains("triangle")){

					Point p1 = null,p2 = null,p3 = null;

					if(orientation.equals("right")){

						p1 = new Point((int) ((int) x+(((Math.sqrt(3))/2) * h)), y+(h/2)); 
						p2 = new Point(x, y);
						p3 = new Point(x, y+h);

					}else if(orientation.equals("left")){

						p1 = new Point(x, y+(h/2)); 
						p2 = new Point((int) ((int) x+(((Math.sqrt(3))/2) * h)), y);
						p3 = new Point((int) ((int) x+(((Math.sqrt(3))/2) * h)), y+h);

					}else if(orientation.equals("top") || orientation.equals("default")){

						p1 = new Point(x+(w/2), y);
						p2 = new Point(x+w, (int) ((int) y+(((Math.sqrt(3))/2) * w)));
						p3 = new Point(x, (int) ((int) y+(((Math.sqrt(3))/2) * w)));

					}else if(orientation.equals("bottom")){

						p1 = new Point(x+(w/2), (int) ((int) y+(((Math.sqrt(3))/2) * w)));
						p2 = new Point(x+w, y);
						p3 = new Point(x, y);

					}

					int[] xs = { p1.x, p2.x, p3.x };
					int[] ys = { p1.y, p2.y, p3.y };
					Polygon triangle = new Polygon(xs, ys, xs.length);

					if(filled){
						g2d.fillPolygon(triangle);
					}else{

						Color tmpC = g2d.getColor();
						g2d.setColor(Color.white);
						g2d.fillPolygon(triangle);
						g2d.setColor(tmpC);
						g2d.draw(triangle);

					}

				}else if(shape.contains("line")){

					int x1 = 0,y1 = 0,w1 = 0, h1 = 0;
					if(orientation.contains("diagonal")){

						g2d.drawLine(x, y, w, h);

					}
					if(orientation.contains("horizontal")){

						x1 = x;
						y1 = y+h/2;
						w1 = x+w;
						h1 = y+h/2;

						g2d.drawLine(x1, y1, w1, h1);

					}
					if(orientation.contains("vertical")){

						x1 = x+w/2;
						y1 = y;
						w1 = x+w/2;
						h1 = y+h;

						g2d.drawLine(x1, y1, w1, h1);

					}
					if(orientation.contains("top")){

						x1 = x+w/2;
						y1 = y;
						w1 = x+w/2;
						h1 = y+h/2;

						g2d.drawLine(x1, y1, w1, h1);

					}
					if(orientation.contains("bottom")){

						x1 = x+w/2;
						y1 = y+(h/2);
						w1 = x+w/2;
						h1 = y+h;

						g2d.drawLine(x1, y1, w1, h1);

					}
					if(orientation.contains("left")){

						x1 = x;
						y1 = y+h/2;
						w1 = x+w/2;
						h1 = y+h/2;

						g2d.drawLine(x1, y1, w1, h1);

					}
					if(orientation.contains("right")){

						x1 = x+w/2;
						y1 = y+h/2;
						w1 = x+w;
						h1 = y+h/2;

						g2d.drawLine(x1, y1, w1, h1);

					}

				}else if(shape.contains("arrow")){

					if(orientation.contains("horizontal")){

						g2d.drawLine(x, y+h/2, x+w, y+h/2);

						if(orientation.contains("right")){

							g2d.drawLine(x, y, x+(w/2), y+(h/2));
							g2d.drawLine(x, y+h, x+(w/2), y+(h/2));

						}else if(orientation.contains("left")){

							g2d.drawLine(x+w, y, x+(w/2), y+(h/2));
							g2d.drawLine(x+w, y+h, x+(w/2), y+(h/2));

						}

					}else if(orientation.contains("vertical")){

						g2d.drawLine(x+w/2, y, x+w/2, y+h);

						if(orientation.contains("top")){

							g2d.drawLine(x, y+h, x+(w/2), y+(h/2));
							g2d.drawLine(x+w, y+h, x+(w/2), y+(h/2));

						}else if(orientation.contains("bottom")){

							g2d.drawLine(x, y, x+(w/2), y+(h/2));
							g2d.drawLine(x+w, y, x+(w/2), y+(h/2));

						}
					}
				}
			}
		}

		public int getX()
		{
			return x;
		}

		public void setX(int x)
		{
			this.x = x;
		}

		public int getY()
		{
			return y;
		}

		public void setY(int y)
		{
			this.y = y;
		}

		public int getW()
		{
			return w;
		}

		public void setW(int w)
		{
			this.w = w;
		}

		public int getH()
		{
			return h;
		}

		public void setH(int h)
		{
			this.h = h;
		}

		public int getWLink()
		{
			if(shape.contains("circle") || shape.contains("triangle")){
				return (w/2);
			}
			return w;
		}

		public int getHLink()
		{
			if(shape.contains("circle") || shape.contains("triangle")){
				return (h/2);
			}
			return h;
		}

		public int getXLink()
		{
			if(shape.contains("circle") || shape.contains("triangle")){
				return (x+(w/2));
			}
			return x;
		}

		public int getYLink()
		{
			if(shape.contains("circle") || shape.contains("triangle")){
				return (y+(h/2));
			}
			return y;
		}

		public String getShape()
		{
			return shape;
		}

		public void setShape(String form)
		{
			this.shape = form;
		}

		public String getOrientation()
		{
			return orientation;
		}

		public void setOrientation(String orientation)
		{
			this.orientation = orientation;
		}

		public boolean isFilled()
		{
			return filled;
		}

		public void setFilled(boolean filled)
		{
			this.filled = filled;
		}

		public void setFilled(String filled)
		{
			if(filled.equals("1")){
				this.filled = true;
			}
		}

		public int getLinkedTo()
		{
			return linkedTo;
		}


		public void setLinkedTo(int linkedTo)
		{
			this.linkedTo = linkedTo;
		}


		public int getLinkedFrom()
		{
			return linkedFrom;
		}


		public void setLinkedFrom(int linkedFrom)
		{
			this.linkedFrom = linkedFrom;
		}

		public DASSupervisor getSup()
		{
			return sup;
		}

		public void setSup(DASSupervisor sup)
		{
			this.sup = sup;
		}

	}

	class DASSupervisorListener implements MouseListener {

		private final JComponent component;


		public DASSupervisorListener(JComponent comp){
			this.component = comp;
		}

		public void mouseClicked(MouseEvent arg0)
		{
			DASShape tmpShape = ((DASShape) component);
			HashMap<Date,String> hl = tmpShape.getSup().getHistoriqueValue();

			if(!hl.isEmpty()){
				if(tmpShape.getSup().graphSup == null){
					tmpShape.getSup().graphSup = new DASSupervisionGraph(tmpShape.getSup());
				}else{
					tmpShape.getSup().graphSup.setVisible(true);
					tmpShape.getSup().graphSup.getRtt().start();
				}
			}

		}

		public void mouseEntered(MouseEvent arg0)
		{

		}

		public void mouseExited(MouseEvent arg0)
		{

		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

	}

	public class DASSupervisor {

		@Override
		public String toString()
		{
			return "DASSupervisor [code=" + code + ", colorList=" + colorList + ", colorMethod=" + colorMethod + ", colorNum=" + colorNum + ", displayValue=" + displayValue + ", graphLine=" + graphLine + ", graphSup=" + graphSup + ", graphTime=" + graphTime + ", graphType=" + graphType + ", historiqueValue=" + historicValue + ", nbsavedata=" + nbsavedata + ", refcode=" + refcode + ", saveData=" + saveData + ", shape=" + shape + ", sourceMaterial=" + sourceMaterial + ", sourceVariable=" + sourceVariable + ", supPanel=" + supPanel + ", topicListener=" + topicListener + ", topicProducer=" + topicProducer + ", value=" + value + "]";
		}

		private int code = -1;
		private int refcode = -1;
		private int linkedTo = -1;
		private int linkedFrom = -1;
		private DASShape shape;
		private String value ="";
		private String sourceMaterial = null;
		private String sourceVariable = null;
		private HashMap<String, Color> colorList = new HashMap<String, Color>();
		HashMap<HashMap<String, Integer>, Color> colorNum = new HashMap<HashMap<String,Integer>, Color>();
		private String colorMethod;
		private boolean displayValue = false;
		private boolean saveData = false;
		private HashMap<Date, String> historicValue = new HashMap<Date, String>();
		private DASSupervisionTopicListener topicListener = null;
		private DASSupervisionTopicProducer topicProducer = null;
		private DASSupervisionPanel supPanel;
		private String graphType = "discrete";
		private String valueType = "Temp"; //Axis value type ex : temp, pressure
		private String graphTime = "1h";
		private boolean graphLine = false;
		private int nbsavedata = 200;
		private DASSupervisionGraph graphSup = null;

		public DASSupervisor(DASShape shape, String value, String link, DASSupervisionPanel supPanel)
		{
			this.shape = shape;
			this.value = value;
			this.sourceMaterial = link;
			this.supPanel = supPanel;
		}

		public void setValue(String value)
		{
			if(this.topicProducer != null){
				try{topicProducer.publish(value);}
				catch (Exception e){e.printStackTrace();}
			}

			if(this.saveData){

				if(this.historicValue.size() >= nbsavedata){
					while(this.historicValue.size() >= nbsavedata){
						TreeMap<Date,String> m1 = new TreeMap<Date,String>(historicValue);
						this.historicValue.remove(m1.firstEntry().getKey());
						graphSup.removeGraphFirstDate();
					}
				}

				this.historicValue.put(new Date(),value);

				if(graphSup != null){
					graphSup.upGraphValue(value);
				}

			}

			//VARIABLE OR SUP
			for(DASSupervisor sup :  this.getPanelSup().getSupervisorList()){
				if(sup != this && sup.getSourceVariable() != null){
					for(Entry<String, List<String>> entry : this.getPanelSup().getPanelDAS().getController().getDataEnCours().entrySet()){
						if(sup.getSourceVariable().equals(entry.getKey())){
							if(!entry.getValue().isEmpty()){
								sup.setValue(entry.getValue().get(0));
							}
						}
					}
				}
				if(sup != this && sup.getRefcode() != -1 && this.getCode() != -1 && sup.getRefcode() == this.getCode()){
					sup.setValue(value);
				}

			}

			this.value = value;

			this.supPanel.repaint();
			this.supPanel.getPanelDAS().getPanelCenter().updateUI();
			this.supPanel.getPanelDAS().getPanelCenter().validate();
		}


		public int getCode()
		{
			return code;
		}


		public void setCode(int code)
		{
			this.code = code;
		}


		public int getRefcode()
		{
			return refcode;
		}


		public void setRefcode(int refcode)
		{
			this.refcode = refcode;
		}


		public int getLinkedTo()
		{
			return linkedTo;
		}


		public void setLinkedTo(int linkedTo)
		{
			this.linkedTo = linkedTo;
		}


		public int getLinkedFrom()
		{
			return linkedFrom;
		}


		public void setLinkedFrom(int linkedFrom)
		{
			this.linkedFrom = linkedFrom;
		}

		public String getSourceMaterial()
		{
			return sourceMaterial;
		}

		public void setSourceMaterial(String sourceMaterial)
		{
			this.sourceMaterial = sourceMaterial;
		}

		public String getSourceVariable()
		{
			return sourceVariable;
		}

		public void setSourceVariable(String sourceVariable)
		{
			this.sourceVariable = sourceVariable;
		}

		public void setNbSavedData(String nbsaveddata){
			nbsavedata = Integer.parseInt(nbsaveddata);
		}

		public DASSupervisionPanel getPanelSup()
		{
			return supPanel;
		}

		public void setSupPanel(DASSupervisionPanel supPanel)
		{
			this.supPanel = supPanel;
		}

		public HashMap<HashMap<String, Integer>, Color> getColorNum()
		{
			return colorNum;
		}

		public void setColorNum(HashMap<HashMap<String, Integer>, Color> colorNum)
		{
			this.colorNum = colorNum;
		}

		public String getGraphType()
		{
			return graphType;
		}

		public void setGraphType(String graphType)
		{
			this.graphType = graphType;
		}
		
		public String getValueType()
		{
			return valueType;
		}

		public void setValueType(String valueType)
		{
			this.valueType = valueType;
		}

		public boolean isGraphLine()
		{
			return graphLine;
		}

		public void setGraphLine(boolean graphLine)
		{
			this.graphLine = graphLine;
		}

		public String getGraphTime()
		{
			return graphTime;
		}

		public void setGraphTime(String graphTime)
		{
			this.graphTime = graphTime;
		}

		public DASSupervisionTopicListener getTopicListener()
		{
			return topicListener;
		}

		public void setTopicListener(DASSupervisionTopicListener topicListener)
		{
			this.topicListener = topicListener;
		}

		public DASSupervisionTopicProducer getTopicProducer()
		{
			return topicProducer;
		}

		public void setTopicProducer(DASSupervisionTopicProducer topicProducer)
		{
			this.topicProducer = topicProducer;
		}
		public DASShape getShape()
		{
			return shape;
		}

		public void setShape(DASShape shape)
		{
			this.shape = shape;
		}

		public String getValue()
		{
			return value;
		}

		public void updateFieldFromTopic(String msg){

		}

		public String getColorMethod()
		{
			return colorMethod;
		}

		public void setColorMethod(String col)
		{
			this.colorMethod = col;
			if(col.equals("num")){
				colorList = null;
				graphType = "discrete";
			}else if(col.equals("liste")){
				colorNum = null;
				graphType = "liste";
			}
		}

		public HashMap<String, Color> getColorList()
		{
			return colorList;
		}

		public void setColorList(HashMap<String, Color> colorList)
		{
			this.colorList = colorList;
		}

		public boolean isDisplayValue()
		{
			return displayValue;
		}

		public void setDisplayValue(boolean displayValue)
		{
			this.displayValue = displayValue;
		}

		public boolean isSaveData()
		{
			return saveData;
		}

		public void setSaveData(boolean saveData)
		{
			this.saveData = saveData;
		}

		public HashMap<Date, String> getHistoriqueValue()
		{
			return historicValue;
		}

		public void setHistoriqueValue(HashMap<Date, String> historicValue)
		{
			this.historicValue = historicValue;
		}

	}

}