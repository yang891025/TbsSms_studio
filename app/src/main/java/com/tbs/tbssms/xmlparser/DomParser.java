package com.tbs.tbssms.xmlparser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.tbs.tbssms.entity.Location;

/**
 * 使用DOM方式解析XML文件
 * 
 * @author任雪涛 pubDate:2011-12-28
 */

public class DomParser
{
    public static Document loadDoc(InputStream inputStreams)
    {
        Document doc = null;
        // 文档工厂
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // 获取文档创建器
        try
        {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            //			File file = new File(uri);
            //			if (!file.exists()) {
            //				throw new FileNotFoundException("为找指定文件:" + uri);
            //			}
            //			doc = builder.parse(uri);
            doc = builder.parse(inputStreams);//inputStreams
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return doc;
    }

    //使用node对象的getElementsByTagName()方法简化解析过程
    public static List<Location> readXml2(InputStream inputStreams)
    {
        List<Location> stuList = new ArrayList<Location>();
        Document doc = loadDoc(inputStreams);
        //获取所有学员节点
        NodeList stuNodes = doc.getElementsByTagName("location");
        //获取所有的姓名节点
        NodeList nameNodes = doc.getElementsByTagName("url");
        for (int i = 0; i < stuNodes.getLength(); i++)
        {
            Element stuNode = (Element)stuNodes.item(i);
            Integer id = Integer.parseInt(stuNode.getAttribute("id"));
            String url = nameNodes.item(i).getFirstChild().getNodeValue();
            Location loc = new Location(id,url);
            stuList.add(loc);
        }
        return stuList;
    }
}
