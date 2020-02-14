package com.wanda.credit.ds.client.jiewei;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpBranch;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpManagement;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpNational;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpPenaltyInfos;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpReq;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpRsp;
import com.wanda.credit.ds.dao.domain.jiewei.JW_CorpShareholder;
import com.wanda.credit.ds.dao.domain.jiewei.SubReportBaseInfo;
import com.wanda.credit.ds.dao.iface.IJieWeiQueryCorpInfoService;

/**
 * 捷为工商信息查询数据源父类(解析响应XML报文)
 * @author changsheng.wu
 * */
@SuppressWarnings("rawtypes")
public class BaseJieWeiDataSourceRequestor extends BaseDataSourceRequestor {
	private final static Logger logger = LoggerFactory
			.getLogger(BaseJieWeiDataSourceRequestor.class);

	@Autowired
	private IJieWeiQueryCorpInfoService corpDaoService;

	@Autowired
	private IExecutorSecurityService synchExecutorService;

	
	/**90001 全国企业信息查询 子报告类型:40001,50001,50002,50003,50004*/
	public static Integer QUERYTYPE = 90001; 
		
	/**	
	 * 解析响应报文
	 * @rspXml 响应的xml报文
	 * @param  ctx 数据总线
     * @throws DocumentException 
	 */
	protected void parseRspXmlAndSave2DB(String rspXml,Map<String,Object> ctx) throws DocumentException {
		Document rspDoc = DocumentHelper.parseText(filtRspBody(rspXml));
		/**解析并保存报告 响应信息 report*/
		parseReportXmlAndSave2DB(rspDoc,ctx);
		
		/**解析并保存企业基本信息 corpBaseNationalInfo*/
		parseBaseXmlAndSave2DB(rspDoc,ctx);
		
		/**解析并保存企业股东信息 nationalCorpShareholderInfo*/
		parseShareholderXmlAndSave2DB(rspDoc,ctx);
		
		/**解析并保存企业主要人员信息 nationalCorpManagementInfo*/
		parseManagementXmlAndSave2DB(rspDoc,ctx);
		
		/**解析并保存企业分支机构信息 nationalCorpBranchInfo*/
		parseBranchXmlAndSave2DB(rspDoc,ctx);
		
		/**解析并保存企业行政处罚信息 penaltyInfos*/
		parsePenaltyXmlAndSave2DB(rspDoc,ctx);
		
	}

	/**
	 * 解析并保存企业行政处罚信息 penaltyInfos
	 * */
	private void parsePenaltyXmlAndSave2DB(Document rspDoc, Map<String,Object> ctx) {
		Element penaltyEle =  (Element)rspDoc.selectSingleNode("//penaltyInfos");
		JW_CorpPenaltyInfos penalty;
	    if(penaltyEle != null){
        	/**读取数据节点[可能有多个]*/
    		List itemEles =  penaltyEle.selectNodes("item");
    		if(itemEles != null && itemEles.size() > 0){
    			for(Object itemEle : itemEles){
    			  if(itemEle == null) continue;
    			  Element dataEle = (Element)itemEle;
    			  penalty = new JW_CorpPenaltyInfos();
    		    	/**读取子报告的公共属性信息*/
    	          parseSubReportAttrs(penaltyEle,penalty);
  		    	   /**读取子报告的数据信息*/
    	          penalty.setRecordNo(getNodeValue(dataEle,"recordNo"));
    	          penalty.setAffair(getNodeValue(dataEle,"affair"));
    	          penalty.setPanalty(getNodeValue(dataEle,"panalty"));
    	          penalty.setExecDepartment(getNodeValue(dataEle,"execDepartment"));
    	          penalty.setRecordDate(getNodeValue(dataEle,"recordDate"));

    	          penalty.setTrade_id((String)ctx.get("trade_id"));
    	    		/**存储主表ID*/
    	          penalty.setRefid((String)ctx.get("masterId"));
    	    		corpDaoService.addCorpPenaltyInfos(penalty);
    			}
    		}else{
    	    	/**如果没有具体的子报告数据 就只保存子报告的公共属性信息(查询状态)*/
    			penalty = new JW_CorpPenaltyInfos();
    	    	/**读取子报告的公共属性信息*/
            	parseSubReportAttrs(penaltyEle,penalty);
            	penalty.setTrade_id((String)ctx.get("trade_id"));
	    		/**存储主表ID*/
            	penalty.setRefid((String)ctx.get("masterId"));
	    		corpDaoService.addCorpPenaltyInfos(penalty);
    		}
	    }	
	}

