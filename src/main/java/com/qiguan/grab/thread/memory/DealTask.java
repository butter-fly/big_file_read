package com.qiguan.grab.thread.memory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.qiguan.grab.util.StringUtil;

/**  
 * <pre>
 * Description
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月9日 下午1:04:57  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class DealTask {
	
	/**
	 * 日志的对象
	 */
	private final Logger logger = Logger.getLogger(Logger.class);
	
	/**
	 * 过滤URL
	 */
	private Map<String, String> filterMap; 
	
	/**
	 * 任务执行
	 */
	private ExecutorService exec;
	
	
	/**
	 * 批处理
	 */
	private CompletionService<String> completionService;

	
	/**
	 * 
	 */
	public DealTask() {
		super();
		this.filterMap =  new HashMap<String, String>();
		this.exec = Executors.newFixedThreadPool(20);
		this.completionService = new ExecutorCompletionService<String>(exec);
	}
	
	/**
	 * 解析html
	 */
	public void start(final List<String> list) {
		List<String> lineList = Collections.synchronizedList(list);
		for (final String line : lineList) {
			// 过滤请求URL处理
			if (!filterMap.containsKey(StringUtil.getDomain(line))) {
				// 提交任务
				completionService.submit(new Callable<String>() {
					public String call() throws Exception {
						// 抓取内容
						String [] lineArray = line.split("	");
						if (null != lineArray && lineArray.length == 3 && null != lineArray[2]) {
							String contentStr = grabTitle(lineArray[2]);
							if (null != contentStr) {
								logger.info(contentStr);
							}
						}
						return line;
					}
				});
				 
			}
		}
	}
	
	/**
	 * @param list
	 * @param threadCount
	 */
	public void processLineTask(final List<String> list, int threadCount) {
		// 确保线程数量不大于队列长度
		threadCount = Math.min(threadCount, list.size());
		logger.info("the work thread count is:" + threadCount);
		for (int i = 0; i < threadCount; i++) {
			new Thread() {
				public void run() {
					while (true) {
						String line;
						// 提取队列元素的时候，需要锁住队列
						synchronized (list) {
							// 当队列长度为0的时候，线程逐个结束
							if (list.size() == 0) {
								break;
							}
							line = list.remove(0);
						}
						// 过滤请求URL处理
						if (!filterMap.containsKey(StringUtil.getDomain(line))) {
							// 抓取内容
							String [] lineArray = line.split("	");
							if (null != lineArray && lineArray.length == 3 && null != lineArray[2]) {
								String contentStr = grabTitle(lineArray[2]);
								if (null != contentStr) {
									logger.info(contentStr);
								}
							}
							/*String contentStr = grabTitle(line);
							if (null != contentStr) {
								logger.info(contentStr);
							}*/
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}.start();
		}
	}
	
	/**
	 * 抓取网站url的title和keywords
	 * 
	 * @param url
	 * @return
	 */
	public String grabTitle(String url) {
		Document doc = null;
		try {
			Connection con = Jsoup.connect(url).timeout(5000);
			con.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0");
			doc = con.get();
		} catch (Exception e) {
			//logger.info(url);
			String domainStr = StringUtil.getDomain(url);
			if (!filterMap.containsKey(domainStr)) {
				filterMap.put(domainStr, url); //过滤
			}
			return null;
		}
		if (null == doc) return null;
		
		
		String title  = doc.title();
		String keywords = null;
		Element el = doc.getElementsByAttributeValue("name", "keywords").first();
		if (null != el) {
			keywords = el.attr("content");
		} 
		if (StringUtil.isNullOrEmpty(title) && StringUtil.isNullOrEmpty(keywords)) {
			return null;
		}
		
		if (StringUtil.isNullOrEmpty(title)) {
			title = "null";
		}
		if (StringUtil.isNullOrEmpty(keywords)) {
			title = "null";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(doc.title()).append("-&-").append(keywords);
		return sb.toString();
	}
}
