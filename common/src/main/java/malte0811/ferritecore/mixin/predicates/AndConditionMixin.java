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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(value = AndCondition.class)
public class AndConditionMixin {
    @Shadow
    @Final
    private Iterable<? extends ICondition> conditions;

    @Inject(method = "getPredicate", at = @At("HEAD"), cancellable = true)
    public void getPredicateFromCache(
            StateContainer<Block, BlockState> stateContainer, CallbackInfoReturnable<Predicate<BlockState>> cir
    ) {
        Deduplicator.AND_PREDICATE_CACHE.getPre(PredicateHelper.toCanonicalList(conditions, stateContainer), cir);
    }

    @Inject(method = "getPredicate", at = @At("RETURN"), cancellable = true)
    public void getPredicateAddToCache(
            StateContainer<Block, BlockState> stateContainer, CallbackInfoReturnable<Predicate<BlockState>> cir
    ) {
        Deduplicator.AND_PREDICATE_CACHE.getPost(cir);
    }
}
