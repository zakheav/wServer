package aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AOP implements InvocationHandler {

	private Object service;// 业务对象
	public Object proxy;
	public void set_service(Object svc) {// 绑定代理对象
		this.service = svc;
		proxy = Proxy.newProxyInstance(service.getClass().getClassLoader(), service.getClass().getInterfaces(), this);// 绑定代理对象的接口
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		try{
			System.out.println("log: this is log");
			result = method.invoke(service, args);// 执行方法调用
		} catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

}
