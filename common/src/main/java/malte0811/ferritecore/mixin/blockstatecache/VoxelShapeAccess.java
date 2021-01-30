package malte0811.ferritecore.mixin.blockstatecache;

import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapePart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(VoxelShape.class)
public interface VoxelShapeAccess {
    @Accessor
    VoxelShapePart getPart();

    @Accessor
    @Nullable
    VoxelShape[] getProjectionCache();

    @Accessor
    @Mutable
    void setPart(VoxelShapePart newPart);

    @Accessor
    void setProjectionCache(@Nullable VoxelShape[] newCache);
}
