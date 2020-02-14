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
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class TestZS_NEWRequestor {
	@Resource(name="ds_suanhua_AuthenBankCard")
	private IDataSourceRequestor checkReq;

	@Test
	public void test() throws Exception {
		
		String tradeId = StringUtil.getRandomNo();
		
		DataSource ds = new DataSource();
		ds.setId("ds_zsCorpQuery_new");//id,creditcode,regno,name,orgcode
		ds.setId("ds_suanhua_AuthenBankCard");//name,cardNo,cardIds,mobileNo
		ds.setRefProdCode("P_C_B999");
		
		List<Param> params_in = new ArrayList<Param>(); 
		Param p1 = new Param(); 
		p1.setId("name");
		p1.setValue("丁家华"); 
		params_in.add(p1);
		
		Param p2 = new Param(); 
		p2.setId("cardNo");
		p2.setValue("432923197212270617"); 
		params_in.add(p2);
		
//		Param p3 = new Param(); 
//		p3.setId("cardIds");
//		p3.setValue("6217931020107530"); 
//		params_in.add(p3);
		
//		Param p4 = new Param(); 
//		p4.setId("mobileNo");
//		p4.setValue(""); 
//		params_in.add(p4);
		
//		Param p5 = new Param(); 
//		p5.setId("orgcode");
//		p5.setValue(""); 
//		params_in.add(p5);
		
//		Param p6 = new Param(); 
//		p6.setId("mask");
//		p6.setValue(""); 
//		params_in.add(p6);
//		
//		Param p7 = new Param(); 
//		p7.setId("version");
//		p7.setValue(""); 
//		params_in.add(p7);
//		
//		Param p8 = new Param(); 
//		p8.setId("enttype");
//		p8.setValue(""); 
//		params_in.add(p8);
//		
//		Param p9 = new Param(); 
//		p9.setId("acct_id");
//		p9.setValue("test"); 
//		params_in.add(p9);
		
		ds.setParams_in(params_in);
		 
		System.out.println("请求信息:\n" + JSONObject.toJSONString(ds, true));
		Map<String, Object> ret = checkReq.request(tradeId, ds);
		System.out.println("返回消息为:\n" + JSONObject.toJSONString(ret, true));
		
	}
	
}
