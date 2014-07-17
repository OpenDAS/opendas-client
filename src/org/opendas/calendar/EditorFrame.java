package org.opendas.calendar;


import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.opendas.gui.DASPickerDate;
import org.opendas.gui.DASPickerNumber24;
import org.opendas.gui.DASPickerNumber60;
import org.opendas.translate.I18n;

public class EditorFrame extends JFrame
{

	private static final long	serialVersionUID	= 1L;
	private DateLook	dateLookInstance;
	private Event		event;
	private Event		event_before_modifed;
	JButton				saveButton;
	public Calendar cal = Calendar.getInstance();
	public void checkdate1beforedate2(JButton button, long date_1, long date_2)
	{
		Date date1 = new Date(date_1);
		Date date2 = new Date(date_2);
		if (date1.before(date2))
		{
			saveButton.setEnabled(true);
		}
		else
		{
			saveButton.setEnabled(false);
		}
	}

	/**
	 * Construct the frame
	 * 
	 * @param t
	 *            event to be changed or has been changed by dragging
	 * @param ot
	 *            orignal event (clone, made before dragging)
	 */
	public EditorFrame(Event t, Event ot, DateLook mf)
	{
		super("Valider Changements");
		this.event = t;
		this.event_before_modifed = ot;
		this.dateLookInstance = mf;
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		this.setSize(600, 340);
		this.setTitle(I18n._("Valid Changes"));
		this.setVisible(false);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setLocationRelativeTo(mf.getDateLookPanel().getMf().getParentPanel());
		this.dateLookInstance.getDateLookPanel().getMf().getParentPanel().SetWindowEnabled(false);
		this.setLayout(null);
		this.getContentPane().setBackground(Color.WHITE);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE d MMMM yyyy");
		cal.setTime(new Date(event.get_begin_UTC_ms()));
		final int current_month = cal.get(Calendar.MONTH);
		// START ROW
		JLabel start_field = new JLabel(I18n._("Start :"));
		this.add(start_field);
		start_field.setBounds(20, 20, 120, 50);
		cal.setTime(new Date(event.get_begin_UTC_ms()));
		final JButton customStartHourButton = new JButton(cal.get(Calendar.HOUR) + "h");
		customStartHourButton.setFocusable(false);
		customStartHourButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				cal.setTime(new Date(event.get_begin_UTC_ms()));
				int newNum = new DASPickerNumber24(dateLookInstance.getDateLookPanel().getMf().getParentPanel().getFrame(), cal.get(Calendar.HOUR)).setPickedDate();
				Date dat = new Date(event.get_begin_UTC_ms());
				//dat.setHours(newNum);
				cal.setTime(dat);
				event.set_begin_UTC_ms(cal.getTime().getTime());
				customStartHourButton.setText("" + newNum + "h");
				checkdate1beforedate2(customStartHourButton, event.get_begin_UTC_ms(), event.get_end_UTC_ms());
			}
		});
		this.add(customStartHourButton);
		customStartHourButton.setBounds(110, 20, 75, 50);
		cal.setTime(new Date(event.get_begin_UTC_ms()));
		final JButton customStartMinuteButton = new JButton(cal.get(Calendar.MINUTE) + "m");
		customStartMinuteButton.setFocusable(false);
		customStartMinuteButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				cal.setTime(new Date(event.get_begin_UTC_ms()));
				int newNum = new DASPickerNumber60(dateLookInstance.getDateLookPanel().getMf().getParentPanel().getFrame(),cal.get(Calendar.MINUTE), true).setPickedDate();
				Date dat = new Date(event.get_begin_UTC_ms());
				dat.setMinutes(newNum);
				cal.set(Calendar.MINUTE,newNum);
				event.set_begin_UTC_ms(cal.getTimeInMillis());
				customStartMinuteButton.setText("" + newNum + "m");
				checkdate1beforedate2(customStartMinuteButton, event.get_begin_UTC_ms(), event.get_end_UTC_ms());
			}
		});
		this.add(customStartMinuteButton);
		customStartMinuteButton.setBounds(190, 20, 75, 50);
		final JButton customStartDateButton = new JButton(sdf.format(new Date(event.get_begin_UTC_ms())));
		customStartDateButton.setFocusable(false);
		customStartDateButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				long newNum = new DASPickerDate(dateLookInstance.getDateLookPanel().getMf().getParentPanel().getFrame(), event.get_begin_UTC_ms(), current_month).getDate();
				event.set_begin_UTC_ms(newNum);
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE d MMMM yyyy");
				customStartDateButton.setText(sdf.format(new Date(event.get_begin_UTC_ms())));
				checkdate1beforedate2(customStartDateButton, event.get_begin_UTC_ms(), event.get_end_UTC_ms());
			}
		});
		this.add(customStartDateButton);
		customStartDateButton.setBounds(300, 20, 250, 50);
		// END ROW
		JLabel End_field = new JLabel(I18n._("End :"));
		this.add(End_field);
		End_field.setBounds(20, 90, 120, 50);
		cal.setTime(new Date(event.get_end_UTC_ms()));
		final JButton customEndHourButton = new JButton(cal.get(Calendar.HOUR)+ "h");
		customEndHourButton.setFocusable(false);
		customEndHourButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				cal.setTime(new Date(event.get_end_UTC_ms()));
				int newNum = new DASPickerNumber24(dateLookInstance.getDateLookPanel().getMf().getParentPanel().getFrame(),cal.get(Calendar.HOUR)).setPickedDate();
				Date dat = new Date(event.get_end_UTC_ms());
				dat.setHours(newNum);
				event.set_end_UTC_ms(dat.getTime());
				customEndHourButton.setText("" + newNum + "h");
				checkdate1beforedate2(customEndHourButton, event.get_begin_UTC_ms(), event.get_end_UTC_ms());
			}
		});
		this.add(customEndHourButton);
		customEndHourButton.setBounds(110, 90, 75, 50);
		@SuppressWarnings("deprecation")
		final JButton customEndMinuteButton = new JButton(new Date(event.get_end_UTC_ms()).getMinutes() + "m");
		customEndMinuteButton.setFocusable(false);
		customEndMinuteButton.addActionListener(new ActionListener() {

			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e)
			{
				int newNum = new DASPickerNumber60(dateLookInstance.getDateLookPanel().getMf().getParentPanel().getFrame(), new Date(event.get_end_UTC_ms()).getMinutes(), true).setPickedDate();
				Date dat = new Date(event.get_end_UTC_ms());
				dat.setMinutes(newNum);
				event.set_end_UTC_ms(dat.getTime());
				customEndMinuteButton.setText("" + newNum + "m");
				checkdate1beforedate2(customEndMinuteButton, event.get_begin_UTC_ms(), event.get_end_UTC_ms());
			}
		});
		this.add(customEndMinuteButton);
		customEndMinuteButton.setBounds(190, 90, 75, 50);
		final JButton customEndDateButton = new JButton(sdf.format(new Date(event.get_end_UTC_ms())));
		customEndDateButton.setFocusable(false);
		customEndDateButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				long newNum = new DASPickerDate(dateLookInstance.getDateLookPanel().getMf().getParentPanel().getFrame(), event.get_end_UTC_ms(), current_month).getDate();
				event.set_end_UTC_ms(newNum);
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE d MMMM yyyy");
				customEndDateButton.setText(sdf.format(new Date(event.get_end_UTC_ms())));
				checkdate1beforedate2(customEndDateButton, event.get_begin_UTC_ms(), event.get_end_UTC_ms());
			}
		});
		this.add(customEndDateButton);
		customEndDateButton.setBounds(300, 90, 250, 50);
		// SUMMARY
		JTextField summary_text_field = new JTextField();
		summary_text_field.setBackground(Color.white);
		summary_text_field.setEditable(true);
		summary_text_field.setText(event.get_summary());
		summary_text_field.setToolTipText("Nom de l'évenement selectionné.");
		if (DateLookPanel.getLockName().equals("0") && DateLookPanel.getLockDate().equals("0"))
		{
			summary_text_field.setEditable(true);
		}
		else
		{
			summary_text_field.setEditable(false);
		}
		this.add(summary_text_field);
		summary_text_field.setBounds(20, 180, 560, 50);
		// SAVE BUTTON
		saveButton = new JButton(I18n._("Save"));
		saveButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
				dateLookInstance.getDateLookPanel().getMf().getParentPanel().SetWindowEnabled(true);
				// synchronize the date
				dateLookInstance.getDateLookPanel().getMf().getParentPanel().getController().synchroDate(event, false);
			}
		});
		// DELETE BUTTON
		JButton deleteButton = new JButton(I18n._("Delete"));
		deleteButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				dateLookInstance.getDateLookPanel().getMf().eventMemory.delete_event(event);
				setVisible(false);
				dateLookInstance.getDateLookPanel().getMf().getParentPanel().SetWindowEnabled(true);
				dateLookInstance.getDateLookPanel().getMf().getParentPanel().getController().synchroDate(event, true);
			}
		});
		// CANCEL BUTTON
		JButton closeButton = new JButton(I18n._("Cancel"));
		closeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				if (event_before_modifed != null)
				{
					// restore original
					event_before_modifed.set_UID(event.get_UID()); // write orig
					dateLookInstance.getDateLookPanel().getMf().eventMemory.purge_event(event);
					dateLookInstance.getDateLookPanel().getMf().eventMemory.add_event(event_before_modifed);
					if (event_before_modifed.get_event_renderer() != null)
					{
						event_before_modifed.get_event_renderer().set_visible(true);
					}
				}
				// new_event &&
				if (event != null)
				{
					dateLookInstance.getDateLookPanel().getMf().eventMemory.purge_event(event);
				}
				dateLookInstance.getDateLookPanel().getMf().getDateLookPanel().changed();
				setVisible(false);
				event.set_my_editor_frame(null);
				dateLookInstance.getDateLookPanel().getMf().getParentPanel().SetWindowEnabled(true);
			}
		});
		this.add(saveButton);
		saveButton.setBounds(20, 250, 150, 50);
		this.add(closeButton);
		closeButton.setBounds(190, 250, 150, 50);
		this.add(deleteButton);
		deleteButton.setBounds(430, 250, 150, 50);
		this.setVisible(false);
	}

	public DateLook getDateLookInstance()
	{
		return dateLookInstance;
	}

	public void setDateLookInstance(DateLook dateLookInstance)
	{
		this.dateLookInstance = dateLookInstance;
	}
}