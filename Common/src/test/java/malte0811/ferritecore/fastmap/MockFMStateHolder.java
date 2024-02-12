package malte0811.ferritecore.fastmap;

import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;

public class MockFMStateHolder<T> implements FastMapStateHolder<T> {
    private final FastMap<T> map;
    private final int index;

    public MockFMStateHolder(FastMap<T> map, int index) {
        this.map = map;
        this.index = index;
    }

    @Override
    public FastMap<T> getStateMap() {
        return map;
    }

    @Override
    public void setStateMap(FastMap<T> newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getStateIndex() {
        return index;
    }

    @Override
    public void setStateIndex(int newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reference2ObjectMap<Property<?>, Comparable<?>> getVanillaPropertyMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replacePropertyMap(Reference2ObjectMap<Property<?>, Comparable<?>> newMap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNeighborTable(Table<Property<?>, Comparable<?>, T> table) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Table<Property<?>, Comparable<?>, T> getNeighborTable() {
        return null;
    }
}
