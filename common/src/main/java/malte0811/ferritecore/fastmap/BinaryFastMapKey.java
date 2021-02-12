package malte0811.ferritecore.fastmap;

import com.google.common.base.Preconditions;
import net.minecraft.state.Property;
import net.minecraft.util.math.MathHelper;

public class BinaryFastMapKey<T extends Comparable<T>> extends FastMapKey<T> {
    private final byte firstBitInValue;
    private final byte firstBitAfterValue;

    public BinaryFastMapKey(Property<T> property, int mapFactor) {
        super(property);
        Preconditions.checkArgument(MathHelper.isPowerOfTwo(mapFactor));
        final int addedFactor = MathHelper.smallestEncompassingPowerOfTwo(numValues());
        Preconditions.checkState(numValues() <= addedFactor);
        Preconditions.checkState(addedFactor < 2 * numValues());
        final int setBitInBaseFactor = MathHelper.log2(mapFactor);
        final int setBitInAddedFactor = MathHelper.log2(addedFactor);
        Preconditions.checkState(setBitInBaseFactor + setBitInAddedFactor <= 32);
        firstBitInValue = (byte) setBitInBaseFactor;
        firstBitAfterValue = (byte) (setBitInBaseFactor + setBitInAddedFactor);
    }

    @Override
    public T getValue(int mapIndex) {
        final int clearAbove = mapIndex & lowestNBits(firstBitAfterValue);
        return byInternalIndex(clearAbove >>> firstBitInValue);
    }

    @Override
    public int replaceIn(int mapIndex, T newValue) {
        final int keepMask = ~lowestNBits(firstBitAfterValue) | lowestNBits(firstBitInValue);
        return (keepMask & mapIndex) | toPartialMapIndex(newValue);
    }

    @Override
    public int toPartialMapIndex(Comparable<?> value) {
        return getInternalIndex(value) << firstBitInValue;
    }

    @Override
    public int getFactorToNext() {
        return 1 << (firstBitAfterValue - firstBitInValue);
    }

    private int lowestNBits(byte n) {
        if (n >= Integer.SIZE) {
            return -1;
        } else {
            return (1 << n) - 1;
        }
    }
}
