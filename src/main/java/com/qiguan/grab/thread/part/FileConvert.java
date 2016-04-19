package com.qiguan.grab.thread.part;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.qiguan.grab.thread.memory.ReadFile2Memory;
import com.qiguan.grab.util.ConfigUtil;
import com.qiguan.grab.util.StringUtil;
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
//		File fin = new File(inputFile);
		File fout = new File(targetFile);
//		FileChannel fcin = null;
		FileChannel fcout = null;
		try {
			// 读
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(inputFile)));
			BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10 * 1024 * 1024);// 10M缓存
			
			// 写
			ByteBuffer wBuffer = ByteBuffer.allocateDirect(bufSize);
			fcout = new RandomAccessFile(fout, "rws").getChannel();
			largeFileIO(bis, in, fcout, wBuffer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			try {
//				fcin.close();
				fcout.close();
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
	 * @throws Exception 
	 */
	public void largeFileIO(BufferedInputStream bis, BufferedReader in, FileChannel fcout, ByteBuffer wBuffer) throws Exception {
		try {
			String line = null;
			String [] lineArray = null;
			String result = null;
			String domain = null;
			while (in.ready()) {
				line = in.readLine();
				domain = StringUtil.getDomain(line);
				// 抓取内容
				lineArray = line.split("	");
				if (null != lineArray && lineArray.length == 3 && null != lineArray[2]) {
					// 包含敏感词过滤
					if (checkSensitiveWord.isContaintSensitiveWord(domain)) {
						continue;
					}
					logger.info(line);
					// 取得一个任务，并从任务List中删除该任务
					result = grabTitle(line, domain);
					if (null != result) {
						logger.info(result);
						line = line.replace(lineArray[2], result);
						writeFileByLine(fcout, wBuffer, line);
					}  else {
						checkSensitiveWord.add(domain);
					}
				}
			}
			bis.close();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 整个文件不是全部存放在内存中
	 * 
	 * @param inputFile
	 * @param fcout
	 * @param wBuffer
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public void readFileByMemory(String inputFile, String outFile) throws Exception {
		List<String> it = ReadFile2Memory.read(ConfigUtil.getValue("log.source.path"));
		// 写
		ByteBuffer wBuffer = ByteBuffer.allocateDirect(1024);
		FileChannel fcout = new RandomAccessFile(new File(outFile), "rws").getChannel();
		try {
			String line = null;
			String [] lineArray = null;
			String result = null;
			String domain = null;
			while (true) {
				if (it.isEmpty()) {
					logger.info(inputFile + "文件处理结束");
					break;
				}
				line = it.remove(0);
				// 抓取内容
				lineArray = line.split("	");
				if (null != lineArray && lineArray.length == 3 && null != lineArray[2]) {
					domain = StringUtil.getDomain(lineArray[2]);
					// 包含敏感词过滤
					if (checkSensitiveWord.isContaintSensitiveWord(domain)) {
						continue;
					}
					// 取得一个任务，并从任务List中删除该任务
					result = grabTitle(lineArray[2], domain);
					if (null != result) {
						logger.info(result);
						line = line.replace(lineArray[2], result);
						writeFileByLine(fcout, wBuffer, line);
					}  else {
						checkSensitiveWord.add(domain);
					}
				}
			}
		} catch(Exception ex) {
			logger.info("异常：" + ex.getMessage());
		} finally {
			fcout.close();
		}
	}
	
	/**
	 * 整个文件不是全部存放在内存中
	 * 
	 * @param inputFile
	 * @param fcout
	 * @param wBuffer
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public void readFileByLineIterator(String inputFile, String outFile) throws Exception {
		LineIterator it = FileUtils.lineIterator(new File(inputFile), "UTF-8");
		// 写
		ByteBuffer wBuffer = ByteBuffer.allocateDirect(1024);
		FileChannel fcout = new RandomAccessFile(new File(outFile), "rws").getChannel();
		try {
			String line = null;
			String [] lineArray = null;
			String result = null;
			String domain = null;
			while (it.hasNext()) {
				line = it.nextLine();
				// 抓取内容
				lineArray = line.split("	");
				if (null != lineArray && lineArray.length == 3 && null != lineArray[2]) {
					domain = StringUtil.getDomain(lineArray[2]);
					// 包含敏感词过滤
					if (checkSensitiveWord.isContaintSensitiveWord(domain)) {
						continue;
					}
					// 取得一个任务，并从任务List中删除该任务
					result = grabTitle(lineArray[2], domain);
					if (null != result) {
						logger.info(result);
						line = line.replace(lineArray[2], result);
						writeFileByLine(fcout, wBuffer, line);
					}  else {
						checkSensitiveWord.add(domain);
					}
				}
			}
		} catch(Exception ex) {
			logger.info("异常：" + ex.getMessage());
		} finally {
			fcout.close();
			LineIterator.closeQuietly(it);
		}
	}
	
	/**
	 * 抓取网站url的title和keywords
	 * 
	 * @param url
	 * @return
	 * @throws Exception 
	 */
	private String grabTitle(String url, String domain) throws Exception {
		Document doc = null;
		Connection con = null;
		try {
			con = Jsoup.connect(url);
			con.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0");
			doc = con.get();
		} catch (Exception e) {
//			logger.info("请求异常：" + e.getMessage());
			return null;
		}
		if (null == doc) return null;
		String title  = doc.title();
		String keywords = null;
		Element el = doc.getElementsByAttributeValue("name", "keywords").first();
		if (null != el) {
			keywords = el.attr("content");
		} 
		if (StringUtil.isNullOrEmpty(title) && StringUtil.isNullOrEmpty(keywords)) {
			return null;
		}
		
		if (StringUtil.isNullOrEmpty(title)) {
			title = "";
		}
		if (StringUtil.isNullOrEmpty(keywords)) {
			keywords = "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(title).append(" ").append(keywords);
		
		// 销毁对象
		con = null;
		doc = null;
		System.runFinalization();
		System.gc();
		return sb.toString();
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
								writeFileByLine(fcout, wBuffer, lineArray[2]);
								logger.info("可用url：" + lineArray[2]);
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
		try {
			new FileConvert().readFileByLineIterator(ConfigUtil.getValue("log.source.path"), ConfigUtil.getValue("log.new.path"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
