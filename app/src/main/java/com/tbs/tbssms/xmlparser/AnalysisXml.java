package com.tbs.tbssms.xmlparser;

/**
 * 使用DOM方式解析XML文件
 * 
 * @author任雪涛 pubDate:2011-12-28
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tbs.tbssms.entity.Location;



public class AnalysisXml {

	public static Document getDoc(String uri) {
		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			File file = new File(uri);
			if (!file.exists()) {
				throw new FileNotFoundException("未找到制定文件:" + uri);
			}//此处判断file指向是否是一个文件
			doc = builder.parse(uri);// 将给定 URI 的内容解析为一个 XML 文档，并且返回一个新的 DOM Document 对象。
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
	public static List<Location> readXml(String uri){

		List<Location> list = new ArrayList<Location>();
		Document doc = getDoc(uri);
		Node stuNode = doc.getDocumentElement();
		NodeList nodeList = stuNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node chileNode= nodeList.item(i);
			if("location".equals(chileNode.getNodeName())){
				Element element = (Element) chileNode;
				Location loc = null;
				Integer id = null;
				String url = null;
				id = Integer.parseInt(element.getAttribute("id"));
				for (int j = 0; j < element.getChildNodes().getLength(); j++) {
					Node chileElement = element.getChildNodes().item(j);
						if("url".equals(chileElement.getNodeName())){
							url = chileElement.getNodeValue();//此处跑出异常	
						}
				}
				loc = new Location(id, url);
				list.add(loc);
			}
		}
		return list;
	}
}
