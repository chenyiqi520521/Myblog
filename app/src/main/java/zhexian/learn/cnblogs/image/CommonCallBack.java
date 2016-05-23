package zhexian.learn.cnblogs.image;

/**
 * Created by Administrator on 2016/5/23.
 */
public interface CommonCallBack<T> {
    void success(T t);

    void fail();

    void finish();
}
