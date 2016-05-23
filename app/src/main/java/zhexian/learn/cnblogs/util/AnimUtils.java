package zhexian.learn.cnblogs.util;

import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;

/**
 * Created by Administrator on 2016/5/18.
 */
public class AnimUtils {
    /**
     * 创建一个颜色的动画
     * 可以逆向运行
     *
     * @param context
     * @param animatorID
     * @param target
     * @return
     */
    public static ObjectAnimator generateColorAnimator(Context context, int animatorID, Object target) {
        ObjectAnimator colorAnimation = (ObjectAnimator) AnimatorInflater.loadAnimator(context, animatorID);
        colorAnimation.setTarget(target);
        colorAnimation.setEvaluator(new ArgbEvaluator());
        return colorAnimation;
    }
}
