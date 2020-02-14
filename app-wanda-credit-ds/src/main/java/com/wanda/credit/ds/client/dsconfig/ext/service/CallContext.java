package com.wanda.credit.ds.client.dsconfig.ext.service;

import java.util.List;

import com.wanda.credit.ds.client.dsconfig.ext.expression.ParamCondExpr;
import com.wanda.credit.ds.client.dsconfig.ext.model.CallParam;
import com.wanda.credit.dsconfig.enums.ServiceType;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2017年3月8日 上午10:12:27 
 *  
 */
public class CallContext {
	private ServiceType type;
    private String serviceId;
    private String methodName;
    private List<CallParam> params;
    private boolean sync = true;
    private String handle;
	public ServiceType getType() {
		return type;
	}
	public CallContext setType(ServiceType type) {
		this.type = type;
		return this;
	}
	public String getServiceId() {
		return serviceId;
	}
	public CallContext setServiceId(String serviceId) {
		this.serviceId = serviceId;
		return this;
	}
	public String getMethodName() {
		return methodName;
	}
	public CallContext setMethodName(String methodName) {
		this.methodName = methodName;
		return this;
	}
	public List<CallParam> getParams() {
		return params;
	}
	public CallContext setParams(List<CallParam> callParams) {
		this.params = callParams;
		return this;
	}
	public boolean isSync() {
		return sync;
	}
	public CallContext setSync(boolean sync) {
		this.sync = sync;
		return this;
	}
	public String getHandle() {
		return handle;
	}
	public CallContext setHandle(String handle) {
		this.handle = handle;
		return this;
	}   
}
