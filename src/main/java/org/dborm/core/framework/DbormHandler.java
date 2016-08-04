package org.dborm.core.framework;

import org.dborm.core.api.*;
import org.dborm.core.domain.PairDborm;
import org.dborm.core.domain.QueryResult;
import org.dborm.core.utils.*;

import java.lang.reflect.Field;
import java.util.*;


public class DbormHandler implements Dborm {

    SQLPairFactory sqlPairFactory = new SQLPairFactory(this);
    StringUtilsDborm stringUtils = new StringUtilsDborm();

    boolean autoCommit = true;//true:自动提交 false:需调用commit函数才会提交


    private DbormDataBase dataBase;
    private SQLExecutor sqlExecutor;
    private DbormLogger logger;
    EntityFactory entityFactory;


    public DbormHandler(DbormDataBase dataBase) {
        this.dataBase = dataBase;
        this.sqlExecutor = dataBase.getSqlExecutor();
        this.logger = dataBase.getLogger();
        this.entityFactory = new EntityFactory(this);
    }

    @Override
    public void beginTransaction() {
        this.autoCommit = false;
        transactionSqlCache.clear();
    }

    @Override
    public boolean commit() {
        boolean result = execute(transactionSqlCache);
        autoCommit = true;
        transactionSqlCache.clear();
        return result;
    }

    @Override
    public <T> boolean insert(T entity) {
        return insert(toEntityCollection(entity));
    }

    @Override
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

    @Override
    public <T> boolean replace(T entity) {
        return replace(toEntityCollection(entity));
    }

    @Override
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

    @Override
    public <T> boolean update(T entity) {
        return update(toEntityCollection(entity));
    }

    @Override
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

    @Override
    public <T> boolean saveOrUpdate(T entity) {
        return saveOrUpdate(toEntityCollection(entity));
    }

    @Override
    public <T> boolean saveOrUpdate(Collection<T> entitys) {
        boolean result = false;
        if (entitys != null && entitys.size() > 0) {
            Object connection = getConnection();
            if (connection != null) {
                try {
                    List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
                    for (T entity : entitys) {
                        pairList.addAll(sqlPairFactory.saveOrUpdateDeep(entity, connection));
                    }

                    if (autoCommit) {
                        sqlExecutor.execSQLUseTransaction(pairList, connection);
                        result = true;
                    } else {
                        transactionSqlCache.addAll(pairList);
                    }
                } catch (Exception e) {
                    logger.error(e);
                } finally {
                    dataBase.closeConnection(connection);
                }
            }
        }
        return result;
    }

    @Override
    public <T> boolean saveOrReplace(T entity) {
        return saveOrReplace(toEntityCollection(entity));
    }

    @Override
    public <T> boolean saveOrReplace(Collection<T> entitys) {
        boolean result = false;
        if (entitys != null && entitys.size() > 0) {
            Object connection = getConnection();
            if (connection != null) {
                try {
                    List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
                    for (T entity : entitys) {
                        pairList.addAll(sqlPairFactory.saveOrReplaceDeep(entity, connection));
                    }

                    if (autoCommit) {
                        sqlExecutor.execSQLUseTransaction(pairList, connection);
                        result = true;
                    } else {
                        transactionSqlCache.addAll(pairList);
                    }
                } catch (Exception e) {
                    logger.error(e);
                } finally {
                    dataBase.closeConnection(connection);
                }
            }
        }
        return result;
    }

    @Override
    public <T> boolean delete(T entity) {
        return delete(toEntityCollection(entity));
    }

    @Override
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

    @Override
    public <T> T getEntity(Class<?> entityClass, String sql, Object... bindArgs) {
        return getEntity(entityClass, sql, toList(bindArgs));
    }

    @Override
    public <T> T getEntity(Class<?> entityClass, String sql, List bindArgs) {
        T result = null;
        Object connection = getConnection();
        if (connection != null) {
            try {
                result = getEntity(connection, entityClass, sql, bindArgs);
            } catch (Exception e) {
                logger.error(e);
            } finally {
                dataBase.closeConnection(connection);
            }
        }
        return result;
    }

    @Override
    public <T> T getEntity(Object connection, Class<?> entityClass, String sql, Object... bindArgs) {
        return getEntity(connection, entityClass, sql, toList(bindArgs));
    }

