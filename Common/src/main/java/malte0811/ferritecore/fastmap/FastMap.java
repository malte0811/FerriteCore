package malte0811.ferritecore.fastmap;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Maps a Property->Value assignment to a value, while allowing fast access to "neighbor" states
 */
public class FastMap<Value> {
    private final Property<?>[] properties;
    private final List<FastMapKey> keys;
    private final List<Value> valueMatrix;

    public FastMap(Property<?>[] properties, boolean compact) {
        this.properties = properties;
        for (var prop : properties) {
            for (int i = 0; i < prop.getPossibleValues().size(); ++i) {
                Preconditions.checkState(getIndexUnsafe(prop, prop.getPossibleValues().get(i)) == i);
            }
        }
        List<FastMapKey> keys = new ArrayList<>(properties.length);
        int factorUpTo = 1;
        for (int propIndex = 0; propIndex < properties.length; ++propIndex) {
            FastMapKey nextKey;
            if (compact) {
                nextKey = new CompactFastMapKey(factorUpTo, properties[propIndex].getPossibleValues().size());
            } else {
                nextKey = BinaryFastMapKey.create(factorUpTo, properties[propIndex].getPossibleValues().size());
            }
            keys.add(nextKey);
            factorUpTo *= nextKey.getFactorToNext();
        }
        this.keys = ImmutableList.copyOf(keys);

        List<Value> valuesList = new ArrayList<>(factorUpTo);
        for (int i = 0; i < factorUpTo; ++i) {
            valuesList.add(null);
        }
        this.valueMatrix = valuesList;
    }

    /**
     * Computes the value for a neighbor state
     *
     * @param oldIndex      The original state index
     * @param propertyIndex The property to be replaced
     * @param valueIndex    The new value of this property
     * @return The value corresponding to the specified neighbor, or null if value is not a valid value for prop
     */
    @Nullable
    public Value with(int oldIndex, int propertyIndex, int valueIndex) {
        int newIndex = keys.get(propertyIndex).replaceIn(oldIndex, valueIndex);
        if (newIndex < 0 || valueMatrix.get(newIndex) == null) {
            throw new RuntimeException(
                    "Invalid access to FastMap: Replacing " + propertyIndex + " to " + valueIndex + " in " + oldIndex +
                            " with properties " + Arrays.toString(this.properties)
            );
        }
        return valueMatrix.get(newIndex);
    }

    public int getIndexOf(Value state, PropertyValueGetter<Value> getValue) {
        int id = 0;
        for (int i = 0; i < this.properties.length; ++i) {
            int valueIndex = getIndexUnsafe(this.properties[i], getValue.getValue(state, this.properties[i]));
            id += keys.get(i).toPartialMapIndex(valueIndex);
        }
        return id;
    }

    public int insertAtIndex(Value state, PropertyValueGetter<Value> getValue) {
        final int index = getIndexOf(state, getValue);
        Preconditions.checkState(this.valueMatrix.get(index) == null);
        this.valueMatrix.set(index, state);
        return index;
    }

    public int getValueIndex(int stateIndex, int propertyIndex) {
        return this.keys.get(propertyIndex).getIndexIn(stateIndex);
    }

    public Property<?>[] getProperties() {
        return properties;
    }

    private static <T extends Comparable<T>> int getIndexUnsafe(Property<T> property, Comparable<?> value) {
        return property.getInternalIndex((T) value);
    }

    public interface PropertyValueGetter<Owner> {
        Comparable<?> getValue(Owner state, Property<?> property);
    }
}
