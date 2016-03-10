package com.qiguan.grab.thread.collection;

import com.qiguan.grab.util.ConfigUtil;

/**  
 * <pre>
 * Description
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月8日 下午2:06:34  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class App {
	
	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {  
		MyLogData data = new MyLogData();
		Thread provider = new MyProvider(data, "生产者-1", ConfigUtil.getValue("log.source.path"));
		provider.start();
		
		// 开启五个线程
		for (int i = 1; i <= 5; i++) {
			Thread t = new MyConsumer(data, "消费者-" + i);
			t.start();
		}
		Thread.sleep(3000);
		System.out.println("--------------| 结束");
	}
}
