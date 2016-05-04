package org.dborm.core.test.query;

import org.dborm.core.domain.BaseDomain;
import org.dborm.core.test.utils.BaseTest;
import org.dborm.core.test.utils.db.DbormHandler;
import org.dborm.core.test.utils.domain.BookInfo;
import org.dborm.core.test.utils.domain.UserInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 默认值的使用
 */
public class SelectBaseDomainTest extends BaseTest {


    @Before
    public void before() {
        UserInfo userInfo = getUserInfo();
        DbormHandler.getDborm().insert(userInfo);
        BookInfo bookInfo = getBookInfo();
        bookInfo.setUserId(userInfo.getId());
        DbormHandler.getDborm().insert(bookInfo);
    }

    @Test
    public void testUseBaseDomain() {
        String sql = "select * from book_info";
        BaseDomain baseDomain = DbormHandler.getDborm().getEntity(BaseDomain.class, sql);
        assertEquals(BOOK_ID, baseDomain.getParam("id"));//可以将查询结果封装在BaseDomain中,然后通过属性名称获取
        assertEquals(USER_ID, baseDomain.getParam("userId"));//如列为user_id,取值的时候需要使用userId(自动将下划线格式转换为驼峰格式)
    }


    @Test
    public void testWithBaseDomain() {
        String sql = "select book.id, book.user_id, user.nickname from book_info book left join user_info user on book.user_id = user.id";
        BookInfo bookInfo = DbormHandler.getDborm().getEntity(BookInfo.class, sql);
        assertEquals(BOOK_ID, bookInfo.getId());
        assertEquals(USER_ID, bookInfo.getUserId());//如列为user_id,取值的时候需要使用userId(自动将下划线格式转换为驼峰格式)
        assertEquals(USER_NICKNAME, bookInfo.getParam("nickname"));//如果查询结果中有某一列的值,但是接受结果的类中没有该属性,则该值将会存储到param中
    }


    @After
    public void after() {
        cleanTable();
    }


}