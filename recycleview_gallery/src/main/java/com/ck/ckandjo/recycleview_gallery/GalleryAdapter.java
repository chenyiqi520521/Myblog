package com.ck.ckandjo.recycleview_gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ckandjo on 2016/6/21.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private List<Integer> mListData;
    private Context mContext;
    LayoutInflater mInflater;
    public GalleryAdapter(List<Integer> mListData,Context mContext) {
        this.mContext=mContext;
        this.mListData=mListData;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_gallery,null);
        ViewHolder holder = new ViewHolder(view);
        holder.mPhoto = (ImageView) view.findViewById(R.id.iv_photo);
        holder.mName = (TextView) view.findViewById(R.id.tv_info);
        return holder;
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder holder, int position) {
         holder.mPhoto.setImageResource(mListData.get(position));
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
        ImageView mPhoto;
        TextView mName;
    }
}
