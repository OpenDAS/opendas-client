package org.opendas.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JToggleButton;

/**
 * JToggleButton composed of background which can be gradiate vertically
 * 
 * @author mlaroche
 */
public class DASGradientJToggleButton extends JToggleButton
{

	private static final long	serialVersionUID	= 1L;
	/** id of corresponding generic */
	private String				id;
	/** gradient numero (0=none, 1= gradiant) **/
	private byte				gradient;
	private Color				active_bg;
	private Color				inactive_bg;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            text to display on the button
	 * @param gradient
	 *            number of degrade (0=none, 1=gradation)
	 * @param abg
	 *            Background color when the button is active
	 * @param ibg
	 *            Background color when the button is inactive
	 */
	public DASGradientJToggleButton(String nom, byte degrade, Color abg, Color ibg)
	{
		super(nom);
		this.gradient = degrade;
		active_bg = abg;
		inactive_bg = ibg;
	}

	/**
	 * Constructeur
	 * 
	 * @param gradient
	 *            numero de degrade (0=aucun, 1=degrade 1)
	 * @param abg
	 *            Background color when the button is active
	 * @param ibg
	 *            Background color when the button is inactive
	 */
	public DASGradientJToggleButton(byte gradient, Color abg, Color ibg)
	{
		super();
		this.gradient = gradient;
		active_bg = abg;
		inactive_bg = ibg;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		if (gradient == 1 && isEnabled())
		{
			setOpaque(false);
			Graphics2D g2 = (Graphics2D) g;
			Color color1 = Color.white;
			Color color2 = this.getBackground();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// Si fenetre trop long a charger, supprimer cette ligne
			g2.setPaint(new GradientPaint(0, 0, color1, 0, this.getHeight(), color2, true));
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			// g2.setPaint(new GradientPaint(0, 0, color2, 0, this.getHeight(),
			// color1, true));
			// g2.fillRect(0, this.getHeight()/2, this.getWidth(),
			// this.getHeight());
			// en décommentant, (et normalement en modifiant ligne précédente)
			// double dégradé, possibilité de se rapprocher du par défaut en
			// inversant les couleurs)
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

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}
}
