package org.dborm.core.framework;

import org.dborm.core.api.Dborm;
import org.dborm.core.domain.PairDborm;
import org.dborm.core.domain.TableBean;
import org.dborm.core.utils.ReflectUtilsDborm;
import org.dborm.core.utils.StringUtilsDborm;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 将实体对象解析成SQL语句及对应的参数对
 *
 * @author COCHO
 * @time 2013-6-5下午1:31:57
 */
public class SQLPairFactory {

    private Dborm dborm;

    public SQLPairFactory(DbormHandler dborm) {
        this.dborm = dborm;
    }

    private StringUtilsDborm stringUtils = new StringUtilsDborm();
    private SQLTranslater sqlTranslater = new SQLTranslater();
    private EntityResolver entityResolver = new EntityResolver();
    ReflectUtilsDborm reflectUtils = new ReflectUtilsDborm();

    public <T> PairDborm<String, List> insert(T entity) {
        entity = dborm.getDataBase().beforeInsert(entity);
        Class<?> entityClass = entity.getClass();
        String sql = Cache.getCache().getSqlCache(entityClass.getName() + ".INSERT");
        if (stringUtils.isEmpty(sql)) {// 如果缓存中取不到已解析的SQL
            sql = sqlTranslater.getInsertSql(entityClass);
            Cache.getCache().putSqlCache(entityClass.getName() + ".INSERT", sql);
        }
        List bindArgs = entityResolver.getColumnFiledValuesUseDefault(entity);
        return PairDborm.create(sql, bindArgs);
    }

    public <T> List<PairDborm<String, List>> insertDeep(T entity) {
        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        pairList.add(insert(entity));
        pairList.addAll(getRelationPair(entity, PairType.INSERT, null));
        return pairList;
    }

    public <T> PairDborm<String, List> replace(T entity) {
        entity = dborm.getDataBase().beforeReplace(entity);
        Class<?> entityClass = entity.getClass();
        String sql = Cache.getCache().getSqlCache(entityClass.getName() + ".REPLACE");
        if (stringUtils.isEmpty(sql)) {// 如果缓存中取不到已解析的SQL
            sql = sqlTranslater.getReplaceSql(entityClass);
            Cache.getCache().putSqlCache(entityClass.getName() + ".REPLACE", sql);
        }
        List bindArgs = entityResolver.getColumnFiledValues(entity);
        bindArgs.addAll(entityResolver.getPrimaryKeyFiledValues(entity));
        return PairDborm.create(sql, bindArgs);
    }

    public <T> List<PairDborm<String, List>> replaceDeep(T entity) {
        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        pairList.add(replace(entity));
        pairList.addAll(getRelationPair(entity, PairType.REPLACE, null));
        return pairList;
    }

    public <T> PairDborm<String, List> delete(T entity) {
        entity = dborm.getDataBase().beforeDelete(entity);
        Class<?> entityClass = entity.getClass();
        String sql = Cache.getCache().getSqlCache(entityClass.getName() + ".DELETE");
        if (stringUtils.isEmpty(sql)) {// 如果缓存中取不到已解析的SQL
            sql = sqlTranslater.getDeleteSql(entityClass);
            Cache.getCache().putSqlCache(entityClass.getName() + ".DELETE", sql);
        }
        List bindArgs = entityResolver.getPrimaryKeyFiledValues(entity);
        return PairDborm.create(sql, bindArgs);
    }

    public <T> List<PairDborm<String, List>> deleteDeep(T entity) {
        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        pairList.add(delete(entity));
        pairList.addAll(getRelationPair(entity, PairType.DELETE, null));
        return pairList;
    }

    public <T> PairDborm<String, List> update(T entity) {
        entity = dborm.getDataBase().beforeUpdate(entity);
        Class<?> entityClass = entity.getClass();
        StringBuilder sqlContent = new StringBuilder("UPDATE ");
        String tableName = Cache.getCache().getTablesCache(entityClass).getTableName();
        sqlContent.append(tableName);
        sqlContent.append(" SET ");
        StringBuilder columnName = new StringBuilder();
        List bindArgs = new ArrayList();

        Map<String, Field> columnFields = Cache.getCache().getEntityColumnFieldsCache(entityClass);
        for (String name : columnFields.keySet()) {
            Field field = columnFields.get(name);
            Object value = reflectUtils.getFieldValue(field, entity);
            if (value != null) {
                columnName.append(name);
                columnName.append("=?, ");
                bindArgs.add(value);
            }
        }
        sqlContent.append(stringUtils.cutLastSign(columnName.toString(), ", "));
        sqlContent.append(" WHERE ");
        sqlContent.append(sqlTranslater.parsePrimaryKeyWhere(entityClass));
        bindArgs.addAll(entityResolver.getPrimaryKeyFiledValues(entity));
        return PairDborm.create(sqlContent.toString(), bindArgs);
    }

    public <T> List<PairDborm<String, List>> updateDeep(T entity) {
        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        pairList.add(update(entity));
        pairList.addAll(getRelationPair(entity, PairType.UPDATE, null));
        return pairList;
    }

