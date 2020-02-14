package com.wanda.credit.ds.client.shangtang;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.ImageUtils;
import com.wanda.credit.base.util.ReadVideo;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.shangtang.IShangTangVideoPhotoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId = "ds_shangtang_video_photo")
public class ShangTangVideoOfPhotoRequestor extends BaseDataSourceRequestor
		implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(ShangTangVideoOfPhotoRequestor.class);
	@Autowired
	private IShangTangVideoPhotoService shangTangVideoPhotoService;

	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private STGladVideoStalessRequestor stLiveStalessService;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		String vopath = null;
		String impath = null;

		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		String shangtang_url = propertyEngine.readById("shangtang_stateless_url");
		String shangtang_path = propertyEngine.readById("shangtang_stateless_path");
		String route206 = propertyEngine.readById("ds_206_send_guozt");

		// 请求交易结果日志表
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id("ds_shangtang_stateless");// log:供应商id
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		logObj.setReq_url(shangtang_url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL); // 初始值-失败
		logObj.setIncache("0");// 不缓存
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			rets = new HashMap<String, Object>();
			String req_video1 = ParamUtil.findValue(
					ds.getParams_in(), paramIds[0]).toString(); // 视频
			String imgPath = mkdirTodayPath(shangtang_path);
			vopath = imgPath + File.separator + trade_id + ".mp4";
			impath = imgPath + File.separator + trade_id;
			logger.info("{} 商汤静默视频imgPath:{}", prefix,imgPath);
			logger.info("{} 商汤静默视频vopath:{}", prefix,vopath);
			logger.info("{} 商汤静默视频impath:{}", prefix,impath);

			int negativeRate = 0;
			String req_video = shangTangVideoPhotoService.queryVideoFile(trade_id, req_video1);
			
			ImageUtils.decodeBase64ToImage(req_video, imgPath + File.separator, trade_id
					+ ".mp4");
			logger.info("{} 进行商汤静默视频活体检测数据源调用...", prefix);
			DataSource ds_live = new DataSource();
			List<Param> params_in = new ArrayList<Param>();
			Param voapaths = new Param();
			voapaths.setId("vopath");
			voapaths.setValue(vopath);
			params_in.add(voapaths);
			ds_live.setParams_in(params_in);
			ds_live.setId("ds_shangtang_liveness");
			rets = stLiveStalessService.request(trade_id, ds_live);
			if(!isSuccess(rets)){
				logger.error("{} 商汤静默视频活体检测未通过", prefix);
				return rets;
			}
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			resource_tag = httpOtherClientPost(route206,vopath,impath,trade_id,ds,rets,negativeRate);
			return rets;
		} catch (Exception ex) {
			ex.printStackTrace();
			resource_tag = Conts.TAG_TST_FAIL;
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_DS_SHANGT_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "视频拍摄不符合要求,请重新拍摄!");
			logger.error("{} 数据源处理时异常：{}", prefix, ex.getMessage());
			logger.error("{} 数据源处理时异常：{}", prefix, ExceptionUtil.getTrace(ex));

			/** 如果是超时异常 记录超时信息 */
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			if (ExceptionUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				resource_tag = Conts.TAG_SYS_TIMEOUT;
			}
			logObj.setState_msg(ex.getMessage());
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		} finally {
			// log入库
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
		}
		return rets;
	}

	public String httpOtherClientPost(String route206,String vopath, String imagepath,
			 String trade_id,DataSource ds,Map<String, Object> rets,int negativeRate){
		logger.info("{} 其他渠道调用开始...", trade_id);
		String resource_tag = Conts.TAG_FOUND;
		String commondtext = propertyEngine.readById("shangtang_stateless_commondtext");
		int max_times = Integer.valueOf(propertyEngine.readById("shangtang_stateless_wait_times"));
		String pex = "0.jpg";
		String commond = ReadVideo.getVideoParam(trade_id, vopath, imagepath+pex, commondtext);
		try {  
			logger.info("{} 视频截取图片开始,执行命令:{}", trade_id,commond);
            Runtime runtime = Runtime.getRuntime();  
            runtime.exec(commond);//Runtime.exec(new String[] {"/bin/sh", "-c", command})
            logger.info("{} 视频截取图片完成", trade_id);
            int count1=1;
            String base_image = "";
			while(true){
				if(count1 > 0)
					logger.info("{} 轮询查询文件是否存在,轮询序号:{}",trade_id,count1);
				if(count1>=max_times){
					logger.info("{} 轮询查询文件是否存在主程序最大容忍次数已到,系统将自动停止！",trade_id);
					break;
				}
				if(count1%10==0){
					logger.info("{} 轮询查询文件为10的倍数,开始执行命令",trade_id);
					pex = count1+".jpg";
					commond = ReadVideo.getVideoParam(trade_id, vopath, imagepath+pex, commondtext);
					logger.info("{} 视频截取图片开始,执行命令:{}", trade_id,commond);
					runtime.exec(commond);
					logger.info("{} 轮询查询文件为10的倍数,执行命令完成", trade_id);
				}
				File file=new File(imagepath+pex);
				if(file.exists()){
					base_image = ImageUtils.encodeFileToBase64(imagepath+pex);
					break;
				}
				Thread.sleep(50);//模拟ds3处理需要时间
				count1++;
			}
			Map<String, Object> retdata = new HashMap<String, Object>();
			
			retdata.put("final_image", base_image);
			resource_tag = Conts.TAG_FOUND;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, "交易成功");
			rets.put(Conts.KEY_RET_TAG, new String[] { Conts.TAG_FOUND });
			logger.info("{} 静默活体检测水印照人脸比对成功!", trade_id);
        } catch (Exception e) {
        	logger.error("{} 视频截取失败：{}",trade_id,ExceptionUtil.getTrace(e));
            resource_tag = Conts.TAG_SYS_ERROR;
        } 
		return resource_tag;
	}
	public boolean isSuccess(Map<String, Object> params_out){
	    if (params_out == null)
	      return false;
	    CRSStatusEnum retstatus = CRSStatusEnum.valueOf(params_out.get(
	      "retstatus").toString());
	    return CRSStatusEnum.STATUS_SUCCESS.equals(retstatus);
	 }
	
	public String mkdirTodayPath(String basePath){
		String typePath = basePath + DateUtil.getSimpleDate(new Date(), "yyyyMMdd");
		if(!new File(typePath).exists())
			new File(typePath).mkdirs();
		return typePath;
	}
	public boolean isCheckSTLive(String properties,String acct_id){
		if(StringUtil.isEmpty(acct_id))
			return false;
		for(String property:properties.split(",")){
			String[] tmp = property.split(":");
			if(tmp[0].equals(acct_id) && "1".equals(tmp[1])){
				return true;
			}
		}
		return false;
	}
}
