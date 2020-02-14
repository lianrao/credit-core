package com.wanda.credit.ds.dao.impl.fahai;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;
import com.wanda.credit.ds.dao.iface.fahai.IFahaiPersonService;
@Service
@Transactional
public class FahaiPersonServiceImpl  extends BaseServiceImpl<Nciic_Check_Result> implements IFahaiPersonService{
	private final  Logger logger = LoggerFactory.getLogger(FahaiPersonServiceImpl.class);
	

	@Override
	public void batchSave(JSONArray result,final String trade_id) {
		 String sql = " INSERT INTO cpdb_ds.t_ds_fahai_person(trade_id,sortTimeString,sortTime,dataType,matchRatio,body,title,entryId) VALUES(?,?,?,?,?,?,?,?) ";
         final JSONArray params = new JSONArray();
         params.addAll(result);
         
         this.daoService.getJdbcTemplate().batchUpdate(sql,
                 new BatchPreparedStatementSetter() {
                     @Override
                     public int getBatchSize() {
                         return params.size();
                     }
                     @Override
                     public void setValues(PreparedStatement ps, int i)
                             throws SQLException {
                         ps.setString(1, trade_id);
                         String nowSort = "";
                         String sortdate = ((JSONObject)params.get(i)).getString("sortTimeString");
						try {
							Date date = new SimpleDateFormat("yyyy年MM月dd日").parse(sortdate);
							nowSort = new SimpleDateFormat("yyyy-MM-dd").format(date);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
                 		 
                         ps.setString(2, nowSort);
                         ps.setString(3, ((JSONObject)params.get(i)).getString("sortTime"));
                         ps.setString(4, ((JSONObject)params.get(i)).getString("dataType"));
                         ps.setString(5, ((JSONObject)params.get(i)).getString("matchRatio"));
                         ps.setString(6, ((JSONObject)params.get(i)).getString("body"));
                         ps.setString(7, ((JSONObject)params.get(i)).getString("title"));
                         ps.setString(8, ((JSONObject)params.get(i)).getString("entryId"));
                     }
                 });
	}
}
