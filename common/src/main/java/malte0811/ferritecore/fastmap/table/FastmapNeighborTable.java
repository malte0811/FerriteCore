package malte0811.ferritecore.fastmap.table;

import com.google.common.collect.Tables;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import malte0811.ferritecore.fastmap.FastMap;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This is mostly untested, and is only used when mods are present that are known to access the neighbor table directly.
 */
public class FastmapNeighborTable<S> extends NeighborTableBase<S> {
    private final FastMapStateHolder<S> owner;

    public FastmapNeighborTable(FastMapStateHolder<S> owner) {
        this.owner = owner;
    }

    @Override
    public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
        if (!(columnKey instanceof Comparable<?>) || !(rowKey instanceof Property<?> rowProperty)) {
            return false;
        }
        Comparable<?> valueInState = owner.getStateMap().getValue(owner.getStateIndex(), rowProperty);
        if (valueInState == null || valueInState.equals(columnKey)) {
            // Not contained in state, or the current value (which isn't added to the table)
            return false;
        } else {
            // Is value allowed for property?
            return rowProperty.getPossibleValues().contains(columnKey);
        }
    }

    @Override
    public boolean containsRow(@Nullable Object rowKey) {
        if (!(rowKey instanceof Property<?> rowProperty)) {
            return false;
        } else {
            // Property is not in state
            return owner.getStateMap().getValue(owner.getStateIndex(), rowProperty) != null;
        }
    }

    @Override
    public boolean containsColumn(@Nullable Object columnKey) {
        FastMap<S> map = owner.getStateMap();
        for (int i = 0; i < map.numProperties(); ++i) {
            Map.Entry<Property<?>, Comparable<?>> entry = map.getEntry(i, owner.getStateIndex());
            if (!entry.getValue().equals(columnKey) && entry.getKey().getPossibleValues().contains(columnKey)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        if (value == null) {
            return false;
        }
        final FastMap<S> map = owner.getStateMap();
        for (int propIndex = 0; propIndex < map.numProperties(); ++propIndex) {
            if (isNeighbor(map.getKey(propIndex).getProperty(), value)) {
                return true;
            }
        }
        return false;
    }

    private <T extends Comparable<T>> boolean isNeighbor(Property<T> prop, Object potentialNeighbor) {
        final FastMap<S> map = owner.getStateMap();
        final T valueInState = map.getValue(owner.getStateIndex(), prop);
        for (final T neighborValue : prop.getPossibleValues()) {
            if (neighborValue.equals(valueInState)) {
                continue;
            }
            final S neighbor = map.with(owner.getStateIndex(), prop, valueInState);
            if (potentialNeighbor.equals(neighbor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public S get(@Nullable Object rowKey, @Nullable Object columnKey) {
        if (!(rowKey instanceof Property<?> rowProperty)) {
            return null;
        }
        return owner.getStateMap().withUnsafe(owner.getStateIndex(), rowProperty, columnKey);
    }

    @Override
    public boolean isEmpty() {
        return owner.getStateMap().isSingleState();
    }

    @Override
    public int size() {
        int numNeighbors = 0;
        for (int i = 0; i < owner.getStateMap().numProperties(); ++i) {
            numNeighbors += owner.getStateMap().getKey(i).numValues();
        }
        return numNeighbors;
    }

    @Override
    public Map<Comparable<?>, S> row(@NotNull Property<?> rowKey) {
        final Map<Comparable<?>, S> rowMap = new HashMap<>();
        final Comparable<?> contained = owner.getStateMap().getValue(owner.getStateIndex(), rowKey);
        for (Comparable<?> val : rowKey.getPossibleValues()) {
            if (!val.equals(contained)) {
                rowMap.put(val, owner.getStateMap().withUnsafe(owner.getStateIndex(), rowKey, val));
            }
        }
        return rowMap;
    }

    @Override
    public Map<Property<?>, S> column(@NotNull Comparable<?> columnKey) {
        final FastMap<S> map = owner.getStateMap();
        final int index = owner.getStateIndex();
        final Map<Property<?>, S> rowMap = new HashMap<>();
        for (int i = 0; i < map.numProperties(); ++i) {
            final Property<?> rowKey = map.getKey(i).getProperty();
            final Comparable<?> contained = map.getValue(index, rowKey);
            for (Comparable<?> val : rowKey.getPossibleValues()) {
                if (!val.equals(contained) && val.equals(columnKey)) {
                    rowMap.put(rowKey, map.withUnsafe(index, rowKey, val));
                }
            }
        }
        return rowMap;
    }

    @Override
    public Set<Cell<Property<?>, Comparable<?>, S>> cellSet() {
        final FastMap<S> map = owner.getStateMap();
        final int index = owner.getStateIndex();
        final Set<Cell<Property<?>, Comparable<?>, S>> rowMap = new HashSet<>();
        for (int i = 0; i < map.numProperties(); ++i) {
            final Property<?> rowKey = map.getKey(i).getProperty();
            final Comparable<?> contained = map.getValue(index, rowKey);
            for (Comparable<?> val : rowKey.getPossibleValues()) {
                if (!val.equals(contained)) {
                    rowMap.add(Tables.immutableCell(rowKey, val, map.withUnsafe(index, rowKey, val)));
                }
            }
        }
        return rowMap;
    }

    @Override
    public Set<Property<?>> rowKeySet() {
        return owner.getVanillaPropertyMap().keySet();
    }

    @Override
    public Set<Comparable<?>> columnKeySet() {
        final FastMap<S> map = owner.getStateMap();
        final Set<Comparable<?>> rowMap = new HashSet<>();
        for (int i = 0; i < map.numProperties(); ++i) {
            final Property<?> rowKey = map.getKey(i).getProperty();
            final Comparable<?> contained = map.getValue(owner.getStateIndex(), rowKey);
            for (Comparable<?> val : rowKey.getPossibleValues()) {
                if (!val.equals(contained)) {
                    rowMap.add(val);
                }
            }
        }
        return rowMap;
    }

    @Override
    public Collection<S> values() {
        final FastMap<S> map = owner.getStateMap();
        final int index = owner.getStateIndex();
        final Set<S> rowMap = new HashSet<>();
        for (int i = 0; i < map.numProperties(); ++i) {
            final Property<?> rowKey = map.getKey(i).getProperty();
            final Comparable<?> contained = map.getValue(index, rowKey);
            for (Comparable<?> val : rowKey.getPossibleValues()) {
                if (!val.equals(contained)) {
                    rowMap.add(map.withUnsafe(index, rowKey, val));
                }
            }
        }
        return rowMap;
    }

    @Override
    public Map<Property<?>, Map<Comparable<?>, S>> rowMap() {
        final FastMap<S> map = owner.getStateMap();
        final Map<Property<?>, Map<Comparable<?>, S>> rowMap = new HashMap<>();
        for (int i = 0; i < map.numProperties(); ++i) {
            final Property<?> rowKey = map.getKey(i).getProperty();
            rowMap.put(rowKey, row(rowKey));
        }
        return rowMap;
    }

    @Override
    public Map<Comparable<?>, Map<Property<?>, S>> columnMap() {
        final Map<Property<?>, Map<Comparable<?>, S>> rowMap = rowMap();
        Map<Comparable<?>, Map<Property<?>, S>> colMap = new HashMap<>();
        for (Map.Entry<Property<?>, Map<Comparable<?>, S>> entry : rowMap.entrySet()) {
            for (Map.Entry<Comparable<?>, S> innerEntry : entry.getValue().entrySet()) {
                colMap.computeIfAbsent(innerEntry.getKey(), $ -> new HashMap<>())
                        .put(entry.getKey(), innerEntry.getValue());
            }
        }
        return colMap;
    }
}
