package malte0811.ferritecore.fastmap;

import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nonnull;
import java.util.AbstractMap;
import java.util.Set;

public class FastSubMap extends AbstractMap<Property<?>, Comparable<?>> {
    final FastMap<?> baseMap;
    final int indexInBaseMap;

    public FastSubMap(FastMap<?> baseMap, int indexInBaseMap) {
        this.baseMap = baseMap;
        this.indexInBaseMap = indexInBaseMap;
    }

    @Nonnull
    @Override
    public Set<Entry<Property<?>, Comparable<?>>> entrySet() {
        return new FastSubMapEntrySet(this);
    }

    @Override
    public Comparable<?> get(Object key) {
        if (!(key instanceof Property<?>)) {
            return null;
        }
        Property<?> prop = (Property<?>) key;
        return baseMap.getValue(indexInBaseMap, prop);
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }
}
