package zhexian.learn.cnblogs.util;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 界面对象，快速操作帮助类
 * Created by 陈俊杰 on 2016/1/7.
 */
public class ZDomHelper {

    public static <T> T getView(View parentView, @IdRes int id) {
        return (T) parentView.findViewById(id);
    }

    public static <T> T getView(Activity context, @IdRes int id) {
        return (T) context.findViewById(id);
    }

    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @return
     */
    public static void setText(View parentView, @IdRes int viewId, String text) {
        TextView tv = getView(parentView, viewId);
        tv.setText(text);
    }

    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @return
     */
    public static void setText(Activity context, @IdRes int viewId, String text) {
        TextView tv = getView(context, viewId);
        tv.setText(text);
    }


    public static void setBackgroundColor(View parentView, @IdRes int viewId, int color) {
        View view = getView(parentView, viewId);
        view.setBackgroundColor(color);
    }

    public static void setBackgroundDrawable(Activity context, @IdRes int viewId, int resourceId) {
        View view = getView(context, viewId);
        view.setBackgroundDrawable(context.getResources().getDrawable(resourceId));
    }

    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @return
     */
    public static void setText(View parentView, @IdRes int viewId, Spanned text) {
        TextView tv = getView(parentView, viewId);
        tv.setText(text);
    }

    public static void setTextColor(View parentView, @IdRes int viewId, int textColor) {
        TextView view = getView(parentView, viewId);
        view.setTextColor(textColor);
    }


    public static void setImageResource(View parentView, @IdRes int viewId, int resId) {
        ImageView view = getView(parentView, viewId);
        view.setImageResource(resId);
    }

    public static void setImageDrawable(View parentView, @IdRes int viewId, Drawable drawable) {
        ImageView view = getView(parentView, viewId);
        view.setImageDrawable(drawable);
    }

    public static void setVisible(View parentView, @IdRes int viewId, boolean visible) {
        View view = getView(parentView, viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

}
