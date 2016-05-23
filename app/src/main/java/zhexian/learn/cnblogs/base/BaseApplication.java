package zhexian.learn.cnblogs.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import zhexian.learn.cnblogs.base.permission.RxPermissions;
import zhexian.learn.cnblogs.lib.ZDate;
import zhexian.learn.cnblogs.util.Constant;
import zhexian.learn.cnblogs.util.DBHelper;
import zhexian.learn.cnblogs.util.SQLiteHelper;
import zhexian.learn.cnblogs.util.Utils;

/**
 * Created by Administrator on 2015/5/6.
 */
public class BaseApplication extends Application {
    private static final String PARAM_IS_AUTO_LOAD_RECOMMEND = "PARAM_IS_AUTO_LOAD_RECOMMEND";
    private static final String PARAM_IS_NIGHT_MODE = "PARAM_IS_NIGHT_MODE";
    private static final String PARAM_IS_IMG_ONLY_WIFI = "PARAM_IS_IMG_ONLY_WIFI";
    private static final String PARAM_PAGE_SIZE = "PARAM_PAGE_SIZE";
    private static final String PARAM_IS_BIG_FONT = "PARAM_IS_BIG_FONT";
    private static final String PARAM_IMAGE_POOL_SIZE = "PARAM_IMAGE_POOL_SIZE";
    private static final String PARAM_SCREEN_WIDTH = "PARAM_SCREEN_WIDTH";
    private static final String PARAM_SCREEN_HEIGHT = "PARAM_SCREEN_HEIGHT";
    private static final String PARAM_SCREEN_WIDTH_DP = "PARAM_SCREEN_WIDTH_DP";
    private static final String PARAM_LAST_MODIFY_DAYS = "PARAM_LAST_MODIFY_DAYS";

    private SharedPreferences mSp;
    private int mImageCachePoolSize;
    private boolean mIsNightMode;
    private boolean mIsImgOnlyWifi;
    private boolean mIsAutoLoadRecommend;
    private boolean mIsBigFont;
    private int mPageSize;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mLastModifyDays;
    private Constant.NetworkStatus mNetWorkStatus;
    private int mScreenWidthDP;
    private String mFileRootDir;

    @Override
    public void onCreate() {
        super.onCreate();
        RxPermissions.init(this);
        mSp = PreferenceManager.getDefaultSharedPreferences(this);
        mIsAutoLoadRecommend = mSp.getBoolean(PARAM_IS_AUTO_LOAD_RECOMMEND, true);
        mIsNightMode = mSp.getBoolean(PARAM_IS_NIGHT_MODE, false);
        mPageSize = mSp.getInt(PARAM_PAGE_SIZE, 15);
        mIsImgOnlyWifi = mSp.getBoolean(PARAM_IS_IMG_ONLY_WIFI, false);
        mIsBigFont = mSp.getBoolean(PARAM_IS_BIG_FONT, false);
        mNetWorkStatus = Utils.GetConnectType(this);
        mImageCachePoolSize = mSp.getInt(PARAM_IMAGE_POOL_SIZE, 0);

        mFileRootDir = Utils.getWritePath(this) + "/";
        mScreenWidth = mSp.getInt(PARAM_SCREEN_WIDTH, 0);
        mScreenHeight = mSp.getInt(PARAM_SCREEN_HEIGHT, 0);
        mScreenWidthDP = mSp.getInt(PARAM_SCREEN_WIDTH_DP, 0);
        mLastModifyDays = mSp.getInt(PARAM_LAST_MODIFY_DAYS, ZDate.getCurrentDate());

        if (mImageCachePoolSize == 0)
            setImageCachePoolSize();
    }

    public boolean isAutoLoadRecommend() {
        return mIsAutoLoadRecommend;
    }

    public void setIsAutoLoadRecommend(boolean isAutoLoadRecommend) {
        if (mIsAutoLoadRecommend == isAutoLoadRecommend)
            return;

        mIsAutoLoadRecommend = isAutoLoadRecommend;
        mSp.edit().putBoolean(PARAM_IS_AUTO_LOAD_RECOMMEND, mIsAutoLoadRecommend).apply();
    }

