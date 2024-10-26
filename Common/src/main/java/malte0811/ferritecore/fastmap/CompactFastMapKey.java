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
    public int replaceIn(int mapIndex, int newPartialIndex) {
        final int lowerData = mapIndex % mapFactor;
        final int upperFactor = mapFactor * numValues();
        final int upperData = mapIndex - mapIndex % upperFactor;
        return lowerData + newPartialIndex + upperData;
    }

    @Override
    public int toPartialMapIndex(int internalIndex) {
        return mapFactor * internalIndex;
    }

    @Override
    public int getFactorToNext() {
        return numValues();
    }
}
