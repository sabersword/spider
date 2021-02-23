package com.ypq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 一个用ArrayList组成的二维数组,每一行代表不同场次,每一列代表不同的马,里面的元素是LinkList组成的按时间排列的折扣.采用单例模式,只能存在一个DataSet
 * @author god
 *
 */
public class DataSet {
	
	/**
	 * DataSet里面具体储存的元素element,主要包含了时间,win,place,win_place.上述4者构成了xy轴的所有数据
	 * @author god
	 *
	 */
	public static class Data implements Comparable<Data>{
		/**
		 * 重写hashCode,主要利用HashSet来去掉时间相同的数据.因为判断两个对象是否相同就是靠hashCode和equals
		 */
		@Override
		public int hashCode() {
			return (int) date.getTime();
		}

		/**
		 * 重写equals,原因同上
		 */
		@Override
		public boolean equals(Object obj) {
			if(date.getTime() == ((Data)obj).date.getTime())
				return true;
			return false;
		}
		
		/**
		 * 重写compareTo,用于Collections.sort()排序用,找出最大和最小的折扣
		 */
		public int compareTo(Data d) {
			double max = Math.max(Math.max(win, place), win_place);
			double dmax = Math.max(Math.max(d.win, d.place), d.win_place);
			return (int)(max - dmax);
		}
		
		/**
		 * 构造函数,将传入的参数写入到成员变量
		 * @param date
		 * @param win
		 * @param place
		 * @param win_place
		 */
		public Data(Date date, double win, double place, double win_place) {
			this.date = date;
			this.win = win;
			this.place = place;
			this.win_place = win_place;
		}

		/**
		 * 构造函数,初始化null日期和win,place,win_place
		 */
		public Data() {
			this.date = null;
			this.win = 0;
			this.place = 0;
			this.win_place = 0;
		}

		/**
		 * 重写toString,主要是记录到文件每一行的时候方便调用
		 * @param race
		 * @param horse
		 * @return
		 */
		public String toString(int race, int horse) {
			return race + "\t" + horse + "\t" + new SimpleDateFormat("HH:mm:ss").format(date) + "\t" + win + "\t" + place + "\t" + win_place + "\t\r\n";
		}
		
		public Date date;
		public double win;
		public double place;
		public double win_place;
	}
	
	private final int RACE_COUNT = 16;			//假设场次不多于15场(去掉场次为0的一场)
	private final int HORSE_COUNT = 16;			//假设马不超过15匹(去掉马匹为0的一匹)
	private String filePath = null;				//记录文件的路径
	private CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Data>>> data;		//使用CopyOnWriteArrayList可以保证线程安全,使用Collections.synchronizedList的话还是会出现线程不安全的情况
	private static final DataSet instance = new DataSet();				//单例模式的实例

	/**
	 * 构造函数,初始化CopyOnWriteArrayList构成的二维数组
	 */
	private DataSet() {
		data = new CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Data>>>();
		for(int race = 0; race < RACE_COUNT; race++) {
			data.add(race, new CopyOnWriteArrayList<CopyOnWriteArrayList<Data>>());
			for(int horse = 0; horse < HORSE_COUNT; horse++)
			data.get(race).add(horse, new CopyOnWriteArrayList<Data>());
		}
	}
	
	/**
	 * 获取单例模式的实例
	 * @return
	 */
	public static DataSet getInstance() {
		return instance;
	}
	
	/**
	 * 获取二维数组中的一个元素
	 * @param race
	 * @param horse
	 * @return
	 */
	public CopyOnWriteArrayList<Data> getElement(int race, int horse) {
		return data.get(race).get(horse);
	}
	
	/**
	 * 将元素插入到二维数组中
	 * @param race
	 * @param horse
	 * @param d
	 */
	public void addElement(int race, int horse, Data d) {
		data.get(race).get(horse).add(d);
	}
	
	/**
	 * 将DataSet的数据写入到文件中,每次更新一次DateSet就清空文件并全量写入DataSet.格式为场次	马	win	place	win_place
	 */
	public void writeFile() {
		StringBuffer sb = new StringBuffer();
		for(int race = 0; race < RACE_COUNT; race++) 
			for(int horse = 0; horse < HORSE_COUNT; horse++) {
				for(Data e : data.get(race).get(horse)) {
					sb.append(e.toString(race, horse));
				}
			}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath, false);
			fos.write(sb.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 读取存在硬盘上的文件,在程序正式开始运行的时候读取一次
	 */
	public void readFile() {
		File file = new File(filePath);
		BufferedReader reader = null;
		if(!file.exists())
			return;
		try {
			reader = new BufferedReader(new FileReader(file));
			String s = null;
			while((s = reader.readLine()) != null) {
				dealLine(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 处理读取文件的每一行数据,格式为场次	马	win	place	win_place
	 * @param s
	 */
	private void dealLine(String s) {
		Data d = new Data();
		try {
			Matcher matcher = Pattern.compile("([\\d:.]+?(?=\t))").matcher(s);
			matcher.find();
			int race = Integer.parseInt(matcher.group());
			matcher.find();
			int horse = Integer.parseInt(matcher.group());
			matcher.find();
			d.date = new SimpleDateFormat("HH:mm:ss").parse(matcher.group());
			matcher.find();
			d.win = Double.parseDouble(matcher.group());
			matcher.find();
			d.place = Double.parseDouble(matcher.group());
			matcher.find();
			d.win_place = Double.parseDouble(matcher.group());
			data.get(race).get(horse).add(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 找出数组一行中(同一场次)里面找出最早的时间,以确定x轴的横坐标起始值
	 * @param race
	 * @return
	 */
	public long minTime(int race) {
		long time = 24*60*60*1000;
		for(int horse = 0; horse < HORSE_COUNT; horse++) {
			if(!data.get(race).get(horse).isEmpty() &&  data.get(race).get(horse).get(0).date.getTime() < time) {
				time = data.get(race).get(horse).get(0).date.getTime();
			}
		}
		return time;
	}
	
	/**
	 * 找出二维数组某一元素的最小折扣,以确定y轴纵坐标最小值
	 * @param race
	 * @param horse
	 * @return
	 */
	public double minDiscount(int race, int horse) {
		List<Data> sortList = new LinkedList<Data>(data.get(race).get(horse));
		double min = 75.0;		//默认最小75.0
		Collections.sort(sortList);
		if(!sortList.isEmpty()) {
			min = Math.min(Math.min(sortList.get(0).win, sortList.get(0).place), sortList.get(0).win_place);
		}
		return min;
	}
	
	/**
	 * 找出二维数组某一元素的最大折扣,以确定y轴纵坐标最大值
	 * @param race
	 * @param horse
	 * @return
	 */
	public double maxDiscount(int race, int horse) {
		List<Data> sortList = new LinkedList<Data>(data.get(race).get(horse));
		double max = 100.0;		//默认最大100.0
		Collections.sort(sortList);
		if(!sortList.isEmpty()) {
			max = Math.max(Math.max(sortList.get(sortList.size() - 1).win, sortList.get(sortList.size() - 1).place), sortList.get(sortList.size() - 1).win_place);
		}
		return max;
	}
	
	/**
	 * 设定记录文件的路径
	 * @param filePath
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
