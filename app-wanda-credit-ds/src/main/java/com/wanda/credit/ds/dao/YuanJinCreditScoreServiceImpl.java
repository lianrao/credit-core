package com.wanda.credit.ds.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.domain.yuanjin.YJ_CreditScoreVO;
import com.wanda.credit.ds.dao.iface.yuanjin.IYuanJinCreditScoreService;

@SuppressWarnings("unchecked")
@Service
@Transactional
public class YuanJinCreditScoreServiceImpl implements IYuanJinCreditScoreService {
	private final Logger logger = LoggerFactory.getLogger(YuanJinCreditScoreServiceImpl.class);

	@Autowired
	private DaoService daoService;

	@Override
	public void save(YJ_CreditScoreVO vo) {
	   if(vo.getCreate_date() == null){vo.setCreate_date(new Date());}
	   if(vo.getUpdate_date() == null){vo.setUpdate_date(new Date());}
	   daoService.create(vo);
	}	

	
	@Override
	public YJ_CreditScoreVO queryCached(String name, String cardNo,
			String crptedCardNo, int months) {
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("name", name);
		param.put("crptedCardNo", crptedCardNo);
		List<YJ_CreditScoreVO> list = daoService.query
				("From YJ_CreditScoreVO " 
						+ " a where a.name = :name and a.cardNo = :crptedCardNo"
						+ " and a.create_date >= add_months(SYSDATE," +Integer.valueOf("-"+months)
						+ ") order by a.create_date desc" , param, 0, 1);
		if(CollectionUtils.isNotEmpty(list)){
			return list.get(0);
		}
        return null;
	}

	public static void main(String[] args) {
		
	}
}
