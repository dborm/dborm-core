package org.dborm.core.test.init;

import org.dborm.core.framework.Dborm;
import org.dborm.core.test.utils.db.DataBaseManager;
import org.dborm.core.test.utils.domain.UserInfo;
import org.dborm.core.utils.DbormContexts;
import org.dborm.core.utils.DbormLoggerDefault;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class InitTest {

    Dborm dborm;
    UserInfo user;

    public InitTest() {
    }

    @Test
    public void init() {
        try {
            initDbormContexts();
            initDborm();
            check();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDbormContexts() {
        DbormContexts.showSql = true;
        DbormContexts.log = new DbormLoggerDefault();
    }

    private void initDborm() {
        try {
            dborm = new Dborm(new DataBaseManager(), new DbormLoggerDefault());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void check() {
        user = new UserInfo();
        user.setId("USID1");
        user.setName("Tom");
        user.setAge(10);
        user.setCreateTime(new Date());
        boolean result = dborm.insert(user);//添加一条数据测试一下数据库是否可用
        assertEquals(true, result);
        result = dborm.delete(user);
        assertEquals(true, result);
    }


}
