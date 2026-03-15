package malte0811.ferritecore.fastmap;

/**
 * A "compact" implementation of a FastMapKey, i.e. one which completely fills the value matrix
 */
public record CompactFastMapKey(int mapFactor, int numValues) implements FastMapKey {
    @Override
    public int replaceIn(int mapIndex, int valueIndex) {
        if (valueIndex >= numValues) {
            return -1;
        }
        final int lowerData = mapIndex % mapFactor;
        final int upperFactor = mapFactor * numValues;
        final int upperData = mapIndex - mapIndex % upperFactor;
        return lowerData + toPartialMapIndex(valueIndex) + upperData;
    }

    @Override
    public int toPartialMapIndex(int internalIndex) {
        return mapFactor * internalIndex;
    }

    @Override
    public int getFactorToNext() {
        return numValues;
    }

    @Override
    public int getIndexIn(int mapIndex) {
        return (mapIndex / mapFactor) % numValues;
    }
}
