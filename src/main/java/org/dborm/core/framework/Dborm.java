package org.dborm.core.framework;

import org.dborm.core.utils.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * 通过Dborm深度操作数据库（级联操作数据库，自动添加事务，操作成功返回true,操作失败返回false）
 *
 * @author COCHO
 */
public class Dborm {

    SQLPairFactory sqlPairFactory = new SQLPairFactory(this);
    SQLExecutor sqlExecutor = new SQLExecutor();
    LoggerUtilsDborm loggerUtils = new LoggerUtilsDborm();
    StringUtilsDborm stringUtils = new StringUtilsDborm();
    EntityResolver entityResolver = new EntityResolver();


    private DbormDataBase dataBase;

    public Dborm(DbormDataBase dataBase) {
        this.dataBase = dataBase;
    }

    public Dborm(DbormDataBase dataBase, DbormLogger logger) {
        this.dataBase = dataBase;
        DbormContexts.log = logger;
    }


    /**
     * 事务缓冲区
     */
    List<PairDborm<String, List>> transactionSqlCache = new ArrayList<PairDborm<String, List>>();

    private boolean autoCommit = true;//true:自动提交 false:需调用commit函数才会提交


    /**
     * 开启事务
     * 如果开启事务,则执行非查询操作时将会把SQL放在事务缓冲区中等待最终提交,提交之后执行数据库操作,并将事务设置为默认的自动提交
     */
    public void beginTransaction() {
        this.autoCommit = false;
        transactionSqlCache.clear();
    }

    /**
     * 提交事务
     * 将事务缓冲区中的SQL批量执行数据库操作
     * 将事务设置为默认的自动提交
     * 清空事务缓冲区中的SQL
     *
     * @return true:执行成功 false:执行失败或空的参数
     */
    public boolean commit() {
        boolean result = execute(transactionSqlCache);
        autoCommit = true;
        transactionSqlCache.clear();
        return result;
    }


    /**
     * 新增实体
     * 主键值不能为空
     *
     * @param entity 实体对象
     * @return true:执行成功 false:执行失败或空的参数
     */
    public <T> boolean insert(T entity) {
        return insert(toEntityCollection(entity));
    }


    /**
     * 批量新增实体
     * 主键值不能为空
     * 自动添加事务
     *
     * @param entitys 实体对象集合
     * @return true:执行成功 false:执行失败或空的参数
     */
    public <T> boolean insert(Collection<T> entitys) {
        boolean result = false;
        if (entitys != null && entitys.size() > 0) {
            List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
            for (T entity : entitys) {
                pairList.addAll(sqlPairFactory.insertDeep(entity));
            }
            result = execSqlByTransaction(pairList);
        }
        return result;
    }

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
    public <T> boolean replace(T entity) {
        return replace(toEntityCollection(entity));
    }

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
    public <T> boolean replace(Collection<T> entitys) {
        boolean result = false;
        if (entitys != null && entitys.size() > 0) {
            List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
            for (T entity : entitys) {
                pairList.addAll(sqlPairFactory.replaceDeep(entity));
            }
            result = execSqlByTransaction(pairList);
        }
        return result;
    }

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
    public <T> boolean update(T entity) {
        return update(toEntityCollection(entity));
    }

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
    public <T> boolean update(Collection<T> entitys) {
        boolean result = false;
        if (entitys != null && entitys.size() > 0) {
            List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
            for (T entity : entitys) {
                pairList.addAll(sqlPairFactory.updateDeep(entity));
            }
            result = execSqlByTransaction(pairList);
        }
        return result;
    }

    /**
     * 新增或修改（根据主键查找数据库是否有该记录，有则修改，没有则新增，自动添加事务）
     * 备注:如果当前批次中存在主键重复,则无法监测出来
     * 如:两个相同主键的UserInfo对象,将会做相同的修改或新增操作,而不会出现第一个UserInfo做新增,第二个UserInfo做修改的情况（因为操作完成之前数据库中还不存在该记录）
     *
     * @param entity 实体类
     * @return true:执行成功 false:执行失败或空的参数或空的实体
     */
    public <T> boolean saveOrUpdate(T entity) {
        return saveOrUpdate(toEntityCollection(entity));
    }

