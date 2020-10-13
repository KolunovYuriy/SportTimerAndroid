package com.taktilidu.sporttimer.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Юрий on 06.01.2016.
 */

public class Navigation {

    private static List navigationList = new ArrayList<String>();
    private static String currentPage = "";

    public static String getPrevious () {
        String result = "";
        exLog.i("navigationList.size() = "+navigationList.size());
        if (navigationList.size()>1) {
            result = (String) navigationList.get(navigationList.size()-2);
            navigationList.remove(navigationList.size()-1);
        }
        return result;
    }

    public static String getCurrentNavigationPage() {
        return currentPage;
    }

    public static void setCurrentNavigationPage(String sPage) {
        currentPage = sPage;
        navigationList.add(sPage);
    };
}