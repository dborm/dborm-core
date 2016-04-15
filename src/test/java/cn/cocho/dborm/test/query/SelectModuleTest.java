package cn.cocho.dborm.test.query;

import cn.cocho.dborm.core.Dborm;
import cn.cocho.dborm.test.utils.BaseTest;
import cn.cocho.dborm.test.utils.DBLogger;
import cn.cocho.dborm.test.utils.DataBaseManager;
import cn.cocho.dborm.test.utils.domain.UserInfo;
import cn.cocho.dborm.test.utils.domain.QsmOption;
import cn.cocho.dborm.test.utils.domain.SelectModule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * 使用查询模版的测试用例
 *
 * @author COCHO
 * @2013年7月27日 @下午2:59:43
 */
public class SelectModuleTest extends BaseTest {


    private final static String USER_ID = "23432423asdasdq321eada";
    private final static String USER_NAME = "Tom";
    private final static int USER_AGE = 20;

    private final static String QSM_CONTENT = "测试内容";

    static Dborm dborm;

    @BeforeClass
    public static void testA10initData() {
        dborm = new Dborm(new DataBaseManager(), new DBLogger());
        UserInfo user = new UserInfo();
        user.setId("dsfdsfsdafdsfds2343sdfsdf");
        user.setUserId(USER_ID);
        user.setUserName(USER_NAME);
        user.setAge(USER_AGE);

        List<QsmOption> qsmOptionList = new ArrayList<QsmOption>();
        for (int i = 0; i < 10; i++) {
            QsmOption option = new QsmOption();
            option.setOptionId("OPTION_ID_" + i);
            option.setQuestionId("789");
            option.setContent(QSM_CONTENT);
            option.setUserId(USER_ID);
            option.setShowOrder(i + 10f);
            qsmOptionList.add(option);
        }
        user.setQsmOptionList(qsmOptionList);

        boolean result = dborm.insert(user);
        assertEquals(true, result);
    }

    /**
     * 测试连接查询
     *
     * @author COCHO
     * @time 2013-6-6下午5:45:59
     */
    @Test
    public void testB25GetJoinEntitys() {
        String sql = "SELECT u.user_id, u.user_name as login_name, q.question_id, q.content, q.show_order FROM qsm_option q LEFT JOIN user_info u ON u.user_id=q.user_id WHERE u.user_id = ? ";
        List<SelectModule> moduleList = dborm.getEntities(SelectModule.class, sql, USER_ID);
        assertNotNull(moduleList.get(0));
        for (int i = 0; i < moduleList.size(); i++) {
            SelectModule module = moduleList.get(i);
            assertEquals(USER_NAME, module.getLoginName());//别名查询
            assertEquals(QSM_CONTENT, module.getContent());
        }
    }

    @Test
    public void testB28GetJoinEntitys() {
        String sql = "SELECT * FROM qsm_option q LEFT JOIN user_info u ON u.user_id=q.user_id WHERE u.user_id = ? ";
        List<SelectModule> moduleList = dborm.getEntities(SelectModule.class, sql, USER_ID);
        for (int i = 0; i < moduleList.size(); i++) {
            SelectModule module = moduleList.get(i);
            assertEquals(USER_NAME, module.getUserName());
            assertEquals(QSM_CONTENT, module.getContent());
        }
    }

    @AfterClass
    public static void deleteData() {
        cleanTable();
    }
}
