package zhexian.learn.cnblogs.comment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import zhexian.learn.cnblogs.base.BaseSwipeListFragment;
import zhexian.learn.cnblogs.util.ConfigConstant;

/**
 * Created by Administrator on 2015/8/28.
 */
public class CommentListFragment extends BaseSwipeListFragment<CommentEntity> {
    private ConfigConstant.CommentCategory mCategory;
    private long mDataID;

    public static CommentListFragment fragmentStart(ConfigConstant.CommentCategory category, long dataID) {
        CommentListFragment fragment = new CommentListFragment();
        Bundle args = new Bundle();
        args.putSerializable(CommentActivity.PARAM_CATEGORY, category);
        args.putLong(CommentActivity.PARAM_DATA_ID, dataID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCategory = (ConfigConstant.CommentCategory) getArguments().getSerializable(CommentActivity.PARAM_CATEGORY);
        mDataID = getArguments().getLong(CommentActivity.PARAM_DATA_ID);
        onRefresh();
    }

    @Override
    protected RecyclerView.Adapter<RecyclerView.ViewHolder> bindArrayAdapter(List<CommentEntity> list) {
        return new CommentAdapter(mBaseActionBarActivity, list);
    }

    @Override
    protected List<CommentEntity> loadData(int pageIndex, int pageSize) {
        return CommentDal.getCommentList(mBaseApplication, mCategory, mDataID, pageIndex, pageSize);
    }

    @Override
    protected void onPreLoadMore() {

    }

    @Override
    protected void onPostLoadMore() {

    }
}
