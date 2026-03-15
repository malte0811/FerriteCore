package malte0811.ferritecore.impl;

import malte0811.ferritecore.ducks.FastMapStateHolder;
import malte0811.ferritecore.fastmap.FastMap;
import malte0811.ferritecore.mixin.accessors.StateHolderAccess;
import malte0811.ferritecore.mixin.config.FerriteConfig;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;

public class FastMapStateHolderImpl {
    public static <S extends StateHolder<?, S>>
    void initializeFastMap(Collection<S> states) {
        S someState = states.iterator().next();
        FastMap<S> mainMap = new FastMap<>(((StateHolderAccess) someState).getPropertyKeys(), FerriteConfig.COMPACT_FAST_MAP.isEnabled());
        for (var entry : states) {
            final int index = mainMap.insertAtIndex(entry, S::getValue);
            ((FastMapStateHolder<S>) entry).ferritecore_setStateMap(mainMap, index);
        }
    }

    public static Comparable<?> getPropertyValue(
            Comparable<?>[] vanillaValues, FastMap<?> fastMap, int fastMapIndex, int propertyIndex
    ) {
        if (vanillaValues != null) {
            // Either the FastMap is not initialized yet, or we are not using the FastMap for value lookups
            return vanillaValues[propertyIndex];
        } else {
            return getPropertyValue(fastMap, fastMapIndex, propertyIndex);
        }
    }

    public static Comparable<?> getPropertyValue(
            FastMap<?> fastMap, int fastMapIndex, int propertyIndex
    ) {
        int internalIndex = fastMap.getValueIndex(fastMapIndex, propertyIndex);
        return fastMap.getProperties()[propertyIndex].getPossibleValues().get(internalIndex);
    }
}
