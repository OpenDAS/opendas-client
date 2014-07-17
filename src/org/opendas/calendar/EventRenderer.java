package org.opendas.calendar;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;



/*
 *  Title:        DateLook
 *  Copyright:    Copyright (c) 2001 - 2010
 *  Author:       Rene Ewald
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details. You should have
 *  received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
/**
 *  Renders events on the DateLookPanel
 */
public class EventRenderer {

	private Event event;

	private int x_pos;  // x-coordinate of rectangle on panel
	private static int width;  // width of rectangle on panel
	private boolean visible = true;
	private boolean focus = false;

	private boolean summary_already_drawn;
	private int x_summary;  //coordinates of drawn description
	private int y_summary;
	private int height;  // height of description

	private String s;  // String of description
	private Font f;  // used font for description
	private int space;  // x-space between description text and description border
	private int text_width;  // description text width
	private int text_height;  // description text height
	private int ascent;  // font ascent

	private boolean displayOverDescription = true;
	private boolean redVerticalLine = false;
	private int period_counter = 0;  // stores number the period where the mouse is

	private static DateLookPanel panel;   // parent panel

	/**
	 *  Constructor for the EventRenderer object
	 *
	 * @param  t  event
	 */
	public EventRenderer(Event t, DateLookPanel p) {
		event = t;
		panel = p;
	}


