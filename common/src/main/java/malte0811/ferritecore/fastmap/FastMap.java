package malte0811.ferritecore.fastmap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import malte0811.ferritecore.classloading.ClassDefiner;
import net.minecraft.state.Property;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;

public class FastMap<Value> {
    private final List<FastMapKey<?>> keys;
    private final List<Value> valueMatrix;
    // It might be possible to get rid of this (and the equivalent map for values) by sorting the key vectors by
    // property name (natural order for values) and using a binary search above a given size, but choosing that size
    // would likely be more effort than it's worth
    private final Map<Property<?>, Integer> toKeyIndex;

    public FastMap(Collection<Property<?>> properties, Map<Map<Property<?>, Comparable<?>>, Value> valuesMap) {
        List<FastMapKey<?>> keys = new ArrayList<>(properties.size());
        int factorUpTo = 1;
        ImmutableMap.Builder<Property<?>, Integer> toKeyIndex = ImmutableMap.builder();
        for (Property<?> prop : properties) {
            toKeyIndex.put(prop, keys.size());
            keys.add(new FastMapKey<>(prop, factorUpTo));
            factorUpTo *= prop.getAllowedValues().size();
        }
        this.keys = ImmutableList.copyOf(keys);
        this.toKeyIndex = toKeyIndex.build();

        List<Value> valuesList = new ArrayList<>(factorUpTo);
        for (int i = 0; i < factorUpTo; ++i) {
            valuesList.add(null);
        }
        for (Map.Entry<Map<Property<?>, Comparable<?>>, Value> state : valuesMap.entrySet()) {
            valuesList.set(getIndexOf(state.getKey()), state.getValue());
        }
        this.valueMatrix = ImmutableList.copyOf(valuesList);
    }

    @Nullable
    public <T extends Comparable<T>>
    Value with(int last, Property<T> prop, T value) {
        final FastMapKey<T> keyToChange = getKeyFor(prop);
        if (keyToChange == null) {
            return null;
        }
        int newIndex = keyToChange.replaceIn(last, value);
        if (newIndex < 0) {
            return null;
        }
        return valueMatrix.get(newIndex);
    }

    public int getIndexOf(Map<Property<?>, Comparable<?>> state) {
        int id = 0;
        for (FastMapKey<?> k : keys) {
            id += k.toPartialMapIndex(state.get(k.getProperty()));
        }
        return id;
    }

    @Nullable
    public <T extends Comparable<T>>
    T getValue(int stateIndex, Property<T> property) {
        final FastMapKey<T> propId = getKeyFor(property);
        if (propId == null) {
            return null;
        }
        return propId.getValue(stateIndex);
    }

    public ImmutableMap<Property<?>, Comparable<?>> makeValuesFor(int index) {
        try {
            class MapAccessor implements Function<Object, Comparable<?>>,
                    IntFunction<Map.Entry<Property<?>, Comparable<?>>>, IntSupplier {
                @Override
                public Comparable<?> apply(Object obj) {
                    if (obj instanceof Property<?>) {
                        return getValue(index, (Property<?>) obj);
                    } else {
                        return null;
                    }
                }

                @Override
                public Map.Entry<Property<?>, Comparable<?>> apply(int subIndex) {
                    return new AbstractMap.SimpleImmutableEntry<>(
                            getKey(subIndex).getProperty(), getKey(subIndex).getValue(index)
                    );
                }

                @Override
                public int getAsInt() {
                    return numProperties();
                }
            }
            MapAccessor func = new MapAccessor();
            return ClassDefiner.makeMap(func);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public <T extends Comparable<T>>
    Value withUnsafe(int globalTableIndex, Property<T> rowKey, Object columnKey) {
        return with(globalTableIndex, rowKey, (T) columnKey);
    }

    public int numProperties() {
        return keys.size();
    }

    FastMapKey<?> getKey(int keyIndex) {
        return keys.get(keyIndex);
    }

    @Nullable
    private <T extends Comparable<T>>
    FastMapKey<T> getKeyFor(Property<T> prop) {
        Integer index = toKeyIndex.get(prop);
        if (index == null) {
            return null;
        } else {
            return (FastMapKey<T>) getKey(index);
        }
    }
}
