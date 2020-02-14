package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.Notice;

public interface INoticeService extends IBaseService<Notice>{
	public void write(Notice notice);
}
