package org.opendas.eanparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opendas.DASLog;
import org.opendas.modele.DASEan128;

//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
/**
 */
public class Parser
{

	private List<DASEan128>		identifiers;
	private static final String	scannerPrefix	= "C1";
	private static final String	delimiter		= "\\x1d";

	public Parser(List<DASEan128> lean)
	{
		this.identifiers = lean;
	}

	// public Parser() {
	// super();
	//
	// List<DASEan128> listIdE128 = new LinkedList<DASEan128>();
	// DASEan128 identifier1 = new DASEan128(),identifier2 = new
	// DASEan128(),identifier3 = new DASEan128();
	// DASEan128 identifier4 = new DASEan128(),identifier5 = new
	// DASEan128(),identifier6 = new DASEan128();
	// DASEan128 identifier7 = new DASEan128(),identifier8 = new DASEan128();
	//
	// identifier1.setIdentifier("251");
	// identifier1.setMaxLength(10);
	// listIdE128.add(identifier1);
	//
	// identifier2.setIdentifier("92");
	// identifier2.setMaxLength(8);
	// listIdE128.add(identifier2);
	//
	// identifier3.setIdentifier("94");
	// identifier3.setMaxLength(2);
	// listIdE128.add(identifier3);
	//
	// identifier4.setIdentifier("422");
	// identifier4.setMaxLength(3);
	// listIdE128.add(identifier4);
	//
	// identifier5.setIdentifier("423");
	// identifier5.setMaxLength(3);
	// listIdE128.add(identifier5);
	//
	// identifier6.setIdentifier("7030");
	// identifier6.setMaxLength(6);
	// listIdE128.add(identifier6);
	//
	// identifier7.setIdentifier("7031");
	// identifier7.setMaxLength(6);
	// listIdE128.add(identifier7);
	//
	// identifier8.setIdentifier("00");
	// identifier8.setMaxLength(18);
	// listIdE128.add(identifier8);
	//
	// this.identifiers = listIdE128;
	// }
	/**
	 * Make a test of the parsing with a fake code
	 * 
	 * @param args
	 *            enter an other code to parse
	 */
	public static void main(String... args)
	{
	}

	/**
	 * Parse the code from a scanner and return all the DASEan128 / values in a
	 * list
	 * 
	 * @param code
	 *            EAN 128 from a scanner
	 * @return a list of DASEan128 with the values
	 * 
	 */
	public Map<String, ArrayList<List<String>>> parseEAN128(String code)
	{
		String codeTmp = code;
		logDebug("Code :" + code);
		// result list
		Map<String, ArrayList<List<String>>> list = new HashMap<String, ArrayList<List<String>>>();
		ArrayList<List<String>> linkCode = new ArrayList<List<String>>();
		List<String> brutCode = new LinkedList<String>();
		// clean the code
		String codeClean = this.cleanCode(codeTmp);
		codeTmp = codeClean;
		// for each identifier in the code
		DASEan128 firstIdentifier = this.getFirstIdentifier(codeTmp);
		while (firstIdentifier != null)
		{
			brutCode = new LinkedList<String>();
			// delete the first identifier (we just got it)
			codeTmp = this.deleteFirstIdentifier(firstIdentifier, codeTmp);
			brutCode.add(firstIdentifier.getId());
			// get the value of the identifier
			String value = this.getValue(firstIdentifier, codeTmp);
			brutCode.add(value);
			linkCode.add(brutCode);
			// delete the first value (we just got it)
			codeTmp = this.deleteValue(value, codeTmp);
			firstIdentifier = this.getFirstIdentifier(codeTmp);
		}
		list.put(codeClean, linkCode);
		return list;
	}

	/*
	 * public Map<String, ArrayList<List<String>>> parseEAN128WithoutCode(String
	 * code) { String codeTmp = code; System.out.println("Code :"+code);
	 * 
	 * // result list Map<String, ArrayList<List<String>>> list = new
	 * HashMap<String, ArrayList<List<String>>>(); ArrayList<List<String>>
	 * linkCode = new ArrayList<List<String>>(); List<String> brutCode = new
	 * LinkedList<String>();
	 * 
	 * // clean the code String codeClean = this.cleanCode(codeTmp); codeTmp =
	 * null;
	 * 
	 * boolean codeRest = true; while (codeRest) {
	 * 
	 * brutCode = new LinkedList<String>();
	 * 
	 * for(int i = 0;i<codeClean.length();i++){
	 * 
	 * int asciic = codeClean.substring(i, i+1).hashCode();
	 * System.out.println(asciic); if(asciic == 29){
	 * System.out.println(codeTmp); codeRest = false; } codeTmp = codeTmp +
	 * codeClean.substring(i, i+1);
	 * 
	 * } }
	 * 
	 * list.put(codeClean,linkCode); return list; }
	 */
	public Map<String, ArrayList<List<String>>> parseEAN13(String code)
	{
		Map<String, ArrayList<List<String>>> list = new HashMap<String, ArrayList<List<String>>>();
		ArrayList<List<String>> linkCode = new ArrayList<List<String>>();
		List<String> brutCode = new LinkedList<String>();
		String firstNumberCode = code.substring(0, 1);
		brutCode.add("firstNumberCode");
		brutCode.add(firstNumberCode);
		linkCode.add(brutCode);
		brutCode = new LinkedList<String>();
		String normalGardeCode = code.substring(1, 7);
		brutCode.add("normalGardeCode");
		brutCode.add(normalGardeCode);
		linkCode.add(brutCode);
		brutCode = new LinkedList<String>();
		String centralGardeCode = code.substring(7, 13);
		brutCode.add("centralGardeCode");
		brutCode.add(centralGardeCode);
		linkCode.add(brutCode);
		list.put(code, linkCode);
		return list;
	}

