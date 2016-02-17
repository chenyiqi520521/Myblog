package zhexian.learn.cnblogs.base;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.adapters.EfficientRecyclerAdapter;
import zhexian.learn.cnblogs.ui.PullToRefreshView;
import zhexian.learn.cnblogs.util.Utils;


/**
 * 下拉刷新列表View的基类
 * 提供了下拉刷新、上拉加载数据的功能
 */
public abstract class BaseSwipeListFragment<DataEntity extends BaseEntity> extends BaseFragment
        implements PullToRefreshView.OnRefreshListener {
    protected BaseApplication mBaseApp;
    protected BaseActivity mBaseActivity;
    private ZOnScrollListener mRecyclerViewScrollListener;
    private PullToRefreshView mPullToRefresh;
    private RecyclerView mRecyclerView;

    private EfficientRecyclerAdapter<DataEntity> mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private List<DataEntity> mDataList;
    private boolean mIsRequestingData = false;
    private boolean mIsLoadAllData = false;
    private boolean mIsCancelTask = false;
    private DataEntity mLoadMorePlaceHolder;
    private AsyncLoadDataTask mLoadTask;

    /**
     * 绑定列表的数据源
     */
    protected abstract EfficientRecyclerAdapter<DataEntity> bindArrayAdapter(List<DataEntity> list);

    /**
     * 获取数据，具体是从缓存中获取，还是从网络中获取，取决于子类决策
     * 比如新闻类缓存之后一般不变的，博客类的设置缓存时间
     *
     * @param pageIndex 页数，遵循博客园api标准，从1开始
     * @return 数据列表
     */
    protected abstract List<DataEntity> loadData(int pageIndex, int pageSize);

    /**
     * 从磁盘中获取数据，避免列表空白
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    protected abstract List<DataEntity> loadDataFromDisk(int pageIndex, int pageSize);

    protected abstract DataEntity getLoadMorePlaceHolder();

    /**
     * 获取每页的数据条数，子类可以重写
     *
     * @return
     */
    protected int getPageSize() {
        return mBaseApp.getPageSize();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseActivity = (BaseActivity) getActivity();
        mBaseApp = mBaseActivity.getApp();
        mLoadMorePlaceHolder = getLoadMorePlaceHolder();
        mIsRequestingData = false;
        mIsLoadAllData = false;
        mIsCancelTask = false;
        mDataList = new ArrayList<>();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mPullToRefresh = (PullToRefreshView) view.findViewById(R.id.base_swipe_container);
        mPullToRefresh.setTextColor(mBaseApp.isNightMode() ? R.color.green_light : R.color.gray);
        mPullToRefresh.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.base_swipe_list);
        mLinearLayoutManager = new LinearLayoutManager(mBaseActivity);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerViewScrollListener = new ZOnScrollListener();
        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);

        mAdapter = bindArrayAdapter(mDataList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        //正在请求数据中就不处理刷新事件了
        if (mIsRequestingData)
            return;

        mPullToRefresh.changeStatus(PullToRefreshView.STATUS_IDLE);
        startLoadTask(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerView.removeOnScrollListener(mRecyclerViewScrollListener);
        mLoadMorePlaceHolder = null;
        mLoadTask = null;
        mDataList = null;
        mBaseActivity = null;
        mBaseApp = null;
    }

    public boolean isNetworkAvailable() {
        return mBaseApp.isNetworkAvailable();
    }

    public void cancelLoadingTask() {
        if (mLoadTask != null && !mLoadTask.isCancelled()) {
            mLoadTask.cancel(true);
            mPullToRefresh.changeStatus(PullToRefreshView.STATUS_IDLE);
        }

        mIsCancelTask = true;
    }

    private int getNextPageIndex() {
        return mDataList.size() / getPageSize() + 1;
    }

    private void onPreLoadMore() {
        if (mLoadMorePlaceHolder == null)
            return;

        mDataList.add(mLoadMorePlaceHolder);
        mAdapter.notifyDataSetChanged();
    }

    private void onPostLoadMore() {
        if (mLoadMorePlaceHolder == null)
            return;

        mDataList.remove(mLoadMorePlaceHolder);
        mAdapter.notifyDataSetChanged();
    }

    private void startLoadTask(boolean isRefresh) {
        if (mLoadTask != null && !mLoadTask.isCancelled())
            mLoadTask.cancel(true);

        mLoadTask = new AsyncLoadDataTask(isRefresh, BaseSwipeListFragment.this);
        mLoadTask.execute();
    }

    /**
     * 异步载入请求列表的数据
     * 第一个参数，true：刷新列表 false：追加数据
     */
    private static class AsyncLoadDataTask<T> extends AsyncTask<Object, Void, List<T>> {
        boolean isRefresh = false;
        int pageIndex = 1;
        WeakReference<BaseSwipeListFragment> swipeFragment;

        public AsyncLoadDataTask(boolean isRefresh, BaseSwipeListFragment baseSwipeListFragment) {
            this.isRefresh = isRefresh;
            swipeFragment = new WeakReference<>(baseSwipeListFragment);
        }

        @Override
        protected void onPreExecute() {
            BaseSwipeListFragment fragment = swipeFragment.get();

            if (fragment == null)
                return;

            fragment.mIsRequestingData = true;
            fragment.mIsLoadAllData = false;

            if (isRefresh) {
                //没数据，默认展示本地
                if (fragment.mDataList.isEmpty()) {

                    List<T> dataList = fragment.loadDataFromDisk(1, fragment.getPageSize());

                    if (dataList != null && !dataList.isEmpty()) {
                        fragment.mDataList.addAll(dataList);
                        fragment.mAdapter.notifyDataSetChanged();
                    }
                }
                fragment.mPullToRefresh.changeStatus(PullToRefreshView.STATUS_REFRESHING);
            } else
                fragment.onPreLoadMore();
        }

        @Override
        protected List<T> doInBackground(Object... params) {
            BaseSwipeListFragment fragment = swipeFragment.get();

            if (fragment == null)
                return null;

            //activity重建时，提前返回
            if (fragment.getActivity() == null || fragment.mIsCancelTask) {
                cancel(true);
                return null;
            }

            pageIndex = isRefresh ? 1 : fragment.getNextPageIndex();
            return fragment.loadData(pageIndex, fragment.getPageSize());
        }

        @Override
        protected void onPostExecute(List<T> baseBusinessListEntity) {
            BaseSwipeListFragment fragment = swipeFragment.get();

            if (fragment == null)
                return;

            //activity重建时，提前返回
            if (fragment.getActivity() == null || fragment.mIsCancelTask) {
                cancel(true);
                return;
            }

            fragment.mIsRequestingData = false;
            if (baseBusinessListEntity == null) {

                if (swipeFragment.get().isNetworkAvailable())
                    Utils.toast(fragment.mBaseApp, R.string.alert_error);
                else
                    Utils.toast(fragment.mBaseApp, R.string.net_work_disconnected);

                if (isRefresh)
                    fragment.mPullToRefresh.changeStatus(PullToRefreshView.STATUS_REFRESH_FAIL);
                else
                    fragment.onPostLoadMore();

                return;
            }

            if (isRefresh) {
                fragment.mLinearLayoutManager.scrollToPosition(0);
                fragment.mPullToRefresh.changeStatus(PullToRefreshView.STATUS_REFRESH_SUCCESS);
            } else
                fragment.onPostLoadMore();

            if (baseBusinessListEntity.size() < fragment.getPageSize()) {
                fragment.mIsLoadAllData = true;
            }

            if (isRefresh)
                fragment.mDataList.clear();

            fragment.mDataList.addAll(baseBusinessListEntity);
            fragment.mAdapter.notifyDataSetChanged();
        }
    }

    private class ZOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mBaseActivity.switchActionBar(dy);

            if (mIsRequestingData)
                return;

            if (mDataList.size() == 0)
                return;

            int lastVisibleItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

            if (lastVisibleItem == mDataList.size() - 1) {

                if (mIsLoadAllData) {
                    if (dy > 0)
                        Utils.toast(mBaseApp, getResources().getString(R.string.alert_load_all_load));
                } else
                    startLoadTask(false);

            }
        }
    }
}
