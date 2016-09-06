package dbpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;

import org.apache.log4j.Logger;

import util.XML;

public class DBpool {
	private static Logger log = Logger.getLogger(DBpool.class);
	private static String url = "";
	private static String user = "";
	private static String password = "";

	private static String driverClassName = "com.mysql.jdbc.Driver";

	private int commonPoolSize = 10;
	private int maxPoolSize = 15;
	private int nowTotalConnections = 10;

	private static DBpool instance = new DBpool();

	private Queue<Connection> pool = new LinkedList<Connection>();
	private Vector<Connection> connectionsInUse = new Vector<Connection>(commonPoolSize);

	private DBpool() {
		Map<String, String> conf = new XML().getDBpoolConf();
		DBpool.url = conf.get("url");
		DBpool.user = conf.get("user");
		DBpool.password = conf.get("password");
		try {
			Class.forName(DBpool.driverClassName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addConnection(commonPoolSize);
		System.out.println("连接池启动");
	}

	public static DBpool getInstance() {
		return instance;
	}

	public String get_url() {
		return url;
	}

	public String get_user() {
		return user;
	}

	public String get_password() {
		return password;
	}

	public String get_driverClassName() {
		return driverClassName;
	}

	private ArrayList<Map<String, Object>> resultSet_to_obj(ResultSet r) {
		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			ResultSetMetaData rsmd = r.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			while (r.next()) {
				Map<String, Object> row = new HashMap<String, Object>();
				for (int i = 1; i <= numberOfColumns; ++i) {
					String name = rsmd.getColumnName(i);
					Object value = r.getObject(name);
					row.put(name, value);
				}
				result.add(row);
			}
		} catch (Exception e) {
			log.error(DBpool.class, e);
		}
		return result;
	}

	private Connection newConnection() {
		try {
			Connection conn = DriverManager.getConnection(DBpool.url, DBpool.user, DBpool.password);
			return conn;
		} catch (Exception e) {
			log.error(DBpool.class, e);
			return null;
		}
	}

	private void addConnection(int num) {
		synchronized (pool) {
			for (int i = 0; i < num; ++i) {
				try {
					Connection conn = newConnection();
					if (conn != null) {
						pool.offer(conn);
					}
				} catch (Exception e) {
					log.error(DBpool.class, e);
				}
			}
		}
	}

	public Connection getConnection() {
		synchronized (pool) {
			while (pool.isEmpty() && (nowTotalConnections == maxPoolSize)) {
				try {
					pool.wait();
				} catch (InterruptedException e) {
					log.error(DBpool.class, e);
				}
			}
			if (pool.isEmpty() && (nowTotalConnections < maxPoolSize)) {
				Connection conn = newConnection();
				connectionsInUse.add(conn);
				++nowTotalConnections;
				return conn;
			}
			if (!pool.isEmpty()) {
				Connection conn = pool.poll();
				connectionsInUse.add(conn);
				return conn;
			}
			return null;
		}
	}

	public void releaseConnection(Connection cn) {
		if (connectionsInUse.remove(cn)) {
			if (connectionsInUse.size() < commonPoolSize / 2 && nowTotalConnections > commonPoolSize) {
				try {
					cn.close();
					--nowTotalConnections;
				} catch (Exception e) {
					log.error(DBpool.class, e);
				}
			} else {
				synchronized (pool) {
					pool.offer(cn);
					pool.notifyAll();
				}
			}
		}
	}

	public ArrayList<Map<String, Object>> executeQuery(String queryString) {// 查询
		Connection conn = null;
		Connection e_conn = null;// 错误状态下重新分配的connection
		Statement stmt = null;
		Statement e_stmt = null;// e_conn生成的statement
		ResultSet rs = null;
		ResultSet e_rs = null;// e_stmt返回的结果集
		boolean connection_timeout = false;
		ArrayList<Map<String, Object>> result = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(queryString);// 出问题
			result = resultSet_to_obj(rs);
			return result;
		} catch (Exception e1) {// 可能连接失效
			try {
				// 释放掉无用链接
				connectionsInUse.remove(conn);
				connection_timeout = true;
				e_conn = newConnection();
				connectionsInUse.add(e_conn);// 更新连接
				// 重新查询
				e_stmt = e_conn.createStatement();
				e_rs = e_stmt.executeQuery(queryString);
				result = resultSet_to_obj(e_rs);
				return result;
			} catch (Exception e2) {
				log.error(DBpool.class, e2);
				return null;
			} finally {// 释放资源
				try {
					e_rs.close();
					e_stmt.close();
					releaseConnection(e_conn);
				} catch (SQLException e3) {
					log.error(DBpool.class, e3);
				}
			}
		} finally {// 释放资源
			try {
				if (!connection_timeout) {
					rs.close();
					stmt.close();
					releaseConnection(conn);
				} else {
					stmt.close();
					conn.close();
				}
			} catch (SQLException e4) {
				log.error(DBpool.class, e4);
			}
		}
	}

