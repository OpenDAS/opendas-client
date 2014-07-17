package org.opendas.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

/**
 * JButton composed of background which can be graduate vertically
 * 
 * @author mlaroche
 */
public class DASGradientJButton extends JButton
{

	private static final long	serialVersionUID	= 1L;
	private byte				gradient;
	private Color				active_bg;
	private Color				inactive_bg;
    private boolean             erase = false;
	
	/**
	 * Constructor
	 * 
	 * @param name
	 *            text to display on the button
	 * @param gradient
	 *            number of degrade (0=none, 1=gradation)
	 */
	public DASGradientJButton(String nom, byte gradient)
	{
		super(nom);
		this.gradient = gradient;
		active_bg = getBackground();
		inactive_bg = active_bg;
	}

	/**
	 * Constructor
	 * 
	 * @param gradient
	 *            number of degrade (0=none, 1=gradation)
	 * @param abg
	 *            Background color when the button is active
	 * @param ibg
	 *            Background color when the button is inactive
	 */
	public DASGradientJButton(byte degrade, Color abg, Color ibg)
	{
		super();
		this.gradient = degrade;
		active_bg = abg;
		inactive_bg = ibg;
	}

	/**
	 * Constructor
	 * 
	 * @param none
	 *            text to display on the button
	 * @param gradient
	 *           numero of gradation (0=none,degrade 1)
     couleur du fond lorsque le bouton est desactive
	 */
	public DASGradientJButton(String nom, byte gradient, Color abg, Color ibg)
	{
		super(nom);
		this.gradient = gradient;
		active_bg = abg;
		inactive_bg = ibg;
	}

	public boolean isErasable()
	{
		return this.erase;
	}
	
	public void setErase(boolean value)
	{
		this.erase = value;
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		if (gradient == 1 && isEnabled())
		{
			setOpaque(false);
			Graphics2D g2 = (Graphics2D) g;
			Color color1 = Color.white;
			Color color2 = getBackground();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// If windows too long to load,delete this line
			g2.setPaint(new GradientPaint(0, 0, color1, 0, getHeight(), color2, true));
			g2.fillRect(0, 0, getWidth(), getHeight());
			// g2.setPaint(new GradientPaint(0, 0, color2, 0, this.getHeight(),
			// color1, true));
			// g2.fillRect(0, this.getHeight()/2, this.getWidth(),
			// this.getHeight());
			// en décommentant, (et normalement en modifiant ligne précédente)		
			// double gradient, possibility to change to default with reversing of colors

			super.paintComponent(g2);
		} else
		{
			setOpaque(true);
			super.paintComponent(g);
		}
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		if (enabled)
		{
			setBackground(active_bg);
		} else
		{
			setBackground(inactive_bg);
		}
		super.setEnabled(enabled);
	}
}
