package zhexian.learn.cnblogs.lib;

import android.graphics.Bitmap;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import zhexian.learn.cnblogs.util.Utils;

/**
 * Created by Administrator on 2015/8/28.
 */
public class ZHttp {
    private static OkHttpClient mOkHttpClient;

    public static OkHttpClient getHttpClient() {

        if (mOkHttpClient == null) {
            synchronized (ZHttp.class) {
                mOkHttpClient = new OkHttpClient();
                mOkHttpClient.setConnectTimeout(12, TimeUnit.SECONDS);
                mOkHttpClient.setReadTimeout(12, TimeUnit.SECONDS);
            }
        }
        return mOkHttpClient;
    }

    public static Response execute(String url) {
        Request request = new Builder().url(url).build();
        try {
            Response response = getHttpClient().newCall(request).execute();

            if (response.isSuccessful())
                return response;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过get请求，获取json实例
     *
     * @param urlStr 请求地址
     */
    public static String getString(String urlStr) {
        Response response = execute(urlStr);

        if (response == null)
            return null;

        try {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取网落图片资源
     *
     * @param url
     * @return
     */
    public static Bitmap getBitmap(String url, int width, int height) {
        byte[] bytes = getByte(url);

        if (bytes == null || bytes.length == 0)
            return null;

        return Utils.getScaledBitMap(bytes, width, height);
    }

    public static byte[] getByte(String url) {
        try {
            Response response = execute(url);

            if (null == response)
                return null;

            return response.body().bytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