	/**
	 *  Draw the event on the DateLookPanel.
	 *
	 * @param  g2              graphics object
	 * @param  paint_rect      if true draw the rectangle to the events display
	 * @param  paint_con_line  if true draw the connection line between the rectangle and the description
	 * @param  paint_summary   if true draw the summary to the summaries display
	 * @return                 indicates whether the event is in shown space of time and is set to visible
	 */
	public boolean draw(Graphics2D g2, boolean paint_rect, boolean paint_con_line, boolean paint_summary) {
		boolean ret_val = false;

		if (!visible) {
			return true;
		}
		int drawn_period_counter = (int) Math.min(Math.max(((    //preset value to prevent many while-loops
				(panel.get_first_rendered_hour_UTC_ms() + panel.get_number_of_rendered_hours() * 60L * 60L * 1000L) -
				event.get_begin_UTC_ms()) /
				Converter.period2ms(event.get_period(), event.get_period_multiplier())) + 2, 0), (long) event.get_number_of_periods() - 1);
		g2.setColor(event.get_renderer_color());
		summary_already_drawn = false;

		while (true) {
			this.set_rect(drawn_period_counter);
			if (drawn_period_counter < 0 || x_pos + width < 0) {
				return ret_val;   // all visible cyclic occurrences painted
			}
			else if (x_pos < panel.getWidth()) {
				ret_val = true;
				if (paint_rect) {
					g2.setColor(Color.black);
					g2.fillRect(x_pos, ((panel.getNum_ligne_top_description()) + real_renderer_group_to_used_rg()) * panel.getSlot_height(), width+1, panel.getSlot_height());

					g2.setColor(event.get_renderer_color());
					g2.fillRect(x_pos+1, ((panel.getNum_ligne_top_description()) + real_renderer_group_to_used_rg()) * panel.getSlot_height()+1, width-1, panel.getSlot_height()-1);

					if(event.isSelected()){
						g2.setColor(Color.red);  
					}else{
						g2.setColor(Color.black);
					}

					int nb_day = Integer.parseInt(""+(event.get_end_UTC_ms()-event.get_begin_UTC_ms())/(86400*1000));

					int day_full = Integer.parseInt((""+nb_day).split(",")[0]); 
					day_full++;

					int espace_margin = 15;
					int stat_xp = (x_pos+espace_margin);
					int xpos = 0;

					if(nb_day > 1){

						if(stat_xp > espace_margin){
							xpos = stat_xp;
						}else{
							xpos = espace_margin;
						}


						if(x_pos < 0){
							text_height = 20;
							g2.drawString("<",xpos-15, (((panel.getNum_ligne_top_description()) + 1 + real_renderer_group_to_used_rg()) * (panel.getSlot_height()))-(panel.getSlot_height()/2)+5);

						}

						// if event spends screen => arrow
						if(this.panel.getWidth() < (x_pos+width)){
							g2.drawString(">",this.panel.getWidth()-8, (((panel.getNum_ligne_top_description()) + 1 + real_renderer_group_to_used_rg()) * (panel.getSlot_height()))-(panel.getSlot_height()/2)+5);
						}

						// if it's in the screen
						if((this.panel.getWidth() - (xpos+(event.get_summary().length()*8))) > 0){
							if(width > event.get_summary().length()*8){
								g2.drawString(event.get_summary(),xpos, (((panel.getNum_ligne_top_description()) + 1 + real_renderer_group_to_used_rg()) * (panel.getSlot_height()))-(panel.getSlot_height()/2)+5);
							}else{
								if((this.panel.getWidth() - (x_pos+width+(event.get_summary().length()*8))) > 0){
									g2.drawString(event.get_summary(),x_pos+width+5, (((panel.getNum_ligne_top_description()) + 1 + real_renderer_group_to_used_rg()) * (panel.getSlot_height()))-(panel.getSlot_height()/2)+5);
								}else{
									g2.drawString(event.get_summary(),(x_pos-45-(event.get_summary().length()*5)), (((panel.getNum_ligne_top_description()) + 1 + real_renderer_group_to_used_rg()) * (panel.getSlot_height()))-(panel.getSlot_height()/2)+5);
								}
							}
						}else{
							//if event spends to the left
							g2.drawString(event.get_summary(),xpos-25-(event.get_summary().length()*8), (((panel.getNum_ligne_top_description()) + 1 + real_renderer_group_to_used_rg()) * (panel.getSlot_height()))-(panel.getSlot_height()/2)+5);
						}
					}else{
						if(width > event.get_summary().length()*8){
							g2.drawString(event.get_summary(),x_pos+15, (((panel.getNum_ligne_top_description()) + 1 + real_renderer_group_to_used_rg()) * (panel.getSlot_height()))-(panel.getSlot_height()/2)+5);
							//si reste de l'espace à droite de l'event...
						}else if((this.panel.getWidth() - (x_pos+width+(event.get_summary().length()*8))) > 0){
							g2.drawString(event.get_summary(),x_pos+width+5, (((panel.getNum_ligne_top_description()) + 1 + real_renderer_group_to_used_rg()) * (panel.getSlot_height()))-(panel.getSlot_height()/2)+5);
						}else{
							g2.drawString(event.get_summary(),(x_pos-45-(event.get_summary().length()*5)), (((panel.getNum_ligne_top_description()) + 1 + real_renderer_group_to_used_rg()) * (panel.getSlot_height()))-(panel.getSlot_height()/2)+5);
						}


					}
					g2.setColor(event.get_renderer_color());
					if (focus) {
						g2.drawRect(x_pos - 2, ((panel.getNum_ligne_top_description()) + real_renderer_group_to_used_rg()) * panel.getSlot_height() - 2, width + 4, panel.getSlot_height() + 4);

						//Red Vertical Lines
						if(redVerticalLine){
							g2.setColor(new Color(255,48,48));
							g2.drawLine(x_pos - 3, 0, x_pos - 3, panel.getHeight());
							g2.drawLine((x_pos - 3)+width + 6, 0,(x_pos - 3)+width + 6, panel.getHeight());
						}
					}
				}

				// paint summary and connection line
				if (paint_summary || paint_con_line) {

					// determine place of description and font
					if (!summary_already_drawn) {
						space = panel.getSlot_height() / (panel.getNum_ligne_top_description());
						height = panel.getSlot_height();
						f = new Font("SansSerif", Font.PLAIN, height * 2 / 3);
						s = "";
						String hm = Converter.ms2hm(event.get_begin_UTC_ms());
						// if event starts at 00:00 or a renderer day is smaller then 16 pix then
						// show begin date instead of begin time
						if (!(panel.getWidth() * 24 / panel.get_number_of_rendered_hours() > 16) |
								hm.equals("00:00")) {
							if (event.get_period() == Event.None) {
								s = Converter.ms2dmy(Converter.UTCplusPeriod2UTC(
										event.get_begin_UTC_ms(), event.get_period(), drawn_period_counter, event.get_period_multiplier()));
							}
							// if periodic event show e.g "2-weekly since 23.Mar 2001"
							else {
								s = event.get_period_as_string() + " since " + Converter.ms2dmy(event.get_begin_UTC_ms());
							}
						}
						else {
							s = hm;
						}
						s = s + " - " + event.get_summary();
						Rectangle2D bounds = f.getStringBounds(s, g2.getFontRenderContext());
						text_width = (int) bounds.getWidth();
						text_height = (int) bounds.getHeight();
						ascent = (int) -bounds.getY();

						// get location on panel, where summary has to be drawn
						x_summary = panel.get_free_space_X(Math.max(x_pos, (panel.getNum_ligne_top_description())), text_width + space * 2);
						y_summary = panel.get_free_space_Y();
					}

					// paint summary to main window below Calendar
					if (paint_summary && !summary_already_drawn) {
						g2.setColor(event.get_renderer_color());
						if (focus) {
							g2.fillRect(x_summary - 2, y_summary - 2, text_width + space * 2 + 4, height + 2);
							g2.setColor(event.get_renderer_color());
						}


						g2.fillRect(x_summary, y_summary, text_width + space * 2, height);


						g2.setColor(Color.white);
						g2.fillRect(x_summary + 2, y_summary + 2, text_width + space * 2 - 4, height - 4);
						g2.setFont(f);
						g2.setColor(Color.black);
						g2.drawString(s, x_summary + space, y_summary + height / 2 + ascent - text_height / 2);

					}

					// paint connection lines between rectangles and descriptions
					if (paint_con_line) {
						g2.setColor(event.get_renderer_color());
						g2.drawLine(Math.max(x_pos, 0), (6 + real_renderer_group_to_used_rg()) * height, x_summary, y_summary);
					}
					summary_already_drawn = true;
				}
				else {
					x_summary = 0;   // to prevent mouse hit of not new determined summary coordinates
					y_summary = 0;
				}
			}
			drawn_period_counter--;
		}
	}

