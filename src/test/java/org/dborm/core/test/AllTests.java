package org.dborm.core.test;


import org.dborm.core.test.excute.DefaultValueTest;
import org.dborm.core.test.excute.ListEntityTest;
import org.dborm.core.test.excute.SingleEntityTest;
import org.dborm.core.test.init.InitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({InitTest.class,
        DefaultValueTest.class, ListEntityTest.class, SingleEntityTest.class})
public class AllTests {

    @Test
    public void testAll() {
        System.out.println("运行所有的测试用例");
    }

}
