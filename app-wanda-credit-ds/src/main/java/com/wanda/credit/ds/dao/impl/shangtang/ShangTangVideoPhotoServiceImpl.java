package com.wanda.credit.ds.dao.impl.shangtang;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.shangtang.ShangTang_videophoto_result;
import com.wanda.credit.ds.dao.domain.yidao.Yidao_bank_result;
import com.wanda.credit.ds.dao.iface.shangtang.IShangTangVideoPhotoService;
import com.wanda.credit.ds.dao.iface.yidao.IYidaoBankService;
@Service
@Transactional
public class ShangTangVideoPhotoServiceImpl  extends BaseServiceImpl<ShangTang_videophoto_result> implements IShangTangVideoPhotoService{
	private final  Logger logger = LoggerFactory.getLogger(ShangTangVideoPhotoServiceImpl.class);
	@Autowired
	private DaoService daoService;
	@Override
	public void batchSave(List<ShangTang_videophoto_result> result) {
		try {
			this.add(result);
		} catch (ServiceException e) {
			logger.error("批量保存失败，详细信息:{}" , e.getMessage());
			e.printStackTrace();
		}
	}
	@Override
	public String queryVideoFile(String trade_id,String param_code) {
		String sql ="select d.value key_value from cpdb_app.t_sys_req_param d where d.trade_id=? and d.key_code=? and rownum=1 ";
		String result = this.daoService.findOneBySql(sql, new Object[]{trade_id,param_code},String.class);
		return result;
	}
}
