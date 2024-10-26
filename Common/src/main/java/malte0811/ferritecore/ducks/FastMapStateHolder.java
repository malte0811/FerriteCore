package malte0811.ferritecore.ducks;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import malte0811.ferritecore.fastmap.FastMap;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;

public interface FastMapStateHolder<S> {
    FastMap<S> getStateMap();

    void setStateMap(FastMap<S> newValue);

    int getStateIndex();

    void setStateIndex(int newValue);

    Reference2ObjectMap<Property<?>, Comparable<?>> getVanillaPropertyMap();

    void replacePropertyMap(Reference2ObjectMap<Property<?>, Comparable<?>> newMap);

    void setNeighborMap(Map<Property<?>, S[]> table);

    Map<Property<?>, S[]> getNeighborMap();
}
