package com.wanda.credit.ds.dao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.xiaohe.Psg_IncacheMain;
import com.wanda.credit.ds.dao.iface.IPsg_IncacheMain;

@Service
@Transactional
public class Psg_IncacheMainImpl extends BaseServiceImpl<Psg_IncacheMain> implements IPsg_IncacheMain {

	private static final long serialVersionUID = -3980145778504782396L;
}
