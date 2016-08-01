package org.dborm.core.framework;

import org.dborm.core.api.DbormLogger;
import org.dborm.core.api.SQLExecutor;
import org.dborm.core.domain.QueryResult;
import org.dborm.core.utils.DbormContexts;
import org.dborm.core.domain.PairDborm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class SQLExecutorHandler implements SQLExecutor {

    public DbormLogger logger;

    public SQLExecutorHandler(DbormLogger logger) {
        this.logger = logger;
    }

    @Override
    public void execSQL(String sql, List bindArgs, Object connection) throws Exception {
        PreparedStatement pst = null;
        try {
            Connection conn = (Connection) connection;
            pst = conn.prepareStatement(sql);
            if (bindArgs != null) {
                for (int i = 0; i < bindArgs.size(); i++) {
                    pst.setObject(i + 1, bindArgs.get(i));
                }
            }
            showSql(getSql(sql, bindArgs));
            pst.executeUpdate();
        } finally {
            if (pst != null) {
                pst.close();
            }
        }
    }

    @Override
    public void execSQLUseTransaction(Collection<PairDborm<String, List>> execSqlPairList, Object connection) throws Exception {
        PreparedStatement pst = null;
        String currentSql = "";
        Connection conn = (Connection) connection;
        try {
            conn.setAutoCommit(false);
            StringBuilder sqlBuffer = new StringBuilder();
            for (PairDborm<String, List> pair : execSqlPairList) {
                currentSql = getSql(pair.first, pair.second);
                sqlBuffer.append(currentSql);
                sqlBuffer.append(";\n");
                pst = conn.prepareStatement(pair.first);
                if (pair.second != null) {
                    for (int x = 0; x < pair.second.size(); x++) {
                        pst.setObject(x + 1, pair.second.get(x));
                    }
                }
                pst.executeUpdate();
            }
            logger.debug(sqlBuffer.toString());
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw new Exception("出异常的SQL如下:\n" + currentSql, e);
        } finally {
            if (pst != null) {
                pst.close();
            }
        }
    }

    @Override
    public List<QueryResult> query(String sql, List bindArgs, Object connection) throws Exception {
        List<QueryResult> queryResults = new ArrayList<QueryResult>();
        Connection conn = (Connection) connection;
        PreparedStatement pst = conn.prepareStatement(sql);
        if (bindArgs != null) {
            for (int i = 0; i < bindArgs.size(); i++) {
                pst.setObject(i + 1, bindArgs.get(i));
            }
        }
        showSql(getSql(sql, bindArgs));
        ResultSet rs = null;
        try {
            rs = pst.executeQuery();
            while (rs.next()) {
                ResultSetMetaData data = rs.getMetaData();
                QueryResult queryResult = new QueryResult();
                for (int i = 1; i <= data.getColumnCount(); i++) {
                    String columnLabel = data.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    queryResult.putResult(columnLabel, value);
                    queryResult.addResult(value);
                }
                queryResults.add(queryResult);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return queryResults;
    }


    @Override
    public List<QueryResult> query(String sql, List bindArgs, Map<String, Field> fields, Object connection) throws Exception {
        return query(sql, bindArgs, connection);
    }


    /**
     * 检查SQL语句并做日志记录
     *
     * @param sql      sql语句
     * @param bindArgs sql语句所绑定的参数
     * @author COCHO
     * @time 2013-5-7上午10:55:38
     */
    private String getSql(String sql, List bindArgs) {
        if (sql != null && bindArgs != null) {
            for (Object bindArg : bindArgs) {
                String arg;
                if (bindArg == null) {
                    arg = "null";
                } else if (bindArg instanceof Date) {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS");
                    arg = "'" + format.format((Date) bindArg) + "'";
                } else if (bindArg instanceof String) {
                    arg = "'" + bindArg + "'";
                } else {
                    arg = bindArg.toString();
                }
                sql = sql.replaceFirst("[?]", arg);
            }
        }
        return sql;
    }

    private void showSql(String sql) {
        if (DbormContexts.showSql) {
            logger.debug(sql);
        }
    }


}
