package org.opendas;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class DASException extends Exception
{
	private static final long	serialVersionUID	= 1L;
	private int code;
	
    public DASException(int code, String message){
    	super(message);
    	this.code = code;
    }
    
    public int getCode()
	{
		return this.code;
	}
    
    public void show()
    {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	this.printStackTrace(pw);
    	
    	JFrame frame = new JFrame("Error");
    	GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle bounds = env.getMaximumWindowBounds();
		frame.setBounds(bounds);
    	JTabbedPane tabbedPane = new JTabbedPane();
    	
    	JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));
		JTextArea text1 = new JTextArea();
		text1.setLineWrap(true);
		text1.setRows(15);
		JScrollPane scrolltext1 = new JScrollPane(text1);
		panel1.add(scrolltext1);
		text1.setText(this.getCode()+"\n"+this.getMessage());
		tabbedPane.addTab("Error", panel1);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
		JTextArea text2 = new JTextArea();
		text2.setLineWrap(true);
		text2.setRows(15);
		JScrollPane scrolltext2 = new JScrollPane(text2);
		panel2.add(scrolltext2);
		
		text2.setText(sw.toString());
		tabbedPane.addTab("Trace", panel2);
		
    	frame.setContentPane(tabbedPane);
    	frame.setVisible(true);
    	
    }
    
}
