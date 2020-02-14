package com.wanda.credit.ds.client.baiduFace;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.baiduFace.bean.FaceBean;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author liunan
 * @version 人脸检测，人脸是否朝上
 */
@DataSourceClass(bindingDataSourceId="ds_baidu_faceCheck")
public class BaiduFaceCheckDetection extends BaseBaiduFaceRequestor implements IDataSourceRequestor {
    private Logger logger = LoggerFactory.getLogger(BaiduFaceCheckDetection.class);
    @Autowired
	public IPropertyEngine propertyEngine;
    @Override
    public Map<String, Object> request(String trade_id, DataSource ds) {
        final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
        long start = System.currentTimeMillis();
        logger.info("{} 百度人脸检测Begin" , prefix);
        String baidu_url =  propertyEngine.readById("baidu_facecheck_address01");
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
			String photo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); // 高清照
			
			Map<String, Object> map = new HashMap<>();
            map.put("image", photo);
            map.put("image_type", "BASE64");
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = getAuth(trade_id,false);
            logger.info("{} 获取的token:{}", prefix,accessToken);
            Map<String, String> headers = new HashMap<String,String>();
            String result = RequestHelper.doPost(baidu_url+"?access_token="+accessToken,null,headers,map,null,false, 1000);
//			String result = RequestHelper.sendPostRequest(baidu_url+"?access_token="+accessToken, JSON.toJSONString(map), "application/json");
			logger.info("{} 返回结果信息:{}", prefix,result);
			FaceBean face = JSONObject.parseObject(result, FaceBean.class);
			if("0".equals(face.getError_code())){
				logger.info("{} 外部人脸检测成功", prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);	//交易成功
				logObj.setState_msg("交易成功");
				//构建返回标签
	            resource_tag = Conts.TAG_FOUND;
	            Map<String, Object> respResult = new HashMap<String, Object>();
				respResult.put("rotation", 
						face.getResult().getFace_list().get(0).getLocation().getRotation());
	            rets.clear();
	            rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
	            rets.put(Conts.KEY_RET_MSG, "请求成功");
	            rets.put(Conts.KEY_RET_DATA, respResult);
			}else if("110".equals(face.getError_code()) || "111".equals(face.getError_code())){
				getAuth(trade_id,true);
	            logger.info("{} 重新获取的token:{}", prefix,accessToken);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败,返回原因:" + face.getError_code());
				logger.error("{} 外部人脸识别失败", prefix);
			}else{				
				//构建返回标签
	            resource_tag = Conts.TAG_UNFOUND;
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "人脸识别失败,返回原因:" + face.getError_code());
				logger.error("{} 外部人脸识别失败", prefix);
			}
        }catch (Exception ex){
            logger.error("{} 百度人脸检测交易处理异常：{}" , prefix , ex.getMessage());
            resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
        }finally {
            rets.put(Conts.KEY_RET_TAG,new String[]{resource_tag});
            //保存日志信息
            logObj.setTag(resource_tag);
            logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
            long dsLogStart = System.currentTimeMillis();
            DataSourceLogEngineUtil.writeLog(trade_id,logObj);
            logger.info("{} 保存ds Log成功,耗时：{}" ,prefix , System.currentTimeMillis() - dsLogStart);
        }
        logger.info("{} 百度人脸检测End，交易时间为(ms):{}", prefix ,(System.currentTimeMillis() - start));
        return rets;
    }
}
