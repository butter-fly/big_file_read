package com.qiguan.grab.util;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

/**  
 * <pre>
 * Description	检查url是否可用
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月14日 下午5:12:42  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class HttpUrlAvailability {
	
	/**
	 * 日志对象
	 */
	private final static Logger logger = Logger.getLogger(HttpUrlAvailability.class);
	/**
	 * URL对象
	 */
	private static URL url;  
	/**
	 * 连接对象
	 */
	private static HttpURLConnection con;  
	
	/**
	 * 响应状态
	 */
	private static int state = -1; 
	
	/**
	 * 判读URL是否可用
	 * 
	 * @param urlStr
	 * @return
	 */
	public static synchronized boolean isConnect(String urlStr) {
		if (urlStr == null || urlStr.length() <= 0) {
			return false;
		}
		try {
			url = new URL(urlStr);
			con = (HttpURLConnection) url.openConnection();
			state = con.getResponseCode();
			if (state == 200) {
				logger.info(urlStr + "可用！");
				return true;
			} 
			return false;
		} catch (Exception ex) {
			urlStr = null;
			return false;
		} finally {
			con.disconnect();
		}
	}
}
