package org.opendas.translate;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n
{
	public static ResourceBundle catalog = getBundle();
	
	public static String _(String s){
		if(catalog == null){
			return s;
		}
		return catalog.getString(s);
	}
	
	public static ResourceBundle getBundle(){
		ResourceBundle ca;
		try{
			ca = ResourceBundle.getBundle("Messages");
		}catch(MissingResourceException mr){
			System.err.println("Warning : i18n file not found for current Locale");
			ca = null;
		}
		return ca;
	}
	
}
