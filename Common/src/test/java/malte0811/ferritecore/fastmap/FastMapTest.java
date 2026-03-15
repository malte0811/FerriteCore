package malte0811.ferritecore.fastmap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import malte0811.ferritecore.impl.FastMapStateHolderImpl;
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
    private static final EnumProperty<Direction> DIR = EnumProperty.create("C", Direction.class);

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
        return Stream.of(
                DynamicTest.dynamicTest("Compact", () -> test.accept(new TestData(true))),
                DynamicTest.dynamicTest("Binary", () -> test.accept(new TestData(false)))
        );
    }

    private void assertBinaryKeySize(int numElements, int expectedFactor) {
        BinaryFastMapKey key = BinaryFastMapKey.create(1, numElements);
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
        BinaryFastMapKey.create(1 << 29, 4);
        Assertions.assertThrows(IllegalStateException.class, () -> BinaryFastMapKey.create(1 << 30, 4));
    }

    @Test
    public void testBinaryKey32Bits() {
        int factor = 1;
        for (int i = 0; i < 31; ++i) {
            BinaryFastMapKey k = BinaryFastMapKey.create(factor, 2);
            Assertions.assertEquals(0, k.getIndexIn(factor / 2));
            Assertions.assertEquals(1, k.getIndexIn(factor));
            Assertions.assertTrue(factor > 0);
            Assertions.assertEquals(0, k.getIndexIn(factor << 2));
            factor *= k.getFactorToNext();
        }
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
            map = new FastMap<>(properties.toArray(Property<?>[]::new), compact);
            for (var entry : this.values.values()) {
                map.insertAtIndex(entry, Map::get);
            }
        }

        private void testBasic() {
            for (Map<Property<?>, Comparable<?>> e : values.keySet()) {
                int stateIndex = map.getIndexOf(e, Map::get);
                Map<Property<?>, Comparable<?>> reversedMap = new HashMap<>();
                for (int i = 0; i < map.getProperties().length; i++) {
                    reversedMap.put(map.getProperties()[i], FastMapStateHolderImpl.getPropertyValue(map, stateIndex, i));
                }
                Assertions.assertEquals(new HashMap<>(e), reversedMap);
            }
        }

        private void testWith() {
            for (Map<Property<?>, Comparable<?>> baseMap : values.keySet()) {
                final int baseIndex = map.getIndexOf(baseMap, Map::get);
                for (int i = 0; i < baseMap.size(); ++i) {
                    testSwaps(baseIndex, i, baseMap);
                }
            }
        }

        private void testWithInvalid() {
            for (Map<Property<?>, Comparable<?>> baseMap : values.keySet()) {
                final int baseIndex = map.getIndexOf(baseMap, Map::get);
                Assertions.assertThrows(RuntimeException.class, () -> map.with(baseIndex, 1, 8));
            }
        }

        private void testSwaps(int baseIndex, int propertyIndex, Map<Property<?>, Comparable<?>> baseMap) {
            Map<Property<?>, Comparable<?>> expected = new HashMap<>(baseMap);
            Property<?> property = map.getProperties()[propertyIndex];
            List<?> values = property.getPossibleValues();
            for (int valueIndex = 0; valueIndex < values.size(); valueIndex++) {
                Map<Property<?>, Comparable<?>> newMap = map.with(baseIndex, propertyIndex, valueIndex);
                expected.put(property, (Comparable<?>) values.get(valueIndex));
                Assertions.assertEquals(expected, newMap, "Setting " + propertyIndex + " to " + valueIndex + " from " + baseMap);
            }
        }
    }
}