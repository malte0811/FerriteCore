package malte0811.ferritecore.fastmap;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class PropertyIndexer<T extends Comparable<T>> {
    private static final Map<Property<?>, PropertyIndexer<?>> KNOWN_INDEXERS = new Object2ObjectOpenHashMap<>();

    private final Property<T> property;
    private final int numValues;

    public static <T extends Comparable<T>> PropertyIndexer<T> makeIndexer(Property<T> prop) {
        synchronized (KNOWN_INDEXERS) {
            PropertyIndexer<?> unchecked = KNOWN_INDEXERS.computeIfAbsent(prop, propInner -> {
                PropertyIndexer<?> result = null;
                if (propInner instanceof BooleanProperty) {
                    result = new BoolIndexer((BooleanProperty) propInner);
                } else if (propInner instanceof IntegerProperty) {
                    result = new IntIndexer((IntegerProperty) propInner);
                } else if (propInner == BlockStateProperties.FACING) {
                    result = new WeirdVanillaDirectionIndexer();
                } else if (propInner instanceof EnumProperty<?>) {
                    result = new EnumIndexer<>((EnumProperty<?>) propInner);
                }
                if (result == null || !result.isValid()) {
                    return new GenericIndexer<>(propInner);
                } else {
                    return result;
                }
            });
            return (PropertyIndexer<T>) unchecked;
        }
    }

    protected PropertyIndexer(Property<T> property) {
        this.property = property;
        this.numValues = property.getAllowedValues().size();
    }

    public Property<T> getProperty() {
        return property;
    }

    public int numValues() {
        return numValues;
    }

    public abstract T byIndex(int index);

    public abstract int toIndex(T value);

    protected boolean isValid() {
        Collection<T> allowed = getProperty().getAllowedValues();
        int index = 0;
        for (T val : allowed) {
            if (toIndex(val) != index || !val.equals(byIndex(index))) {
                return false;
            }
            ++index;
        }
        return true;
    }

    private static class BoolIndexer extends PropertyIndexer<Boolean> {

        protected BoolIndexer(BooleanProperty property) {
            super(property);
        }

        @Override
        public Boolean byIndex(int index) {
            return index == 1 ? Boolean.FALSE : Boolean.TRUE;
        }

        @Override
        public int toIndex(Boolean value) {
            return value ? 0 : 1;
        }
    }

    private static class IntIndexer extends PropertyIndexer<Integer> {
        private final int min;

        protected IntIndexer(IntegerProperty property) {
            super(property);
            this.min = property.getAllowedValues().stream().min(Comparator.naturalOrder()).orElse(0);
        }

        @Override
        public Integer byIndex(int index) {
            return index + min;
        }

        @Override
        public int toIndex(Integer value) {
            return value - min;
        }
    }

    private static class EnumIndexer<E extends Enum<E> & IStringSerializable>
            extends PropertyIndexer<E> {
        private final int ordinalOffset;
        private final E[] enumValues;

        protected EnumIndexer(EnumProperty<E> property) {
            super(property);
            this.ordinalOffset = property.getAllowedValues()
                    .stream()
                    .mapToInt(Enum::ordinal)
                    .min()
                    .orElse(0);
            this.enumValues = getProperty().getValueClass().getEnumConstants();
        }

        @Override
        public E byIndex(int index) {
            return enumValues[index + ordinalOffset];
        }

        @Override
        public int toIndex(E value) {
            return value.ordinal() - ordinalOffset;
        }
    }

    /**
     * This is a kind of hack for a vanilla quirk: BlockStateProperties.FACING (which is used everywhere) has the order
     * NORTH, EAST, SOUTH, WEST, UP, DOWN
     * instead of the "canonical" order given by the enum
     */
    private static class WeirdVanillaDirectionIndexer extends PropertyIndexer<Direction> {
        private static final Direction[] ORDER = {
                Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN
        };

        public WeirdVanillaDirectionIndexer() {
            super(BlockStateProperties.FACING);
            Preconditions.checkState(isValid());
        }

        @Override
        public Direction byIndex(int index) {
            return ORDER[index];
        }

        @Override
        public int toIndex(Direction value) {
            switch (value) {
                case NORTH:
                    return 0;
                case EAST:
                    return 1;
                case SOUTH:
                    return 2;
                case WEST:
                    return 3;
                case UP:
                    return 4;
                case DOWN:
                    return 5;
            }
            throw new IllegalArgumentException("Invalid direction: "+value);
        }
    }

    private static class GenericIndexer<T extends Comparable<T>> extends PropertyIndexer<T> {
        private final Map<Comparable<?>, Integer> toValueIndex;
        private final List<T> values;

        protected GenericIndexer(Property<T> property) {
            super(property);
            this.values = ImmutableList.copyOf(property.getAllowedValues());
            ImmutableMap.Builder<Comparable<?>, Integer> toValueIndex = ImmutableMap.builder();
            for (int i = 0; i < this.values.size(); i++) {
                toValueIndex.put(this.values.get(i), i);
            }
            this.toValueIndex = toValueIndex.build();
        }

        @Override
        public T byIndex(int index) {
            return values.get(index);
        }

        @Override
        public int toIndex(T value) {
            return toValueIndex.get(value);
        }
    }
}
