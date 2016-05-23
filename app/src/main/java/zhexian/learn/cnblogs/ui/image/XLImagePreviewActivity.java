package zhexian.learn.cnblogs.ui.image;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.XLFragmentPagerAdapter;
import zhexian.learn.cnblogs.util.AnimUtils;
import zhexian.learn.cnblogs.util.Constant;
import zhexian.learn.cnblogs.util.DisplayUtil;
import zhexian.learn.cnblogs.util.DoAction;
import zhexian.learn.cnblogs.util.Utils;

/**
 * 图片预览界面，支持缩放、保存到本地
 * POWERED BY 陈俊杰 2016-05-08
 */
public class XLImagePreviewActivity extends FragmentActivity implements IDelayClose, View.OnClickListener {
    private static final String PARAM_IMAGE_URL_LIST = "PARAM_IMAGE_URL_LIST";
    private static final String PARAM_SMALL_IMAGE_URL_LIST = "PARAM_SMALL_IMAGE_URL_LIST";
    private static final String PARAM_IMAGE_INDEX = "PARAM_IMAGE_INDEX";
    private static final int DOUBLE_TAP_DURATION = ViewConfiguration.getDoubleTapTimeout() + 20;
    private static final int SHOW_MENU_DELAY_MILLION_SECONDS = DOUBLE_TAP_DURATION - 70;

