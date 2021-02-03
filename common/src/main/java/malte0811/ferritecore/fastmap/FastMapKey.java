package malte0811.ferritecore.fastmap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.state.Property;

import java.util.List;
import java.util.Map;

class FastMapKey<T extends Comparable<T>> {
    private final int mapFactor;
    private final PropertyIndexer<T> indexer;

    FastMapKey(Property<T> property, int mapFactor) {
        this.indexer = PropertyIndexer.makeIndexer(property);
        this.mapFactor = mapFactor;
    }

    T getValue(int mapIndex) {
        int index = (mapIndex / mapFactor) % indexer.size();
        return indexer.byIndex(index);
    }

    int replaceIn(int mapIndex, T newValue) {
        final int lowerData = mapIndex % mapFactor;
        final int upperFactor = mapFactor * indexer.size();
        final int upperData = mapIndex - mapIndex % upperFactor;
        int internalIndex = getInternalIndex(newValue);
        if (internalIndex < 0) {
            return -1;
        } else {
            return lowerData + mapFactor * internalIndex + upperData;
        }
    }

    Property<T> getProperty() {
        return indexer.getProperty();
    }

    int toPartialMapIndex(Comparable<?> value) {
        return mapFactor * getInternalIndex(value);
    }

    private int getInternalIndex(Comparable<?> value) {
        return indexer.toIndex((T) value);
    }
}
