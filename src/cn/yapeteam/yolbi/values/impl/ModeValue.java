package cn.yapeteam.yolbi.values.impl;

import cn.yapeteam.yolbi.values.Value;
import cn.yapeteam.yolbi.values.Visibility;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

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

    public void setValue(String str) {
        Arrays.stream(modes).filter(m -> m.toString().equals(str)).findFirst().ifPresent(toSet -> value = toSet);
    }

    public void increment() {
        int index = Arrays.asList(modes).indexOf(getValue());
        setValue(modes[index < modes.length - 1 ? index + 1 : 0]);
    }

    public void decrement() {
        int index = Arrays.asList(modes).indexOf(getValue());
        setValue(modes[index > 0 ? index - 1 : modes.length - 1]);
    }
}
