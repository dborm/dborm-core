package org.dborm.core.framework;

import org.dborm.core.domain.ColumnBean;
import org.dborm.core.schema.SchemaConstants;
import org.dborm.core.utils.DbormConstants;
import org.dborm.core.utils.DbormContexts;
import org.dborm.core.utils.ReflectUtilsDborm;
import org.dborm.core.utils.StringUtilsDborm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 实体解析器
 *
 * @author COCHO
 * @time 2013-5-6上午11:28:20
 */
public class EntityResolver {

    StringUtilsDborm stringUtils = new StringUtilsDborm();
    ReflectUtilsDborm reflectUtils = new ReflectUtilsDborm();
    DataTypeConverter dataTypeConverter = new DataTypeConverter();

    /**
     * 获得实体类的全部属性
     *
     * @param entityClass 实体类
     * @return 全部属性集（键：列名，值：属性对象）
     * @author COCHO
     * @time 2013-5-10下午2:01:26
     */
    public Map<String, Field> getEntityAllFields(Class<?> entityClass) {
        List<Field> fields = reflectUtils.getFields(entityClass);
        Map<String, Field> allFields = new HashMap<String, Field>();
        for (Field field : fields) {
            allFields.put(stringUtils.humpToUnderlineName(field.getName()), field);
        }
        return allFields;
    }

    /**
     * 获得实体类的列属性（属性上有column标注的属性）
     *
     * @param entityClass 实体类
     * @return 列属性集（键：列名，值：属性对象）
     * @author COCHO
     * @time 2013-5-8上午11:03:02
     */
    public Map<String, Field> getEntityColumnFields(Class<?> entityClass) {
        Map<String, Field> columnFields = new HashMap<String, Field>();
        Map<String, ColumnBean> columns = CacheDborm.getCache().getTablesCache(entityClass).getColumns();
        Map<String, Field> allFields = CacheDborm.getCache().getEntityAllFieldsCache(entityClass);
        for (Entry<String, Field> fieldInfo : allFields.entrySet()) {
            Field field = fieldInfo.getValue();
            if (columns.containsKey(fieldInfo.getKey())) {// 如果表的列属性信息中包含该属性，则说明该属性属为列属性
                columnFields.put(fieldInfo.getKey(), field);
            }
        }
        return columnFields;
    }

    /**
     * 获得实体类的主键属性（属性上有PrimaryKey标注的属性）
     *
     * @param entityClass 实体类
     * @return 列属性集（键：列名，值：属性对象）
     * @author COCHO
     * @time 2013-5-8上午11:03:02
     */
    public Map<String, Field> getEntityPrimaryKeyFields(Class<?> entityClass) {
        Map<String, Field> primaryKeys = new HashMap<String, Field>();
        Map<String, ColumnBean> columns = CacheDborm.getCache().getTablesCache(entityClass).getColumns();
        for (Entry<String, ColumnBean> entry : columns.entrySet()) {
            ColumnBean column = entry.getValue();
            if (column.isPrimaryKey()) {
                Field field = reflectUtils.getFieldByName(entityClass, column.getFieldName());
                primaryKeys.put(entry.getKey(), field);
            }
        }
        return primaryKeys;
    }

    /**
     * 将结果集转换为实体对象（如果列没有对应的属性则丢弃）
     *
     * @param entityClass 实体类
     * @param rs          结果集
     * @param columnNames 结果集中包含的列名
     * @return 实体对象
     * @throws SQLException
     */
    public Object getEntity(Class<?> entityClass, ResultSet rs, String[] columnNames) throws SQLException {
        Map<String, Field> fields = CacheDborm.getCache().getEntityAllFieldsCache(entityClass);// 获得该类的所有属性，支持联合查询
        Object entity = reflectUtils.createInstance(entityClass);// 创建实体类的实例
        for (String columnName : columnNames) {
            Field field = fields.get(columnName);
            if (field != null) {
                Object value = dataTypeConverter.columnValueToFieldValue(rs, columnName, field);
                reflectUtils.setFieldValue(field, entity, value);
            }
        }
        return entity;
    }

