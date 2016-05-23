package zhexian.learn.cnblogs.util;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import java.lang.ref.WeakReference;

import zhexian.learn.cnblogs.ui.image.ImagePreviewHelper;


/**
 * Created by Administrator on 2015/8/28.
 */
public class WebViewJsInterface {

    WeakReference<Activity> mActivity;

    public WebViewJsInterface(Activity activity) {
        mActivity = new WeakReference<>(activity);
    }

    @JavascriptInterface
    public void displayImage(String url) {
        ImagePreviewHelper.getInstance().showPreview(mActivity.get(), url);
    }
}
