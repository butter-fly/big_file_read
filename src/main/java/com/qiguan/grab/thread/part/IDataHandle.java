package com.qiguan.grab.thread.part;

/**  
 * <pre>
 * Description	数据返回处理接口
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月7日 下午2:59:49  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */  
public interface IDataHandle {

	/**
	 * 处理接口
	 * 
	 * @param line
	 */
	public void handle(String line);
}
