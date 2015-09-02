package zhexian.learn.cnblogs.ui;

import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseActivity;


/**
 * Created by 陈俊杰 on 2015/6/4.
 * 嵌入在tab上的标签卡
 */
public class TabActionBarView implements View.OnClickListener {

    private static final int LEFT_TAB_INDEX = 0;
    private static final int MIDDLE_TAB_INDEX = 1;
    private static final int RIGHT_TAB_INDEX = 2;

    private int mSelectTabIndex = -1;
    private View mLeftView;
    private TextView mLeftTextView;
    private View mMiddleView;
    private TextView mMiddleTextView;
    private View mRightView;
    private TextView mRightTextView;
    private int mTextSelectedColor;
    private int mTextNormalColor;
    private ITabActionCallback mCallback;

    public TabActionBarView(BaseActivity activity, ITabActionCallback callback) {
        ActionBar actionBar = activity.getSupportActionBar();
        mCallback = callback;
        mTextNormalColor = activity.getResources().getColor(R.color.white);
        mTextSelectedColor = activity.getResources().getColor(R.color.green_dark);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_item_tab);
        actionBar.setDisplayHomeAsUpEnabled(true);
        View tabView = actionBar.getCustomView();

        mLeftView = tabView.findViewById(R.id.action_tab_left);
        mLeftTextView = (TextView) tabView.findViewById(R.id.action_tab_left_text);
        mMiddleView = tabView.findViewById(R.id.action_tab_middle);
        mMiddleTextView = (TextView) tabView.findViewById(R.id.action_tab_middle_text);
        mRightView = tabView.findViewById(R.id.action_tab_right);
        mRightTextView = (TextView) tabView.findViewById(R.id.action_tab_right_text);
    }

    public void bindTab(String leftText, String rightText) {
        bindTab(leftText, null, rightText);
    }

    public void bindTab(String leftText, String middleText, String rightText) {
        mLeftTextView.setText(leftText);
        mLeftView.setOnClickListener(this);

        mRightTextView.setText(rightText);
        mRightView.setOnClickListener(this);

        if (TextUtils.isEmpty(middleText))
            mMiddleView.setVisibility(View.GONE);
        else {
            mMiddleView.setVisibility(View.VISIBLE);
            mMiddleTextView.setText(middleText);
            mMiddleView.setOnClickListener(this);
        }
        leftClick();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.action_tab_left:
                leftClick();
                break;

            case R.id.action_tab_middle:
                middleClick();
                break;

            case R.id.action_tab_right:
                rightClick();
                break;
        }
    }

    void cleanPreviousStyle() {
        switch (mSelectTabIndex) {

            case LEFT_TAB_INDEX:
                mLeftView.setBackgroundResource(R.mipmap.tab_left_normal);
                mLeftTextView.setTextColor(mTextNormalColor);
                break;
            case MIDDLE_TAB_INDEX:
                mMiddleView.setBackgroundResource(R.mipmap.tab_middle_normal);
                mMiddleTextView.setTextColor(mTextNormalColor);
                break;
            case RIGHT_TAB_INDEX:
                mRightView.setBackgroundResource(R.mipmap.tab_right_normal);
                mRightTextView.setTextColor(mTextNormalColor);
                break;
        }
    }

    void leftClick() {
        if (mSelectTabIndex == LEFT_TAB_INDEX)
            return;

        cleanPreviousStyle();
        mLeftView.setBackgroundResource(R.mipmap.tab_left_select);
        mLeftTextView.setTextColor(mTextSelectedColor);
        mCallback.onLeftTabClick();

        mSelectTabIndex = LEFT_TAB_INDEX;
    }

    void middleClick() {
        if (mSelectTabIndex == MIDDLE_TAB_INDEX)
            return;

        cleanPreviousStyle();
        mMiddleView.setBackgroundResource(R.mipmap.tab_middle_select);
        mMiddleTextView.setTextColor(mTextSelectedColor);
        mCallback.onMiddleTabClick();

        mSelectTabIndex = MIDDLE_TAB_INDEX;
    }

    void rightClick() {
        if (mSelectTabIndex == RIGHT_TAB_INDEX)
            return;

        cleanPreviousStyle();
        mRightView.setBackgroundResource(R.mipmap.tab_right_select);
        mRightTextView.setTextColor(mTextSelectedColor);
        mCallback.onRightClick();

        mSelectTabIndex = RIGHT_TAB_INDEX;
    }

    public interface ITabActionCallback {
        void onLeftTabClick();

        void onMiddleTabClick();

        void onRightClick();
    }
}
