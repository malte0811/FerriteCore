package malte0811.ferritecore.mixin.predicates;

import malte0811.ferritecore.impl.PropertyValueConditionImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.PropertyValueCondition;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = PropertyValueCondition.class, priority = 1100)
public class PropertyValueConditionMixin {
    @Inject(
            method = "getPredicate",
            at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", remap = false),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void getPredicateCached(
            StateContainer<?, ?> $1, CallbackInfoReturnable<Predicate<BlockState>> cir,
            Property<?> property, String $2, boolean negate, List<String> split
    ) {
        PropertyValueConditionImpl.FULL_PREDICATE_CACHE.getPre(
                new PropertyValueConditionImpl.PredicateKey(property, split, negate), cir
        );
    }

    @Inject(method = "getPredicate", at = @At("RETURN"), cancellable = true)
    public void getPredicateAddNew(CallbackInfoReturnable<Predicate<BlockState>> cir) {
        PropertyValueConditionImpl.FULL_PREDICATE_CACHE.getPost(cir);
    }

    @Inject(method = "makePropertyPredicate", at = @At("HEAD"), cancellable = true)
    public void getSingleCached(
            StateContainer<Block, BlockState> $, Property<?> property, String string,
            CallbackInfoReturnable<Predicate<BlockState>> cir
    ) {
        PropertyValueConditionImpl.SINGLE_VALUE_CACHE.getPre(Pair.of(property, string), cir);
    }

    @Inject(method = "makePropertyPredicate", at = @At("RETURN"), cancellable = true)
    public void getSingleAddNew(CallbackInfoReturnable<Predicate<BlockState>> cir) {
        PropertyValueConditionImpl.SINGLE_VALUE_CACHE.getPost(cir);
    }
}
