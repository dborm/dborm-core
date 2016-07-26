package org.dborm.core.framework;

import org.dborm.core.utils.StringUtilsDborm;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * SQL转换器（根据实体对象转换出相应的SQL）
 *
 * @author COCHO
 * @time 2013-5-3下午2:07:40
 */
public class SQLTranslater {

    StringUtilsDborm stringUtils = new StringUtilsDborm();

    /**
     * 解析出实体类的新增SQL语句
     *
     * @param entityClass 实体类
     * @return 新增SQL语句
     */
    public String getInsertSql(Class<?> entityClass) {
        // 例如： INSERT INTO users(user_Id, username) VALUES (?,?) ;
        String sql;
        StringBuilder sqlContent = new StringBuilder("INSERT INTO ");
        String tableName = CacheDborm.getCache().getTablesCache(entityClass).getTableName();
        sqlContent.append(tableName);
        sqlContent.append(" (");
        StringBuilder columnNames = new StringBuilder();
        StringBuilder columnValue = new StringBuilder();

        Map<String, Field> fields = CacheDborm.getCache().getEntityColumnFieldsCache(entityClass);
        for(String name: fields.keySet()){
            columnNames.append(name);
            columnNames.append(", ");
            columnValue.append("?, ");
        }
        sqlContent.append(stringUtils.cutLastSign(columnNames.toString(), ", "));
        sqlContent.append(") VALUES (");
        sqlContent.append(stringUtils.cutLastSign(columnValue.toString(), ", "));
        sqlContent.append(")");
        sql = sqlContent.toString();

        return sql;
    }

    /**
     * 解析出实体类的删除SQL语句
     *
     * @param entityClass 实体类
     * @return 删除SQL语句
     */
    public String getDeleteSql(Class<?> entityClass) {
        // 例如： DELETE FROM users WHERE user_id=?;
        String sql;
        StringBuilder sqlContent = new StringBuilder("DELETE FROM ");
        String tableName = CacheDborm.getCache().getTablesCache(entityClass).getTableName();
        sqlContent.append(tableName);
        sqlContent.append(" WHERE ");
        sqlContent.append(parsePrimaryKeyWhere(entityClass));
        sql = sqlContent.toString();
        return sql;
    }

    /**
     * 解析出实体类的替换SQL语句
     *
     * @param entityClass 实体类
     * @return 替换SQL语句
     */
    public String getReplaceSql(Class<?> entityClass) {
        // 例如： UPDATE users SET user_id=?, user_name=?, user_age=? WHERE user_id=?;
        String sql;
        StringBuilder sqlContent;
        sqlContent = new StringBuilder("UPDATE ");
        String tableName = CacheDborm.getCache().getTablesCache(entityClass).getTableName();
        sqlContent.append(tableName);
        sqlContent.append(" SET ");
        StringBuilder columnNames = new StringBuilder();

        Map<String, Field> columnFields = CacheDborm.getCache().getEntityColumnFieldsCache(entityClass);
        for(String name : columnFields.keySet()){
            columnNames.append(name);
            columnNames.append("=?, ");
        }
        sqlContent.append(stringUtils.cutLastSign(columnNames.toString(), ", "));
        sqlContent.append(" WHERE ");
        sqlContent.append(parsePrimaryKeyWhere(entityClass));
        sql = sqlContent.toString();
        return sql;
    }

    /**
     * 解析出where条件后面的主键SQL语句
     *
     * @param entityClass 实体类
     * @return where条件后面的主键SQL语句
     */
    public String parsePrimaryKeyWhere(Class<?> entityClass) {
        StringBuilder sqlContent = new StringBuilder();
        Map<String, Field> fields = CacheDborm.getCache().getEntityPrimaryKeyFieldsCache(entityClass);
        for(String name : fields.keySet()){
            sqlContent.append(name);
            sqlContent.append("=? and ");
        }
        return stringUtils.cutLastSign(sqlContent.toString(), "and ");
    }

}
