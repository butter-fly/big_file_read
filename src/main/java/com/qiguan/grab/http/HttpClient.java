package com.qiguan.grab.http;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.Dispatcher;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**  
 * <pre>
 * Description	模拟HTTP请求
 *  			使用OkHttp作为HTTP客户端 
 * 				支持SPDY, 可以合并多个到同一个主机的请求
 * 				  使用连接池技术减少请求的延迟(如果SPDY是可用的话), 使用GZIP压缩减少传输的数据量
 * 				 缓存响应避免重复的网络请求
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年4月19日 下午4:33:32  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public final class HttpClient {
	/**
	 * 返回消息内容类型
	 */
	public static final String ContentTypeHeader = "Content-Type";
	
	/**
	 * mine
	 */
	public static final String DefaultMime = "application/octet-stream";
	
	/**
	 * 数据类型
	 */
	public static final String JsonMime = "application/json";
	
	/**
	 * 表单
	 */
	public static final String FormMime = "application/x-www-form-urlencoded";
	
	/**
	 * Http对象
	 */
	private final OkHttpClient httpClient;
	
	/**
	 * 无参构造
	 */
	public HttpClient() {
		// 配置最大并发请求(当异步请求执行政策。每个调度程序使用一个ExecutorService运行在内部调用。如果你提供自己的遗嘱执行人,它应该能够运行配置最大数量的并发调用)
		Dispatcher dispatcher = new Dispatcher();
		dispatcher.setMaxRequests(64);
		dispatcher.setMaxRequestsPerHost(16);
		// maxConnections最大数量的空闲连接池中保持。默认是5 keepAliveDuration时间以毫秒为单位保持在关闭之前连接池中活着 默认是5分钟
		ConnectionPool connectionPool = new ConnectionPool(32, 5 * 60 * 1000);
		httpClient = new OkHttpClient();
		httpClient.setDispatcher(dispatcher);
		httpClient.setConnectionPool(connectionPool);
		// 所有网络请求都附上你的拦截器
		httpClient.networkInterceptors().add(new Interceptor() {
			/* (non-Javadoc)
			 * @see com.squareup.okhttp.Interceptor#intercept(com.squareup.okhttp.Interceptor.Chain)
			 */
			@Override
			public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
				Request request = chain.request();
				com.squareup.okhttp.Response response = chain.proceed(request);
				IpTag tag = (IpTag) request.tag();
				String ip = chain.connection().getSocket().getRemoteSocketAddress().toString();
				tag.ip = ip;
				return response;
			}
		});
		// OkHttp连接超时时间
		httpClient.setConnectTimeout(Constant.CONNECT_TIMEOUT, TimeUnit.SECONDS);
		// OkHttp读取超时时间
		httpClient.setReadTimeout(Constant.RESPONSE_TIMEOUT, TimeUnit.SECONDS);
		// OkHttp写入超时时间
		httpClient.setWriteTimeout(Constant.WRITE_TIMEOUT, TimeUnit.SECONDS);
	}
	
	/**
	 * 用户代理
	 * 
	 * @return 模拟游览器用户代理
	 */
	private static String userAgent() {
		StringBuffer str = new StringBuffer();
		str.append("QiguanJava/").append(Constant.VERSION)
		.append(" (")
		.append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.arch")).append(" ").append(System.getProperty("os.version"))
		.append(") ")
		.append("Java/").append(System.getProperty("java.version"));
		return str.toString();
	}
	
	/**
	 * post请求
	 * 
	 * @param url
	 * @param bodyJson
	 * @param headers
	 * @return
	 * @throws HttpIoException 
	 */
	public Response post(String url, String bodyJson, StrMap headers) throws HttpIoException {
		// MIME类型
		MediaType mediaType  = MediaType.parse(JsonMime);
		RequestBody body = RequestBody.create(mediaType, bodyJson);
		Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
		return send(requestBuilder, headers);
	}
	
	/**
	 * 异步Post发送
	 * 
	 * @param url
	 * @param bodyJson
	 * @param headers
	 * @throws HttpIoException
	 */
	public void asyncPost(String url, String bodyJson, StrMap headers, AsyncHttpCallback cb) throws HttpIoException {
		if (null != url && url.trim().trim().length() == 0) return;
		MediaType mediaType  = MediaType.parse(JsonMime);
		RequestBody body = RequestBody.create(mediaType, bodyJson);
		Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
		asyncSend(requestBuilder, headers, cb);
	}
	
	/**
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 * @throws HttpIoException
	 */
	public Response post(String url, StrMap params, StrMap headers) throws HttpIoException {
		final FormEncodingBuilder f = new FormEncodingBuilder();
		params.forEach(new StrMap.Consumer() {
			/* (non-Javadoc)
			 * @see com.qiguan.grab.http.StringMap.Consumer#accept(java.lang.String, java.lang.Object)
			 */
			@Override
			public void accept(String key, Object value) {
				f.add(key, value.toString());
			}
		});
		return post(url, f.build(), headers);
	}
	
	/**
	 * 同步发送POST请求
	 * 
	 * @param url
	 * @param body
	 * @param headers
	 * @return
	 * @throws HttpIoException
	 */
	private Response post(String url, RequestBody body, StrMap headers) throws HttpIoException {
		Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
		return send(requestBuilder, headers);
	}
	
	/**
	 * 单个文件同步上传
	 * 
	 * @param url
	 * @param fields
	 * @param file
	 * @param mimeType
	 * @param headers
	 * @return
	 * @throws HttpIoException
	 */
	public Response multipartPost(String url, File file, String mimeType, StrMap headers) throws HttpIoException {
		MediaType mediaType;
		if (null == mimeType) {
			mediaType = MediaType.parse(DefaultMime);
		} else {
			mediaType = MediaType.parse(mimeType);
		}
		RequestBody requestBody = RequestBody.create(mediaType, file);
		return multipartPost(url, "file", file.getName().trim(), requestBody, headers);
	}
	
	/**
	 * 多文件同步上传
	 * 
	 * @param url
	 * @param paths
	 * @param mimeType
	 * @param headers
	 * @return
	 * @throws HttpIoException
	 */
	public void asyncMultipleFileUpload(String url, StrMap paths, String mimeType, StrMap headers, AsyncHttpCallback cb) throws HttpIoException {
		asyncMultipartPost(url, paths, "file", mimeType, headers, cb);
	}
	
	/**
	 * 单个文件同步上传
	 * 
	 * @param url
	 * @param fields
	 * @param file
	 * @param mimeType
	 * @param headers
	 * @return
	 * @throws HttpIoException
	 */
	public void asyncMultipartPost(String url, File file, String mimeType, StrMap headers, AsyncHttpCallback cb) throws HttpIoException {
		MediaType mediaType;
		if (null == mimeType) {
			mediaType = MediaType.parse(DefaultMime);
		} else {
			mediaType = MediaType.parse(mimeType);
		}
		RequestBody requestBody = RequestBody.create(mediaType, file);
		asyncMultipartPost(url, null, "file", file.getName().trim(), requestBody, headers, cb);
	}
	
	/**
	 * 单文件同步上传
	 * 
	 * @param url
	 * @param fields
	 * @param name 表单文件file name
	 * @param fileName 文件名称如1.jpg
	 * @param requestBody
	 * @param headers
	 * @return
	 * @throws HttpIoException
	 */
	private Response multipartPost(String url, String name, String fileName, RequestBody requestBody, StrMap headers) throws HttpIoException {
		final MultipartBuilder mb = new MultipartBuilder().type(MultipartBuilder.FORM);
		mb.addFormDataPart(name, fileName, requestBody);
		mb.type(MediaType.parse("multipart/form-data"));
		RequestBody body = mb.build();
		Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
		return send(requestBuilder, headers);
	}
	
	
	/**
	 * 多文件同步上传
	 * 
	 * @param url
	 * @param fields
	 * @param name 表单文件file name
	 * @param mimeType
	 * @param headers
	 * @return
	 * @throws HttpIoException
	 */
	private void asyncMultipartPost(String url, StrMap paths, String name, String mimeType, StrMap headers, AsyncHttpCallback cb) throws HttpIoException {
		final MultipartBuilder mb = new MultipartBuilder().type(MultipartBuilder.FORM);
		if (null != paths) {
			MediaType mediaType;
			if (null == mimeType) {
				mediaType = MediaType.parse(DefaultMime);
			} else {
				mediaType = MediaType.parse(mimeType);
			}
			// 添加多个文件
			paths.forEach(new StrMap.Consumer() {
				/* (non-Javadoc)
				 * @see com.qiguan.grab.http.StringMap.Consumer#accept(java.lang.String, java.lang.Object)
				 */
				@Override
				public void accept(String key, Object value) {
					mb.addFormDataPart(key, value.toString().trim().substring(value.toString().trim().lastIndexOf("\\") + 1), RequestBody.create(mediaType, new File(value.toString())));
				}
			});
		}
		mb.type(MediaType.parse("multipart/form-data"));
		// 构建请求体
		RequestBody requestBody = mb.build();
		// 构建请求
		Request.Builder requestBuilder = new Request.Builder().url(url).post(requestBody);
		asyncSend(requestBuilder, headers, cb);
	}
	
	/**
	 * 异步单个文件上传
	 * 
	 * @param url
	 * @param fields
	 * @param name  表单文件file name
	 * @param fileName 文件名称如1.jpg
	 * @param file
	 * @param headers
	 * @param cb
	 */
	private void asyncMultipartPost(String url, StrMap fields, String name, String fileName, RequestBody requestBody, StrMap headers, AsyncHttpCallback cb) {
		final MultipartBuilder mb = new MultipartBuilder().type(MultipartBuilder.FORM);;
		mb.addFormDataPart(name, fileName, requestBody);
		if (null != fields) {
			fields.forEach(new StrMap.Consumer() {
				/* (non-Javadoc)
				 * @see com.qiguan.grab.http.StringMap.Consumer#accept(java.lang.String, java.lang.Object)
				 */
				@Override
				public void accept(String key, Object value) {
					mb.addFormDataPart(key, value.toString());
				}
			});
		}
		mb.type(MediaType.parse("multipart/form-data"));
		RequestBody body = mb.build();
		Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
		asyncSend(requestBuilder, headers, cb);
	}
	
	/**
	 * Get Or Post 发送同步
	 * 
	 * @param requestBuilder
	 * @param headers
	 * @return
	 * @throws HttpIoException
	 */
	private Response send(final Request.Builder requestBuilder, StrMap headers) throws HttpIoException {
		if (headers != null) {
			headers.forEach(new StrMap.Consumer() {
				/* (non-Javadoc)
				 * @see com.qiguan.grab.http.StringMap.Consumer#accept(java.lang.String, java.lang.Object)
				 */
				@Override
				public void accept(String key, Object value) {
					requestBuilder.header(key, value.toString());
				}
			});
		}
		requestBuilder.header("User-Agent", userAgent());
		Response res = null;
		IpTag tag = new IpTag();
		try {
			res = httpClient.newCall(requestBuilder.tag(tag).build()).execute();
		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpIoException(e);
		}
		if (res.code() >= 300) {
			throw new HttpIoException(res);
		}
		return res;
	}
	
	 /**
	  * Get Or Post 异步发送
	  * 
	 * @param requestBuilder
	 * @param headers
	 * @param cb
	 */
	private void asyncSend(final Request.Builder requestBuilder, StrMap headers, final AsyncHttpCallback cb) {
		// 报头
		 if (headers != null) {
			headers.forEach(new StrMap.Consumer() {
				/* (non-Javadoc)
				 * @see com.qiguan.grab.http.StringMap.Consumer#accept(java.lang.String, java.lang.Object)
				 */
				@Override
				public void accept(String key, Object value) {
					requestBuilder.header(key, value.toString());
				}
			});
		}
		 requestBuilder.header("Authorization", "Client-ID ");
		// 用户代理
		requestBuilder.header("User-Agent", userAgent());
		final long start = System.currentTimeMillis();
		// IpTag
		final IpTag tag = new IpTag();
		httpClient.newCall(requestBuilder.tag(tag).build()).enqueue(new Callback() {
			
			/* (non-Javadoc)
			 * @see com.squareup.okhttp.Callback#onFailure(com.squareup.okhttp.Request, java.io.IOException)
			 */
			@Override
			public void onFailure(Request request, IOException e) {
				long duration = (System.currentTimeMillis() - start) / 1000;
				cb.complete(new ResponseTag.Builder().statusCode(-1).address(tag.ip).url(request.urlString()).duration(duration).msg(e.getMessage()).build());
			}

			/* (non-Javadoc)
			 * @see com.squareup.okhttp.Callback#onResponse(com.squareup.okhttp.Response)
			 */
			@Override
			public void onResponse(com.squareup.okhttp.Response response) throws IOException {
				long duration = (System.currentTimeMillis() - start) / 1000;
				// 用于回调
				cb.complete(new ResponseTag.Builder().response(response).address(tag.ip).duration(duration).create().build());
			}
		});
	 }
	

	/**  
	 * <pre>
	 * Description	IP TAG
	 * Copyright:	Copyright (c)2016
	 * Company:		杭州启冠网络技术有限公司
	 * Author:		Administrator
	 * Version: 	1.0
	 * Create at:	2016年4月19日 下午4:36:46  
	 *  
	 * Modification History:  
	 * Date         Author      Version     Description 
	 * ------------------------------------------------------------------  
	 * 
	 * </pre>
	 */  
	private static class IpTag {
		public String ip = null;
	}
}
