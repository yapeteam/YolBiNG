package cn.yapeteam.yolbi.values;

import lombok.Getter;
import lombok.Setter;

public class Value<T> {
    protected T value;
    @Getter
    public String name;
    @Getter
    protected String desc;
    @Getter
    @Setter
    private ChangedCallback<T> callback = null;

    @Getter
    @Setter
    private Visibility visibility = () -> true;

    public Value(String name) {
        this.name = name;
    }

    public Value(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (callback != null)
            callback.run(this.value, value);
        this.value = value;
    }
}