    public boolean isBigFont() {
        return mIsBigFont;
    }

    public void setIsBigFont(boolean isBigFont) {
        if (mIsBigFont == isBigFont)
            return;

        mIsBigFont = isBigFont;
        mSp.edit().putBoolean(PARAM_IS_BIG_FONT, mIsBigFont).apply();
    }

    public boolean isNightMode() {
        return mIsNightMode;
    }

    public void setIsNightMode(boolean isNightMode) {
        if (mIsNightMode == isNightMode)
            return;

        mIsNightMode = isNightMode;
        mSp.edit().putBoolean(PARAM_IS_NIGHT_MODE, mIsNightMode).apply();
    }

    public int getPageSize() {
        return mPageSize;
    }

    public void setPageSize(int pageSize) {
        if (mPageSize == pageSize)
            return;

        mPageSize = pageSize;
        mSp.edit().putInt(PARAM_PAGE_SIZE, mPageSize).apply();
    }

    public boolean isImgOnlyWifi() {
        return mIsImgOnlyWifi;
    }

    public void setIsImgOnlyWifi(boolean isImgOnlyWifi) {
        if (mIsImgOnlyWifi == isImgOnlyWifi)
            return;

        mIsImgOnlyWifi = isImgOnlyWifi;
        mSp.edit().putBoolean(PARAM_IS_IMG_ONLY_WIFI, mIsImgOnlyWifi).apply();
    }

    public Constant.NetworkStatus getNetworkStatus() {
        return mNetWorkStatus;
    }

    public void setNetworkStatus(Constant.NetworkStatus mNetworkStatus) {
        this.mNetWorkStatus = mNetworkStatus;
    }

    public boolean isNetworkAvailable() {
        return mNetWorkStatus != Constant.NetworkStatus.DisConnect;
    }

    public boolean isNetworkWifi() {
        return mNetWorkStatus == Constant.NetworkStatus.Wifi;
    }

    public boolean canRequestImage() {
        return mNetWorkStatus == Constant.NetworkStatus.Wifi ||
                (mNetWorkStatus == Constant.NetworkStatus.Mobile && !mIsImgOnlyWifi);
    }


    public int getScreenWidthInDP() {
        return mScreenWidthDP;
    }

    public void setScreenWidthInDP(int screenWidthInDP) {
        if (mScreenWidthDP == screenWidthInDP)
            return;

        mScreenWidthDP = screenWidthInDP;
        mSp.edit().putInt(PARAM_SCREEN_WIDTH_DP, mScreenWidthDP).apply();
    }

    public String getFileRootDir() {
        return mFileRootDir;
    }

    public int getImageCachePoolSize() {
        return mImageCachePoolSize;
    }

    private void setImageCachePoolSize() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        mImageCachePoolSize = activityManager.getMemoryClass() * 1024 * 1024 / 8;
        mSp.edit().putInt(PARAM_IMAGE_POOL_SIZE, mImageCachePoolSize).apply();
    }


    public int getScreenWidth() {
        return mScreenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        if (mScreenWidth == screenWidth)
            return;

        mScreenWidth = screenWidth;
        mSp.edit().putInt(PARAM_SCREEN_WIDTH, mScreenWidth).apply();
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        if (mScreenHeight == screenHeight)
            return;

        mScreenHeight = screenHeight;
        mSp.edit().putInt(PARAM_SCREEN_HEIGHT, mScreenHeight).apply();
    }

    /**
     * 自动清理过期缓存,每个指定天执行一次，每次清理指定天前的文件
     */
    public void autoCleanCache(int availableDays) {
        int currentDays = ZDate.getCurrentDate();

        if (ZDate.daysOfTwo(mLastModifyDays, currentDays) < availableDays)
            return;

        DBHelper.cache().cleanExpireFile(availableDays);
        SQLiteHelper.getInstance().cleanHistory(availableDays);
        mLastModifyDays = currentDays;
        mSp.edit().putInt(PARAM_LAST_MODIFY_DAYS, mLastModifyDays).apply();
    }
}
