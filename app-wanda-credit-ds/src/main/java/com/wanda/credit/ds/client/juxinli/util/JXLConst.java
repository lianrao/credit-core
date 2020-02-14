package com.wanda.credit.ds.client.juxinli.util;

public class JXLConst {
	
	public static final  String RES_CODE = "res_code";
	public static final  String RES_MSG = "res_message";
	
	public static final String CARDNO_INCORRECT = "cardno_incorrect";
	public static final String MOBILENO_INCORRECT = "mobile_incorrect";
	public static final String EMAIL_INCORRECT = "email_incorrect";
	
	public static final String LOG_ON = "1";
	
	public static final String FLAG_SUCCESS = "success";
	public static final String FLAG_FAILED = "fail";
	public static final String JUXINLI_DSONLINE_STATUS = "1";//数据源状态(0暂时下线 1 在线 2 开发中 3 长时间下线)
	public static final String SUCCESS_CODE = "10008";
	public static final int PROCESS_CODE_SUCC = 10008;
	public static final String SUCCESS_RESULT = "true"; 
	public static final String FLAG_DATA = "data";
	
	//获取安全凭证码聚信立返回为空
	public static final String ACCEPT_TOKEN_RES_NULL = "accept_token_res_null";
	//获取安全凭证码聚信立返回结果中success节点内容为false
	public static final String ACCEPT_TOKEN_SUC_FALSE = "accept_token_res_sucess_false";
	//获取安全凭证码聚信立返回结果中success节点内容为false
	public static final String ACCEPT_TOKEN_RES_ACCESS_TOKEN_NULL = "ACCEPT_TOKEN_RES_ACCESS_TOKEN_NULL";
	// 对应的序列号不存在，
	public static final String REQUESTID_NO_EXIST = "requestid_no_exist";
	// 没有响应的表单参数，可能未调用
	public static final String ECESSARY_PARAM_NULL = "ecessary_param_null";
	// 没有响应的表单参数，可能未调用
	public static final String NO_LOGIN_INFO = "no_login_param_info";
	// 公积金登陆方式
	public static final String LOGIN_TYPE_ERROR = "login_type_error";
	//ignoreMobile参数格式错误
	public static final String PARAM_IGNOREMOBILE_ERROR = "PARAM_IGNOREMOBILE_ERROR";
	//参数websiteInfos格式错误
	public static final String PARAMS_WEBSITE_ERROR = "PARAMS_WEBSITE_ERROR";
	//参数contacts格式错误
	public static final String PARMS_CONTACTS_ERROR = "PARMS_CONTACTS_ERROR";
	//跳过手机运营商认证的时候电商网站信息为null
	public static final String PARMS_WEBSITE_SIZE_ZERO = "WEBSITE_SIZE_ZERO";
	//http请求聚信立返回为NULL标志
	public static final String RES_NULL = "JUXINLI_RETURN_NULL";
	//聚信立返回结果data节点内容为NULL标志
	public static final String RES_DATA_NULL = "JUXINLI_RETURN_DATA_NULL";
	//聚信立返回节点中success节点内容不为TRUE
	public static final String RES_SUC_NOT_TRUE = "JUXINLI_RETURN_SUCCESS_NOT_TRUE";
	//聚信立返回节点中success节点为空
	public static final String RES_SUC_NULL = "JUXINLI_RETURN_SUCCESS_NULL";
	//请求聚信立客户机构标识码 key
	public static final String CLIENT_SECRET = "client_secret";
	//请求聚信访问标志码  key
	public static final String ACCESS_TOKEN = "access_token";
	//请求聚信立token key
	public static final String COLL_TOKEN = "token";
	//请求聚信立安全凭证过期时效 key
	public static final String ACCESS_TOKEN_HOURS = "hours";
	//请求聚信立机构名称key
	public static final String ORG_NAME = "org_name";
	
	public static final String EBUSI_REQUEST_ID = "request_id";
	public static final String EBUSI_NEXT_DATASOURCE = "next_datasource";
	//跳过手机运营商验证
	public static final String EBUSI_SKIP_MOBILE_YES = "1";
	//不跳过手机运营商验证
	public static final String EBUSI_SKIP_MOBILE_NO = "0";
	
	
	//采集请求是否完成标识
	public final static String SUBMIT_FINISHED = "is_finished";
	//采集流程结束
	public final static String SUBMIT_FINISHED_YES = "Y";
	//采集流程没有结束，继续提交
	public final static String SUBMIT_FINISHED_NO = "N";
	//采集请求结果响应类型
	public final static String SUBMIT_RESPONSE_TYPE = "respose_type";
	//采集请求结果响应类型结果
	//正常流程可继续交互
	public final static String SUBMIT_RESPONSE_NORMAL = "NORMAL";
	//异常情况停止流程重新开始
	public final static String SUBMIT_RESPONSE_ERROR = "ERROR";
	//非正常流程建议稍后重试
	public final static String SUBMIT_RESPONSE_RETRY = "RETRY";	
	//提交动态验证码
	public static final String MOBILE_SUBMIT_CAPTCHA = "SUBMIT";
	public static final String JXL_SUBMIT_CAPTCHA = "SUBMIT_CAPTCHA";
	//重发动态验证码
	public static final String MOBILE_RESEND_CAPTCHA = "RESEND";
	public static final String JXL_RESEND_CAPTCHA = "RESEND_CAPTCHA";
	//提交查询密码
	public static final String MOBILE_SUBMIT_QUERY_PWD = "SUBMIT_QUERY_PWD";
	public static final String JXL_SUBMIT_QUERY_PWD = "SUBMIT_QUERY_PWD";
	
