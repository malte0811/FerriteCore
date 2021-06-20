package malte0811.ferritecore.mixin.blockstatecache;

import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

// TODO this does not work in Dev, but BB$BSB$C causes build issues
@Mixin(targets = "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase.Cache")
public interface BlockStateCacheAccess {
    @Accessor
    VoxelShape getCollisionShape();

    @Accessor
    @Mutable
    void setCollisionShape(VoxelShape newShape);

    @Accessor
    @Nullable
    VoxelShape[] getOcclusionShapes();

    @Accessor
    @Mutable
    void setOcclusionShapes(@Nullable VoxelShape[] newShapes);
}
