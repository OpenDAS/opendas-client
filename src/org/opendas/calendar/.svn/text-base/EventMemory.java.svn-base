package org.opendas.calendar;
import java.util.*;
import java.io.*;
import javax.swing.filechooser.*;
import javax.swing.*;

import org.opendas.DASLog;

import java.text.*;


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
 *  Memory that stores all events (deleted and present events)
 */
public class EventMemory {

  /**
   *  encoding of the used operating system e.g. "ISO-8859-15"
   */
  public final String default_encoding = new OutputStreamWriter(new ByteArrayOutputStream()).getEncoding();  // get OS default charset;

  private ArrayList<ArrayList<Event>> evenList = new ArrayList<ArrayList<Event>>();
  private int evenListEnCours = 0;
  private ArrayList<Event> deleted_event_list = new ArrayList<Event>();  // stores the deleted events for sync

  private String data_file_name;    // file to store events in vCalendar 1.0-format (local database)
  private String backup_file_name;  // file to backup events at start time of DateLook
  private String lock_file_name;    // file that indicates, that the data_file_name is locked for other 
                                    // datelook instances

  private DateLook	dateLookInstance;

  
  /**
   *  Get the instance of the event memory<br>
   *  or create an instance if there is none.
   *
   * @param  dp       datelook panel displaying the data in event memory<br>
   *                  (only used at first call)
   * @param  df_name  filename of datafile or null for default<br>
   *                  (only used at first call)
   */
//  public static EventMemory get_instance(String df_name) {
//    if (instance == null) {
//      instance = new EventMemory(df_name);
//    }
//    return instance;
//  }

	public EventMemory(ArrayList<ArrayList<Event>> eventList, DateLook dateLookInstance){
		this.dateLookInstance = dateLookInstance;
		this.setEvent_list(eventList);
		for(ArrayList<Event> i : this.evenList){
			for(Event y : i){
				y.setDateLookInstance(dateLookInstance);
			}
		}
	}

  /**
   *  Constructor for the EventMemory object.
   *
   * @param  df_name  filename of datafile or null for default
   */
  public EventMemory(String df_name) {
    if (df_name == null) {
      data_file_name = FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath()
         + File.separatorChar + ".datelook" + File.separatorChar + "dates.vcs";
    }
    else {
      data_file_name = df_name;
    }
    lock_file_name = data_file_name + ".locked";
    backup_file_name = data_file_name + ".bak";
    //System.out.println("Local data base: '" + data_file_name + "'");
  }

  
  /**
   *  Read the database from predefined file and make a backup of that file (.bak).
   *
   * @return true - file could be read<br>
   *         false - file is locked by another datelook-instance
   */
  public  boolean read_data_file() {
        return false;
  }


  /**
   *  Save all events to predefined file.
   *
   * @param  unlook if true the database file will be unlocked for other datelook instances<br>
   *                if false it remains locked.
   */
  public  void save(boolean unlock) {
    try {
      String tmp_file_name = data_file_name + "." + (new Date()).getTime() + ".tmp";
      File tmp_file = new File(tmp_file_name);
      int[] i = new int[evenList.get(evenListEnCours).size()];
      
      for (int k = 0; k < i.length; k++) {
        i[k] = k;  // array for all events built
      }
      
      export_vCalendar(tmp_file, i, true, null, false);

      if (tmp_file.canWrite()) {
        File data_file = new File(data_file_name);
        data_file.delete();
        tmp_file.renameTo(data_file);
      }
      
      if (unlock) {
        // first delete backup file
        File backup_file = new File(backup_file_name);
        backup_file.delete();
        
        // unlock database
        File lock_file = new File(lock_file_name);
        if (lock_file.delete() != true) {
          logDebug("Local data base: '" + data_file_name + "'" + "can't be UNLOCKED!");
        }
      }
    }
    catch (Exception b) {
      b.printStackTrace();
    }
  }


