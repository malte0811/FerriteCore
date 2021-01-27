package malte0811.ferritecore.fastmap;

import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

class FastSubMapEntrySet extends AbstractSet<Map.Entry<Property<?>, Comparable<?>>> {
    private final FastMap<?> fastMap;
    private final int mapIndex;

    public FastSubMapEntrySet(FastSubMap fastSubMap) {
        this.fastMap = fastSubMap.baseMap;
        this.mapIndex = fastSubMap.indexInBaseMap;
    }

    @Nonnull
    @Override
    public Iterator<Map.Entry<Property<?>, Comparable<?>>> iterator() {
        return new Iterator<Map.Entry<Property<?>, Comparable<?>>>() {
            private int iteratorIndex = 0;

            @Override
            public boolean hasNext() {
                return iteratorIndex < fastMap.numProperties();
            }

            @Override
            public Map.Entry<Property<?>, Comparable<?>> next() {
                Property<?> nextProp = fastMap.getProperties().get(iteratorIndex);
                Comparable<?> nextValue = fastMap.getKey(iteratorIndex).getValue(mapIndex);
                ++iteratorIndex;
                return Pair.of(nextProp, nextValue);
            }
        };
    }

    @Override
    public int size() {
        return fastMap.numProperties();
    }
}
