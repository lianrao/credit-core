package com.wanda.credit.ds.client.huifa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.huifa.BillDetails;
import com.wanda.credit.ds.dao.domain.huifa.BillInputInfo;
import com.wanda.credit.ds.dao.iface.huifa.inter.IBillDetailsService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IBillInputInfoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_huifaBillQuery")
public class BillDataSourceRequestor extends BaseDataSourceRequestor implements IDataSourceRequestor{
	private Logger logger = LoggerFactory.getLogger(BillDataSourceRequestor.class);
	@Autowired
	private DaoService daoService;
	@Autowired
	private IBillDetailsService billDetailsService;
	@Autowired
	private IBillInputInfoService IBillInputInfoService;
	private String url;
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String,Object> rets = null;
		try {
			rets = new HashMap<String, Object>();
			Map<String,Object> respMap = new HashMap<String, Object>();
			String pg =  ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String pz =  ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			Map<String,String> params_in = new HashMap<String, String>();
			params_in.put("pg", pg);
			params_in.put("pz", pz);
			String content = RequestHelper.doGet(url, params_in, true);
			if(!"".equals(content)&&content!=null){
				respMap = new ObjectMapper().readValue(content, Map.class);
			}
			String success=null;
			String message=null;
			String totalnumber=null;
			String totalmoney=null;
			String moneynow=null;
			if(respMap.containsKey("success")){
				 success = (String) respMap.get("success");
			}
			if(respMap.containsKey("message")){
				 message = (String) respMap.get("message");
			}
			if(respMap.containsKey("totalnumber")){
				 totalnumber = (String) respMap.get("totalnumber").toString();
			}
			if(respMap.containsKey("totalmoney")){
				 totalmoney = (String) respMap.get("totalmoney");
			}
			if(respMap.containsKey("moneynow")){
				 moneynow = (String) respMap.get("moneynow");
			}
			BillInputInfo billInputInfo = new BillInputInfo(trade_id,pg,pz,totalnumber,totalmoney,moneynow,success,message,content);
			IBillInputInfoService.write(billInputInfo);
			String sql = "SELECT ID FROM T_DS_HF_BILLINPUTINFO WHERE TRADE_ID=?";
			String refid = daoService.getJdbcTemplate().queryForObject(sql, new Object[]{trade_id}, String.class);
			List<Map<String,Object>> listMap = null;
			if(respMap.containsKey("models")){
			    listMap =  respMap.get("models")==null?null:(List<Map<String, Object>>)respMap.get("models");
			}
			if("s".equals(success)&&listMap!=null&&listMap.size()>0){
				for (Map<String, Object> map : listMap) {
					BillDetails billDetails = new BillDetails();
					billDetails.setLogid(map.get("logid")!=null?(Integer)map.get("logid"):null);
					billDetails.setKeyword(map.get("keyword")!=null?map.get("keyword").toString():"");
					billDetails.setPaymoney(map.get("paymoney")!=null?map.get("paymoney").toString():"");
					billDetails.setRemark(map.get("remark")!=null?map.get("remark").toString():"");
					billDetails.setPosttime(map.get("posttime")!=null?map.get("posttime").toString():"");
					billDetails.setTrade_id(trade_id);
					billDetails.setRefid(refid);
					billDetailsService.write(billDetails);
				}
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, respMap);
				rets.put(Conts.KEY_RET_MSG, "交易成功!");
			}else if("e".equals(success)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "汇法网计费查询失败! 失败描述:"+ message);
			}else if(listMap == null || listMap.size() == 0){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "汇法网计费查询失败! 失败描述：汇法网计费查询返回数据为空:"+message);
			}
		} catch (Exception ex) {
			rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_DS_HUIFA4_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG,"汇法网计费查询异常! 详细信息:" + ex.getMessage());
			logger.error(prefix+" 汇法网计费查询返回异常：{}", ex);	
		}
		return rets;
	}
	 public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
}
