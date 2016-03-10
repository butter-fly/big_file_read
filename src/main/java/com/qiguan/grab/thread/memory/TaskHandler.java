package com.qiguan.grab.thread.memory;

import java.util.Queue;

/**  
 * <pre>
 * Description
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月9日 下午1:11:42  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class TaskHandler implements Runnable {
	
	/**
	 * 任务名称
	 */
	private String name;
	/**
	 * 任务队列
	 */
	private final Queue<String> tasks;
	/**
	 * @param name
	 * @param tasks
	 */
	public TaskHandler(String name, Queue<String> tasks) {
		this.name = name;
		this.tasks = tasks;
	}

	public void run() {
		System.out.println("start task is :" + name);
		while (!tasks.isEmpty()) {
			String task = tasks.poll();
			if (task != null) {
				
			}
		}
	}

}
