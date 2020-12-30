package malte0811.ferritecore.impl;

import malte0811.ferritecore.ducks.FastMapStateHolder;
import malte0811.ferritecore.ducks.NoPropertyStateHolder;
import malte0811.ferritecore.fastmap.FastMap;
import net.minecraft.state.Property;

import java.util.Map;

public class StateHolderImpl {
    public static final ThreadLocal<Map<Map<Property<?>, Comparable<?>>, ?>> LAST_STATE_MAP = new ThreadLocal<>();
    public static final ThreadLocal<FastMap<?>> LAST_FAST_STATE_MAP = new ThreadLocal<>();

    public static <S>
    void populateNeighbors(Map<Map<Property<?>, Comparable<?>>, S> states, FastMapStateHolder<S> holder) {
        if (holder.getStateMap() != null) {
            throw new IllegalStateException();
        } else if (states == LAST_STATE_MAP.get()) {
            // Use threadlocal state to use the same fast map for all states of one block
            holder.setStateMap((FastMap<S>) LAST_FAST_STATE_MAP.get());
        } else {
            LAST_STATE_MAP.set(states);
            FastMap<S> globalTable = new FastMap<>(holder.getVanillaPropertyMap().keySet(), states);
            holder.setStateMap(globalTable);
            LAST_FAST_STATE_MAP.set(globalTable);
        }
        int index = holder.getStateMap().getIndexOf(holder.getVanillaPropertyMap());
        holder.setStateIndex(index);
        if (holder instanceof NoPropertyStateHolder) {
            holder.deleteVanillaPropertyMap();
        }
    }
}
