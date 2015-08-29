package zhexian.learn.cnblogs.util;


public class ConfigConstant {

    public static final int BUFFER_SIZE = 4096;
    public static final int ENTITY_TYPE_LOAD_MORE_PLACE_HOLDER = -1;

    public static final int ENTITY_TYPE_NORMAL_ITEM = 0;

    public static final double HTML_FONT_SIZE_NORMAL = 1.2;
    public static final double HTML_FONT_SIZE_BIG = 1.4;
    public static final long MIN_CHANGE_DURATION_MILLION_SECONDS = 500;
    public static final long MIN_TRIGGER_ACTION_BAR_DISTANCE = 10;
    public static final int LIST_ITEM_IMAGE_SIZE_DP = 90;

    public enum NetworkStatus {
        DisConnect,

        Mobile,

        Wifi
    }


    public enum InfoCategory {
        Recommend,

        Recent
    }


    public enum CommentCategory {

        News,

        Blog
    }
}
