package zhexian.learn.cnblogs.main;

/**
 * Created by Administrator on 2015/8/28.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseActivity;
import zhexian.learn.cnblogs.base.BaseApplication;
import zhexian.learn.cnblogs.util.DBHelper;
import zhexian.learn.cnblogs.util.Utils;


public class SettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private CheckBox mCKBIsAutoLoadNews;
    private CheckBox mCKBImageOnlyWifi;
    private CheckBox mCKBIsBigFont;
    private CheckBox mCKBIsNightMode;
    private View mViewCleanCache;
    private TextView mTVCacheSize;
    private BaseApplication baseApp;
    private View mCleanCacheProgress;
    private boolean mIsDoTask = false;
    private String emptyStr;
    private boolean mIsCleanAll = false;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected boolean isSwipeToClose() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        emptyStr = getResources().getString(R.string.dir_empty);
        baseApp = getApp();

        mCKBIsAutoLoadNews = (CheckBox) findViewById(R.id.setting_is_auto_load_news);
        mCKBIsAutoLoadNews.setChecked(baseApp.isAutoLoadRecommend());
        mCKBIsAutoLoadNews.setOnCheckedChangeListener(this);

        mCKBImageOnlyWifi = (CheckBox) findViewById(R.id.setting_is_image_only_wifi);
        mCKBImageOnlyWifi.setChecked(baseApp.isImgOnlyWifi());
        mCKBImageOnlyWifi.setOnCheckedChangeListener(this);

        mCKBIsBigFont = (CheckBox) findViewById(R.id.setting_is_big_font);
        mCKBIsBigFont.setChecked(baseApp.isBigFont());
        mCKBIsBigFont.setOnCheckedChangeListener(this);

        mCKBIsNightMode = (CheckBox) findViewById(R.id.setting_is_night_mode);
        mCKBIsNightMode.setChecked(baseApp.isNightMode());
        mCKBIsNightMode.setOnCheckedChangeListener(this);

        mViewCleanCache = findViewById(R.id.setting_clean_cache);
        mViewCleanCache.setOnClickListener(this);

        mTVCacheSize = (TextView) findViewById(R.id.setting_cache_size);
        String sizeText = DBHelper.cache().getDirSize();
        mTVCacheSize.setText(sizeText);
        mIsCleanAll = sizeText.equals(emptyStr);
        mCleanCacheProgress = findViewById(R.id.setting_cache_progress);
        findViewById(R.id.title_left_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        switch (id) {
            case R.id.setting_is_auto_load_news:
                baseApp.setIsAutoLoadRecommend(b);
                break;
            case R.id.setting_is_image_only_wifi:
                baseApp.setIsImgOnlyWifi(b);
                break;
            case R.id.setting_is_big_font:
                baseApp.setIsBigFont(b);
                break;
            case R.id.setting_is_night_mode:
                changeViewMode();
                break;
        }
    }

    void changeViewMode() {
        boolean isNight = getApp().isNightMode();
        if (isNight)
            ChangeToDay();
        else
            ChangeToNight();

        recreate();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.setting_clean_cache:
                onCleanCacheClick();
                break;
        }
    }

    void onCleanCacheClick() {

        if (mIsCleanAll) {
            Utils.toast(this, R.string.alert_is_cleaned);
            return;
        }

        if (mIsDoTask)
            return;

        new CleanCacheTask().execute();
    }

    class CleanCacheTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mIsDoTask = true;
            mCleanCacheProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DBHelper.cache().clean();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mIsDoTask = false;
            mIsCleanAll = true;
            mCleanCacheProgress.setVisibility(View.GONE);
            mTVCacheSize.setText(emptyStr);
            Utils.toast(baseApp, R.string.alert_is_cleaned);
        }
    }
}
