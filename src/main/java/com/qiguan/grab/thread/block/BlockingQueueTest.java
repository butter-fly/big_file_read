package com.qiguan.grab.thread.block;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import com.qiguan.grab.util.ConfigUtil;

/**  
 * <pre>
 * Description	队列数据处理测试
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月7日 下午5:16:08  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */  
public class BlockingQueueTest {
	
	/**
	 * 目标文件
	 */
	public static final String targetPath = "G://url_lineout.txt/url_lineout_temp.txt";
	
	/**
	 * 当前已生成记录
	 */
	public AtomicLong currentSynCount = new AtomicLong(0);
	
	/**
	 * 文件瘦身 保存有效数据
	 * 
	 * @param sourcePath
	 * @param targetPath
	 */
	public void  reduceFile(String sourcePath, String targetPath) {
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(ConfigUtil.getValue("log.source.path"))));
			BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 20 * 1024 * 1024);// 10M缓存
			FileWriter fw = new FileWriter(targetPath);
			while (in.ready()) {
				String line = in.readLine();
				String [] lineArray = line.split("	");
				if (null != lineArray && lineArray.length == 3) {
					//System.out.println(lineArray[2]);
					fw.append(lineArray[2] + "\r\n");
					currentSynCount.incrementAndGet();//递增
				}
			}
			in.close();
			fw.flush();
			fw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("URL记录数:" + currentSynCount.get());
		}
	}
	
	/**
	 * 处理日志
	 * 
	 * @param inputPath
	 * @throws InterruptedException 
	 */
	public void postUrl(String inputPath) throws InterruptedException {
		Long start = System.currentTimeMillis();
		// 声明一个容量为20的缓存队列
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		
		// 实例化数据生产者
		GrabProducer producer1 = new GrabProducer(inputPath, queue);
		
		// 实例化数据消费者
		GrabConsumer consumer1 = new GrabConsumer(queue);
		GrabConsumer consumer2 = new GrabConsumer(queue);
		GrabConsumer consumer3 = new GrabConsumer(queue);
		GrabConsumer consumer4 = new GrabConsumer(queue);
		GrabConsumer consumer5 = new GrabConsumer(queue);
		GrabConsumer consumer6 = new GrabConsumer(queue);
		GrabConsumer consumer7 = new GrabConsumer(queue);
		
		// 借助Executors
		ExecutorService service = Executors.newFixedThreadPool(15);
		
		// 启动生产者线程
		service.execute(producer1);
		
		// 启动消费者线程
		service.execute(consumer1);
		service.execute(consumer2);
		service.execute(consumer3);
		service.execute(consumer4);
		service.execute(consumer5);
		service.execute(consumer6);
		service.execute(consumer7);
		
		Thread.sleep(2000);
		Long end = System.currentTimeMillis();
		
		System.out.println("读取并消费耗时：" + ((end - start)/1000));
		// 退出Executor
		service.shutdown();
	}
	
	public static void main(String[] args) throws InterruptedException {
		BlockingQueueTest bq  = new BlockingQueueTest();
		// 处理日志文件
//		bq.reduceFile(sourcePath, targetPath);
		
		// 发送请求
		bq.postUrl(targetPath);
	}
}