    /**
     * 批量新增或修改（根据主键查找数据库是否有该记录，有则修改，没有则新增，自动添加事务）
     * 备注:如果当前批次中存在主键重复,则无法监测出来
     * 如:两个相同主键的UserInfo对象,将会做相同的修改或新增操作,而不会出现第一个UserInfo做新增,第二个UserInfo做修改的情况（因为操作完成之前数据库中还不存在该记录）
     *
     * @param entitys 实体类集合
     * @return true:执行成功 false:执行失败或空的参数
     */
    public <T> boolean saveOrUpdate(Collection<T> entitys) {
        boolean result = true;
        if (entitys != null && entitys.size() > 0) {
            Connection conn = getConnection();
            if (conn != null) {
                try {
                    List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
                    for (T entity : entitys) {
                        pairList.addAll(sqlPairFactory.saveOrUpdateDeep(entity, conn));
                    }

                    if (autoCommit) {
                        sqlExecutor.execSQLUseTransaction(pairList, conn);
                    } else {
                        transactionSqlCache.addAll(pairList);
                    }
                } catch (Exception e) {
                    result = false;
                    loggerUtils.error(e);
                } finally {
                    dataBase.closeConn(conn);
                }
            }
        }
        return result;
    }

    /**
     * 新增或替换（根据主键查找数据库是否有该记录，有则替换，没有则新增，自动添加事务）
     * 备注:如果当前批次中存在主键重复,则无法监测出来
     * 如:两个相同主键的UserInfo对象,将会做相同的修改或新增操作,而不会出现第一个UserInfo做新增,第二个UserInfo做修改的情况（因为操作完成之前数据库中还不存在该记录）
     *
     * @param entity 实体类
     * @return true:执行成功 false:执行失败或空的参数
     */
    public <T> boolean saveOrReplace(T entity) {
        return saveOrReplace(toEntityCollection(entity));
    }

    /**
     * 批量新增或替换（根据主键查找数据库是否有该记录，有则替换，没有则新增，自动添加事务）
     * 备注:如果当前批次中存在主键重复,则无法监测出来
     * 如:两个相同主键的UserInfo对象,将会做相同的修改或新增操作,而不会出现第一个UserInfo做新增,第二个UserInfo做修改的情况（因为操作完成之前数据库中还不存在该记录）
     *
     * @param entitys 实体类集合
     * @return true:执行成功 false:执行失败或空的参数
     */
    public <T> boolean saveOrReplace(Collection<T> entitys) {
        boolean result = true;
        if (entitys != null && entitys.size() > 0) {
            Connection conn = getConnection();
            if (conn != null) {
                try {
                    List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
                    for (T entity : entitys) {
                        pairList.addAll(sqlPairFactory.saveOrReplaceDeep(entity, conn));
                    }

                    if (autoCommit) {
                        sqlExecutor.execSQLUseTransaction(pairList, conn);
                    } else {
                        transactionSqlCache.addAll(pairList);
                    }
                } catch (Exception e) {
                    result = false;
                    loggerUtils.error(e);
                } finally {
                    dataBase.closeConn(conn);
                }
            }
        }
        return result;
    }

    /**
     * 删除实体(主键值不能为空，自动添加事务)
     *
     * @param entity 实体对象
     * @return true:执行成功 false:执行失败或空的参数
     */
    public <T> boolean delete(T entity) {
        return delete(toEntityCollection(entity));
    }

    /**
     * 批量删除实体(主键值不能为空，自动添加事务)
     *
     * @param entitys 实体对象集合
     * @return true:执行成功 false:执行失败或空的参数
     */
    public <T> boolean delete(Collection<T> entitys) {
        boolean result = false;
        if (entitys != null && entitys.size() > 0) {
            List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
            for (T entity : entitys) {
                pairList.addAll(sqlPairFactory.deleteDeep(entity));
            }
            result = execSqlByTransaction(pairList);
        }
        return result;
    }


