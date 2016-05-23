package zhexian.learn.cnblogs.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import org.xutils.x;

import java.util.Date;

import zhexian.learn.cnblogs.base.XLPermissionHelper;

/**
 * Created by Administrator on 2016/2/16.
 */
public class DoAction {

    public static void jumpToWeb(Context context, String webUrl) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(webUrl);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    public static void trySaveBitmapToGallery(final Context context, View hookView, final Bitmap bitmap) {
        if (bitmap == null)
            return;

        XLPermissionHelper.requestStoragePermission(hookView, new XLPermissionHelper.IPermissionCallBack() {
            @Override
            public void onResult(boolean isGranted) {
                if (isGranted) {
                    saveBitmapToGallery(context, bitmap);
                }
            }
        });
    }

    public static void saveBitmapToGallery(final Context context, final Bitmap bitmap) {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                String title = String.valueOf(new Date().getTime());
                MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, "");
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                Utils.toast(context, "已保存到系统相册");
            }
        });
    }
}