    /**
     * 将结果集转换为实体对象（如果列没有对应的属性则存放在Map集合中）
     *
     * @param entityClass 实体类
     * @param rs          结果集
     * @param columnNames 结果集中包含的列名
     * @return 实体对象
     * @throws SQLException
     */
    public Object getEntityAll(Class<?> entityClass, ResultSet rs, String[] columnNames) throws SQLException {
        Map<String, Field> fields = CacheDborm.getCache().getEntityAllFieldsCache(entityClass);// 获得该类的所有属性，支持联合查询
        Object entity = reflectUtils.createInstance(entityClass);// 创建实体类的实例
        Method putParam = null;
        for (String columnName : columnNames) {
            Field field = fields.get(columnName);
            if (field != null) {
                Object value = dataTypeConverter.columnValueToFieldValue(rs, columnName, field);
                reflectUtils.setFieldValue(field, entity, value);
            } else {//如果找不到该属性,则将值存放到Map集合中
                if (putParam == null) {
                    putParam = reflectUtils.getMethod(entity, DbormConstants.BASE_PUT_METHOD, String.class, Object.class);
                }
                String name = stringUtils.underlineToHumpName(columnName, false);
                Object value = dataTypeConverter.columnValueToFieldValue(rs, columnName, field);
                reflectUtils.setMethodValue(entity, putParam, name, value);
            }
        }
        return entity;
    }


    /**
     * 获得指定实体的列属性值集合
     *
     * @param entity 实体
     * @param <T>    实体类型
     * @return 实体column属性值集合
     * @author COCHO
     * @time 2013-5-3上午11:26:28
     */
    public <T> List getColumnFiledValues(T entity) {
        Class<?> entityClass = entity.getClass();
        List fieldValues = new ArrayList();
        Map<String, Field> columnFields = CacheDborm.getCache().getEntityColumnFieldsCache(entityClass);
        Set<Entry<String, Field>> entrySet = columnFields.entrySet();
        for (Entry<String, Field> entry : entrySet) {
            Field field = entry.getValue();
            Object value = reflectUtils.getFieldValue(field, entity);
            value = dataTypeConverter.fieldValueToColumnValue(value);
            fieldValues.add(value);
        }
        return fieldValues;
    }

    /**
     * 获得指定实体的列属性值集合
     *
     * @param entity 实体
     * @param <T>    实体类型
     * @return 实体column属性值集合
     * @author COCHO
     * @time 2013-5-3上午11:26:28
     */
    public <T> List getColumnFiledValuesUseDefault(T entity) {
        Class<?> entityClass = entity.getClass();
        List fieldValues = new ArrayList();
        Map<String, ColumnBean> columns = CacheDborm.getCache().getTablesCache(entityClass).getColumns();
        Map<String, Field> columnFields = CacheDborm.getCache().getEntityColumnFieldsCache(entityClass);
        Set<Entry<String, Field>> entrySet = columnFields.entrySet();
        for (Entry<String, Field> entry : entrySet) {
            Field field = entry.getValue();
            Object value = reflectUtils.getFieldValue(field, entity);
            if (value == null) {//如果属性的值为空，则查看一下该属性是否设置的有默认值，如果默认值不为空则使用默认值
                Object defaultValue = columns.get(entry.getKey()).getDefaultValue();
                if (defaultValue != null && !defaultValue.toString().equalsIgnoreCase(SchemaConstants.DEFAULT_VALUE_NULL)) {
                    value = defaultValue;
                }
            }
            value = dataTypeConverter.fieldValueToColumnValue(value);
            fieldValues.add(value);
        }
        return fieldValues;
    }

    /**
     * 获得指定实体的主键属性值集合
     *
     * @param entity 实体
     * @param <T>    实体类型
     * @return 实体主键属性值集合
     * @author COCHO
     * @time 2013-5-3上午11:26:28
     */
    public <T> List getPrimaryKeyFiledValues(T entity) {
        List primaryKeyValues = new ArrayList();
        Class<?> entityClass = entity.getClass();
        Map<String, Field> primaryKeyFields = CacheDborm.getCache().getEntityPrimaryKeyFieldsCache(entityClass);
        Set<Entry<String, Field>> entrySet = primaryKeyFields.entrySet();
        for (Entry<String, Field> entry : entrySet) {
            Field field = entry.getValue();
            Object value = reflectUtils.getFieldValue(field, entity);
            if (value != null) {
                value = dataTypeConverter.fieldValueToColumnValue(value);
                primaryKeyValues.add(value);
            } else {
                String warnMessage = "警告: 属性(" + field.getName() + ") 在类(" + entityClass.getName() + ")里面是主键，不能为空!";
                if (DbormContexts.log != null) {
                    DbormContexts.log.debug(warnMessage);
                } else {
                    System.out.println(warnMessage);
                }
            }
        }
        return primaryKeyValues;
    }


}
