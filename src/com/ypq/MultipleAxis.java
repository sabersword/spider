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
 * ��ͼ��,��Ҫ��������ǳ��κ���ƥ,�����û�ͼ��Ҫ�����ĸ����ε���ƥ��,�������л�ͼ������һ��DataSet���ݼ�,
 * �����ݼ���������������г���������ƥ������
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
	 * ���ø�ͼ��ʾ��һ��
	 * 
	 * @param race
	 */
	public void setRace(int race) {
		this.race = race;
	}

	/**
	 * ���ø�ͼ��ʾ��һֻ��
	 * 
	 * @param horse
	 */
	public void setHorse(int horse) {
		this.horse = horse;
	}

	/**
	 * ����ʱָ����ͼ����ʾ��һֻ��
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
	 * ����һ��jfreechar,��chart���������ͼ�������(multipleAxis),xyplot����Ϣ
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
		xyplot.getRenderer().setSeriesPaint(BestData.WIN, Color.RED); // Win�ú�ɫ
		xyplot.getRenderer().setSeriesPaint(BestData.PLACE, Color.GREEN); // Place����ɫ
		xyplot.getRenderer().setSeriesPaint(BestData.WIN_PLACE, Color.BLUE);// Win_Place����ɫ
		xyplot.getRenderer().setSeriesStroke(BestData.WIN,
				new BasicStroke(2.0F)); // ���ô�ϸΪ2
		xyplot.getRenderer().setSeriesStroke(BestData.PLACE,
				new BasicStroke(2.0F));
		xyplot.getRenderer().setSeriesStroke(BestData.WIN_PLACE,
				new BasicStroke(2.0F));
		xyplot.setBackgroundPaint(Color.WHITE); // ����ɫΪ��ɫ
		return jfreechart;
	}

	/**
	 * ����DataSet��race��horse�ҵ�����(��win,place,win_place��������),����xy�������
	 * 
	 * @param s
	 * @param race
	 * @param horse
	 * @return
	 */
	private static XYDataset createDataset(String s, int race, int horse) {
		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(); // һ��TimeSeriesCollection��������˶��TimeSeries,ÿһ��TimeSeries�ֱ����win,place,win_place
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
	 * ����һ��������jfreechart��panel��ͼ
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
	 * ����createDataset������������ȥ����xy��
	 */
	public void updateDataSet() {
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		XYDataset xydataset = createDataset("Win", race, horse);
		if (xydataset == null)
			return;
		xyplot.getDomainAxis().setRange(dataSet.minTime(race),
				dataSet.minTime(race) + MINUTES30); // ÿ��updateDataSet��Ҫ�޸ĺ������������ķ�Χ,�����귶ΧΪ��[��ʼʱ��,
													// ��ʼʱ��+30����]
//		xyplot.getRangeAxis().setRange(dataSet.minDiscount(race, horse) - 1,
//				dataSet.maxDiscount(race, horse) + 1); // �����귶Χ��[��С�ۿ�-1, ����ۿ�+1]
		xyplot.getRangeAxis().setRange(75, 100); // �����귶Χ��[��С�ۿ�-1, ����ۿ�+1]
		xyplot.setDataset(xydataset);
	}

	/**
	 * �������DataSet
	 * 
	 * @return
	 */
	public static DataSet getDataSet() {
		return dataSet;
	}

	/**
	 * ���panel
	 * 
	 * @return
	 */
	public ChartPanel getChartpanel() {
		return chartpanel;
	}

	/**
	 * ���jfreechart
	 * 
	 * @return
	 */
	public JFreeChart getJfreechart() {
		return jfreechart;
	}

}
