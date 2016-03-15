package com.qiguan.grab.thread.split;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.qiguan.grab.util.ConfigUtil;

public class App {

	public static void main(String[] args) {
		System.out.println(new File(ConfigUtil.getValue("log.new.path")).length() / 8);
		new App().exe();
	}
	
	public void exe() {
		System.out.println(FileUtil.currentWorkDir);
		FileUtil fileUtil = new FileUtil();
		try {
			// 大文件拆分
			File sourceFile = new File(ConfigUtil.getValue("log.new.path"));
			int threadNum = ConfigUtil.getIntValue("reader.part.threadNum");
			int size = (int) (sourceFile.length() / threadNum);
			List<String> fileList = fileUtil.splitBySize(ConfigUtil.getValue("log.new.path"), size);
			try {
				System.out.println("fileList:" + fileList);
				// 稍等10秒，等前面的小文件全都写完
				Thread.sleep(1000);
				
//				// 线程池
//				ExecutorService executor = Executors.newCachedThreadPool();
//				for (String file : fileList) {
//					executor.execute(new GrabThread(file));
//				}
//				// 关闭
//				executor.shutdown();
				
				// 合并成新文件
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
