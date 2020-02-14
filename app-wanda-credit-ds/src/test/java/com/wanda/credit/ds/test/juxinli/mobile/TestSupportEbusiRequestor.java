package com.wanda.credit.ds.test.juxinli.mobile;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class TestSupportEbusiRequestor {

	@Resource(name = "ds_jxlEbusiDataSources")
	private IDataSourceRequestor supportDSReq;

	@Test
	public void test() throws Exception {
		
		String tradeId = StringUtil.getRandomNo();
		
		DataSource ds = new DataSource();
		ds.setId("ds_jxlEbusiDataSources");
		ds.setRefProdCode("P_C_B251");
		/*
		 * List<Param> params_in = new ArrayList<Param>(); Param p1 = new
		 * Param(); Param p2 = new Param(); p1.setId("name");
		 * p1.setValue("韩宝欣"); p2.setId("cardNo");
		 * p2.setValue("320911198501112510"); params_in.add(p1);
		 * params_in.add(p2); ds.setParams_in(params_in);
		 */
		System.out.println("请求信息:\n" + JSONObject.toJSONString(ds, true));
		Map<String, Object> ret = supportDSReq.request(tradeId, ds);
		System.out.println("返回消息为:\n" + JSONObject.toJSONString(ret, true));
	}

}
