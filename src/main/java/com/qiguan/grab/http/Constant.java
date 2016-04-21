package com.qiguan.grab.http;


/**  
 * <pre>
 * Description	SDK常量数据
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年4月20日 下午5:08:23  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */  
public final class Constant {
	
	 /**
	 * 版本号
	 */
	public static final String VERSION = "1.0.1";

	/**
	 * 连接超时时间 单位秒(默认10s)
	 */
	public static int CONNECT_TIMEOUT = 10;
	
	/**
	 * 写超时时间 单位秒(默认 0 , 不超时)
	 */
	public static int WRITE_TIMEOUT = 0;
	
	/**
	 * 回复超时时间 单位秒(默认30s)
	 */
	public static int RESPONSE_TIMEOUT = 30;

	/**
	 * 私有化
	 */
	private Constant() {}
}
