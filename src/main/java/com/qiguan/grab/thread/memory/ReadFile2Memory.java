package com.qiguan.grab.thread.memory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


/**  
 * <pre>
 * Description
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月9日 上午11:47:24  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class ReadFile2Memory {
	
	/**
	 * 日志对象
	 */
	private static final Logger logger = Logger.getLogger(ReadFile2Memory.class);
	
	/**
	 * 一次性读取文件到内存
	 * 
	 * @param inputPath
	 * @return
	 */
	public static List<String> read(String inputPath) {
		logger.info("start read file to Memory...");
		long start = System.currentTimeMillis();
		logger.info("start read file to Memory");
		List<String> linesList = null;
		try {
			System.gc();
			linesList = FileUtils.readLines(new File(inputPath), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		logger.info("加载文件到内存总共花费" + ((end - start ) / 1000)+ "毫秒");
		return linesList;
	}
	
	public static void main(String[] args) {
		List<String> lineList = read("G://url_lineout.txt/url_lineout_clean_1.txt");
		System.out.println(lineList.size() + ":" + lineList.get(0));
	}
}
