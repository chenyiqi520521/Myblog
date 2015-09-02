package zhexian.learn.cnblogs.blog;

import android.annotation.SuppressLint;
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
import zhexian.learn.cnblogs.ui.ScrollWebView;
import zhexian.learn.cnblogs.util.ConfigConstant;
import zhexian.learn.cnblogs.util.HtmlHelper;
import zhexian.learn.cnblogs.util.Utils;
import zhexian.learn.cnblogs.util.WebViewJsInterface;

public class BlogDetailActivity extends BaseActivity {
    private static final String PARAM_BLOG_ENTITY = "PARAM_BLOG_ENTITY";
    private ScrollWebView mWebView;
    private BlogEntity mEntity;

    private View mProgress;
    private int mPreviousYPos;

    public static void actionStart(Context context, BlogEntity entity) {
        Intent intent = new Intent(context, BlogDetailActivity.class);
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(PARAM_BLOG_ENTITY, entity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mEntity = (BlogEntity) getIntent().getSerializableExtra(PARAM_BLOG_ENTITY);

        if (mEntity == null)
            return;

        mProgress = findViewById(R.id.news_detail_progress);
        mWebView = (ScrollWebView) findViewById(R.id.html_detail_web_view);
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
        new BlogDetailTask().execute(mEntity.getId());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_html_detail, menu);
        View likeItem = menu.findItem(R.id.action_detail_like).getActionView();
        ((TextView) likeItem.findViewById(R.id.action_item_like_text)).setText(String.valueOf(mEntity.getRecommendAmount()));
        likeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.toast(getApp(), "TODO：点击喜爱，会收藏到本地");
            }
        });

        View commentItem = menu.findItem(R.id.action_detail_comment).getActionView();
        ((TextView) commentItem.findViewById(R.id.action_item_comment_text)).setText(String.valueOf(mEntity.getCommentAmount()));

        commentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEntity.getCommentAmount() == 0) {
                    Utils.toast(getApp(), R.string.alert_no_comment);
                    return;
                }
                CommentActivity.actionStart(BlogDetailActivity.this, ConfigConstant.CommentCategory.Blog, mEntity.getId(), mEntity.getTitle());
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
        }

        return super.onOptionsItemSelected(item);
    }

    private class BlogDetailTask extends AsyncTask<Long, Void, String> {

        @Override
        protected void onPreExecute() {
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Long... params) {
            return BlogDal.getBlogContent(getApp(), mEntity.getId());
        }

        @Override
        protected void onPostExecute(String s) {
            mProgress.setVisibility(View.GONE);
            super.onPostExecute(s);
            mEntity.setContent(s);

            HtmlHelper.getInstance().render(mWebView, mEntity);
        }
    }
}
