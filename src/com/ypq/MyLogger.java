package com.ypq;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * 封装了log4j的日志类,采用单例模式
 *
 * @author god
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
