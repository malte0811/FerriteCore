package malte0811.ferritecore.fastmap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.state.Property;

import java.util.List;
import java.util.Map;

class FastMapKey<T extends Comparable<T>> {
    private final Property<T> property;
    private final List<T> values;
    private final int mapFactor;
    private final Map<Comparable<?>, Integer> toValueIndex;

    FastMapKey(Property<T> property, int mapFactor) {
        this.property = property;
        this.values = ImmutableList.copyOf(property.getAllowedValues());
        this.mapFactor = mapFactor;
        ImmutableMap.Builder<Comparable<?>, Integer> toValueIndex = ImmutableMap.builder();
        for (int i = 0; i < this.values.size(); i++) {
            toValueIndex.put(this.values.get(i), i);
        }
        this.toValueIndex = toValueIndex.build();
    }

    T getValue(int mapIndex) {
        int index = (mapIndex / mapFactor) % values.size();
        return values.get(index);
    }

    int replaceIn(int mapIndex, T newValue) {
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

    Property<T> getProperty() {
        return property;
    }

    int toPartialMapIndex(Comparable<?> value) {
        return mapFactor * getInternalIndex(value);
    }

    private int getInternalIndex(Comparable<?> value) {
        Integer result = toValueIndex.get(value);
        if (result != null) {
            return result;
        } else {
            throw new IllegalStateException("Unknown value: " + value + " in " + property);
        }
    }
}
