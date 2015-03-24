package org.opendas.calendar;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.JPopupMenu;

import org.opendas.DASLog;
import org.opendas.gui.DASPanel;

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
 *  DateLookPanel
 */
@SuppressWarnings("serial")
public class DateLookPanel extends RPanel implements Printable {

	private CalendarRendererWeek my_calendar;
	private ArrayList<EventRenderer> visible_event_renderer_list = new ArrayList<EventRenderer>(); // contains only events visible in calendar

	private RowLabels row_labels = new RowLabels();

	private DateLook main_frame;

	private JPopupMenu jPopupMenu_switch_deltaT = new JPopupMenu();

	/* for mouse contextual menu
	private JMenuItem jMenuItem_dt_day = new JMenuItem();
	private JMenuItem jMenuItem_dt_week = new JMenuItem();
	private JMenuItem jMenuItem_dt_month = new JMenuItem();

	private JPopupMenu jPopupMenu_choice_generic = new JPopupMenu();
	private JMenuItem jMenuItem_select = new JMenuItem();
	 */

	private int last_x;  // position of mouse pointer when pressed
	private int last_y;
	private int mouse_x;  // coordinates of mouse pointer
	private int mouse_y;

	private static int num_ligne_top_description = 3;

	private long first_rendered_hour_UTC_ms;

	private final long first_rendered_hour_UTC_ms_min = (new GregorianCalendar(1, 0, 1, 0, 0)).getTime().getTime(); // 01.01.01 00:00
	private final long last_rendered_UTC_ms_max = (new GregorianCalendar(2501, 0, 1, 0, 0)).getTime().getTime(); // 01.01.2501 00:00
	private final static long rendered_hours_min = 24;
	private final static long rendered_hours_max = 365 * 24;
	private boolean rebuilt_visible_event_renderer_list = true; // indicates that visible events have changed

	private long number_of_rendered_hours;
	private boolean extended_view = false;

	private EventRenderer mouse_over_event_renderer;  // "event_renderer" where the mouse is over
	private Event dragging_event;  // event that is dragging
	private Event dragging_event_before_dragging;  // stores the original event when dragging start
	private boolean dragging_event_is_new = false;  // true if the dragging event is a new one not copied
	private long last_begin_UTC_ms;  // values of dragging event when dragging starts
	private long last_end_UTC_ms;
	private long last_alarm_UTC_ms;

	/**
	 *  Height of a line for year/date/day of week.<br>
	 *  very important! controls all sizes of other windows and fonts
	 */
	private int slot_height = 0;


	public int getSlot_height()
	{
		return slot_height;
	}


	public void setSlot_height(int slotHeight)
	{
		slot_height = slotHeight;
	}

	/**
	 *  slots for calendar and dates rectangles
	 */
	protected static int number_of_slots = 17; //description rows in.

	/**
	 *  height of the frame decor
	 */
	protected static int frame_decor_height = 0;
	/**
	 *  width of the frame decor
	 */
	protected static int frame_decor_width = 0;

	// variables to control the descriptons renderer
	private int[] free_x = new int[15];  // array to remember x_coordinate of free space in row before
	private int free_space_y;  // temporarily store calculated y-coordinate for a description
	private int space_between_date_descriptions;
	private int descriptions_slot_height;
	private int required_description_renderer_height = 0;  // to show all dates descriptions in extended view
	private int y_description_slot0;  // y-coordinate of first row in descriptons renderer
	private int main_frame_height_before_ext_view;  // used to switch back to simple view

	private DateLook mf;
	private String deltaT = "week";
	private boolean isSelectionMode = true;
	private GregorianCalendar gc = new GregorianCalendar();
	private static String lockName = "1";
	private static String lockDate = "1";

	/**
	 *  Constructor for the DateLookPanel object
	 *
	 * @param  mf  main frame of DateLook
	 */
	public DateLookPanel(DateLook mf) {
		super(mf, false);
		this.setMf(mf);		

		deltaT = mf.getDeltaT();

		lockName = mf.getLockName();

		lockDate = mf.getLockDate();

		free_x = new int[mf.getParentPanel().getCALENDAR_ROWS()];

		gc = mf.getGc();

		if(gc==null){
			gc = new GregorianCalendar();
		}

		free_x = new int[mf.getParentPanel().getCALENDAR_ROWS()];

		// Type Panel
		if(deltaT.equals("week")){
			num_ligne_top_description = 3;
			my_calendar = new CalendarRendererWeek();
			number_of_slots = mf.getParentPanel().getCALENDAR_ROWS()+3;

			gc.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);


		}else if(deltaT.equals("month")){
			num_ligne_top_description = 3;
			my_calendar = new CalendarRendererMonth();
			number_of_slots = mf.getParentPanel().getCALENDAR_ROWS()+3;

			gc.set(GregorianCalendar.DAY_OF_MONTH, 1);

		}else if(deltaT.equals("day")){
			num_ligne_top_description = 2;
			my_calendar = new CalendarRendererDay();
			number_of_slots = mf.getParentPanel().getCALENDAR_ROWS()+2;
		}   

