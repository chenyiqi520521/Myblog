package zhexian.learn.cnblogs.util;


public class ConfigConstant {

    public static final int MESSAGE_WHAT_NORMAL = -1;

    /**
     * 下拉刷新
     */
    public static final int SWIPE_LOAD_DATA_REFRESH = 1;

    /**
     * 上拉加载更多
     */
    public static final int SWIPE_LOAD_DATA_LOAD_MORE = 1;

    public static final int BUFFER_SIZE = 4096;

    /**
     * 缓存文件有效日期
     */
    public static final int CACHE_AVAILABLE_DAYS = 30;


    public static final double HTML_FONT_SIZE_NORMAL = 1.2;
    public static final double HTML_FONT_SIZE_BIG = 1.4;
    public static final long MIN_CHANGE_DURATION_MILLION_SECONDS = 500;
    public static final long MIN_TRIGGER_ACTION_BAR_DISTANCE = 10;

    public enum NetworkStatus {
        DisConnect,

        Mobile,

        Wifi
    }

    public enum BlogCategory {

        /**
         * 首页
         */
        HOME,

        /**
         * 推荐
         */
        RECOMMEND,


        /**
         * 热门
         */
        HOT
    }

    public enum NewsCategory {
        Recommend,

        Recent
    }


    public enum CommentCategory {

        News,

        Blog
    }
}
