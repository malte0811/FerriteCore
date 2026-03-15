package malte0811.ferritecore.fastmap;

/**
 * Defines the indexing strategy for a single property in a FastMap
 */
public interface FastMapKey {
    /**
     * @param mapIndex The original index in the FastMap's value matrix
     * @param valueIndex The value to assign to this property
     * @return The index in the value matrix corresponding to the input state with only the value of this property
     * replaced by <code>newValue</code>
     */
    int replaceIn(int mapIndex, int valueIndex);

    /**
     * @param valueIndex A possible value of this property
     * @return An integer such that the sum over the returned values for all properties is the state corresponding to
     * the arguments
     */
    int toPartialMapIndex(int valueIndex);

    /**
     * @return An integer such that adding multiples of this value does not change the result of getValue
     */
    int getFactorToNext();

    int getIndexIn(int mapIndex);
}
