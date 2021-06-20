package malte0811.ferritecore.mixin.blockstatecache;

import malte0811.ferritecore.impl.BlockStateCacheImpl;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {
    @Inject(method = "initCache", at = @At("HEAD"))
    public void cacheStateHead(CallbackInfo ci) {
        BlockStateCacheImpl.deduplicateCachePre((BlockBehaviour.BlockStateBase) (Object) this);
    }

    @Inject(method = "initCache", at = @At("TAIL"))
    public void cacheStateTail(CallbackInfo ci) {
        BlockStateCacheImpl.deduplicateCachePost((BlockBehaviour.BlockStateBase) (Object) this);
    }
}
