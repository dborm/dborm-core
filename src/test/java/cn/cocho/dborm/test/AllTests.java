package cn.cocho.dborm.test;

import cn.cocho.dborm.test.deep.DeepSaveOrUpdateTest;
import cn.cocho.dborm.test.deep.RelationFieldTest;
import cn.cocho.dborm.test.deep.RelationMoreSaveOrUpdateTest;
import cn.cocho.dborm.test.excute.DefaultValuesTest;
import cn.cocho.dborm.test.excute.ListEntityTest;
import cn.cocho.dborm.test.excute.SigleEntityTest;
import cn.cocho.dborm.test.init.InitTest;
import cn.cocho.dborm.test.query.SelectModuleTest;
import cn.cocho.dborm.test.query.SelectRelationTest;
import cn.cocho.dborm.test.query.SelectTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({InitTest.class,
        DeepSaveOrUpdateTest.class, RelationFieldTest.class, RelationMoreSaveOrUpdateTest.class,
        DefaultValuesTest.class, ListEntityTest.class, SigleEntityTest.class,
        SelectModuleTest.class, SelectRelationTest.class, SelectTest.class})
public class AllTests {

    @Test
    public void testAll() {
        System.out.println("运行所有的测试用例");
    }

}
