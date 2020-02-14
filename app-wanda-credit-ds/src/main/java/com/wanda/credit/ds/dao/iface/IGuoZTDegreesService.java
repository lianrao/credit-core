package com.wanda.credit.ds.dao.iface;

import java.text.ParseException;
import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.Guozt_degrees_check_result;

public interface IGuoZTDegreesService extends IBaseService<Guozt_degrees_check_result> {
	/**
	 * 学历缓存
	 * 
	 * @date 2016年7月26日 下午3:51:17
	 * @author nan.liu
	 * @param dsId
	 * @param name
	 * @param cardNo
	 * @return
	 * @throws ParseException
	 */
	public boolean inCached(String name, String cardNo);
	
	/**
	 * 获取学历
	 * @date 2016年7月26日 下午4:06:09
	 * @author nan.liu
	 * @param tradeId
	 * @return
	 */
	Guozt_degrees_check_result getDegreesByTradeId(String name,String cardNo);
	
	/**
	 * 学历缓存查询
	 * 
	 * @date 2016年7月26日 下午3:51:17
	 * @author nan.liu
	 * @param dsId
	 * @param name
	 * @param cardNo
	 * @return
	 * @throws ParseException
	 */
	public boolean inCachedDate(String name, String cardNo,int num);
	
	/**
	 * 按缓存时间获取学历
	 * @date 2016年7月26日 下午4:06:09
	 * @author nan.liu
	 * @param tradeId
	 * @return
	 */
	Guozt_degrees_check_result getDegreesByTradeIdDate(String name,String cardNo,int num);
	
	/**
	 * 按缓存时间获取学历
	 * @date 2016年7月26日 下午4:06:09
	 * @author nan.liu
	 * @param tradeId
	 * @return
	 */
	Guozt_degrees_check_result getDegreesByTradeIdEver(String name,String cardNo);
	
	/**
	 * 学历缓存
	 * 
	 * @date 2016年7月26日 下午3:51:17
	 * @author nan.liu
	 * @param dsId
	 * @param name
	 * @param cardNo
	 * @return
	 * @throws ParseException
	 */
	String inCached(String dsId, String name, String cardNo) throws ParseException;
	
	/**
	 * 获取学历
	 * @date 2016年7月26日 下午4:06:09
	 * @author ou.guohao
	 * @param tradeId
	 * @return
	 */
	Guozt_degrees_check_result getDegreesByTradeId(String tradeId);

public	void update(String name, String cardNo, Guozt_degrees_check_result retdegrees);

}
