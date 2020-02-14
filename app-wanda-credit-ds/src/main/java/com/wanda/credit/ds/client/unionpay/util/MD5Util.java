package com.wanda.credit.ds.client.unionpay.util;

import org.apache.commons.codec.digest.DigestUtils;

import com.wanda.credit.base.util.MD5;

public class MD5Util {

    public static String encrypt(String data) {
        return DigestUtils.md5Hex(data);
    }
    
    
    public static void main(String[] args) {
        String data = "123456789";
        String unpayMD5Data = encrypt(data);
        System.out.println(unpayMD5Data);// 25F9E794323B453885F5181F1B624D0B
        String wandaMD5Data = MD5.ecodeByMD5(data);
        System.out.println(wandaMD5Data);
        if (wandaMD5Data.equals(unpayMD5Data)) {
			System.out.println("皆大欢喜");
		}
        data = "25F9E794323B453885F5181F1B624D0B";
    }
}
