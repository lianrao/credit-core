package com.wanda.credit.ds.client.zhongshu;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
@WebService(targetNamespace = "http://service.webservice.front.query.gsinfo.chinadaas.com/")
public interface EntInfoQueryService {
	@WebMethod
	@WebResult String queryEntInfoByKey(@WebParam(name="encodeString",targetNamespace = "http://service.webservice.front.query.gsinfo.chinadaas.com/") String encodeString);
	
	@WebMethod
	@WebResult String queryEntInfoForXml(@WebParam(name="encodeString",targetNamespace = "http://service.webservice.front.query.gsinfo.chinadaas.com/")  String encodeString);
	
	@WebMethod
	@WebResult String queryPerInfoForXml(@WebParam String encodeString);
	
	@WebMethod
	@WebResult String queryDisHonestyForXml(@WebParam(name="encodeString",targetNamespace = "http://service.webservice.front.query.gsinfo.chinadaas.com/")  String encodeString);
	
	@WebMethod
	@WebResult String postMonitorOrderAdd(@WebParam String encodeString);
	
	@WebMethod
	@WebResult String postChangeOrderlistQuery(@WebParam String encodeString);
	
	@WebMethod
	@WebResult String postChangeOrderlistQueryByUnread(@WebParam String encodeString);
	
	@WebMethod
	@WebResult String postChangeDetailsQuery(@WebParam String encodeString);

	/**
	 * 根据企业名称、人员姓名、职务查询人员信息 模板12
	 * add by wangdd 20160517
	 * @param encodeString
	 * @return
	 */
	@WebMethod
	@WebResult String queryPersonByPositionForXml(@WebParam String encodeString);
	
}
