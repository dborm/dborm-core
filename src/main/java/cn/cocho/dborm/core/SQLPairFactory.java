package cn.cocho.dborm.core;

import cn.cocho.dborm.domain.TableBean;
import cn.cocho.dborm.utils.PairDborm;
import cn.cocho.dborm.utils.ReflectUtilsDborm;
import cn.cocho.dborm.utils.StringUtilsDborm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 将实体对象解析成SQL语句及对应的参数对
 *
 * @author COCHO
 * @time 2013-6-5下午1:31:57
 */
public class SQLPairFactory {

    Dborm dborm;

    public SQLPairFactory(Dborm dborm) {
        this.dborm = dborm;
    }

    StringUtilsDborm stringUtils = new StringUtilsDborm();
    SQLTranslater sqlTranslater = new SQLTranslater();
    EntityResolver entityResolver = new EntityResolver();
    DataTypeConverter dataTypeConverter = new DataTypeConverter();
    ReflectUtilsDborm reflectUtils = new ReflectUtilsDborm();
//    dborm.getDataBase() dborm.getDataBase() = new dborm.getDataBase()();


    public <T> PairDborm<String, List> insert(T entity) {
        entity = dborm.getDataBase().beforeInsert(entity);
        Class<?> entityClass = entity.getClass();
        String sql = CacheDborm.getCache().getSqlCache(entityClass.getName() + ".INSERT");
        if (stringUtils.isEmpty(sql)) {// 如果缓存中取不到已解析的SQL
            sql = sqlTranslater.getInsertSql(entityClass);
            CacheDborm.getCache().getCache().putSqlCache(entityClass.getName() + ".INSERT", sql);
        }
        List bindArgs = entityResolver.getColumnFiledValuesUseDefault(entity);
        return PairDborm.create(sql, bindArgs);
    }

    public <T> List<PairDborm<String, List>> insertDeep(T entity) {
        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        pairList.add(insert(entity));
        pairList.addAll(getRelationPair(entity, PairType.INSERT));
        return pairList;
    }

    public <T> PairDborm<String, List> replace(T entity) {
        entity = dborm.getDataBase().beforeReplace(entity);
        Class<?> entityClass = entity.getClass();
        String sql = CacheDborm.getCache().getCache().getSqlCache(entityClass.getName() + ".REPLACE");
        if (stringUtils.isEmpty(sql)) {// 如果缓存中取不到已解析的SQL
            sql = sqlTranslater.getReplaceSql(entityClass);
            CacheDborm.getCache().getCache().putSqlCache(entityClass.getName() + ".REPLACE", sql);
        }
        List bindArgs = entityResolver.getColumnFiledValues(entity);
        bindArgs.addAll(entityResolver.getPrimaryKeyFiledValues(entity));
        return PairDborm.create(sql, bindArgs);
    }

    public <T> List<PairDborm<String, List>> replaceDeep(T entity) {
        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        pairList.add(replace(entity));
        pairList.addAll(getRelationPair(entity, PairType.REPLACE));
        return pairList;
    }

    public <T> PairDborm<String, List> delete(T entity) {
        entity = dborm.getDataBase().beforeDelete(entity);
        Class<?> entityClass = entity.getClass();
        String sql = CacheDborm.getCache().getCache().getSqlCache(entityClass.getName() + ".DELETE");
        if (stringUtils.isEmpty(sql)) {// 如果缓存中取不到已解析的SQL
            sql = sqlTranslater.getDeleteSql(entityClass);
            CacheDborm.getCache().getCache().putSqlCache(entityClass.getName() + ".DELETE", sql);
        }
        List bindArgs = entityResolver.getPrimaryKeyFiledValues(entity);
        return PairDborm.create(sql, bindArgs);
    }

    public <T> List<PairDborm<String, List>> deleteDeep(T entity) {
        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        pairList.add(delete(entity));
        pairList.addAll(getRelationPair(entity, PairType.DELETE));
        return pairList;
    }