  /**
   *  Revert to the backup database (*.vcs.bak)<br>
   *  (rename file *.vcs.bak to *.vcs) and exit.
   */
  public  void revert_and_exit() {
    File backup_file = new File(backup_file_name);
    if (backup_file.canWrite()) {
      File data_file = new File(data_file_name);
      data_file.delete();
      backup_file.renameTo(data_file);
    }
    
    // unlock database
    File lock_file = new File(lock_file_name);
    if (lock_file.delete() != true) {
      logDebug("Local data base: '" + data_file_name + "'" + "can't be UNLOCKED!");
    }
    System.exit(0);
  }


//  /**
//   *  Import events from an input stream reader.
//   *
//   * @param  isr            input stream reader
//   * @param  fl             length of data in byte
//   * @param  import_deleted import deleted events too (vCalendar-property: "X-DTLK-DELETED:TRUE")
//   * @param  pb             progress bar
//   * @exception  Exception  -
//   */
//  public  void import_vCalendar(InputStreamReader isr, long fl, boolean import_deleted, JProgressBar pb) throws Exception {
//    boolean begin_found = false;
//    boolean vcalender_version_ok = false;
//    final long file_length = fl;
//    final JProgressBar progress_bar = pb;
//    if (progress_bar != null) {
//      progress_bar.setMaximum(1000);
//      progress_bar.setValue(0);
//    }
//
//    BufferedReader b_file_reader =
//      new BufferedReader(isr) {
//        // readLine() is overidden with new readLine() that unfolds lines
//        // according to two different methods: remove CRLF + LWSP or
//        // if it is a property value coded with quoted-printable: remove '=' + CRLF
//        long read_bytes = 0;
//
//
//        public String readLine() throws IOException {
//          String s = super.readLine();
//          if (s == null) {
//            return null;
//          }
//          boolean line_unfolding_quoted_printable = false;
//          if (s.toUpperCase().indexOf("QUOTED-PRINTABLE") < s.indexOf(":") &&
//              s.toUpperCase().indexOf("QUOTED-PRINTABLE") > 0) {
//            line_unfolding_quoted_printable = true;
//          }
//
//          mark(100);
//          while (true) {
//            String s2 = super.readLine();
//            if (s2 == null || s2.length() == 0) {
//              return s;
//            }
//            if (s2.charAt(0) == ' ' & !line_unfolding_quoted_printable | s.endsWith("=") & line_unfolding_quoted_printable) {
//              mark(100);
//              if (line_unfolding_quoted_printable) {
//                s = s.substring(0, s.length() - 1) + s2;
//              }
//              else {
//                s = s + s2.substring(1);
//              }
//              read_bytes = read_bytes + 3;
//            }
//            else {
//              reset();
//              break;
//            }
//          }
//          if (progress_bar != null) {
//            read_bytes = read_bytes + s.length() + 2;
//            progress_bar.setValue((int) ((read_bytes * 1000L) / file_length));
//          }
//          return s;
//        }
//      };
//
//    // read line by line.
//    while (true) {
//      String line = b_file_reader.readLine();
//      if (line == null) {
//        break;
//      }
//      else if (!begin_found && line.toUpperCase().compareTo("BEGIN:VCALENDAR") == 0) {
//        begin_found = true;
//      }
//      else if (begin_found && !vcalender_version_ok && line.toUpperCase().startsWith("VERSION:")) {
//        if (line.substring(8).trim().compareTo("1.0") != 0) {
//          break;
//        }
//        else {
//          vcalender_version_ok = true;
//        }
//      }
//      else if (vcalender_version_ok &&
//          line.trim().toUpperCase().compareTo("BEGIN:VEVENT") == 0 ||
//          line.trim().toUpperCase().compareTo("BEGIN:VTODO") == 0) {
//        // handle VEVENT
//        Event t = new Event();
//        t.set_period(Event.None);
//        t.set_vcal_class(Event.Public);
//        t.set_summary("");
//        t.set_description("");
//        boolean deleted_event = false;
//        boolean summary_found = false;
//        boolean dtstart_found = false;
//        boolean dtend_found = false;
//        boolean x_alarmcounter_found = false;
//        boolean last_modified_found = false;
//        while (line.trim().toUpperCase().compareTo("END:VEVENT") != 0 &&
//            line.trim().toUpperCase().compareTo("END:VTODO") != 0) {
//          line = b_file_reader.readLine();
//          if (line == null) {
//            break;
//          }
//          else if (line.toUpperCase().startsWith("DTSTART")) {
//            Long l = Converter.dtstart2UTC(line.substring(line.indexOf(":") + 1));
//            if (l != null) {
//              t.set_begin_UTC_ms(l.longValue());
//              t.set_alarm_UTC_ms(l.longValue());
//              dtstart_found = true;
//            }
//          }
//          else if (line.toUpperCase().startsWith("DTEND")) {
//            Long l = Converter.dtstart2UTC(line.substring(line.indexOf(":") + 1));
//            if (l != null) {
//              t.set_end_UTC_ms(l.longValue());
//              dtend_found = true;
//            }
//          }
//          else if (line.toUpperCase().startsWith("DALARM")) {
//            Long l = Converter.dtstart2UTC(line.substring(line.indexOf(":") + 1));
//            if (l != null) {
//              t.set_alarm_UTC_ms(l.longValue());
//              t.set_alarm_active(true);
//            }
//          }
//          else if (line.toUpperCase().startsWith("RRULE")) {
//            int idp = line.indexOf(":");
//            if (line.indexOf("YM") > idp && idp > 0) {
//              t.set_period(Event.Yearly);
//              t.set_period_multiplier(Converter.stringNumberBehindPos2int(line, line.indexOf("YM") + 2));
//            }
//            else if (line.indexOf("MD") > idp && idp > 0) {
//              t.set_period(Event.Monthly);
//              t.set_period_multiplier(Converter.stringNumberBehindPos2int(line, line.indexOf("MD") + 2));
//            }
//            else if (line.indexOf("W") > idp && idp > 0) {
//              t.set_period(Event.Weekly);
//              t.set_period_multiplier(Converter.stringNumberBehindPos2int(line, line.indexOf("W") + 1));
//            }
//            else if (line.indexOf("D") > idp && idp > 0) {
//              t.set_period(Event.Daily);
//              t.set_period_multiplier(Converter.stringNumberBehindPos2int(line, line.indexOf("D") + 1));
//            }
//            if (t.get_period() != Event.None) {
//              if (line.indexOf(" #") > line.indexOf(":")) {
//                int period = Integer.parseInt(line.substring(line.indexOf("#") + 1).trim());
//                if (period == 0) {
//                  period = 999; // 999 is used internally for unlimited
//                }
//                t.set_number_of_periods(period);
//              }
//              else {
//                t.set_number_of_periods(2);
//              }
//            }
//          }
//          else if (line.toUpperCase().startsWith("UID")) {
//            t.set_UID(line.substring(line.indexOf(":") + 1).trim());
//          }
//          else if (line.toUpperCase().startsWith("LAST-MODIFIED")) {
//            Long l = Converter.dtstart2UTC(line.substring(line.indexOf(":") + 1));
//            if (l != null) {
//              t.set_last_mod_UTC_ms(l.longValue());
//            }
//            last_modified_found = true;
//          }
//          else if (line.toUpperCase().startsWith("CLASS")) {
//            if (line.substring(line.indexOf(":") + 1).toUpperCase().trim().equals("PUBLIC")) {
//              t.set_vcal_class(Event.Public);
//            }
//            else {
//              t.set_vcal_class(Event.Private);
//            }
//          }
//          else if (line.toUpperCase().startsWith("SUMMARY")) {
//            // determine CHARSET
//            t.set_summary_encoding(Converter.getEncodingfromLine(line));
//            // ckeck for quoted-printable
//            boolean qt = false;
//            if (line.toUpperCase().indexOf("QUOTED-PRINTABLE") < line.indexOf(":") && line.toUpperCase().indexOf("QUOTED-PRINTABLE") > 0) {
//              qt = true;
//            }
//            t.set_summary(Converter.byte2unicode(line.substring(line.indexOf(":") + 1), t.get_summary_encoding(), qt));
//            summary_found = true;
//          }
//          else if (line.toUpperCase().startsWith("DESCRIPTION")) {
//            // determine CHARSET
//            t.set_description_encoding(Converter.getEncodingfromLine(line));
//            // ckeck for quoted-printable
//            boolean qt = false;
//            if (line.toUpperCase().indexOf("QUOTED-PRINTABLE") < line.indexOf(":") && line.toUpperCase().indexOf("QUOTED-PRINTABLE") > 0) {
//              qt = true;
//            }
//            t.set_description(Converter.byte2unicode(line.substring(line.indexOf(":") + 1), t.get_description_encoding(), qt));
//          }
//          else if (line.toUpperCase().startsWith("X-DTLK-COLOUR:") && line.length() > 22) {
//            t.set_renderer_color(new Color(Integer.parseInt(line.substring(14, 17)),
//                Integer.parseInt(line.substring(17, 20)),
//                Integer.parseInt(line.substring(20, 23))));
//          }
//          else if (line.toUpperCase().startsWith("X-DTLK-GROUP:")) {
//            t.set_renderer_group(Integer.parseInt(line.substring(13).trim()));
//          }
//          else if (line.toUpperCase().startsWith("X-DTLK-ALARMCOUNTER:")) {
//            t.set_alarm_counter(Integer.parseInt(line.substring(20).trim()));
//            x_alarmcounter_found = true;
//          }
//          else if (line.toUpperCase().startsWith("X-DTLK-DELETED:TRUE")) {
//            deleted_event = true;
//          }
//        }
//
//        // add new event to event- or if reqired to deleted-event-list
//        if (summary_found & dtstart_found & dtend_found & !(!import_deleted & deleted_event)) {
//          // if an alarmcounter found in event then use this, otherwise
//          // calculate a value by own so that all alarms in past are marked as performed
//          if (!x_alarmcounter_found) {
//            t.set_alarm_counter_to_next_after_now();
//          }
//          
//          // test for already existing UID and compare Last Modification Time
//          boolean store_imported_event = true;
//          boolean stop_searching = false;
//          // in event list
//          for (int i = 0; i < evenList.get(evenListEnCours).size(); i++) {
//            if (stop_searching) {
//              break;
//            }
//            if (((Event) evenList.get(evenListEnCours).get(i)).get_UID().compareTo(t.get_UID()) == 0) {
//              stop_searching = true;
//              if (((Event) evenList.get(evenListEnCours).get(i)).get_last_mod_UTC_ms() < t.get_last_mod_UTC_ms()
//                   && last_modified_found) {
//                // event is already in memory and older than the imported one
//                // purge stored event
//                evenList.get(evenListEnCours).remove(i);
//                if (deleted_event) {
//                  t.set_deleted_while_import(true);
//                }
//                else {
//                  t.set_now_imported(true);
//                }
//              }
//              else {
//                // event is already in memory and younger than the imported one
//                store_imported_event = false;
//              }
//            }
//          }
//          // in deleted event list
//          for (int i = 0; i < deleted_event_list.size(); i++) {
//            if (stop_searching) {
//              break;
//            }
//            if (((Event) deleted_event_list.get(i)).get_UID().compareTo(t.get_UID()) == 0) {
//              stop_searching = true;
//              if (((Event) deleted_event_list.get(i)).get_last_mod_UTC_ms() < t.get_last_mod_UTC_ms()
//                   && last_modified_found) {
//                // event is already in memory and older than the imported one
//                // purge stored event
//                deleted_event_list.remove(i);
//                if (!deleted_event) {
//                  t.set_now_imported(true);
//                }
//              }
//              else {
//                // event is already in memory and younger than the imported one
//                store_imported_event = false;
//              }
//            }
//          }
//          if (store_imported_event) {
//            if (deleted_event) {
//              deleted_event_list.add(t);
//            }
//            else {
//              evenList.get(evenListEnCours).add(t);
//              t.set_now_imported(true);
//            }
//          }
//        }
//      }
//    }
//    b_file_reader.close();
//    Collections.sort(evenList.get(evenListEnCours));
//    this.changed();
//    DateLookPanel.get_instance(null).changed();
//  }


//  /**
//   *  Import events from a file in vCalendar-format
//   *
//   * @param  file           file (object)
//   * @param  import_deleted import all deleted events too (vCalendar-property: "X-DTLK-DELETED:TRUE")
//   * @param  pb             progress bar
//   * @exception  Exception  -
//   */
//  public  void import_vCalendar(File file, boolean import_deleted, JProgressBar pb) throws Exception {
//    if (file.canRead()) {
//      this.import_vCalendar(new MyInputStreamReader(new FileInputStream(file)), file.length(), import_deleted, pb);
//    }
//  }


