package com.qiguan.grab.sort;

import java.io.BufferedReader;

public class SortedData implements Comparable<SortedData> {
	/**
	 * compare key
	 */
	private String key;
	/**
	 * compare line
	 */
	private String content;
	/**
	 * the file from which reads the compare line
	 */
	private BufferedReader fileReader;

	public SortedData(String key, String content, BufferedReader fileReader) {
		super();
		this.key = key;
		this.content = content;
		this.fileReader = fileReader;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public BufferedReader getFileReader() {
		return fileReader;
	}

	public void setFileReader(BufferedReader fileReader) {
		this.fileReader = fileReader;
	}

	/**
	 * implement the comparable interface
	 * write the compare logic here
	 */
	public int compareTo(SortedData s) {
		String sKey = s.getKey();
		String tKey = this.getKey();
		
		if(tKey==null || "".equals(tKey)) {
			return 1;
		} else if(sKey==null || "".equals(sKey)) {
			return -1;
		} else {
			return tKey.compareToIgnoreCase(sKey);
		}
	}
}
