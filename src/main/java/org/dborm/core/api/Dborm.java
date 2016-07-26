package org.dborm.core.api;

import org.dborm.core.domain.PairDborm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Dborm（数据库对象映射操作框架）
 * Dborm框架操作数据库的核心API
 * <p>
 * 框架名字来由:
 * Dborm由DB加ORM组合得来,合并组成单词,读音:拼音读作[di bao mu], 英文音标为[:dɪ'bɔːm]
 * DB（DataBase）是数据库的简称,ORM（Object Relational Mapping）是对象关系映射,
 * 本框架的核心思路是将对象映射解析为数据库操作的SQL（曾删改等操作）及SQL查询的返回结果映射为对象（查询操作）。
 * <p>
 *
 * @author COCHO
 */
public interface Dborm {

    /**
     * 事务缓冲区
     */
    List<PairDborm<String, List>> transactionSqlCache = new ArrayList<PairDborm<String, List>>();


    /**
     * 开启事务
     * 如果开启事务,则执行非查询操作时将会把SQL放在事务缓冲区中等待最终提交,提交之后执行数据库操作,并将事务设置为默认的自动提交
     */
    void beginTransaction();

    /**
     * 提交事务
     * 将事务缓冲区中的SQL批量执行数据库操作
     * 将事务设置为默认的自动提交
     * 清空事务缓冲区中的SQL
     *
     * @return true:执行成功 false:执行失败或空的参数
     */
    boolean commit();


    /**
     * 新增实体
     * 主键值不能为空
     *
     * @param entity 实体对象
     * @return true:执行成功 false:执行失败或空的参数
     */
    <T> boolean insert(T entity);


    /**
     * 批量新增实体
     * 主键值不能为空
     * 自动添加事务
     *
     * @param entitys 实体对象集合
     * @return true:执行成功 false:执行失败或空的参数
     */
    <T> boolean insert(Collection<T> entitys);

    /**
     * 替换实体（修改所有的列）
     * <p/>
     * 主键值不能为空
     * 属性值不为null,则修改该列
     * 属性值为null,则修改为null
     * 自动添加事务
     *
     * @param entity 实体对象
     * @return true:执行成功 false:执行失败或空的参数
     */
    <T> boolean replace(T entity);

    /**
     * 批量替换实体（修改所有的列）
     * <p/>
     * 主键值不能为空
     * 属性值不为null,则修改该列
     * 属性值为null,则修改为null
     * 自动添加事务
     *
     * @param entitys 实体对象集合
     * @return true:执行成功 false:执行失败或空的参数
     */
    <T> boolean replace(Collection<T> entitys);

    /**
     * 修改实体（仅修改属性值不为null的列）
     * <p/>
     * 主键值不能为空
     * 属性值不为null,则修改该列
     * 属性值为null,则忽略修改该列（修改后仍保持原来的值）
     *
     * @param entity 实体对象
     * @return true:执行成功 false:执行失败或空的参数
     */
    <T> boolean update(T entity);

    /**
     * 批量修改实体（仅修改属性值不为null的列）
     * <p/>
     * 主键值不能为空
     * 属性值不为null,则修改该列
     * 属性值为null,则忽略修改该列（修改后仍保持原来的值）
     * 自动添加事务
     *
     * @param entitys 实体对象集合
     * @return true:执行成功 false:执行失败或空的参数
     */
    <T> boolean update(Collection<T> entitys);

    /**
     * 新增或修改（根据主键查找数据库是否有该记录，有则修改，没有则新增，自动添加事务）
     * 备注:如果当前批次中存在主键重复,则无法监测出来
     * 如:两个相同主键的UserInfo对象,将会做相同的修改或新增操作,而不会出现第一个UserInfo做新增,第二个UserInfo做修改的情况（因为操作完成之前数据库中还不存在该记录）
     *
     * @param entity 实体类
     * @return true:执行成功 false:执行失败或空的参数或空的实体
     */
    <T> boolean saveOrUpdate(T entity);

    /**
     * 批量新增或修改（根据主键查找数据库是否有该记录，有则修改，没有则新增，自动添加事务）
     * 备注:如果当前批次中存在主键重复,则无法监测出来
     * 如:两个相同主键的UserInfo对象,将会做相同的修改或新增操作,而不会出现第一个UserInfo做新增,第二个UserInfo做修改的情况（因为操作完成之前数据库中还不存在该记录）
     *
     * @param entitys 实体类集合
     * @return true:执行成功 false:执行失败或空的参数
     */
    <T> boolean saveOrUpdate(Collection<T> entitys);

    /**
     * 新增或替换（根据主键查找数据库是否有该记录，有则替换，没有则新增，自动添加事务）
     * 备注:如果当前批次中存在主键重复,则无法监测出来
     * 如:两个相同主键的UserInfo对象,将会做相同的修改或新增操作,而不会出现第一个UserInfo做新增,第二个UserInfo做修改的情况（因为操作完成之前数据库中还不存在该记录）
     *
     * @param entity 实体类
     * @return true:执行成功 false:执行失败或空的参数
     */
    <T> boolean saveOrReplace(T entity);

    /**
     * 批量新增或替换（根据主键查找数据库是否有该记录，有则替换，没有则新增，自动添加事务）
     * 备注:如果当前批次中存在主键重复,则无法监测出来
     * 如:两个相同主键的UserInfo对象,将会做相同的修改或新增操作,而不会出现第一个UserInfo做新增,第二个UserInfo做修改的情况（因为操作完成之前数据库中还不存在该记录）
     *
     * @param entitys 实体类集合
     * @return true:执行成功 false:执行失败或空的参数
     */
    <T> boolean saveOrReplace(Collection<T> entitys);

    /**
     * 删除实体(主键值不能为空，自动添加事务)
     *
     * @param entity 实体对象
     * @return true:执行成功 false:执行失败或空的参数
     */
    <T> boolean delete(T entity);

    /**
     * 批量删除实体(主键值不能为空，自动添加事务)
     *
     * @param entitys 实体对象集合
     * @return true:执行成功 false:执行失败或空的参数
     */
    <T> boolean delete(Collection<T> entitys);

    /**
     * 根据查询语句返回实体(如果查询出多个实体时仅返回第一个)
     *
     * @param entityClass 返回的实体类型
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体或null
     */
    <T> T getEntity(Class<?> entityClass, String sql, Object... bindArgs);

    /**
     * 根据查询语句返回实体(如果查询出多个实体时仅返回第一个)
     *
     * @param entityClass 返回的实体类型
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体或null
     */
    <T> T getEntity(Class<?> entityClass, String sql, List bindArgs);

    /**
     * 根据查询语句返回实体(如果查询出多个实体时仅返回第一个)
     *
     * @param connection  数据库连接
     * @param entityClass 返回的实体类型
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体或null
     */
    <T> T getEntity(Object connection, Class<?> entityClass, String sql, Object... bindArgs);

    /**
     * 根据查询语句返回实体(如果查询出多个实体时仅返回第一个)
     *
     * @param connection  数据库连接
     * @param entityClass 返回的实体类型
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体或null
     */
    <T> T getEntity(Object connection, Class<?> entityClass, String sql, List bindArgs);

    /**
     * 根据查询语句返回实体集合
     *
     * @param entityClass 返回的实体类型
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体集合或无实体的list集合
     * @author COCHO
     * @time 2013-5-6上午11:23:46
     */
    <T> List<T> getEntities(Class<?> entityClass, String sql, Object... bindArgs);

    /**
     * 根据查询语句返回实体集合
     *
     * @param entityClass 返回的实体类型
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体集合或无实体的list集合
     * @author COCHO
     * @time 2013-5-6上午11:23:46
     */
    <T> List<T> getEntities(Class<?> entityClass, String sql, List bindArgs);

    /**
     * 根据查询语句返回实体集合
     *
     * @param entityClass 返回的实体类型
     * @param connection  数据库连接
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体集合或无实体的list集合
     * @author COCHO
     * @time 2013-5-6上午11:23:46
     */
    <T> List<T> getEntities(Class<?> entityClass, Object connection, String sql, Object... bindArgs);

    /**
     * 根据查询语句返回实体集合
     *
     * @param entityClass 返回的实体类型
     * @param connection  数据库连接
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体集合或无实体的list集合
     * @author COCHO
     * @time 2013-5-6上午11:23:46
     */
    <T> List<T> getEntities(Class<?> entityClass, Object connection, String sql, List bindArgs);

    /**
     * 组合查询（连接的多个表中有相同字段的时候不建议使用该方式）
     * 可以将查询结果映射到多个实体组中
     *
     * @param entityClasses 实体类组合
     * @param sql           查询语句
     * @param bindArgs      查询语句所需的参数（该参数允许为null）
     * @return 实体集合或无实体的list集合
     * @author COCHO
     * @time 2013-6-7上午10:42:18
     */
    List<Map<String, Object>> getEntities(Class<?>[] entityClasses, String sql, Object... bindArgs);

    /**
     * 组合查询（连接的多个表中有相同字段的时候不建议使用该方式）
     * 可以将查询结果映射到多个实体组中
     *
     * @param entityClasses 实体类组合
     * @param sql           查询语句
     * @param bindArgs      查询语句所需的参数（该参数允许为null）
     * @return 实体集合或无实体的list集合
     * @author COCHO
     * @time 2013-6-7上午10:42:18
     */
    List<Map<String, Object>> getEntities(Class<?>[] entityClasses, String sql, List bindArgs);

    /**
     * 实例查询（根据实例对象中属性值不为空的属性做过滤条件，默认情况下，添加之间是AND关系）
     *
     * @param example 实例模版
     * @param <T>     实例类型
     * @return 实体对象(如果有多个实体对象则返回第一个)或null
     */
    <T> T getEntityByExample(T example);

    /**
     * 实例查询（根据实例对象中属性值不为空的属性做过滤条件）
     *
     * @param example 实例模版
     * @param isAnd   true：使用AND连接多个条件，false：使用OR连接多个条件
     * @param <T>     实例类型
     * @return 实体对象(如果有多个实体对象则返回第一个)或null
     */
    <T> T getEntityByExample(T example, boolean isAnd);

    /**
     * 实例查询（根据实例对象中属性值不为空的属性做过滤条件，默认情况下，添加之间是AND关系）
     *
     * @param example 实例模版
     * @param <T>     实例类型
     * @return 实体集合或无实体的list集合
     */
    <T> List<T> getEntitiesByExample(T example);

    /**
     * 实例查询（根据实例对象中属性值不为空的属性做过滤条件）
     *
     * @param example 实例模版
     * @param isAnd   true：使用AND连接多个条件，false：使用OR连接多个条件
     * @param <T>     实例类型
     * @return 实体集合或无实体的list集合
     */
    <T> List<T> getEntitiesByExample(T example, boolean isAnd);

    /**
     * 根据对象主键判断对象是否存在
     *
     * @param entity 实体对象
     * @return true：存在;false：不存在
     * @author COCHO
     * @time 2013-5-15上午11:29:16
     */
    <T> boolean isExist(T entity);

    /**
     * 根据对象主键判断对象是否存在
     *
     * @param entity     实体对象
     * @param connection 数据库操作对象
     * @return true：存在;false：不存在
     * @author COCHO
     */
    <T> boolean isExist(T entity, Object connection);

    /**
     * 获得实体类的记录条数
     *
     * @param entityClass 实体类
     * @return 条数
     * @author COCHO
     * @time 2013-6-6下午5:23:13
     */
    long getEntityCount(Class<?> entityClass);

    /**
     * 查询行数
     *
     * @param sql      查询行数的SQL语句（必须是select count(*) from ...）
     * @param bindArgs SQL语句所需参数（该参数允许为null）
     * @return 行数
     * @author COCHO
     * @time 2013-5-15上午11:32:30
     */
    long getCount(String sql, Object... bindArgs);

    /**
     * 查询行数
     *
     * @param sql      查询行数的SQL语句（必须是select count(*) from ...）
     * @param bindArgs SQL语句所需参数（该参数允许为null）
     * @return 行数
     */
    long getCount(String sql, List bindArgs);

    /**
     * 执行SQL
     *
     * @param sql sql语句
     * @return true:执行成功 false:执行失败或空的参数
     * @author COCHO
     */
    boolean execSql(String sql);


    /**
     * 执行SQL
     *
     * @param sql      SQL语句
     * @param bindArgs SQL语句所需的参数（该参数允许为null）
     * @return true:执行成功 false:执行失败或空的参数
     * @author COCHO
     * @time 2013-5-6下午4:23:11
     */
    boolean execSql(String sql, Object... bindArgs);

    /**
     * 执行SQL
     *
     * @param sql      SQL语句
     * @param bindArgs SQL语句所需的参数（该参数允许为null）
     * @return true:执行成功 false:执行失败或空的参数
     * @author COCHO
     * @time 2013-5-6下午4:23:11
     */
    boolean execSql(String sql, List bindArgs);

    /**
     * 执行指定的SQL语句
     *
     * @param connection 数据库连接
     * @param sql        sql语句
     * @param bindArgs   sql语句所需的参数
     * @return true:执行成功 false:执行失败或空的参数
     */
    boolean execSql(Object connection, String sql, Object... bindArgs);

    /**
     * 执行指定的SQL语句
     *
     * @param connection 数据库连接
     * @param sql        sql语句
     * @param bindArgs   sql语句所需的参数
     * @return true:执行成功 false:执行失败或空的参数
     */
    boolean execSql(Object connection, String sql, List bindArgs);

    /**
     * 按事务方式批量执行SQL
     *
     * @param execSqlPairList sql语句集合
     * @return true:执行成功 false:执行失败或空的参数
     * @author COCHO
     * @time 2013-6-7下午3:08:45
     */
    boolean execSql(Collection<PairDborm<String, List>> execSqlPairList);

    /**
     * 获得数据库连接
     *
     * @return 数据库连接或null
     */
    Object getConnection();

    /**
     * 获取数据库操作类
     * @return
     */
    DbormDataBase getDataBase();

    /**
     * 设置数据库操作类
     * @param dataBase
     */
    void setDataBase(DbormDataBase dataBase);



}
