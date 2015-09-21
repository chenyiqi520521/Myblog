package zhexian.learn.cnblogs.image;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.SoftReference;

import zhexian.learn.cnblogs.R;
import zhexian.learn.cnblogs.base.BaseApplication;
import zhexian.learn.cnblogs.util.DBHelper;


/**
 * 图片管理类，访问网络图片，保存在本地
 * 使用方式参考RequestCreator内部类
 * 完整的使用方式ZImage.ready().want("请求地址").reSize(图片尺寸).cache(缓存方式).canQueryByHttp(是否可以走流量访问图片).lowPriority(将请求放到栈的底部).empty(图片占位符).into(图片控件);
 */
public class ZImage {

    private static ZImage mZImage;
    private LruCache<String, SoftReference<Bitmap>> mMemoryCache;
    private BaseApplication mBaseApp;
    private Bitmap placeHolderBitmap;

    private ZImage(BaseApplication baseApp) {
        mBaseApp = baseApp;
        placeHolderBitmap = BitmapFactory.decodeResource(mBaseApp.getResources(), R.mipmap.image_place_holder);

        mMemoryCache = new LruCache<String, SoftReference<Bitmap>>(mBaseApp.getImageCachePoolSize()) {
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    public static void init(BaseApplication baseApp) {
        if (mZImage == null)
            mZImage = new ZImage(baseApp);
    }

    /**
     * 获得图片管理类
     *
     * @return
     */
    public static ZImage ready() {
        if (mZImage == null) {
            throw new RuntimeException("ZImage需要被初始化才能使用，建议在程序的入口处使用Init()");
        }
        return mZImage;
    }

    /**
     * 构造器起手式，从一个资源开始
     *
     * @param url
     * @return
     */
    public RequestCreator want(String url) {
        return new RequestCreator(url);
    }

    /**
     * 使用本地资源
     *
     * @param imageView
     * @param placeHolder
     */
    public void loadResource(ImageView imageView, int placeHolder) {
        if (placeHolder <= 0) {
            imageView.setImageBitmap(placeHolderBitmap);
            return;
        }

        String key = String.valueOf(placeHolder);
        Bitmap bitmap = getFromMemoryCache(key);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        bitmap = BitmapFactory.decodeResource(mBaseApp.getResources(), placeHolder);

        if (bitmap != null) {
            putToMemoryCache(key, bitmap);
            imageView.setImageBitmap(bitmap);
            return;
        }

        imageView.setImageBitmap(placeHolderBitmap);
    }

    Bitmap getFromMemoryCache(String url) {

        if (mMemoryCache.get(url) == null)
            return null;

        Bitmap bitmap = mMemoryCache.get(url).get();

        if (bitmap == null) {
            mMemoryCache.remove(url);
            return null;
        }
        return bitmap;
    }

    void putToMemoryCache(String url, Bitmap bitmap) {
        if (bitmap != null && bitmap.getByteCount() > 0)
            mMemoryCache.put(url, new SoftReference<>(bitmap));
    }


    /**
     * 加载图片，经过内存、磁盘、两层缓存如果还没找到，则走http访问网络资源
     *
     * @param url          地址
     * @param imageView    图片控件
     * @param width        图片宽度
     * @param height       图片高度
     * @param cacheType    缓存类型
     * @param workType     队列优先级
     * @param placeHolder  占位图片
     * @param canQueryHttp 是否可以走流量获取图片
     */
    private void load(String url, ImageView imageView, int width, int height, CacheType cacheType, ImageTaskManager.WorkType workType, int placeHolder, boolean canQueryHttp) {
        if (TextUtils.isEmpty(url)) {
            loadResource(imageView, placeHolder);
            return;
        }

        Bitmap bitmap = getFromMemoryCache(url);
        imageView.setTag(url);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        loadResource(imageView, placeHolder);

        bitmap = DBHelper.cache().getBitmap(url, width, height);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);

            if (cacheType == CacheType.DiskMemory || cacheType == CacheType.Memory)
                putToMemoryCache(url, bitmap);

            return;
        }

        if (canQueryHttp)
            ImageTaskManager.getInstance().addTask(new LoadImageTask(mBaseApp, imageView, url, width, height, cacheType), workType);
    }

    /**
     * 缓存类型
     */
    public enum CacheType {

        /**
         * 不缓存
         */
        None,


        /**
         * 缓存到硬盘
         */
        Disk,

        /**
         * 缓存到内存
         */
        Memory,

        /**
         * 缓存到硬盘和内存
         */
        DiskMemory
    }


    /**
     * 请求构造器
     */
    public class RequestCreator {
        /**
         * 请求地址
         */
        String url;

        /**
         * 优先级,默认后进先出
         */
        ImageTaskManager.WorkType priority = ImageTaskManager.WorkType.LIFO;

        /**
         * 占位图
         */
        int placeHolder = -1;

        /**
         * 缓存类型，默认内存缓存，基于LRU算法，不用担心内存爆炸
         */
        CacheType cacheType = CacheType.DiskMemory;

        /**
         * 图片的宽度
         */
        int width = mBaseApp.getScreenWidth();

        /**
         * 图片的高度
         */
        int height = mBaseApp.getScreenHeight();

        /**
         * 能否通过http请求网络数据
         */
        boolean canQueryByHttp = true;

        public RequestCreator(String url) {
            this.url = url;
            this.canQueryByHttp = mBaseApp.canRequestImage();
        }

        /**
         * 占位图
         *
         * @param resID 本地图片资源 R.drawable.
         * @return
         */
        public RequestCreator empty(int resID) {
            placeHolder = resID;
            return this;
        }

        /**
         * 缓存
         *
         * @param cacheType 缓存类型
         * @return
         */
        public RequestCreator cache(CacheType cacheType) {
            this.cacheType = cacheType;
            return this;
        }

        /**
         * 优先级，默认后进先出。使用本方法降低优先级
         *
         * @return
         */
        public RequestCreator lowPriority() {
            priority = ImageTaskManager.WorkType.LILO;
            return this;
        }

        /**
         * 对图片尺寸进行缩放，节约内存
         *
         * @param width  图片宽度，默认屏幕宽度
         * @param height 图片高度，默认屏幕高度
         * @return
         */
        public RequestCreator reSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public RequestCreator canQueryByHttp(boolean canQueryByHttp) {
            this.canQueryByHttp = canQueryByHttp;
            return this;
        }

        /**
         * 载入图片到控件
         *
         * @param imageView
         */
        public void into(ImageView imageView) {
            mZImage.load(url, imageView, width, height, cacheType, priority, placeHolder, canQueryByHttp);
        }

        /**
         * 下载图片
         */
        public void save() {
            if (DBHelper.cache().exist(url))
                return;

            ImageTaskManager.getInstance().addTask(new SaveImageTask(mBaseApp, url, width, height), priority);
        }
    }
}
