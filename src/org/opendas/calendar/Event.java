package org.opendas.calendar;
import java.awt.*;
import java.util.*;
import java.io.*;


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
 *  Describes an event.
 */ 
public class Event implements Comparable<Event> {

  private String UID;  // Unique Identifier (vCalendar 1.0)
  private long begin_UTC_ms;
  private long end_UTC_ms;
  private long alarm_UTC_ms;
 
  @Override
public String toString()
{
	return "Event [UID=" + UID + ", begin_UTC_ms=" + begin_UTC_ms + ", code=" + code + ", dateLookInstance=" + dateLookInstance + ", end_UTC_ms=" + end_UTC_ms + ", id=" + id + ", init_begin_UTC_ms=" + init_begin_UTC_ms + ", init_end_UTC_ms=" + init_end_UTC_ms + ", init_name=" + init_name + ", isSelected=" + isSelected + ", last_mod_UTC_ms=" + last_mod_UTC_ms + ", my_editor_frame=" + my_editor_frame + ", my_renderer=" + my_renderer + ", renderer_color=" + renderer_color + ", renderer_group=" + renderer_group + ", summary=" + summary + ", summary_encoding=" + summary_encoding + "]";
}


private long last_mod_UTC_ms;
  private int period = this.None;
  private int id = -1;
  private String code;
  private long init_begin_UTC_ms = 0;
  private long init_end_UTC_ms = 0;
  private String init_name;
  private DateLook dateLookInstance;
  
  


public DateLook getDateLookInstance()
{
	return dateLookInstance;
}





public void setDateLookInstance(DateLook dateLookInstance)
{
	this.dateLookInstance = dateLookInstance;
}




public String getInit_name()
{
	return init_name;
}




public void setInit_name(String initName)
{
	init_name = initName;
}



public long getInit_begin_UTC_ms()
{
	return init_begin_UTC_ms;
}



public void setInit_begin_UTC_ms(long initBeginUTCMs)
{
	init_begin_UTC_ms = initBeginUTCMs;
}



public long getInit_end_UTC_ms()
{
	return init_end_UTC_ms;
}



public void setInit_end_UTC_ms(long initEndUTCMs)
{
	init_end_UTC_ms = initEndUTCMs;
}


/**
   *  Events cycle None
   */
  public final static int None = 0;
  
  /**
   *  Events cycle Daily
   */
  public final static int Daily = 1;
  
  /**
   *  Events cycle Weekly
   */
  public final static int Weekly = 2;
  
  /**
   *  Events cycle Monthly
   */
  public final static int Monthly = 3;
  
  /**
   *  Events cycle Yearly
   */
  public final static int Yearly = 4;

  private int period_multiplier = 1;  // to provide the possiblility for biweekly or every other day..

  /**
   *  Events class Public
   */
  public final static int Public = 0;
  
  /**
   *  Events class Private
   */
  public final static int Private = 1;

  private int number_of_periods = 2;
  private int alarm_counter = 0;
  private boolean alarm_active = false;

  private int renderer_group;  // line in panel where it is rendered
  private Color renderer_color;
  private String summary = "new event";
  private String summary_encoding;  // encoding of summary e.g. "ISO-8859-15"
  private String description = "";
  private String description_encoding;
  private EventRenderer my_renderer;
  private EditorFrame my_editor_frame = null;
  private boolean isSelected = false;
  
  
  
  public boolean isSelected() {
	return isSelected;
}


public void setSelected(boolean isSelected) {
	this.isSelected = isSelected;
}


// temporary fields
  private boolean now_imported = false;  // to remeber that the event is imported/undeleted now
  private boolean deleted_while_import = false; // indicates that event was deleted while last import/sync of events
  
  /**
   *  Events class Public or Private.<br>
   *  Public - will be exchanged with remote database if synchronizing.<br>
   *  Private - will NOT be exchanged with remote database if synchronizing.
   */
  private int vcal_class = this.Public;


  /**
   *  Constructor for the Event object
   *
   * @param  b   Begin time UTC in ms.
   * @param  rg  Renderer group.<br>
   *             Determines the row in main window where the events rectangle is drawn.
   */
  public Event(long b, int rg) {
    generate_UID();
    last_mod_UTC_ms = new GregorianCalendar().getTime().getTime();  // is set to now
    /*b= b (5L * 60L * 1000L) * (5L * 60L * 1000L);*/   //set in 5 min steps
    begin_UTC_ms = b;
    end_UTC_ms = b + 60L * 60L * 1000L;  // one hour duration for default
    alarm_UTC_ms = b;
    renderer_group = rg;
    renderer_color = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
  }

