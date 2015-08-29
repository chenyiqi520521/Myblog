package zhexian.learn.cnblogs.util;

import android.webkit.WebView;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zhexian.learn.cnblogs.base.BaseApplication;
import zhexian.learn.cnblogs.image.ZImage;
import zhexian.learn.cnblogs.lib.ZDate;
import zhexian.learn.cnblogs.lib.ZIO;
import zhexian.learn.cnblogs.news.NewsDetailEntity;

/**
 * Created by Administrator on 2015/8/29.
 */
public class HtmlHelper {
    private static final String mDayImagePlaceHolderName = "click_load_day.png";
    private static final String mNightImagePlaceHolderName = "click_load_night.png";
    private static HtmlHelper ourInstance;
    private BaseApplication mApp;
    private String mHtmlString;
    private String mDayCssString;
    private String mNightCssString;

    private HtmlHelper(BaseApplication baseApp) {
        mApp = baseApp;

        try {
            mHtmlString = ZIO.readString(mApp.getAssets().open("content.html"));
            mDayCssString = ZIO.readString(mApp.getAssets().open("style_day.css"));
            mNightCssString = ZIO.readString(mApp.getAssets().open("style_night.css"));

            //将占位图片拷贝到缓存目录
            if (!DBHelper.cache().exist(mDayImagePlaceHolderName))
                DBHelper.cache().save(mDayImagePlaceHolderName, mApp.getAssets().open(mDayImagePlaceHolderName));

            if (!DBHelper.cache().exist(mNightImagePlaceHolderName))
                DBHelper.cache().save(mNightImagePlaceHolderName, mApp.getAssets().open(mNightImagePlaceHolderName));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HtmlHelper getInstance() {
        return ourInstance;
    }

    public static void init(BaseApplication baseApp) {
        if (ourInstance == null)
            ourInstance = new HtmlHelper(baseApp);
    }

    /**
     * 将数据对象处理成本地可以展示的web页面内容
     *
     * @param entity
     * @return
     */
    public String processHtml(NewsDetailEntity entity) {
        String content = decorateIMGTag(entity.getContent(), mApp.getScreenWidthInDP(), mApp.canRequestImage(), mApp.isNightMode());
        String source = String.format("%s %s 发布", entity.getSource(), ZDate.FriendlyTime(entity.getPublishTime()));
        String htmlString = new String(mHtmlString);
        htmlString = htmlString.replace("{style}", getHtmlCssString()).replace("{title}", entity.getTitle()).replace("{fontSize}", getFontSize()).replace("{source}", source).replace("{html}", content);
        return htmlString;
    }

    /**
     * 将数据渲染到webView上
     *
     * @param webView
     * @param entity
     */
    public void render(WebView webView, NewsDetailEntity entity) {
        String htmlContent = HtmlHelper.getInstance().processHtml(entity);
        webView.loadDataWithBaseURL(String.format("file://%s", DBHelper.cache().getRootDir()), htmlContent, "text/html", "utf-8", null);
    }

    public String getHtmlString() {
        return mHtmlString;
    }

    public String getHtmlCssString() {
        return mApp.isNightMode() ? mNightCssString : mDayCssString;
    }

    public String getFontSize() {
        double fontSize = mApp.isBigFont() ? ConfigConstant.HTML_FONT_SIZE_BIG : ConfigConstant.HTML_FONT_SIZE_NORMAL;
        return String.valueOf(fontSize);
    }


    public String decorateIMGTag(String htmlContent, int screenWidth, boolean canRequest, boolean isNight) {
        Pattern patternImgSrc = Pattern.compile("<img(.+?)src=\"(.+?)\"(.+?)/>");
        Matcher localMatcher = patternImgSrc.matcher(htmlContent);

        screenWidth -= 16;
        String placeHolder = isNight ? mNightImagePlaceHolderName : mDayImagePlaceHolderName;
        StringBuffer sb = new StringBuffer();

        while (localMatcher.find()) {

            String jsStr = "onclick=\"showImage(this,'$2')\"";
            String src;

            String imageUrl = localMatcher.group(2);

            if (DBHelper.cache().exist(imageUrl))
                src = Utils.transToLocal(imageUrl);
            else {
                ZImage.ready().want(imageUrl).save();

                if (canRequest)
                    src = "$2";
                else
                    src = placeHolder;
            }
            localMatcher.appendReplacement(sb, String.format("<img src='%s' %s style='max-width:%dpx'/>", src, jsStr, screenWidth));
        }

        localMatcher.appendTail(sb);
        return sb.toString();
    }
}
