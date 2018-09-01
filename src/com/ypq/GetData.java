package com.ypq;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * ��װ��http��get,���ڻ�ȡ����
 * @author god
 *
 */
public class GetData {
	private String date = null;
	private String raceType = null; 
	private int race = 1;				//Ĭ�ϵ�һ��,�����0�Ļ����ȡȫ������
	private OkHttpClient client;
	private static final String limitDate = "20201021";
	
	/**
	 * ��ȡ���ݵķ���,��Ҫ�Ĳ���������date,����raceType,����race
	 */
	public void toGetData() {
		//http://info.ctb988.net/betdata?race_date=07-07-2016&race_type=9U&rc=6&m=HK&c=3&lu=0
		String year = date.split("-")[2];
	    String month = date.split("-")[1];
	    String day = date.split("-")[0];
	    String yearMonthDay = year + month + day;
	    if (yearMonthDay.compareTo(this.limitDate) > 0)
	        return;
		String url = "http://cont.jsdyms.com/bdata?race_date=" + date + "&race_type=" + raceType + "&rc=" + race + "&m=HK&c=3&lu=0";
		Request request = new Request.Builder().url(url).build();
		Call call = client.newCall(request);
		
		call.enqueue(new Callback() {
			/**
			 * http���سɹ��ķ���
			 */
			public void onResponse(Response response) throws IOException {
				String body = response.body().string();
				BestData bestData = new BestData();
				int race = 0;
				//ѭ���ҳ�ÿһ�е�����
				Matcher row = Pattern.compile("\\d{1,2}\\\\t\\d{1,2}\\\\t\\d+\\\\t\\d+\\\\t\\d+(.\\d)?.+?\\\\n").matcher(body);
				while(row.find()) {
					String rowData = row.group();
					//ѭ���ҳ�ÿһ������		
					Matcher column = Pattern.compile("((?<=\\\\n).+?(?=\\\\t))|(\\d+(?=\\\\t))").matcher(rowData);
					while(column.find()) {
						race = Integer.parseInt(column.group());
						column.find();
						int horse = Integer.parseInt(column.group());
						column.find();
						int win = Integer.parseInt(column.group());
						column.find();
						int place = Integer.parseInt(column.group());
						column.find();
						double discount = Double.parseDouble(column.group());
						if(win != 0 && place == 0)
							bestData.join(BestData.WIN, horse, discount);
						else if(win == 0 && place != 0)
							bestData.join(BestData.PLACE, horse, discount);
						else if(win != 0 && place != 0) {
							bestData.join(BestData.WIN_PLACE, horse, discount);
						}
					}
				}
				bestData.insert(race);
				DataSet.getInstance().writeFile();
			}
			/**
			 * http����ʧ�ܵķ���
			 */
			public void onFailure(Request arg0, IOException arg1) {
				System.out.println("request toGetData fail!!!");
			}
		});
	}

	/**
	 * ���캯��,��ʼ���������ں�����
	 * @param date
	 * @param raceType
	 */
	public GetData(String date, String raceType) {
		this.client = new OkHttpClient();
		this.date = date;
		this.raceType = raceType;
	}


	/**
	 * �趨����
	 * @param race
	 */
	public void setRace(int race) {
		this.race = race;
	}

	/**
	 * �趨����
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * �趨����
	 * @param raceType
	 */
	public void setRaceType(String raceType) {
		this.raceType = raceType;
	}

	/**
	 * �������
	 * @return
	 */
	public String getDate() {
		return date;
	}

	/**
	 * �������
	 * @return
	 */
	public String getRaceType() {
		return raceType;
	}
}
