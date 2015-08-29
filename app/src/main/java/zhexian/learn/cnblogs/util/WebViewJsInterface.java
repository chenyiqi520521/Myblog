package zhexian.learn.cnblogs.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.JavascriptInterface;

import zhexian.learn.cnblogs.base.BaseApplication;
import zhexian.learn.cnblogs.lib.ZHttp;


/**
 * Created by Administrator on 2015/8/28.
 */
public class WebViewJsInterface {

    Activity mActivity;

    public WebViewJsInterface(Activity activity) {
        mActivity = activity;
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

            BaseApplication app = (BaseApplication) mActivity.getApplication();
            Bitmap bitmap = ZHttp.getBitmap(strings[0], app.getScreenWidth(), app.getScreenHeight());

            if (bitmap != null && bitmap.getByteCount() > 0) {
                DBHelper.cache().save(strings[0], bitmap);
            }
            return strings[0];

        }

        @Override
        protected void onPostExecute(String s) {
            if (mActivity.isFinishing())
                return;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + DBHelper.cache().trans2Local(s)), "image/*");
            mActivity.startActivity(intent);
        }
    }
}
