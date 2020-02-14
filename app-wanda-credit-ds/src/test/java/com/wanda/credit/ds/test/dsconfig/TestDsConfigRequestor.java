package com.wanda.credit.ds.test.dsconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.util.SpringContextUtils;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.aijin.AiJinDataSourceRequestor;
import com.wanda.credit.ds.client.dsconfig.DefaultConfigurableDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class TestDsConfigRequestor {
	@Resource(name="defaultConfigurableDataSourceRequestor")
	private DefaultConfigurableDataSourceRequestor service;
//	final static String ds_id = "ds_aijin_jx";
//	final static String ds_id = "ds_fahai_riskmanage_queryDetail";
	final static String ds_id = "ds_guozt_eduNew";
	@Test
	public void test() throws Exception {
	   DataSource ds =new DataSource();
	   ds.setId(ds_id);
//	   ds.setRefProdCode("P_C_B001");
	   List<Param> params_in = new ArrayList<Param>();
	   Param  p1 = new Param();
	   Param  p2 = new Param(); 
	  
	  /* p1.setId("datatype");
	   p1.setValue("satparty");
	   p2.setId("entryId");
	   p2.setValue("AVnUYsBUBDDA6C8n4bcB@AVoXBktuPemI-mbopCv3");*/
	   p1.setValue("田晓英");p1.setId("name");
	   p2.setValue("120221199002161643");p2.setId("cardNo");
	  
	   params_in.add(p1);
	   params_in.add(p2);
	  
	   ds.setParams_in(params_in);
	   System.out.println("请求信息:\n"+JSONObject.toJSONString(ds, true));
		Map<String,Object> ret = service.request(StringUtil.getRandomNo(), ds);
		System.out.println("返回消息为:\n"+JSONObject.toJSONString(ret, true));
	}
	
}
