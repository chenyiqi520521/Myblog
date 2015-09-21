package zhexian.learn.cnblogs.news;

import android.text.TextUtils;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import zhexian.learn.cnblogs.base.BaseEntity;
import zhexian.learn.cnblogs.lib.ZDate;

/**
 * 新闻列表数据类
 */
@JsonObject
public class NewsListEntity extends BaseEntity {
    @JsonField
    private int newsID;
    @JsonField
    private String title;
    @JsonField
    private String iconUrl;
    @JsonField
    private String publishDate;
    @JsonField
    private int recommendAmount;
    @JsonField
    private int commentAmount;

    public static List<NewsListEntity> ParseXML(String xmlStr) {
        if (TextUtils.isEmpty(xmlStr))
            return null;

        XmlPullParser parser;

        try {
            parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new StringReader(xmlStr));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        List<NewsListEntity> newsList = null;
        NewsListEntity newsEntity = null;

        try {
            int type = parser.getEventType();

            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();

                        switch (name) {
                            case "feed":
                                newsList = new ArrayList<>();
                                break;
                            case "entry":
                                newsEntity = new NewsListEntity();
                                break;
                            case "id":
                                if (newsEntity != null)
                                    newsEntity.setNewsID(Integer.parseInt(parser.nextText()));
                                break;
                            case "title":
                                if (newsEntity != null)
                                    newsEntity.setTitle(parser.nextText());
                                break;
                            case "topicIcon":
                                if (newsEntity != null)
                                    newsEntity.setIconUrl(parser.nextText());
                                break;
                            case "published":
                                if (newsEntity != null)
                                    newsEntity.setPublishDate(ZDate.FriendlyDate(parser.nextText()));
                                break;
                            case "diggs":
                                if (newsEntity != null)
                                    newsEntity.setRecommendAmount(Integer.parseInt(parser.nextText()));
                                break;
                            case "comments":
                                if (newsEntity != null)
                                    newsEntity.setCommentAmount(Integer.parseInt(parser.nextText()));
                                break;
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        if ("entry".equals(parser.getName())) {
                            newsList.add(newsEntity);
                            newsEntity = null;
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            return null;
        }
        return newsList;
    }

    public int getNewsID() {
        return newsID;
    }

    public void setNewsID(int newsID) {
        this.newsID = newsID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public int getRecommendAmount() {
        return recommendAmount;
    }

    public void setRecommendAmount(int recommendAmount) {
        this.recommendAmount = recommendAmount;
    }

    public int getCommentAmount() {
        return commentAmount;
    }

    public void setCommentAmount(int commentAmount) {
        this.commentAmount = commentAmount;
    }
}