	/**
	 * 解析并保存企业分支机构信息 nationalCorpBranchInfo
	 * */
	private void parseBranchXmlAndSave2DB(Document rspDoc, Map<String,Object> ctx) {
		Element branchEle =  (Element)rspDoc.selectSingleNode("//nationalCorpBranchInfo");
		JW_CorpBranch branch;
	    if(branchEle != null){
        	/**读取数据节点[可能有多个]*/
    		List itemEles =  branchEle.selectNodes("item");
    		if(itemEles != null && itemEles.size() > 0){
    			for(Object itemEle : itemEles){
    			  if(itemEle == null) continue;
    			  Element dataEle = (Element)itemEle;
    		      branch = new JW_CorpBranch();
    		    	/**读取子报告的公共属性及item节点数据信息*/
    	          parseSubReportAttrs(branchEle,branch);
    	          branch.setRegisterNo(getNodeValue(dataEle,"registerNo"));
    	          branch.setBranchName(getNodeValue(dataEle,"branchName"));
    	          branch.setRegisterDepartment(getNodeValue(dataEle,"registerDepartment"));
    	          branch.setTrade_id((String)ctx.get("trade_id"));
    	    		/**存储主表ID*/
    	          branch.setRefid((String)ctx.get("masterId"));
    	    		corpDaoService.addCorpBranch(branch);
    			}
    		}else{
    	    	/**如果没有具体的子报告数据 就只保存子报告的查询状态信息*/
    	    	branch = new JW_CorpBranch();
    	    	/**读取子报告的公共属性信息*/
            	parseSubReportAttrs(branchEle,branch);
	    		branch.setTrade_id((String)ctx.get("trade_id"));
	    		/**存储主表ID*/
	    		branch.setRefid((String)ctx.get("masterId"));
	    		corpDaoService.addCorpBranch(branch);
    		}
	    }	
	
}

	/**
	 * 解析并保存企业主要人员信息 nationalCorpManagementInfo
	 * */
	private void parseManagementXmlAndSave2DB(Document rspDoc, Map<String,Object> ctx) {
		Element mangerEle =  (Element)rspDoc.selectSingleNode("//nationalCorpManagementInfo");
		JW_CorpManagement manger;
		if(mangerEle != null){
	    	/**读取数据节点[可能有多个]*/
    		List itemEles =  mangerEle.selectNodes("item");
    		if(itemEles != null && itemEles.size() > 0){
	    	  for(Object itemEle : itemEles){
	    		if(itemEle == null) continue;
	    		Element dataEle = (Element)itemEle;
		    	manger = new JW_CorpManagement();
		    	/**读取子报告的公共属性及item节点数据信息*/
	        	parseSubReportAttrs(mangerEle,manger);
	    		manger.setName(getNodeValue(dataEle,"name"));
	    		manger.setRole(getNodeValue(dataEle,"role"));
	    		
	    		manger.setTrade_id((String)ctx.get("trade_id"));
	    		/**存储主表ID*/
	    		manger.setRefid((String)ctx.get("masterId"));
	    		corpDaoService.addCorpManagement(manger);
		      }
          }else{
		    	manger = new JW_CorpManagement();
		    	/**读取子报告的公共属性*/
	        	parseSubReportAttrs(mangerEle,manger);
	    		manger.setTrade_id((String)ctx.get("trade_id"));
	    		/**存储主表ID*/
	    		manger.setRefid((String)ctx.get("masterId"));
	    		corpDaoService.addCorpManagement(manger);
    	      }
    	}
	
}

	/**
	 * 解析并保存企业股东信息 nationalCorpShareholderInfo
	 * */
	private void parseShareholderXmlAndSave2DB(Document rspDoc, Map<String,Object> ctx) {
		Element holderEle =  (Element)rspDoc.selectSingleNode("//nationalCorpShareholderInfo");
	    if(holderEle != null){
	    	JW_CorpShareholder holder ;
	    	/**读取数据节点[可能有多个]*/
    		List itemEles =  holderEle.selectNodes("item");
    		if(itemEles != null && itemEles.size() > 0){
	    	  for(Object itemEle : itemEles){
	    		if(itemEle == null) continue;
	    		Element dataEle = (Element)itemEle;
		    	holder = new JW_CorpShareholder();
	    		/**读取子报告的公共属性及item节点数据信息*/
        	    parseSubReportAttrs(holderEle,holder);
	    		holder.setName(getNodeValue(dataEle,"name"));
	    		holder.setType(getNodeValue(dataEle,"type"));
	    		holder.setCertType(getNodeValue(dataEle,"certType"));
	    		try {
					holder.setCertID(synchExecutorService.encrypt(getNodeValue(dataEle,"certID")));
				} catch (Exception e) {
					logger.error("encrypt certID exception",e);
				}
	    		holder.setContributiveType(getNodeValue(dataEle,"contributiveType"));
	    		holder.setContributiveFund(getNodeValue(dataEle,"contributiveFund"));        	
    		
	    		holder.setTrade_id((String)ctx.get("trade_id"));
	    		/**存储主表ID*/
	    		holder.setRefid((String)ctx.get("masterId"));
	    		corpDaoService.addCorpShareholder(holder);
	           }
	      }else{
	    	  holder = new JW_CorpShareholder();
	    	  /**读取子报告的公共属性信息*/
      	      parseSubReportAttrs(holderEle,holder);
    		  holder.setTrade_id((String)ctx.get("trade_id"));
    		  /**存储主表ID*/
    		  holder.setRefid((String)ctx.get("masterId"));
    		  corpDaoService.addCorpShareholder(holder);
	    	  }
    	}
	    
}

