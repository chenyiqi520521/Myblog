package zhexian.learn.cnblogs.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import java.util.Date;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.lib.ZBroadcast;
import zhexian.learn.cnblogs.receiver.NetWorkChangeReceiver;
import zhexian.learn.cnblogs.util.ConfigConstant;

public class BaseActivity extends ActionBarActivity {

    private BaseApplication mBaseApp = null;
    private WindowManager mWindowManager = null;
    private View mNightView = null;
    private LayoutParams mNightViewParam;
    private ActionBar mActionbar;

    private boolean mIsAddedView;
    private int mPreviousDeltaY = -1;
    private boolean mIsActionbarHide;
    private Long mLastChangeTime;
    private BroadcastReceiver mNetWorkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBaseApp = (BaseApplication) getApplication();

        if (mBaseApp.isNightMode())
            setTheme(R.style.AppTheme_night);
        else
            setTheme(R.style.AppTheme_day);

        super.onCreate(savedInstanceState);

        mActionbar = getSupportActionBar();
        mIsAddedView = false;
        mLastChangeTime = new Date().getTime();

        if (mBaseApp.isNightMode()) {
            initNightView();
            mNightView.setBackgroundResource(R.color.night_mask);
        }

        mNetWorkChangeReceiver = new NetWorkChangeReceiver(getApp());
        ZBroadcast.registerNetworkStatusChange(this, mNetWorkChangeReceiver);
    }

    @Override
    protected void onDestroy() {
        if (mIsAddedView) {
            mBaseApp = null;
            mWindowManager.removeViewImmediate(mNightView);
            mWindowManager = null;
            mNightView = null;
        }
        ZBroadcast.unRegister(this, mNetWorkChangeReceiver);
        super.onDestroy();
    }

    public void switchActionBar(int deltaY) {

        if (Math.abs(deltaY) <= ConfigConstant.MIN_TRIGGER_ACTION_BAR_DISTANCE)
            return;

        if (deltaY * mPreviousDeltaY >= 0)
            return;

        long curChangeTime = new Date().getTime();

        if (curChangeTime - mLastChangeTime <= ConfigConstant.MIN_CHANGE_DURATION_MILLION_SECONDS)
            return;

        mLastChangeTime = curChangeTime;


        if (deltaY < 0 && mIsActionbarHide) {
            mActionbar.show();
            mIsActionbarHide = false;
        }

        if (deltaY > 0 && !mIsActionbarHide) {
            mActionbar.hide();
            mIsActionbarHide = true;
        }
        mPreviousDeltaY = deltaY;
    }

    public BaseApplication getApp() {
        return mBaseApp;
    }

    public void ChangeToDay() {
        mBaseApp.setIsNightMode(false);
        mNightView.setBackgroundResource(android.R.color.transparent);
    }

    public void ChangeToNight() {
        mBaseApp.setIsNightMode(true);
        initNightView();
        mNightView.setBackgroundResource(R.color.night_mask);
    }

    /**
     * wait a time until the onresume finish
     */
    public void recreateOnResume() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                recreate();
            }
        }, 100);
    }

    private void initNightView() {
        if (mIsAddedView == true)
            return;
        mNightViewParam = new LayoutParams(
                LayoutParams.TYPE_APPLICATION,
                LayoutParams.FLAG_NOT_TOUCHABLE | LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mNightView = new View(this);
        mWindowManager.addView(mNightView, mNightViewParam);
        mIsAddedView = true;
    }

}
