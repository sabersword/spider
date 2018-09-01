package com.ypq;

import java.awt.BasicStroke;
import java.awt.Color;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.ypq.DataSet.Data;

/**
 * 绘图类,主要构造参数是场次和马匹,表明该绘图需要绘制哪个场次的哪匹马,另外所有绘图对象共享一个DataSet数据集,
 * 该数据集表明当天比赛所有场次所有马匹的数据
 * 
 * @author god
 * 
 */
public class MultipleAxis {
	private ChartPanel chartpanel;
	private JFreeChart jfreechart;
	private int race = 1;
	private int horse = 0;
	private static DataSet dataSet;
	private static final long MINUTES30 = 30 * 60 * 1000;

	/**
	 * 设置该图显示哪一场
	 * 
	 * @param race
	 */
	public void setRace(int race) {
		this.race = race;
	}

	/**
	 * 设置该图显示哪一只马
	 * 
	 * @param horse
	 */
	public void setHorse(int horse) {
		this.horse = horse;
	}

	/**
	 * 构造时指明该图是显示哪一只马
	 * 
	 * @param horse
	 */
	public MultipleAxis(int horse) {
		this.horse = horse;
		if (dataSet == null)
			dataSet = DataSet.getInstance();
		chartpanel = (ChartPanel) createDemoPanel(horse);
		chartpanel.setDomainZoomable(true);
		chartpanel.setRangeZoomable(true);
	}

	/**
	 * 创建一个jfreechar,该chart下面包含了图标的类型(multipleAxis),xyplot的信息
	 * 
	 * @param horse
	 * @return
	 */
	private JFreeChart createChart(int horse) {
		XYDataset xydataset = createDataset("Win", race, horse);
		jfreechart = ChartFactory.createTimeSeriesChart(String.valueOf(horse),
				"", "Discount", xydataset, false, false, false);
		jfreechart.setBorderVisible(true);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		xyplot.setOrientation(PlotOrientation.VERTICAL);
		xyplot.setDomainPannable(true);
		xyplot.setRangePannable(true);
		xyplot.getRangeAxis().setFixedDimension(20D);
		xyplot.getRangeAxis().setAutoRange(false);
		xyplot.getDomainAxis().setAutoRange(false);
		xyplot.getRenderer().setSeriesPaint(BestData.WIN, Color.RED); // Win用红色
		xyplot.getRenderer().setSeriesPaint(BestData.PLACE, Color.GREEN); // Place用绿色
		xyplot.getRenderer().setSeriesPaint(BestData.WIN_PLACE, Color.BLUE);// Win_Place用蓝色
		xyplot.getRenderer().setSeriesStroke(BestData.WIN,
				new BasicStroke(2.0F)); // 设置粗细为2
		xyplot.getRenderer().setSeriesStroke(BestData.PLACE,
				new BasicStroke(2.0F));
		xyplot.getRenderer().setSeriesStroke(BestData.WIN_PLACE,
				new BasicStroke(2.0F));
		xyplot.setBackgroundPaint(Color.WHITE); // 背景色为白色
		return jfreechart;
	}

	/**
	 * 根据DataSet的race和horse找到数据(分win,place,win_place三种数据),生成xy轴的数据
	 * 
	 * @param s
	 * @param race
	 * @param horse
	 * @return
	 */
	private static XYDataset createDataset(String s, int race, int horse) {
		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(); // 一个TimeSeriesCollection里面包含了多个TimeSeries,每一个TimeSeries分别就是win,place,win_place
		TimeSeries timeSeriesWin = new TimeSeries(s);
		for (Data e : DataSet.getInstance().getElement(race, horse)) {
			RegularTimePeriod regularTimePeriod = new Second(e.date);
			timeSeriesWin.addOrUpdate(regularTimePeriod, e.win);
		}
		timeSeriesCollection.addSeries(timeSeriesWin);

		TimeSeries timeSeriesPlace = new TimeSeries(s);
		for (Data e : DataSet.getInstance().getElement(race, horse)) {
			RegularTimePeriod regularTimePeriod = new Second(e.date);
			timeSeriesPlace.addOrUpdate(regularTimePeriod, e.place);
		}
		timeSeriesCollection.addSeries(timeSeriesPlace);

		TimeSeries timeSeriesWinPlace = new TimeSeries(s);
		for (Data e : DataSet.getInstance().getElement(race, horse)) {
			RegularTimePeriod regularTimePeriod = new Second(e.date);
			timeSeriesWinPlace.addOrUpdate(regularTimePeriod, e.win_place);
		}
		timeSeriesCollection.addSeries(timeSeriesWinPlace);

		return timeSeriesCollection;
	}

	/**
	 * 创建一个包含了jfreechart和panel的图
	 * 
	 * @param horse
	 * @return
	 */
	public JPanel createDemoPanel(int horse) {
		JFreeChart jfreechart = createChart(horse);
		ChartPanel chartpanel = new ChartPanel(jfreechart);
		chartpanel.setMouseWheelEnabled(true);
		return chartpanel;
	}

	/**
	 * 根据createDataset生成最新数据去更新xy轴
	 */
	public void updateDataSet() {
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		XYDataset xydataset = createDataset("Win", race, horse);
		if (xydataset == null)
			return;
		xyplot.getDomainAxis().setRange(dataSet.minTime(race),
				dataSet.minTime(race) + MINUTES30); // 每次updateDataSet都要修改横坐标和纵坐标的范围,横坐标范围为最[开始时间,
													// 开始时间+30分钟]
//		xyplot.getRangeAxis().setRange(dataSet.minDiscount(race, horse) - 1,
//				dataSet.maxDiscount(race, horse) + 1); // 纵坐标范围是[最小折扣-1, 最大折扣+1]
		xyplot.getRangeAxis().setRange(75, 100); // 纵坐标范围是[最小折扣-1, 最大折扣+1]
		xyplot.setDataset(xydataset);
	}

	/**
	 * 获得整个DataSet
	 * 
	 * @return
	 */
	public static DataSet getDataSet() {
		return dataSet;
	}

	/**
	 * 获得panel
	 * 
	 * @return
	 */
	public ChartPanel getChartpanel() {
		return chartpanel;
	}

	/**
	 * 获得jfreechart
	 * 
	 * @return
	 */
	public JFreeChart getJfreechart() {
		return jfreechart;
	}

}
