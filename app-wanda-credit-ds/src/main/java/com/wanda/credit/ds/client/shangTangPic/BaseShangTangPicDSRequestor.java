/**   
* @Description: 商汤图像识别接口
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:22:56 
* @version V1.0   
*/
package com.wanda.credit.ds.client.shangTangPic;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.phjr.ISTFileUploadService;
import com.wanda.credit.ds.dao.iface.phjr.ISTImageAuthService;

/**
 * @author xiaobin.hou
 *
 */
public class BaseShangTangPicDSRequestor extends BaseDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(BaseShangTangPicDSRequestor.class);
	
	protected final static String HEADER_AUTHOR= "Authorization";
	protected final static String BODY_DATA = "data";
	protected final static String RES_CODE = "code";
	protected final static String RES_MESSAGE = "message";
	protected final static String RES_REQUEST_ID = "request_id";
	protected final static String FIRST_IMAGE = "first_image_id";
	protected final static String SECOND_IMAGE = "second_image_id";
	protected final static String OPTION = "option";
	protected final static String AUTO_ROTATE = "auto_rotate";
	protected final static String VERIFICATION_SCORE = "verification_score";
	@Autowired
	protected IPropertyEngine propertyEngine;
	@Autowired
	protected IExecutorFileService fileService;
	@Autowired
	protected ISTFileUploadService uploadService;
	@Autowired
	protected ISTImageAuthService imageAuthService;
	/**
	 * 初始化数据源返回的初始化对象 Map<String,Object>
	 * @return
	 */
	protected Map<String, Object> initRets(){
		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG, "交易失败");
		return rets;
	}
	
	protected DataSourceLogVO buildLogObj(String dsId, String url) {
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(dsId);
		logObj.setReq_url(url);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		return logObj;
	}
	/**
	 * 
	 * @param paramIn
	 * @param trade_id
	 * @param logObj
	 * @return
	 */
	protected boolean saveParamIn(Map<String, Object> paramIn,String trade_id, DataSourceLogVO logObj) {
		boolean isSave = true;
		try {
			long start = System.currentTimeMillis();
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
			logger.info("{} 保存请求参数成功,耗时为 {}", trade_id , System.currentTimeMillis() - start	);
		} catch (Exception e) {
			logger.info("{}保存入参信息异常 {}", trade_id, e.getMessage());
			isSave = false;
		}
		return isSave;
	}
}
