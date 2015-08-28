package zhexian.learn.cnblogs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import zhexian.learn.cnblogs.base.BaseApplication;
import zhexian.learn.cnblogs.util.Utils;


/**
 * Created by Administrator on 2015/8/28.
 */
public class NetWorkChangeReceiver extends BroadcastReceiver {
    private BaseApplication mBaseApplication;

    public NetWorkChangeReceiver(BaseApplication baseApplication) {
        mBaseApplication = baseApplication;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mBaseApplication.setNetworkStatus(Utils.GetConnectType(context));
    }
}
