package com.qiguan.grab.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**  
 * <pre>
 * Description	字符串处理工具类
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月4日 上午9:22:14  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */  
public class StringUtil {
	/**
	 * 禁止实例化
	 */
	private StringUtil() {
		
	}
	
	/**
	 * 判断是否为空
	 * 
	 * @param value
	 * @return
	 */
	public static final boolean isNullOrEmpty(String value) {
		return (value == null) || (value.length() == 0);
	}
	
	/**
	 * @param curl
	 * @return
	 */
	public static String getDomain(String curl) {
		URL url = null;
		String q = "";
		try {
			url = new URL(curl);
			q = url.getHost();
		} catch (MalformedURLException e) {

		}
		url = null;
		return q;
	}
	
	/**
	 * 截取url中的域名
	 * 
	 * @param url 初始化请求url
	 * @return url域名
	 */
	public static String getDomainName(String url) {
		if (null != url && url != "") {
			Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
			Matcher m = p.matcher(url);
			if(m.find()){
			      return m.group();
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(getDomain("http://tf.360.cn/e/wb?_=76b6fa2e03fe712e&ip=49.69.92.204&reduce=0&width=0&height=0"));;
	}
}