    public <T> List<PairDborm<String, List>> saveOrReplaceDeep(T entity, Object connection) {
        entity = dborm.getDataBase().beforeSaveOrReplace(entity);

        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        if (dborm.isExist(entity, connection)) {
            pairList.add(replace(entity));
        } else {
            pairList.add(insert(entity));
        }
        pairList.addAll(getRelationPair(entity, PairType.SAVE_OR_REPLACE, connection));
        return pairList;
    }

    public <T> List<PairDborm<String, List>> saveOrUpdateDeep(T entity, Object connection) {
        entity = dborm.getDataBase().beforeSaveOrUpdate(entity);

        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        if (dborm.isExist(entity, connection)) {
            pairList.add(update(entity));
        } else {
            pairList.add(insert(entity));
        }
        pairList.addAll(getRelationPair(entity, PairType.SAVE_OR_UPDATE, connection));
        return pairList;
    }

    public PairDborm<String, List> getEntityCount(Class<?> entityClass) {
        // 例如： SELECT COUNT(*) FROM
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
        String tableName = Cache.getCache().getTablesCache(entityClass).getTableName();
        sql.append(tableName);
        return PairDborm.create(sql.toString(), null);
    }

    public <T> PairDborm<String, List> getCountByPrimaryKey(T entity) {
        List primaryKeyValue = entityResolver.getPrimaryKeyFiledValues(entity);
        if (primaryKeyValue.size() > 0) {
            // 例如： SELECT COUNT(*) FROM users WHERE user_id=?
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
            Class<?> entityClass = entity.getClass();
            String tableName = Cache.getCache().getTablesCache(entityClass).getTableName();
            sql.append(tableName);
            sql.append(" WHERE ");
            sql.append(sqlTranslater.parsePrimaryKeyWhere(entityClass));
            return PairDborm.create(sql.toString(), primaryKeyValue);
        } else {//如果主键没有值则无法根据主键查询
            return null;
        }
    }

    public <T> PairDborm<String, List> getEntitiesByExample(T entity, boolean isAnd) {
        Class<?> entityClass = entity.getClass();
        StringBuilder sqlContent = new StringBuilder("SELECT * FROM ");
        String tableName = Cache.getCache().getTablesCache(entityClass).getTableName();
        sqlContent.append(tableName);
        sqlContent.append(" WHERE 1=1 ");
        StringBuilder columnNames = new StringBuilder();
        List bindArgs = new ArrayList();

        Map<String, Field> columnFields = Cache.getCache().getEntityColumnFieldsCache(entityClass);
        for (String name : columnFields.keySet()) {
            Field field = columnFields.get(name);
            Object value = reflectUtils.getFieldValue(field, entity);
            if (value != null) {
                if (isAnd) {
                    columnNames.append(" AND ");
                } else {
                    columnNames.append(" OR ");
                }
                columnNames.append(name);
                columnNames.append("=? ");
                bindArgs.add(value);
            }
        }
        sqlContent.append(stringUtils.cutLastSign(columnNames.toString(), ", "));
        return PairDborm.create(sqlContent.toString(), bindArgs);
    }


    /**
     * 当前的级联操作类型
     */
    private enum PairType {
        INSERT, REPLACE, DELETE, UPDATE, SAVE_OR_REPLACE, SAVE_OR_UPDATE
    }

    /**
     * 获取级联对象SAVE相关的SQL语句对
     *
     * @param entity 对象
     * @param type   操作类型
     * @param connection   数据库连接
     * @return SQL操作集合
     * @author COCHO
     * @time 2013-6-5下午1:55:14
     */
    private <T> List<PairDborm<String, List>> getRelationPair(T entity, PairType type, Object connection) {
        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        Class<?> entityClass = entity.getClass();
        TableBean table = Cache.getCache().getTablesCache(entityClass);
        Set<String> relations = table.getRelation();
        if (relations.size() > 0) {
            for (String fieldName : relations) {
                Field relationField = reflectUtils.getFieldByName(entityClass, fieldName);
                Object relationObj = reflectUtils.getFieldValue(relationField, entity);
                if (relationObj != null) {
                    if (relationObj instanceof Collection) {
                        List relationObjList = (List) reflectUtils.getFieldValue(relationField, entity);
                        for (Object obj : relationObjList) {
                            relation(pairList, obj, type, connection);
                        }
                    } else {
                        relation(pairList, relationObj, type, connection);
                    }
                }
            }
        }
        return pairList;
    }


    private void relation(List<PairDborm<String, List>> pairList, Object relationObj, PairType type, Object connection) {
        switch (type) {
            case INSERT:
                pairList.addAll(insertDeep(relationObj));
                break;
            case REPLACE:
                pairList.addAll(replaceDeep(relationObj));
                break;
            case DELETE:
                pairList.addAll(deleteDeep(relationObj));
                break;
            case UPDATE:
                pairList.addAll(updateDeep(relationObj));
                break;
            case SAVE_OR_REPLACE:
                pairList.addAll(saveOrReplaceDeep(relationObj, connection));
                break;
            case SAVE_OR_UPDATE:
                pairList.addAll(saveOrUpdateDeep(relationObj, connection));
                break;
            default:
                break;
        }
    }


}
