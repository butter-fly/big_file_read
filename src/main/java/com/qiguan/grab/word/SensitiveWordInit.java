package com.qiguan.grab.word;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.qiguan.grab.util.ConfigUtil;

/**  
 * <pre>
 * Description	初始化敏感词库，将敏感词加入到HashMap
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月9日 下午5:41:13  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */  
public class SensitiveWordInit {
	
	/**
	 * 敏感词库
	 */
	public Set<String> keyWordSet;
	
	/**
	 * 
	 */
	public SensitiveWordInit(){
		super();
	}
	
	/**
	 * @author chenming 
	 * @date 2014年4月20日 下午2:28:32
	 * @version 1.0
	 */
	public Set<String> initKeyWord(){
		try {
			//读取敏感词库
			keyWordSet = readSensitiveWordFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keyWordSet;
	}
	 
	/**
	 * 读取敏感词库中的内容，将内容添加到set集合中
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	private Set<String> readSensitiveWordFile() throws Exception{
		Set<String> set = null;
		File file = new File(ConfigUtil.getValue("sensitive.word.path"));    //读取文件
		InputStreamReader read = new InputStreamReader(new FileInputStream(file));
		try {
			// 文件流是否存在
			if(file.isFile() && file.exists()){
				set = new HashSet<String>();
				BufferedReader bufferedReader = new BufferedReader(read);
				String txt = null;
				while((txt = bufferedReader.readLine()) != null){    //读取文件，将文件内容放入到set中
					if ("" != txt)
					set.add(txt.trim());
			    }
			}
			else{         //不存在抛出异常信息
				throw new Exception("敏感词库文件不存在");
			}
		} catch (Exception e) {
			throw e;
		}finally{
			read.close();     //关闭文件流
		}
		return set;
	}
}
