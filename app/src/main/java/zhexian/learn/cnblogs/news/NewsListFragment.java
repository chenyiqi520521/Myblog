package zhexian.learn.cnblogs.news;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseSwipeListFragment;
import zhexian.learn.cnblogs.base.adapters.EfficientRecyclerAdapter;
import zhexian.learn.cnblogs.image.ZImage;
import zhexian.learn.cnblogs.lib.ZDate;
import zhexian.learn.cnblogs.main.MainActivity;
import zhexian.learn.cnblogs.ui.TabActionBarView;
import zhexian.learn.cnblogs.util.ConfigConstant;
import zhexian.learn.cnblogs.util.DBHelper;


/**
 * 新闻列表的UI
 */
public class NewsListFragment extends BaseSwipeListFragment<NewsListEntity> implements TabActionBarView.ITabActionCallback {

    private ConfigConstant.NewsCategory mCategory;
    private TabActionBarView mActionBarView;

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.news_list;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActionBarView = (TabActionBarView) view.findViewById(R.id.title_tab_bar);
        mActionBarView.bindTab(this, "精选", "最新");
        final MainActivity activity = (MainActivity) getActivity();
        findViewById(R.id.title_left_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.switchNavigator();
            }
        });
    }

    @Override
    protected EfficientRecyclerAdapter<NewsListEntity> bindArrayAdapter(List<NewsListEntity> list) {
        return new NewsListAdapter(list);
    }

    @Override
    protected List<NewsListEntity> loadData(int pageIndex, int pageSize) {
        List<NewsListEntity> list = NewsDal.getNewsList(mBaseApp, mCategory, pageIndex, pageSize);

        if (mBaseApp == null)
            return null;

        if (list != null && mCategory == ConfigConstant.NewsCategory.Recommend && mBaseApp.isNetworkWifi() && mBaseApp.isAutoLoadRecommend())
            new AsyncCacheNews().execute(list);

        return list;
    }

    @Override
    protected List<NewsListEntity> loadDataFromDisk(int pageIndex, int pageSize) {
        return NewsDal.getNewsListFromDisk(mCategory, pageIndex, pageSize);
    }

    @Override
    protected NewsListEntity getLoadMorePlaceHolder() {
        NewsListEntity entity = new NewsListEntity();
        entity.setEntityType(EfficientRecyclerAdapter.LOADING_MORE_ITEM);
        return entity;
    }

    @Override
    public void onLeftTabClick() {
        mCategory = ConfigConstant.NewsCategory.Recommend;
        onRefresh();
    }

    @Override
    public void onMiddleTabClick() {

    }

    @Override
    public void onRightClick() {
        mCategory = ConfigConstant.NewsCategory.Recent;
        onRefresh();
    }

    @Override
    public void bindData() {
        mActionBarView.leftClick();
    }

    private class AsyncCacheNews extends AsyncTask<List<NewsListEntity>, Void, Void> {

        @Override
        protected Void doInBackground(List<NewsListEntity>... lists) {
            List<NewsListEntity> list = lists[0];

            for (NewsListEntity entity : list) {

                if (mBaseApp == null || mBaseApp.isNetworkWifi() == false)
                    break;

                if (entity == null)
                    continue;

                String key = String.format("news_content_%d", entity.getNewsID());

                if (DBHelper.cache().exist(key))
                    continue;

                ZImage.ready().want(entity.getIconUrl()).lowPriority().save();

                //自动缓存三天内的新闻
                boolean isNeedCache = entity.getPublishDate().equals(ZDate.TODAY_STRING) || entity.getPublishDate().equals(ZDate.YESTERDAY_STRING);

                if (isNeedCache)
                    NewsDal.CacheNews(entity.getNewsID(), key);
            }
            return null;
        }
    }
}
