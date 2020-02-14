/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年12月21日 上午9:39:28 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.qixinbao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.qxb.CorpListPojo;
import com.wanda.credit.ds.dao.iface.qixinbao.IQXBCorpListService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class QXBCorpListServiceImpl extends BaseServiceImpl<CorpListPojo> implements
		IQXBCorpListService {
	
	
}
