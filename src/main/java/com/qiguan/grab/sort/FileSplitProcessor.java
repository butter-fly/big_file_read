package com.qiguan.grab.sort;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.List;

public class FileSplitProcessor implements Runnable{
	
	private List<SortedData> sList;
	private String fileName;
	private int fileIndex;

	public FileSplitProcessor(List<SortedData> sList, String fileName,
			int fileIndex) {
		super();
		this.sList = sList;
		this.fileName = fileName;
		this.fileIndex = fileIndex;
	}

	public void run() {
		BufferedWriter bw = null;
		String outputFile = fileName + ".tmp" + fileIndex;
		Collections.sort(sList);
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
			for(SortedData sData : sList) {
				bw.write(sData.getContent() + "\n");
			}
			bw.flush();
			bw.close();
			sList.clear();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
