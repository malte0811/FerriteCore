package malte0811.ferritecore.impl;

import malte0811.ferritecore.util.PredicateHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.ICondition;
import net.minecraft.state.StateContainer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class AndConditionImpl {
    private static final Map<List<Predicate<BlockState>>, Predicate<BlockState>> COMBINED_PREDICATE_CACHE = new ConcurrentHashMap<>();

    public static Predicate<BlockState> getPredicate(
            StateContainer<Block, BlockState> stateContainer, Iterable<? extends ICondition> conditions
    ) {
        return COMBINED_PREDICATE_CACHE.computeIfAbsent(
                PredicateHelper.toCanonicalList(conditions, stateContainer),
                listInt -> state -> listInt.stream().allMatch((predicate) -> predicate.test(state))
        );
    }
}
