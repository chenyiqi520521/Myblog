package zhexian.learn.cnblogs.util;

import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zhexian.learn.cnblogs.base.BaseApplication;

/**
 * Created by Administrator on 2015/8/28.
 */
public class Utils {

    public static ObjectAnimator GenerateColorAnimator(Context context, int animatorID, Object target) {
        ObjectAnimator colorAnimation = (ObjectAnimator) AnimatorInflater.loadAnimator(context, animatorID);
        colorAnimation.setTarget(target);
        colorAnimation.setEvaluator(new ArgbEvaluator());
        return colorAnimation;
    }

    public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, int textID) {
        Toast.makeText(context, context.getString(textID), Toast.LENGTH_SHORT).show();
    }

    public static Bitmap getBitMap(String imgPath) {
        return BitmapFactory.decodeFile(imgPath);
    }

    public static Bitmap getScaledBitMap(String imgPath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int inSampleSize = 1;

        if (srcHeight > height || srcWidth > width) {
            if (srcWidth > srcHeight)
                inSampleSize = Math.round(srcHeight / height);
            else
                inSampleSize = Math.round(srcWidth / width);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(imgPath, options);
    }

    public static Bitmap getScaledBitMap(byte[] data, int width, int height) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int inSampleSize = 1;

        if (srcHeight > height || srcWidth > width) {
            if (srcWidth > srcHeight)
                inSampleSize = Math.round(srcHeight / height);
            else
                inSampleSize = Math.round(srcWidth / width);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static String decorateIMGTag(String htmlContent, int screenWidth, boolean canRequest, boolean isNight) {
        Pattern patternImgSrc = Pattern.compile("<img(.+?)src=\"(.+?)\"(.+?)/>");
        Matcher localMatcher = patternImgSrc.matcher(htmlContent);

        screenWidth -= 16;
        String placeHolder = isNight ? "click_load_night.png" : "click_load_day.png";

        while (true) {
            if (!(localMatcher.find()))
                return htmlContent;

            String jsStr = "onclick=\"showImage(this,'$2')\"";
            String src;

            if (canRequest)
                src = "$2";
            else
                src = placeHolder;

            htmlContent = localMatcher.replaceAll(String.format("<img src='%s' %s style='max-width:%dpx'/>", src, jsStr, screenWidth));
        }
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


    public static String getHTMLCSS(BaseApplication baseApplication) {
        return baseApplication.isNightMode() ? "style_night.css" : "style_day.css";
    }

    public static File getDirPath(Context context, String dirName) {
        String file = Environment.isExternalStorageEmulated() ? context.getExternalFilesDir(null).getAbsolutePath() : context.getFilesDir().getAbsolutePath();
        File cacheFile = new File(file + File.separator + dirName);

        if (!cacheFile.exists())
            cacheFile.mkdirs();

        return cacheFile;
    }
}


