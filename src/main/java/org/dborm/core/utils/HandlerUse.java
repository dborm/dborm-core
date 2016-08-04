package org.dborm.core.utils;

/**
 * Created by shk
 * 16/8/4 13:50
 */
public class HandlerUse {


    public void test() {
        int i = 0;
        for (i = 0; i < 3000; i++) {
            System.out.println(Thread.currentThread().getName() + " ----" + i);
        }
    }

}
