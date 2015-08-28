package zhexian.learn.cnblogs.news;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import zhexian.learn.cnblogs.base.BaseActivity;
import zhexian.learn.cnblogs.base.BaseSwipeListFragment;
import zhexian.learn.cnblogs.ui.TabActionBarView;
import zhexian.learn.cnblogs.util.ConfigConstant;


/**
 * 新闻列表的UI
 */
public class NewsListFragment extends BaseSwipeListFragment<NewsListEntity> implements TabActionBarView.ITabActionCallback {

    private ConfigConstant.InfoCategory mCategory;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    private List<NewsListEntity> mList;
    private NewsListEntity mPlaceHolder;

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabActionBarView actionBarView = new TabActionBarView((BaseActivity) getActivity(), this);
        actionBarView.bindTab("精选", "最新");
        mPlaceHolder = new NewsListEntity();
        mPlaceHolder.setEntityType(ConfigConstant.ENTITY_TYPE_LOAD_MORE_PLACE_HOLDER);
    }

    @Override
    protected RecyclerView.Adapter<RecyclerView.ViewHolder> bindArrayAdapter(List<NewsListEntity> list) {
        mList = list;
        mAdapter = new NewsListAdapter((BaseActivity) getActivity(), mList);
        return mAdapter;
    }

    @Override
    protected List<NewsListEntity> loadData(int pageIndex, int pageSize) {
        List<NewsListEntity> list = NewsDal.getNewsList(mBaseApplication, mCategory, pageIndex, pageSize);

        if (mBaseApplication == null)
            return null;

        if (list != null && mCategory == ConfigConstant.InfoCategory.Recommend && mBaseApplication.isNetworkWifi() && mBaseApplication.isAutoLoadRecommend())
            new AsyncCacheNews().execute(list);

        return list;
    }

    @Override
    protected void onPreLoadMore() {
        mList.add(mPlaceHolder);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPostLoadMore() {
        mList.remove(mPlaceHolder);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFirstTabClick() {
        mCategory = ConfigConstant.InfoCategory.Recommend;
        onRefresh();
    }

    @Override
    public void onSecondTabClick() {
        mCategory = ConfigConstant.InfoCategory.Recent;
        onRefresh();
    }

    @Override
    public void onThirdClick() {

    }

    private class AsyncCacheNews extends AsyncTask<List<NewsListEntity>, Void, Void> {

        @Override
        protected Void doInBackground(List<NewsListEntity>... lists) {
            List<NewsListEntity> list = lists[0];

            for (NewsListEntity entity : list) {

                if (mBaseApplication.isNetworkWifi() == false)
                    break;

                NewsDal.CacheNews(entity.getNewsID());
            }
            return null;
        }
    }
}
