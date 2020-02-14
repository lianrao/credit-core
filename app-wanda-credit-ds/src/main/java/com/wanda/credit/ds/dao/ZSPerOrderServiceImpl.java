package com.wanda.credit.ds.dao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Person_Order;
import com.wanda.credit.ds.dao.iface.IZSPersonOrderService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;

@Service
@Transactional
public class ZSPerOrderServiceImpl extends BaseServiceImpl<ZS_Person_Order> implements IZSPersonOrderService {
}
