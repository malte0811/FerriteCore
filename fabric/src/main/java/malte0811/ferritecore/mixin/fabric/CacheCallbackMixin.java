package malte0811.ferritecore.mixin.fabric;

import malte0811.ferritecore.impl.BlockStateCacheImpl;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Blocks.class)
public class CacheCallbackMixin {
    @Inject(method = "cacheBlockStates", at = @At("TAIL"))
    private static void afterCacheRebuild(CallbackInfo ci) {
        BlockStateCacheImpl.resetCaches();
    }
}
