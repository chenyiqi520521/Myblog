package zhexian.learn.cnblogs.news;


import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.Serializable;
import java.io.StringReader;

/**
 * Created by Administrator on 2015/5/19.
 */
@JsonObject
public class NewsDetailEntity implements Serializable {
    @JsonField
    private long id;
    @JsonField
    private String title;
    @JsonField
    private String source;
    @JsonField
    private String publishTime;
    @JsonField
    private String content;
    @JsonField
    private String imageUrls;

    public static NewsDetailEntity ParseXML(String xmlStr) {
        XmlPullParser parser;

        try {
            parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new StringReader(xmlStr));

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return null;
        }
        NewsDetailEntity entity = null;

        try {
            int type = parser.getEventType();

            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();

                        switch (name) {
                            case "NewsBody":
                                entity = new NewsDetailEntity();
                                break;
                            case "Title":
                                if (entity != null)
                                    entity.setTitle(parser.nextText());
                                break;
                            case "SourceName":
                                if (entity != null)
                                    entity.setSource(parser.nextText());
                                break;
                            case "SubmitDate":
                                if (entity != null)
                                    entity.setPublishTime(parser.nextText());
                                break;
                            case "Content":
                                if (entity != null)
                                    entity.setContent(parser.nextText());
                                break;
                            case "ImageUrl":
                                if (entity != null)
                                    entity.setImageUrls(parser.nextText());
                                break;
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return entity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }
}
