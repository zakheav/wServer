package dbpool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Query {
	public String queryString = "";

	public Query() {
	}

	public Query(String query) {
		queryString = query;
	}

	public Query select(String attributes) {// "user,id"
		this.queryString = "select " + attributes;
		return this;
	}

	public Query update(String table) {
		this.queryString = "update " + table;
		return this;
	}
	
	public Query set(HashMap<String, Object> attr_value){
		this.queryString += " set ";
		
		Iterator<Entry<String, Object>> entries = attr_value.entrySet().iterator();
		int length = attr_value.size();
		int count = 0;
		while(entries.hasNext()){
			++count;
			Map.Entry<String, Object> entry = entries.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			this.queryString += key + " = ";
			if( value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double ){
				if(count == length)
					this.queryString += value + " ";
				else
					this.queryString += value + ", ";
			} else if(value instanceof String){
				if(count == length)
					this.queryString += "'" + value + "' ";
				else
					this.queryString += "'" + value + "', ";
			} else{
				return null;
			}
		}
		return this;
	}

	public Query table(String tables) {
		this.queryString += " from " + tables;
		return this;
	}

	public Query where(String attribute) {
		this.queryString += " where " + attribute;
		return this;
	}

	public Query andWhere(String attribute) {
		this.queryString += " and " + attribute;
		return this;
	}

	public Query orWhere(String attribute) {
		this.queryString += " or " + attribute;
		return this;
	}

	public Query equal(Object value) {
		if (value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double) {
			this.queryString += " = " + value;
		} else if (value instanceof String) {
			this.queryString += " = '" + value + "'";
		} else {
			return null;// �Ƿ���������
		}
		return this;
	}

	public Query bigger(Object value) {
		if (value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double) {
			this.queryString += " > " + value;
		} else {
			return null;
		}
		return this;
	}

	public Query smaller(Object value) {
		if (value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double) {
			this.queryString += " < " + value;
		} else {
			return null;
		}
		return this;
	}
	
	public Query notbigger(Object value) {
		if (value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double) {
			this.queryString += " <= " + value;
		} else {
			return null;
		}
		return this;
	}
	
	public Query notSmaller(Object value) {
		if (value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double) {
			this.queryString += " >= " + value;
		} else {
			return null;
		}
		return this;
	}

	public Query limit(int begin, int number) {
		this.queryString += " limit " + begin + " , " + number;
		return this;
	}

	public Query in(ArrayList<Object> list) {
		this.queryString += " in ( ";
		int i = 0;
		for (i = 0; i < list.size() - 1; ++i) {
			if (list.get(i) instanceof Integer || list.get(i) instanceof Long || list.get(i) instanceof Float
					|| list.get(i) instanceof Double) {
				this.queryString += list.get(i) + " , ";
			} else if (list.get(i) instanceof String) {
				this.queryString += "'" + list.get(i) + "' , ";
			} else {
				return null;
			}
		}
		if (list.get(i) instanceof Integer || list.get(i) instanceof Long || list.get(i) instanceof Float
				|| list.get(i) instanceof Double) {
			this.queryString += list.get(i) + " ) ";
		} else if (list.get(i) instanceof String) {
			this.queryString += "'" + list.get(i) + "' ) ";
		} else {
			return null;
		}
		return this;
	}

	public Query orderBy(String str) {
		this.queryString += " order by " + str;
		return this;
	}

	public void executeUpdate(){
		DBpool.getInstance().executeUpdate(queryString);
	}
	
	public ArrayList<Map<String, Object>> fetchAll() {
		return DBpool.getInstance().executeQuery(queryString);
	}

	public Map<String, Object> fetchOne() {
		ArrayList<Map<String, Object>> r = DBpool.getInstance().executeQuery(queryString);
		if(!r.isEmpty()) return r.get(0);
		return null;
	}

	public ArrayList<Map<String, Object>> fetchGroup(String Group) {
		this.queryString += " group by " + Group;
		return DBpool.getInstance().executeQuery(queryString);
	}

//	public static void main(String[] args) {
//		HashMap<String, Object> attr_value = new HashMap<String, Object>();
//		attr_value.put("name","weiyuan");
//		attr_value.put("age", 24);
//		
//		new Query().update("user").set(attr_value).where("tel").equal("18810543471").executeUpdate();
//	}
}