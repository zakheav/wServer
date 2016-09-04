package util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import ioc.ObjectGraphNode;

public class XML {
	public Map<String, ObjectGraphNode> getObjectGraph() {// 从xml文件中得到对象之间的依赖关系
		Map<String, ObjectGraphNode> refGraph = new HashMap<String, ObjectGraphNode>();// 依赖图邻接表
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new File("conf/conf.xml"));
			Element root = document.getRootElement();
			Element objects = root.element("objects");
			@SuppressWarnings("unchecked")
			List<Element> objectList = objects.elements("object");
			for (Element o : objectList) {// 遍历每一个对象
				ObjectGraphNode ogn = new ObjectGraphNode();
				String singleton = o.attributeValue("singleton");
				String className = o.attributeValue("class");
				String classId = o.attributeValue("id");
				if (singleton != null && singleton.equals("false")) {
					ogn.singleton = false;
				} else {
					ogn.singleton = true;
				}
				ogn.className = className;

				@SuppressWarnings("unchecked")
				List<Element> propertyList = o.elements("property");// 获得每一个属性
				for (Element p : propertyList) {
					String propertyName = p.attributeValue("name");
					Element v = p.element("value");
					if (v != null) {// 普通的属性
						String type = v.attributeValue("type");
						String value = v.getText();
						ogn.propertyType.put(propertyName, type);
						if (type.equals("java.lang.String")) {
							ogn.propertyValue.put(propertyName, value);
						} else if (type.equals("java.lang.Integer")) {
							ogn.propertyValue.put(propertyName, Integer.parseInt(value));
						} else if (type.equals("java.lang.Long")) {
							ogn.propertyValue.put(propertyName, Long.parseLong(value));
						} else if (type.equals("java.lang.Double")) {
							ogn.propertyValue.put(propertyName, Double.parseDouble(value));
						} else if (type.equals("java.lang.Float")) {
							ogn.propertyValue.put(propertyName, Float.parseFloat(value));
						}
					} else {// 该属性是引用的对象
						v = p.element("ref");
						String refId = v.attributeValue("id");
						ogn.ref.put(propertyName, refId);
					}
				}

				// 把ObjectGraphNode对象存入依赖图邻接表
				refGraph.put(classId, ogn);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return refGraph;
	}
	
	public Map<String, String> getServlets() {
		Map<String, String> servletMap = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new File("conf/conf.xml"));
			Element root = document.getRootElement();
			Element config = root.element("config");
			Element servletConf = config.element("servlets");
			@SuppressWarnings("unchecked")
			List<Element> servletList = servletConf.elements("servlet");
			for(Element e : servletList) {
				servletMap.put(e.attributeValue("uri"), e.attributeValue("class"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return servletMap;
	}
	
	public Map<String, String> getDBpoolConf() {
		Map<String, String> DBpoolConf = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new File("conf/conf.xml"));
			Element root = document.getRootElement();
			Element config = root.element("config");
			Element mysqlConf = config.element("mysql");
			String url = mysqlConf.elementText("url");
			String user = mysqlConf.elementText("user");
			String password = mysqlConf.elementText("password");
			DBpoolConf.put("url", url);
			DBpoolConf.put("user", user);
			DBpoolConf.put("password", password);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return DBpoolConf;
	}
	
	public Map<String, String> getServerConf() {
		Map<String, String> serverConf = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new File("conf/conf.xml"));
			Element root = document.getRootElement();
			Element config = root.element("config");
			Element sc = config.element("server");
			String ip = sc.elementText("ip");
			String port = sc.elementText("port");
			serverConf.put("ip", ip);
			serverConf.put("port", port);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return serverConf;
	}
}
