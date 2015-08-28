package zhexian.learn.cnblogs.news;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.TextView;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseActivity;
import zhexian.learn.cnblogs.comment.CommentActivity;
import zhexian.learn.cnblogs.lib.ZDate;
import zhexian.learn.cnblogs.ui.ScrollWebView;
import zhexian.learn.cnblogs.util.ConfigConstant;
import zhexian.learn.cnblogs.util.Utils;
import zhexian.learn.cnblogs.util.WebViewJsInterface;

public class NewsDetailActivity extends BaseActivity {

    private static final String PARAM_NEWS_TITLE = "PARAM_NEWS_TITLE";
    private static final String PARAM_NEWS_ID = "PARAM_NEWS_ID";
    private static final String PARAM_NEWS_LIKE_COUNT = "PARAM_NEWS_LIKE_COUNT";
    private static final String PARAM_NEWS_COMMENT_COUNT = "PARAM_NEWS_COMMENT_COUNT";

    private ScrollWebView mNewsContent;
    private View mProgress;
    private long mDataID;
    private int mLikeCount;
    private int mCommentCount;
    private int mPreviousYPos;
    private String mTitle;

    public static void actionStart(Context context, long newsID, int recommendCount, int commentCount, String title) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra(PARAM_NEWS_ID, newsID);
        intent.putExtra(PARAM_NEWS_LIKE_COUNT, recommendCount);
        intent.putExtra(PARAM_NEWS_COMMENT_COUNT, commentCount);
        intent.putExtra(PARAM_NEWS_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mProgress = findViewById(R.id.news_detail_progress);
        mNewsContent = (ScrollWebView) findViewById(R.id.news_detail_content);
        mNewsContent.getSettings().setJavaScriptEnabled(true);
        mNewsContent.addJavascriptInterface(new WebViewJsInterface(this), "Android");
        mNewsContent.getSettings().setPluginState(WebSettings.PluginState.ON);
        mNewsContent.setOnScrollListener(new ScrollWebView.OnScrollListener() {
            @Override
            public void onScroll(int x, int y) {
                switchActionBar(y - mPreviousYPos);
                mPreviousYPos = y;
            }
        });

        Intent intent = getIntent();
        mDataID = intent.getLongExtra(PARAM_NEWS_ID, -1);
        mLikeCount = intent.getIntExtra(PARAM_NEWS_LIKE_COUNT, 0);
        mCommentCount = intent.getIntExtra(PARAM_NEWS_COMMENT_COUNT, 0);
        mTitle = intent.getStringExtra(PARAM_NEWS_TITLE);
        new NewsDetailTask().execute(mDataID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_detail, menu);
        View likeItem = menu.findItem(R.id.action_detail_like).getActionView();
        ((TextView) likeItem.findViewById(R.id.action_item_like_text)).setText(String.valueOf(mLikeCount));
        likeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.toast(getApp(), "like click");
            }
        });

        View commentItem = menu.findItem(R.id.action_detail_comment).getActionView();
        ((TextView) commentItem.findViewById(R.id.action_item_comment_text)).setText(String.valueOf(mCommentCount));

        commentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCommentCount == 0) {
                    Utils.toast(getApp(), R.string.alert_no_comment);
                    return;
                }
                CommentActivity.actionStart(NewsDetailActivity.this, ConfigConstant.CommentCategory.News, mDataID, mTitle);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_detail_share:
                Utils.toast(this, "share click");
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private class NewsDetailTask extends AsyncTask<Long, Void, NewsDetailEntity> {

        @Override
        protected void onPreExecute() {
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected NewsDetailEntity doInBackground(Long... longs) {
            return NewsDal.getNewsDetail(getApp(), longs[0]);
        }

        @Override
        protected void onPostExecute(NewsDetailEntity newsDetailEntity) {
            mProgress.setVisibility(View.GONE);

            if (newsDetailEntity == null)
                return;

            double fontSize = getApp().isBigFont() ? ConfigConstant.HTML_FONT_SIZE_BIG : ConfigConstant.HTML_FONT_SIZE_NORMAL;
            String source = String.format("%s %s %s", newsDetailEntity.getSource(), ZDate.FriendlyTime(newsDetailEntity.getPublishTime()), getResources().getString(R.string.publish));
            String content = getApp().getHtmlString().replace("{style}", Utils.getHTMLCSS(getApp())).replace("{title}", newsDetailEntity.getTitle())
                    .replace("{source}", source).replace("{fontSize}", String.valueOf(fontSize)).replace("{html}", newsDetailEntity.getContent());
            mNewsContent.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "utf-8", null);

        }
    }
}
