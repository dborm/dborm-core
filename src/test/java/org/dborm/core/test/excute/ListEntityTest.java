package org.dborm.core.test.excute;

import org.dborm.core.test.utils.BaseTest;
import org.dborm.core.test.utils.db.DbormHandler;
import org.dborm.core.test.utils.domain.UserInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 多个实体对象的增删改操作
 */
public class ListEntityTest extends BaseTest {


    @Before
    public void before() {
        List<UserInfo> userInfos = getUserInfos(10);
        boolean result = DbormHandler.getDborm().insert(userInfos);
        assertEquals(true, result);
    }

    @Test
    public void testUpdate() {
        List<UserInfo> userInfos = getUserInfos(10);
        boolean result = DbormHandler.getDborm().update(userInfos);
        assertEquals(true, result);
    }

    @Test
    public void testReplace() {
        List<UserInfo> userInfos = getUserInfos(10);
        boolean result = DbormHandler.getDborm().replace(userInfos);
        assertEquals(true, result);
    }

    @Test
    public void testSaveOrReplace() {
        List<UserInfo> userInfos = getUserInfos(10);
        boolean result = DbormHandler.getDborm().replace(userInfos);
        assertEquals(true, result);
    }


    @Test
    public void testSaveOrUpdate() {
        List<UserInfo> userInfos = getUserInfos(10);
        boolean result = DbormHandler.getDborm().replace(userInfos);
        assertEquals(true, result);
    }

    @Test
    public void delete() {
        List<UserInfo> userInfos = getUserInfos(10);
        boolean result = DbormHandler.getDborm().delete(userInfos);
        assertEquals(true, result);
        userInfos = DbormHandler.getDborm().getEntities(UserInfo.class, "select * from user_info");
        assertEquals(0, userInfos.size());
    }

    @After
    public void after() {
        cleanTable();
    }


}
