package malte0811.ferritecore.mixin.mrl;

import malte0811.ferritecore.impl.Deduplicator;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
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

    @Inject(
            method = "<init>(Lnet/minecraft/util/ResourceLocation;Ljava/lang/String;)V",
            at = @At("TAIL")
    )
    private void constructTail(ResourceLocation location, String variantIn, CallbackInfo ci) {
        // Do not use new strings for path and namespace, and deduplicate the variant string
        ((ResourceLocationAccess) this).setPath(location.getPath());
        ((ResourceLocationAccess) this).setNamespace(location.getNamespace());
        this.variant = Deduplicator.deduplicateVariant(this.variant);
    }
}
