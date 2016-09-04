package servlet;

public class Hello implements HelloInterface {

	@Override
	public void sayHello() {
		System.out.println("hello, this is test of aop");
	}
}
