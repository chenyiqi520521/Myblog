package zhexian.learn.cnblogs.base.adapters;

/**
 * 添加了一个属性用于支持列表空的状况
 * Created by 陈俊杰 on 2016/1/18.
 */
public class M_EmptyHolderFeature {
    private boolean isDataEmpty;

    public boolean isDataEmpty() {
        return isDataEmpty;
    }

    public void setIsDataEmpty(boolean isDataEmpty) {
        this.isDataEmpty = isDataEmpty;
    }
}
