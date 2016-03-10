package com.qiguan.grab.thread.block;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * <pre>
 * Description	生产者
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月7日 下午5:02:33  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------
 * 
 * </pre>
 */

public class GrabProducer implements Runnable {
	/**
	 * 日志对象
	 */
	private Logger logger = Logger.getLogger(GrabProducer.class);
	
	/**
	 * 文件名称
	 */
	private String fileName;
	
	/**
	 * 消息队列
	 */
	private BlockingQueue<String> queue;
	
	/**
	 * @param queue
	 */
	public GrabProducer(String fileName, BlockingQueue<String> queue) {
		this.fileName = fileName;
		this.queue = queue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		logger.info("启动生产者线程！");
		FileChannel fcin = null;
		try {
			logger.info("正在生产数据...");
			// 输入文件
			File fin = new File(fileName); 
			if (!fin.exists()) throw new FileNotFoundException("读取的文件不存在");
			Long startTime = System.currentTimeMillis();
//			int bufSize = 100; 
//			fcin = new RandomAccessFile(fin, "r").getChannel();
//			ByteBuffer rBuffer = ByteBuffer.allocate(bufSize);
//			String enterStr = "\n"; 
			try {
					BigFileRead bFileRead = new BigFileRead(fin, "UTF-8");
					String line = null;
					while (bFileRead.hasNext()) {
						line = new String(bFileRead.next());
						if (!queue.offer(line, 2, TimeUnit.SECONDS)) {
							logger.info("放入队列数据失败：" + line);
						}
					}
//				byte[] bs = new byte[bufSize];
//				StringBuilder strBuf = new StringBuilder();
//				while (fcin.read(rBuffer) != -1) {
//					int rSize = rBuffer.position();
//					rBuffer.rewind();
//					rBuffer.get(bs);
//					rBuffer.clear();
//					String tempString = new String(bs, 0, rSize);
//					int fromIndex = 0;
//					int endIndex = 0;
//					while ((endIndex = tempString.indexOf(enterStr, fromIndex)) != -1) {
//						String line = tempString.substring(fromIndex, endIndex);
//						line = new String(strBuf.toString() + line);
////						String [] lineArray = line.split("	");
////						if (null != lineArray && lineArray.length == 3) {
////							if (!queue.offer(lineArray[2], 2, TimeUnit.SECONDS)) {
////								logger.info("放入队列数据失败：" + lineArray[2]);
////							}
////						}
//						if (!queue.offer(line, 2, TimeUnit.SECONDS)) {
//							logger.info("放入队列数据失败：" + line);
//						}
//						strBuf.delete(0, strBuf.length());
//						fromIndex = endIndex + 1;
//					}
//					if (rSize > tempString.length()) {
//						strBuf.append(tempString.substring(fromIndex, tempString.length()));
//					} else {
//						strBuf.append(tempString.substring(fromIndex, rSize));
//					}
//				}
				Long endTime = System.currentTimeMillis();
				logger.info("读取" + fileName +  "总耗时：" + ((endTime - startTime) / 1000));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != fcin) {
					fcin.close();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			logger.info("退出生产者线程！");
		}
	}
}
