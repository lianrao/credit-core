package com.wanda.credit.ds.client.tengxun;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.yidaoOcr.YidaoOcrRequestor;
import com.wanda.credit.ds.dao.domain.baidu.Baidu_Face_Result;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * @author liunan
 * @version $$Id: BaiduCardOcrDetection, V 0.1 2018/08/20 11:22 shiwei Exp $$
 */
@DataSourceClass(bindingDataSourceId="ds_tengxun_ocr")
public class TengxunIdcardOcr extends BaseTengxRequestor implements IDataSourceRequestor {
    private Logger logger = LoggerFactory.getLogger(TengxunIdcardOcr.class);
    @Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private FileEngine fileEngines;
	@Autowired
	private YidaoOcrRequestor yidaoService;
    @Override
    public Map<String, Object> request(String trade_id, DataSource ds) {
        final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
        long start = System.currentTimeMillis();
        logger.info("{} 腾讯ocr Begin" , prefix);
        String tengxun_url =  propertyEngine.readById("tengxun_idcardocr_address01");
        String app_id =  propertyEngine.readById("tengxun_ocr_appid");
        String app_key =  propertyEngine.readById("tengxun_ocr_appkey");
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
  		logObj.setReq_url(tengxun_url);
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
			if(!"idCard".equals(recotype)){
				resource_tag = Conts.TAG_TST_SUCCESS;
				rets = yidaoService.request(trade_id, ds);
				return rets;
			}
			if(StringUtils.isNotEmpty(req_image)){
				logger.info("{} 图片上传征信存储开始...", prefix);
				fpath = fileEngines.store("ds_yuanjian_photo",FileArea.DS, FileType.JPG, req_image,trade_id);
				logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix,fpath);
			}
			String file_full_path = fileEngines.getFullPathById(fpath);
			byte[] images = FileUtil.readFileByBytes(file_full_path);
			String image = Base64Util.encode(images);
			HashMap<String, String> param = new HashMap<>();
			String time_stamp = System.currentTimeMillis()/1000+"";
			String card_type = "0";
			if("front".equals(side)){
				card_type = "0";
			}else{
				card_type = "1";
			}
			param.put("app_id", app_id+"");
			param.put("time_stamp", time_stamp);
			param.put("card_type", card_type);
			param.put("nonce_str", trade_id);
			param.put("image", image);
			param.put("sign", getSignature(trade_id,param,app_key));
			logger.info("{} 腾讯云请求开始...", prefix);
			String res = "";
			if(!tengxun_url.startsWith("https:")){
				res = doPost(trade_id,tengxun_url, param,false);
			}else{
				res = doPost(trade_id,tengxun_url, param,true);
			}
			logger.info("{} 腾讯云请求结束", prefix);
			JSONObject rsp = (JSONObject) JSONObject.parse(res);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);	//初始值-失败
//			res = res.replaceAll("\\", "");
//			logger.info("{} 返回信息：{}", prefix,res);
			resource_tag = buildRsp(trade_id, rsp, rets, resource_tag, side);
        }catch (Exception e){
            logger.error("{} 腾讯ocr交易处理异常：{}" , prefix , ExceptionUtil.getTrace(e));
            //设置标签
            resource_tag = Conts.TAG_TST_FAIL;
            if (e instanceof ConnectTimeoutException) {
                logger.error("{} 连接远程数据源超时" , prefix);

                logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
                logObj.setState_msg("请求超时");
                //设置标签
                resource_tag = Conts.TAG_SYS_TIMEOUT;
            }
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
        logger.info("{} 腾讯人脸识别End，交易时间为(ms):{}", prefix ,(System.currentTimeMillis() - start));
        return rets;
    }
}