  /**
   *  Export events to a OutputStreamWriter
   *
   * @param  osw            OutputStreamWriter object
   * @param  i              array with indexes out of the event-list to be exported
   * @param  deleted        export all deleted events too (property X-DTLK-DELETED:TRUE)
   * @param  pb             progress bar that shows the progress
   * @param  public_only    true - export only events of class public<br>
   *                        false - export all selected events
   * @return                number of exported events
   * @exception  Exception  -
   */
  public  int export_vCalendar(OutputStreamWriter osw, int[] i, boolean deleted, JProgressBar pb, boolean public_only) throws Exception {
    int number_of_exported_events = 0;
    int bp_maximum = i.length;
    JProgressBar progress_bar = pb;
    BufferedWriter file_writer = new BufferedWriter(osw);
    
    if (deleted) {
      bp_maximum = bp_maximum + deleted_event_list.size();
    }
    file_writer.write("BEGIN:VCALENDAR\r\n");
    file_writer.write("PRODID:-//Rene Ewald//DateLook 2.2//EN\r\n");
    file_writer.write("VERSION:1.0\r\n");
    if (progress_bar != null) {
      progress_bar.setValue(0);
      progress_bar.setMaximum(bp_maximum);
    }
    for (int n = 0; n < i.length; n++) {
      if (progress_bar != null) {
        progress_bar.setValue(n);
      }
      if (!(public_only && ((Event) evenList.get(evenListEnCours).get(i[n])).get_vcal_class() == Event.Private)) {
        write_vevent(file_writer, (Event) evenList.get(evenListEnCours).get(i[n]), false);
        number_of_exported_events++;
      }
    }
    if (deleted) {
      for (int n = 0; n < deleted_event_list.size(); n++) {
        if (progress_bar != null) {
          progress_bar.setValue(n + i.length);
        }
        if (!(public_only && ((Event) deleted_event_list.get(n)).get_vcal_class() == Event.Private)) {
          write_vevent(file_writer, (Event) deleted_event_list.get(n), true);
        }
      }
    }
    file_writer.write("END:VCALENDAR\r\n");
    file_writer.close();
    return number_of_exported_events;
  }


