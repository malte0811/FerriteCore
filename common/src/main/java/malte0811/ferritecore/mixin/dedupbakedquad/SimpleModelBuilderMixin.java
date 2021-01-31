package malte0811.ferritecore.mixin.dedupbakedquad;

import malte0811.ferritecore.impl.Deduplicator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleBakedModel.Builder.class)
public class SimpleModelBuilderMixin {
    @Inject(method = "addGeneralQuad", at = @At("HEAD"))
    public void deduplicate(BakedQuad quad, CallbackInfoReturnable<SimpleBakedModel.Builder> cir) {
        Deduplicator.deduplicate(quad);
    }

    @Inject(method = "addFaceQuad", at = @At("HEAD"))
    public void deduplicate(
            Direction direction, BakedQuad quad, CallbackInfoReturnable<SimpleBakedModel.Builder> cir
    ) {
        Deduplicator.deduplicate(quad);
    }
}
