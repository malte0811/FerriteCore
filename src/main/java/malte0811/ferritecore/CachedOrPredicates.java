package malte0811.ferritecore;

import net.minecraft.block.BlockState;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class CachedOrPredicates {

    public static final Map<List<Predicate<BlockState>>, Predicate<BlockState>> OR_PREDICATE_CACHE = new ConcurrentHashMap<>();

    public static Predicate<BlockState> or(List<Predicate<BlockState>> list) {
        return OR_PREDICATE_CACHE.computeIfAbsent(
                list,
                listInt -> state -> listInt.stream().anyMatch((predicate) -> predicate.test(state))
        );
    }
}
