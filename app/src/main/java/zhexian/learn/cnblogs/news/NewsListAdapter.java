package zhexian.learn.cnblogs.news;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.adapters.EfficientAdapter;
import zhexian.learn.cnblogs.base.adapters.EfficientRecyclerAdapter;
import zhexian.learn.cnblogs.base.adapters.EfficientViewHolder;
import zhexian.learn.cnblogs.image.ZImage;
import zhexian.learn.cnblogs.lib.ZDisplay;
import zhexian.learn.cnblogs.util.SQLiteHelper;


public class NewsListAdapter extends EfficientRecyclerAdapter<NewsListEntity> {
    private int mImgSize;

    public NewsListAdapter(List<NewsListEntity> objects) {
        super(objects);

        mImgSize = ZDisplay.getInstance().Dp2Px(90);

        setOnItemClickListener(new OnItemClickListener<NewsListEntity>() {
            @Override
            public void onItemClick(EfficientAdapter<NewsListEntity> adapter, View view, NewsListEntity object, int position) {
                if (object.getNewsID() > 0) {
                    NewsDetailActivity.actionStart((Activity) view.getContext(), object.getNewsID(), object.getRecommendAmount(), object.getCommentAmount(), object.getTitle());

                    if (!SQLiteHelper.getInstance().isReadNews(object.getNewsID()))
                        ((TextView) view.findViewById(R.id.news_item_title)).setTextColor(ZDisplay.getInstance().getFontColor(true));
                }
            }
        });
    }

    @Override
    public int getLayoutResId(int viewType) {
        switch (viewType) {
            case NORMAL_ITEM:
                return R.layout.news_list_item;
            case GROUP_ITEM:
                return R.layout.news_list_group_item;
            case LOADING_MORE_ITEM:
                return R.layout.base_swipe_item_loading;
        }
        throw new IllegalArgumentException("不支持类型为" + viewType + "的布局类型");
    }

    @Override
    public Class<? extends EfficientViewHolder<? extends NewsListEntity>> getViewHolderClass(int viewType) {
        switch (viewType) {
            case NORMAL_ITEM:
                return NormalItemHolder.class;
            case GROUP_ITEM:
                return GroupItemHolder.class;
            case LOADING_MORE_ITEM:
                return LoadingMoreViewHolder.class;
        }
        throw new IllegalArgumentException("不支持类型为" + viewType + "的元素类型");
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

        NewsListEntity entity = get(position);

        if (entity.getEntityType() == LOADING_MORE_ITEM)
            return LOADING_MORE_ITEM;

        String currentDate = entity.getPublishDate();
        int prevIndex = position - 1;
        boolean isGroup = !get(prevIndex).getPublishDate().equals(currentDate);
        return isGroup ? GROUP_ITEM : NORMAL_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return get(position).getNewsID();
    }


    /**
     * 新闻标题
     */
    public class NormalItemHolder extends EfficientViewHolder<NewsListEntity> {
        TextView newsTitle;
        ImageView newsIcon;

        public NormalItemHolder(View itemView) {
            super(itemView);
            newsTitle = findViewByIdEfficient(R.id.news_item_title);
            newsIcon = findViewByIdEfficient(R.id.news_item_icon);
        }

        @Override
        protected void updateView(Context context, NewsListEntity object) {
            if (TextUtils.isEmpty(object.getIconUrl())) {
                if (newsIcon.getVisibility() != View.GONE)
                    newsIcon.setVisibility(View.GONE);
            } else {
                ZImage.ready().want(object.getIconUrl()).reSize(mImgSize, mImgSize).into(newsIcon);

                if (newsIcon.getVisibility() != View.VISIBLE)
                    newsIcon.setVisibility(View.VISIBLE);
            }
            boolean isExist = SQLiteHelper.getInstance().isReadNews(object.getNewsID());
            newsTitle.setTextColor(ZDisplay.getInstance().getFontColor(isExist));
            newsTitle.setText(Html.fromHtml(object.getTitle()));
        }
    }

    /**
     * 带日期新闻标题
     */
    public class GroupItemHolder extends NormalItemHolder {
        public GroupItemHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void updateView(Context context, NewsListEntity object) {
            super.updateView(context, object);
            setText(R.id.news_item_time, object.getPublishDate());
        }
    }

    public class LoadingMoreViewHolder extends EfficientViewHolder<NewsListEntity> {

        public LoadingMoreViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void updateView(Context context, NewsListEntity object) {

        }

    }
}
