package zhexian.learn.cnblogs.base.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

@TargetApi(Build.VERSION_CODES.M)
public class ShadowActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String[] askedPermissions = intent.getStringArrayExtra("permissions");
        ActivityCompat.requestPermissions(this, askedPermissions, 42);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        RxPermissions.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxPermissions.getInstance().onShadowActivityStop();
    }
}
