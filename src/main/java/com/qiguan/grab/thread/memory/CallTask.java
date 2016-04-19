package com.qiguan.grab.thread.memory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.qiguan.grab.util.ConfigUtil;
import com.qiguan.grab.util.StringUtil;
import com.qiguan.grab.word.CheckSensitiveWord;

/**  
 * <pre>
 * Description
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月14日 上午9:50:43  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class CallTask implements Callable<List<String>> {
	
	/**
	 * 日志对象
	 */
	private Logger logger = Logger.getLogger(CallTask.class);
	
	/**
	 * 分片集合
	 */
	private List<String> itemList;
	
	/**
	 * 敏感词输出流
	 */
	private FileWriter wordfos;
	
	/**
	 * 敏感词处理
	 */
	private CheckSensitiveWord checkSensitiveWord;
	
	/**
	 * 抓取内容写入
	 */
	private FileWriter grabfw;
	
	/**
	 * @param i 文件编号
	 * @param itemList 请求Url集合
	 * @param sensitivePath 敏感词过滤文件路径
	 */
	public CallTask(int i, List<String> itemList, FileWriter wordfos, CheckSensitiveWord checkSensitiveWord) {
		this.itemList = itemList;
		this.wordfos = wordfos;
		this.checkSensitiveWord = checkSensitiveWord;
		try {
			File file = new File(ConfigUtil.getValue("out.grab.dir") + i + "_result.txt");
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			grabfw = new FileWriter(file, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public List<String> call() throws Exception {
		List<String> retList = new ArrayList<String>();
		String line = null;
		String domain = null;
		String result = null;
		logger.info(Thread.currentThread().getName() +  " ：开始处理数据");
		while (true) {
			// 任务List中有任务时进行循环
			if (itemList != null && itemList.size() > 0) {
				line = itemList.remove(0).trim();
				domain = StringUtil.getDomain(line);
				// 包含敏感词过滤
				if (checkSensitiveWord.isContaintSensitiveWord(domain)) {
					continue;
				}
				// 取得一个任务，并从任务List中删除该任务
				result = grabTitle(line, domain);
				if (null != result) {
					// retList.add(result);
					grabfw.write(result + "\r\n");
					grabfw.flush();
					// logger.info(Thread.currentThread().getName() + ":" + result);
				}  else {
					wordfos.write(domain + "\r\n");
					wordfos.flush();
					checkSensitiveWord.add(domain);
				}
			} else {
				// 资源释放
				close();
				logger.info(Thread.currentThread().getName() +  " ： 数据处理完成");
				break;
			}
		}
		return retList;
	}
	
	/**
	 * 抓取网站url的title和keywords
	 * 
	 * @param url
	 * @return
	 * @throws Exception 
	 */
	private String grabTitle(String url, String domain) throws Exception {
		Document doc = null;
		Connection con = null;
		try {
			con = Jsoup.connect(url);
			con.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0");
			doc = con.get();
		} catch (Exception e) {
//			logger.info("请求异常：" + e.getMessage());
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
		
		// 销毁对象
		con = null;
		doc = null;
		System.runFinalization();
		System.gc();
		return sb.toString();
	}
	
	/**
	 * 关闭数据
	 */
	private void close() {
		if (null != grabfw) {
			try {
				grabfw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
