package com.google.common.collect;

import java.util.Map;
import java.util.function.IntFunction;

public class FerriteCoreIterator<K, V> extends UnmodifiableIterator<Map.Entry<K, V>> {
    //TODO tie together properly
    private final IntFunction<Map.Entry<K, V>> getIth;
    private final int length;

    private int currentIndex;

    public FerriteCoreIterator(IntFunction<Map.Entry<K, V>> getIth, int length) {
        this.getIth = getIth;
        this.length = length;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < length;
    }

    @Override
    public Map.Entry<K, V> next() {
        Map.Entry<K, V> next = getIth.apply(currentIndex);
        ++currentIndex;
        return next;
    }
}
