package com.wanda.credit.ds.dao.impl.xiaoshi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.domain.xiaoshi.XiaoShi_faceInfo_result;
import com.wanda.credit.ds.dao.iface.xiaoshi.IXiaoshiFaceInfoService;
@Service
@Transactional
public class XiaoShiFaceInfoImpl  implements IXiaoshiFaceInfoService{
	private final  Logger logger = LoggerFactory.getLogger(XiaoShiFaceInfoImpl.class);
	@Autowired
	private DaoService daoService;
	@Override
	public void batchSave(XiaoShi_faceInfo_result result) {
		try {
			String sql = " insert into t_ds_head_faceinfo(id,dev_id,face_token,trade_id,confidence,timestamp, "
					+"age,age_confidence,gender,gender_confidence,photo_url,photo_id,sendflag,reserve1,reserve2,reserve3) "
					+" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			this.daoService.getJdbcTemplate().update(sql,
					result.getId(),
					result.getDev_id(),
					result.getFace_token(),
					result.getTrade_id(),
					result.getConfidence(),
					result.getTimestamp(),
					result.getAge(),
					result.getAge_confidence(),
					result.getGender(),
					result.getGender_confidence(),
					result.getPhoto_url(),
					result.getPhoto_id(),
					result.getSendflag(),
					result.getReserve1(),
					result.getReserve2(),
					result.getReserve3());
		} catch (Exception e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
}
