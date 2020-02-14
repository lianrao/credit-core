package com.wanda.credit.ds.dao.impl.dmp;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.dmpCar.DMP_carBreak;
import com.wanda.credit.ds.dao.iface.dmp.ICarBreakMain;

@Service
@Transactional
public class CarBreakMainImpl extends BaseServiceImpl<DMP_carBreak> implements ICarBreakMain {
	private final  Logger logger = LoggerFactory.getLogger(CarBreakMainImpl.class);
	private static final long serialVersionUID = -3980145778504782396L;
	@Override
	public List<Map<String, Object>> queryCarCity(String hphm,String engine,String classa,int num,String car_source){
		String sql = "select city_code,nvl(d1.engine,':') engine,nvl(d1.classa,':') classa,d1.city_level from ("
				+"select m.city_code,case when m.isengine='1' and m.engineno='0' then ? "
				+" when m.isengine='1' and m.engineno<>'0' then SUBSTR(?,-m.engineno) when m.isengine='0' then '|' end engine,"
				+" case when m.isclass='1' and m.classno='0' then ? when m.isclass='1' and m.classno<>'0' then SUBSTR(?,-m.classno) "
				+" when m.isclass='0' then '|' end classa,m.city_level from CPDB_MK.T_MK_DMP_WEIZHANGDICT m "
				+" where m.car_code=SUBSTR(?,0,?) and m.car_status='01' and m.car_source=? order by m.city_level) d1 ";
		List<Map<String, Object>> result = this.daoService.getJdbcTemplate().queryForList(sql, engine,engine,classa,classa,hphm,num,car_source);
		return result;
	}
	@Override
	public synchronized void saveCarCity(String trade_id){
		try {
			//删除支持城市规则表历史数据
			logger.info(trade_id+" 插入支持城市表数据执行开始...");
			String sql0 ="DELETE FROM CPDB_MK.T_MK_DMP_WEIZHANGDICT D WHERE D.CAR_STATUS='02' ";
			this.daoService.getJdbcTemplate().update(sql0);
			//插入刚刚调用成功城市数据
			String sql ="INSERT INTO CPDB_MK.T_MK_DMP_WEIZHANGDICT(ID,CITY_CODE,CITY_NAME,PROVINCE,PROVINCE_NAME "
					+",CAR_CODE,CAR_RULE,ISENGINE,ENGINENO,ISCLASS,CLASSNO,CAR_STATUS,CITY_LEVEL,CAR_SOURCE) "
					+"SELECT ?,D.CITY_CODE,D.CITY_NAME,D.PROVINCE,D.PROVINCE_NAME "
					+",D.CAR_CODE,D.CAR_RULE,M.ENGINE,M.ENGINENO,M.CLASSA,M.CLASSNO,'01',D.CITY_LEVEL,D.CAR_SOURCE "
					+"  FROM CPDB_MK.T_MK_DMP_CARCITYDICT D,CPDB_DS.T_DS_DMP_WEIZHANGCITYRT M "
					+" WHERE D.CITY_CODE=M.CITY_CODE AND M.TRADE_ID=? ";
			this.daoService.getJdbcTemplate().update(sql,trade_id,trade_id);
			String sql1 ="SELECT COUNT(1) CNT FROM(SELECT D.CITY_CODE FROM CPDB_MK.T_MK_DMP_WEIZHANGDICT D "
					+" WHERE D.CAR_STATUS='01' GROUP BY D.CITY_CODE HAVING COUNT(1)>1) ";
			Integer result = this.daoService.findOneBySql(sql1, new Object[]{},Integer.class);
			if(result>0){
				//将之前数据变为历史数据
				String sql2 ="UPDATE CPDB_MK.T_MK_DMP_WEIZHANGDICT D SET D.CAR_STATUS='02' WHERE D.CAR_STATUS='01' "
						+" AND D.ID NOT IN(SELECT  ID FROM CPDB_MK.T_MK_DMP_WEIZHANGDICT D2 WHERE D2.CREATE_TIME="
						+ "(SELECT MAX(M.CREATE_TIME) FROM CPDB_MK.T_MK_DMP_WEIZHANGDICT M WHERE M.CAR_STATUS='01') AND ROWNUM=1) ";
				this.daoService.getJdbcTemplate().update(sql2);
			}
		}catch(Exception e){
			logger.error(trade_id+" 插入支持城市表数据到结果表失败！");
		}	
	}
	
	public List<Map<String, Object>> queryCarCity(String hphm,String cardrivenumber, String carcode){
		String sql = "SELECT CITYID,CARNUMBERPREFIX,CARCODELEN,CARENGINELEN, CAROWNERLEN, PROXYENABLE FROM CPDB_DS.T_DS_DMPCXY_CARIEG_QRY WHERE CARNUMBERPREFIX =?";
		List<Map<String, Object>> result = this.daoService.getJdbcTemplate().queryForList(sql, hphm);
		if (result.size()>0) {
			for (int i = 0; i <result.size(); i++) {
				Map<String, Object> map = result.get(i);
				int carResult = Integer.parseInt(map.get("CARCODELEN").toString());//车架号
				if (carcode.length()==0||carcode.equals("")||carResult==0) {//不需要车架号
					map.put("CARCODELEN", "");
				}else if (carResult==99||carcode.length()==carResult||carcode.length()<carResult) {
					map.put("CARCODELEN", carcode);
				}
				else {
					map.put("CARCODELEN", carcode.substring(carcode.length()-carResult, carcode.length()));
				}
				int carnumber = Integer.parseInt(map.get("CARENGINELEN").toString());//发动机号
				if (cardrivenumber.length()==0||cardrivenumber.equals("")||carnumber==0) {//不需要发动机号
					map.put("CARENGINELEN", "");
				}else if (carnumber==99||cardrivenumber.length()==carnumber||cardrivenumber.length()<carnumber) {
					map.put("CARENGINELEN", cardrivenumber);
				}else {
					map.put("CARENGINELEN", cardrivenumber.substring(cardrivenumber.length()-carnumber, cardrivenumber.length()));
				}
			}
		  return result;
		}
		
	
		return result;
	}
	public void delectCarCity() {
		String sql = "DELETE FROM CPDB_DS.T_DS_DMPCXY_CARIEG_QRY";
		this.daoService.getJdbcTemplate().update(sql);
	}
}
