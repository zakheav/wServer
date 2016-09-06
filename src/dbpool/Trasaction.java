package dbpool;

import java.util.ArrayList;

public class Trasaction extends Query {
	public ArrayList<String> queryStringList = new ArrayList<String>();
	public void addToTrasaction() {
		queryStringList.add(super.queryString);
	}
	public void trasactionExecute() {
		DBpool.getInstance().trasaction(queryStringList);
	}
}
