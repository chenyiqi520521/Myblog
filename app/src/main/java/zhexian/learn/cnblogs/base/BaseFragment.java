package zhexian.learn.cnblogs.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * fragment的父类
 * Created by 陈俊杰 on 2016/1/30.
 */
public abstract class BaseFragment extends Fragment {

    /**
     * 视图对象
     */
    public View mFragmentViewObject;

    /**
     * 布局文件ID
     */
    protected abstract int getLayoutId();

    /**
     * 载入数据
     */
    public abstract void bindData();

    /**
     * 通知此页面做些事情
     *
     * @param actionCode
     * @param arg        参数
     */
    public void doAction(String actionCode, Object arg) {
    }

    /**
     * 找到元素
     *
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findViewById(@IdRes int id) {
        return (T) mFragmentViewObject.findViewById(id);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        if (mFragmentViewObject == null) {
            mFragmentViewObject = inflater.inflate(getLayoutId(), container, false);
        }
        return mFragmentViewObject;
    }

    @Override
    public final void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mFragmentViewObject == null)
            return;

        ViewGroup mParent = (ViewGroup) mFragmentViewObject.getParent();

        if (mParent == null)
            return;

        mParent.removeView(mFragmentViewObject);
    }
}
