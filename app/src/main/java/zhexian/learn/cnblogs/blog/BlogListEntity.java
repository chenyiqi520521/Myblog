package zhexian.learn.cnblogs.blog;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import zhexian.learn.cnblogs.base.BaseEntity;

/**
 * 博客列表数据类
 */
@JsonObject
public class BlogListEntity extends BaseEntity {
    @JsonField
    private long id;

    @JsonField
    private String title;

    @JsonField
    private String summary;

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


}
