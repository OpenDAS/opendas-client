package org.opendas.calendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarRendererWeek {
	// main colors
	  private float red_begin = 80;
	  private float green_begin = 150;
	  private float blue_begin = 190;
	  private Color month_color = new Color((int) (red_begin + 0.85 * (255 - red_begin)),
	      (int) (green_begin + 0.85 * (255 - green_begin)),
	      (int) (blue_begin + 0.85 * (255 - blue_begin)));
	  private Color week_end_color = new Color(255, 180, 180);
	  private Color week_color = Color.white;
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

	    // month
	    g2.setColor(Color.black);
	    if (i == 0 || gc.get(GregorianCalendar.DAY_OF_MONTH) == 1) {
	      g2.setColor(month_color);
	      g2.fillRect(x_pos, csh, panel.getWidth(), csh);
	      g2.setColor(Color.black);
	      if (i != 0) {
	      	g2.drawLine(x_pos, 0, x_pos, csh);
	      }
	      g2.drawString(Converter.gc2monthl(gc).replace(Converter.gc2monthl(gc).substring(0,1), 
	    		  Converter.gc2monthl(gc).substring(0,1).toUpperCase())+" - "+gc.get(GregorianCalendar.YEAR), 
	    		  x_pos + space + 10,
	          csh / 2 + font_ascent / 2);
	    }
	      

	      int day_width_ext = day_width * 25 / 24 + 2;  // to render DST-switch-day exact too!

	      // day
	      g2.setColor(this.getDayColour(gc));
	      g2.fillRect(x_pos, 2 * csh, day_width_ext, csh);
	      if (day_width > 16) {
	        g2.setColor(Color.black);
	        g2.drawString(gc.get(GregorianCalendar.DAY_OF_MONTH) + "",
	            x_pos + space+10, ((3 * csh) / 2 + font_ascent / 2));
	        
	        // week of the year
	        if (gc.get(GregorianCalendar.DAY_OF_WEEK) == Calendar.MONDAY && (day_width > 2 * (digit_font_width + small_digit_font_width + space))) {
	          g2.setFont(small_f);
	          g2.setColor(Color.red);
	          g2.drawString(gc.get(GregorianCalendar.WEEK_OF_YEAR) + "",
	              x_pos + day_width - space - small_digit_font_width * 2,
	              (3 * csh) / 2 + font_ascent / 2);
	          g2.setFont(f);
	          g2.setColor(Color.black);
	        }
	      }
	      else if ((gc.get(GregorianCalendar.DAY_OF_WEEK) == Calendar.SUNDAY) && (7 * day_width > space + 2 * digit_font_width)) {
	        g2.setColor(Color.red);
	        g2.drawString(gc.get(GregorianCalendar.WEEK_OF_YEAR) + "",
	            x_pos - 6 * day_width + space / 2, 
	            (3 * csh) / 2);
	        g2.setColor(Color.black);
	      }

	      // day of week
	      g2.setColor(this.getDayOfWeekColour(gc, dayOff));
	      g2.fillRect(x_pos, 2 * csh, day_width_ext, csh);
	      if (day_width > 16) {
	    	  String dayOfWeek = Converter.getDayOfWeekWString(gc).replace(Converter.getDayOfWeekWString(gc).substring(0,1), 
	    			  Converter.getDayOfWeekWString(gc).substring(0,1).toUpperCase());
	    	  g2.setColor(Color.black);
	    	  g2.drawString(dayOfWeek,
	    			  x_pos + space+5,((5 * csh) / 2 + font_ascent - font_height / 2));
	    	  
	    	  
	      }

	      // determine number of hours of that day (23/24/25)
	      int day_hours = 24;
	      day_hours = day_hours + gc.get(Calendar.DST_OFFSET) / (1000 * 60 * 60);

	      // align digits to right edge of a day (necessary for DST-switch!)
	      gc.add(GregorianCalendar.DAY_OF_YEAR, 1);  // increase day
	      day_hours = day_hours - gc.get(Calendar.DST_OFFSET) / (1000 * 60 * 60);
	      x_pos_real = UTC2x_pos(gc.getTime().getTime(),panel) - day_width;

	      if (day_width > 30) {
	        g2.setFont(f);
	      }

	      // lines between days
	      g2.setColor(Color.black);
	      if (i != 0 && day_width > 16) {
	        g2.drawLine(x_pos, 1 * csh, x_pos, 3 * csh);
	      }
	      if (i != 0 && day_width > 16) {
		      g2.setColor(Color.gray);
	    	  g2.drawLine(x_pos, 3 * csh, x_pos, DateLookPanel.number_of_slots * csh);
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
	   *  Gets the dayColour attribute of the CalendarRenderer object
	   *
	   * @param  g  gregorian calendar object
	   * @return    The dayColor value
	   */
	  private Color getDayColour(GregorianCalendar g) {
	    float c = (float) g.get(GregorianCalendar.DAY_OF_MONTH) / 31F;
	    return new Color((int) (red_begin + c * (255 - red_begin)),
	        (int) (green_begin + c * (255 - green_begin)),
	        (int) (blue_begin + c * (255 - blue_begin)));
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
		      return week_end_color;
		    }
			}
		    return week_color;
		  }


	  /**
	   *  Convert UTC to x position on DateLookPanel
	   *
	   * @param  l  UTC
	   * @return    x position
	   */
	  private int UTC2x_pos(long l,DateLookPanel panel) {
	    return (int) ((l - panel.get_first_rendered_hour_UTC_ms()) * panel.getWidth() /
	        (panel.get_number_of_rendered_hours() * 60 * 60 * 1000));
	  }
}

