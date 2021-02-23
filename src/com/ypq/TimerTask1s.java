package com.ypq;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * 供1s定时器调用,每个1s做指定的任务
 * @author god
 *
 */
public class TimerTask1s extends TimerTask{
	private GetData getData;
	private ArrayList<MultipleAxis> al;
	
	/**
	 * 构造函数,初始化要做的任务,分别是getData获取数据和更新坐标轴
	 * @param getData
	 * @param al
	 */
	public TimerTask1s(GetData getData, ArrayList<MultipleAxis> al) {
		this.getData = getData;
		this.al = al;
	}
	
	/**
	 * 指明定时的任务
	 */
	@Override
	public void run() {
		getData.toGetData();
		for(MultipleAxis e : al) {
			e.updateDataSet();
		}
	}
}
