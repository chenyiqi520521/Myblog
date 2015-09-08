package zhexian.learn.cnblogs.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.FrameLayout;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.ui.ScrollWebView;
import zhexian.learn.cnblogs.util.WebViewJsInterface;

/**
 * Created by Administrator on 2015/9/8.
 * 单个浏览器父类
 */
public class BaseSingleWebView extends BaseActivity {
    protected ScrollWebView mWebView;
    private FrameLayout mWebViewContainer;
    private View mProgress;
    private int mPreviousYPos;

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mProgress = findViewById(R.id.news_detail_progress);

        mWebViewContainer = (FrameLayout) findViewById(R.id.html_detail_web_view);
        mWebView = new ScrollWebView(getApp());
        mWebViewContainer.addView(mWebView);


        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebViewJsInterface(this), "Android");
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.setOnScrollListener(new ScrollWebView.OnScrollListener() {
            @Override
            public void onScroll(int x, int y) {
                switchActionBar(y - mPreviousYPos);
                mPreviousYPos = y;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebViewContainer.removeAllViews();
        mWebView.destroy();
    }

    protected void renderProgress(boolean isShow) {
        if (isShow)
            mProgress.setVisibility(View.VISIBLE);
        else
            mProgress.setVisibility(View.GONE);

    }
}
