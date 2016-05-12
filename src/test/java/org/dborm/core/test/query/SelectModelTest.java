package org.dborm.core.test.query;

import org.dborm.core.test.utils.BaseSelectTest;
import org.dborm.core.test.utils.db.DbormHandler;
import org.dborm.core.test.utils.domain.SelectModel;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * 将查询结果映射到查询模板中
 * Created by shk
 */
public class SelectModelTest extends BaseSelectTest {


    @Test
    public void testSelectModel() {
        /* 注意:
            1、想要将值映射到属性名为bookId的属性上,列的别名必须是book_id,不能为bookId
            2、联合查询中如果多个表有相同的字段名称,最好将其中一个字段设置别名避免冲突,如果不设置,则只能接收到靠前的一个表对应的列的值
               如user_info表中存在字段名称为id的字段,book_info表中也存在字段名称为id的字段名称,
               如果连接查询添加为FROM user_info u left join book_info b on u.id = b.user_id
               接受查询结果的对象中有一个属性名为id的属性,则接收的id的值是user_info表的值
         */
        String sql = "SELECT u.*, b.id as book_id, b.name as book_name, b.* FROM user_info u left join book_info b on u.id = b.user_id";
        List<SelectModel> selectModels = DbormHandler.getDborm().getEntities(SelectModel.class, sql);
        assertNotNull(selectModels.get(0));
        for (SelectModel selectModel : selectModels) {
            assertEquals(USER_NICKNAME, selectModels.get(0).getNickname());
            assertEquals(USER_ID, selectModels.get(0).getId());
            assertEquals(BOOK_ID, selectModels.get(0).getBookId());

        }
    }


}
