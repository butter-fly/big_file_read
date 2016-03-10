package com.qiguan.grab.thread.jsoup;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.qiguan.grab.util.StringUtil;


/**
 * <pre>
 * Description	抓取网站title和keywords
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月8日 下午2:47:06  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------
 * 
 * </pre>
 */

public class GrabTitle implements Runnable {
	
	/**
	 * 日志对象
	 */
	private final Logger logger = Logger.getLogger(GrabTitle.class);

	/**
	 * 缓冲字节流读取-日志文件
	 */
	private static BufferedReader brSourceFile;
	
	/**
	 * 缓冲字节流写入-抓取结果输出文件
	 */
	private static BufferedWriter bwTargetFile;
	
	/**
	 * 缓冲字节流写入-抓取失败URUrlL
	 */
	private static BufferedWriter bwLogFile;
	
	/**
	 * 当前已生成记录
	 */
	public AtomicLong currentSynCount = new AtomicLong(0);
	
	/**
	 * 
	 */
	public GrabTitle() {
		super();
	}
	

	/**
	 * @param inputFile
	 * @param outFile
	 * @param logFile
	 */
	public GrabTitle(String inputFile, String outFile, String logFile) {
		if (brSourceFile == null || bwTargetFile == null) {
			try {
				brSourceFile = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(new File(inputFile))), "utf-8"), 10 * 1024 * 1024);// 10M缓存
				bwTargetFile = new BufferedWriter(new FileWriter(outFile, true));
				bwLogFile = new BufferedWriter(new FileWriter(logFile, true));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {
			try {
				String url = null;
				synchronized (brSourceFile) {
					url = brSourceFile.readLine();
					if (url == null) {
						continue;
					}
//					System.out.println(url);keywords
					String [] lineArray = url.split("	");
					if (null != lineArray && lineArray.length == 3) {
						url = lineArray[2];
					}
				}
				String content = grabTitle(url);
				if (content != null) {
					synchronized (bwTargetFile) {
						bwTargetFile.write(content);
						bwTargetFile.newLine();
						bwTargetFile.flush();
					}
				} else {
					synchronized (bwLogFile) {
						bwLogFile.write(url);
						bwLogFile.newLine();
						bwLogFile.flush();
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 关闭数据流
	 */
	public static void close() {
		try {
			if (brSourceFile != null) {
				brSourceFile.close();
			}
			if (bwTargetFile != null) {
				bwTargetFile.flush();
				bwTargetFile.close();
			}
			if (bwLogFile != null) {
				bwTargetFile.flush();
				bwLogFile.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 抓取网站url的title和keywords
	 * 
	 * @param url
	 * @return
	 */
	public synchronized String grabTitle(String url) {
		Document doc = null;
		try {
			Connection con = Jsoup.connect(url).timeout(5000);
			con.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0");
			doc = con.get();
		} catch (Exception e) {
			logger.info(url);
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

	public void MultiThreadsGetTitle(int threadsNum) throws InterruptedException {
		ExecutorService executor = Executors.newCachedThreadPool();
		for (int i = 0; i < threadsNum; i++) {
			executor.execute(new GrabTitle());
			System.out.println("thread" + i + "started");
		}
		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.DAYS);
		GrabTitle.close();
	}

	public static void main(String[] args) throws InterruptedException {
//		System.out.println(grabTitle("http://www.thinksaas.cn/group/topic/378285"));
		// new GrabTitle("G://url_lineout.txt/url_lineout.txt",  "G://url_lineout.txt/result_log.txt", "G://url_lineout.txt/fail_log.txt").MultiThreadsGetTitle(5);
		new GrabTitle("D://grab/file/url_lineout.txt",  "D://grab/file/result_log.txt", "D://grab/file/fail_log.txt").MultiThreadsGetTitle(10);
	}
}
