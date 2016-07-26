package org.dborm.core.test.excute;

import org.dborm.core.test.utils.BaseTest;
import org.dborm.core.test.utils.db.DbormManager;
import org.dborm.core.test.utils.domain.UserInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 事务的使用
 */
public class TransactionTest extends BaseTest {


    @Before
    public void before() {
    }

    @Test
    public void testTransactionSuccess() {
        UserInfo user = getUserInfo();
        DbormManager.getDborm().beginTransaction();
        DbormManager.getDborm().insert(user);

        user.setName("Jack");
        DbormManager.getDborm().update(user);
        boolean result = DbormManager.getDborm().commit();
        assertEquals(true, result);
    }

    @Test
    public void testTransactionError() {
        //如需测试请将如下注释打开

//        UserInfo user = getUserInfo();
//        DbormHandler.getDborm().beginTransaction();
//        DbormHandler.getDborm().insert(user);
//
//        user.setName("Jack");
//        DbormHandler.getDborm().insert(user);//因主键相同所以第二次新增将会出错
//        boolean result = DbormHandler.getDborm().commit();
//        assertEquals(true, result);
//
//        List<UserInfo> userInfos = DbormHandler.getDborm().getEntities(UserInfo.class, "select * from user_info");
//        assertEquals(0, userInfos.size());//因事务具有原子性,所以以上两条记录都没有添加成功
    }

    @After
    public void after() {
        cleanTable();
    }


}
