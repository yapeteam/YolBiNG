package cn.yapeteam.yolbi.values.impl;

import cn.yapeteam.yolbi.values.Value;
import cn.yapeteam.yolbi.values.Visibility;

@SuppressWarnings("unchecked")
public class NumberValue<T extends Number> extends Value<T> {
    /**
     * 单次可拖动的数值
     */
    private final T inc;
    private final T min;
    private final T max;
    private final Class<? extends Number> type;

    public NumberValue(String name, T value, T min, T max, T inc) {
        super(name);
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.inc = inc;
        type = value.getClass();
    }

    public NumberValue(String name, Visibility visibility, T value, T min, T max, T inc) {
        super(name);
        this.name = name;
        setVisibility(visibility);
        this.value = value;
        this.min = min;
        this.max = max;
        this.inc = inc;
        type = value.getClass();
    }

    public NumberValue(String name, String desc, T value, T min, T max, T inc) {
        this(name, value, min, max, inc);
        this.desc = desc;
    }

    public NumberValue(String name, String desc, Visibility visibility, T value, T min, T max, T inc) {
        this(name, desc, value, min, max, inc);
        setVisibility(visibility);
    }

    public T getMin() {
        return (T) convertValue(type, min);
    }

    public T getMax() {
        return (T) convertValue(type, max);
    }

    public T getInc() {
        return (T) convertValue(type, inc);
    }

    @Override
    public T getValue() {
        return (T) convertValue(type, value);
    }

    @Override
    public void setValue(Number value) {
        this.value = (T) convertValue(type, getCallback() != null ? getCallback().run(this.value, (T) convertValue(type, value)) : value);
    }

    private Object convertValue(Class<? extends Number> type, Number value) {
        if (type.equals(Integer.class))
            return Integer.parseInt(String.valueOf(value.intValue()));
        else if (type.equals(Long.class))
            return Long.parseLong(String.valueOf(value.longValue()));
        else if (type.equals(Float.class))
            return Float.parseFloat(String.valueOf(value.floatValue()));
        else if (type.equals(Double.class))
            return Double.parseDouble(String.valueOf(value.doubleValue()));
        throw new RuntimeException("invalid type" + type);
    }
}
