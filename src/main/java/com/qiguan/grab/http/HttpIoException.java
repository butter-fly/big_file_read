package com.qiguan.grab.http;


import java.io.IOException;

import com.squareup.okhttp.Response;


/**  
 * <pre>
 * Description	覆写IO异常
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年4月20日 上午9:16:13  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */  
public final class HttpIoException extends IOException {
	
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 6346029089202567629L;
	
	/**
	 * 响应
	 */
	public final Response response;

	/**
	 * @param response
	 */
	public HttpIoException(Response response) {
		this.response = response;
	}

	/**
	 * @param e
	 */
	public HttpIoException(Exception e) {
		super(e);
		this.response = null;
	}

	/**
	 * @return
	 */
	public int code() {
		return response == null ? -1 : response.code();
	}
	
	/**
	 * @return
	 */
	public String msg() {
		return response == null ? null : response.message();
	}
}
