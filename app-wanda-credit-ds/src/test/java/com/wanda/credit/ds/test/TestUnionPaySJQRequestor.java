package com.wanda.credit.ds.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.client.unionpay.SJQBankCardQiuDataSourceRequestor;
import com.wanda.credit.ds.client.unionpay.SJQMobileQiuDataSourceRequestor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class TestUnionPaySJQRequestor {
//	@Resource(name = "ds_shuijingqiu_mobile")
//	private SJQMobileQiuDataSourceRequestor sjqService;
//	final static String ds_id = "ds_shuijingqiu_mobile";

	 @Resource(name = "ds_shuijingqiu_bankcard")
	 private SJQBankCardQiuDataSourceRequestor sjqService;
	 final static String ds_id = "ds_shuijingqiu_bankcard";

	@Test
	public void test() throws Exception {
		DataSource ds = new DataSource();
		ds.setId(ds_id);
		ds.setRefProdCode("P_C_B999");
		List<Param> params_in = new ArrayList<Param>();
		Param p1 = new Param();

		//ds_shuijingqiu_mobile
		p1.setId("mobileCode");
		p1.setValue("韩宝欣");

		//ds_shuijingqiu_bankcard
		p1.setId("bankCode");
		p1.setValue("韩宝欣");

		params_in.add(p1);
		ds.setParams_in(params_in);
		System.out.println("请求信息:\n" + JSONObject.toJSONString(ds, true));
		Map<String, Object> ret = sjqService.request(
				StringUtil.getRandomNo(), ds);
		System.out.println("返回消息为:\n" + JSONObject.toJSONString(ret, true));
	}

}
