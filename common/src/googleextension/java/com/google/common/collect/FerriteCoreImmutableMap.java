package com.google.common.collect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;

public class FerriteCoreImmutableMap<K> extends ImmutableMap<K, Comparable<?>> {
    // This is a very inconvenient "handle" on a FastMap, but
    // a) we need classloader separation
    // b) by keeping the functions static we can keep the size of the object down
    public static ToIntFunction<Object> numProperties;
    public static BiFunction<Object, Object, Comparable<?>> getByStateAndKey;
    public static BiFunction<Object, Integer, Entry<?, Comparable<?>>> entryByStateAndIndex;

    // Actually a FastMapStateHolder, but classloader separation…
    private final Object viewedState;

    public FerriteCoreImmutableMap(Object viewedState) {
        this.viewedState = viewedState;
    }

    @Override
    public int size() {
        return numProperties.applyAsInt(viewedState);
    }

    @Override
    public Comparable<?> get(@Nullable Object key) {
        return getByStateAndKey.apply(viewedState, key);
    }

    @Override
    ImmutableSet<Map.Entry<K, Comparable<?>>> createEntrySet() {
        return new FerriteCoreEntrySet<>(viewedState);
    }

    @Override
    @NotNull
    public ImmutableSet<Entry<K, Comparable<?>>> entrySet() {
        return new FerriteCoreEntrySet<>(viewedState);
    }

    @Override
    boolean isPartialView() {
        return false;
    }
}
