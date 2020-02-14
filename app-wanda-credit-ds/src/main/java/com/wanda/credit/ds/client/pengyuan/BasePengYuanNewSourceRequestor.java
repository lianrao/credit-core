package com.wanda.credit.ds.client.pengyuan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.base.util.EncryptionHelper.RSAHelper.PublicKeyException;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.ds.BaseDataSourceRequestor;

public class BasePengYuanNewSourceRequestor extends BaseDataSourceRequestor {
	private final static Logger logger = LoggerFactory.getLogger(BasePengYuanNewSourceRequestor.class);
	protected String userId;
	protected String userPwd;
	protected String queryType;
	protected String reportIds;
	protected static WebServiceSingleQueryOfUnzipSoapBindingStub  stubEdu;
	protected static WebServiceSingleQueryOfUnzipSoapBindingStub  oldStubEdu;
	protected static WebServiceSingleQueryOfUnzipSoapBindingStub  ostaStubEdu;
	
	
	/**
	 * 初始化连接资源
	 * @throws PublicKeyException
	 */
	public static void init() throws PublicKeyException{
		if(stubEdu == null){
			try {
				stubEdu = (WebServiceSingleQueryOfUnzipSoapBindingStub) new WebServiceSingleQueryOfUnzipServiceLocator()
						.getWebServiceSingleQueryOfUnzip();
				oldStubEdu = (WebServiceSingleQueryOfUnzipSoapBindingStub) new WebServiceSingleQueryOfUnzipServiceOldLocator()
				.getWebServiceSingleQueryOfUnzip();
				ostaStubEdu = (WebServiceSingleQueryOfUnzipSoapBindingStub) new WebServiceSingleQueryOfUnzipServiceOstaLocator()
						.getWebServiceSingleQueryOfUnzip();
				stubEdu.setTimeout(Integer.parseInt(DynamicConfigLoader.get("sys.credit.client.httpEdu.timeout")));
				oldStubEdu.setTimeout(Integer.parseInt(DynamicConfigLoader.get("sys.credit.client.httpEdu.timeout")));
				ostaStubEdu.setTimeout(Integer.parseInt(DynamicConfigLoader.get("sys.credit.client.httpEdu.timeout")));
			} catch (ServiceException e) {
				logger.error("连接鹏元服务器失败!",e);				
			}
		}
	}
	/**
	 * 报文格式过滤
	 * @param rspBody
	 * @return
	 */
	protected String filtRspBody(String rspBody){
		rspBody = rspBody.replace("<![CDATA[", "");
		rspBody = rspBody.replace("<?xml version=\"1.0\" encoding=\"GBK\" ?>","");
		rspBody = rspBody.replace("]]>", "");
		return rspBody;
	}
	//根据身份证号输出年龄
    public static int IdNOToAge(String IdNO){
        int leh = IdNO.length();
        String dates="";
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        String year=df.format(new Date());
        if (leh == 18) {
            dates = IdNO.substring(6, 10);          
            int u=Integer.parseInt(year)-Integer.parseInt(dates);
            return u;
        }else{
            dates = "19"+IdNO.substring(6, 8);
            return Integer.parseInt(year)-Integer.parseInt(dates);
        }
    }
    //判断是否拍拍贷账户,如果是返回true
  	public boolean isStartTimeReturn(String edu_accts,String acct_id){
  		if(StringUtil.isEmpty(acct_id) || StringUtil.isEmpty(edu_accts))
  			return false;
  		for(String acct_tmp:edu_accts.split(",")){
  			if(acct_id.equals(acct_tmp)){
  				return true;
  			}
  		}
  		return false;
  	}
 // 毕业年限
 	public String EndTime(String dates) {
 		if (dates == null)
 			return "";
 		SimpleDateFormat df = new SimpleDateFormat("yyyy");
 		String year = df.format(new Date());
 		int u = Integer.parseInt(year) - Integer.parseInt(dates);
 		return String.valueOf(u);
 	}
    /**
	 * 从交易返回消息中提取参数值
	 * @param key
	 * @param params_out
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object extractValueFromResult(String key,
			Map<String, Object> params_out) {
		Object retdataObj = params_out.get(Conts.KEY_RET_DATA);
		if(retdataObj!=null){
			if(retdataObj instanceof Map){
				return ((Map<String, Object>) params_out.get(Conts.KEY_RET_DATA))
						.get(key);
			}
		}
		return null;
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getReportIds() {
		return reportIds;
	}
	public void setReportIds(String reportIds) {
		this.reportIds = reportIds;
	}
}
