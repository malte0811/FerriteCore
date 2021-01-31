package com.google.common.collect;

import net.minecraft.state.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;

public class FerriteCoreEntrySet<K, V, F extends Function<Object, V> & IntFunction<Map.Entry<K, V>> & IntSupplier>
        extends ImmutableSet<Map.Entry<K, V>> {
    // Function<Object, V>: Map#get
    // IntFunction<Entry<K, V>>: get i-th entry of the map
    // IntSupplier: Map#size
    private final F access;

    public FerriteCoreEntrySet(F access) {
        this.access = access;
    }

    @Override
    @NotNull
    public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
        return new FerriteCoreIterator<>(access, size());
    }

    @Override
    public int size() {
        return access.getAsInt();
    }

    @Override
    public boolean contains(@Nullable Object object) {
        if (!(object instanceof Map.Entry)) {
            return false;
        }
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
        if (!(entry.getKey() instanceof Property<?>)) {
            return false;
        }
        Object valueInMap = access.apply(entry.getKey());
        return valueInMap != null && valueInMap.equals(((Map.Entry<?, ?>) object).getValue());
    }

    @Override
    boolean isPartialView() {
        return false;
    }
}
