package org.dborm.core.test.utils.db;

import org.dborm.core.framework.Dborm;

/**
 * Created by shk
 * 16/4/20 下午2:09
 */
public class DbormHandler {


    private static Dborm dborm;

    public static synchronized Dborm getDborm() {
        if (dborm == null) {
            dborm = new Dborm(new DataBaseManager());
        }
        return dborm;
    }

}
