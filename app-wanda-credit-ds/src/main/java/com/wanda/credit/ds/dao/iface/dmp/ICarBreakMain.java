package com.wanda.credit.ds.dao.iface.dmp;

import java.util.List;
import java.util.Map;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.dmpCar.DMP_carBreak;
 
public interface ICarBreakMain  extends IBaseService<DMP_carBreak>{
	public List<Map<String, Object>> queryCarCity(String hphm,String engine,String classa,int num,String car_source);
	public List<Map<String, Object>> queryCarCity(String hphm,String cardrivenumber, String carcode);
	public void delectCarCity();
	public void saveCarCity(String trade_id);
}
