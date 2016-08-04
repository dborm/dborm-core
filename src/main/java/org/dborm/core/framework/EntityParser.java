package org.dborm.core.framework;

import org.dborm.core.domain.ColumnBean;
import org.dborm.core.utils.ReflectUtilsDborm;
import org.dborm.core.utils.StringUtilsDborm;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体解析器
 *
 * @author COCHO
 * @time 2013-5-6上午11:28:20
 */
public class EntityParser {

    StringUtilsDborm stringUtils = new StringUtilsDborm();
    ReflectUtilsDborm reflectUtils = new ReflectUtilsDborm();

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
        Map<String, ColumnBean> columns = Cache.getCache().getTablesCache(entityClass).getColumns();
        Map<String, Field> allFields = Cache.getCache().getEntityAllFieldsCache(entityClass);
        for (String columnName : allFields.keySet()) {
            Field field = allFields.get(columnName);
            if (columns.containsKey(columnName)) {// 如果表的列属性信息中包含该属性，则说明该属性属为列属性
                columnFields.put(columnName, field);
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
        Map<String, ColumnBean> columns = Cache.getCache().getTablesCache(entityClass).getColumns();
        for (String columnName : columns.keySet()) {
            ColumnBean column = columns.get(columnName);
            if (column.isPrimaryKey()) {
                Field field = reflectUtils.getFieldByName(entityClass, column.getFieldName());
                primaryKeys.put(columnName, field);
            }
        }
        return primaryKeys;
    }



}
