package com.wanda.credit.ds.client.bill99lBankCard.util;

import java.util.HashMap;

/**
 * @Description: 快钱VOP_CNP的Interface接口程序
 * @Copyright (c) 上海快钱信息服务有限公司
 * @version 2.0
 */

/**
 * 该类用来拼接XML串和解析XML
 * */
@SuppressWarnings("unchecked")
public class ParseUtil {
	/**
	 * 具体解析XML方法，返回一个HashMap
	 * resXml：快钱返回的XML数据流
	 * */
	public  HashMap parseXML(String resXml,String... nodeNames){
		HashMap returnRespXml=null;
		ParseXMLUtil pxu=ParseXMLUtil.initParseXMLUtil();//初始化ParseXMLUtil
		if(resXml!=null){
			returnRespXml= pxu.returnXMLDataList(pxu.parseXML(resXml), nodeNames[0], nodeNames[1]);
		}
		return returnRespXml;
	}
}
