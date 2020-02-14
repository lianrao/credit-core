package com.wanda.credit.ds.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.domain.qianhai.BlackListVO_2_0;
import com.wanda.credit.ds.dao.iface.IBlackListService_2_0;

@SuppressWarnings("unchecked")
@Service
@Transactional
public class BlackListServiceImpl_2_0 implements IBlackListService_2_0 {
	private final Logger logger = LoggerFactory.getLogger(BlackListServiceImpl_2_0.class);

	@Autowired
	private DaoService daoService;

	/**
	 * @throws Exception 
	 * @blacklistJsn 结构: {batchNo:'001',record:[{idNo:'zhsan',idType:'330...'}]}
	 * */
	public List<BlackListVO_2_0> addOneBlackList(String trade_id,JSONObject blackListJsn,
			Map<String,Object> ctx) throws Exception {
		if (blackListJsn == null || !blackListJsn.containsKey("records"))
			return null;
				
		 JSONArray datas = (JSONArray)blackListJsn.get("records");
		
		if(datas == null || datas.size() == 0) return null;
		
		List<BlackListVO_2_0> retrnls = new ArrayList<BlackListVO_2_0>();
		BlackListVO_2_0 vo;
		for (Object data : datas) {
			JSONObject jsnObj = (JSONObject)data;
			if(jsnObj == null)continue;			
			else if("E000000".equals(jsnObj.get("erCode"))){
	            vo = (BlackListVO_2_0)JSONObject.toBean(jsnObj, BlackListVO_2_0.class);		
                vo.setIps((String)ctx.get("ips"));
                vo.setMoblieNos((String)ctx.get("cryptedMobiles"));
                vo.setCardIds((String)ctx.get("cryptedCardIds"));
                vo.setCardNo((String)ctx.get("cryptedIdNo"));
                vo.setTrade_id(trade_id);
                retrnls.add(vo);
			}else if("E000996".equals(jsnObj.get("erCode"))){
				//unfound
				ctx.put(Conts.KEY_RET_TAG, Conts.TAG_UNFOUND);
				logger.warn("{} 未查询到qh黑名单信息",trade_id);
				return null;
			}else{
				//other error
				ctx.put(Conts.KEY_RET_TAG, Conts.TAG_SYS_ERROR);
				ctx.put("erMsg", jsnObj.get("erMsg") );
				logger.error("{} qh黑名单信息查询失败 {}",trade_id,jsnObj.get("erMsg"));
				return null;
			}
		}
        /**批量提交*/
		if(CollectionUtils.isNotEmpty(retrnls)){
			this.daoService.create(retrnls);
		}
		return retrnls;
	} 
	
		public static void main(String[] args) {
			String x ="{\"cardNo\":\"cardNo\" ,\"idType\":\"idType\",\"name\":\"name\", \"seqNo\":\"seqno\" ,\"sourceId\":\"sourceid\",\"rskScore\":\"rskscore\" ,\"rskMark\":\"rskmak\",\"dataBuildTime\":\"dataBuildTime\",\"dataStatus\":\"dtastatus\",\"erCode\":\"ercode\",\"erMsg\":\"ermsg\"}";
            JSONObject a =JSONObject.fromObject(x);
            System.out.println(">>"+a.get("cardnO"));
		}
}
