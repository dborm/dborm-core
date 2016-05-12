package org.dborm.core.test.utils;

import org.dborm.core.test.utils.db.DbormHandler;
import org.dborm.core.test.utils.domain.BookInfo;
import org.dborm.core.test.utils.domain.UserInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Created by shk
 * 16/5/12 20:46
 */
public class BaseSelectTest extends BaseTest{

    @BeforeClass
    public static void before() {
        UserInfo userInfo = getUserInfo();
        DbormHandler.getDborm().insert(userInfo);
        BookInfo bookInfo = getBookInfo();
        bookInfo.setUserId(userInfo.getId());
        DbormHandler.getDborm().insert(bookInfo);
    }

    @AfterClass
    public static void after() {
        cleanTable();
    }


}
