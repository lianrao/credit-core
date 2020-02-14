package com.wanda.credit.ds.client.bairong.service.impl;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.client.bairong.service.IMobileSrcLocService;
import com.wanda.credit.ds.dao.domain.bairong.MobileSrcLocVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author shiwei
 * @version $$Id: MobileSrcLocServiceImpl, V 0.1 2017/8/22 16:32 shiwei Exp $$
 */
@Service
@Transactional
public class MobileSrcLocServiceImpl extends BaseServiceImpl<MobileSrcLocVo> implements IMobileSrcLocService {
    private static final Logger logger = LoggerFactory.getLogger(MobileSrcLocServiceImpl.class);

    @Autowired
    private DaoService daoService;

    public MobileSrcLocVo findByMobileNo(String mobileNo){
        MobileSrcLocVo mobileSrcLocVo = new MobileSrcLocVo();

        String sql = "select * from (select t.* from CPDB_MK.T_ETL_MOBILE_SRCLOC t where" +
                " t.mobile_no = ? order by update_time desc) where rownum = 1";

        String tempMobile = mobileNo.substring(0,7);

        RowMapper<MobileSrcLocVo> rw =  BeanPropertyRowMapper.newInstance(MobileSrcLocVo.class);

        mobileSrcLocVo = daoService.findOneBySql(sql, new Object[]{tempMobile}, rw);

        return mobileSrcLocVo;
    }

}
