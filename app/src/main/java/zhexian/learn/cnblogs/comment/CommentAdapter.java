package zhexian.learn.cnblogs.comment;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseActivity;
import zhexian.learn.cnblogs.common.LoadingViewHolder;
import zhexian.learn.cnblogs.util.ConfigConstant;

/**
 * Created by Administrator on 2015/8/28.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private BaseActivity mContext;
    private List<CommentEntity> mDataList;
    private LayoutInflater mLayoutInflater;

    public CommentAdapter(BaseActivity mContext, List<CommentEntity> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ConfigConstant.ENTITY_TYPE_LOAD_MORE_PLACE_HOLDER)
            return new LoadingViewHolder(mLayoutInflater, parent);

        return new CommentViewHolder(mLayoutInflater.inflate(R.layout.base_comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CommentViewHolder)
            ((CommentViewHolder) holder).bind(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {

        CommentEntity entity = mDataList.get(position);

        if (entity.getEntityType() == ConfigConstant.ENTITY_TYPE_LOAD_MORE_PLACE_HOLDER)
            return ConfigConstant.ENTITY_TYPE_LOAD_MORE_PLACE_HOLDER;

        return ConfigConstant.ENTITY_TYPE_NORMAL_ITEM;

    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentAuthor;
        TextView commentTime;
        TextView commentContent;
        String authorUrl;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commentAuthor = (TextView) itemView.findViewById(R.id.comment_author);
            commentTime = (TextView) itemView.findViewById(R.id.comment_time);
            commentContent = (TextView) itemView.findViewById(R.id.comment_content);

            commentAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(authorUrl);
                    mContext.startActivity(intent);
                }
            });
        }

        public void bind(CommentEntity entity) {
            commentAuthor.setText(entity.getUserName());
            commentTime.setText(entity.getPublishTime());
            commentContent.setText(Html.fromHtml(entity.getContent()));
            authorUrl = entity.getUserHomeUrl();
        }
    }
}