  /**
   *  Export events to file
   *
   * @param  file           database file
   * @param  i              array with indexes out of event-list to be exported
   * @param  deleted        export all deleted events too (vCalendar-property: "X-DTLK-DELETED:TRUE")
   * @param  pb             progress bar that shows the progress
   * @param  public_only    true - only events of class public will be exported,<br>
   *                        false - all selected events will be exported
   * @return                number of exported events
   * @exception  Exception  
   */
  public  int export_vCalendar(File file, int[] i, boolean deleted, JProgressBar pb, boolean public_only) throws Exception {
    // charset to export is always US-ASCII, that is sure because there are no other characters inside the file
    return this.export_vCalendar(new OutputStreamWriter(new FileOutputStream(file), "US-ASCII"), i, deleted, pb, public_only);
  }


  /**
   *  Delete event -> put to list of deleted events
   *
   * @param  e      event object
   */
  // this is the central method to delete an event.
  // from here all information about changes are
  // sent to other involved instance via e.deleted()
  public  void delete_event(Event e) {
    deleted_event_list.add(e);
    evenList.get(evenListEnCours).remove(e);
    e.deleted();
    this.changed();
  }


  /**
   *  Purge event -> remove event totally (remove from event- AND deleted-event-list)
   *
   * @param  e      event object
   */
  public  void purge_event(Event e) {
    evenList.get(evenListEnCours).remove(e);
    this.changed();
  }


