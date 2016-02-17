package zhexian.learn.cnblogs.comment;

import android.content.Context;
import android.text.Html;
import android.view.View;

import java.util.List;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.adapters.EfficientAdapter;
import zhexian.learn.cnblogs.base.adapters.EfficientRecyclerAdapter;
import zhexian.learn.cnblogs.base.adapters.EfficientViewHolder;
import zhexian.learn.cnblogs.util.DoAction;

/**
 * Created by Administrator on 2015/8/28.
 */
public class CommentAdapter extends EfficientRecyclerAdapter<CommentEntity> {

    public CommentAdapter(List<CommentEntity> objects) {
        super(objects);

        setOnItemClickListener(new OnItemClickListener<CommentEntity>() {
            @Override
            public void onItemClick(EfficientAdapter<CommentEntity> adapter, View view, CommentEntity object, int position) {
                DoAction.jumpToWeb(view.getContext(), object.getUserHomeUrl());
            }
        });
    }

    @Override
    public int getLayoutResId(int viewType) {
        switch (viewType) {
            case NORMAL_ITEM:
                return R.layout.base_comment_item;
            case LOADING_MORE_ITEM:
                return R.layout.base_swipe_item_loading;
        }
        throw new IllegalArgumentException("不支持类型为" + viewType + "的布局类型");
    }

    @Override
    public Class<? extends EfficientViewHolder<? extends CommentEntity>> getViewHolderClass(int viewType) {
        switch (viewType) {
            case NORMAL_ITEM:
                return CommentViewHolder.class;
            case LOADING_MORE_ITEM:
                return LoadingMoreViewHolder.class;
        }
        throw new IllegalArgumentException("不支持类型为" + viewType + "的元素类型");
    }

    @Override
    public int getItemViewType(int position) {

        CommentEntity entity = get(position);

        if (entity.getEntityType() == LOADING_MORE_ITEM)
            return LOADING_MORE_ITEM;

        return NORMAL_ITEM;
    }

    public class CommentViewHolder extends EfficientViewHolder<CommentEntity> {
        public CommentViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void updateView(Context context, CommentEntity object) {
            setText(R.id.comment_author, object.getUserName());
            setText(R.id.comment_time, object.getPublishTime());
            setText(R.id.comment_content, Html.fromHtml(object.getContent()));
        }
    }

    public class LoadingMoreViewHolder extends EfficientViewHolder<CommentEntity> {

        public LoadingMoreViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void updateView(Context context, CommentEntity object) {

        }

    }
}
