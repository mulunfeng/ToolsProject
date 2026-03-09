package com.nk.db.util;

import com.nk.db.constant.OracleTypeConstant;
import com.nk.db.entity.ColumnTag;
import com.nk.db.entity.TableTag;
import com.nk.excel.util.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by zhangyuyang1 on 2016/7/22.
 */
public class DaoUtils {
    private static Connection connection = null;

//	private static final String url = pro.getProperty("jdbc.url");

    public static Connection getConnection(Properties pro) {
        // 事先导入驱动
        try {
            if (connection == null) {
                String driver = pro.getProperty("jdbc.driver");
                Class.forName(driver);// 加载驱动
                // 创建连接对象,即已经连接到数据库
                if (driver.equalsIgnoreCase("com.mysql.jdbc.Driver")) {
                    connection = DriverManager.getConnection(pro.getProperty("jdbc.url"), pro.getProperty("user"), pro.getProperty("password"));
                } else if (driver.equalsIgnoreCase("oracle.jdbc.driver.OracleDriver")) {
                    connection = DriverManager.getConnection(pro.getProperty("jdbc.url"), pro);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


    public static <T> T getVo(T obj, Statement stmt) throws Exception {
        StringBuilder sql = getSelectEntitySql(obj);
        ResultSet resultSet = stmt.executeQuery(sql.toString());
        T _obj = null;
        if (resultSet.next()) {
            _obj = parseObj(obj, resultSet);
        }
        try {
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return _obj;
    }

    private static <T> StringBuilder getSelectEntitySql(T obj) {
        StringBuilder sqlBuilder = new StringBuilder();
        String tableName = null;
        if (obj.getClass().isAnnotationPresent(TableTag.class)) {
            TableTag des = obj.getClass().getAnnotation(TableTag.class);
            tableName = des.name();
        }
        sqlBuilder.append(" select ");
        Method[] methods = obj.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isAnnotationPresent(ColumnTag.class)) {
                ColumnTag ColumnTag = methods[i].getAnnotation(ColumnTag.class);
                if (StringUtils.isNotBlank(ColumnTag.name())){
                    sqlBuilder.append(tableName + "." + ColumnTag.name()).append(",");
                }
            }
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(" from ").append(tableName);
        return sqlBuilder;
    }

    public static <T> List getList(T obj, Statement stmt, int from, int pageSize) throws Exception {
        StringBuilder sql = getSelectEntitySql(obj);
        sql.append(" limit ");
        sql.append(String.valueOf(from));
        sql.append(" , ");
        sql.append(String.valueOf(pageSize));
        System.out.println(sql.toString());
        ResultSet resultSet = stmt.executeQuery(sql.toString());
        return parseList(obj, resultSet);
    }

    public static <T> List getList(T obj, Statement stmt, String sql)
            throws Exception {

        ResultSet resultSet = stmt.executeQuery(sql);
        return parseList(obj, resultSet);
    }

    private static <T> List parseList(T obj, ResultSet resultSet) throws Exception {
        List<T> list = new ArrayList<T>();
        while (resultSet.next()) {
            list.add(parseObj(obj, resultSet));
        }
        return (List) list;
    }

    private static <T> T parseObj(T obj, ResultSet resultSet) throws Exception {
        Method[] methods = obj.getClass().getDeclaredMethods();
        Map<String, String> map = new HashMap<String, String>();

        T outCome = (T) obj.getClass().newInstance();
        String colName = "";
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().startsWith("get")) {
                ColumnTag ColumnTag = methods[i]
                        .getAnnotation(ColumnTag.class);
                if (ColumnTag != null) {
                    colName = ColumnTag.name();
                    map.put(methods[i].getName().substring(3),
                            colName.toLowerCase());
                }
            }
        }

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().startsWith("set")) {
                if (map.get(methods[i].getName().substring(3)) == null) {
                    ColumnTag ColumnTag = methods[i]
                            .getAnnotation(ColumnTag.class);
                    if (ColumnTag == null) {
                        continue;
                    }
                }

                Object[] objects = methods[i].getParameterTypes();
                ColumnTag columnTag = methods[i].getAnnotation(ColumnTag.class);
                boolean isBlob = columnTag != null && StringUtils.isNotBlank(columnTag.type()) && columnTag.type().equals(OracleTypeConstant.O_BLOB);
                if (objects != null &&
                        StringUtils.isNotBlank(resultSet.getString(map.get(methods[i].getName().substring(3))))) {
                    if (isBlob){
                        Blob noteBlob = resultSet.getBlob(map.get(methods[i].getName().substring(3)));
                        if(noteBlob != null){
                            InputStream is = noteBlob.getBinaryStream();
                            ByteArrayInputStream bais = (ByteArrayInputStream)is;
                            byte[] byte_data = new byte[bais.available()]; //bais.available()返回此输入流的字节数

                            bais.read(byte_data, 0,byte_data.length);//将输入流中的内容读到指定的数组
                            methods[i].invoke(outCome, new String(byte_data,"utf-8"));
                            is.close();
                            continue;
                        }
                    }
                    if (objects[0].equals(Integer.class)) {
                        methods[i].invoke(outCome, resultSet.getInt(map.get(methods[i].getName().substring(3))));
                    } else if (objects[0].equals(String.class)) {
                        methods[i].invoke(outCome, resultSet.getString(map.get(methods[i].getName().substring(3))));
                    } else if (objects[0].equals(Double.class)) {
                        methods[i].invoke(outCome, resultSet.getDouble(map.get(methods[i].getName().substring(3))));
                    } else if (objects[0].equals(Boolean.class)) {
                        methods[i].invoke(outCome, resultSet.getBoolean(map.get(methods[i].getName().substring(3))));
                    } else if (objects[0].equals(Date.class)) {
                        methods[i].invoke(outCome, resultSet.getDate(map.get(methods[i].getName().substring(3))));
                    } else if (objects[0].equals(BigDecimal.class)) {
                        methods[i].invoke(outCome, resultSet.getBigDecimal(map.get(methods[i].getName().substring(3))));
                    } else if (objects[0].equals(Long.class)) {
                        methods[i].invoke(outCome, resultSet.getLong(map.get(methods[i].getName().substring(3))));
                    }  else if (objects[0].equals(Float.class)) {
                        methods[i].invoke(outCome, resultSet.getFloat(map.get(methods[i].getName().substring(3))));
                    } else {
                        throw new RuntimeException("方法:"+methods[i].getName()+"  类型:"+methods[i].getParameterTypes()[0]);
                    }
                }
            }
        }

        return outCome;
    }

