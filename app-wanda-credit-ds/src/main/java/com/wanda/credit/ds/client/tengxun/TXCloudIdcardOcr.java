package com.wanda.credit.ds.client.tengxun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.ImgCompress;
import com.wanda.credit.base.util.StringUtil;
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
 */
@DataSourceClass(bindingDataSourceId="ds_tengxun_cloud_ocr")
public class TXCloudIdcardOcr extends BaseTengxRequestor implements IDataSourceRequestor {
    private Logger logger = LoggerFactory.getLogger(TXCloudIdcardOcr.class);
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
        logger.info("{} 腾讯云ocr Begin" , prefix);
        String tengxun_url =  propertyEngine.readById("tengxun_idcardocr_cloud_address");
        Map<String,Object> paramIn = new HashMap<String,Object>();
        Map<String, Object> rets = new HashMap<>();
        rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
        rets.put(Conts.KEY_RET_MSG, "交易失败");
        double guozt_comBase = Double.valueOf(propertyEngine.readById("ds_guozt_face_photo_comBase"));//压缩基数
		double guozt_scale = Double.valueOf(propertyEngine.readById("ds_guozt_face_photo_scale"));//压缩限制(宽/高)比例  一般用1
		int photo_limit = Integer.valueOf(propertyEngine.readById("ds_police_auth_limit"));
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
				String file_full_path = fileEngines.getFullPathById(fpath);
				ImgCompress imgCom = new ImgCompress(trade_id,file_full_path); 
				String comperss_rsp = imgCom.getCompressBase64FromUrl(trade_id, guozt_comBase, guozt_scale,photo_limit);
				if(!StringUtil.isEmpty(comperss_rsp)){
					req_image = comperss_rsp;
				}
			}
			logger.info("{} 腾讯云请求开始...", prefix);
			String commond = "java -jar /u01/tmp/tengxun/wanda-corp-test.jar "+trade_id+" ";
			if("front".equals(side)){
				commond = commond+"front";
			}else{
				commond = commond+"back";
			}
			commond = commond+" "+req_image;
			Runtime runtime = Runtime.getRuntime();  
            runtime.exec(commond);
            int count1=1;
            String image_path = "/app_nas/face/ocr/"+trade_id+".txt";
            String res = "";
            File file=null;
			while(true){
				if(count1 > 0)
					logger.info("{} 轮询查询文件是否存在,轮询序号:{}",trade_id,count1);
				if(count1>=100){
					logger.info("{} 轮询查询文件是否存在主程序最大容忍次数已到,系统将自动停止！",trade_id);
					break;
				}
				file=new File(image_path);
				if(file.exists()){
					FileInputStream fis = new FileInputStream(file);
					InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF-8");
					BufferedReader in = new BufferedReader(inputStreamReader);
					StringBuffer sBuffer = new StringBuffer();
					String sbt =null;
					while((sbt = in.readLine())!=null){
						sBuffer.append(sbt);
					}					
					res = sBuffer.toString();
					if(in != null)
						in.close();
					if(fis != null)
						fis.close();
					break;
				}
				Thread.sleep(200);//模拟ds3处理需要时间
				count1++;
			}
			logger.info("{} 腾讯云请求结束", prefix);
			JSONObject rsp = (JSONObject) JSONObject.parse(res);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);	//初始值-失败
			resource_tag = buildRsponse(trade_id, rsp, rets, resource_tag, side);
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
    public String buildRsponse(String trade_id,JSONObject json,Map<String,Object> rets,String resource_tag,String side)
    		throws JSONException{
    	logger.info("{} 出参包装开始...",trade_id);
    	if(json == null){
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源请求失败!");
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
			return resource_tag;
		}
    	HashMap<String, String> result = new HashMap<String, String>();
    	HashMap<String, Object> retdata = new HashMap<String, Object>();
    	if("front".equals(side)){
			logger.info("{} 正面解析开始...",trade_id);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");  			       
			String birday_str = "";
			try {
				String birday = json.getString("Birth");
				Date birday_tmp = sdf.parse(birday);
				birday_str = DateUtil.getSimpleDate(birday_tmp, "yyyy年M月dd日");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			
			JSONObject crop_image = JSONObject.parseObject(json.getString("AdvancedInfo"));
			result.put("name", json.getString("Name"));
			result.put("gender", json.getString("Sex"));
			result.put("nation", json.getString("Nation"));
			result.put("birthdate", birday_str);
			result.put("address", json.getString("Address"));
			result.put("cardNo", json.getString("IdNum"));
			result.put("side", side);
			result.put("head_image", crop_image.getString("Portrait"));
			result.put("cropped_image", crop_image.getString("IdCard"));
			logger.info("{} 正面解析完成",trade_id);
		}else{
			logger.info("{} 反面解析开始...",trade_id);
			JSONObject crop_image = JSONObject.parseObject(json.getString("AdvancedInfo"));
			result.put("issuedby", json.getString("Authority"));
			result.put("validthru", json.getString("ValidDate").replace(".", ""));
			result.put("side", "back");
			result.put("cropped_image", crop_image.getString("IdCard"));
			logger.info("{} 反面解析完成",trade_id);
		}
    	retdata.put("result", result);
		resource_tag = Conts.TAG_TST_SUCCESS;
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		rets.put(Conts.KEY_RET_DATA, retdata);
		rets.put(Conts.KEY_RET_MSG, "交易成功!");
		rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		return resource_tag;
    }
}
