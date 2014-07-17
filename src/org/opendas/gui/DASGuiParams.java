package org.opendas.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.Serializable;
import java.util.Map;

import javax.swing.SwingConstants;

import org.opendas.DASLog;
/**
 * Class allowing interpretation parameters for graphical interface
 * 
 * @author mlaroche
 */
public class DASGuiParams implements Serializable
{

	private static final long	serialVersionUID	= 1L;
	private static DASGuiParams	params				= new DASGuiParams();

	public static DASGuiParams getInstance()
	{
		return params;
	}

	private DASGuiParams()
	{
	}

	/**
	 * Converts the string corresponding full value of hue
	 * 
	 * 
	 * @param color
	 *            the string corresponding full value of hue
	 * @return integer correspond at the value of hue
	 * @throws NumberFormatException
	 *            exception exception generated when the parameter not corresponds at a value of hue
	 */
	private int getIntColor(String color) throws NumberFormatException
	{
		int resColor;
		try
		{
			resColor = Integer.parseInt(color);
		} catch (NumberFormatException nfe)
		{
			throw new NumberFormatException("A component of color must be defined with a integer");
		}
		if (resColor >= 0 && resColor < 256)
		{
			return resColor;
		} else
		{
			logDebug("Value of color out of interval [0-255]");
			throw new NumberFormatException("Value of color out of interval [0-255]");
		}
	}

	/**
	 * Converts the corresponding string to a size of police at integer
	 * 
	 * 
	 * @param size
	 *            the police size (in the form of string)
	 * @return font size (in the form of integer)
	 * @throws NumberFormatException
	 *             exception generated when the parameter not corresponds at a font size
	 */
	private int getFontSize(String size) throws NumberFormatException
	{
		int resSize = Integer.parseInt(size);
		if (resSize > 0)
		{
			return resSize;
		} else
		{
			logDebug("The font size must be stricly positive");
			throw new NumberFormatException("The font size must be stricly positive");
		}
	}

	/**
	 * Return the style called in parameter
	 * 
	 * @return the style called in parameter
	 */
	private int getFontStyle(String style) throws DASFontException
	{
		if (style == null)
		{
			throw new DASFontException();
		}
		if (style.equals("BOLD"))
		{
			return Font.BOLD;
		} else if (style.equals("ITALIC"))
		{
			return Font.ITALIC;
		} else if (style.equals("PLAIN"))
		{
			return Font.PLAIN;
		} else
		{
			return Font.TYPE1_FONT;
		}
	}

	/**
	 * 
	 * Return the name of the string given in parameter if this one is defined on the system
	 *
	 * 
	 * @param name
	 *            Font name what we want to use
	 * @return Return the name of the string given in parameter if this one is defined on the system
	 * @throws DASFontException
	 *             exception generated when the parameter not corresponds at font name existant on the system
	 */
	private String getFontName(String name) throws DASFontException
	{
		return name;
	}

