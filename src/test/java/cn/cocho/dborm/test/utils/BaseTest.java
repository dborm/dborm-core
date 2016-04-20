package cn.cocho.dborm.test.utils;

import cn.cocho.dborm.test.utils.db.DbormHandler;
import cn.cocho.dborm.test.utils.domain.UserInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BaseTest {


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

    public static List<UserInfo> getUserInfos(int num) {
        List<UserInfo> userInfos = new ArrayList<UserInfo>();
        for (int i = 0; i < num; i++) {
            UserInfo user = new UserInfo();
            user.setId(USER_ID + i);
            user.setName("Tom");
            user.setCreateTime(new Date());
            userInfos.add(user);
        }
        return userInfos;
    }


    public static void cleanTable() {
        boolean result = DbormHandler.getDborm().execSql("delete from user_info");
        assertEquals(true, result);
//        result = getDborm().execSql("delete from book_info");
//        assertEquals(true, result);
    }


}
