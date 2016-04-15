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

public class SigleEntityTest extends BaseTest {


    static Dborm dborm;

    @BeforeClass
    public static void testB11Insert() {
        dborm = new Dborm(new DataBaseManager(), new DBLogger());

        UserInfo user = new UserInfo();
        user.setId("ID1");
        user.setUserId("USID1");
        user.setUserName("Tom");
        user.setAge(10);
        user.setBirthday(new Date());
        boolean result = dborm.insert(user);
        assertEquals(true, result);
    }

    @Test
    public void testB15Update() {
        UserInfo user = new UserInfo();
        user.setId("ID1");
        user.setUserId("USID1");
        user.setAge(10);
        boolean result = dborm.update(user);
        assertEquals(true, result);
        user = dborm.getEntityByExample(user);
        assertEquals("Tom", user.getUserName());//因为没有设置用户名的值,所以用户名不变
    }

    @Test
    public void testB20Replace() {
        UserInfo user = new UserInfo();
        user.setId("ID1");
        user.setUserId("USID1");
        user.setAge(10);
        boolean result = dborm.replace(user);
        assertEquals(true, result);

        user = dborm.getEntityByExample(user);
        assertEquals(null, user.getUserName());//虽然没有设置用户名的值,但是整个对象替换,没有设置值的属性将会被替换为空,所以用户名为空
    }

    @Test
    public void testD10SaveOrReplace() {
        UserInfo user = new UserInfo();
        user.setId("ID2");
        user.setUserId("USID2");
        user.setUserName("Tom");
        boolean result = dborm.saveOrReplace(user);
        assertEquals(true, result);
    }

    @Test
    public void testD15SaveOrReplace() {
        UserInfo user = new UserInfo();
        user.setId("ID2");
        user.setUserId("USID2");
        user.setUserName("TomSaveOrReplace");
        boolean result = dborm.saveOrReplace(user);
        assertEquals(true, result);
    }

    @Test
    public void testD20SaveOrUpdate() {
        UserInfo user = new UserInfo();
        user.setId("ID2");
        user.setUserId("USID2");
        user.setAge(10);
        boolean result = dborm.saveOrUpdate(user);
        assertEquals(true, result);
    }

    @AfterClass
    public static void testB35Delete() {
        UserInfo user = new UserInfo();
        user.setId("ID1");
        user.setUserId("USID1");
        boolean result = dborm.delete(user);
        assertEquals(true, result);
    }


}
