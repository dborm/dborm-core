package cn.cocho.dborm.test.excute;

import cn.cocho.dborm.test.utils.BaseTest;
import cn.cocho.dborm.test.utils.domain.UserInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 单个实体对象的曾删改操作
 */
public class SigleEntityTest extends BaseTest {


    @BeforeClass
    public static void before() {
        cleanTable();
        UserInfo user = getUserInfo();
        boolean result = getDborm().insert(user);
        assertEquals(true, result);
    }

    @Test
    public void testUpdate() {
        UserInfo user = new UserInfo();
        user.setId(USER_ID);
        user.setName("Jack");
        boolean result = getDborm().update(user);
        assertEquals(true, result);
        user = getDborm().getEntityByExample(user);
        assertEquals("Jack", user.getName());//因为设置了用户名的值,所以用户名被修改
        assertEquals("汤姆", user.getNickname());//因为没有设置昵称的值,所以昵称不变
    }

    @Test
    public void testReplace() {
        UserInfo user = new UserInfo();
        user.setId(USER_ID);
        user.setName("Jack");
        boolean result = getDborm().replace(user);
        assertEquals(true, result);
        user = getDborm().getEntityByExample(user);
        assertEquals("Jack", user.getName());//因为设置了用户名的值,所以用户名被修改
        assertEquals(null, user.getNickname());//因为没有设置昵称的值,所以昵称被替换为null
    }
//
//    @Test
//    public void testD10SaveOrReplace() {
//        UserInfo user = new UserInfo();
//        user.setId("USID2");
//        user.setName("Tom");
//        boolean result = dborm.saveOrReplace(user);
//        assertEquals(true, result);
//    }
//
//    @Test
//    public void testD15SaveOrReplace() {
//        UserInfo user = new UserInfo();
//        user.setId("USID2");
//        user.setName("TomSaveOrReplace");
//        boolean result = dborm.saveOrReplace(user);
//        assertEquals(true, result);
//    }
//
//    @Test
//    public void testD20SaveOrUpdate() {
//        UserInfo user = new UserInfo();
//        user.setId("USID2");
//        user.setAge(10);
//        boolean result = dborm.saveOrUpdate(user);
//        assertEquals(true, result);
//    }

    @AfterClass
    public static void after() {
        UserInfo user = new UserInfo();
        user.setId("USID1");
        boolean result = getDborm().delete(user);
        assertEquals(true, result);
    }


}
