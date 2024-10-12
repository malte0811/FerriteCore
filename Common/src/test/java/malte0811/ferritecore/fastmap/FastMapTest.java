package malte0811.ferritecore.fastmap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import malte0811.ferritecore.impl.FastMapEntryMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.*;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FastMapTest {
    private static final BooleanProperty BOOL = BooleanProperty.create("A");
    private static final IntegerProperty INT = IntegerProperty.create("B", 0, 7);
    private static final EnumProperty<Direction> DIR = DirectionProperty.create("C", Direction.class);
    private static final BooleanList BOOLS = new BooleanArrayList(new boolean[]{false, true});

    @TestFactory
    public Stream<DynamicTest> basicMapping() {
        return forEachType(TestData::testBasic);
    }

    @TestFactory
    public Stream<DynamicTest> testWithInvalid() {
        return forEachType(TestData::testWithInvalid);
    }

    @TestFactory
    public Stream<DynamicTest> testWith() {
        return forEachType(TestData::testWith);
    }

    private Stream<DynamicTest> forEachType(Consumer<TestData> test) {
        return BOOLS.stream().map(
                b -> DynamicTest.dynamicTest("Compact: " + b, () -> test.accept(new TestData(b)))
        );
    }

    private void assertBinaryKeySize(int numElements, int expectedFactor) {
        Property<?> temp = IntegerProperty.create("", 1, numElements);
        BinaryFastMapKey<?> key = new BinaryFastMapKey<>(temp, 1);
        Assertions.assertEquals(expectedFactor, key.getFactorToNext());
    }

    @Test
    public void testBinaryKeySizes() {
        assertBinaryKeySize(2, 2);
        assertBinaryKeySize(16, 16);
        assertBinaryKeySize(15, 16);
        assertBinaryKeySize(17, 32);
    }

    @Test
    public void testOversizedBinaryKey() {
        new BinaryFastMapKey<>(IntegerProperty.create("", 1, 4), 1 << 29);
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> new BinaryFastMapKey<>(IntegerProperty.create("", 1, 4), 1 << 30)
        );
    }

    @Test
    public void testBinaryKey32Bits() {
        int factor = 1;
        for (int i = 0; i < 31; ++i) {
            BinaryFastMapKey<Boolean> k = new BinaryFastMapKey<>(BOOL, factor);
            Assertions.assertEquals(true, k.getValue(factor / 2));
            Assertions.assertEquals(false, k.getValue(factor));
            Assertions.assertTrue(factor > 0);
            Assertions.assertEquals(true, k.getValue(factor << 2));
            factor *= k.getFactorToNext();
        }
    }

    @Test
    public void testInvalidKeys() {
        forEachType(TestData::testBadType);
    }

    private static class TestData {
        private final FastMap<Map<Property<?>, Comparable<?>>> map;
        private final ImmutableMap<Map<Property<?>, Comparable<?>>, Map<Property<?>, Comparable<?>>> values;

        public TestData(boolean compact) {
            List<Property<?>> properties = ImmutableList.of(BOOL, INT, DIR);
            ImmutableMap.Builder<Map<Property<?>, Comparable<?>>, Map<Property<?>, Comparable<?>>> values = ImmutableMap.builder();
            Stream<List<Pair<Property<?>, Comparable<?>>>> stream = Stream.of(Collections.emptyList());

            for (Property<?> property : properties) {
                stream = stream.flatMap(baseList -> property.getPossibleValues().stream().map(value -> {
                    List<Pair<Property<?>, Comparable<?>>> withAdded = Lists.newArrayList(baseList);
                    withAdded.add(Pair.of(property, value));
                    return withAdded;
                }));
            }
            stream.forEach(l -> {
                Map<Property<?>, Comparable<?>> entry = l.stream().collect(Collectors.toMap(
                        Pair::getFirst,
                        Pair::getSecond
                ));
                values.put(entry, entry);
            });
            this.values = values.build();
            map = new FastMap<>(properties, this.values, compact);
        }

        private void testBasic() {
            for (Map<Property<?>, Comparable<?>> e : values.keySet()) {
                int index = map.getIndexOf(e);
                FastMapStateHolder<Map<Property<?>, Comparable<?>>> holder = new MockFMStateHolder<>(map, index);
                Map<Property<?>, Comparable<?>> map = new FastMapEntryMap(holder);
                Assertions.assertEquals(new HashMap<>(e), new HashMap<>(map));
            }
        }

        private void testWith() {
            for (Map<Property<?>, Comparable<?>> baseMap : values.keySet()) {
                final int baseIndex = map.getIndexOf(baseMap);
                for (Property<?> toSwap : baseMap.keySet()) {
                    testSwaps(baseIndex, toSwap, baseMap);
                }
            }
        }

        private void testWithInvalid() {
            for (Map<Property<?>, Comparable<?>> baseMap : values.keySet()) {
                final int baseIndex = map.getIndexOf(baseMap);
                Assertions.assertNull(map.with(baseIndex, INT, 8));
            }
        }

        private <T extends Comparable<T>>
        void testSwaps(int baseIndex, Property<T> toSwap, Map<Property<?>, Comparable<?>> baseMap) {
            Map<Property<?>, Comparable<?>> expected = new HashMap<>(baseMap);
            for (T newValue : toSwap.getPossibleValues()) {
                Map<Property<?>, Comparable<?>> newMap = map.with(baseIndex, toSwap, newValue);
                expected.put(toSwap, newValue);
                Assertions.assertEquals(expected, newMap);
            }
        }

        private void testBadType() {
            Assertions.assertNull(map.with(0, BOOL, ""));
            Assertions.assertNull(map.with(0, INT, ""));
            Assertions.assertNull(map.with(0, DIR, ""));
        }
    }
}