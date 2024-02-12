package malte0811.ferritecore.fastmap;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Maps a Property->Value assignment to a value, while allowing fast access to "neighbor" states
 */
public class FastMap<Value> {
    private static final int INVALID_INDEX = -1;

    private final List<FastMapKey<?>> keys;
    private final List<Value> valueMatrix;
    // It might be possible to get rid of this (and the equivalent map for values) by sorting the key vectors by
    // property name (natural order for values) and using a binary search above a given size, but choosing that size
    // would likely be more effort than it's worth
    private final Reference2IntMap<Property<?>> toKeyIndex;
    private final ReferenceSet<Property<?>> propertySet;

    public FastMap(
            Collection<Property<?>> properties, Map<Map<Property<?>, Comparable<?>>, Value> valuesMap, boolean compact
    ) {
        List<FastMapKey<?>> keys = new ArrayList<>(properties.size());
        int factorUpTo = 1;
        if (useArrayMapForSize(properties.size())) {
            this.toKeyIndex = new Reference2IntArrayMap<>();
        } else {
            this.toKeyIndex = new Reference2IntOpenHashMap<>();
        }
        this.toKeyIndex.defaultReturnValue(INVALID_INDEX);
        for (Property<?> prop : properties) {
            this.toKeyIndex.put(prop, keys.size());
            FastMapKey<?> nextKey;
            if (compact) {
                nextKey = new CompactFastMapKey<>(prop, factorUpTo);
            } else {
                nextKey = new BinaryFastMapKey<>(prop, factorUpTo);
            }
            keys.add(nextKey);
            factorUpTo *= nextKey.getFactorToNext();
        }
        this.keys = ImmutableList.copyOf(keys);

        List<Value> valuesList = new ArrayList<>(factorUpTo);
        for (int i = 0; i < factorUpTo; ++i) {
            valuesList.add(null);
        }
        for (Map.Entry<Map<Property<?>, Comparable<?>>, Value> state : valuesMap.entrySet()) {
            valuesList.set(getIndexOf(state.getKey()), state.getValue());
        }
        this.valueMatrix = Collections.unmodifiableList(valuesList);
        if (useArrayMapForSize(properties.size())) {
            this.propertySet = new ReferenceArraySet<>(properties);
        } else {
            this.propertySet = new ReferenceOpenHashSet<>(properties);
        }
    }

    /**
     * Computes the value for a neighbor state
     *
     * @param oldIndex The original state index
     * @param prop     The property to be replaced
     * @param value    The new value of this property
     * @return The value corresponding to the specified neighbor, or null if value is not a valid value for prop
     */
    @Nullable
    public <T extends Comparable<T>>
    Value with(int oldIndex, Property<T> prop, T value) {
        final FastMapKey<T> keyToChange = getKeyFor(prop);
        if (keyToChange == null) {
            return null;
        }
        int newIndex = keyToChange.replaceIn(oldIndex, value);
        if (newIndex < 0) {
            return null;
        }
        return valueMatrix.get(newIndex);
    }

    /**
     * @return The map index corresponding to the given property-value assignment
     */
    public int getIndexOf(Map<Property<?>, Comparable<?>> state) {
        int id = 0;
        for (FastMapKey<?> k : keys) {
            id += k.toPartialMapIndex(state.get(k.getProperty()));
        }
        return id;
    }

    /**
     * Returns the value assigned to a property at a given map index
     *
     * @param stateIndex The map index for the assignment to check
     * @param property   The property to retrieve
     * @return The value of the property or null if the state if not present
     */
    @Nullable
    public <T extends Comparable<T>>
    T getValue(int stateIndex, Property<T> property) {
        final FastMapKey<T> propId = getKeyFor(property);
        if (propId == null) {
            return null;
        }
        return propId.getValue(stateIndex);
    }

    @Nullable
    public Comparable<?> getValue(int stateIndex, Object key) {
        if (key instanceof Property<?>) {
            return getValue(stateIndex, (Property<?>) key);
        } else {
            return null;
        }
    }

    /**
     * Returns the given property and its value in the given state
     *
     * @param propertyIndex The index of the property to retrieve
     * @param stateIndex    The index of the state to use for the value
     */
    public Map.Entry<Property<?>, Comparable<?>> getEntry(int propertyIndex, int stateIndex) {
        return new AbstractMap.SimpleImmutableEntry<>(
                getKey(propertyIndex).getProperty(), getKey(propertyIndex).getValue(stateIndex)
        );
    }

    /**
     * Same as {@link FastMap#with(int, Property, Comparable)}, but usable when the type of the value to set is not
     * correctly typed
     */
    public <T extends Comparable<T>>
    Value withUnsafe(int globalTableIndex, Property<T> property, Object newValue) {
        return with(globalTableIndex, property, (T) newValue);
    }

    public int numProperties() {
        return keys.size();
    }

    public FastMapKey<?> getKey(int keyIndex) {
        return keys.get(keyIndex);
    }

    @Nullable
    private <T extends Comparable<T>>
    FastMapKey<T> getKeyFor(Property<T> prop) {
        int index = toKeyIndex.getInt(prop);
        if (index == INVALID_INDEX) {
            return null;
        } else {
            return (FastMapKey<T>) getKey(index);
        }
    }

    public boolean isSingleState() {
        return valueMatrix.size() == 1;
    }

    public ReferenceSet<Property<?>> getPropertySet() {
        return propertySet;
    }

    private static boolean useArrayMapForSize(int numElements) {
        return numElements < 5;
    }
}
