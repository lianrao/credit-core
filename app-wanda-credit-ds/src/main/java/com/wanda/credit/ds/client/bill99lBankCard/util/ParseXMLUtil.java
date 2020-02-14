package com.wanda.credit.ds.client.bill99lBankCard.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * 此类用于解析XML
 * */
@SuppressWarnings("unchecked")
public class ParseXMLUtil {
	private final  Logger logger = LoggerFactory.getLogger(ParseXMLUtil.class);
	
	/**
	 * 此方法用来初始化这个类
	 * 返回一个ParseXMLUtil类的实例化对象
	 * */
	public static ParseXMLUtil initParseXMLUtil(){
		return new ParseXMLUtil();
	}
	
	/**
	 * 此方法用来解析XML中的节点，返回得到一个HashMap的键值对象
	 * hm为传递来的用来保存XML节点信息的
	 * e是传递来的XML节点
	 * */
	private HashMap parseXMLNode(HashMap hm,Element e){
		Element child=null;   //定义一个Element元素对象
		
		//下面开始迭代循环
		for(Iterator childs= e.getChildren().iterator();childs.hasNext();){
			child=(Element)childs.next(); //获取节点下的每一个子元素
			hm.put(child.getName(), child.getValue()); //将每一个元素的名称和值都保存到HashMap中，方便以后查询取出
		}
		return hm;
	}
	
	/**
	 * 此方法用来解析XML中的节点(主要是保存List列表)，返回得到一个HashMap的键值对象
	 * hm为传递来的用来保存XML节点信息的
	 * e是传递来的XML节点
	 * */
	private HashMap parseXMLNodeList(HashMap hm,Element e,String flag2){
		Element child=null;   //定义一个Element元素对象
		Element child_txn=null;		//定义一个Element元素对象
		HashMap HM_txn=null;
		List list=null;
		
		//下面开始迭代循环
		for(Iterator childs= e.getChildren().iterator();childs.hasNext();){
			child=(Element)childs.next(); //获取节点下的每一个子元素
			if(flag2.equals(child.getName())){
				list=new ArrayList();   //实例化一个List

				//下面开始迭代循环，并且将部分HashMap保存到List列表中
				for (Iterator childs2= child.getChildren().iterator();childs2.hasNext();){
					HM_txn=new HashMap();      //重新实例化一个HashMap对象，用来保存Txn中的元素
					child_txn=(Element)childs2.next(); //获取每一个子元素
					HM_txn=parseXMLNode(HM_txn,child_txn);   //调用parseXMLNode函数，得到该节点下的所有元素
					list.add(HM_txn);     //将HM_txn放入List列表中，方便以后查询取出
				}
				hm.put("Txn", list);
			}else{
				hm.put(child.getName(), child.getValue()); //将每一个元素的名称和值都保存到HashMap中，方便以后查询取出
			}
		}
		return hm;
	}
	
	/**
	 * 此方法用来解析获取的XML数据,返回一个XML的根元素Element对象
	 * resXml：快钱返回的XML数据流
	 * root：最终得到的根元素
	 * */
	public Element parseXML(String resXml){
		SAXBuilder sb=new SAXBuilder();  //初始化SAXBuilder
		//创建一个新的字符串
        StringReader read = new StringReader(resXml);
        //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource inSource = new InputSource(read);
		Document doc=null;  //定义一个Document文本对象
		Element root=null;  //定义一个Element根元素对象
		try {
			doc=sb.build(inSource);		//得到并构建XML文档对象
		} catch (JDOMException e1) {
			logger.info("独立鉴权:解析文本报错,详细信息:{}",e1.getMessage());
			e1.printStackTrace();
		} catch (IOException e1) {
			logger.info("独立鉴权:获取输入流报错,详细信息:{}",e1.getMessage());
			e1.printStackTrace();
		}
		
		if(doc!=null){
			root=doc.getRootElement();  //得到XML的根元素
		}else{
			logger.info("独立鉴权:解析root出错");
		}
		return root;
	}
	
	/**
	 * 此方法通过得到的根元素root对象，具体解析XML数据
	 * flag1、flag2用来判断XML数据的标志字段
	 * xmlData：返回一个保存有XML元素数据的HashMap
	 * 适用于：消费、分期消费、预授权、预授权完成、撤销、退货交易、
	 *       查询交易流水报文、预授权到期通知、维护客户号关系、卡信息查询报文
	 * */
	public HashMap returnXMLData(Element root,String flag1,String flag2){
		HashMap xmlData=null;	//定义一个HashMap对象
		Element child=null;   //定义一个Element元素对象
		
		if(root!=null){
			xmlData=new HashMap(); //初始化HashMap,用来保存得到的数据
			String childName="";
			
			//下面开始迭代循环
			for (Iterator childs= root.getChildren().iterator();childs.hasNext();) {
				child=(Element)childs.next(); //获取每一个子元素
				childName=(String)child.getName();  //得到该节点的名称
				
				//下面第一个判断，用来匹配第一个XML标志节点（如：TxnMsgContent）；
				//第二个判断，用来匹配第二个XML标志节点（如：ErrorMsgContent）
				if(flag1.equals(childName)){
					xmlData=parseXMLNode(xmlData,child);   //调用parseXMLNode函数，得到该节点下的所有元素
				}else if(flag2.equals(childName)){
					xmlData=parseXMLNode(xmlData,child);   //调用parseXMLNode函数，得到该节点下的所有元素
				}else if("ErrorMsgContent".equals(childName)){
					xmlData=parseXMLNode(xmlData,child);   //调用parseXMLNode函数，得到该节点下的所有元素
				}else{
					xmlData.put(childName, child.getValue());  //将子元素保存到HashMap中
				}
			}
		}
		return xmlData;
	}
	
	/**
	 * 此方法通过得到的根元素root对象，具体解析XML数据，返回多个相同节点的报文
	 * flag1、flag2用来判断XML数据的标志字段
	 * xmlData：返回一个保存有XML元素数据的HashMap
	 * 适用于：查询日确认流水报文、查询日入账流水报文、查询批交易流水报文
	 * */
	public HashMap returnXMLDataList(Element root,String flag1,String flag2){
		HashMap xmlData=null;	//定义一个HashMap对象
		Element child=null;			//定义一个Element元素对象
		
		if(root!=null){
			xmlData=new HashMap(); //初始化HashMap,用来保存得到的数据
			String childName="";
			//下面开始迭代循环
			for (Iterator childs1= root.getChildren().iterator();childs1.hasNext();) {
				child=(Element)childs1.next(); //获取每一个子元素
				childName=(String)child.getName();  //得到该节点的名称
				//下面判断用来判断每次从哪个节点下取出XML元素，其他与上个函数同理；
				if(flag1.equals(childName)){
					xmlData=parseXMLNodeList(xmlData,child,flag2);   //调用parseXMLNode函数，得到该节点下的所有元素
				}else if("ErrorMsgContent".equals(childName)){
					xmlData=parseXMLNode(xmlData,child);   //调用parseXMLNode函数，得到该节点下的所有元素
				}else{
					xmlData.put(childName, child.getValue());  //将子元素保存到HashMap中
				}
			}
		}
		//循环结束
		return xmlData;
	}
	
}
