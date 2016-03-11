package com.qiguan.grab.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

/**  
 * <pre>
 * Description	java读取大文件
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月7日 上午11:38:41  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class FileUtil {
	
	/**
	 * 日志对象
	 */
	private static final Logger logger = Logger.getLogger(FileUtil.class);
	
	/**
	 * 读取大文件数据并写入到指定文件
	 * 
	 * @param source
	 * @param target
	 * @throws Exception 
	 */
	public static void read(String source, String traget) throws Exception {
		// 输入文件
		File fin = new File(source); 
		if (!fin.exists()) throw new Exception("读取的文件不存在");
		Long startTime = System.currentTimeMillis();
		int bufSize = 100; 
		FileChannel fcin = new RandomAccessFile(fin, "r").getChannel();
		ByteBuffer rBuffer = ByteBuffer.allocate(bufSize);
		String enterStr = "\n"; 
		try {
			byte[] bs = new byte[bufSize];
			StringBuilder strBuf = new StringBuilder();
			while (fcin.read(rBuffer) != -1) {
				int rSize = rBuffer.position();
				rBuffer.rewind();
				rBuffer.get(bs);
				rBuffer.clear();
				String tempString = new String(bs, 0, rSize);
				int fromIndex = 0;
				int endIndex = 0;
				while ((endIndex = tempString.indexOf(enterStr, fromIndex)) != -1) {
					String line = tempString.substring(fromIndex, endIndex);
					line = new String(strBuf.toString() + line);
					String []lineArray = line.split("	");
					if (null != lineArray && lineArray.length == 3) {
						logger.info(lineArray[2]);
						append2File(traget, lineArray[2] + "\r\n");
					}
					System.out.println();
					strBuf.delete(0, strBuf.length());
					fromIndex = endIndex + 1;
				}
				if (rSize > tempString.length()) {
					strBuf.append(tempString.substring(fromIndex, tempString.length()));
				} else {
					strBuf.append(tempString.substring(fromIndex, rSize));
				}
			}
			Long endTime = System.currentTimeMillis();
			logger.info("读取总耗时：" + ((endTime - startTime) / 1000));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fcin) {
				fcin.close();
			}
		}
	}
	
	/**
	 * B方法追加文件：使用FileWriter
	 */
	public static synchronized void append2File(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * NIO方式写入文件
	 * 
	 * @param content
	 * @param inputFile
	 * @throws IOException
	 */
	public static void writeByNio(String content, String inputFile) throws IOException {
		FileOutputStream fos = null;
		FileChannel fc_out = null;
		try {
			fos = new FileOutputStream(inputFile, true);
			fc_out = fos.getChannel();
			ByteBuffer buf = ByteBuffer.wrap(content.getBytes());
			buf.put(content.getBytes());
			buf.flip();
			fc_out.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fc_out) {
				fc_out.close();
			}
			if (null != fos) {
				fos.close();
			}
		}
	}
	
	/**
	 * NIO的方式读取文件
	 * 
	 * @param inputFile
	 */
	public static void readByNio(String inputFile) {
		// 第一步 获取通道
		FileInputStream fis = null;
		FileChannel channel = null;
		try {
			fis = new FileInputStream(inputFile);
			channel = fis.getChannel();
			// 文件内容的大小
			int size = (int) channel.size();
			// 第二步 指定缓冲区
			ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
			// 第三步 将通道中的数据读取到缓冲区中
			channel.read(buffer);
			byte[] bt = buffer.array();
			System.out.println(new String(bt, 0, size));
			buffer.clear();
			buffer = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				channel.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param filePath
	 */
	public static void readFile(String filePath) {
		byte[] bytes = new byte[1024];
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		FileChannel channel = null;
		try { 
			// 得到一个通道
			channel = new RandomAccessFile(filePath, "r").getChannel();
			while (channel.read(byteBuffer) != -1) {
				int size = byteBuffer.position();
				byteBuffer.rewind();
				byteBuffer.get(bytes);
				String tmp = new String(bytes, 0, size);
				byteBuffer.clear();
				logger.info(tmp);
			}
			channel.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	/**
	 * 读取大文件并输出到指定文件
	 * 
	 * @param inputFile
	 * @param outputFile
	 */
	public static void largeFileIO(String inputFile, String outputFile) {
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(inputFile)));
			BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10 * 1024 * 1024);// 10M缓存
			FileWriter fw = new FileWriter(outputFile);
			while (in.ready()) {
				String line = in.readLine();
				fw.append(line + " ");
			}
			in.close();
			fw.flush();
			fw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 统计文件行数
	 * 
	 * @param inputFile
	 * @return
	 */
	public static int getFileLines(String inputFile) {
		File test = new File(inputFile);
		long fileLength = test.length();
		LineNumberReader rf = null;
		int lines = 0;
		try {
			rf = new LineNumberReader(new FileReader(test));
			if (rf != null) {
				rf.skip(fileLength);
				lines = rf.getLineNumber();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (rf != null) {
				try {
					rf.close();
				} catch (IOException ee) {
				}
			}
		}
		return lines;
	}
	
	public static void main(String[] args) {
		try {
//			read("D://grab/file/url_lineout.txt", "D://grab/file/grab_urls.txt");
//			read("G://url_lineout.txt/url_lineout.txt", "F://url_lineout.txt/url_lineout_clean.txt");
			System.out.println(getFileLines("g://url_lineout.txt/url_lineout_new.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
