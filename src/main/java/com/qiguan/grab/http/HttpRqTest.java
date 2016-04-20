package com.qiguan.grab.http;

import java.io.IOException;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class HttpRqTest {
	
	/**
	 * 
	 */
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	/**
	 * 
	 */
	private OkHttpClient client = new OkHttpClient();
	
	/**
	 * Get 
	 * 
	 * @param url
	 * @param json
	 * @throws IOException
	 */
	void getExecute(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();
		Response response = client.newCall(request).execute();
		if (response.isSuccessful()) {
			System.out.println(response.code());
			System.out.println(response.body().string());
		}
	}
	
	/**
	 * Get 
	 * 
	 * @param url
	 * @param json
	 * @throws IOException
	 */
	void getEnqueue(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();
		client.newCall(request).enqueue(new Callback() {
			
			/* (non-Javadoc)
			 * @see com.squareup.okhttp.Callback#onResponse(com.squareup.okhttp.Response)
			 */
			@Override
			public void onResponse(Response response) throws IOException {
				if (response.isSuccessful()) {
					System.out.println("getEnqueue:"+ response.code());
					System.out.println(response.body().string());
				}
			}
			
			/* (non-Javadoc)
			 * @see com.squareup.okhttp.Callback#onFailure(com.squareup.okhttp.Request, java.io.IOException)
			 */
			@Override
			public void onFailure(Request request, IOException e) {
				
			}
		});;
	}

	/**
	 * @param url
	 * @param json
	 * @return
	 * @throws IOException
	 */
	String postExecute(String url, String json) throws IOException {
		//RequestBody body = new FormEncodingBuilder().add("id", "1").build();
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		System.out.println(response.code());
		String response2Body = response.body().string();
		return response2Body;
	}
	
	/**
	 * @param url
	 * @param json
	 * @return
	 * @throws IOException
	 */
	void postEnqueue(String url, String json) {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder()
		.url(url)
		.post(body).build();
		client.newCall(request).enqueue(new Callback() {
			/**
			 * @param response
			 * @throws IOException
			 */
			public void onResponse(Response response) throws IOException {
				// NOT UI Thread
				if (response.isSuccessful()) {
					System.out.println(response.code());
					System.out.println(response.body().string());
				}
			}

			public void onFailure(Request request, IOException e) {

			}
		});
	}
	
	public static void main(String[] args) {
		String json ="{\"id\":\"1\"}";
//		HttpRqTest r = new HttpRqTest();
//		try {
//			System.out.println(r.postExecute("http://localhost:7002/bbs/testUser/get", json));
//			r.postEnqueue("http://localhost:7002/bbs/testUser/get", json);
//			
//			r.getExecute("http://127.0.0.1:7002/bbs/dic/detail?id=39");
//			r.getEnqueue("http://127.0.0.1:7002/bbs/dic/detail?id=39");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		HttpClient c = new HttpClient();
		try {
			String url = "http://localhost:7002/sdk-gradle/testUser/get";
//			Response res = c.post(url, json, null);
//			System.out.println("同步请求结果：" + res.body().string());
//			Headers headers = res.headers();
//			System.out.println(headers.toString());
			
			
			System.out.println("开始异步模拟Http请求...............");
			c.asyncPost(url, json, new StrMap().put("Accept", "application/json"), new AsyncHttpCallback() {
				
				/* (non-Javadoc)
				 * @see com.qiguan.grab.http.AsyncCallback#complete(com.qiguan.grab.http.ResponseTag)
				 */
				@Override
				public void complete(ResponseTag tag) {
					System.out.println("异步处理：" + tag.statusCode + "\r\n body : " + tag.body + " \r\n:" + tag.address + " \r\n: " + tag.url);
					System.out.println(tag.duration);
				}
			});
			
//			// 同步上传单个文件
//			c.multipartPost("http://localhost:7002/sdk-gradle/upload/single", new File("G://images/avatar/2.jpg"), null, new StringMap().put("Accept", "application/octet-stream"));
//			// 异步上传单个文件
//			c.asyncMultipartPost("http://localhost:7002/sdk-gradle/upload/mult", new File("G://images/avatar/1.jpg"), null, new StringMap().put("Accept", "application/json"), new AsyncCallback() {
//				/* (non-Javadoc)
//				 * @see com.qiguan.grab.http.AsyncCallback#complete(com.qiguan.grab.http.ResponseTag)
//				 */
//				@Override
//				public void complete(ResponseTag tag) {
//					System.out.println("异步处理：" + tag.statusCode + "\r\n body : " + tag.body + " \r\n:" + tag.address + " \r\n: " + tag.url);
//					System.out.println(tag.duration);
//				}
//			});
			
			// 批量上传多个文件
			c.asyncMultipleFileUpload("http://localhost:7002/sdk-gradle/upload/mult", new StrMap().put("admin", "G://images/avatar/admin.jpg").
					put("2", "G://images/avatar/2.jpg").
					put("1", "G://images/avatar/1.jpg").
					put("url", "G://images/avatar/url_split_1.txt").
					put("data", "G://images/avatar/data.dat"), "application/octet-stream", new StrMap().put("Accept", "application/json"), new AsyncHttpCallback() {
				
				/* (non-Javadoc)
				 * @see com.qiguan.grab.http.AsyncCallback#complete(com.qiguan.grab.http.ResponseTag)
				 */
				@Override
				public void complete(ResponseTag tag) {
					System.out.println("多文件异步上传：" + tag.statusCode + "\r\n body : " + tag.body + " \r\n:" + tag.address + " \r\n: " + tag.url);
					System.out.println(tag.error);
				}
			});
		} catch (HttpIoException e) {
			System.out.println(e.code());
		} 
	}
}
