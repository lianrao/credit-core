package com.wanda.credit.ds.dao.iface;

import java.util.Map;

import net.sf.json.JSONObject;

import com.wanda.credit.ds.dao.domain.zhengtong.ZT_BankCard__Authen;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年4月12日 上午10:07:57 
 *  
 */

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
public interface IAuthenBankCardService {

	public ZT_BankCard__Authen addAuthenBackCard(JSONObject resultJsn,Map<String,Object> ctx);

}
