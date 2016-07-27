package org.dborm.test.excute;

import org.dborm.test.utils.BaseTest;
import org.dborm.test.utils.db.DbormManager;
import org.dborm.test.utils.domain.UserInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 默认值的使用
 */
public class DefaultValueTest extends BaseTest {


    @Before
    public void before() {
    }

    @Test
    public void testDefaultValue() {
        UserInfo user = new UserInfo();
        user.setId(USER_ID);
        user.setName("Jack");
        boolean result = DbormManager.getDborm().insert(user);
        assertEquals(true, result);
        user = DbormManager.getDborm().getEntityByExample(user);
        if (user.getAge() == 18) {
            assertEquals(true, true);//因为age属性的column注解指定了默认值,所以新增的时候没有设置age的值时会使用默认值
        } else {
            assertEquals(true, false);
        }
    }


    @After
    public void after() {
        cleanTable();
    }


}
