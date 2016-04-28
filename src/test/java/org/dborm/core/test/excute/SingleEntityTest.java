package org.dborm.core.test.excute;

import org.dborm.core.test.utils.BaseTest;
import org.dborm.core.test.utils.db.DbormHandler;
import org.dborm.core.test.utils.domain.UserInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 单个实体对象的增删改操作
 */
public class SingleEntityTest extends BaseTest {


    @Before
    public void before() {
        UserInfo user = getUserInfo();
        boolean result = DbormHandler.getDborm().insert(user);
        assertEquals(true, result);
    }

    @Test
    public void testUpdate() {
        UserInfo user = new UserInfo();
        user.setId(USER_ID);
        user.setName("Jack");
        boolean result = DbormHandler.getDborm().update(user);
        assertEquals(true, result);
        user = DbormHandler.getDborm().getEntityByExample(user);
        assertEquals("Jack", user.getName());//因为设置了用户名的值,所以用户名被修改
        assertEquals("汤姆", user.getNickname());//因为没有设置昵称的值,所以昵称不变
    }

    @Test
    public void testReplace() {
        UserInfo user = new UserInfo();
        user.setId(USER_ID);
        user.setName("Jack");
        boolean result = DbormHandler.getDborm().replace(user);
        assertEquals(true, result);
        user = DbormHandler.getDborm().getEntityByExample(user);
        assertEquals("Jack", user.getName());//因为设置了用户名的值,所以用户名被修改
        assertEquals(null, user.getNickname());//因为没有设置昵称的值,所以昵称被替换为null
    }

    @Test
    public void testSaveOrReplace() {
        UserInfo user = new UserInfo();
        user.setId("USID2");
        user.setName("Tom");
        boolean result = DbormHandler.getDborm().saveOrReplace(user);//因为主键id为"USID2"的值不存在所以做新增操作
        assertEquals(true, result);

        user = new UserInfo();
        user.setId(USER_ID);
        user.setName("Jack");
        result = DbormHandler.getDborm().saveOrReplace(user);//因为主键id为USER_ID的值存在所以做替换操作
        assertEquals(true, result);
        user = DbormHandler.getDborm().getEntityByExample(user);
        assertEquals("Jack", user.getName());//因为设置了用户名的值,所以用户名被修改
        assertEquals(null, user.getNickname());//因为没有设置昵称的值,所以昵称被替换为null
    }


    @Test
    public void testSaveOrUpdate() {
        UserInfo user = new UserInfo();
        user.setId("USID2");
        user.setName("Tom");
        boolean result = DbormHandler.getDborm().saveOrUpdate(user);//因为主键id为"USID2"的值不存在所以做新增操作
        assertEquals(true, result);

        user = new UserInfo();
        user.setId(USER_ID);
        user.setName("Jack");
        result = DbormHandler.getDborm().saveOrUpdate(user);//因为主键id为USER_ID的值存在所以做替换操作
        assertEquals(true, result);
        user = DbormHandler.getDborm().getEntityByExample(user);
        assertEquals("Jack", user.getName());//因为设置了用户名的值,所以用户名被修改
        assertEquals("汤姆", user.getNickname());//因为没有设置昵称的值,所以昵称不变
    }

    @Test
    public void testDelete() {
        UserInfo user = new UserInfo();
        user.setId(USER_ID);
        user.setName("Jack");
        boolean result = DbormHandler.getDborm().delete(user);
        assertEquals(true, result);
        user = DbormHandler.getDborm().getEntityByExample(user);
        assertEquals(null, user);
    }

    @After
    public void after() {
        cleanTable();
    }


}
