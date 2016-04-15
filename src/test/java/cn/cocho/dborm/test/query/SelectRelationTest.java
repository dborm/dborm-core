package cn.cocho.dborm.test.query;

import cn.cocho.dborm.core.Dborm;
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
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SelectRelationTest extends BaseTest {

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
        String sql = "SELECT u.*, q.question_id, q.content FROM qsm_option q LEFT JOIN user_info u ON u.user_id=q.user_id WHERE u.user_id = ? ";
        // UserInfo对象里面一定要有questionId和content属性
        List<UserInfo> userList = dborm.getEntities(UserInfo.class, sql, USER_ID);
        for (int i = 0; i < userList.size(); i++) {
            UserInfo user = userList.get(i);
            assertEquals(USER_NAME, user.getUserName());
            assertEquals(QSM_CONTENT, user.getContent());// 将添加其它对象的部分字段（这些字段不需要再xml文件中标注）
        }
    }

    @Test
    public void testB28GetJoinEntitys() {
        String sql = "SELECT * FROM qsm_option q LEFT JOIN user_info u ON u.user_id=q.user_id WHERE u.user_id = ? ";
        String[] bindArgs = new String[]{USER_ID};
        List<Map<String, Object>> entityList = dborm.getEntities(new Class<?>[]{UserInfo.class, QsmOption.class}, sql, USER_ID);
        for (int i = 0; i < entityList.size(); i++) {
            Map<String, Object> entityTeam = entityList.get(i);
            UserInfo user = (UserInfo) entityTeam.get(UserInfo.class.getName());
            QsmOption option = (QsmOption) entityTeam.get(QsmOption.class.getName());
            assertEquals(USER_NAME, user.getUserName());
            assertEquals(QSM_CONTENT, option.getContent());
        }
    }

    @AfterClass
    public static void deleteData() {
        cleanTable();
    }

}
