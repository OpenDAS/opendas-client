package org.opendas.calendar;
import java.awt.AWTEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
 *  Applications main class
 */
public class DateLook extends JFrame {

	private JPanel contentPane;
	private DASPanel panel;
	private DateLookPanel dateLookPanel;
	private String deltaT = "week";
	private String lockName = "1";
	private String lockDate = "1";
	private GregorianCalendar gc;
	public ArrayList<ArrayList<Event>>	evenList = new ArrayList<ArrayList<Event>>();
	public EventMemory eventMemory;

	/**
	 *  Constructor for the DateLook object
	 **/
	public DateLook(HashMap<String, Object> calendarData, ArrayList<Event> listEv, DASPanel panel) {

		this.panel = panel;

		this.deltaT = (String)calendarData.get("calendarDeltaT");
		Settings.setDeltaT((String)calendarData.get("calendarDeltaT"));

		this.lockName = (String)calendarData.get("calendarLockName");

		this.lockDate = (String)calendarData.get("calendarLockDate");

		this.gc = (GregorianCalendar)calendarData.get("gc");

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		try {

			dateLookPanel = new DateLookPanel(this);

			// Sort Event List
			int eventLeft =  listEv.size();
			int numBtns = 0;

			ArrayList<Event> ListButton = new ArrayList<Event>();

			ArrayList<Event> noPlacedEvent = new ArrayList<Event>();
			noPlacedEvent.addAll(listEv);

			// New Template listEv
			for (Event i : noPlacedEvent){	
				numBtns++;	
				eventLeft--;
				ListButton.add(new Event());
				if(numBtns == (dateLookPanel.getNumber_of_slots()-dateLookPanel.getNum_ligne_top_description()) || eventLeft == 0){
					numBtns = 0;
					if(ListButton.size() < dateLookPanel.getNumber_of_slots()-dateLookPanel.getNum_ligne_top_description()){
						for(int j=dateLookPanel.getNumber_of_slots()-dateLookPanel.getNum_ligne_top_description()-ListButton.size();j>0;j--){
							ListButton.add(new Event());	
						}
					}
					evenList.add(ListButton);
					ListButton = new ArrayList<Event>();
				}
			}

			for(Event i : noPlacedEvent){
				for(int eventListEnCours = 0;eventListEnCours<evenList.size();eventListEnCours++){
					for(int z=0;z<dateLookPanel.getNumber_of_slots()-dateLookPanel.getNum_ligne_top_description();z++){
						if(evenList.get(eventListEnCours).get(z).getId() == -1){
							i.set_renderer_group(z);
							evenList.get(eventListEnCours).set(z,i);
							z=dateLookPanel.getNumber_of_slots()-dateLookPanel.getNum_ligne_top_description();
							eventListEnCours = evenList.size();
						} 	
					}
				}
			}		

			eventMemory = new EventMemory(evenList, this);
			if((eventMemory.getEvenList().size()) > (Integer)calendarData.get("pageEnCours")){
				eventMemory.setListEventEnCours((Integer)calendarData.get("pageEnCours"));
			}


			this.setSize(Settings.get_instance(null).get_frame_width(), 
					Settings.get_instance(null).get_decor_height() + Settings.get_instance(null).get_number_of_slots() * Settings.get_instance(null).get_slot_height());

			contentPane = (JPanel) this.getContentPane();
			contentPane.add(dateLookPanel);
			this.addKeyListener(dateLookPanel);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void switchPanel(String dt){
		this.remove(dateLookPanel);  
		Settings.setDeltaT(dt);
		this.deltaT = dt;
		dateLookPanel = new DateLookPanel(this);
		this.add(dateLookPanel);
		panel.refreshCalendar(dt);
	}

	public void sendCodeToPanelParent(String code){
		panel.codeSend(code, -1);
	}

	public DASPanel getParentPanel(){
		return this.panel;
	}

	public JPanel getPanel(){
		return this.contentPane;
	}

	
	public DateLookPanel getDateLookPanel()
	{
		return dateLookPanel;
	}


	public void setDateLookPanel(DateLookPanel dateLookPanel)
	{
		this.dateLookPanel = dateLookPanel;
	}


	/**
	 *  Process window event.<br>
	 *  If closing event is received the database will be saved to file<br>
	 *  and DateLook will be left.
	 *
	 * @param  e  window event
	 */
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			super.processWindowEvent(e);
			System.exit(0);
		}
		else {
			super.processWindowEvent(e);
		}
	}

	public void setPanel(DateLookPanel dateLookPanel)
	{
		this.contentPane = dateLookPanel;	
	}


	public String getDeltaT()
	{
		return deltaT;
	}


	public void setDeltaT(String deltaT)
	{
		this.deltaT = deltaT;
	}


	public void setPanel(DASPanel panel)
	{
		this.panel = panel;
	}

	public String getLockName()
	{
		return lockName;
	}


	public void setLockName(String lockName)
	{
		this.lockName = lockName;
	}


	public String getLockDate()
	{
		return lockDate;
	}


	public void setLockDate(String lockDate)
	{
		this.lockDate = lockDate;
	}



	public GregorianCalendar getGc()
	{
		return gc;
	}


	public void setGc(GregorianCalendar gc)
	{
		this.gc = gc;
	}
	
	public ArrayList<ArrayList<Event>> getEvenList()
	{
		return evenList;
	}


	public void setEvenList(ArrayList<ArrayList<Event>> evenList)
	{
		this.evenList = evenList;
	}


}

