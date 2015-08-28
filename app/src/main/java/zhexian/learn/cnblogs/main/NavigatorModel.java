package zhexian.learn.cnblogs.main;

/**
 * Created by Administrator on 2015/8/28.
 */
public class NavigatorModel {
    private int id;
    private String name;
    private Boolean isCollected;

    public NavigatorModel(int id, String name, Boolean isCollected) {
        this.id = id;
        this.name = name;
        this.isCollected = isCollected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isCollected() {
        return isCollected;
    }

    public void setIsCollected(Boolean isCollected) {
        this.isCollected = isCollected;
    }
}
