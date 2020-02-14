package com.wanda.credit.ds.action;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.base.counter.GlobalCounter;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.DataSourceService;
import com.wanda.credit.ds.client.dsconfig.DefaultConfigurableDataSourceRequestor;
import com.wanda.credit.ds.client.dsconfig.services.RedisAppender;
import com.wanda.credit.dsconfig.dao.IDsCfgDaoServie;
import com.wanda.credit.dsconfig.dao.domain.DsConfigRepoVO;
import com.wanda.credit.dsconfig.dao.domain.DsConfigVO;
import com.wanda.credit.dsconfig.loader.DsCfgHolder;
import com.wanda.credit.dsconfig.model.vo.DataSourceCfgInfo;
import com.wanda.credit.dsconfig.model.vo.ServiceScriptInfo;

import redis.clients.jedis.ShardedJedis;

/**
 * @Title: 数据源配置接口
 * @Package com.wanda.credit.ds.action
 * @Description:
 * @author changsheng.wu
 * @date 2017年4月26日 下午12:16:36
 * @version V1.0
 */
@Controller
@RequestMapping(value = "/dscfg")
public class DSConfigAction implements ApplicationContextAware {
	private final Logger logger = LoggerFactory.getLogger(DSConfigAction.class);
	@Autowired
	private DataSourceService dataSourceService;
	@Autowired
	private IDsCfgDaoServie dsCfgDaoService;
	@Autowired
	private DefaultConfigurableDataSourceRequestor dsrequstor;

	private ApplicationContext ctx;

