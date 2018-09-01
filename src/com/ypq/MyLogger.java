package com.ypq;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * ��װ��log4j����־��,���õ���ģʽ
 * @author god
 *
 */
public class MyLogger {
	private MyLogger() {
		
	}
	
	static {
		logger = Logger.getLogger(MyLogger.class);
		PropertyConfigurator.configure("log4j.properties");
	}
	
	public static Logger getInstance() {
		return logger;
	}

	private static Logger logger;
}
