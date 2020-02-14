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
import com.wanda.credit.ds.client.xyan.XYanIdcardCheckSourceRequestor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class TestXYanRequestor {
	@Resource(name="ds_xyan_police")
	private XYanIdcardCheckSourceRequestor aijinService;
	final static String ds_id = "ds_xyan_police";
	@Test
	public void test() throws Exception {
	   DataSource ds =new DataSource();
	   ds.setId(ds_id);
	   ds.setRefProdCode("P_C_B001");
	   List<Param> params_in = new ArrayList<Param>();
	   Param  p1 = new Param();
	   Param  p2 = new Param(); 
	   Param  p3 = new Param();
	   p1.setId("name");
	   p1.setValue("韩宝欣");
	   p2.setId("cardNo");
	   p2.setValue("320911198501112510");
	   p3.setId("flag");
	   p3.setValue("01");
	   params_in.add(p1);
	   params_in.add(p2);
	   ds.setParams_in(params_in);
	   System.out.println("请求信息:\n"+JSONObject.toJSONString(ds, true));
		Map<String,Object> ret = aijinService.request(StringUtil.getRandomNo(), ds);
		System.out.println("返回消息为:\n"+JSONObject.toJSONString(ret, true));
	}
	
}
