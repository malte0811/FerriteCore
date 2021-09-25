package malte0811.ferritecore.mixin.blockstatecache;

import malte0811.ferritecore.impl.BlockStateCacheImpl;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Shadow
    protected abstract BlockState asState();

    @Inject(method = "initCache", at = @At("HEAD"))
    public void cacheStateHead(CallbackInfo ci) {
        BlockStateCacheImpl.deduplicateCachePre(asState());
    }

    @Inject(method = "initCache", at = @At("TAIL"))
    public void cacheStateTail(CallbackInfo ci) {
        BlockStateCacheImpl.deduplicateCachePost(asState());
    }
}