	/**
	 *  Draw the "mouse over description"
	 *
	 * @param  g2  graphics object
	 * @param  xi  x coordinate of mouse pointer
	 * @param  yi  y coordinate of mouse pointer
	 */
	public void draw_mouse_over_description(Graphics2D g2, int xi, int yi) {
		if(displayOverDescription && (panel.getEd()==null || !panel.getEd().isVisible()) && DateLookPanel.getLockDate().equals("0")){
			space = panel.getSlot_height() / (panel.getNum_ligne_top_description());
			height = panel.getSlot_height()-1;
			f = new Font("SansSerif", Font.PLAIN, height * 2 / 3);
			String s = "";
			// if event starts at 00:00 or a renderer day is smaller then 16 pix then
			// show begin date instead of begin time
			String hm = Converter.ms2hm(event.get_begin_UTC_ms());
			/*if (!(panel.getWidth() * 24 / panel.get_number_of_rendered_hours() > 16) | hm.equals("00:00")) {
				hm = Converter.ms2dmy(
						Converter.UTCplusPeriod2UTC(event.get_begin_UTC_ms(), event.get_period(),
								period_counter, event.get_period_multiplier()));
			}
			else {
				s = hm;
			}*/
			s = hm;
			String h = "";
			String hm_end = Converter.ms2hm(event.get_end_UTC_ms());
			if (!(panel.getWidth() * 24 / panel.get_number_of_rendered_hours() > 16) |
					hm_end.equals("00:00")) {
				hm_end = Converter.ms2dmy(
						Converter.UTCplusPeriod2UTC(event.get_end_UTC_ms(), event.get_period(),
								period_counter, event.get_period_multiplier()));
			}
			else {
				h = hm_end;
			}

			int x,y;
			String typepanel = panel.getDeltaT();
			String converted_stop_date = parseDateToErp(event.get_end_UTC_ms());
			String converted_start_date = parseDateToErp(event.get_begin_UTC_ms());
			int hauteur_diff = 0;

			height = height*2;
			
			String startDateAndTime=null;
			String endDateAndTime = null;
			
			startDateAndTime = converted_start_date + " - " + s;
			endDateAndTime = converted_stop_date + " - " + h;
			
			Rectangle2D bounds = f.getStringBounds(endDateAndTime, g2.getFontRenderContext());
			text_width = (int) bounds.getWidth();
			text_height = (int) bounds.getHeight();
			ascent = (int) -bounds.getY();
			
			
//			startAndendDateString = converted_start_date+" - "+converted_stop_date;
			hauteur_diff = height/5;
			/*text_width = (text_width *2)-space*7;*/
			/*last_space = (space*3)+2;*/
			x = Math.max(Math.min(xi, panel.getWidth() - text_width - 2 * space), 0);
			
			/*if(typepanel.equals("day")){
				last_space = space*2;
			}*/

			// Determine if the over_description is draw above or below 
			y= ((((panel.getNum_ligne_top_description()) - 2 + real_renderer_group_to_used_rg()) * (panel.getSlot_height()))+1);

			g2.setColor(Color.black);
			g2.fillRect(x, y, text_width + 20, height+2);
			g2.setColor(Color.white);
			g2.fillRect(x + 2, y, text_width + 20, height+1);

			g2.setFont(f);
			g2.setColor(Color.black);
					
			if(startDateAndTime != null){
				g2.drawString(startDateAndTime, x + space, (y + height / 2 + ascent - text_height / 2)-(height/2)+hauteur_diff);	
			}
			
			if(endDateAndTime != null){
				g2.drawString(endDateAndTime, x + space, (y + height / 2 + ascent - text_height / 2)+hauteur_diff);	
			}
		}
	}

