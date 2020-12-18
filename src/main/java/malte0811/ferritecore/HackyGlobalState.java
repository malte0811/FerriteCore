package malte0811.ferritecore;

import net.minecraft.state.Property;

import java.util.Map;

public class HackyGlobalState {
    public static ThreadLocal<Map<Map<Property<?>, Comparable<?>>, ?>> lastStateMap;
    public static ThreadLocal<FastMap<?>> lastFastStateMap;
}
