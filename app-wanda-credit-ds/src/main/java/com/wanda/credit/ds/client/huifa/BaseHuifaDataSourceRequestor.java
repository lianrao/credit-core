package com.wanda.credit.ds.client.huifa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.huifa.AdminLaw;
import com.wanda.credit.ds.dao.domain.huifa.AdminPunishForm;
import com.wanda.credit.ds.dao.domain.huifa.CancelInfo;
import com.wanda.credit.ds.dao.domain.huifa.CourtInfo;
import com.wanda.credit.ds.dao.domain.huifa.DishonestPer;
import com.wanda.credit.ds.dao.domain.huifa.ExecuteOtherInfo;
import com.wanda.credit.ds.dao.domain.huifa.FeeTotal;
import com.wanda.credit.ds.dao.domain.huifa.IdentifyDishonestTax;
import com.wanda.credit.ds.dao.domain.huifa.IllegalCase;
import com.wanda.credit.ds.dao.domain.huifa.JudgeDoc;
import com.wanda.credit.ds.dao.domain.huifa.LimitExit;
import com.wanda.credit.ds.dao.domain.huifa.LimitHighConsum;
import com.wanda.credit.ds.dao.domain.huifa.MissingTaxInfo;
import com.wanda.credit.ds.dao.domain.huifa.Notice;
import com.wanda.credit.ds.dao.domain.huifa.OldLaiInfo;
import com.wanda.credit.ds.dao.domain.huifa.OnlineOverdueList;
import com.wanda.credit.ds.dao.domain.huifa.OverdueInfo;
import com.wanda.credit.ds.dao.domain.huifa.PerformPub;
import com.wanda.credit.ds.dao.domain.huifa.RegisterInfo;
import com.wanda.credit.ds.dao.domain.huifa.ReminderNot;
import com.wanda.credit.ds.dao.domain.huifa.TaxNotice;
import com.wanda.credit.ds.dao.iface.huifa.inter.IAdminLawService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IAdminPunishFormService;
import com.wanda.credit.ds.dao.iface.huifa.inter.ICancelInfoService;
import com.wanda.credit.ds.dao.iface.huifa.inter.ICourtInfoService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IDishonestPerService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IExecuteOtherInfoService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IFeeTotalService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IIdentifyDishonestTaxService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IIllegalCaseService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IJudgeDocService;
import com.wanda.credit.ds.dao.iface.huifa.inter.ILimitExitService;
import com.wanda.credit.ds.dao.iface.huifa.inter.ILimitHighConsumService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IMissingTaxInfoService;
import com.wanda.credit.ds.dao.iface.huifa.inter.INoticeService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IOldLaiInfoService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IOnlineOverdueListService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IOverdueInfoService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IPerformPubService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IRegisterInfoService;
import com.wanda.credit.ds.dao.iface.huifa.inter.IReminderNotService;
import com.wanda.credit.ds.dao.iface.huifa.inter.ITaxNoticeService;

public class BaseHuifaDataSourceRequestor extends BaseDataSourceRequestor{
	// 信息类型对应关系map
	Map<String, Object> infoType = new HashMap<String, Object>();
	@Autowired
	protected DaoService daoService;
	@Autowired
	protected IAdminLawService adminLawService;
	@Autowired
	protected IAdminPunishFormService adminPunishFormService;
	@Autowired
	protected ICancelInfoService cancelInfoService;
	@Autowired
	protected ICourtInfoService courtInfoService;
	@Autowired
	protected IDishonestPerService dishonestPerService;
	@Autowired
	protected IExecuteOtherInfoService executeOtherInfoService;
	@Autowired
	protected IIdentifyDishonestTaxService identifyDishonestTaxService;
	@Autowired
	protected IIllegalCaseService illegalCaseService;
	@Autowired
	protected IJudgeDocService judgeDocService;
	@Autowired
	protected ILimitExitService limitExitService;
	@Autowired
	protected ILimitHighConsumService limitHighConsumService;
	@Autowired
	protected IMissingTaxInfoService missingTaxInfoService;
	@Autowired
	protected INoticeService noticeService;
	@Autowired
	protected IOldLaiInfoService oldLaiInfoService;
	@Autowired
	protected IOnlineOverdueListService onlineOverdueListService;
	@Autowired
	protected IOverdueInfoService overdueInfoService;
	@Autowired
	protected IPerformPubService performPubService;
	@Autowired
	protected IRegisterInfoService registerInfoService;
	@Autowired
	protected IReminderNotService reminderNotService;
	@Autowired
	protected ITaxNoticeService taxNoticeService;