  /**
   *  Constructor for the Event object
   *
   */
  public Event() {
    generate_UID();
    last_mod_UTC_ms = new GregorianCalendar().getTime().getTime();   // is set to now
    renderer_group = 0;
    init_begin_UTC_ms = 0;
    init_end_UTC_ms = 0;
    renderer_color = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
    summary_encoding = new OutputStreamWriter(new ByteArrayOutputStream()).getEncoding();
    description_encoding = new OutputStreamWriter(new ByteArrayOutputStream()).getEncoding();
  }


  /**
   *  Compare to another event.<br>
   *  The criteria is the events begin time only.
   *
   * @param  o  other event.
   * @return    0 - if both begin time are equal<br>
   *            1 - if own begin time is later than the other<br>
   *           -1 - if own begin time is earlier than the other
   */
  public int compareTo(Event o) {
    if (this.begin_UTC_ms >  ((Event) o).begin_UTC_ms) {
      return 1;
    }
    if (this.begin_UTC_ms == ((Event) o).begin_UTC_ms) {
      return 0;
    }
    return -1;
  }


  /**
   *  Set summary
   *
   * @param  s  summary
   */
  public  void set_summary(String s) {
    summary = s;
  }


  /**
   *  Get summary
   *
   * @return    summary
   */
  public  String get_summary() {
    return summary;
  }


  /**
   *  Set summary encoding (e.g. "ISO-8859-15").
   *
   * @param  s  summary encoding
   */
  public  void set_summary_encoding(String s) {
    if (s.length() == 0) {  // if string is empty the operating system's default charset is used
      summary_encoding = dateLookInstance.eventMemory.default_encoding;  // get os default charset
    }
    else {
      summary_encoding = s;
    }
  }


  /**
   *  Get summaries encoding (e.g. "ISO-8859-15").
   *
   * @return    summaries encoding
   */
  public  String get_summary_encoding() {
    return summary_encoding;
  }


  /**
   *  Set description
   *
   * @param  s  description.
   */
  public  void set_description(String s) {
    description = s;
  }


  /**
   *  Get description
   *
   * @return    description
   */
  public  String get_description() {
    return description;
  }


  /**
   *  Set description encoding.
   *
   * @param  s  description encoding (e.g. "ISO-8859-15")
   */
  public  void set_description_encoding(String s) {
    if (s.length() == 0) {  // if string is empty the operating system's default charset is used
      description_encoding = dateLookInstance.eventMemory.default_encoding;   // get os default charset
    }
    else {
      description_encoding = s;
    }
  }


  /**
   *  Get description encoding.
   *
   * @return    description encoding (e.g. "ISO-8859-15")
   */
  public  String get_description_encoding() {
    return description_encoding;
  }


  /**
   *  Set begin time in UTC (ms).
   *
   * @param  l  begin time in UTC (ms)
   */
  public  void set_begin_UTC_ms(long l) {
    begin_UTC_ms = l /*/ (5L * 60L * 1000L) * (5L * 60L * 1000L)*/;   // set in 5 min steps
  }


  /**
   *  Set UID.
   *
   * @param  s  UID
   */
  public  void set_UID(String s) {
    UID = s;
  }


  /**
   *  Get UID.
   *
   * @return    UID
   */
  public  String get_UID() {
    return UID;
  }


  /**
   *  Set last modified time in UTC (ms).
   *
   * @param  l  last modified time in UTC (ms)
   */
  public  void set_last_mod_UTC_ms(long l) {
    last_mod_UTC_ms = l / 1000L * 1000L;   // in 1 sec steps
  }


  /**
   *  Get last modified time in UTC (ms).
   *
   * @return    last modified time in UTC (ms)
   */
  public  long get_last_mod_UTC_ms() {
    return last_mod_UTC_ms;
  }


  /**
   *  Get begin time in UTC (ms).
   *
   * @return    begin time in UTC (ms)
   */
  public  long get_begin_UTC_ms() {
    return begin_UTC_ms;
  }


  /**
   *  Set end time in UTC (ms).
   *
   * @param  l  UTC in ms
   */
  public  void set_end_UTC_ms(long l) {
    end_UTC_ms = l /*/ (5 * 60 * 1000) * (5 * 60 * 1000)*/;   // set in 5 min steps
  }


  /**
   *  Get end time in UTC (ms).
   *
   * @return    end time in UTC (ms)
   */
  public  long get_end_UTC_ms() {
    return end_UTC_ms;
  }


