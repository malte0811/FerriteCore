package malte0811.ferritecore.fastmap;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

/**
 * Provides a way of converting between values of a property and indices in [0, #values). Most properties are covered
 * by one of the (faster) specific implementations, all other properties use the {@link GenericIndexer}
 */
public abstract class PropertyIndexer<T extends Comparable<T>> {
    private static final Map<Property<?>, PropertyIndexer<?>> KNOWN_INDEXERS = new Reference2ObjectOpenHashMap<>();

    private final Property<T> property;
    private final int numValues;
    protected final T[] valuesInOrder;

    public static <T extends Comparable<T>> PropertyIndexer<T> makeIndexer(Property<T> prop) {
        synchronized (KNOWN_INDEXERS) {
            PropertyIndexer<?> unchecked = KNOWN_INDEXERS.computeIfAbsent(prop, propInner -> {
                PropertyIndexer<?> result = null;
                if (propInner instanceof BooleanProperty boolProp) {
                    result = new BoolIndexer(boolProp);
                } else if (propInner instanceof IntegerProperty intProp) {
                    result = new IntIndexer(intProp);
                } else if (WeirdVanillaDirectionIndexer.isApplicable(propInner)) {
                    result = new WeirdVanillaDirectionIndexer((Property<Direction>) propInner);
                } else if (propInner instanceof EnumProperty<?> enumProp) {
                    result = new EnumIndexer<>(enumProp);
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

    protected PropertyIndexer(Property<T> property, T[] valuesInOrder) {
        this.property = property;
        this.numValues = property.getPossibleValues().size();
        this.valuesInOrder = valuesInOrder;
    }

    public Property<T> getProperty() {
        return property;
    }

    public int numValues() {
        return numValues;
    }

    @Nullable
    public final T byIndex(int index) {
        if (index >= 0 && index < valuesInOrder.length) {
            return valuesInOrder[index];
        } else {
            return null;
        }
    }

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
        private static final Boolean[] VALUES = {true, false};

        protected BoolIndexer(BooleanProperty property) {
            super(property, VALUES);
        }

        @Override
        public int toIndex(Boolean value) {
            return value ? 0 : 1;
        }
    }

    private static class IntIndexer extends PropertyIndexer<Integer> {
        private final int min;

        protected IntIndexer(IntegerProperty property) {
            super(property, property.getPossibleValues().toArray(new Integer[0]));
            this.min = property.getPossibleValues().stream().min(Comparator.naturalOrder()).orElse(0);
        }

        @Override
        public int toIndex(Integer value) {
            return value - min;
        }
    }

    private static class EnumIndexer<E extends Enum<E> & StringRepresentable>
            extends PropertyIndexer<E> {
        private final int ordinalOffset;

        protected EnumIndexer(EnumProperty<E> property) {
            super(property, property.getPossibleValues().toArray((E[]) new Enum<?>[0]));
            this.ordinalOffset = property.getPossibleValues()
                    .stream()
                    .mapToInt(Enum::ordinal)
                    .min()
                    .orElse(0);
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
            super(prop, ORDER);
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
        public int toIndex(Direction value) {
            return switch (value) {
                case NORTH -> 0;
                case EAST -> 1;
                case SOUTH -> 2;
                case WEST -> 3;
                case UP -> 4;
                case DOWN -> 5;
            };
        }
    }

    private static class GenericIndexer<T extends Comparable<T>> extends PropertyIndexer<T> {
        private final Map<Comparable<?>, Integer> toValueIndex;

        protected GenericIndexer(Property<T> property) {
            super(property, property.getPossibleValues().toArray((T[]) new Comparable[0]));
            ImmutableMap.Builder<Comparable<?>, Integer> toValueIndex = ImmutableMap.builder();
            for (int i = 0; i < this.valuesInOrder.length; i++) {
                toValueIndex.put(this.valuesInOrder[i], i);
            }
            this.toValueIndex = toValueIndex.build();
        }

        @Override
        public int toIndex(T value) {
            return toValueIndex.getOrDefault(value, -1);
        }
    }
}
