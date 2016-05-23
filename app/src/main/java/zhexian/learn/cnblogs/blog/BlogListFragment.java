package zhexian.learn.cnblogs.blog;

import android.os.Bundle;
import android.view.View;

import java.util.List;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseSwipeListFragment;
import zhexian.learn.cnblogs.base.adapters.EfficientRecyclerAdapter;
import zhexian.learn.cnblogs.main.MainActivity;
import zhexian.learn.cnblogs.ui.TabActionBarView;
import zhexian.learn.cnblogs.util.Constant;

/**
 * Created by 陈俊杰 on 2015/8/30.
 * 博客列表
 */
public class BlogListFragment extends BaseSwipeListFragment<BlogEntity> implements TabActionBarView.ITabActionCallback {
    Constant.BlogCategory mCategory = Constant.BlogCategory.HOME;
    private TabActionBarView mActionBarView;

    public static BlogListFragment newInstance() {
        return new BlogListFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.blogs_list;
    }

    @Override
    protected int getPageSize() {
        if (mCategory == Constant.BlogCategory.HOME)
            return super.getPageSize();
        else
            return 100;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActionBarView = (TabActionBarView) view.findViewById(R.id.title_tab_bar);
        mActionBarView.bindTab(this, "推荐", "热门", "首页");
        final MainActivity activity = (MainActivity) getActivity();

        findViewById(R.id.title_left_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.switchNavigator();
            }
        });
    }

    @Override
    protected EfficientRecyclerAdapter<BlogEntity> bindArrayAdapter(List<BlogEntity> list) {
        return new BlogListAdapter(list);
    }

    @Override
    protected List<BlogEntity> loadData(int pageIndex, int pageSize) {

        if (mBaseApp == null)
            return null;

        //分页获取首页信息
        if (mCategory == Constant.BlogCategory.HOME)
            return BlogDal.getHomeBlogs(mBaseApp, pageIndex, pageSize);

        //获取全部热门信息
        if (mCategory == Constant.BlogCategory.HOT)
            return BlogDal.getHotBlogs(mBaseApp);

        //获取全部推荐信息
        if (mCategory == Constant.BlogCategory.RECOMMEND)
            return BlogDal.getRecommendBlogs(mBaseApp);

        return null;
    }

    @Override
    protected List<BlogEntity> loadDataFromDisk(int pageIndex, int pageSize) {
        return BlogDal.getBlogsFromDisk(mCategory, pageIndex, pageSize);
    }

    @Override
    protected BlogEntity getLoadMorePlaceHolder() {
        if (mCategory != Constant.BlogCategory.HOME)
            return null;

        BlogEntity entity = new BlogEntity();
        entity.setEntityType(EfficientRecyclerAdapter.LOADING_MORE_ITEM);
        return entity;
    }

    @Override
    public void onLeftTabClick() {
        mCategory = Constant.BlogCategory.RECOMMEND;
        onRefresh();
    }

    @Override
    public void onMiddleTabClick() {
        mCategory = Constant.BlogCategory.HOT;
        onRefresh();
    }

    @Override
    public void onRightClick() {
        mCategory = Constant.BlogCategory.HOME;
        onRefresh();
    }


    @Override
    public void bindData() {
        mActionBarView.leftClick();
    }
}
