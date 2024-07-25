package cn.yapeteam.yolbi.util.network.http.utils;

import java.util.HashMap;
import java.util.Map;

public class ColUtils {
    public static <K, V> Map<K, V> mapOf(Object... objects) {
        Map<K, V> map = new HashMap<>();
        boolean isKey = true;
        K key = null;
        for (Object object : objects) {
            if (isKey) {
                key = (K) object;
                isKey = false;
            } else {
                map.put(key, (V) object);
                isKey = true;
            }
        }
        return map;
    }
}
