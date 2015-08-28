package zhexian.learn.cnblogs.main;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/28.
 */
public class NavigatorDal {
    public static final int ID_NEWS = 1;
    public static final int ID_BLOGS = 2;

    private static NavigatorDal mDal;

    public static NavigatorDal getInstance() {
        if (mDal == null)
            mDal = new NavigatorDal();

        return mDal;
    }

    public String getNavigatorName(int id) {
        switch (id) {
            case ID_NEWS:
                return "精选资讯";
            case ID_BLOGS:
                return "推荐博客";
            default:
                return "";
        }
    }

    public List<NavigatorModel> getList() {
        List<NavigatorModel> list = new ArrayList<>();

        list.add(new NavigatorModel(ID_NEWS, getNavigatorName(ID_NEWS), false));
        list.add(new NavigatorModel(ID_BLOGS, getNavigatorName(ID_BLOGS), false));

        return list;
    }
}
