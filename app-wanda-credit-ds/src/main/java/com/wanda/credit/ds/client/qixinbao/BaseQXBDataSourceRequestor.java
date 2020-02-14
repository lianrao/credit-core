/**   
* @Description: 启信宝数据源基础类 
* @author xiaobin.hou  
* @date 2016年12月19日 下午2:00:32 
* @version V1.0   
*/
package com.wanda.credit.ds.client.qixinbao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import com.wanda.credit.ds.BaseDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
public class BaseQXBDataSourceRequestor extends BaseDataSourceRequestor {
	public String getMockTxt(){
		File file = new File(BaseQXBDataSourceRequestor.class.getClassLoader().
	    		getResource("").getPath()+"/mock/ds_qxbCorp_mock.txt");
		FileInputStream fis;
		String contents = "";
		try {
			fis = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF-8");
			BufferedReader in = new BufferedReader(inputStreamReader);
			StringBuffer sBuffer = new StringBuffer();
			String sbt =null;
			while((sbt = in.readLine())!=null){
				sBuffer.append(sbt);
			}			
			contents = sBuffer.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contents;
	}
}
