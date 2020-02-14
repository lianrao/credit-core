package com.wanda.credit.ds.dao.iface;

import java.text.ParseException;
import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.Guozt_badInfo_check_result;

public interface IGuoZTBadInfoService extends IBaseService<Guozt_badInfo_check_result>{
	/**
	 * 缓存查询一致性 
	 * @param enCardNo 
	 * @param score
	 * @throws ParseException 
	 */
	String inCached(String dsId,String name, String cardNo) throws ParseException;

	/**
	 * 查询犯罪记录
	 * @date 2016年6月28日 下午4:17:41
	 * @author ou.guohao
	 * @param cardNo 身份证号码
	 * @param name 姓名
	 * @return
	 */
	List<Guozt_badInfo_check_result> getBadInfoList(String tradeId);
	/**
	 * 保存
	 * */
	public void saveCertiResult(String trade_id,String ds_id,String name,String cardNo,String content);
	 /**
     * 查询万维数据
     */
    public boolean inCachedCount(String name,String cardNo,String dsid,int days);
    /**
     * 缓存万维数据
     */
    public String findCertiDetail(String ds_id,String name,String cardNo,int days);
}
