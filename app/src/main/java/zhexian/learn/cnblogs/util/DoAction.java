package zhexian.learn.cnblogs.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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
}
