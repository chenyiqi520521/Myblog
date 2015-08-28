package zhexian.learn.cnblogs.base;

/**
 * Created by 陈俊杰 on 2015/8/28.
 * 数据父类
 */
public class BaseEntity {
    private String dataText;
    private int dataType;

    public String getDataText() {
        return dataText;
    }

    public void setDataText(String dataText) {
        this.dataText = dataText;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}
