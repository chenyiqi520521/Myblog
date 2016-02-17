package zhexian.learn.cnblogs.news;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseSingleWebView;
import zhexian.learn.cnblogs.comment.CommentActivity;
import zhexian.learn.cnblogs.util.ConfigConstant;
import zhexian.learn.cnblogs.util.HtmlHelper;
import zhexian.learn.cnblogs.util.SQLiteHelper;
import zhexian.learn.cnblogs.util.Utils;
import zhexian.learn.cnblogs.util.ZDomHelper;

public class NewsDetailActivity extends BaseSingleWebView {

    private static final String PARAM_NEWS_TITLE = "PARAM_NEWS_TITLE";
    private static final String PARAM_NEWS_ID = "PARAM_NEWS_ID";
    private static final String PARAM_NEWS_LIKE_COUNT = "PARAM_NEWS_LIKE_COUNT";
    private static final String PARAM_NEWS_COMMENT_COUNT = "PARAM_NEWS_COMMENT_COUNT";

    private int mDataID;
    private int mLikeCount;
    private int mCommentCount;
    private String mTitle;
    private View mTitleView;

    public static void actionStart(Activity context, int newsID, int recommendCount, int commentCount, String title) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra(PARAM_NEWS_ID, newsID);
        intent.putExtra(PARAM_NEWS_LIKE_COUNT, recommendCount);
        intent.putExtra(PARAM_NEWS_COMMENT_COUNT, commentCount);
        intent.putExtra(PARAM_NEWS_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.common_web_detail;
    }

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleView = findViewById(R.id.title_bar);
        mTitleView.setClickable(true);
        Intent intent = getIntent();
        mDataID = intent.getIntExtra(PARAM_NEWS_ID, -1);
        mLikeCount = intent.getIntExtra(PARAM_NEWS_LIKE_COUNT, 0);
        mCommentCount = intent.getIntExtra(PARAM_NEWS_COMMENT_COUNT, 0);
        mTitle = intent.getStringExtra(PARAM_NEWS_TITLE);

        findViewById(R.id.title_left_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTitleView.isClickable())
                    return;

                finish();
            }
        });

        findViewById(R.id.title_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTitleView.isClickable())
                    return;

                if (mCommentCount == 0) {
                    Utils.toast(getApp(), R.string.alert_no_comment);
                    return;
                }
                CommentActivity.actionStart(NewsDetailActivity.this, ConfigConstant.CommentCategory.News, mDataID, mTitle);
            }
        });

        findViewById(R.id.title_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTitleView.isClickable())
                    return;

                Utils.toast(NewsDetailActivity.this, R.string.function_building);
            }
        });

        ZDomHelper.setText(this, R.id.title_comment_text, String.valueOf(mCommentCount));
        ZDomHelper.setText(this, R.id.title_like_text, String.valueOf(mLikeCount));
        new NewsDetailTask().execute(mDataID);
        regScrollTitleBar(findViewById(R.id.title_bar));
    }

    private class NewsDetailTask extends AsyncTask<Integer, Void, NewsDetailEntity> {

        @Override
        protected void onPreExecute() {
            renderProgress(true);
        }

        @Override
        protected NewsDetailEntity doInBackground(Integer... integers) {
            return NewsDal.getNewsDetail(getApp(), integers[0]);
        }

        @Override
        protected void onPostExecute(NewsDetailEntity newsDetailEntity) {
            renderProgress(false);

            if (newsDetailEntity == null)
                return;

            HtmlHelper.getInstance().render(mWebView, newsDetailEntity);
            SQLiteHelper.getInstance().addNewsHistory(newsDetailEntity.getId());
        }
    }
}
