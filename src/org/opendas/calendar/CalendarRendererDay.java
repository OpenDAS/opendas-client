package org.opendas.calendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarRendererDay extends CalendarRendererWeek{
	  // main colors
	  private float red_begin = 80;
	  private float green_begin = 150;
	  private float blue_begin = 190;
	  private Color hour_color = new Color((int) (red_begin + 0.5 * (255 - red_begin)),
	      (int) (green_begin + 0.5 * (255 - green_begin)),
	      (int) (blue_begin + 0.5 * (255 - blue_begin)));
	  private int day_width;
	  private int space;
	  private Font f;
	  private Font small_f;
	  private int digit_font_width;
	  private int font_height;
	  private int font_ascent;
	  private int small_digit_font_width;
	  private int small_font_ascent;

	  /**
	   *  Gets the font attribute of the CalendarRenderer object
	   *
	   * @return    The font value
	   */
	  public Font get_font() {
	    return f;
	  }


	  /**
	   *  Draw the calendar on the DateLookPanel
	   *
	   * @param  g2  graphics object
	   */
	  public void draw(Graphics2D g2, DateLookPanel panel) {
	    int csh = panel.getSlot_height();
	    int csn = panel.number_of_slots;
	    
	    // day_width and small_f will be changed by zooming therefore both must be determined on each draw
	    day_width = panel.getWidth() * 24 / (int) panel.get_number_of_rendered_hours(); // not exact for DST-switches!
	    small_f = new Font("SansSerif", Font.PLAIN, 12);
	    FontRenderContext context = g2.getFontRenderContext();
	    Rectangle2D bounds = small_f.getStringBounds("0", context);
	    small_digit_font_width = (int) bounds.getWidth();
	    small_font_ascent = (int) -bounds.getY();

	    g2.setFont(f);
	    g2.setColor(Color.black);

	    GregorianCalendar gc = Converter.ms2gc(panel.get_first_rendered_hour_UTC_ms());
	    gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
	    
	    DateDayOff dayOff = new DateDayOff(gc);

	    // render each day
	    int i = 0;
	    // counter of renderer days
	    while (true) {
	      int x_pos_real = UTC2x_pos(gc.getTime().getTime(),panel);
	      int x_pos;
	      // x where day rendering begins (for first day not equal to x_pos_real!)
	      if (i != 0) {
	        x_pos = x_pos_real;
	      }
	      else {
	        x_pos = 0;
	      }
	      if (x_pos > panel.getWidth()) {
	        break;
	      }

	    // dayweek + n + month + years
	    g2.setColor(Color.black);
	    if (i == 0 || gc.get(GregorianCalendar.DAY_OF_MONTH) == 1) {
	      g2.fillRect(x_pos, csh, panel.getWidth(), csh);
		   g2.setColor(Color.black);
	      if (i != 0) {
	      	g2.drawLine(x_pos, 0, x_pos, csh);
	      }
	      g2.setColor(this.getDayOfWeekColour(gc, dayOff));
	      String dayText =(Converter.getDayOfWeekWString(gc).replace(Converter.getDayOfWeekWString(gc).substring(0,1), Converter.getDayOfWeekWString(gc).substring(0,1).toUpperCase()))+" "+gc.get(GregorianCalendar.DAY_OF_MONTH)+" "+(Converter.gc2monthl(gc).replace(Converter.gc2monthl(gc).substring(0,1), Converter.gc2monthl(gc).substring(0,1).toUpperCase()))+" "+gc.get(GregorianCalendar.YEAR);
	      g2.drawString(dayText, x_pos + space+10, (csh / 2) + font_ascent / 2);
   
	      // week of the year
		  g2.setColor(Color.black);
	      dayText = "(Semaine "+(gc.get(GregorianCalendar.WEEK_OF_YEAR))+")";     
	      g2.drawString(dayText, x_pos + (panel.getWidth() - (panel.getWidth()/8)), csh / 2 + font_ascent / 2);
 
	    }

	      // hours
	      g2.setColor(hour_color);
	      g2.fillRect(x_pos, csh, panel.getWidth(), csh);
	     
	      // determine number of hours of that day (23/24/25)
	      int day_hours = 24;
	      day_hours = day_hours + gc.get(Calendar.DST_OFFSET) / (1000 * 60 * 60);

	      // align digits to right edge of a day (necessary for DST-switch!)
	      gc.add(GregorianCalendar.DAY_OF_YEAR, 1);  // increase day
	      day_hours = day_hours - gc.get(Calendar.DST_OFFSET) / (1000 * 60 * 60);
	      x_pos_real = UTC2x_pos(gc.getTime().getTime(),panel) - day_width;

	      if (day_width > 30) {
	        g2.setColor(Color.black);

	        g2.drawString("6", x_pos_real + day_width / 4 - small_digit_font_width / 2, (3 * csh) / 2 + small_font_ascent / 2);
	        g2.drawString("12", x_pos_real + day_width / 2 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);
	        g2.drawString("18", x_pos_real + day_width * 3 / 4 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);
	        if (day_width / 16 > small_digit_font_width) {
	          g2.drawString("3", x_pos_real + day_width / 8 - small_digit_font_width / 2, (3 * csh) / 2 + small_font_ascent / 2);
	          g2.drawString("9", x_pos_real + day_width * 3 / 8 - small_digit_font_width / 2, (3 * csh) / 2 + small_font_ascent / 2);
	          g2.drawString("15", x_pos_real + day_width * 5 / 8 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);
	          g2.drawString("21", x_pos_real + day_width * 7 / 8 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);
	          if (day_width / 48 > small_digit_font_width) {
	            // draw hour-lines too
				g2.setColor(Color.gray);
	            g2.drawLine(x_pos_real + day_width * 6 / 24, 2 * csh, x_pos_real + day_width * 6 / 24, csn * csh);
	            g2.drawLine(x_pos_real + day_width * 12 / 24, 2 * csh, x_pos_real + day_width * 12 / 24, csn * csh);
	            g2.drawLine(x_pos_real + day_width * 18 / 24, 2 * csh, x_pos_real + day_width * 18 / 24, csn * csh);
	            g2.drawLine(x_pos_real + day_width * 3 / 24, 2 * csh, x_pos_real + day_width * 3 / 24, csn * csh);
	            g2.drawLine(x_pos_real + day_width * 9 / 24, 2 * csh, x_pos_real + day_width * 9 / 24, csn * csh);
	            g2.drawLine(x_pos_real + day_width * 15 / 24, 2 * csh, x_pos_real + day_width * 15 / 24, csn * csh);
	            g2.drawLine(x_pos_real + day_width * 21 / 24, 2 * csh, x_pos_real + day_width * 21 / 24, csn * csh);
	            g2.setColor(Color.black);
	            g2.drawString("4", x_pos_real + day_width * 4 / 24 - small_digit_font_width / 2, (3 * csh) / 2 + small_font_ascent / 2);
				g2.setColor(Color.gray);
	            g2.drawLine(x_pos_real + day_width * 4 / 24, 2 * csh, x_pos_real + day_width * 4 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            g2.drawString("5", x_pos_real + day_width * 5 / 24 - small_digit_font_width / 2, (3 * csh) / 2 + small_font_ascent / 2);
				g2.setColor(Color.gray);

	            g2.drawLine(x_pos_real + day_width * 5 / 24, 2 * csh, x_pos_real + day_width * 5 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            g2.drawString("7", x_pos_real + day_width * 7 / 24 - small_digit_font_width / 2, (3 * csh) / 2 + small_font_ascent / 2);				g2.setColor(Color.gray);

	            g2.drawLine(x_pos_real + day_width * 7 / 24, 2 * csh, x_pos_real + day_width * 7 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            g2.drawString("8", x_pos_real + day_width * 8 / 24 - small_digit_font_width / 2, (3 * csh) / 2 + small_font_ascent / 2);				g2.setColor(Color.gray);

	            g2.drawLine(x_pos_real + day_width * 8 / 24, 2 * csh, x_pos_real + day_width * 8 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            g2.drawString("10", x_pos_real + day_width * 10 / 24 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);				
	            g2.setColor(Color.gray);

	            g2.drawLine(x_pos_real + day_width * 10 / 24, 2 * csh, x_pos_real + day_width * 10 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            g2.drawString("11", x_pos_real + day_width * 11 / 24 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);				
	            g2.setColor(Color.gray);

	            g2.drawLine(x_pos_real + day_width * 11 / 24, 2 * csh, x_pos_real + day_width * 11 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            g2.drawString("13", x_pos_real + day_width * 13 / 24 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);				
	            g2.setColor(Color.gray);

	            g2.drawLine(x_pos_real + day_width * 13 / 24, 2 * csh, x_pos_real + day_width * 13 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            g2.drawString("14", x_pos_real + day_width * 14 / 24 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);				
	            g2.setColor(Color.gray);

	            g2.drawLine(x_pos_real + day_width * 14 / 24, 2 * csh, x_pos_real + day_width * 14 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            g2.drawString("16", x_pos_real + day_width * 16 / 24 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);				
	            g2.setColor(Color.gray);

	            g2.drawLine(x_pos_real + day_width * 16 / 24, 2 * csh, x_pos_real + day_width * 16 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            g2.drawString("17", x_pos_real + day_width * 17 / 24 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);				
	            g2.setColor(Color.gray);

	            g2.drawLine(x_pos_real + day_width * 17 / 24, 2 * csh, x_pos_real + day_width * 17 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            g2.drawString("19", x_pos_real + day_width * 19 / 24 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);				
	            g2.setColor(Color.gray);

	            g2.drawLine(x_pos_real + day_width * 19 / 24, 2 * csh, x_pos_real + day_width * 19 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            g2.drawString("20", x_pos_real + day_width * 20 / 24 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);				
	            g2.setColor(Color.gray);

	            g2.drawLine(x_pos_real + day_width * 20 / 24, 2 * csh, x_pos_real + day_width * 20 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            g2.drawString("22", x_pos_real + day_width * 22 / 24 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);				
	            g2.setColor(Color.gray);

	            g2.drawLine(x_pos_real + day_width * 22 / 24, 2 * csh, x_pos_real + day_width * 22 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            g2.drawString("23", x_pos_real + day_width * 23 / 24 - small_digit_font_width, (3 * csh) / 2 + small_font_ascent / 2);				
	            g2.setColor(Color.gray);

	            g2.drawLine(x_pos_real + day_width * 23 / 24, 2 * csh, x_pos_real + day_width * 23 / 24, csn * csh);	        
	            g2.setColor(Color.black);

	            // check for DST-switch, if true "1" and "2" must be shifted
	            if (day_hours == 24) {
	              // 24 h day
	              g2.drawString("1", x_pos_real + day_width / 24 - small_digit_font_width / 2, (3 * csh) / 2 + small_font_ascent / 2);				
	              g2.setColor(Color.gray);
	              g2.drawLine(x_pos_real + day_width / 24, 2 * csh, x_pos_real + day_width / 24, csn * csh);	        
	              g2.setColor(Color.black);
	              g2.drawString("2", x_pos_real + day_width * 2 / 24 - small_digit_font_width / 2, (3 * csh) / 2 + small_font_ascent / 2);				
	              g2.setColor(Color.gray);
	              g2.drawLine(x_pos_real + day_width * 2 / 24, 2 * csh, x_pos_real + day_width * 2 / 24, csn * csh);	        
	              g2.setColor(Color.black);

	            }
	            else if (day_hours == 25) {
	              // 25 h day
	              g2.drawString("1", x_pos_real - small_digit_font_width / 2, (3 * csh) / 2 + small_font_ascent / 2);
	              g2.drawLine(x_pos_real, 2 * csh, x_pos_real, csn * csh);
	              g2.drawString("2", x_pos_real + day_width / 24 - small_digit_font_width / 2, (3 * csh) / 2 + small_font_ascent / 2);
	              g2.drawLine(x_pos_real + day_width / 24, 2 * csh, x_pos_real + day_width / 24, csn * csh);
	              g2.drawString("2", x_pos_real + day_width * 2 / 24 - small_digit_font_width / 2, (3 * csh) / 2 + small_font_ascent / 2);
	              g2.drawLine(x_pos_real + day_width * 2 / 24, 2 * csh, x_pos_real + day_width * 2 / 24, csn * csh);
	            }
	            else {
	              // 23 h day
	              g2.drawString("1", x_pos_real + day_width * 2 / 24 - small_digit_font_width / 2, (3 * csh) / 2 + small_font_ascent / 2);
	              g2.drawLine(x_pos_real + day_width * 2 / 24, 2 * csh, x_pos_real + day_width * 2 / 24, csn * csh);
	            }
	          }
	        }
	      }

	      // lines between days
	      g2.setColor(Color.black);
	      if (i != 0 && day_width > 16) {
	        g2.drawLine(x_pos, 2 * csh, x_pos, DateLookPanel.number_of_slots * csh);
	      }
	      i++;
	    }

	    // render horizontal lines
	    g2.setColor(Color.black);
	    for (int k = 1; k < DateLookPanel.number_of_slots + 1; k++) {
	      g2.drawLine(0, csh * k, panel.getWidth(), csh * k);
	    }
	  }


	  /**
	   *  Determines default font (called after resizing of the DateLookPanel by this)
	   */
	  public void resized(DateLookPanel panel) {
	    Graphics2D g2 = (Graphics2D) panel.getGraphics();
	    day_width = panel.getWidth() * 24 / (int) panel.get_number_of_rendered_hours(); // not exact for DST-switches!
	    space = panel.getSlot_height() / 4;

	    f = new Font("SansSerif", Font.PLAIN, 12);
	    FontRenderContext context = g2.getFontRenderContext();
	    Rectangle2D bounds = f.getStringBounds("0", context);
	    digit_font_width = (int) bounds.getWidth();
	    font_height = (int) bounds.getHeight();
	    font_ascent = (int) -bounds.getY();
	  }


	  /**
	   *  Gets the dayOfWeekColour attribute of the CalendarRenderer object
	   *
	   * @param  g  gregorian calendar object
	   * @return    The dayOfWeekColor value
	   */
	  private Color getDayOfWeekColour(GregorianCalendar g, DateDayOff dayOff) {
			for (int rng=1; rng<=11; rng++){
			  if (g.get(GregorianCalendar.DAY_OF_WEEK) == Calendar.SATURDAY
		         || g.get(GregorianCalendar.DAY_OF_WEEK) == Calendar.SUNDAY
		         || (g.get(Calendar.MONTH) == dayOff.aFerie(rng).get(Calendar.MONTH)) &&(g.get(Calendar.DATE) ==
		        	 dayOff.aFerie(rng).get(Calendar.DATE))) {
		      return Color.red;
		    }
			}
		    return Color.black;

		  }


	  /**
	   *  Convert UTC to x position on DateLookPanel
	   *
	   * @param  l  UTC
	   * @return    x position
	   */
	  private int UTC2x_pos(long l, DateLookPanel panel) {
	    return (int) ((l - panel.get_first_rendered_hour_UTC_ms()) * panel.getWidth() /
	        (panel.get_number_of_rendered_hours() * 60 * 60 * 1000));
	  }
}
