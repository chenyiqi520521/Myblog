package zhexian.learn.cnblogs.ui;

import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextView;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseActivity;


/**
 * Created by Administrator on 2015/6/4.
 */
public class TabActionBarView implements View.OnClickListener {

    private static final int FIRST_TAB_INDEX = 0;
    private static final int SECOND_TAB_INDEX = 1;
    private static final int THIRD_TAB_INDEX = 2;

    private int mSelectTabIndex = -1;
    private View mFirstView;
    private TextView mFirstTextView;
    private View mSecondView;
    private TextView mSecondTextView;
    private View mThirdView;
    private TextView mThirdTextView;
    private int mTextSelectedColor;
    private int mTextNormalColor;
    private ITabActionCallback mCallback;
    private View tabView;

    public TabActionBarView(BaseActivity activity, ITabActionCallback callback) {
        ActionBar actionBar = activity.getSupportActionBar();
        mCallback = callback;
        mTextNormalColor = activity.getResources().getColor(R.color.white);
        mTextSelectedColor = activity.getResources().getColor(R.color.green_dark);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_item_tab);
        actionBar.setDisplayHomeAsUpEnabled(true);
        tabView = actionBar.getCustomView();
    }

    public void bindTab(String firstText, String secondText) {
        bindTab(firstText, secondText, null);
    }

    public void bindTab(String firstText, String secondText, String thirdText) {
        mFirstView = tabView.findViewById(R.id.action_tab_first);
        mFirstTextView = (TextView) tabView.findViewById(R.id.action_tab_first_text);
        mFirstTextView.setText(firstText);
        mFirstView.setOnClickListener(this);

        mSecondView = tabView.findViewById(R.id.action_tab_second);
        mSecondTextView = (TextView) tabView.findViewById(R.id.action_tab_second_text);
        mSecondTextView.setText(secondText);
        mSecondView.setOnClickListener(this);

        if (thirdText != null) {
            mThirdView = tabView.findViewById(R.id.action_tab_third);
            mThirdTextView = (TextView) tabView.findViewById(R.id.action_tab_third_text);
            mThirdTextView.setText(secondText);
            mThirdView.setOnClickListener(this);
        }
        firstClick();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.action_tab_first:
                firstClick();
                break;

            case R.id.action_tab_second:
                secondClick();
                break;
            case R.id.action_tab_third:
                thirdClick();
                break;
        }
    }

    void cleanPreviousStyle() {
        switch (mSelectTabIndex) {

            case FIRST_TAB_INDEX:
                mFirstView.setBackgroundResource(R.mipmap.tab_left_normal);
                mFirstTextView.setTextColor(mTextNormalColor);
                break;
            case SECOND_TAB_INDEX:
                mSecondView.setBackgroundResource(R.mipmap.tab_right_normal);
                mSecondTextView.setTextColor(mTextNormalColor);
                break;
            case THIRD_TAB_INDEX:
                mThirdView.setBackgroundResource(R.mipmap.tab_middle_normal);
                mThirdTextView.setTextColor(mTextNormalColor);
                break;
        }
    }

    void firstClick() {
        if (mSelectTabIndex == FIRST_TAB_INDEX)
            return;

        cleanPreviousStyle();
        mFirstView.setBackgroundResource(R.mipmap.tab_left_select);
        mFirstTextView.setTextColor(mTextSelectedColor);
        mCallback.onFirstTabClick();

        mSelectTabIndex = FIRST_TAB_INDEX;
    }

    void secondClick() {
        if (mSelectTabIndex == SECOND_TAB_INDEX)
            return;

        cleanPreviousStyle();
        mSecondView.setBackgroundResource(R.mipmap.tab_right_select);
        mSecondTextView.setTextColor(mTextSelectedColor);
        mCallback.onSecondTabClick();

        mSelectTabIndex = SECOND_TAB_INDEX;
    }

    void thirdClick() {
        if (mSelectTabIndex == THIRD_TAB_INDEX)
            return;

        cleanPreviousStyle();
        mThirdView.setBackgroundResource(R.mipmap.tab_middle_select);
        mThirdTextView.setTextColor(mTextSelectedColor);
        mCallback.onThirdClick();

        mSelectTabIndex = THIRD_TAB_INDEX;
    }

    public interface ITabActionCallback {
        void onFirstTabClick();

        void onSecondTabClick();

        void onThirdClick();
    }
}
