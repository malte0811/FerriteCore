package malte0811.ferritecore.fastmap.neighbormap;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import malte0811.ferritecore.fastmap.FastMapKey;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * This is mostly untested, and is only used when mods are present that are known to access the neighbor table directly.
 */
public class FastmapNeighborMap<S> extends NeighborMapBase<S> {
    private final FastMapStateHolder<S> owner;

    public FastmapNeighborMap(FastMapStateHolder<S> owner) {
        this.owner = owner;
    }

    @Override
    public int size() {
        return owner.getStateMap().numProperties();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        //noinspection SuspiciousMethodCalls
        return owner.getStateMap().getPropertySet().contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        // Not entirely "legal", but the value arrays are recreated all the time and since arrays are compared by
        // reference this is the best we can do right now.
        return false;
    }

    @Override
    public S[] get(Object key) {
        if (containsKey(key)) {
            return buildNeighbors((Property<?>) key);
        } else {
            return null;
        }
    }

    @Override
    @NotNull
    public Set<Property<?>> keySet() {
        return owner.getStateMap().getPropertySet();
    }

    @Override
    @NotNull
    public Collection<S[]> values() {
        ImmutableSet.Builder<S[]> builder = ImmutableSet.builder();
        for (Property<?> property : owner.getStateMap().getPropertySet()) {
            builder.add(buildNeighbors(property));
        }
        return builder.build();
    }

    @Override
    @NotNull
    public Set<Entry<Property<?>, S[]>> entrySet() {
        ImmutableSet.Builder<Entry<Property<?>, S[]>> builder = ImmutableSet.builder();
        for (Property<?> property : owner.getStateMap().getPropertySet()) {
            builder.add(new AbstractMap.SimpleEntry<>(property, buildNeighbors(property)));
        }
        return builder.build();
    }

    private <V extends Comparable<V>> S[] buildNeighbors(Property<V> prop) {
        S[] neighbors = (S[]) new Object[prop.getPossibleValues().size()];
        FastMapKey<V> key = Preconditions.checkNotNull(owner.getStateMap().getKeyFor(prop));
        for (int i = 0; i < neighbors.length; ++i) {
            int neighborIndex = key.replaceIn(owner.getStateIndex(), key.toPartialMapIndex(i));
            neighbors[i] = owner.getStateMap().getStateByIndex(neighborIndex);
        }
        return neighbors;
    }
}
