package malte0811.ferritecore.mixin.fabric;

import malte0811.ferritecore.impl.Deduplicator;
import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemRenderer;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/model/ModelManager;Lnet/minecraft/client/renderer/color/ItemColors;)V"
            )
    )
    private void injectAfterModels(GameConfiguration gameConfig, CallbackInfo ci) {
        Deduplicator.registerReloadListener();
    }
}
