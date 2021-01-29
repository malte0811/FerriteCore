package malte0811.ferritecore.mixin.fabric;

import malte0811.ferritecore.impl.Deduplicator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
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
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/resources/model/ModelManager;Lnet/minecraft/client/color/item/ItemColors;)V"
            )
    )
    private static void injectAfterModels(GameConfig gameConfig, CallbackInfo ci) {
        Deduplicator.registerReloadListener();
    }
}
