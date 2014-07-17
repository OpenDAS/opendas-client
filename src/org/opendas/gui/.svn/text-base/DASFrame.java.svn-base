package org.opendas.gui;

import gnu.io.NoSuchPortException;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Classe permitting the display of main screen
 * 
 * @author martineaua
 * @author mlaroche
 */
public class DASFrame extends JFrame
{

	private static final long	serialVersionUID	= 1L;
	private DASPanel			panel;

	public DASFrame()
	{
		super();
		build();
	}

	private void build()
	{
		setTitle("OpenDAS");
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle bounds = env.getMaximumWindowBounds();
		this.setBounds(bounds);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		try
		{
			panel = new DASPanel(this);
		}
		catch (NoSuchPortException e1)
		{
			e1.printStackTrace();
		}
		JPanel panel1 = panel.buildPaneHeader();
		JPanel panel2 = panel.buildPaneCenter();
		JPanel panel3 = panel.buildPaneFooter();
		this.add(panel1, BorderLayout.NORTH);
		this.add(panel2, BorderLayout.CENTER);
		this.add(panel3, BorderLayout.SOUTH);
		try
		{
			panel.activate();
		} catch (Exception e)
		{
			e.printStackTrace();
			panel.showError("Erreur");
		}
	}
}
