package com.qiguan.grab.http;


/**  
 * <pre>
 * Description	自定义异步回调接口
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年4月19日 下午4:55:03  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */  
public interface AsyncHttpCallback {
	
	/**
	 * 响应完成回调
	 * 
	 * @param tag
	 */
	public void complete(ResponseTag tag);
}
