package com.qiguan.grab.thread.part;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.qiguan.grab.util.ConfigUtil;



/**  
 * <pre>
 * Description
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月7日 下午2:59:54  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */  
public class App {
	
	/**
	 * 日志对象
	 */
	private static final Logger logger = Logger.getLogger(App.class);

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		logger.info("开始时间：" + new Date());
		// 声明缓存队列
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		BigFileReaderByPart.Builder builder = new BigFileReaderByPart.Builder(ConfigUtil.getValue("log.source.path"), new IDataHandle() {
			public void handle(String line) {
				
			}
		}, queue);
		builder.withCharset("UTF-8").withTreahdSize(ConfigUtil.getIntValue("reader.part.threadNum")).withBufferSize(524288);
		BigFileReaderByPart bigFileReader = builder.build();
		bigFileReader.start();
		
		// 线程写入数据到同一个文件
		BufferedWriter  fw = new BufferedWriter(new FileWriter(ConfigUtil.getValue("log.target.path"), true));
		// 线程池
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(new QueueResult2File(fw, queue));
		
		// 关闭线程
		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.DAYS);
		
		// 关闭数据流
		fw.close();
		
		System.out.println("结束时间：" + new Date());
		logger.info("app1 Shutdown...");
	}
}
