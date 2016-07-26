package org.dborm.core.test.query;

import org.dborm.core.domain.QueryResult;
import org.dborm.core.framework.SQLExecutor;
import org.dborm.core.test.utils.BaseTest;
import org.dborm.core.test.utils.db.DbormHandler;
import org.dborm.core.test.utils.domain.BookInfo;
import org.dborm.core.test.utils.domain.UserInfo;
import org.dborm.core.utils.DbormDataBase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 查询相关的测试用例
 * Created by shk
 */
public class SelectTest extends BaseTest {

    @BeforeClass
    public static void before() {
        UserInfo userInfo = getUserInfo();
        DbormHandler.getDborm().insert(userInfo);
        BookInfo bookInfo = getBookInfo();
        bookInfo.setUserId(userInfo.getId());
        DbormHandler.getDborm().insert(bookInfo);
    }


    @Test
    public void testGetEntity() {
        String sql = "SELECT * FROM user_info where id = ? ";
        String[] bindArgs = new String[]{USER_ID};
        UserInfo user = DbormHandler.getDborm().getEntity(UserInfo.class, sql, USER_ID);
        assertEquals(USER_NICKNAME, user.getNickname());
    }

    @Test
    public void testGetEntities() {
        String sql = "SELECT * FROM user_info";
        List<UserInfo> user = DbormHandler.getDborm().getEntities(UserInfo.class, sql);
        assertEquals(USER_NICKNAME, user.get(0).getNickname());
    }

    @Test
    public void testGetEntitiesByExample() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(USER_ID);
        userInfo.setNickname(USER_NICKNAME);
        List<UserInfo> userList = DbormHandler.getDborm().getEntitiesByExample(userInfo, true);
        assertEquals(1, userList.size());
    }

    @Test
    public void testGetEntityCount() {
        long count = DbormHandler.getDborm().getEntityCount(UserInfo.class);
        assertEquals(1, count);
    }

    @Test
    public void testGetCount() {
        String sql = "SELECT COUNT(*) FROM user_info where id = ? ";
        long count = DbormHandler.getDborm().getCount(sql, USER_ID);
        assertEquals(1, count);
    }

    @Test
    public void testUseResultSet() {
        String sql = "SELECT id, nickname, age FROM user_info where id = ? ";
        List<String> selectionArgs = new ArrayList<String>();
        selectionArgs.add(USER_ID);
        DbormDataBase dbormDataBase = DbormHandler.getDborm().getDataBase();
        Connection conn = null;
        try {
            conn = dbormDataBase.getConnection();
            List<QueryResult> queryResults = new SQLExecutor().query(sql, selectionArgs, conn);
            for (QueryResult queryResult : queryResults) {
                assertEquals(USER_NICKNAME, queryResult.getObject("nickname"));
                assertEquals(USER_ID, queryResult.getObject(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbormDataBase.closeConn(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @AfterClass
    public static void after() {
        cleanTable();
    }


}
