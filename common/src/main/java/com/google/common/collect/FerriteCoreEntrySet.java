package com.google.common.collect;

import net.minecraft.state.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FerriteCoreEntrySet<K> extends ImmutableSet<Map.Entry<K, Comparable<?>>> {
    private final Object viewedState;

    public FerriteCoreEntrySet(Object viewedState) {
        this.viewedState = viewedState;
    }

    @Override
    @NotNull
    public UnmodifiableIterator<Map.Entry<K, Comparable<?>>> iterator() {
        return new FerriteCoreIterator<>(
                i -> (Map.Entry<K, Comparable<?>>) FerriteCoreImmutableMap.entryByStateAndIndex.apply(viewedState, i),
                size()
        );
    }

    @Override
    public int size() {
        return FerriteCoreImmutableMap.numProperties.applyAsInt(viewedState);
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
        Object valueInMap = FerriteCoreImmutableMap.getByStateAndKey.apply(viewedState, entry.getKey());
        return valueInMap != null && valueInMap.equals(((Map.Entry<?, ?>) object).getValue());
    }

    @Override
    boolean isPartialView() {
        return false;
    }
}
