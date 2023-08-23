package cn.yapeteam.yolbi.util.animation;

import cn.yapeteam.yolbi.values.Visibility;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;

public class AnimationUtil {

    public static ModeValue<AnimationType> getAnimationType(AnimationType defaultAnim) {
        return new ModeValue<>("Animation", defaultAnim, AnimationType.values());
    }

    public static ModeValue<AnimationType> getAnimationType(Visibility visibility, AnimationType defaultAnim) {
        return new ModeValue<>("Animation", visibility, defaultAnim, AnimationType.values());
    }

    public static NumberValue<Integer> getAnimationDuration(int defaultDuration) {
        return new NumberValue<>("Animation duration", defaultDuration, 0, 1000, 25);
    }

    public static NumberValue<Integer> getAnimationDuration(Visibility visibility, int defaultDuration) {
        return new NumberValue<>("Animation duration", visibility, defaultDuration, 0, 1000, 25);
    }
}