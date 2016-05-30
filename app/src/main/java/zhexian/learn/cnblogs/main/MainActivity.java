package zhexian.learn.cnblogs.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseActivity;
import zhexian.learn.cnblogs.base.BaseSwipeListFragment;
import zhexian.learn.cnblogs.blog.BlogListFragment;
import zhexian.learn.cnblogs.image.ZImage;
import zhexian.learn.cnblogs.lib.ZDisplay;
import zhexian.learn.cnblogs.news.NewsListFragment;
import zhexian.learn.cnblogs.util.Constant;
import zhexian.learn.cnblogs.util.DBHelper;
import zhexian.learn.cnblogs.util.DisplayUtil;
import zhexian.learn.cnblogs.util.HtmlHelper;
import zhexian.learn.cnblogs.util.SQLiteHelper;


public class MainActivity extends BaseActivity implements INavigatorCallback {
    private static final int STATE_NEWS = 1;
    private static final int STATE_BLOG = 2;
    private DrawerLayout mDrawerLayout;
    private View mNavigatorView;
    private int currentState = -1;
    private Fragment currentFragment;
    private SparseArray<Fragment> fragmentArray;
    private boolean mIsNavigatorOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayUtil.init(this);
        fragmentArray = new SparseArray<>(2);

        addFragment(STATE_BLOG);
        addFragment(STATE_NEWS);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerLayout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mNavigatorView = findViewById(R.id.main_navigator);
        NavigatorFragment navigatorFragment = (NavigatorFragment) getSupportFragmentManager().findFragmentById(R.id.main_navigator);
        navigatorFragment.InitDrawToggle(mDrawerLayout);

        ZDisplay.init(getApp());

        if (getApp().getScreenWidth() == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            getApp().setScreenWidth(dm.widthPixels);
            getApp().setScreenHeight(dm.heightPixels);
            int screenWidth = ZDisplay.getInstance().Px2Dp(dm.widthPixels);
            getApp().setScreenWidthInDP(screenWidth);
        }

        ZImage.init(getApp());
        DBHelper.init(getApp().getFileRootDir());
        SQLiteHelper.Init(getApp());
        HtmlHelper.init(getApp());

        getApp().autoCleanCache(Constant.CACHE_AVAILABLE_DAYS);
    }

    private void addFragment(int state) {
        if (fragmentArray.get(state) == null) {
            Fragment fragment;

            switch (state) {
                case STATE_NEWS:
                    fragment = NewsListFragment.newInstance();
                    break;
                case STATE_BLOG:
                    fragment = BlogListFragment.newInstance();
                    break;
                default:
                    throw new IllegalArgumentException("指定类型 " + state + " 的fragment的类型没用命中，出错位置MainActivity=>addFragment()");
            }
            fragmentArray.put(state, fragment);
        }
    }

    @Override
    public void openNavigator() {
        mDrawerLayout.openDrawer(mNavigatorView);
        mIsNavigatorOpened = true;
    }

    @Override
    public void closeNavigator() {
        mDrawerLayout.closeDrawer(mNavigatorView);
        mIsNavigatorOpened = false;
    }

    public void switchNavigator() {
        if (mIsNavigatorOpened)
            closeNavigator();
        else
            openNavigator();

        mIsNavigatorOpened = !mIsNavigatorOpened;
    }

    @Override
    public void onClickNews() {
        if (currentState == STATE_NEWS)
            return;

        currentState = STATE_NEWS;
        Fragment fragment = fragmentArray.get(STATE_NEWS);
        ReplaceFragment(fragment);
    }

    @Override
    public void onClickBlog() {
        if (currentState == STATE_BLOG)
            return;

        currentState = STATE_BLOG;
        Fragment fragment = fragmentArray.get(STATE_BLOG);
        ReplaceFragment(fragment);
    }

    public void ReplaceFragment(Fragment fragment) {
        if (currentFragment != null && currentFragment instanceof BaseSwipeListFragment)
            ((BaseSwipeListFragment) currentFragment).cancelLoadingTask();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).commit();
        currentFragment = fragment;
    }
}

