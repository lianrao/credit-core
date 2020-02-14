package com.wanda.credit.ds.client.xyan;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
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
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.xyan.utils.RsaCodingUtil;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description 新颜银联画像
 * @author nan.liu
 * @version 1.0
 * @createdate 2019年8月21日
 * 
 */
@DataSourceClass(bindingDataSourceId = "ds_xyan_uniondraw")
public class XYanUnionDrawSourceRequestor extends BaseXYanAuthenBankCardDataSourceRequestor implements	IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(XYanUnionDrawSourceRequestor.class);

	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id;

		String member_id = propertyEngine.readById("ds_xyan_member_id");
		String terminal_id = propertyEngine.readById("ds_xyan_termid");
		String request_url = propertyEngine.readById("ds_xyan_unionDraw_url_new");
		String pfxpwd = propertyEngine.readById("ds_xyan_pfxpwd");
		String pfxname = propertyEngine.readById("ds_xyan_pfxname");

		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		//计费标签
		String resource_tag = Conts.TAG_SYS_ERROR;
		logger.info("{} 银联画像交易开始...", prefix);
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setIncache("0");
		logObj.setTrade_id(trade_id);
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(request_url);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易成功");

		Map<String, Object> rets = new HashMap<String, Object>();

		Map<String, Object> reqparam = new HashMap<String, Object>();
		try {
			/** 姓名-必填 */
			String name = (String) ParamUtil.findValue(ds.getParams_in(),"name");
			/** 身份证号码-选填 */
			String cardNo = (String) ParamUtil.findValue(ds.getParams_in(),"cardNo");
			String cardId = "";
			if(ParamUtil.findValue(ds.getParams_in(), "cardId")!=null){
				cardId = ParamUtil.findValue(ds.getParams_in(), "cardId").toString();
			}
			/** 请求参数记录到日志 */
			reqparam.put("cardNo", cardNo);
			reqparam.put("name", name);
			reqparam.put("cardId", cardId);

			if (!StringUtil.isEmpty(cardNo) && StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("身份证号码不符合规范");
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				logger.warn("{} 身份证号码不符合规范", prefix);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){
				logObj.setIncache("1");
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR.getRet_msg());
				return rets;
			}
			/** 构建请求参数 */
			JSONObject po = new JSONObject();
			po.put("member_id", member_id);// 配置参数
			po.put("terminal_id", terminal_id);// 配置参数
			po.put("id_name", name);// ds入参 必填
			po.put("id_no", cardNo);// ds入参 必填
			po.put("trans_id", trade_id);
			po.put("trade_date",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			// 20170515 houxiabin add 新颜银行卡认证 2.0.2 Begin
			po.put("product_type", "NORMAL");
			po.put("versions", "1.0.0");
			po.put("id_secret_type", "N");
			po.put("bank_card_secret_type", "N");
			po.put("bankcard_no", cardId);
			// 20170515 houxiabin add 新颜银行卡认证 2.0.2 End
			String base64str = com.wanda.credit.ds.client.xyan.utils.SecurityUtil
					.Base64Encode(po.toString());
			/** rsa加密 **/
			String data_content = RsaCodingUtil.encryptByPriPfxFile(base64str,
					cer_file_base_path + pfxname, pfxpwd);// 加密数据
			Map<String, String> Header = new HashMap<String, String>();
			Map<String, String> HeadPostParam = new HashMap<String, String>();
			HeadPostParam.put("member_id", member_id);
			HeadPostParam.put("terminal_id", terminal_id);
			HeadPostParam.put("data_type", "json");
			HeadPostParam.put("data_content", data_content);
			
			logger.info("{} 开始请求远程服务器... ", prefix);
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
//			String postString = HttpUtil.RequestForm(request_url, HeadPostParam);
			String postString = RequestHelper.doPost(request_url, HeadPostParam, Header, null,
					ContentType.create("application/json", Consts.UTF_8),false);
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logger.info("{} 请求返回:{}", prefix,postString);
			if(StringUtils.isEmpty(postString)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源调用失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}公安数据源厂商返回异常! ",prefix);
				return rets;
			}
			JSONObject result_obj = JSONObject.parseObject(postString);
			if(result_obj.getBoolean("success")){
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				JSONObject data = result_obj.getJSONObject("data");
				String code = data.getString("code");
				if("0".equals(code)){
					logger.info("{} 查得数据", prefix);
					resource_tag = Conts.TAG_TST_SUCCESS;
					retdata.putAll(data.getJSONObject("result_detail"));
				}else if("1".equals(code)){
					resource_tag = Conts.TAG_UNFOUND;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_JIAO_NO_RESULT);
					rets.put(Conts.KEY_RET_MSG, "查询未命中");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}else{
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "查询失败!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}
			}else{
				logger.info("{} 新颜交易失败:{}", prefix,result_obj.getString("errorMsg"));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "查询失败!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
            
            rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		} catch (Exception e) {
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(e));
			if (ExceptionUtil.isTimeoutException(e)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + e.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		} finally {
			//保存日志信息
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,false);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,false);
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		return rets;
	}

}
