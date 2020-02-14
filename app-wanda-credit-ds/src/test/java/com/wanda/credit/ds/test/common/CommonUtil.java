/**   
* @Description: 读文件写文件
* @author xiaobin.hou  
* @date 2016年5月4日 上午10:46:21 
* @version V1.0   
*/
package com.wanda.credit.ds.test.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

/**
 * @author xiaobin.hou
 *
 */
public class CommonUtil {
	

	/**
	 * 按行读取文件
	 * @param path		需要读取的文件路径
	 * @param charSet	字符集
	 * @return
	 */
	public static String readFile(String path, String charSet) {

		InputStreamReader reader = null;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer("");
		try {
			reader = new InputStreamReader(new FileInputStream(path), charSet);
			br = new BufferedReader(reader);

			String str = null;

			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
			System.out.println("----------1---------------" + sb.length());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return sb.toString();
	}
	
	/**
	 * 按行写文件
	 * @param path		目标文件
	 * @param content	内容
	 * @param charSet	字符集
	 * @param append	是否追加
	 */
	public static void writeFile(String path,String content,String charSet,boolean append){
		FileOutputStream out = null;
		try{
        File file=new File(path);
        if(!file.exists())
            file.createNewFile();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        out=new FileOutputStream(file,append); //如果追加方式用true        
        StringBuffer sb=new StringBuffer();
//        sb.append("-----------"+sdf.format(new Date())+"------------\n");
        sb.append(content+"\n");
        out.write(sb.toString().getBytes(charSet));//注意需要转换对应的字符集
        out.close();
        }catch(IOException ex) {
            System.out.println(ex.getStackTrace());
        }finally{
        	if(out != null){
        		try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
	}

}
