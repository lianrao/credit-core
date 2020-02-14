/**   
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author xiaobin.hou  
 * @date 2017年2月14日 上午10:54:04 
 * @version V1.0   
 */
package com.wanda.credit.ds.client.trulioo.bean;

import java.util.List;
import java.util.Map;

/**
 * @author xiaobin.hou
 *
 */
public class VerifyReqBean {

	private boolean AcceptTruliooTermsAndConditions;
	private boolean Demo;
	private String CallBackUrl;
//	private int Timeout;	//超时时间 秒
	private boolean CleansedAddress;
	private String ConfigurationName = "Identity Verification";
//	private List<String> ConsentForDataSources;
	private String CountryCode;
	private Map<String,Object> DataFields;
	private boolean VerboseMode;
	
	
	public boolean isAcceptTruliooTermsAndConditions() {
		return AcceptTruliooTermsAndConditions;
	}
	public void setAcceptTruliooTermsAndConditions(
			boolean acceptTruliooTermsAndConditions) {
		AcceptTruliooTermsAndConditions = acceptTruliooTermsAndConditions;
	}
	public boolean isDemo() {
		return Demo;
	}
	public void setDemo(boolean demo) {
		Demo = demo;
	}
	public String getCallBackUrl() {
		return CallBackUrl;
	}
	public void setCallBackUrl(String callBackUrl) {
		CallBackUrl = callBackUrl;
	}
//	public int getTimeout() {
//		return Timeout;
//	}
//	public void setTimeout(int timeout) {
//		Timeout = timeout;
//	}
	public boolean isCleansedAddress() {
		return CleansedAddress;
	}
	public void setCleansedAddress(boolean cleansedAddress) {
		CleansedAddress = cleansedAddress;
	}
	public String getConfigurationName() {
		return ConfigurationName;
	}
	public void setConfigurationName(String configurationName) {
		ConfigurationName = configurationName;
	}
//	public List<String> getConsentForDataSources() {
//		return ConsentForDataSources;
//	}
//	public void setConsentForDataSources(List<String> consentForDataSources) {
//		ConsentForDataSources = consentForDataSources;
//	}
	public String getCountryCode() {
		return CountryCode;
	}
	public void setCountryCode(String countryCode) {
		CountryCode = countryCode;
	}
	public Map<String, Object> getDataFields() {
		return DataFields;
	}
	public void setDataFields(Map<String, Object> dataFields) {
		DataFields = dataFields;
	}
	public boolean isVerboseMode() {
		return VerboseMode;
	}
	public void setVerboseMode(boolean verboseMode) {
		VerboseMode = verboseMode;
	}
//	@Override
//	public String toString() {
//		return "VerifyReqBean [AcceptTruliooTermsAndConditions="
//				+ AcceptTruliooTermsAndConditions + ", Demo=" + Demo
//				+ ", CallBackUrl=" + CallBackUrl + ", Timeout=" + Timeout
//				+ ", CleansedAddress=" + CleansedAddress
//				+ ", ConfigurationName=" + ConfigurationName
//				+ ", ConsentForDataSources=" + ConsentForDataSources
//				+ ", CountryCode=" + CountryCode + ", DataFields=" + DataFields
//				+ ", VerboseMode=" + VerboseMode + "]";
//	}
	
	

}
