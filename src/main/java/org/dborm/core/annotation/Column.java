package org.dborm.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 列的注解
 *
 * @author COCHO
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Column {

    /**
     * 该属性对应的表中的列名
     * 如果不填写，则将驼峰格式的属性名转换为下划线格式的名称作为列名（如属性名为createTime对应的列名为create_time）
     * 如果填写则以填写的为准（如属性名为为createUserId对应的列名为create_by,则将该值设置为create_by即可）
     *
     * @author COCHO
     * @time 2013-5-2下午4:08:21
     */
    String value() default "";

    /**
     * 是否作为主键
     */
    boolean isPrimaryKey() default false;

    /**
     * 默认值(字符串"null"或者null相当于没有默认值)
     *
     * @author COCHO
     */
    String defaultValue() default "null";

}
