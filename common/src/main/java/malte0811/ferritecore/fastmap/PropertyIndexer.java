package malte0811.ferritecore.fastmap;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.*;

/**
 * Provides a way of converting between values of a property and indices in [0, #values). Most properties are covered
 * by one of the (faster) specific implementations, all other properties use the {@link GenericIndexer}
 */
public abstract class PropertyIndexer<T extends Comparable<T>> {
    private static final Map<Property<?>, PropertyIndexer<?>> KNOWN_INDEXERS = new Object2ObjectOpenCustomHashMap<>(
            Util.identityStrategy()
    );

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
                } else if (WeirdVanillaDirectionIndexer.isApplicable(propInner)) {
                    result = new WeirdVanillaDirectionIndexer((Property<Direction>) propInner);
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
        this.numValues = property.getPossibleValues().size();
    }

    public Property<T> getProperty() {
        return property;
    }

    public int numValues() {
        return numValues;
    }

    @Nullable
    public abstract T byIndex(int index);

    public abstract int toIndex(T value);

    /**
     * Checks if this indexer is valid, i.e. iterates over the correct set of values in the correct order
     */
    protected boolean isValid() {
        Collection<T> allowed = getProperty().getPossibleValues();
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
        @Nullable
        public Boolean byIndex(int index) {
            switch (index) {
                case 0:
                    return Boolean.TRUE;
                case 1:
                    return Boolean.FALSE;
                default:
                    return null;
            }
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
            this.min = property.getPossibleValues().stream().min(Comparator.naturalOrder()).orElse(0);
        }

        @Override
        @Nullable
        public Integer byIndex(int index) {
            if (index >= 0 && index < numValues()) {
                return index + min;
            } else {
                return null;
            }
        }

        @Override
        public int toIndex(Integer value) {
            return value - min;
        }
    }

    private static class EnumIndexer<E extends Enum<E> & StringRepresentable>
            extends PropertyIndexer<E> {
        private final int ordinalOffset;
        private final E[] enumValues;

        protected EnumIndexer(EnumProperty<E> property) {
            super(property);
            this.ordinalOffset = property.getPossibleValues()
                    .stream()
                    .mapToInt(Enum::ordinal)
                    .min()
                    .orElse(0);
            this.enumValues = getProperty().getValueClass().getEnumConstants();
        }

        @Override
        @Nullable
        public E byIndex(int index) {
            final int arrayIndex = index + ordinalOffset;
            if (arrayIndex < enumValues.length) {
                return enumValues[arrayIndex];
            } else {
                return null;
            }
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

        public WeirdVanillaDirectionIndexer(Property<Direction> prop) {
            super(prop);
            Preconditions.checkState(isValid());
        }

        static boolean isApplicable(Property<?> prop) {
            Collection<?> values = prop.getPossibleValues();
            if (values.size() != ORDER.length) {
                return false;
            }
            return Arrays.equals(ORDER, values.toArray());
        }

        @Override
        @Nullable
        public Direction byIndex(int index) {
            if (index >= 0 && index < ORDER.length) {
                return ORDER[index];
            } else {
                return null;
            }
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
            return -1;
        }
    }

    private static class GenericIndexer<T extends Comparable<T>> extends PropertyIndexer<T> {
        private final Map<Comparable<?>, Integer> toValueIndex;
        private final List<T> values;

        protected GenericIndexer(Property<T> property) {
            super(property);
            this.values = ImmutableList.copyOf(property.getPossibleValues());
            ImmutableMap.Builder<Comparable<?>, Integer> toValueIndex = ImmutableMap.builder();
            for (int i = 0; i < this.values.size(); i++) {
                toValueIndex.put(this.values.get(i), i);
            }
            this.toValueIndex = toValueIndex.build();
        }

        @Override
        @Nullable
        public T byIndex(int index) {
            return values.get(index);
        }

        @Override
        public int toIndex(T value) {
            return toValueIndex.getOrDefault(value, -1);
        }
    }
}
