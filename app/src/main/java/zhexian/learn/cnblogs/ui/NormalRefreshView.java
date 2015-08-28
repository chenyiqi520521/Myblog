package zhexian.learn.cnblogs.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import zhexian.learn.cnblogs.R;


/**
 * 本代码源于github上一个知名的下拉刷新控件https://github.com/Yalantis/Phoenix
 * 在此基础山进行修改以用于本项目
 */
public class NormalRefreshView extends BaseRefreshView implements Animatable {
    private static final int ANIMATION_DURATION = 1000;
    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private PullToRefreshView mParent;
    private Matrix mMatrix;
    private Animation mAnimation;
    private Paint textPaint;
    private int mTop;
    private int mScreenWidth;
    private int mIndicatorHalfSize;
    private float mIndicatorLeftOffset;
    private float mIndicatorTopOffset;
    private float mRotate = 0.0f;
    private Bitmap mIndicatorBitmap;
    private float mTextLeftOffset;
    private float mTextTopOffset;

    public NormalRefreshView(Context context, final PullToRefreshView parent) {
        super(context, parent);
        mParent = parent;
        mMatrix = new Matrix();

        mIndicatorHalfSize = context.getResources().getDimensionPixelSize(R.dimen.loading_indicator_size) / 2;
        setupAnimations();
        parent.post(new Runnable() {
            @Override
            public void run() {
                initiateDimens(parent.getWidth());
            }
        });
        textPaint = new Paint();
        textPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.loading_text_size));
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mIndicatorBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.load_indicator);
        mIndicatorBitmap = Bitmap.createScaledBitmap(mIndicatorBitmap, mIndicatorHalfSize * 2, mIndicatorHalfSize * 2, true);
        mMatrix = new Matrix();
    }

    public void initiateDimens(int viewWidth) {
        if (viewWidth <= 0 || viewWidth == mScreenWidth)
            return;

        mScreenWidth = viewWidth;
        mIndicatorLeftOffset = 0.3f * (float) mScreenWidth;
        mIndicatorTopOffset = (mParent.getTotalDragDistance() * 0.5f) - mIndicatorHalfSize;
        mTop = -mParent.getTotalDragDistance();
        mTextLeftOffset = mIndicatorHalfSize * 2.3f;
        mTextTopOffset = mParent.getTotalDragDistance() * 0.39f;
    }

    @Override
    public void setPercent(float percent, boolean invalidate) {
        if (invalidate)
            setRotate(percent);
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        mTop += offset;
        invalidateSelf();
    }

    @Override
    public void setTextColor(int color) {
        textPaint.setColor(mParent.getResources().getColor(color));
    }

    @Override
    public void draw(Canvas canvas) {
        if (mScreenWidth <= 0) return;

        final int saveCount = canvas.save();
        drawIndicator(canvas);
        canvas.restoreToCount(saveCount);
    }


    private void drawIndicator(Canvas canvas) {
        boolean isRefresh = mParent.getStatus() == PullToRefreshView.STATUS_REFRESHING || mParent.getStatus() == PullToRefreshView.STATUS_REFRESH_SUCCESS || mParent.getStatus() == PullToRefreshView.STATUS_REFRESH_FAIL;
        float degree = (isRefresh ? 360 : -360) * mRotate;
        mMatrix.reset();
        mMatrix.postRotate(degree, mIndicatorHalfSize, mIndicatorHalfSize);

        canvas.translate(mIndicatorLeftOffset, mIndicatorTopOffset + mTop);
        canvas.drawBitmap(mIndicatorBitmap, mMatrix, null);
        canvas.drawText(getDescription(mParent.getStatus()), mTextLeftOffset, mTextTopOffset, textPaint);
    }

    public void setRotate(float rotate) {
        mRotate = rotate;
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void start() {
        mAnimation.reset();
        mParent.startAnimation(mAnimation);
    }

    @Override
    public void stop() {
        mParent.clearAnimation();
    }

    private void setupAnimations() {
        mAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setRotate(interpolatedTime);
            }
        };
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setInterpolator(LINEAR_INTERPOLATOR);
        mAnimation.setDuration(ANIMATION_DURATION);
    }
}
