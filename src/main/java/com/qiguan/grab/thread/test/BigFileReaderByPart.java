package com.qiguan.grab.thread.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import com.qiguan.grab.thread.part.IDataHandle;
import com.sun.istack.internal.logging.Logger;

/**  
 * <pre>
 * Description	多线程分片读取文件
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月7日 下午2:58:40  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */  
public class BigFileReaderByPart {
	/****************** 初始化  *********************/
	private Logger logger = Logger.getLogger(BigFileReaderByPart.class); // 日志对象
	private int threadSize; // 线程大小
	private String charset; // 编码
	private int bufferSize; // 缓存字节大小 
	private IDataHandle handle; // 数据返回定义接口
	private ExecutorService  executorService; // 线程池
	private long fileLength; // 文件长度
	private RandomAccessFile rAccessFile; // 文件读写对象
	private Set<StartEndPair> startEndPairs; // 分片
	private CyclicBarrier cyclicBarrier;
	private AtomicLong counter = new AtomicLong(0); // 计数
	private BlockingQueue<String> queue; // 队列
	/**
	 * @param file
	 * @param handle
	 * @param charset
	 * @param bufferSize
	 * @param threadSize
	 */
	private BigFileReaderByPart(File file,IDataHandle handle, BlockingQueue<String> queue, String charset,int bufferSize,int threadSize){
		this.fileLength = file.length();
		this.handle = handle;
		this.charset = charset;
		this.bufferSize = bufferSize;
		this.threadSize = threadSize;
		try {
			this.rAccessFile = new RandomAccessFile(file,"rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.executorService = Executors.newFixedThreadPool(threadSize);
		startEndPairs = new HashSet<BigFileReaderByPart.StartEndPair>();
		this.queue = queue;
	}
	
	/**
	 * 开启任务
	 */
	public void start(){
		long everySize = this.fileLength / this.threadSize;
		try {
			calculateStartEnd(0, everySize);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		final long startTime = System.currentTimeMillis();
		cyclicBarrier = new CyclicBarrier(startEndPairs.size(), new Runnable() {
			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				logger.info("use time: " + (System.currentTimeMillis() - startTime));
				logger.info("all line: " + counter.get());
			}
		});
		for (StartEndPair pair : startEndPairs) {
			logger.info("分配分片：" + pair);
			this.executorService.execute(new SliceReaderTask(pair));
		}
	}
	
	/**
	 * 计算指针读取开始和结尾即分片
	 * 
	 * @param start
	 * @param size
	 * @throws IOException
	 */
	private void calculateStartEnd(long start, long size) throws IOException {
		if (start > fileLength - 1) {
			return;
		}
		StartEndPair pair = new StartEndPair();
		pair.start = start;
		long endPosition = start + size - 1;
		if (endPosition >= fileLength - 1) {
			pair.end = fileLength - 1;
			startEndPairs.add(pair);
			return;
		}
		// 移动指针读取
		rAccessFile.seek(endPosition);
		byte tmp = (byte) rAccessFile.read();
		while (tmp != '\n' && tmp != '\r') {
			endPosition++;
			if (endPosition >= fileLength - 1) {
				endPosition = fileLength - 1;
				break;
			}
			rAccessFile.seek(endPosition);
			tmp = (byte) rAccessFile.read();
		}
		pair.end = endPosition;
		startEndPairs.add(pair);
		
		// 迭代计算
		calculateStartEnd(endPosition + 1, size);
	}
	
	/**
	 * 关闭
	 */
	public void shutdown(){
		try {
			this.rAccessFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.executorService.shutdown();
	}
	
	/**
	 * @param bytes
	 * @throws UnsupportedEncodingException
	 */
	private void handle(byte[] bytes) throws UnsupportedEncodingException{
		String line = null;
		if (this.charset == null) {
			line = new String(bytes);
		} else {
			line = new String(bytes, charset);
		}
		if (line != null && !"".equals(line)) {
			this.handle.handle(line);
			if (null != line) {
				// 抓取内容
				String [] lineArray = line.split("	");
				if (null != lineArray && lineArray.length == 3 && null != lineArray[2]) {
					// 加入队列
					queue.add(lineArray[2]);
				}
			}
			logger.info("第" + counter.get() + "行 : " + line);
			counter.incrementAndGet(); // 递增 保证数据计数同步
		}
	}
	
	/**  
	 * <pre>
	 * Description	分片数据对象
	 * Copyright:	Copyright (c)2016
	 * Company:		杭州启冠网络技术有限公司
	 * Author:		Administrator
	 * Version: 	1.0
	 * Create at:	2016年3月7日 下午3:00:54  
	 *  
	 * Modification History:  
	 * Date         Author      Version     Description 
	 * ------------------------------------------------------------------  
	 * 
	 * </pre>
	 */  
	private static class StartEndPair{
		public long start;
		public long end;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "star=" + start + ";end=" + end;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (end ^ (end >>> 32));
			result = prime * result + (int) (start ^ (start >>> 32));
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StartEndPair other = (StartEndPair) obj;
			if (end != other.end)
				return false;
			if (start != other.start)
				return false;
			return true;
		}
		
	}
	
	/**  
	 * <pre>
	 * Description	分片任务处理线程任务
	 * Copyright:	Copyright (c)2016
	 * Company:		杭州启冠网络技术有限公司
	 * Author:		Administrator
	 * Version: 	1.0
	 * Create at:	2016年3月7日 下午2:59:39  
	 *  
	 * Modification History:  
	 * Date         Author      Version     Description 
	 * ------------------------------------------------------------------  
	 * 
	 * </pre>
	 */  
	private class SliceReaderTask implements Runnable{
		private long start;
		private long sliceSize;
		private byte[] readBuff;
		/**
		 * @param start 	read position (include)
		 * @param end 	the position read to(include)
		 */
		public SliceReaderTask(StartEndPair pair) {
			this.start = pair.start;
			this.sliceSize = pair.end-pair.start+1;
			this.readBuff = new byte[bufferSize];
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				MappedByteBuffer mapBuffer = rAccessFile.getChannel().map(MapMode.READ_ONLY, start, this.sliceSize);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				for (int offset = 0; offset < sliceSize; offset += bufferSize) {
					int readLength;
					if (offset + bufferSize <= sliceSize) {
						readLength = bufferSize;
					} else {
						readLength = (int) (sliceSize - offset);
					}
					mapBuffer.get(readBuff, 0, readLength);
					for (int i = 0; i < readLength; i++) {
						byte tmp = readBuff[i];
						if (tmp == '\n' || tmp == '\r') {
							handle(bos.toByteArray());
							bos.reset();
						} else {
							bos.write(tmp);
						}
					}
				}
				
				if (bos.size() > 0) {
					handle(bos.toByteArray());
				}
				cyclicBarrier.await();// 等待其他线程操作完毕
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**  
	 * <pre>
	 * Description
	 * Copyright:	Copyright (c)2016
	 * Company:		杭州启冠网络技术有限公司
	 * Author:		Administrator
	 * Version: 	1.0
	 * Create at:	2016年3月7日 下午3:00:45  
	 *  
	 * Modification History:  
	 * Date         Author      Version     Description 
	 * ------------------------------------------------------------------  
	 * 
	 * </pre>
	 */  
	public static class Builder{
		private int threadSize = 1;
		private String charset = null;
		private int bufferSize = 1024 * 1024;
		private IDataHandle handle;
		private File file;
		private BlockingQueue<String> queue;
		public Builder(String file, IDataHandle handle, BlockingQueue<String> queue){
			this.file = new File(file);
			if(!this.file.exists())
				throw new IllegalArgumentException("文件不存在！");
			this.handle = handle;
			this.queue = queue;
		}
		
		public Builder withTreahdSize(int size){
			this.threadSize = size;
			return this;
		}
		
		public Builder withCharset(String charset){
			this.charset= charset;
			return this;
		}
		
		public Builder withBufferSize(int bufferSize){
			this.bufferSize = bufferSize;
			return this;
		}
		
		public BigFileReaderByPart build(){
			return new BigFileReaderByPart(this.file,this.handle,this.queue, this.charset,this.bufferSize,this.threadSize);
		}
	}
}
