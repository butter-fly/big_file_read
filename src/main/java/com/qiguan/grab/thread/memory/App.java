package com.qiguan.grab.thread.memory;

import java.util.List;

import com.qiguan.grab.util.ConfigUtil;

/**  
 * <pre>
 * Description	数据一次加载到内存中
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月9日 下午1:13:40  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class App {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> lineList = ReadFile2Memory.read(ConfigUtil.getValue("log.source.path"));
		
		new DealTask().start(lineList);
	}
}