  /**
   *  Set alarm time in UTC (ms).
   *
   * @param  l  alarm time in UTC (ms)
   */
  public  void set_alarm_UTC_ms(long l) {
    alarm_UTC_ms = l / (5L * 60L * 1000L) * (5L * 60L * 1000L);  // set in 5 min steps
  }


  /**
   *  Get alarm time in UTC (ms).
   *
   * @return    alarm time in UTC (ms)
   */
  public  long get_alarm_UTC_ms() {
    return alarm_UTC_ms;
  }


  /**
   *  Set vcal class
   *
   * @param  i  Public or Private
   */
  public  void set_vcal_class(int i) {
    vcal_class = Math.min(Math.max(i, Public), Private);
  }


  /**
   *  Get vcal class
   *
   * @return    Public or Private
   */
  public  int get_vcal_class() {
    return vcal_class;
  }


  /**
   *  Set period.
   *
   * @param  i  period (range: None, Daily, Weekly, Monthly or Yearly)
   */
  public  void set_period(int i) {
    period = Math.min(Math.max(i, None), Yearly);
  }


  /**
   *  Get period
   *
   * @return    period
   */
  public  int get_period() {
    return period;
  }


  /**
   *  Get period as string
   *
   * @return    period as string (e.g. weekly, daily,...)
   */
  public  String get_period_as_string() {
    String pms = "";
    if (period_multiplier != 1) {
      pms = Integer.toString(period_multiplier) + "-";
    }
    if (period == None) {
      return "once";
    }
    else if (period == Daily) {
      return pms + "daily";
    }
    else if (period == Weekly) {
      return pms + "weekly";
    }
    else if (period == Monthly) {
      return pms + "monthly";
    }
    return pms + "yearly";
  }


  /**
   *  Set number of periods.
   *
   * @param  i  number of periods
   */
  public  void set_number_of_periods(int i) {
    number_of_periods = Math.min(Math.max(i, 2), 999);
  }


  /**
   *  Get number of periods.
   *
   * @return    number of periods
   */
  public  int get_number_of_periods() {
    return number_of_periods;
  }


  /**
   *  Set period multiplier.
   *
   * @param  i  period multiplier
   */
  public  void set_period_multiplier(int i) {
    period_multiplier = Math.min(Math.max(i, 1), 9);
  }


  /**
   *  Get period multiplier.
   *
   * @return    period multiplier
   */
  public  int get_period_multiplier() {
    return period_multiplier;
  }


  /**
   *  Set alarm counter.
   *
   * @param  i  alarm counter
   */
  public  void set_alarm_counter(int i) {
    alarm_counter = Math.max(i, 0);
  }


  /**
   *  Get alarm counter.
   *
   * @return    alarm counter
   */
  public  int get_alarm_counter() {
    return alarm_counter;
  }


  /**
   *  Increase alarm counter.
   */
  public  void inc_alarm_counter() {
    alarm_counter++;
  }


  /**
   *  Set renderer group.<br>
   *  Determines the rows where the event is drawn in the main window.
   *
   * @param  i  renderer group (range 0 - 29)
   */
  public  void set_renderer_group(int i) {
    renderer_group = Math.max(i, 0);
  }


  /**
   *  Get renderer group.<br>
   *  Indicates the rows where the event is drawn in the main window.
   *
   * @return    renderer group (range 0 - 29)
   */
  public  int get_renderer_group() {
    return renderer_group;
  }


  /**
   *  Get renderer colour
   *
   * @return    colour of that event
   */
  public  Color get_renderer_color() {
    return renderer_color;
  }


  /**
   *  Set renderer colour
   *
   * @param  c  colour
   */
  public  void set_renderer_color(Color c) {
    renderer_color = c;
  }


  /**
   *  Set "alarm active"-flag
   *
   * @param  b  true - set alarm active.<br>
   *            false - set alarm inactive.
   */
  public  void set_alarm_active(boolean b) {
    alarm_active = b;
  }


  /**
   *  Get "alarm active"-flag
   *
   * @return    "alarm active"-flag
   */
  public  boolean get_alarm_active() {
    return alarm_active;
  }


  /**
   *  Get my editor frame.
   *
   * @return    my editor frame or null.
   */
  public  EditorFrame get_my_editor_frame() {
    return my_editor_frame;
  }


