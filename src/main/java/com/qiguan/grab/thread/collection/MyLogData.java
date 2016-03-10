package com.qiguan.grab.thread.collection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**  
 * <pre>
 * Description	日志数据
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月8日 下午1:53:21  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */  
public class MyLogData {
	/**
	 * 集合数据
	 */
	List<String> shareList = Collections.synchronizedList(new LinkedList<String>());  
	  
	/**
	 * @param product
	 * @throws InterruptedException
	 */
	public synchronized void setData(String product) throws InterruptedException {
		while (shareList.size() != 0) {
			this.wait();
		}

		this.push(product);
		System.out.println(Thread.currentThread().getName() + " 生产了|  " + product);
		this.notifyAll();

	}

	/**
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized String getData() throws InterruptedException {
		while (shareList.size() == 0) {
			this.wait();
		}
		String str = this.popLast();
		System.out.println(Thread.currentThread().getName() + " 消费了   ---|  " + str);
		this.notifyAll();
		return str;

	}

	/*********************
	 * 设置和输出队列
	 * 
	 * @param obj
	 */

	private void push(String obj) {
		synchronized (shareList) {
			shareList.add(0, obj);
		}
	}

	/**
	 * 取出
	 * 
	 * @return
	 */
	private String popLast() {
		synchronized (shareList) {
			if (shareList.size() == 0) {
				return null;
			}
			return shareList.remove(this.shareList.size() - 1);
		}
	}
}
