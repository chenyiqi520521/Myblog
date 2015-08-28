package zhexian.learn.cnblogs.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import zhexian.learn.cnblogs.R;

/**
 * Created by Administrator on 2015/5/5.
 */
public class NavigatorArrayAdapter extends ArrayAdapter<NavigatorModel> {
    private int mResourceID;
    private List<NavigatorModel> mNavigatorList;
    private Context mContext;

    public NavigatorArrayAdapter(Context context, int resource, List<NavigatorModel> objects) {
        super(context, resource, objects);
        mContext = context;
        mResourceID = resource;
        mNavigatorList = objects;
    }

    @Override
    public int getCount() {
        return mNavigatorList.size();
    }

    @Override
    public NavigatorModel getItem(int position) {
        return mNavigatorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mNavigatorList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        NavigatorModel model = mNavigatorList.get(position);

        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(mResourceID, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.navigator_item_name);
            viewHolder.viewContainer = view.findViewById(R.id.navigator_item_container);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.Init(model);
        return view;
    }

    class ViewHolder {
        TextView tvName;
        View viewContainer;

        public void Init(NavigatorModel model) {
            tvName.setText(model.getName());
        }
    }
}
