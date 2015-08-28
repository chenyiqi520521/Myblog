package zhexian.learn.cnblogs.comment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseActivity;
import zhexian.learn.cnblogs.util.ConfigConstant;

public class CommentActivity extends BaseActivity {

    public static final String PARAM_CATEGORY = "zhexian.app.zoschina.comment.PARAM_CATEGORY";
    public static final String PARAM_DATA_ID = "zhexian.app.zoschina.comment.PARAM_DATA_ID";
    public static final String PARAM_TITLE = "zhexian.app.zoschina.comment.PARAM_TITLE";


    public static void actionStart(Context context, ConfigConstant.CommentCategory category, long dataID, String title) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(PARAM_CATEGORY, category);
        intent.putExtra(PARAM_DATA_ID, dataID);
        intent.putExtra(PARAM_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Intent intent = getIntent();

        if (intent != null) {
            ActionBar actionbar = getSupportActionBar();
            actionbar.setTitle(intent.getStringExtra(PARAM_TITLE));
            actionbar.setDisplayHomeAsUpEnabled(true);

            ConfigConstant.CommentCategory category = (ConfigConstant.CommentCategory) intent.getSerializableExtra(PARAM_CATEGORY);
            long dataID = intent.getLongExtra(PARAM_DATA_ID, 0);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.comment_container, CommentListFragment.fragmentStart(category, dataID))
                    .commit();
        }
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
}
