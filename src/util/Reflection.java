package util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


public class Reflection {
	public Object getObject_default(String className) {// 根据类名获取对象(调用默认构造函数)
		try {
			Class<?> classObject = Class.forName(className);// 获得指定类的Class对象
			Object object = classObject.newInstance();// 调用默认构造函数
			return object;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getObject(String className) {// 根据类名获取对象(调用自定义构造函数)
		try {
			Class<?> ClassObject = Class.forName(className);// 获得指定类的Class对象
			// 获得这个Class对象中的自定义构造函数
			Constructor<?>[] cstrcts = ClassObject.getDeclaredConstructors();
			if (cstrcts.length == 0) {// 不存在自定义的构造函数
				return getObject_default(className);// 调用默认构造函数
			} else {
				Class<?> paramsClass[] = cstrcts[0].getParameterTypes();
				Object params[] = new Object[paramsClass.length];
				int i = 0;
				for (Class<?> param : paramsClass) {
					if (param.getTypeName() == "java.lang.String") {
						params[i] = "";
					} else if (param.getTypeName() == "java.lang.Integer") {
						params[i] = new Integer(0);
					} else if (param.getTypeName() == "java.lang.Float") {
						params[i] = new Float(0);
					} else if (param.getTypeName() == "java.lang.Double") {
						params[i] = new Double(0);
					} else {
						params[i] = new Object();
					}
					++i;
				}
				return cstrcts[0].newInstance(params);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void set(Object object, String propertyName, Object propertyValue, String propertyType) {
		Class<?> ClassObject = object.getClass();
		Class<?> paramClass = null;
		try {
			paramClass = Class.forName(propertyType);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
			
		String methodName = "set_" + propertyName;
		try {
			Method setMethod = ClassObject.getMethod(methodName, paramClass);// 获得设定指定property的set函数
			setMethod.invoke(object, propertyValue);// 调用set函数，为指定的对象object的某个propertyName的属性赋值
		} catch (Exception e) {
			try {// 这个部分的代码是为了AOP动态代理准备的
				Method setMethod = ClassObject.getMethod(methodName, Class.forName("java.lang.Object"));
				setMethod.invoke(object, propertyValue);// 调用set函数，为指定的对象object的某个propertyName的属性赋值
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

}
