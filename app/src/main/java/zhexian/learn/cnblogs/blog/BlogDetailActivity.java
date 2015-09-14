package zhexian.learn.cnblogs.blog;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseSingleWebView;
import zhexian.learn.cnblogs.comment.CommentActivity;
import zhexian.learn.cnblogs.util.ConfigConstant;
import zhexian.learn.cnblogs.util.HtmlHelper;
import zhexian.learn.cnblogs.util.SQLiteHelper;
import zhexian.learn.cnblogs.util.Utils;

public class BlogDetailActivity extends BaseSingleWebView {
    private static final String PARAM_BLOG_ENTITY = "PARAM_BLOG_ENTITY";
    private BlogEntity mEntity;


    public static void actionStart(Context context, BlogEntity entity) {
        Intent intent = new Intent(context, BlogDetailActivity.class);
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(PARAM_BLOG_ENTITY, entity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEntity = (BlogEntity) getIntent().getSerializableExtra(PARAM_BLOG_ENTITY);
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

    private class BlogDetailTask extends AsyncTask<Long, Void, String> {

        @Override
        protected void onPreExecute() {
            renderProgress(true);
        }

        @Override
        protected String doInBackground(Long... params) {
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
