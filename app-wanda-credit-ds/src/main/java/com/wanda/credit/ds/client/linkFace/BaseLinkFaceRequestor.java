package com.wanda.credit.ds.client.linkFace;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.ds.BaseDataSourceRequestor;

public class BaseLinkFaceRequestor extends BaseDataSourceRequestor{
	private final Logger logger = LoggerFactory.getLogger(BaseLinkFaceRequestor.class);
	public String bulidResp(String trade_id,String code,
			Map<String,Object> retdata,Map<String,Object> rets){
		String resource_tag = Conts.TAG_SYS_ERROR;
		logger.info("{} 构建出参开始...", trade_id);
		switch(code){
			case "1":
				retdata.put("respCode", "2000");
				retdata.put("respDesc", "认证一致");
				retdata.put("detailRespCode", "");
				retdata.put("respDetail", "");
				logger.info("{} 认证一致",trade_id);
				break;
			case "2":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "");
				retdata.put("respDetail", "");
				logger.info("{} 认证不一致",trade_id);
				break;
			case "30":
				retdata.put("respCode", "2003");
				retdata.put("respDesc", "不支持验证");
				retdata.put("detailRespCode", "0307");
				retdata.put("respDetail", "请开通无卡支付服务");
				break;
			case "31":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "");
				retdata.put("respDetail", "");
				logger.info("{} 认证不一致",trade_id);
				break;
			case "32":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0103");
				retdata.put("respDetail", "无效卡号");
				logger.info("{} 认证不一致",trade_id);
				break;
			case "33":
				retdata.put("respCode", "2003");
				retdata.put("respDesc", "不支持验证");
				retdata.put("detailRespCode", "0308");
				retdata.put("respDetail", "未知商户，不予通过");
				logger.info("{} 认证不一致",trade_id);
				break;
			case "34":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0111");
				retdata.put("respDetail", "卡状态不正常（睡眠卡/未初始化卡）");
				logger.info("{} 认证不一致:睡眠卡",trade_id);
				break;
			case "35":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0109");
				retdata.put("respDetail", "有作弊嫌疑");
				logger.info("{} 认证不一致:作弊卡",trade_id);
				break;
			case "36":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0113");
				retdata.put("respDetail", "卡状态不正常(挂失卡)");
				logger.info("{} 认证不一致:作弊卡",trade_id);
				break;
			case "37":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0112");
				retdata.put("respDetail", "卡状态不正常(过期卡)");
				logger.info("{} 认证不一致:过期",trade_id);
				break;
			case "38":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0107");
				retdata.put("respDetail", "密码错误次数超限");
				logger.info("{} 认证不一致:密码错误次数超限",trade_id);
				break;
			case "39":
				retdata.put("respCode", "2003");
				retdata.put("respDesc", "不支持验证");
				retdata.put("detailRespCode", "0301");
				retdata.put("respDetail", "发卡行不支持此交易，请联系发卡行");
				logger.info("{} 认证不一致:发卡行不支持此交易，请联系发卡行",trade_id);
				break;
			case "40":
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "0102");
				retdata.put("respDetail", "受限制的卡");
				logger.info("{} 认证不一致:受限制的卡",trade_id);
				break;
			default :
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "");
				retdata.put("respDetail", "");
				logger.info("{} 认证不一致",trade_id);
				break;
		}
		resource_tag = Conts.TAG_TST_SUCCESS;
		
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		rets.put(Conts.KEY_RET_DATA, retdata);
		rets.put(Conts.KEY_RET_MSG, "交易成功");
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		return resource_tag;
	}
}
