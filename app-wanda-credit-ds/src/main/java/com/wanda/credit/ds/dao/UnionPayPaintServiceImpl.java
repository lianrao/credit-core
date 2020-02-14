package com.wanda.credit.ds.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.dao.iface.IUnionPayPaintService;

/**
 * @Title: 返回处理
 * @Description: 银联用户画像数据保存
 * @author chenglin.xiao@99bill.com
 * @date 2015年9月21日 下午09:43:12
 * @version V1.0
 */
@Service
@Transactional
public class UnionPayPaintServiceImpl implements IUnionPayPaintService {
	private final Logger logger = LoggerFactory.getLogger(UnionPayPaintServiceImpl.class);
	@Autowired
	private DaoService daoService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	@SuppressWarnings("unchecked")
	@Override
	public void save(String trade_id, String result, Map<String, String> params) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> respMap = null;
		Map<String, Object> dataMap = null;
//		Map<String, Object> resultMap = null;
		// Map<String, String> quotaMap = null;
		String sql = "";
		// add by wangjing个人/企业标识：0-个人，1-企业
		String bc_flag = params.get("bc_flag");// add by wangjing
		try {
			logger.info("{} 银联用户画像数据保存：[{}] \n[{}]", prefix, result, JSONObject.toJSONString(params));
			
			String cardId_encrypt = synchExecutorService.encrypt(params.get("card"));
			respMap = new ObjectMapper().readValue(result, Map.class);// 转成map
			dataMap = (Map<String, Object>) respMap.get("data");
//			resultMap = (Map<String, Object>) dataMap.get("result");
//			sql = "INSERT INTO T_DS_YL_PAINT_RESULT VALUES (sys_guid(),?,?,?,?,?,SYSDATE,'0',?,'','','','')";
			if (result != null && result.length() > 4000) {
				result = result.substring(0, 3000);
			}
			logger.info("{} 需要落库的result长度为 {}" , prefix ,result.length());
			logger.info("{} 需要落库的trade_id为 {}" , prefix ,trade_id);
			logger.info("{} 需要落库的cardId_encrypt为 {}" , prefix ,cardId_encrypt);
			logger.info("{} 返回状态  {}" , prefix ,respMap.get("status"));
			sql = "INSERT INTO CPDB_DS.T_DS_YL_PAINT_RESULT(ID,TRADE_ID,CARD,VALIDATED,ACTIVE,CONTENT,CREATE_DATE,BC_FLAG,P_NAME) VALUES (sys_guid(),?,?,?,?,?,SYSDATE,'0',?)";
//			this.daoService.getJdbcTemplate().update(sql, trade_id, cardId_encrypt, dataMap.get("validate"),
//					"-", result, params.get("name"));
			this.daoService.getJdbcTemplate().update(sql, trade_id, cardId_encrypt, "1",
					"-", result, params.get("name"));
			if(!"2000".equals(String.valueOf(respMap.get("status"))))
				return;
			
			//quotaMap = (Map<String, String>) resultMap.get("quota");
			//Iterator<Entry<String, String>> iter = quotaMap.entrySet().iterator();
			
			Iterator<Entry<String, Object>> iter = dataMap.entrySet().iterator();
			
			while (iter.hasNext()) {
				//Entry<String, String> entry = iter.next();
				Entry<String, Object> entry = iter.next();
				String key = entry.getKey();
				//String value = entry.getValue();
				String[] parentIds = this.queryForDict(key, bc_flag);
				sql = "INSERT INTO CPDB_DS.T_DS_YL_PAINT_RESULT_DETAIL VALUES(sys_guid(),(SELECT MAX(ID) FROM CPDB_DS.T_DS_YL_PAINT_RESULT WHERE TRADE_ID=? and card=?),?,?,SYSDATE, ?)";
				if (parentIds.length == 1)
					this.daoService.getJdbcTemplate().update(sql, trade_id, cardId_encrypt, parentIds[0], entry.getValue(), bc_flag);
				else if (parentIds.length > 1) {
					String[] values = null;
					/*
					 * if(value.indexOf("_")>=0) values = value.split(";"); else
					 * values = value.split(","); if("S0501".equals(key)){
					 * values = value.split(";"); }
					 */
					String value = StringUtil.stringNoNull(entry.getValue());
					if (value.contains(";") && !value.contains(",")) {
						values = value.split(";");
					} else if (value.contains(",") && !value.contains(";")) {
						values = value.split(",");
						// add by wangjing
					} else if (!value.contains(",") && !value.contains(";")) {
						values = new String[1];
						values[0] = value;
					}
					if (values != null && values.length > 0) {
						for (int i = 0; i < values.length; i++) {							
							String v = "";
							if(!"S0484".equals(key)){
								v = values[i].indexOf("_") >= 0 ? values[i].substring(values[i].indexOf("_") + 1,
										values[i].length()) : values[i];
							}else{
								v = values[i];
							}
							this.daoService.getJdbcTemplate().update(sql, trade_id, cardId_encrypt, parentIds[i], v, bc_flag);
						}
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("{} 银联用户画像数据保存时异常：{}", prefix, ex.getMessage());
		}
	}

	public String[] queryForDict(String itemcode, String bc_flag) {
		String[] rets = null;
		String sql = "SELECT ID,ITEMCODE FROM CPDB_DS.T_DS_YL_PAINT_DICT WHERE ITEMCODE  like ? and bc_flag = '" + bc_flag
				+ "'";
		List<Map<String, Object>> list = this.daoService.getJdbcTemplate().queryForList(sql, "%" + itemcode + "%");
		if (list != null && list.size() > 1) {
			Collections.sort(list, new MyComparator());
		}
		rets = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> itemMap = list.get(i);
			rets[i] = itemMap.get("ID").toString();
		}
		return rets;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveMerchant(String trade_id, String result, Map<String, String> params) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> respMap = null;
		Map<String, String> dataMap = null;
		Map<String, String> resultMap = null;
		String sql = "";
		// 个人/企业标识：0-个人，1-企业
		String bc_flag = params.get("bc_flag");// add by wangjing
		try {
//			sql = "select CREATE_MONTH,CREATE_YEAR from (select to_char(CREATE_DATE,'mm') as CREATE_MONTH,to_char(CREATE_DATE,'yyyy') as CREATE_YEAR from T_DS_YL_PAINT_RESULT where M_ID = '" +mid+ "' order by CREATE_DATE desc) where rownum = 1";
//			List<Map<String, Object>> list = this.daoService.getJdbcTemplate().queryForList(sql);
//			String createMonth = "";
//			String createYear = "";
//			for (Map<String, Object> map : list) {
//				createMonth = map.get("CREATE_MONTH").toString();
//				createYear = map.get("CREATE_YEAR").toString();
//			}
//			Date curDate = new Date(System.currentTimeMillis());
//			String curMonth = curDate.getMonth()+1+"";
//			String curYear = curDate.getYear()+"";
//			int yearDs = (Integer.valueOf(createYear)-1900)-Integer.valueOf(curYear);
//			int monDs = Integer.valueOf(createMonth)-Integer.valueOf(curMonth);
//			if(yearDs==0 && monDs==0){
//				logger.info("{}本月已查询过该商户的银联画像，不再重复落地数据！", prefix);
//				return;
//			}
			
			respMap = new ObjectMapper().readValue(result, Map.class);// 转成map
			Object resCode = respMap.get("code");
			Object resStatus = respMap.get("status");
			dataMap = (Map<String, String>) respMap.get("data");
			

			// card和active字段为非空字段，商户画像时分别存为0和9；
			sql = "INSERT INTO CPDB_DS.T_DS_YL_PAINT_RESULT(id,trade_id,card,validated,active,content,create_date,bc_flag,p_name,m_id,m_name,reg_no,legal_name) VALUES (sys_guid(),?,'0',?,'9',?,SYSDATE,?,'',?,?,?,?)";
			Object validated = "0";
			if ("200".equals(resCode) && "2000".equals(resStatus) && dataMap != null) {
				validated = "1";
			}
			if (result != null && result.length() >=4000) {
				result = result.substring(0, 3000);
			}
			this.daoService.getJdbcTemplate().update(sql, trade_id, validated , result, bc_flag,
					params.get("mid"), params.get("mname"), params.get("regNo"), params.get("legalName"));

			if(!("200".equals(resCode) && "2000".equals(resStatus)))
				return;
				
//			resultMap = (Map<String, String>) dataMap.get("result");
			resultMap = dataMap;
			Iterator<Entry<String, String>> iter = resultMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				String key = entry.getKey();
				String value = entry.getValue();
				String[] parentIds = this.queryForDict(key, bc_flag);
				sql = "INSERT INTO CPDB_DS.T_DS_YL_PAINT_RESULT_DETAIL VALUES(sys_guid(),(SELECT ID FROM CPDB_DS.T_DS_YL_PAINT_RESULT WHERE TRADE_ID=?),?,?,SYSDATE,?)";
				if (parentIds.length == 1)
					this.daoService.getJdbcTemplate().update(sql, trade_id, parentIds[0], entry.getValue(), bc_flag);
				else if (parentIds.length > 1) {
					String[] values = null;
					if (value.contains(";") && !value.contains(",")) {
						values = value.split(";");
					} else if (value.contains(",") && !value.contains(";")) {
						values = value.split(",");
					} else if (!value.contains(",") && !value.contains(";")) {
						values = new String[1];
						values[0] = value;
					}
					if (values != null && values.length > 0) {
						for (int i = 0; i < values.length; i++) {
							String v = values[i].indexOf("_") >= 0 ? values[i].substring(values[i].indexOf("_") + 1,
									values[i].length()) : values[i];
							this.daoService.getJdbcTemplate().update(sql, trade_id, parentIds[i], v, bc_flag);
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("{} 银联商户画像数据保存时异常：{}", prefix, ex.getMessage());
		}
	}

	public String[] queryItemcodes(String bc_flag) {
		String[] rets = null;
		String sql = "SELECT ID,ITEMCODE FROM CPDB_DS.T_DS_YL_PAINT_DICT WHERE bc_flag = '" + bc_flag + "'";
		List<Map<String, Object>> list = this.daoService.getJdbcTemplate().queryForList(sql);
		rets = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> itemMap = list.get(i);
			rets[i] = itemMap.get("ITEMCODE").toString();
		}
		return rets;
	}

	public TreeMap<String,Object> queryLastResult(String trade_id, String resultId ) {
		final String prefix = trade_id + " "  + resultId;
		
		TreeMap<String,Object> paintData = null;

		try {
			
			StringBuffer sqlBuffer = new StringBuffer();
			
			sqlBuffer.append("select r.value as VALUE, d.itemcode as ITEMCODE  from CPDB_DS.t_ds_yl_paint_result_detail r ")
				.append("left join ").append("CPDB_DS.t_ds_yl_paint_dict d on (d.id = r.itemid) ")
				.append("left join ").append("CPDB_DS.t_ds_yl_paint_result p on (p.id = r.refid)")
				.append(" where d.bc_flag = '1'  and p.ID = '").append(resultId)
				.append("' ").append(" and r.VALUE != 'NA'");
			
			List<Map<String, Object>> queryForList = this.daoService.getJdbcTemplate().queryForList(sqlBuffer.toString());
			
			if (queryForList != null && queryForList.size() > 0) {
				paintData = new TreeMap<String, Object>();
				for (Map<String, Object> map : queryForList) {
					if(map.containsKey("ITEMCODE") && map.containsKey("VALUE")){
						paintData.put(map.get("ITEMCODE").toString(), map.get("VALUE").toString());
					}
					
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.info("{} 从缓存获取银联商户画像异常 " + e.getMessage() ,prefix);
		}

		return paintData;
	}


	public String isExistCurentMonthRecord(String trade_id, String mid) {
		final String prefix = trade_id + " " + mid;
		
		String sql = null;
		String paintId = null;
		
		Map<String, Object> queryForMap = null;

		try {
			sql = "select CREATE_MONTH,CREATE_YEAR,ID from (select to_char(CREATE_DATE,'mm') as CREATE_MONTH,to_char(CREATE_DATE,'yyyy') as CREATE_YEAR,ID as ID from CPDB_DS.T_DS_YL_PAINT_RESULT p where p.M_ID = '" +mid+ "' and p.VALIDATED = '1' order by CREATE_DATE desc) where rownum = 1";

			List<Map<String, Object>> queryForList = this.daoService.getJdbcTemplate().queryForList(sql);
			
			if (queryForList != null && queryForList.size() > 0) {
				queryForMap = queryForList.get(0);
			}else{
				logger.info("{} 本地缓存中没有该mid对应的数据" ,prefix);
				return paintId;
			}
			
			if(!queryForMap.containsKey("CREATE_MONTH") || !queryForMap.containsKey("CREATE_YEAR")){
				logger.info("{} 没有对应的交易时间信息" , prefix);
				return paintId;
			}
			
			int crtMon = Integer.parseInt(queryForMap.get("CREATE_MONTH").toString());
			int crtYear = Integer.parseInt(queryForMap.get("CREATE_YEAR").toString());
			
			Calendar cal = Calendar.getInstance();
		    int month = cal.get(Calendar.MONTH) + 1;
		    int year = cal.get(Calendar.YEAR);
		    
		    if (month == crtMon && year == crtYear ) {
				paintId = queryForMap.get("ID").toString();
				logger.info("{} 存在对应的商户画像数据，对应的ID为" + paintId ,prefix);
			}
		    
		    

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("{} 判断本地是否缓存一个月银联商户画像异常：{}", prefix, ex.getMessage());
		}
		
		return paintId;
	}
	
	
	public TreeMap<String,Object> queryLastResultBak(String trade_id, String mid ,String[] indexes) {
		final String prefix = trade_id + " " + mid;
		
		TreeMap<String,Object> retdata = new TreeMap<String, Object>();

		try {
			if(indexes == null || indexes.length < 1){
				logger.info("{} 从缓存获取银联商户画像数据，传入接口查询编号为空" + prefix);
				return null;
			}
			
			retdata = new TreeMap<String, Object>();
			
			for (int i = 0; i < indexes.length; i++) {
				
				StringBuffer sqlBuffer = new StringBuffer();
				
				sqlBuffer.append("select distinct(r.value) as VALUE, d.itemcode as ITEMCODE  from CPDB_DS.t_ds_yl_paint_result_detail r ")
				.append("left join ").append("CPDB_DS.t_ds_yl_paint_dict d on (d.id = r.itemid) ")
				.append("left join ").append("CPDB_DS.t_ds_yl_paint_result p on (p.id = r.refid)")
				.append(" where d.bc_flag = '1'  and p.m_id = '").append(mid)
				.append("'");
//				.append("' and d.itemcode like '").append(indexes[i])
//				.append("%'");
				
				logger.info("{} sql 为" + sqlBuffer.toString());
				
				List<Map<String, Object>> queryForList = this.daoService.getJdbcTemplate().queryForList(sqlBuffer.toString());
				
				Map<String,String> paintData = new TreeMap<String, String>();
				Map<Object,Object> paintData2 = new HashMap<Object,Object>();
				if (queryForList != null && queryForList.size() > 0) {
					
					for (Map<String, Object> map : queryForList) {
						paintData.put(map.get("ITEMCODE").toString(),map.get("VALUE").toString());
						paintData2.put(map.get("ITEMCODE"),map.get("VALUE"));
					}
				}
				
				retdata.put(indexes[i], JSONObject.toJSONString(paintData2));
				
				
				
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.info("{} 从缓存获取银联商户画像异常 " + e.getMessage() ,prefix);
		}

		return retdata;
	}

	
	public Map<String, String> isExistPerPaintData(String trade_id, String cardId ,int period) {
		final String prefix = trade_id;
		
		String sql = null;
		Map<String, String> paintId = null;
		
		Map<String, Object> queryForMap = null;

		try {

			String encCardId = synchExecutorService.encrypt(cardId);
			Date nowDate = new Date();
			
			Date addDays = DateUtil.addDays(nowDate, period);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String now = dateFormat.format(nowDate);
			String add = dateFormat.format(addDays);
			
			sql = "select ID,CONTENT,ACTIVE,VALIDATED from (select r.id as ID,r.content as CONTENT,r.active as ACTIVE,r.validated as VALIDATED  from CPDB_DS.t_ds_yl_paint_result r where r.validated = '1' and r.card = '"+ encCardId +"' and to_char(r.create_date,'yyyy-MM-dd') between '"+add+"' and  '"+now+"' order by r.create_date desc) where rownum = 1";

			List<Map<String, Object>> queryForList = this.daoService.getJdbcTemplate().queryForList(sql);
			
			if (queryForList != null && queryForList.size() > 0) {
				queryForMap = queryForList.get(0);
			}else{
				logger.info("{} 本地缓存中没有卡号对应的数据" ,prefix);
				return paintId;
			}
			
			if (!queryForMap.containsKey("ID")) {
				logger.info("{} 没有对应的交易时间信息" , prefix);
				return paintId;
			}
			
			Object idObj = queryForMap.get("ID");
			
			if (!StringUtil.isEmpty(idObj)) {
				paintId = new HashMap<String, String>();
				paintId.put("ID", idObj.toString());
				paintId.put("CONTENT", queryForMap.get("CONTENT").toString());
				paintId.put("ACTIVE", queryForMap.get("ACTIVE").toString());
				paintId.put("VALIDATED", queryForMap.get("VALIDATED").toString());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("{} 判断本地是否缓存半个月银联商户画像异常：{}", prefix, ex.getMessage());
		}
		
		return paintId;
	}

	
	public Map<String, Object> queryCachePerCacheData(String trade_id, String resultId) {

		final String prefix = trade_id + " "  + resultId;
		
		TreeMap<String,Object> paintData = null;

		try {
			
			StringBuffer sqlBuffer = new StringBuffer();
			
			sqlBuffer.append("select r.value as VALUE, d.itemcode as ITEMCODE  from CPDB_DS.t_ds_yl_paint_result_detail r ")
				.append("left join ").append("CPDB_DS.t_ds_yl_paint_dict d on (d.id = r.itemid) ")
				.append("left join ").append("CPDB_DS.t_ds_yl_paint_result p on (p.id = r.refid)")
				.append(" where d.bc_flag = '0'  and p.ID = '").append(resultId)
				.append("' ").append(" and r.VALUE != 'NA'");
			
			List<Map<String, Object>> queryForList = this.daoService.getJdbcTemplate().queryForList(sqlBuffer.toString());
			
			if (queryForList != null && queryForList.size() > 0) {
				paintData = new TreeMap<String, Object>();
				for (Map<String, Object> map : queryForList) {
					if(map.containsKey("ITEMCODE") && map.containsKey("VALUE")){
						paintData.put(map.get("ITEMCODE").toString(), map.get("VALUE").toString());
					}
					
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.info("{} 从缓存获取银联个人画像异常 " + e.getMessage() ,prefix);
		}

		return paintData;
	
	}
	
	@Override
	public void saveNew(String trade_id, String result, Map<String, String> params) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		try {
			logger.info("{} 银联消费画像数据保存：[{}] \n[{}]", prefix, result, JSONObject.toJSONString(params));
			
//			String cardId_encrypt = params.get("card");
			String cardId_encrypt = synchExecutorService.encrypt(params.get("card"));
			Map<Integer, String> results = new HashMap<Integer, String>();
			if (result != null) {
				int i = 0;
				int n = result.length() / 3000;
				if(n<1)
					results.put(1, result);
				else{
					int idx = 0;
					for ( ; i < n; i++) {
						idx = (i+1) *3000;
						results.put(i+1, result.substring(i * 3000, idx));
						
					}
					if(idx <= result.length()-1)
						results.put(i+1, result.substring(idx, result.length()));
				}
			}
			
			logger.info("{} 需要落库的result长度为 {}" , prefix ,result.length());
			logger.info("{} 需要落库的trade_id为 {}" , prefix ,trade_id);
			logger.info("{} 需要落库的cardId_encrypt为 {}" , prefix ,cardId_encrypt);
			logger.info("{} 需要落库的name为 {}" , prefix ,params.get("name"));
			String sql = "INSERT INTO CPDB_DS.T_DS_YL_PAINT_RESULT"
					+ "(ID,TRADE_ID,CARD,VALIDATED,ACTIVE,CONTENT,CREATE_DATE,BC_FLAG,P_NAME,SNO) "
					+ "VALUES (sys_guid(),?,?,?,?,?,SYSDATE,'0',?,?)";
			
			Iterator<Entry<Integer, String>> iter = results.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Integer, String> entry = iter.next();
				this.daoService.getJdbcTemplate().update(sql, trade_id, cardId_encrypt, "1",
						"-", entry.getValue(), params.get("name"), entry.getKey());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("{} 银联用户画像数据保存时异常：{}", prefix, ex.getMessage());
		}
	}
	public Map<String, String> isExistPerPaintDataNew(String trade_id, String cardId ,int period) {
		final String prefix = trade_id;
		
		String sql = null;
		Map<String, String> paintId = null;
		
		Map<String, Object> queryForMap = null;

		try {
//			String encCardId = cardId;
			String encCardId = synchExecutorService.encrypt(cardId);
			Date nowDate = new Date();
			
			Date addDays = DateUtil.addDays(nowDate, period);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String now = dateFormat.format(nowDate);
			String add = dateFormat.format(addDays);
			
			sql = "select trade_id,ID,CONTENT,ACTIVE,VALIDATED,sno from "
					+ "(select r.trade_id as trade_id, r.id as ID,r.content as CONTENT,r.active as ACTIVE,r.validated as VALIDATED, r.sno as sno  "
					+ "from CPDB_DS.t_ds_yl_paint_result r "
					+ "where r.sno is not null and r.validated = '1' and r.card = '"+ encCardId +
					"' and to_char(r.create_date,'yyyy-MM-dd') between '"+add+"' and  '"
					+now+"' order by r.create_date desc) where rownum = 1";

			List<Map<String, Object>> queryForList = this.daoService.getJdbcTemplate().queryForList(sql);
			List<Map<String, Object>> queryForList2 = null;
			if (queryForList != null && queryForList.size() > 0) {
				queryForMap = queryForList.get(0);
				
				sql =  "select r.trade_id as trade_id,r.content as CONTENT, r.sno as sno  "
					 + "from CPDB_DS.t_ds_yl_paint_result r "
					 + "where r.TRADE_ID = '"+ queryForMap.get("TRADE_ID")+"' "
					 + "order by sno";
				
				queryForList2 = this.daoService.getJdbcTemplate().queryForList(sql);
			}else{
				logger.info("{} 本地缓存中没有卡号对应的数据" ,prefix);
				return paintId;
			}
			
			if (!queryForMap.containsKey("ID")) {
				logger.info("{} 没有对应的交易时间信息" , prefix);
				return paintId;
			}
			
			Object idObj = queryForMap.get("ID");
			
			if (!StringUtil.isEmpty(idObj)) {
				paintId = new HashMap<String, String>();
				paintId.put("ID", idObj.toString());
				String content = "";
				for (int i = 0; i < queryForList2.size(); i++) {
					Map<String, Object> map = queryForList2.get(i);
					content = content + String.valueOf(map.get("CONTENT"));
				}
				paintId.put("CONTENT", content);
				paintId.put("ACTIVE", queryForMap.get("ACTIVE").toString());
				paintId.put("VALIDATED", queryForMap.get("VALIDATED").toString());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("{} 判断本地是否缓存银联消费画像异常：{}", prefix, ex.getMessage());
		}
		
		return paintId;
	}
	
}

class MyComparator implements Comparator<Map<String, Object>> {
	@Override
	public int compare(Map<String, Object> o1, Map<String, Object> o2) {
		String m1 = (String) o1.get("ITEMCODE");
		String m2 = (String) o2.get("ITEMCODE");
		return Integer.parseInt(m1.substring(6)) - Integer.parseInt(m2.substring(6));
	}
}
