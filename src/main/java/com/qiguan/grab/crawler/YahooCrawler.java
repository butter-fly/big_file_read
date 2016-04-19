package com.qiguan.grab.crawler;

import org.jsoup.nodes.Document;

import cn.edu.hfut.dmic.webcollector.crawler.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;

import java.util.regex.Pattern;

/**  
 * <pre>
 * Description
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年4月15日 下午2:19:48  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class YahooCrawler extends BreadthCrawler {

	/**
	 * @param crawlPath
	 *            crawlPath is the path of the directory which maintains
	 *            information of this crawler
	 * @param autoParse
	 *            if autoParse is true,BreadthCrawler will auto extract links
	 *            which match regex rules from pag
	 */
	public YahooCrawler(String crawlPath, boolean autoParse) {
		super(crawlPath, autoParse);
		/* start page */
		this.addSeed("http://blog.csdn.net/ajaxhu/article/details/");

		/* fetch url like http://news.yahoo.com/xxxxx */
		this.addRegex("http://blog.csdn.net/ajaxhu/article/details/.*");
		/* do not fetch url like http://news.yahoo.com/xxxx/xxx) */
		this.addRegex("-http://blog.csdn.net/ajaxhu/article/details/.+/.*");
		/* do not fetch jpg|png|gif */
		this.addRegex("-.*\\.(jpg|png|gif).*");
		/* do not fetch url contains # */
		this.addRegex("-.*#.*");
	}

	@Override
	public void visit(Page page, Links nextLinks) {
		String url = page.getUrl();
		/* if page is news page */
		if (Pattern.matches("http://news.yahoo.com/.+html", url)) {
			/* we use jsoup to parse page */
			Document doc = page.getDoc();

			/* extract title and content of news by css selector */
			String title = doc.select("h1[class=headline]").first().text();
			String content = doc.select("div[class=body yom-art-content clearfix]").first().text();

			System.out.println("URL:\n" + url);
			System.out.println("title:\n" + title);
			System.out.println("content:\n" + content);
		}
	}

	public static void main(String[] args) throws Exception {
		YahooCrawler crawler = new YahooCrawler("crawl", true);
		crawler.setThreads(50);
		crawler.setTopN(100);
		// crawler.setResumable(true);
		/* start crawl with depth of 4 */
		crawler.start(4);
	}

}
