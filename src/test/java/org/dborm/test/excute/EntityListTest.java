package org.dborm.test.excute;

import org.dborm.test.utils.BaseTest;
import org.dborm.test.utils.db.DbormManager;
import org.dborm.test.utils.domain.UserInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 多个实体对象的增删改操作
 */
public class EntityListTest extends BaseTest {


    @Before
    public void before() {
        List<UserInfo> userInfos = getUserInfos(10);
        boolean result = DbormManager.getDborm().insert(userInfos);
        assertEquals(true, result);
    }

    @Test
    public void testUpdate() {
        List<UserInfo> userInfos = getUserInfos(10);
        boolean result = DbormManager.getDborm().update(userInfos);
        assertEquals(true, result);
    }

    @Test
    public void testReplace() {
        List<UserInfo> userInfos = getUserInfos(10);
        boolean result = DbormManager.getDborm().replace(userInfos);
        assertEquals(true, result);
    }

    @Test
    public void testSaveOrReplace() {
        List<UserInfo> userInfos = getUserInfos(10);
        boolean result = DbormManager.getDborm().replace(userInfos);
        assertEquals(true, result);
    }


    @Test
    public void testSaveOrUpdate() {
        List<UserInfo> userInfos = getUserInfos(10);
        boolean result = DbormManager.getDborm().replace(userInfos);
        assertEquals(true, result);
    }

    @Test
    public void delete() {
        List<UserInfo> userInfos = getUserInfos(10);
        boolean result = DbormManager.getDborm().delete(userInfos);
        assertEquals(true, result);
        userInfos = DbormManager.getDborm().getEntities(UserInfo.class, "select * from user_info");
        assertEquals(0, userInfos.size());
    }

    @After
    public void after() {
        cleanTable();
    }


}
