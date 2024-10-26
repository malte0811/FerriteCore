package malte0811.ferritecore.mixin.blockstatecache;

import malte0811.ferritecore.ducks.BlockStateCacheAccess;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase$Cache")
public class BlockStateCacheMixin implements BlockStateCacheAccess {
    @Shadow
    @Final
    @Mutable
    protected VoxelShape collisionShape;

    @Shadow
    @Final
    @Mutable
    private boolean[] faceSturdy;

    @Override
    public VoxelShape getCollisionShape() {
        return this.collisionShape;
    }

    @Override
    public void setCollisionShape(VoxelShape newShape) {
        this.collisionShape = newShape;
    }

    @Override
    public boolean[] getFaceSturdy() {
        return faceSturdy;
    }

    @Override
    public void setFaceSturdy(final boolean[] newFaceSturdyArray) {
        this.faceSturdy = newFaceSturdyArray;
    }
}
