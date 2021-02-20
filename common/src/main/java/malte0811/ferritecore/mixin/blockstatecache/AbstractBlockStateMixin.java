package malte0811.ferritecore.mixin.blockstatecache;

import malte0811.ferritecore.impl.BlockStateCacheImpl;
import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Inject(method = "cacheState", at = @At("HEAD"))
    public void cacheStateHead(CallbackInfo ci) {
        BlockStateCacheImpl.deduplicateCachePre((AbstractBlock.AbstractBlockState) (Object) this);
    }

    @Inject(method = "cacheState", at = @At("TAIL"))
    public void cacheStateTail(CallbackInfo ci) {
        BlockStateCacheImpl.deduplicateCachePost((AbstractBlock.AbstractBlockState) (Object) this);
    }
}
