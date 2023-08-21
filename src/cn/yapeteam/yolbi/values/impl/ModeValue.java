package cn.yapeteam.yolbi.values.impl;

import cn.yapeteam.yolbi.values.Value;
import cn.yapeteam.yolbi.values.Visibility;
import lombok.Getter;
import lombok.Setter;

public class ModeValue<T> extends Value<T> {
    @Getter
    @Setter
    private T[] modes;

    @SafeVarargs
    public ModeValue(String name, T current, T... modes) {
        super(name);
        this.value = current;
        this.modes = modes;
    }

    @SafeVarargs
    public ModeValue(String name, Visibility visibility, T current, T... modes) {
        super(name);
        this.value = current;
        this.modes = modes;
        setVisibility(visibility);
    }

    public boolean is(T str) {
        return getValue().equals(str);
    }
}