  /**
   *  Set my editor frame.<br>
   *  Sets the focus to the renderer if ef != null or otherwise.<br>
   *  removes the focus from the renderer.
   *
   * @param  editorFrameRe  editor frame or null
   */
  public  void set_my_editor_frame(EditorFrame editorFrameRe) {
    my_editor_frame = editorFrameRe;
    if (my_renderer != null) {
      if (editorFrameRe != null) {
        my_renderer.set_focus(true);
      }
      else {
        my_renderer.set_focus(false);
      }
    }
  }

  /**
   *  Sets the event_renderer
   *
   * @param  tr  The new event_renderer value
   */
  public  void set_event_renderer(EventRenderer tr) {
    my_renderer = tr;
  }


  /**
   *  Get my event renderer.
   *
   * @return    my event renderer
   */
  public  EventRenderer get_event_renderer() {
    return my_renderer;
  }


  /**
   *  Set "now imorted"-flag.
   *
   * @param  i  true - is now imorted.<br>
   *            false - is not now imorted.
   */
  public  void set_now_imported(boolean i) {
    now_imported = i;
  }


  /**
   *  Get "now imorted"-flag.
   *
   * @return    now imorted
   */
  public  boolean get_now_imported() {
    return now_imported;
  }


  /**
   *  Set "deleted_while_import"-flag.
   *
   * @param  i  true - is deleted while import.<br>
   *            false - is not deleted while import.
   */
  public  void set_deleted_while_import(boolean i) {
    deleted_while_import = i;
  }


  /**
   *  Get "deleted_while_import"-flag.
   *
   * @return    deleted_while_import
   */
  public  boolean get_deleted_while_import() {
    return deleted_while_import;
  }


  /**
   *  Changed.<br>
   *  Sets "last modified time" to now and informs its renderer and memory.<br>
   *  Must be called always after changing data of the event.
   */
  public void changed() {
    set_last_mod_UTC_ms(new GregorianCalendar().getTime().getTime());
    EventRenderer.changed();
  }


  /**
   *  Deleted.<br>
   *  Called to indicate that this event has been deleted<br>
   *  in EventMemory
   */
  // it is called only by the EventMemory
  public  void deleted() {
	  set_last_mod_UTC_ms(new GregorianCalendar().getTime().getTime());
    if ( my_editor_frame != null) {
      my_editor_frame.dispose();
      my_editor_frame = null;
    }

    EventRenderer.changed();
    my_renderer = null;
  }


  /**
   *  Clone event.<br>
   *  But UID is a new one in the cloned event.
   *
   * @return    the cloned event
   */
  public  Event clone2() {
    Event t = new Event();
    t.setCode(code);
    t.setId(id);
    t.set_alarm_active(alarm_active);
    t.set_alarm_counter(alarm_counter);
    t.set_alarm_UTC_ms(alarm_UTC_ms);
    t.set_begin_UTC_ms(begin_UTC_ms);
    t.set_end_UTC_ms(end_UTC_ms);
    t.setInit_begin_UTC_ms(init_begin_UTC_ms);
    t.setInit_end_UTC_ms(init_end_UTC_ms);
    t.setInit_name(init_name);
    t.set_period(period);
    t.set_period_multiplier(period_multiplier);
    t.set_number_of_periods(number_of_periods);
    t.set_renderer_color(renderer_color);
    t.set_renderer_group(renderer_group);
    t.set_summary(summary);
    t.set_description(description);
    t.set_last_mod_UTC_ms(last_mod_UTC_ms);
    t.set_description_encoding(description_encoding);
    t.set_summary_encoding(summary_encoding);
    t.set_vcal_class(vcal_class);
    return t;
  }


  /**
   *  Set alarm counter to next after now
   */
  public  void set_alarm_counter_to_next_after_now() {
    long now_ms = new GregorianCalendar().getTime().getTime();
    alarm_counter = (int) Math.max(
        ((now_ms - alarm_UTC_ms) / Converter.period2ms(period, period_multiplier)) - 1, 0);
    while (!(Converter.UTCplusPeriod2UTC(alarm_UTC_ms, period, alarm_counter, period_multiplier) > now_ms)) {
      alarm_counter++;
    }
  }


  /**
   *  Generate UID
   */
  private void generate_UID() {
    UID = "datelook.de-" + Double.toString(Math.random()).substring(2) + "-" + Long.toString(new GregorianCalendar().getTime().getTime());  
  }

public void setId(int i)
{
	this.id = i;
}


public int getId()
{
	return this.id;
}




public String getCode()
{
	return code;
}




public void setCode(String code)
{
	this.code = code;
}


}

