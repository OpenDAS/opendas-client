package org.opendas.calendar;

import java.awt.*;
import java.awt.event.*;

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
 * Abstract renderer.<br>
 * Renders objects on a RPanel that are derived from this class,<br>
 * provides a set of methodes to handle mouse events
 */
public abstract class Renderer
{

	/**
	 * the parent panel
	 */
	protected RPanel	panel;

	/**
	 * Constructor for the Renderer object
	 * 
	 * @param p
	 *            the parent panel or null id later set
	 */
	Renderer(RPanel p)
	{
		panel = p;
	}

	/**
	 * Should be called within the paintComponent-method of the panel
	 * 
	 * @param g2
	 *            Graphics object
	 */
	public void draw(Graphics2D g2)
	{
	}

	/**
	 * Check whether the mouse clickes on the renderer
	 * 
	 * @param e
	 *            mouse event
	 * @return true - mouse hits the renderer<br>
	 *         false - mouse doesn't hit the renderer
	 */
	public boolean mouse_clicked(MouseEvent e)
	{
		return false;
	}

	/**
	 * Check whether the mouse is over the renderer
	 * 
	 * @param e
	 *            mouse event
	 * @return true - mouse hits the renderer<br>
	 *         false - mouse doesn't hit the renderer
	 */
	public boolean mouse_over(MouseEvent e)
	{
		return false;
	}

	/**
	 * Check whether the mouse is pressed on renderer
	 * 
	 * @param e
	 *            mouse event
	 * @return true - mouse hits the renderer<br>
	 *         false - mouse doesn't hit the renderer
	 */
	public boolean mouse_pressed(MouseEvent e)
	{
		return false;
	}

	/**
	 * Check whether the mouse is released on the renderer
	 * 
	 * @param e
	 *            mouse event
	 * @return true - mouse hits the renderer<br>
	 *         false - mouse doesn't hit the renderer
	 */
	public boolean mouse_released(MouseEvent e)
	{
		return false;
	}

	/**
	 * Check whether the mouse wheel rotates over the renderer
	 * 
	 * @param e
	 *            mouse wheel event
	 * @return true - mouse hits the renderer<br>
	 *         false - mouse doesn't hit the renderer
	 */
	public boolean mouse_wheel_rotate(MouseWheelEvent e)
	{
		return false;
	}
}
