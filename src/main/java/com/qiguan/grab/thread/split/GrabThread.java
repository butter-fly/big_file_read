package com.qiguan.grab.thread.split;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.qiguan.grab.util.StringUtil;

/**  
 * <pre>
 * Description	文件抓取
 * Copyright:	Copyright (c)2014  
 * Company:		墙角信息科技有限公司
 * Author:		lenovo
 * Version:		1.0  
 * Create at:	2016年3月13日 下午12:06:52  
 *  
 * 修改历史:
 * 日期    作者    版本  修改描述
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */  
public class GrabThread implements Runnable {
	
	/**
	 * 输出路径
	 */
	private File inputFile;
	
	public GrabThread(String pathname) {
		this.inputFile = new File(pathname);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try { 
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
			BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10 * 1024 * 1024);// 10M缓存
			FileWriter fw = new FileWriter(inputFile.getParent() + File.separator + inputFile.getName() + ".log");
			while (in.ready()) {
				String line = in.readLine();
				fw.append(grabTitle(line));
			}
			bis.close();
			in.close();
			fw.flush();
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		Connection con = null;
		try {
			con = Jsoup.connect(url);
			con.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0");
			doc = con.get();
		} catch (Exception e) {
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
		doc = null;
		con = null;
		System.gc();
		return sb.toString();
	}

}
