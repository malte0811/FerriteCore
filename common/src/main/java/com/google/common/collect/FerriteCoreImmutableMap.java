package com.google.common.collect;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;

public class FerriteCoreImmutableMap<K, V, F extends Function<Object, V> & IntFunction<Map.Entry<K, V>> & IntSupplier>
        extends ImmutableMap<K, V> {
    // This is a quite inconvenient "handle" on a FastMap, but we need classloader separation
    // Function<Object, V>: Map#get
    // IntFunction<Entry<K, V>>: get i-th entry of the map
    // IntSupplier: Map#size
    private final F access;

    public FerriteCoreImmutableMap(F access) {
        this.access = access;
    }

    @Override
    public int size() {
        return access.getAsInt();
    }

    @Override
    public V get(@Nullable Object key) {
        return access.apply(key);
    }

    @Override
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
        return new FerriteCoreEntrySet<>(access);
    }

    @Override
    boolean isPartialView() {
        return false;
    }
}
