package malte0811.ferritecore.mixin.threaddetec;

import malte0811.ferritecore.ducks.SmallThreadDetectable;
import malte0811.ferritecore.util.SmallThreadingDetector;
import net.minecraft.util.ThreadingDetector;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PalettedContainer.class)
public class PalettedContainerMixin implements SmallThreadDetectable {
    @Shadow
    @Final
    @Mutable
    private ThreadingDetector threadingDetector;

    private byte ferritecore$threadingState = UNLOCKED;

    @Inject(
            method = {
                    "<init>(Lnet/minecraft/core/IdMap;Ljava/lang/Object;Lnet/minecraft/world/level/chunk/PalettedContainer$Strategy;)V",
                    "<init>(Lnet/minecraft/core/IdMap;Lnet/minecraft/world/level/chunk/PalettedContainer$Strategy;Lnet/minecraft/world/level/chunk/PalettedContainer$Data;)V",
                    "<init>(Lnet/minecraft/core/IdMap;Lnet/minecraft/world/level/chunk/PalettedContainer$Strategy;Lnet/minecraft/world/level/chunk/PalettedContainer$Configuration;Lnet/minecraft/util/BitStorage;Ljava/util/List;)V",
            },
            at = @At("TAIL")
    )
    public void redirectBuildThreadingDetector(CallbackInfo ci) {
        this.threadingDetector = null;
    }

    /**
     * @reason The vanilla ThreadingDetector field is null now, and replaced by SmallThreadingDetector
     * @author malte0811
     */
    @Overwrite
    public void acquire() {
        SmallThreadingDetector.acquire(this, "PalettedContainer");
    }

    /**
     * @reason The vanilla ThreadingDetector field is null now, and replaced by SmallThreadingDetector
     * @author malte0811
     */
    @Overwrite
    public void release() {
        SmallThreadingDetector.release(this);
    }

    @Override
    public byte ferritecore$getState() {
        return ferritecore$threadingState;
    }

    @Override
    public void ferritecore$setState(byte newState) {
        ferritecore$threadingState = newState;
    }
}