  /**
   *  Purge all deleted events -> remove all events from deleted-event-list
   *
   * @return          number of purged events
   */
  public  int purge_all() {
    int size = deleted_event_list.size();
    deleted_event_list.clear();
    this.changed();
    return size;
  }


  /**
   *  undelete all deleted events -> move all events from deleted-event-list
   *                                 to event-list
   */
  public  void undelete_all() {
    Event e;
    while (deleted_event_list.size() > 0) {
      e = (Event) deleted_event_list.get(0);
      deleted_event_list.remove(e);
      evenList.get(evenListEnCours).add(e);
      e.set_now_imported(true);
      e.changed();
    }
    this.changed();
    return;
  }


  /**
   *  Add event to event memory
   *
   * @param  e  event object
   */
  public  void add_event(Event e) {
    deleted_event_list.remove(e);    // if an event only temporary deleted during drag
    evenList.get(evenListEnCours).add(e);
    this.changed();
  }


  /**
   *  Gets the number of (not deleted) events stored in EventMemory
   *
   * @return    The size value
   */
  public  int get_size() {
    return evenList.get(evenListEnCours).size();
  }


  /**
   *  Gets the number of deleted events stored in EventMemory
   *
   * @return    The size value
   */
  public  int get_deleted_size() {
    return deleted_event_list.size();
  }


