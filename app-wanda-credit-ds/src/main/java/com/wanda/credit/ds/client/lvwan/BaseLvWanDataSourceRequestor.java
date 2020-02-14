package com.wanda.credit.ds.client.lvwan;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lvwan.gb.sdk.common.HttpUtil;
import com.lvwan.gb.sdk.util.VerifyUtil;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;

public class BaseLvWanDataSourceRequestor extends BaseDataSourceRequestor {
	private final static Logger logger = LoggerFactory.getLogger(BaseLvWanDataSourceRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;

	/**
	 * 
	 * 获取结果的方法，60秒后服务器不返回结果则不再重试
	 * 
	 * @param _sUrl
	 * @param _hParams
	 * @param _sRequestMethod
	 * @return
	 */
	protected String getResult(String _sUrl, HashMap<String, Object> _hParams, String _sRequestMethod) {

		String sResult = HttpUtil.send(_sUrl, _hParams, _sRequestMethod);
		HashMap<String, Object> resultMap = VerifyUtil.handleResult(sResult);
		int nError = (Integer) resultMap.get("error");
		int m_nResultTimeout = Integer.valueOf(propertyEngine.readById("lw_result_timeout"));
		int result_sleep = Integer.valueOf(propertyEngine.readById("lw_result_sleep"));
		int m_nRetryTimes = 0;
		if (44206 == nError) {
			if (m_nRetryTimes > m_nResultTimeout) {
				return sResult;
			}

			// task no response,please retry.
			try {
				logger.info("Please wait task response. sResult::" + sResult);
				m_nRetryTimes++;
				Thread.sleep(result_sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// request again
			return getResult(_sUrl, _hParams, _sRequestMethod);
		}

		return sResult;
	}

	protected final static Map<Integer, String> ERROR_CODE = new HashMap<Integer, String>();
	static {
		ERROR_CODE.put(-1, "系统繁忙，此时请开发者稍候再试");
		ERROR_CODE.put(0, "请求成功");
		ERROR_CODE.put(40001, "获取access_token时AppSecret错误");
		ERROR_CODE.put(40002, "不合法的AppID");
		ERROR_CODE.put(40003, "不合法的access_token，请重新获取access_token");
		ERROR_CODE.put(40004, "非法访问，不在服务器白名单内的访问");
		ERROR_CODE.put(40005, "api功能未授权，请确认开发者帐号已获得该接口权限");
		ERROR_CODE.put(40006, "api功能未启用，请开启");
		ERROR_CODE.put(40007, "api访问次数限制，访问已经超出每天的次数限制");
		ERROR_CODE.put(40008, "api签名无效或者时间戳误差超过2小时");
		ERROR_CODE.put(40009, "每秒提交任务的次数已经超过次数限制");
		ERROR_CODE.put(40010, "每分钟提交任务的次数已经超过次数限制");
		ERROR_CODE.put(40011, "每秒获取任务结果的次数已经超过次数限制");
		ERROR_CODE.put(40100, "任务参数对应的用户认证未通过");
		ERROR_CODE.put(40400, "API不存在");
		ERROR_CODE.put(43001, "需要GET请求");
		ERROR_CODE.put(43002, "需要POST请求");
		ERROR_CODE.put(43003, "需要HTTPS请求");
		ERROR_CODE.put(44000, "参数错误");
		ERROR_CODE.put(44001, "缺少身份证号");
		ERROR_CODE.put(44002, "缺少驾驶证号");
		ERROR_CODE.put(44003, "缺少车牌号或车架号(发动机号、号牌种类)等");
		ERROR_CODE.put(44004, "TaskId不存在或者不合法");
		ERROR_CODE.put(44005, "此TaskId无权访问");
		ERROR_CODE.put(44006, "比对服务至少需要两个字段");
		ERROR_CODE.put(44007, "比对服务内容超长，可以拆分为多次比对");
		ERROR_CODE.put(44008, "Task类型不匹配，不能提取数据");
		ERROR_CODE.put(44009, "VIN码不合法");
		ERROR_CODE.put(44010, "缺少姓名");
		ERROR_CODE.put(44011, "缺少企业编号（组织机构代码）");
		ERROR_CODE.put(44012, "缺少企业名称");
		ERROR_CODE.put(44013, "缺少手机号码");
		ERROR_CODE.put(44014, "非法的手机号码");
		ERROR_CODE.put(44206, "Task结果尚未返回，请稍等");
		ERROR_CODE.put(44208, "Task结果敏感，必须回流数据（包括但不限于手机号、地理位置坐标、调用原因等信息）");
		ERROR_CODE.put(50001, "帐号已被禁用");
		ERROR_CODE.put(50002, "余额不足");
	}
}
