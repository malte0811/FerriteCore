package malte0811.ferritecore.mixin.mrl;

import malte0811.ferritecore.impl.Deduplicator;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelResourceLocation.class)
public class ModelResourceLocationMixin {
    @Shadow
    @Final
    @Mutable
    private String variant;

    @Inject(method = "<init>(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;)V", at = @At("TAIL"))
    private void constructTail(ResourceLocation location, String variantIn, CallbackInfo ci) {
        this.variant = Deduplicator.deduplicateVariant(this.variant);
    }
}
