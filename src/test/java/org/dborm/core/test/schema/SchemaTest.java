package org.dborm.core.test.schema;

import org.dborm.core.api.Dborm;
import org.dborm.core.framework.DbormHandler;
import org.dborm.core.schema.DbormSchemaInit;
import org.dborm.core.test.utils.BaseTest;
import org.dborm.core.test.utils.db.DataBaseManager;
import org.dborm.core.test.utils.domain.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by shk
 * 16/5/16 13:57
 */
public class SchemaTest extends BaseTest {

    @BeforeClass
    public static void before() {

    }


    @Test
    public void init() {
        try {
            DbormSchemaInit init = new DbormSchemaInit();
            init.initSchema("dborm/schema");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Dborm dborm = new DbormHandler(new DataBaseManager());

        User user = new User();//User类没有添加任何注解,仍然可以操作,是因为用xml（resources/dborm/schema/schema.schema）描述了该类
        user.setId(USER_ID);
        user.setName("Tom");
        user.setNickname(USER_NICKNAME);
        user.setAge(10);
        user.setCreateTime(new Date());
        boolean result = dborm.insert(user);
        assertEquals(true, result);
    }


    @AfterClass
    public static void after() {
        cleanTable();
    }


}
