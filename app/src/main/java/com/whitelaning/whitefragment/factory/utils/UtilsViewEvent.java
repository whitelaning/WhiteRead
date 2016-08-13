package com.whitelaning.whitefragment.factory.utils;

/**
 * Created by Whitelaning on 2016/8/5.
 * Email：whitelaning@qq.com
 */
public class UtilsViewEvent {
    private static long lastClickTime;

    /**
     * 用于判断是否是1秒钟内的重复点击
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean isFastDoubleClick2() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
