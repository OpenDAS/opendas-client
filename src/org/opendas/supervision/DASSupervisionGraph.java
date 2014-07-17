package org.opendas.supervision;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.UnitType;
import org.opendas.gui.DASPanel;
import org.opendas.gui.DASPickerCalendarTimeUnit;
import org.opendas.gui.DASPickerNumber60;
import org.opendas.supervision.DASSupervisionPanel.DASSupervisor;
import org.opendas.translate.I18n;


public class DASSupervisionGraph extends JFrame
{

	private static final long		serialVersionUID	= 1L;
	private DASPanel				dasPanel;
	private ChartPanel				panel				= null;
	private TimeSeries				timeSeries;
	private String					graphTime			= "30m";
	final JButton					customNumberButton	= new JButton("30");
	final JButton					customTypeButton	= new JButton("Minute(s)");
	private RealTimeTimer			rtt;
	private String					type;
	private boolean					isGraphLine;
	private HashMap<Date, String>	hl;
	private Object					colorList;
	private String					title;
	private DASSupervisor			sup;
	private int						screenWidth			= (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
									screenHeight 		= (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	public DASSupervisionGraph(DASSupervisor sup)
	{
		super("Graph");
		this.sup = sup;
		this.dasPanel = sup.getPanelSup().getPanelDAS();
		this.graphTime = sup.getGraphTime();
		this.type = sup.getGraphType();
		this.isGraphLine = sup.isGraphLine();
		this.hl = sup.getHistoriqueValue();
		
		if(sup.getColorMethod().equals("num") && !sup.getColorNum().isEmpty()){
			this.colorList = sup.getColorNum();
		}else if(sup.getColorMethod().equals("liste") && !sup.getColorList().isEmpty()){
			this.colorList = sup.getColorList();			
		}
		
		this.setLayout(null);
		this.getContentPane().setBackground(Color.WHITE);
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(screenWidth, screenHeight);
		
		//TODO for screen cover, no use of popup could be good
		//this.setSize((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
		//(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 140);
		//this.setLocation(0, 140);
		
		this.setResizable(false);
		dasPanel.SetWindowEnabled(false);

		//CLOSE BUTTON
		JButton closeButton = new JButton(I18n._("CLOSE"));
		closeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				rtt.stop();
				setVisible(false);
				dasPanel.SetWindowEnabled(true);
			}
		});
		
		this.add(closeButton);
		closeButton.setBounds((screenWidth-(5+175)), (screenHeight-110), 175, 50);

		//CUSTOM BUTTONS
		
		customNumberButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				int oldNum = Integer.parseInt(customNumberButton.getText());
				if(customTypeButton.getText().equals("Week(s)")){
					oldNum = oldNum*7;
				}
				int newNum = new DASPickerNumber60(dasPanel.getFrame(), oldNum, false).setPickedDate();
				//CHANGE NUMBER
				customNumberButton.setText(""+newNum);
				
