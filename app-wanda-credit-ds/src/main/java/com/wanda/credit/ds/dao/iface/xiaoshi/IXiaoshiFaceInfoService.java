package com.wanda.credit.ds.dao.iface.xiaoshi;


import java.util.List;
import com.wanda.credit.ds.dao.domain.xiaoshi.XiaoShi_faceInfo_result;

public interface IXiaoshiFaceInfoService{
	/**
	 * 批量保存卡号查询信息
	 * @param score
	 */
	void batchSave(XiaoShi_faceInfo_result result);
}
