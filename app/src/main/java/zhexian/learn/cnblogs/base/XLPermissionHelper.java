package zhexian.learn.cnblogs.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import rx.functions.Action1;
import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.permission.RxPermissions;

/**
 * 权限检查
 * Created by 陈俊杰 on 2016/3/30.
 */
public class XLPermissionHelper {

    /**
     * @param alertTextRes
     * @param permissionCallBack
     * @param permissions
     */
    public static void requestPermission(@StringRes final int alertTextRes, final IPermissionCallBack permissionCallBack, final String... permissions) {
        requestPermission(null, alertTextRes, permissionCallBack, permissions);
    }

    /**
     * @param hookView           snackerbar需要的一个定位View
     * @param alertTextRes
     * @param permissionCallBack
     * @param permissions
     */
    public static void requestPermission(final View hookView, @StringRes final int alertTextRes, final IPermissionCallBack permissionCallBack, final String... permissions) {
        RxPermissions.getInstance().request(permissions).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (permissionCallBack != null) {
                    permissionCallBack.onResult(aBoolean);
                }

                if (!aBoolean && hookView != null) {
                    showPermissionSnackBar(hookView, alertTextRes);
                }
            }
        });
    }

    public static void requestStoragePermission(final View hookView, final IPermissionCallBack permissionCallBack) {
        requestPermission(hookView, R.string.permission_storage, permissionCallBack, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    /**
     * 跳转到app设置页面
     *
     * @param context
     */
    public static void jumpToAppSettingView(Context context) {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + context.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myAppSettings);
    }

    public static void showPermissionSnackBar(View view, @StringRes int alertTextRes) {
        if (view == null)
            return;

        String buttonText = "前往系统设置";
        Snackbar snackbar = Snackbar.make(view, view.getContext().getString(alertTextRes), Snackbar.LENGTH_LONG);

        snackbar.setAction(buttonText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToAppSettingView(v.getContext());
            }
        });
        snackbar.show();
    }

    public interface IPermissionCallBack {
        void onResult(boolean isGranted);
    }

}
