package com.qiguan.grab.http;


/**
 * <pre>
 * Description	字符串连接工具类
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年4月19日 下午5:50:26  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------
 * 
 * </pre>
 */
public final class StrUtils {

	/**
	 * 私有化
	 */
	private StrUtils() {
		
	}

	/**
	 * @param s
	 * @return
	 */
	public static boolean isNullOrEmpty(String s) {
		return s == null || "".equals(s.trim());
	}
}
