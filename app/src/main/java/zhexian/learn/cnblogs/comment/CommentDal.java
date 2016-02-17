package zhexian.learn.cnblogs.comment;

import java.util.List;

import zhexian.learn.cnblogs.base.BaseApplication;
import zhexian.learn.cnblogs.lib.ZHttp;
import zhexian.learn.cnblogs.util.ConfigConstant;
import zhexian.learn.cnblogs.util.DBHelper;

/**
 * Created by Administrator on 2015/8/28.
 */
public class CommentDal {

    private static String generateKey(ConfigConstant.CommentCategory category, long dataID, int pageIndex, int pageSize) {
        String prefix;
        if (category == ConfigConstant.CommentCategory.News)
            prefix = "news";
        else
            prefix = "blog";

        return String.format("%s_comment_%d_%d_%d", prefix, dataID, pageIndex, pageSize);
    }

    public static List<CommentEntity> getCommentFromDisk(ConfigConstant.CommentCategory category, long dataID, int pageIndex, int pageSize) {
        String key = generateKey(category, dataID, pageIndex, pageSize);
        return DBHelper.cache().getList(key, CommentEntity.class);
    }

    private static String generateUrl(ConfigConstant.CommentCategory category, long dataID, int pageIndex, int pageSize) {
        if (category == ConfigConstant.CommentCategory.News)
            return String.format("http://wcf.open.cnblogs.com/news/item/%d/comments/%d/%d", dataID, pageIndex, pageSize);
        else
            return String.format("http://wcf.open.cnblogs.com/blog/post/%d/comments/%d/%d", dataID, pageIndex, pageSize);
    }

    public static List<CommentEntity> getCommentList(BaseApplication baseApp, ConfigConstant.CommentCategory category, long dataID, int pageIndex, int pageSize) {

        List<CommentEntity> listEntity;
        String key = generateKey(category, dataID, pageIndex, pageSize);

        if (baseApp.isNetworkAvailable() == false) {
            listEntity = DBHelper.cache().getList(key, CommentEntity.class);
        } else {
            String xmlStr = generateUrl(category, dataID, pageIndex, pageSize);
            listEntity = CommentEntity.ParseXML(ZHttp.getString(xmlStr));
            DBHelper.cache().save(key, listEntity, CommentEntity.class);
        }
        return listEntity;
    }
}
