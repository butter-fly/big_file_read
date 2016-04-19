package com.qiguan.grab.sort;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {
	
	BlockingQueue<Runnable> workQueue;
	ThreadPoolExecutor pool;
	
	public ThreadPoolTest() {
		this.workQueue = new LinkedBlockingQueue<Runnable>();
		this.pool = new ThreadPoolExecutor(5, 5, 600, TimeUnit.SECONDS, workQueue);
	}

	public static void main(String[] args) throws InterruptedException {
		new ThreadPoolTest().process();
	}
	
	public void process() throws InterruptedException {
		for(int i=0; i<100; i++) {
			System.out.println("start putting task " + (i+1));
			dispatchTask(i);
		}
        /**
    	 * wait for all task to be completed
    	 */
		while(workQueue.size()>1) {
			System.out.println("there is still some task unfinish,sleep 5 seconds and check again");
			Thread.sleep(5000);
		}
		System.out.println("finish all task");
		pool.shutdown();
	}
	
	public void dispatchTask(int index) throws InterruptedException {
		/**
		 * most task in the waiting queue is 10
		 * if more than 10 then sleep 5 seconds
		 * and check the work queue size again
		 */
		while(workQueue.size() > 20) {
			Thread.sleep(5000);
		}
		pool.submit(new MyTask(index));
	}
}

class MyTask implements Runnable {
	private int i;

	public MyTask(int i) {
		this.i = i;
	}

	public void run() {
		System.out.println("Task " + (i+1) + " is processing!");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}