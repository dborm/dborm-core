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

    int count = 10;

    @Before
    public void before() {
        List<UserInfo> userInfos = getUserInfos(count);
        int result = DbormManager.getDborm().insert(userInfos);
        assertEquals(count, result);
    }

    @Test
    public void testUpdate() {
        List<UserInfo> userInfos = getUserInfos(count);
        int result = DbormManager.getDborm().update(userInfos);
        assertEquals(count, result);
    }

    @Test
    public void testReplace() {
        List<UserInfo> userInfos = getUserInfos(count);
        int result = DbormManager.getDborm().replace(userInfos);
        assertEquals(count, result);
    }

    @Test
    public void testSaveOrReplace() {
        List<UserInfo> userInfos = getUserInfos(count);
        int result = DbormManager.getDborm().replace(userInfos);
        assertEquals(count, result);
    }


    @Test
    public void testSaveOrUpdate() {
        List<UserInfo> userInfos = getUserInfos(count);
        int result = DbormManager.getDborm().replace(userInfos);
        assertEquals(count, result);
    }

    @Test
    public void testDelete() {
        List<UserInfo> userInfos = getUserInfos(count);
        int result = DbormManager.getDborm().delete(userInfos);
        assertEquals(count, result);
        userInfos = DbormManager.getDborm().getEntities(UserInfo.class, "select * from user_info");
        assertEquals(0, userInfos.size());
    }

    @After
    public void after() {
        cleanTable();
    }


}
