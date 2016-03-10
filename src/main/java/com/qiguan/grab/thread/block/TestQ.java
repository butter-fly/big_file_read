package com.qiguan.grab.thread.block;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

import com.qiguan.grab.util.ConfigUtil;



public class TestQ {
	/**
	 * 当前已生成记录
	 */
	public AtomicLong currentSynCount = new AtomicLong(0);
	
	/**
	 * @param localPath
	 * @return
	 */
	private boolean readFileFromFile(String localPath) {
		boolean flag = true;
		try {
			File fin = new File(localPath);
			BigFileRead bFileRead = new BigFileRead(fin, "UTF-8");
			String line = "";
			while (bFileRead.hasNext()) {
				line = new String(bFileRead.next());
				currentSynCount.incrementAndGet();
				System.out.println(currentSynCount.get() + " : " + line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {

		}
		return flag;
	}
	
	public static void main(String[] args) {
		TestQ aimport = new TestQ();
		aimport.readFileFromFile(ConfigUtil.getValue("log.source.path"));
	}
}