	/**
	 * Specify if the font is installed on the system or not
	 * 
	 * @param name
	 *            the font name searched
	 * @return true if the font is installed , false else
	 * 
	 */
	private boolean policeSysteme(String name)
	{
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (String f : fonts)
		{
			if (name.equals(f))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the corresponding color at the request
	 * 
	 * @param params
	 *            map containing prefixed value by prefix
	 * @param prefix
	 *            the prefix who corresponds at searched values
	 * @return the color corresponding at prefix in parameters
	 * @throws NumberFormatException
	 *             exception generated if the color found in parameters is missing or incorrect
	 */
	public Color getColor(Map<String, String> params, String prefix) throws NumberFormatException
	{
		int red = getIntColor(params.get(prefix + "_RED"));
		int green = getIntColor(params.get(prefix + "_GREEN"));
		int blue = getIntColor(params.get(prefix + "_BLUE"));
		return new Color(red, green, blue);
	}

	/**
	 * Return corresponding number at the request
	 * 
	 * @param params
	 *            map contenant les valeurs préfixées par prefix
	 * @param prefix
	 *            le préfixe correspondant aux valeurs recherchées
	 * @return le nombre correspondant au prefix dans params
	 */
	public int getNBB(Map<String, String> params, String prefix)
	{
		int Int = Integer.parseInt(params.get(prefix));
		return Int;
	}

	/**
	 * Retourne la police correspondant a la demande
	 * 
	 * @param params
	 *            map contenant les valeurs préfixées par prefix
	 * @param prefix
	 *            le préfixe correspondant aux valeurs recherchées
	 * @return la police correspondant au prefix dans params
	 * @throws NumberFormatException
	 *             exception generee si la taille de police est absente ou
	 *             incorrecte
	 * @throws DASFontException
	 *             exception generee si la police trouvee dans params est
	 *             absente ou incorrecte
	 */
	public Font getFont(Map<String, String> params, String prefix) throws NumberFormatException, DASFontException
	{
		logDebug("prefix :"+ prefix);
		
		String name = getFontName(params.get(prefix + "_FONT_NAME"));
		int style = getFontStyle(params.get(prefix + "_FONT_STYLE"));
		int size = getFontSize(params.get(prefix + "_FONT_SIZE"));
		
		if((style <=0 || size <= 0)){
			throw new DASFontException();
		}
		if (policeSysteme(name))
		{
			return new Font(name, style, size);
		} else
		{
			try
			{
				Font f = Font.createFont(Font.TRUETYPE_FONT, new File("ressources/" + name + ".ttf"));
				logDebug(name);
//				Font f = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream(name + ".ttf"));
				
				f = f.deriveFont((float) size);
				f = f.deriveFont(style);
//				System.out.println("===========================");
//				System.out.println(getClass().getClassLoader().getResource("dd"+name + ".ttf").toString());
//		    	 
//	    	    try{
//	    	    	InputStream is = getClass().getClassLoader().getResourceAsStream(name + ".ttf");
//	    	    	BufferedReader br = new BufferedReader(new InputStreamReader(is));
//	    	 
//	    	          String line;
//	    	          while ((line = br.readLine()) != null) {
//	    	             System.out.println(line);
//	    	       	  } 
//	    	          br.close();
//    	 
//    	    	}catch(IOException e){
//    	    		e.printStackTrace();
//    	    	}
//	    	 
//				f2 = new File(getClass().getClassLoader().getResource(name + ".ttf").toString());
//				if (f2.exists())
//					return name;
//			}
//			catch (URISyntaxException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	
				return f;
			} catch (Exception e)
			{
				e.printStackTrace();
				throw new DASFontException();
			}
		}
	}

	/**
	 * Convert the string corresponding at gradient to byte
	 * 
	 * @param gradient
	 *            numero of gradient(in the form of string)
	 * @return numero of gradient (in the form of byte)
	 * @throws NumberFormatException
	 *             exception generated when the parameter not corresponds at numero of gradient
	 */
	public byte getGradient(String gradient) throws NumberFormatException
	{
		byte resGradient;
		try
		{
			resGradient = Byte.parseByte(gradient);
		} catch (NumberFormatException nfe)
		{
			throw new NumberFormatException("The choice of gradient must be defined in the form of integer");
		}
		if (resGradient >= 0 && resGradient < 2)
		{
			return resGradient;
		} else
		{
			logDebug("Gradient value defined out of interval [0-1]");
			throw new NumberFormatException("Gradient value defined out of interval [0-1]");
		}
	}

	/**
	 * Return object type(Button or Display) if the one is defined
	 * 
	 * @param object type of object
	 * @return object type if it's valid
	 * @throws DASTypeException
	 *             exception generated when the parameter not corresponds at a
	 *             accepted type of object
	 */
	public String getTypeObject(String type) throws DASTypeException
	{
		if ((type != null) && (typeIsButton(type) || typeIsField(type)))
		{
			return type;
		} else
		{
			throw new DASTypeException("Object type'" + type + "' unknown");
		}
	}

	/**
	 * Specify if the given type corresponds at a button or not
	 * 
	 * @param type
	 *           object type to test
	 * @return true if the type correspond at a button , false else
	 */
	public boolean typeIsButton(String type)
	{
		return type.toUpperCase().equals("BUTTON");
	}

	/**
	 * Specify if the given type correspond at the display or not
	 * 
	 * @param type
	 *            the object type to test
	 * @return true if the type correspond at the field , false else
	 */
	public boolean typeIsField(String type)
	{
		return type.toUpperCase().equals("FIELD");
	}

	/**
	 * Return the corresponding constant at the wanted alignment of horizontal text
	 * 
	 * @param align
	 * 			  the english name of the wanted alignment of horizontal text
	 * @return Integer which corresponds at the SwingConstant of the indicated alignment 
	 * @throws DASTextAlignException
	 * 			   exception generated when the parameter not corresponds at horizontal alignment of text
	 */
	public int getTextAlign(String align) throws DASTextAlignException
	{
		try
		{
			align = align.toUpperCase();
			if (align.equals("RIGHT"))
			{
				return SwingConstants.RIGHT;
			} else if (align.equals("LEFT"))
			{
				return SwingConstants.LEFT;
			} else if (align.equals("CENTER"))
			{
				return SwingConstants.CENTER;
			} else
			{
				throw new DASTextAlignException();
			}
		} catch (NullPointerException e)
		{
			throw new DASTextAlignException();
		}
	}

	/**
	 * Return the textual form of the given alignment as parameter
	 * 
	 * @param align
	 *            Integer corresponding at a horizontal alignment of text
	 * @return textual value of the alignement precised in the parameters
	 */
	public String IntAlignToTextAlign(int align)
	{
		if (align == SwingConstants.RIGHT)
		{
			return "right";
		} else if (align == SwingConstants.LEFT)
		{
			return "left";
		} else if (align == SwingConstants.CENTER)
		{
			return "center";
		} else
		{
			try
			{
				throw new Exception("incorrect alignment numero");
			} catch (Exception e)
			{
				e.printStackTrace();
				return "center";
			}
		}
	}

	/**
	 * Exception when the fonts recovery
	 */
	public class DASFontException extends Exception
	{

		private static final long	serialVersionUID	= 1L;
	}

	/**
	 * Exception when the recovery of the text alignment
	 */
	public class DASTextAlignException extends Exception
	{

		private static final long	serialVersionUID	= 1L;
	}

	/**
	 * Exception when the recovery of object type (Button, Field)
	 */
	public class DASTypeException extends Exception
	{

		private static final long	serialVersionUID	= 1L;

		DASTypeException(String message)
		{
			super(message);
		}
	}

	private void logDebug(String log)
	{
		DASLog.logDebug(getClass().getSimpleName(), log);
	}

	public Dimension getDIM(Map<String, String> params, String prefix)
	{
		int x = Integer.parseInt(params.get(prefix + "_X"));
		int y = Integer.parseInt(params.get(prefix + "_Y"));
		return new Dimension(x, y);
	}
	
}
