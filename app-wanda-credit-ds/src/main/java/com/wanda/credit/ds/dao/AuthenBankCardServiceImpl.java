package com.wanda.credit.ds.dao;

import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.ds.dao.domain.zhengtong.ZT_BankCard__Authen;
import com.wanda.credit.ds.dao.iface.IAuthenBankCardService;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年4月12日 上午10:38:17 
 *  
 */
@Service
@Transactional
public class AuthenBankCardServiceImpl implements IAuthenBankCardService {

	@Autowired
	private DaoService daoService;
	
	/**
	 * @param ctx 数据总线
	 * @param resultJsn format eg. 
	 * {
    * "error_no": "0",
    * "results": [
    *    {
    *        "cerkey": "","respinfo": "认证一致(通过)","status": "00",
    *       "mpssim": "0.0","biztyp": "0541","sysSeqNb": "19103520160412880575730073",
    *       "respcd": "2000","certseq": "342422199203247574","accountName": "中国农业银行",
    *       "name": "李四","ptyAcct": "kuaiqiantest","ptycd": "kuaiqiantest",
    *       "localsim": "0.0","telephone": "13411112222"
    *   }
    * ],
    * "error_info": ""}
	* */
	@Override
	public ZT_BankCard__Authen addAuthenBackCard(JSONObject resultJsn,Map<String,Object> ctx) {
		if(resultJsn == null) return null;
		JSONArray jsonArray = resultJsn.getJSONArray("results");
		if(jsonArray != null && jsonArray.size() > 0){
			JSONObject jsnObj = jsonArray.getJSONObject(0);
			if(jsnObj != null){
				ZT_BankCard__Authen vo = new ZT_BankCard__Authen();
				vo.setAcctno((String)ctx.get("cardId"));
				vo.setCardno((String)ctx.get("cardNo"));
				vo.setPhoneno((String)ctx.get("phone"));
				vo.setName((String)ctx.get("name"));
				vo.setTrade_id((String)ctx.get("trade_id"));
				vo.setSourcechnl((String)ctx.get("sourcechnl"));

				vo.setBiztyp((String)jsnObj.get("biztyp"));
				vo.setMpssim((String)jsnObj.get("mpssim"));
				vo.setPtycd((String)jsnObj.get("ptycd"));
				vo.setRespcd((String)jsnObj.get("respcd"));
				vo.setRespinfo((String)jsnObj.get("respinfo"));
				vo.setStatus((String)jsnObj.get("status"));
				vo.setSysseqnb((String)jsnObj.get("sysSeqNb"));
				
				daoService.create(vo);
			}
			
		}
		return null;
	}

	public static void main(String[] args) {
		JSONObject jsnObj = JSONObject.fromObject("{a:123}");
		System.out.println(jsnObj.get("a"));
		System.out.println((String)jsnObj.get("aa1"));
		System.out.println((String)jsnObj.getString("aa1"));

	}
}
