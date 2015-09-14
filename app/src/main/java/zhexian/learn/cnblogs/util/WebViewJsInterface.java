package zhexian.learn.cnblogs.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.JavascriptInterface;

import java.lang.ref.WeakReference;

import zhexian.learn.cnblogs.lib.ZHttp;


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
        new DisplayTask().execute(url);
    }

    class DisplayTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            if (DBHelper.cache().exist(strings[0]))
                return strings[0];

            byte[] bytes = ZHttp.getBytes(strings[0]);
            if (bytes != null && bytes.length > 0)
                DBHelper.cache().save(strings[0], bytes);

            return strings[0];
        }

        @Override
        protected void onPostExecute(String s) {
            Activity activity = mActivity.get();

            if (activity == null || activity.isFinishing())
                return;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + DBHelper.cache().trans2Local(s)), "image/*");
            activity.startActivity(intent);
        }
    }
}
