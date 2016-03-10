package com.qiguan.grab.thread.jsoup;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**  
 * <pre>
 * Description	
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月8日 下午4:30:44  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class DealResult2File implements Runnable {
	
	/**
	 * 写入
	 */
	private BufferedWriter writer;
	
	/**
	 * 队列
	 */
	private ConcurrentLinkedQueue<String> queue;
	
	/**
	 * @param writer
	 * @param queue
	 */
	public DealResult2File(BufferedWriter writer, ConcurrentLinkedQueue<String> queue) {
		this.writer = writer;
		this.queue = queue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {// 循环监听
			if (!queue.isEmpty()) {
				try {
					writer.write(queue.poll());
					writer.newLine();
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
