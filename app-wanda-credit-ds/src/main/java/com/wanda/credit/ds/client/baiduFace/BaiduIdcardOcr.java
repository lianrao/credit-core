package com.wanda.credit.ds.client.baiduFace;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.baidu.Baidu_Face_Result;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * @author liunan
 * @version $$Id: BaiduCardOcrDetection, V 0.1 2018/08/20 11:22 shiwei Exp $$
 */
@DataSourceClass(bindingDataSourceId="ds_baidu_ocr")
public class BaiduIdcardOcr extends BaseBaiduFaceRequestor implements IDataSourceRequestor {
    private Logger logger = LoggerFactory.getLogger(BaiduIdcardOcr.class);
    @Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorFileService fileService;
    @Override
    public Map<String, Object> request(String trade_id, DataSource ds) {
        final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
        long start = System.currentTimeMillis();
        logger.info("{} 百度ocr Begin" , prefix);
        String baidu_url =  propertyEngine.readById("baidu_idcardocr_address01");
        String baidu_app_id =  propertyEngine.readById("baidu_ocr_appid");
        String baidu_api_key =  propertyEngine.readById("baidu_ocr_appkey");
        String baidu_secret_key =  propertyEngine.readById("baidu_ocr_appsercret");
        Map<String,Object> paramIn = new HashMap<String,Object>();
        Map<String, Object> rets = new HashMap<>();
        rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
        rets.put(Conts.KEY_RET_MSG, "交易失败");

        //计费标签
        String resource_tag = Conts.TAG_SYS_ERROR;
        //请求交易结果日志表
  		DataSourceLogVO logObj = new DataSourceLogVO();
  		logObj.setDs_id(ds.getId());//log:供应商id
  		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
  		logObj.setReq_url(baidu_url);
  		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);	//初始值-失败
  		logObj.setIncache("0");//不缓存
        try{
            logger.info("{} 开始解析传入的参数" , prefix);
            Baidu_Face_Result baidu_face = new Baidu_Face_Result();
            baidu_face.setTrade_id(trade_id);
            String recotype = ParamUtil.findValue(ds.getParams_in(),
					paramIds[0]).toString(); // 证件类型
			String req_image = ParamUtil.findValue(ds.getParams_in(),
					paramIds[1]).toString(); // 传入图片
			String side = "";
			if (!(ParamUtil.findValue(ds.getParams_in(), paramIds[2])==null)) {
				 side =ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
			}
			paramIn.put("recotype", recotype);
			String fpath = null;
			if(StringUtils.isNotEmpty(req_image)){
				logger.info("{} 图片上传征信存储开始...", prefix);
				fpath = fileService.upload(req_image, FileType.JPG, FileArea.DS,trade_id);
				logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix,fpath);
			}
			BaiduOcrPost baiduocr = new BaiduOcrPost(baidu_app_id,baidu_api_key,baidu_secret_key);
			JSONObject res = baiduocr.getOcrRsp(side, baidu_url, req_image, trade_id);
			logger.info("{} 返回信息：{}", prefix,res);
			resource_tag = baiduocr.buildRsp(trade_id, res, rets, resource_tag, side);
        }catch (Exception e){
            logger.error("{} 百度ocr交易处理异常：{}" , prefix , e.getMessage());
            //设置标签
            resource_tag = Conts.TAG_TST_FAIL;
            if (e instanceof ConnectTimeoutException) {
                logger.error("{} 连接远程数据源超时" , prefix);

                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
                logObj.setState_msg("请求超时");
                //设置标签
                resource_tag = Conts.TAG_SYS_TIMEOUT;
            }
            e.printStackTrace();
        }finally {
            rets.put(Conts.KEY_RET_TAG,new String[]{resource_tag});
            //保存日志信息
            logObj.setTag(resource_tag);
            logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
            long dsLogStart = System.currentTimeMillis();
            DataSourceLogEngineUtil.writeLog(trade_id,logObj);
            DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
            logger.info("{} 保存ds Log成功,耗时：{}" ,prefix , System.currentTimeMillis() - dsLogStart);
        }
        logger.info("{} 百度人脸识别End，交易时间为(ms):{}", prefix ,(System.currentTimeMillis() - start));
        return rets;
    }
}
