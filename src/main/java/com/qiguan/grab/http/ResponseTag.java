package com.qiguan.grab.http;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;

/**  
 * <pre>
 * Description	服务端响应结果封装对象
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年4月19日 下午5:31:52  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public final class ResponseTag {
	/**
	 * 回复状态码
	 */
	public final int statusCode;
	
	/**
	 * 扩展头
	 */
	public final String reqId;

	/**
	 * 错误信息
	 */
	public final String error;
	
	/**
	 * 请求消耗时间，单位秒
	 */
	public final long duration;
	
	/**
	 * 请求的Url
	 */
	public final String url;
	
	/**
	 * 服务器IP
	 */
	public final String address;
	
	/**
	 * 服务器IP
	 */
	public final String body;
	
	
    /**
     * @param statusCode
     * @param reqId
     * @param error
     * @param duration
     * @param address
     * @param body
     */
    public ResponseTag(int statusCode, String reqId, String error, long duration, String url, String address, String body) {
		super();
		this.statusCode = statusCode;
		this.reqId = reqId;
		this.error = error;
		this.duration = duration;
		this.url = url;
		this.address = address;
		this.body = body;
	}


	/**
	 * 创建正常响应对象
	 * 
	 * @param response
	 * @param address
	 * @param duration
	 * @return
	 */
	public static ResponseTag create(Response response, String address, long duration) {
		String error = response.message();
		int code = response.code();
		String reqId = null;
		String body = null;
		if (ctype(response).equals(HttpClient.JsonMime)) {
			reqId = response.header("X-Reqid");
			reqId = (reqId == null) ? null : reqId.trim();
			try {
				body = response.body().string();
				// if (response.code() >= 400 && !StringUtils.isNullOrEmpty(reqId) && content != null) {
				if (response.code() >= 400  && body != null) {
					ErrorBody errorBody = JSONObject.parseObject(body, ErrorBody.class);
					error = errorBody.error;
				}
			} catch (Exception e) {
				if (response.code() < 300) {
					error = e.getMessage();
				}
			} finally {
				try {
					// 关闭Body
					if (null != response.body()) {
						response.body().close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new ResponseTag(code, reqId, error, duration, response.request().urlString(), address, body);
	}
	
	/**
	 * @param response
	 * @param address
	 * @param duration
	 * @return
	 */
	public static ResponseTag createError(Response response, String address, long duration, String error) {
		if (response == null) {
			return new ResponseTag(-1, "", error, duration, null, null, null);
		}
		int code = response.code();
		String reqId = null;
		String body = null;
		if (ctype(response).equals(HttpClient.JsonMime)) {
			reqId = response.header("X-Reqid");
			reqId = (reqId == null) ? null : reqId.trim();
			try {
				body = response.body().string();
				if (response.code() >= 400 && body != null) {
					ErrorBody errorBody = JSONObject.parseObject(body, ErrorBody.class);
					error = errorBody.error;
				}
			} catch (Exception e) {
				if (response.code() < 300) {
					error = e.getMessage();
				}
			} finally {
				try {
					// 关闭Body
					if (null != response.body()) {
						response.body().close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new ResponseTag(code, reqId, error, duration, response.request().urlString(), address, body);
	}
	/**
	 * @param response
	 * @return
	 */
	private static String ctype(Response response) {
		MediaType mediaType = response.body().contentType();
		if (mediaType == null) {
			return "";
		}
		return mediaType.type() + "/" + mediaType.subtype();
	}
	
	/**
	 * 是否正常
	 * 
	 * @return
	 */
	public boolean isOK() {
		return statusCode == 200 && error == null && reqId != null && reqId.length() > 0;
	}

	/**
	 * 是否服务器内部错误
	 * 
	 * @return
	 */
	public boolean isServerError() {
		return (statusCode >= 500 && statusCode < 600 && statusCode != 579) || statusCode == 996;
	}
	
	/**
	 * 是否网络异常
	 * 
	 * @return
	 */
	public boolean isNetworkBroken() {
		return (statusCode == -1);
	}

	/**
	 * 是否需要切换服务地址
	 * 
	 * @return
	 */
	public boolean needSwitchServer() {
		return isNetworkBroken() || (statusCode >= 500 && statusCode < 600 && statusCode != 579);
	}

	/**
	 * 是否需要重试
	 * 
	 * @return
	 */
	public boolean needRetry() {
		return isNetworkBroken() || isServerError() || statusCode == 406 || (statusCode == 200 && error != null);
	}

	/**  
	 * <pre>
	 * Description
	 * Copyright:	Copyright (c)2016
	 * Company:		杭州启冠网络技术有限公司
	 * Author:		Administrator
	 * Version: 	1.0
	 * Create at:	2016年4月19日 下午5:49:43  
	 *  
	 * Modification History:  
	 * Date         Author      Version     Description 
	 * ------------------------------------------------------------------  
	 * 
	 * </pre>
	 */  
	public static class ErrorBody {
		public String error;
	}
}
