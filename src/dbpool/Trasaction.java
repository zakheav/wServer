package dbpool;

import java.util.ArrayList;

public class Trasaction {
	public ArrayList<String> queryStringList = new ArrayList<String>();
	
	public void trasactionExecute() {
		DBpool.getInstance().trasaction(queryStringList);
	}
}
