package org.dborm.core.api;

/**
 * 数据库连接相关的信息管理接口
 *
 * @author COCHO
 * @time 2014年1月15日 @下午5:08:18
 */
public abstract class DbormDataBase {

    /**
     * 获得数据库连接
     *
     * @return 数据库连接
     * @author COCHO
     * @time 2013-5-6上午10:46:44
     */
    public abstract Object getConnection();


    /**
     * 关闭数据库链接
     *
     * @param connection 数据库链接
     */
    public abstract void closeConnection(Object connection);



    /**
     * 新曾对象操作之前
     *
     * @param entity 实体对象
     * @param <T>    对象类型
     * @return 处理之后的对象
     */
    public <T> T beforeInsert(T entity) {
        return entity;
    }

    /**
     * 替换对象操作之前
     *
     * @param entity 实体对象
     * @param <T>    对象类型
     * @return 处理之后的对象
     */
    public <T> T beforeReplace(T entity) {
        return entity;
    }

    /**
     * 删除对象操作之前
     *
     * @param entity 实体对象
     * @param <T>    对象类型
     * @return 处理之后的对象
     */
    public <T> T beforeDelete(T entity) {
        return entity;
    }

    /**
     * 修改对象操作之前
     *
     * @param entity 实体对象
     * @param <T>    对象类型
     * @return 处理之后的对象
     */
    public <T> T beforeUpdate(T entity) {
        return entity;
    }

    /**
     * 新增或替换对象操作之前
     *
     * @param entity 实体对象
     * @param <T>    对象类型
     * @return 处理之后的对象
     */
    public <T> T beforeSaveOrReplace(T entity) {
        return entity;
    }

    /**
     * 新增或修改对象操作之前
     *
     * @param entity 实体对象
     * @param <T>    对象类型
     * @return 处理之后的对象
     */
    public <T> T beforeSaveOrUpdate(T entity) {
        return entity;
    }




}
