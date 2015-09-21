package zhexian.learn.cnblogs.blog;

import android.text.TextUtils;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import zhexian.learn.cnblogs.base.BaseEntity;

/**
 * 博客数据实体
 */
@JsonObject
public class BlogEntity extends BaseEntity implements Serializable {
    @JsonField
    private int id;

    @JsonField
    private String title;

    @JsonField
    private String published;

    @JsonField
    private String authorName;

    @JsonField
    private String authorAvatar;

    @JsonField
    private String blogapp;

    @JsonField
    private int recommendAmount;

    @JsonField
    private int commentAmount;

    @JsonField
    private int viewAmount;

    @JsonField
    private String content;

    public static List<BlogEntity> ParseXML(String xmlStr) {
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

        List<BlogEntity> blogList = null;
        BlogEntity blogEntity = null;

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
                                blogList = new ArrayList<>();
                                break;
                            case "entry":
                                blogEntity = new BlogEntity();
                                break;
                            case "id":
                                if (blogEntity != null)
                                    blogEntity.setId(Integer.parseInt(parser.nextText()));
                                break;
                            case "title":
                                if (blogEntity != null)
                                    blogEntity.setTitle(parser.nextText());
                                break;
                            case "name":
                                if (blogEntity != null)
                                    blogEntity.setAuthorName(parser.nextText());
                                break;

                            case "avatar":
                                if (blogEntity != null)
                                    blogEntity.setAuthorAvatar(parser.nextText());
                                break;

                            case "blogapp":
                                if (blogEntity != null)
                                    blogEntity.setBlogapp(parser.nextText());
                                break;

                            case "published":
                                if (blogEntity != null)
                                    blogEntity.setPublished(parser.nextText());
                                break;
                            case "diggs":
                                if (blogEntity != null)
                                    blogEntity.setRecommendAmount(Integer.parseInt(parser.nextText()));
                                break;

                            case "views":
                                if (blogEntity != null)
                                    blogEntity.setViewAmount(Integer.parseInt(parser.nextText()));
                                break;
                            case "comments":
                                if (blogEntity != null)
                                    blogEntity.setCommentAmount(Integer.parseInt(parser.nextText()));
                                break;
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        if ("entry".equals(parser.getName())) {
                            blogList.add(blogEntity);
                            blogEntity = null;
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            return null;
        }
        return blogList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getBlogapp() {
        return blogapp;
    }

    public void setBlogapp(String blogapp) {
        this.blogapp = blogapp;
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

    public int getViewAmount() {
        return viewAmount;
    }

    public void setViewAmount(int viewAmount) {
        this.viewAmount = viewAmount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
