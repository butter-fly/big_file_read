package com.qiguan.grab.http;

import java.io.IOException;

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
	 * 错误信息
	 */
	public final String msg;
	
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
	 * builder构造
	 * 
	 * @param builder
	 */
	public ResponseTag(Builder builder) {
		this.statusCode = builder.statusCode;
		this.msg = builder.msg;
		this.duration = builder.duration;
		this.url = builder.url;
		this.address = builder.address;
		this.body = builder.body;
	}
	
	
    /**
     * 
     * 
     * @param statusCode http状态码
     * @param msg 
     * @param duration
     * @param address
     * @param body
     */
    public ResponseTag(int statusCode, String msg, long duration, String url, String address, String body) {
		super();
		this.statusCode = statusCode;
		this.msg = msg;
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
//	public static ResponseTag create(Response response, String address, long duration) {
//		String msg = null;
//		int code = response.code();
//		String body = null;
//		if (ctype(response).equals(HttpClient.JsonMime)) {
//			try {
//				body = response.body().string();
//				// if (response.code() >= 400 && !StringUtils.isNullOrEmpty(reqId) && content != null) {
//				msg = response.message();
//			} catch (Exception e) {
//				if (response.code() < 300) {
//					msg = e.getMessage();
//				}
//			} finally {
//				try {
//					// 关闭Body
//					if (null != response.body()) {
//						response.body().close();
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return new ResponseTag(code, msg, duration, response.request().urlString(), address, body);
//	}
	
	/** 响应出错返回信息创建
	 * 
	 * @param response
	 * @param address
	 * @param duration
	 * @return
	 */
//	public static ResponseTag createError(Response response, String address, long duration, String msg) {
//		if (response == null) {
//			return new ResponseTag(-1, msg, duration, null, null, null);
//		}
//		int code = response.code();
//		String body = null;
//		if (ctype(response).equals(HttpClient.JsonMime)) {
//			try {
//				body = response.body().string();
//				msg = response.message();
//			} catch (Exception e) {
//				if (response.code() < 300) {
//					msg = e.getMessage();
//				}
//			} finally {
//				try {
//					// 关闭Body
//					if (null != response.body()) {
//						response.body().close();
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return new ResponseTag(code,  msg, duration, response.request().urlString(), address, body);
//	}
	
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
		return statusCode == 200 && msg != null && "ok".equalsIgnoreCase(msg);
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
		return isNetworkBroken() || isServerError() || statusCode == 406 || (statusCode == 200 && msg != null && !"ok".equalsIgnoreCase(msg));
	}
	
	
	/**  
	 * <pre>
	 * Description	构造参数以build模式创建
	 * Copyright:	Copyright (c)2016
	 * Company:		杭州启冠网络技术有限公司
	 * Author:		Administrator
	 * Version: 	1.0
	 * Create at:	2016年4月21日 上午9:54:59  
	 *  
	 * Modification History:  
	 * Date         Author      Version     Description 
	 * ------------------------------------------------------------------  
	 * 
	 * </pre>
	 */  
	public static class Builder {
		
		/**
		 * HTTP响应
		 */
		private Response response;
		
		/**
		 * 回复状态码
		 */
		public int statusCode;
		
		/**
		 * 错误信息
		 */
		public String msg;
		
		/**
		 * 请求消耗时间，单位秒
		 */
		public long duration;
		
		/**
		 * 请求的Url
		 */
		public String url;
		
		/**
		 * 服务器IP
		 */
		public String address;
		
		/**
		 * 响应Body
		 */
		public String body;
		
		/**
		 * 构建this对象
		 * 
		 * @param response
		 */
		public Builder() {
			
		}
		
		/**
		 * 构造Response
		 * 
		 * @param statusCode
		 * @return
		 */
		public Builder response(Response response) {
			this.response = response;
			return this;
		}
		
		/**
		 * 构造状态码
		 * 
		 * @param statusCode
		 * @return
		 */
		public Builder statusCode(int statusCode) {
			this.statusCode = statusCode;
			return this;
		}
		
		/**
		 * 构造响应耗时
		 * 
		 * @param duration
		 * @return
		 */
		public Builder duration(long duration) {
			this.duration = duration;
			return this;
		}
		
		/**
		 * 构造响应信息
		 * 
		 * @param msg
		 * @return
		 */
		public Builder msg(String msg) {
			this.msg = msg;
			return this;
		}
		
		/**
		 * 构造请求url
		 * 
		 * @param url
		 * @return
		 */
		public Builder url(String url) {
			this.url = url;
			return this;
		}
		
		/**
		 * 构造请求address
		 * 
		 * @param address
		 * @return
		 */
		public Builder address(String address) {
			this.address = address;
			return this;
		}
		
		/**
		 * 构造请求body
		 * 
		 * @param body
		 * @return
		 */
		public Builder body(String body) {
			this.body = body;
			return this;
		}

		
		/**
		 * 构造入口
		 * 
		 * @return 响应标签
		 */
		public ResponseTag build() {
			return new ResponseTag(this);
		}
		
		/**
		 * 创建正常响应对象
		 * 
		 * @param response
		 * @param address
		 * @param duration
		 * @return
		 */
		public Builder create() {
			if (null == this.response) {
				this.statusCode = -1;
				return this;
			}
			// 服务器端状态及响应信息
			this.statusCode(response.code());
			this.msg(response.message());
			this.url(response.request().urlString());
			// 业务端响应数据
			if (ctype(response).equals(HttpClient.Constant.JsonMime)) {
				try {
					this.body(response.body().string());
				} catch (Exception e) {
					if (response.code() < 300) {
						this.msg(e.getMessage());
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
			return this;
		}
	}
}
