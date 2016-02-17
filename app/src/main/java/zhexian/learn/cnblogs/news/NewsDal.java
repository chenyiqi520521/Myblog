package zhexian.learn.cnblogs.news;

import android.text.TextUtils;

import java.util.List;

import zhexian.learn.cnblogs.base.BaseApplication;
import zhexian.learn.cnblogs.image.ZImage;
import zhexian.learn.cnblogs.lib.ZHttp;
import zhexian.learn.cnblogs.util.ConfigConstant;
import zhexian.learn.cnblogs.util.DBHelper;

/**
 * 新闻数据操作类，pageindex从1开始
 */
public class NewsDal {
    public static final String RECOMMEND_STRING = "recommend";
    public static final String RECENT_STRING = "recent";
    static final String endPoint = "http://wcf.open.cnblogs.com/news";
    static final String recommendNewsUrl = String.format("%s/%s/paged/", endPoint, RECOMMEND_STRING);
    static final String recentNewsUrl = String.format("%s/%s/paged/", endPoint, RECENT_STRING);
    static final String newsDetailUrl = String.format("%s/item/", endPoint);

    /**
     * 从本地获取数据
     *
     * @param category
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public static List<NewsListEntity> getNewsListFromDisk(ConfigConstant.NewsCategory category, int pageIndex, int pageSize) {
        String prefix = category == ConfigConstant.NewsCategory.Recommend ? RECOMMEND_STRING : RECENT_STRING;
        String key = String.format("%s_news_%d_%d", prefix, pageIndex, pageSize);

        return DBHelper.cache().getList(key, NewsListEntity.class);
    }

    /**
     * 获取推荐新闻
     * wifi下自动缓存新闻
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public static List<NewsListEntity> getNewsList(BaseApplication baseApp, ConfigConstant.NewsCategory category, int pageIndex, int pageSize) {
        if (baseApp == null)
            return null;

        List<NewsListEntity> newsList = null;
        //默认使用最新
        String prefix = RECENT_STRING;
        String requestUrl = recentNewsUrl;

        if (category == ConfigConstant.NewsCategory.Recommend) {
            prefix = RECOMMEND_STRING;
            requestUrl = recommendNewsUrl;
        }

        String key = String.format("%s_news_%d_%d", prefix, pageIndex, pageSize);

        if (baseApp.isNetworkAvailable() == false) {
            if (DBHelper.cache().exist(key))
                newsList = DBHelper.cache().getList(key, NewsListEntity.class);
        } else {
            String xmlStr = ZHttp.getString(String.format("%s%d/%d", requestUrl, pageIndex, pageSize));
            newsList = NewsListEntity.ParseXML(xmlStr);
            DBHelper.cache().save(key, newsList, NewsListEntity.class);
        }
        return newsList;
    }

    public static NewsDetailEntity getNewsDetail(BaseApplication baseApp, int newsID) {
        NewsDetailEntity entity = null;
        String key = String.format("news_content_%d", newsID);

        if (DBHelper.cache().exist(key)) {
            entity = DBHelper.cache().getObj(key, NewsDetailEntity.class);
            entity.setId(newsID);
        }

        if (entity == null && baseApp.isNetworkAvailable()) {
            String xmlStr = ZHttp.getString(String.format("%s%d", newsDetailUrl, newsID));
            entity = NewsDetailEntity.ParseXML(xmlStr);
            entity.setId(newsID);
            DBHelper.cache().save(key, entity);
        }
        return entity;
    }

    public static void CacheNews(long newsID, String key) {

        String xmlStr = ZHttp.getString(String.format("%s%d", newsDetailUrl, newsID));
        NewsDetailEntity entity = NewsDetailEntity.ParseXML(xmlStr);

        if (entity == null)
            return;

        DBHelper.cache().save(key, entity);

        String imageUrls = entity.getImageUrls();

        if (TextUtils.isEmpty(imageUrls))
            return;

        String[] imageUrlList = imageUrls.split(";");

        if (imageUrlList.length == 0)
            return;

        for (String url : imageUrlList) {
            ZImage.ready().want(url).lowPriority().save();
        }
    }
}
