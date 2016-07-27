package org.dborm.test;


import org.dborm.test.excute.*;
import org.dborm.test.init.InitTest;
import org.dborm.test.query.SelectJoinTest;
import org.dborm.test.query.SelectTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({InitTest.class,
        RelationTest.class, RelationListTest.class,
        DefaultValueTest.class, EntityListTest.class, EntityTest.class,
        SelectTest.class, SelectJoinTest.class})
public class AllTests {

    @Test
    public void testAll() {
        System.out.println("运行所有的测试用例");
    }

}
