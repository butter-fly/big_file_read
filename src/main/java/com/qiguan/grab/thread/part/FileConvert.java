package com.qiguan.grab.thread.part;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import com.qiguan.grab.util.ConfigUtil;
import com.qiguan.grab.word.CheckSensitiveWord;

/**  
 * <pre>
 * Description	文件数据读取并转化过滤
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月10日 下午5:21:23  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class FileConvert {
	
	/**
	 * 日志对象
	 */
	private final Logger logger = Logger.getLogger(FileConvert.class);
	
	/**
	 * 敏感词库处理
	 */
	private CheckSensitiveWord checkSensitiveWord;
	
	/**
	 * 构造
	 */
	public FileConvert() {
		super();
		checkSensitiveWord = new CheckSensitiveWord();
	}
	
	/**
	 * 执行入口
	 */
	@SuppressWarnings("resource")
	public void execute(String inputFile, String targetFile) {
		int bufSize = 1024;
		File fin = new File(inputFile);
		File fout = new File(targetFile);
		FileChannel fcin = null;
		FileChannel fcout = null;
		try {
			// 读
			ByteBuffer rBuffer = ByteBuffer.allocate(bufSize);
			fcin = new RandomAccessFile(fin, "r").getChannel();
			
			// 写
			ByteBuffer wBuffer = ByteBuffer.allocateDirect(bufSize);
			fcout = new RandomAccessFile(fout, "rws").getChannel();
			readFileByLine(bufSize, fcin, rBuffer, fcout, wBuffer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fcin.close();
				fcout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* 读文件同时写文件 */
	public void readFileByLine(int bufSize, FileChannel fcin, ByteBuffer rBuffer, FileChannel fcout, ByteBuffer wBuffer) {
		String enterStr = "\n";
		try {
			byte[] bs = new byte[bufSize];
			StringBuffer strBuf = new StringBuffer();
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
					// 抓取内容
					String [] lineArray = line.split("	");
					if (null != lineArray && lineArray.length == 3 && null != lineArray[2]) {
						// 敏感词过滤
						if (!checkSensitiveWord.isContaintSensitiveWord(lineArray[2])) {
							// 检查链接是否可用
//							if (HttpUrlAvailability.isConnect(lineArray[2])) {
//								// write to anthone file
								writeFileByLine(fcout, wBuffer, lineArray[2]);
								logger.info("可用url：" + lineArray[2]);
//							} else {
//								logger.info("不可用url：" + lineArray[2]);
//								// 添加到敏感词库
//								checkSensitiveWord.add(StringUtil.getDomain(lineArray[2]));
//							}
						}
					}
					strBuf.delete(0, strBuf.length());
					fromIndex = endIndex + 1;
				}
				if (rSize > tempString.length()) {
					strBuf.append(tempString.substring(fromIndex, tempString.length()));
				} else {
					strBuf.append(tempString.substring(fromIndex, rSize));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 写文件 */
	@SuppressWarnings("static-access")
	public void writeFileByLine(FileChannel fcout, ByteBuffer wBuffer, String line) {
		try {
			// wirte append file on foot
			fcout.write(wBuffer.wrap((line + "\n").getBytes()), fcout.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new FileConvert().execute(ConfigUtil.getValue("log.source.path"), ConfigUtil.getValue("log.new.path"));
	}
}
