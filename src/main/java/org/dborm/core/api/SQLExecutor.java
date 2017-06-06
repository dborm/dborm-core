package org.dborm.core.api;

import org.dborm.core.domain.QueryResult;
import org.dborm.core.domain.PairDborm;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 连接数据库及执行SQL
 *
 * @author COCHO
 * @time 2013-5-6上午10:40:40
 */
public interface SQLExecutor {

    /**
     * 执行SQL(并作SQL检查及输出)
     *
     * @param sql        sql语句
     * @param bindArgs   该SQL语句所需的参数
     * @param connection 数据库连接（注意:实现类中用完该连接不要关闭,框架中用完之后会自动关闭连接）
     * @author COCHO
     * @time 2013-6-7下午2:54:48
     */
    int execSQL(String sql, List bindArgs, Object connection) throws Exception;

    /**
     * 批量执行SQL，在事务中完成
     *
     * @param execSqlPairList 第一个参数为SQL语句， 第二个参数为SQL语句所需的参数
     * @param connection      数据库连接（注意:实现类中用完该连接不要关闭,框架中用完之后会自动关闭连接）
     * @author COCHO
     * @time 2013-5-6上午10:41:26
     */
    int execSQLUseTransaction(Collection<PairDborm<String, List>> execSqlPairList, Object connection) throws Exception;

    /**
     * 查询操作
     *
     * @param sql        查询语句
     * @param bindArgs   查询语句所需的参数
     * @param connection 数据库连接（注意:实现类中用完该连接不要关闭,框架中用完之后会自动关闭连接）
     * @return 查询结果集或null
     * @author COCHO
     * @time 2013-5-6上午10:43:44
     */
    List<QueryResult> query(String sql, List bindArgs, Object connection) throws Exception;


    /**
     * 查询操作
     *
     * @param sql        查询语句
     * @param bindArgs   查询语句所需的参数
     * @param fields     结果集映射的属性集合（键:列名,值:列对应的属性）
     * @param connection 数据库连接（注意:实现类中用完该连接不要关闭,框架中用完之后会自动关闭连接）
     * @return 查询结果集或null
     * @author COCHO
     * @time 2013-5-6上午10:43:44
     */
    List<QueryResult> query(String sql, List bindArgs, Map<String, Field> fields, Object connection) throws Exception;


}
