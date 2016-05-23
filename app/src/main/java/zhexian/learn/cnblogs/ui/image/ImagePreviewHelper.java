package zhexian.learn.cnblogs.ui.image;

import android.app.Activity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
public class ImagePreviewHelper {
    private static ImagePreviewHelper mImageHelper = new ImagePreviewHelper();

    private List<String> mImageUrlList = new ArrayList<>(8);

    public static ImagePreviewHelper getInstance() {
        return mImageHelper;
    }

    public void clearImageList() {
        mImageUrlList.clear();
    }

    public void addImageUrl(String url) {
        mImageUrlList.add(url);
    }

    public void showPreview(Activity context, String img) {

        if (mImageUrlList.isEmpty()) {
            XLImagePreviewActivity.start(context, context.getCurrentFocus(), img, null);
            return;
        }

        int findIndex = -1;
        int curPos = 0;

        for (String curUrl : mImageUrlList) {

            if (TextUtils.equals(curUrl, img)) {
                findIndex = curPos;
                break;
            }

            curPos++;
        }

        if (findIndex >= 0) {
            XLImagePreviewActivity.start(context, context.getCurrentFocus(), mImageUrlList, null, findIndex);
        } else {
            XLImagePreviewActivity.start(context, context.getCurrentFocus(), img, null);
        }

    }
}
