package malte0811.ferritecore;

import net.minecraft.state.Property;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.*;

public class FastMapEntry extends AbstractMap<Property<?>, Comparable<?>> {
    private final FastMap<?> baseMap;
    private final int indexInBaseMap;

    public FastMapEntry(FastMap<?> baseMap, int indexInBaseMap) {
        this.baseMap = baseMap;
        this.indexInBaseMap = indexInBaseMap;
    }

    @Nonnull
    @Override
    public Set<Entry<Property<?>, Comparable<?>>> entrySet() {
        return new EntrySet();
    }

    private class EntrySet extends AbstractSet<Map.Entry<Property<?>, Comparable<?>>> {

        @Nonnull
        @Override
        public Iterator<Entry<Property<?>, Comparable<?>>> iterator() {
            Iterator<Property<?>> baseIterator = baseMap.getProperties().iterator();
            return new Iterator<Entry<Property<?>, Comparable<?>>>() {
                @Override
                public boolean hasNext() {
                    return baseIterator.hasNext();
                }

                @Override
                public Entry<Property<?>, Comparable<?>> next() {
                    Property<?> nextProp = baseIterator.next();
                    return Pair.of(nextProp, baseMap.getValue(indexInBaseMap, nextProp));
                }
            };
        }

        @Override
        public int size() {
            return baseMap.getProperties().size();
        }
    }
}
