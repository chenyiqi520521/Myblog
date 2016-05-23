package zhexian.learn.cnblogs.image;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import zhexian.learn.cnblogs.base.BaseApplication;
import zhexian.learn.cnblogs.lib.ZHttp;
import zhexian.learn.cnblogs.util.DBHelper;
import zhexian.learn.cnblogs.util.Utils;

/**
 * Created by Administrator on 2016/5/23.
 */
public class LoadBitmapTask extends BaseImageAsyncTask {
    private static final int MSG_LOAD_IMAGE_DONE = 1;

    private String mImageUrl;
    private CommonCallBack<Bitmap> mCallBack;
    private BaseApplication baseApp;
    private int width, height;

    public LoadBitmapTask(BaseApplication baseApp, String imageUrl, int width, int height, CommonCallBack<Bitmap> callBack) {
        mImageUrl = imageUrl;
        mCallBack = callBack;
        this.width = width;
        this.height = height;
        this.baseApp = baseApp;
    }

    @Override
    public int getTaskId() {
        return QUERY_BITMAP_TASK_ID;
    }

    @Override
    public String getUrl() {
        return mImageUrl;
    }

    @Override
    public void run() {
        Bitmap bitmap = DBHelper.cache().getBitmap(mImageUrl, width, height);

        if (bitmap == null && baseApp.canRequestImage()) {
            byte[] bytes = ZHttp.getBytes(mImageUrl);

            if (bytes != null && bytes.length > 0) {
                bitmap = Utils.getScaledBitMap(bytes, width, height);
                DBHelper.cache().save(mImageUrl, bytes);
            }
        }

        new LoadBitmapHandler(baseApp.getMainLooper(), bitmap, mCallBack).sendEmptyMessage(MSG_LOAD_IMAGE_DONE);
        ImageTaskManager.getInstance().Done(getTaskId());
    }


    static class LoadBitmapHandler extends Handler {
        WeakReference<CommonCallBack<Bitmap>> mCallbackWeakRef;
        WeakReference<Bitmap> mBitmap;

        LoadBitmapHandler(Looper looper, Bitmap bitmap, CommonCallBack<Bitmap> callBack) {
            super(looper);
            mCallbackWeakRef = new WeakReference<>(callBack);
            mBitmap = new WeakReference<>(bitmap);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what != MSG_LOAD_IMAGE_DONE)
                return;

            if (mCallbackWeakRef.get() == null)
                return;


            if (mBitmap.get() != null) {
                mCallbackWeakRef.get().success(mBitmap.get());
            } else {
                mCallbackWeakRef.get().fail();
            }

            mCallbackWeakRef.get().finish();
        }
    }
}
