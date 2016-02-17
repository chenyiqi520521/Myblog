package zhexian.learn.cnblogs.comment;

import android.os.Bundle;

import java.util.List;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseSwipeListFragment;
import zhexian.learn.cnblogs.base.adapters.EfficientRecyclerAdapter;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = (ConfigConstant.CommentCategory) getArguments().getSerializable(CommentActivity.PARAM_CATEGORY);
        mDataID = getArguments().getLong(CommentActivity.PARAM_DATA_ID);
    }

    @Override
    protected EfficientRecyclerAdapter<CommentEntity> bindArrayAdapter(List<CommentEntity> list) {
        return new CommentAdapter(list);
    }

    @Override
    protected List<CommentEntity> loadData(int pageIndex, int pageSize) {
        return CommentDal.getCommentList(mBaseApp, mCategory, mDataID, pageIndex, pageSize);
    }

    @Override
    protected List<CommentEntity> loadDataFromDisk(int pageIndex, int pageSize) {
        return CommentDal.getCommentFromDisk(mCategory, mDataID, pageIndex, pageSize);
    }

    @Override
    protected CommentEntity getLoadMorePlaceHolder() {
        CommentEntity entity = new CommentEntity();
        entity.setEntityType(EfficientRecyclerAdapter.LOADING_MORE_ITEM);
        return entity;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.base_swipe_list;
    }

    @Override
    public void bindData() {
        onRefresh();
    }
}
