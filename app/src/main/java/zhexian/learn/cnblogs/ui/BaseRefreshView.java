package zhexian.learn.cnblogs.ui;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import zhexian.learn.cnblogs.R;


/**
 * Created by Administrator on 2015/8/28.
 */
public abstract class BaseRefreshView extends Drawable implements Drawable.Callback, Animatable {

    private String statusPullToRefresh;
    private String statusReleaseToLoad;
    private String statusRefreshing;
    private String statusRefreshSuccess;
    private String statusRefreshFail;

    private PullToRefreshView mRefreshLayout;

    public BaseRefreshView(Context context, PullToRefreshView layout) {
        mRefreshLayout = layout;
        statusPullToRefresh = context.getString(R.string.notify_pull_to_refresh);
        statusReleaseToLoad = context.getString(R.string.notify_release_to_load);
        statusRefreshing = context.getString(R.string.notify_refreshing);
        statusRefreshSuccess = context.getString(R.string.notify_refresh_success);
        statusRefreshFail = context.getString(R.string.notify_refresh_fail);
    }

    public Context getContext() {
        return mRefreshLayout != null ? mRefreshLayout.getContext() : null;
    }

    public PullToRefreshView getRefreshLayout() {
        return mRefreshLayout;
    }

    public abstract void setPercent(float percent, boolean invalidate);

    public abstract void offsetTopAndBottom(int offset);

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    protected String getDescription(int status) {
        switch (status) {
            case PullToRefreshView.STATUS_IDLE:
                return statusPullToRefresh;
            case PullToRefreshView.STATUS_REFRESH_SUCCESS:
                return statusRefreshSuccess;
            case PullToRefreshView.STATUS_REFRESH_FAIL:
                return statusRefreshFail;
            case PullToRefreshView.STATUS_PULL_TO_REFRESH:
                return statusPullToRefresh;
            case PullToRefreshView.STATUS_RELEASE_TO_LOAD:
                return statusReleaseToLoad;
            case PullToRefreshView.STATUS_REFRESHING:
                return statusRefreshing;

        }
        return "";
    }
}
