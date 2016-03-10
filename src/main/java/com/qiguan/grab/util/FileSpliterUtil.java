package com.qiguan.grab.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

/**  
 * <pre>
 * Description	文件分割工具类
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月7日 下午4:07:22  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class FileSpliterUtil {
	
		// 将大数据文件切分到另外的十个小文件中
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public static void sqlitFileData(String filepath, String fileName, String ext, String sqlitPath, int CountFile) throws IOException {
			FileWriter fs = null;
			BufferedWriter fw = null;
			FileReader fr = new FileReader(filepath + "/" + fileName + ext);
			BufferedReader br = new BufferedReader(fr); // 读取获取整行数据
			int i = 1;
			LinkedList WriterLists = new LinkedList(); // 初始化文件流对象集合
			LinkedList fwLists = new LinkedList();
			for (int j = 1; j <= CountFile; j++) {
				// 声明对象
				fs = new FileWriter(sqlitPath + "\\" + fileName + "_" + j + ext, false);
				fw = new BufferedWriter(fs);
				// 将对象装入集合
				WriterLists.add(fs);
				fwLists.add(fw);
			}
			// 判断是文件流中是否还有数据返回
			while (br.ready()) {
				int count = 1;// 初始化第一文件流
				for (Iterator iterator = fwLists.iterator(); iterator.hasNext();) {
					BufferedWriter type = (BufferedWriter) iterator.next();
					if (i == count)// 判断轮到第几个文件流写入数据了
					{
						// 写入数据，跳出，进行下一个文件流，下一个数据的写入
						type.write(br.readLine() + "\r\n");
						break;
					}
					count++;
				}
				// 判断是否到了最后一个文件流了
				if (i >= CountFile) {
					i = 1;
				} else
					i++;
			}
			br.close();
			fr.close();
			for (Iterator iterator = fwLists.iterator(); iterator.hasNext();) {
				BufferedWriter object = (BufferedWriter) iterator.next();
				object.close();
			}
			// 遍历关闭所有子文件流
			for (Iterator iterator = WriterLists.iterator(); iterator.hasNext();) {
				FileWriter object = (FileWriter) iterator.next();
				object.close();
			}
		}
		
		public static void main(String[] args) {
			try {
				sqlitFileData("G://url_lineout.txt/", "url_lineout", ".txt", "g://url_lineout.txt/split",  20);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
}