	@SuppressWarnings("deprecation")
	public String parseDateToErp(Long date){
		Date tmp_date = new Date(date);
		String gMt = String.valueOf(tmp_date.getMonth()+1);
		String gD = String.valueOf(tmp_date.getDate());
		String gH = String.valueOf(tmp_date.getHours());
		String gM = String.valueOf(tmp_date.getMinutes());
		if(gMt.length()==1)gMt = "0"+gMt;
		if(gD.length()==1)gD = "0"+gD;
		if(gH.length()==1)gH = "0"+gH;
		if(gM.length()==1)gM = "0"+gM;

		String converted_date = gD+"/"+gMt+"/"+(tmp_date.getYear()-100);

		return converted_date;
	}
	/**
	 *  Check whether event's rectangle or summary is under the mouse pointer
	 *
	 * @param  x  x coordinate of mouse pointer
	 * @param  y  y coordinate of mouse pointer
	 * @return    true - mouse is over the event's rectangle,<br>
	 *            false - mouse is not over
	 */
	public boolean clicked(int x, int y) {
		if (y > ((panel.getNum_ligne_top_description()) + real_renderer_group_to_used_rg()) * panel.getSlot_height() &&
				y < ((panel.getNum_ligne_top_description()) + 1 + real_renderer_group_to_used_rg()) * panel.getSlot_height()) {
			period_counter = (int) Math.max((  // preset value to prevent many while-loops
					panel.get_first_rendered_hour_UTC_ms() - event.get_end_UTC_ms()) /
					Converter.period2ms(event.get_period(), event.get_period_multiplier()) - 1, 0);
			while (true) {
				if (period_counter + 1 > event.get_number_of_periods()) {
					break;
				}
				this.set_rect(period_counter);
				if (x_pos > panel.getWidth()) {
					break;
				}
				if (x > x_pos && x < x_pos + width) {
					return true;  // mouse over event's rectangle
				}
				period_counter++;
			}
		}

		if (x > x_summary && x < x_summary + text_width + space * 2 - 4 && y > y_summary && y < y_summary + height) {
			return true;  // mouse over summary
		}
		return false;
	}


	/**
	 *  Gets the event attribute of the EventRenderer object
	 *
	 * @return    The event value
	 */
	public Event get_event() {
		return event;
	}


	/**
	 *  Sets the visible attribute of the EventRenderer object
	 *
	 * @param  b  The new visible value
	 */
	public void set_visible(boolean b) {
		visible = b;
		panel.repaint();
	}


	/**
	 *  Gets the visible attribute of the EventRenderer object
	 *
	 * @return    The visible value
	 */
	public boolean get_visible() {
		return visible;
	}


	/**
	 *  Gets the period_counter attribute of the EventRenderer object
	 *
	 * @return    The period_counter value
	 */
	public int get_period_counter() {
		return period_counter;
	}


	/**
	 *  Sets the focus attribute of the EventRenderer object
	 *
	 * @param  b  The new focus value
	 */
	public void set_focus(boolean b) {
		focus = b;
		panel.repaint();
	}


	/**
	 *  Gets the focus attribute of the EventRenderer object
	 *
	 * @return    The focus value
	 */
	public boolean get_focus() {
		return focus;
	}


	/**
	 *  Set rectangle (x_pos, y_pos) of the events rectangle in the events display.
	 *
	 * @param  period  number of period
	 */
	private void set_rect(int period) {
		float panel_width = (float) panel.getWidth();
		float panel_time_width = (float) panel.get_number_of_rendered_hours() * 60 * 60 * 1000;
		long begin_UTC_of_period =
			Converter.UTCplusPeriod2UTC(event.get_begin_UTC_ms(), event.get_period(), period, event.get_period_multiplier());
		width = (int) Math.round(
				(float) ((Converter.UTCplusPeriod2UTC(event.get_end_UTC_ms(), event.get_period(), period, event.get_period_multiplier()) -
						begin_UTC_of_period) * panel_width / panel_time_width));
		width = Math.max(width, (panel.getNum_ligne_top_description()));
		x_pos = (int) Math.round((float) (begin_UTC_of_period - panel.get_first_rendered_hour_UTC_ms()) *
				panel_width / panel_time_width);
		return;
	}


	/**
	 *  Delete itself
	 */
	public void delete() {
		panel.rebuilt_visible_event_renderer_list();
		panel.repaint();
	}


	/**
	 *  Changed, called to indicate that at least an event has been changed.
	 */
	public static void changed() {
		panel.changed();
	}

	/**
	 * If stored renderer group is greater than the displayed rows
	 * this function determines the last row.
	 */
	private int real_renderer_group_to_used_rg() {
		if (event.get_renderer_group() > DateLookPanel.number_of_slots) {
			return DateLookPanel.number_of_slots;
		}
		return event.get_renderer_group();
	}

}

