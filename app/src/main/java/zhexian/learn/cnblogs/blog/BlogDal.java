package zhexian.learn.cnblogs.blog;

import android.text.TextUtils;

import java.util.List;

import zhexian.learn.cnblogs.base.BaseApplication;
import zhexian.learn.cnblogs.lib.ZHttp;
import zhexian.learn.cnblogs.util.ConfigConstant;
import zhexian.learn.cnblogs.util.DBHelper;
import zhexian.learn.cnblogs.util.Utils;

/**
 * Created by 陈俊杰 on 2015/8/30.
 * 博客数据层
 */
class BlogDal {
    private static final String endPoint = "http://wcf.open.cnblogs.com/blog";
    private static final String HOME_STRING = "sitehome";
    private static final String RECOMMEND_STRING = "TenDaysTopDiggPosts";
    private static final String HOT_STRING = "48HoursTopViewPosts";

    private static final String HOME_URL = String.format("%s/%s/paged/", endPoint, HOME_STRING);
    private static final String RECOMMEND_URL = String.format("%s/%s/", endPoint, RECOMMEND_STRING);
    private static final String HOT_URL = String.format("%s/%s/", endPoint, HOT_STRING);
    private static final String DETAIL_URL = String.format("%s/post/body/", endPoint);

    public static List<BlogEntity> getBlogsFromDisk(ConfigConstant.BlogCategory category, int pageIndex, int pageSize) {
        String key;

        if (category == ConfigConstant.BlogCategory.HOME)
            key = String.format("%s_blog_%d_%d", HOME_STRING, pageIndex, pageSize);
        else
            key = String.format("%s_blog", category == ConfigConstant.BlogCategory.RECOMMEND ? RECOMMEND_STRING : HOT_STRING);

        return DBHelper.cache().getList(key, BlogEntity.class);
    }


    /**
     * 分页获取首页博客
     *
     * @param baseApp
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public static List<BlogEntity> getHomeBlogs(BaseApplication baseApp, int pageIndex, int pageSize) {
        if (baseApp == null)
            return null;

        List<BlogEntity> blogList = null;
        String key = String.format("%s_blog_%d_%d", HOME_STRING, pageIndex, pageSize);

        if (!baseApp.isNetworkAvailable()) {
            if (DBHelper.cache().exist(key))
                blogList = DBHelper.cache().getList(key, BlogEntity.class);
        } else {
            String xmlStr = ZHttp.getString(String.format("%s%d/%d", HOME_URL, pageIndex, pageSize));
            blogList = BlogEntity.ParseXML(xmlStr);
            DBHelper.cache().save(key, blogList, BlogEntity.class);
        }
        return blogList;
    }

    /**
     * 获得推荐博客
     *
     * @param baseApp
     * @return
     */
    public static List<BlogEntity> getRecommendBlogs(BaseApplication baseApp) {
        return getList(baseApp, ConfigConstant.BlogCategory.RECOMMEND);
    }

    /**
     * 获得热门博客
     *
     * @param baseApp
     * @return
     */
    public static List<BlogEntity> getHotBlogs(BaseApplication baseApp) {
        return getList(baseApp, ConfigConstant.BlogCategory.HOT);
    }

    /**
     * 获取推荐、热门博客（不分页，一次取100条）
     *
     * @param baseApp
     * @param category
     * @return
     */
    private static List<BlogEntity> getList(BaseApplication baseApp, ConfigConstant.BlogCategory category) {
        if (baseApp == null)
            return null;

        String prefix;
        String requestUrl;

        if (category == ConfigConstant.BlogCategory.RECOMMEND) {
            prefix = RECOMMEND_STRING;
            requestUrl = RECOMMEND_URL;
        } else if (category == ConfigConstant.BlogCategory.HOT) {
            prefix = HOT_STRING;
            requestUrl = HOT_URL;
        } else
            return null;

        List<BlogEntity> blogList = null;
        String key = String.format("%s_blog", prefix);

        if (!baseApp.isNetworkAvailable()) {
            if (DBHelper.cache().exist(key))
                blogList = DBHelper.cache().getList(key, BlogEntity.class);
        } else {
            String xmlStr = ZHttp.getString(String.format("%s100", requestUrl));

            if (TextUtils.isEmpty(xmlStr))
                return null;

            blogList = BlogEntity.ParseXML(xmlStr);
            DBHelper.cache().save(key, blogList, BlogEntity.class);
        }
        return blogList;
    }

    /**
     * 获取博客的文章内容
     *
     * @param baseApp
     * @param blogId
     * @return
     */
    public static String getBlogContent(BaseApplication baseApp, long blogId) {
        if (baseApp == null)
            return null;

        String key = String.format("blog_%d", blogId);

        if (baseApp.isNetworkAvailable() == false) {
            if (DBHelper.cache().exist(key))
                return DBHelper.cache().getString(key);
        } else {
            String result = ZHttp.getString(String.format("%s%d", DETAIL_URL, blogId));

            int startLength = 46;
            int endLength = 9;

            if (TextUtils.isEmpty(result))
                return null;

            String contentStr = Utils.transHtmlTag(result.substring(startLength, result.length() - endLength));
            DBHelper.cache().save(key, contentStr);
            return contentStr;
        }
        return null;
    }
}