	public boolean trasaction(ArrayList<String> queryList) {
		Connection conn = null;
		Connection e_conn = null;// 错误状态下重新分配的connection
		Statement stmt = null;
		Statement e_stmt = null;// e_conn生成的statement
		boolean connection_timeout = false;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			for(String queryString : queryList) {
				stmt.executeUpdate(queryString);// 出问题
			}
			conn.commit();// 提交
			return true;
		} catch (Exception e) {// 可能连接失效
			// 尝试回滚
			try {
				conn.rollback();
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				// 释放掉无用链接
				connectionsInUse.remove(conn);
				connection_timeout = true;
				e_conn = newConnection();
				connectionsInUse.add(e_conn);// 更新连接
				
				// 重新查询
				e_conn.setAutoCommit(false);
				e_stmt = e_conn.createStatement();
				for(String queryString : queryList) {
					e_stmt.executeUpdate(queryString);
				}
				e_conn.commit();// 提交
				return true;
			} catch (Exception e2) {// 重新查询事务执行出错
				try{
					log.error(DBpool.class, e2);
					e_conn.rollback();// 回滚
					return false;
				} catch(Exception e3) {
					log.error(DBpool.class, e3);
					return false;
				}
			} finally {// 释放资源
				try {
					e_stmt.close();
					releaseConnection(e_conn);
				} catch (SQLException e4) {
					log.error(DBpool.class, e4);
				}
			}
		} finally {// 释放资源
			try {
				if (!connection_timeout) {
					stmt.close();
					releaseConnection(conn);
				} else {
					stmt.close();
					conn.close();
				}
			} catch (SQLException e5) {
				log.error(DBpool.class, e5);
			}
		}
	}

	public boolean executeUpdate(String queryString) {// 更新
		Connection conn = null;
		Connection e_conn = null;// 错误状态下重新分配的connection
		Statement stmt = null;
		Statement e_stmt = null;// e_conn生成的statement
		boolean connection_timeout = false;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(queryString);
			return true;
		} catch (Exception e1) {// 可能连接失效
			try {
				// 释放掉无用链接
				connection_timeout = true;
				connectionsInUse.remove(conn);
				e_conn = newConnection();
				connectionsInUse.add(e_conn);// 更新连接
				// 重新更新
				e_stmt = e_conn.createStatement();
				e_stmt.executeUpdate(queryString);
				return true;
			} catch (Exception e2) {
				log.error(DBpool.class, e2);
				return false;
			} finally {
				try {
					e_stmt.close();
					releaseConnection(e_conn);
				} catch (SQLException e3) {
					log.error(DBpool.class, e3);
				}
			}
		} finally {
			try {
				if (!connection_timeout) {
					stmt.close();
					releaseConnection(conn);
				} else {
					stmt.close();
					conn.close();
				}
			} catch (SQLException e4) {
				log.error(DBpool.class, e4);
			}
		}
	}

}
