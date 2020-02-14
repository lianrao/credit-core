package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.JudgeDoc;
import com.wanda.credit.ds.dao.iface.huifa.inter.IJudgeDocService;

@Service
@Transactional
public class JudgeDocService extends BaseServiceImpl<JudgeDoc> implements IJudgeDocService{
	@Autowired
    private DaoService daoService;
	public void write(JudgeDoc judgeDoc){
		daoService.create(judgeDoc);
	}
}
