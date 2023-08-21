package cn.yapeteam.yolbi.values;

public interface ChangedCallback<T> {
    void run(T oldV, T newV);
}
