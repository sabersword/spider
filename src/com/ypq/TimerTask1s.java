package com.ypq;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * ��1s��ʱ������,ÿ��1s��ָ��������
 * @author god
 *
 */
public class TimerTask1s extends TimerTask{
	private GetData getData;
	private ArrayList<MultipleAxis> al;
	
	/**
	 * ���캯��,��ʼ��Ҫ��������,�ֱ���getData��ȡ���ݺ͸���������
	 * @param getData
	 * @param al
	 */
	public TimerTask1s(GetData getData, ArrayList<MultipleAxis> al) {
		this.getData = getData;
		this.al = al;
	}
	
	/**
	 * ָ����ʱ������
	 */
	@Override
	public void run() {
		getData.toGetData();
		for(MultipleAxis e : al) {
			e.updateDataSet();
		}
	}
}