	/** 创建新的数据源 */
	@RequestMapping(value = "/createDs", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> createDs(final HttpServletResponse response,
			final HttpServletRequest request, @RequestParam  String cfgjsn) {
		Map<String, Object> rets = null;
		try {
			cfgjsn = URLDecoder.decode(cfgjsn, "utf-8");
			response.setHeader("Access-Control-Allow-Origin", "*");
			String userCode = getLoginInfo(request);
			if (userCode == null) {
				rets = buildRetStatu("login_not", "请先登录");
				return rets;
			}
			if (StringUtil.isEmpty(cfgjsn)) {
				rets = buildRetStatu("config_empty", "配置内容不能为空");
				return rets;
			}
			/** validate */
			DataSourceCfgInfo cfgObj = buildDataSourceInfo(cfgjsn);
			if (cfgObj != null) {
				if (dsCfgDaoService.existDsCfg(cfgObj.getBasicInfo().getDsId())) {
					rets = buildRetStatu("dsid_exist", "数据源ID已经存在");
					return rets;
				}
				DsConfigVO vo = new DsConfigVO();
//				vo.setContent(cfgjsn);
/*				vo.setContent(JSON.toJSONString(JSONObject.parse(cfgjsn),
						SerializerFeature.PrettyFormat));
*/
				ObjectMapper mapper = new ObjectMapper();  
				mapper.configure(SerializationFeature.INDENT_OUTPUT, true);  
				vo.setContent(mapper.writeValueAsString(cfgObj));
				vo.setDs_id(cfgObj.getBasicInfo().getDsId().trim());
				vo.setDs_name(cfgObj.getBasicInfo().getDsName());
				vo.setCreator(userCode);
				dsCfgDaoService.add(vo);
				rets = buildRetStatu("00", "操作成功");
			} else {
				rets = buildRetStatu("config_error", "配置内容格式不正确");
			}
		} catch (Exception ex) {
			logger.error("error", ex);
			rets = buildRetStatu("sys_error", "系统异常");
		}
		return rets;
	}

	/** 修改数据源配置信息 */
	@RequestMapping(value = "/editDs/{dsId}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> editDs(final HttpServletResponse response,
			final HttpServletRequest request, @PathVariable String dsId,
			@RequestParam String cfgjsn) {
		Map<String, Object> rets = null;
		try {
			response.setHeader("Access-Control-Allow-Origin", "*");
			// 响应类型
			response.setHeader("Access-Control-Allow-Methods", "POST");
			// 响应头设置
			response.setHeader("Access-Control-Allow-Headers",
					"x-requested-with,content-type");
			cfgjsn = URLDecoder.decode(cfgjsn, "utf-8");
			logger.info(">>>>{}",cfgjsn);
			String userCode = getLoginInfo(request);
			if (userCode == null) {
				rets = buildRetStatu("login_not", "请先登录");
				return rets;
			}
			if (StringUtil.isEmpty(cfgjsn)) {
				rets = buildRetStatu("config_empty", "配置内容不能为空");
				return rets;
			}
			/** validate */
			DataSourceCfgInfo cfgObj = buildDataSourceInfo(cfgjsn);
			if (cfgObj != null) {
				DsConfigVO vo = new DsConfigVO();
//				vo.setContent(cfgjsn);
/*				vo.setContent(JSON.toJSONString(JSONObject.parse(cfgjsn),
						SerializerFeature.PrettyFormat));
*/
				ObjectMapper mapper = new ObjectMapper();  
				mapper.configure(SerializationFeature.INDENT_OUTPUT, true);  
				vo.setContent(mapper.writeValueAsString(cfgObj));
				vo.setDs_id(dsId);
				vo.setModifier(userCode);
				dsCfgDaoService.updateByDsId(vo);
				rets = buildRetStatu("00", "操作成功");
			} else {
				rets = buildRetStatu("config_error", "配置内容格式不正确");
			}
		} catch (Exception ex) {
			logger.error("error", ex);
			rets = buildRetStatu("sys_error", "系统异常");
		}

		return rets;
	}

	/** 删除数据源 */
	@RequestMapping(value = "/delDs/{dsId}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> delDs(final HttpServletResponse response,
			final HttpServletRequest request, @PathVariable String dsId)
			throws Exception {
		logger.info("dsid {},cfgjsn {}", dsId);
		Map<String, Object> rets = null;
		response.setHeader("Access-Control-Allow-Origin", "*");
		String userCode = getLoginInfo(request);
		if (userCode == null) {
			rets = buildRetStatu("login_not", "请先登录");
			return rets;
		}
		try {
			dsCfgDaoService.deleteByDsId(dsId);
		} catch (Exception ex) {
			logger.error("error", ex);
			rets = buildRetStatu("sys_error", "系统异常");
		}
		rets = buildRetStatu("00", "操作成功");
		return rets;
	}

	/** 删除仓库数据源 */
	@RequestMapping(value = "/delRepoDs/{dsId}/{version:.+}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> delRepoDs(final HttpServletResponse response,
			final HttpServletRequest request, @PathVariable String dsId
			, @PathVariable String version)
			throws Exception {
		Map<String, Object> rets = null;
		response.setHeader("Access-Control-Allow-Origin", "*");
		String userCode = getLoginInfo(request);
		if (userCode == null) {
			rets = buildRetStatu("login_not", "请先登录");
			return rets;
		}
		logger.info(" >>>> {}",dsId+"/"+version);
		try {
			dsCfgDaoService.deleteRepoDs(dsId,version);
		} catch (Exception ex) {
			logger.error("error", ex);
			rets = buildRetStatu("sys_error", "系统异常");
		}
		rets = buildRetStatu("00", "操作成功");
		return rets;
	}
	/** 查询数据源列表 */
	@RequestMapping(value = "/queryList", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> queryList(final HttpServletResponse response,
			final HttpServletRequest request) throws Exception {
		Map<String, Object> rets = null;
		response.setHeader("Access-Control-Allow-Origin", "*");
		String userCode = getLoginInfo(request);
		if (userCode == null) {
			rets = buildRetStatu("login_not", "请先登录");
			return rets;
		}
		try {
			List<DsConfigVO> list = dsCfgDaoService.queryDsList();
			rets = buildRetStatu("00", "操作成功");
			rets.put("data", list);
		} catch (Exception ex) {
			logger.error("error", ex);
			rets = buildRetStatu("sys_error", "系统异常");
		}
		return rets;
	}

	/** 查询数据源配置信息 */
	@RequestMapping(value = "/queryContent/{dsId}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> queryContent(final HttpServletResponse response,
			final HttpServletRequest request, @PathVariable String dsId)
			throws Exception {
		Map<String, Object> rets = null;
		response.setHeader("Access-Control-Allow-Origin", "*");
		String userCode = getLoginInfo(request);
		if (userCode == null) {
			rets = buildRetStatu("login_not", "请先登录");
			return rets;
		}
		try {
			String content = dsCfgDaoService.queryContent(dsId);
			rets = buildRetStatu("00", "操作成功");
			rets.put("data", content);
		} catch (Exception ex) {
			logger.error("error", ex);
			rets = buildRetStatu("sys_error", "系统异常");
		}
		return rets;
	}

	/** 查询历史版本记录 */
	@RequestMapping(value = "/queryHisList/{dsId}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> queryHisList(final HttpServletResponse response,
			final HttpServletRequest request, @PathVariable String dsId)
			throws Exception {
		Map<String, Object> rets = null;
		response.setHeader("Access-Control-Allow-Origin", "*");
		String userCode = getLoginInfo(request);
		if (userCode == null) {
			rets = buildRetStatu("login_not", "请先登录");
			return rets;
		}
		try {
			List<DsConfigRepoVO> list = dsCfgDaoService.queryDsHisList(dsId);
			rets = buildRetStatu("00", "操作成功");
			rets.put("data", list);
		} catch (Exception ex) {
			logger.error("error", ex);
			rets = buildRetStatu("sys_error", "系统异常");
		}
		return rets;
	}

	/** 查询历史版本的配置信息 */
	@RequestMapping(value = "/queryHisContent/{dsId}/{version:.+}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> queryHisContent(
			final HttpServletResponse response,
			final HttpServletRequest request, @PathVariable String dsId,
			@PathVariable String version) throws Exception {
		Map<String, Object> rets = null;
		response.setHeader("Access-Control-Allow-Origin", "*");
		String userCode = getLoginInfo(request);
		if (userCode == null) {
			rets = buildRetStatu("login_not", "请先登录");
			return rets;
		}
		try {
			String content = dsCfgDaoService.queryHisContent(dsId, version);
			rets = buildRetStatu("00", "操作成功");
			rets.put("data", content);
		} catch (Exception ex) {
			logger.error("error", ex);
			rets = buildRetStatu("sys_error", "系统异常");
		}
		return rets;
	}

	/** 发布一个版本 */
	@RequestMapping(value = "/publish/{dsId}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> publish(final HttpServletResponse response,
			final HttpServletRequest request, @PathVariable String dsId)
			throws Exception {
		Map<String, Object> rets = null;
		response.setHeader("Access-Control-Allow-Origin", "*");
		String userCode = getLoginInfo(request);
		if (userCode == null) {
			rets = buildRetStatu("login_not", "请先登录");
			return rets;
		}
		try {
			dsCfgDaoService.publish(dsId);
			rets = buildRetStatu("00", "操作成功");
		} catch (Exception ex) {
			logger.error("error", ex);
			rets = buildRetStatu("sys_error", "系统异常");
		}
		return rets;
	}
	
	/**恢复一个已经生效的版本到工作区间 */
	@RequestMapping(value = "/resume/{dsId}/{version:.+}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> resumeRepo(final HttpServletResponse response,
			final HttpServletRequest request, @PathVariable String dsId,
			@PathVariable String version) throws Exception {
		Map<String, Object> rets = null;
		response.setHeader("Access-Control-Allow-Origin", "*");
		String userCode = getLoginInfo(request);
		if (userCode == null) {
			rets = buildRetStatu("login_not", "请先登录");
			return rets;
		}
		try {
			String content = dsCfgDaoService.queryHisContent(dsId, version);
			DsConfigVO vo = new DsConfigVO();
			vo.setContent(content);
			vo.setDs_id(dsId);
			vo.setModifier(userCode);
			dsCfgDaoService.updateByDsId(vo);
			rets = buildRetStatu("00", "操作成功");
		} catch (Exception ex) {
			logger.error("error", ex);
			rets = buildRetStatu("sys_error", "系统异常");
		}
		return rets;
	}
	
	/** 生效一个一个版本 */
	@RequestMapping(value = "/effective/{dsId}/{version:.+}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> effective(final HttpServletResponse response,
			final HttpServletRequest request, @PathVariable String dsId,
			@PathVariable String version) throws Exception {
		Map<String, Object> rets = null;
		response.setHeader("Access-Control-Allow-Origin", "*");
		String userCode = getLoginInfo(request);
		if (userCode == null) {
			rets = buildRetStatu("login_not", "请先登录");
			return rets;
		}
		try {
			dsCfgDaoService.effective(dsId, version);
			rets = buildRetStatu("00", "操作成功");
		} catch (Exception ex) {
			logger.error("error", ex);
			rets = buildRetStatu("sys_error", "系统异常");
		}
		return rets;
	}

	
	/** 测试运行 */
	@RequestMapping(value = "/runtest/{dsId}/{version:.+}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> runtest(final HttpServletResponse response,
			final HttpServletRequest request, @PathVariable String dsId,
			@PathVariable String version,
			@RequestParam final String params) throws Exception {
		Map<String, Object> rets = null;
		Appender appender = null;
		response.setHeader("Access-Control-Allow-Origin", "*");
		String userCode = getLoginInfo(request);
		if (userCode == null) {
			rets = buildRetStatu("login_not", "请先登录");
			return rets;
		}
		String tradeId = StringUtil.getRandomNo();
//		String dsid_tmp = dsId + tradeId;
		String dsid_tmp = dsId;
		logger.info(" {} {}",tradeId,dsId+"/"+version);
		try {
			String content = null;
			if(StringUtil.isEmpty(version) || version.equals("-1")){
				 content = dsCfgDaoService.queryContent(dsId);			
			}else{
				 content = dsCfgDaoService.queryHisContent(dsId, version);
			}
			if (StringUtil.isEmpty(content)) {
				rets = buildRetStatu("ds_notfound", String.format("没有找到要运行的数据源 id:%s version:%s",dsId,version));
				return rets;
			}
			DataSourceCfgInfo obj = buildDataSourceInfo(content);
			obj.getBasicInfo().setDsId(dsid_tmp);
			DsCfgHolder.setDsCfg(dsid_tmp, obj);
			/** set services */
			setServiceRefForRunTest(obj);
			DataSource dsReqObj = buildDataSource(params);
			dsReqObj.setId(dsid_tmp);
			/** inject temporary log4j2 appender */
			appender = injectTemporaryLoggerAppender();
			logger.info("dsReqObj>>> {}",JSONObject.toJSONString(dsReqObj));
			Map<String, Object> ret = dsrequstor.request(tradeId, dsReqObj);
			rets = buildRetStatu("00", "操作成功");
			rets.put("data", ret);
		} catch (Exception ex) {
			logger.error("error", ex);
			rets = buildRetStatu("sys_error", "系统异常");
		} finally {
			DsCfgHolder.clear(dsid_tmp);
			removeTemporaryLoggerAppder(appender);
		}
		return rets;
	}

	/** 测试运行 */
	@RequestMapping(value = "/getlogs", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> runtest(final HttpServletResponse response,
			final HttpServletRequest request) {
		Map<String, Object> rets = null;
		rets = buildRetStatu("00", "操作成功");
		List<String> logs = getLogsInRedis();
		rets.put("data", logs);
		return rets;
	}

	private List<String> getLogsInRedis() {
		ShardedJedis jedis = GlobalCounter.getJedisPool().getResource();
		try {
			return jedis.lrange("loggerlist", 0, -1);

		} catch (Exception e) {
			logger.error("getLogsInRedis error", e);
		} finally {
			jedis.close();
		}
		return null;
	}

	private Appender injectTemporaryLoggerAppender() {
		LoggerConfig rootLoggerCfg = getRootLoggerConfig();
		Appender redisappender = createRedisAppender();
		rootLoggerCfg.addAppender(redisappender, Level.INFO, null);
		redisappender.start();
		return redisappender;
	}

	private Appender createRedisAppender() {
		RedisAppender.initJedisPool(GlobalCounter.getJedisPool());
		LoggerConfig rootLoggerCfg = getRootLoggerConfig();
		RedisAppender appender = RedisAppender.createAppender("redisAppender",
				false, rootLoggerCfg.getAppenders().get("RollingFile")
						.getLayout(), null);
		return appender;
	}

	private void removeTemporaryLoggerAppder(Appender appender) {
		if (appender != null)
			appender.stop();
		LoggerConfig root = getRootLoggerConfig();
		root.removeAppender("redisAppender");
	}

	private LoggerConfig getRootLoggerConfig() {
		LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager
				.getContext(false);
		Configuration log4jCfg = ctx.getConfiguration();
		LoggerConfig rootLoggerCfg = log4jCfg.getLoggerConfig("root");
		return rootLoggerCfg;
	}

	/** 登陆 */
	@RequestMapping(value = "/loginIn", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> login(final HttpServletResponse response,
			final HttpServletRequest request) throws Exception {
		GlobalCounter.init();
		Map<String, Object> rets = null;
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			boolean succ = login(name, password);
			if (!succ) {
				rets = buildRetStatu("login_fail", "登录失败");
				return rets;
			} else {
				String jssesionID = StringUtil.getRandomNo();
				GlobalCounter.registStr("jssesionID_" + jssesionID, name,
						30 * 60);
				rets = buildRetStatu("00", "操作成功");
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("jssesionID", jssesionID);
				rets.put("data", data);
			}
		} catch (Exception ex) {
			logger.error("error", ex);
			rets = buildRetStatu("sys_error", "系统异常");
		}

		return rets;
	}

	private String getLoginInfo(HttpServletRequest request)
			throws ServiceException {
		GlobalCounter.init();
		String jssesionID = request.getParameter("jssesionID");
		if (StringUtil.isEmpty(jssesionID))
			return null;
		String userCode = GlobalCounter.getString("jssesionID_" + jssesionID);
		if (!StringUtil.isEmpty(userCode)) {
			setSessionAge(jssesionID, userCode);
		}
		return userCode;
	}

	private void setSessionAge(String jssesionID, String userCode) {
		GlobalCounter.registStr("jssesionID_" + jssesionID, userCode, 30 * 60);
	}

	private boolean login(String usercode, String passwd) {
		return true;
	}

	private DataSource buildDataSource(String paramStr) {
		JSONObject paramMap = JSONObject.parseObject(paramStr);
		DataSource ds = new DataSource();
		List<Param> params = new ArrayList<Param>();
		ds.setParams_in(params);
		Param p;
		for (String key : paramMap.keySet()) {
			p = new Param();
			params.add(p);
			p.setId(key);
			p.setValue(paramMap.get(key));
		}
		return ds;
	}

	private void setServiceRefForRunTest(DataSourceCfgInfo cfgobj) {
		if (cfgobj.getSdkInfo() == null)
			return;
		/** 注册服务信息 */
		List<ServiceScriptInfo> services = cfgobj.getSdkInfo().getServices();
		if (services != null) {
			for (ServiceScriptInfo service : services) {
				if (StringUtils.isNotBlank(service.getBeanId())) {
					DsCfgHolder.setService(cfgobj.getBasicInfo().getDsId(),
							service.getRefName(),
							ctx.getBean(service.getBeanId()));
				}

			}
		}
	}

	private DataSourceCfgInfo buildDataSourceInfo(String cfgjsn) {
		ObjectMapper mapper = new ObjectMapper();
		DataSourceCfgInfo obj = null;
		try {
			obj = mapper.readValue(cfgjsn, DataSourceCfgInfo.class);
		} catch (Exception e) {
			logger.error("deserialize error", e);
		}
		return obj;
	}

	private Map<String, Object> buildRetStatu(String code, String msg) {
		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put("code", code);
		rets.put("msg", msg);
		return rets;
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		ctx = arg0;
	}
}
