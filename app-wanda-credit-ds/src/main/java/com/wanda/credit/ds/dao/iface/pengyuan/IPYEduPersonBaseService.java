package com.wanda.credit.ds.dao.iface.pengyuan;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.Py_edu_college;
import com.wanda.credit.ds.dao.domain.Py_edu_degree;
import com.wanda.credit.ds.dao.domain.Py_edu_personBase;

public interface IPYEduPersonBaseService extends IBaseService<Py_edu_personBase>{
	/**
	 * 查询是否缓存数据
	 * @param name
	 * @param cardNo
	 * @return
	 */
	public boolean inCached(String name,String cardNo);
	/**
	 * 查询缓存个人教育数据
	 * @param name
	 * @param cardNo
	 * @return
	 */
	public Py_edu_personBase queryPersonBase(String name,String cardNo);
	/**
	 * 查询缓存最高学历数据
	 * @param trade_id
	 * @return
	 */
	public Py_edu_degree queryPersonDegree(String trade_id);
	/**
	 * 查询缓存学院数据
	 * @param trade_id
	 * @return
	 */
	public Py_edu_college queryPersonCollege(String trade_id);
	/**
	 * 查询规定时间内是否有缓存记录
	 * @param name
	 * @param crptedCardNo
	 * @return
	 */
	public boolean inCachedMonth(String name, String crptedCardNo,int num);
	/**
	 * 删除之前缓存的数据并保存最新数据
	 * @param personBase
	 */
	public void saveNewPerBase(Py_edu_personBase personBase);
	/**
	 * 删除之前缓存的数据并保存最新数据
	 * @param personBase
	 * @param degree
	 * @param college
	 */
	public void saveNewPerBase(Py_edu_personBase personBase,
			Py_edu_degree degree, Py_edu_college college) throws Exception;
}
