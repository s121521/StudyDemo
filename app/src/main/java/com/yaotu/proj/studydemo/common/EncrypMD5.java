package com.yaotu.proj.studydemo.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncrypMD5 {
	public static String eccrypt(String s_info) {
		
		//根据MD5算法生成MessageDigest对象  
		String newstr="";
		try {
			// 确定计算方法
	        MessageDigest md5 = MessageDigest.getInstance("MD5");

	        // 加密后的字符串
	        // 说明：MD5加密后的字节数组，再使用base64对其进行编码
	        //new String(Base64Str.decode(this.getUsername()),"utf-8")
			newstr =new String(md5.digest(s_info.getBytes("UTF-8")));//new String(Base64Str.decode(new String(md5.digest(s_info.getBytes("utf-8")))));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
        return newstr;  
	}
}
