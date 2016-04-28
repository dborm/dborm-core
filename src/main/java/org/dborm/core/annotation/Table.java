package org.dborm.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表的注解
 *
 * @author COCHO
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface Table {

    /**
     * 该类对应的表的名称
     * 如果不填写，则将驼峰格式的类名转换为下划线格式的名称作为表名（如类名为UserInfo对应的表名为user_info）
     * 如果填写则以填写的为准（如类名为为User表名为user_info,则将该值设置为user_info即可）
     *
     * @author COCHO
     * @time 2013-5-2下午4:08:21
     */
    public String tableName() default "";

}
