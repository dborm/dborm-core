package org.dborm.core.test.query;

import org.dborm.core.framework.SQLExecutor;
import org.dborm.core.test.utils.BaseSelectTest;
import org.dborm.core.test.utils.db.DbormHandler;
import org.dborm.core.test.utils.domain.UserInfo;
import org.dborm.core.utils.DbormDataBase;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 查询相关的测试用例
 * Created by shk
 */
public class SelectTest extends BaseSelectTest {

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
        int count = DbormHandler.getDborm().getEntityCount(UserInfo.class);
        assertEquals(1, count);
    }

    @Test
    public void testGetCount() {
        String sql = "SELECT COUNT(*) FROM user_info where id = ? ";
        int count = DbormHandler.getDborm().getCount(sql, USER_ID);
        assertEquals(1, count);
    }

    @Test
    public void testUseResultSet() {
        String sql = "SELECT id, nickname, age FROM user_info where id = ? ";
        List<String> selectionArgs = new ArrayList<String>();
        selectionArgs.add(USER_ID);
        DbormDataBase dbormDataBase = DbormHandler.getDborm().getDataBase();
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dbormDataBase.getConnection();
            rs = new SQLExecutor().getResultSet(sql, selectionArgs, conn);
            while (rs.next()) {
                assertEquals(USER_NICKNAME, rs.getString("nickname"));
                assertEquals(USER_ID, rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                dbormDataBase.closeConn(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
