package malte0811.ferritecore;

import net.minecraft.block.BlockState;
import net.minecraft.state.Property;

import java.util.Map;

public class HackyGlobalState {
    public static Map<Map<Property<?>, Comparable<?>>, ?> lastStateMap;
    public static FastMap<?> lastFastStateMap;
}
