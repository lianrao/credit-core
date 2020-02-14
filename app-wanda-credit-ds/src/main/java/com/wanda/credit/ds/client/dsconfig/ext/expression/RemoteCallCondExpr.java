package com.wanda.credit.ds.client.dsconfig.ext.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IDataSourceService;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.dsconfig.ext.model.CallParam;
import com.wanda.credit.ds.client.dsconfig.ext.service.CallContext;
import com.wanda.credit.ds.client.dsconfig.ext.service.CallerFactory;
import com.wanda.credit.ds.client.dsconfig.ext.service.ICaller;
import com.wanda.credit.dsconfig.enums.ServiceType;
import com.wanda.credit.dsconfig.main.ResolveContext;
import com.wanda.credit.dsconfig.model.expression.ConditionExpr;

/**
 * @description 用于调用ds api 或者product
 * @author wuchsh
 * @version 1.0
 * @createdate 2017年3月7日 下午8:21:06
 * 
 */
public class RemoteCallCondExpr extends ConditionExpr {
	private final Logger logger = LoggerFactory
			.getLogger(RemoteCallCondExpr.class);

	private ServiceType type;
	private String serviceId;
	private String methodName;
	private List<ParamCondExpr> params;
	private boolean sync = true;
	private String handle;

	public ServiceType getType() {
		return type;
	}

	public void setType(ServiceType type) {
		this.type = type;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public List<ParamCondExpr> getParams() {
		return params;
	}

	public void setParams(List<ParamCondExpr> params) {
		this.params = params;
	}

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public Object executeOnCondition() {
		if (checkCondition()) {
			return execute();
		}
		return null;
	}

	@Override
	public Object execute() {
		ICaller caller = CallerFactory.getCaller(type);
		CallContext context = new CallContext();
		context.setServiceId(serviceId).setHandle(handle).setSync(sync)
				.setType(type);
		if (type.equals(ServiceType.api)) {
			context.setMethodName(methodName);
		}
		List<CallParam> callParams = buildCallParams();
		context.setParams(callParams);
		caller.call(context);
		return null;

	}

	private List<CallParam> buildCallParams() {
		List<CallParam> ripeParams = new ArrayList<CallParam>();
		if (CollectionUtils.isNotEmpty(params)) {
			for (ParamCondExpr paramExpr : params) {
				if (paramExpr == null)
					continue;
				if (paramExpr.checkCondition()) {
					CallParam callparam = new CallParam();
					ripeParams.add(callparam);
					callparam.setName(paramExpr.getName()).setValue(
							paramExpr.execute());
				}
			}
		}
		return ripeParams;
	}

}
