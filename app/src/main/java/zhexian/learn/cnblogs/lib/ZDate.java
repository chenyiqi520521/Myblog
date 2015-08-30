package zhexian.learn.cnblogs.lib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by kimmy on 2015/5/16.
 */
public class ZDate {


    public static final String TODAY_STRING = "今日";
    public static final String YESTERDAY_STRING = "昨日";
    public static final String THE_DAY_BEFORE_YESTERDAY_STRING = "前日";

    /**
     * 一天中的毫秒数
     */
    public static final long MILLISECONDS_DAY = 86400000L;


    /**
     * 返回 刚刚、xx分钟前、xx小时前，否则返回x月x日x点
     *
     * @param dateStr
     * @return
     */
    public static String FriendlyTime(String dateStr) {
        return FriendlyTime(dateStr, "M月d日 ah点");
    }

    public static String FriendlyTime(String dateStr, String formatStr) {
        Date compareDate;

        try {
            if (dateStr.indexOf('T') >= 0)
                compareDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(dateStr);
            else
                compareDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (ParseException e) {
            return dateStr;
        }

        Date now = new Date();
        long minutesDiff = (now.getTime() - compareDate.getTime()) / (1000 * 60);

        if (minutesDiff <= 1)
            return "刚刚";
        else if (minutesDiff <= 60)
            return String.format("%d分钟前", minutesDiff);
        else if (minutesDiff <= 60 * 24)
            return String.format("%d小时前", minutesDiff / 60);
        else {
            return new SimpleDateFormat(formatStr).format(compareDate);
        }
    }

    /**
     * 解析特定日期格式，返回今日、昨日、前日、X月X日 星期X
     *
     * @param dateStr xml日期格式如： 2015-05-15T23:40:45+08:00
     * @return
     */
    public static String FriendlyDate(String dateStr) {
        try {
            Date compareDate;
            if (dateStr.indexOf('T') >= 0)
                compareDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(dateStr);
            else
                compareDate = SimpleDateFormat.getDateTimeInstance().parse(dateStr);

            return FriendlyDate(compareDate);
        } catch (ParseException e) {
            return "";
        }
    }

    public static int daysOfTwo(Date originalDate, Date compareDateDate) {
        return daysOfTwo(originalDate.getTime() / MILLISECONDS_DAY, compareDateDate.getTime() / MILLISECONDS_DAY);
    }

    public static int daysOfTwo(long originalDay, long compareDay) {
        return (int) (originalDay - compareDay);
    }

    public static String FriendlyDate(Date compareDate) {
        Date nowDate = new Date();
        int dayDiff = daysOfTwo(nowDate, compareDate);

        if (dayDiff <= 0)
            return TODAY_STRING;
        else if (dayDiff == 1)
            return YESTERDAY_STRING;
        else if (dayDiff == 2)
            return THE_DAY_BEFORE_YESTERDAY_STRING;
        else
            return new SimpleDateFormat("M月d日 E").format(compareDate);
    }


    /**
     * 获取当前距离1970-01-01的天数
     *
     * @return
     */
    public static int getCurrentDate() {
        return (int) (new Date().getTime() / MILLISECONDS_DAY);
    }
}
