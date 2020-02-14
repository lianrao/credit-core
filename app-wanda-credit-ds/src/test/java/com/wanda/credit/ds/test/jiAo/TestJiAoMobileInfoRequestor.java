package com.wanda.credit.ds.test.jiAo;

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
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class TestJiAoMobileInfoRequestor {

	@Resource(name = "ds_jiAo_multiple")
	private IDataSourceRequestor req;

	@Test
	public void test() throws Exception {
		
		String tradeId = StringUtil.getRandomNo();
		
		DataSource ds = new DataSource();
		ds.setId("ds_jiAo_mobileCheck");
		ds.setRefProdCode("P_C_B150");
		
		List<Param> params_in = new ArrayList<Param>(); 
		
		Param p1 = new Param(); 
		p1.setId("name");
		p1.setValue("韩宝欣"); 
		params_in.add(p1);
		
		Param p2 = new Param(); 
		p2.setId("cardNo");
		p2.setValue("320911198501112510"); 
		params_in.add(p2);
		
		Param p3 = new Param(); 
		p3.setId("mobile");
		p3.setValue("13482478737"); 
		params_in.add(p3);
		
		Param p4 = new Param(); 
		p4.setId("inTime");
		p4.setValue("1"); 
		params_in.add(p4);
		
		Param p5 = new Param(); 
		p5.setId("mobileState");
		p5.setValue("1"); 
		params_in.add(p5);
		
		ds.setParams_in(params_in);
		 
		System.out.println("请求信息:\n" + JSONObject.toJSONString(ds, true));
		Map<String, Object> ret = req.request(tradeId, ds);
		System.out.println("返回消息为:\n" + JSONObject.toJSONString(ret, true));
	}

}
