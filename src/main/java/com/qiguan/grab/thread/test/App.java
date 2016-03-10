package com.qiguan.grab.thread.test;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.qiguan.grab.thread.part.IDataHandle;

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
		System.out.println("开始时间：" + new Date());
		// 声明URL缓存队列
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		BigFileReaderByPart.Builder builder = new BigFileReaderByPart.Builder("G:\\url_lineout.txt\\url_lineout.txt", new IDataHandle() {
			public void handle(String line) {
				
			}
		}, queue);
		builder.withTreahdSize(20).withCharset("UTF-8").withBufferSize(1024 * 1024 * 10);
		BigFileReaderByPart bigFileReader = builder.build();
		bigFileReader.start();
		
		// 多线程写入数据到同一个文件
		BufferedWriter  fw = new BufferedWriter(new FileWriter("G:\\url_lineout.txt\\result_log_temp.txt", true));
		Map<String, String> filterMap = new HashMap<String, String>();
		// 线程池
		new TaskHandler(fw, queue, filterMap).start();
		
		// 关闭数据流
		//fw.close();
		
		System.out.println("结束时间：" + new Date());
		logger.info("app2 Shutdown...");
	}
}
