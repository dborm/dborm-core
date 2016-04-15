package cn.cocho.dborm.test.excute;

import cn.cocho.dborm.core.Dborm;
import cn.cocho.dborm.test.utils.BaseTest;
import cn.cocho.dborm.test.utils.DBLogger;
import cn.cocho.dborm.test.utils.DataBaseManager;
import cn.cocho.dborm.test.utils.domain.UserInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * 测试数据库字段中使用默认值
 *
 * @author COCHO
 * @2013年8月6日 @下午6:06:30
 */
public class TransactionTest extends BaseTest {


    static Dborm dborm;

    @BeforeClass
    public static void testA10initData() {
        dborm = new Dborm(new DataBaseManager(), new DBLogger());
    }



    @Test
    public void testB11Insert() {
        UserInfo user = new UserInfo();
        user.setId("ID1");
        user.setUserId("USID1");
        user.setUserName("Tom");
        user.setAge(10);
        user.setBirthday(new Date());

        UserInfo user2 = new UserInfo();
        user2.setId("ID1");//制造主键冲突
        user2.setUserId("USID1");
        user2.setUserName("Tom");
        user2.setAge(10);
        user2.setBirthday(new Date());

        dborm.beginTransaction();
        dborm.insert(user);
        dborm.insert(user2);

        //需要测试事务的时候请将如下注释打开运行测试

//        boolean result = dborm.commit();
//        assertEquals(false, result);
//
//        List<UserInfo> userList = dborm.getEntities(UserInfo.class, "select * from user_info");
//        assertEquals(0, userList.size());//没有新增进去记录
    }

    @AfterClass
    public static void testZ10DeleteDb() {
        boolean delLogin = dborm.execSql("delete from user_info");
        assertEquals(true, delLogin);
    }


}
