package com.whitelaning.whitefragment.factory.console;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    /**
     * 将开打的Activity添加进入activities
     * @param activity
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 将开打的Activity从activities中移除
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * 结束所有还没用被系统收回的Activity
     */
    public static void finishAllActivity() {
        for (Activity activity : activities) {
            activity.finish();
        }
        System.exit(0);//强制退出
    }

    /**
     * 结束所有还没用被系统收回的Activity,保留传入的Activity
     * @param activity
     */
    public static void finishAllActivity(Activity activity) {
        for (Activity mActivity : activities) {
            if (!mActivity.equals(activity)) {
                activity.finish();
            }
        }
    }

    /**
     * 结束所有还没用被系统收回的一组Activity
     * @param list
     */
    public static void finishActivityList(ArrayList<Class> list) {
        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i).getName();
            for (Activity activity : activities) {
                if (activity.getClass().getName().equals(name)) {
                    activity.finish();
                }
            }
        }
    }
}
