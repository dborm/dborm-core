package org.dborm.core.test;


import org.dborm.core.test.excute.*;
import org.dborm.core.test.init.InitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({InitTest.class,
        RelationTest.class, RelationListTest.class,
        DefaultValueTest.class, EntityListTest.class, EntityTest.class})
public class AllTests {

    @Test
    public void testAll() {
        System.out.println("运行所有的测试用例");
    }

}