	/**
	 * 解析并保存企业基本信息 corpBaseNationalInfo
	 * eg.
	 *  <corpBaseNationalInfo subReportType="40001" treatResult="1" errorMessage="">
     *  <item> <corpName>..</corpName><registerNo>..</registerNo><registDate>..</registDate>
     *  <artificialName>..</artificialName><status>..</status><registFund/>
     *  <manageRange>..</manageRange><openDate/><manageBeginDate/><manageEndDate/>
     *  <corpType>..</corpType><registerDepartment>..</registerDepartment>
     *  <registerAddress>..</registerAddress></item>
     */
	private void parseBaseXmlAndSave2DB(Document rspDoc, Map<String,Object> ctx) {
		Element baseEle =  (Element)rspDoc.selectSingleNode("//corpBaseNationalInfo");
        JW_CorpNational base = new JW_CorpNational();
//      <corpBaseNationalInfo subReportType="40001" treatResult="3" treatErrorCode="203" errorMessage="ip被封"/>
        if(baseEle != null ){
        	parseSubReportAttrs(baseEle,base);
    		/** 读取 数据节点 item*/
    		Element dataEle =  (Element)baseEle.selectSingleNode("item");
    		if(dataEle != null){
    			base.setCorpName(getNodeValue(dataEle,"corpName"));
    			base.setRegisterNo(getNodeValue(dataEle,"registerNo"));
    			base.setRegistDate(getNodeValue(dataEle,"registDate"));
    			base.setArtificialName(getNodeValue(dataEle,"artificialName"));
    			base.setStatus(getNodeValue(dataEle,"status"));
    			base.setRegistFund(getNodeValue(dataEle,"registFund"));
    			base.setManageRange(getNodeValue(dataEle,"manageRange"));
    			base.setOpenDate(getNodeValue(dataEle,"openDate"));
    			base.setManageBeginDate(getNodeValue(dataEle,"manageBeginDate"));
    			base.setManageEndDate(getNodeValue(dataEle,"manageEndDate"));
    			base.setCorpType(getNodeValue(dataEle,"corpType"));
    			base.setRegisterDepartment(getNodeValue(dataEle,"registerDepartment"));
    			base.setRegisterAddress(getNodeValue(dataEle,"registerAddress"));

    		}
    		base.setTrade_id((String)ctx.get("trade_id"));
    		/**关联主表ID*/
    		base.setRefid((String)ctx.get("masterId"));
    		
    		corpDaoService.addCorpNational(base);
         
        }
	
}

	/**
	 * 解析并保存报告 响应信息 report
	 * eg.
	 * <cisReports batNo="1" refID="12" receiveTime="20141106 21:01:43">
     * <cisReport reportID="1" buildEndTime="20141106 21:01:59">
     * </cisReport>
	 * */
	private void parseReportXmlAndSave2DB(Document rspDoc, Map<String,Object> ctx) {
        /**根节点*/ 
		Element reportsEle =  (Element)rspDoc.selectSingleNode("//cisReports");
		Element reportEle =  (Element)rspDoc.selectSingleNode("//cisReport");
        JW_CorpRsp rsp = new JW_CorpRsp();
        if(reportsEle != null){
    	    rsp.setBatNo(reportsEle.attributeValue("batNo"));
    	    rsp.setReceiveTime(str2SqlDate((String)reportsEle.attributeValue("receiveTime")));

    	    /**和请求的关联字段  值基本上和trade_id一致*/
    	    rsp.setRefid(reportsEle.attributeValue("refID"));        	
        }
	    if(reportEle != null){
		    rsp.setReportID(reportEle.attributeValue("reportID"));
		    rsp.setBuildEndTime(str2SqlDate((String)reportEle.attributeValue("buildEndTime")));
	    }
	    
		rsp.setTrade_id((String)ctx.get("trade_id"));
	    corpDaoService.addCorpRsp(rsp);
	    /**放入主表ID到数据总线*/
	    ctx.put("masterId",rsp.getId()); 
  }


