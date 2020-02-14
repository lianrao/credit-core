package com.wanda.credit.ds.dao.iface.impl.juxinli.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.report.UserInfoCheckPojo;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLRepUserInfoCheckService;
@Service
@Transactional
public class JXLRepUserInfoCheckServiceImpl extends BaseServiceImpl<UserInfoCheckPojo>
		implements IJXLRepUserInfoCheckService {
	
}
