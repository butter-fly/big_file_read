package com.qiguan.grab.thread.block;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;


/**  
 * <pre>
 * Description	消费者
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月7日 下午4:55:52  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class GrabConsumer implements Runnable {
	
	/**
	 * 日志对象
	 */
	private Logger logger = Logger.getLogger(GrabConsumer.class);
	
	/**
	 * 消息队列
	 */
	private BlockingQueue<String> queue;
	
	/**
	 * @param queue
	 */
	public GrabConsumer(BlockingQueue<String> queue) {
		this.queue = queue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		logger.info("启动消费者线程！");
		boolean isRunning = true;
		try {
			logger.info("正从队列获取数据...");
			while (isRunning) {
				String data = queue.poll(2, TimeUnit.SECONDS);
				if (null != data) {
					logger.info("队列中获取数据：" + data);
					//休眠1000ms
					Thread.sleep(10);
					//FileUtil.append2File("D://grab/file/tomcat_log.txt", data);
				} else {
					// 超过2s还没数据，认为所有生产线程都已经退出，自动退出消费线程。
					isRunning = false;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		} finally {
			logger.info(Thread.currentThread().getName() + "退出消费者线程！");
		}
	}
}
