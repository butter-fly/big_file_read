package com.qiguan.grab.thread.jsoup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**  
 * <pre>
 * Description
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月8日 下午4:43:16  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class MultiReaderFileTest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final int DOWN_THREAD_NUM = 10;// 起10个线程去读取指定文件
		final String OUT_FILE_NAME = "F://url_lineout.txt/url_lineout.txt";
		RandomAccessFile[] outArr = new RandomAccessFile[DOWN_THREAD_NUM];
		try {
			System.out.println("开始时间：" + new Date());
			long length = new File(OUT_FILE_NAME).length();
			System.out.println("文件总长度：" + length + "字节");
			// 每线程应该读取的字节数
			long numPerThred = length / DOWN_THREAD_NUM;
			System.out.println("每个线程读取的字节数：" + numPerThred + "字节");
			// 整个文件整除后剩下的余数
			long left = length % DOWN_THREAD_NUM;
			// 线程池
			ExecutorService executor = Executors.newCachedThreadPool();
			// 队列
			ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>(); 
			Map<String, String> filterMap = new HashMap<String, String>();
			//多线程往队列中写入数据  
			for (int i = 0; i < DOWN_THREAD_NUM; i++) {
				// 为每个线程打开一个输入流、一个RandomAccessFile对象，
				// 让每个线程分别负责读取文件的不同部分
				outArr[i] = new RandomAccessFile(OUT_FILE_NAME, "rw");
				// 最后一个
				if (i == DOWN_THREAD_NUM - 1) {
					executor.execute(new ReadByPartThread(i * numPerThred, (i + 1) * numPerThred + left, outArr[i], queue, filterMap));
				} else {
					// 每个线程负责读取一定的numPerThred个字节
					executor.execute(new ReadByPartThread(i * numPerThred, (i + 1) * numPerThred, outArr[i], queue, filterMap));
				}
			}
			
			
			// 多线程写入数据到同一个文件
			BufferedWriter  fw = new BufferedWriter(new FileWriter("F://url_lineout.txt/result_log.txt", true));
			for (int i = 0; i < DOWN_THREAD_NUM/2; i++) {
				
			}
			
			executor.execute(new DealResult2File(fw, queue));
			// 关闭线程
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.DAYS);
			
			// 关闭数据流
			fw.close();
			
			System.out.println("结束时间：" + new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
