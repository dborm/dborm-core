package cn.cocho.dborm.test.utils.db;

import cn.cocho.dborm.core.Dborm;

/**
 * Created by shk
 * 16/4/20 下午2:09
 */
public class DbormHandler {


    private static Dborm dborm;

    public static synchronized Dborm getDborm() {
        if (dborm == null) {
            dborm = new Dborm(new DataBaseManager(), new DBLogger());
        }
        return dborm;
    }

}
