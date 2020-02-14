package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.OnlineOverdueList;

public interface IOnlineOverdueListService extends IBaseService<OnlineOverdueList>{
	public void write(OnlineOverdueList onlineOverdueList);
}
