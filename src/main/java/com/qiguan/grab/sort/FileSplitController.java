package com.qiguan.grab.sort;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;


public class FileSplitController {
	private ThreadPoolExecutor pool;
	private BlockingQueue<Runnable> workQueue;
	private List<SortedData> sList;
	private String fileName;
	private int fileIndex;
	
	public FileSplitController(ThreadPoolExecutor pool,
			BlockingQueue<Runnable> workQueue, List<SortedData> sList,
			String fileName, int fileIndex) {
		super();
		this.pool = pool;
		this.workQueue = workQueue;
		this.sList = sList;
		this.fileName = fileName;
		this.fileIndex = fileIndex;
	}

	public void dispatchTask() throws InterruptedException {
		//keeping having task in the queue waiting to process, but not putting to much task to avoid out of memory or full gc
		int maxTaskCount = SysConfig.THREAD_NUMBER;
		/**
		 * most task in the waiting queue is 10
		 * if more than 10 then sleep 5 seconds
		 * and check the work queue size again
		 */
		while(workQueue.size() > maxTaskCount) {
			Thread.sleep(5000);
		}
		pool.submit(new FileSplitProcessor(sList,fileName,fileIndex));
	}
}
