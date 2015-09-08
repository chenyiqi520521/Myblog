package zhexian.learn.cnblogs.news;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseSingleWebView;
import zhexian.learn.cnblogs.comment.CommentActivity;
import zhexian.learn.cnblogs.util.ConfigConstant;
import zhexian.learn.cnblogs.util.HtmlHelper;
import zhexian.learn.cnblogs.util.Utils;

public class NewsDetailActivity extends BaseSingleWebView {

    private static final String PARAM_NEWS_TITLE = "PARAM_NEWS_TITLE";
    private static final String PARAM_NEWS_ID = "PARAM_NEWS_ID";
    private static final String PARAM_NEWS_LIKE_COUNT = "PARAM_NEWS_LIKE_COUNT";
    private static final String PARAM_NEWS_COMMENT_COUNT = "PARAM_NEWS_COMMENT_COUNT";

    private long mDataID;
    private int mLikeCount;
    private int mCommentCount;
    private String mTitle;

    public static void actionStart(Context context, long newsID, int recommendCount, int commentCount, String title) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra(PARAM_NEWS_ID, newsID);
        intent.putExtra(PARAM_NEWS_LIKE_COUNT, recommendCount);
        intent.putExtra(PARAM_NEWS_COMMENT_COUNT, commentCount);
        intent.putExtra(PARAM_NEWS_TITLE, title);
        context.startActivity(intent);
    }

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        getMenuInflater().inflate(R.menu.menu_html_detail, menu);
        View likeItem = menu.findItem(R.id.action_detail_like).getActionView();
        ((TextView) likeItem.findViewById(R.id.action_item_like_text)).setText(String.valueOf(mLikeCount));
        likeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.toast(getApp(), "TODO：点击喜爱，会收藏到本地");
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
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private class NewsDetailTask extends AsyncTask<Long, Void, NewsDetailEntity> {

        @Override
        protected void onPreExecute() {
            renderProgress(true);
        }

        @Override
        protected NewsDetailEntity doInBackground(Long... longs) {
            return NewsDal.getNewsDetail(getApp(), longs[0]);
        }

        @Override
        protected void onPostExecute(NewsDetailEntity newsDetailEntity) {
            renderProgress(false);

            if (newsDetailEntity == null)
                return;

            HtmlHelper.getInstance().render(mWebView, newsDetailEntity);
        }
    }
}