  /**
   *  Get event from event memory
   *
   * @param  i  index of the event (event list)
   * @return    event object
   */
  public  Event get_event(int i) {
    return (Event) evenList.get(evenListEnCours).get(i);
  }


  /**
   *  Get deleted event from event memory
   *
   * @param  i  index of the deleted event (deleted event list)
   * @return    event object
   */
  public  Event get_deleted_event(int i) {
    return (Event) deleted_event_list.get(i);
  }


//  /**
//   *  Set the event_table_frame attribute of the EventMemory object
//   *
//   * @param  ttf  The new event_table_frame value
//   */
//  public  void set_event_table_frame(EventTableFrame ttf) {
//    event_table_frame = ttf;
//  }
//
//
//  /**
//   *  Get the event_table_frame attribute of the EventMemory object
//   *
//   * @return    The event_table_frame value
//   */
//  public EventTableFrame get_event_table_frame() {
//    return event_table_frame;
//  }


  /**
   *  Must be called if values of at least one event has been changed.<br>
   *  The list of events will be sorted.
   */
  public void changed() {
    Collections.sort(evenList.get(evenListEnCours));
//    if (event_table_frame != null) {
//      event_table_frame.changed();
//    }
  }


  /**
   *  Write a event to a BufferedWriter object.
   *
   * @param  bw               BufferedWriter object
   * @param  t                event
   * @param  deleted          indicates deleted event (vCalendar-property: "X-DTLK-DELETED:TRUE")
   * @exception  IOException  -
   */
  public void write_vevent(BufferedWriter bw, Event t, boolean deleted) throws IOException {

    NumberFormat formatter = NumberFormat.getNumberInstance();
    formatter.setMinimumIntegerDigits(2);
    formatter.setGroupingUsed(false);
    NumberFormat formatter4 = NumberFormat.getNumberInstance();
    formatter4.setMinimumIntegerDigits(4);
    formatter4.setGroupingUsed(false);

    Date d = new Date();
    GregorianCalendar gc = new GregorianCalendar();
    bw.write("BEGIN:VEVENT\r\n");

    // write DTSTART
    d.setTime(t.get_begin_UTC_ms());
    gc.setTime(d);
    bw.write("DTSTART:" + formatter4.format(gc.get(GregorianCalendar.YEAR)) +
        formatter.format(gc.get(GregorianCalendar.MONTH) + 1) +
        formatter.format(gc.get(GregorianCalendar.DAY_OF_MONTH)) + "T" +
        formatter.format(gc.get(GregorianCalendar.HOUR_OF_DAY)) +
        formatter.format(gc.get(GregorianCalendar.MINUTE)) +
        "00Z\r\n");

    // write DTEND
    d.setTime(t.get_end_UTC_ms());
    gc.setTime(d);
    bw.write("DTEND:" + formatter4.format(gc.get(GregorianCalendar.YEAR)) +
        formatter.format(gc.get(GregorianCalendar.MONTH) + 1) +
        formatter.format(gc.get(GregorianCalendar.DAY_OF_MONTH)) + "T" +
        formatter.format(gc.get(GregorianCalendar.HOUR_OF_DAY)) +
        formatter.format(gc.get(GregorianCalendar.MINUTE)) +
        "00Z\r\n");

    // write DALARM
    if (t.get_alarm_active()) {
      d.setTime(t.get_alarm_UTC_ms());
      gc.setTime(d);
      bw.write("DALARM:" + formatter4.format(gc.get(GregorianCalendar.YEAR)) +
          formatter.format(gc.get(GregorianCalendar.MONTH) + 1) +
          formatter.format(gc.get(GregorianCalendar.DAY_OF_MONTH)) + "T" +
          formatter.format(gc.get(GregorianCalendar.HOUR_OF_DAY)) +
          formatter.format(gc.get(GregorianCalendar.MINUTE)) +
          "00Z\r\n");
    }

    // write RRULE
    d.setTime(t.get_begin_UTC_ms());
    gc.setTime(d);
    int period = t.get_period();
    if (period != Event.None) {
      String s = "";
      if (period == Event.Yearly) {
        s = "YM" + Integer.toString(t.get_period_multiplier()) + " " + Integer.toString(gc.get(GregorianCalendar.MONTH) + 1);
      }
      else if (period == Event.Monthly) {
        s = "MD" + Integer.toString(t.get_period_multiplier()) + " " + Integer.toString(gc.get(GregorianCalendar.DAY_OF_MONTH) + 1);
      }
      else if (period == Event.Weekly) {
        s = "W" + Integer.toString(t.get_period_multiplier());
      }
      else if (period == Event.Daily) {
        s = "D" + Integer.toString(t.get_period_multiplier());
      }
      int num = t.get_number_of_periods();
      if (num == 999) {
        num = 0; // 999 is internally used for unlimited
      }
      s = s + " #" + Integer.toString(num);
      bw.write("RRULE:" + s + "\r\n");
    }

    // write UID
    String uid = "UID:" + t.get_UID();
    int i = 65;
    // fold line
    while (i < uid.length()) {
      uid = uid.substring(0, i) + "\r\n " + uid.substring(i);
      i = i + 65;
    }
    bw.write(uid + "\r\n");

    // write LAST-MODIFIED
    d.setTime(t.get_last_mod_UTC_ms());
    gc.setTime(d);
    bw.write("LAST-MODIFIED:" + formatter4.format(gc.get(GregorianCalendar.YEAR)) +
        formatter.format(gc.get(GregorianCalendar.MONTH) + 1) +
        formatter.format(gc.get(GregorianCalendar.DAY_OF_MONTH)) + "T" +
        formatter.format(gc.get(GregorianCalendar.HOUR_OF_DAY)) +
        formatter.format(gc.get(GregorianCalendar.MINUTE)) +
        formatter.format(gc.get(GregorianCalendar.SECOND)) + "Z\r\n");

    // write CLASS
    String class_string = "PUBLIC";
    if (t.get_vcal_class() == Event.Private) {
      class_string = "PRIVATE";
    }
    bw.write("CLASS:" + class_string + "\r\n");

    // write SUMMARY
    bw.write("SUMMARY;CHARSET="
         + t.get_summary_encoding()
         + ";ENCODING=QUOTED-PRINTABLE:"
         + Converter.unicode2quodedPrintable(t.get_summary(), t.get_summary_encoding())
         + "\r\n");

    // write DESCRIPTION
    String tmp = t.get_description();
    if (tmp.length() > 0) {
      bw.write("DESCRIPTION;CHARSET="
           + t.get_description_encoding()
           + ";ENCODING=QUOTED-PRINTABLE:"
           + Converter.unicode2quodedPrintable(tmp, t.get_description_encoding())
           + "\r\n");
    }

    // write X-DTLK-COLOUR, X-DTLK-GPOUP, X-DTLK-ALARMCOUNTER and X-DTLK-DELETED:TRUE)
    formatter.setMinimumIntegerDigits(3);
    bw.write("X-DTLK-COLOUR:" + formatter.format(t.get_renderer_color().getRed())
         + formatter.format(t.get_renderer_color().getGreen())
         + formatter.format(t.get_renderer_color().getBlue())
         + "\r\n");
    bw.write("X-DTLK-GROUP:" + Integer.toString(t.get_renderer_group()) + "\r\n");
    bw.write("X-DTLK-ALARMCOUNTER:" + Integer.toString(t.get_alarm_counter()) + "\r\n");
    if (deleted) {
      bw.write("X-DTLK-DELETED:TRUE\r\n");
    }
    bw.write("END:VEVENT" + "\r\n");
  }

  
  /**
   *  Get lock file name
   *
   * @return    lock file name
   */
  public String get_lock_file_name() {
    return lock_file_name;
  }




