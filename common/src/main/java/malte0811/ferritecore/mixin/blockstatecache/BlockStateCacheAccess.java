package malte0811.ferritecore.mixin.blockstatecache;

import net.minecraft.util.math.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

//TODO This is a hack to fix builds for the time being (should be AB$ABS$C). Running in dev is currently not possible.
@Mixin(targets = "net.minecraft.block.AbstractBlock.AbstractBlockState.Cache")
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
