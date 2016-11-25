package com.nk.db.util;

import com.nk.hbase.entity.WebPageInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

/**
 * Created by zhangyuyang1 on 2016/7/22.
 */
public class DbToVoUtils {

    private final static String dbConn = "jdbc:mysql://127.0.0.1:3306/test?createDatabaseIfNotExist=true&amp;characterEncoding=utf-8&amp;useUnicode=true";
    private final static String username = "root";
    private final static String password = "root";

    /**
     * 从数据库获取该实体数据
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List getDataFromDB(Class<T> clazz) {
        return getDataFromDB( clazz, 0, Integer.MAX_VALUE);
    }

    private static Properties getProp(String url, String username, String password) {
        Properties pro = new Properties();
        pro.put("jdbc.driver", "com.mysql.jdbc.Driver");
        pro.put("jdbc.url", url);
        pro.put("user", username);
        pro.put("password", password);
        pro.put("remarksReporting", "true");
        return pro;
    }

    public static <T> List getDataFromDB(String url, String username, String password, Class<T> clazz, int from, int pageSize) {
        Properties pro = getProp(url,username,password);
        try {
            List<T> projectList = getList(pro, clazz.newInstance(), from, pageSize);
            return projectList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> List<T> getList(Properties pro, T obj, int from, int pageSize) throws Exception {
        Statement stmt = null;
        Connection conn = DaoUtils.getConnection(pro);
        try {
            stmt = conn.createStatement();
            List<T> list = DaoUtils.getList(obj, stmt,from, pageSize);
            return list;
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private static <T> int getCount(Properties pro, Class<T> clazz){
        Statement stmt = null;
        Connection conn = DaoUtils.getConnection(pro);
        try {
            stmt = conn.createStatement();
            int count = DaoUtils.getCount(clazz.newInstance(), stmt);
            return count;
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static <T> List<WebPageInfo> getDataFromDB(Class<T> clazz, int from, int pageSize) {
        return getDataFromDB(dbConn, username, password, clazz, from, pageSize);
    }

    public static <T> int getCountFromDB(Class<T> clazz){
        Properties pro = getProp(dbConn, username, password);
        return getCount(pro, clazz);
    }
}

