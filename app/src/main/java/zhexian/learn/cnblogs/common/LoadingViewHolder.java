package zhexian.learn.cnblogs.common;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import zhexian.learn.cnblogs.R;

/**
 * Created by Administrator on 2015/8/28.
 */
public class LoadingViewHolder extends RecyclerView.ViewHolder {

    public LoadingViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        super(layoutInflater.inflate(R.layout.base_swipe_item_loading, viewGroup, false));
    }

    public LoadingViewHolder(View itemView) {
        super(itemView);
    }
}
