/**   
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author xiaobin.hou  
 * @date 2016年11月10日 上午11:32:32 
 * @version V1.0   
 */
package com.wanda.credit.ds.dao.iface.impl.phjr;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.phjr.STFileUpload;
import com.wanda.credit.ds.dao.iface.phjr.ISTFileUploadService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class STFileUploadServiceImpl extends
		BaseServiceImpl<STFileUpload> implements ISTFileUploadService {

}