    /**
     * 根据查询语句返回实体(如果查询出多个实体时仅返回第一个)
     *
     * @param entityClass 返回的实体类型
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体或null
     */
    public <T> T getEntity(Class<?> entityClass, String sql, Object... bindArgs) {
        return getEntity(entityClass, sql, toList(bindArgs));
    }

    /**
     * 根据查询语句返回实体(如果查询出多个实体时仅返回第一个)
     *
     * @param entityClass 返回的实体类型
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体或null
     */
    public <T> T getEntity(Class<?> entityClass, String sql, List bindArgs) {
        T result = null;
        Connection conn = getConnection();
        if (conn != null) {
            try {
                result = getEntity(entityClass, conn, sql, bindArgs);
            } catch (Exception e) {
                loggerUtils.error(e);
            } finally {
                dataBase.closeConn(conn);
            }
        }
        return result;
    }

    /**
     * 根据查询语句返回实体(如果查询出多个实体时仅返回第一个)
     *
     * @param entityClass 返回的实体类型
     * @param conn        数据库连接
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体或null
     */
    public <T> T getEntity(Class<?> entityClass, Connection conn, String sql, Object... bindArgs) {
        return getEntity(entityClass, conn, sql, toList(bindArgs));
    }

    /**
     * 根据查询语句返回实体(如果查询出多个实体时仅返回第一个)
     *
     * @param entityClass 返回的实体类型
     * @param conn        数据库连接
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体或null
     */
    public <T> T getEntity(Class<?> entityClass, Connection conn, String sql, List bindArgs) {
        if (stringUtils.isNotBlank(sql) && entityClass != null && conn != null) {
            try {
                List<T> entityList = getEntities(entityClass, conn, sql, bindArgs);
                if (entityList != null && entityList.size() > 0) {
                    return entityList.get(0);
                }
            } catch (Exception e) {
                loggerUtils.error(e);
            }
        }
        return null;
    }

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
    public <T> List<T> getEntities(Class<?> entityClass, String sql, Object... bindArgs) {
        return getEntities(entityClass, sql, toList(bindArgs));
    }

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
    public <T> List<T> getEntities(Class<?> entityClass, String sql, List bindArgs) {
        List<T> results = new ArrayList<T>();
        Connection conn = getConnection();
        if (conn != null) {
            try {
                results = getEntities(entityClass, conn, sql, bindArgs);
            } catch (Exception e) {
                loggerUtils.error(e);
            } finally {
                dataBase.closeConn(conn);
            }
        }
        return results;
    }

    /**
     * 根据查询语句返回实体集合
     *
     * @param entityClass 返回的实体类型
     * @param conn        数据库连接
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体集合或无实体的list集合
     * @author COCHO
     * @time 2013-5-6上午11:23:46
     */
    public <T> List<T> getEntities(Class<?> entityClass, Connection conn, String sql, Object... bindArgs) {
        return getEntities(entityClass, conn, sql, toList(bindArgs));
    }

    /**
     * 根据查询语句返回实体集合
     *
     * @param entityClass 返回的实体类型
     * @param conn        数据库连接
     * @param sql         查询语句
     * @param bindArgs    查询语句所需的参数（该参数允许为null）
     * @return 实体集合或无实体的list集合
     * @author COCHO
     * @time 2013-5-6上午11:23:46
     */
    public <T> List<T> getEntities(Class<?> entityClass, Connection conn, String sql, List bindArgs) {
        List<T> results = new ArrayList<T>();
        if (stringUtils.isNotBlank(sql) && entityClass != null && conn != null) {
            ResultSet rs = null;
            try {
                rs = sqlExecutor.getResultSet(sql, bindArgs, conn);
                if (rs != null) {
                    String[] columnNames = getColumnNames(rs);
                    while (rs.next()) {
                        Object entity = entityResolver.getEntityAll(entityClass, rs, columnNames);
                        results.add((T) entity);
                    }
                }
            } catch (Exception e) {
                loggerUtils.error(e);
            } finally {
                closeRs(rs);
            }
        }
        return results;
    }

