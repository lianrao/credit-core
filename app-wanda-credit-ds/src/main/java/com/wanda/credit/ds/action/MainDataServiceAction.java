package com.wanda.credit.ds.action;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.DataSourceService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Title: 征信数据源主服务接口
 * @Package com.wanda.credit.ds.action
 * @Description: 征信数据源主服务接口
 * @author chenglin.xiao
 * @date 2016年6月17日 下午12:16:36
 * @version V1.0
 */
@Controller
@RequestMapping(value="/inner/service/dataService")
public class MainDataServiceAction {
    private final Logger logger = LoggerFactory.getLogger(MainDataServiceAction.class);
    @Autowired
    private DataSourceService dataSourceService;
    @RequestMapping(value = "fetch/{trade_id}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> service(final HttpServletResponse response,
                                      final HttpServletRequest request,
                                      @PathVariable final String trade_id ,
                                      @RequestBody final DataSource ds)
            throws Exception {
        String prefix = trade_id +" "+ Conts.KEY_SYS_AGENT_HEADER; //流水号标识
        logger.info("{} 收到HTTP请求!",prefix);
        request.setCharacterEncoding("utf-8");
        Map<String, Object> rets = new HashedMap();
        if(!StringUtil.isEmpty(trade_id) && ds!=null){
            rets =  dataSourceService.fetch(trade_id,ds);
        }
        logger.info("{} HTTP请求处理完成!",prefix);
        return rets;
    }
    
    @RequestMapping(value = "testfetch", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> test(final HttpServletResponse response,
                                      final HttpServletRequest request,
                                      @RequestBody final DataSource ds)
            throws Exception {
    	String trade_id = StringUtil.getRandomNo();
        return this.service(response, request, trade_id, ds);
    }
}
