package malte0811.ferritecore.fastmap;

import net.minecraft.world.level.block.state.properties.Property;

/**
 * A "compact" implementation of a FastMapKey, i.e. one which completely fills the value matrix
 */
public class CompactFastMapKey<T extends Comparable<T>> extends FastMapKey<T> {
    private final int mapFactor;

    CompactFastMapKey(Property<T> property, int mapFactor) {
        super(property);
        this.mapFactor = mapFactor;
    }

    @Override
    public T getValue(int mapIndex) {
        int index = (mapIndex / mapFactor) % numValues();
        return byInternalIndex(index);
    }

    @Override
    public int replaceIn(int mapIndex, T newValue) {
        final int lowerData = mapIndex % mapFactor;
        final int upperFactor = mapFactor * numValues();
        final int upperData = mapIndex - mapIndex % upperFactor;
        int internalIndex = getInternalIndex(newValue);
        if (internalIndex < 0 || internalIndex >= numValues()) {
            return -1;
        } else {
            return lowerData + mapFactor * internalIndex + upperData;
        }
    }

    @Override
    public int toPartialMapIndex(Comparable<?> value) {
        return mapFactor * getInternalIndex(value);
    }

    @Override
    public int getFactorToNext() {
        return numValues();
    }
}