    /**
     * 多表联合查询
     *
     * @param entityClasses 实体类集合
     * @param sql           查询语句
     * @param bindArgs      查询语句所需的参数（该参数允许为null）
     * @return 实体集合或无实体的list集合
     * @author COCHO
     * @time 2013-6-7上午10:42:18
     */
    public List<Map<String, Object>> getEntities(Class<?>[] entityClasses, String sql, Object... bindArgs) {
        return getEntities(entityClasses, sql, toList(bindArgs));
    }

    /**
     * 多表联合查询
     *
     * @param entityClasses 实体类集合
     * @param sql           查询语句
     * @param bindArgs      查询语句所需的参数（该参数允许为null）
     * @return 实体集合或无实体的list集合
     * @author COCHO
     * @time 2013-6-7上午10:42:18
     */
    public List<Map<String, Object>> getEntities(Class<?>[] entityClasses, String sql, List bindArgs) {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        if (stringUtils.isNotBlank(sql) && entityClasses != null && entityClasses.length > 0) {
            Connection conn = getConnection();
            if (conn != null) {
                ResultSet rs = null;
                try {
                    rs = sqlExecutor.getResultSet(sql, bindArgs, conn);
                    if (rs != null) {
                        String[] columnNames = getColumnNames(rs);
                        while (rs.next()) {// 遍历每一行记录
                            Map<String, Object> entityTeam = new HashMap<String, Object>();// 实体组
                            for (Class<?> entityClass : entityClasses) {// 对每一个对象实例化
                                Object entity = entityResolver.getEntity(entityClass, rs, columnNames);
                                entityTeam.put(entityClass.getName(), entity);
                            }
                            results.add(entityTeam);
                        }
                    }
                } catch (Exception e) {
                    loggerUtils.error(e);
                } finally {
                    closeRs(rs);
                }
            }
        }
        return results;
    }

    /**
     * 根据实例模版查询（根据实例对象中属性值不为空的属性做过滤条件，默认情况下，添加之间是AND关系）
     *
     * @param example 实例模版
     * @param <T>     实例类型
     * @return 实体对象(如果有多个实体对象则返回第一个)或null
     */
    public <T> T getEntityByExample(T example) {
        return getEntityByExample(example, true);
    }

    /**
     * 根据实例模版查询（根据实例对象中属性值不为空的属性做过滤条件）
     *
     * @param example 实例模版
     * @param isAnd   true：使用AND连接多个条件，false：使用OR连接多个条件
     * @param <T>     实例类型
     * @return 实体对象(如果有多个实体对象则返回第一个)或null
     */
    public <T> T getEntityByExample(T example, boolean isAnd) {
        if (example != null) {
            try {
                List<T> entityList = getEntitiesByExample(example, isAnd);
                if (entityList != null && entityList.size() > 0) {
                    return entityList.get(0);
                }
            } catch (Exception e) {
                loggerUtils.error(e);
            }
        }
        return null;
    }

    /**
     * 根据实例模版查询（根据实例对象中属性值不为空的属性做过滤条件，默认情况下，添加之间是AND关系）
     *
     * @param example 实例模版
     * @param <T>     实例类型
     * @return 实体集合或无实体的list集合
     */
    public <T> List<T> getEntitiesByExample(T example) {
        return getEntitiesByExample(example, true);
    }

    /**
     * 根据实例模版查询（根据实例对象中属性值不为空的属性做过滤条件）
     *
     * @param example 实例模版
     * @param isAnd   true：使用AND连接多个条件，false：使用OR连接多个条件
     * @param <T>     实例类型
     * @return 实体集合或无实体的list集合
     */
    public <T> List<T> getEntitiesByExample(T example, boolean isAnd) {
        List<T> results = new ArrayList<T>();
        Connection conn = getConnection();
        if (conn != null) {
            try {
                PairDborm<String, List> pair = sqlPairFactory.getEntitiesByExample(example, isAnd);
                results = getEntities(example.getClass(), conn, pair.first, pair.second);
            } catch (Exception e) {
                loggerUtils.error(e);
            } finally {
                dataBase.closeConn(conn);
            }
        }
        return results;
    }

