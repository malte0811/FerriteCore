package malte0811.ferritecore.ducks;

import com.google.common.collect.ImmutableMap;
import malte0811.ferritecore.fastmap.FastMap;
import net.minecraft.state.Property;

public interface FastMapStateHolder<S> {
    FastMap<S> getStateMap();

    void setStateMap(FastMap<S> newValue);

    int getStateIndex();

    void setStateIndex(int newValue);

    ImmutableMap<Property<?>, Comparable<?>> getVanillaPropertyMap();

    void deleteVanillaPropertyMap();
}