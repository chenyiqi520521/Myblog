package zhexian.learn.cnblogs.comment;

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
 * 评论类，新闻与博客通用
 */
@JsonObject
public class CommentEntity extends BaseEntity {
    @JsonField
    private String publishTime;
    @JsonField
    private String userName;
    @JsonField
    private String userHomeUrl;
    @JsonField
    private String content;

    public static List<CommentEntity> ParseXML(String xmlStr) {
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
        List<CommentEntity> commentList = null;
        CommentEntity commentEntity = null;

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
                                commentList = new ArrayList<>();
                                break;
                            case "entry":
                                commentEntity = new CommentEntity();
                                break;
                            case "published":
                                if (commentEntity != null)
                                    commentEntity.setPublishTime(ZDate.FriendlyTime(parser.nextText()));
                                break;
                            case "name":
                                if (commentEntity != null)
                                    commentEntity.setUserName(parser.nextText());
                                break;
                            case "uri":
                                if (commentEntity != null)
                                    commentEntity.setUserHomeUrl(parser.nextText());
                                break;
                            case "content":
                                if (commentEntity != null)
                                    commentEntity.setContent(parser.nextText());
                                break;
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        if ("entry".equals(parser.getName())) {
                            commentList.add(commentEntity);
                            commentEntity = null;
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            return null;
        }
        return commentList;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserHomeUrl() {
        return userHomeUrl;
    }

    public void setUserHomeUrl(String userHomeUrl) {
        this.userHomeUrl = userHomeUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
