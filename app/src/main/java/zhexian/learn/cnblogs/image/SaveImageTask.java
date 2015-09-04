package zhexian.learn.cnblogs.image;

import zhexian.learn.cnblogs.base.BaseApplication;
import zhexian.learn.cnblogs.lib.ZHttp;
import zhexian.learn.cnblogs.util.DBHelper;


/**
 * 图片下载功能
 */
public class SaveImageTask extends BaseImageAsyncTask {
    private BaseApplication baseApp;
    private String url;
    private int width;
    private int height;

    public SaveImageTask(BaseApplication baseApp, String url, int width, int height) {
        this.baseApp = baseApp;
        this.url = url;
        this.width = width;
        this.height = height;
    }

    @Override
    public void run() {
        if (baseApp.isNetworkWifi()) {
            byte[] bytes = ZHttp.getBytes(url);

            if (bytes != null && bytes.length > 0) {
                DBHelper.cache().save(url, bytes);
            }
        }
        ImageTaskManager.getInstance().Done(getTaskId());
    }

    @Override
    public int getTaskId() {
        return SAVE_IMAGE_TASK_ID;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
