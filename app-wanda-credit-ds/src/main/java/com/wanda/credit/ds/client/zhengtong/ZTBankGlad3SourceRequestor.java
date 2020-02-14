package com.wanda.credit.ds.client.zhengtong;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.jixin.JiXinAuthenBankCardDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.zhengtong.ZT_Face_Result;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 描述: 银行卡鉴权
 */
@DataSourceClass(bindingDataSourceId = "ds_zhengt_bank3")
public class ZTBankGlad3SourceRequestor extends BaseZTDataSourceRequestor
		implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(ZTBankGlad3SourceRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
    protected IAllAuthCardService allAuthCardService;
	@Autowired
	private JiXinAuthenBankCardDataSourceRequestor jixinCard3;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 政通银行卡三要素服务请求开始...", prefix);
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> retdata = new HashMap<String, Object>();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		String zt_url = propertyEngine.readById("ds_zhengt_bank_inner_url");// 政通调用连接
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			String name = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[0])); // 身份证号码
			String cardNo = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[1])); // 姓名
			String cardId = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[2])); // 照片数据包 	
			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();
			logger.info("{} 政通银行卡三要素服务加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			reqparam.put("cardId", cardId);
			logObj.setReq_url(zt_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			if(!isChineseWord(name)){
				logObj.setIncache("1");
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "传入参数格式有误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			if (StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))) {
				logger.warn("{}入参格式不符合要求!", prefix);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				return rets;
			} else {
				logObj.setIncache("0");
				Map<String, String> params = new HashMap<String,String>();
				Map<String, String> headers = new HashMap<String,String>();
				params.put("trade_id", trade_id);
				params.put("name", name);
				params.put("cardNo", cardNo);
				params.put("cardId", cardId);
				logger.info("{} 政通银行卡三要素服务请求开始...", prefix);
				 String res = RequestHelper.doPost(zt_url,null,headers,params,null,false, 10000);
//				 String res = "{\"result\":{\"error_info\":\"\",\"dsName\":[\"results\"],\"results\":[{\"localsim\":\"0.0\",\"biztyp\":\"A001\",\"certseq\":\"411303198501274819\",\"accountName\":\"建设银行\",\"mpssim\":\"0.0\",\"respinfo\":\"认证不一致(不通过):发卡行不支持此笔交易\",\"telephone\":\"\",\"sysSeqNb\":\"19103520191012011610220001\",\"ptycd\":\"4984600001\",\"name\":\"尚建\",\"cerkey\":\"\",\"respcd\":\"2001\",\"ptyAcct\":\"gelande01\",\"status\":\"00\"}],\"error_no\":\"0\"}}";
				logger.info("{} 政通银行卡三要素服务请求结束,返回信息:{}", prefix, res);

				if (StringUtil.isEmpty(res)) {
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败");
					rets.put(Conts.KEY_RET_TAG, new String[] { Conts.TAG_UNFOUND });
					logger.error("{} 外部返回识别失败,返回结果为空", trade_id);
					return rets;
				}
				JSONObject resJson = JSONObject.fromObject(res);
				JSONObject result = JSONObject.fromObject(resJson.getString("result"));
				ZT_Face_Result ztFRes = new ZT_Face_Result();
				if ("0".equals(result.getString("error_no"))) {
					JSONArray resAry = result.getJSONArray("results");
					String arr = com.alibaba.fastjson.JSONObject.toJSONString(JSONObject.fromObject(resAry.get(0)), true);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					ztFRes = com.alibaba.fastjson.JSONObject.parseObject(arr, ZT_Face_Result.class);
				}		
				ztFRes.setTrade_id(trade_id);
				ztFRes.setError_info(result.getString("error_info"));
				ztFRes.setError_no(result.getString("error_no"));
//				zTFace251Service.add(ztFRes);
				logObj.setBiz_code1(result.getString("error_no"));
				logObj.setBiz_code2(String.valueOf(ztFRes.getSysSeqNb()));
				logObj.setState_msg(result.getString("error_info"));
				if("-1005".equals(result.getString("error_no"))){
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
					rets.put(Conts.KEY_RET_TAG, new String[] { Conts.TAG_UNFOUND });
					rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败:手机号或者银行卡号次数超过限制");
					logger.error("{} 银行卡鉴权失败:手机号或者银行卡号次数超过限制", trade_id);
					return rets;
				}
				if("2000".equals(ztFRes.getRespcd())){
					resource_tag = Conts.TAG_TST_SUCCESS;
					retdata.put("respCode", "2000");
					retdata.put("respDesc", "认证一致");
					retdata.put("detailRespCode", "");
					retdata.put("respDetail", "");
					logger.info("{} 认证一致",trade_id);
					String req_values = cardNo+"_"+cardId;
					allAuthCardService.saveAuthCard(ds.getId(), trade_id, name, GladDESUtils.encrypt(cardNo), GladDESUtils.encrypt(cardId), "",
							retdata, req_values);
				}else if("2001".equals(ztFRes.getRespcd())){
					resource_tag = Conts.TAG_TST_SUCCESS;
					retdata.put("respCode", "2001");
					retdata.put("respDesc", ztFRes.getRespinfo());
					retdata.put("detailRespCode", "");
					retdata.put("respDetail", "");
					if(ztFRes.getRespinfo().contains("发卡行不支持")){
						resource_tag = Conts.TAG_NOMATCH;
					}
					logger.info("{} 认证不一致",trade_id);
				}else{
					if(ztFRes.getRespinfo().contains("请求频繁") || ztFRes.getRespinfo().contains("交易次数超限")){
						rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败:请求频繁,验证次数超限");
					}else if(ztFRes.getRespinfo().contains("该卡暂不支持认证")){
						rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败:该卡暂不支持认证");
					}else{
						ds.setId("ds_jixin_AuthenBankCard3");
			        	rets = jixinCard3.request(trade_id, ds);
			        	return rets;
					}
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
					rets.put(Conts.KEY_RET_TAG, new String[] { Conts.TAG_UNFOUND });
					logger.error("{} 外部返回识别失败,返回结果为空", trade_id);
					return rets;
				}
			}
			retdata.put("name", name);
            retdata.put("cardNo", cardNo);
            retdata.put("cardId", cardId);
            
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		} catch (Exception ex) {
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			logger.error("{} 数据源处理时异常：{}", prefix, ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		} finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}
		return rets;
	}
}