    /**
     * 根据对象主键判断对象是否存在
     *
     * @param entity 实体对象
     * @return true：存在;false：不存在
     * @author COCHO
     * @time 2013-5-15上午11:29:16
     */
    public <T> boolean isExist(T entity) {
        boolean result = false;
        if (entity != null) {
            Connection conn = getConnection();
            if (conn != null) {
                try {
                    result = isExist(entity, conn);
                } catch (Exception e) {
                    loggerUtils.error(e);
                } finally {
                    dataBase.closeConn(conn);
                }
            }
        }
        return result;
    }

    /**
     * 根据对象主键判断对象是否存在
     *
     * @param entity 实体对象
     * @param conn   数据库操作对象
     * @return true：存在;false：不存在
     * @author COCHO
     */
    public <T> boolean isExist(T entity, Connection conn) {
        boolean result = false;
        if (entity != null) {
            PairDborm<String, List> pair = sqlPairFactory.getCountByPrimaryKey(entity);
            if (pair != null) {
                ResultSet rs = null;
                try {
                    rs = sqlExecutor.getResultSet(pair.first, pair.second, conn);
                    if (rs != null && rs.next() && rs.getInt(1) > 0) {// rs.moveToNext()一定要走
                        result = true;
                    }
                } catch (Exception e) {
                    loggerUtils.error(e);
                } finally {
                    closeRs(rs);
                }
            }
        }
        return result;
    }

    /**
     * 获得实体类的记录条数
     *
     * @param entityClass 实体类
     * @return 条数
     * @author COCHO
     * @time 2013-6-6下午5:23:13
     */
    public int getEntityCount(Class<?> entityClass) {
        int count = 0;
        if (entityClass != null) {
            Connection conn = getConnection();
            if (conn != null) {
                ResultSet rs = null;
                try {
                    PairDborm<String, List> pair = sqlPairFactory.getEntityCount(entityClass);
                    rs = sqlExecutor.getResultSet(pair.first, pair.second, conn);
                    if (rs != null) {
                        rs.next();
                        count = rs.getInt(1);
                    }
                } catch (Exception e) {
                    loggerUtils.error(e);
                } finally {
                    closeRs(rs);
                    dataBase.closeConn(conn);
                }
            }
        }
        return count;
    }

    /**
     * 查询行数
     *
     * @param sql      查询行数的SQL语句（必须是select count(*) from ...）
     * @param bindArgs SQL语句所需参数（该参数允许为null）
     * @return 行数
     * @author COCHO
     * @time 2013-5-15上午11:32:30
     */
    public int getCount(String sql, Object... bindArgs) {
        return getCount(sql, toList(bindArgs));
    }

    /**
     * 查询行数
     *
     * @param sql      查询行数的SQL语句（必须是select count(*) from ...）
     * @param bindArgs SQL语句所需参数（该参数允许为null）
     * @return 行数
     */
    public int getCount(String sql, List bindArgs) {
        int count = 0;
        if (stringUtils.isNotBlank(sql)) {
            Connection conn = getConnection();
            if (conn != null) {
                ResultSet rs = null;
                try {
                    rs = sqlExecutor.getResultSet(sql, bindArgs, conn);
                    rs.next();
                    count = rs.getInt(1);
                } catch (Exception e) {
                    loggerUtils.error(e);
                } finally {
                    closeRs(rs);
                    dataBase.closeConn(conn);
                }
            }
        }
        return count;
    }

    /**
     * 执行SQL
     *
     * @param sql sql语句
     * @return true:执行成功 false:执行失败或空的参数
     * @author COCHO
     */
    public boolean execSql(String sql) {
        boolean result = false;
        if (stringUtils.isNotBlank(sql)) {
            List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
            List bindArgs = new ArrayList();
            pairList.add(PairDborm.create(sql, bindArgs));
            result = execSqlByTransaction(pairList);
        }
        return result;
    }


    /**
     * 执行SQL
     *
     * @param sql      SQL语句
     * @param bindArgs SQL语句所需的参数（该参数允许为null）
     * @return true:执行成功 false:执行失败或空的参数
     * @author COCHO
     * @time 2013-5-6下午4:23:11
     */
    public boolean execSql(String sql, Object... bindArgs) {
        return execSql(sql, toList(bindArgs));
    }

