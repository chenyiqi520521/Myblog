package zhexian.learn.cnblogs.lib;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import zhexian.learn.cnblogs.util.ConfigConstant;

/**
 * Created by Administrator on 2015/8/28.
 */
public class ZIO {

    /**
     * 清空所有子文件
     *
     * @param dir
     * @return
     */
    public static boolean emptyChildDir(File dir) {
        for (File f : dir.listFiles())
            f.delete();

        return true;
    }


    /**
     * 清空所有子文件中过期的文件
     *
     * @param dir          清空目录
     * @param availableDay 有效期
     * @return
     */
    public static boolean emptyChildDir(File dir, int availableDay) {
        long currentDay = ZDate.getCurrentDate();

        for (File f : dir.listFiles()) {
            if (isFileExpiry(f, currentDay, availableDay))
                f.delete();
        }
        return true;
    }

    /**
     * 清空文件夹
     *
     * @param dir
     * @return
     */
    public static boolean emptyDir(File dir) {
        if (dir.isDirectory()) {
            for (File f : dir.listFiles())
                emptyDir(f);
        }
        return dir.delete();
    }

    public static boolean isExist(String url) {
        File file = new File(url);
        return file.exists();
    }

    public static boolean deleteFile(String url) {
        File file = new File(url);
        return file.delete();
    }

    public static void mkDirs(String url) {
        File file = new File(url);
        if (!file.exists())
            file.mkdirs();
    }

    public static boolean createNewFile(File file) {
        if (file.exists())
            return true;

        try {
            file.createNewFile();
            return true;
        } catch (IOException e) {
            Log.e("存储出错", e.getMessage());
            return false;
        }
    }

    public static boolean writeToFile(String url, String content) {
        FileWriter fs = null;
        try {
            fs = new FileWriter(url);
            fs.write(content);
            return true;
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            return false;
        } finally {
            if (fs != null)
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


    public static String readFromFile(String url) {
        BufferedReader reader = null;
        try {
            FileReader fr = new FileReader(url);
            reader = new BufferedReader(fr);
            StringBuilder jsonStr = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStr.append(line);
            }
            return jsonStr.toString();

        } catch (IOException e) {
            return null;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static String readString(InputStream in) {
        String content;
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ((content = reader.readLine()) != null) {
                sb.append(content);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb
                .toString();
    }

    public static String getDirSizeInfo(String url, String emptyDescription) {

        File file = new File(url);
        if (!file.exists())
            return emptyDescription;

        long size = getDirCapacity(file);

        if (size == 0)
            return emptyDescription;

        return formatFileSize(size);
    }


    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString;
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }


    public static long getDirCapacity(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirCapacity(file);
            }
        }
        return dirSize;
    }

    public static long getDirCount(File dir) {
        long count;
        File[] files = dir.listFiles();
        count = files.length;
        for (File file : files) {
            if (file.isDirectory()) {
                count = count + getDirCount(file);
                count--;
            }
        }
        return count;
    }

    /**
     * 将InputStream转换成byte数组
     *
     * @param in InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] InputStreamToByte(InputStream in) {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[ConfigConstant.BUFFER_SIZE];
        int count;
        try {
            while ((count = in.read(data, 0, ConfigConstant.BUFFER_SIZE)) != -1)
                outStream.write(data, 0, count);

            data = null;
            in.close();
            byte[] buffer = outStream.toByteArray();
            outStream.close();
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查文件是否过期
     *
     * @param file         文件
     * @param availableDay 有效天数
     * @return
     */
    public static boolean isFileExpiry(File file, int availableDay) {
        long fileDay = file.lastModified() / ZDate.MILLISECONDS_DAY;
        return ZDate.daysOfTwo(ZDate.getCurrentDate(), fileDay) > availableDay;
    }

    /**
     * 检查文件是否过期
     *
     * @param file         文件
     * @param currentDay   当前日期
     * @param availableDay 有效天数
     * @return
     */
    public static boolean isFileExpiry(File file, long currentDay, int availableDay) {
        long fileDay = file.lastModified() / ZDate.MILLISECONDS_DAY;
        return ZDate.daysOfTwo(currentDay, fileDay) > availableDay;
    }
}
