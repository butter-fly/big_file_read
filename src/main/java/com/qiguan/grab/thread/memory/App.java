package com.qiguan.grab.thread.memory;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.qiguan.grab.util.ConfigUtil;
import com.qiguan.grab.word.CheckSensitiveWord;

/**  
 * <pre>
 * Description	数据一次加载到内存中
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月9日 下午1:13:40  
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
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
//		List<String> lineList = ReadFile2Memory.read(ConfigUtil.getValue("log.source.path"));
//		List<String> lineList = ReadFile2Memory.read("d://url_lineout_new_5.txt");
		//new DealTask().start(lineList);
		
		// 数据分片
		List<List<String>> lineArray = divTasks(ReadFile2Memory.read(ConfigUtil.getValue("log.new.path")), ConfigUtil.getIntValue("reader.part.threadNum"));
		logger.info("实际要启动的工作线程数：" + lineArray.size());  
		// 创建一个固定大小的线程池
		ExecutorService ex = Executors.newFixedThreadPool(lineArray.size());
		// 异步任务用于获取结果
		List<FutureTask<List<String>>> futures = new ArrayList<FutureTask<List<String>>>(lineArray.size());
		// 响应返回
		FutureTask<List<String>> task = null;
		// 敏感词路径
		String sensitivePath = ConfigUtil.getValue("sensitive.word.path");
		// 敏感词库处理
		CheckSensitiveWord checkSensitiveWord = new CheckSensitiveWord();
		FileWriter fos = new FileWriter(sensitivePath ,true);
		try {
			// 自动关闭
			for (int i = 1; i <= lineArray.size(); i++ ) {
				List<String> itemList = lineArray.get(i - 1);
				task = new FutureTask<List<String>>(new CallTask(i, itemList, fos, checkSensitiveWord));
				// 产生结果
				ex.execute(task);
				futures.add(task);
			}
			
			// 抓取结果汇总处理
//			List<String> result = new ArrayList<String>();
//			for (FutureTask<List<String>> future : futures) {
//				// 合并操作
//				result.addAll(future.get());
//			}
//			System.out.println("抓取总记录：" + result.size());
//			Thread.sleep(1000);
//			File file = new File("D://result_log_new.txt");
//			if (file.exists()) {
//				file.delete();
//			} 
//			// 创建新文件
//			file.createNewFile();
//			// 合并结果输出到指定文件中
//			FileOutputStream outStream = new FileOutputStream(file);
//			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
//			objectOutputStream.writeObject(result);
//			outStream.close();
			
			// 关闭启动线程
			ex.shutdown();
			// 等待子线程结束，再继续执行下面的代码
			ex.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			System.out.println("all thread complete");
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * 任务分发
	 * 
	 * @param taskList
	 * @param threadCount
	 */
	public static List<List<String>>  divTasks(List<String> taskList, int threadCount) {
		// 每个线程至少要执行的任务数,假如不为零则表示每个线程都会分配到任务
		int minTaskCount = taskList.size() / threadCount;
		// 平均分配后还剩下的任务数，不为零则还有任务依个附加到前面的线程中
		int remainTaskCount = taskList.size() % threadCount;
		// 自然只需要启动与任务相同个数的工作线程，一对一的执行
		int actualThreadCount = minTaskCount > 0 ? threadCount : remainTaskCount;
		// 要启动的线程数组，以及每个线程要执行的任务列表
		List<List<String>> taskListPerThread = new ArrayList<List<String>>();
		int taskIndex = 0;
		// 平均分配后多余任务，每附加给一个线程后的剩余数，重新声明与 remainTaskCount
		int remainIndces = remainTaskCount;
		for (int i = 0; i < actualThreadCount; i++) {
			taskListPerThread.add(i, new ArrayList<String>());
			// 如果大于零，线程要分配到基本的任务
			if (minTaskCount > 0) {
				for (int j = taskIndex; j < minTaskCount + taskIndex; j++) {
					taskListPerThread.get(i).add(taskList.get(j));
				}
				taskIndex += minTaskCount;
			}
			// 假如还有剩下的，则补一个到这个线程中
			if (remainIndces > 0) {
				taskListPerThread.get(i).add(taskList.get(taskIndex++));
				remainIndces--;
			}
		}
		// 打印任务的分配情况
		for (int i = 0; i < taskListPerThread.size(); i++) {
			System.out.println("线程 " + i + " 的任务数：" + taskListPerThread.get(i).size());
		}
		System.out.println(taskList.size());
		return taskListPerThread;
	}
}