  /**
   *  Read bytes from FileInputStream and extends each byte to a character by adding 0x00 at MSB.
   */
  public static class MyInputStreamReader extends InputStreamReader {
    InputStream my_input_stream;


    /**
     *  Constructor for the MyInputStreamReader object
     *
     * @param  in  input stream
     */
    public MyInputStreamReader(InputStream in) {
      super(in);
      my_input_stream = in;
    }


    /**
     *  Read a character from predefined input stream.
     *
     * @return    character
     */
    public int read() {
      try {
        return (char) my_input_stream.read();
      }
      catch (Exception e) {
        e.printStackTrace();
        return -1;
      }
    }


    /**
     *  Read characters from my_input_stream and write the character to a character buffer<br>
     *  to a given offset.
     *
     * @param  cbuf    character buffer
     * @param  offset  offset in cbuf where read characters is written to
     * @param  length  number of characters to be read
     * @return         number of read characters
     */
    public int read(char[] cbuf, int offset, int length) {
      byte[] my_byte_array = new byte[length];
      int retVal;

      try {
        retVal = my_input_stream.read(my_byte_array, 0, length);
      }
      catch (Exception e) {
        e.printStackTrace();
        return -1;
      }

      for (int k = 0; k < retVal; k++) {
        cbuf[k + offset] = (char) my_byte_array[k];
      }

      return retVal;
    }
  }


public ArrayList<Event> getEvent_list() {
	return evenList.get(evenListEnCours);
}

public ArrayList<ArrayList<Event>> getEvenList() {
	return evenList;
}

public void setEvent_list(ArrayList<ArrayList<Event>> eventList) {
	evenList = eventList;
}



public int getListEventEnCours()
{
	return evenListEnCours;
}



public void setListEventEnCours(int listEventEnCours)
{
	this.evenListEnCours = listEventEnCours;
}


public void setDateLookInstance(DateLook dateLook)
{
this.dateLookInstance = dateLook;	
}

public DateLook getDateLookInstance()
{
	return this.dateLookInstance;	
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


