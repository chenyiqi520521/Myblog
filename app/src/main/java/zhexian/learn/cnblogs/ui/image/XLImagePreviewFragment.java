package zhexian.learn.cnblogs.ui.image;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.image.CommonCallBack;
import zhexian.learn.cnblogs.image.ZImage;

/**
 * 预览fragment
 */
public class XLImagePreviewFragment extends Fragment {
    private static final String PARAM_IMAGE_URL = "PARAM_IMAGE_URL";
    private static final String PARAM_SMALL_IMAGE_URL = "PARAM_SMALL_IMAGE_URL";
    private String mImageUrl;
    private String mSmallImageUrl;
    private View mLoadingView;
    private ZoomImageView mZoomImageView;
    private boolean mIsPlaceHolderHasCache;

    public static XLImagePreviewFragment newInstance(String imageUrl) {
        return newInstance(imageUrl, null);
    }

    public static XLImagePreviewFragment newInstance(String imageUrl, String smallImageUrl) {
        XLImagePreviewFragment fragment = new XLImagePreviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_IMAGE_URL, imageUrl);

        if (!TextUtils.isEmpty(smallImageUrl)) {
            args.putString(PARAM_SMALL_IMAGE_URL, smallImageUrl);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_preview, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getArguments();

        if (data == null)
            return;

        mImageUrl = data.getString(PARAM_IMAGE_URL);
        mSmallImageUrl = data.getString(PARAM_SMALL_IMAGE_URL);
        mIsPlaceHolderHasCache = false;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadingView = view.findViewById(R.id.imagePreview_progressBar);
        mZoomImageView = (ZoomImageView) view.findViewById(R.id.imagePreview_image);
        Bitmap drawable = null;

        if (!TextUtils.isEmpty(mSmallImageUrl)) {
            drawable = ZImage.ready().getFromMemoryCache(mSmallImageUrl);
        }
        //先用小图占位
        if (drawable != null) {
            mIsPlaceHolderHasCache = true;
            mZoomImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mZoomImageView.setImageBitmap(drawable);
        } else {
            //没有小图数据，则显示加载进度条
            mIsPlaceHolderHasCache = false;
            mLoadingView.setVisibility(View.VISIBLE);
        }

        ZImage.ready().want(mImageUrl).getBitmap(new CommonCallBack<Bitmap>() {

            @Override
            public void success(Bitmap bitmap) {
                mZoomImageView.setScaleType(ImageView.ScaleType.MATRIX);
                mZoomImageView.setImageBitmap(bitmap);
                mZoomImageView.refreshScale();
            }

            @Override
            public void fail() {

            }

            @Override
            public void finish() {
                if (!mIsPlaceHolderHasCache)
                    mLoadingView.setVisibility(View.GONE);
            }
        });
    }
}
