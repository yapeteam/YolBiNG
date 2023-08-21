package cn.yapeteam.yolbi.util.animation;

import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;

import java.util.function.Supplier;

public class AnimationUtil {

    public static ModeValue<AnimationType> getAnimationType(AnimationType defaultAnim) {
        return new ModeValue<>("Animation", defaultAnim, AnimationType.values());
    }

    public static ModeValue<AnimationType> getAnimationType(Supplier<Boolean> visibility, AnimationType defaultAnim) {
        return new ModeValue<>("Animation", visibility, defaultAnim, AnimationType.values());
    }

    public static NumberValue getAnimationDuration(int defaultDuration) {
        return new NumberValue("Animation duration", defaultDuration, 0, 1000, 25);
    }

    public static NumberValue getAnimationDuration(Supplier<Boolean> visibility, int defaultDuration) {
        return new NumberValue("Animation duration", visibility, defaultDuration, 0, 1000, 25);
    }

}