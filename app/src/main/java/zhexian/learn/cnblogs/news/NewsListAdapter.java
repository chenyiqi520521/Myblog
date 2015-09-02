package zhexian.learn.cnblogs.news;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseActivity;
import zhexian.learn.cnblogs.common.LoadingViewHolder;
import zhexian.learn.cnblogs.image.ZImage;
import zhexian.learn.cnblogs.util.ConfigConstant;


public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int NORMAL_ITEM = 0;
    private static final int GROUP_ITEM = 1;

    private BaseActivity mContext;
    private List<NewsListEntity> mDataList;
    private LayoutInflater mLayoutInflater;
    private int mImgSize;

    public NewsListAdapter(BaseActivity context, List<NewsListEntity> mDataList) {
        this.mContext = context;
        this.mDataList = mDataList;
        mLayoutInflater = LayoutInflater.from(mContext);
        mImgSize = mContext.getResources().getDimensionPixelSize(R.dimen.news_img_size);
    }

    /**
     * 渲染具体的ViewHolder
     *
     * @param viewGroup ViewHolder的容器
     * @param i         一个标志，我们根据该标志可以实现渲染不同类型的ViewHolder
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == ConfigConstant.ENTITY_TYPE_LOAD_MORE_PLACE_HOLDER)
            return new LoadingViewHolder(mLayoutInflater, viewGroup);

        if (i == NORMAL_ITEM)
            return new NormalItemHolder(mLayoutInflater.inflate(R.layout.news_list_item, viewGroup, false));
        else
            return new GroupItemHolder(mLayoutInflater.inflate(R.layout.news_list_group_item, viewGroup, false));
    }

    /**
     * 绑定ViewHolder的数据。
     *
     * @param viewHolder
     * @param i          数据源list的下标
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        NewsListEntity entity = mDataList.get(i);

        if (null == entity)
            return;

        if (viewHolder instanceof GroupItemHolder) {
            bindGroupItem(entity, (GroupItemHolder) viewHolder);
        } else if (viewHolder instanceof NormalItemHolder) {
            NormalItemHolder holder = (NormalItemHolder) viewHolder;
            bindNormalItem(entity, holder.newsTitle, holder.newsIcon);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * 决定元素的布局使用哪种类型
     *
     * @param position 数据源List的下标
     * @return 一个int型标志，传递给onCreateViewHolder的第二个参数
     */
    @Override
    public int getItemViewType(int position) {
        //第一个要显示时间
        if (position == 0)
            return GROUP_ITEM;

        NewsListEntity entity = mDataList.get(position);

        if (entity.getEntityType() == ConfigConstant.ENTITY_TYPE_LOAD_MORE_PLACE_HOLDER)
            return ConfigConstant.ENTITY_TYPE_LOAD_MORE_PLACE_HOLDER;

        String currentDate = entity.getPublishDate();
        int prevIndex = position - 1;
        boolean isDifferent = !mDataList.get(prevIndex).getPublishDate().equals(currentDate);
        return isDifferent ? GROUP_ITEM : NORMAL_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return mDataList.get(position).getNewsID();
    }

    void bindNormalItem(NewsListEntity entity, TextView newsTitle, ImageView newsIcon) {
        if (entity.getIconUrl().isEmpty()) {

            if (newsIcon.getVisibility() != View.GONE)
                newsIcon.setVisibility(View.GONE);
        } else {

            ZImage.ready().want(entity.getIconUrl()).reSize(mImgSize, mImgSize).into(newsIcon);

            if (newsIcon.getVisibility() != View.VISIBLE)
                newsIcon.setVisibility(View.VISIBLE);
        }
        newsTitle.setText(Html.fromHtml(entity.getTitle()));
    }

    void bindGroupItem(NewsListEntity entity, GroupItemHolder holder) {
        bindNormalItem(entity, holder.newsTitle, holder.newsIcon);
        holder.newsTime.setText(entity.getPublishDate());
    }

    void showNewsDetail(int pos) {
        NewsListEntity entity = mDataList.get(pos);
        NewsDetailActivity.actionStart(mContext, entity.getNewsID(), entity.getRecommendAmount(), entity.getCommentAmount(), entity.getTitle());
    }

    /**
     * 新闻标题
     */
    public class NormalItemHolder extends RecyclerView.ViewHolder {
        TextView newsTitle;
        ImageView newsIcon;

        public NormalItemHolder(View itemView) {
            super(itemView);
            newsTitle = (TextView) itemView.findViewById(R.id.news_item_title);
            newsIcon = (ImageView) itemView.findViewById(R.id.news_item_icon);
            itemView.findViewById(R.id.news_item_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNewsDetail(getPosition());
                }
            });
        }
    }

    /**
     * 带日期新闻标题
     */
    public class GroupItemHolder extends NormalItemHolder {
        TextView newsTime;

        public GroupItemHolder(View itemView) {
            super(itemView);
            newsTime = (TextView) itemView.findViewById(R.id.news_item_time);
        }
    }
}
