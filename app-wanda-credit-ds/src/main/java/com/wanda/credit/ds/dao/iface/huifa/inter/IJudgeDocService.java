package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.JudgeDoc;

public interface IJudgeDocService extends IBaseService<JudgeDoc>{
	public void write(JudgeDoc judgeDoc);
}
