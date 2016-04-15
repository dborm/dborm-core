package cn.cocho.dborm.test.query;

import cn.cocho.dborm.core.Dborm;
import cn.cocho.dborm.domain.BaseDomain;
import cn.cocho.dborm.test.utils.BaseTest;
import cn.cocho.dborm.test.utils.DBLogger;
import cn.cocho.dborm.test.utils.DataBaseManager;
import cn.cocho.dborm.test.utils.domain.UserInfo;
import cn.cocho.dborm.test.utils.domain.QsmOption;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 使用查询模版的测试用例
 *
 * @author COCHO
 * @2013年7月27日 @下午2:59:43
 */
public class SelectMapTest extends BaseTest {


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
        String sql = "SELECT u.user_id, u.user_name, q.question_id, q.content, q.show_order FROM qsm_option q LEFT JOIN user_info u ON u.user_id=q.user_id WHERE u.user_id = ? ";
        List<BaseDomain> domains = dborm.getEntities(BaseDomain.class, sql, USER_ID);
        for (int i = 0; i < domains.size(); i++) {
            BaseDomain domain = domains.get(i);
            System.out.println(domain.getParam("userId"));
            assertEquals(QSM_CONTENT, domain.getParam("content").toString());
        }
    }


    @AfterClass
    public static void deleteData() {
        cleanTable();
    }
}
