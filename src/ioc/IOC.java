package ioc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import util.Reflection;
import util.XML;

public class IOC {
	private Map<String, ObjectGraphNode> objectGraph = null;// key是每个对象在xml文件中的id属性

	private static IOC instance = new IOC();

	private IOC() {
		XML xml = new XML();
		objectGraph = xml.getObjectGraph();
		Init();// 初始化
	}

	public static IOC getInstance() {
		return instance;
	}

	public Map<String, ObjectGraphNode> bfs(String ObjectId) {// 广度优先搜索，获取到和指定ObjectId对象相关联的所有对象，以hash表
		Map<String, ObjectGraphNode> list = new HashMap<String, ObjectGraphNode>();
		Queue<ObjectGraphNode> queue = new LinkedList<ObjectGraphNode>();
		ObjectGraphNode nowNode = objectGraph.get(ObjectId);
		if (nowNode != null) {
			queue.offer(nowNode);
			list.put(ObjectId, nowNode);
		} else {
			System.out.println("there is a wrong object id in xml file.");
		}
		while (!queue.isEmpty()) {
			nowNode = queue.poll();
			for (String propertyName : nowNode.ref.keySet()) {
				String id = nowNode.ref.get(propertyName);// 引用对象的id
				ObjectGraphNode child = objectGraph.get(id);
				if (child != null) {
					if(!list.containsKey(id)) {
						queue.offer(child);
						list.put(id, child);
					}
				} else {
					System.out.println("there is a wrong object id in xml file.");
				}
			}
		}
		return list;
	}

	private void Init() {
		for (String objectId : objectGraph.keySet()) {
			ObjectGraphNode ogn = objectGraph.get(objectId);
			if (ogn.singleton == true) {// 是单例模式的对象
				Map<String, ObjectGraphNode> list = bfs(objectId);
				// 把list中的对象节点全部实例化，存储到对应的ObjectGraphNode中的instance里
				for (String objId : list.keySet()) {
					ObjectGraphNode o = list.get(objId);
					if (o.instance == null) {// 需要实例化这个对象存储到instance域中
						Reflection r = new Reflection();
						Object object = r.getObject(o.className);// 得到对象的实例
						for (String propertyName : o.propertyValue.keySet()) {// 为对象的实例中的非引用属性赋值
							Object propertyValue = o.propertyValue.get(propertyName);
							String propertyType = o.propertyType.get(propertyName);
							r.set(object, propertyName, propertyValue, propertyType);
						}
						o.instance = object;
					}
				}

				// 对象之间依照依赖关系相互组装
				for (String objId : list.keySet()) {
					ObjectGraphNode o = list.get(objId);
					Object object = o.instance;
					// 找到这个对象依赖的对象
					for (String propertyName : o.ref.keySet()) {
						String refId = o.ref.get(propertyName);
						Object propertyValue = objectGraph.get(refId).instance;
						String propertyType = objectGraph.get(refId).className;
						Reflection r = new Reflection();
						// 把依赖对象注入当前对象object
						r.set(object, propertyName, propertyValue, propertyType);
					}
				}
			}
		}
	}

	public Object getObject(String objectId) {// 输入objectid，获取object
		// 先检查这个对象是否是单例对象
		ObjectGraphNode ogn = objectGraph.get(objectId);
		if(ogn.singleton == true) return ogn.instance;
		
		// 广度优先搜索，搜索出这个object依赖的所有对象列表
		Map<String, ObjectGraphNode> list = bfs(objectId);
		Map<String, Object> objectList = new HashMap<String, Object>();
		// 把list中的object实例化，存储在objectList中
		for (String objId : list.keySet()) {
			ObjectGraphNode o = list.get(objId);
			Reflection r = new Reflection();
			Object object = r.getObject(o.className);// 得到对象的实例
			for (String propertyName : o.propertyValue.keySet()) {// 为对象的实例中的非引用属性赋值
				Object propertyValue = o.propertyValue.get(propertyName);
				String propertyType = o.propertyType.get(propertyName);
				r.set(object, propertyName, propertyValue, propertyType);
			}
			objectList.put(objId, object);
		}
		// 把objectList中的对象按照依赖关系相互组装
		for (String objId : list.keySet()) {
			Object object = objectList.get(objId);
			ObjectGraphNode o = list.get(objId);// 这里面蕴含了object的对象依赖关系
			// 找到这个对象依赖的对象
			for (String refName : o.ref.keySet()) {
				String propertyName = refName;
				String refId = o.ref.get(propertyName);
				Object propertyValue = objectList.get(refId);
				String propertyType = objectGraph.get(refId).className;

				Reflection r = new Reflection();
				// 把依赖对象注入当前对象object
				r.set(object, propertyName, propertyValue, propertyType);
			}
		}
		
		return objectList.get(objectId);
	}
	
}