	@Autowired
	protected IFeeTotalService feeTotalService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	protected void save(String success ,String message ,String trade_id,String refid,
			List<Map<String, Object>>  listInfo,Map<String, Object> respMap,
			Map<String,Object> rets,DataSourceLogVO logObj,List<String> tags,String acct_id) throws Exception{
		if ("s".equals(success) && listInfo != null && listInfo.size() > 0){
			/**方法级的 加密数据缓存*/
		    Map<String,String> crptCache = new HashMap<String, String>();
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").excludeFieldsWithoutExposeAnnotation().create();
			int newRecord = 0;
			Map<String, Object> params = null;
			for (Map<String, Object> map : listInfo) {
					if (map != null && map.size() > 0) {
						Integer keyId = map.get("keyid")==null?null:Integer.parseInt(map.get("keyid").toString());//wcs add
						String type = map.get("type")==null?null:(String)map.get("type");
						String json = map.get("json")==null?null:(String)map.get("json");
						if(!"".equals(type)&&type!=null&&!"".equals(json)&&json!=null){
							if (infoType.get(type) instanceof AdminLaw) {
								AdminLaw adminLaw = gson.fromJson(json,AdminLaw.class);
								adminLaw.setQueryType(type);
								adminLaw.setTrade_id(trade_id);
								adminLaw.setRefId(refid);
								String cidorcode = adminLaw.getCidorcode();
								String taxnum = adminLaw.getTaxnum();
								adminLaw.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								adminLaw.setTaxnum(fetchEncryptedValue(taxnum,crptCache));
			                    adminLawService.write(adminLaw);
								adminLaw.setCidorcode(cidorcode);
								adminLaw.setTaxnum(taxnum);
							} else if (infoType.get(type) instanceof AdminPunishForm) {
								AdminPunishForm adminPunishForm = gson.fromJson(json, AdminPunishForm.class);
								adminPunishForm.setQueryType(type);
								adminPunishForm.setTrade_id(trade_id);
								adminPunishForm.setRefId(refid);
								String cidorcode = adminPunishForm.getCidorcode();
								String taxnum = adminPunishForm.getTaxnum();
								adminPunishForm.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								adminPunishForm.setTaxnum(fetchEncryptedValue(taxnum,crptCache));								
								adminPunishFormService.write(adminPunishForm);
								adminPunishForm.setCidorcode(cidorcode);
								adminPunishForm.setTaxnum(taxnum);
							} else if (infoType.get(type) instanceof CancelInfo) {
								CancelInfo cancelInfo = gson.fromJson(json,CancelInfo.class);
								cancelInfo.setQueryType(type);
								cancelInfo.setTrade_id(trade_id);
								cancelInfo.setRefId(refid);
								String cidorcode = cancelInfo.getCidorcode();
								String taxnum = cancelInfo.getTaxnum();
								cancelInfo.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								cancelInfo.setTaxnum(fetchEncryptedValue(taxnum,crptCache));							
								cancelInfoService.write(cancelInfo);
								cancelInfo.setCidorcode(cidorcode);
								cancelInfo.setTaxnum(taxnum);							
							} else if (infoType.get(type) instanceof CourtInfo) {
								CourtInfo courtInfo = gson.fromJson(json,CourtInfo.class);
								courtInfo.setQueryType(type);
								courtInfo.setTrade_id(trade_id);
								courtInfo.setRefId(refid);
								courtInfoService.write(courtInfo);
							} else if (infoType.get(type) instanceof DishonestPer) {
								DishonestPer dishonestPer = gson.fromJson(json, DishonestPer.class);
								dishonestPer.setQueryType(type);
								dishonestPer.setTrade_id(trade_id);
								dishonestPer.setRefId(refid);
								String cidorcode = dishonestPer.getCidorcode();
								dishonestPer.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								dishonestPerService.write(dishonestPer);
								dishonestPer.setCidorcode(cidorcode);

							} else if (infoType.get(type) instanceof ExecuteOtherInfo) {
								ExecuteOtherInfo executeOtherInfo = gson.fromJson(json, ExecuteOtherInfo.class);
								executeOtherInfo.setQueryType(type);
								executeOtherInfo.setTrade_id(trade_id);
								executeOtherInfo.setRefId(refid);
								String cidorcode = executeOtherInfo.getCidorcode();
								executeOtherInfo.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								executeOtherInfoService.write(executeOtherInfo);
								executeOtherInfo.setCidorcode(cidorcode);

							} else if (infoType.get(type) instanceof IdentifyDishonestTax) {
								IdentifyDishonestTax identifyDishonestTax = gson.fromJson(json,IdentifyDishonestTax.class);
								identifyDishonestTax.setQueryType(type);
								identifyDishonestTax.setTrade_id(trade_id);
								identifyDishonestTax.setRefId(refid);
								String cidorcode = identifyDishonestTax.getCidorcode();
								String taxnum = identifyDishonestTax.getTaxnum();
								identifyDishonestTax.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								identifyDishonestTax.setTaxnum(fetchEncryptedValue(taxnum,crptCache));							
								identifyDishonestTaxService.write(identifyDishonestTax);
								identifyDishonestTax.setCidorcode(cidorcode);
								identifyDishonestTax.setTaxnum(taxnum);							

							} else if (infoType.get(type) instanceof IllegalCase) {
								IllegalCase illegalCase = gson.fromJson(json,IllegalCase.class);
								illegalCase.setQueryType(type);
								illegalCase.setTrade_id(trade_id);
								illegalCase.setRefId(refid);
								String cidorcode = illegalCase.getCidorcode();
								String taxnum = illegalCase.getTaxnum();
								illegalCase.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								illegalCase.setTaxnum(fetchEncryptedValue(taxnum,crptCache));							
								illegalCaseService.write(illegalCase);
								illegalCase.setCidorcode(cidorcode);
								illegalCase.setTaxnum(taxnum);							

							} else if (infoType.get(type) instanceof JudgeDoc) {
								JudgeDoc judgeDoc = gson.fromJson(json,JudgeDoc.class);
								judgeDoc.setQueryType(type);
								judgeDoc.setTrade_id(trade_id);
								judgeDoc.setRefId(refid);
								String cidorcode = judgeDoc.getCidorcode();
								judgeDoc.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								judgeDocService.write(judgeDoc);
								judgeDoc.setCidorcode(cidorcode);
								Map<String, Object> mapObj=gson.fromJson(json, HashMap.class);
								mapObj.remove("contenthref");
								mapObj.remove("pdfhref");
								mapObj.remove("ownfile");
								map.put("json", gson.toJson(mapObj));
							} else if (infoType.get(type) instanceof LimitExit) {
								LimitExit limitExit = gson.fromJson(json,LimitExit.class);
								limitExit.setQueryType(type);
								limitExit.setTrade_id(trade_id);
								limitExit.setRefId(refid);
								String cidorcode = limitExit.getCidorcode();
								limitExit.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								limitExitService.write(limitExit);
								limitExit.setCidorcode(cidorcode);

							} else if (infoType.get(type) instanceof LimitHighConsum) {
								LimitHighConsum limitHighConsum = gson.fromJson(json, LimitHighConsum.class);
								limitHighConsum.setQueryType(type);
								limitHighConsum.setTrade_id(trade_id);
								limitHighConsum.setRefId(refid);
								String cidorcode = limitHighConsum.getCidorcode();
								limitHighConsum.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								limitHighConsumService.write(limitHighConsum);
								limitHighConsum.setCidorcode(cidorcode);

							} else if (infoType.get(type) instanceof MissingTaxInfo) {
								MissingTaxInfo missingTaxInfo = gson.fromJson(json, MissingTaxInfo.class);
								missingTaxInfo.setQueryType(type);
								missingTaxInfo.setTrade_id(trade_id);
								missingTaxInfo.setRefId(refid);
								String cidorcode = missingTaxInfo.getCidorcode();
								String taxnum = missingTaxInfo.getTaxnum();
								missingTaxInfo.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								missingTaxInfo.setTaxnum(fetchEncryptedValue(taxnum,crptCache));							
								missingTaxInfoService.write(missingTaxInfo);
								missingTaxInfo.setCidorcode(cidorcode);
								missingTaxInfo.setTaxnum(taxnum);							

							} else if (infoType.get(type) instanceof Notice) {
								Notice notice = gson.fromJson(json,Notice.class);
								notice.setQueryType(type);
								notice.setTrade_id(trade_id);
								notice.setRefId(refid);
								String cidorcode = notice.getCidorcode();
								notice.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								noticeService.write(notice);
								notice.setCidorcode(cidorcode);

							} else if (infoType.get(type) instanceof OldLaiInfo) {
								OldLaiInfo oldLaiInfo = gson.fromJson(json,OldLaiInfo.class);
								oldLaiInfo.setQueryType(type);
								oldLaiInfo.setTrade_id(trade_id);
								oldLaiInfo.setRefId(refid);
								String cidorcode = oldLaiInfo.getCidorcode();
								oldLaiInfo.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								oldLaiInfoService.write(oldLaiInfo);
								oldLaiInfo.setCidorcode(cidorcode);

							} else if (infoType.get(type) instanceof OnlineOverdueList) {
								OnlineOverdueList onlineOverdueList = gson.fromJson(json, OnlineOverdueList.class);
								onlineOverdueList.setQueryType(type);
								onlineOverdueList.setTrade_id(trade_id);
								onlineOverdueList.setRefId(refid);
								String cidorcode = onlineOverdueList.getCidorcode();
								String phone = onlineOverdueList.getPhone();
								String workunitphone = onlineOverdueList.getWorkunitphone();
								String othercidorcode = onlineOverdueList.getOthercidorcode();
								String otherphone = onlineOverdueList.getOtherphone();

								onlineOverdueList.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								onlineOverdueList.setPhone(fetchEncryptedValue(phone,crptCache));
								onlineOverdueList.setWorkunitphone(fetchEncryptedValue(workunitphone,crptCache));
								onlineOverdueList.setOthercidorcode(fetchEncryptedValue(othercidorcode,crptCache));
								onlineOverdueList.setOtherphone(fetchEncryptedValue(otherphone,crptCache));

								onlineOverdueListService.write(onlineOverdueList);
								
								onlineOverdueList.setCidorcode(cidorcode);
								onlineOverdueList.setPhone(phone);
								onlineOverdueList.setWorkunitphone(workunitphone);
								onlineOverdueList.setOthercidorcode(othercidorcode);
								onlineOverdueList.setOtherphone(otherphone);

							} else if (infoType.get(type) instanceof OverdueInfo) {
								OverdueInfo overdueInfo = gson.fromJson(json,OverdueInfo.class);
								overdueInfo.setQueryType(type);
								overdueInfo.setTrade_id(trade_id);
								overdueInfo.setRefId(refid);
								String cidorcode = overdueInfo.getCidorcode();
								String taxnum = overdueInfo.getTaxnum();
								overdueInfo.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								overdueInfo.setTaxnum(fetchEncryptedValue(taxnum,crptCache));					
								overdueInfoService.write(overdueInfo);
								overdueInfo.setCidorcode(cidorcode);
								overdueInfo.setTaxnum(taxnum);							

							} else if (infoType.get(type) instanceof PerformPub) {
								PerformPub performPub = gson.fromJson(json,PerformPub.class);
								performPub.setQueryType(type);
								performPub.setTrade_id(trade_id);
								performPub.setRefId(refid);
								String cidorcode = performPub.getCidorcode();
								performPub.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								performPubService.write(performPub);
								performPub.setCidorcode(cidorcode);

							} else if (infoType.get(type) instanceof RegisterInfo) {
								RegisterInfo registerInfo = gson.fromJson(json, RegisterInfo.class);
								registerInfo.setQueryType(type);
								registerInfo.setTrade_id(trade_id);
								registerInfo.setRefId(refid);
								registerInfoService.write(registerInfo);
							} else if (infoType.get(type) instanceof ReminderNot) {
								ReminderNot reminderNot = gson.fromJson(json,ReminderNot.class);
								reminderNot.setQueryType(type);
								reminderNot.setTrade_id(trade_id);
								reminderNot.setRefId(refid);
								String cidorcode = reminderNot.getCidorcode();
								reminderNot.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								String phone = reminderNot.getPhone();
								reminderNot.setPhone(fetchEncryptedValue(phone,crptCache));
								reminderNotService.write(reminderNot);
								reminderNot.setCidorcode(cidorcode);
								reminderNot.setPhone(phone);

							} else if (infoType.get(type) instanceof TaxNotice) {
								TaxNotice taxNotice = gson.fromJson(json,TaxNotice.class);
								taxNotice.setQueryType(type);
								taxNotice.setTrade_id(trade_id);
								taxNotice.setRefId(refid);
								String cidorcode = taxNotice.getCidorcode();
								String taxnum = taxNotice.getTaxnum();
								taxNotice.setCidorcode(fetchEncryptedValue(cidorcode,crptCache));
								taxNotice.setTaxnum(fetchEncryptedValue(taxnum,crptCache));					
								taxNoticeService.write(taxNotice);
								taxNotice.setCidorcode(cidorcode);
								taxNotice.setTaxnum(taxnum);					
							}
							
							if (params == null){
								params = new HashMap<String, Object>();
							}else{
								params.clear();
							}
							String hql ="select count(t.id) from FeeTotal t where keyId=:keyId "
									+ "and queryType=:queryType and acct_id=:acct_id";
							params.put("keyId", keyId.toString());
							params.put("queryType", type);
							params.put("acct_id", acct_id);
							Long count = daoService.findOneByHQL(hql, params);
							if(count == null || count==0){
								newRecord++;
							}
							/** 存储 FeeTotal 对象 wcs add */
							FeeTotal feeTotal = new FeeTotal();
							feeTotal.setKeyId(keyId.toString());
							feeTotal.setRefId(refid);
							feeTotal.setTrade_id(trade_id);
							feeTotal.setQueryType(type);
							feeTotal.setUse_table(getTableNameFromClassAnnotation(infoType.get(type)));
							feeTotal.setAcct_id(acct_id);
							feeTotalService.write(feeTotal);
						}
					}
			}
			String bizCode = "records:" + listInfo.size() + ";new_records:" + newRecord;
			logObj.setBiz_code1(bizCode);
			tags.add("found_records:" + listInfo.size());
			tags.add("found_newrecords:" + newRecord);			
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, respMap);
			rets.put(Conts.KEY_RET_MSG, "交易成功!");
		}else if("e".equals(success)){
			rets.clear();
			tags.add(Conts.TAG_SYS_ERROR);
			rets.put(Conts.KEY_RET_DATA, respMap);
			rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "查询失败! 失败描述:"+ message);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			logObj.setState_msg(message);
		}else if(listInfo == null || listInfo.size() == 0){
			tags.add(Conts.TAG_UNFOUND);
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, respMap);
			rets.put(Conts.KEY_RET_MSG, "交易成功!");
			logObj.setBiz_code1("unfound");
		}
	}
	
	/**加密数据缓存 避免cidorcode的多次加密操作
	 * @throws Exception */
	private String fetchEncryptedValue(String source,
			Map<String, String> crptCachedMap) throws Exception {
		if(StringUtils.isBlank(source))return source;
		String crptedValue = crptCachedMap.get(source);
		if(crptedValue == null){
			crptedValue= synchExecutorService.encrypt(source);
			crptCachedMap.put(source,crptedValue);
		}
		return crptedValue;
	}
	public BaseHuifaDataSourceRequestor() {
		super();
		final String  s1="行政执法-责令限期改正";
		final String  s2="税务信息-案件查处-行政处罚决定书";
		final String  s3="税务信息-税务登记-注销信息";
		final String  s4="审判流程-开庭信息";
		final String  s5="执行信息-失信老赖-失信被执行人";
		final String  s6="执行信息-执行公开-其他执行信息";
		final String  s7="税务信息-认定认证-失信纳税人";
		final String  s8="税务信息-案件查处-违法案件";
		final String  s9="判决文书";
		final String  s10="执行信息-执行惩戒-限制出境被执行人";
		final String  s11="执行信息-执行惩戒-限制高消费被执行人";
		final String  s12="税务信息-税务监管-失踪纳税人";
		final String  s13="审判流程-送达信息-通知公告";
		final String  s14="执行信息-失信老赖-老赖信息";
		final String  s15="催收索赔-网贷逾期名单";
		final String  s16="税务信息-税务监管-逾期信息";
		final String  s17="执行信息-执行公开-最高法执行";
		final String  s18="审判流程-立案信息";
		final String  s19="催收索赔-催欠公告";
		final String  s20="税务信息-税务监管-欠税公告";
		infoType.put(s1, new AdminLaw());
		infoType.put(s2, new AdminPunishForm());
		infoType.put(s3, new CancelInfo());
		infoType.put(s4, new CourtInfo());
		infoType.put(s5, new DishonestPer());
		infoType.put(s6, new ExecuteOtherInfo());
		infoType.put(s7, new IdentifyDishonestTax());
		infoType.put(s8, new IllegalCase());
		infoType.put(s9, new JudgeDoc());
		infoType.put(s10, new LimitExit());
		infoType.put(s11, new LimitHighConsum());
		infoType.put(s12, new MissingTaxInfo());
		infoType.put(s13, new Notice());
		infoType.put(s14, new OldLaiInfo());
		infoType.put(s15, new OnlineOverdueList());
		infoType.put(s16, new OverdueInfo());
		infoType.put(s17, new PerformPub());
		infoType.put(s18, new RegisterInfo());
		infoType.put(s19, new ReminderNot());
		infoType.put(s20, new TaxNotice());
	}
	
	/**
	 * 获取领域类的TABLE注解信息(类和table的 映射)
	 * wcs add
	 * */
	private String getTableNameFromClassAnnotation(Object obj){
		if(obj != null){
		javax.persistence.Table table = obj.getClass().getAnnotation(javax.persistence.Table.class);
		   if(table != null) 
			   return table.name();
		}
		return null;
	} 
	
	/**从本地缓存中取加密后的字段信息*/
	
	
	public static void main(String[] args) {
		Long count = new Long(0);
	if(count == null || count.intValue()==0){
		System.out.println(">>"+count);
	}
	if(count == null || count==0){
		System.out.println(">>"+count);
	}
	}
	
}
