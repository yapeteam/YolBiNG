package cn.yapeteam.yolbi.values;

public interface ChangedCallback<T> {
    T run(T oldV, T newV);
}
