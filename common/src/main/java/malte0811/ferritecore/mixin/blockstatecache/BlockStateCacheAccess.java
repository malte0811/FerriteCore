package malte0811.ferritecore.mixin.blockstatecache;

import net.minecraft.util.math.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(targets = "net.minecraft.block.AbstractBlock$AbstractBlockState$Cache")
public interface BlockStateCacheAccess {
    @Accessor
    VoxelShape getCollisionShape();

    @Accessor
    @Mutable
    void setCollisionShape(VoxelShape newShape);

    @Accessor
    @Nullable
    VoxelShape[] getRenderShapes();

    @Accessor
    @Mutable
    void setRenderShapes(@Nullable VoxelShape[] newShapes);
}
