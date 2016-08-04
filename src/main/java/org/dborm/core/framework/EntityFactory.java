package org.dborm.core.framework;

import org.dborm.core.api.Dborm;
import org.dborm.core.domain.ColumnBean;
import org.dborm.core.domain.QueryResult;
import org.dborm.core.schema.SchemaConstants;
import org.dborm.core.utils.DbormConstants;
import org.dborm.core.utils.ReflectUtilsDborm;
import org.dborm.core.utils.StringUtilsDborm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实体生成器
 *
 * @author COCHO
 * @time 2013-5-6上午11:28:20
 */
public class EntityFactory {

    StringUtilsDborm stringUtils = new StringUtilsDborm();
    ReflectUtilsDborm reflectUtils = new ReflectUtilsDborm();
    Dborm dborm;

    public EntityFactory(Dborm dborm) {
        this.dborm = dborm;
    }

    /**
     * 将结果集转换为实体对象（如果列没有对应的属性则丢弃）
     *
     * @param entityClass 实体类
     * @param queryResult 结果集
     * @return 实体对象
     */
    public Object getEntity(Class<?> entityClass, QueryResult queryResult) throws Exception {
        Map<String, Field> fields = Cache.getCache().getEntityAllFieldsCache(entityClass);// 获得该类的所有属性，支持联合查询
        Object entity = reflectUtils.createInstance(entityClass);// 创建实体类的实例
        for (String columnName : queryResult.getResultMap().keySet()) {
            Field field = fields.get(columnName);
            if (field != null) {
                Object value = queryResult.getObject(columnName);
                value = dborm.getDataBase().getDataConverter().columnValueToFieldValue(value, field);
                reflectUtils.setFieldValue(field, entity, value);
            }
        }
        return entity;
    }

    /**
     * 将结果集转换为实体对象（如果列没有对应的属性则存放在Map集合中）
     *
     * @param entityClass 实体类型
     * @param queryResult 结果集
     * @return 实体类对应的对象
     */
    public Object getEntityAll(Class<?> entityClass, QueryResult queryResult) throws Exception {
        Map<String, Field> fields = Cache.getCache().getEntityAllFieldsCache(entityClass);// 获得该类的所有属性，支持联合查询
        Object entity = reflectUtils.createInstance(entityClass);// 创建实体类的实例
        Method putParam = null;
        for (String columnName : queryResult.getResultMap().keySet()) {
            Field field = fields.get(columnName);
            Object value = queryResult.getObject(columnName);
            value = dborm.getDataBase().getDataConverter().columnValueToFieldValue(value, field);
            if (field != null) {
                reflectUtils.setFieldValue(field, entity, value);
            } else {//如果找不到该属性,则将值存放到Map集合中
                if (putParam == null) {
                    putParam = reflectUtils.getMethod(entity, DbormConstants.BASE_PUT_METHOD, String.class, Object.class);
                }
                String propertyName = stringUtils.underlineToHumpName(columnName, false);
                reflectUtils.setMethodValue(entity, putParam, propertyName, value);
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
    public <T> List<Object> getColumnFiledValues(T entity) {
        Class<?> entityClass = entity.getClass();
        List<Object> fieldValues = new ArrayList<Object>();
        Map<String, Field> columnFields = Cache.getCache().getEntityColumnFieldsCache(entityClass);
        for (Field field : columnFields.values()) {
            Object value = reflectUtils.getFieldValue(field, entity);
            value = dborm.getDataBase().getDataConverter().fieldValueToColumnValue(value);
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
    public <T> List<Object> getColumnFiledValuesUseDefault(T entity) {
        Class<?> entityClass = entity.getClass();
        List<Object> fieldValues = new ArrayList<Object>();
        Map<String, ColumnBean> columns = Cache.getCache().getTablesCache(entityClass).getColumns();
        Map<String, Field> columnFields = Cache.getCache().getEntityColumnFieldsCache(entityClass);
        for (String columnName : columnFields.keySet()) {
            Field field = columnFields.get(columnName);
            Object value = reflectUtils.getFieldValue(field, entity);
            if (value == null) {//如果属性的值为空，则查看一下该属性是否设置的有默认值，如果默认值不为空则使用默认值
                Object defaultValue = columns.get(columnName).getDefaultValue();
                if (defaultValue != null && !defaultValue.toString().equalsIgnoreCase(SchemaConstants.DEFAULT_VALUE_NULL)) {
                    value = defaultValue;
                }
            }
            value = dborm.getDataBase().getDataConverter().fieldValueToColumnValue(value);
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
    public <T> List<Object> getPrimaryKeyFiledValues(T entity) {
        List<Object> primaryKeyValues = new ArrayList<Object>();
        Class<?> entityClass = entity.getClass();
        Map<String, Field> primaryKeyFields = Cache.getCache().getEntityPrimaryKeyFieldsCache(entityClass);
        for (Field field : primaryKeyFields.values()) {
            Object value = reflectUtils.getFieldValue(field, entity);
            if (value != null) {
                value = dborm.getDataBase().getDataConverter().fieldValueToColumnValue(value);
                primaryKeyValues.add(value);
            } else {
                String warnMessage = "警告: 属性(" + field.getName() + ") 在类(" + entityClass.getName() + ")里面是主键，不能为空!";
                System.out.println(warnMessage);//此处不需要抛出异常,否则自动填充主键值的功能将会失效
            }
        }
        return primaryKeyValues;
    }


}
