package zhexian.learn.cnblogs.base.adapters;


import java.util.List;

/**
 * 支持空界面的RecyclerView
 * 用于保证在ViewPager下，AppBar能有一个提供滑动的控件，来支持标题栏的下拉、收起
 * Created by 陈俊杰 on 2016/1/19.
 */
public abstract class EmptyRecyclerViewAdapter<T extends M_EmptyHolderFeature> extends EfficientRecyclerAdapter<T> {

    private static final int EMPTY_ITEM = -1;

    public EmptyRecyclerViewAdapter(List<T> objects) {
        super(objects);
    }

    /**
     * 获取空页面的资源Id
     *
     * @return
     */
    protected abstract int getEmptyLayoutResId();

    /**
     * 创建一个空的类
     * 记得调用 {@link T#setIsDataEmpty(boolean)}}来声明这是一个空类
     *
     * @return
     */
    protected abstract T getEmptyItem();

    /**
     * 获取空的界面转换
     *
     * @return
     */
    protected abstract Class<? extends EfficientViewHolder<? extends T>> getEmptyViewHolderClass();

    /**
     * 获取正常业务的页面资源Id
     *
     * @return
     */
    protected abstract int getMyLayoutResId(int viewType);


    /**
     * 同 getItemViewType(int position)
     *
     * @param position
     * @return
     */
    protected abstract int getMyItemViewType(int position);

    /**
     * 获取正常业务的界面转换
     *
     * @return
     */
    protected abstract Class<? extends EfficientViewHolder<? extends T>> getMyViewHolderClass(int viewType);


    @Override
    public final int getLayoutResId(int viewType) {
        if (viewType == EMPTY_ITEM)
            return getEmptyLayoutResId();

        return getMyLayoutResId(viewType);

    }

    /**
     * 清空RecyclerView，并添加默认的空界面
     */
    public void emptyRecyclerView() {
        clear();
        add(getEmptyItem());
        notifyDataSetChanged();
    }

    @Override
    public final int getItemViewType(int position) {
        if (getItemCount() == 1 && get(0).isDataEmpty())
            return EMPTY_ITEM;

        return getMyItemViewType(position);
    }

    @Override
    public final Class<? extends EfficientViewHolder<? extends T>> getViewHolderClass(int viewType) {
        if (viewType == EMPTY_ITEM)
            return getEmptyViewHolderClass();

        return getMyViewHolderClass(viewType);
    }
}
