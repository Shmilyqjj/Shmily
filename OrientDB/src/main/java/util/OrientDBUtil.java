package util;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseSessionMetadata;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientDynaElementIterable;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.apache.hadoop.util.hash.Hash;


import java.sql.*;
import java.util.*;

public class OrientDBUtil {
	private static String orientDBHost = "";
	private static Connection conn = null;
	private static PreparedStatement ps = null;
	private static ResultSet rs = null;

	private static String trimString(String str, String delStr){
		int delStrLength = delStr.length();
		if (str.startsWith(delStr)) {
			str = str.substring(delStrLength);
		}
		if (str.endsWith(delStr)) {
			str = str.substring(0,str.length() - delStrLength);
		}
		return str;
	}

	public static void close() {
		try {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取orientDB连接
	 * @param url
	 * @param user
	 * @param password
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection(String url, String user, String password,Properties info)
			throws Exception {
		Class.forName("com.orientechnologies.orient.jdbc.OrientJdbcDriver");
		info.put("user", user);
		info.put("password", password);
		Connection con = DriverManager.getConnection(url,info);
		return con;
	}

	/**
	 * 默认参数的连接
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection(String dbName) throws Exception {
		Properties info = new Properties();
		info.put("db.usePool", "true");
		info.put("db.pool.min", 1);
		info.put("db.pool.max", 5);
		String url = "jdbc:orient:remote:" + orientDBHost + "/" + dbName;
		return getConnection(url,"admin","admin",info);
	}

	public static ResultSet select(String dbName,String sql) {
		try {
			conn = getConnection(dbName);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery(sql);
		} catch (SQLException e) {
			System.out.println("查询数据异常:"+ e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;

	}


	/**
	 * 新增、修改、删除 sql
	 * 返回影响条数
	 */
	public static int executeUpdate(String dbName, String sql) throws Exception {
		int num = 0;
		try {
			conn = getConnection(dbName);
			ps = conn.prepareStatement(sql);
			num = ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println(e);
		} finally {
			close();
		}
		System.out.println("Rows affected: "+num);
		return num;
	}

	/**
	 * 获取图 OrientBaseGraph可以对图做一些操作 比如addEdge addVertex getEdge
	 * @param dbName 连接到的库名
	 * @param userName 登录用户
	 * @param passWord 登录密码
	 * @param minPoolNum  连接池最小连接数
	 * @param maxPoolNum  连接池最大连接数
	 * @return
	 */
	public OrientBaseGraph getGraph(String dbName,String userName,String passWord,int minPoolNum,int maxPoolNum) {
		OrientGraphFactory orientGraphFactory = new OrientGraphFactory("remote:" + orientDBHost + "/" + dbName, userName, passWord).setupPool(minPoolNum, maxPoolNum);
		return orientGraphFactory.getTx();
	}

	/**
	 * @param graph
	 * @param sql
	 * @return execute
	 */
	public static Object getObjectBySQL(OrientBaseGraph graph,String sql){
		return graph.command(new OCommandSQL(sql)).execute();
	}

	/**
	 * 通过SQL获取object迭代器
	 * @param graph
	 * @param sql
	 * @return ObjectIterator
	 */
	public static Iterator<Object> getObjectIteratorBySQL(OrientBaseGraph graph, String sql){
		OrientDynaElementIterable execute = null;
		execute = (OrientDynaElementIterable) getObjectBySQL(graph, sql);
		try {
			Iterator<Object> iterator = execute.iterator();
			if (iterator.hasNext()) {
				return iterator;
			}
			return null;
		}finally {
			if(execute!=null){
				execute.close();
			}
		}
	}

	/**
	 * 通过class名获取图中所有该class的Vertex顶点
	 * @param graph OrientBaseGraph对象
	 * @param className String顶点类名
	 * @return ArrayList<Vertex>
	 */
	public static ArrayList<Vertex> getVertex(OrientBaseGraph graph, String className){
		ArrayList<Vertex> arr = new ArrayList<>();
		OrientDynaElementIterable orientDynaElementIterable  = (OrientDynaElementIterable)getObjectBySQL(graph, String.format("select from %s",className));
		Iterator<Object> iterator =orientDynaElementIterable.iterator();
		while (iterator.hasNext()){
			Vertex vertex = (Vertex)iterator.next();
			arr.add(vertex);
		}
		return arr;
	}

	/**
	 * 通过class名和where条件获取图中所有符合条件的Vertex顶点
	 * @param graph OrientBaseGraph对象
	 * @param className 顶点类名
	 * @param whereConditions where property1 = xx and property2 = xx...
	 * @return ArrayList<Vertex>
	 */
	public static ArrayList<Vertex> getVertex(OrientBaseGraph graph, String className,String whereConditions){
		ArrayList<Vertex> arr = new ArrayList<>();
		OrientDynaElementIterable orientDynaElementIterable  = (OrientDynaElementIterable)getObjectBySQL(graph, String.format("select from %s %s",className,whereConditions));
		Iterator<Object> iterator = orientDynaElementIterable.iterator();
		while (iterator.hasNext()){
			Vertex vertex = (Vertex)iterator.next();
			arr.add(vertex);
		}
		return arr;
	}


	public static ArrayList<Edge> getEdges(OrientBaseGraph graph, String className){
		ArrayList<Edge> arr = new ArrayList<>();
		OrientDynaElementIterable orientDynaElementIterable  = (OrientDynaElementIterable)getObjectBySQL(graph, String.format("select from %s",className));
		Iterator<Object> iterator = orientDynaElementIterable.iterator();
		while (iterator.hasNext()){
			Edge edge = (Edge)iterator.next();
			arr.add(edge);
		}
		return arr;
	}

	/**
	 * 根据class的properties获取orientDB的recordID
	 * @param dbName
	 * @param className
	 * @param props
	 * @return
	 * @throws SQLException
	 */
	public static String getRidByProperties(String dbName, String className, Map<String,Object> props) throws SQLException {
		StringBuilder sql = new StringBuilder(String.format("select @rid from %s", className));
		boolean flag = false;
		Set<Map.Entry<String, Object>> mapSet = props.entrySet();
		Iterator<Map.Entry<String, Object>> iter = mapSet.iterator();
		while(iter.hasNext()){
			Map.Entry<String, Object> me = iter.next();
			String key = me.getKey();
			Object value = me.getValue();
			if(value != null){
				if(!flag){
					sql.append(" where ");
					flag = true;
				}
				if(value instanceof Integer){
					sql.append(String.format("%s = %s and ",key,value));
				}else {
					sql.append(String.format("%s = '%s' and ",key,value));
				}
			}
		}
		String finalSql = trimString(sql.toString(),"and ");
		System.out.println(finalSql);
		ResultSet select = select(dbName, finalSql);
		if(select.next()){
			return select.getString("@rid");
		}
		return "";
	}

	public static boolean vertexExists(OrientBaseGraph graph, String vertexClassName, String dbName, String tableName){
		OrientDynaElementIterable orientDynaElementIterable  = (OrientDynaElementIterable)OrientDBUtil.getObjectBySQL(graph, String.format("select from %s where db_name = '%s' and table_name = '%s'",vertexClassName,dbName,tableName));
		return orientDynaElementIterable.iterator().hasNext();
	}
	public static boolean edgeExists(OrientBaseGraph graph, String edgeClassName, String inSelect,String outSelect){
		OrientDynaElementIterable orientDynaElementIterable  = (OrientDynaElementIterable)OrientDBUtil.getObjectBySQL(graph, String.format("select from %s where in IN (%s) and out IN (%s)",edgeClassName,inSelect,outSelect));
		return orientDynaElementIterable.iterator().hasNext();
	}
	public static boolean edgeExistsByRID(OrientBaseGraph graph, String edgeClassName, String inRid,String outRid){
		OrientDynaElementIterable orientDynaElementIterable  = (OrientDynaElementIterable)OrientDBUtil.getObjectBySQL(graph, String.format("select from %s where in = '%s' and out = '%s'",edgeClassName,inRid,outRid));
		return orientDynaElementIterable.iterator().hasNext();
	}

	/**
	 * 通过commit提交数据改动
	 * @param graph
	 */
	public static void commitGraph(OrientBaseGraph graph) throws InterruptedException {
		try {
			if(graph!=null){
				graph.commit();
			}
		}catch (ORecordDuplicatedException e) {
			System.out.println("ORecordDuplicated: " + e.toString());
		}catch (OConcurrentModificationException e) {
			Thread.sleep(3000);
			commitGraph(graph);
		}catch (Exception e) {
			graph.rollback();
			System.out.println("Failed to commit, graph rollback." + e.getMessage());
		}
	}

	public static void closeGraph(OrientBaseGraph graph){
		try {
			if(graph!=null){
				graph.shutdown();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}









}