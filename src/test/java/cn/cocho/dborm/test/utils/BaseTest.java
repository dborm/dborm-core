package cn.cocho.dborm.test.utils;

import cn.cocho.dborm.core.Dborm;
import cn.cocho.dborm.utils.DbormContexts;

import static org.junit.Assert.assertEquals;

public class BaseTest {


    static {
        DbormContexts.log = new DBLogger();
        DbormContexts.showSql = true;
        try {
            cleanTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cleanTable() {
        Dborm dborm = new Dborm(new DataBaseManager(), new DBLogger());

        boolean delLogin = dborm.execSql("delete from user_info");
        assertEquals(true, delLogin);
        boolean delOption = dborm.execSql("delete from qsm_option");
        assertEquals(true, delOption);
        boolean delInfo = dborm.execSql("delete from qsm_info");
        assertEquals(true, delInfo);
    }


}
