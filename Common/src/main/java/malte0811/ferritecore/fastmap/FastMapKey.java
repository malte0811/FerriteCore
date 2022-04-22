package malte0811.ferritecore.fastmap;

import net.minecraft.world.level.block.state.properties.Property;

/**
 * Defines the indexing strategy for a single property in a FastMap
 */
public abstract class FastMapKey<T extends Comparable<T>> {
    /**
     * Maps values of the property to indices in [0, numValues()) and vice versa
     */
    private final PropertyIndexer<T> indexer;

    protected FastMapKey(Property<T> property) {
        this.indexer = PropertyIndexer.makeIndexer(property);
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
    abstract int replaceIn(int mapIndex, T newValue);

    /**
     * @param value A possible value of this property
     * @return An integer such that the sum over the returned values for all properties is the state corresponding to
     * the arguments
     */
    abstract int toPartialMapIndex(Comparable<?> value);

    /**
     * @return An integer such that adding multiples of this value does not change the result of getValue
     */
    abstract int getFactorToNext();

    public final int numValues() {
        return indexer.numValues();
    }

    public final Property<T> getProperty() {
        return indexer.getProperty();
    }

    protected final int getInternalIndex(Comparable<?> value) {
        return indexer.toIndex((T) value);
    }

    protected final T byInternalIndex(int internalIndex) {
        return indexer.byIndex(internalIndex);
    }
}
