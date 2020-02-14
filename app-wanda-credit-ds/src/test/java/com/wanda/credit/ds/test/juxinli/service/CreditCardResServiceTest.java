/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年3月18日 下午4:44:34 
* @version V1.0   
*/
package com.wanda.credit.ds.test.juxinli.service;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardDataResPojo;
import com.wanda.credit.ds.dao.iface.juxinli.creditCardBill.IJXLCreditCardDataResService;


/**
 * @author xiaobin.hou
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class CreditCardResServiceTest {
	@Autowired
	private IJXLCreditCardDataResService cardDataService;
	
	
	
	@Test
	public void testRes() throws Exception{
		
		CreditCardDataResPojo cardData = new CreditCardDataResPojo();
		
		cardData.setRequestId("12354854saffewfesadffe");
		cardData.setCreate_date(new Date());
		cardData.setUpdate_date(new Date());
		
		cardDataService.add(cardData);
		
	}


}
