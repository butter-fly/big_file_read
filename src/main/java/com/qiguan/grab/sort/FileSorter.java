package com.qiguan.grab.sort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FileSorter {
	
	public int sort(String fileName, LineProcessor lineProcessor) throws InterruptedException, IOException {
		int index = split(fileName, lineProcessor);
		int totalCount = merge(fileName, index, lineProcessor);
		return totalCount;
	}
	
	public int split(String fileName, LineProcessor lineProcessor) throws InterruptedException, IOException {
		int fileIndex = 0;
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor pool = new ThreadPoolExecutor(SysConfig.THREAD_NUMBER, SysConfig.THREAD_NUMBER, 600, TimeUnit.SECONDS, workQueue);
		BufferedReader br = null;
		int row = 0;
		String sLine = null;
		String sKey = null;
		List<SortedData> sList = new ArrayList<SortedData>();
		//LineProcessor lineProcessor = new CSVLineProcessor(SysConfig.KEY_INDEX);
		br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		while((sLine=br.readLine())!=null) {
			sKey = lineProcessor.process(sLine);
			sList.add(new SortedData(sKey, sLine, br));
			row++;
			
			if(row!=0 && row%SysConfig.BATCH_ROW_COUNT==0) {
				new FileSplitController(pool, workQueue, sList, fileName, fileIndex).dispatchTask();
				sList = new ArrayList<SortedData>();
				fileIndex++;
			}
		}
		/**
		 * check whether there is still records to be processed
		 */
		if(sList.size()>0) {
			new FileSplitController(pool, workQueue, sList, fileName, fileIndex).dispatchTask();
			fileIndex++;
		}
		while(workQueue.size()>1) {
			Thread.sleep(5000);
		}
		pool.shutdown();
		/**
		 * if all task still not finish, sleep 5 seconds and check again
		 */
		while(!pool.isTerminated()) {
			pool.awaitTermination(5, TimeUnit.SECONDS);
		}
		return fileIndex;
	}
	
	public int merge(String fileName, int index, LineProcessor lineProcessor) throws IOException {
		int totalCount = 0;
		BufferedWriter bw = null;
		BufferedReader[] fileReaders = new BufferedReader[index];
		List<SortedData> sortedDatas = new ArrayList<SortedData>(index);
		File[] tempFiles = new File[index];
		String iFilePath = null;
		String outputFile = null;
		String sLine = null;
		String sKey = null;
		for(int i=0; i<index; i++) {
			iFilePath = fileName + ".tmp" + i;
			tempFiles[i] = new File(iFilePath);
			fileReaders[i] = new BufferedReader(new InputStreamReader(new FileInputStream(iFilePath)));
			sLine = fileReaders[i].readLine();
			sKey = lineProcessor.process(sLine);
			sortedDatas.add(new SortedData(sKey, sLine, fileReaders[i]));
		}
		
		outputFile = fileName + ".sorted";
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
		
		SortedData smallestData = null;
		String smallestKey = null;
		String smallestContent = null;
		String tempKey = null;
		String tempContent = null;
		BufferedReader tempReader = null;
		while(true) {
			Collections.sort(sortedDatas);
			smallestData = sortedDatas.get(0);
			smallestKey = smallestData.getKey();
			if(smallestKey==null || "".equals(smallestKey)) {
				break;
			}
			smallestContent = smallestData.getContent();
			tempReader = smallestData.getFileReader();
			bw.write(smallestContent + "\n");
			totalCount++;
			tempContent = tempReader.readLine();
			tempKey = lineProcessor.process(tempContent);
			sortedDatas.set(0, new SortedData(tempKey, tempContent, tempReader));
		}
		
		bw.flush();
		bw.close();
		
		for(int i=0; i<index; i++) {
			fileReaders[i].close();
			tempFiles[i].delete();
		}
		System.out.println(totalCount);
		return totalCount;
	}
}