package org.dborm.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 级联操作标识，用于深度操作
 * 级联操作仅仅针对insert,update,replace,delete,saveOrUpdate,saveOrReplace有效
 * 对于查询等操作无效
 *
 * @author COCHO
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD})
public @interface Relation {


}
