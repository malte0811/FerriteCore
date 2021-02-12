package malte0811.ferritecore.fastmap;

import com.google.common.collect.ImmutableMap;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import net.minecraft.state.Property;

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
    public ImmutableMap<Property<?>, Comparable<?>> getVanillaPropertyMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replacePropertyMap(ImmutableMap<Property<?>, Comparable<?>> newMap) {
        throw new UnsupportedOperationException();
    }
}
