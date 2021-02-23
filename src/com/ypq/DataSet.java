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
 * һ����ArrayList��ɵĶ�ά����,ÿһ�д���ͬ����,ÿһ�д���ͬ����,�����Ԫ����LinkList��ɵİ�ʱ�����е��ۿ�.���õ���ģʽ,ֻ�ܴ���һ��DataSet
 * @author god
 *
 */
public class DataSet {
	
	/**
	 * DataSet������崢���Ԫ��element,��Ҫ������ʱ��,win,place,win_place.����4�߹�����xy�����������
	 * @author god
	 *
	 */
	public static class Data implements Comparable<Data>{
		/**
		 * ��дhashCode,��Ҫ����HashSet��ȥ��ʱ����ͬ������.��Ϊ�ж����������Ƿ���ͬ���ǿ�hashCode��equals
		 */
		@Override
		public int hashCode() {
			return (int) date.getTime();
		}

		/**
		 * ��дequals,ԭ��ͬ��
		 */
		@Override
		public boolean equals(Object obj) {
			if(date.getTime() == ((Data)obj).date.getTime())
				return true;
			return false;
		}
		
		/**
		 * ��дcompareTo,����Collections.sort()������,�ҳ�������С���ۿ�
		 */
		public int compareTo(Data d) {
			double max = Math.max(Math.max(win, place), win_place);
			double dmax = Math.max(Math.max(d.win, d.place), d.win_place);
			return (int)(max - dmax);
		}
		
		/**
		 * ���캯��,������Ĳ���д�뵽��Ա����
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
		 * ���캯��,��ʼ��null���ں�win,place,win_place
		 */
		public Data() {
			this.date = null;
			this.win = 0;
			this.place = 0;
			this.win_place = 0;
		}

		/**
		 * ��дtoString,��Ҫ�Ǽ�¼���ļ�ÿһ�е�ʱ�򷽱����
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
	
	private final int RACE_COUNT = 16;			//���賡�β�����15��(ȥ������Ϊ0��һ��)
	private final int HORSE_COUNT = 16;			//����������15ƥ(ȥ����ƥΪ0��һƥ)
	private String filePath = null;				//��¼�ļ���·��
	private CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Data>>> data;		//ʹ��CopyOnWriteArrayList���Ա�֤�̰߳�ȫ,ʹ��Collections.synchronizedList�Ļ����ǻ�����̲߳���ȫ�����
	private static final DataSet instance = new DataSet();				//����ģʽ��ʵ��

	/**
	 * ���캯��,��ʼ��CopyOnWriteArrayList���ɵĶ�ά����
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
	 * ��ȡ����ģʽ��ʵ��
	 * @return
	 */
	public static DataSet getInstance() {
		return instance;
	}
	
	/**
	 * ��ȡ��ά�����е�һ��Ԫ��
	 * @param race
	 * @param horse
	 * @return
	 */
	public CopyOnWriteArrayList<Data> getElement(int race, int horse) {
		return data.get(race).get(horse);
	}
	
	/**
	 * ��Ԫ�ز��뵽��ά������
	 * @param race
	 * @param horse
	 * @param d
	 */
	public void addElement(int race, int horse, Data d) {
		data.get(race).get(horse).add(d);
	}
	
	/**
	 * ��DataSet������д�뵽�ļ���,ÿ�θ���һ��DateSet������ļ���ȫ��д��DataSet.��ʽΪ����	��	win	place	win_place
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
	 * ��ȡ����Ӳ���ϵ��ļ�,�ڳ�����ʽ��ʼ���е�ʱ���ȡһ��
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
	 * �����ȡ�ļ���ÿһ������,��ʽΪ����	��	win	place	win_place
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
	 * �ҳ�����һ����(ͬһ����)�����ҳ������ʱ��,��ȷ��x��ĺ�������ʼֵ
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
	 * �ҳ���ά����ĳһԪ�ص���С�ۿ�,��ȷ��y����������Сֵ
	 * @param race
	 * @param horse
	 * @return
	 */
	public double minDiscount(int race, int horse) {
		List<Data> sortList = new LinkedList<Data>(data.get(race).get(horse));
		double min = 75.0;		//Ĭ����С75.0
		Collections.sort(sortList);
		if(!sortList.isEmpty()) {
			min = Math.min(Math.min(sortList.get(0).win, sortList.get(0).place), sortList.get(0).win_place);
		}
		return min;
	}
	
	/**
	 * �ҳ���ά����ĳһԪ�ص�����ۿ�,��ȷ��y�����������ֵ
	 * @param race
	 * @param horse
	 * @return
	 */
	public double maxDiscount(int race, int horse) {
		List<Data> sortList = new LinkedList<Data>(data.get(race).get(horse));
		double max = 100.0;		//Ĭ�����100.0
		Collections.sort(sortList);
		if(!sortList.isEmpty()) {
			max = Math.max(Math.max(sortList.get(sortList.size() - 1).win, sortList.get(sortList.size() - 1).place), sortList.get(sortList.size() - 1).win_place);
		}
		return max;
	}
	
	/**
	 * �趨��¼�ļ���·��
	 * @param filePath
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
