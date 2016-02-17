package zhexian.learn.cnblogs.main;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseActivity;


/**
 * Created by Administrator on 2015/8/28.
 */
public class NavigatorFragment extends Fragment implements View.OnClickListener {

    private static final String PARAM_IS_USER_LEARNED_NAVIGATOR = "PARAM_IS_USER_LEARNED_NAVIGATOR";

    private static final String PARAM_NAVIGATOR_SELECTED_POSITION = "PARAM_NAVIGATOR_SELECTED_POSITION";

    private BaseActivity mBaseActivity;
    private ListView mNavigatorItemList;
    private NavigatorArrayAdapter mNavigatorAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private INavigatorCallback mINavigatorCallBack;

    private int mCurrentSelectedPosition = 0;
    private boolean mIsUserLearnedNavigator = false;
    private boolean mIsFromSavedInstance = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mBaseActivity = (BaseActivity) activity;
        mINavigatorCallBack = (INavigatorCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsUserLearnedNavigator = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(PARAM_IS_USER_LEARNED_NAVIGATOR, false);

        if (savedInstanceState != null) {
            mIsFromSavedInstance = true;
            mCurrentSelectedPosition = savedInstanceState.getInt(PARAM_NAVIGATOR_SELECTED_POSITION);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_navigator, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNavigatorItemList = (ListView) view.findViewById(R.id.main_navigatorList);
        mNavigatorAdapter = new NavigatorArrayAdapter(getActivity(), R.layout.main_side_navigator_item, NavigatorDal.getInstance().getList());
        mNavigatorItemList.setAdapter(mNavigatorAdapter);

        mNavigatorItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSelectNavigator(position);
            }
        });

        mNavigatorItemList.setItemChecked(mCurrentSelectedPosition, true);
        View mSettingBtn = view.findViewById(R.id.navigator_setting);
        View mFavoriteBtn = view.findViewById(R.id.navigator_my_favorite);

        mSettingBtn.setOnClickListener(this);
        mFavoriteBtn.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PARAM_NAVIGATOR_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mINavigatorCallBack = null;
    }

    void onSelectNavigator(int position) {
        mCurrentSelectedPosition = position;
        mNavigatorAdapter.notifyDataSetChanged();

        if (mNavigatorItemList != null)
            mNavigatorItemList.setItemChecked(position, true);

        if (mINavigatorCallBack == null)
            return;

        mINavigatorCallBack.closeNavigator();
        int navigatorID = (int) mNavigatorAdapter.getItemId(position);

        switch (navigatorID) {
            case NavigatorDal.ID_NEWS:
                mINavigatorCallBack.onClickNews();
                break;
            case NavigatorDal.ID_BLOGS:
                mINavigatorCallBack.onClickBlog();
                break;
        }
    }

    void InitDrawToggle(DrawerLayout drawerLayout) {
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.mipmap.ic_drawer, R.string.open_navigator, R.string.close_navigator) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);


                if (mIsUserLearnedNavigator)
                    return;

                mIsUserLearnedNavigator = true;
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                sp.edit().putBoolean(PARAM_IS_USER_LEARNED_NAVIGATOR, true).apply();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        if (!mIsUserLearnedNavigator && !mIsFromSavedInstance)
            mINavigatorCallBack.openNavigator();

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        drawerLayout.setDrawerListener(mDrawerToggle);
        onSelectNavigator(mCurrentSelectedPosition);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.navigator_setting: {
                mINavigatorCallBack.closeNavigator();
                SettingActivity.actionStart(mBaseActivity);
            }
            break;

            case R.id.navigator_my_favorite:
                //   mINavigatorCallBack.closeNavigator();

                break;
        }
    }
}
