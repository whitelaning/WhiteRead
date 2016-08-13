package com.whitelaning.whitefragment.factory.utils;

import android.content.Context;
import android.view.View;

public class UtilsDynamicSize {
    public static int defaultDisplayWidth;//屏幕的宽度
    public static int defaultDisplayHeight;//屏幕的高度

    static {
        init();
    }

    public static void setWidth(Context context, View view, int widthPx) {
        android.view.ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = widthPx;
    }

    public static void setHeight(Context context, View view, int heightPx) {
        android.view.ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.height = heightPx;
    }

    public static void setHeightAndWidth(Context context, View view, int heightPx, int widthPx) {
        android.view.ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.height = heightPx;
        lp.width = widthPx;
    }

    /**
     * 通过屏幕宽度和图片比例适配图片高度
     *
     * @param context
     * @param view
     */
    public static void adaptiveImageByScreenWidth(Context context, View view, int imageViewWidth, int imageViewHeight) {
        android.view.ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = defaultDisplayWidth;
        lp.height = defaultDisplayWidth * imageViewHeight / imageViewWidth;
    }

    private static void init() {
        defaultDisplayWidth = UtilsScreen.getWidth();
        defaultDisplayHeight = UtilsScreen.getHeight();
    }
}