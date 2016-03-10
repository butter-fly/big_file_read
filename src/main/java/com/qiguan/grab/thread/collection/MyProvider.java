package com.qiguan.grab.thread.collection;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


/**  
 * <pre>
 * Description	生产者
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月8日 下午1:50:44  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class MyProvider extends Thread {
	/**
	 * 数据对象
	 */
	private MyLogData data;
	
	/**
	 * 文件名称
	 */
	private String inputName;

	/**
	 * @param data
	 * @param name
	 */
	public MyProvider(MyLogData data, String name, String inputName) {
		super(name);
		this.data = data;
		this.inputName = inputName;
	}
	
	// private Thread cThread;  
	@Override
	public void run() {
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(inputName)));
			BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 20 * 1024 * 1024);// 10M缓存
			while (in.ready()) {
				String line = in.readLine();
				String [] lineArray = line.split("	");
				if (null != lineArray && lineArray.length == 3) {
					data.setData(lineArray[2]);
				}
			}
			in.close();
			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
