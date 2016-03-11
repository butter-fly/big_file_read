package com.qiguan.grab.thread.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

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
 * Create at:	2016年3月9日 下午1:11:42  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class TaskHandler extends Thread {
	
	private final Logger logger = Logger.getLogger(TaskHandler.class); // 日志对象
	
	/**
	 * 写入
	 */
	private BufferedWriter writer;
	
	/**
	 * 队列
	 */
	private BlockingQueue<String> queue;
	
	/**
	 * 过滤URL
	 */
	private Map<String, String> filterMap; 
	
	/**
	 * @param writer
	 * @param queue
	 */
	public TaskHandler(BufferedWriter writer, BlockingQueue<String> queue, Map<String, String> filterMap) {
		this.writer = writer;
		this.queue = queue;
		this.filterMap = filterMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {// 循环监听
			if (!queue.isEmpty()) {
				try {
					String url = queue.poll();
					// 过滤请求URL处理
					if (!filterMap.containsKey(StringUtil.getDomain(url))) {
						// 抓取内容
						String str = grabTitle(url);
						if (null != str) {
							writer.write(str);
							writer.newLine();
							writer.flush();
							logger.info(str);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
