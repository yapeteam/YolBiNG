package cn.yapeteam.yolbi.util.auth.animation;

public class AnimationHolder<T> extends Animation {

    private T t;

    public AnimationHolder(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

}