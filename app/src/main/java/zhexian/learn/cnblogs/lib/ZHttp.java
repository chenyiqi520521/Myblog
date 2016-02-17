package zhexian.learn.cnblogs.lib;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2015/8/28.
 */
public class ZHttp {
    private static OkHttpClient mOkHttpClient;

    public static OkHttpClient getHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (ZHttp.class) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(12, TimeUnit.SECONDS).readTimeout(12, TimeUnit.SECONDS);
                mOkHttpClient = builder.build();
            }
        }

        return mOkHttpClient;
    }

    public static Response execute(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = getHttpClient().newCall(request).execute();

            if (response.isSuccessful())
                return response;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void AysncExec(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        getHttpClient().newCall(request).enqueue(callback);
    }

    /**
     * 通过get请求，获取json实例
     *
     * @param urlStr 请求地址
     */
    public static String getString(String urlStr) {
        try {
            Response response = execute(urlStr);

            if (response == null)
                return null;
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] getBytes(String url) {
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
