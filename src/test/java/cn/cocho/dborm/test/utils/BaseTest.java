package cn.cocho.dborm.test.utils;

import cn.cocho.dborm.core.Dborm;
import cn.cocho.dborm.test.utils.domain.UserInfo;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class BaseTest {


    private static Dborm dborm;

    public static synchronized Dborm getDborm() {
        if (dborm == null) {
            dborm = new Dborm(new DataBaseManager(), new DBLogger());
        }
        return dborm;
    }


    public static final String USER_ID = "USID1";

    public static UserInfo getUserInfo() {
        UserInfo user = new UserInfo();
        user.setId(USER_ID);
        user.setName("Tom");
        user.setNickname("汤姆");
        user.setAge(10);
        user.setCreateTime(new Date());
        return user;
    }

    public static void cleanTable() {
        Dborm dborm = new Dborm(new DataBaseManager(), new DBLogger());
        boolean result = dborm.execSql("delete from user_info");
        assertEquals(true, result);
        result = dborm.execSql("delete from book_info");
        assertEquals(true, result);
    }


}
