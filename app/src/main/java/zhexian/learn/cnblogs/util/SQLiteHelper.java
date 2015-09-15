package zhexian.learn.cnblogs.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/**
 * Created by Administrator on 2015/9/8.
 */
public class SQLiteHelper {

    private static final long TYPE_NEWS = 1;
    private static final long TYPE_BLOG = 2;

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

    public void addNewsHistory(long id) {
        addHistory(id, TYPE_NEWS);
    }

    public void addBlogHistory(long id) {
        addHistory(id, TYPE_BLOG);
    }

    private void addHistory(long id, long type) {
        getDb(false).execSQL("delete from viewHistory where id=? and infoType=?", new Long[]{id, type});
        getDb(false).execSQL("insert into viewHistory(id,infoType,addTime) values(?,?,?)", new Long[]{id, type, new Date().getTime()});
    }

    public boolean isReadNews(long id) {
        return isRead(id, TYPE_NEWS);
    }

    public boolean isReadBlog(long id) {
        return isRead(id, TYPE_BLOG);
    }

    private boolean isRead(long id, long type) {
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