				if(customTypeButton.getText().equals("Week(s)")){
					graphTime = (newNum*7)+"D";
					graphTime = graphTime.replace(""+oldNum, ""+newNum);

				}else{
					graphTime = graphTime.replace(""+oldNum, ""+newNum);
				}
				//THEN CHANGE GRAPH
				buildPanelGraph(dasPanel, type, isGraphLine, hl, colorList);
			}
		});
		
		this.add(customNumberButton);
		customNumberButton.setBounds(500, (screenHeight-100), 50, 40);


		customTypeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				String oldNum = customTypeButton.getText();
				if(oldNum.equals("Week(s)")){
					oldNum = "D";
				}else if(oldNum.equals("Second(s)")){
					oldNum = "s";
				}else if(oldNum.equals("Minute(s)")){
					oldNum = "m";
				}else if(oldNum.equals("Hour(s)")){
					oldNum = "h";
				}else if(oldNum.equals("Day(s)")){
					oldNum = "D";
				}else if(oldNum.equals("Month(s)")){
					oldNum = "M";
				}else if(oldNum.equals("Year(s)")){
					oldNum = "Y";
				}
						
				String newNum = new DASPickerCalendarTimeUnit(dasPanel.getFrame(), customTypeButton.getText()).setPickedDate();
				//CHANGE NUMBER
				customTypeButton.setText(""+newNum);
				
				if(newNum.equals("Week(s)")){
					graphTime = (Integer.parseInt(customNumberButton.getText())*7)+"D";
				}else{
					if(newNum.equals("Second(s)")){
						newNum = "s";
					}else if(newNum.equals("Minute(s)")){
						newNum = "m";
					}else if(newNum.equals("Hour(s)")){
						newNum = "h";
					}else if(newNum.equals("Day(s)")){
						newNum = "D";
					}else if(newNum.equals("Month(s)")){
						newNum = "M";
					}else if(newNum.equals("Year(s)")){
						newNum = "Y";
					}
					graphTime = customNumberButton.getText()+newNum;
				}
				//THEN CHANGE GRAPH
				buildPanelGraph(dasPanel, type, isGraphLine, hl, colorList);
			}
		});
		
		this.add(customTypeButton);
		customTypeButton.setBounds(555, (screenHeight-100), 120, 40);


		//MOIS BUTTON
		final JButton monthButton = new JButton(I18n._("1 Month"));
		monthButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				graphTime = "1M";
				buildPanelGraph(dasPanel, type, isGraphLine, hl, colorList);
			}
		});
		
		this.add(monthButton);
		monthButton.setBounds(5, (screenHeight-100), 100, 40);
		
		//SEMAINE BUTTON
		JButton weekButton = new JButton(I18n._("1 Week"));
		weekButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				graphTime = "7D";
				buildPanelGraph(dasPanel, type, isGraphLine, hl, colorList);
			}
		});
		
		this.add(weekButton);
		weekButton.setBounds(110, (screenHeight-100), 100, 40);
		
		//JOUR BUTTON
		JButton dayButton = new JButton(I18n._("1 Day"));
		dayButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				graphTime = "24h";
				buildPanelGraph(dasPanel, type, isGraphLine, hl, colorList);
			}
		});
		
		this.add(dayButton);
		dayButton.setBounds(215, (screenHeight-100), 100, 40);
		
		//HOUR BUTTON
		JButton hourButton = new JButton(I18n._("1 Hour"));
		hourButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				graphTime = "1h";
				buildPanelGraph(dasPanel, type, isGraphLine, hl, colorList);
			}
		});
		
		this.add(hourButton);
		hourButton.setBounds(320, (screenHeight-100), 100, 40);

		// GRAPH PANEL
		buildPanelGraph(dasPanel, type, isGraphLine, hl, colorList);
		
		setVisible(true);
	}

	public void upGraphValue(String value){
		if(value == null){
			if(this.getTimeSeries().getItemCount() > 0){
				this.getTimeSeries().addOrUpdate(new Second(new Date()), this.getTimeSeries().getValue(this.getTimeSeries().getItemCount()-1));
			}
		}else{
			this.getTimeSeries().addOrUpdate(new Second(new Date()), Double.parseDouble(value));
		}
	}

	public void removeGraphFirstDate(){
		this.getTimeSeries().delete(0,0);
	}

	@SuppressWarnings("unchecked")
	public void buildPanelGraph(DASPanel dP, String type, boolean isGraphLine, HashMap<Date,String> hl, Object colorList){

		JFreeChart chart = null;

		if(type.equals("continuous")){
			chart = buildXYChartDateAxis((HashMap<HashMap<String, Integer>, Color>)colorList, hl, false, true);
		}
		else if(type.equals("discrete"))
		{
			chart = buildXYChartDateAxis((HashMap<HashMap<String, Integer>, Color>)colorList, hl, isGraphLine, false);	
		}
		else if(type.equals("liste"))
		{
			chart = buildXYChartListe((HashMap<String, Color>)colorList, hl, isGraphLine, false);
		}

		panel = new ChartPanel(chart);
		panel.getChartRenderingInfo().setEntityCollection(null);

		this.add(panel);
		panel.setBounds(0, 0, screenWidth, (screenHeight-130));

		rtt = new RealTimeTimer(1000, this);
		rtt.start();

		panel.updateUI();
	}

	public Date maxdateRef = new Date();
	public Date mindateRef = new Date();

	DateAxis dateAxis;

	private JFreeChart buildXYChartListe(HashMap<String, Color> cn, HashMap<Date,String> hl, boolean isDiscretLine, boolean isContinue) {

		dateAxis = new DateAxis("Temps");

		NumberAxis valueAxis = new NumberAxis();

		TimeSeriesCollection dataset = createTimeDataset(hl, dateAxis);

		DASXYAreaRenderer2 renderer = new DASXYAreaRenderer2(StandardXYItemRenderer.SHAPES_AND_LINES, cn);

		XYPlot plot = new XYPlot(dataset, dateAxis, valueAxis, renderer);
		valueAxis.setVisible(false);
		valueAxis.setRange(0.0, 1.0);

		String code = " "+sup.getCode();
		JFreeChart chart = new JFreeChart("Supervisor"+code+title, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
		chart.setBackgroundPaint(java.awt.Color.WHITE);

		return chart;
	}

	private JFreeChart buildXYChartDateAxis(HashMap<HashMap<String,Integer>,Color> cn, HashMap<Date,String> hl, boolean isDiscretLine, boolean isContinue) {

		dateAxis = new DateAxis("Temps");

		NumberAxis valueAxis = new NumberAxis();

		TimeSeriesCollection dataset = createTimeDataset(hl, dateAxis);

		DASStandardXYItemRenderer renderer = new DASStandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES, cn);
		renderer.setContinue(isContinue);
		renderer.setDiscretWithLine(isDiscretLine);

		XYPlot plot = new XYPlot(dataset, dateAxis, valueAxis, renderer);

		String code = " "+sup.getCode();
		JFreeChart chart = new JFreeChart("Supervisor"+code+title, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
		chart.setBackgroundPaint(java.awt.Color.WHITE);

		return chart;
	}
	
	@SuppressWarnings("deprecation")
	private TimeSeriesCollection createTimeDataset(HashMap<Date, String> hl, DateAxis dateAxis)
	{
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		setTimeSeries(new TimeSeries("Val"));

		DateTickUnit unit = null;
		DateFormat chartFormatter = null;
		title = "Time";
		String legend = "";
		boolean isVertical = false;

		Calendar cal = Calendar.getInstance();
		int tmpNum = 0;
		if(graphTime.contains("s")){
			tmpNum = Integer.parseInt((graphTime.substring(0, graphTime.indexOf("s"))));
			cal.add(Calendar.SECOND, - tmpNum);
			customTypeButton.setText(I18n._("Second(s)"));
			title = " - Last "+tmpNum+" "+ I18n._("Second(s)");

			
		}else if(graphTime.contains("m")){
			tmpNum = Integer.parseInt((graphTime.substring(0, graphTime.indexOf("m"))));
			cal.add(Calendar.MINUTE, - Integer.parseInt((graphTime.substring(0, graphTime.indexOf("m")))));
			customTypeButton.setText(I18n._("Minute(s)"));
			title = " - Last "+tmpNum+" "+ I18n._("Minute(s)");

			
		}else if(graphTime.contains("h")){
			tmpNum = Integer.parseInt((graphTime.substring(0, graphTime.indexOf("h"))));
			cal.add(Calendar.HOUR, - Integer.parseInt((graphTime.substring(0, graphTime.indexOf("h")))));
			customTypeButton.setText(I18n._("Hour(s)"));
			title = " - Last "+tmpNum+I18n._("Hour(s)");

			
		}else if(graphTime.contains("D")){
			tmpNum = Integer.parseInt((graphTime.substring(0, graphTime.indexOf("D"))));
			cal.add(Calendar.DAY_OF_MONTH, - Integer.parseInt((graphTime.substring(0, graphTime.indexOf("D")))));
			customTypeButton.setText(I18n._("Day(s)"));
			title = " - Last "+tmpNum+" "+I18n._("Day(s)");
			
		}else if(graphTime.contains("M")){
			tmpNum = Integer.parseInt((graphTime.substring(0, graphTime.indexOf("M"))));
			cal.add(Calendar.MONTH, - Integer.parseInt((graphTime.substring(0, graphTime.indexOf("M")))));
			customTypeButton.setText(I18n._("Month(s)"));
			title = " - Last "+tmpNum+" "+I18n._("Month(s)");
			
		}else if(graphTime.contains("Y")){
			tmpNum = Integer.parseInt((graphTime.substring(0, graphTime.indexOf("Y"))));
			cal.add(Calendar.YEAR, - Integer.parseInt((graphTime.substring(0, graphTime.indexOf("Y")))));
			customTypeButton.setText(I18n._("Year(s)"));
			title = " - Last "+tmpNum+" "+I18n._("Year(s)");
		}

		customNumberButton.setText(""+tmpNum);
		
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.SECOND, -60);
		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MINUTE, -5);
		Calendar cal3 = Calendar.getInstance();
		cal3.add(Calendar.HOUR, -1);
		Calendar cal4 = Calendar.getInstance();
		cal4.add(Calendar.DAY_OF_MONTH, -1);
		Calendar cal9 = Calendar.getInstance();
		cal9.add(Calendar.DAY_OF_MONTH, -7);
		Calendar cal5 = Calendar.getInstance();
		cal5.add(Calendar.MONTH, -1);
		Calendar cal6 = Calendar.getInstance();
		cal6.add(Calendar.YEAR, -1);
		Calendar cal7 = Calendar.getInstance();
		cal7.add(Calendar.YEAR, -5);
		Calendar cal8 = Calendar.getInstance();
		cal8.add(Calendar.YEAR, -10);

		if(cal.getTime().after(cal1.getTime()) || cal.getTime().equals(cal1.getTime())){
			unit = new DateTickUnit(DateTickUnit.SECOND,5);
			chartFormatter = new SimpleDateFormat("HH:mm:ss");
			legend = "Second(s)";
			//isVertical = true;
		}else if(cal.getTime().after(cal2.getTime()) || cal.getTime().equals(cal2.getTime())){
			unit = new DateTickUnit(DateTickUnit.SECOND,60);
			chartFormatter = new SimpleDateFormat("HH:mm");
			legend = "Minute(s)";
			//isVertical = true;
		}else if(cal.getTime().after(cal3.getTime()) || cal.getTime().equals(cal3.getTime())){
			unit = new DateTickUnit(DateTickUnit.MINUTE,5);
			chartFormatter = new SimpleDateFormat("HH:mm");
			legend = "Minute(s)";
			//isVertical = true;
		}else if(cal.getTime().after(cal4.getTime()) || cal.getTime().equals(cal4.getTime())){
			unit = new DateTickUnit(DateTickUnit.HOUR,1);
			chartFormatter = new SimpleDateFormat("HH");
			legend = "Hour(s)";
		}else if(cal.getTime().after(cal9.getTime()) || cal.getTime().equals(cal9.getTime())){
			unit = new DateTickUnit(DateTickUnit.DAY,1);
			chartFormatter = new SimpleDateFormat("dd - MMM");
			legend = "Day(s)";
		}else if(cal.getTime().after(cal5.getTime()) || cal.getTime().equals(cal5.getTime())){
			unit = new DateTickUnit(DateTickUnit.DAY,3);
			chartFormatter = new SimpleDateFormat("dd - MMM");
			legend = "Day(s)";
		}else if(cal.getTime().after(cal6.getTime()) || cal.getTime().equals(cal6.getTime())){
			unit = new DateTickUnit(DateTickUnit.MONTH,1);
			chartFormatter = new SimpleDateFormat("MMMM");
			legend = "Month(s)";
		}else if(cal.getTime().after(cal7.getTime()) || cal.getTime().equals(cal7.getTime())){
			unit = new DateTickUnit(DateTickUnit.YEAR,1);
			chartFormatter = new SimpleDateFormat("yyyy");
			legend = "Year(s)";
		}else if(cal.getTime().after(cal8.getTime()) || cal.getTime().equals(cal8.getTime())){
			unit = new DateTickUnit(DateTickUnit.YEAR,2);
			chartFormatter = new SimpleDateFormat("yyyy");
			legend = "Year(s)";
		}else{
			unit = new DateTickUnit(DateTickUnit.YEAR,10);
			chartFormatter = new SimpleDateFormat("yyyy");
			legend = "Year(s)";
		}
		
		mindateRef = cal.getTime();
		maxdateRef = new Date();
		dateAxis.setMinimumDate(mindateRef);
		dateAxis.setMaximumDate(maxdateRef);
		dateAxis.setLabel(legend);
		dateAxis.setVerticalTickLabels(isVertical);
		dateAxis.setTickUnit(unit);
		dateAxis.setDateFormatOverride(chartFormatter);

		//		TODO TEST FULL DATA
		//		hl = new HashMap<Date, String>();
		//		for(int i = -25; i < 300; i=i+25){
		//			Random random = new Random();
		//			//hltest.put(new Date((random.nextInt(110)+1), (random.nextInt(11)+1), (random.nextInt(27)+1), (random.nextInt(23)+1), (random.nextInt(59)+1), (random.nextInt(59)+1)), tmpHM);
		//			//hltest.put(new Date((random.nextInt(110)+1), (random.nextInt(11)+1), (random.nextInt(27)+1), (random.nextInt(23)+1), (random.nextInt(59)+1), (random.nextInt(59)+1)), tmpHM);
		//			//hltest.put(new Date(111, 8, 16, 12, (random.nextInt(58)+1), (random.nextInt(58)+1)), tmpHM);
		//			hltest.put(new Date(111, 8, 16, (random.nextInt(23)+1), (random.nextInt(58)+1), (random.nextInt(58)+1)), ""+i); //""+(random.nextInt(4))
		//			//hltest.put(new Date(111, (random.nextInt(11)+1), (random.nextInt(27)+1), (random.nextInt(23)+1), (random.nextInt(58)+1), (random.nextInt(58)+1)), tmpHM);
		//			//hltest.put(new Date((random.nextInt(110)+1), (random.nextInt(11)+1), (random.nextInt(27)+1), (random.nextInt(23)+1), (random.nextInt(58)+1), (random.nextInt(58)+1)), tmpHM);
		//		}	

		Map<Date,String> m1 = new TreeMap<Date,String>(hl);
		for(Entry<Date, String> o : m1.entrySet()){
			if(o.getKey().after(cal.getTime())){
				getTimeSeries().addOrUpdate(new Second(o.getKey()), Double.parseDouble(o.getValue()));
			}
		}

		dataset.addSeries(getTimeSeries());
		return dataset;
	}

	public void setTimeSeries(TimeSeries timeSeries)
	{
		this.timeSeries = timeSeries;
	}

	public TimeSeries getTimeSeries()
	{
		return timeSeries;
	}

	public RealTimeTimer getRtt()
	{
		return rtt;
	}

	public void setRtt(RealTimeTimer rtt)
	{
		this.rtt = rtt;
	}

	public String getGraphTime()
	{
		return graphTime;
	}

	public void setGraphTime(String graphTime)
	{
		this.graphTime = graphTime;
	}

	class DASStandardXYItemRenderer extends StandardXYItemRenderer {

		private HashMap<HashMap<String, Integer>, Color> cn = new HashMap<HashMap<String, Integer>, Color>();
		private boolean isDiscretWithLine = true, isContinue = true;

		public void setDiscretWithLine(boolean isDiscretWithLine){
			this.isDiscretWithLine = isDiscretWithLine;
		}

		public void setContinue(boolean isContinue){
			this.isContinue = isContinue;
		}

		public DASStandardXYItemRenderer(int type, HashMap<HashMap<String, Integer>, Color> cn) {

			super();
			if ((type & SHAPES) != 0) {
				setBaseShapesVisible(true);
			}
			if ((type & LINES) != 0) {
				setPlotLines(true);
			}
			if ((type & IMAGES) != 0) {
				setPlotImages(true);
			}
			if ((type & DISCONTINUOUS) != 0) {
				setPlotDiscontinuous(true);
			}

			this.cn = cn;

			setBaseShapesFilled(true);
			setLegendLine(new Line2D.Double(-7.0, 0.0, 7.0, 0.0));
			setDrawSeriesLineAsPath(false);
		}

		public Color getPaintWithMinMax(Number yint){
			for(Entry<HashMap<String, Integer>, Color> entry : cn.entrySet()){
				boolean min = false, max = false;
				if(entry.getKey().containsKey("min")){
					min = true;
				}
				if(entry.getKey().containsKey("max")){
					max = true;
				}
				if(min || max){
					if(min && !max){
						if(entry.getKey().get("min") < yint.intValue()){
							return entry.getValue();
						}
					}else if(!min && max){
						if(yint.intValue() < entry.getKey().get("max")){
							return entry.getValue();
						}
					}else if(min && max){
						if(entry.getKey().get("min") <= yint.intValue() && yint.intValue() <= entry.getKey().get("max")){
							return  entry.getValue();
						}
					}
				}
			}
			return Color.black;
		}

		public void drawItem(Graphics2D g2,
				XYItemRendererState state,
				Rectangle2D dataArea,
				PlotRenderingInfo info,
				XYPlot plot,
				ValueAxis domainAxis,
				ValueAxis rangeAxis,
				XYDataset dataset,
				int series,
				int item,
				CrosshairState crosshairState,
				int pass) {

			boolean itemVisible = getItemVisible(series, item);

			// setup for collecting optional entity info...
			Shape entityArea = null;
			EntityCollection entities = null;
			if (info != null) {
				entities = info.getOwner().getEntityCollection();
			}

			PlotOrientation orientation = plot.getOrientation();
			Paint paint = getItemPaint(series, item);
			Stroke seriesStroke = getItemStroke(series, item);
			g2.setPaint(paint);
			g2.setStroke(seriesStroke);

			// get the data point...
			double x1 = dataset.getXValue(series, item);
			double y1 = dataset.getYValue(series, item);
			if (Double.isNaN(x1) || Double.isNaN(y1)) {
				itemVisible = false;
			}

			RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
			RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
			double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
			double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

			if (getPlotLines()) {
				if (this.getDrawSeriesLineAsPath()) {
					State s = (State) state;
					if (s.getSeriesIndex() != series) {
						// we are starting a new series path
						s.seriesPath.reset();
						s.setLastPointGood(false);
						s.setSeriesIndex(series);
					}

					// update path to reflect latest point
					if (itemVisible && !Double.isNaN(transX1)
							&& !Double.isNaN(transY1)) {
						float x = (float) transX1;
						float y = (float) transY1;
						if (orientation == PlotOrientation.HORIZONTAL) {
							x = (float) transY1;
							y = (float) transX1;
						}
						if (s.isLastPointGood()) {
							s.seriesPath.lineTo(x, y);
						}
						else {
							s.seriesPath.moveTo(x, y);
						}
						s.setLastPointGood(true);
					}
					else {
						s.setLastPointGood(false);
					}
					if (item == dataset.getItemCount(series) - 1) {
						if (s.getSeriesIndex() == series) {
							// draw path
							g2.setStroke(lookupSeriesStroke(series));
							g2.setPaint(lookupSeriesPaint(series));
							g2.draw(s.seriesPath);
						}
					}
				}

				else if (item != 0 && itemVisible && (isDiscretWithLine || isContinue)) {
					// get the previous data point...
					double x0 = dataset.getXValue(series, item - 1);
					double y0 = dataset.getYValue(series, item - 1);
					if (!Double.isNaN(x0) && !Double.isNaN(y0)) {
						boolean drawLine = true;
						if (getPlotDiscontinuous()) {
							// only draw a line if the gap between the current and
							// previous data point is within the threshold
							int numX = dataset.getItemCount(series);
							double minX = dataset.getXValue(series, 0);
							double maxX = dataset.getXValue(series, numX - 1);
							if (this.getGapThresholdType() == UnitType.ABSOLUTE) {
								drawLine = Math.abs(x1 - x0) <= this.getGapThreshold();
							}
							else {
								drawLine = Math.abs(x1 - x0) <= ((maxX - minX)
										/ numX * getGapThreshold());
							}
						}
						if (drawLine) {
							double transX0 = domainAxis.valueToJava2D(x0, dataArea,
									xAxisLocation);
							double transY0 = rangeAxis.valueToJava2D(y0, dataArea,
									yAxisLocation);

							// only draw if we have good values
							if (Double.isNaN(transX0) || Double.isNaN(transY0)
									|| Double.isNaN(transX1) || Double.isNaN(transY1)) {
								g2.dispose();
								return;
							}

							if (orientation == PlotOrientation.HORIZONTAL) {
								state.workingLine.setLine(transY0, transX0,
										transY1, transX1);
							}
							else if (orientation == PlotOrientation.VERTICAL) {
								if(isContinue){
									g2.setPaint(getPaintWithMinMax(dataset.getYValue(series, item - 1)));
									state.workingLine.setLine(transX0, transY0,
											transX1, transY0);
								}else{
									g2.setPaint(Color.BLACK);
									state.workingLine.setLine(transX0, transY0,
											transX1, transY1);
								}
							}

							if (state.workingLine.intersects(dataArea)) {
								g2.draw(state.workingLine);
							}
						}
					}
				}
			}

			// we needed to get this far even for invisible items, to ensure that
			// seriesPath updates happened, but now there is nothing more we need
			// to do for non-visible items...
			if (!itemVisible) {
				g2.dispose();
				return;
			}

			if (item == dataset.getItemCount(series) -1 || (getBaseShapesVisible() && !isContinue)) {

				Shape shape = getItemShape(series, item);
				if (orientation == PlotOrientation.HORIZONTAL) {
					shape = ShapeUtilities.createTranslatedShape(shape, transY1,
							transX1);
				}
				else if (orientation == PlotOrientation.VERTICAL) {
					shape = ShapeUtilities.createTranslatedShape(shape, transX1,
							transY1);
					g2.setPaint(getPaintWithMinMax(dataset.getYValue(series, item)));
				}
				if (shape.intersects(dataArea)) {
					if (getItemShapeFilled(series, item)) {
						g2.fill(shape);
					}
					else {
						g2.draw(shape);
					}
				}
				entityArea = shape;

			}

			if (getPlotImages()) {
				Image image = getImage(plot, series, item, transX1, transY1);
				if (image != null) {
					Point hotspot = getImageHotspot(plot, series, item, transX1,
							transY1, image);
					g2.drawImage(image, (int) (transX1 - hotspot.getX()),
							(int) (transY1 - hotspot.getY()), null);
					entityArea = new Rectangle2D.Double(transX1 - hotspot.getX(),
							transY1 - hotspot.getY(), image.getWidth(null),
							image.getHeight(null));
				}

			}

			double xx = transX1;
			double yy = transY1;
			if (orientation == PlotOrientation.HORIZONTAL) {
				xx = transY1;
				yy = transX1;
			}

			// draw the item label if there is one...
			if (isItemLabelVisible(series, item)) {
				drawItemLabel(g2, orientation, dataset, series, item, xx, yy,
						(y1 < 0.0));
			}

			int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
			int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
			updateCrosshairValues(crosshairState, x1, y1, domainAxisIndex,
					rangeAxisIndex, transX1, transY1, orientation);

			// add an entity for the item...
			if (entities != null && isPointInRect(dataArea, xx, yy)) {
				addEntity(entities, entityArea, dataset, series, item, xx, yy);
			}

		}


	}

	class DASXYAreaRenderer2 extends XYAreaRenderer2 {

		private HashMap<String, Color> cn = new HashMap<String, Color>();

		public DASXYAreaRenderer2(int type, HashMap<String, Color> cn) {
			this.cn = cn;
		}

		public Color getPaintOfValue(Number yint){
			for(Entry<String, Color> entry : cn.entrySet()){
				if((entry.getKey()).equals(yint.toString())){
					return entry.getValue();
				}
			}
			return Color.black;
		}

		public void drawItem(Graphics2D g2,
				XYItemRendererState state,
				Rectangle2D dataArea,
				PlotRenderingInfo info,
				XYPlot plot,
				ValueAxis domainAxis,
				ValueAxis rangeAxis,
				XYDataset dataset,
				int series,
				int item,
				CrosshairState crosshairState,
				int pass) {

			if (!getItemVisible(series, item)) {
				return;
			}
			// get the data point...
			double x1 = dataset.getXValue(series, item);
			double y1 = 1.0;
			double transX1 = domainAxis.valueToJava2D(x1, dataArea,
					plot.getDomainAxisEdge());
			double transY1 = rangeAxis.valueToJava2D(y1, dataArea,
					plot.getRangeAxisEdge());

			// get the previous point and the next point so we can calculate a
			// "hot spot" for the area (used by the chart entity)...
			double x0 = dataset.getXValue(series, Math.max(item - 1, 0));
			double y0 = 1.0;
			double transX0 = domainAxis.valueToJava2D(x0, dataArea,
					plot.getDomainAxisEdge());
			double transY0 = rangeAxis.valueToJava2D(y0, dataArea,
					plot.getRangeAxisEdge());

			int itemCount = dataset.getItemCount(series);
			double x2 = dataset.getXValue(series, Math.min(item + 1,
					itemCount - 1));
			double y2 = 1.0;
			double transX2 = domainAxis.valueToJava2D(x2, dataArea,
					plot.getDomainAxisEdge());
			double transY2 = rangeAxis.valueToJava2D(y2, dataArea,
					plot.getRangeAxisEdge());

			double transZero = rangeAxis.valueToJava2D(0.0, dataArea,
					plot.getRangeAxisEdge());
			Polygon hotspot = null;
			if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
				hotspot = new Polygon();
				hotspot.addPoint((int) transZero,
						(int) ((transX0 + transX1) / 2.0));
				hotspot.addPoint((int) ((transY0 + transY1) / 2.0),
						(int) ((transX0 + transX1) / 2.0));
				hotspot.addPoint((int) transY1, (int) transX1);
				hotspot.addPoint((int) ((transY1 + transY2) / 2.0),
						(int) ((transX1 + transX2) / 2.0));
				hotspot.addPoint((int) transZero,
						(int) ((transX1 + transX2) / 2.0));
			}
			else {  // vertical orientation
				hotspot = new Polygon();
				hotspot.addPoint((int) ((transX0 + transX1) / 2.0),
						(int) transZero);
				hotspot.addPoint((int) ((transX0 + transX1) / 2.0),
						(int) ((transY0 + transY1) / 2.0));
				hotspot.addPoint((int) transX1, (int) transY1);
				hotspot.addPoint((int) ((transX1 + transX2) / 2.0),
						(int) ((transY1 + transY2) / 2.0));
				hotspot.addPoint((int) ((transX1 + transX2) / 2.0),
						(int) transZero);
			}

			PlotOrientation orientation = plot.getOrientation();
			Stroke stroke = getItemStroke(series, item);
			g2.setPaint(getPaintOfValue(dataset.getYValue(series, item)));
			g2.setStroke(stroke);

			// Check if the item is the last item for the series.
			// and number of items > 0.  We can't draw an area for a single point.
			g2.fill(hotspot);

			// draw an outline around the Area.
			if (isOutline()) {
				g2.setStroke(lookupSeriesOutlineStroke(series));
				g2.setPaint(lookupSeriesOutlinePaint(series));
				g2.draw(hotspot);
			}
			int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
			int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
			updateCrosshairValues(crosshairState, x1, y1, domainAxisIndex,
					rangeAxisIndex, transX1, transY1, orientation);

			// collect entity and tool tip information...
			if (state.getInfo() != null) {
				EntityCollection entities = state.getEntityCollection();
				if (entities != null && hotspot != null) {
					String tip = null;
					XYToolTipGenerator generator = getToolTipGenerator(series,
							item);
					if (generator != null) {
						tip = generator.generateToolTip(dataset, series, item);
					}
					String url = null;
					if (getURLGenerator() != null) {
						url = getURLGenerator().generateURL(dataset, series, item);
					}
					XYItemEntity entity = new XYItemEntity(hotspot, dataset,
							series, item, tip, url);
					entities.add(entity);
				}
			}
		}
	}
}