		Settings.setNumber_of_slots(number_of_slots);

		main_frame = getMf();
		this.setBackground(Color.white);

		gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
		gc.set(GregorianCalendar.MINUTE, 0);
		gc.set(GregorianCalendar.SECOND, 0);
		gc.set(GregorianCalendar.MILLISECOND, 0);
		first_rendered_hour_UTC_ms = gc.getTime().getTime();

		number_of_rendered_hours = Settings.get_instance(null).get_number_of_rendered_hours();

		/*		jMenuItem_dt_day.setText("Jour");
				jMenuItem_dt_week.setText("Semaine");
				jMenuItem_dt_month.setText("Month");
				jMenuItem_select.setText("Selectionner");

				jPopupMenu_switch_deltaT.add(jMenuItem_dt_day);
				jPopupMenu_switch_deltaT.add(jMenuItem_dt_week);
				jPopupMenu_switch_deltaT.add(jMenuItem_dt_month);
				jPopupMenu_choice_generic.add(jMenuItem_select);

				jMenuItem_dt_day.addActionListener(
						new java.awt.event.ActionListener() {
							public void actionPerformed(ActionEvent e) {
								switchDisplayMode("day");
							}
						});

				jMenuItem_dt_week.addActionListener(
						new java.awt.event.ActionListener() {
							public void actionPerformed(ActionEvent e) {
								switchDisplayMode("week");
							}
						});

				jMenuItem_dt_month.addActionListener(
						new java.awt.event.ActionListener() {
							public void actionPerformed(ActionEvent e) {
								switchDisplayMode("month");
							}
						});

				jMenuItem_select.addActionListener(
						new java.awt.event.ActionListener() {
							public void actionPerformed(ActionEvent e) {
								getMf().sendCodeToPanelParent(codeEventToSend);
							}
						});*/

	}

	public GregorianCalendar getGc()
	{
		return gc;
	}



	public void setGc(GregorianCalendar gc)
	{
		this.gc = gc;
	}



	public void changeDeltaT(String dt) {
		Settings.setDeltaT(dt);
		deltaT = dt;
		mf.getParentPanel().refreshCalendar(dt);
	}

	/**
	 *  Process component event.<br>
	 *  If panel resized then it resizes all components
	 *
	 * @param  e  component event
	 */
	public void processComponentEvent(ComponentEvent e) {
		super.processComponentEvent(e);
		if (e.getID() == ComponentEvent.COMPONENT_RESIZED) {
			row_labels.parent_panel_resized();
			frame_decor_height = 3000;
			frame_decor_width = main_frame.getWidth() - this.getWidth();
			if (!extended_view) {
				slot_height = this.getHeight() / number_of_slots;  // in extended mode slot_height is frozen
				descriptions_slot_height = (slot_height * 20) / 15;
			}
			my_calendar.resized(this);
			this.repaint();
		}
	}


	/**
	 *  Paint component
	 *
	 * @param  g  Graphics object
	 */
	public void paintComponent(Graphics g) {
		required_description_renderer_height = 0;
		super.paintComponent(g);

		my_calendar.draw(g2,this);
		if (dragging_event == null) {
			if (rebuilt_visible_event_renderer_list) {
				// rebuilt visible_event_renderer_list here
				rebuilt_visible_event_renderer_list = false;
				visible_event_renderer_list.clear();
				for (int i = 0; i < this.mf.eventMemory.get_size(); i++) {
					EventRenderer tmp_renderer = this.mf.eventMemory.get_event(i).get_event_renderer();
					if (tmp_renderer == null) {
						// create a renderer for this event
						tmp_renderer = new EventRenderer(this.mf.eventMemory.get_event(i), this);
						this.mf.eventMemory.get_event(i).set_event_renderer(tmp_renderer);
					}
					if (tmp_renderer.draw(g2, true, false, false)) {
						// event is visible in calendar
						visible_event_renderer_list.add(tmp_renderer); // add to visible_event_renderer_list
					}
				}
			}
			else {
				// reuse visible_event_renderer_list
				for (int i = 0; i < visible_event_renderer_list.size(); i++) {
					((EventRenderer) visible_event_renderer_list.get(i)).draw(g2, true, false, false);
				}
			}
		}
		else {
			// if dragging is in progress the "old" list of visible events can be used -> faster rendering!
			if (!visible_event_renderer_list.contains(dragging_event.get_event_renderer())) {
				rebuilt_visible_event_renderer_list = true;
				visible_event_renderer_list.add(dragging_event.get_event_renderer());
			}
			else {
				// dragging event to foreground if not already there
				if (visible_event_renderer_list.get(visible_event_renderer_list.size() - 1) != dragging_event.get_event_renderer()) {
					visible_event_renderer_list.add(visible_event_renderer_list.remove(
							// dragging event to foreground
							visible_event_renderer_list.indexOf(dragging_event.get_event_renderer())));
				}
			}
			if (dragging_event_before_dragging != null && !visible_event_renderer_list.contains(dragging_event_before_dragging.get_event_renderer())) {
				visible_event_renderer_list.add(dragging_event_before_dragging.get_event_renderer());
			}
			for (int i = 0; i < visible_event_renderer_list.size(); i++) {
				((EventRenderer) visible_event_renderer_list.get(i)).draw(g2, true, false, false);
			}
		}

		if (extended_view) {
			// draw connection lines over rectangles
			reset_space_map();    // reset occupied space for date description on page
			for (int i = 0; i < visible_event_renderer_list.size(); i++) {
				((EventRenderer) visible_event_renderer_list.get(i)).draw(g2, false, true, false);
			}
			// draw descriptions over the connection lines
			reset_space_map();    // reset occupied space for date description on page
			for (int i = 0; i < visible_event_renderer_list.size(); i++) {
				((EventRenderer) visible_event_renderer_list.get(i)).draw(g2, false, false, true);
			}
		}

		row_labels.draw(g2,this);

		if (getMouse_over_event_renderer() != null) {
			getMouse_over_event_renderer().draw_mouse_over_description(g2, mouse_x, mouse_y);
		}

		if (extended_view && required_description_renderer_height > this.getHeight() - number_of_slots * slot_height) {
			// enlarge main_frame if extended view and there are descriptions invisible

			EventQueue.invokeLater(
					new Runnable() {
						// invoke later because it isn't a good idea to start new paint within a paint
						public void run() {
							main_frame.setSize(main_frame.getWidth(), required_description_renderer_height + main_frame_height_before_ext_view);
							main_frame.paintAll(main_frame.getGraphics());
						}
					});
		}
	}


	/**
	 *  Print the main window
	 *
	 * @param  g   Graphics object
	 * @param  pf  Page Format
	 * @param  p   
	 * @return     "no such page" or "page exists"
	 * @exception  PrinterException
	 */
	public int print(Graphics g, PageFormat pf, int p) throws PrinterException {
		if (p != 0) {
			return Printable.NO_SUCH_PAGE;
		}
		Graphics2D g2 = (Graphics2D) g;

		// set coordinates and scale: x_range = 0 to panel-width, y_range = 0 to (20 + number_of_slots) * slot_height
		g2.translate(pf.getImageableX(), pf.getImageableY());
		g2.scale(pf.getImageableWidth() / (double) this.getWidth(), pf.getImageableHeight() / (double) ((24 + number_of_slots) * slot_height));

		// print rectangles first
		reset_space_map();
		// reset occupied space for date description on page
		for (int i = 0; i < visible_event_renderer_list.size(); i++) {
			((EventRenderer) visible_event_renderer_list.get(i)).draw(g2, true, false, false);
		}

		// print connection lines over rectangles
		reset_space_map();    // reset occupied space for date description on page
		for (int i = 0; i < visible_event_renderer_list.size(); i++) {
			((EventRenderer) visible_event_renderer_list.get(i)).draw(g2, false, true, false);
		}

		// print descriptions over the connection lines
		reset_space_map();    // reset occupied space for date description on page
		for (int i = 0; i < visible_event_renderer_list.size(); i++) {
			((EventRenderer) visible_event_renderer_list.get(i)).draw(g2, false, false, true);
		}
		my_calendar.draw(g2,this);
		g2.setColor(Color.white);
		g2.fillRect(-100, 0, 100, slot_height * (descriptions_slot_height + number_of_slots));   // prevent faulty print left from x=0
		g2.setColor(Color.black);
		g2.drawRect(0, 0, this.getWidth(), (number_of_slots * slot_height) + 15 + (descriptions_slot_height * slot_height));   // print a frame
		row_labels.draw(g2,this);
		return Printable.PAGE_EXISTS;
	}

	public void mouseClicked(MouseEvent e) {
		
		System.out.println("Passage par mouseClicked");
		
		int x = e.getX();
		int y = e.getY();
		if((e.getClickCount() == 2)){
			if(isSelectionMode){
				for (int i = visible_event_renderer_list.size() - 1; i > -1; i--) {
					if (((EventRenderer) visible_event_renderer_list.get(i)).clicked(x, y)) {
						// meet an event
						if (((EventRenderer) visible_event_renderer_list.get(i)).get_event().get_my_editor_frame() != null) {
							
							return;
							// meet event is still edited
						}
						setMouse_over_event_renderer(null);
						codeEventToSend = visible_event_renderer_list.get(i).get_event().getCode();
						this.repaint();

						getMf().getParentPanel().refreshCalendar(null);
						getMf().sendCodeToPanelParent(codeEventToSend);
						return;
					}
				}
			}
			jPopupMenu_switch_deltaT.show(this, x, y);
		}
	}


	/**
	 *  Handle "mouse pressed" event
	 *
	 * @param  e  mouse event
	 */
	public void mousePressed(MouseEvent e) {
		
		System.out.println("Passage par mousePressed");
		
		ts = e.getWhen();
		jPopupMenu_switch_deltaT.setVisible(false);
		this.set_font_antialiasing (false);
		if (dragging_event != null) {
			return;   // zoom/shift or dragging in progress -> do nothing
		}

		last_x = e.getX();    // remember position for dragging or zoom/shift
		last_y = e.getY();

		if (last_y < (slot_height * (number_of_slots+num_ligne_top_description))) {
			// mouse points into dates renderer
			if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0 && (e.getModifiers() & InputEvent.BUTTON3_MASK) == 0) {
				// start dragging of an event
				for (int i = visible_event_renderer_list.size() - 1; i > -1; i--) {
					if (((EventRenderer) visible_event_renderer_list.get(i)).clicked(last_x, last_y)) {
						// existing event matched
						Event t = ((EventRenderer) visible_event_renderer_list.get(i)).get_event();
						if (t.get_my_editor_frame() == null) {
							dragging_event_before_dragging = t.clone2();  // remark: new UID!
							dragging_event = t;
							dragging_event.get_event_renderer().set_focus(true);
							// should be invisible in Event Manager
							if (dragging_event_before_dragging.get_event_renderer() == null) {
								// create a renderer for this event
								dragging_event_before_dragging.set_event_renderer(new EventRenderer(dragging_event_before_dragging, this));
							}
							dragging_event_before_dragging.get_event_renderer().set_visible(false);
							setMouse_over_event_renderer(dragging_event.get_event_renderer());
							setLast_begin_UTC_ms(dragging_event.get_begin_UTC_ms());
							last_end_UTC_ms = dragging_event.get_end_UTC_ms();
							last_alarm_UTC_ms = dragging_event.get_alarm_UTC_ms();
							dragging_event_is_new = false;
						}
						else {  // can't move event, editor is open
							Toolkit.getDefaultToolkit().beep();
						}

						cursor_control(e);
						return;
					}
				}
				// FOR CREATE NEW EVENT WITH MOUSE
				//				// no event matched therefore create a new one
				//				int group = (last_y - slot_height * num_ligne_top_description) / slot_height;
				//				long begin_UTC_ms = ((long) last_x * number_of_rendered_hours * 60L * 60L * 1000L) /
				//				((long) this.getWidth()) + first_rendered_hour_UTC_ms;
				//				dragging_event = new Event(begin_UTC_ms, group);
				//				dragging_event.set_end_UTC_ms(begin_UTC_ms);
				//				dragging_event_is_new = true;
				//				last_begin_UTC_ms = begin_UTC_ms;
				//				last_end_UTC_ms = begin_UTC_ms;
				//				last_alarm_UTC_ms = begin_UTC_ms;
				//				this.mf.eventMemory.add_event(dragging_event);
				//				if (dragging_event.get_event_renderer() == null) {
				//					// create a renderer for this event
				//					dragging_event.set_event_renderer(new EventRenderer(dragging_event, this, num_ligne_top_description));
				//				}
				//				dragging_event.get_event_renderer().set_focus(true);
				//
				//				cursor_control(e);
			}
		}
	}

	/**
	 *  Handle "mouse moved" event
	 *
	 * @param  e  mouse event
	 */
	public void mouseMoved(MouseEvent e) {

		mouse_x = e.getX();
		mouse_y = e.getY();

		if (mouse_y > (slot_height * num_ligne_top_description)) {  // 5 * slot_height
			for (int i = visible_event_renderer_list.size() - 1; i > -1; i--) {
				if (((EventRenderer) visible_event_renderer_list.get(i)).clicked(mouse_x, mouse_y) && mouse_y < ((slot_height * number_of_slots))) {
					setMouse_over_event_renderer((EventRenderer) visible_event_renderer_list.get(i));
					this.repaint();
					return;
				}
			}
		}
		else {
		}
		setMouse_over_event_renderer(null);

		this.repaint();
	}


	/**
	 *  Handle mouse dragged event
	 *
	 * @param  e  mouse event
	 */
	public void mouseDragged(MouseEvent e) {

		if(last_x == e.getX() && last_y == e.getX()){

		}else{

			if(DateLookPanel.getLockDate().equals("0")){

				mouse_x = e.getX();
				mouse_y = e.getY();
				if (mouse_y > (slot_height * num_ligne_top_description)) {

					if (dragging_event != null) {  // drag an event
						// Vertical Drag
						//dragging_event.set_renderer_group(
						//		Math.max(Math.min(((e.getY() - (num_ligne_top_description*slot_height))/slot_height), 25), 0));

						long delta = (long) (e.getX() - last_x) * (long) number_of_rendered_hours *
								60L * 60L * 1000L / (long) this.getWidth();
						long begin_delta;
						long end_delta;
						long alarm_delta;
						boolean shift_pressed = false;
						if ((e.getModifiers() & InputEvent.SHIFT_MASK) == 1) {
							shift_pressed = true;
						}
						if (!shift_pressed || (dragging_event_is_new == true)) {
							// shift in five-minutes-steps
							delta = (delta / (5L * 60L * 1000L)) * (5L * 60L * 1000L);  // set to 5 min steps
							begin_delta = delta;
							end_delta = delta;
							alarm_delta = delta;
						}
						else {
							// shift in one-day-steps
							// set delta to full days and consider DST-switches (23h or 25h-days).
							// because of alarm or/and begin can be shifted over a DST-switch and end not
							// all delta times must considered separately
							int shifted_days = (int) (delta / (24L * 60L * 60L * 1000L));
							begin_delta = Converter.UTCplusPeriod2UTC(getLast_begin_UTC_ms(), Event.Daily, shifted_days, 1) - getLast_begin_UTC_ms();
							end_delta = Converter.UTCplusPeriod2UTC(last_end_UTC_ms, Event.Daily, shifted_days, 1) - last_end_UTC_ms;
							alarm_delta = Converter.UTCplusPeriod2UTC(last_alarm_UTC_ms, Event.Daily, shifted_days, 1) - last_alarm_UTC_ms;
						}

						long new_alarm_UTC_ms = last_alarm_UTC_ms + alarm_delta;
						long new_begin_UTC_ms = getLast_begin_UTC_ms() + begin_delta;
						long new_end_UTC_ms = last_end_UTC_ms + end_delta;
						if (new_alarm_UTC_ms > first_rendered_hour_UTC_ms_min && new_end_UTC_ms < last_rendered_UTC_ms_max) {
							// do not shift over absolute time limits!
							if (dragging_event_is_new == false) {
								// move event
								dragging_event.set_begin_UTC_ms(new_begin_UTC_ms);
								dragging_event.set_alarm_UTC_ms(new_alarm_UTC_ms);
								dragging_event.set_end_UTC_ms(new_end_UTC_ms);
								dragging_event.set_alarm_counter_to_next_after_now();
							}
							else {
								// new event has been created
								if (shift_pressed) {
									// if shift is pressed round begin/end/alarm-time to day-boundary
									if (end_delta > 0) {
										// shift end time
										dragging_event.set_end_UTC_ms(Converter.ms2msdayboundary(new_end_UTC_ms));
										dragging_event.set_begin_UTC_ms(Converter.ms2msdayboundary(getLast_begin_UTC_ms()));
										dragging_event.set_alarm_UTC_ms(Converter.ms2msdayboundary(last_alarm_UTC_ms));
									}
									else {
										// shift begin time and alarm time
										dragging_event.set_begin_UTC_ms(Converter.ms2msdayboundary(new_begin_UTC_ms));
										dragging_event.set_alarm_UTC_ms(Converter.ms2msdayboundary(new_alarm_UTC_ms));
										dragging_event.set_end_UTC_ms(Converter.ms2msdayboundary(last_end_UTC_ms));
									}
								}
								else {
									if (end_delta > 0) {
										// shift end time
										dragging_event.set_end_UTC_ms(new_end_UTC_ms);
										dragging_event.set_begin_UTC_ms(getLast_begin_UTC_ms());
										dragging_event.set_alarm_UTC_ms(last_alarm_UTC_ms);
									}
									else {
										// shift begin time and alarm time
										dragging_event.set_begin_UTC_ms(new_begin_UTC_ms);
										dragging_event.set_alarm_UTC_ms(new_alarm_UTC_ms);
										dragging_event.set_end_UTC_ms(last_end_UTC_ms);
									}
								}
							}
							dragging_event.changed();
						}
						cursor_control(e);
					}
				}
			}
			//	}
		}
	}

	private EditorFrame ed;
	private long ts;
	private String codeEventToSend;

	/**
	 *  Handle "mouse released" event
	 *
	 * @param  e  mouse event
	 */
	public void mouseReleased(MouseEvent e) {
		this.main_frame.getParentPanel().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		if(e.getWhen() > (ts+175)){
			this.set_font_antialiasing (true);
			int y = e.getY();

			if (y > (slot_height * num_ligne_top_description)) {

				if (dragging_event != null) {
					// dragging took place
					if (dragging_event_is_new) {
						// dragging of new event
					}
					else if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {						
					}
					else {
						// only shift of old event
						setEd(new EditorFrame(dragging_event, dragging_event_before_dragging, this.mf));
						dragging_event.set_my_editor_frame(getEd()); 
						getEd().setLocation(((this.getWidth()/2)-350), ((this.getHeight()/2)));						
						getEd().setVisible(true);												
						
						this.mf.eventMemory.purge_event(dragging_event_before_dragging);
						
					}
					dragging_event.changed();
					rebuilt_visible_event_renderer_list = true;
					dragging_event_before_dragging = null;
					dragging_event = null;
					return;
				}
			}
		}
	}

	public int getListEventEnCoursSize(){
		return this.mf.eventMemory.getListEventEnCours();
	};

	public int getListEventSize(){
		return this.mf.eventMemory.getEvenList().size();
	};

	public void switchVerticalList(int num){
		int listEventSize = this.mf.eventMemory.getEvenList().size();
		int numListEventEncours = this.mf.eventMemory.getListEventEnCours();
		if(num==-1){
			if((numListEventEncours-1)>= 0){
				numListEventEncours--;
			}
		}
		else{ 
			if((numListEventEncours+1) <= (listEventSize-1)){
				numListEventEncours++;
			}
		}

		this.mf.eventMemory.setListEventEnCours(numListEventEncours);

		if(getListEventEnCoursSize() > 0){
			mf.getParentPanel().enableUpButton(true);
		}else{
			mf.getParentPanel().enableUpButton(false);
		};
		if(getListEventEnCoursSize() < getListEventSize()-1){
			mf.getParentPanel().enableDownButton(true);
		}else{
			mf.getParentPanel().enableDownButton(false);
		};

		rebuilt_visible_event_renderer_list = true;
		this.repaint();
	}

	public int getVerticalListEnCours(){
		return this.mf.eventMemory.getListEventEnCours();
	}

	public void switchDisplayMode(String dt){
		Settings.setDeltaT(dt);
		deltaT = dt;
		this.getMf().setDeltaT(dt);

		free_x = new int[mf.getParentPanel().getCALENDAR_ROWS()];

		logDebug("Calendar Type : "+Settings.getDeltaT());
		if(gc==null){
			gc = new GregorianCalendar();
		}

		if(deltaT.equals("week")){
			num_ligne_top_description = 3;
			my_calendar = new CalendarRendererWeek();
			number_of_slots = mf.getParentPanel().getCALENDAR_ROWS()+3;

			gc.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);


		}else if(deltaT.equals("month")){
			num_ligne_top_description = 3;
			my_calendar = new CalendarRendererMonth();
			number_of_slots = mf.getParentPanel().getCALENDAR_ROWS()+3;

			gc.set(GregorianCalendar.DAY_OF_MONTH, 1);

		}else if(deltaT.equals("day")){
			num_ligne_top_description = 2;
			my_calendar = new CalendarRendererDay();
			number_of_slots = mf.getParentPanel().getCALENDAR_ROWS()+2;
		} 

		Settings.setNumber_of_slots(number_of_slots);

		slot_height = this.getHeight() / number_of_slots;  // in extended mode slot_height is frozen
		descriptions_slot_height = (slot_height * 20) / 15;

		this.setBackground(Color.white);

		gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
		gc.set(GregorianCalendar.MINUTE, 0);
		gc.set(GregorianCalendar.SECOND, 0);
		gc.set(GregorianCalendar.MILLISECOND, 0);

		this.getMf().setGc(gc);

		first_rendered_hour_UTC_ms = gc.getTime().getTime();

		number_of_rendered_hours = Settings.get_instance(null).get_number_of_rendered_hours();

		rebuilt_visible_event_renderer_list = true;

		my_calendar.resized(this);
		this.repaint();

		this.getMf().getParentPanel().refreshCalendar(dt);

	}

	private static int current_screen_view = 0;

	public static int getCurrent_screen_view() {
		return current_screen_view;
	}


	public void setCurrent_screen_view(int currentScreenView) {
		current_screen_view = currentScreenView;
	}


	public void nextButtonCliqued() {

		current_screen_view++;

		if (deltaT.equals("month")) {
			gc.set(GregorianCalendar.MONTH, (gc.get(GregorianCalendar.MONTH)+1));
			gc.set(GregorianCalendar.DAY_OF_MONTH, 1);		}
		else if (deltaT.equals("week")) {
			gc.set(GregorianCalendar.WEEK_OF_YEAR, (gc.get(GregorianCalendar.WEEK_OF_YEAR)+1));
			gc.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
		}
		else if (deltaT.equals("day")) {
			gc.set(GregorianCalendar.DAY_OF_YEAR, (gc.get(GregorianCalendar.DAY_OF_YEAR)+1));
		}

		gc.add(GregorianCalendar.HOUR_OF_DAY, 0);
		set_first_rendered_hour_UTC_ms(gc.getTime().getTime());
		rebuilt_visible_event_renderer_list = true;

		this.repaint();
	}

	public void prevButtonCliqued() {

		current_screen_view--;

		if (deltaT.equals("month")) {
			gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH)-1);
			gc.set(GregorianCalendar.DAY_OF_MONTH, 1);
		}
		else if (deltaT.equals("week")) {
			gc.set(GregorianCalendar.WEEK_OF_YEAR, (gc.get(GregorianCalendar.WEEK_OF_YEAR)-1));
			gc.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
		}
		else if (deltaT.equals("day")) {
			gc.set(GregorianCalendar.DAY_OF_YEAR, (gc.get(GregorianCalendar.DAY_OF_YEAR)-1));
		}

		gc.add(GregorianCalendar.HOUR_OF_DAY, 0);
		set_first_rendered_hour_UTC_ms(gc.getTime().getTime());
		rebuilt_visible_event_renderer_list = true;

		this.repaint();
	}

	/**
	 *  Method is called if an event is dragging or if a key is pressed or released.<br>
	 *  It controls the cursor and the rendering of the event before dragging starts
	 *
	 * @param  e  input event
	 */
	private void cursor_control(InputEvent e) {
		if (dragging_event == null) {
			return;
		}
		if (dragging_event_is_new) {

		}
		// dragging an old or copied event
		else if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
			if (!dragging_event_before_dragging.get_event_renderer().get_visible()) {
				this.mf.eventMemory.add_event(dragging_event_before_dragging);
			}
			dragging_event_before_dragging.get_event_renderer().set_visible(true);
			dragging_event.changed();
		}
		else {
			if(DateLookPanel.getLockDate().equals("0")){
				this.main_frame.getParentPanel().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}
			if (dragging_event_before_dragging.get_event_renderer().get_visible()) {
				this.mf.eventMemory.purge_event(dragging_event_before_dragging);
			}
			dragging_event_before_dragging.get_event_renderer().set_visible(false);
			dragging_event.changed();
		}
	}


	/**
	 *  Rebuild the list of visible event renderer in the current<br>
	 *  visible space of time.
	 */
	public void rebuilt_visible_event_renderer_list() {
		rebuilt_visible_event_renderer_list = true;
	}


	/**
	 *  Gets the first_rendered_hour_UTC_ms attribute of the DateLookPanel object
	 *
	 * @return    The first_rendered_hour_UTC_ms value
	 */
	public long get_first_rendered_hour_UTC_ms() {
		return first_rendered_hour_UTC_ms;
	}


	/**
	 *  Set set_first_rendered_hour_UTC_ms attribute
	 *
	 * @param  d  the value
	 */
	public void set_first_rendered_hour_UTC_ms(long d) {
		rebuilt_visible_event_renderer_list = true;

		// prevent that time is visible which is out of range 01.01.01 00:00 to 01.01.2500 00:00
		first_rendered_hour_UTC_ms =
				Math.min(Math.max(first_rendered_hour_UTC_ms_min, d),
						last_rendered_UTC_ms_max - number_of_rendered_hours * 60L * 60L * 1000L);
	}


	/**
	 *  Gets the number_of_rendered_hours attribute of the DateLookPanel object
	 *
	 * @return    The number_of_rendered_hours value
	 */
	public long get_number_of_rendered_hours() {
		return number_of_rendered_hours;
	}


	/**
	 *  Set number of renderer hours.
	 *
	 * @param  i  number of renderer hours
	 */
	public void set_number_of_rendered_hours(int i) {
		rebuilt_visible_event_renderer_list = true;

		// prevent that time is visible which is out of range 01.01.01 00:00 to 01.01.2500 00:00
		number_of_rendered_hours = Math.min(Math.min(Math.max(rendered_hours_min, i), rendered_hours_max),
				(last_rendered_UTC_ms_max - first_rendered_hour_UTC_ms) / (60L * 60L * 1000L));
	}


	/**
	 *  Sets number of rows
	 *
	 * @param  n  new number of rows
	 */
	public void set_row_number(int n) {
		int old_slot_numer = number_of_slots;
		//number_of_slots = n + num_ligne_top_description;
		int delta_slot_numer = old_slot_numer - number_of_slots;
		main_frame_height_before_ext_view = main_frame_height_before_ext_view - delta_slot_numer * slot_height;
		main_frame.setSize(main_frame.getWidth(), main_frame.getHeight() - delta_slot_numer * slot_height);
		main_frame.paintAll(main_frame.getGraphics());
	}

	/**
	 *  Indicate to the DateLookPanel that at least one event has been changed.<br>
	 *  The panel will be repainted and the visible_event_renderer_list will be rebuilt.
	 */
	public void changed() {
		// called if an event has been changed
		if (dragging_event == null) {
			rebuilt_visible_event_renderer_list = true;
		}
		this.repaint();
	}


	/**
	 *  Give coordinates of free space to render the event's description on descriptions renderer
	 *
	 * @param  x_rectangle  x position of the event's rectangle
	 * @param  width        width of event's description
	 * @return              x coordinate of free space in main window
	 */
	public int get_free_space_X(int x_rectangle, int width) {

		int description_slot = 0;
		int panel_width = this.getWidth();
		while (description_slot < 21 &&
				free_x[description_slot] < x_rectangle + width + space_between_date_descriptions &&
				free_x[description_slot] < panel_width) {
			description_slot++;
		}
		required_description_renderer_height = Math.max(required_description_renderer_height,
				Math.min(21, (description_slot + 1)) * descriptions_slot_height);
		if (description_slot == 21) {
			// search for slot with the most free space
			int best_description_slot = 0;
			for (int i = 1; i < 21; i++) {
				if (free_x[best_description_slot] < free_x[i]) {
					best_description_slot = i;
				}
			}
			free_space_y = y_description_slot0 + best_description_slot * descriptions_slot_height;
			x_rectangle = free_x[best_description_slot] - width - space_between_date_descriptions;
			free_x[best_description_slot] = x_rectangle;
			return x_rectangle;
		}
		else {
			if (x_rectangle + width + space_between_date_descriptions > panel_width) {
				x_rectangle = panel_width - width - space_between_date_descriptions;
			}
			x_rectangle = Math.max(x_rectangle, space_between_date_descriptions);
			free_x[description_slot] = x_rectangle - space_between_date_descriptions;
			free_space_y = y_description_slot0 + description_slot * descriptions_slot_height;
			return x_rectangle;
		}
	}


	/**
	 *  Gets the free_space_Y attribute of the DateLookPanel object
	 *
	 * @return    The free_space_Y value
	 */
	public int get_free_space_Y() {
		// must be called immediately after get_free_space_X()
		return free_space_y;
	}


	/**
	 *  Set all space of descriptions renderer to empty
	 */
	private void reset_space_map() {
		int panel_width = this.getWidth();
		for (int i = 0; i < 21; i++) {
			free_x[i] = panel_width;
		}
		space_between_date_descriptions = this.getWidth() / 100;
		y_description_slot0 = (slot_height * number_of_slots) + descriptions_slot_height / num_ligne_top_description;
	}

	/**
	 *  Row label renderer
	 */
	public class RowLabels {
		private boolean visible = true;
		private boolean parent_panel_resized = true;
		private Font font;  // font of the row label
		private int height; // height of a row label
		private int space; // space between label border and text


		/**
		 *  Constructor for the RowLabels object
		 *
		 */
		public RowLabels() {
			visible = Settings.get_instance(null).get_show_row_labels();
		}


		/**
		 *  Draw the row names
		 *
		 * @param  g2  graphics object
		 */
		public void draw(Graphics2D g2, DateLookPanel panel) {
			if (visible) {
				if (parent_panel_resized) {
					font = my_calendar.get_font();
					height = slot_height * num_ligne_top_description / 6;
					space = (int) font.getStringBounds("0", g2.getFontRenderContext()).getWidth();
				}
				for (int i = 0; i < number_of_slots; i++) {
					String label = Settings.get_instance(null).get_row_label(i);
					if (label.length() != 0) {
						Rectangle2D bounds = font.getStringBounds(label, g2.getFontRenderContext());
						int width = (int) bounds.getWidth() + space * 2;
						int y = (num_ligne_top_description + i) * slot_height + (slot_height - height) / 2;
						int x = space;
						if ((mouse_x < width + num_ligne_top_description) && (mouse_y > (num_ligne_top_description + i) * slot_height) && (mouse_y < ((21 + i) * slot_height))) {
							x = space + width;
						}

						g2.setFont(font);
						g2.setColor(Color.white);
						g2.fillRoundRect(x, y, width, height, height, height);
						g2.setColor(Color.black);
						g2.drawRoundRect(x, y, width, height, height, height);
						g2.drawString(label, x + space, y - (int) bounds.getY());
					}
				}
			}
		}


		/**
		 *  Set visible
		 *
		 * @param  v  visible
		 */
		public void set_visible(boolean v) {
			visible = v;
		}


		/**
		 *  Get visible
		 *
		 * @return  visible
		 */
		public boolean get_visible() {
			return visible;
		}


		/**
		 *  Parent panel resized
		 *
		 */
		public void parent_panel_resized() {
			parent_panel_resized = true;
		}
	}


	public String getDeltaT() {
		return deltaT;
	}


	public void setDeltaT(String deltaT) {
		this.deltaT = deltaT;
	}



	public static String getLockName()
	{
		return lockName;
	}



	public void setLockName(String lockName)
	{
		this.lockName = lockName;
	}



	public static String getLockDate()
	{
		return lockDate;
	}



	public void setLockDate(String lockDate)
	{
		this.lockDate = lockDate;
	}



	public int getNum_ligne_top_description()
	{
		return num_ligne_top_description;
	}



	public static void setNum_ligne_top_description(int numLigneTopDescription)
	{
		num_ligne_top_description = numLigneTopDescription;
	}



	public int getNumber_of_slots()
	{
		return number_of_slots;
	}



	public static void setNumber_of_slots(int numberOfSlots)
	{
		number_of_slots = numberOfSlots;
	}


	public void setMouse_over_event_renderer(EventRenderer mouse_over_event_renderer)
	{
		this.mouse_over_event_renderer = mouse_over_event_renderer;
	}


	public EventRenderer getMouse_over_event_renderer()
	{
		return mouse_over_event_renderer;
	}


	public void setMf(DateLook mf)
	{
		this.mf = mf;
	}


	public DateLook getMf()
	{
		return mf;
	}


	public void setLast_begin_UTC_ms(long last_begin_UTC_ms)
	{
		this.last_begin_UTC_ms = last_begin_UTC_ms;
	}


	public long getLast_begin_UTC_ms()
	{
		return last_begin_UTC_ms;
	}



	public long getLast_end_UTC_ms()
	{
		return last_end_UTC_ms;
	}



	public void setLast_end_UTC_ms(long lastEndUTCMs)
	{
		last_end_UTC_ms = lastEndUTCMs;
	}


	public void setEd(EditorFrame editorFrameRe)
	{
		this.ed = editorFrameRe;
	}


	public EditorFrame getEd()
	{
		return ed;
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

