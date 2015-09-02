package zhexian.learn.cnblogs.blog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.image.ZImage;

/**
 * Created by 陈俊杰 on 2015/8/30.
 * 博客列表的适配器
 */
public class BlogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<BlogEntity> mDataList;
    private LayoutInflater mLayoutInflater;
    private int mAvatarSize;

    public BlogListAdapter(Context mContext, List<BlogEntity> dataList) {
        this.mContext = mContext;
        this.mDataList = dataList;
        mLayoutInflater = LayoutInflater.from(mContext);
        mAvatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.blog_author_avatar_size);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BlogViewHolder(mLayoutInflater.inflate(R.layout.blog_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final BlogEntity entity = mDataList.get(position);

        if (null == entity)
            return;

        BlogViewHolder blogHolder = (BlogViewHolder) holder;

        ZImage.ready().want(entity.getAuthorAvatar()).reSize(mAvatarSize, mAvatarSize).empty(R.mipmap.avatar_place_holder).into(blogHolder.imgUserAvatar);

        if (!TextUtils.isEmpty(entity.getTitle()))
            blogHolder.tvTitle.setText(Html.fromHtml(entity.getTitle()));

        blogHolder.tvDescription.setText(entity.getAuthorName());
        blogHolder.tvComment.setText(String.format("评 %d", entity.getCommentAmount()));
        blogHolder.tvLike.setText(String.format("赞 %d", entity.getRecommendAmount()));

        blogHolder.blogContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlogDetailActivity.actionStart(mContext, entity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public class BlogViewHolder extends RecyclerView.ViewHolder {
        View blogContainer;
        ImageView imgUserAvatar;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvComment;
        TextView tvLike;

        public BlogViewHolder(View itemView) {
            super(itemView);
            blogContainer = itemView.findViewById(R.id.blog_item_container);
            imgUserAvatar = (ImageView) itemView.findViewById(R.id.blog_item_avatar);
            tvTitle = (TextView) itemView.findViewById(R.id.blog_item_title);
            tvDescription = (TextView) itemView.findViewById(R.id.blog_item_description);
            tvComment = (TextView) itemView.findViewById(R.id.blog_item_comment);
            tvLike = (TextView) itemView.findViewById(R.id.blog_item_like);
        }
    }
}
