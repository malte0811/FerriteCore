package malte0811.ferritecore.fastmap;

import net.minecraft.world.level.block.state.properties.Property;

/**
 * Defines the indexing strategy for a single property in a FastMap
 */
public abstract class FastMapKey<T extends Comparable<T>> {
    private final Property<T> property;

    protected FastMapKey(Property<T> property) {
        this.property = property;
    }

    /**
     * @param mapIndex An index in the FastMap's value matrix
     * @return The value of this property in that index
     */
    public abstract T getValue(int mapIndex);

    /**
     * @param mapIndex The original index in the FastMap's value matrix
     * @param newValue The value to assign to this property
     * @return The index in the value matrix corresponding to the input state with only the value of this property
     * replaced by <code>newValue</code>
     */
    public final int replaceIn(int mapIndex, Comparable<?> newValue) {
        final int newPartialIndex = toPartialMapIndex(newValue);
        if (newPartialIndex < 0) {
            return -1;
        } else {
            return replaceIn(mapIndex, newPartialIndex);
        }
    }

    public abstract int replaceIn(int mapIndex, int newPartialIndex);

    /**
     * @param value A possible value of this property
     * @return An integer such that the sum over the returned values for all properties is the state corresponding to
     * the arguments
     */
    public final int toPartialMapIndex(Comparable<?> value) {
        final int internalIndex = property.getInternalIndex((T) value);
        if (internalIndex < 0 || internalIndex >= numValues()) {
            return -1;
        } else {
            return toPartialMapIndex(internalIndex);
        }
    }

    public abstract int toPartialMapIndex(int valueIndex);

    /**
     * @return An integer such that adding multiples of this value does not change the result of getValue
     */
    abstract int getFactorToNext();

    public final int numValues() {
        return property.getPossibleValues().size();
    }

    public final Property<T> getProperty() {
        return property;
    }

    protected final T byInternalIndex(int internalIndex) {
        return property.getPossibleValues().get(internalIndex);
    }
}
