package org.opendas.calendar;

import java.util.*;

public class DateDayOff
{
	private Calendar	datePaques	= new GregorianCalendar();
	private int			annee;
	private int			a, b, c, d, e, f, g, h, i, k, l, m, n, p;
	private Calendar	jourFerie	= new GregorianCalendar();

	public DateDayOff(Calendar dPaques)
	{
		datePaques = dPaques;
		annee = datePaques.get(Calendar.YEAR);
		a = annee % 19;
		b = annee / 100;
		c = annee % 100;
		d = b / 4;
		e = b % 4;
		f = (b + 8) / 25;
		g = (b - f + 1) / 3;
		h = (19 * a + b - d - g + 15) % 30;
		i = c / 4;
		k = c % 4;
		l = (32 + 2 * e + 2 * i - h - k) % 7;
		m = (a + 11 * h + 22 * l) / 451;
		n = (h + l - 7 * m + 114) / 31;
		p = (h + l - 7 * m + 114) % 31;
	}

	public Calendar aFerie(int rang)
	{
		// initialization with the easter sunday
		jourFerie.set(annee, n - 1, p + 1);
		if (rang == 1)
		{
			// day of new year
			jourFerie.set(annee, 0, 1);
		}
		if (rang == 2)
		{
			// easter monday
			jourFerie.add(Calendar.DATE, 1);
		}
		if (rang == 3)
		{
			// 1st may
			jourFerie.set(annee, 4, 1);
		}
		if (rang == 4)
		{
			// 8th may
			jourFerie.set(annee, 4, 8);
		}
		if (rang == 5)
		{
			// ascension thursday 
			jourFerie.add(Calendar.DATE, 39);
		}
		if (rang == 6)
		{
			// whit monday
			jourFerie.add(Calendar.DATE, 50);
		}
		if (rang == 7)
		{
			// 14th july
			jourFerie.set(annee, 6, 14);
		}
		if (rang == 8)
		{
			// 15th august assumption
			jourFerie.set(annee, 7, 15);
		}
		if (rang == 9)
		{
			// 1st november all saint's day
			jourFerie.set(annee, 10, 1);
		}
		if (rang == 10)
		{
			// 11th november
			jourFerie.set(annee, 10, 11);
		}
		if (rang == 11)
		{
			// 25th december christmas
			jourFerie.set(annee, 11, 25);
		}

		return jourFerie;
	}

}
