package com.wanda.credit.ds.client.bairong.service;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.bairong.MobileSrcLocVo;
import org.springframework.stereotype.Service;

/**
 * @author shiwei
 * @version $$Id: IMobileSrcLocService, V 0.1 2017/8/22 16:32 shiwei Exp $$
 */
public interface IMobileSrcLocService extends IBaseService<MobileSrcLocVo> {

    public MobileSrcLocVo findByMobileNo(String mobileNo);

}
