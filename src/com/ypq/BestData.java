package com.ypq;

import java.util.ArrayList;
import java.util.Calendar;

import com.ypq.DataSet.Data;

/**
 * ����һ����ά����,ÿһ���Ǵ���winOrPlace,ÿһ�д���ͬ����,���е�Ԫ�ش����˵�ǰ��͵��ۿ�.ע��winOrPlace=0��win,=1��place,=2��winplace
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
	 * ����winOrPlace��horse�ҵ���Ӧ��discount,�����ֵ��������
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
	 * ���õ������ֵ���뵽DataSet��,���Ե�win,place,win_place��Ϊ0������(����Ϊ��û�и���)
	 * @param race
	 */
	public void insert(int race) {
		for(int horse = 0; horse < HORSE_COUNT; horse++) {
			if(data.get(WIN).get(horse) > 0 || data.get(PLACE).get(horse) > 0 || data.get(WIN_PLACE).get(horse) > 0) {
				//��Ϊ�洢��txt�еļ�¼û�м�¼����,����ͳһĬ��Ϊ1970.01.01
				Calendar calendar = Calendar.getInstance();
				calendar.set(1970, 0, 1);
				DataSet.getInstance().getElement(race, horse).add(new Data(calendar.getTime(), data.get(WIN).get(horse), data.get(PLACE).get(horse), data.get(WIN_PLACE).get(horse)));
			}
		}
	}
	
	/**
	 * ��ʼ����������
	 */
	public BestData() {
		data = new ArrayList<ArrayList<Double>>(WIN_OR_PLACE_COUNT);
		for(int i = 0; i < WIN_OR_PLACE_COUNT; i++) {
			data.add(i, new ArrayList<Double>(HORSE_COUNT));
			for(int j = 0; j < HORSE_COUNT; j++) {
				data.get(i).add(j, new Double(76));			//û�����ݵ�ʱ����Ϊ�ۿ���76
			}
		}
	}
	
}
