package com.google.common.collect;

import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

public class FerriteCoreEntrySet<K, V> extends ImmutableSet<Map.Entry<K, V>> {
    private final int numProperties;
    private final Function<Object, V> getValue;
    private final IntFunction<Map.Entry<K, V>> getIth;

    public FerriteCoreEntrySet(int numProperties, Function<Object, V> getValue, IntFunction<Map.Entry<K, V>> getIth) {
        this.numProperties = numProperties;
        this.getValue = getValue;
        this.getIth = getIth;
    }

    @Override
    @NotNull
    public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
        return new FerriteCoreIterator<>(getIth, size());
    }

    @Override
    public int size() {
        return numProperties;
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
        Object valueInMap = getValue.apply(entry.getKey());
        return valueInMap != null && valueInMap.equals(((Map.Entry<?, ?>) object).getValue());
    }

    @Override
    boolean isPartialView() {
        return false;
    }
}