    public <T> PairDborm<String, List> update(T entity) {
        entity = dborm.getDataBase().beforeUpdate(entity);
        Class<?> entityClass = entity.getClass();
        StringBuilder sqlContent = new StringBuilder("UPDATE ");
        String tableName = CacheDborm.getCache().getCache().getTablesCache(entityClass).getTableName();
        sqlContent.append(tableName);
        sqlContent.append(" SET ");
        StringBuilder columnName = new StringBuilder();
        List bindArgs = new ArrayList();

        Map<String, Field> columnFields = CacheDborm.getCache().getCache().getEntityColumnFieldsCache(entityClass);
        Set<Entry<String, Field>> entrySet = columnFields.entrySet();
        if (entrySet.size() > 0) {
            for (Entry<String, Field> entry : entrySet) {
                Field field = entry.getValue();
                Object value = reflectUtils.getFieldValue(field, entity);
                if (value != null) {
                    columnName.append(entry.getKey());
                    columnName.append("=?, ");
                    value = dataTypeConverter.fieldValueToColumnValue(value);
                    bindArgs.add(value);
                }
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
        pairList.addAll(getRelationPair(entity, PairType.UPDATE));
        return pairList;
    }

    public <T> List<PairDborm<String, List>> saveOrReplaceDeep(T entity, Connection conn) {
        entity = dborm.getDataBase().beforeSaveOrReplace(entity);

        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        if (dborm.isExist(entity, conn)) {
            pairList.add(replace(entity));
        } else {
            pairList.add(insert(entity));
        }
        pairList.addAll(getRelationSavePair(entity, PairType.SAVEORREPLACE, conn));
        return pairList;
    }

    public <T> List<PairDborm<String, List>> saveOrUpdateDeep(T entity, Connection conn) {
        entity = dborm.getDataBase().beforeSaveOrUpdate(entity);

        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        if (dborm.isExist(entity, conn)) {
            pairList.add(update(entity));
        } else {
            pairList.add(insert(entity));
        }
        pairList.addAll(getRelationSavePair(entity, PairType.SAVEORUPDATE, conn));
        return pairList;
    }

    public PairDborm<String, List> getEntityCount(Class<?> entityClass) {
        // 例如： SELECT COUNT(*) FROM
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
        String tableName = CacheDborm.getCache().getCache().getTablesCache(entityClass).getTableName();
        sql.append(tableName);
        return PairDborm.create(sql.toString(), null);
    }

    public <T> PairDborm<String, List> getCountByPrimaryKey(T entity) {
        List primaryKeyValue = entityResolver.getPrimaryKeyFiledValues(entity);
        if (primaryKeyValue.size() > 0) {
            // 例如： SELECT COUNT(*) FROM users WHERE user_id=?
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
            Class<?> entityClass = entity.getClass();
            String tableName = CacheDborm.getCache().getCache().getTablesCache(entityClass).getTableName();
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
        String tableName = CacheDborm.getCache().getCache().getTablesCache(entityClass).getTableName();
        sqlContent.append(tableName);
        sqlContent.append(" WHERE 1=1 ");
        StringBuilder columnName = new StringBuilder();
        List bindArgs = new ArrayList();

        Map<String, Field> columnFields = CacheDborm.getCache().getCache().getEntityColumnFieldsCache(entityClass);
        Set<Entry<String, Field>> entrySet = columnFields.entrySet();
        if (entrySet.size() > 0) {
            for (Entry<String, Field> entry : entrySet) {
                Field field = entry.getValue();
                Object value = reflectUtils.getFieldValue(field, entity);
                if (value != null) {
                    if (isAnd) {
                        columnName.append(" AND ");
                    } else {
                        columnName.append(" OR ");
                    }
                    columnName.append(entry.getKey());
                    columnName.append("=? ");
                    value = dataTypeConverter.fieldValueToColumnValue(value);
                    bindArgs.add(value);
                }
            }
        }
        sqlContent.append(stringUtils.cutLastSign(columnName.toString(), ", "));
        return PairDborm.create(sqlContent.toString(), bindArgs);
    }


    /**
     * 当前的级联操作类型
     */
    private enum PairType {
        INSERT, REPLACE, DELETE, UPDATE, SAVEORREPLACE, SAVEORUPDATE
    }

    /**
     * 获取级联对象的SQL语句对
     *
     * @param entity 对象
     * @param type   操作类型
     * @return SQL操作集合
     * @author COCHO
     * @time 2013-6-5下午1:55:14
     */
    private <T> List<PairDborm<String, List>> getRelationPair(T entity, PairType type) {
        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        Class<?> entityClass = entity.getClass();
        TableBean table = CacheDborm.getCache().getCache().getTablesCache(entityClass);
        Set<String> relations = table.getRelation();
        if (relations.size() > 0) {
            for (String fieldName : relations) {
                Field relationField = reflectUtils.getFieldByName(entityClass, fieldName);
                List<?> relationObjList = (List<?>) reflectUtils.getFieldValue(relationField, entity);
                if (relationObjList == null) {
                    continue;
                }
                for (Object relationObj : relationObjList) {
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
                        default:
                            break;
                    }
                }
            }
        }
        return pairList;
    }

    /**
     * 获取级联对象SAVE相关的SQL语句对
     *
     * @param entity 对象
     * @param type   操作类型
     * @param conn   数据库连接
     * @return SQL操作集合
     * @author COCHO
     * @time 2013-6-5下午1:55:14
     */
    private <T> List<PairDborm<String, List>> getRelationSavePair(T entity, PairType type, Connection conn) {
        List<PairDborm<String, List>> pairList = new ArrayList<PairDborm<String, List>>();
        Class<?> entityClass = entity.getClass();
        TableBean table = CacheDborm.getCache().getCache().getTablesCache(entityClass);
        Set<String> relations = table.getRelation();
        if (relations.size() > 0) {
            for (String fieldName : relations) {
                Field relationField = reflectUtils.getFieldByName(entityClass, fieldName);
                List<?> relationObjList = (List<?>) reflectUtils.getFieldValue(relationField, entity);
                if (relationObjList == null) {
                    continue;
                }
                for (Object relationObj : relationObjList) {
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
                        case SAVEORREPLACE:
                            pairList.addAll(saveOrReplaceDeep(relationObj, conn));
                            break;
                        case SAVEORUPDATE:
                            pairList.addAll(saveOrUpdateDeep(relationObj, conn));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return pairList;
    }


}