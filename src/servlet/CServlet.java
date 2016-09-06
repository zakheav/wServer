package servlet;

import java.util.ArrayList;
import dbpool.Query;
import dbpool.Trasaction;
import webServer.Request;
import webServer.Response;
import webServer.Servlet;

public class CServlet extends Servlet {
	public void business(Request req, Response res) throws Exception {
		// 事务
		Trasaction t = new Trasaction();
		ArrayList<Object> params = new ArrayList<Object>();
		params.add("a"); params.add("b"); params.add(1111);
		new Query().insertInto("test(a,b,c)").values(params).addToTrasaction(t);
		new Query().insertInto("test(a,b,c)").values(params).addToTrasaction(t);
		new Query().insertInto("test(a,b,c)").values(params).addToTrasaction(t);
		params.add(111);// 构造出错，造成事务回滚
		new Query().insertInto("test(a,b,c)").values(params).addToTrasaction(t);
		t.trasactionExecute();

		res.sendData("I am B-servlet");
	}
}
