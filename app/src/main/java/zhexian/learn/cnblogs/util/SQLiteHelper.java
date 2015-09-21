package zhexian.learn.cnblogs.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import zhexian.learn.cnblogs.lib.ZDate;

/**
 * Created by Administrator on 2015/9/8.
 */
public class SQLiteHelper {

    private static final int TYPE_NEWS = 1;
    private static final int TYPE_BLOG = 2;

    private static SQLiteHelper mSQLiteHelper;
    private MySQLiteDal mSQLiteDal;

    private SQLiteHelper(Context context) {
        mSQLiteDal = new MySQLiteDal(context);
    }

    public static void Init(Context context) {
        if (mSQLiteHelper == null) {
            mSQLiteHelper = new SQLiteHelper(context);
        }
    }

    public static SQLiteHelper getInstance() {
        return mSQLiteHelper;
    }

    public SQLiteDatabase getDb(boolean isRead) {
        if (isRead)
            return mSQLiteDal.getReadableDatabase();
        else
            return mSQLiteDal.getWritableDatabase();
    }

    public void addNewsHistory(int id) {
        addHistory(id, TYPE_NEWS);
    }

    public void addBlogHistory(int id) {
        addHistory(id, TYPE_BLOG);
    }

    private void addHistory(int id, int type) {
        getDb(false).execSQL("delete from viewHistory where id=? and infoType=?", new Integer[]{id, type});
        getDb(false).execSQL("insert into viewHistory(id,infoType,addTime) values(?,?,?)", new Integer[]{id, type, ZDate.getCurrentDate()});
    }


    /**
     * 清空浏览历史记录
     */
    public void cleanHistory(int availableDate) {
        getDb(false).execSQL("delete from viewHistory where addTime<=?", new Integer[]{ZDate.getCurrentDate() - availableDate});
    }

    public List<String> dumpHistory() {

        Cursor cursor = getDb(true).query("viewHistory", null, null, null, null, null, null);

        List<String> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            list.add(String.format("类型：%d,ID：%d,时间：%d", cursor.getInt(0), cursor.getInt(1), cursor.getInt(2)));
        }

        cursor.close();
        return list;
    }

    public boolean isReadNews(int id) {
        return isRead(id, TYPE_NEWS);
    }

    public boolean isReadBlog(int id) {
        return isRead(id, TYPE_BLOG);
    }

    private boolean isRead(int id, int type) {
        Cursor cursor = getDb(true).rawQuery("select count(*) from viewHistory where id=? and infoType=?", new String[]{String.valueOf(id), String.valueOf(type)});

        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }

        int count = cursor.getInt(0);
        cursor.close();

        return count > 0;
    }


    class MySQLiteDal extends SQLiteOpenHelper {

        public MySQLiteDal(Context context) {
            super(context, "cnblogs.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table viewHistory (id INTEGER,infoType INTEGER,addTime INTEGER);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
