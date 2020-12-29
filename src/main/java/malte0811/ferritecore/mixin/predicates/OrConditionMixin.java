package malte0811.ferritecore.mixin.predicates;

import malte0811.ferritecore.impl.CachedOrPredicates;
import malte0811.ferritecore.util.PredicateHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.ICondition;
import net.minecraft.client.renderer.model.multipart.OrCondition;
import net.minecraft.state.StateContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(OrCondition.class)
public class OrConditionMixin {
    @Shadow @Final private Iterable<? extends ICondition> conditions;

    /**
     * @reason Use cached result predicates
     * @author malte0811
     */
    @Overwrite
    public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> stateContainer) {
        return CachedOrPredicates.or(PredicateHelper.toCanonicalList(conditions, stateContainer));
    }
}
