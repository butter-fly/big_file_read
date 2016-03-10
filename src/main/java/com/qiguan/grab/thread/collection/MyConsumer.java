package com.qiguan.grab.thread.collection;



/**  
 * <pre>
 * Description	消费者
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月8日 下午1:55:12  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class MyConsumer extends Thread {
	
	/**
	 * 数据对象
	 */
	private MyLogData data;

	/**
	 * @param data
	 * @param name
	 */
	public MyConsumer(MyLogData data, String name) {
		super(name);
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		String str;
		try {
			while (true) {
				str = data.getData();
				if (null == str) break;
				// System.out.println(this.getName()+"  消费了:    " + str);
				grab(str);
				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 抓取内容
	 * 
	 * @param url
	 */
	private synchronized void grab(String url) {
//		Document doc;
//		try {
//			doc = JsoupUtil.getInstance(url, "get");
//			System.out.println(doc.title());
//			FileUtil.append2File("G://url_lineout.txt/title.txt", doc.title());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
	}
}
