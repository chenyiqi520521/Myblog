package zhexian.learn.cnblogs.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import zhexian.learn.cnblogs.util.Utils;

/**
 * Created by 陈俊杰 on 2015/8/20.
 * 本地磁盘管理类
 */
public class ZDisk {

    /**
     * 存储绝对路径的地址
     */
    private final String mStoreDir;

    public ZDisk(String mStoreDir) {
        this.mStoreDir = mStoreDir;
        ZIO.mkDirs(mStoreDir);
    }

    /**
     * 获取资源关键路径，并和存储路径拼接
     * 如 http://images.cnitblog.com/news_topic/apple.png 到 news_topic/apple.png
     *
     * @param url 资源地址
     * @return
     */
    public String trans2Local(String url) {
        return Utils.transToLocal(url, mStoreDir);
    }

    /**
     * 获取根目录文件
     *
     * @return
     */
    public String getRootDir() {
        return mStoreDir;
    }

    /**
     * 文件是否被保存在本地
     *
     * @param url
     * @return
     */
    public boolean exist(String url) {
        url = trans2Local(url);
        File file = new File(url);
        return file.exists();
    }

    /**
     * 保存输入流到本地
     *
     * @param url
     * @param inputStream
     * @return
     */
    public boolean save(String url, InputStream inputStream) {
        return save(url, ZIO.InputStreamToByte(inputStream));
    }

    /**
     * 保存字符串到文件
     *
     * @param url
     * @param content
     * @return
     */
    public boolean save(String url, String content) {
        url = trans2Local(url);
        return ZIO.writeToFile(url, content);
    }

    /**
     * 写入bytes
     *
     * @param url
     * @param bytes
     * @return
     */
    public boolean save(String url, byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return false;

        url = trans2Local(url);
        File file = new File(url);

        if (file.exists())
            return true;

        ZIO.createNewFile(file);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("存储出错", e.getMessage());
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 写入对象
     *
     * @param url
     * @param obj
     */
    public <T> boolean save(String url, T obj) {
        if (obj == null)
            return true;

        url = trans2Local(url);
        File file = new File(url);

        if (file.exists())
            return true;

        ZIO.createNewFile(file);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            LoganSquare.serialize(obj, fos);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 写入数组
     *
     * @param url
     * @param list
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> boolean save(String url, List<T> list, Class<T> tClass) {
        if (list == null || list.size() == 0)
            return true;

        url = trans2Local(url);
        File file = new File(url);

        if (file.exists())
            return true;

        ZIO.createNewFile(file);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            LoganSquare.serialize(list, fos, tClass);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getString(String url) {
        url = trans2Local(url);

        if (!new File(url).exists())
            return null;

        return ZIO.readFromFile(url);
    }

    /**
     * 获取缩放后的本地图片
     *
     * @param url    路径
     * @param width  宽
     * @param height 高
     * @return
     */
    public Bitmap getBitmap(String url, int width, int height) {
        url = trans2Local(url);

        if (!new File(url).exists())
            return null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int inSampleSize = 1;

        if (srcHeight > height || srcWidth > width) {
            if (srcWidth > srcHeight)
                inSampleSize = Math.round(srcHeight / height);
            else
                inSampleSize = Math.round(srcWidth / width);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(url, options);
    }

    /**
     * 获取对象
     *
     * @param url    路径
     * @param tClass 类型
     * @param <T>
     * @return
     */
    public <T> T getObj(String url, Class<T> tClass) {
        url = trans2Local(url);

        File file = new File(url);
        if (!file.exists())
            return null;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            T t = LoganSquare.parse(fileInputStream, tClass);
            fileInputStream.close();
            return t;
        } catch (IOException e) {
            e.printStackTrace();
            ZIO.deleteFile(url);
        }
        return null;
    }

    /**
     * 获取对象数组
     *
     * @param url    路径
     * @param tClass 类型
     * @param <T>
     * @return
     */
    public <T> List<T> getList(String url, Class<T> tClass) {
        url = trans2Local(url);

        File file = new File(url);
        if (!file.exists())
            return null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            List<T> t = LoganSquare.parseList(fileInputStream, tClass);
            fileInputStream.close();
            return t;
        } catch (IOException e) {
            e.printStackTrace();
            ZIO.deleteFile(url);
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param url
     */
    public void delete(String url) {
        url = trans2Local(url);
        ZIO.deleteFile(url);
    }

    /**
     * 清空文件夹
     */
    public void clean() {
        ZIO.emptyChildDir(new File(mStoreDir));
    }

    /**
     * 清空文件夹中过期的文件
     *
     * @param days 距离当前有效天数，比如30，代表30天以前的文件要被清除
     */
    public void cleanExpireFile(int days) {
        ZIO.emptyChildDir(new File(mStoreDir), days);
    }

    /**
     * 获取文件夹容量描述
     * 单位取决于容量数据，比如100MB，1GB
     *
     * @return
     */
    public String getDirSize() {
        return ZIO.getDirSizeInfo(mStoreDir, "空空如也");
    }
}
