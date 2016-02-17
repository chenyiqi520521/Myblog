package zhexian.learn.cnblogs.blog;

import android.content.Context;
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

public class BlogDetailActivity extends BaseSingleWebView {
    private static final String PARAM_BLOG_ENTITY = "PARAM_BLOG_ENTITY";
    private BlogEntity mEntity;
    private View mTitleView;

    public static void actionStart(Context context, BlogEntity entity) {
        Intent intent = new Intent(context, BlogDetailActivity.class);
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(PARAM_BLOG_ENTITY, entity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.common_web_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleView = findViewById(R.id.title_bar);
        mTitleView.setClickable(true);
        mEntity = (BlogEntity) getIntent().getSerializableExtra(PARAM_BLOG_ENTITY);

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

                if (mEntity.getCommentAmount() == 0) {
                    Utils.toast(getApp(), R.string.alert_no_comment);
                    return;
                }
                CommentActivity.actionStart(BlogDetailActivity.this, ConfigConstant.CommentCategory.Blog, mEntity.getId(), mEntity.getTitle());
            }
        });

        findViewById(R.id.title_like).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mTitleView.isClickable())
                    return;

                Utils.toast(BlogDetailActivity.this, "功能建设中");
            }
        });


        ZDomHelper.setText(this, R.id.title_comment_text, String.valueOf(mEntity.getCommentAmount()));
        ZDomHelper.setText(this, R.id.title_like_text, String.valueOf(mEntity.getRecommendAmount()));
        new BlogDetailTask().execute(mEntity.getId());

        regScrollTitleBar(findViewById(R.id.title_bar));
    }

    private class BlogDetailTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            renderProgress(true);
        }

        @Override
        protected String doInBackground(Integer... params) {
            return BlogDal.getBlogContent(getApp(), mEntity.getId());
        }

        @Override
        protected void onPostExecute(String s) {
            renderProgress(false);
            mEntity.setContent(s);
            HtmlHelper.getInstance().render(mWebView, mEntity);
            SQLiteHelper.getInstance().addBlogHistory(mEntity.getId());
        }
    }
}
