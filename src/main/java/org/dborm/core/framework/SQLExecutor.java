package org.dborm.core.framework;

import org.dborm.core.utils.DbormContexts;
import org.dborm.core.utils.LoggerUtilsDborm;
import org.dborm.core.utils.PairDborm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 连接数据库及执行SQL
 *
 * @author COCHO
 * @time 2013-5-6上午10:40:40
 */
public class SQLExecutor {


    LoggerUtilsDborm loggerUtilsDborm = new LoggerUtilsDborm();

    /**
     * 执行SQL(并作SQL检查及输出)
     *
     * @param sql      sql语句
     * @param bindArgs 该SQL语句所需的参数
     * @param conn     数据库连接
     * @throws Exception
     * @author COCHO
     * @time 2013-6-7下午2:54:48
     */
    public void execSQL(String sql, List bindArgs, Connection conn) throws Exception {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(sql);
            if (bindArgs != null) {
                for (int i = 0; i < bindArgs.size(); i++) {
                    pst.setObject(i + 1, bindArgs.get(i));
                }
            }
            showSql(getSql(sql, bindArgs));
            pst.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            if (pst != null) {
                pst.close();
            }
        }
    }

    /**
     * 批量执行SQL，在事务中完成
     *
     * @param execSqlPairList 第一个参数为SQL语句， 第二个参数为SQL语句所需的参数
     * @param conn            数据库连接
     * @throws Exception
     * @author COCHO
     * @time 2013-5-6上午10:41:26
     */
    public void execSQLUseTransaction(Collection<PairDborm<String, List>> execSqlPairList, Connection conn) throws Exception {
        PreparedStatement pst = null;
        String currentSql = "";
        try {
            conn.setAutoCommit(false);
            StringBuffer sqlBuffer = new StringBuffer();
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
            conn.commit();
            loggerUtilsDborm.debug(sqlBuffer.toString());
        } catch (Exception e) {
            conn.rollback();
            loggerUtilsDborm.error("出异常的SQL如下:\n" + currentSql, e);
            throw e;
        } finally {
            if (pst != null) {
                pst.close();
            }
        }
    }

    /**
     * 查询操作
     *
     * @param sql      查询语句
     * @param bindArgs 查询语句所需的参数
     * @param conn     数据库连接
     * @return 查询结果集或null
     * @throws Exception
     * @author COCHO
     * @time 2013-5-6上午10:43:44
     */
    public ResultSet getResultSet(String sql, List bindArgs, Connection conn) throws Exception {
        ResultSet result;
        PreparedStatement pst = conn.prepareStatement(sql);
        if (bindArgs != null) {
            for (int i = 0; i < bindArgs.size(); i++) {
                pst.setObject(i + 1, bindArgs.get(i));
            }
        }
        showSql(getSql(sql, bindArgs));
        result = pst.executeQuery();
        return result;
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
            loggerUtilsDborm.debug(sql);
        }
    }


}
