package malte0811.ferritecore.mixin.predicates;

import malte0811.ferritecore.impl.Deduplicator;
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

import java.util.function.Predicate;

@Mixin(AndCondition.class)
public class AndConditionMixin {
    @Shadow
    @Final
    private Iterable<? extends ICondition> conditions;

    /**
     * @reason Use cached result predicates
     * @author malte0811
     */
    @Overwrite
    public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> stateContainer) {
        return Deduplicator.and(PredicateHelper.toCanonicalList(conditions, stateContainer));
    }
}
