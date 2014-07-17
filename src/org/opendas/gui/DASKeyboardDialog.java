package org.opendas.gui;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.concurrent.TimeoutException;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Window which display a virtual keyboard
 * 
 * @author mlaroche
 */
public class DASKeyboardDialog extends JDialog
{

	private static final long	serialVersionUID	= 1L;
	private DASKeyboardPanel	keyboardPanel;

	public DASKeyboardDialog(JFrame frame, String title) throws TimeoutException
	{
		super(frame, title, true);
		init();
	}

	private void init() throws TimeoutException
	{
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle bounds = env.getMaximumWindowBounds();
		this.setBounds(bounds);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
	}

	/**
	 * 
	 * Build the screens of the window of the keyboard Methode to cann after the personalization of parameters 
	 *
	 */
	public void build(String mode)
	{
		keyboardPanel.inits(mode);
		try
		{
			keyboardPanel.activate();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public DASKeyboardPanel getKeyboardPanel()
	{
		return keyboardPanel;
	}

	public String getInput()
	{
		return keyboardPanel.getSaisie();
	}

	public boolean validatedInput()
	{
		return keyboardPanel.validatedInput();
	}
}
