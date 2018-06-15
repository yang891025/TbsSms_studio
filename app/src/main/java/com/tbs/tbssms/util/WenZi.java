/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * wenzi.java
 * @version 2014-11-24
 * @time 下午1:41:54
 * @function 
 */
package com.tbs.tbssms.util;

public class WenZi {

	/***************************************
	 * 
	 * 全角转换成半角
	 * 
	 * @param input
	 *            原始字符串
	 * @return 转换后的字符串
	 * 
	 ***************************************/
	public String QtoB(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}

	// 半角转全角
	public String BtoQ(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}
}
