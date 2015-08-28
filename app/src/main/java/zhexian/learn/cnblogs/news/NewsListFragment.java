package zhexian.learn.cnblogs.news;

/**
 * Created by Administrator on 2015/8/28.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import zhexian.learn.cnblogs.base.BaseActivity;
import zhexian.learn.cnblogs.base.BaseSwipeListFragment;
import zhexian.learn.cnblogs.ui.ITabActionCallback;
import zhexian.learn.cnblogs.ui.TabActionBarView;
import zhexian.learn.cnblogs.util.ConfigConstant;


/**
 * 新闻列表的UI
 */
public class NewsListFragment extends BaseSwipeListFragment<NewsListEntity> implements ITabActionCallback {

    private ConfigConstant.InfoCategory mCategory;

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabActionBarView actionBarView = new TabActionBarView((BaseActivity) getActivity(), this);
        actionBarView.bindTab("精选", "最新");
    }

    @Override
    protected RecyclerView.Adapter<RecyclerView.ViewHolder> bindArrayAdapter(List<NewsListEntity> list) {
        return new NewsListAdapter((BaseActivity) getActivity(), list);
    }

    @Override
    protected List<NewsListEntity> loadData(int pageIndex, int pageSize) {
        List<NewsListEntity> list = NewsDal.getNewsList(mBaseApplication, mCategory, pageIndex, pageSize);

        if (list != null && mCategory == ConfigConstant.InfoCategory.Recommend && mBaseApplication.isNetworkWifi() && mBaseApplication.isAutoLoadRecommend())
            new AsyncCacheNews().execute(list);

        return list;
    }

    @Override
    public void onFirstTabClick() {
        mCategory = ConfigConstant.InfoCategory.Recommend;
        showLoadingIndicatorTask();
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
