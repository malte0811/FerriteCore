package com.google.common.collect;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

public class FerriteCoreImmutableMap<K, V> extends ImmutableMap<K, V> {
    // This is a quite inconvenient "handle" on a FastMap, but we need classloader separation
    private final int numProperties;
    private final Function<Object, V> getValue;
    private final IntFunction<Entry<K, V>> getIth;

    public FerriteCoreImmutableMap(int numProperties, Function<Object, V> getValue, IntFunction<Entry<K, V>> getIth) {
        this.numProperties = numProperties;
        this.getValue = getValue;
        this.getIth = getIth;
    }

    @Override
    public int size() {
        return numProperties;
    }

    @Override
    public V get(@Nullable Object key) {
        return getValue.apply(key);
    }

    @Override
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
        return new FerriteCoreEntrySet<>(numProperties, getValue, getIth);
    }

    @Override
    boolean isPartialView() {
        return false;
    }
}