	/**
	 * 保存请求信息到数据库
	 * @param ctx 数据总线 必须含有以下keys
	 * corpName 企业名称   同registerno 必输其一
	 * registerNo 营业证号或者社会统一信用好吗 同corpname必输其一
	 * province 省份代码
	 * trade_id
	 * timeoutflag 超时标志 可输
	 * */
	protected void saveRequest2DB(Map<String,Object> ctx) {
		JW_CorpReq req = new JW_CorpReq();
		req.setCorpName((String)ctx.get("corpName"));
		req.setProvince((String)ctx.get("province"));
		req.setQueryType(QUERYTYPE);
		req.setRegisterNo((String)ctx.get("registerNo"));
		req.setTrade_id((String)ctx.get("trade_id"));
		req.setRefid((String)ctx.get("trade_id"));
		req.setTimeoutflag((String)ctx.get("timeoutflag"));
	    corpDaoService.addCorpReq(req);
	}

	/**
	 * @param ctx 必须含有以下keys:
	 * corpName 企业名称
	 * registerNo 营业证号或者社会统一信用好吗
	 * province 省份代码
	 * trade_id 交易流水号
	 * 请求报文 eg. <?xml version="1.0" encoding="UTF-8"?> <conditions><condition
	 *     queryType="查询类型ID">
	 *     <item><name>corpName</name><value>被查询企业名称</value></item>
	 *     <item><name>province</name><value>被查询企业所在省市代码</value></item>
	 *     <item><name>regCode</name><value>被查询企业注册号</value></item>
	 *     <item><name>refID</name><value>***</value></item>
	 *     </condition></conditions>
	 */
	protected static String buildRequestMsg(Map<String,Object> ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<conditions><condition queryType=\""+QUERYTYPE+"\">");
		/**公司名*/
		if(StringUtils.isNotBlank((String)ctx.get("corpName"))){
			sb.append("<item><name>corpName</name><value>");
			sb.append(ctx.get("corpName")).append("</value></item>");
		}
		if(StringUtils.isNotBlank((String)ctx.get("registerNo"))){
			sb.append("<item><name>registerNo</name><value>");
			sb.append(ctx.get("registerNo")).append("</value></item>");			
		}
		sb.append("<item><name>province</name><value>");
		sb.append(ctx.get("province")).append("</value></item>");
		sb.append("<item><name>refID</name><value>");
		sb.append(ctx.get("trade_id")).append("</value></item>");
		sb.append("</condition></conditions>");
        return sb.toString();
	}

	private void parseSubReportAttrs(Element subReportEle,SubReportBaseInfo subReport){
    	/**读取报告属性字段*/
		subReport.setSubReportType(convertStr2Int((String)subReportEle.attributeValue("subReportType")));
		subReport.setTreatErrorCode(subReportEle.attributeValue("treatErrorCode"));
		subReport.setTreatResult(convertStr2Int((String)subReportEle.attributeValue("treatResult")));
		subReport.setErrorMessage(subReportEle.attributeValue("errorMessage"));

	}
	/**
	 * 报文格式过滤
	 * 
	 * @param rspBody
	 * @return
	 */
	protected String filtRspBody(String rspBody) {
		rspBody = rspBody.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
				"");
		return rspBody;
	}

	/**
	 * 用'yyyy-MM-dd HH:mm:ss' 格式日期
	 * eg. 20141106 21:01:59
	 * */
	private static DateFormat defaultDateFormatter = new SimpleDateFormat(
			"yyyyMMdd HH:mm:ss");
	
	public static Date str2UtilDate(String dateStr) {
		if(StringUtils.isNotBlank(dateStr)){
			try {
				return defaultDateFormatter.parse(dateStr);
			} catch (ParseException e) {
				logger.error("convert ["+dateStr +"] to date exception!!", e);
			}
		}
		return null;
	}
	
	public static java.sql.Timestamp str2SqlDate(String dateStr) {
		Date utilDate = str2UtilDate(dateStr);
		if(utilDate != null){
		   return new java.sql.Timestamp(utilDate.getTime());
		}else{
			return null;
		}
	}
	
	public static String sqlDate2Str(java.sql.Timestamp time) {
          return defaultDateFormatter.format(new Date(time.getTime()));
	}
	
	private Integer convertStr2Int(String intStr) {
		if (StringUtils.isNotBlank(intStr)) {
			return Integer.parseInt(intStr);
		} else {
			return null;
		}
	}
   private String getNodeValue(Node node){
	   if(node != null) return node.getText();
	   return null;
   }
   
   private String getNodeValue(Element ele,String childname){
	   Node node =ele.selectSingleNode(childname);
	   return getNodeValue(node);
   }
   
}
