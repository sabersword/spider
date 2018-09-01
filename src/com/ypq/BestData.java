package com.ypq;

import java.util.ArrayList;
import java.util.Calendar;

import com.ypq.DataSet.Data;

/**
 * 这是一个二维数组,每一行是代表winOrPlace,每一列代表不同的马,其中的元素代表了当前最低的折扣.注意winOrPlace=0是win,=1是place,=2是winplace
 * @author god
 *
 */
public class BestData {
	
	private static final int WIN_OR_PLACE_COUNT = 3;
	public static final int HORSE_COUNT = 16;
	public static final int WIN = 0;
	public static final int PLACE = 1;
	public static final int WIN_PLACE = 2;
	private ArrayList<ArrayList<Double>> data;
	
	/**
	 * 根据winOrPlace和horse找到对应的discount,将最大值插入其中
	 * @param winOrPlace
	 * @param horse
	 * @param discount
	 */
	public void join(int winOrPlace, int horse, double discount) {
		if(data.get(winOrPlace).get(horse) < discount) {
			data.get(winOrPlace).set(horse, discount);
		}
	}
	
	/**
	 * 将得到的最大值插入到DataSet中,忽略掉win,place,win_place均为0的数据(被认为是没有该马)
	 * @param race
	 */
	public void insert(int race) {
		for(int horse = 0; horse < HORSE_COUNT; horse++) {
			if(data.get(WIN).get(horse) > 0 || data.get(PLACE).get(horse) > 0 || data.get(WIN_PLACE).get(horse) > 0) {
				//因为存储在txt中的记录没有记录日期,所以统一默认为1970.01.01
				Calendar calendar = Calendar.getInstance();
				calendar.set(1970, 0, 1);
				DataSet.getInstance().getElement(race, horse).add(new Data(calendar.getTime(), data.get(WIN).get(horse), data.get(PLACE).get(horse), data.get(WIN_PLACE).get(horse)));
			}
		}
	}
	
	/**
	 * 初始化整个数组
	 */
	public BestData() {
		data = new ArrayList<ArrayList<Double>>(WIN_OR_PLACE_COUNT);
		for(int i = 0; i < WIN_OR_PLACE_COUNT; i++) {
			data.add(i, new ArrayList<Double>(HORSE_COUNT));
			for(int j = 0; j < HORSE_COUNT; j++) {
				data.get(i).add(j, new Double(76));			//没有数据的时候认为折扣是76
			}
		}
	}
	
}