	/*聚信立支持的数据源列表返回参数列表 BEGIN*/
	//网站英文名称
	public static final String WEBSITE_EN_NAME = "website_en_name";
	//网站中文名称
	public static final String WEBSITE_CN_NAME = "website_cn_name";
	//网站分类英文名称
	public static final String CATEGORY_EN_NAME = "category_en_name";
	//网站分类中文名称
	public static final String CATEGORY_CN_NAME = "category_cn_name";
	/*聚信立支持的数据源列表返回参数列表 END*/
	
	
	/*聚信立提交采集请求对应常量 BEGIN*/
	public static final String MEBUSI_SUBMIT_SUCCESSCODE = "10008";
	//完成所有采集请求标识,数据库中标示T_DS_JXL_ORIG_APPLY_INFO
	public static final String MEBUSI_SUBMIT_All_FINISH = "finished";
	//当前数据还未完成采集
	public static final String MEBUSI_SUBMIT_ING = "false";
	//当前数据源已完成采集
	public static final String MEBUSI_SUBMIT_SUCC = "true";
	//跳过当前数据源
	public static final String MEBUSI_SUBMIT_SKIP = "skip";
	/*聚信立提交采集请求对应常量 END*/
	
	
	/*运营商原始数据对外返回报文标识符 BEGIN*/
	public static final String MOBILE_RAWDATA_BASIC = "basic";
	public static final String MOBILE_RAWDATA_CALL = "calls";
	public static final String MOBILE_RAWDATA_SMSE = "smses";
	public static final String MOBILE_RAWDATA_NET = "nets";
	public static final String MOBILE_RAWDATA_ACCOUNT = "accounts";
	
	public static final String MOBILE_RAWDATA_SUCCESS_CODE = "31200";
	/*运营商原始数据对外返回报文标识符 END*/
	
	
	/*报告数据对外返回报文标识 BEGIN*/
	//运营商通话详情（contact_list）
	public static final String REPORTDATA_CONTACT_LIST = "call_details";
	//绑定数据源信息（data_source）
	public static final String REPORTDATA_DATASOURCE = "depends_datasource";
	//行为检测（behavior_check）
	public static final String REPORTDATA_BEHAVIOR_CHECK = "behavior_info";
	//联系人名单（collection_contact）
	public static final String REPORTDATA_COLL_CONTACT= "contact_collection";
	//出行消费（trip_consume）
	public static final String REPORTDATA_TRIP_CONSUME = "trip_expense";
	//电商月消费（ebusiness_expense）
	public static final String REPORTDATA_EBUSI_EXPENSE = "ebusiness_month_expense";
	//申请人信息（person）
	public static final String REPORTDATA_PERSON = "person_basic_info";
	//主要服务（main_service）
	public static final String REPORTDATA_MAIN_SERVICE = "main_service";
	//联系人区域汇总（contact_region）
	public static final String REPORTDATA_CONTACT_REGION = "contact_region";
	//信息核对（application_check）
	public static final String REPORTDATA_APPLICATION_CHECK = "person_info_check";
	//电商地址分析（deliver_address）
	public static final String REPORTDATA_DELIVER_ADDRESS = "express_address";
	//出行分析（trip_info）
	public static final String REPORTDATA_TRIP_INFO = "trip_info";
	//运营商数据整理（cell_behavior）
	public static final String REPORTDATA_CELL_BEHAVIOR = "call_behavior";
	//
	public static final String REPORTDATA_USER_INFO_CHECK = "user_info_check";
	/*报告数据对外返回报文标识 END*/
	
	/*报告中的子项内容  BEGIN*/
	public final static String APPLICATION_CHECK_POINTS = "check_points";
	public final static String BEHAVIOR_CHECK_POINT = "check_point";
	public final static String APPLICATION_CHECK_BUSINESS = "check_ebusiness";
	public final static String CONTACT_COLL_TOTAL_COUNT= "total_count";
	public final static String CONTACT_COLL_TOTAL_AMOUNT= "total_amount";
	/*报告中的子项内容  END*/
	
	//相同身份证号码，姓名，手机号最近一次提交采集的日期
	public static final String LAST_COLL_DATE = "last_collect_date";
	
	
	//产品编号
	public static final String TF_GET_ALL_DS = "EBUSI_G_1";
	public static final String TF_GET_TOKEN = "EBUSI_G_2";
	public static final String TF_SUB_ACCOUT = "EBUSI_G_3";
	public static final String TF_GET_SUB_RES = "EBUSI_G_4";
	public static final String TF_GET_SUB_RES_BY_DET = "EBUSI_G_5";
	public static final String TF_GET_MOBILE_RAWDATA = "MOBILED1";
	public static final String TF_GET_MOBILE_RAWDATA_BY_DET = "MOBILED2";
	public static final String TF_GET_REPORT = "REPORTD1";
	public static final String TF_GET_REPORT_BY_DET = "REPORTD2";
	
	
	public static final String SWITCH_FLAG = "TRADE_SAVE_SWITCH";
	public static final String SWITCH_ON = "Y";
	
	public static final String TABLE_BIZ_DS_ID_FLAG = "ds_jxl_ebusi";
	

}
