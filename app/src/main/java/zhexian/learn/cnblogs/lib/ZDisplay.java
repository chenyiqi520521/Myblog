package zhexian.learn.cnblogs.lib;


import android.util.DisplayMetrics;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseApplication;

/**
 * Created by Administrator on 2015/8/28.
 */
public class ZDisplay {
    private static ZDisplay mZDsiplay;
    private static int mScreenWidthPx;
    private static int mScreenHeightPx;
    private float mScale;
    private BaseApplication mContext;
    private int dayPrimaryColor;
    private int nightPrimaryColor;
    private int dayMinorColor;
    private int nightMinorColor;

    private ZDisplay(BaseApplication context) {
        this.mContext = context;
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        mScreenWidthPx = metrics.widthPixels;
        mScreenHeightPx = metrics.heightPixels;
        mScale = metrics.density;
        dayPrimaryColor = context.getResources().getColor(R.color.gray_dark);
        nightPrimaryColor = context.getResources().getColor(R.color.white_dark);
        dayMinorColor = context.getResources().getColor(R.color.gray);
        nightMinorColor = context.getResources().getColor(R.color.green_light);
    }

    public static void init(BaseApplication context) {
        if (mZDsiplay == null)
            mZDsiplay = new ZDisplay(context);
    }

    public static ZDisplay getInstance() {
        return mZDsiplay;
    }

    public static int getScreenWidthPx() {
        return mScreenWidthPx;
    }

    public static int getScreenHeightPx() {
        return mScreenHeightPx;
    }

    public int Dp2Px(float dp) {
        return (int) (dp * mScale + 0.5f);
    }

    public int Px2Dp(float px) {
        return (int) (px / mScale + 0.5f);
    }

    public int getFontColor(boolean isRead) {
        if (mContext.isNightMode()) {
            return isRead ? nightMinorColor : nightPrimaryColor;
        }
        return isRead ? dayMinorColor : dayPrimaryColor;
    }
}
