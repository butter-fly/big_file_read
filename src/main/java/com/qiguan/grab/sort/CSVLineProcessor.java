package com.qiguan.grab.sort;

public class CSVLineProcessor implements LineProcessor {
	
	private int keyIndex;
	
	public CSVLineProcessor(int keyIndex) {
		this.keyIndex = keyIndex;
	}

	public String process(String source) {
		String result = null;
		if(source==null||"".equals(source)) {
			result = "";
		} else {
			result = source.split(",")[keyIndex];
		}
		return result;
	}
}
