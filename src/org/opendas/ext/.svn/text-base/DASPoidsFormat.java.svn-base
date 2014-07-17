package org.opendas.ext;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * Sert à afficher un poids en grammes ou kilogrammes, le poids passé en
 * paramètre étant en grammes.
 * 
 */
public class DASPoidsFormat extends Format
{

	private static final long		serialVersionUID	= 1L;
	// declaration de la forme en gramme
	protected static DecimalFormat	formatG				= new DecimalFormat("##0.00 g");
	// declaration de la forme en Kilo
	protected static DecimalFormat	formatKg			= new DecimalFormat("##0.000 Kg");

	// permet de former un objet sous la forme formatG
	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
	{
		if (!(obj instanceof Number))
			throw new IllegalArgumentException();
		double poids = ((Number) obj).doubleValue();
		if (poids >= 1000)
			formatKg.format(poids / 1000, toAppendTo, pos);
		else
			formatG.format(poids, toAppendTo, pos);
		return toAppendTo;
	}

	// converti une chaine en un nombre
	@Override
	public Number parseObject(String source, ParsePosition pos)
	{
		Number n = formatG.parse(source, pos);
		if (n == null)
		{
			n = formatKg.parse(source, pos);
			if (n != null)
				return n.doubleValue() * 1000;
		}
		return n;
	}

	public Number parse(String source, ParsePosition pos)
	{
		return parseObject(source, pos);
	}

	public Number parse(String source)
	{
		return parseObject(source, new ParsePosition(0));
	}

	/**
	 * 
	 * @param poids
	 *            en gramme
	 * @return
	 */
	public String format(double poids)
	{
		return format((Object) poids);
	}
}
