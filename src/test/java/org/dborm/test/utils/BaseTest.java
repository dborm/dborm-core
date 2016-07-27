package org.dborm.test.utils;

import org.dborm.test.utils.db.DbormManager;
import org.dborm.test.utils.domain.BookInfo;
import org.dborm.test.utils.domain.UserInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BaseTest {


    public static final String USER_ID = "USER_ID";
    public static final String USER_NICKNAME = "汤姆";
    public static final String BOOK_ID = "BOOK_ID";

    public static UserInfo getUserInfo() {
        UserInfo user = new UserInfo();
        user.setId(USER_ID);
        user.setName("Tom");
        user.setNickname(USER_NICKNAME);
        user.setAge(10);
        user.setCreateTime(new Date());
        return user;
    }

    public static BookInfo getBookInfo() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.setId(BOOK_ID);
        bookInfo.setUserId(USER_ID);
        bookInfo.setName("《代码简洁之道》");
        bookInfo.setPrice(55.0);
        bookInfo.setLooked(true);
        bookInfo.setReadTime(15l);
        return bookInfo;
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
        boolean result = DbormManager.getDborm().execSql("delete from user_info");
        assertEquals(true, result);
        result = DbormManager.getDborm().execSql("delete from book_info");
        assertEquals(true, result);
    }


}
