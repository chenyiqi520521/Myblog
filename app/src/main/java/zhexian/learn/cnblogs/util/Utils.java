package zhexian.learn.cnblogs.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Administrator on 2015/8/28.
 */
public class Utils {
    /**
     * https://
     */
    private static final int HTTP_FIRST_SPLIT_POS = 8;


    public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, int textID) {
        Toast.makeText(context, context.getString(textID), Toast.LENGTH_SHORT).show();
    }

    public static Bitmap getBitMap(String imgPath) {
        return BitmapFactory.decodeFile(imgPath);
    }

    public static Bitmap getScaledBitMap(byte[] data, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        if (srcWidth < width && srcHeight < height)
            return BitmapFactory.decodeByteArray(data, 0, data.length);


        int inSampleSize;

        if (srcWidth > srcHeight)
            inSampleSize = Math.round(srcHeight / height);
        else
            inSampleSize = Math.round(srcWidth / width);

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static ConfigConstant.NetworkStatus GetConnectType(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();

        if (activeInfo != null && activeInfo.isConnected()) {
            if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI)
                return ConfigConstant.NetworkStatus.Wifi;
            else if (activeInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                return ConfigConstant.NetworkStatus.Mobile;
        }
        return ConfigConstant.NetworkStatus.DisConnect;
    }

    public static String transToLocal(String url) {
        return transToLocal(url, null);
    }

    public static String transToLocal(String url, String dir) {
        //不用截取
        if (url.indexOf(':') < 0)
            return dir + url;

        url = url.substring(url.indexOf('/', HTTP_FIRST_SPLIT_POS) + 1);
        url = url.replace('/', '_');

        if (!TextUtils.isEmpty(dir))
            url = dir + url;

        return url;
    }

    public static String transHtmlTag(String htmlTag) {
        htmlTag = htmlTag.replace("&apos;", "'");
        htmlTag = htmlTag.replace("&quot;", "\"");
        htmlTag = htmlTag.replace("&gt;", ">");
        htmlTag = htmlTag.replace("&lt;", "<");
        htmlTag = htmlTag.replace("&amp;", "&");
        return htmlTag;
    }

    public static String getWritePath(Context context) {

        //外部有挂载
        if (Environment.isExternalStorageEmulated()) {
            File file = context.getExternalFilesDir(null);

            if (file != null)
                return file.getAbsolutePath();
        }

        return context.getFilesDir().getAbsolutePath();
    }
}


