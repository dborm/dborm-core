package org.dborm.test.query;

import org.dborm.core.domain.BaseDomain;
import org.dborm.test.utils.BaseTest;
import org.dborm.test.utils.db.DbormManager;
import org.dborm.test.utils.domain.BookInfo;
import org.dborm.test.utils.domain.SelectModel;
import org.dborm.test.utils.domain.UserInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * 连接查询等相关的测试用例
 * Created by shk
 */
public class SelectJoinTest extends BaseTest {

    @BeforeClass
    public static void before() {
        UserInfo userInfo = getUserInfo();
        DbormManager.getDborm().insert(userInfo);
        BookInfo bookInfo = getBookInfo();
        bookInfo.setUserId(userInfo.getId());
        DbormManager.getDborm().insert(bookInfo);
    }


    /**
     * 通用查询
     * 所有的查询结果都可以使用BaseDomain接收
     */
    @Test
    public void testUseBaseDomain() {
        String sql = "select * from book_info";
        BaseDomain baseDomain = DbormManager.getDborm().getEntity(BaseDomain.class, sql);
        assertEquals(BOOK_ID, baseDomain.getParam("id"));//可以将查询结果封装在BaseDomain中,然后通过属性名称获取
        assertEquals(USER_ID, baseDomain.getParam("userId"));//如列为user_id,取值的时候需要使用userId(自动将下划线格式转换为驼峰格式)
    }


    /**
     * 补充查询（连接查询时推荐使用该方式）
     * 当查询出的列在接收返回值的类中找不到对应的属性时会将该值以键值对的方式存到BaseDomain的param中
     * 此时列名会由下划线格式转换为驼峰格式的属性名,如列名为user_id,则存入param中的键名为userId
     */
    @Test
    public void testWithBaseDomain() {
        String sql = "select book.id, book.user_id, user.nickname from book_info book left join user_info user on book.user_id = user.id";
        BookInfo bookInfo = DbormManager.getDborm().getEntity(BookInfo.class, sql);
        assertEquals(BOOK_ID, bookInfo.getId());
        assertEquals(USER_ID, bookInfo.getUserId());//如列为user_id,取值的时候需要使用userId(自动将下划线格式转换为驼峰格式)
        assertEquals(USER_NICKNAME, bookInfo.getParam("nickname"));//如果查询结果中有某一列的值,但是接受结果的类中没有该属性,则该值将会存储到param中
    }


    /**
     * 模板查询
     * 可以将查询结果映射到一个指定的类模板上,该模板需要继承BaseDomain,属性上不需要添加column注解
     */
    @Test
    public void testSelectModel() {
        /* 注意:
            1、想要将列的值映射到属性名为bookId的属性上,列的别名必须是book_id,不能为bookId
            2、联合查询中如果多个表有相同的字段名称,最好将其中一个字段设置别名避免冲突,如果不设置,则只能接收到靠后的一个表对应的列的值
               如user_info表中存在字段名称为id的字段,book_info表中也存在字段名称为id的字段名称,
               如果连接查询为select * FROM user_info u left join book_info b on u.id = b.user_id
               接受查询结果的对象中有一个属性名为id的属性,则接收的id的值是book_info表的值
         */
        String sql = "SELECT u.*, b.id as book_id, b.name as book_name FROM user_info u left join book_info b on u.id = b.user_id";
        List<SelectModel> selectModels = DbormManager.getDborm().getEntities(SelectModel.class, sql);
        assertNotNull(selectModels.get(0));
        for (SelectModel selectModel : selectModels) {
            assertEquals(USER_NICKNAME, selectModels.get(0).getNickname());
            assertEquals(USER_ID, selectModels.get(0).getId());
            assertEquals(BOOK_ID, selectModels.get(0).getBookId());

        }
    }

    /**
     * 组合查询（连接的多个表中有相同字段的时候不建议使用该方式）
     * 可以将查询结果映射到多个实体组中
     */
    @Test
    public void testSelectTeam() {
        String sql = "SELECT u.*, b.* FROM user_info u left join book_info b on u.id = b.user_id";
        List<Map<String, Object>> entityList = DbormManager.getDborm().getEntities(new Class<?>[]{UserInfo.class, BookInfo.class}, sql);
        for (int i = 0; i < entityList.size(); i++) {
            Map<String, Object> entityTeam = entityList.get(i);
            UserInfo user = (UserInfo) entityTeam.get(UserInfo.class.getName());
            BookInfo bookInfo = (BookInfo) entityTeam.get(BookInfo.class.getName());
            assertEquals(BOOK_ID, user.getId());
            /*
            注意:此时查询结果中列名为id的列有两个,连接查询的时候会出现靠后的表的值覆盖靠前的表的值
                连接的多个表中有相同字段的时候不建议使用该查询模式方式,如果使用该模式,需要将同名列起别名
             */
            assertEquals(BOOK_ID, bookInfo.getId());
            assertEquals(true, bookInfo.getLooked());
        }
    }

    @AfterClass
    public static void after() {
        cleanTable();
    }


}
