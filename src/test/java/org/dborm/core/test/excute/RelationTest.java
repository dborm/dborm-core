package org.dborm.core.test.excute;

import org.dborm.core.test.utils.BaseTest;
import org.dborm.core.test.utils.db.DbormManager;
import org.dborm.core.test.utils.domain.BookInfo;
import org.dborm.core.test.utils.domain.UserInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 级联操作的使用
 */
public class RelationTest extends BaseTest {


    @Before
    public void before() {
        boolean result = DbormManager.getDborm().insert(getRelationUserInfo());
        assertEquals(true, result);
    }

    @Test
    public void testRelationUpdate() {
        boolean result = DbormManager.getDborm().update(getRelationUserInfo());
        assertEquals(true, result);
    }

    @Test
    public void testRelationDelete() {
        boolean result = DbormManager.getDborm().delete(getRelationUserInfo());
        assertEquals(true, result);
    }

    @Test
    public void testRelationReplace() {
        boolean result = DbormManager.getDborm().replace(getRelationUserInfo());
        assertEquals(true, result);
    }

    @Test
    public void testRelationSaveOrUpdate() {
        boolean result = DbormManager.getDborm().saveOrUpdate(getRelationUserInfo());
        assertEquals(true, result);
    }

    @Test
    public void testRelationInsertAndUpdate() {
        UserInfo userInfo = getUserInfo();
        BookInfo bookInfo = new BookInfo();
        bookInfo.setId("111");
        bookInfo.setUserId("222");
        bookInfo.setName("《重构》");
        userInfo.setBookInfo(bookInfo);
        boolean result = DbormManager.getDborm().saveOrUpdate(userInfo);//因bookInfo的主键并不存在,所以userInfo做新增操作,bookInfo做修改操作
        assertEquals(true, result);
    }

    @Test
    public void testRelationSaveOrReplace() {
        boolean result = DbormManager.getDborm().saveOrReplace(getRelationUserInfo());
        assertEquals(true, result);
    }


    @After
    public void after() {
        cleanTable();
    }


    private UserInfo getRelationUserInfo() {
        UserInfo userInfo = getUserInfo();
        BookInfo bookInfo = getBookInfo();
        userInfo.setBookInfo(bookInfo);
        return userInfo;
    }

}
