package malte0811.ferritecore.mixin.blockstatecache;

import malte0811.ferritecore.ducks.BlockStateCacheAccess;
import net.minecraft.util.math.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.block.AbstractBlock$AbstractBlockState$Cache")
public class BlockStateCacheMixin implements BlockStateCacheAccess {
    @Shadow
    @Final
    @Mutable
    protected VoxelShape collisionShape;

    @Shadow
    @Final
    @Mutable
    @Nullable
    private VoxelShape[] renderShapes;

    @Override
    public VoxelShape getCollisionShape() {
        return this.collisionShape;
    }

    @Override
    public void setCollisionShape(VoxelShape newShape) {
        this.collisionShape = newShape;
    }

    @Override
    public VoxelShape[] getRenderShapes() {
        return this.renderShapes;
    }

    @Override
    public void setRenderShapes(@Nullable VoxelShape[] newShapes) {
        this.renderShapes = newShapes;
    }
}
