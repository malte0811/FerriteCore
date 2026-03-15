package malte0811.ferritecore.fastmap;

import com.google.common.base.Preconditions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * A bitmask-based implementation of a FastMapKey. This reduces the density of data in the value matrix, but allows
 * accessing values with only some bitwise operations, which are much faster than integer division
 */
public record BinaryFastMapKey(int numValues, byte firstBitInValue, byte firstBitAfterValue) implements FastMapKey {

    public static BinaryFastMapKey create(int mapFactor, int numValues) {
        Preconditions.checkArgument(Mth.isPowerOfTwo(mapFactor));
        final int addedFactor = Mth.smallestEncompassingPowerOfTwo(numValues);
        Preconditions.checkState(numValues <= addedFactor);
        Preconditions.checkState(addedFactor < 2 * numValues);
        final int setBitInBaseFactor = Mth.log2(mapFactor);
        final int setBitInAddedFactor = Mth.log2(addedFactor);
        Preconditions.checkState(setBitInBaseFactor + setBitInAddedFactor <= 31);
        return new BinaryFastMapKey(
                numValues, (byte) setBitInBaseFactor, (byte) (setBitInBaseFactor + setBitInAddedFactor)
        );
    }

    @Override
    public int replaceIn(int mapIndex, int valueIndex) {
        if (valueIndex >= numValues) {
            return -1;
        }
        final int keepMask = ~lowestNBits(firstBitAfterValue) | lowestNBits(firstBitInValue);
        return (keepMask & mapIndex) | toPartialMapIndex(valueIndex);
    }

    @Override
    public int toPartialMapIndex(int internalIndex) {
        return internalIndex << firstBitInValue;
    }

    @Override
    public int getFactorToNext() {
        return 1 << (firstBitAfterValue - firstBitInValue);
    }

    @Override
    public int getIndexIn(int mapIndex) {
        return (mapIndex >> firstBitInValue) & lowestNBits((byte) (firstBitAfterValue - firstBitInValue));
    }

    private static int lowestNBits(byte n) {
        if (n >= Integer.SIZE) {
            return -1;
        } else {
            return (1 << n) - 1;
        }
    }
}
