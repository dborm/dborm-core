package org.dborm.core.test.utils.db;

import org.dborm.core.api.DbormDataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseManager extends DbormDataBase {


    /**
     * 获得数据库连接
     *
     * @return
     * @author COCHO
     */
    @Override
    public Object getConnection() {
        return createConnection();
    }

    public void closeConnection(Object connection) {
        if(connection != null){
            Connection con = (Connection) connection;
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获得数据库连接
     *
     * @return
     * @author COCHO
     * @time 2013-5-6上午10:46:44
     */
    private Connection createConnection() {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://rds3tmsxzi96h6921824.mysql.rds.aliyuncs.com:3306/dborm_test_db?useUnicode=true&characterEncoding=utf8";
        String username = "dborm";
        String password = "dborm_test";

        Connection conn = null;
        try {
            Class.forName(driver);// 加载驱动程序
            conn = DriverManager.getConnection(url, username, password);// 连续数据库
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

}
