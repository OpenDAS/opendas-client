package org.opendas.gui;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.opendas.ctrl.DASController;
import org.opendas.modele.DASGeneric;
import org.opendas.translate.I18n;

public class DASContextViewer extends JFrame implements ActionListener, ListSelectionListener, MouseListener
{
	JComponent				actual;
	String					log;
	JTabbedPane				tabbedPane;
	JPanel					panel;
	JTextArea				text;
	JTextArea				textlog;
	JScrollPane				scrolltext;
	JPanel					panel1;
	JPanel					panel2;
	JPanel					panel3;
	JPanel					panel4;
	JTable					tableau1;
	JTable					tableau2;
	JTable					tableau3;
	JScrollPane				scroll1;
	JScrollPane				scroll2;
	JScrollPane				scroll3;
	JScrollPane				scroll4;
	Vector<Vector<String>>	data1;
	Vector<Vector<String>>	data2;
	Vector<Vector<String>>	data3;
	Vector<String>			header1;
	Vector<String>			header2;
	Vector<String>			header3;
	DASController		controller;

	public DASContextViewer(DASController controller) throws HeadlessException
	{
		super("Context");
		this.log = "";
		this.controller = controller;
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle bounds = env.getMaximumWindowBounds();
		this.setBounds(bounds);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
		this.panel = new JPanel();
		this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.PAGE_AXIS));
		this.tabbedPane = new JTabbedPane();

		this.panel1 = new JPanel();
		this.panel1.setLayout(new BoxLayout(this.panel1, BoxLayout.PAGE_AXIS));
		this.data1 = new Vector<Vector<String>>();
		this.header1 = new Vector<String>();
		this.header1.add("name");
		this.header1.add("value");
		this.tableau1 = new JTable(data1, header1);
		this.tableau1.getSelectionModel().addListSelectionListener(this);
		this.tableau1.addMouseListener(this);
		this.scroll1 = new JScrollPane(this.tableau1);
		this.panel1.add(this.scroll1);
		this.tabbedPane.addTab("Super Context", panel1);

		this.panel2 = new JPanel();
		this.panel2.setLayout(new BoxLayout(this.panel2, BoxLayout.PAGE_AXIS));
		this.data2 = new Vector<Vector<String>>();
		this.header2 = new Vector<String>();
		this.header2.add("name");
		this.header2.add("value");
		this.tableau2 = new JTable(data2, header2);
		this.tableau2.getSelectionModel().addListSelectionListener(this);
		this.tableau2.addMouseListener(this);
		this.scroll2 = new JScrollPane(this.tableau2);
		this.panel2.add(scroll2);
		this.tabbedPane.addTab("Functional Context", panel2);

		this.panel3 = new JPanel();
		this.panel3.setLayout(new BoxLayout(this.panel3, BoxLayout.PAGE_AXIS));
		this.data3 = new Vector<Vector<String>>();
		this.header3 = new Vector<String>();
		this.header3.add("name");
		this.header3.add("value");
		this.tableau3 = new JTable(data3, header3);
		this.tableau3.getSelectionModel().addListSelectionListener(this);
		this.tableau3.addMouseListener(this);
		this.scroll3 = new JScrollPane(this.tableau3);
		this.panel3.add(scroll3);
		this.tabbedPane.addTab("Env", panel3);

		this.panel4 = new JPanel();
		this.panel4.setLayout(new BoxLayout(this.panel4, BoxLayout.PAGE_AXIS));
		this.textlog = new JTextArea();
		this.textlog.setLineWrap(false);
		this.scroll4 = new JScrollPane(this.textlog);
		this.scroll4.setAutoscrolls(false);
		this.panel4.add(this.scroll4);
		this.tabbedPane.addTab("Log", panel4);

		JButton refresh = new JButton(I18n._("Refresh"));
		refresh.addActionListener(this);
		refresh.setActionCommand("refresh");
		this.panel.add(tabbedPane);
		this.text = new JTextArea();
		this.text.setLineWrap(true);
		this.text.setRows(15);
		this.scrolltext = new JScrollPane(this.text);

		this.panel.add(this.scrolltext);
		this.panel.add(refresh);
		this.setContentPane(panel);
	}

	public void refresh(JTable table, Vector<Vector<String>> data, Map<String, Object> context)
	{
		data.clear();
		for (Iterator i = context.keySet().iterator(); i.hasNext();)
		{
			String key = (String) i.next();
			Vector<String> tmp = new Vector<String>();
			tmp.add(key);
			if (context.get(key) instanceof String)
			{
				tmp.add((String) context.get(key));
			} else if (context.get(key) instanceof DASGeneric)
			{
				tmp.add("Code:" + ((DASGeneric) context.get(key)).getCode() + ",Name:" + ((DASGeneric) context.get(key)).getName());
			}
			else
			{

				if (context.get(key) != null)
				{
					tmp.add(context.get(key).toString());
				} else
				{
					tmp.add("null");
				}
			}
			data.add(tmp);
		}
		table.repaint();
		table.setVisible(false);
		table.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ("refresh".equals(e.getActionCommand()))
		{
			this.refresh(this.tableau1, this.data1, this.controller.getSuper_context());
			this.refresh(this.tableau3, this.data3, this.controller.getEnv());
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		if (lsm.isSelectionEmpty())
		{

		} else
		{
			int selectedRow = lsm.getMinSelectionIndex();
			this.text.setText(((JTable) this.actual).getValueAt(selectedRow, 0) + " --> " + ((JTable) this.actual).getValueAt(selectedRow, 1));
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{

	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
		this.actual = (JComponent) arg0.getComponent();

	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{

	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{

	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{

	}

	public void setlog(String str)
	{
		int max = 200 * 10000;
		this.log += str;
		this.textlog.setText(this.log);
		if (this.log.length() > max)
			this.log = this.log.substring(this.log.length() - max, this.log.length());
		this.refresh(this.tableau1, this.data1, this.controller.getSuper_context());
		this.refresh(this.tableau3, this.data3, this.controller.getEnv());
	}

	public void clearlog()
	{
		this.log = "";
		this.textlog.setText(this.log);
	}
}
