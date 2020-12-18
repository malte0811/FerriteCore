package malte0811.ferritecore.mixin;

import malte0811.ferritecore.util.PredicateHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.AndCondition;
import net.minecraft.client.renderer.model.multipart.ICondition;
import net.minecraft.state.StateContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Mixin(AndCondition.class)
public class AndConditionMixin {
    private static final Map<List<Predicate<BlockState>>, Predicate<BlockState>> COMBINED_PREDICATE_CACHE = new ConcurrentHashMap<>();

    @Shadow @Final private Iterable<? extends ICondition> conditions;

    /**
     * @reason Use cached result predicates
     * @author malte0811
     */
    @Overwrite
    public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> stateContainer) {
        return COMBINED_PREDICATE_CACHE.computeIfAbsent(
                PredicateHelper.toCanonicalList(conditions, stateContainer),
                listInt -> state -> listInt.stream().allMatch((predicate) -> predicate.test(state))
        );
    }
}
