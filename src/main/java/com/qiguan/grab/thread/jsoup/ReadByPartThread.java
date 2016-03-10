package com.qiguan.grab.thread.jsoup;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.qiguan.grab.util.StringUtil;



/**  
 * <pre>
 * Description	分片读取文件内容
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月8日 下午4:35:08  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class ReadByPartThread extends Thread {
	
	/**
	 * 日志对象
	 */
	private Logger logger = Logger.getLogger(ReadByPartThread.class);
	
	/**
	 * 定义字节数组（取水的竹筒）的长度
	 */
	private final int BUFF_LEN = 256;
	/**
	 * 定义读取的起始点
	 */
	private long start;
	
	/**
	 * 定义读取的结束点
	 */
	private long end;
	
	/**
	 * 将读取到的字节输出到raf中 randomAccessFile可以理解为文件流，即文件中提取指定的一部分的包装对象
	 */
	private RandomAccessFile raf;
	
	/**
	 * 队列
	 */
	private ConcurrentLinkedQueue<String> queue;
	
	/**
	 * URL过滤
	 */
	private Map<String, String> filterMap;
	
	/**
	 * 
	 */
	public ReadByPartThread() {
		super();
	}
	
	/**
	 * @param start
	 * @param end
	 * @param raf
	 */
	public ReadByPartThread(long start, long end, RandomAccessFile raf, ConcurrentLinkedQueue<String> queue, Map<String, String> filterMap) {
		this.start = start;
		this.end = end;
		this.raf = raf;
		this.queue = queue;
		this.filterMap = filterMap;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			raf.seek(start);
			// 本线程负责读取文件的大小
			long contentLen = end - start;
			// 定义最多需要读取几次就可以完成本线程的读取
			long times = contentLen / BUFF_LEN + 1;
			System.out.println(this.toString() + " 需要读的次数：" + times);
			byte[] buff = new byte[BUFF_LEN];
			int hasRead = 0;
			String line = null;
			for (int i = 0; i < times; i++) {
				// 之前SEEK指定了起始位置，这里读入指定字节组长度的内容，read方法返回的是下一个开始读的position
				hasRead = raf.read(buff);
				// 如果读取的字节数小于0，则退出循环！ （到了字节数组的末尾）
				if (hasRead < 0) {
					break;
				}
				line = new String(buff, "UTF-8");
				if (filterMap.containsKey(StringUtil.getDomain(line))) {
					continue;
				}
				logger.info("url:" + line);
				String [] lineArray = line.split("	");
				if (null != lineArray && lineArray.length == 3 && null != lineArray[2]) {
					String contentStr = grabTitle(lineArray[2]);
					if (null != contentStr) {
						queue.add(contentStr);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public RandomAccessFile getRaf() {
		return raf;
	}

	public void setRaf(RandomAccessFile raf) {
		this.raf = raf;
	}
	
	public static void main(String[] args) {
		System.out.println(new ReadByPartThread().grabTitle("http://sunnylocus.iteye.com/blog/694666"));
	}
}
