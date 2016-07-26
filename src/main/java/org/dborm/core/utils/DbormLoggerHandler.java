package org.dborm.core.utils;

import org.dborm.core.api.DbormLogger;

import java.util.Date;

/**
 * 日志处理类的默认实现类
 *
 * @author COCHO
 * @date 2013-4-17 下午3:07:28
 */
public class DbormLoggerHandler implements DbormLogger {


    @Override
    public void debug(String msg) {
        System.out.println("日期：" + new Date() + " DEBUG  TARGET:" + commonTarget + " " + "MSG:    " + msg);
    }

    @Override
    public void error(Throwable e) {
        System.out.println("日期：" + new Date() + " ERROR");
        e.printStackTrace();
    }


    @Override
    public void error(String msg, Throwable e) {
        System.out.println("日期：" + new Date() + " ERROR MSG:    " + msg);
        e.printStackTrace();
    }

}