    private ViewPager mViewPager;
    private Animation mSlideInAnimation;
    private Animation mSlideOutAnimation;
    private WeakReference<ZoomImageView> mRequestMenuImageView;
    private ObjectAnimator mMenuBackgroundAnimation;
    private View mMenuContainerView;
    private View mMenuButtonContainer;
    private
    @Constant.AnimationState
    int mMenuAnimationState = Constant.IDLE;
    private int mImageCount;
    private boolean mIsCancelClose;
    private Runnable mFinishRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsCancelClose) {
                return;
            }

            finishPreview();
        }
    };
    private Runnable mCloseMenuRunnable = new Runnable() {
        @Override
        public void run() {
            mMenuAnimationState = Constant.IDLE;
            mMenuContainerView.setVisibility(View.GONE);
        }
    };
    private Runnable mAnimationFinishRunnable = new Runnable() {
        @Override
        public void run() {
            mMenuAnimationState = Constant.IDLE;
        }
    };
    /**
     * 高清原图地址
     */
    private List<String> mImgUrlList;
    /**
     * 缩略图地址
     */
    private List<String> mSmallUrlList;
    private int mImageIndex;
    private DefaultIndicatorController mController;
    private int mMenuAnimationTime;
    private Runnable mShowMenuRunnable = new Runnable() {
        @Override
        public void run() {

            if (mRequestMenuImageView == null || mRequestMenuImageView.get() == null)
                return;

            if (!mRequestMenuImageView.get().getLongPressState())
                return;

            cancelClose();
            startMenuInAnimation();
        }
    };

    /**
     * 列表形式开启Preview
     *
     * @param context
     * @param imgUrlList 数组集合
     * @param imageIndex 下标
     */
    public static void start(Activity context, List<String> imgUrlList, int imageIndex) {
        start(context, imgUrlList, null, imageIndex);
    }

    /**
     * 列表形式开启Preview
     *
     * @param context
     * @param imgUrlList 数组集合
     * @param imageIndex 下标
     */
    public static void start(Activity context, List<String> imgUrlList, List<String> smallImgUrlList, int imageIndex) {
        Intent starter = generateIntent(context, imgUrlList, smallImgUrlList, imageIndex);
        context.startActivity(starter);
        context.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    public static void start(Activity context, View beginAnchorView, String imageUrl, String smallImgUrl) {
        List<String> imgUrlList = new ArrayList<>(1);
        imgUrlList.add(imageUrl);

        List<String> smallImageUrlList = null;

        if (!TextUtils.isEmpty(smallImgUrl)) {
            smallImageUrlList = new ArrayList<>(1);
            smallImageUrlList.add(smallImgUrl);
        }

        start(context, beginAnchorView, imgUrlList, smallImageUrlList, 0);
    }

    /**
     * 利用android 5.0动画特性，从点击区域开始，缓缓扩大新的界面
     *
     * @param context
     * @param beginAnchorView
     * @param imgUrlList
     * @param smallImgUrlList
     * @param imageIndex
     */
    public static void start(Activity context, View beginAnchorView, List<String> imgUrlList, List<String> smallImgUrlList, int imageIndex) {
        if (beginAnchorView == null) {
            start(context, imgUrlList, smallImgUrlList, imageIndex);
            return;
        }
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeScaleUpAnimation(beginAnchorView,
                beginAnchorView.getWidth() / 2, beginAnchorView.getHeight() / 2, 0, 0);

        ActivityCompat.startActivity(context, generateIntent(context, imgUrlList, smallImgUrlList, imageIndex),
                compat.toBundle());
    }

    private static Intent generateIntent(Context context, List<String> imgUrlList, List<String> smallImgUrlList, int imageIndex) {
        Intent starter = new Intent(context, XLImagePreviewActivity.class);
        starter.putStringArrayListExtra(PARAM_IMAGE_URL_LIST, (ArrayList<String>) imgUrlList);

        if (!Utils.isEmpty(smallImgUrlList)) {
            starter.putStringArrayListExtra(PARAM_SMALL_IMAGE_URL_LIST, (ArrayList<String>) smallImgUrlList);
        }

        starter.putExtra(PARAM_IMAGE_INDEX, imageIndex);
        return starter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initParam();
        bindView();
    }

    private void initParam() {
        mImgUrlList = getIntent().getStringArrayListExtra(PARAM_IMAGE_URL_LIST);
        mImageCount = mImgUrlList.size();
        mSmallUrlList = getIntent().getStringArrayListExtra(PARAM_SMALL_IMAGE_URL_LIST);

        mImageIndex = getIntent().getIntExtra(PARAM_IMAGE_INDEX, 0);
        mMenuAnimationTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    private void initController() {
        if (mImageCount == 1) return;

        if (mController == null)
            mController = new DefaultIndicatorController();

        FrameLayout indicatorContainer = (FrameLayout) findViewById(R.id.imagePreview_indicator);
        indicatorContainer.addView(mController.newInstance(this));

        mController.initialize(mImageCount);
    }

    private void startMenuInAnimation() {
        if (mMenuAnimationState != Constant.IDLE)
            return;

        if (mSlideInAnimation == null) {
            mSlideInAnimation = AnimationUtils.loadAnimation(XLImagePreviewActivity.this, R.anim.slide_in_from_bottom);
        }
        if (mMenuBackgroundAnimation == null) {
            mMenuBackgroundAnimation = AnimUtils.generateColorAnimator(XLImagePreviewActivity.this, R.animator.alpha_black_reverse, mMenuContainerView);
        }

        mMenuAnimationState = Constant.RUNNING;
        mMenuButtonContainer.startAnimation(mSlideInAnimation);
        mMenuContainerView.setVisibility(View.VISIBLE);
        mMenuBackgroundAnimation.start();
        x.task().removeCallbacks(mAnimationFinishRunnable);
        x.task().postDelayed(mAnimationFinishRunnable, mMenuAnimationTime);
    }

    private void startMenuOutAnimation() {
        if (mMenuAnimationState != Constant.IDLE)
            return;

        if (mSlideOutAnimation == null) {
            mSlideOutAnimation = AnimationUtils.loadAnimation(XLImagePreviewActivity.this, R.anim.slide_out_to_bottom);
        }

        if (mMenuBackgroundAnimation == null) {
            mMenuBackgroundAnimation = AnimUtils.generateColorAnimator(XLImagePreviewActivity.this, R.animator.alpha_black_reverse, mMenuContainerView);
        }

        mMenuAnimationState = Constant.RUNNING;
        mMenuButtonContainer.startAnimation(mSlideOutAnimation);
        mMenuBackgroundAnimation.reverse();
        x.task().removeCallbacks(mCloseMenuRunnable);
        x.task().postDelayed(mCloseMenuRunnable, mMenuAnimationTime);
    }

    private void bindView() {
        initController();
        mViewPager = (ViewPager) findViewById(R.id.imagePreview_viewPager);

        mViewPager.setAdapter(new XLFragmentPagerAdapter<Fragment>(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mImgUrlList.size();
            }

            @Override
            protected Fragment getFragmentItem(int position) {
                String smallUrl = Utils.isEmpty(mSmallUrlList) ? null : mSmallUrlList.get(position);
                XLImagePreviewFragment previewFragment = XLImagePreviewFragment.newInstance(mImgUrlList.get(position), smallUrl);
                return previewFragment;
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                cancelClose();
            }

            @Override
            public void onPageSelected(int position) {
                if (mController != null) {
                    mController.selectPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setPageMargin(DisplayUtil.dip2px(10));
        mViewPager.setCurrentItem(mImageIndex);

        mMenuContainerView = findViewById(R.id.imagePreview_menu);
        mMenuContainerView.setOnClickListener(this);

        mMenuButtonContainer = findViewById(R.id.imagePreview_buttonContainer);
        findViewById(R.id.imagePreview_SaveImage).setOnClickListener(this);
        findViewById(R.id.imagePreview_cancel).setOnClickListener(this);
    }

    private void finishPreview() {
        finish();
        overridePendingTransition(0, R.anim.zoom_out);
    }

    @Override
    public void startClose() {
        mIsCancelClose = false;
        x.task().removeCallbacks(mFinishRunnable);
        x.task().postDelayed(mFinishRunnable, DOUBLE_TAP_DURATION);
    }

    @Override
    public void cancelClose() {
        if (mIsCancelClose)
            return;

        mIsCancelClose = true;
        x.task().removeCallbacks(mFinishRunnable);
    }

    @Override
    public void doAction(String actionCode, Object param) {
        if (actionCode.equals(IDelayClose.ACTION_SHOW_MENU)) {

            if (param instanceof ZoomImageView) {
                if (mMenuAnimationState != Constant.IDLE)
                    return;

                mRequestMenuImageView = new WeakReference<>((ZoomImageView) param);
                x.task().removeCallbacks(mShowMenuRunnable);
                x.task().postDelayed(mShowMenuRunnable, SHOW_MENU_DELAY_MILLION_SECONDS);

                //重新刷新下关闭计时器
                startClose();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishPreview();
    }

    @Override
    public void onClick(View v) {
        //android module模块 无法使用switch语法，因此用if代替

        int id = v.getId();

        if (id == R.id.imagePreview_menu || id == R.id.imagePreview_cancel) {
            startMenuOutAnimation();
            return;
        }

        if (id == R.id.imagePreview_SaveImage) {
            if (mRequestMenuImageView.get() != null) {
                Bitmap bitmap = ((BitmapDrawable) mRequestMenuImageView.get().getDrawable()).getBitmap();
                DoAction.trySaveBitmapToGallery(XLImagePreviewActivity.this, v, bitmap);
            }

            startMenuOutAnimation();
            return;
        }
    }
}
