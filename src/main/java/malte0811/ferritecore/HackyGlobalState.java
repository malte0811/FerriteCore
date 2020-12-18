package malte0811.ferritecore;

import net.minecraft.state.Property;

import java.util.Map;

public class HackyGlobalState {
    public static final ThreadLocal<Map<Map<Property<?>, Comparable<?>>, ?>> LAST_STATE_MAP = new ThreadLocal<>();
    public static final ThreadLocal<FastMap<?>> LAST_FAST_STATE_MAP = new ThreadLocal<>();
}
