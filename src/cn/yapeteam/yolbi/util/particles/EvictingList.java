package cn.yapeteam.yolbi.util.particles;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.util.particles
 * don't mind
 * @date 2023/8/22 16:59
 */
// skid from FDPClient

public final class EvictingList<T> extends LinkedList<T> {

    private final int maxSize;

    public EvictingList(final int maxSize) {
        this.maxSize = maxSize;
    }

    public EvictingList(final Collection<? extends T> c, final int maxSize) {
        super(c);
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(final T t) {
        if (size() >= maxSize) removeFirst();
        return super.add(t);
    }

    public boolean isFull() {
        return size() >= maxSize;
    }
}
