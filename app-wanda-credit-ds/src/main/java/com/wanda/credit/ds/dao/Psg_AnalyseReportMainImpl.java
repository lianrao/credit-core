package com.wanda.credit.ds.dao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.xiaohe.Psg_AnalyseReportMain;
import com.wanda.credit.ds.dao.iface.IPsg_AnalyseReportMain;

@Service
@Transactional
public class Psg_AnalyseReportMainImpl extends BaseServiceImpl<Psg_AnalyseReportMain> implements IPsg_AnalyseReportMain {

	private static final long serialVersionUID = -3980145778504782396L;
}
