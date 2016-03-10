package com.qiguan.grab.util;


import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**  
 * <pre>
 * Description
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月7日 上午10:59:27  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class ConfigUtil {
	
	/**
	 * 日志对象
	 */
	private static Logger log = Logger.getLogger(ConfigUtil.class);
	
	static Properties p = null;

	static {
		p = new Properties();
		try {
			p.load(ConfigUtil.class.getClassLoader().getResourceAsStream("app.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回配置文件中key对应的字符串 。
	 * 
	 * @param key
	 * @return 如果未找到，返回 ""
	 */
	public static String getValue(String key) {
		if(p.containsKey(key)){
			return p.getProperty(key);
		}
		return null;
	}

	/**
	 * 返回配置文件中key对应的int值 。
	 * 
	 * @param key
	 * @return 如果未找到，返回 -1
	 */
	public static int getIntValue(String key) {
		try {
			return Integer.parseInt(getValue(key));
			
		} catch (Exception e) {
			log.error("获取配置属性错误", e);
			return -1;
		}
	}
	
	/**
	 * 返回配置文件中key对应的boolean值 。
	 * 
	 * @param key
	 * @return 如果未找到，返回false
	 */
	public static boolean getBooleanValue(String key) {
		try {
			return Boolean.parseBoolean(key);
			
		} catch (Exception e) {
			log.error("获取配置属性错误", e);
			return false;
		}
	}
}
