package cn.cocho.dborm.test.excute;

import cn.cocho.dborm.core.Dborm;
import cn.cocho.dborm.test.utils.BaseTest;
import cn.cocho.dborm.test.utils.DBLogger;
import cn.cocho.dborm.test.utils.DataBaseManager;
import cn.cocho.dborm.test.utils.domain.UserInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * 对象的批量操作
 *
 * @author COCHO
 * @2013年7月27日 @下午3:05:06
 */
public class ListEntityTest extends BaseTest {

    static int nums = 10;


    static Dborm dborm;

    @BeforeClass
    public static void setUp() {
        dborm = new Dborm(new DataBaseManager(), new DBLogger());
        List<UserInfo> userList = new ArrayList<UserInfo>();
        for (int i = 0; i < nums; i++) {
            UserInfo user = new UserInfo();
            user.setId("id" + i);
            user.setUserId("userId" + i);
            user.setUserName("Tom");
            user.setAge(10);
            user.setBirthday(new Date());
            userList.add(user);
        }
        boolean result = dborm.insert(userList);
        assertEquals(true, result);
    }

    @Test
    public void testC15UpdateList() {
        List<UserInfo> userList = new ArrayList<UserInfo>();
        for (int i = 0; i < nums; i++) {
            UserInfo user = new UserInfo();
            user.setId("id" + i);
            user.setUserId("userId" + i);
            user.setUserName("TomUpdateToLucy");
            userList.add(user);
        }
        boolean result = dborm.update(userList);
        assertEquals(true, result);
    }

    @Test
    public void testC20ReplaceList() {
        List<UserInfo> userList = new ArrayList<UserInfo>();
        for (int i = 0; i < nums; i++) {
            UserInfo user = new UserInfo();
            user.setId("id" + i);
            user.setUserId("userId" + i);
            user.setUserName("LucyReplaceToJack");
            user.setBirthday(new Date());
            userList.add(user);
        }
        boolean result = dborm.replace(userList);
        assertEquals(true, result);
    }

    @AfterClass
    public static void testC30DeleteList() {
        List<UserInfo> userList = new ArrayList<UserInfo>();
        for (int i = 0; i < nums; i++) {
            UserInfo user = new UserInfo();
            user.setId("id" + i);
            user.setUserId("userId" + i);
            userList.add(user);
        }
        boolean result = dborm.delete(userList);
        assertEquals(true, result);
    }


}
