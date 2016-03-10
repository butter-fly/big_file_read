package com.qiguan.grab.word;

import java.util.Set;

/**  
 * <pre>
 * Description
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月7日 下午2:59:54  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */ 
public class CheckSensitiveWord {
	
	/**
	 * 词库
	 */
	private Set<String> keyWordSet = null;
	
	/**
	 * 构造函数，初始化敏感词库
	 */
	public CheckSensitiveWord(){
		keyWordSet = new SensitiveWordInit().initKeyWord();
	}
	
	/**
	 * 判断文字是否包含敏感字符
	 * @param txt  文字
	 * @return 若包含返回true，否则返回false
	 * @version 1.0
	 */
	public boolean isContaintSensitiveWord(String txt){
		boolean flag = false;
		for (String str : keyWordSet) {
			if (txt.indexOf(str.trim()) != -1) {
				return true;
			}
		}
		return flag;
	}
	
	
	/**
	 * @return the {@link #keyWordSet}
	 */
	public Set<String> getKeyWordSet() {
		return keyWordSet;
	}

	public static void main(String[] args) {
		CheckSensitiveWord filter = new CheckSensitiveWord();
		System.out.println("敏感词的数量：" + filter.keyWordSet.size());
		String string = "http://www.lequ.com/shop/api/action/point/t=1456761666559";
		long beginTime = System.currentTimeMillis();
		boolean set = filter.isContaintSensitiveWord(string);
		long endTime = System.currentTimeMillis();
		System.out.println("语句中包含敏感词的个数为：" + set + "。包含：" + set);
		System.out.println("总共消耗时间为：" + (endTime - beginTime));
	}
}
