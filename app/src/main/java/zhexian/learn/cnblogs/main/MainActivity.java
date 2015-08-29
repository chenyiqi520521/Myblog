package zhexian.learn.cnblogs.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.View;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseActivity;
import zhexian.learn.cnblogs.image.ZImage;
import zhexian.learn.cnblogs.lib.ZDisplay;
import zhexian.learn.cnblogs.news.NewsListFragment;
import zhexian.learn.cnblogs.util.DBHelper;
import zhexian.learn.cnblogs.util.HtmlHelper;


public class MainActivity extends BaseActivity implements INavigatorCallback {
    private DrawerLayout mDrawerLayout;
    private View mNavigatorView;
    private boolean mIsNightMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIsNightMode = getApp().isNightMode();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerLayout);
        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);
        mNavigatorView = findViewById(R.id.main_navigator);
        NavigatorFragment navigatorFragment = (NavigatorFragment) getSupportFragmentManager().findFragmentById(R.id.main_navigator);
        navigatorFragment.InitDrawToggle(mDrawerLayout);

        if (getApp().getScreenWidth() == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            getApp().setScreenWidth(dm.widthPixels);
            getApp().setScreenHeight(dm.heightPixels);
            int screenWidth = ZDisplay.Px2Dp(getApp(), dm.widthPixels);
            getApp().setScreenWidthInDP(screenWidth);
        }

        ZImage.init(getApp());
        DBHelper.init(getApp().getFileRootDir());
        HtmlHelper.init(getApp());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsNightMode != getApp().isNightMode()) {
            recreateOnResume();
        }
    }

    @Override
    public void OpenNavigator() {
        mDrawerLayout.openDrawer(mNavigatorView);
    }

    @Override
    public void CloseNavigator() {
        mDrawerLayout.closeDrawer(mNavigatorView);
    }

    @Override
    public void OnClickNews() {
        ReplaceFragment(NewsListFragment.newInstance());
    }

    @Override
    public void OnClickOpenSource() {
        ReplaceFragment(NewsListFragment.newInstance());
    }

    public void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
    }
}

