package com.qiguan.grab.crawler;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import org.jsoup.select.Elements;

import com.qiguan.grab.util.ConfigUtil;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**  
 * <pre>
 * Description	重写爬虫WebCrawler父类相关方法
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月7日 上午10:06:07  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class GrabWebCrawler extends WebCrawler {
	
	/**
	 * 日志对象
	 */
	private Logger logger = Logger.getLogger(GrabWebCrawler.class);
	
	/**
	 * 过滤抓取正则
	 */
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g|ico" + "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	
	
	/* (non-Javadoc)
	 * @see edu.uci.ics.crawler4j.crawler.WebCrawler#shouldVisit(edu.uci.ics.crawler4j.url.WebURL)
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		if (FILTERS.matcher(href).matches() || !href.startsWith(ConfigUtil.getValue("data.baseMatchUrl"))) {
			return false;
		}
		 
		return true;
	}
	 
	/* (non-Javadoc)
	 * @see edu.uci.ics.crawler4j.crawler.WebCrawler#visit(edu.uci.ics.crawler4j.crawler.Page)
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		logger.info("请求的Url:" + url);
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();
			Document doc = Jsoup.parse(html);
			Elements contents = doc.select("html");
			// 抓取某一个节点
			System.out.println(contents);
		}
	}
}
