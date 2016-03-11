package com.qiguan.grab.demo;

/**  
 * <pre>
 * Description
 * Copyright:	Copyright (c)2016
 * Company:		杭州启冠网络技术有限公司
 * Author:		Administrator
 * Version: 	1.0
 * Create at:	2016年3月10日 下午2:11:56  
 *  
 * Modification History:  
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------  
 * 
 * </pre>
 */

public class BaseQuestion {
	
	/**
	 * 测试==
	 */
	public static void test1() {
		String a = "a" + "b" + "1";
		String b = "ab1";
		System.out.println(a == b);
		System.out.println(a.equals(b));
	}
	
	/**
	 * 测试==
	 */
	public static void test2() {
		String a = "a";
		final String a1 = "a";
		String c = a + "b";
		String d =  a1 + "b";
		String corprate = "ab";
		System.out.println(c == d);
		System.out.println(c == corprate);
		System.out.println(d == corprate);
		System.out.println(d.equals(corprate));
	}
	
	public static void test3() {
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test1();
		test2();
	}

}
