package cn.cocho.dborm.test.utils.db;

import cn.cocho.dborm.utils.DbormLogger;

import java.util.Date;

/**
 * 日志处理类
 *
 * @author COCHO
 * @date 2013-4-17 下午3:07:28
 */
public class DBLogger implements DbormLogger {

    private String commonTarget = "cn.cocho.dborm";


    /**
     * 调试信息
     *
     * @param msg
     * @return void
     * @author hsx
     * @time 2013-6-17下午04:12:48
     */
    public void debug(String msg) {
        System.out.println("日期：" + new Date() + " DEBUG  TARGET:" + commonTarget + "      " + "MSG:" + msg);

    }

    /**
     * 异常
     *
     * @param msg 目标类路径
     * @param e      异常对象
     * @author COCHO
     * @time 2013-4-22下午5:03:03
     */
    public void error(String msg, Throwable e) {
        System.out.println("日期：" + new Date() + " ERROR MSG:" + msg);
        e.printStackTrace();
    }

}
