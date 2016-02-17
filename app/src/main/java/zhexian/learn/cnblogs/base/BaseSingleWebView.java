package zhexian.learn.cnblogs.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
public abstract class BaseSingleWebView extends BaseActivity implements ScrollWebView.OnScrollListener {
    protected ScrollWebView mWebView;
    private FrameLayout mWebViewContainer;
    private View mProgress;
    private int mPreviousYPos;

    @Override
    protected boolean isSwipeToClose() {
        return true;
    }

    protected abstract int getLayoutId();

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mProgress = findViewById(R.id.news_detail_progress);
        mWebViewContainer = (FrameLayout) findViewById(R.id.html_detail_web_view);
        mWebView = new ScrollWebView(this);
        mWebViewContainer.addView(mWebView);

        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.addJavascriptInterface(new WebViewJsInterface(this), "Android");
        mWebView.setOnScrollListener(this);
    }


    @Override
    protected void onDestroy() {
        mWebView.setOnScrollListener(null);
        mWebView.destroy();
        mWebViewContainer.removeAllViews();
        super.onDestroy();
    }

    protected void renderProgress(boolean isShow) {
        if (isShow)
            mProgress.setVisibility(View.VISIBLE);
        else
            mProgress.setVisibility(View.GONE);
    }

    @Override
    public void onScroll(int x, int y) {
        switchActionBar(y - mPreviousYPos);
        mPreviousYPos = y;
    }
}