    /**
     * 执行SQL
     *
     * @param sql      SQL语句
     * @param bindArgs SQL语句所需的参数（该参数允许为null）
     * @return true:执行成功 false:执行失败或空的参数
     * @author COCHO
     * @time 2013-5-6下午4:23:11
     */
    public boolean execSql(String sql, List bindArgs) {
        boolean result = false;
        if (stringUtils.isNotBlank(sql)) {
            List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
            pairList.add(PairDborm.create(sql, bindArgs));
            result = execSqlByTransaction(pairList);
        }
        return result;
    }

    /**
     * 执行指定的SQL语句
     *
     * @param conn     数据库连接
     * @param sql      sql语句
     * @param bindArgs sql语句所需的参数
     * @return true:执行成功 false:执行失败或空的参数
     */
    public boolean execSql(Connection conn, String sql, Object... bindArgs) {
        return execSql(conn, sql, toList(bindArgs));
    }

    /**
     * 执行指定的SQL语句
     *
     * @param conn     数据库连接
     * @param sql      sql语句
     * @param bindArgs sql语句所需的参数
     * @return true:执行成功 false:执行失败或空的参数
     */
    public boolean execSql(Connection conn, String sql, List bindArgs) {
        boolean result = false;
        if (stringUtils.isNotBlank(sql)) {
            try {
                sqlExecutor.execSQL(sql, bindArgs, conn);
                result = true;
            } catch (Exception e) {
                loggerUtils.error(e);
                result = false;
            }
        }
        return result;
    }

    /**
     * 按事务方式批量执行SQL
     *
     * @param execSqlPairList sql语句集合
     * @return true:执行成功 false:执行失败或空的参数
     * @author COCHO
     * @time 2013-6-7下午3:08:45
     */
    private boolean execSql(Collection<PairDborm<String, List>> execSqlPairList) {
        boolean result = false;
        if (execSqlPairList != null && execSqlPairList.size() > 0) {
            result = execSqlByTransaction(execSqlPairList);
        }
        return result;
    }


    /**
     * 获得数据库连接
     *
     * @return 数据库连接或null
     */
    public Connection getConnection() {
        if (dataBase != null) {
            return dataBase.getConnection();
        } else {
            return null;
        }
    }


    public DbormDataBase getDataBase() {
        return dataBase;
    }

    public void setDataBase(DbormDataBase dataBase) {
        this.dataBase = dataBase;
    }


    private boolean execSqlByTransaction(Collection<PairDborm<String, List>> pairList) {
        boolean result = true;
        if (autoCommit) {
            result = execute(pairList);
        } else {
            transactionSqlCache.addAll(pairList);
        }
        return result;
    }

    private boolean execute(Collection<PairDborm<String, List>> execSqlPairList) {
        boolean result = false;
        if (execSqlPairList != null && execSqlPairList.size() > 0) {
            Connection conn = getConnection();
            if (conn != null) {
                try {
                    sqlExecutor.execSQLUseTransaction(execSqlPairList, conn);
                    result = true;
                } catch (Exception e) {
                    loggerUtils.error(e);
                } finally {
                    dataBase.closeConn(conn);
                }
            }
        }
        return result;
    }

    private String[] getColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int count = resultSetMetaData.getColumnCount();
        String[] columnNames = new String[count];
        for (int i = 0; i < count; i++) {
            columnNames[i] = resultSetMetaData.getColumnLabel(i + 1);//取别名
        }
        return columnNames;
    }


    private void closeRs(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ignored) {
            }
        }
    }

    private <T> Collection<T> toEntityCollection(T entity) {
        Collection<T> entitys = new ArrayList<T>();
        entitys.add(entity);
        return entitys;
    }

    private Collection toCollection(Object... bindArgs) {
        Collection result = new ArrayList();
        if (bindArgs != null) {
            result = Arrays.asList(bindArgs);
        }
        return result;
    }

    private List toList(Object... bindArgs) {
        List result = new ArrayList();
        if (bindArgs != null) {
            result = Arrays.asList(bindArgs);
        }
        return result;
    }

}
