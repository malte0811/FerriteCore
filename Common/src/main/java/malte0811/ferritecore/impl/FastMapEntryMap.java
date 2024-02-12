package malte0811.ferritecore.impl;

import it.unimi.dsi.fastutil.objects.*;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import malte0811.ferritecore.fastmap.FastMap;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FastMapEntryMap implements Reference2ObjectMap<Property<?>, Comparable<?>> {
    private final FastMapStateHolder<?> viewedState;

    public FastMapEntryMap(FastMapStateHolder<?> viewedState) {
        this.viewedState = viewedState;
    }

    @Override
    public int size() {
        return getFastMap().numProperties();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Property<?> key : keySet()) {
            if (Objects.equals(value, get(key))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Comparable<?> get(@Nullable Object key) {
        return viewedState.getStateMap().getValue(viewedState.getStateIndex(), key);
    }

    @NotNull
    @Override
    public ReferenceSet<Property<?>> keySet() {
        return getFastMap().getPropertySet();
    }

    @NotNull
    @Override
    public ObjectCollection<Comparable<?>> values() {
        // TODO custom smaller/faster object?
        ObjectList<Comparable<?>> values = new ObjectArrayList<>();
        for (Property<?> key : keySet()) {
            values.add(get(key));
        }
        return values;
    }

    @Override
    public void putAll(@NotNull Map<? extends Property<?>, ? extends Comparable<?>> m) {
        throw exceptionForMutation();
    }

    @Override
    public void defaultReturnValue(Comparable<?> comparable) {
        throw exceptionForMutation();
    }

    @Override
    public Comparable<?> defaultReturnValue() {
        return null;
    }

    @Override
    public ObjectSet<Entry<Property<?>, Comparable<?>>> reference2ObjectEntrySet() {
        ObjectSet<Entry<Property<?>, Comparable<?>>> entries = new ObjectArraySet<>();
        for (Property<?> key : keySet()) {
            entries.add(new AbstractReference2ObjectMap.BasicEntry<>(key, get(key)));
        }
        return entries;
    }

    private FastMap<?> getFastMap() {
        return viewedState.getStateMap();
    }

    private RuntimeException exceptionForMutation() {
        return new UnsupportedOperationException();
    }
}