@SuppressWarnings("serial")
class RealTimeTimer extends Timer implements ActionListener {

	/**
	 * Constructor.
	 *
	 * @param interval  the interval (in milliseconds)
	 */

	DASSupervisionGraph gs;

	RealTimeTimer(int interval, DASSupervisionGraph gs) {
		super(interval, null);
		this.gs = gs;
		addActionListener(this);
	}

	/**
	 * Adds a new free/total memory reading to the dataset.
	 *
	 * @param event  the action event.
	 */
	public void actionPerformed(ActionEvent event) {
		Calendar cal = Calendar.getInstance();
		int tmpNum = 0;
		if(gs.getGraphTime().contains("s")){
			tmpNum = Integer.parseInt((gs.getGraphTime().substring(0, gs.getGraphTime().indexOf("s"))));
			cal.add(Calendar.SECOND, - tmpNum);
		}else if(gs.getGraphTime().contains("m")){
			tmpNum = Integer.parseInt((gs.getGraphTime().substring(0, gs.getGraphTime().indexOf("m"))));
			cal.add(Calendar.MINUTE, - Integer.parseInt((gs.getGraphTime().substring(0, gs.getGraphTime().indexOf("m")))));
		}else if(gs.getGraphTime().contains("h")){
			tmpNum = Integer.parseInt((gs.getGraphTime().substring(0, gs.getGraphTime().indexOf("h"))));
			cal.add(Calendar.HOUR, - Integer.parseInt((gs.getGraphTime().substring(0, gs.getGraphTime().indexOf("h")))));
		}else if(gs.getGraphTime().contains("D")){
			tmpNum = Integer.parseInt((gs.getGraphTime().substring(0, gs.getGraphTime().indexOf("D"))));
			cal.add(Calendar.DAY_OF_MONTH, - Integer.parseInt((gs.getGraphTime().substring(0, gs.getGraphTime().indexOf("D")))));
		}else if(gs.getGraphTime().contains("M")){
			tmpNum = Integer.parseInt((gs.getGraphTime().substring(0, gs.getGraphTime().indexOf("M"))));
			cal.add(Calendar.MONTH, - Integer.parseInt((gs.getGraphTime().substring(0, gs.getGraphTime().indexOf("M")))));
		}else if(gs.getGraphTime().contains("Y")){
			tmpNum = Integer.parseInt((gs.getGraphTime().substring(0, gs.getGraphTime().indexOf("Y"))));
			cal.add(Calendar.YEAR, - Integer.parseInt((gs.getGraphTime().substring(0, gs.getGraphTime().indexOf("Y")))));
		}
		while(!gs.getTimeSeries().isEmpty() && gs.getTimeSeries().getItemCount() > 0 && gs.getTimeSeries().getDataItem(0).getPeriod().getStart().before(cal.getTime())){
			gs.removeGraphFirstDate();
		}
		gs.mindateRef = cal.getTime();
		gs.maxdateRef = new Date();
		gs.dateAxis.setMinimumDate(gs.mindateRef);
		gs.dateAxis.setMaximumDate(gs.maxdateRef);
	}

}