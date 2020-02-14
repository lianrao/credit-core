package com.wanda.credit.ds.client.guoztFace;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.guozt.GuoZT_Face_Result;

public class BaseGuoZTSourceRequestor extends BaseDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(BaseGuoZTSourceRequestor.class);
	//构建出参
	public String buildOutPutParams(String trade_id,Map<String, Object> respResult,
			GuoZT_Face_Result result,Map<String, Object> rets){
		logger.info("{} 开始构建输出参数...",trade_id);
		String out_tag = Conts.TAG_SYS_ERROR;
		if("6".equals(result.getResult())){
			if(StringUtil.isEmpty(result.getUser_check_result())){
				if("11".equals(result.getUser_check_result()) || "-999".equals(result.getUser_check_result())
						|| "-990".equals(result.getUser_check_result())){
					logger.info("{} 国政通交易异常:{}",trade_id,result.getMessage());
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, result.getMessage());
					rets.put(Conts.KEY_RET_TAG, new String[]{out_tag});
					return out_tag;
				}
			}
		}
		if(!StringUtil.isEmpty(result.getUser_check_result())){
			if("5".equals(result.getResult()) && "3".equals(result.getVerify_result())){
				rets.clear();
				out_tag = Conts.TAG_MATCH;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
				rets.put(Conts.KEY_RET_MSG, result.getMessage());
				rets.put(Conts.KEY_RET_TAG, new String[]{out_tag});
				logger.warn("{} 比对失败:{}",trade_id,result.getMessage());
				return out_tag;
			}
			if("0".equals(result.getResult()) && 
					("0".equals(result.getVerify_result()) || "1".equals(result.getVerify_result()))){
				logger.info("{} 人脸识别成功",trade_id);
				rets.clear();
				out_tag = Conts.TAG_FOUND;
				respResult.put("rtn", 0);
				if(StringUtil.isEmpty(result.getVerify_similarity()) || "null".equals(result.getVerify_similarity())){
					respResult.put("pair_verify_similarity", 0);
				}else{
					respResult.put("pair_verify_similarity", Math.round(Float.valueOf(result.getVerify_similarity())));
				}		
				if("0".equals(result.getVerify_result())){
					respResult.put("pair_verify_result", "1");
				}else{
					respResult.put("pair_verify_result", "0");
				}
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_DATA, respResult);
				rets.put(Conts.KEY_RET_MSG, "人脸识别成功!");
				rets.put(Conts.KEY_RET_TAG, new String[]{out_tag});
	        }else{			
				switch(result.getUser_check_result()){
					case "1":
						rets.clear();
						out_tag = Conts.TAG_UNMATCH;
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
						rets.put(Conts.KEY_RET_MSG, "库中无此号");
						rets.put(Conts.KEY_RET_TAG, new String[]{out_tag});
						logger.warn("{}公安库中无此号",trade_id);
						break;
					case "2":
						rets.clear();
						out_tag = Conts.TAG_UNMATCH;
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
						rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
						rets.put(Conts.KEY_RET_TAG, new String[]{out_tag});
						logger.warn("{} 认证不一致",trade_id);
						break;
					case "4":
						rets.clear();
						out_tag = Conts.TAG_UNMATCH;
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
						rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
						rets.put(Conts.KEY_RET_TAG, new String[]{out_tag});
						logger.warn("{}国政通数据源厂商返回异常01!",trade_id);
						break;
					default :
						rets.clear();
						out_tag = Conts.TAG_UNFOUND;
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_YITU2_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "人脸识别失败,返回原因:" + result.getMessage());
						rets.put(Conts.KEY_RET_TAG, new String[]{out_tag});
						logger.error("{} 外部返回识别失败:{}", trade_id,result.getMessage());
						break;
				}
	        }
		}else{
			rets.clear();
			out_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, result.getMessage());
			rets.put(Conts.KEY_RET_TAG, new String[]{out_tag});
			logger.warn("{}国政通数据源厂商返回异常02!",trade_id);
		}
		return out_tag;
	}
}
