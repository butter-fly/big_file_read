package com.qiguan.grab.crawler;

import com.qiguan.grab.util.ConfigUtil;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * <pre>
 * Description	程序执行入口
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月7日 上午10:06:41  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------
 * 
 * </pre>
 */
public class App {
	
	/**
	 * 数据临时存储目录
	 */
	private static String dataCrawlStorageFolder = null;
	
	/**
	 * 工作线程数目
	 */
	private static int numberOfCrawlers = 10;
	
	static {
		dataCrawlStorageFolder = ConfigUtil.getValue("data.crawlStorageFolder");
		numberOfCrawlers = ConfigUtil.getIntValue("data.threadNum");
	}
	
	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(dataCrawlStorageFolder);

		/*
		 * 爬虫控制器的参数初始化.
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		/*
		 *  添加抓取的URL
		 */
		controller.addSeed("http://www.devnote.cn/articles/catalog/mongodb");

		/*
		 * 启动线程爬取u
		 */
		controller.start(GrabWebCrawler.class, numberOfCrawlers);
	}
}
