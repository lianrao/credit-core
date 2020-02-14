package com.wanda.credit.ds.client.guoztCredit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ModelUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.Guozt_degrees_check_result;
import com.wanda.credit.ds.dao.domain.Py_edu_college;
import com.wanda.credit.ds.dao.domain.Py_edu_degree;
import com.wanda.credit.ds.dao.domain.Py_edu_personBase;
import com.wanda.credit.ds.dao.iface.IGuoZCollageService;
import com.wanda.credit.ds.dao.iface.IGuoZTDegreesService;
@Service
public class BaseGuoZTPPdRequestor{
	private final static  Logger logger = LoggerFactory.getLogger(BaseGuoZTPPdRequestor.class);	
	@Resource(name="guoZTDegreesServiceImpl")
	private IGuoZTDegreesService degreesService;
	@Resource(name="guoZTCollageServiceImpl")
	private IGuoZCollageService collageService;
	@Autowired
	private IPropertyEngine propertyEngine;
	public  Map<String, Object>  getEduIncacheGuoZT(Map<String, String> mapStr,int date_num){
		final String prefix = mapStr.get("trade_id") + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String,String> photoData = new HashMap<String, String>();
		Map<String, Object> rets = new HashMap<String, Object>();
		String initTag = Conts.TAG_SYS_ERROR;
		int topDegreeCode = 0;
		String topDegree = "";
		Py_edu_personBase personBase = new Py_edu_personBase();
		Py_edu_degree degree = new Py_edu_degree();
		Py_edu_college college = new Py_edu_college();
		Map<String, Object> retdata =  new HashMap<String, Object>();
		Guozt_degrees_check_result degrees = new Guozt_degrees_check_result();
		String degreeLevel = "";
		if(mapStr.get("degreeLevel") != null){
			degreeLevel = mapStr.get("degreeLevel");
		}
		if("2".equals(mapStr.get("incache_flag"))){
			degrees = degreesService.getDegreesByTradeIdEver(mapStr.get("name"),mapStr.get("enCardNo"));
		}else{
			degrees = degreesService.getDegreesByTradeIdDate(mapStr.get("name"),mapStr.get("enCardNo"),date_num*30);
		}		
		if (degrees == null) {
			return null;
		}
		if("0".equals(degrees.getStatus1())){
			logger.info("{}缓存数据中存在学历查询数据！", new String[] { prefix });

			degrees.setTrade_id(mapStr.get("trade_id"));
			degrees.setCardNo(mapStr.get("cardNo"));
			if (StringUtils.isNotBlank(degrees.getImage_file())) {
				degrees.setPhoto("");
				photoData.put("fileId", degrees.getImage_file());
			}
			String riskInfo = "";
			
			personBase.setDocumentNo(mapStr.get("cardNo"));
			personBase.setName(mapStr.get("name"));
			personBase.setTrade_id(mapStr.get("trade_id"));
			personBase.setId(mapStr.get("trade_id"));
			personBase.setReportId(mapStr.get("trade_id"));
			personBase.setDegree(degrees.getEducationDegree());
			personBase.setSpecialty("");
			personBase.setCollege(degrees.getGraduate());
			personBase.setGraduateTime(degrees.getGraduateTime());
			personBase.setOriginalAddress("");
//			personBase.setVerifyResult(1);
			personBase.setGender(Integer.valueOf(mapStr.get("gender")));
			personBase.setAge(IdNOToAge(mapStr.get("cardNo")));
			
			personBase.setGraduateYears(EndTime(degrees.getGraduateTime()));
			personBase.setBirthday(mapStr.get("birthDays"));
			//学历信息					
			degree.setId(mapStr.get("trade_id"));
			degree.setCollege(degrees.getGraduate());
			degree.setStartTime("");
			degree.setGraduateTime(degrees.getGraduateTime());
			degree.setStudyStyle(degrees.getDstudyStyle());
			degree.setStudyType(degrees.getStudyStyle());
			degree.setSpecialty("");
			degree.setDegree(degrees.getEducationDegree());
			degree.setStudyResult(degrees.getStudyResult());
			if(StringUtils.isNotEmpty(degrees.getPhoto())){
				degree.setPhoto(degrees.getPhoto());
			}else{
				degree.setPhoto("");
			}
			if(StringUtils.isNotEmpty(degrees.getImage_file())){
				degree.setPhoto_id(degrees.getImage_file());
			}else{
				degree.setPhoto_id("");
			}	
			degree.setPhotoStyle("");
			degree.setTrade_id(mapStr.get("trade_id"));
			degree.setLevelNo("");
			degree.setIsKeySubject("");
			
			//学院信息					
			college=getColleage(degrees,prefix);
			college.setId(mapStr.get("trade_id"));
			college.setTrade_id(mapStr.get("trade_id"));
			if(degreeMap.get(personBase.getDegree())!=null){
				topDegreeCode = degreeMap.get(personBase.getDegree());
				topDegree = personBase.getDegree();
			}
			try {
				riskInfo = EduRiskInfo(EndTime(degrees.getGraduateTime()),mapStr.get("gender"),
						college.getIs211(),degrees.getStudyStyle(), degrees.getEducationDegree());
				if("100".equals(riskInfo)){
					riskInfo = "";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initTag = Conts.TAG_INCACHE_FOUND;	
			personBase.setRiskAndAdviceInfo("");
			retdata.put("topDegreeCode", topDegreeCode);
			retdata.put("topDegree", topDegree);
			retdata.put("personBase", personBase);
			retdata.put("degree", degree);
			retdata.put("college", college);
			rets.put("photoData", photoData);
			retdata.put("edu_result","0");
		}else if("1".equals(degrees.getStatus1())){
			logger.warn("{} 未查询到学历信息",mapStr.get("trade_id"));
			initTag = Conts.TAG_INCACHE_UNFOUND;	
			retdata.put("topDegreeCode", topDegreeCode);
			retdata.put("topDegree", topDegree);
			retdata.put("edu_result","1");
		}else{
			logger.info("{} 国政通学历查询失败!",prefix);
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "学历查询失败!");
			rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
			return rets;
		}
		if(StringUtils.isNotBlank(degreeLevel)){
			degreeLevel = degreeLevel.trim();
			retdata.put("degreeLevelCheck", check(degreeLevel,topDegree));
		}
		rets.put(Conts.KEY_RET_TAG, new String[]{initTag});
		rets.put(Conts.KEY_RET_DATA, retdata);
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		rets.put(Conts.KEY_RET_MSG, "采集成功!");
		return rets;
	}
	public  Py_edu_college getColleage(Guozt_degrees_check_result eduInfo,String prefix) {
		logger.info("{} 国政通学历查询本地院校信息开始...",  prefix);
    	Py_edu_college colleage1 = new Py_edu_college();
    	List<Map<String, Object>> collage = collageService.getCollageByName(eduInfo.getGraduate());
    	if(!StringUtils.isNotEmpty(eduInfo.getCreateDate()) && collage.size()>0){
    		logger.info("{} 国政通学历查询本地院校信息!",  prefix);    		
			colleage1.setCollege(eduInfo.getGraduate());
    		colleage1.setAddress(collage.get(0).get("ADDRESS")!=null?collage.get(0).get("ADDRESS").toString():"");
    		if(collage.get(0).get("CREATEDATE") != null){
    			colleage1.setCreateDate(collage.get(0).get("CREATEDATE").toString());
        		colleage1.setCreateYears(EndTime(collage.get(0).get("CREATEDATE").toString()));
    		}
    		colleage1.setColgCharacter("");
    		colleage1.setColgLevel(collage.get(0).get("COLGLEVEL")!=null?collage.get(0).get("COLGLEVEL").toString():"");
    		colleage1.setCharacter(collage.get(0).get("CHARACTER")!=null?collage.get(0).get("CHARACTER").toString():"");
    		colleage1.setColgType(collage.get(0).get("COLGTYPE")!=null?collage.get(0).get("COLGTYPE").toString():"");
    		colleage1.setPostDoctorNum("");
    		colleage1.setDoctorDegreeNum("");
    		colleage1.setMasterDegreeNum("");
    		colleage1.setAcademicianNum("");
    		colleage1.setIs211(collage.get(0).get("IS211")!=null?collage.get(0).get("IS211").toString():"");
    		colleage1.setManageDept(collage.get(0).get("MANAGEDEPT")!=null?collage.get(0).get("MANAGEDEPT").toString():"");
    		colleage1.setKeySubjectNum("");
    		colleage1.setCollegeOldName("");
    		colleage1.setScienceBatch("");
    		colleage1.setArtBatch(""); 		
		}else{
			logger.info("{} 国政通学历查询非本地院校信息!",  prefix);
			colleage1.setCollege(eduInfo.getGraduate());
			colleage1.setAddress(eduInfo.getSchoolCity());
			colleage1.setCreateDate(eduInfo.getCreateDate());
			colleage1.setCreateYears(eduInfo.getCreateYear());
			colleage1.setColgCharacter(eduInfo.getEducationApproach());
			colleage1.setColgLevel(eduInfo.getLevel());
			colleage1.setCharacter(eduInfo.getSchoolTrade());
			colleage1.setColgType(eduInfo.getSchoolNature());
			colleage1.setPostDoctorNum(eduInfo.getBshldzNum());
			colleage1.setDoctorDegreeNum(eduInfo.getBsdNum());
			colleage1.setMasterDegreeNum(eduInfo.getSsdNum());
			colleage1.setAcademicianNum(eduInfo.getAcademicianNum());
			colleage1.setIs211(eduInfo.getIs211());
			colleage1.setManageDept(eduInfo.getOrganization());
			colleage1.setKeySubjectNum(eduInfo.getZdxkNum());
			colleage1.setCollegeOldName("");
			colleage1.setScienceBatch("");
			colleage1.setArtBatch("");
		}	
    	return colleage1;
    }
	//学历综合评估分
    public  String EduRiskInfo(String gradeYear,String gender,String is211,String StudyStyle,String educationDegree) throws Exception{
    	String result = "";
    	Map<String, Object> model_param = new HashMap<String, Object>();
    	model_param.put("GRADEYEAR", gradeYear);
    	model_param.put("SEX", gender);
    	model_param.put("IS211", is211);
    	model_param.put("STUDYSTYLE", StudyStyle);
    	model_param.put("EDUDEGREE", educationDegree);
//    	logger.info("计算评估模型入参:\n" + JSONObject.toJSONString(model_param,true));
    	model_param = ModelUtils.calculate("M_credit_RiskModel", ParamUtil.convertParams(model_param),false);
//    	logger.info("计算评估模型返回结果:\n" + JSONObject.toJSONString(model_param,true));
    	if(model_param!=null){
    		result = extractValueFromResult("RESULT_FLAG",model_param).toString();
		}
    	return result;
    }
	//毕业年限
    public  String EndTime(String dates){
    	if(dates==null)
    		return "";
    	SimpleDateFormat df = new SimpleDateFormat("yyyy");
        String year=df.format(new Date());         
        int u=Integer.parseInt(year)-Integer.parseInt(dates);
        return String.valueOf(u);
    } 
  //根据身份证号输出年龄
    public  int IdNOToAge(String IdNO){
 	   int ageAdd = -1;
        int leh = IdNO.length();
        String year="";
        String month = "";
        String today = "";
        Date new_date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String date_new = df.format(new_date);
        String year_new = date_new.substring(0, 4);
        String mm=date_new.substring(4, 6);
        String dd=date_new.substring(6, 8);
        if (leh < 18) {
     	   year = "19"+IdNO.substring(6, 8);  
            month = IdNO.substring(8, 10); 
            today = IdNO.substring(10, 12);
        }else{
     	   year = IdNO.substring(6, 10);  
            month = IdNO.substring(10, 12); 
            today = IdNO.substring(12, 14);
        }       
        int u=Integer.parseInt(year_new)-Integer.parseInt(year);
        if(Integer.parseInt(mm)>=Integer.parseInt(month)){
     	   if(Integer.parseInt(dd)>=Integer.parseInt(today)){
     		   ageAdd = ageAdd+1;
            }  
        }
        return u+ageAdd;
    }
  //根据身份证号输出性别
    public Map<String, String> IdNOToSex(String cardNo,String prefix){
    	Map<String, String> result = new HashMap<String, String>();
    	int lengthStr = cardNo.length();
		String birthDays="";
		if(lengthStr == 15){
			if (Integer.parseInt(cardNo.substring(14, 15)) % 2 != 0)
				result.put("gender", "1");
			else
				result.put("gender", "2");
			birthDays="19"+cardNo.substring(6, 12);
			result.put("birthDays", birthDays);
		}else{
			 logger.info("{} 身份要素提取开始...", prefix);
			if (Integer.parseInt(cardNo.substring(16, 17)) % 2 != 0)
				result.put("gender", "1");
			else
				result.put("gender", "2");
			birthDays=cardNo.substring(6, 14);
			result.put("birthDays", birthDays);
		}
		return result;
    }
    public static Map<String, String> checkCode = new HashMap<String, String>();
	static {
		checkCode.put("0", "found");//查询有数据
		checkCode.put("1", "notfound");//查询无数据
		checkCode.put("2", "other");//数据源接口调用失败
	}
	public static Map<String, Integer> degreeMap = new HashMap<String, Integer>();
	static {
		//专科以下
		degreeMap.put("不详", 5);
		degreeMap.put("夜大电大函大普通班", 10);//6分
		//专科
		degreeMap.put("第二专科", 15);//6
		degreeMap.put("专科(高职)", 20);//6 大专
		degreeMap.put("专科", 25);//6 大专
		//本科
		degreeMap.put("专升本", 30);//13
		degreeMap.put("本科", 35);//13
		degreeMap.put("高升本", 40);
		degreeMap.put("第二本科", 45);//14
		degreeMap.put("第二学士学位", 50);//14
		//研究生
		degreeMap.put("研究生班", 55);//14
		degreeMap.put("硕士研究生", 60);//15
		//博士
		degreeMap.put("博士研究生", 65);//17
	} 
	/**学历一致性核查*/
	public String check(String degreeLevel, String topDegree) {
		
		/*11=博士研究生;10=硕士研究生,研究生班;6=本科,第二学士学位,高升本,专升本,第二本科,研究生班,夜大电大函大普通班;
          4=专科,专科(高职);other=不详*/
		String ruleStr = propertyEngine.readById("py_edu_degreelvl_map");
		if(StringUtils.isNotBlank(ruleStr)){
			String[] rules = ruleStr.split(";");
	        for(int i=0;i<rules.length;i++){
	        	String[] rule = rules[i].split("=");	        	
	        	if(degreeLevel.equals(rule[0])){
	        		if(rule[1].indexOf(topDegree) > -1){
	        			return "一致";
	        		}
	        	}else if("other".equals(rule[0])){
	        		if(rule[1].indexOf(topDegree) > -1){
	        			return "其他原因不一致";
	        		}
	        	}
	        }	
		}        
		return "不一致";
	}
	/**
	 * 从交易返回消息中提取参数值
	 * @param key
	 * @param params_out
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object extractValueFromResult(String key,
			Map<String, Object> params_out) {
		Object retdataObj = params_out.get(Conts.KEY_RET_DATA);
		if(retdataObj!=null){
			if(retdataObj instanceof Map){
				return ((Map<String, Object>) params_out.get(Conts.KEY_RET_DATA))
						.get(key);
			}
		}
		return null;
	}
}
