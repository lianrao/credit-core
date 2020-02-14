package com.wanda.credit.ds.test.dmpCar;

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
import com.wanda.credit.ds.client.wangshu.WDWangShuCarBreakRequestor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class TestCarIlleagRequestor {
	@Resource(name="ds_wangshu_carillegal")
	private WDWangShuCarBreakRequestor aijinService;
	final static String ds_id = "ds_wangshu_carillegal";
	@Test
	public void test() throws Exception {
	   DataSource ds =new DataSource();
	   ds.setId(ds_id);
	   ds.setRefProdCode("P_C_B001");
	   List<Param> params_in = new ArrayList<Param>();
	   Param  p1 = new Param();
	   Param  p2 = new Param(); 
	   Param  p3 = new Param();
	   Param  p4 = new Param();
	   p1.setId("hphm");
	   p1.setValue("沪J91167");
	   p2.setId("engineno");
	   p2.setValue("9B140418");
	   p3.setId("classno");
	   p3.setValue("");
	   p4.setId("carType");
	   p4.setValue("02");
	   params_in.add(p1);
	   params_in.add(p2);
	   params_in.add(p3);
	   params_in.add(p4);
	   ds.setParams_in(params_in);
	   System.out.println("请求信息:\n"+JSONObject.toJSONString(ds, true));
		Map<String,Object> ret = aijinService.request(StringUtil.getRandomNo(), ds);
		System.out.println("返回消息为:\n"+JSONObject.toJSONString(ret, true));
	}
	
}
