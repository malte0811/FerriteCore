package malte0811.ferritecore;

import com.google.common.collect.ImmutableList;
import net.minecraft.state.Property;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FastMap<Value> {
    private final List<Key<?>> keys;
    private final List<Property<?>> rawKeys;
    private final List<Value> values;

    public FastMap(Collection<Property<?>> properties, Map<Map<Property<?>, Comparable<?>>, Value> valuesMap) {
        List<Key<?>> keys = new ArrayList<>(properties.size());
        int factorUpTo = 1;
        for (Property<?> prop : properties) {
            keys.add(new Key<>(prop, factorUpTo));
            factorUpTo *= prop.getAllowedValues().size();
        }
        this.keys = keys;
        List<Value> valuesList = new ArrayList<>(factorUpTo);
        for (int i = 0; i < factorUpTo; ++i) {
            valuesList.add(null);
        }
        for (Map.Entry<Map<Property<?>, Comparable<?>>, Value> state : valuesMap.entrySet()) {
            valuesList.set(getIndexOf(state.getKey()), state.getValue());
        }
        this.values = valuesList;
        this.rawKeys = ImmutableList.copyOf(properties);
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
        // It might be possible to speed this up by sorting the keys by their hash code and using a binary search,
        // however I do not think that it would actually be faster in practice.
        for (Key<?> key : keys) {
            if (key.getProperty().equals(prop)) {
                return (Key<T>) key;
            }
        }
        return null;
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

    private static class Key<T extends Comparable<T>> {
        private final Property<T> property;
        private final List<T> values;
        private final int mapFactor;

        private Key(Property<T> property, int mapFactor) {
            this.property = property;
            this.values = new ArrayList<>(property.getAllowedValues());
            this.mapFactor = mapFactor;
        }

        public int toPartialMapIndex(Comparable<?> value) {
            return mapFactor * getInternalIndex(value);
        }

        private int getInternalIndex(Comparable<?> value) {
            int result = values.indexOf(value);
            if (result >= 0) {
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
