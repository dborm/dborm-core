package org.dborm.test.utils.db;

import org.dborm.core.api.Dborm;
import org.dborm.core.framework.DbormHandler;

/**
 * Created by shk
 * 16/4/20 下午2:09
 */
public class DbormManager {


    private static Dborm dborm;

    public static synchronized Dborm getDborm() {
        if (dborm == null) {
            dborm = new DbormHandler(new DataBaseManager());
        }
        return dborm;
    }

    //也可以不用单例模式
//    public static Dborm getDborm() {
//        return new Dborm(new DataBaseManager());
//    }

}