    @Override
    public <T> T getEntity(Object connection, Class<?> entityClass, String sql, List bindArgs) {
        if (stringUtils.isNotBlank(sql) && entityClass != null && connection != null) {
            try {
                List<T> entityList = getEntities(entityClass, connection, sql, bindArgs);
                if (entityList != null && entityList.size() > 0) {
                    return entityList.get(0);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return null;
    }

    @Override
    public <T> List<T> getEntities(Class<?> entityClass, String sql, Object... bindArgs) {
        return getEntities(entityClass, sql, toList(bindArgs));
    }

    @Override
    public <T> List<T> getEntities(Class<?> entityClass, String sql, List bindArgs) {
        List<T> results = new ArrayList<T>();
        Object connection = getConnection();
        if (connection != null) {
            try {
                results = getEntities(entityClass, connection, sql, bindArgs);
            } catch (Exception e) {
                logger.error(e);
            } finally {
                dataBase.closeConnection(connection);
            }
        }
        return results;
    }

    @Override
    public <T> List<T> getEntities(Class<?> entityClass, Object connection, String sql, Object... bindArgs) {
        return getEntities(entityClass, connection, sql, toList(bindArgs));
    }

    @Override
    public <T> List<T> getEntities(Class<?> entityClass, Object connection, String sql, List bindArgs) {
        List<T> results = new ArrayList<T>();
        if (stringUtils.isNotBlank(sql) && entityClass != null && connection != null) {
            try {
                Map<String, Field> fields = Cache.getCache().getEntityAllFieldsCache(entityClass);
                List<QueryResult> queryResults = sqlExecutor.query(sql, bindArgs, fields, connection);
                for (QueryResult queryResult : queryResults) {
                    Object entity = entityFactory.getEntityAll(entityClass, queryResult);
                    results.add((T) entity);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return results;
    }

    @Override
    public List<Map<String, Object>> getEntities(Class<?>[] entityClasses, String sql, Object... bindArgs) {
        return getEntities(entityClasses, sql, toList(bindArgs));
    }

    @Override
    public List<Map<String, Object>> getEntities(Class<?>[] entityClasses, String sql, List bindArgs) {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        if (stringUtils.isNotBlank(sql) && entityClasses != null && entityClasses.length > 0) {
            Object connection = getConnection();
            if (connection != null) {
                try {
                    Map<String, Field> allFields = new HashMap<String, Field>();
                    for (Class<?> entityClass : entityClasses) {// 对每一个对象实例化
                        Map<String, Field> fields = Cache.getCache().getEntityAllFieldsCache(entityClass);
                        allFields.putAll(fields);
                    }

                    List<QueryResult> queryResults = sqlExecutor.query(sql, bindArgs, allFields, connection);
                    for (QueryResult queryResult : queryResults) {
                        Map<String, Object> entityTeam = new HashMap<String, Object>();// 实体组
                        for (Class<?> entityClass : entityClasses) {// 对每一个对象实例化
                            Object entity = entityFactory.getEntity(entityClass, queryResult);
                            entityTeam.put(entityClass.getName(), entity);
                        }
                        results.add(entityTeam);
                    }
                } catch (Exception e) {
                    logger.error(e);
                } finally {
                    dataBase.closeConnection(connection);
                }
            }
        }
        return results;
    }

    @Override
    public <T> T getEntityByExample(T example) {
        return getEntityByExample(example, true);
    }

    @Override
    public <T> T getEntityByExample(T example, boolean isAnd) {
        if (example != null) {
            try {
                List<T> entityList = getEntitiesByExample(example, isAnd);
                if (entityList != null && entityList.size() > 0) {
                    return entityList.get(0);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return null;
    }

    @Override
    public <T> List<T> getEntitiesByExample(T example) {
        return getEntitiesByExample(example, true);
    }

    @Override
    public <T> List<T> getEntitiesByExample(T example, boolean isAnd) {
        List<T> results = new ArrayList<T>();
        Object connection = getConnection();
        if (connection != null) {
            try {
                PairDborm<String, List> pair = sqlPairFactory.getEntitiesByExample(example, isAnd);
                results = getEntities(example.getClass(), connection, pair.first, pair.second);
            } catch (Exception e) {
                logger.error(e);
            } finally {
                dataBase.closeConnection(connection);
            }
        }
        return results;
    }

    @Override
    public <T> boolean isExist(T entity) {
        boolean result = false;
        if (entity != null) {
            Object connection = getConnection();
            if (connection != null) {
                try {
                    result = isExist(entity, connection);
                } catch (Exception e) {
                    logger.error(e);
                } finally {
                    dataBase.closeConnection(connection);
                }
            }
        }
        return result;
    }

    @Override
    public <T> boolean isExist(T entity, Object connection) {
        boolean result = false;
        if (entity != null) {
            PairDborm<String, List> pair = sqlPairFactory.getCountByPrimaryKey(entity);
            if (pair != null) {
                try {
                    List<QueryResult> queryResults = sqlExecutor.query(pair.first, pair.second, connection);
                    if ((Long) queryResults.get(0).getObject(0) > 0) {
                        result = true;
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }
        return result;
    }

    @Override
    public long getEntityCount(Class<?> entityClass) {
        long count = 0;
        if (entityClass != null) {
            Object connection = getConnection();
            if (connection != null) {
                PairDborm<String, List> pair = sqlPairFactory.getEntityCount(entityClass);
                try {
                    List<QueryResult> queryResults = sqlExecutor.query(pair.first, pair.second, connection);
                    count = (Long) queryResults.get(0).getObject(0);
                } catch (Exception e) {
                    logger.error(e);
                } finally {
                    dataBase.closeConnection(connection);
                }
            }
        }
        return count;
    }

    @Override
    public long getCount(String sql, Object... bindArgs) {
        return getCount(sql, toList(bindArgs));
    }

    @Override
    public long getCount(String sql, List bindArgs) {
        long count = 0;
        if (stringUtils.isNotBlank(sql)) {
            Object connection = getConnection();
            if (connection != null) {
                try {
                    List<QueryResult> queryResults = sqlExecutor.query(sql, bindArgs, connection);
                    count = (Long) queryResults.get(0).getObject(0);
                } catch (Exception e) {
                    logger.error(e);
                } finally {
                    dataBase.closeConnection(connection);
                }
            }
        }
        return count;
    }

    @Override
    public boolean execSql(String sql, Object... bindArgs) {
        return execSql(sql, toList(bindArgs));
    }

    @Override
    public boolean execSql(String sql, List bindArgs) {
        boolean result = false;
        if (stringUtils.isNotBlank(sql)) {
            List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
            pairList.add(PairDborm.create(sql, bindArgs));
            result = execSqlByTransaction(pairList);
        }
        return result;
    }

    @Override
    public boolean execSql(Object connection, String sql, Object... bindArgs) {
        return execSql(connection, sql, toList(bindArgs));
    }

    @Override
    public boolean execSql(Object connection, String sql, List bindArgs) {
        boolean result = false;
        if (stringUtils.isNotBlank(sql)) {
            try {
                sqlExecutor.execSQL(sql, bindArgs, connection);
                result = true;
            } catch (Exception e) {
                logger.error(e);
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean execSql(Collection<PairDborm<String, List>> execSqlPairList) {
        boolean result = false;
        if (execSqlPairList != null && execSqlPairList.size() > 0) {
            result = execSqlByTransaction(execSqlPairList);
        }
        return result;
    }

    @Override
    public Object getConnection() {
        if (dataBase != null) {
            return dataBase.getConnection();
        } else {
            return null;
        }
    }

    @Override
    public DbormDataBase getDataBase() {
        return dataBase;
    }

    @Override
    public void setDataBase(DbormDataBase dataBase) {
        this.dataBase = dataBase;
    }


    private boolean execSqlByTransaction(Collection<PairDborm<String, List>> pairList) {
        boolean result = false;
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
            Object connection = getConnection();
            if (connection != null) {
                try {
                    sqlExecutor.execSQLUseTransaction(execSqlPairList, connection);
                    result = true;
                } catch (Exception e) {
                    logger.error(e);
                } finally {
                    dataBase.closeConnection(connection);
                }
            }
        }
        return result;
    }


    private <T> Collection<T> toEntityCollection(T entity) {
        Collection<T> entitys = new ArrayList<T>();
        entitys.add(entity);
        return entitys;
    }

    private List toList(Object... bindArgs) {
        List result = new ArrayList();
        if (bindArgs != null) {
            result = Arrays.asList(bindArgs);
        }
        return result;
    }

}
