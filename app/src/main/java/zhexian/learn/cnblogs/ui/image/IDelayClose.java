package zhexian.learn.cnblogs.ui.image;

/**
 * 延迟关闭
 * Created by 陈俊杰 on 2016/5/6.
 */
public interface IDelayClose {
    String ACTION_SHOW_MENU = "ACTION_SHOW_MENU";

    void startClose();

    void cancelClose();

    void doAction(String actionCode, Object param);
}