	/**
	 * Clean the code (remove the prefix for scanner "]C1")
	 * 
	 * @param code
	 * @return the cleaned code
	 */
	private String cleanCode(String code)
	{
		code = code.replace(Parser.scannerPrefix, "");
		return code;
	}

	/**
	 * Retourne un DASEan128 correspondant à l'identifier trouvé en première
	 * position.
	 * 
	 * @return the DASEan128 or null if not found
	 */
	private DASEan128 getFirstIdentifier(String code)
	{
		DASEan128 identifier = null;
		for (DASEan128 temp : this.identifiers)
		{
			Pattern p = Pattern.compile(temp.getId());
			Matcher m = p.matcher(code);
			// Pattern p = Pattern.compile("\\d*");
			// Matcher m = p.matcher(code);
			//
			// while (m.find()) {
			// String texte = m.group();
			// int debut = m.start();
			// int fin = m.end();
			// System.out.println("Trouvé "+texte+" à la position "+debut);
			// }
			// It's this identifier
			if (m.find() && m.start() == 0)
			{
				if (m.groupCount() > 0)
				{
					// identifier = new DASEan128(temp, m.group(1));
				} else
				{
					identifier = temp;
				}
			}
		}
		logDebug(" ===========> IDENTIFIER ---> " + identifier);
		return identifier;
	}

	/**
	 * 
	 * @param identifier
	 * @param code
	 * @return the code without the first identifier
	 */
	private String deleteFirstIdentifier(DASEan128 identifier, String code)
	{
		return code.replaceFirst(identifier.getId(), "");
	}

	/**
	 * Get the value from the code
	 * 
	 * @param identifier
	 * @param code
	 *            the code containing the value
	 * @return the value
	 */
	private String getValue(DASEan128 identifier, String code)
	{
		// calculate the length of the value
		Integer length = identifier.getMaxLength();
		if (length >= code.length())
		{
			length = code.length();
		}
		// take the value
		String result = code.substring(0, length);
		// if a delimitator is found, we cut the string before
		Pattern p = Pattern.compile(Parser.delimiter);
		Matcher m = p.matcher(code);
		if (m.find())
		{
			result = code.substring(0, m.start());
		}
		return result;
	}

	/**
	 * Delete the value from the code.
	 * 
	 * @param value
	 *            the value to delete
	 * @param code
	 *            the code containing the value
	 * @return the new String without the value
	 */
	private String deleteValue(String value, String code)
	{
		// delete the value
		code = code.replaceFirst(value, "");
		// delete the delimiter character (is exists)
		Pattern p = Pattern.compile(Parser.delimiter);
		Matcher m = p.matcher(code);
		if (m.find() && m.start() == 0)
		{
			code = code.substring(1);
		}
		return code;
	}

	public String getCode(DASEan128 identifier, String code)
	{
		// clean the code
		code = this.cleanCode(code);
		// for each identifer in the code
		DASEan128 firstIdentifier = this.getFirstIdentifier(code);
		while (firstIdentifier != null && !firstIdentifier.getId().equals(identifier.getId()))
		{
			// delete the first identifier (we just got it)
			code = this.deleteFirstIdentifier(firstIdentifier, code);
			// get the value of the identifier
			String value = this.getValue(firstIdentifier, code);
			// delete the first value (we just got it)
			code = this.deleteValue(value, code);
			firstIdentifier = this.getFirstIdentifier(code);
		}
		if (firstIdentifier == null)
			return null;
		// calculate the length of the value
		int length = identifier.getMaxLength();
		if (length >= code.length())
		{
			length = code.length();
		}
		// take the value
		int begin = identifier.getId().length();
		String result = code.substring(begin, Math.min(begin + length, code.length()));
		// if a delimitator is found, we cut the string before
		Pattern p = Pattern.compile(Parser.delimiter);
		Matcher m = p.matcher(result);
		if (m.find())
		{
			result = result.substring(0, m.start());
		}
		return result;
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
