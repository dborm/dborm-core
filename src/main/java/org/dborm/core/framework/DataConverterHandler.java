package org.dborm.core.framework;

import org.dborm.core.api.DataConverter;

import java.lang.reflect.Field;
import java.sql.SQLException;

/**
 * 数据库的数据与Java数据的转换器
 *
 * @author dborm@cocho
 * @time 2013-5-6下午12:01:06
 */
public class DataConverterHandler implements DataConverter{

    /**
     * 将Java属性类型的值转换为数据列对应的值
     *
     * @param fieldValue Java类型的参数值
     * @return 数据列的值
     * @author dborm@cocho
     * @time 2013-5-5上午2:36:18
     */
    public Object fieldValueToColumnValue(Object fieldValue) {
        return fieldValue;
    }

    /**
     * 将数据列对应的值转换为Java属性类型的值
     * <p>
     *
     * @param columnValue 列的值
     * @param field       该类对应的属性对象
     * @return 该属性类型的值
     * @throws SQLException
     * @author dborm@cocho
     * @time 2013-5-5上午2:44:02
     */
    public Object columnValueToFieldValue(Object columnValue, Field field){
        return columnValue;
    }


}