    public static <T> int getCount(T obj, Statement stmt) throws SQLException {
        StringBuilder sql = getCountEntitySql(obj);
        ResultSet resultSet = stmt.executeQuery(sql.toString());
        int count = 0;
        while(resultSet.next()){
            count = resultSet.getInt(1);
        }
        return count;
    }

    private static <T> StringBuilder getCountEntitySql(T obj) {
        StringBuilder sqlBuilder = new StringBuilder();
        String tableName = null;
        if (obj.getClass().isAnnotationPresent(TableTag.class)) {
            TableTag des = obj.getClass().getAnnotation(TableTag.class);
            tableName = des.name();
        }
        sqlBuilder.append(" select ");
        sqlBuilder.append("count(1)");
        sqlBuilder.append(" from ").append(tableName);
        return sqlBuilder;
    }

    public static <T> void insert(List<T> list, Statement stmt) throws Exception {
        for (T t : list) {
            String insertSql = getInsertEntitySql(list.get(0));
            insertSql = MessageFormat.format(insertSql, getParameterValueAll(t));
            stmt.executeUpdate(insertSql);
        }
    }

    private static Object[] getParameterValueAll(Object obj) throws Exception {
        List list = new ArrayList();
        Method[] methods = obj.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isAnnotationPresent(ColumnTag.class)) {
                Object myobj = methods[i].invoke(obj, new Object[] {});
                Class returnType = methods[i].getReturnType();
                if (returnType != null) {
                    if (returnType.getSimpleName().equals("Date")) {
                        myobj = DateUtils.toDateTimeString((Date) myobj);
                    }
                }
                list.add("'"+myobj+"'");
            }
        }
        return list.toArray();

    }

    private static <T> String getInsertEntitySql(Object obj) {
        StringBuffer sb = new StringBuffer();
        if (obj.getClass().isAnnotationPresent(TableTag.class)) {
            TableTag des = obj.getClass().getAnnotation(TableTag.class);
            String tableName = des.name();
            sb.append(" insert into ").append(tableName).append(" (");
        }
        Method[] methods = obj.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isAnnotationPresent(ColumnTag.class)) {
                ColumnTag nkProperty = methods[i]
                        .getAnnotation(ColumnTag.class);
                String propertyName = nkProperty.name();
                if (StringUtils.isBlank(propertyName)) {
                    propertyName = getColoumName(methods[i]);
                }
                sb.append(propertyName).append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(") values (");
        int j=0;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isAnnotationPresent(ColumnTag.class)) {
                methods[i].getAnnotation(ColumnTag.class);
                sb.append("{"+j+++"},");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    private static String getColoumName(Method method) {
        String first = method.getName().substring(3,4);
        return method.getName().substring(3).replaceFirst(first , first.toLowerCase());
    }
}
