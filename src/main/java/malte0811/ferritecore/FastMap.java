package malte0811.ferritecore;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.state.Property;

import javax.annotation.Nullable;
import java.util.*;

public class FastMap<Value> {
    private final List<Key<?>> keys;
    private final List<Property<?>> rawKeys;
    private final List<Value> values;
    private final Map<Property<?>, Integer> toKeyIndex;

    public FastMap(Collection<Property<?>> properties, Map<Map<Property<?>, Comparable<?>>, Value> valuesMap) {
        this.rawKeys = ImmutableList.copyOf(properties);
        List<Key<?>> keys = new ArrayList<>(rawKeys.size());
        int factorUpTo = 1;
        ImmutableMap.Builder<Property<?>, Integer> toKeyIndex = ImmutableMap.builder();
        for (Property<?> prop : rawKeys) {
            toKeyIndex.put(prop, keys.size());
            keys.add(new Key<>(prop, factorUpTo));
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
        this.values = ImmutableList.copyOf(valuesList);
    }

    @Nullable
    public <T extends Comparable<T>>
    Value with(int last, Property<T> prop, T value) {
        final Key<T> keyToChange = getKeyFor(prop);
        if (keyToChange == null) {
            return null;
        }
        int newIndex = keyToChange.replaceIn(last, value);
        if (newIndex < 0) {
            return null;
        }
        return values.get(newIndex);
    }

    @Nullable
    private <T extends Comparable<T>>
    Key<T> getKeyFor(Property<T> prop) {
        Integer index = toKeyIndex.get(prop);
        if (index == null) {
            return null;
        } else {
            return (Key<T>) keys.get(index);
        }
    }

    public int getIndexOf(Map<Property<?>, Comparable<?>> state) {
        int id = 0;
        for (Key<?> k : keys) {
            id += k.toPartialMapIndex(state.get(k.getProperty()));
        }
        return id;
    }

    @Nullable
    public <T extends Comparable<T>>
    T getValue(int stateIndex, Property<T> property) {
        final Key<T> propId = getKeyFor(property);
        if (propId == null) {
            return null;
        }
        return propId.getValue(stateIndex);
    }

    public Collection<Property<?>> getProperties() {
        return rawKeys;
    }

    public ImmutableMap<Property<?>, Comparable<?>> makeValuesFor(int index) {
        ImmutableMap.Builder<Property<?>, Comparable<?>> result = ImmutableMap.builder();
        for (Property<?> p : getProperties()) {
            result.put(p, Objects.requireNonNull(getValue(index, p)));
        }
        return result.build();
    }

    public <T extends Comparable<T>>
    Value withUnsafe(int globalTableIndex, Property<T> rowKey, Object columnKey) {
        return with(globalTableIndex, rowKey, (T) columnKey);
    }

    private static class Key<T extends Comparable<T>> {
        private final Property<T> property;
        private final List<T> values;
        private final int mapFactor;
        private final Map<Comparable<?>, Integer> toValueIndex;

        private Key(Property<T> property, int mapFactor) {
            this.property = property;
            this.values = ImmutableList.copyOf(property.getAllowedValues());
            this.mapFactor = mapFactor;
            ImmutableMap.Builder<Comparable<?>, Integer> toValueIndex = ImmutableMap.builder();
            for (int i = 0; i < this.values.size(); i++) {
                toValueIndex.put(this.values.get(i), i);
            }
            this.toValueIndex = toValueIndex.build();
        }

        public int toPartialMapIndex(Comparable<?> value) {
            return mapFactor * getInternalIndex(value);
        }

        private int getInternalIndex(Comparable<?> value) {
            Integer result = toValueIndex.get(value);
            if (result != null) {
                return result;
            } else {
                throw new IllegalStateException("Unknown value: "+value+" in "+property);
            }
        }

        public T getValue(int mapIndex) {
            int index = (mapIndex / mapFactor) % values.size();
            return values.get(index);
        }

        public int replaceIn(int mapIndex, T newValue) {
            final int lowerData = mapIndex % mapFactor;
            final int upperFactor = mapFactor * values.size();
            final int upperData = mapIndex - mapIndex % upperFactor;
            int internalIndex = getInternalIndex(newValue);
            if (internalIndex < 0) {
                return -1;
            } else {
                return lowerData + mapFactor * internalIndex + upperData;
            }
        }

        public Property<T> getProperty() {
            return property;
        }
    }
}
