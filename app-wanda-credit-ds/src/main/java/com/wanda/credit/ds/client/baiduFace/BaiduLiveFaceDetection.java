package com.wanda.credit.ds.client.baiduFace;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.baiduFace.bean.FaceBean;
import com.wanda.credit.ds.client.yituNew.YiTuLocalFacialPhotographContrast;
import com.wanda.credit.ds.dao.domain.baidu.Baidu_Face_Result;
import com.wanda.credit.ds.dao.iface.baidu.IBaiduFaceService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author liunan
 * @desc 百度活体照比对
 */
@DataSourceClass(bindingDataSourceId="ds_baidu_face_live")
public class BaiduLiveFaceDetection extends BaseBaiduFaceRequestor implements IDataSourceRequestor {
    private Logger logger = LoggerFactory.getLogger(BaiduLiveFaceDetection.class);
    @Autowired
	public IPropertyEngine propertyEngine;
    @Autowired
	private FileEngine fileEngines;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
    private IBaiduFaceService baiduService;
	@Autowired
	private YiTuLocalFacialPhotographContrast yituPhotoService;
    @Override
    public Map<String, Object> request(String trade_id, DataSource ds) {
        final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
        long start = System.currentTimeMillis();
        logger.info("{} 百度人脸识别Begin" , prefix);
        String baidu_url =  propertyEngine.readById("baidu_face_address01");
        double score_sub =  Double.parseDouble(propertyEngine.readById("baidu_face_score_sub"));//对百度获取的相似度减值score_sub
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
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); // 身份证号码
			String photo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); // 网纹照片数据
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); // 姓名
			String query_Image_Content = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString(); //活体照片数据
			
			String enCardNo = synchExecutorService.encrypt(cardNo);
			//记录入参到入参记录表			
			paramIn.put("cardNo", cardNo);
			paramIn.put("name", name);

			String fpath = null;
			if(StringUtils.isNotEmpty(query_Image_Content)){
				logger.info("{} 图片上传征信存储开始...", prefix);
				fpath = fileEngines.store("ds_yuanjian_photo",FileArea.DS, FileType.JPG, photo,trade_id);
				logger.info("{} 图片上传征信存储成功,照片ID为：{}", prefix,fpath);
			}
			baidu_face.setCardno(enCardNo);
			baidu_face.setName(name);
			baidu_face.setPhoto_id(fpath);
			
			List<Map<String, Object>> images = new ArrayList<>();
			buildParams(images,query_Image_Content,photo);
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = getAuth(trade_id,false);
            logger.info("{} 获取的token:{}", prefix,accessToken);
			String result = RequestHelper.sendPostRequest(baidu_url+"?access_token="+accessToken, JSON.toJSONString(images), "application/json");
			logger.info("{} 返回结果信息:{}", prefix,result);
			FaceBean face = JSONObject.parseObject(result, FaceBean.class);
			baidu_face.setCached(face.getCached());
			baidu_face.setError_code(face.getError_code());
			baidu_face.setError_msg(face.getError_msg());
			baidu_face.setLog_id(face.getLog_id());
			if("0".equals(face.getError_code())){
				logger.info("{} 外部人脸识别成功", prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);	//交易成功
				logObj.setState_msg("交易成功");
				baidu_face.setScore(String.valueOf(face.getResult().getScore()));
				baidu_face.setFace_token1(face.getResult().getFace_list().get(0).getFace_token());
				baidu_face.setFace_token2(face.getResult().getFace_list().get(1).getFace_token());
				//构建返回标签
	            resource_tag = Conts.TAG_FOUND;
	            Map<String, Object> respResult = new HashMap<String, Object>();
				respResult.put("server_idx", "06");
				respResult.put("rtn", 0);
				respResult.put("pair_verify_similarity", face.getResult().getScore()-score_sub);
				if(face.getResult().getScore()>=80){
					respResult.put("pair_verify_result", "0");
				}else{
					respResult.put("pair_verify_result", "1");
				}
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
			baiduService.add(baidu_face);
        }catch (Exception e){
            logger.error("{} 百度人脸识别交易处理异常：{}" , prefix , e.getMessage());
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
            //请求依图
            if(!"found".equals(resource_tag)){
            	ds.setId("ds_yitu_authPhoto102");
            	rets = yituPhotoService.request(trade_id, ds);
            }
        }
        logger.info("{} 百度人脸识别End，交易时间为(ms):{}", prefix ,(System.currentTimeMillis() - start));
        return rets;
    }
    public void buildParams(List<Map<String, Object>> images,String query_Image_Content,String photo){
        Map<String, Object> map1 = new HashMap<>();
        map1.put("image", query_Image_Content);//活体照
        map1.put("image_type", "BASE64");
        map1.put("face_type", "LIVE");
        map1.put("quality_control", propertyEngine.readById("baidu_face_param1_quality"));//HIGH
        map1.put("liveness_control", propertyEngine.readById("baidu_face_param1_liveness"));//NORMAL

        Map<String, Object> map2 = new HashMap<>();
        map2.put("image", photo);//网纹照
        map2.put("image_type", "BASE64");
        map2.put("face_type", "LIVE");
        map2.put("quality_control", propertyEngine.readById("baidu_face_param2_quality"));//HIGH
        map2.put("liveness_control", propertyEngine.readById("baidu_face_param2_liveness"));//NORMAL

        images.add(map1);
        images.add(map2);
    }
}
